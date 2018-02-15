package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedComponent;

/**
 * This class does some administration work for the execution of the graph
 * drawing algorithms of Schnyder (and Brehm, which are adaptions from
 * Schnyder`s). The administration work contains the following: - Check the
 * preconditions of the algorithms: - Gain and keep the paramaters the algorithm
 * should run with - Decide (from the parameters) which algorithm to perform and
 * execute it - Remove bends, multiedges and loops and reinsert them afterwards.
 * - Draw the result of the algorithm in GRAVISTO. - If there is more than one
 * drawing, switch between them. (<code>SchnyderAllCanonicalOrders</code> and
 * <code>SchnyderAllRealizer</code> are just primitive algorithms and not really
 * developed by Schnyder but named "Schnyder" to keep a better overview over the
 * inheritance hierarchy.)
 * 
 * @author hofmeier
 */
public class SchnyderRealizerAdministration extends AbstractAlgorithm {

    /**
     * The String shown in the parameter window for
     * <code>SchnyderOneRealizer</code>
     */
    private static final String ONE_REALIZER = "(1) Get one Schnyder Realizer and draw the graph";

    /**
     * The String shown in the parameter window for
     * <code>SchnyderAllCanonicalOrders</code>
     */
    private static final String ALL_CANONICAL_ORDERS = "(2) Calculate all canonical orders (FOR SMALL GRAPHS)";

    /**
     * The String shown in the parameter window for
     * <code>SchnyderAllRealizer</code>
     */
    private static final String ALL_REALIZERS = "(3) Calculate all Schnyder Realizers (FOR SMALL GRAPHS)";

    /**
     * The String shown in the parameter window for
     * <code>BrehmOneRealizer</code>
     */
    private static final String ONE_REALIZER_BREHM = "(4) Calculate one Schnyder Realizer (by Brehm)";

    /**
     * The String shown in the parameter window for
     * <code>BrehmAllRealizer</code>
     */
    private static final String ALL_REALIZER_BREHM = "(5) Calculate all Schnyder Realizers (by Brehm)";

    /**
     * The String shown in the parameter window for <code>HeImprovement</code>
     */
    private static final String HE_IMPROVEMENT = "(6) Improve algorithm (4) by He`s method";

    /**
     * The String shown in the parameter window for <code>ImprovedFPP</code>
     */
    private static final String IMPROVED_FPP = "(7) Improved FPP-algorithm (using some properties of realizers)";

    /**
     * Parameter, indicating which algorithm was chosen: set true for
     * <code>SchnyderOneRealizer</code>
     */
    protected static boolean oneRealizer;

    /**
     * Parameter, indicating which algorithm was chosen: set true for
     * <code>SchnyderAllCanonicalOrders</code>
     */
    protected static boolean allCanonicalOrders;

    /**
     * Parameter, indicating which algorithm was chosen: set true for
     * <code>SchnyderAllRealizer</code>
     */
    protected static boolean allRealizers;

    /**
     * Parameter, indicating which algorithm was chosen: set true for
     * <code>BrehmOneRealizer</code>
     */
    protected static boolean oneBrehm;

    /**
     * Parameter, indicating which algorithm was chosen: set true for
     * <code>BrehmAllRealizer</code>
     */
    protected static boolean allBrehm;

    /**
     * Parameter, indicating which algorithm was chosen: set true for
     * <code>HeImprovement</code>
     */
    protected static boolean improvementHe;

    /**
     * Parameter, indicating which algorithm was chosen: set true for
     * <code>ImprovedFPP</code>
     */
    protected static boolean improvedFPP;

    /**
     * Parameter indicating, if the edges should be colored according to the
     * realizers
     */
    protected static boolean color;

    /**
     * Parameter indicating, if the edges should be directed towards the roots
     * of the realizers
     */
    protected static boolean directEdges;

    /**
     * Parameter indicating, if removed loops and multiedges should be readded
     * at the end of the algorithm
     */
    protected static boolean readd;

