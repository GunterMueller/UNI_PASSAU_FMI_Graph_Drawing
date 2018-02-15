package org.graffiti.plugins.tools.stylemanager;

import org.graffiti.graph.Edge;

class EdgeStyle extends ElementStyle {
    private static String[] ignoreAttributes = { ".graphics.bends", "*.label",
            "*.position.alignSegment" };

    protected EdgeStyle(String name, Edge edge) {
        super(name, edge);
    }

    protected EdgeStyle(String name, byte[] bytes) {
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
        String shape = (String) attributes.get("graphics.shape").getValue();
        shape = shape.substring(shape.lastIndexOf(".") + 1);

        return StyleManager.resourceBundle.getString("edgestyle.shape") + shape;
    }
}
