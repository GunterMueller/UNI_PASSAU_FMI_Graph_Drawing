package org.graffiti.plugins.views.fast.triggers;

import org.graffiti.plugin.view.interactive.Trigger;
import org.graffiti.plugins.views.fast.FastViewPlugin;

public abstract class FastViewTrigger extends Trigger {
    private static String getFullId(Trigger parent, String relativeId) {
        String parentId = parent.getId();
        return parentId == "" ? relativeId : parentId + "." + relativeId;
    }

    private static String getName(Trigger parent, String relativeId) {
        return FastViewPlugin.getString("triggers."
                + getFullId(parent, relativeId) + ".name");
    }

    private static String getDescription(Trigger parent, String relativeId) {
        return FastViewPlugin.getString("triggers."
                + getFullId(parent, relativeId) + ".desc");
    }

    public FastViewTrigger(Trigger parent, String relativeId) {
        super(parent, relativeId, getName(parent, relativeId), getDescription(
                parent, relativeId));
    }
}
