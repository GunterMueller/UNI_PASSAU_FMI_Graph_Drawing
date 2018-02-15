package org.graffiti.plugins.editcomponents.yagi;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.core.Bundle;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.VECChangeEvent;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugin.editcomponent.ValueEditComponentListener;
import org.graffiti.util.Pair;

/**
 * This class maintains a {@code JComponent} to edit collection attributes. It
 * is separated from {@link CollectionEditComponent} in order to be usable by
 * subclasses of both {@link JComponent} and {@link ValueEditComponent}.
 * Customized behavior may be provided by subclasses of {@code
 * CollectionEditWorker}. The organization of the maintained component is
 * two-stage: On construction of {@code CollectionEditWorker}, the component
 * gets its layout and border. Each time it is assigned an attribute by
 * {@link #build(CollectionAttribute)}, it is rebuilt by removing old
 * subcomponents and adding new subcomponents.
 * 
 * @author Andreas Glei&szlig;ner
 */
public class CollectionEditWorker implements ValueEditComponentListener,
        SelfLabelingComponent {
    /**
     * The component to maintain.
     */
    private JComponent component;

    /**
     * The value edit subcomponents.
     */
    private LinkedList<ValueEditComponent> children;

    /**
     * The J subcomponents.
     */
    private LinkedList<JComponent> oldComponents;

    /**
     * The the factory to create appropriate new subcomponents for
     * subattributes.
     */
    private ValueEditComponentFactory componentFactory;

    /**
     * The layout of the maintained component.
     */
    private GridBagLayout gridBagLayout;

    /**
     * Constraints to use with {@link #gridBagLayout}.
     */
    private GridBagConstraints gridBagConstraints;

    /**
     * The VEC that uses the worker.
     */
    private ValueEditComponent parentVEC;

    /**
     * Denotes if the maintained component does contain a label for itself.
     * 
     * @see #isSelfLabeling()
     * @see SelfLabelingComponent
     */
    private boolean isSelfLabelling;

    /**
     * Constructs a new {@code CollectionEditWorker}, which maintains a new
     * {@code JPanel} and uses the default {@code ValueEditComponentFactory}.
     * 
     * @see JPanel
     * @see ValueEditComponentFactory
     */
    public CollectionEditWorker() {
        this(new JPanel());
    }

    /**
     * Constructs a new {@code CollectionEditWorker}, which maintains the
     * specified component and uses the default {@code
     * ValueEditComponentFactory}.
     * 
     * @param component
     *            the component to maintain.
     * @see ValueEditComponentFactory
     */
    public CollectionEditWorker(JComponent component) {
        this(component, new ValueEditComponentFactory());
    }

    /**
     * Constructs a new {@code CollectionEditWorker}, which maintains a new
     * {@code JPanel} and uses the specified {@code ValueEditComponentFactory}.
     * 
     * @param componentFactory
     *            the {@link ValueEditComponentFactory} to use.
     * @see JPanel
     */
    public CollectionEditWorker(ValueEditComponentFactory componentFactory) {
        this(new JPanel(), componentFactory);
    }

    /**
     * Constructs a new {@code CollectionEditWorker}, which maintains the
     * specified component and uses the specified {@code
     * ValueEditComponentFactory}.
     * 
     * @param component
     *            the component to maintain.
     * @param componentFactory
     *            the {@link ValueEditComponentFactory} to use.
     */
    public CollectionEditWorker(JComponent component,
            ValueEditComponentFactory componentFactory) {
        this.component = component;
        this.componentFactory = componentFactory;
        oldComponents = new LinkedList<JComponent>();
        children = new LinkedList<ValueEditComponent>();

        gridBagLayout = new GridBagLayout();
        gridBagConstraints = new GridBagConstraints();
        component.setLayout(gridBagLayout);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        Pair<String, String> denomination = denominateSelf();
        isSelfLabelling = denomination != null;
        if (isSelfLabelling) {
            component.setBorder(BorderFactory.createTitledBorder(denomination
                    .getFirst()));
            String description = denomination.getSecond();
            if (description != null) {
                component.setToolTipText(description);
            }
        }
    }

    /**
     * Returns the maintained component.
     * 
     * @return the maintained component.
     */
    public JComponent getComponent() {
        return component;
    }

    /**
     * Set the VEC that uses the worker.
     */
    public void setValueEditComponent(ValueEditComponent vec) {
        parentVEC = vec;
    }

    /**
     * Assigns the specified attribute and rebuilds the maintained component by
     * removing the old subcomponents and adding new subcomponents for all
     * subattributes of the specified attribute.
     * 
     * @param attribute
     *            the attribute to assign.
     */
    public final void build(CollectionAttribute attribute) {
        build(new CollectionAttribute[] { attribute });
    }

    /**
     * Assigns the specified attribute and rebuilds the maintained component by
     * removing the old subcomponents and adding new subcomponents for all
     * subattributes of the specified attribute.
     * 
     * @param attributes
     *            the attributes to assign.
     */
    public final void build(CollectionAttribute[] attributes) {
        CollectionAttribute attribute = attributes[0];
        if (attributes.length > 1) {
            // try to find attribute with the minimum number of children
            int size = Integer.MAX_VALUE;
            for (CollectionAttribute attr : attributes) {
                if (size > attr.getCollection().size()) {
                    size = attr.getCollection().size();
                    attribute = attr;
                }
            }
        }
        for (JComponent oldComponent : oldComponents) {
            component.remove(oldComponent);
        }

        oldComponents.clear();
        children.clear();

        for (Attribute attr : attribute.getCollection().values()) {
            ValueEditComponent vec = componentFactory.createComponent(attr);
            if (vec == null) {
                continue;
            }

            if (attributes.length > 1) {
                Attribute[] attrs = new Attribute[attributes.length];
                String key = attr.getId();
                for (int i = 0; i < attributes.length; i++) {
                    attrs[i] = attributes[i].getCollection().get(key);
                }
                vec.setDisplayables(attrs);
            } else {
                vec.setDisplayable(attr);
            }

            children.addLast(vec);
            JComponent child = vec.getComponent();
            boolean useLabel = (!(vec instanceof SelfLabelingComponent) || !((SelfLabelingComponent) vec)
                    .isSelfLabeling())
                    && (!(child instanceof SelfLabelingComponent) || !((SelfLabelingComponent) child)
                            .isSelfLabeling());
            Pair<String, String> denomination = null;
            JLabel label = null;
            if (useLabel) {
                denomination = denominate(attr, vec);
                label = new JLabel(denomination.getFirst(),
                        SwingConstants.RIGHT);
                gridBagConstraints.gridwidth = GridBagConstraints.RELATIVE;
                gridBagLayout.setConstraints(label, gridBagConstraints);
                oldComponents.add(label);
                component.add(label);
            }

            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagLayout.setConstraints(child, gridBagConstraints);
            oldComponents.add(child);
            component.add(child);

            if (denomination != null) {
                String description = denomination.getSecond();
                if (description != null && !description.isEmpty()) {
                    label.setToolTipText(description);
                    child.setToolTipText(description);
                }
            }

            // child.addPropertyChangeListener(VEC_VALUE, this);
            vec.addVECChangeListener(this);

            componentAdded(attr, vec, child);
            component.validate();
        }
        postConstruction();
    }

    /**
     * Makes all value edit subcomponents to set the current value of the
     * {@code Displayable} in the corresponding {@code JComponent}.
     * 
     * @param showEmpty
     *            if {@code showEmpty} is {@code true}, the maintained component
     *            should instead show only empty fields.
     * @see Displayable
     * @see JComponent
     * @see ValueEditComponent#setEditFieldValue()
     */
    public final void setEditFieldValue(boolean showEmpty) {
        if (!showEmpty) {
            for (ValueEditComponent child : children) {
                child.setEditFieldValue();
            }
        }
    }

    /**
     * Makes all value edit subcomponents to set the value of the {@code
     * Displayable} specified in the {@code JComponent}, if the value is in fact
     * different.
     * 
     * @see Displayable
     * @see JComponent
     * @see ValueEditComponent#setValue()
     */
    public final void setValue() {
        for (ValueEditComponent child : children) {
            child.setValue();
        }
    }

    // /**
    // * Reacts to changes of the {@code GraffitiValueEditComponents.VEC_VALUE}
    // * property in one of the subcomponents. It calls
    // * {@link ValueEditComponent#setValue()} it the respective value edit
    // * subcomponent.
    // *
    // * @param evt the {@code PropertyChangeEvent} representing the value
    // change.
    // * @see GraffitiValueEditComponents#VEC_VALUE
    // */
    // public final void propertyChange(PropertyChangeEvent evt)
    // {
    // Object source = evt.getSource();
    // component.firePropertyChange(VEC_VALUE, -1,
    // oldComponents.indexOf(source));
    //        
    // // component.firePropertyChange(VEC_VALUE, true, false);
    // // might already have been carried out by PropertyChangeListener
    // // via setValue(); however it shouldn't do any harm to do it twice
    // for (ValueEditComponent vec : children)
    // {
    // if (vec.getComponent() == source)
    // {
    // vec.setValue();
    // }
    // }
    // }

    public void vecChanged(VECChangeEvent event) {
        if (parentVEC != null) {
            parentVEC.fireVECChanged(event);
        } else {
            System.err.println("Warning: CollectionEditWorker: no parentVec ("
                    + getClass() + ")");
        }
    }

    /**
     * Returns all value edit subcomponents.
     * 
     * @return all value edit subcomponents.
     */
    protected final LinkedList<ValueEditComponent> getValueEditComponents() {
        return children;
    }

    /**
     * Returns user readable name and description for the specified
     * subattribute. The default implementation tries to get a resource bundle
     * by {@link #getResourceBundle()} and obtain name and description by
     * querying for the keys {@code path + ".name"} and {@code path +
     * ".description"} where {@code path} is the path of the specified
     * attribute.
     * 
     * @param attribute
     *            the attribute to denominate.
     * @param vec
     *            the value edit component editing the attribute to denominate.
     * @return a pair containing user readable name and description of the
     *         specified subattribute or {@code null} if no denomination is
     *         available. If the name is available but not a description, the
     *         second entry of the returned pair may be {@code null}.
     */
    protected Pair<String, String> denominate(Attribute attribute,
            ValueEditComponent vec) {
        String name = null;
        String description = null;

        Bundle bundle = getResourceBundle();
        if (bundle != null) {
            String path = attribute.getPath();
            name = bundle.getString(path + ".name");
            description = bundle.getString(path + ".description");
        }

        return Pair
                .create(name == null ? attribute.getId() : name, description);
    }

    /**
     * Returns the localization key useful for self-denomination. If
     * {@link #denominateSelf()} is overridden, this method can be ignored.
     * 
     * @return the localization key useful for self-denomination or {@code null}
     *         if no self-denomination is available.
     */
    protected String getL13nKey() {
        return null;
    }

    /**
     * Returns user readable name and description for the attribute edited by
     * the maintained component. The default implementation tries to get a
     * resource bundle by {@link #getResourceBundle()} and obtain name and
     * description by querying for the keys {@code key + ".name"} and {@code key
     * + ".description"} where {@code key} is obtained from
     * {@link #getL13nKey()}.
     * 
     * @return a pair containing user readable name and description for the
     *         attribute edited by the maintained component or {@code null} if
     *         no self-denomination is available. If the name is available but
     *         not a description, the second entry of the returned pair may be
     *         {@code null}.
     */
    protected Pair<String, String> denominateSelf() {
        String name = null;
        String description = null;
        String l13nKey = getL13nKey();
        if (l13nKey == null)
            return null;

        Bundle bundle = getResourceBundle();
        if (bundle != null) {
            name = bundle.getString(l13nKey + ".name");
            description = bundle.getString(l13nKey + ".description");
        }

        return name == null ? null : Pair.create(name, description);
    }

    /**
     * Is called after a value edit subcomponent has been created by the used
     * {@code ValueEditComponentFactory} in order to edit the specified
     * subattribute. The default implementation does nothing.
     * 
     * @param attribute
     *            the subattribute to edit.
     * @param vec
     *            the created value edit subcomponent editing the specified
     *            attribute.
     * @param component
     *            the {@code JComponent} maintained by the specified value edit
     *            component.
     * @see ValueEditComponentFactory
     */
    protected void componentAdded(Attribute attribute, ValueEditComponent vec,
            JComponent component) {
    }

    /**
     * Is called by {@code build(CollectionAttribute)} after the rebuild
     * process. The default implementation does nothing.
     * 
     * @see #build(CollectionAttribute)
     */
    protected void postConstruction() {
    }

    /**
     * Returns a resource bundle used by the denomination methods. If they are
     * overridden, this method can be ignored.
     * 
     * @return a resource bundle used by the denomination methods. May be
     *         {@code null}.
     * @see #denominate(Attribute, ValueEditComponent)
     * @see #denominateSelf()
     */
    protected Bundle getResourceBundle() {
        return null;
    }

    /**
     * {@inheritDoc} This implementation returns {@code true} and automatically
     * labels its maintained component if it can denominate it by
     * {@link #denominateSelf()}.
     */
    public final boolean isSelfLabeling() {
        return isSelfLabelling;
    }

    /**
     * Sets the current value of the <code>Displayable</code> in the
     * corresponding <code>JComponent</code> for the child whose path matches
     * <code>childPath</code>.
     * 
     * @param childPath
     *            the child attribute's path
     */
    public void setEditFieldValue(String childPath) {

        for (ValueEditComponent vec : children) {
            if (childPath.startsWith(((Attribute) vec.getDisplayable())
                    .getPath())) {
                if (vec instanceof CollectionEditComponent) {
                    ((CollectionEditComponent) vec)
                            .setEditFieldValue(childPath);
                } else {
                    vec.setEditFieldValue();
                }
            }
        }
    }
}
