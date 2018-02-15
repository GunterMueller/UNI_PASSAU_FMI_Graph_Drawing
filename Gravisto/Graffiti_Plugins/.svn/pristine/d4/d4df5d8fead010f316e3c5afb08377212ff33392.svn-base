package org.graffiti.plugins.algorithms.phyloTrees.exceptions;

/**
 * Exception to indicate, that an unknown Node Placement has been returned.
 */
public class UnknownNodePlacementException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 5238956736066405048L;
    /** The name of the illegal Node placement. */
    private String placementName;

    /**
     * Creates a new Exception.
     * 
     * @param placementName
     *            The name of the illegal Node placement.
     */
    public UnknownNodePlacementException(String placementName) {
        this.placementName = placementName;
    }

    /**
     * Message explaining the error.
     */
    @Override
    public String getMessage() {
        return "Node position " + placementName + " is unknown.";
    }

}
