package org.graffiti.plugins.editcomponents.yagi.grid;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.JComponent;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;

/**
 * {@code ValueEditComponent}, which provides a {@code SingletonComponent} to
 * edit attributes of type {@code A}. {@link SingletonComponent} s are
 * components that have at most one instance per attributable and attribute in
 * any point of time. This is beneficial if the construction process of the
 * component is expensive and there is no need for simultaneous components.
 * While the {@code SingletonAdapter} may be created often, it does on its part
 * not each time create its own component but rather looks up for an existing
 * one in a map. This class may be moved up in the package hierarchy in a later
 * version, if it proves useful.
 * 
 * @param <A>
 *            the type of attribute the provided component edits.
 */
abstract class SingletonAdapter<A extends Attribute> extends
        AbstractValueEditComponent {
    /**
     * Maps from attributables and attributes to components editing them. The
     * components implement {@link SingletonComponent}.
     */
    private static WeakHashMap<Attributable, Map<Class<? extends Attribute>, JComponent>> components;

    /**
     * The provided component. It implements {@link SingletonAdapter}.
     */
    private JComponent component;

    /**
     * Constructs a {@code SingletonAdapter}.
     * 
     * @param displayable
     *            the displayable to edit, which must be of type {@code A}.
     * @throws IllegalArgumentException
     *             if {@code displayable} is not of type {@code A}.
     */
    public SingletonAdapter(Displayable<?> displayable) {
        super(displayable);
        Class<A> attributeClass = getAttributeClass();
        if (!attributeClass.isInstance(displayable))
            throw new IllegalArgumentException();
        A attribute = attributeClass.cast(displayable);
        Attributable attributable = ((Attribute) displayable).getAttributable();
        if (components == null) {
            components = new WeakHashMap<Attributable, Map<Class<? extends Attribute>, JComponent>>();
        }
        Map<Class<? extends Attribute>, JComponent> map = components
                .get(attributable);
        if (map == null) {
            map = new HashMap<Class<? extends Attribute>, JComponent>();
            if (attributable != null) {
                components.put(attributable, map);
            }
        }
        component = map.get(attributeClass);

        if (component == null) {
            component = (JComponent) createJComponent();
            if (attributable != null) {
                map.put(attributeClass, component);
            }
        }

        @SuppressWarnings("unchecked")
        SingletonComponent<A> cc = (SingletonComponent<A>) component;
        cc.setAttribute(attribute);
    }

    /**
     * Returns the provided component.
     */
    public final JComponent getComponent() {
        return component;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setDispEditFieldValue() {
        @SuppressWarnings("unchecked")
        SingletonComponent<A> cc = (SingletonComponent<A>) component;
        cc.setEditFieldValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setDispValue() {
        @SuppressWarnings("unchecked")
        SingletonComponent<A> cc = (SingletonComponent<A>) component;
        cc.setValue();
    }

    /**
     * Eventually creates a new appropriate {@code SingletonComponent}. It is
     * only called if there is no component available for the intended
     * attributable and attribute to edit.
     * 
     * @return a new appropriate {@code SingletonComponent}.
     */
    protected abstract SingletonComponent<A> createJComponent();

    /**
     * Returns the {@code Class}-object for {@code A}. This method is needed as
     * a runtime type information supplement for the erased generic type {@code
     * A}.
     * 
     * @return the {@code Class}-object for {@code A}.
     */
    protected abstract Class<A> getAttributeClass();
}
