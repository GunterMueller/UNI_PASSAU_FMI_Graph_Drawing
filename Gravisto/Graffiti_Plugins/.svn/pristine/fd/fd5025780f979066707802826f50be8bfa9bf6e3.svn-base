package org.graffiti.plugins.editcomponents.yagi;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.managers.EditComponentManager;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;
import org.graffiti.plugin.editcomponent.StandardValueEditComponent;
import org.graffiti.plugin.editcomponent.ValueEditComponent;

/**
 * Factory class to create the appropriate value edit components editing {@code
 * Displayable}s. It by default creates the value edit component suggested by
 * the global {@link EditComponentManager}. This behavior may be overridden.
 * 
 * @author Andreas Glei&szlig;ner
 * @see Displayable
 * @see ValueEditComponent
 * @see MainFrame#getEditComponentManager()
 */
public class ValueEditComponentFactory implements GraphicAttributeConstants {
    /**
     * Maps from the {@code Class}-objects representing {@code Displayable}
     * types to the {@code Class}-objects representing the kind of
     * {@link ValueEditComponent} to create, which is obtained from the global
     * {@link EditComponentManager}.
     * 
     * @see MainFrame#getEditComponentManager()
     */
    private static Map<Class<? extends Displayable<?>>, Class<? extends ValueEditComponent>> map;

    /**
     * Constructs a {@code ValueEditComponentFactory}.
     */
    public ValueEditComponentFactory() {
        if (map == null) {
            map = new HashMap<Class<? extends Displayable<?>>, Class<? extends ValueEditComponent>>();

            for (Map.Entry<Class<?>, Class<?>> entry : GraffitiSingleton
                    .getInstance().getMainFrame().getEditComponentManager()
                    .getEditComponents().entrySet()) {
                @SuppressWarnings("unchecked")
                Class<? extends Displayable<?>> displayableClass = (Class<? extends Displayable<?>>) entry
                        .getKey().asSubclass(Displayable.class);
                map.put(displayableClass, entry.getValue().asSubclass(
                        ValueEditComponent.class));
            }
        }
    }

