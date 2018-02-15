package org.graffiti.plugins.editcomponents.yagi.grid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugin.view.GridParameter;

/**
 * Adapter class, which represents an item in a {@code JComboBox} for selecting
 * {@code Grid} types.
 * 
 * @author Andreas Glei&szlig;ner
 * @see JComboBox
 * @see Grid
 */
class GridClassComboAdapter {
    /**
     * The default icon to display if a more specific icon could not be created
     * for a grid type.
     */
    public static final Icon DEFAULT_ICON = new ImageIcon(createImage());

    /**
     * The width of icons representing grid types.
     */
    private static final int WIDTH = 64;

    /**
     * The height of icons representing grid types.
     */
    private static final int HEIGHT = 64;

    /**
     * A grid is scaled by this factor on the x-axis when the representing icon
     * is created.
     */
    private static final double SCALE_X = 0.2;

    /**
     * A grid is scaled by this factor on the x-axis when the representing icon
     * is created.
     */
    private static final double SCALE_Y = 0.2;

    /**
     * {@code Class}-object representing the grid type.
     */
    private Class<? extends Grid> gridClass;

    /**
     * Icon representing the grid type.
     */
    private Icon icon;

    /**
     * User readable name of the grid type.
     */
    private String name;

    /**
     * User readable description of the grid type.
     */
    private String description;

    /**
     * Maps the entries representing grid type specific parameters from their
     * id.
     */
    private Map<String, GridParameterEntry> parameters;

    /**
     * Holds the state of the grid when this grid type was selected the last
     * time.
     */
    private Grid historyGrid;

    /**
     * Constructs a {@code GridClassComboAdapter} for the specified grid type.
     * 
     * @param gridClass
     *            the {@code Class}-object representing the grid type.
     * @param isKeyOnly
     *            if {@code true}, this is just used as a key in a {@link Map}.
     *            The fields besides {@code gridClass} will not be initialized.
     */
    public GridClassComboAdapter(Class<? extends Grid> gridClass,
            boolean isKeyOnly) {
        this.gridClass = gridClass;
        if (isKeyOnly)
            return;
        try {
            Grid grid = gridClass.newInstance();
            double width = WIDTH / SCALE_X + 20;
            double height = HEIGHT / SCALE_Y + 20;
            List<Shape> shapes = grid.getShapes(new Rectangle2D.Double(
                    -width / 2, -height / 2, width, height));
            BufferedImage image = createImage();
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(Color.BLACK);
            graphics.translate(WIDTH / 2, HEIGHT / 2);
            graphics.scale(SCALE_X, SCALE_Y);

            for (Shape shape : shapes) {
                graphics.draw(shape);
            }

            graphics.dispose();
            icon = new ImageIcon(image);

            Bundle bundle = Bundle.getBundle(gridClass);

            name = null;
            description = null;

            if (bundle != null) {
                name = bundle.getString("name");
                description = bundle.getString("description");
            }

            if (name == null) {
                name = gridClass.getSimpleName();
            }

            if (description == null) {
                description = "";
            }

            parameters = new HashMap<String, GridParameterEntry>();
            for (Field field : gridClass.getDeclaredFields()) {
                field.setAccessible(true);
                addEntryForField(field, bundle);
            }
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
    }

    /**
     * Adds an entry representing the grid type specific parameter held by the
     * specified field.
     * 
     * @param field
     *            the field holding the grid type specific parameter to
     *            represent by the {@link GridParameterEntry} to add. If the
     *            field is not annotated by {@link GridParameter}, it is
     *            ignored.
     * @param bundle
     *            resource bundle where the name and the description of the grid
     *            type specific parameter are tried to be obtained from. May be
     *            {@code null}. Name and description are looked up at the keys
     *            {@code "parameter." + id + ".name"} and {@code "parameter." +
     *            id + ".description"} where {@code id} is the name of the
     *            specified field.
     * 
     * @see Grid
     * @see Field#getName()
     */
    private void addEntryForField(Field field, Bundle bundle) {
        GridParameter gp = field.getAnnotation(GridParameter.class);
        if (gp == null)
            return;
        String id = field.getName();

        String name = null;
        String description = null;

        if (bundle != null) {
            name = bundle.getString(String.format(Grid.NAME_PATTERN, id)) + ":";
            description = bundle.getString(String.format(
                    Grid.DESCRIPTION_PATTERN, id));

        }

        if (name == null) {
            name = id;
        }

        if (description == null) {
            description = "";
        }

        parameters.put(id, new GridParameterEntry(name, description, gp.min(),
                gp.max(), gp.sliderMin(), gp.sliderMax()));
    }

    /**
     * Returns the {@code Class}-object representing the grid type.
     * 
     * @return the {@code Class}-object representing the grid type.
     */
    public Class<? extends Grid> getGridClass() {
        return gridClass;
    }

    /**
     * Returns the icon representing the grid type.
     * 
     * @return the icon representing the grid type.
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * Returns a user readable name of the grid type.
     * 
     * @return a user readable name of the grid type.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a user readable description of the grid type.
     * 
     * @return a user readable description of the grid type.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Creates a new, empty {@code BufferedImage}, which can be used to draw a
     * grid in, in order to create an icon for its grid type.
     * 
     * @return a new, empty {@code BufferedImage}.
     */
    private static BufferedImage createImage() {
        BufferedImage image = new BufferedImage(64, 64,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.fillRect(0, 0, 64, 64);
        graphics.dispose();
        return image;
    }

    /**
     * {@inheritDoc} This implementation returns {@code true} if the other
     * object is a {@code GridClassComboAdapter} representing the same grid
     * type.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof GridClassComboAdapter)
                && ((GridClassComboAdapter) obj).gridClass.equals(gridClass);
    }

    /**
     * {@inheritDoc} This implementation returns the hash code of the {@code
     * Class}-object representing the grid type represented by this.
     * 
     * @see #equals(Object)
     */
    @Override
    public int hashCode() {
        return gridClass.hashCode();
    }

    /**
     * Returns the grid parameter entry for the field with the specified name.
     * 
     * @param id
     *            the name of the field holding the grid type specific parameter
     *            represented by the entry to return.
     * 
     * @return the grid parameter entry for the field with the specified name or
     *         {@code null} if there is no such field.
     */
    public GridParameterEntry getParameterEntry(String id) {
        GridParameterEntry entry = parameters.get(id);
        if (entry == null) {
            entry = new GridParameterEntry(id);
        }
        return entry;
    }

    /**
     * Returns the grid that was active when this grid type was selected the
     * last time.
     * 
     * @return the grid that was active when this grid type was selected the
     *         last time.
     */
    public Grid getHistoryGrid() {
        return historyGrid;
    }

    /**
     * Sets the grid that was active when this grid type was selected the last
     * time.
     * 
     * @param historyGrid
     *            the grid that was active when this grid type was selected the
     *            last time.
     */
    public void setHistoryGrid(Grid historyGrid) {
        this.historyGrid = historyGrid;
    }
}
