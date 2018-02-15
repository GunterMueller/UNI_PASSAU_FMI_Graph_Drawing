package org.graffiti.plugins.editcomponents.yagi.grid;

import javax.swing.JComponent;

import org.graffiti.attributes.Attribute;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.ValueEditComponent;

/**
 * Classes implementing {@code SingletonComponent} edit attributes of type
 * {@code A} and have at most one instance per attributable and attribute in any
 * point of time. This is beneficial if the construction process of the
 * component is expensive and there is no need for simultaneous components.
 * While the {@link SingletonAdapter} may be created often, it does on its part
 * not each time create its own component but rather looks up for an existing
 * one in a map. Each class implementing {@code SingletonComponent} must be a
 * subclass of {@link JComponent}. This interface may be moved up in the package
 * hierarchy in a later version, if it proves useful.
 */
public interface SingletonComponent<A extends Attribute> {
    /**
     * Sets the attribute to edit. The component should possibly rebuild its
     * subcomponents. This method plays the cheap part of the r&ocirc;le of
     * constructors in standard value edit components, while the expensive part
     * remains in the constructor.
     * 
     * @param attribute
     *            the attribute to edit.
     * @see ValueEditComponent
     */
    public void setAttribute(A attribute);

    /**
     * Sets the current value of the {@code Displayable} in the corresponding
     * {@code JComponent}.
     * 
     * @see Displayable
     * @see JComponent
     * @see ValueEditComponent#setEditFieldValue()
     */
    public void setEditFieldValue();

    /**
     * Sets the value of the {@code Displayable} specified in the {@code
     * JComponent}. Should only change the value if the value is in fact
     * different otherwise too many events will be sent.
     * 
     * @see Displayable
     * @see JComponent
     * @see ValueEditComponent#setValue()
     */
    public void setValue();
}
