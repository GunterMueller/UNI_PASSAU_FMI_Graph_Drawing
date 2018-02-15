package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.graphics.LineModeAttribute;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegates.DashDelegate;

/**
 * {@code AttributeHandler} for {@code LineModeAttribute}s.
 */
public class LineModeAttributeHandler extends
        AttributeHandler<LineModeAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object get(LineModeAttribute attribute, Scope scope) {
        return scope.getCanonicalDelegate(attribute.getValue(),
                new DashDelegate.Factory(scope));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(LineModeAttribute attribute, Object value) {
        attribute.setValue(((DashDelegate) value).unwrap());
    }
}
