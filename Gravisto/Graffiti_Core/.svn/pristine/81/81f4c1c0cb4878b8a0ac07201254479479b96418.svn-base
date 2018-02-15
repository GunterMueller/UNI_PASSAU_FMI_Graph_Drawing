package quoggles;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JPanel;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.util.GeneralUtils;

import quoggles.auxiliary.BoxBooleanIndex;
import quoggles.auxiliary.ConnectedSign;
import quoggles.auxiliary.PointComparator;
import quoggles.auxiliary.Util;
import quoggles.boxes.IBox;
import quoggles.boxes.IOutputBox;
import quoggles.constants.QConstants;
import quoggles.representation.IBoxRepresentation;
import quoggles.stdboxes.input.Input_Box;

/**
 *
 */
public class QGraphConnect {

    /** Colored bulbs indicating good / bad connections between boxes */
    private Collection signs = new LinkedList();
    
    private QMain qMain;
    
    
    public QGraphConnect(QMain q) {
        qMain = q;
    }
    
    
    /**
     * Adds a (green) sign showing the validness of the connection between 
     * <code>box1</code> and <code>box2</code>.
     * The boolean parameters indicate whether the boxes need input (true) or 
     * output (false).
     * The indices specify the index of the IO connection at the box.
     * If the first parameter is true, an edge will be added to the query graph
     * (if it does not already exist).
     * 
     * @param addEdges specifies whether or not to update the query graph
     * @param box1
     * @param isInput1 true iff first box wants an input
     * @param index1
     * @param box2
     * @param isInput2
     * @param index2
     * @param conPoint the position where the sign appears
     */
    private void connect(boolean addEdges, IBox box1, boolean isInput1, 
        int index1, IBox box2, boolean isInput2, int index2, Point conPoint) {
        
        if (QModes.followMode) {
            addEdges = false;
        }
        
        Node node1 = box1.getNode();
        Node node2 = box2.getNode();
//        if (isInput1 == isInput2) {
//            throw new RuntimeException("Quoggles - connect: "+
//                "tried to connect two inputs / two outputs");
//        }

        ConnectedSign conSign = new ConnectedSign(true);
        if (addEdges) {           
            if (isInput1) {
                Collection edges = GeneralUtils.getEdges(node2, node1);
                boolean doAddEdge = true;
                for (Iterator eIt = edges.iterator(); eIt.hasNext();) {
                    Edge edge = (Edge)eIt.next();
                    if (edge.getInteger(QConstants.INPUT_INDEX_ID) == index1 &&
                        edge.getInteger(QConstants.OUTPUT_INDEX_ID) == index2) {

                        doAddEdge = false;
                        break;
                    }
                }
                if (doAddEdge) {
                    // only add edge if not (exactly the same edge) exists
                    Edge edge = 
                        qMain.getQueryGraph().addEdge(node2, node1, true);
                    System.out.println(" adding edge " + box2.getId() + " - \t " + box1.getId());
                    edge.addInteger("", QConstants.INPUT_INDEX_ID, index1);
                    edge.addInteger("", QConstants.OUTPUT_INDEX_ID, index2);
                }
            } else {
                Collection edges = GeneralUtils.getEdges(node1, node2);
                boolean doAddEdge = true;
                for (Iterator eIt = edges.iterator(); eIt.hasNext();) {
                    Edge edge = (Edge)eIt.next();
                    if (edge.getInteger(QConstants.INPUT_INDEX_ID) == index2 &&
                        edge.getInteger(QConstants.OUTPUT_INDEX_ID) == index1) {
    
                        doAddEdge = false;
                        break;
                    }
                }
                if (doAddEdge) {
                    // only add edge if not (exactly the same edge) exists
                    Edge edge = 
                        qMain.getQueryGraph().addEdge(node1, node2, true);
                    System.out.println(" adding edge " + box1.getId() + " - \t " + box2.getId());
                    edge.addInteger("", QConstants.INPUT_INDEX_ID, index2);
                    edge.addInteger("", QConstants.OUTPUT_INDEX_ID, index1);
                }
            }
        }
        
        conSign.setLocation(conPoint.x - ConnectedSign.RADIUS, 
            conPoint.y - ConnectedSign.RADIUS);
        qMain.getMainPanel().add(conSign, 0);
        signs.add(conSign);
    }

    /**
     * Adds a (red) sign showing that tere is no valid connection at the given 
     * point.
     * 
     * @param pt
     */
    private void showUnconnected(Point pt) {
        ConnectedSign conSign = new ConnectedSign(false);
        conSign.setLocation(pt.x - ConnectedSign.RADIUS, 
            pt.y - ConnectedSign.RADIUS);
        qMain.getMainPanel().add(conSign, 0);
        signs.add(conSign);
    }
    
