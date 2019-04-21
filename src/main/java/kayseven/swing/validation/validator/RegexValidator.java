package kayseven.swing.validation.validator;

import java.lang.reflect.Field;
import kayseven.swing.validation.annotations.RegexPattern;
import org.jdesktop.beansbinding.Validator;

/**
 *
 * @author K7
 */
public class RegexValidator extends ValidatorBase<RegexPattern, String> {
    public static final Object ERROR_NOT_MATCHED = new Object();
    @Override
    public Class<RegexPattern> getAnnotationClass() {
        return RegexPattern.class;
    }

    protected Validator.Result isMatch(String value, RegexPattern annotation, Field field, Validator<String> validator, String regex) {
        if (value != null && !value.matches(regex)) {
            return validator.new Result(ERROR_NOT_MATCHED, errorMessage(annotation, field));
        }

        return null;
    }

    @Override
    public Validator.Result validate(String value, RegexPattern annotation, Field field, Validator<String> validator) {
        return isMatch(value, annotation, field, validator, annotation.value());
    }

}
