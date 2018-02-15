package org.graffiti.plugins.editcomponents.yagi.grid;

import org.graffiti.plugin.view.Grid;
import org.graffiti.plugin.view.GridParameter;
import org.graffiti.util.Pair;

/**
 * Class representing a parameter specific to a grid type.
 * 
 * @author Andreas Glei&szlig;ner
 * @see Grid
 * @see GridParameter
 */
class GridParameterEntry {
    /**
     * User readable name of the parameter.
     */
    private String name;

    /**
     * User readable description of the parameter.
     */
    private String description;

    /**
     * Minimum value of the parameter.
     */
    private double min;

    /**
     * Maximum value of the parameter.
     */
    private double max;

    /**
     * Minimum value for slider components editing the parameter.
     */
    private double sliderMin;

    /**
     * Maximum value for slider components editing the parameter.
     */
    private double sliderMax;

    /**
     * Constructs a {@code GridParameterEntry}.
     * 
     * @param name
     *            user readable name.
     */
    public GridParameterEntry(String name) {
        this(name, null, GridParameter.MIN_DEFAULT, GridParameter.MAX_DEFAULT,
                GridParameter.SLIDER_MIN_DEFAULT,
                GridParameter.SLIDER_MAX_DEFAULT);
    }

    /**
     * Constructs a {@code GridParameterEntry}.
     * 
     * @param name
     *            user readable name.
     * @param description
     * @param min
     *            minimum value of the parameter.
     * @param max
     *            maximum value of the parameter.
     * @param sliderMin
     *            minimum value for slider components editing the parameter.
     * @param sliderMax
     *            maximum value for slider components editing the parameter.
     */
    public GridParameterEntry(String name, String description, double min,
            double max, double sliderMin, double sliderMax) {
        this.name = name;
        this.description = description;
        this.min = min;
        this.max = max;
        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;
    }

    /**
     * Returns user readable name and description of the parameter.
     * 
     * @return a pair consisting of name and description.
     */
    public Pair<String, String> getDenomination() {
        return Pair.create(name, description);
    }

    /**
     * Returns the minimum value of the parameter.
     * 
     * @return the minimum value of the parameter.
     */
    public double getMin() {
        return min;
    }

    /**
     * Returns the maximum value of the parameter.
     * 
     * @return the maximum value of the parameter.
     */
    public double getMax() {
        return max;
    }

    /**
     * Returns the minimum value for slider components editing the parameter.
     * 
     * @return the minimum value for slider components editing the parameter.
     */
    public double getSliderMin() {
        return sliderMin;
    }

    /**
     * Returns the maximum value for slider components editing the parameter.
     * 
     * @return the maximum value for slider components editing the parameter.
     */
    public double getSliderMax() {
        return sliderMax;
    }
}
