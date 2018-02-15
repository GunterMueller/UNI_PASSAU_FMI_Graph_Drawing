package org.graffiti.plugins.algorithms.planarity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Node;

/**
 * Stores the embedding of the complete graph
 * 
 * @author Wolfgang Brunner
 */
public class TestedGraph extends TestedObject {

    /**
     * The number of connected components in the graph
     */
    private int numberOfComponents;

    /**
     * The number of planar connected components in the graph
     */
    private int numberOfPlanarComponents;

    /**
     * The list of <code>TestedComponent</code> objects. These store the
     * embeddings of the connected components
     */
    private List<TestedComponent> testedComponents;

    /**
     * The number of loops in the complete graph
     */
    private int loops;

    /**
     * Constructs a new <code>TestedGraph</code>
     * 
     * @param components
     *            The list of <code>ConnectedComponent</code> objects
     * @param map
     *            The mapping between <code>org.graffiti.graph.Node</code> and
     *            <code>RealNode</code> objects
     */
    public TestedGraph(List<ConnectedComponent> components,
            HashMap<Node, RealNode> map) {
        super(map);
        testedComponents = new LinkedList<TestedComponent>();
        nodes = new LinkedList<Node>();
        numberOfComponents = components.size();
        numberOfPlanarComponents = 0;
        loops = 0;

        for (Iterator<ConnectedComponent> i = components.iterator(); i
                .hasNext();) {
            ConnectedComponent comp = i.next();
            if (comp.isPlanar()) {
                numberOfPlanarComponents++;
            }
            loops += comp.loops;
            doubleEdges += comp.doubleEdges;
            TestedComponent testedComponent = new TestedComponent(comp, map);
            testedComponents.add(testedComponent);
            nodes.addAll(testedComponent.getNodes());
        }
        numberOfNodes = nodes.size();

        planar = (numberOfPlanarComponents == numberOfComponents);
    }

    /**
     * Gives the number of connected components
     * 
     * @return The number of connected components
     */
    public int getNumberOfComponents() {
        return numberOfComponents;
    }

    /**
     * Gives the number of planar connected components
     * 
     * @return The number of planar connected components
     */
    public int getNumberOfPlanarComponents() {
        return numberOfPlanarComponents;
    }

    /**
     * Gives the list of <code>TestedComponent</code> objects
     * 
     * @return The list of connected components
     */
    public List<TestedComponent> getTestedComponents() {
        return testedComponents;
    }

    /**
     * Gives the number of loops in the graph
     * 
     * @return The number of loops
     */
    public int getNumberOfLoops() {
        return loops;
    }

    /**
     * Gives a textual representation of the embedding
     * 
     * @return The <code>String</code> representing the graph
     */
    @Override
    public String toString() {
        String result = "";

        if (getNumberOfComponents() == 1) {
            result += "Graph consists of 1 connected component";
            if (getNumberOfPlanarComponents() == 1) {
                result += " which is planar.\n\n";
            } else {
                result += " which is not planar.\n\n";
            }
        } else {
            result += "Graph consists of " + getNumberOfComponents()
                    + " connected components";
            if (getNumberOfComponents() == getNumberOfPlanarComponents()) {
                result += " and all are planar.\n\n";
            } else if (getNumberOfPlanarComponents() == 0) {
                result += " but none is planar.\n\n";
            } else if (getNumberOfPlanarComponents() == 1) {
                result += " but only 1 is planar.\n\n";
            } else {
                result += " of which " + getNumberOfPlanarComponents()
                        + " are planar.\n\n";
            }

        }

        int current = 1;
        for (Iterator<TestedComponent> i = getTestedComponents().iterator(); i
                .hasNext();) {
            TestedComponent comp = i.next();
            result += "Connected component " + current + ":\n\n";
            current++;
            result += indent(comp.toString(), 4);
        }
        return result;
    }

}