    /**
     * Sorts the absolute positions of ALL input and output positions.
     * 
     * @param pointComparator
     * 
     * @return sorted map mapping absolute IO positions to
     * <code>BoxBooleanIndex</code> objects
     */
    private SortedMap fillTreeMap(PointComparator pointComparator) {
        SortedMap map = new TreeMap(pointComparator);
        for (Iterator it = qMain.getQueryGraph().getNodesIterator(); it.hasNext();) {
            Node node = (Node)it.next();
            IBox iBox = Util.getBox(node);
            IBoxRepresentation boxRep = iBox.getGraphicalRepresentation();
            
            Point[] absInputs = Util.getAbsoluteInputPos(boxRep);
            for (int i = 0; i < absInputs.length; i++) {
                map.put(absInputs[i], new BoxBooleanIndex(iBox, true, i));
            }
            
            Point[] absOutputs = Util.getAbsoluteOutputPos(boxRep);
            for (int i = 0; i < absOutputs.length; i++) {
                if (boxRep instanceof Input_Box.InputBoxRepresentation) {
                    absOutputs[i].setLocation(0, absOutputs[i].y);
                }
                map.put(absOutputs[i], new BoxBooleanIndex(iBox, false, i));
            }
        }
        
        return map;
    }
    
    /**
     * Check which inputs and outputs are connected by searching through the
     * io positions.
     * 
     * @param addEdges if true, edges will be added to the queryGraph if two
     * boxes are found to be connected.
     */
    public void checkConnections(boolean addEdges) {
        JPanel mPanel = qMain.getMainPanel();
        for (Iterator it = signs.iterator(); it.hasNext();) {
            JPanel sign = (JPanel)it.next();
            mPanel.remove(sign);
        }
        signs.clear();
        
        PointComparator pointComparator = new PointComparator();
        SortedMap map = fillTreeMap(pointComparator);
        
//        boolean veryFirst = true;
        Iterator it = map.entrySet().iterator();
        Point pt1 = null;
//        IBox box1 = null;
//        boolean isInput1 = true;
//        int index1 = 0;
        BoxBooleanIndex bb1 = null;
                
        if (it.hasNext()) {
            Map.Entry entry1 = (Map.Entry)it.next();
            pt1 = (Point)entry1.getKey();
            bb1 = (BoxBooleanIndex)entry1.getValue();
        }
        
        if (!it.hasNext()) {
            showUnconnected(pt1);
        }

        ArrayList checkList = new ArrayList(4);
//        boolean lastwasok = false;
        
        while (it.hasNext()) {
            Map.Entry entry2 = (Map.Entry)it.next();
            Point pt2 = (Point)entry2.getKey();
            BoxBooleanIndex bb2 = (BoxBooleanIndex)entry2.getValue();

            if (pointComparator.realcompare(pt1, pt2) == 0) {
                
                checkList.add(bb1);
                checkList.add(bb2);
                
                while (it.hasNext()) {
                    Map.Entry entry3 = (Map.Entry)it.next();
                    Point pt3 = (Point)entry3.getKey();
                    BoxBooleanIndex bb3 = (BoxBooleanIndex)entry3.getValue();

                    if (pointComparator.realcompare(pt2, pt3) == 0) {
                        checkList.add(bb3);
                    } else {
                        pt2 = pt3; // wil become pt1 further down
                        bb2 = bb3; // wil become bb1 further down
                        doCheckList(checkList, addEdges, pt1, mPanel);
                        checkList.clear();
                        break;
                    }
                }                
                if (!it.hasNext()) {
                    if (!checkList.isEmpty()) {
                        doCheckList(checkList, addEdges, pt1, mPanel);
                    } else {
                        showUnconnected(pt2);
                    }
                }
                
            } else {
                showUnconnected(pt1);
                // the very last is unconnected (if the last two have not been
                // at the same spot)
                if (!it.hasNext()) {
                    showUnconnected(pt2);
                }
            }            
            
            pt1 = pt2;
            bb1 = bb2;
        }
        
        mPanel.validate();
        mPanel.repaint();
    }

