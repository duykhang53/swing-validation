package kayseven.swing.validation;

import java.util.Set;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Validator;

/**
 *
 * @author K7
 */
public interface Validation {

    void applyValidator(BindingGroup bindingGroup);

    void applyValidator(BindingGroup bindingGroup, BindingFinder finder);

    Set<String> allFieldNames();

    Validator getValidator(String fieldName);
}
