package org.graffiti.plugins.algorithms.eades;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.NodeShapeAttribute;
import org.graffiti.editor.GraffitiInternalFrame;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.view.Zoomable;
import org.graffiti.selection.Selection;
import org.graffiti.session.Session;

/**
 * This class implements the Algorithm of Eades
 * 
 * @author Hilmi Ertuerk
 * @version 1.35 date 16. Juli 2006
 */
public class AlgorithmEades extends AbstractAlgorithm {

    /** Constant for Node Attributes Height */
    @SuppressWarnings("nls")
    public static final String HEIGHT = "height";

    /**
     * Constant for Node Attributes leaves
     */
    @SuppressWarnings("nls")
    public static final String LEAVES = "leaves";

    /**
     * Constant for Node Attributes diameter
     */
    @SuppressWarnings("nls")
    public static final String DIAMETER = "diameter";

    /**
     * Constant for Node Attributes diameterSumOfSons
     */
    @SuppressWarnings("nls")
    public static final String DIAMETER_SUM_OF_SONS = "diameterSumOfSons";

    /**
     * Constant for Node Attributes numberOfNeighbors
     */
    @SuppressWarnings("nls")
    private static final String NUMBER_OF_NEIGHBORS = "numberOfNeighbors";

    /**
     * This option is for pushing Leaves to the outside..
     */
    private boolean pushLeaves = false;

    /**
     * allocate every leave even space
     */
    private boolean useEades = true;

    /**
     * make graph convex
     */
    private boolean makeConvex = true;

    /**
     * make ordering dependant on size of node
     */
    private boolean makeSizeDependant = true;

    /**
     * auto enlarge radius
     */
    private boolean autoEnlargeRadius = true;

    /**
     * stores the maximum dimension of a node
     */
    private double maxDimensionOfNodes;

    /**
     * default radius size
     */
    private int radius = 75;

    private Selection selectedRootNodes = new Selection();

    /**
     * temporaly hashSet for isTree method
     */
    private HashSet<Node> visited = new HashSet<Node>();

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    @SuppressWarnings("nls")
    public String getName() {
        return "EadesAlgorithm";
    }

    /**
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "Eades will start with the only selected node.");

        BooleanParameter bolParam = new BooleanParameter(new Boolean(
                this.pushLeaves), "leave option", "push the leaves outside");

        BooleanParameter bolParam2 = new BooleanParameter(new Boolean(
                this.useEades), "space option",
                "allocate every leave even space");

        BooleanParameter convexParam = new BooleanParameter(new Boolean(
                this.makeConvex), "convex option", "make convex");

        BooleanParameter variableSizeParam = new BooleanParameter(new Boolean(
                this.makeSizeDependant), "sizeDependant",
                "make ordering dependant on size of node");

        BooleanParameter autoEnlargeRadiusParam = new BooleanParameter(
                new Boolean(this.autoEnlargeRadius), "auto enlarge radius",
                "auto enlarge radius...");

        Integer min = new Integer(20);
        Integer max = new Integer(300);
        IntegerParameter radiusParam = new IntegerParameter(new Integer(
                this.radius), min, max, "radius", "Choose own radius");

        return new Parameter[] { selParam, bolParam, bolParam2, convexParam,
                variableSizeParam, autoEnlargeRadiusParam, radiusParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#setAlgorithmParameters(org.graffiti.plugin.parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {

        this.parameters = params;

        this.selectedRootNodes = ((SelectionParameter) params[0])
                .getSelection();

        this.pushLeaves = ((BooleanParameter) params[1]).getBoolean();

        this.useEades = ((BooleanParameter) params[2]).getBoolean();

        this.makeConvex = ((BooleanParameter) params[3]).getBoolean();

        this.makeSizeDependant = ((BooleanParameter) params[4]).getBoolean();

        this.autoEnlargeRadius = ((BooleanParameter) params[5]).getBoolean();

        this.radius = ((IntegerParameter) params[6]).getInteger();
    }

    /**
     * checks if the graph is a tree.
     * 
     * @param current
     *            Node to consider as root
     * @param predecessor
     *            Node predecessor from current
     * @return if graph starting at Node <code>current</code> is a tree
     */
    private boolean isTree(Node current, Node predecessor) {

        if (this.visited.contains(current))
            return false;

        this.visited.add(current);

        HashSet<Node> neighbors = new HashSet<Node>();
        neighbors.addAll(current.getNeighbors());

        if ((neighbors.size() == 0)
                || ((predecessor != null) && (neighbors.size() == 1) && (neighbors
                        .contains(predecessor))))
            return true;

        final Iterator<Node> neighborIterator = neighbors.iterator();

        while (neighborIterator.hasNext()) {

            Node son = neighborIterator.next();
            if ((son == predecessor) || (son == current)) {
                continue;
            }

            if (!isTree(son, current))
                return false;
        }
        return true;
    }

