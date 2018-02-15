package org.graffiti.plugins.algorithms.hexagonalTrees;

import static org.graffiti.graphics.GraphicAttributeConstants.GRID_PATH;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.DockingAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.graphics.NodeLabelPositionAttribute;
import org.graffiti.graphics.Port;
import org.graffiti.graphics.PortsAttribute;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.phyloTrees.utility.GravistoUtil;
import org.graffiti.plugins.grids.HexagonalGrid2;

public class UnorderedTernaryTrees extends TreeInHexa2 {

    private int gridWidth;

    private int gridHeight;

    private int maxX;

    private int maxY;

    private int tmpMaxY;

    private LinkedList<TernaryTree> list = new LinkedList<TernaryTree>();

    // private int drawingNumber = 1;

    private ArrayList<Dimension> dimensions = new ArrayList<Dimension>();

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {

    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter vWidth = new IntegerParameter(new Integer(200), 50,
                2000, "vWidth", "Breite von v");
        IntegerParameter vHeight = new IntegerParameter(new Integer(300), 50,
                2000, "vHeight", "H�he von v");
        IntegerParameter wWidth = new IntegerParameter(new Integer(100), 50,
                2000, "wWidth", "Breite von w");
        IntegerParameter wHeight = new IntegerParameter(new Integer(600), 50,
                2000, "wHeight", "Breite von w");
        IntegerParameter xWidth = new IntegerParameter(new Integer(500), 50,
                2000, "xWidth", "Breite von x");
        IntegerParameter xHeight = new IntegerParameter(new Integer(400), 50,
                2000, "xHeight", "Breite von x");
        return new Parameter[] { vWidth, vHeight, wWidth, wHeight, xWidth,
                xHeight };
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {

        maxX = 0;
        maxY = 0;
        tmpMaxY = 0;
        // drawingNumber = 1;
        this.graph.getListenerManager().transactionStarted(this);

        ((GridAttribute) graph.getAttribute(GRID_PATH))
                .setGrid(new HexagonalGrid2());

        gridWidth = (Integer) (graph
                .getAttribute("graphics.grid.parameters.cellWidth")).getValue();

        gridHeight = (Integer) (graph
                .getAttribute("graphics.grid.parameters.cellHeight"))
                .getValue();

        // the given parameters of the user interface (heights and widhts of the
        // simulated subtrees)
        // int vWidth =
        // ((IntegerParameter)parameters[0]).getInteger().intValue();
        // int vHeight =
        // ((IntegerParameter)parameters[1]).getInteger().intValue();
        // int wWidth =
        // ((IntegerParameter)parameters[2]).getInteger().intValue();
        // int wHeight =
        // ((IntegerParameter)parameters[3]).getInteger().intValue();
        // int xWidth =
        // ((IntegerParameter)parameters[4]).getInteger().intValue();
        // int xHeight =
        // ((IntegerParameter)parameters[5]).getInteger().intValue();

        ArrayList<Subtree> listOne = new ArrayList<Subtree>();
        ArrayList<Subtree> listTwo = new ArrayList<Subtree>();
        ArrayList<Subtree> listThree = new ArrayList<Subtree>();

        int rx1 = 200;
        int rx2 = 300;
        int rx3 = 400;
        int rx4 = 800;
        int rx5 = 1000;
        int rx6 = 600;

        int ry1 = 800;
        int ry2 = 600;
        int ry3 = 400;
        int ry4 = 200;
        int ry5 = 100;
        int ry6 = 100;

        // the initial subtrees (root at origin) of listOne
        Subtree v1 = new Subtree(rx1, ry1, new Color(255, 0, 0));
        Subtree w1 = new Subtree(rx2, ry2, new Color(255, 0, 0));
        Subtree x1 = new Subtree(rx3, ry3, new Color(255, 0, 0));
        Subtree y1 = new Subtree(rx4, ry4, new Color(255, 0, 0));
        Subtree z1 = new Subtree(rx5, ry5, new Color(255, 0, 0));
        // Subtree a1 =
        new Subtree(rx6, ry6, new Color(255, 0, 0));

        rx1 = 100;
        rx2 = 300;
        rx3 = 500;
        rx4 = 600;
        rx5 = 900;
        rx6 = 600;

        ry1 = 900;
        ry2 = 600;
        ry3 = 500;
        ry4 = 200;
        ry5 = 100;
        ry6 = 100;

        // the initial subtrees (root at origin) of listTwo
        Subtree v2 = new Subtree(rx1, ry1, new Color(0, 255, 0));
        Subtree w2 = new Subtree(rx2, ry2, new Color(0, 255, 0));
        Subtree x2 = new Subtree(rx3, ry3, new Color(0, 255, 0));
        Subtree y2 = new Subtree(rx4, ry4, new Color(0, 255, 0));
        Subtree z2 = new Subtree(rx5, ry5, new Color(0, 255, 0));
        // Subtree a2 =
        new Subtree(rx6, ry6, new Color(0, 255, 0));
        rx1 = 200;
        rx2 = 400;
        rx3 = 500;
        rx4 = 600;
        rx5 = 800;
        rx6 = 600;

        ry1 = 1000;
        ry2 = 800;
        ry3 = 700;
        ry4 = 300;
        ry5 = 100;
        ry6 = 100;

        // the initial subtrees (root at origin) of listThree
        Subtree v3 = new Subtree(rx1, ry1, new Color(0, 0, 255));
        Subtree w3 = new Subtree(rx2, ry2, new Color(0, 0, 255));
        Subtree x3 = new Subtree(rx3, ry3, new Color(0, 0, 255));
        Subtree y3 = new Subtree(rx4, ry4, new Color(0, 0, 255));
        Subtree z3 = new Subtree(rx5, ry5, new Color(0, 0, 255));
        // Subtree a3 =
        new Subtree(rx6, ry6, new Color(0, 0, 255));

        listOne.add(v1);
        listOne.add(w1);
        listOne.add(x1);
        listOne.add(y1);
        listOne.add(z1);
        // listOne.add(a1);

        listTwo.add(w2);
        listTwo.add(v2);
        listTwo.add(x2);
        listTwo.add(y2);
        listTwo.add(z2);
        // listTwo.add(a2);

        Collections.sort(listTwo);

        listThree.add(x3);
        listThree.add(w3);
        listThree.add(v3);
        listThree.add(y3);
        listThree.add(z3);
        // listThree.add(a3);

        Collections.sort(listThree);

        Collections.sort(listOne);
        Collections.sort(listTwo);
        Collections.sort(listThree);

        System.out.println("nachher listOne  : " + listOne);
        System.out.println("nachher listTwo  : " + listTwo);
        System.out.println("nachher listThree: " + listThree);

        getSecondSubtreeForLists(listOne, listTwo, listThree);

        int n = 0;
        // int i = listOne.size() - 1; // listOne
        // int j = 0; // listTwo
        // int k = 0; // listThree
        //
        // int oldWidth = 1000000;

        // get the thinest(?) composition
        secondSubtreeAtOuterPositionGetThinestComposition(listOne, listTwo,
                listThree);

        System.out.println("#Durchl�ufe: " + n);
        System.out.println("\n\n#: " + list.size());

        System.out.println("Vor dem Sortieren:\n" + list);
        Collections.sort(list);
        System.out.println("\nNach dem Sortieren:\n" + list);

        // rausschmei�en der elemente, die andere dominieren
        int m = 1;
        while (m < list.size()) {
            if (TernaryTree.dominating(list.get(m), list.get(m - 1))) {
                list.remove(m);
                continue;
            } else if (TernaryTree.dominating(list.get(m - 1), list.get(m))) {
                list.remove(m - 1);
                continue;
            }
            // wenn keines der beiden elemente dominiert, dann erh�he
            m++;
        }
        System.out.println("\nNur noch die Elemente, die keine anderen "
                + "dominieren:\n" + list);

        // first combination of the six possible combinations
        // secondSubtreeAtOuterPosition(v1, w1, x1);
        // secondSubtreeAtMiddlePosition(v1, w1, x1);
        // secondSubtreeAtInnerPosition(v1, w1, x1);
        // maxX = 0;
        // secondSubtreeAtOuterPosition(w1, v1, x1);
        // secondSubtreeAtMiddlePosition(w1, v1, x1);
        // secondSubtreeAtInnerPosition(w1, v1, x1);
        // maxX = 0;
        // secondSubtreeAtOuterPosition(w1, x1, v1);
        // secondSubtreeAtMiddlePosition(w1, x1, v1);
        // secondSubtreeAtInnerPosition(w1, x1, v1);
        // maxX = 0;
        // secondSubtreeAtOuterPosition(v1, x1, w1);
        // secondSubtreeAtMiddlePosition(v1, x1, w1);
        // secondSubtreeAtInnerPosition(v1, x1, w1);
        // maxX = 0;
        // secondSubtreeAtOuterPosition(x1, v1, w1);
        // secondSubtreeAtMiddlePosition(x1, v1, w1);
        // secondSubtreeAtInnerPosition(x1, v1, w1);
        // maxX = 0;
        // secondSubtreeAtOuterPosition(x1, w1, v1);
        // secondSubtreeAtMiddlePosition(x1, w1, v1);
        // secondSubtreeAtInnerPosition(x1, w1, v1);
        // maxX = 0;
        ((GridAttribute) graph.getAttribute(GRID_PATH))
                .setGrid(new HexagonalGrid2());

        this.graph.getListenerManager().transactionFinished(this);
    }

    private void paint(TernaryTree t) {
        dimensions.add(t.getDim());

        // create new nodes for drawing the tree with its subtrees
        Node u = graph.addNode();
        Node v = graph.addNode();
        Node w = graph.addNode();
        Node x = graph.addNode();

        // set node informations for a better representation
        setPortLeftUp(u);
        setPortLeftUp(v);
        setPortLeftUp(w);
        setPortLeftUp(x);
        setRectangleShape(v);
        setRectangleShape(w);
        setRectangleShape(x);

        t.setRoot(u);
        setDimension(v, t.getSubtree(0));
        setDimension(w, t.getSubtree(1));
        setDimension(x, t.getSubtree(2));

        // the respective node color for a better representation
        colorNode(v, t.getSubtree(0));
        colorNode(w, t.getSubtree(1));
        colorNode(x, t.getSubtree(2));

        moveTreeToVisiblePosition(t);

        // the midpoints of the respective nodes
        // Point pU = new Point(0, 0);
        Point pV = getSubtreePositionForDrawing(t.getSubtree(0));
        Point pW = getSubtreePositionForDrawing(t.getSubtree(1));
        Point pX = getSubtreePositionForDrawing(t.getSubtree(2));

        // sets the repective node u to position pU

        GravistoUtil.setCoords(v, pV);
        GravistoUtil.setCoords(w, pW);
        GravistoUtil.setCoords(x, pX);

        // the tree directed edges (u,v), (u,w) and (u,x)
        Edge edgeUV = graph.addEdge(u, v, true);
        Edge edgeUW = graph.addEdge(u, w, true);
        Edge edgeUX = graph.addEdge(u, x, true);

        // information about the Ternary Tree with the current composition
        // at the node label of u
        NodeLabelAttribute la = new NodeLabelAttribute("mm", t.toString());
        u.addAttribute(la, "");
        la.setFontSize(45);
        la.setPosition(new NodeLabelPositionAttribute("mm", "0", "0", 0, -3, 0,
                0));

        // set edge informations for a better representation
        setLeftUpDocking(edgeUV);
        setLeftUpDocking(edgeUW);
        setLeftUpDocking(edgeUX);

        normalizePosition(v);
        normalizePosition(w);
        normalizePosition(x);

        // System.out.println(" " + t.getDim().toString() + " Number: "
        // + drawingNumber);
        // drawingNumber++;
    }

    private void setRectangleShape(Node n) {

        NodeGraphicAttribute ngaV = (NodeGraphicAttribute) n
                .getAttribute(GraphicAttributeConstants.GRAPHICS);

        ngaV.setShape("org.graffiti.plugins.views.defaults.RectangleNodeShape");
    }

    private void setDimension(Node n, Subtree sp) {

        NodeGraphicAttribute ngaV = (NodeGraphicAttribute) n
                .getAttribute(GraphicAttributeConstants.GRAPHICS);

        DimensionAttribute da = ngaV.getDimension();
        da.setWidth(sp.getWidth());
        da.setHeight(sp.getHeight());

    }

    private void setPortLeftUp(Node n) {
        PortsAttribute portsAttr = (PortsAttribute) n
                .getAttribute("graphics.ports");
        LinkedList<Port> ports = new LinkedList<Port>();

        ports.add(new Port("left_up", -1, -1));
        portsAttr.setCommonPorts(ports);

    }

    private void setLeftUpDocking(Edge e) {
        DockingAttribute docking = (DockingAttribute) e
                .getAttribute("graphics.docking");
        docking.setTarget("left_up");
    }

    /**
     * This method takes the calculated root position of the subtree and
     * calculates the position on the grid with considerring the node size The
     * position, where the node's position is represented by the center
     */
    private Point getSubtreePositionForDrawing(Subtree subtree) {

        return new Point((int) subtree.getRoot().getX() + subtree.getWidth()
                / 2, (int) subtree.getRoot().getY() + subtree.getHeight() / 2);
    }

    /**
     * Normalizes the position of the subtrees (=nodes) (numerical errors)
     */
    private void normalizePosition(Node v) {
        Point2D coord = GravistoUtil.getCoords(v);

        double x = coord.getX();
        double y = coord.getY();

        x = (Math.round(x / 25)) * 25;
        y = (Math.round(y / 25)) * 25;
        coord.setLocation(x, y);
        GravistoUtil.setCoords(v, coord);
    }

    /**
     * Sets the positions of the subtrees, where the middle subtree is drawn
     * outside.
     */
    private void secondSubtreeAtOuterPosition(Subtree a, Subtree b, Subtree c) {
        int treeWidth = 0;
        int treeHeight = 0;
        // first position - B outside
        a.moveRoot(a.getHeight() + gridWidth, 0);
        c.moveRoot(0, c.getWidth() + gridHeight);
        if (a.getHeight() > c.getWidth()) {
            b.moveRoot(a.getHeight() + gridWidth, a.getHeight() + gridHeight);

            // width and height of the current drawing of the composition of the
            // subtrees
            treeWidth = (int) a.getRoot().getX()
                    + Math.max(a.getWidth(), b.getWidth());
            treeHeight = (int) Math.max(c.getRoot().getY() + c.getHeight(), b
                    .getRoot().getY()
                    + b.getHeight());

            // System.out.println("outer case A_y > C_x");
        } else if (a.getHeight() == c.getWidth()) {
            b.moveRoot(c.getWidth() + gridWidth, c.getWidth() + gridHeight);

            // width and height of the current drawing of the composition of the
            // subtrees
            treeWidth = (int) a.getRoot().getX()
                    + Math.max(a.getWidth(), b.getWidth());
            treeHeight = (int) a.getRoot().getX()
                    + Math.max(b.getHeight(), c.getHeight());
            // System.out.println("outer case A_y = C_x");
        } else {
            b.moveRoot(c.getWidth() + gridWidth, c.getWidth() + gridHeight);

            // width and height of the current drawing of the composition of the
            // subtrees
            treeWidth = (int) Math.max(a.getRoot().getX() + a.getWidth(), b
                    .getRoot().getX()
                    + b.getWidth());
            treeHeight = (int) c.getRoot().getY()
                    + Math.max(c.getHeight(), b.getHeight());
            // System.out.println("outer case A_y < C_x");
        }
        tmpMaxY = Math.max(tmpMaxY, treeHeight);
        TernaryTree t = new TernaryTree(a, b, c);
        t.setDim(treeWidth, treeHeight);
        list.add(t);
        paint(t);
        System.out.print(t);
    }

    // /**
    // * Sets the positions of the subtrees, where the middle subtree is drawn
    // in
    // * the middle position
    // */
    // private void secondSubtreeAtMiddlePosition(Subtree a, Subtree b, Subtree
    // c)
    // {
    //
    // int treeWidth = 0;
    // int treeHeight = 0;
    //
    // if (!(a.getHeight() == c.getWidth()))
    // {
    //
    // // A_y > C_x
    // if (a.getHeight() > c.getWidth())
    // {
    //
    // a.moveRoot(c.getWidth() + b.getWidth() + 2 * gridWidth, 0);
    // b.moveRoot(c.getWidth() + gridWidth, c.getWidth() + gridHeight);
    // c.moveRoot(0, c.getWidth() + gridHeight);
    //
    // treeWidth = (int)a.getRoot().getX() + a.getWidth();
    // treeHeight = (int)Math.max(a.getHeight(), c.getRoot().getX()
    // + Math.max(c.getHeight(), b.getHeight()));
    // // System.out.println("middle case A_y > C_x");
    // }
    // // A_y < C_x
    // else if (a.getHeight() < c.getWidth())
    // {
    //
    // a.moveRoot(a.getHeight() + gridWidth, 0);
    // b.moveRoot(a.getHeight() + gridWidth, a.getHeight()
    // + gridHeight);
    // c.moveRoot(0, a.getHeight() + b.getHeight() + 2 * gridHeight);
    //
    // treeWidth = (int)Math.max(c.getWidth(), a.getRoot().getX()
    // + Math.max(a.getWidth(), b.getWidth()));
    // treeHeight = (int)c.getRoot().getY() + c.getHeight();
    // // System.out.println("middle case A_y < C_x");
    // }
    // tmpMaxY = Math.max(tmpMaxY, treeHeight);
    // TernaryTree t = new TernaryTree(a, b, c);
    // t.setDim(treeWidth, treeHeight);
    // paint(t);
    // System.out.print(t);
    //
    // }
    // else
    // {
    // // System.out.println("middle case A_y == C_x (nothing to do)");
    // // nothing to do, becaus this case is considerred yet in the outer
    // // case for b
    // }
    // }

    // /**
    // * Sets the positions of the subtrees, where the middle subtree is drawn
    // in
    // * the most inner position (coordinates of root of A: (1,1)
    // */
    // private void secondSubtreeAtInnerPosition(Subtree a, Subtree b, Subtree
    // c)
    // {
    // int treeWidth1 = 0;
    // int treeHeight1 = 0;
    // int treeWidth2 = 0;
    // int treeHeight2 = 0;
    //
    // // in this case there are two possibilities (horizontal vs vertical
    // // compaction) ==> 2 x paint(...)
    // if ((a.getHeight() > b.getHeight() + gridHeight)
    // && (c.getWidth() > b.getWidth() + gridWidth))
    // {
    //
    // // first possible compostition (horizontal compacted)
    // a.moveRoot(b.getWidth() + 2 * gridWidth, 0);
    // b.moveRoot(gridWidth, gridHeight);
    // c.moveRoot(0, a.getHeight() + gridHeight);
    //
    // treeWidth1 = (int)Math.max(a.getRoot().getX() + a.getWidth(), c
    // .getWidth());
    // treeHeight1 = (int)c.getRoot().getY() + c.getHeight();
    //
    // TernaryTree t1 = new TernaryTree(a, b, c);
    // t1.setDim(treeWidth1, treeHeight1);
    // paint(t1);
    // System.out.print(t1 + "\t");
    // // System.out
    // // .println("inner case A_y > B_y + 1 && C_x > B_y + 1 --> case 1");
    // // second possible composition (vertical compacted)
    // b.moveRoot(gridWidth, gridHeight);
    // a.moveRoot(c.getWidth() + gridWidth, 0);
    // c.moveRoot(0, b.getHeight() + 2 * gridHeight);
    //
    // treeWidth2 = (int)a.getRoot().getX() + a.getWidth();
    // treeHeight2 = (int)Math.max(a.getHeight(), c.getRoot().getY()
    // + c.getHeight());
    // tmpMaxY = Math.max(tmpMaxY, Math.max(treeHeight1, treeHeight2));
    // TernaryTree t2 = new TernaryTree(a, b, c);
    // t2.setDim(treeWidth2, treeHeight2);
    // paint(t2);
    // System.out.println(t2);
    //
    // // System.out
    // // .println("inner case A_y > B_y + 1 && C_x > B_y + 1 --> case 2");
    // }
    // // the subtrees A and B don't reach each other
    // else
    // {
    // b.moveRoot(gridWidth, gridHeight);
    // a.moveRoot(b.getWidth() + 2 * gridWidth, 0);
    // c.moveRoot(0, b.getHeight() + 2 * gridHeight);
    //
    // treeWidth1 = (int)Math.max(a.getRoot().getX() + a.getWidth(), c
    // .getWidth());
    // treeHeight1 = (int)Math.max(c.getRoot().getY() + c.getHeight(), a
    // .getHeight());
    //
    // tmpMaxY = Math.max(tmpMaxY, treeHeight1);
    // TernaryTree t = new TernaryTree(a, b, c);
    // t.setDim(treeWidth1, treeHeight1);
    // paint(t);
    // System.out.println(t);
    // // System.out
    // // .println("inner case not(A_y > B_y + 1 && C_x > B_y + 1) "
    // // + "(subtrees do not reach each other)");
    // }
    // maxY += tmpMaxY + 2 * gridHeight;
    // tmpMaxY = 0;
    //
    // }

    private void colorNode(Node u, Subtree sp) {

        NodeGraphicAttribute nga = (NodeGraphicAttribute) u
                .getAttribute("graphics");
        ColorAttribute ca = nga.getFillcolor();
        ca.setColor(sp.getColor());
    }

    // private int getMaxY()
    // {
    // int max = 0;
    // Iterator<Node> it = graph.getNodesIterator();
    // while (it.hasNext())
    // {
    // Node n = it.next();
    //
    // Point2D nPoint = GravistoUtil.getCoords(n);
    //
    // NodeGraphicAttribute nga = (NodeGraphicAttribute)n
    // .getAttribute(GraphicAttributeConstants.GRAPHICS);
    //
    // DimensionAttribute da = nga.getDimension();
    //
    // max = Math.max(max, (int)nPoint.getY() + (int)da.getHeight());
    //
    // }
    //
    // return ((max / 50) * 50 + 50);
    // }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Unordered Ternary Trees (Prototype)";
    }