    /**
     * Parameter indicating, if edges that were added during the triangulation
     * of the graph should be removed at the end of the algorithm
     */
    protected static boolean removeAdded;

    /** Parameter indicating, if the nodes should be labelled */
    protected static boolean label;

    /**
     * Parameter indicating, if a detailled output on every realizer should be
     * written to a file (only for <code>BrehmAllRealizers</code>
     */
    protected static boolean writeToFile;

    /**
     * Determines the size of a node (to gain better drawing results) -> do not
     * set larger than <code>GRID_SIZE</code>
     */
    protected static double nodeSize;

    /**
     * Determines the grid size of the drawing (to gain better drawing results)
     */
    protected static double gridSize;

    /**
     * To prevent OutOfMemoryErrors, there is a maximum number of realizers. If
     * it is reached, the algorithm proceeds as normal but does not calculate
     * all realizers.
     */
    protected static int maxNumberOfRealizers;

    /** The integer key for green color */
    public static final int GREEN = 1;

    /** The integer key for blue color */
    public static final int BLUE = 2;

    /** The integer key for red color */
    public static final int RED = 3;

    /**
     * The integer key for black color (only used to overwrite existing node
     * colors, but not relevant for the algorithm itself)
     */
    public static final int BLACK = 0;

    /** The graph to be drawn */
    private Graph graph;

    /** Removed loops and multiedges */
    private LinkedList<Edge> removedEdges = new LinkedList<Edge>();

    /** The logger to inform or warn the user */
    private static final Logger logger = Logger
            .getLogger(SchnyderRealizerAdministration.class.getName());

    /** The chosen drawing algorithm */
    private AbstractDrawingAlgorithm ada;

    /** The result of the algorithm: The realizer(s) of the graph */
    private LinkedList<Realizer> realizers = new LinkedList<Realizer>();

    /**
     * The result of the algorithm: The coordinates of the nodes for each
     * realizer.
     */
    private LinkedList<BarycentricRepresentation> barycentricReps = new LinkedList<BarycentricRepresentation>();

    /**
     * Returns the name of the algorithm
     * 
     * @return name of the algorithm
     */
    public String getName() {
        return "Schnyder Realizer";
    }

