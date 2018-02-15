package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.graph.Graph;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugins.views.fast.FastView;

public class GridAttributeHandler extends AttributeHandler<GridAttribute> {
    public GridAttributeHandler() {
        super(GRID);
    }

    @Override
    protected boolean acceptsAttribute(Graph graph, GridAttribute attribute) {
        return equalsPath(attribute, GRID_PATH);
    }

    @Override
    protected void onChange(Graph graph, GridAttribute attribute,
            FastView fastView) {
        fastView.setGrid(attribute.getGrid());
    }
}
