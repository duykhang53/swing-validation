package kayseven.swing.validation.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.jdesktop.beansbinding.Validator;

/**
 *
 * @author K7
 * @param <TAnnotation>
 * @param <T>
 */
public abstract class ValidatorBase<TAnnotation extends Annotation, T> {

    public abstract Class<TAnnotation> getAnnotationClass();

    public abstract Validator.Result validate(T value, TAnnotation annotation, Field field, Validator<T> validator);

    protected String errorMessage(TAnnotation annotation, Field field) {
        String fieldName = field.getName();

        try {
            Method method = annotation.getClass().getMethod("errorMessage");
            return String.format((String) method.invoke(annotation), fieldName);
        } catch (Exception ex) {
            return String.format("Validation error for field '%s'", fieldName);
        }
    }
}
