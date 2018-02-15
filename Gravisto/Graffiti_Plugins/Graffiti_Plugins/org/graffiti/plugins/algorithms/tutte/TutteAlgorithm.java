// =============================================================================
//
//   TutteAlgorithm.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.tutte;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedComponent;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;
import org.graffiti.plugins.algorithms.planarity.faces.Face;
import org.graffiti.plugins.algorithms.planarity.faces.Faces;

/**
 * @author hanauer
 * @version $Revision$ $Date$
 */
public class TutteAlgorithm extends AbstractAlgorithm {
    
    private Faces faces = null;

    @Override
    public void check() throws PreconditionException {
        PlanarityAlgorithm p = new PlanarityAlgorithm();
        p.attach(graph);
        try {
            p.check();
        } catch (PreconditionException pe) {
            throw pe;
        }
        p.execute();
        TestedGraph tg = p.getTestedGraph();
        if (tg.getNumberOfComponents() > 1) {
            throw new PreconditionException("Graph is not connected.");
        }
        TestedComponent tc = tg.getTestedComponents().get(0);
        faces = tc.getFaces();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        
        assert faces != null;
        
        graph.getListenerManager().transactionStarted(this);
        
        Face outerFace = selectOuterFace();
        
        Map<Node,Double> xCoords = new HashMap<Node,Double>();
        Map<Node,Double> yCoords = new HashMap<Node,Double>();
        
        placeOuterFaceNodes(outerFace, xCoords, yCoords);
        try {
            computeCoordinates(xCoords, yCoords);
            setNodesToCoordinates(xCoords, yCoords);
        } catch (LpSolveException e) {
            // should not happen..?
            e.printStackTrace();
        }
        
        graph.getListenerManager().transactionFinished(this);
    }
    
    /**
     * Selects the outer face... somehow
     * 
     * @return the selected outer face
     */
    private Face selectOuterFace() {
        // TODO implement different selection methods
        return getMaxFace();
    }
    
    /**
     * Finds the face with a maximum number of nodes.
     * 
     * @return a face with a maximum number of nodes.
     */
    private Face getMaxFace() {
        Face maxFace = null;
        int numNodes = 0;
        for (Face f : faces.getFaces()) {
            int n = f.getNodes().size();
            if (n > numNodes) {
                maxFace = f;
            }
        }
        return maxFace;
    }
    
    private void placeOuterFaceNodes(Face face, Map<Node,Double> xCoords, Map<Node,Double> yCoords) {
        double radius = 400.0; // should be a parameter or something
        List<Node> nodes = new LinkedList<Node>(face.getNodes());
        Collections.reverse(nodes);
        double deltaDeg = 2*Math.PI / nodes.size();
        
        double angle = 0.0;
        for (Node node : nodes) {
            double x = Math.cos(angle) * radius + 500.0;
            double y = -Math.sin(angle) * radius + 500.0;
            xCoords.put(node, x);
            yCoords.put(node, y);
            angle += deltaDeg;
            System.out.println("Setting outer face node " + getNodeLabel(node) + " to (" + x + "," + y + ")");
        }
    }
    
    /**
     * @param xCoords
     * @param yCoords
     * @throws LpSolveException
     */
    private void computeCoordinates(Map<Node,Double> xCoords, Map<Node,Double> yCoords) throws LpSolveException {
        
        int n = graph.getNumberOfNodes();
        ArrayList<Node> orderedNodes = new ArrayList<Node>();
        Map<Node,Integer> nodeIndex = new HashMap<Node,Integer>();
        Iterator<Node> it = graph.getNodesIterator();
        int i = 1;
        while (it.hasNext()) {
            Node node = it.next();
            nodeIndex.put(node, i);
            orderedNodes.add(node);
            i++;
        }
        
        LpSolve solver = LpSolve.makeLp(0, 2*n);
        
        it = graph.getNodesIterator();
        while (it.hasNext()) {
            Node node = it.next();
            System.out.println("node " + getNodeLabel(node) + ":");
            if (xCoords.containsKey(node)) {
                double x = xCoords.get(node);
                solver.addConstraint(setCoordinate(nodeIndex.get(node), n, true), 
                        LpSolve.EQ, x);
                System.out.println(" = " + x);
            } else {
                solver.addConstraint(makeConstraint(node, nodeIndex, n, true), 
                        LpSolve.EQ, 0.0);
                System.out.println(" = 0.0");
            }
            
            if (yCoords.containsKey(node)) {
                double y = yCoords.get(node);
                solver.addConstraint(setCoordinate(nodeIndex.get(node), n, false), 
                        LpSolve.EQ, y);
                System.out.println(" = " + y);
            } else {
                solver.addConstraint(makeConstraint(node, nodeIndex, n, false), 
                        LpSolve.EQ, 0.0);
                System.out.println(" = 0.0");
            }
        }
        // objective function... shouldn't really matter
        solver.setObj(1, 1.0);
        solver.solve();
        
        // in contrast to constraints, this array is indexed 0-based...
        double[] var = solver.getPtrVariables();
        for (int j = 0; j < n; j++) {
            Node node = orderedNodes.get(j);
            double xCoord = var[j];
            double yCoord = var[n + j];
            xCoords.put(node, xCoord);
            yCoords.put(node, yCoord);
            System.out.println("node " + getNodeLabel(node) + " -> (" + xCoord + "," + yCoord + ")");
        }
        solver.deleteLp();
    }
    
    /**
     * @param node
     * @param nodeIndex
     * @param n
     * @param forX
     * @return
     */
    private double[] makeConstraint(Node node, Map<Node,Integer> nodeIndex, int n, boolean forX) {
        double[] d = new double[2*n + 1];
        
        Collection<Node> neighbors = node.getNeighbors();
        double degreeInv = 1.0 / neighbors.size();
        
        for (Node neighbor : neighbors) {
            int index = nodeIndex.get(neighbor);
            if (!forX) {
                index += n;
            }
            d[index] = degreeInv;
        }
        int nIndex = nodeIndex.get(node);
        if (!forX) {
            nIndex += n;
        }
        d[nIndex] = -1.0;
        
        System.out.print("row: " + Arrays.toString(d));
        
        return d;
    }
    
    /**
     * 
     * 
     * @param nodeIndex
     * @param n
     * @param forX
     * @return
     */
    private double[] setCoordinate(int nodeIndex, int n, boolean forX) {
        double[] d = new double[2*n + 1];
        int index = nodeIndex;
        if (!forX) {
            index += n;
        }
        d[index] = 1.0;
        
        System.out.print("row: " + Arrays.toString(d));
        
        return d;
    }
    
    /**
     * @param xCoords
     * @param yCoords
     */
    private void setNodesToCoordinates(Map<Node,Double> xCoords, Map<Node,Double> yCoords) {
        for (Iterator<Node> it = graph.getNodesIterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setDouble(GraphicAttributeConstants.COORDX_PATH, xCoords.get(node));
            node.setDouble(GraphicAttributeConstants.COORDY_PATH, yCoords.get(node));
        }
    }
    
    /**
     * Helper method for debug purposes only.
     * 
     * @param node
     * @return
     */
    private String getNodeLabel(Node node) {
        String label;
        try {
            label = node.getString("label.label");
        } catch (AttributeNotFoundException e) {
            label = "";
        }
        
        return label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Tutte's Drawing Algorithm";
    }

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