    /**
     * Sets the parameters of the algorithm
     * 
     * @param params
     *            the parameters of the algorithm
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        SchnyderRealizerAdministration.label = ((BooleanParameter) params[0])
                .getBoolean().booleanValue();
        SchnyderRealizerAdministration.removeAdded = ((BooleanParameter) params[1])
                .getBoolean().booleanValue();
        SchnyderRealizerAdministration.readd = ((BooleanParameter) params[2])
                .getBoolean().booleanValue();
        SchnyderRealizerAdministration.color = ((BooleanParameter) params[3])
                .getBoolean().booleanValue();
        SchnyderRealizerAdministration.directEdges = ((BooleanParameter) params[4])
                .getBoolean().booleanValue();
        SchnyderRealizerAdministration.writeToFile = ((BooleanParameter) params[5])
                .getBoolean().booleanValue();
        String algSelection = ((StringSelectionParameter) params[6])
                .getSelectedValue();
        SchnyderRealizerAdministration.nodeSize = ((IntegerParameter) params[7])
                .getInteger().intValue();
        SchnyderRealizerAdministration.gridSize = ((IntegerParameter) params[8])
                .getInteger().intValue();
        SchnyderRealizerAdministration.maxNumberOfRealizers = ((IntegerParameter) params[9])
                .getInteger().intValue();
        SchnyderRealizerAdministration.oneRealizer = false;
        SchnyderRealizerAdministration.allCanonicalOrders = false;
        SchnyderRealizerAdministration.allRealizers = false;
        SchnyderRealizerAdministration.oneBrehm = false;
        SchnyderRealizerAdministration.allBrehm = false;
        SchnyderRealizerAdministration.improvementHe = false;
        SchnyderRealizerAdministration.improvedFPP = false;
        if (algSelection.equals(ONE_REALIZER)) {
            SchnyderRealizerAdministration.oneRealizer = true;
        } else if (algSelection.equals(ALL_CANONICAL_ORDERS)) {
            SchnyderRealizerAdministration.allCanonicalOrders = true;
        } else if (algSelection.equals(ALL_REALIZERS)) {
            SchnyderRealizerAdministration.allRealizers = true;
        } else if (algSelection.equals(ONE_REALIZER_BREHM)) {
            SchnyderRealizerAdministration.oneBrehm = true;
        } else if (algSelection.equals(ALL_REALIZER_BREHM)) {
            SchnyderRealizerAdministration.allBrehm = true;
        } else if (algSelection.equals(HE_IMPROVEMENT)) {
            SchnyderRealizerAdministration.improvementHe = true;
        } else {
            SchnyderRealizerAdministration.improvedFPP = true;
        }
        this.parameters = params;
    }

    /**
     * Gets the parameters of the algorithm
     * 
     * @return the parameters of the algorithm
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        Parameter<?>[] parameter = new Parameter[10];
        String[] algParams = { ONE_REALIZER, ALL_CANONICAL_ORDERS,
                ALL_REALIZERS, ONE_REALIZER_BREHM, ALL_REALIZER_BREHM,
                HE_IMPROVEMENT, IMPROVED_FPP };
        BooleanParameter label = new BooleanParameter(true, "Label Nodes",
                "Label the nodes (existing labels will be overwritten)");
        BooleanParameter removeEdges = new BooleanParameter(false,
                "Remove Edges",
                "Remove the edges added during the triangulation at the end of "
                        + "the algorithm");
        BooleanParameter reinsert = new BooleanParameter(false,
                "Loops, Multiedges",
                "Loops and Multiedges have to be removed at the beginning. "
                        + "Reinsert them at the end");
        BooleanParameter colors = new BooleanParameter(true, "Color edges",
                "Color the edges / realizer");
        BooleanParameter directEdges = new BooleanParameter(true, "Root edges",
                "Root all edges towards the corresponding outer node");
        BooleanParameter writeOutput = new BooleanParameter(false,
                "Write output to file",
                "Write a detailled output and save data for further analysis "
                        + "(only for algorithm (5))");
        StringSelectionParameter algorithm = new StringSelectionParameter(
                algParams, "ALGORITHM", "get one or all realizers");
        IntegerParameter getNodeSize = new IntegerParameter(new Integer(25),
                new Integer(1), new Integer(100), "Node Size:",
                "Size of the nodes in the drawing:");
        IntegerParameter getGridSize = new IntegerParameter(new Integer(50),
                new Integer(1), new Integer(200), "Grid Size:",
                "Size of one grid in the drawing (should be about twice the node "
                        + "size):");
        IntegerParameter getMaxNumOfRealizers = new IntegerParameter(
                new Integer(1000),
                new Integer(1),
                new Integer(100000),
                "Max of realizers:",
                "Algorithm will not calculate more realizers / canonical orders "
                        + "than this (to prevent OutOfMemoryErrors, dependent of your "
                        + "computer");
        parameter[0] = label;
        parameter[1] = removeEdges;
        parameter[2] = reinsert;
        parameter[3] = colors;
        parameter[4] = directEdges;
        parameter[5] = writeOutput;
        parameter[6] = algorithm;
        parameter[7] = getNodeSize;
        parameter[8] = getGridSize;
        parameter[9] = getMaxNumOfRealizers;
        return parameter;
    }

    /**
     * Checks the algorithms preconditions: - graph is planar - graph has more
     * than three vertices - graph is biconnected
     * 
     * @throws PreconditionException
     *             if any of the preconditions is not satisfied.
     */
    @Override
    public void check() throws PreconditionException {
        this.graph = GraffitiSingleton.getInstance().getMainFrame()
                .getActiveSession().getGraph();
        PlanarityAlgorithm pAlgorithm = new PlanarityAlgorithm();
        pAlgorithm.attach(graph);
        pAlgorithm.testPlanarity();

        if (this.graph.getNumberOfNodes() < 4)
            throw new PreconditionException(
                    "The graph is too small. The number of nodes must be > 3.");
        if (!pAlgorithm.isPlanar())
            throw new PreconditionException(
                    "The graph is not planar. Graph must be planar to run "
                            + "Schnyder Realizer.");

        if (!checkBiconnection(pAlgorithm))
            throw new PreconditionException(
                    "The graph is not biconnected. Graph must be biconnected to "
                            + "run Schnyder Realizer.");
    }

