package kayseven.swing.validation;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kayseven.swing.validation.validator.ValidatorBase;

/**
 *
 * @author K7
 */
class ClassMeta {

    private final Writer sourceWriter;
    private final Map<String, List<ValidatorBase>> fieldMetaMap;
    private final String classFQN;

    public ClassMeta(String classFQN, Writer sourceWriter) {
        this.classFQN = classFQN;
        this.sourceWriter = sourceWriter;
        fieldMetaMap = new HashMap<String, List<ValidatorBase>>();
    }

    public synchronized void addFieldValidator(String fieldName, ValidatorBase validator) {
        List<ValidatorBase> vals = fieldMetaMap.get(fieldName);

        if (vals == null) {
            vals = new ArrayList<ValidatorBase>();
            fieldMetaMap.put(fieldName, vals);
        }
        if (validator != null && !vals.contains(validator)) {
            vals.add(validator);
        }
    }

    public Set<String> allFields() {
        return fieldMetaMap.keySet();
    }

    public List<ValidatorBase> getFieldValidators(String fieldName) {
        return fieldMetaMap.getOrDefault(fieldName, Collections.<ValidatorBase>emptyList());
    }

    public Writer getSourceWriter() {
        return sourceWriter;
    }

    public String getClassFQN() {
        return classFQN;
    }

    public String getClassSimpleName() {
        if (classFQN == null) {
            return null;
        }

        int dotIdx = classFQN.lastIndexOf(".");

        return dotIdx > -1 ? classFQN.substring(dotIdx + 1) : classFQN;
    }
}
