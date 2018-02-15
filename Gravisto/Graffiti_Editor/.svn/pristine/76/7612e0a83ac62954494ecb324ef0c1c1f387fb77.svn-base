// =============================================================================
//
//   AbstractIsomorphism.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.isomorphism;

import java.awt.Color;

import org.graffiti.attributes.Attribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.editor.dialog.ParameterDialog;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.EditorAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.selection.Selection;

/**
 * Superclass for all classes that implement an algorithm to test isomorphism of
 * two graphs.
 * 
 * @author mary-k
 * @version $Revision$ $Date$
 */
public abstract class AbstractIsomorphism extends AbstractAlgorithm implements
        EditorAlgorithm, CalculatingAlgorithm {
    /**
     * The two graphs that we want to check for isomorphism
     */
    protected Graph g1, g2;

    /**
     * The two graphs represented as matrices.
     */
    protected int[][] m1, m2;

    /**
     * Indicates, after the execution of the algorithm, if the two given graphs
     * are isomorphic.
     */
    protected String result;

    /**
     * This parameter is needed by <code>GraphChoosingPanel</code> (a panel
     * where the user can choose a certain number of graphs) to determine how
     * many graphs shall be passed to the algorithm.
     */
    public final int GRAPHS_TO_BE_CHOSEN = 2;

    /**
     * Constructs an instance of this algorithm.
     * <p>
     * Determines parameters that the user will be asked for before starting the
     * execution of the algorithm (managed by <code>ParameterEditPanel</code>).
     */
    // (z.B. ob Kantengewichte beachtet werden sollen, ob die Richtung beachtet
    // werden soll, ...)
    // Achtung: in reset() nicht l�schen, da Instanz nur einmal erzeugt wird!!
    public AbstractIsomorphism() {
        // BooleanParameter edgeLabels = new BooleanParameter(false,"edge
        // labels", "Take edge labels into account for isomorphism.");
        // BooleanParameter nodeLabels = new BooleanParameter(false,"node
        // labels", "Take node labels into account for isomorphism.");
        BooleanParameter direction = new BooleanParameter(false, "direction",
                "Ignore direction of edges.");
        parameters = new Parameter[1];
        // parameters[1] = edgeLabels;
        // parameters[2] = nodeLabels;
        parameters[0] = direction;
    }

    /**
     * Checks trivial conditions; returns true, if graphs can't possibly be
     * isomorphic
     */
    public boolean quickCheck() {
        if (g1.getNumberOfNodes() != g2.getNumberOfNodes()) {
            result = "The graphs are not isomorphic!\nThey have a different number of nodes.";
            return true;
        }
        if (g1.getNumberOfEdges() != g2.getNumberOfEdges()) {
            result = "The graphs are not isomorphic!\nThey have a different number of edges.";
            return true;
        }
        if (g1.getNumberOfDirectedEdges() != g2.getNumberOfDirectedEdges()) {
            result = "The graphs are not isomorphic!\nThey have a different number of directed edges.";
            return true;
        }
        if (g1.getNumberOfUndirectedEdges() != g2.getNumberOfUndirectedEdges()) {
            result = "The graphs are not isomorphic!\nThey have a different number of undirected edges.";
            return true;
        }
        return false;
    }

    /**
     * Checks on some preconditions that have to be met in order for this
     * algorithm to be executed.<br>
     * Preconditions:
     * <ul>
     * <li>Exactly two graphs have to be chosen.
     * <li>The graph instances may not be null.
     * <li>Each graph has to have at least one node.
     * </ul>
     * 
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#check()
     */
    @Override
    public void check() throws PreconditionException {

        PreconditionException errors = new PreconditionException();
        if (g1 == null || g2 == null) { // should not happen
            errors.add("Please choose exactly two graphs!");
        }
        if (g1.getNumberOfNodes() <= 0 || g2.getNumberOfNodes() <= 0) {
            errors
                    .add("The number of nodes of a graph may not be smaller than zero.");
        }
        if (!errors.isEmpty())
            throw errors;
    }

    // Wird von GraffitiSingleton aufgerufen wenn Algo im Plugin-Men� ausgew�hlt
    // wird.
    // sel wird nicht gebraucht (ausgew�hlte Knoten/Kanten interessieren hier
    // nicht)
    /**
     * Returns an IsomorphismParameterDialog. It lets the user edit the
     * parameters of the algorithm as usual. In addition, the two graphs that
     * shall be tested for isomorphism can be selected here.
     * 
     * @see org.graffiti.plugin.algorithm.EditorAlgorithm#getParameterDialog(org.graffiti.selection.Selection)
     * @return an IsomorphismParameterDialog where graphs and parameters can be
     *         selected.
     */
    public ParameterDialog getParameterDialog(Selection sel) {
        MainFrame parent = GraffitiSingleton.getInstance().getMainFrame();
        IsomorphismParameterDialog dia = new IsomorphismParameterDialog(parent
                .getEditComponentManager(), parent, parameters, sel, getName(),
                this);
        return dia;
    }

    /**
     * Sets the parameters that this algorithm needs (these parameters are
     * provided by a user interface, namely a ParameterDialog).
     * 
     * They have the same types and order as the array returned by
     * <code>getParameter</code>.
     * 
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(org.graffiti.plugin.parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        // get g1 and g2
        if (params[0] != null) {
            g1 = (Graph) params[0].getValue();
            g2 = (Graph) params[1].getValue();
        }
        // should not happen
        else {
            g1 = null;
            g2 = null;
        }
        // take g1 and g2 out of array so the arrangement corresponds to the
        // arrangement defined in the constructor
        parameters = new Parameter[params.length - GRAPHS_TO_BE_CHOSEN];
        for (int i = 0; i < params.length - GRAPHS_TO_BE_CHOSEN; i++) {
            parameters[i] = params[i + GRAPHS_TO_BE_CHOSEN];
        }
    }

    // eigene Datenstruktur aufbauen
    // undirected edges are represented by an in edge and an out edge
    public void matrixRepresentation() {
        m1 = new int[g1.getNumberOfNodes()][g1.getNumberOfNodes()];
        // Knoten Nummern zuordnen
        int i = 0;
        for (Node n : g1.getNodes()) {
            n.setInteger("node number", i);
            i++;
        }
        for (Node n : g1.getNodes()) {
            for (Node nb : n.getAllInNeighbors()) {
                m1[n.getInteger("node number")][nb.getInteger("node number")] = -1;
            }
            for (Node nb : n.getAllOutNeighbors()) {
                m1[n.getInteger("node number")][nb.getInteger("node number")] = 1;
            }
        }
        m2 = new int[g2.getNumberOfNodes()][g2.getNumberOfNodes()];
        // Knoten Nummern zuordnen
        i = 0;
        for (Node n : g2.getNodes()) {
            n.setInteger("node number", i);
            i++;
        }
        for (Node n : g2.getNodes()) {
            for (Node nb : n.getAllInNeighbors()) {
                m2[n.getInteger("node number")][nb.getInteger("node number")] = -1;
            }
            for (Node nb : n.getAllOutNeighbors()) {
                m2[n.getInteger("node number")][nb.getInteger("node number")] = 1;
            }
        }
    }

    /*
     * @see
     * org.graffiti.plugin.algorithm.AbstractAlgorithm#attach(org.graffiti.graph
     * .Graph)
     */
    @Override
    public void attach(Graph g) {
        super.attach(g);
        System.out
                .println("Must attach two graphs!! Please use attach(Graph g1, Graph g2)");
    }

    /**
     * Attaches two given graphs to this algorithm. Is not being used so far.
     * 
     * @param g1
     *            the graph that will be compared to graph g2
     * @param g2
     *            the graph that will be compared to graph g1
     */
    public void attach(Graph g1, Graph g2) {
        this.g1 = g1;
        this.g2 = g2;
    }

    /*
     * @see org.graffiti.plugin.algorithm.CalculatingAlgorithm#getResult()
     */
    public AlgorithmResult getResult() {
        if (result != null) {
            AlgorithmResult aresult = new IsomorphismAlgorithmResult();
            aresult.addToResult("Result", result);
            return aresult;
        } else
            return null;
    }

    public static void setNodeColor(Node node, Color c) {
        ColorAttribute ca = (ColorAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.FILLCOLOR);
        // if ((ca.getColor().getRed() == 255) && (c == Color.RED))

        ca.setColor(c);
    }

    /**
     * Resets the internal state of the algorithm.
     * 
     * (Da jeder Algo nur einmal instanziiert wird (in IsomorphismPlugin) und
     * deshalb Klassenvariablen aus einer fr�heren Exekution erhalten bleiben.)
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        // super.reset(); setzt parameters auf null -> Problem, weil dann
        // Texte im Dialogfenster fehlen.
        g1 = null;
        g2 = null;
        result = null;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
