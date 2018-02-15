package org.graffiti.plugins.editcomponents.yagi.grid;

import static org.graffiti.plugins.editcomponents.yagi.GraffitiValueEditComponents.VEC_VALUE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.graffiti.attributes.Attribute;
import org.graffiti.core.Bundle;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.graphics.grid.SnapOnGridAttribute;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugins.editcomponents.yagi.CollectionEditComponent;
import org.graffiti.plugins.editcomponents.yagi.CollectionEditWorker;
import org.graffiti.plugins.editcomponents.yagi.DoubleEditComponent;

/**
 * {@code CollectionEditComponent}, which provides a component for editing a
 * {@code SnapOnGridAttribute}.
 * 
 * @author Kathrin Hanauer
 * @author Andreas Glei&szlig;ner
 * @see SnapOnGridAttribute
 */
public class SnapOnGridEditComponent extends CollectionEditComponent {
    /**
     * Constructs a {@code SnapOnGridEditComponent}.
     * 
     * @param displayable
     *            the displayable to edit, which must be a
     *            {@link SnapOnGridAttribute}.
     * @throws IllegalArgumentException
     *             if {@code displayable} is not a {@code SnapOnGridAttribute}.
     */
    public SnapOnGridEditComponent(final Displayable<?> displayable) {
        super(convert(displayable), new CollectionEditWorker() {
            /**
             * Component editing the
             * {@link SnapOnGridAttribute.SnapEnabledAttribute}.
             */
            private JCheckBox enabledCheckbox;

            /**
             * {@inheritDoc}
             */
            @Override
            protected void componentAdded(Attribute attribute,
                    ValueEditComponent vec, JComponent component) {
                if ((attribute instanceof SnapOnGridAttribute.ToleranceAttribute)
                        && (vec instanceof DoubleEditComponent)) {
                    DoubleEditComponent dec = (DoubleEditComponent) vec;
                    dec.setLimits(0.0, 100.0, 0.0, Double.MAX_VALUE);
                } else if (attribute instanceof SnapOnGridAttribute.SnapEnabledAttribute) {
                    if (!(component instanceof JCheckBox)) {
                        Logger.getAnonymousLogger().warning(
                                "Edit component for BooleanAttribute"
                                        + " is not JCheckbox anymore.");
                        return;
                    }
                    enabledCheckbox = (JCheckBox) component;
                    component.addPropertyChangeListener(VEC_VALUE,
                            new PropertyChangeListener() {
                                public void propertyChange(
                                        PropertyChangeEvent evt) {
                                    enableComponents(enabledCheckbox
                                            .isSelected());
                                }
                            });
                }
            }

            /**
             * Enables/Disables the subcomponents if the snapping behavior is
             * enabled/disabled.
             * 
             * @param enabled
             *            denotes if the snapping behavior is enabled.
             */
            private void enableComponents(boolean enabled) {
                for (ValueEditComponent vec : getValueEditComponents()) {
                    if (vec.getComponent() != enabledCheckbox) {
                        vec.setEnabled(enabled);
                    }
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void postConstruction() {
                enableComponents(((SnapOnGridAttribute) displayable)
                        .isEnabled());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected String getL13nKey() {
                return GraphicAttributeConstants.GRID_PATH
                        + Attribute.SEPARATOR + GridAttribute.GRID_SNAP;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected Bundle getResourceBundle() {
                return GridEditComponent.getResourceBundle();
            }
        });
    }
}
