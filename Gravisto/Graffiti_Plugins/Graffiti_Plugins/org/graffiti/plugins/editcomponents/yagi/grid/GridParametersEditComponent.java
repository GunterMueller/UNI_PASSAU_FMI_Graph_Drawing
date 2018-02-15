package org.graffiti.plugins.editcomponents.yagi.grid;

import javax.swing.JComponent;

import org.graffiti.attributes.Attribute;
import org.graffiti.core.Bundle;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.graphics.grid.GridParametersAttribute;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugins.editcomponents.yagi.CollectionEditComponent;
import org.graffiti.plugins.editcomponents.yagi.CollectionEditWorker;
import org.graffiti.plugins.editcomponents.yagi.SliderEditComponent;
import org.graffiti.util.Pair;

/**
 * {@code CollectionEditComponent}, which provides a component for editing a
 * {@code GridParametersAttribute}.
 * 
 * @author Andreas Glei&szlig;ner
 * @see GridParametersAttribute
 */
public class GridParametersEditComponent extends CollectionEditComponent {
    /**
     * Constructs a {@code GridParametersEditComponent}.
     * 
     * @param gpa
     *            the {@code GridParametersAttribute} to edit.
     * @param gcca
     *            the {@code GridClassComboAdapter} representing the type of the
     *            current grid set in the attribute.
     */
    public GridParametersEditComponent(GridParametersAttribute gpa,
            final GridClassComboAdapter gcca) {
        super(gpa, new CollectionEditWorker() {
            /**
             * {@inheritDoc}
             */
            @Override
            protected void componentAdded(Attribute attribute,
                    ValueEditComponent vec, JComponent component) {
                if (vec instanceof SliderEditComponent) {
                    SliderEditComponent sec = (SliderEditComponent) vec;
                    GridParameterEntry gpe = gcca.getParameterEntry(attribute
                            .getId());
                    sec.setLimits(gpe.getSliderMin(), gpe.getSliderMax(), gpe
                            .getMin(), gpe.getMax());
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected Pair<String, String> denominate(Attribute attribute,
                    ValueEditComponent vec) {
                return gcca.getParameterEntry(attribute.getId())
                        .getDenomination();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected String getL13nKey() {
                return GraphicAttributeConstants.GRID_PATH
                        + Attribute.SEPARATOR + GridAttribute.GRID_PARAMETERS;
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