    /**
     * Creates a new {@code ValueEditComponent} editing the specified {@code
     * Displayable}. The default implementation creates the value edit component
     * suggested by the global {@link EditComponentManager}.
     * 
     * @param displayable
     *            the displayable to edit by the value edit component to create.
     * @return a new {@link ValueEditComponent} editing the specified
     *         {@link Displayable}.
     * @see EditComponentManager#getEditComponents()
     */
    public ValueEditComponent createComponent(Displayable<?> displayable) {
        Class<?> attrClass = displayable.getClass();
        String attributeName = displayable.getName();
        ValueEditComponent editComponent = null;
        Class<?> vecClass = null;
        while (attrClass != null && vecClass == null) {
            vecClass = map.get(attrClass);
            vecClass = getIndividualVEC(attributeName, vecClass);

            attrClass = attrClass.getSuperclass();

        }
        if (vecClass == null) {
            vecClass = StandardValueEditComponent.class;
        }

        try {
            editComponent = (ValueEditComponent) vecClass.getConstructor(
                    new Class<?>[] { Displayable.class }).newInstance(
                    displayable);

            if (editComponent instanceof SliderEditComponent) {
                setSliderEditComponentSettings(attributeName,
                        (SliderEditComponent) editComponent);
            }
            editComponent.setEditFieldValue();
            return editComponent;
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (NoSuchMethodException e) {
        } catch (ClassCastException e) {
        }

        return null;
    }

    /**
     * Creates a simple wrapping value edit component, which maintains the
     * specified component in order to edit the specified {@code Displayable}.
     * 
     * @param displayable
     *            the {@link Displayable} to edit by the returned value edit
     *            component.
     * @param component
     *            the {@link JComponent} to maintain by the returned value edit
     *            component.
     * @return a simple wrapping value edit component, which maintains the
     *         specified component in order to edit the specified {@code
     *         Displayable}.
     */
    protected ValueEditComponent wrap(Displayable<?> displayable,
            final JComponent component) {
        return new AbstractValueEditComponent(displayable) {

            public JComponent getComponent() {
                return component;
            }

            @Override
            protected void setDispEditFieldValue() {
            }

            @Override
            protected void setDispValue() {
            }
        };
    }

    private void setSliderEditComponentSettings(String attributeName,
            SliderEditComponent editComponent) {
        // set slider limits
        if (attributeName.equals("x")) {
            // coordinate x
            editComponent
                    .setLimits(0, 640, -Double.MAX_VALUE, Double.MAX_VALUE);
        } else if (attributeName.equals("y")) {
            // coordinate y
            editComponent
                    .setLimits(0, 480, -Double.MAX_VALUE, Double.MAX_VALUE);
        } else if (attributeName.equals("z")) {
            // coordinate z (depth)
            editComponent.setLimits(0.0, 1.0, 0.0, 1.0);
        } else if (attributeName.equals("depth")) {
            // edge depth
            editComponent.setLimits(0.0, 1.0, 0.0, 1.0);
        } else if (attributeName.equals("width")) {
            // node width
            editComponent.setLimits(0, 100, 0, Double.MAX_VALUE);
        } else if (attributeName.equals("height")) {
            // node height
            editComponent.setLimits(0, 100, 0, Double.MAX_VALUE);
        } else if (attributeName.equals("relHor")) {
            // node label horizontal
            editComponent.setLimits(-5, 5, -Double.MAX_VALUE, Double.MAX_VALUE);
        } else if (attributeName.equals("relVert")) {
            // node label vertical
            editComponent.setLimits(-5, 5, -Double.MAX_VALUE, Double.MAX_VALUE);
        } else if (attributeName.equals("localAlign")) {
            // node label local alignment
            editComponent.setLimits(-20, 20, -Double.MAX_VALUE,
                    Double.MAX_VALUE);
        }

        else if (attributeName.equals("absoluteXOffset")) {
            // node label absolute X offset
            editComponent.setLimits(-25, 25, -Integer.MAX_VALUE,
                    Integer.MAX_VALUE);
        } else if (attributeName.equals("absoluteYOffset")) {
            // node label absolute Y offset
            editComponent.setLimits(-25, 25, -Integer.MAX_VALUE,
                    Integer.MAX_VALUE);
        } else if (attributeName.equals("relativeXOffset")) {
            // node label relative X offset
            editComponent.setLimits(-1.0, 1.0, -Double.MAX_VALUE,
                    Double.MAX_VALUE);
        } else if (attributeName.equals("relativeYOffset")) {
            // node label relative Y offset
            editComponent.setLimits(-1.0, 1.0, -Double.MAX_VALUE,
                    Double.MAX_VALUE);
        }

        else if (attributeName.equals("frameThickness")) {
            // node/edge frame thickness
            editComponent.setLimits(0, 20, 0, Double.MAX_VALUE);
        } else if (attributeName.equals(THICKNESS)) {
            // edge arrow thickness
            editComponent.setLimits(0, 20, 0, Double.MAX_VALUE);
        } else if (attributeName.equals(ROTATION)) {
            editComponent.setLimits(-180, 180, -Double.MAX_VALUE,
                    Double.MAX_VALUE);
        } else if (attributeName.equals(RELATIVE_ALIGNMENT)) {
            // edge label relative
            editComponent.setLimits(0, 1, 0, 1);
        } else if (attributeName.equals(ALIGNMENT_SEGMENT)) {
            // edge label segment
            editComponent.setLimits(0, 20, 0, Integer.MAX_VALUE);
        } else if (attributeName.equals("green")) {
            // color green
            editComponent.setLimits(0, 255, 0, 255);
        } else if (attributeName.equals("red")) {
            // color red
            editComponent.setLimits(0, 255, 0, 255);
        } else if (attributeName.equals("blue")) {
            // color blue
            editComponent.setLimits(0, 255, 0, 255);
        } else if (attributeName.equals("transparency")) {
            // color transparency
            editComponent.setLimits(0, 255, 0, 255);
        }
    }

    private Class<?> getIndividualVEC(String attributeName, Class<?> vecClass) {
        if (vecClass == null && attributeName.equals("image")) {
            vecClass = NotEditableEditComponent.class;
        } else if (vecClass != null
                && vecClass.getName().equals(
                        "org.graffiti.plugins."
                                + "editcomponents.yagi.StringEditComponent")) {
            vecClass = getVECforStringEditComponent(attributeName, vecClass);
        } else if (attributeName.equals("tiled")
                || attributeName.equals("maximize")) {
            vecClass = NotEditableEditComponent.class;
        } else if (attributeName.equals("fontSize")) {
            vecClass = FontSizeEditComponent.class;
        } else if (attributeName.equals("bends")) {
            vecClass = DefaultCollectionEditComponent.class;
        } else if (vecClass != null
                && vecClass.getName().equals(
                        "org.graffiti.plugins."
                                + "editcomponents.yagi.IntegerEditComponent")
                && attributeName.equals(ALIGNMENT_SEGMENT)) {
            // we don't want to use the default SliderVEC for
            // .position.alignSegment for edge labels
            vecClass = EdgeLabelSegmentEditComponent.class;
        }
        return vecClass;
    }

    private Class<?> getVECforStringEditComponent(String attributeName,
            Class<?> vecClass) {
        if (attributeName.equals("alignmentX")) {
            // we don't want to use the default StringVEC for
            // labels.alignmentX
            vecClass = NodeLabelXAlignmentEditComponent.class;
        }
        if (attributeName.equals("alignmentY")) {
            // we don't want to use the default StringVEC for
            // labels.alignmentY
            vecClass = NodeLabelYAlignmentEditComponent.class;
        }

        else if (attributeName.equals("reference")) {
            vecClass = NotEditableEditComponent.class;
        } else if (attributeName.equals("font")) {
            // we don't want to use the default StringVEC for
            // labels.font
            vecClass = FontFaceEditComponent.class;
        } else if (attributeName.equals("arrowhead")
                || attributeName.equals("arrowtail")) {
            // we don't want to use the default StringVEC for
            // .graphics.arrowhead or .graphics.arrowtail
            vecClass = ArrowShapeEditComponent.class;
        }
        return vecClass;
    }
}