    /**
     * check conditions
     * 
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#check()
     * 
     */
    @Override
    public void check() throws PreconditionException {
        final PreconditionException exp = new PreconditionException();

        if (this.graph.getNumberOfNodes() == 0) {
            exp.add("At least one Node!!");
        }

        // user have chosen more than one selected node
        if ((this.selectedRootNodes.getNodes().size() > 1)) {
            exp.add("Eades needs at most one selected node.");
        }

        // check for beeing a tree
        boolean isTree = true;
        if (exp.isEmpty()) {
            if (((this.selectedRootNodes.getNodes().size() == 1) && !isTree(
                    this.selectedRootNodes.getNodes().get(0), null))
                    || ((this.selectedRootNodes.getNodes().size() == 0) && !isTree(
                            this.graph.getNodes().get(0), null))) {

                exp.add("Has to be a tree!");
                isTree = false;
            }
        }

        if (isTree && (this.visited.size() != this.graph.getNumberOfNodes())) {
            exp.add("Please connect all nodes with edges!");
        }

        this.visited.clear();
        if (!exp.isEmpty())
            throw exp;
    }

    /**
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {

        graph.getListenerManager().transactionStarted(this);

        // if no node is selected...
        if (selectedRootNodes.getNodes().size() == 0) {

            // ... we calculate the root
            // setRootNode();
            setRootNode_New();
        }

        // if one node is selected...
        if (this.selectedRootNodes.getNodes().size() == 1) {

            setNodeHeights(this.selectedRootNodes);
            setNumberOfLeaves(this.selectedRootNodes.getNodes().get(0), null);
            startAlgo(this.selectedRootNodes.getNodes().get(0));

            // else if two nodes are selected ....
        } else {

            final Node root1 = selectedRootNodes.getNodes().get(0);
            final Node root2 = selectedRootNodes.getNodes().get(1);
            final Collection<Edge> edgesBetweenRoots = graph.getEdges(root1,
                    root2);

            for (Edge edge : edgesBetweenRoots) {
                graph.deleteEdge(edge);
            }

            // set the virtual node
            final Node virtualNode = this.graph.addNode();

            // modify the connection of the edges
            graph.addEdge(virtualNode, root1, false);
            graph.addEdge(virtualNode, root2, false);
            Selection virtualRootSelection = new Selection();
            virtualRootSelection.add(virtualNode);

            // calculate the height of the node with virtual node as root
            setNodeHeights(virtualRootSelection);
            setNumberOfLeaves(virtualNode, null);
            startAlgo(virtualNode);

            // remove it again
            graph.deleteNode(virtualNode);

            // correct the edges
            for (Edge edge : edgesBetweenRoots) {
                graph.addEdgeCopy(edge, edge.getSource(), edge.getTarget());
            }
        }

        moveAndResizeGraph();

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * positions the graph to the center and zooming it to fit the page
     */
    private void moveAndResizeGraph() {

        // Test for Unit-Test :)
        if (GraffitiSingleton.getInstance().getMainFrame() == null)
            return;

        // move graph...
        double minX = Integer.MAX_VALUE;
        double minY = Integer.MAX_VALUE;

        for (Node n : graph.getNodes()) {

            CoordinateAttribute ca = (CoordinateAttribute) n
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);

            final double dim = getDimensionOfNode(n);
            final Point2D p = ca.getCoordinate();
            minX = Math.min(p.getX() - dim, minX);
            minY = Math.min(p.getY() - dim, minY);
        }

        double maxX = Integer.MIN_VALUE;
        double maxY = Integer.MIN_VALUE;

        for (Node n : graph.getNodes()) {

            CoordinateAttribute ca = (CoordinateAttribute) n
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);

