package kayseven.swing.validation.validator;

import java.lang.reflect.Field;
import java.util.regex.Pattern;
import kayseven.swing.validation.annotations.Digit;
import org.jdesktop.beansbinding.Validator;

/**
 *
 * @author K7
 */
public class DigitValidator extends ValidatorBase<Digit, Object> {

    public static final Object ERROR_NOT_MATCHED = new Object();
    private static Pattern digitPattern;

    private static synchronized Pattern getDigitPattern() {
        if (digitPattern == null) {
            digitPattern = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE);
        }

        return digitPattern;
    }

    @Override
    public Class<Digit> getAnnotationClass() {
        return Digit.class;
    }

    @Override
    public Validator.Result validate(Object value, Digit annotation, Field field, Validator<Object> validator) {
        if (value != null) {
            if (value instanceof Number) {
                return null;
            }

            Class valClazz = value.getClass();

            if (valClazz.isPrimitive() && !boolean.class.equals(valClazz) && !char.class.equals(valClazz)) {
                return null;
            }

            if (char.class.equals(valClazz)) {
                return validateString(String.valueOf((Character) value), annotation, field, validator);
            }

            if (String.class.equals(valClazz)) {
                return validateString((String) value, annotation, field, validator);
            }
        }

        return null;
    }

    protected Validator.Result validateString(String value, Digit annotation, Field field, Validator<Object> validator) {
        if (!value.isEmpty() && !getDigitPattern().matcher(value).matches()) {
            return validator.new Result(ERROR_NOT_MATCHED, errorMessage(annotation, field));
        }

        return null;
    }
}
