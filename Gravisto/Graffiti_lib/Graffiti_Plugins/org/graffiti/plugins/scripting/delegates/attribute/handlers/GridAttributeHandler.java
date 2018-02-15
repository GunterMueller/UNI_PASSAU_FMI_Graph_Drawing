package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegates.GridDelegate;

public class GridAttributeHandler extends AttributeHandler<GridAttribute> {
    @Override
    public Object get(GridAttribute attribute, Scope scope) {
        return scope.getCanonicalDelegate(attribute.getGrid(),
                new GridDelegate.Factory(scope));
    }

    @Override
    public void set(GridAttribute attribute, Object value) {
        attribute.setGrid((Grid) value);
    }
}
