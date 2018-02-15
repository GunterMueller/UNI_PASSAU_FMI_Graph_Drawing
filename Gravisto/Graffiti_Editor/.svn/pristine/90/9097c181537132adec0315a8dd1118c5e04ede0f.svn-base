package quoggles.representation;

import java.awt.geom.Point2D;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.IBox;

/**
 * Interface for classes that specify properties that affect how the
 * graphical representation will look like. This includes for example the 
 * position of inputs and outputs.
 */
public interface IBoxRepresentation {

    /**
     * Gets a representation that can be added to the visual system.
     */
    public BoxRepresentation getRepresentation();
    
    /**
     * Called whenever something (parameters, number of IO, ...) changed that
     * requires a change in the looks of the box's graphicsal representation.
     * <p>
     * Also useful to be called in the constructor of an 
     * <code>IBoxRepresentation</code>.<p>
     * <b>Implementation node:</b><p>
     * Don't assign to the <code>graphicalRep</code> field. Just add components
     * to it and adjust its size.
     *
     */
    public void updateGraphicalRep();
    
    /**
     * Called when the ID changed (e.g. the box number).
     *
     */
    public void updateId();

    /**
     * Sets the parameters for this representation.
     * If the second parameter is false, the <code>setParameters</code> method
     * of the box associated with this representation is called (with the
     * second parameter set to true).
     * 
     * Should be overridden and called by subclasses with parameters. They then
     * update the values of the representations of the parameters with the new 
     * values of the set parameters.
     * 
     * @param params
     * @param fromBox
     */
    public void setParameters(Parameter[] params, boolean fromBox);

//    /**
//     * Relative position of input, measured from top left corner,
//     * values between 0 and 1.
//     */
//    public Point2D getRelInputPos();

    /**
     * Relative position of outputs, measured from top left corner,
     * values between 0 and 1.
     * Only used if the box has several outputs.
     */
    public Point2D[] getRelOutputsPos();
    
    /**
     * Relative position of inputs, measured from top left corner,
     * values between 0 and 1.
     * Only used if the box has several inputs.
     */
    public Point2D[] getRelInputsPos();

//    /**
//     * Relative position of output, measured from top left corner,
//     * values between 0 and 1.
//     */
//    public Point2D getRelOutputPos();
    
    /**
     * Returns the IBox it represents.
     * 
     * @return
     */
    public IBox getIBox();

//    /**
//     * Tells the <code>IBox</code> that parameters have changed.
//     */
//    public void parametersChanged();
}
