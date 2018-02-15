package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegates.attribute.CollectionAttributeDelegate;

/**
 * {@code AttributeHandler} for {@code CollectionAttribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 */
final class CollectionAttributeHandler extends
        AttributeHandler<CollectionAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object get(CollectionAttribute attribute, Scope scope) {
        return new CollectionAttributeDelegate(scope, attribute);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(CollectionAttribute attribute, Object value) {
    }
}