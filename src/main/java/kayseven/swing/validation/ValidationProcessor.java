package kayseven.swing.validation;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import kayseven.swing.validation.validator.DigitValidator;
import kayseven.swing.validation.validator.EmailValidator;
import kayseven.swing.validation.validator.RegexValidator;
import kayseven.swing.validation.validator.RequiredValidator;
import kayseven.swing.validation.validator.StringLengthValidator;
import kayseven.swing.validation.validator.ValidCharsValidator;
import kayseven.swing.validation.validator.ValidatorBase;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Validator;

/**
 *
 * @author K7
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ValidationProcessor extends AbstractProcessor {

    private static final List<ValidatorBase> VALIDATORS;
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    static {
        VALIDATORS = new ArrayList<ValidatorBase>();
        addValidator(new RequiredValidator());
        addValidator(new RegexValidator());
        addValidator(new StringLengthValidator());
        addValidator(new EmailValidator());
        addValidator(new DigitValidator());
        addValidator(new ValidCharsValidator());
    }

    public static void addValidator(ValidatorBase validatorBase) {
        VALIDATORS.add(validatorBase);
    }

    public static void clearValidator() {
        VALIDATORS.clear();
    }

    private ClassMeta getClassMeta(Map<String, ClassMeta> map, String fqn) {
        ClassMeta classMeta = map.get(fqn);

        if (classMeta == null) {
            try {
                ValidationUtils.ValidationClassMeta vcm = new ValidationUtils.ValidationClassMeta(fqn);

                String packageName = vcm.getPackageName();
                String className = vcm.getClassName();

                JavaFileObject fileObject = filer.createSourceFile(packageName + '.' + className);
                Writer writer = fileObject.openWriter();
                writer.write("package ");
                writer.write(packageName);
                writer.write(";\n\n");

                writer.write("public class ");
                writer.write(className);
                writer.write(" implements ");
                writer.write(Validation.class.getCanonicalName());
                writer.write(" {\n");

                classMeta = new ClassMeta(fqn, writer);
                map.put(fqn, classMeta);
            } catch (IOException ex) {
                Logger.getLogger(ValidationProcessor.class.getName()).log(Level.SEVERE, null, ex);
                return new ClassMeta(null, new StringWriter());
            }
        }

        return classMeta;
    }

    private String indent(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append("  ");
        }

        return sb.toString();
    }

    private String fromResource(String resourceUrl, Object... args) throws IOException {
        InputStream stream = null;
        try {
            stream = getClass().getResourceAsStream(resourceUrl);
            int avail = stream.available();
            byte[] byt = new byte[avail];
            stream.read(byt);
            return String.format(new String(byt), args);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private boolean generateSources(Map<String, ClassMeta> classMetaMap) {
        for (Map.Entry<String, ClassMeta> entry : classMetaMap.entrySet()) {
            ClassMeta meta = entry.getValue();
            Writer writer = meta.getSourceWriter();

            try {
                StringBuilder fieldSB = new StringBuilder();

                for (String fieldName : meta.allFields()) {
                    String fieldClassName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                    StringBuilder valSB = new StringBuilder();
                    for (ValidatorBase fieldValidator : meta.getFieldValidators(fieldName)) {
                        valSB.append(indent(4));
                        valSB.append("new ");
                        valSB.append(fieldValidator.getClass().getCanonicalName());
                        valSB.append("(),\n");
                    }
                    writer.write(fromResource("/kayseven/swing/validation/template/ClassTemplate.txt", fieldName, fieldClassName, meta.getClassFQN(), valSB));
                    writer.write(fromResource("/kayseven/swing/validation/template/MethodTemplate.txt", Validator.class.getCanonicalName(), fieldClassName));
                    writer.flush();

                    fieldSB.append(indent(2));
                    fieldSB.append("validatorMap.put(new ");
                    fieldSB.append(fieldClassName);
                    fieldSB.append("Validator().getFieldName()");
                    fieldSB.append(", get");
                    fieldSB.append(fieldClassName);
                    fieldSB.append("Validator());\n");
                }

                writer.write(fromResource("/kayseven/swing/validation/template/ValidatorMapTemplate.txt",
                        meta.getClassSimpleName(),
                        EntityValidator.class.getCanonicalName(),
                        fieldSB,
                        BindingGroup.class.getCanonicalName(),
                        Binding.class.getCanonicalName(),
                        BindingFinder.class.getCanonicalName(),
                        Validator.class.getCanonicalName()));
                writer.write("}");
                writer.flush();
                writer.close();

            } catch (Exception ex) {
                Logger.getLogger(ValidationProcessor.class.getName()).log(Level.SEVERE, null, ex);
                messager.printMessage(Diagnostic.Kind.ERROR, "Error while generating source: " + ex.getMessage());
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, ClassMeta> classMetaMap = new HashMap<String, ClassMeta>();

        for (TypeElement annotation : annotations) {
            List<ValidatorBase> validatorBases = new ArrayList<ValidatorBase>();
            for (ValidatorBase validatorBase : VALIDATORS) {
                if (annotation.getQualifiedName().toString().equals(validatorBase.getAnnotationClass().getCanonicalName())) {
                    validatorBases.add(validatorBase);
                }
            }

            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element instanceof VariableElement) {
                    VariableElement var = (VariableElement) element;
                    Element clazz = var.getEnclosingElement();

                    if (clazz.getKind() == ElementKind.CLASS) {
                        TypeElement clazzType = (TypeElement) clazz;
                        ClassMeta meta = getClassMeta(classMetaMap, clazzType.getQualifiedName().toString());
                        for (ValidatorBase validatorBase : validatorBases) {
                            meta.addFieldValidator(var.getSimpleName().toString(), validatorBase);
                        }
                    }
                }
            }
        }

        return generateSources(classMetaMap);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> hashSet = new TreeSet<String>();

        for (ValidatorBase validatorBase : VALIDATORS) {
            hashSet.add(validatorBase.getAnnotationClass().getCanonicalName());
        }
//        VALIDATORS.stream().map((validator) -> validator.getAnnotationClass()).forEachOrdered((annotationClass) -> {
//            hashSet.add(annotationClass.getCanonicalName());
//        });

        return hashSet;
    }

}
