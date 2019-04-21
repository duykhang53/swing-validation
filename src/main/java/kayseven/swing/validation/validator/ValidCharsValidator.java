package kayseven.swing.validation.validator;

import java.lang.reflect.Field;
import java.util.TreeSet;
import kayseven.swing.validation.annotations.ValidChars;
import org.jdesktop.beansbinding.Validator;

/**
 *
 * @author K7
 */
public class ValidCharsValidator extends ValidatorBase<ValidChars, String> {

    public static final Object ERROR_INVALID_CHARS = new Object();

    @Override
    public Class<ValidChars> getAnnotationClass() {
        return ValidChars.class;
    }

    @Override
    public Validator.Result validate(String value, ValidChars annotation, Field field, Validator<String> validator) {
        if (value != null && !value.isEmpty()) {
            TreeSet<Character> set = new TreeSet<Character>();
            String annoVal = annotation.value();
            for (int i = 0; i < annoVal.length(); i++) {
                set.add(annoVal.charAt(i));
            }

            for (int i = 0; i < value.length(); i++) {
                if (!set.contains(value.charAt(i))) {
                    return validator.new Result(ERROR_INVALID_CHARS, errorMessage(annotation, field));
                }
            }
        }

        return null;
    }

}
