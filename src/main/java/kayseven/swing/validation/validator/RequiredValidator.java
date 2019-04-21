package kayseven.swing.validation.validator;

import java.lang.reflect.Field;
import kayseven.swing.validation.annotations.Required;
import org.jdesktop.beansbinding.Validator;
import org.jdesktop.beansbinding.Validator.Result;

/**
 *
 * @author K7
 */
public class RequiredValidator extends ValidatorBase<Required, Object> {

    public static final Object ERROR_EMPTY = new Object();
    public static final Object ERROR_EMPTY_STRING = new Object();
    public static final Object ERROR_WHITESPACE_STRING = new Object();

    @Override
    public final Class<Required> getAnnotationClass() {
        return Required.class;
    }

    @Override
    public Result validate(Object value, Required annotation, Field field, Validator<Object> validator) {
        if (value == null) {
            return validator.new Result(ERROR_EMPTY, errorMessage(annotation, field));
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if ("".equals(strVal)) {
                return validator.new Result(ERROR_EMPTY_STRING, errorMessage(annotation, field));
            }

            if (!annotation.whitespaceAllowed() && "".equals(strVal.trim())) {
                return validator.new Result(ERROR_WHITESPACE_STRING, errorMessage(annotation, field));
            }
        }

        return null;
    }

}
