package org.graffiti.plugins.algorithms.planarity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.planarity.faces.Faces;

/**
 * Stores the embedding of a connected component
 * 
 * @author Wolfgang Brunner
 */
public class TestedComponent extends TestedObject {

    /**
     * The number of biconnected components
     */
    private int numberOfBicomps;

    /**
     * The number of planar biconnected components
     */
    private int numberOfPlanarBicomps;

    /**
     * The list of <code>TestedBicomp</code> objects. These store the embeddings
     * of the biconnected components
     */
    private List<TestedBicomp> testedBicomps;

    /**
     * The number of loops in the connected component
     */
    private int loops;

    /**
     * Faces of this component.
     */
    private Faces faces;

    /**
     * Constructs a new <code>TestedComponent</code>
     * 
     * @param comp
     *            The <code>ConnectedComponent</code> object
     * @param map
     *            The mapping between <code>org.graffiti.graph.Node</code> and
     *            <code>RealNode</code> objects
     */
    public TestedComponent(ConnectedComponent comp, HashMap<Node, RealNode> map) {
        super(map);
        testedBicomps = new LinkedList<TestedBicomp>();
        nodes = new LinkedList<Node>();
        numberOfBicomps = comp.bicomps.size();
        numberOfPlanarBicomps = 0;
        loops = comp.loops;
        doubleEdges = comp.doubleEdges;
        numberOfNodes = comp.numberOfNodes;
        for (Iterator<RealNode> i = comp.realNodes.iterator(); i.hasNext();) {
            RealNode rNode = i.next();
            nodes.add(rNode.originalNode);
        }

        for (Iterator<Bicomp> i = comp.bicomps.iterator(); i.hasNext();) {
            Bicomp bicomp = i.next();
            if (bicomp.isPlanar()) {
                numberOfPlanarBicomps++;
            }
            TestedBicomp testedBicomp = new TestedBicomp(bicomp, map);
            testedBicomps.add(testedBicomp);
        }

        planar = (numberOfPlanarBicomps == numberOfBicomps);

    }

    /**
     * Get all faces for this component.
     * 
     * @return Faces for this component.
     */
    public synchronized Faces getFaces() {
        if (faces != null)
            return faces;
        faces = new Faces(this);
        return faces;
    }

    /**
     * Gives the number of biconnected components
     * 
     * @return The number of biconnected components
     */
    public int getNumberOfBicomps() {
        return numberOfBicomps;
    }

    /**
     * Gives the number of planar biconnected components
     * 
     * @return The number of planar biconnected components
     */
    public int getNumberOfPlanarBicomps() {
        return numberOfPlanarBicomps;
    }

    /**
     * Gives the list of <code>TestedBicomp</code> objects
     * 
     * @return The list of biconnected components
     */
    public List<TestedBicomp> getTestedBicomps() {
        return testedBicomps;
    }

    /**
     * Gives the number of loops in the connected component
     * 
     * @return The number of loops
     */
    public int getNumberOfLoops() {
        return loops;
    }

    /**
     * Gives a textual representation of the embedding
     * 
     * @return The <code>String</code> representing the connected component
     */
    @Override
    public String toString() {

        String result = "";

        int DFSStartNumber = map.get(getNodes().get(0)).DFSStartNumber;

        if (numberOfNodes == 1) {
            result += "Consists of Node " + DFSStartNumber + ".\n";
        } else {
            result += "Consists of Nodes " + DFSStartNumber + " to "
                    + (DFSStartNumber + getNumberOfNodes() - 1) + ".\n";
        }
        if (getNumberOfLoops() != 0) {
            result += "\nThe following nodes have loops:\n\n";
            for (Iterator<Node> i = getNodes().iterator(); i.hasNext();) {
                RealNode current = map.get(i.next());
                if (current.loops == 1) {
                    result += "    " + current + ": 1 loop\n";
                } else if (current.loops > 1) {
                    result += "    " + current + ": " + current.loops
                            + " loops\n";

                }

            }
        }
        if (getNumberOfDoubleEdges() > 0) {
            if (getNumberOfDoubleEdges() == 1) {
                result += "\n1 double edge was removed.\n";
            } else {
                result += "\n" + getNumberOfDoubleEdges()
                        + " double edges were removed.\n";
            }
            result += "\nRemoved Double Edges:\n";
            for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
                Node node = i.next();
                if (getDoubleEdgeTargets(node).size() != 0) {
                    result += indent(map.get(node) + ": "
                            + toStringNodeList(getDoubleEdgeTargets(node))
                            + "\n", 4);
                }
            }
        }

        if (isPlanar()) {
            result += "\nComponent is planar.\n";
            result += "\n" + getFaces().toString();
        } else {
            result += "\nComponent is not planar.\n";
        }

        if (getNumberOfBicomps() == 1) {
            result += "\nConsists of 1 biconnected component";
            if (getNumberOfPlanarBicomps() == 1) {
                result += " which is planar.\n\n";
            } else {
                result += " which is not planar.\n\n";
            }
        } else {
            result += "\nConsists of " + getNumberOfBicomps()
                    + " biconnected components";
            if (getNumberOfBicomps() == getNumberOfPlanarBicomps()) {
                result += " and all are planar.\n\n";
            } else if (getNumberOfPlanarBicomps() == 0) {
                result += " but none is planar.\n\n";
            } else if (getNumberOfPlanarBicomps() == 1) {
                result += " but only 1 is planar.\n\n";
            } else {
                result += " of which " + getNumberOfPlanarBicomps()
                        + " are planar.\n\n";
            }

        }

        int counter = 1;
        for (Iterator<TestedBicomp> i = testedBicomps.iterator(); i.hasNext();) {
            TestedBicomp bicomp = i.next();
            result += "Biconnected component " + counter + ":\n\n";
            result += indent(bicomp.toString(), 4) + "\n";
            counter++;
        }
        return result;
    }
}
