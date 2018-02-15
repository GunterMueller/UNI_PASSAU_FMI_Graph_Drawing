package org.graffiti.plugins.algorithms.springembedderFR;

import org.graffiti.graph.Edge;

/**
 * Wrapper Class of the original <code>org.graffiti.graph.Edge</code>, to save
 * the special properties for the Fruchterman&Reingold Spring Embedder algorithm
 * 
 * @author matzeder
 * 
 */
public class FREdge {

    /**
     * Original <code>org.graffiti.graph.Edge</code>
     */
    private Edge originalEdge;

    /**
     * The source of the FREdge
     */
    private FRNode source;

    /**
     * The target of the FREdge
     */
    private FRNode target;

    /**
     * 
     * Constructs a new FREdge Object with originalEdge, source and target
     * 
     * @param originalEdge
     *            Edge this Object corresponds to
     * @param source
     *            FRNode, which is source of this FREdge
     * @param target
     *            FRNode, which is target of this FREdge
     */
    public FREdge(Edge originalEdge, FRNode source, FRNode target) {

        this.originalEdge = originalEdge;

        this.source = source;
        this.target = target;
    }

    /**
     * Returns the original edge of this FREdge
     * 
     * @return originalEdge
     */
    public Edge getOriginalEdge() {
        return originalEdge;
    }

    /**
     * Returns the source of this FREdge
     * 
     * @return source
     */
    public FRNode getSource() {
        return source;
    }

    /**
     * Returns the target of this FREdge
     * 
     * @return target
     */
    public FRNode getTarget() {
        return target;
    }

    public double getLength() {

        double deltaX = this.getSource().getXPos() - this.getTarget().getXPos();
        double deltaY = this.getSource().getYPos() - this.getTarget().getYPos();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);

    }

    /**
     * Returns true, if the given node is element of the edge.
     * 
     * @param node
     *            The node to check
     * @return True, if node is element of this edge.
     */
    public boolean isElementOf(FRNode node) {

        GeometricalVector sVector = new GeometricalVector(this.getSource());
        GeometricalVector tVector = new GeometricalVector(this.getTarget());
        LineEquation line = new LineEquation(sVector, GeometricalVector
                .subtract(tVector, sVector));
        GeometricalVector nodeVector = new GeometricalVector(node);
        boolean nodeVectorOnStraightLine = line.isElementOf(nodeVector);

        if (nodeVectorOnStraightLine)
            return GeometricalVector.isPointBetweenSourceAndTarget(nodeVector,
                    sVector, tVector);

        return false;

    }

    /**
     * The output of an edge.
     */
    @Override
    public String toString() {
        return "Edge " + source.getLabel() + "--" + target.getLabel();
    }
}
