// =============================================================================
//
//   ImprovedFPP.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * @author hofmeier
 * @version $Revision$ $Date$
 */
public class ImprovedFPP extends BrehmOneRealizer {

    private int maxRealizer;
    // private HashList<Node> greenSaveNodes = new HashList<Node>();
    // private HashList<Node> blueSaveNodes = new HashList<Node>();
    // private HashList<Node> contour = new HashList<Node>();
    private Realizer realizer;
    private BarycentricRepresentation br;
    // private int[] bestRealizer = new int[3];
    private HashMap<Node, LinkedList<Node>> uSets = new HashMap<Node, LinkedList<Node>>();

    /**
     * @param g
     * @param m
     */
    public ImprovedFPP(Graph g, int m) {
        super(g, m);
        this.maxRealizer = m;
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            LinkedList<Node> uList = new LinkedList<Node>();
            uList.add(n);
            this.uSets.put(n, uList);
        }
    }

    /**
     * Executes the algorithm by calculating the realizer from the leftmost
     * canonical ordering and creating an FFP-like drawing from one of the three
     * outer vertices (the "best one" is chosen from the properties of the
     * realizer).
     */
    @Override
    public void execute() {
        Iterator<Face> facesIt = this.getFaces().iterator();
        int graphInDB = -1;
        while (facesIt.hasNext()) {
            Face outerFace = facesIt.next();
            Node outerNode1 = outerFace.nodes[0];
            Node outerNode2 = outerFace.nodes[1];
            Node outerNode3 = outerFace.nodes[2];

            ImprovedFPPDrawing ifd1 = new ImprovedFPPDrawing(outerFace,
                    outerNode1, outerNode2, outerNode3, this.graph,
                    this.maxRealizer, graphInDB);
            graphInDB = 1;
            ifd1.writeToDB();
            ImprovedFPPDrawing ifd2 = new ImprovedFPPDrawing(outerFace,
                    outerNode3, outerNode1, outerNode2, this.graph,
                    this.maxRealizer, graphInDB);
            ifd2.writeToDB();
            ImprovedFPPDrawing ifd3 = new ImprovedFPPDrawing(outerFace,
                    outerNode2, outerNode3, outerNode1, this.graph,
                    this.maxRealizer, graphInDB);
            ifd3.writeToDB();
            this.realizer = ifd3.getRealizer();
            this.realizers.add(realizer);
            this.br = ifd3.getBr();
            this.barycentricReps.add(this.br);
        }
    }

    // private Realizer getBestRealizer() {
    // for (int i=0; i<3; i++) {
    // Realizer realizer = this.createRealizer();
    // this.bestRealizer[i] = this.getNumberOfInnerNodes(realizer);
    // Node temp = this.outerNodes[0];
    // this.outerNodes[0]=this.outerNodes[1];
    // this.outerNodes[1]=this.outerNodes[2];
    // this.outerNodes[2]=temp;
    // this.reset();
    // }
    // int j=0;
    // if ((this.bestRealizer[0] <= this.bestRealizer[1]) &&
    // (this.bestRealizer[0] <= this.bestRealizer[2])) {
    // j=0;
    // }
    // else if ((this.bestRealizer[1] <= this.bestRealizer[0]) &&
    // (this.bestRealizer[1] <= this.bestRealizer[2])) {
    // j=1;
    // }
    // else if ((this.bestRealizer[2] <= this.bestRealizer[1]) &&
    // (this.bestRealizer[2] <= this.bestRealizer[0])) {
    // j=2;
    // }
    // for (int i=0; i<j; i++) {
    // Node temp = this.outerNodes[0];
    // this.outerNodes[0]=this.outerNodes[1];
    // this.outerNodes[1]=this.outerNodes[2];
    // this.outerNodes[2]=temp;
    // }
    // Realizer realizer = this.createRealizer();
    // this.outputCanonicalOrder(this.canonicalOrder);
    // return realizer;
    // }

    // private int getNumberOfInnerNodes(Realizer realizer) {
    // boolean[] isInner = new boolean[this.graph.getNumberOfNodes()];
    // Iterator<Node> greenTreeIt = realizer.getGreen().keySet().iterator();
    // while (greenTreeIt.hasNext()) {
    // isInner[this.getIndex(realizer.getGreen().get(greenTreeIt.next()))] =
    // true;
    // }
    // int numberOfInnerNodes = 0;
    // for (int i=0; i<isInner.length; i++) {
    // if (isInner[i]) {
    // numberOfInnerNodes++;
    // }
    // }
    // return numberOfInnerNodes;
    // }

    // private void outputCanonicalOrder(LinkedList<Node> order) {
    // Iterator<Node> orderIt = order.iterator();
    // while (orderIt.hasNext()) {
    // Node node = orderIt.next();
    // NodeLabelAttribute label = new NodeLabelAttribute("label");
    // // Check if there is an existing label
    // try
    // {
    // label = (NodeLabelAttribute)node.getAttributes()
    // .getAttribute("label");
    //
    // System.out.print(label.getLabel()+ " ");
    // }
    // catch (AttributeNotFoundException e)
    // {
    // System.out.println("No node labels. Can't print the canonical order");
    // return;
    // }
    // }
    // System.out.println();
    // }

    // ONLY FOR DEBUGGING
    // private void outputOuterNodes() {
    // for (int i=0; i<3; i++) {
    // Node node = this.outerNodes[i];
    // NodeLabelAttribute label = new NodeLabelAttribute("label");
    // // Check if there is an existing label
    // try
    // {
    // label = (NodeLabelAttribute)node.getAttributes()
    // .getAttribute("label");
    //
    // System.out.println(i + ": " + label.getLabel()+ " ");
    // }
    // catch (AttributeNotFoundException e)
    // {
    // System.out.println("No node labels. Can't print the outer nodes.");
    // return;
    // }
    // }
    // }

    // private void outputCoordinates() {
    // Iterator<Node> nodesIt = this.graph.getNodesIterator();
    // while (nodesIt.hasNext()) {
    // Node toPrint = nodesIt.next();
    // NodeLabelAttribute label = new NodeLabelAttribute("label");
    // // Check if there is an existing label
    // try
    // {
    // label = (NodeLabelAttribute)toPrint.getAttributes()
    // .getAttribute("label");
    //
    // System.out.println(label.getLabel()+ ": " + this.getX(toPrint)
    // + ", " + this.getY(toPrint));
    // }
    // catch (AttributeNotFoundException e)
    // {
    // System.out.println("No node labels. Can't print the outer nodes.");
    // return;
    // }
    // }
    //        
    // }

    // private void reset() {
    // this.canonicalOrder = new LinkedList<Node>();
    // this.neighborCounter = new int[this.graph.getNodes().size()];
    // this.neighbors = new HashList<Node>();
    // this.finishedNodes = new HashSet<Node>();
    // }
    //    
    // private void fpp() {
    // Iterator<Node> nodesIt = this.graph.getNodesIterator();
    // while (nodesIt.hasNext()) {
    // Node n = nodesIt.next();
    // this.setX(n, 0);
    // this.setY(n, 0);
    // }
    // this.setX(this.outerNodes[1], 2);
    // this.setY(this.outerNodes[1], 0);
    // this.setX(this.outerNodes[0], 0);
    // this.setY(this.outerNodes[0], 0);
    // this.setX(this.canonicalOrder.getLast(), 1);
    // this.setY(this.canonicalOrder.getLast(), 1);
    // this.contour.append(this.outerNodes[0]);
    // this.contour.append(this.canonicalOrder.getLast());
    // this.contour.append(this.outerNodes[1]);
    //        
    //        
    // for (int i=this.canonicalOrder.size()-2; i>=0; i--) {
    // Node toDraw = this.canonicalOrder.get(i);
    // Node leftmost = this.outerNodes[0];
    // Node rightmost = this.outerNodes[1];
    // if (!toDraw.equals(this.outerNodes[2])) {
    // leftmost = this.getLeftmostNeighbor(toDraw);
    // rightmost = this.getRightmostNeighbor(toDraw);
    // }
    // LinkedList<Node> onContour = this.getRedSons(toDraw);
    // this.updateUSet(toDraw, onContour);
    // if (!onContour.isEmpty()) {
    // this.shift(this.contour.getNextNeighbor(leftmost));
    // this.shift(rightmost);
    // this.updateContour(toDraw, leftmost, onContour);
    // this.setNewCoordinatesInner(toDraw, leftmost, rightmost);
    // } else {
    // if (this.greenSaveNodes.contains(leftmost)) {
    // this.setNewCoordinatesLeafGreenSave(toDraw, leftmost, rightmost);
    // }
    // else if (this.blueSaveNodes.contains(rightmost)) {
    // this.setNewCoordinatesLeafBlueSave(toDraw, leftmost, rightmost);
    // }
    // else {
    // this.shift(rightmost);
    // this.updateContour(toDraw, leftmost, onContour);
    // this.setNewCoordinatesLeafNoSave(toDraw, leftmost, rightmost);
    // }
    // }
    // }
    // this.outputCoordinates();
    // }

    // private Node getLeftmostNeighbor(Node n) {
    // return this.realizer.getGreen().get(n);
    // }
    //    
    // private Node getRightmostNeighbor(Node n) {
    // return this.realizer.getBlue().get(n);
    // }

    // private void setX(Node n, int x) {
    // this.br.setCoordinate(n, 1, x);
    // }
    //    
    // private void setY(Node n, int y) {
    // this.br.setCoordinate(n, 2, y);
    // }

    // private int getX(Node n) {
    // return this.br.getCoordinate(n, 1);
    // }
    //    
    // private int getY(Node n) {
    // return this.br.getCoordinate(n, 2);
    // }

    // private LinkedList<Node> getRedSons(Node n) {
    // LinkedList<Node> sons = new LinkedList<Node>();
    // Iterator<Node> redTreeIt = this.realizer.getRed().keySet().iterator();
    // while(redTreeIt.hasNext()) {
    // Node son = redTreeIt.next();
    // if(this.realizer.getRed().get(son).equals(n)) {
    // sons.add(son);
    // }
    // }
    // return sons;
    // }
    //    
    // private void updateUSet(Node n, LinkedList<Node> sons) {
    // LinkedList<Node> uSet = this.uSets.get(n);
    // Iterator<Node> sonsIt = sons.iterator();
    // while (sonsIt.hasNext()) {
    // Node son = sonsIt.next();
    // LinkedList<Node> sonsUSet = this.uSets.get(son);
    // Iterator<Node> sonsUSetIt = sonsUSet.iterator();
    // while (sonsUSetIt.hasNext()) {
    // uSet.add(sonsUSetIt.next());
    // }
    // }
    // }

    // private void updateContour(Node toDraw, Node leftmost, LinkedList<Node>
    // onContour) {
    // Iterator<Node> onContourIt = onContour.iterator();
    // while (onContourIt.hasNext()) {
    // this.contour.remove(onContourIt.next());
    // }
    // this.contour.addAfter(leftmost, toDraw);
    // }

    // private void shift(Node toShift) {
    // Node current = toShift;
    // while (current!=this.outerNodes[0]) {
    // Iterator<Node> uSetIt = this.uSets.get(current).iterator();
    // while (uSetIt.hasNext()) {
    // Node shiftNode = uSetIt.next();
    // this.setX(shiftNode, this.getX(shiftNode)+1);
    // }
    // current = this.contour.getNextNeighbor(current);
    // }
    //        
    // }

    // private void setNewCoordinatesInner(Node toDraw, Node leftmost, Node
    // rightmost) {
    // double x1 = this.getX(leftmost);
    // double y1 = this.getY(leftmost);
    // double x2 = this.getX(rightmost);
    // double y2 = this.getY(rightmost);
    // double newx = 0;
    // if (this.greenSaveNodes.contains(leftmost)) {
    // this.greenSaveNodes.remove(leftmost);
    // }
    // if (this.blueSaveNodes.contains(rightmost)) {
    // this.blueSaveNodes.remove(rightmost);
    // }
    // if (y1<=y2) {
    // newx = x1 + (x2-x1+Math.abs(y2-y1))/2;
    // }
    // else {
    // newx = x1 + (x2-x1-Math.abs(y2-y1))/2;
    // }
    // double newy = Math.min(y1, y2) + (x2-x1+Math.abs(y2-y1))/2;
    // if (Math.floor(newx)!=Math.ceil(newx)) {
    // this.shift(rightmost);
    // System.out.println("UNGERADE!");
    // this.setNewCoordinatesInner(toDraw, leftmost, rightmost);
    // }
    // this.setX(toDraw, (int)newx);
    // this.setY(toDraw, (int)newy);
    //        
    // }
    //    
    // private void setNewCoordinatesLeafNoSave(Node toDraw, Node leftmost, Node
    // rightmost) {
    //               
    // double x1 = this.getX(leftmost);
    // double y1 = this.getY(leftmost);
    // double x2 = this.getX(rightmost);
    // double y2 = this.getY(rightmost);
    //        
    // double newx = 0;
    // double newy = 0;
    //        
    // if (Math.abs(y1-y2)>1) {
    // if (y1<y2) {
    // newy = y1+1;
    // newx = x1+1;
    // this.greenSaveNodes.append(toDraw);
    // System.out.println("GREEN SAVE NODE");
    // }
    // else {
    // newy = y2+1;
    // newx = x2-1;
    // this.blueSaveNodes.append(toDraw);
    // System.out.println("BLUE SAVE NODE");
    // }
    // }
    // else {
    // if (y1<y2) {
    // newy = y2;
    // newx = x2-1;
    // }
    // else if (y1>y2) {
    // newy = y1;
    // newx = x1+1;
    // }
    // else {
    // newx = (x1+x2)/2;
    // newy = y1 + Math.abs(x1-x2)/2;
    // }
    // }
    // this.setX(toDraw, (int) newx);
    // this.setY(toDraw, (int) newy);
    // }
    //    
    // private void setNewCoordinatesLeafGreenSave(Node toDraw, Node leftmost,
    // Node rightmost) {
    //        
    // double x1 = this.getX(leftmost);
    // double y1 = this.getY(leftmost);
    // double y2 = this.getY(rightmost);
    //        
    // double newx = x1+1;
    // double newy = y1+1;
    // this.greenSaveNodes.remove(leftmost);
    // if (y2-newy>1) {
    // this.greenSaveNodes.append(toDraw);
    // }
    // this.setX(toDraw, (int) newx);
    // this.setY(toDraw, (int) newy);
    // }
    //    
    // private void setNewCoordinatesLeafBlueSave(Node toDraw, Node leftmost,
    // Node rightmost) {
    //        
    // double y1 = this.getY(leftmost);
    // double x2 = this.getX(rightmost);
    // double y2 = this.getY(rightmost);
    //        
    // double newx = x2-1;
    // double newy = y2+1;
    // this.blueSaveNodes.remove(rightmost);
    // if (y1-newy>1) {
    // this.blueSaveNodes.append(toDraw);
    // }
    // this.setX(toDraw, (int) newx);
    // this.setY(toDraw, (int) newy);
    // }

    // Tabellen erstellen:
    // -------------------
    // CREATE TABLE graphs (graphid INTEGER Primary Key,
    // numOfNodes INTEGER not null,
    // numOfRealizers INTEGER not null,
    // filename VARCHAR(60) not null)
    //    
    // CREATE TABLE drawings (graphid INTEGER not null,
    // realizerID INTEGER not null,
    // outerFace VARCHAR(17) not null,
    // basis1 VARCHAR(5) not null,
    // basis2 VARCHAR(5) not null,
    // spitze VARCHAR(5) not null,
    // numberOfInnerNodes INTEGER not null,
    // numberOfLeafs INTEGER not null,
    // oddDistances INTEGER not null,
    // badOddDistances INTEGER not null,
    // width INTEGER not null,
    // height INTEGER not null,
    // FOREIGN KEY(graphid) REFERENCES graphs(graphid))

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
