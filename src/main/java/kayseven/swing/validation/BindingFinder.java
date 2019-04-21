package kayseven.swing.validation;

import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;

/**
 *
 * @author K7
 */
public interface BindingFinder {

    Binding findBinding(BindingGroup bindingGroup, String fieldName);
}
