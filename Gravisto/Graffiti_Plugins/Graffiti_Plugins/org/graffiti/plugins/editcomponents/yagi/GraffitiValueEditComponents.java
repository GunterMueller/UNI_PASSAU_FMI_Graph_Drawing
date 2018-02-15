//=============================================================================
//
//   GraffitiValueEditComponents.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: GraffitiValueEditComponents.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.ByteAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.EdgeShapeAttribute;
import org.graffiti.attributes.FloatAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.LongAttribute;
import org.graffiti.attributes.NodeShapeAttribute;
import org.graffiti.attributes.ShortAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.DockingAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.EdgeLabelPositionAttribute;
import org.graffiti.graphics.GraphGraphicAttribute;
import org.graffiti.graphics.ImageAttribute;
import org.graffiti.graphics.LineModeAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.graphics.NodeLabelPositionAttribute;
import org.graffiti.graphics.PortsAttribute;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.graphics.grid.GridOriginAttribute;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.FloatParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.ProbabilityParameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.editcomponents.yagi.grid.GridEditComponent;
import org.graffiti.plugins.editcomponents.yagi.grid.GridOriginEditComponent;
import org.graffiti.plugins.editcomponents.yagi.grid.SnapOnGridEditComponent;

/**
 * This class is a plugin providing some default value edit components.
 * 
 * @see org.graffiti.plugin.editcomponent.ValueEditComponent
 * @see org.graffiti.plugin.GenericPlugin
 */
public class GraffitiValueEditComponents extends EditorPluginAdapter {
    public static final String VEC_VALUE = "vecValue";

    /**
     * Constructs a new <code>GraffitiValueEditComponent</code>.
     */
    public GraffitiValueEditComponents() {
        super();

        // register the ui components for the displayable types
        valueEditComponents.put(ColorAttribute.class,
                ColorChooserEditComponent.class);
        valueEditComponents.put(LineModeAttribute.class,
                LineModeEditComponent.class);
        valueEditComponents.put(IntegerAttribute.class,
                IntegerEditComponent.class);
        valueEditComponents.put(ShortAttribute.class, ShortEditComponent.class);
        valueEditComponents.put(LongAttribute.class, LongEditComponent.class);
        valueEditComponents.put(ByteAttribute.class, ByteEditComponent.class);
        valueEditComponents.put(FloatAttribute.class, FloatEditComponent.class);
        valueEditComponents.put(DoubleAttribute.class,
                DoubleEditComponent.class);
        valueEditComponents.put(BooleanAttribute.class,
                BooleanEditComponent.class);
        valueEditComponents.put(NodeShapeAttribute.class,
                NodeShapeEditComponent.class);
        valueEditComponents.put(EdgeShapeAttribute.class,
                EdgeShapeEditComponent.class);
        valueEditComponents.put(StringAttribute.class,
                StringEditComponent.class);
        valueEditComponents.put(GridAttribute.class,
                GridEditComponent.GridEditComponentAdapter.class);
        valueEditComponents.put(
                org.graffiti.graphics.grid.SnapOnGridAttribute.class,
                SnapOnGridEditComponent.class);
        valueEditComponents.put(GridOriginAttribute.class,
                GridOriginEditComponent.class);
        valueEditComponents.put(DimensionAttribute.class,
                DefaultCollectionEditComponent.class);
        valueEditComponents.put(PortsAttribute.class,
                DefaultCollectionEditComponent.class);
        valueEditComponents.put(CoordinateAttribute.class,
                DefaultCollectionEditComponent.class);
        valueEditComponents.put(ImageAttribute.class,
                DefaultCollectionEditComponent.class);
        valueEditComponents.put(DockingAttribute.class,
                DefaultCollectionEditComponent.class);
        valueEditComponents.put(GraphGraphicAttribute.class,
                DefaultCollectionEditComponent.class);
        valueEditComponents.put(NodeGraphicAttribute.class,
                DefaultCollectionEditComponent.class);
        valueEditComponents.put(EdgeGraphicAttribute.class,
                DefaultCollectionEditComponent.class);
        valueEditComponents.put(EdgeLabelAttribute.class,
                DefaultCollectionEditComponent.class);
        valueEditComponents.put(EdgeLabelPositionAttribute.class,
                DefaultCollectionEditComponent.class);
        valueEditComponents.put(NodeLabelAttribute.class,
                DefaultCollectionEditComponent.class);
        valueEditComponents.put(NodeLabelPositionAttribute.class,
                DefaultCollectionEditComponent.class);

        // register the ui components for the parameter types
        valueEditComponents.put(IntegerParameter.class,
                IntegerEditComponent.class);
        valueEditComponents.put(DoubleParameter.class,
                DoubleEditComponent.class);
        valueEditComponents.put(ProbabilityParameter.class,
                ProbabilityEditComponent.class);
        valueEditComponents.put(FloatParameter.class, FloatEditComponent.class);
        valueEditComponents.put(StringParameter.class,
                StringEditComponent.class);
        valueEditComponents.put(BooleanParameter.class,
                BooleanEditComponent.class);
        valueEditComponents.put(StringSelectionParameter.class,
                StringSelectionEditComponent.class);
        valueEditComponents.put(ImageAttribute.class, ImageEditComponent.class);
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
