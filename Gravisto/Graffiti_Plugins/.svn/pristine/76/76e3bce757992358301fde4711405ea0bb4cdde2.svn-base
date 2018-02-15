package org.graffiti.plugins.tools.stylemanager;

import org.graffiti.graph.Node;

class NodeStyle extends ElementStyle {
    private static String[] ignoreAttributes = { ".graphics.coordinate",
            "*.label" };

    NodeStyle(String name, Node node) {
        super(name, node);
    }

    NodeStyle(String name, byte[] bytes) {
        super(name, bytes);
    }

    @Override
    protected boolean ignoreAttribute(String path) {
        for (String ia : ignoreAttributes)
            if (path.startsWith(ia) || ia.startsWith("*")
                    && path.endsWith(ia.substring(1))
                    && ia.substring(1).compareTo(path) != 0)
                return true;
        return false;
    }

    @Override
    public String getDescription() {
        double width = (Double) attributes.get("graphics.dimension.width")
                .getValue();
        double height = (Double) attributes.get("graphics.dimension.height")
                .getValue();

        return StyleManager.resourceBundle.getString("nodestyle.size") + width
                + "x" + height;
    }
}
