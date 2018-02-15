package org.graffiti.plugins.tools.stylemanager;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;

abstract class ElementStyle implements GraphicAttributeConstants {
    private RendererInfo rendererInfo;

    protected String styleName;

    protected Logger logger = Logger.getLogger(getClass().getName());

    protected Map<String, StyleData> attributes;

    protected ElementStyle(String name, GraphElement element) {
        CollectionAttribute attribute = element.getAttributes();
        styleName = name;
        attributes = new HashMap<String, StyleData>();

        mapAttributes(attribute);
    }

    protected ElementStyle(String name, byte[] bytes) {
        styleName = name;
        attributes = new HashMap<String, StyleData>();

        readByteArray(bytes);
    }
    
    void apply(CollectionAttribute attribute) {
        apply(attribute, null, null);
    }

    void apply(CollectionAttribute attribute, Map<Attribute, Object> map,
            List<Attribute> newAttributes) {

        for (String key : attributes.keySet()) {
            if (attribute.containsAttribute(key)) {
                if (attributes.get(key).getValue() != StyleData.NO_VALUE
                        && !attribute.getAttribute(key).getValue().equals(
                                attributes.get(key).getValue())) {
                    if (map != null)
                        map
                                .put(attribute.getAttribute(key),
                                        ((Attribute) attribute
                                                .getAttribute(key).copy())
                                                .getValue());

                    attribute.getAttribute(key).setValue(
                            attributes.get(key).getValue());
                }
            } else {
                try {
                    addAttribute(attribute, key);
                    if (newAttributes != null)
                        newAttributes.add(attribute.getAttribute(key));
                } catch (InstanceCreationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Attribute addAttribute(CollectionAttribute attribute, String path)
            throws InstanceCreationException {

        String sub;
        CollectionAttribute parent;
        if (!path.contains(".")) {
            parent = attribute;
        } else if (attribute.containsAttribute(sub = path.substring(0, path
                .lastIndexOf('.')))) {
            parent = (CollectionAttribute) attribute.getAttribute(sub);
        } else {
            parent = (CollectionAttribute) addAttribute(attribute, sub);
        }

        // child attribute might by created during recursive call!
        if (attribute.containsAttribute(path)) {
            if (attributes.get(path).getValue() != StyleData.NO_VALUE) {
                attribute.getAttribute(path).setValue(
                        attributes.get(path).getValue());
            }
            return attribute.getAttribute(path);
        }

        String id = path.substring(path.lastIndexOf('.') + 1);
        Attribute attr = (Attribute) InstanceLoader.createInstance(attributes
                .get(path).getAttributeClass(), id);
        if (attributes.get(path).getValue() != StyleData.NO_VALUE) {
            attr.setValue(attributes.get(path).getValue());
        }
        parent.add(attr);
        return attr;
    }

    private void mapAttributes(CollectionAttribute attribute) {
        for (Attribute a : attribute.getCollection().values()) {
            if (ignoreAttribute(a.getPath())) {
                continue;
            } else if (a instanceof CollectionAttribute) {
                attributes.put(a.getPath().substring(1), new StyleData(
                        StyleData.NO_VALUE, a.getClass().getName()));
                mapAttributes((CollectionAttribute) a);
            } else {
                attributes.put(a.getPath().substring(1), new StyleData(a
                        .getValue(), a.getClass().getName()));
            }
        }
    }

    byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeInt(attributes.size());
            for (String key : attributes.keySet()) {

                oos.writeObject(key);
                // oos.writeObject(graphicAttributes.get(key));
                oos.writeObject(handleValue(key,
                        attributes.get(key).getValue(), true));
                oos.writeObject(attributes.get(key).getAttributeClass());
            }
            oos.flush();
        } catch (IOException e) {
            // e.printStackTrace();
            return new byte[] {};
        }

        return baos.toByteArray();
    }

    private void readByteArray(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            int size = ois.readInt();
            String key;
            Object value;
            String c;
            for (int i = 0; i < size; i++) {
                key = (String) ois.readObject();
                value = handleValue(key, ois.readObject(), false);
                c = (String) ois.readObject();
                attributes.put(key, new StyleData(value, c));
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    protected Object handleValue(String key, Object value, boolean output) {
        if (key.compareTo("graphics.backgroundImage.image") == 0) {
            if (output) {
                BufferedImage image = (BufferedImage) value;
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                try {
                    ImageIO.write(image, "png", os);
                    os.flush();
                } catch (IOException e) {
                    logger.log(Level.WARNING, e.getMessage());
                }
                byte[] val = os.toByteArray();
                return val;
            } else {
                ByteArrayInputStream is = new ByteArrayInputStream(
                        (byte[]) value);
                BufferedImage image = null;
                try {
                    image = ImageIO.read(is);
                } catch (IOException e) {
                    logger.log(Level.WARNING, e.getMessage());
                    image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                }
                return image;
            }
        }

        if (key.compareTo("graphics.linemode") == 0) {
            if (output) {
                Dash dash = (Dash) value;
                float[] dashArray = dash.getDashArray();
                if (dashArray == null)
                    return new float[] { dash.getDashPhase() };

                float[] data = Arrays.copyOf(dashArray, dashArray.length + 1);
                data[data.length - 1] = dash.getDashPhase();

                return data;
            } else {
                Dash dash = new Dash();
                float[] data = (float[]) value;
                float[] dashArray = Arrays.copyOf(data, data.length - 1);
                if (data.length > 1) {
                    dash.setDashArray(dashArray);
                }
                dash.setDashPhase(data[data.length - 1]);

                return dash;
            }
        }

        if (output && !(value instanceof Serializable)) {
            logger.log(Level.WARNING, "Cannot write attribute " + key);
            return null;
        }

        return value;
    }

    protected abstract boolean ignoreAttribute(String path);

    public abstract String getDescription();

    String getStyleName() {
        return styleName;
    }

    void setRendererInfo(RendererInfo info) {
        rendererInfo = info;
    }

    RendererInfo getRendererInfo() {
        return rendererInfo;
    }

    protected static class StyleData {

        protected static String NO_VALUE = "NO_VALUE";

        private Object value;

        private String attributeClass;

        private StyleData(Object value, String attributeClass) {
            if (value instanceof String
                    && ((String) value).compareTo(NO_VALUE) == 0) {
                this.value = NO_VALUE;
            } else {
                this.value = value;
            }
            this.attributeClass = attributeClass;
        }

        protected Object getValue() {
            return value;
        }

        protected String getAttributeClass() {
            return attributeClass;
        }
    }
}
