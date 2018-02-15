// =============================================================================
//
//   AngleMatrix.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * An <code>AngleMatrix</code> object is needed for building the Angle LP.
 * 
 * @author Mirka Kossak
 */
public class AngleMatrix extends Matrix {
    // all inner faces of the graph
    private Face[] innerFaces;

    private int numberOfInnerFaces;

    private int outerFaceIndex;

    // the outer face of the graph
    private Face outerFace;

    private Graph graph;

    private TestedGraph testedGraph;

    private int numberOfFaces;

    private Face[] faces;

    private Angle[] anglesInMatrix;

    private HashMap<Node, ArrayList<Angle>> nodeAngleMap;

    /**
     * Constructs a new <code>AngleMatrix<code>. 
     * Sets the <code>Face</code> with the most
     * <code>org.graffiti.graph.Node</code>'s as the outerface of the graph.
     * 
     * @param faces
     *            All <code>Face</code>'s of the
     *            <code>org.graffiti.graph.Graph</code>.
     * @param graph
     *            The <code>org.graffiti.graph.Graph</code>.
     * @param testedGraph
     *            The embedding of the <code>org.graffiti.graph.Graph</code>.
     */
    public AngleMatrix(Face[] faces, Graph graph, TestedGraph testedGraph) {
        this.faces = faces;
        this.numberOfFaces = faces.length;
        this.testedGraph = testedGraph;
        this.graph = graph;
        nodeAngleMap = new HashMap<Node, ArrayList<Angle>>();
        numberOfInnerFaces = numberOfFaces - 1;
        innerFaces = new Face[numberOfInnerFaces];
        outerFace = faces[0];
        setOuterFaceIndex(0);
        int outerFaceNumberOfNodes = outerFace.getNumberOfNodes();
        for (int i = 1; i < faces.length; i++) {
            if (faces[i].getNumberOfNodes() > outerFaceNumberOfNodes) {
                setOuterFaceIndex(i);
                outerFaceNumberOfNodes = faces[i].getNumberOfNodes();
            }
        }
        Face temp = faces[0];
        faces[0] = faces[getOuterFaceIndex()];
        faces[getOuterFaceIndex()] = temp;
        setOuterFaceIndex(0);
        outerFace = faces[0];
        for (int i = 0; i < numberOfInnerFaces; i++) {
            if (i < outerFaceIndex) {
                innerFaces[i] = faces[i];
            } else {
                innerFaces[i] = faces[i + 1];
            }
        }
    }

    /**
     * Builds the angle matrix for the angle lp.
     * 
     */
    @Override
    public void makeMatrix() {
        initNodeAngleMap();
        int angles = this.graph.getNumberOfEdges() * 2;
        this.setNumberOfRows(this.graph.getNumberOfNodes() + this.numberOfFaces
                + angles + 1);
        this.setNumberOfColumns(angles * 2 + 1);
        init(this.getNumberOfRows(), this.getNumberOfColumns(), 0);
        int currentColumn = 0;
        int faceRow = this.graph.getNumberOfNodes();
        int anglesIndex = 0;
        anglesInMatrix = new Angle[angles];
        for (int i = 0; i < numberOfFaces; i++) {
            Face currentFace = faces[i];
            int numberOfAngles = currentFace.getNumberOfAngles();
            LinkedList<Angle> currentAngles = currentFace.getAngles();
            for (int j = 0; j < numberOfAngles; j++) {
                Angle currentAngle = currentAngles.get(j);
                currentAngle.setFace(currentFace);
                anglesInMatrix[anglesIndex] = currentAngle;
                int nodeInMatrix = getNumberOfNodeInMatrix(currentAngle
                        .getVertex());
                ArrayList<Angle> anglesList = nodeAngleMap.get(currentAngle
                        .getVertex());
                anglesList.add(currentAngle);

                this.setValue(nodeInMatrix, currentColumn++, 1);
                this.setValue(faceRow, anglesIndex++, 1);
            }
            faceRow++;
        }
        int currentRow = this.graph.getNumberOfNodes()
                + this.getNumberOfFaces();
        for (int k = currentRow; k < currentRow + angles; k++) {
            this.setValue(k, k - currentRow, -1);
            this.setValue(k, angles, 1);
            this.setValue(k, k + angles + 1 - currentRow, 1);
        }
        this.setValue(currentRow + angles, angles, 1);
    }

    /**
     * Inits the map from which you can get for every node a list with all
     * angles this node is the vertex of.
     * 
     */
    public void initNodeAngleMap() {
        List<Node> nodes = graph.getNodes();
        for (Iterator<Node> nodesIt = nodes.iterator(); nodesIt.hasNext();) {
            Node current = nodesIt.next();
            ArrayList<Angle> anglesList = new ArrayList<Angle>();
            nodeAngleMap.put(current, anglesList);
        }
    }

    /**
     * Returns the map from which you can get for every node a list with all
     * angles this node is the vertex of.
     * 
     * @return The nodeAngleMap.
     */
    public HashMap<Node, ArrayList<Angle>> getNodeAngleMap() {
        return this.nodeAngleMap;
    }

    /**
     * Returns the index of the <code>org.graffiti.graph.Node</code> in the
     * matrix.
     * 
     * @param node
     * @return Index
     */
    public int getNumberOfNodeInMatrix(Node node) {
        int index;
        List<Node> tNodes = testedGraph.getNodes();
        for (index = 0; index < tNodes.size(); index++) {
            if (tNodes.get(index) == node) {
                break;
            }
        }
        return index;
    }

    /**
     * Changes a row in the matrix and returns it. That is used when an angle
     * has a value for sure and does not need its slack anymore.
     * 
     * @param row
     *            The row which has to changed.
     * @return The changed row.
     */
    public double[] getChangedRow(int row) {
        double[] changedRow = new double[this.getNumberOfColumns() + 1];
        changedRow[0] = 0;
        for (int i = 1; i <= this.getNumberOfColumns(); i++) {
            if (this.getValue(row, i - 1) == -1) {
                changedRow[i] = 1;
            } else {
                changedRow[i] = 0;
            }
        }
        return changedRow;
    }

    /**
     * Returns the number of faces of the graph.
     * 
     * @return The number of faces.
     */
    public int getNumberOfFaces() {
        return this.numberOfFaces;
    }

    /**
     * Returns the index of the outerface.
     * 
     * @return The index of the outerface.
     */
    public int getOuterFaceIndex() {
        return this.outerFaceIndex;
    }

    /**
     * Returns the number of innerfaces of the graph.
     * 
     * @return The number of innerfaces.
     */
    public int getNumberOfInnerFaces() {
        return this.numberOfInnerFaces;
    }

    /**
     * Returns all <code>Face</code>'s of the graph.
     * 
     * @return Returns the faces.
     */
    public Face[] getFaces() {
        return faces;
    }

    /**
     * Returns the <code>org.graffiti.graph.Graph</code>.
     * 
     * @return The <code>org.graffiti.graph.Graph</code>.
     */
    public Graph getGraph() {
        return this.graph;
    }

    /**
     * Returns all angles.
     * 
     * @return All angles.
     */
    public Angle[] getAngles() {
        return this.anglesInMatrix;
    }

    /**
     * Sets the <cod>Face</code> with the index index as the outerface of the
     * graph.
     * 
     * @param index
     */
    public void setOuterFaceIndex(int index) {
        this.outerFaceIndex = index;
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