    private void doCheckList(ArrayList checkList, boolean addEdges, 
        Point pt, JPanel mainPanel) {
            
        int nrGoodCons = 0;
        int nrBadCons = 0;
        
        // check if there is an outputbox involved
        // TODO check if we can assume there is only ONE output box
        int obindex1 = -1;
        int obindex2 = -1;
        for (int i = 0; i < checkList.size(); i++) {
            BoxBooleanIndex bb = (BoxBooleanIndex)checkList.get(i);
            IBox box = bb.getIBox();
            if (box instanceof IOutputBox) {
                if (obindex1 == -1) {
                    obindex1 = i;
                } else {
                    obindex2 = i;
                    break;
                }
            }
        }

        // test all pairs (if an output box is present, only check connections
        // to the output box
        if (obindex1 != -1) {
            assert obindex2 != -1;
            // have an output box
            // System.out.println("have output box");

            BoxBooleanIndex obb1 = (BoxBooleanIndex)checkList.get(obindex1);
            IBox obox1 = obb1.getIBox();
            boolean oisInput1 = obb1.isInput();
            BoxBooleanIndex obb2 = (BoxBooleanIndex)checkList.get(obindex2);
            IBox obox2 = obb2.getIBox();
            boolean oisInput2 = obb2.isInput();

            // connect all with the correct output box input/output
            for (int i = 0; i < checkList.size(); i++) {
                if (i == obindex1 || i == obindex2) {
                    continue;
                }
                BoxBooleanIndex bb = (BoxBooleanIndex)checkList.get(i);
                IBox box = bb.getIBox();
                boolean isInput = bb.isInput();

                if (oisInput1 != isInput && obox1 != box) {
                    // System.out.println(obox1 + "    -    " + box);
                    int oindex1 = obb1.getIndex();
                    int index = bb.getIndex();
                    // different type of IO
                    nrGoodCons++;
                    connect(addEdges, obox1, oisInput1, oindex1, 
                        box, isInput, index, pt);
                } else if (oisInput2 != isInput && obox2 != box) {
                    // System.out.println(obox2 + "    -    " + box);
                    int oindex2 = obb1.getIndex();
                    int index = bb.getIndex();
                    // different type of IO
                    nrGoodCons++;
                    connect(addEdges, obox2, oisInput2, oindex2, 
                        box, isInput, index, pt);
                } else {
                    nrBadCons++;
                }
            }
            
            if (addEdges) {
                Graph queryGraph = qMain.getQueryGraph();
                // remove all connections between two non-output boxes
                // (the output box has to be squeezed in between)
                for (int i = 0; i < checkList.size(); i++) {
                    for (int j = i + 1; j < checkList.size(); j++) {
                        if (i == obindex1 || i == obindex2 || j == obindex1 || j == obindex2) {
                            // ignore output boxes
                            continue;
                        }
                        BoxBooleanIndex bb1 = (BoxBooleanIndex)checkList.get(i);
                        BoxBooleanIndex bb2 = (BoxBooleanIndex)checkList.get(j);
                        
                        IBox box1 = bb1.getIBox();
                        int index1 = bb1.getIndex();
                        IBox box2 = bb2.getIBox();
                        int index2 = bb2.getIndex();
                        
                        Node node1 = box1.getNode();
                        Node node2 = box2.getNode();
                        
                        Collection edges = GeneralUtils.getEdges(node2, node1);
                        Edge edgeToRemove = null;
                        for (Iterator eIt = edges.iterator(); eIt.hasNext();) {
                            Edge edge = (Edge)eIt.next();
                            if (edge.getInteger(QConstants.INPUT_INDEX_ID) == index1 &&
                                edge.getInteger(QConstants.OUTPUT_INDEX_ID) == index2) {
    
                                edgeToRemove = edge;
                                break;
                            }
                        }
                        if (edgeToRemove != null) {
                            queryGraph.deleteEdge(edgeToRemove);
                        }
                    }
                }
            }
            
        } else {
            // no output box, test all
            for (int i = 0; i < checkList.size(); i++) {
                for (int j = i + 1; j < checkList.size(); j++) {
                    BoxBooleanIndex bb1 = (BoxBooleanIndex)checkList.get(i);
                    BoxBooleanIndex bb2 = (BoxBooleanIndex)checkList.get(j);
                    
                    IBox box1 = bb1.getIBox();
                    boolean isInput1 = bb1.isInput();
                    IBox box2 = bb2.getIBox();
                    boolean isInput2 = bb2.isInput();
                    
                    if (isInput1 != isInput2 && box1 != box2) {
                        // System.out.println(box1 + "    -    " + box2);
                        int index1 = bb1.getIndex();
                        int index2 = bb2.getIndex();
                        // different type of IO
                        nrGoodCons++;
                        connect(addEdges, box1, isInput1, index1, 
                            box2, isInput2, index2, pt);
                    } else {
                        nrBadCons++;
                    }
                }
            }
        }
        if (nrGoodCons > 0) {
            if (nrBadCons > 0) {
                ConnectedSign sign = new ConnectedSign(true, false);
                sign.setLocation(pt.x - ConnectedSign.RADIUS, 
                    pt.y - ConnectedSign.RADIUS);
                mainPanel.add(sign, 0);
                signs.add(sign);
            }
        } else {
            ConnectedSign sign = new ConnectedSign(false);
            sign.setLocation(pt.x - ConnectedSign.RADIUS, 
                pt.y - ConnectedSign.RADIUS);
            mainPanel.add(sign, 0);
            signs.add(sign);
        }
    }
    
    public void reset() {
        
    }

}
