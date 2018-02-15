// =============================================================================
//
//   ImprovedFPPDrawing.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.NodeLabelAttribute;

/**
 * @author hofmeier
 * @version $Revision$ $Date$
 */
public class ImprovedFPPDrawing extends ImprovedFPP {

    private int graphID;

    // private Face outerFace;

    private String outerFaceString;

    private Node basis1;

    private Node basis2;

    private Node spitze;

    private int numOfNodes;

    private Graph graph;

    private int numOfInnerNodes;

    private int numOfLeafs;

    private int width;

    private int height;

    private int oddDistances = 0;

    private int badOddDistances = 0;

    private HashList<Node> contour = new HashList<Node>();

    private Realizer realizer;

    private BarycentricRepresentation br;

    private HashMap<Node, LinkedList<Node>> uSets = new HashMap<Node, LinkedList<Node>>();

    public static final String dbDriver = "org.postgresql.Driver";

    public static final String dbHost = "snickers.fmi.uni-passau.de";

    public static final String dbName = "hofmeier";

    public static final String dbUser = "hofmeier";

    public static final String dbPassword = "";

    public ImprovedFPPDrawing(Face oF, Node b1, Node b2, Node s, Graph g,
            int m, int gId) {
        super(g, m);
        this.graphID = gId;
        this.outerNodes[0] = b1;
        this.outerNodes[1] = b2;
        this.outerNodes[2] = s;
        // this.outerFace = oF;
        this.basis1 = b1;
        this.basis2 = b2;
        this.spitze = s;
        this.outerFaceString = this.getOuterFaceString();

        this.graph = g;
        this.numOfNodes = this.graph.getNumberOfNodes();
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            LinkedList<Node> uList = new LinkedList<Node>();
            uList.add(n);
            this.uSets.put(n, uList);
        }
        this.realizer = this.createRealizer();
        this.numOfInnerNodes = this.getNumberOfInnerNodes(this.realizer);
        this.numOfLeafs = this.getNumberOfLeafs(this.realizer);
        this.br = new BarycentricRepresentation(realizer, this.graph,
                this.outerNodes);
        this.fpp();
        this.width = this.getX(basis2);
        this.height = this.getY(spitze);
    }

    private int fpp() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            this.setX(n, 0);
            this.setY(n, 0);
        }
        this.setX(basis2, 2);
        this.setY(basis2, 0);
        this.setX(basis1, 0);
        this.setY(basis1, 0);
        this.setX(this.canonicalOrder.getLast(), 1);
        this.setY(this.canonicalOrder.getLast(), 1);
        this.contour.append(basis1);
        this.contour.append(this.canonicalOrder.getLast());
        this.contour.append(basis2);

        for (int i = this.canonicalOrder.size() - 2; i >= 0; i--) {
            Node toDraw = this.canonicalOrder.get(i);
            Node leftmost = basis1;
            Node rightmost = basis2;
            if (!toDraw.equals(spitze)) {
                leftmost = this.getLeftmostNeighbor(toDraw);
                rightmost = this.getRightmostNeighbor(toDraw);
            }
            LinkedList<Node> onContour = this.getRedSons(toDraw);
            this.updateUSet(toDraw, onContour);
            if (!onContour.isEmpty()) {
                this.shift(this.contour.getNextNeighbor(leftmost));
                this.shift(rightmost);
                this.updateContour(toDraw, leftmost, onContour);
                this.setNewCoordinatesInner(toDraw, leftmost, rightmost);
            } else {
                this.shift(rightmost);
                this.updateContour(toDraw, leftmost, onContour);
                this.setNewCoordinatesLeaf(toDraw, leftmost, rightmost);
            }
        }
        this.outputCoordinates();
        return this.width;
    }

    private void setNewCoordinatesLeaf(Node toDraw, Node leftmost,
            Node rightmost) {

        double x1 = this.getX(leftmost);
        double y1 = this.getY(leftmost);
        double x2 = this.getX(rightmost);
        double y2 = this.getY(rightmost);

        double newx = 0;
        double newy = 0;

        if (y1 < y2) {
            newy = y2;
            newx = x2 - 1;
        } else if (y1 > y2) {
            newy = y1;
            newx = x1 + 1;
        } else {
            newx = (x1 + x2) / 2;
            newy = y1 + Math.abs(x1 - x2) / 2;
        }
        this.setX(toDraw, (int) newx);
        this.setY(toDraw, (int) newy);
    }

    private void setNewCoordinatesInner(Node toDraw, Node leftmost,
            Node rightmost) {
        double x1 = this.getX(leftmost);
        double y1 = this.getY(leftmost);
        double x2 = this.getX(rightmost);
        double y2 = this.getY(rightmost);
        double newx = 0;

        if (y1 <= y2) {
            newx = x1 + (x2 - x1 + Math.abs(y2 - y1)) / 2;
        } else {
            newx = x1 + (x2 - x1 - Math.abs(y2 - y1)) / 2;
        }
        double newy = Math.min(y1, y2) + (x2 - x1 + Math.abs(y2 - y1)) / 2;
        if (Math.floor(newx) != Math.ceil(newx)) {
            this.isBadOddDistance(leftmost, rightmost);
            this.shift(rightmost);
            this.oddDistances++;
            this.setNewCoordinatesInner(toDraw, leftmost, rightmost);
        }
        this.setX(toDraw, (int) newx);
        this.setY(toDraw, (int) newy);
    }

    private void setX(Node n, int x) {
        this.br.setCoordinate(n, 1, x);
    }

    private void setY(Node n, int y) {
        this.br.setCoordinate(n, 2, y);
    }

    private int getX(Node n) {
        return this.br.getCoordinate(n, 1);
    }

    private int getY(Node n) {
        return this.br.getCoordinate(n, 2);
    }

    private Node getLeftmostNeighbor(Node n) {
        return this.realizer.getGreen().get(n);
    }

    private Node getRightmostNeighbor(Node n) {
        return this.realizer.getBlue().get(n);
    }

    private void updateContour(Node toDraw, Node leftmost,
            LinkedList<Node> onContour) {
        Iterator<Node> onContourIt = onContour.iterator();
        while (onContourIt.hasNext()) {
            this.contour.remove(onContourIt.next());
        }
        this.contour.addAfter(leftmost, toDraw);
    }

    private void shift(Node toShift) {
        Node current = toShift;
        while (current != this.outerNodes[0]) {
            Iterator<Node> uSetIt = this.uSets.get(current).iterator();
            while (uSetIt.hasNext()) {
                Node shiftNode = uSetIt.next();
                this.setX(shiftNode, this.getX(shiftNode) + 1);
            }
            current = this.contour.getNextNeighbor(current);
        }

    }

    private LinkedList<Node> getRedSons(Node n) {
        LinkedList<Node> sons = new LinkedList<Node>();
        Iterator<Node> redTreeIt = this.realizer.getRed().keySet().iterator();
        while (redTreeIt.hasNext()) {
            Node son = redTreeIt.next();
            if (this.realizer.getRed().get(son).equals(n)) {
                sons.add(son);
            }
        }
        return sons;
    }

    private void updateUSet(Node n, LinkedList<Node> sons) {
        LinkedList<Node> uSet = this.uSets.get(n);
        Iterator<Node> sonsIt = sons.iterator();
        while (sonsIt.hasNext()) {
            Node son = sonsIt.next();
            LinkedList<Node> sonsUSet = this.uSets.get(son);
            Iterator<Node> sonsUSetIt = sonsUSet.iterator();
            while (sonsUSetIt.hasNext()) {
                uSet.add(sonsUSetIt.next());
            }
        }
    }

    private int getNumberOfInnerNodes(Realizer realizer) {
        boolean[] isInner = new boolean[this.graph.getNumberOfNodes()];
        Iterator<Node> greenTreeIt = realizer.getGreen().keySet().iterator();
        while (greenTreeIt.hasNext()) {
            isInner[this.getIndex(realizer.getGreen().get(greenTreeIt.next()))] = true;
        }
        int numberOfInnerNodes = 0;
        for (int i = 0; i < isInner.length; i++) {
            if (isInner[i]) {
                numberOfInnerNodes++;
            }
        }
        return numberOfInnerNodes;
    }

    private int getNumberOfLeafs(Realizer realizer) {
        boolean[] isInner = new boolean[this.graph.getNumberOfNodes()];
        Iterator<Node> greenTreeIt = realizer.getGreen().keySet().iterator();
        while (greenTreeIt.hasNext()) {
            isInner[this.getIndex(realizer.getGreen().get(greenTreeIt.next()))] = true;
        }
        int numberOfLeafs = 0;
        for (int i = 0; i < isInner.length; i++) {
            if (!isInner[i]) {
                numberOfLeafs++;
            }
        }
        return numberOfLeafs;
    }

    public void writeToDB() {
        Connection conn = null;
        // Load the database driver.
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found");
            return;
        }

        // Open a database connection.
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://" + dbHost
                    + "/" + dbName, dbUser, dbPassword);
            int primaryKey = this.getNextPrimaryKey(conn);
            if (this.graphID > -1) {
                primaryKey--;
            }

            this.write(conn, this.graphID, primaryKey);

        } catch (SQLException e) {
            System.err.println("Fehler beim Verbindungsaufbau");
            e.printStackTrace();
        }

        // Close the database connection in every case.
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Fehler beim Schlie�en der Connection");
            }
        }
    }

    private int getNextPrimaryKey(Connection conn) throws SQLException {
        int primaryKey = -1;
        Statement stmt = conn.createStatement();
        ResultSet rst = stmt.executeQuery("SELECT Max(graphid) FROM graphs");

        while (rst.next()) {
            primaryKey = rst.getInt(1);
        }
        stmt.close();
        return ++primaryKey;
    }

    private void write(Connection conn, int graphID, int key)
            throws SQLException {
        Statement stmt = conn.createStatement();
        try {

            if (graphID < 0) {
                stmt.executeUpdate("INSERT INTO graphs (graphid, numOfNodes, "
                        + "numOfRealizers, filename) VALUES (" + key + ","
                        + this.numOfNodes + ", " + -1 + "," + key + ");");
                stmt.close();
            }

            stmt = conn.createStatement();
            stmt
                    .executeUpdate("INSERT INTO drawings (graphid, realizerid, outerface, "
                            + "basis1, basis2, spitze, numberofinnernodes, numberofleafs, "
                            + "odddistances, badodddistances, width, height) "
                            + "VALUES ("
                            + key
                            + ", "
                            + (-1)
                            + ", '"
                            + this.outerFaceString
                            + "', "
                            + this.getLabel(this.basis1)
                            + ", "
                            + this.getLabel(this.basis2)
                            + ", "
                            + this.getLabel(this.spitze)
                            + ", "
                            + this.numOfInnerNodes
                            + ", "
                            + this.numOfLeafs
                            + ", "
                            + this.oddDistances
                            + ", "
                            + this.badOddDistances
                            + ", "
                            + this.width
                            + ", "
                            + this.height + ");");

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            stmt.close();
        }
    }

    private String getLabel(Node node) {
        NodeLabelAttribute label = new NodeLabelAttribute("label");
        // Check if there is an existing label
        try {
            label = (NodeLabelAttribute) node.getAttributes().getAttribute(
                    "label");
            return label.getLabel();
        } catch (AttributeNotFoundException e) {
            System.out.println("Error! No node labels.");
            return "";
        }
    }

    private String getOuterFaceString() {
        String ofs = "";
        int label1 = Integer.parseInt(this.getLabel(this.basis1));
        int label2 = Integer.parseInt(this.getLabel(this.basis2));
        int spitze = Integer.parseInt(this.getLabel(this.spitze));
        if (label1 < label2) {
            if (label2 < spitze) {
                ofs += label1 + "_" + label2 + "_" + spitze;
            } else if (label1 < spitze) {
                ofs += label1 + "_" + spitze + "_" + label2;
            } else {
                ofs += spitze + "_" + label1 + "_" + label2;
            }
        } else {
            if (label1 < spitze) {
                ofs += label2 + "_" + label1 + "_" + spitze;
            } else if (label2 < spitze) {
                ofs += label2 + "_" + spitze + "_" + label1;
            } else {
                ofs += spitze + "_" + label2 + "_" + label1;
            }
        }
        return ofs;
    }

    private void outputCoordinates() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node toPrint = nodesIt.next();
            NodeLabelAttribute label = new NodeLabelAttribute("label");
            // Check if there is an existing label
            try {
                label = (NodeLabelAttribute) toPrint.getAttributes()
                        .getAttribute("label");

                System.out.println(label.getLabel() + ": " + this.getX(toPrint)
                        + ", " + this.getY(toPrint));
            } catch (AttributeNotFoundException e) {
                System.out
                        .println("No node labels. Can't print the outer nodes.");
                return;
            }
        }
    }

    public Realizer getRealizer() {
        return realizer;
    }

    public BarycentricRepresentation getBr() {
        return br;
    }

    private void isBadOddDistance(Node leftmost, Node rightmost) {
        Node leftFstNeighbor = this.contour.getNextNeighbor(leftmost);
        Node leftSndNeighbor = this.contour.getNextNeighbor(leftFstNeighbor);
        Node rightFstNeighbor = this.contour.getPredecessor(rightmost);
        Node rightSndNeighbor = this.contour.getPredecessor(rightFstNeighbor);
        boolean onDia1 = this.isOnDia(leftmost, leftFstNeighbor);
        boolean onDia2 = this.isOnDia(leftFstNeighbor, leftSndNeighbor);
        boolean onDia3 = this.isOnDia(rightmost, rightFstNeighbor);
        boolean onDia4 = this.isOnDia(rightSndNeighbor, rightFstNeighbor);

        if (onDia1 && onDia2 && onDia3 && onDia4) {
            this.badOddDistances++;
        }
    }

    private boolean isOnDia(Node one, Node two) {
        int x1 = this.getX(one);
        int y1 = this.getY(one);
        int x2 = this.getX(two);
        int y2 = this.getY(two);
        return ((x1 - x2) == (y1 - y2));
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
