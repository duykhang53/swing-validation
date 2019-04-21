package kayseven.swing.validation;

import java.util.HashMap;
import java.util.Map;
import org.jdesktop.beansbinding.BindingGroup;

/**
 *
 * @author K7
 */
public class ValidationUtils {

    public static final String SUFFIX_CLASS = "Validation";
    private static final Map<Class<?>, Validation> VALIDATION_MAP
            = new HashMap<Class<?>, Validation>();

    private static final Map<Class, Class<?>> VALIDATION_CLASS_MAP
            = new HashMap<Class, Class<?>>();

    public static <T extends Validation> void applyValidator(BindingGroup bindingGroup, Class<T> validationClass) {
        try {
            T inst = getValidatorInner(validationClass);
            inst.applyValidator(bindingGroup);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Class<?> getModelValidatorClass(Class modelClass) throws ClassNotFoundException {
        Class<?> cls;

        if (null == (cls = VALIDATION_CLASS_MAP.get(modelClass))) {
            ValidationClassMeta meta = new ValidationClassMeta(modelClass.getCanonicalName());
            cls = Class.forName(meta.getPackageName() + "." + meta.getClassName());
            VALIDATION_CLASS_MAP.put(modelClass, cls);
        }

        return cls;
    }

    public static <T extends Validation> T getModelValidator(Class modelClass) {
        try {
            Class<?> validatorClass = getModelValidatorClass(modelClass);
            return getValidatorInner(validatorClass);
        } catch (Exception ex) {
            return null;
        }
    }

    private static <T extends Validation> T getValidatorInner(Class<?> validationClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        T inst;

        if (null == (inst = (T) VALIDATION_MAP.get(validationClass))) {
            inst = (T) validationClass.newInstance();
            VALIDATION_MAP.put(validationClass, inst);
        }

        return inst;
    }

    public static <T extends Validation> void applyModelValidator(BindingGroup bindingGroup, Class modelClass) {
        try {
            applyValidator(bindingGroup, (Class<T>) getModelValidatorClass(modelClass));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class ValidationClassMeta {

        private final String className;
        private final String packageName;

        public String getClassName() {
            return className;
        }

        public String getPackageName() {
            return packageName;
        }

        public ValidationClassMeta(String fqn) {
            int dIdx = fqn.lastIndexOf(".");
            String pkg = "validators";
            String cls;

            if (dIdx > -1) {
                pkg = fqn.substring(0, dIdx) + '.' + pkg;
                cls = fqn.substring(dIdx + 1);
            } else {
                cls = fqn;
            }
            cls += ValidationUtils.SUFFIX_CLASS;

            className = cls;
            packageName = pkg;
        }
    }
}