    public void moveTreeToVisiblePosition(TernaryTree t) {

        Point p = new Point(maxX, maxY);
        GravistoUtil.setCoords(t.getRoot(), p);

        for (int i = 0; i <= 2; i++) {

            t.getSubtree(i).getRoot().setLocation(
                    t.getSubtree(i).getRoot().getX() + maxX,
                    t.getSubtree(i).getRoot().getY() + maxY);
        }
        maxX += t.getDim().getWidth() + 2 * gridWidth;
    }

    public int getRandomNumber(int i, int j) {

        return (i + (int) (Math.random() * (j - i))) * 50;
    }

    private void getSecondSubtreeForLists(ArrayList<Subtree> listOne,
            ArrayList<Subtree> listTwo, ArrayList<Subtree> listThree) {

        for (int i = listOne.size() - 1; i >= 0; i--) {
            for (int j = 0; j < listTwo.size(); j++) {
                for (int k = 0; k < listThree.size(); k++) {
                    secondSubtreeAtOuterPosition(listOne.get(i),
                            listTwo.get(k), listThree.get(j));
                }
                maxX = 0;
                maxY += tmpMaxY + 2 * gridHeight;
            }

        }

    }

    private void secondSubtreeAtOuterPositionGetThinestComposition(
            ArrayList<Subtree> listOne, ArrayList<Subtree> listTwo,
            ArrayList<Subtree> listThree) {

        // begin at the beginnings of the lists, because of the ascending
        // ordering of the lists with respect to the width
        // ==> the thinest subtrees are at the first position

        // Subtree a = listOne.get(0);
        // Subtree b = listTwo.get(0);
        // Subtree c = listThree.get(0);

    }
}
