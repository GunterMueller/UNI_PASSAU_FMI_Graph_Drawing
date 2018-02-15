package org.graffiti.plugins.editcomponents.yagi;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.util.Pair;

/**
 *
 */
public class DefaultCollectionEditComponent extends CollectionEditComponent {
    public DefaultCollectionEditComponent(Displayable<?> displayable) {
        this(convert(displayable));
    }

    public DefaultCollectionEditComponent(final CollectionAttribute attribute) {
        super(attribute, new CollectionEditWorker() {

            @Override
            protected Pair<String, String> denominateSelf() {
                String description = attribute.getDescription();
                if (description.isEmpty()) {
                    description = attribute.getPath().substring(1);
                }
                return Pair.create(attribute.getId(), description);
            }

            @Override
            protected Pair<String, String> denominate(Attribute attribute,
                    ValueEditComponent vec) {
                String description = attribute.getDescription();
                if (description.isEmpty()) {
                    description = attribute.getPath().substring(1);
                }
                return Pair.create(attribute.getId(), description);
            }
        });
    }

    /**
     * Create a new DefaultCollectionEditComponent for the specified attributes
     */
    public DefaultCollectionEditComponent(final CollectionAttribute[] attributes) {
        super(attributes, new CollectionEditWorker() {

            @Override
            protected Pair<String, String> denominateSelf() {
                String description = attributes[0].getDescription();
                if (description.isEmpty()) {
                    description = attributes[0].getPath().substring(1);
                }
                return Pair.create(attributes[0].getId(), description);
            }

            @Override
            protected Pair<String, String> denominate(Attribute attribute,
                    ValueEditComponent vec) {
                String description = attribute.getDescription();
                if (description.isEmpty()) {
                    description = attribute.getPath().substring(1);
                }
                return Pair.create(attribute.getId(), description);
            }
        });
    }
}
