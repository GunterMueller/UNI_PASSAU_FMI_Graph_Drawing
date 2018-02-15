package org.graffiti.plugins.algorithms.graviso;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

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
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.selection.Selection;

/**
 * This class implements the backtracking portion of the standard version of the
 * refinement algorithm.
 * 
 * @author lenhardt
 * 
 */
public abstract class AbstractRefinementAlgorithm extends AbstractAlgorithm
        implements EditorAlgorithm, CalculatingAlgorithm {

    // the two graphs we operate on
    protected Graph g1, g2;

    public final int GRAPHS_TO_BE_CHOSEN = 2;
    protected String result;

    // counters and logger for debugging and animation
    protected static final Logger logger = Logger
            .getLogger(AbstractRefinementAlgorithm.class.getName());
    protected static int btCounter, refCounter;
    protected static int btStop, refStop;

    // holds the array positions of the parameters
    protected static final int P_VISUALIZE = 0;
    protected static final int P_REGARD_DIRECTIONS = 1;
    protected static final int P_USE_BFS_INFO = 2;
    protected static int P_NUM_TOTAL = 3;

    // results of isomorphic tests;
    protected static final int R_ISO = 1;
    protected static final int R_MAYBE = 0;
    protected static final int R_NOT_ISO = -1;

    // here, we store the color numbers once the graphs prove to be isomorphic
    protected int[] finalG1Colors;
    protected int[] finalG2Colors;

    @Override
    public void check() throws PreconditionException {

        PreconditionException errors = new PreconditionException();

        if (g1.isEmpty() || g2.isEmpty() || g1.getNumberOfNodes() == 0
                || g2.getNumberOfNodes() == 0) {
            errors.add("Both Graphs have to contain at least one Node");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * Constructor initializes the parameters to the algorithm.
     */
    public AbstractRefinementAlgorithm() {
        refCounter = 0;
        btCounter = 0;
        btStop = 1;
        refStop = 1;

        BooleanParameter regardDirections = new BooleanParameter(true,
                "regard Direction",
                "check if you want to take edges' direction into account");

        BooleanParameter useBFSInfo = new BooleanParameter(true,
                "use BFS to speed up computation",
                "check if you want to use BFS to calculate result");

        String[] visOpts = { "no animation", "final animation", "step-by-step" };
        StringSelectionParameter visualize = new StringSelectionParameter(
                visOpts, "level of visual feedback",
                "select if you want to see no colors, colors at the end, or step-by-step");

        parameters = new Parameter[P_NUM_TOTAL];
        parameters[P_REGARD_DIRECTIONS] = regardDirections;
        parameters[P_USE_BFS_INFO] = useBFSInfo;
        parameters[P_VISUALIZE] = visualize;

        result = "not run yet";

    }

    /**
     * This method performs preliminary checks whether the two graphs might be
     * isomorphic by comparing their degree sequences.
     * 
     * @param g1
     * @param g2
     * @return true if the graphs might be isormorphic, false otherwise.
     */
    protected boolean canBeIsmorphic(Graph g1, Graph g2) {
        if (g1.getNumberOfEdges() != g2.getNumberOfEdges()) {
            result = "The Graphs are not Isomorphic: The number of Edges is different";
            return false;
        }
        if (g1.getNumberOfDirectedEdges() != g2.getNumberOfDirectedEdges()
                && ((BooleanParameter) parameters[P_REGARD_DIRECTIONS])
                        .getBoolean()) {
            result = "The Graphs are not Isomorphic: The number of directed Edges is different";
            return false;
        }
        if (g1.getNumberOfUndirectedEdges() != g2.getNumberOfUndirectedEdges()
                && ((BooleanParameter) parameters[P_REGARD_DIRECTIONS])
                        .getBoolean()) {
            result = "The Graphs are not Isomorphic: The number of undirected Edges is different";
            return false;
        }
        if (g1.getNumberOfNodes() != g2.getNumberOfNodes()) {
            result = "The Graphs are not Isomorphic: The number of Nodes is different";
            return false;
        }

        if (((BooleanParameter) parameters[P_REGARD_DIRECTIONS]).getBoolean()) {
            ArrayList<Integer> degrees1 = new ArrayList<Integer>();
            ArrayList<Integer> degrees2 = new ArrayList<Integer>();

            // collect the degree sequences
            for (Node node : g1.getNodes()) {
                degrees1.add(node.getInDegree());
            }
            for (Node node : g2.getNodes()) {
                degrees2.add(node.getInDegree());
            }
            Collections.sort(degrees1);
            Collections.sort(degrees2);

            if (!degrees1.equals(degrees2)) {
                result = "The Graphs are not Isomorphic: The number of distinct indegrees is different";
                return false;
            }

            degrees1 = new ArrayList<Integer>();
            degrees2 = new ArrayList<Integer>();

            for (Node node : g1.getNodes()) {
                degrees1.add(node.getOutDegree());
            }
            for (Node node : g2.getNodes()) {
                degrees2.add(node.getOutDegree());
            }
            Collections.sort(degrees1);
            Collections.sort(degrees2);

            if (!degrees1.equals(degrees2)) {
                result = "The Graphs are not Isomorphic: The number of distinct outdegrees is different";
                return false;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Isomorphism Test (Refinement)";
    }

    public AlgorithmResult getResult() {
        if (result != "") {
            // DEBUG
            logger.warning(result + " BTs: " + btCounter + ", Refinements: "
                    + refCounter);
            AlgorithmResult aresult = new GravISOAlgorithmResult();
            aresult.addToResult("Result", result);
            return aresult;
        } else
            return null;
    }

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

    /**
     * This method assigns colors to the vertices of the graph in Gravisto's
     * panel, according to their color numbers (0..g1Colors.length) given in the
     * parameters
     * 
     * @param g1Colors
     *            the color numbers of graph 1
     * @param g2Colors
     *            the color numbers of graph 2
     */
    protected void colorNodes(int[] g1Colors, int[] g2Colors) {

        if (((StringSelectionParameter) parameters[P_VISUALIZE])
                .getSelectedIndex() > 0) {
            g1.getListenerManager().transactionStarted(this);
            g2.getListenerManager().transactionStarted(this);
            int i = 0;
            int numColors = g1Colors.length;

            // import RGB values from JColorChooser
            int[] niceColors = getRawValues();
            float fac = (niceColors.length / 3) / numColors;
            for (Node n : g1.getNodes()) {
                ColorAttribute ca = (ColorAttribute) n
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.FILLCOLOR);
                int col = Math.round(g1Colors[i] * fac);
                Color c = new Color(niceColors[col * 3],
                        niceColors[(col * 3) + 1], niceColors[(col * 3) + 2]);
                ca.setColor(c);
                i++;

            }
            i = 0;
            for (Node n : g2.getNodes()) {
                ColorAttribute ca = (ColorAttribute) n
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.FILLCOLOR);
                // if ((ca.getColor().getRed() == 255) && (c == Color.RED))
                int col = Math.round(g2Colors[i] * fac);
                Color c = new Color(niceColors[col * 3],
                        niceColors[(col * 3) + 1], niceColors[(col * 3) + 2]);
                ca.setColor(c);
                i++;
            }
            g1.getListenerManager().transactionFinished(this);
            g2.getListenerManager().transactionFinished(this);
        }
    }

    @Override
    public void reset() {
        g1 = null;
        g2 = null;

        refCounter = 0;
        btCounter = 0;
        btStop = 1;
        refStop = 1;

        BooleanParameter regardDirections = new BooleanParameter(true,
                "regard Direction",
                "check if you want to take edges' direction into account");

        BooleanParameter useBFSInfo = new BooleanParameter(true,
                "use BFS to speed up computation",
                "check if you want to use BFS to calculate result");
        String[] visOpts = { "no animation", "final animation", "step-by-step" };

        StringSelectionParameter visualize = new StringSelectionParameter(
                visOpts, "show node colors after calculation",
                "check if you want the nodes to be colored after calculation");

        parameters = new Parameter[P_NUM_TOTAL];
        parameters[P_REGARD_DIRECTIONS] = regardDirections;
        parameters[P_USE_BFS_INFO] = useBFSInfo;
        parameters[P_VISUALIZE] = visualize;

        result = "not run yet";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.graffiti.plugin.algorithm.EditorAlgorithm#getParameterDialog(org.
     * graffiti.selection.Selection)
     */
    public ParameterDialog getParameterDialog(Selection sel) {
        MainFrame parent = GraffitiSingleton.getInstance().getMainFrame();

        GravISOParameterDialog dia = new GravISOParameterDialog(parent
                .getEditComponentManager(), parent, parameters, sel, getName(),
                this);
        return dia;
    }

    /**
     * This method is called after each refinement step. It checks, whether the
     * user requested to see the current colorings of the graphs. If this is the
     * case, the graphs are colored in Gravisto's panel, and a message asks to
     * which step the algorithm may advance.
     */
    protected void showMessage(int[] g1Colors, int[] g2Colors) {
        if (((StringSelectionParameter) parameters[P_VISUALIZE])
                .getSelectedIndex() > 1) {
            if (btCounter == btStop || refCounter == refStop) {

                String[] stepStrings = { "next refinement", "next backtrack",
                        "to the end" };
                String msg = "Refinement: " + refCounter + " Backtrack: "
                        + btCounter + "\nGoto ";
                String s = (String) JOptionPane.showInputDialog(
                        GraffitiSingleton.getInstance().getMainFrame(), msg,
                        "", JOptionPane.PLAIN_MESSAGE, null, stepStrings,
                        "refinements");
                colorNodes(g1Colors, g2Colors);
                if (s != null) {
                    if (s.equals("next refinement")) {
                        refStop = (refCounter + 1);
                        btStop = -1;
                    } else if (s.equals("next backtrack")) {
                        btStop = (btCounter + 1);
                        refStop = -1;
                    } else {
                        btStop = -1;
                        refStop = -1;
                    }
                } else {
                    showMessage(g1Colors, g2Colors);
                }
            }
        }
    }

    /**
     * Taken from JColorChooser, (c) Sun Microsystems
     * 
     * In order to color the graphs nicely, this premade list of good colors has
     * been taken from the Java API's JColorChooser.
     * 
     * @return an array of RGB values
     */
    private static int[] getRawValues() {
        final int[] rawValues = {
                255,
                255,
                255, // first row.
                204, 255, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255,
                204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255,
                204, 204, 255, 204, 204, 255, 255, 204, 255, 255, 204, 204,
                255, 204, 204, 255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                255,
                204,
                204,
                204,
                204, // second row.
                153, 255, 255, 153, 204, 255, 153, 153, 255, 153, 153, 255,
                153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255,
                153, 153, 255, 204, 153, 255, 255, 153, 255, 255, 153, 204,
                255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153,
                153,
                255,
                153,
                153,
                255,
                153,
                153,
                255,
                153,
                153,
                255,
                204,
                153,
                255,
                255,
                153,
                204,
                255,
                153,
                153,
                255,
                153,
                153,
                255,
                153,
                153,
                255,
                153,
                153,
                255,
                153,
                153,
                255,
                153,
                153,
                255,
                153,
                153,
                255,
                153,
                153,
                255,
                204,
                204,
                204,
                204, // third row
                102, 255, 255, 102, 204, 255, 102, 153, 255, 102, 102, 255,
                102, 102, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255,
                153, 102, 255, 204, 102, 255, 255, 102, 255, 255, 102, 204,
                255, 102, 153, 255, 102, 102, 255, 102, 102, 255, 102, 102,
                255, 102, 102, 255, 102, 102, 255,
                153,
                102,
                255,
                204,
                102,
                255,
                255,
                102,
                204,
                255,
                102,
                153,
                255,
                102,
                102,
                255,
                102,
                102,
                255,
                102,
                102,
                255,
                102,
                102,
                255,
                102,
                102,
                255,
                102,
                102,
                255,
                153,
                102,
                255,
                204,
                153,
                153,
                153, // fourth row
                51, 255, 255, 51, 204, 255, 51, 153, 255, 51, 102, 255, 51, 51,
                255, 51, 51, 255, 51, 51, 255, 102, 51, 255, 153, 51, 255, 204,
                51, 255, 255, 51, 255, 255, 51, 204, 255, 51, 153, 255, 51,
                102, 255, 51, 51, 255, 51, 51, 255, 51, 51, 255, 102, 51, 255,
                153, 51, 255, 204, 51, 255, 255,
                51,
                204,
                255,
                51,
                153,
                244,
                51,
                102,
                255,
                51,
                51,
                255,
                51,
                51,
                255,
                51,
                51,
                255,
                51,
                51,
                255,
                102,
                51,
                255,
                153,
                51,
                255,
                204,
                153,
                153,
                153, // Fifth row
                0, 255, 255, 0, 204, 255, 0, 153, 255, 0, 102, 255, 0, 51, 255,
                0, 0, 255, 51, 0, 255, 102, 0, 255, 153, 0, 255, 204, 0, 255,
                255, 0, 255, 255, 0, 204, 255, 0, 153, 255, 0, 102, 255, 0, 51,
                255, 0, 0, 255, 51, 0, 255, 102, 0, 255, 153, 0, 255, 204, 0,
                255, 255, 0, 204, 255, 0, 153, 255,
                0,
                102,
                255,
                0,
                51,
                255,
                0,
                0,
                255,
                0,
                0,
                255,
                51,
                0,
                255,
                102,
                0,
                255,
                153,
                0,
                255,
                204,
                102,
                102,
                102, // sixth row
                0, 204, 204, 0, 204, 204, 0, 153, 204, 0, 102, 204, 0, 51, 204,
                0, 0, 204, 51, 0, 204, 102, 0, 204, 153, 0, 204, 204, 0, 204,
                204, 0, 204, 204, 0, 204, 204, 0, 153, 204, 0, 102, 204, 0, 51,
                204, 0, 0, 204, 51, 0, 204, 102, 0, 204, 153, 0, 204, 204, 0,
                204, 204, 0, 204, 204, 0, 153, 204, 0, 102, 204, 0, 51, 204,
                0,
                0,
                204,
                0,
                0,
                204,
                51,
                0,
                204,
                102,
                0,
                204,
                153,
                0,
                204,
                204,
                102,
                102,
                102, // seventh row
                0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 102, 153, 0, 51, 153,
                0, 0, 153, 51, 0, 153, 102, 0, 153, 153, 0, 153, 153, 0, 153,
                153, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 102, 153, 0, 51,
                153, 0, 0, 153, 51, 0, 153, 102, 0, 153, 153, 0, 153, 153, 0,
                153, 153, 0, 153, 153, 0, 153, 153, 0, 102, 153, 0, 51, 153, 0,
                0, 153, 0, 0, 153, 51,
                0,
                153,
                102,
                0,
                153,
                153,
                0,
                153,
                153,
                51,
                51,
                51, // eigth row
                0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 51, 102,
                0, 0, 102, 51, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102,
                102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 51,
                102, 0, 0, 102, 51, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0,
                102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 51, 102, 0,
                0, 102, 0, 0, 102, 51, 0, 102, 102, 0, 102, 102,
                0,
                102,
                102,
                0,
                0,
                0, // ninth row
                0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 0,
                51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51,
                0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 0, 51, 51, 0,
                51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51,
                0, 51, 51, 0, 0, 51, 0, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51,
                51, 51, 51, 51 };
        return rawValues;
    }
}
