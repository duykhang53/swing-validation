package kayseven.swing.validation.validator;

import java.lang.reflect.Field;
import kayseven.swing.validation.annotations.StringLength;
import org.jdesktop.beansbinding.Validator;

/**
 *
 * @author K7
 */
public class StringLengthValidator extends ValidatorBase<StringLength, Object> {
    public static final Object ERROR_TOO_SHORT = new Object();
    public static final Object ERROR_TOO_LONG = new Object();

    @Override
    public Class<StringLength> getAnnotationClass() {
        return StringLength.class;
    }

    protected Validator.Result checkLength(String value, StringLength annotation, Field field, Validator<Object> validator, int min, int max) {
        if (value.length() < min) {
            return validator.new Result(ERROR_TOO_SHORT, errorMessage(annotation, field));
        }

        if (max > -1 && value.length() > max) {
            return validator.new Result(ERROR_TOO_LONG, errorMessage(annotation, field));
        }

        return null;
    }

    @Override
    public Validator.Result validate(Object value, StringLength annotation, Field field, Validator<Object> validator) {
        if (value != null) {
            String strVal = value instanceof String ? (String) value : value.toString();
            if (strVal.isEmpty()) {
                return null;
            }

            int min;
            int max;

            if (annotation.value() > -1) {
                min = max = annotation.value();
            } else {
                min = annotation.min();
                max = annotation.max();
            }

            return checkLength(strVal, annotation, field, validator, min, max);
        }

        return null;
    }

}
