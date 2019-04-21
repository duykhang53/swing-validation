package kayseven.swing.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author K7
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringLength {

    public int value() default -1;

    public int max() default -1;

    public int min() default 0;

    public String errorMessage() default "'%s' has invalid length.";
}