            // distance to the border
            final double dim = getDimensionOfNode(n);
            final Point2D p = ca.getCoordinate();
            p.setLocation(p.getX() - minX + 5, p.getY() - minY + 5);
            ca.setCoordinate(p);

            maxX = Math.max(p.getX() + dim, maxX);
            maxY = Math.max(p.getY() + dim, maxY);
        }

        // zoom graph to fit the screen...
        final GraffitiSingleton gs = GraffitiSingleton.getInstance();
        final MainFrame mf = gs.getMainFrame();

        for (GraffitiInternalFrame iFrame : mf.getActiveFrames()) {

            final Session ses = iFrame.getSession();

            if (mf.getActiveEditorSession() == ses) {

                double frameWidth = iFrame.getWidth();
                double frameHeight = iFrame.getHeight();

                double zoom = 1.0;

                if (maxX > frameWidth) {
                    zoom = frameWidth / maxX;
                }

                if (maxY > frameHeight)
                    if ((frameHeight / maxY) < zoom) {
                        zoom = frameHeight / maxY;
                    }

                zoom *= 0.95;

                Session activeSession = mf.getActiveSession();

                Zoomable zoomView = activeSession.getActiveView();

                zoomView.setZoom(zoom);
            }
        }
    }

    /**
     * algorithm getting started
     * 
     * @param startNode
     *            gives the root of the graph
     * 
     */
    private void startAlgo(Node startNode) {

        this.maxDimensionOfNodes = 0;
        apexAngle(startNode, null, 0);

        if (autoEnlargeRadius) {
            this.radius = (int) Math.ceil(maxDimensionOfNodes);
        }

        algo(startNode, null, 0, 2 * Math.PI, 0);
    }

    /**
     * calculates the diameter of the subtree
     * 
     * @param node
     *            is the current node
     * @param predecessor
     *            is the predecessor of node
     * @param circleNum
     *            is the circleNumber for the node
     * @return sum of the diameters of all nodes below <code>node</code>
     */
    private double apexAngle(Node node, Node predecessor, int circleNum) {

        // gives the dimension of node
        double dimOfNode = getDimensionOfNode(node);

        // store the biggest node for autoEnlargeRadius
        this.maxDimensionOfNodes = Math
                .max(this.maxDimensionOfNodes, dimOfNode);

        dimOfNode /= circleNum;

        Collection<Node> set = node.getNeighbors();
        set.remove(predecessor);
        set.remove(node);

        // for one node
        if (set.isEmpty()) {
            node.setDouble(DIAMETER, dimOfNode);
            node.setDouble(DIAMETER_SUM_OF_SONS, dimOfNode);
            return dimOfNode;
        }

        // if there is more than one node...
        double sum = 0.0;
        for (Node n : set) {
            sum += apexAngle(n, node, circleNum + 1);
        }

        double result = Math.max(sum, dimOfNode);

        node.setDouble(DIAMETER_SUM_OF_SONS, sum);
        node.setDouble(DIAMETER, result);

        return result;
    }

    /**
     * make a DFS and check the positions of the nodes,look for dfsHeightLabeler
     * 
     * @param selectedRootNodes
     *            contains the selected node
     */
    private void setNodeHeights(Selection selectedRootNodes) {
        DFS dfs = new DFS(new DFSHeightLabeler());
        dfs.attach(this.graph);
        dfs.setAlgorithmParameters(new Parameter[] { new SelectionParameter(
                selectedRootNodes, "Start node",
                "DFS will start with the only selected node.") });

        try {
            dfs.check();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dfs.execute();
        int height = selectedRootNodes.getNodes().get(0).getInteger(HEIGHT);
        for (Node node : graph.getNodes()) {

            // difference
            node.setInteger(HEIGHT, height - node.getInteger(HEIGHT));
        }
    }

    /**
     * count the number of leaves
     * 
     * @param node
     *            is the current node
     * @param predecessor
     *            is the predecessor of the current node
     * @return number of leaves
     */
    private int setNumberOfLeaves(Node node, Node predecessor) {

        Collection<Node> c = node.getNeighbors();
        c.remove(node);
        c.remove(predecessor);

        if (c.size() == 0) {

            // if its a leave
            node.setInteger(LEAVES, 1);
            return 1;
        }
        // number of leaves
        int sum = 0;
        for (Node n : c) {
            sum += setNumberOfLeaves(n, node);
        }

        node.setInteger(LEAVES, sum);

        return sum;
    }

    /**
     * calculates for each node the right position
     * 
     * @param n
     *            is the current node
     * @param predecessor
     *            is the predecessor node of n
     * @param leftAngle
     *            is the angle left from n
     * @param rightAngle
     *            is the angle right from n
     * @param circleNumber
     *            is the circle number which contains n
     */
    private void algo(Node n, Node predecessor, double leftAngle,
            double rightAngle, int circleNumber) {

        int circleToUse = circleNumber;
        int radiusToUse = this.radius;
        double x = 0, y = 0;

        // actual position of the nodes
        final CoordinateAttribute ca;

        ca = (CoordinateAttribute) n
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);

        // pushing the leave outside
        if (this.pushLeaves && (circleNumber > 0)) {
            circleToUse = n.getInteger(HEIGHT);
        }

        // if the circleNumber which contains the node is > 0
        if (circleNumber > 0) {
            final double angleOfNode = (rightAngle + leftAngle) / 2.0;
            x = Math.cos(angleOfNode) * (circleToUse * radiusToUse);
            y = Math.sin(angleOfNode) * (circleToUse * radiusToUse);
        }

        // setting the node at the intersection of the circlenumber and the
        // center
        ca.setCoordinate(new Point2D.Double(x, y));

        // range for the subtrees
        double range = rightAngle - leftAngle;

        // variant being convex:
        if (this.makeConvex && (circleNumber > 0)) {
            final double teta = 2 * Math.acos(circleNumber
                    / (circleNumber + 1.0));

            // if it was not convex,left and right angle will be updated
            if (teta < range) {
                final double temp = (range - teta) / 2;
                leftAngle += temp;
                rightAngle -= temp;

                range = teta;
            }
        }

        final Collection<Node> set = n.getNeighbors();
        set.remove(predecessor);
        set.remove(n);

        final int numberOfSons = set.size();
        final double spaceForOneSon;

        // 3 kind of differences:
        if (this.makeSizeDependant) {

            // 1.sum of the diameter of all sons
            spaceForOneSon = range / n.getDouble(DIAMETER_SUM_OF_SONS);
        } else {
            if (this.useEades) {
                // 2.depends on number of leaves
                spaceForOneSon = range / n.getInteger(LEAVES);
            } else {
                // 3.depends on number of sons
                spaceForOneSon = range / numberOfSons;
            }
        }

        double newLeft, newRight = leftAngle;

        for (Node son : set) {

            // new node starts where old one ends..
            newLeft = newRight;

            if (this.makeSizeDependant) {
                newRight = newLeft + (son.getDouble(DIAMETER) * spaceForOneSon);
            } else {
                if (this.useEades) {
                    newRight = newLeft
                            + (son.getInteger(LEAVES) * spaceForOneSon);
                } else {
                    newRight = newLeft + spaceForOneSon;
                }
            }
            algo(son, n, newLeft, newRight, circleToUse + 1);
        }
    }

    /**
     * degree of the node
     */
    private int degree(Node node) {

        int val = 0;

        val = node.getInteger("DEGREE");

        if (val > 0)
            return val;

        Collection<Node> c = node.getNeighbors();
        c.remove(node);

        node.setInteger("DEGREE", c.size());

        return c.size();
    }

    /**
     * 
     * @param node
     *            is the given node
     * @return the parent of node
     */
    private Node getParent(Node node) {

        Collection<Node> c = node.getNeighbors();
        c.remove(node);

        for (Node n : c) {
            if (!n.getBoolean("ADDED"))
                return n;
        }
        return null;
    }

    /**
     * calculate the root node
     * 
     */
    private void setRootNode_New() {

        LinkedList<Node> l = new LinkedList<Node>();
        l.add(null);
        int n = 0;

        for (Node node : graph.getNodes()) {
            node.setBoolean("ADDED", false);
            node.setInteger("DEGREE", 0);
            if (degree(node) == 1) {
                node.setBoolean("ADDED", true);
                n++;
                l.add(node);
            }
        }

        int nrNodes = graph.getNodes().size();
        while (n < nrNodes) {

            final Node node = l.removeFirst();
            if (node == null) {
                l.add(null);
            } else {
                final Node parent = getParent(node);
                int deg = degree(parent) - 1;
                parent.setInteger("DEGREE", deg);
                if (deg == 1) {
                    if (!parent.getBoolean("ADDED")) {
                        parent.setBoolean("ADDED", true);
                        n++;
                    }
                    l.add(parent);
                }
            }
        }

        while (l.removeFirst() != null) {
            ;
        }
        for (Node node : l) {
            if (node != null) {
                selectedRootNodes.add(node);
            }
        }
    }

    /**
     * gives the dimension of the node
     */
    @SuppressWarnings("nls")
    private double getDimensionOfNode(Node n) {

        // gives the shape of the node
        final NodeShapeAttribute nsa;
        final DimensionAttribute da;

        try {
            n.getAttribute(GraphicAttributeConstants.GRAPHICS);
        } catch (AttributeNotFoundException e) {
            n.addAttribute(new NodeGraphicAttribute(), "");
        }

        nsa = (NodeShapeAttribute) n
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR + GraphicAttributeConstants.SHAPE);

        // gives the dimension of the node
        da = (DimensionAttribute) n
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.DIMENSION);

        final double w = da.getWidth();
        final double h = da.getHeight();

        // getting the shape
        final String shape = nsa.getString();

        if (shape.equals(GraphicAttributeConstants.RECTANGLE_CLASSNAME))
            return Math.sqrt(w * w + h * h);
        else if (shape.equals(GraphicAttributeConstants.CIRCLE_CLASSNAME)) {
            if (w != h) {
                System.err.println("This is not a circle!!!");
            }
            return w;
        } else if (shape.equals(GraphicAttributeConstants.ELLIPSE_CLASSNAME))
            return Math.max(w, h);
        else if (shape.equals(GraphicAttributeConstants.POLYLINE_CLASSNAME))
            return Math.sqrt(w * w + h * h);
        else {
            System.err.println("Unknown Shape: " + shape);
            return 42;
        }
    }

    /**
     * Returns the autoEnlargeRadius.
     * 
     * @return the autoEnlargeRadius.
     */
    public boolean isAutoEnlargeRadius() {
        return autoEnlargeRadius;
    }

    /**
     * Sets the autoEnlargeRadius.
     * 
     * @param autoEnlargeRadius
     *            the autoEnlargeRadius to set.
     */
    public void setAutoEnlargeRadius(boolean autoEnlargeRadius) {
        this.autoEnlargeRadius = autoEnlargeRadius;
    }

    /**
     * Returns the makeConvex.
     * 
     * @return the makeConvex.
     */
    public boolean isMakeConvex() {
        return makeConvex;
    }

    /**
     * Sets the makeConvex.
     * 
     * @param makeConvex
     *            the makeConvex to set.
     */
    public void setMakeConvex(boolean makeConvex) {
        this.makeConvex = makeConvex;
    }

    /**
     * Returns the makeSizeDependant.
     * 
     * @return the makeSizeDependant.
     */
    public boolean isMakeSizeDependant() {
        return makeSizeDependant;
    }

    /**
     * Sets the makeSizeDependant.
     * 
     * @param makeSizeDependant
     *            the makeSizeDependant to set.
     */
    public void setMakeSizeDependant(boolean makeSizeDependant) {
        this.makeSizeDependant = makeSizeDependant;
    }

    /**
     * Returns the radius.
     * 
     * @return the radius.
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Sets the radius.
     * 
     * @param radius
     *            the radius to set.
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * Returns if the algo should use the eades variante.
     * 
     * @return if the algo should use eades
     */
    public boolean isUseEades() {
        return useEades;
    }

    /**
     * Sets the useEades.
     * 
     * @param useEades
     *            the useEades to set.
     */
    public void setUseEades(boolean useEades) {
        this.useEades = useEades;
    }

    /**
     * inner class NodeDegreeComparator<br>
     * this class compares the degree of two <code>Node</code>s.
     */
    private class NodeDegreeComparator<T extends Node> implements Comparator<T> {

        /**
         * @see java.util.Comparator#compare(T, T)
         */
        public int compare(T arg0, T arg1) {
            final Node n1 = arg0;
            final Node n2 = arg1;
            return n1.getInteger(NUMBER_OF_NEIGHBORS)
                    - n2.getInteger(NUMBER_OF_NEIGHBORS);
        }
    }
}