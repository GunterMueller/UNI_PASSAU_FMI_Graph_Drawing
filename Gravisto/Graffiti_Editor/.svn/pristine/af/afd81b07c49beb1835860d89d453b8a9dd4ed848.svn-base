package org.graffiti.plugin.view;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.swing.JSlider;

import org.graffiti.graphics.grid.GridParametersAttribute;

/**
 * {@code GridParameter} is annotated to public fields of classes implementing
 * {@code Grid} in order to indicate grid parameters.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see GridParametersAttribute
 */
@Documented
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface GridParameter {
    /**
     * Default value of {@code min()}.
     * 
     * @see #min()
     */
    public static double MIN_DEFAULT = Double.NEGATIVE_INFINITY;

    /**
     * Default value of {@code max()}.
     * 
     * @see #max()
     */
    public static double MAX_DEFAULT = Double.POSITIVE_INFINITY;

    /**
     * Default value of {@code sliderMin()}.
     * 
     * @see #sliderMin()
     */
    public static double SLIDER_MIN_DEFAULT = 0;

    /**
     * Default value of {@code sliderMax()}.
     * 
     * @see #sliderMax()
     */
    public static double SLIDER_MAX_DEFAULT = 100;

    /**
     * Minimum value of the annotated parameter.
     * 
     * @return the minimum value of the annotated parameter.
     */
    double min() default MIN_DEFAULT;

    /**
     * Maximum value of the annotated paramater.
     * 
     * @return the maximum value of the annotated paramater.
     */
    double max() default MAX_DEFAULT;

    /**
     * Minimum value for slider components editing the annotated parameter.
     * 
     * @return the minimum value for slider components editing the annotated
     *         parameter.
     * @see JSlider#JSlider(int, int)
     */
    double sliderMin() default SLIDER_MIN_DEFAULT;

    /**
     * Maximum value for slider components editing the annotated parameter.
     * 
     * @return the maximum value for slider components editing the annotated
     *         parameter.
     * @see JSlider#JSlider(int, int)
     */
    double sliderMax() default SLIDER_MAX_DEFAULT;
}
