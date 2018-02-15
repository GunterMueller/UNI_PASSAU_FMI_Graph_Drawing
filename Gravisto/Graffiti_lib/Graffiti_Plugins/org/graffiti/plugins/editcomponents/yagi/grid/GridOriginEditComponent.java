package org.graffiti.plugins.editcomponents.yagi.grid;

import javax.swing.JComponent;

import org.graffiti.attributes.Attribute;
import org.graffiti.core.Bundle;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.graphics.grid.GridOriginAttribute;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugins.editcomponents.yagi.CollectionEditComponent;
import org.graffiti.plugins.editcomponents.yagi.CollectionEditWorker;
import org.graffiti.plugins.editcomponents.yagi.SliderEditComponent;

/**
 * {@code CollectionEditComponent}, which provides a component for editing a
 * {@code GridOriginAttribute}.
 * 
 * @author Andreas Glei&szlig;ner
 * @see GridOriginAttribute
 */
public class GridOriginEditComponent extends CollectionEditComponent {
    /**
     * Constructs a {@code GridOriginEditComponent}.
     * 
     * @param displayable
     *            the displayable to edit, which must be a
     *            {@link GridOriginAttribute}.
     * @throws IllegalArgumentException
     *             if {@code displayable} is not a {@code GridOriginAttribute}.
     */
    public GridOriginEditComponent(Displayable<?> displayable) {
        super(convert(displayable), new CollectionEditWorker() {
            /**
             * {@inheritDoc}
             */
            @Override
            protected void componentAdded(Attribute attribute,
                    ValueEditComponent vec, JComponent component) {
                if (!(vec instanceof SliderEditComponent))
                    return;
                ((SliderEditComponent) vec).setLimits(-600.0, 600.0,
                        Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected String getL13nKey() {
                return GraphicAttributeConstants.GRID_PATH
                        + Attribute.SEPARATOR + GridAttribute.GRID_ORIGIN;
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