    /**
     * Decides (from the parameters), which algorithm to perform and executes
     * it. Furthermore all the administration work mentioned above is done here.
     */
    public void execute() {
        String algorithmString = SchnyderRealizerAdministration.ONE_REALIZER;
        if (SchnyderRealizerAdministration.allCanonicalOrders) {
            algorithmString = SchnyderRealizerAdministration.ALL_CANONICAL_ORDERS;
        } else if (SchnyderRealizerAdministration.allRealizers) {
            algorithmString = SchnyderRealizerAdministration.ALL_REALIZERS;
        } else if (SchnyderRealizerAdministration.oneBrehm) {
            algorithmString = SchnyderRealizerAdministration.ONE_REALIZER_BREHM;
        } else if (SchnyderRealizerAdministration.allBrehm) {
            algorithmString = SchnyderRealizerAdministration.ALL_REALIZER_BREHM;
        } else if (SchnyderRealizerAdministration.improvementHe) {
            algorithmString = SchnyderRealizerAdministration.HE_IMPROVEMENT;
        } else if (SchnyderRealizerAdministration.improvedFPP) {
            algorithmString = SchnyderRealizerAdministration.IMPROVED_FPP;
        }

        logger
                .info("Starting algorithm to calculate Schnyder realizers with "
                        + "the following parameters:\n"
                        + "CHOSEN ALGORITHM: "
                        + algorithmString
                        + "\n"
                        + "- Label the nodes and overwrite existing labels: "
                        + SchnyderRealizerAdministration.label
                        + "\n"
                        + "- Remove the edges added during the triangulation of the graph: "
                        + SchnyderRealizerAdministration.removeAdded
                        + "\n"
                        + "- Reinsert loops and multiedges: "
                        + SchnyderRealizerAdministration.readd
                        + "\n"
                        + "- Color the edges: "
                        + SchnyderRealizerAdministration.color
                        + "\n"
                        + "- Root all edges towards the corresponding outer node: "
                        + SchnyderRealizerAdministration.directEdges
                        + "\n"
                        + "- Write a detailed output of the calculated realizer to "
                        + "the database: "
                        + SchnyderRealizerAdministration.writeToFile);

        this.sizeNodes();
        this.removeMultiedgesLoops();
        this.removeBends();

        if (SchnyderRealizerAdministration.oneRealizer) {
            this.ada = new SchnyderOneRealizer(this.graph,
                    SchnyderRealizerAdministration.maxNumberOfRealizers);
        } else if (SchnyderRealizerAdministration.allCanonicalOrders) {
            this.ada = new SchnyderAllCanonicalOrders(this.graph,
                    SchnyderRealizerAdministration.maxNumberOfRealizers);
        } else if (SchnyderRealizerAdministration.allRealizers) {
            this.ada = new SchnyderAllRealizer(this.graph,
                    SchnyderRealizerAdministration.maxNumberOfRealizers);
        } else if (SchnyderRealizerAdministration.oneBrehm) {
            this.ada = new BrehmOneRealizer(this.graph,
                    SchnyderRealizerAdministration.maxNumberOfRealizers);
        } else if (SchnyderRealizerAdministration.allBrehm) {
            this.ada = new BrehmAllRealizers(this.graph,
                    SchnyderRealizerAdministration.maxNumberOfRealizers);
        } else if (SchnyderRealizerAdministration.improvementHe) {
            this.ada = new HeImprovement(this.graph,
                    SchnyderRealizerAdministration.maxNumberOfRealizers);
        } else if (SchnyderRealizerAdministration.improvedFPP) {
            this.ada = new ImprovedFPP(this.graph,
                    SchnyderRealizerAdministration.maxNumberOfRealizers);
        }
        if (SchnyderRealizerAdministration.label) {
            this.labelNodes();
        }
        this.ada.execute();
        this.realizers = ada.getRealizers();
        this.barycentricReps = ada.getBarycentricReps();
        this.showResult();
    }

