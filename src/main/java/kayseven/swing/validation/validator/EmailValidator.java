package kayseven.swing.validation.validator;

import java.lang.reflect.Field;
import java.util.regex.Pattern;
import kayseven.swing.validation.annotations.Email;
import org.jdesktop.beansbinding.Validator;

/**
 *
 * @author K7
 */
public class EmailValidator extends ValidatorBase<Email, String> {

    public static final Object ERROR_NOT_MATCHED = new Object();
    private static Pattern emailPattern;

    private static synchronized Pattern getEmailPattern() {
        if (emailPattern == null) {
            emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        }

        return emailPattern;
    }

    @Override
    public Class<Email> getAnnotationClass() {
        return Email.class;
    }

    @Override
    public Validator.Result validate(String value, Email annotation, Field field, Validator<String> validator) {
        if (value != null && !value.isEmpty()) {
            if (!getEmailPattern().matcher(value).matches()) {
                return validator.new Result(ERROR_NOT_MATCHED, errorMessage(annotation, field));
            }
        }

        return null;
    }

}
