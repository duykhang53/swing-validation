package kayseven.swing.validation;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import kayseven.swing.validation.validator.ValidatorBase;
import org.jdesktop.beansbinding.Validator;

/**
 *
 * @author K7
 */
public abstract class EntityValidator<T> extends Validator<T> {
    protected abstract ValidatorBase[] createValidators();

    public abstract String getFieldName();

    protected abstract Class getValidatingClass() throws ClassNotFoundException;

    private ValidatorBase[] validators;

    private synchronized ValidatorBase[] getValidators() {
        if (validators == null) {
            validators = createValidators();
        }

        return validators;
    }

    @Override
    public Result validate(T value) {
        Field field = null;

        try {
            field = getValidatingClass().getDeclaredField(getFieldName());
        } catch (Exception ex) {
            Logger.getLogger(EntityValidator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        for (ValidatorBase validator : getValidators()) {
            Result result = validator.validate(value, field.getAnnotation(validator.getAnnotationClass()), field, this);

            if (result != null) {
                return result;
            }
        }

        return null;
    }

}