    /**
     * Checks if the graph is biconnected, by counting the components of the
     * <code>TestedGraph</code>
     * 
     * @return true if the graph is biconnected
     */
    private boolean checkBiconnection(PlanarityAlgorithm pAlgorithm) {
        if (pAlgorithm.getTestedGraph().getNumberOfComponents() > 1)
            return false;
        TestedComponent comp = pAlgorithm.getTestedGraph()
                .getTestedComponents().get(0);
        if (comp.getNumberOfBicomps() > 1)
            return false;
        return true;
    }

    /**
     * Checks every edge if there are other edges connecting the same nodes and
     * if so removes them. This is necessary as loops and multiedges don`t play
     * any role in a drawing created by Schnyder`s realizer and just complicate
     * the algorithm.
     */
    private void removeMultiedgesLoops() {
        HashList<Edge> edgesToDelete = new HashList<Edge>();
        Iterator<Edge> edgeIt = this.graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();

            if (e.getSource() == e.getTarget()) {
                System.out.println("REMOVED");
                this.removedEdges.add(e);
                edgesToDelete.append(e);
            }
        }
        edgeIt = this.graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            Collection<Edge> edges = this.graph.getEdges(e.getSource(), e
                    .getTarget());
            if (edges.size() > 1) {
                Iterator<Edge> multiEdgeIt = edges.iterator();
                multiEdgeIt.next();
                while (multiEdgeIt.hasNext()) {
                    Edge multiEdge = multiEdgeIt.next();
                    edgesToDelete.append(multiEdge);
                    this.removedEdges.add(multiEdge);
                }
            }
        }
        edgeIt = edgesToDelete.iterator();
        while (edgeIt.hasNext()) {
            this.graph.deleteEdge(edgeIt.next());
        }
    }

    /**
     * Removes all bends from all edges in the graph (as these would completely
     * destroy the layout of the graph drawn with Schnyder`s realizer. (Only the
     * bends in the removed loops and multiedges are kept, as these wouldn`t be
     * visible after the reinsertion otherwise.)
     */
    private void removeBends() {
        Iterator<Edge> edgeIt = this.graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) e
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            ega.setBends(new LinkedHashMapAttribute(
                    GraphicAttributeConstants.BENDS));
        }
    }

    /**
     * All nodes are set to circular shape and a determined size. This is
     * necessary for good drawings.
     */
    private void sizeNodes() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            NodeGraphicAttribute nga = (NodeGraphicAttribute) n
                    .getAttribute("graphics");
            nga
                    .setShape(org.graffiti.graphics.GraphicAttributeConstants.ELLIPSE_CLASSNAME);
            nga.getDimension().setHeight(
                    SchnyderRealizerAdministration.nodeSize);
            nga.getDimension()
                    .setWidth(SchnyderRealizerAdministration.nodeSize);
        }
    }

    /**
     * All nodes are labeled with ascending integer numbers. The outer nodes are
     * labeled with "1", "n-1" and "n", where n is the number nodes in the
     * graph. Existing labels are overwritten. This method is only executed if
     * <code>SchnyderRealizerAdministration.label</code> is set true.
     */
    private void labelNodes() {
        Node[] outerNodes = this.ada.getOuterNodes();
        int labelOfNode = 2;
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node nodeToLabel = nodesIt.next();
            String labelString = (labelOfNode + "");
            if (nodeToLabel == outerNodes[0]) {
                labelString = ("1");
                labelOfNode--;
            } else if (nodeToLabel == outerNodes[1]) {
                labelString = (this.graph.getNodes().size() - 1 + "");
                labelOfNode--;
            } else if (nodeToLabel == outerNodes[2]) {
                labelString = ((this.graph.getNodes().size() + ""));
                labelOfNode--;
            }
            NodeLabelAttribute label = new NodeLabelAttribute("label");
            // Check if there is an existing label
            try {
                label = (NodeLabelAttribute) nodeToLabel.getAttributes()
                        .getAttribute("label");

                label.setLabel(labelString);
            } catch (AttributeNotFoundException e) {
                label.setLabel(labelString);
                nodeToLabel.getAttributes().add(label);
            }
            labelOfNode++;
        }
    }

    /**
     * Draws the graph according to the first realizer of the graph. If there is
     * more than one realizer a message box is shown to switch through the
     * drawings.
     */
    private void showResult() {
        int position = 0;
        drawGraph(position);
        // Check if this is an algorithm, that calcualtes all realzers
        if ((this.ada instanceof SchnyderAllCanonicalOrders)
                || (this.ada instanceof SchnyderAllRealizer)
                || (this.ada instanceof BrehmAllRealizers)) {
            while (true) {
                // Create the message box
                int chosen = this.getJOptionPane(position);
                if (chosen == 0) {
                    position--;
                    if (position < 0) {
                        position = this.realizers.size() - 1;
                    }
                } else if (chosen == 1) {
                    position = (position + 1) % this.realizers.size();
                }
                this.drawGraph(position);
                if ((chosen != 0) && (chosen != 1)) {
                    break;
                }
            }
        }
    }

    /**
     * Draws the graph according to the parameters of the algorithm. The drawing
     * is according to a certain realizer from the list of realizers, which is
     * determined by a given list position.
     * 
     * @param position
     *            the position in the list of realizers
     */
    private void drawGraph(int position) {
        if (SchnyderRealizerAdministration.color) {
            this.colorEdges(position);
        }
        if (SchnyderRealizerAdministration.directEdges) {
            this.directEdges(position);
        }
        this.draw(position);
        if (SchnyderRealizerAdministration.readd) {
            this.reinsertEdges();
        }
        if (SchnyderRealizerAdministration.removeAdded) {
            this.removeInserted();
        }
    }

    /**
     * Colors all edges in the graph according to the current realizer. Is
     * online executed, if the parameter
     * <code>SchnyderRealizerAdministration.color</code> is set true.
     */
    protected void colorEdges(int position) {
        Iterator<Edge> edgesIt = this.graph.getEdgesIterator();
        while (edgesIt.hasNext()) {
            this
                    .colorEdge(edgesIt.next(),
                            SchnyderRealizerAdministration.BLACK);
        }
        edgesIt = this.realizers.get(position).getGreenEdges().iterator();
        while (edgesIt.hasNext()) {
            this
                    .colorEdge(edgesIt.next(),
                            SchnyderRealizerAdministration.GREEN);
        }
        edgesIt = this.realizers.get(position).getBlueEdges().iterator();
        while (edgesIt.hasNext()) {
            this.colorEdge(edgesIt.next(), SchnyderRealizerAdministration.BLUE);
        }
        edgesIt = this.realizers.get(position).getRedEdges().iterator();
        while (edgesIt.hasNext()) {
            this.colorEdge(edgesIt.next(), SchnyderRealizerAdministration.RED);
        }
    }

    /**
     * Colors an edge in a given color. The existing color is overwritten.
     * 
     * @param e
     *            the edge to be colored
     * @param color
     *            an integer key of the color (1=green, 2=blue, 3 = red,
     *            0=black)
     */
    private void colorEdge(Edge e, int color) {
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) e
                .getAttribute("graphics");
        ColorAttribute c = ega.getFramecolor();
        ColorAttribute d = ega.getFillcolor();
        if (color == SchnyderRealizerAdministration.GREEN) {
            c.setColor(Color.GREEN);
            d.setColor(Color.GREEN);
        } else if (color == SchnyderRealizerAdministration.RED) {
            c.setColor(Color.RED);
            d.setColor(Color.RED);
        } else if (color == SchnyderRealizerAdministration.BLUE) {
            c.setColor(Color.BLUE);
            d.setColor(Color.BLUE);
        } else if (color == SchnyderRealizerAdministration.BLACK) {
            c.setColor(Color.BLACK);
            d.setColor(Color.BLACK);
        }
    }

    /**
     * Method directs all edges towards the root of the tree it belongs to. Is
     * only executed, if <code>SchnyderRealizerAdministration.directEdges</code>
     * is set true.
     */
    public void directEdges(int position) {

        this.graph.setDirected(Edge.DIRECTED, true);
        Iterator<Node> treeIt = this.realizers.get(position).getGreen()
                .keySet().iterator();
        while (treeIt.hasNext()) {
            Node child = treeIt.next();
            Node father = this.realizers.get(position).getGreen().get(child);
            Edge e = this.graph.getEdges(child, father).iterator().next();
            if (e.getSource() == father) {
                e.reverse();
            }
        }
        treeIt = this.realizers.get(position).getBlue().keySet().iterator();
        while (treeIt.hasNext()) {
            Node child = treeIt.next();
            Node father = this.realizers.get(position).getBlue().get(child);
            Edge e = this.graph.getEdges(child, father).iterator().next();
            if (e.getSource() == father) {
                e.reverse();
            }
        }
        treeIt = this.realizers.get(position).getRed().keySet().iterator();
        while (treeIt.hasNext()) {
            Node child = treeIt.next();
            Node father = this.realizers.get(position).getRed().get(child);
            Edge e = this.graph.getEdges(child, father).iterator().next();
            if (e.getSource() == father) {
                e.reverse();
            }
        }
    }

    /**
     * This methods draws the graph into a grid of size (n-2 x n-2), where n is
     * the number of nodes in the graph.
     */
    protected void draw(int position) {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            NodeGraphicAttribute nga = (NodeGraphicAttribute) n
                    .getAttribute("graphics");
            int greenCoordinate = this.barycentricReps.get(position)
                    .getCoordinate(n, AbstractDrawingAlgorithm.GREEN);
            int blueCoordinate = this.barycentricReps.get(position)
                    .getCoordinate(n, AbstractDrawingAlgorithm.BLUE);

            nga.getCoordinate().setX(
                    greenCoordinate * SchnyderRealizerAdministration.gridSize
                            + SchnyderRealizerAdministration.nodeSize);
            nga.getCoordinate().setY(
                    blueCoordinate * SchnyderRealizerAdministration.gridSize
                            + SchnyderRealizerAdministration.nodeSize);
        }
    }

    /**
     * Removes all edges, that were added during the triangulation algorithm
     * from the graph. Is online executed, if the parameter
     * <code>SchnyderRealizerAdministration.removeAdded</code> is set true.
     */
    protected void removeInserted() {
        Iterator<Edge> edgeIt = this.ada.getAddedEdges().iterator();
        while (edgeIt.hasNext()) {
            this.graph.deleteEdge(edgeIt.next());
        }
    }

    /**
     * Reinserts all removed loops and multiedges, that were removed at the
     * beginning of the algorithm. The bends in these edges are kept as they
     * would not be visible in the drawing otherwise. Is online executed, if the
     * parameter <code>SchnyderRealizerAdministration.readd</code> is set true.
     * 
     */
    protected void reinsertEdges() {
        Iterator<Edge> edgeIt = this.removedEdges.iterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            this.graph.addEdgeCopy(e, e.getSource(), e.getTarget());
        }
    }

    /**
     * Shows a message box to switch through the different drawings of a graph.
     * 
     * @param orderPosition
     *            The current position the drawing has in the list of drawings.
     * @return an integer value indicating, which button has been pressed.
     */
    private int getJOptionPane(int orderPosition) {
        String[] options = { "<<", ">>", "Cancel" };
        String title = "Next or last realizer";
        String message = "THIS IS CANONICAL ORDER / REALIZER "
                + (orderPosition + 1) + " OF " + (this.realizers.size());
        return JOptionPane.showOptionDialog(null, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                options, options[1]);
    }
}
