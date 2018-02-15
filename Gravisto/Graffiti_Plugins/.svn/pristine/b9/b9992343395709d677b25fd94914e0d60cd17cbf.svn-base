package quoggles;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graph.OptAdjListGraph;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.util.GeneralEditorUtils;

import quoggles.auxiliary.ConnectedSign;
import quoggles.auxiliary.GhostBox;
import quoggles.auxiliary.HighlightBorder;
import quoggles.auxiliary.Triple;
import quoggles.auxiliary.Util;
import quoggles.boxes.IBox;
import quoggles.boxes.IOutputBox;
import quoggles.constants.IBoxConstants;
import quoggles.constants.QConstants;
import quoggles.icons.IBoxIcon;
import quoggles.parameters.OptionParameter;
import quoggles.representation.BoxRepresentation;
import quoggles.representation.IBoxRepresentation;
import quoggles.stdboxes.connectors.OneOneConnector_Box;
import quoggles.stdboxes.connectors.OneOneConnector_Rep;
import quoggles.stdboxes.input.Input_Box;
import quoggles.stdboxes.output.BoolPredicateEnd_Box;

/**
 * Implements actions resulting from mouse events.
 */
public class QGraphMouse 
    implements MouseListener, MouseMotionListener {

    /** Used to communicate between the individual parts of the system */
    private QMain qMain;

    /** Used for dragging purposes */
    private Point relDrag = null;

    /** Used for dragging purposes: component currently dragged */
    private JComponent draggedComp = null;

    /** Used for dragging purposes: Show "virtual component" at mouse pos */
    private final JComponent ghostComp = new GhostBox();

    /** Used for dragging purposes: true iff user drags */
    private boolean dragged = false;

    /** Used for dragging purposes */
    private Point2D dragPoint1 = new Point2D.Double(50d, 50d);
    /** Used for dragging purposes */
    private Point2D dragPoint2 = new Point2D.Double(150d, 50d);

    /** Used for dragging purposes */
    private boolean dragSecondPoint = true;

    /** Marks a corner of the selection rectangle */
    private Point2D selRectPt1 = null;

    /** Marks a corner of the selection rectangle */
    private Point2D selRectPt2 = null;

    /** Saves all relative drag positions for all marked boxes */
    private Map relDragMap = new HashMap();

    /** Empty (i.e. invisible) border */
    private final Border emptyBorder = new EmptyBorder(0, 0, 0, 0);
    
    /** Border used to highlight boxes */
    private final Border markBorder = new HighlightBorder(
        new Color(255, 255, 0, 175)); 

    /** Collection of all currently marked BoxRepresentations */
    private Collection markedBoxReps = new ArrayList(0);

    /** The induced subgraph of the marked nodes */
    private Graph markedSubGraph = new OptAdjListGraph();

    /** Maps copied marked nodes to their originals */
    private Map markedCopyOrigMap = new HashMap();

    /** Components used to show the order of IO of marked sub graph */
    private Collection showOrderComps = new LinkedList();
    

    /**
     * Constructor.
     * 
     * @param q main component used for communication.
     */
    public QGraphMouse(QMain q) {
        qMain = q;
        
        ghostComp.setVisible(false);
    }
    

    /**
     * Returns the currently marked sub graph.
     * 
     * @return the currently marked sub graph
     */
    public Graph getMarkedSubGraph() {
        return markedSubGraph;
    }

    /**
     * If double clicked on an icon, adds the corresponding box (uses the
     * functionality of <code>mousePressed</code>, <code>mouseDragged</code> 
     * and <code>mouseReleased</code>).
     * If double clicked on a box representation, removes this box.
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        if(!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        this.qMain.getMainPanel().remove(ghostComp);
        Object source = Util.getCorrectSource(e);
        this.qMain.getMainPanel().add(ghostComp);
        if (source instanceof IBoxIcon) {
            if (e.getClickCount() >= 2) {
                IBoxIcon boxIcon = (IBoxIcon)source;
                qMain.addBox(boxIcon);
                qMain.checkConnections(true);
            }
        } else if (source instanceof BoxRepresentation)  {
            if (e.getClickCount() >= 2) {
                BoxRepresentation boxRep = (BoxRepresentation)source;
                if (markedBoxReps.contains(boxRep)) {
                    // remove all marked
                    for (Iterator it = markedBoxReps.iterator();it.hasNext();){
                        qMain.removeBoxRep((BoxRepresentation)it.next());
                    }
                    markedBoxReps.clear();
                    markedCopyOrigMap.clear();
                    markedSubGraph.clear();
                } else {
                    qMain.removeBoxRep(boxRep);
                }
                markedBoxRepsChanged();

            } else {
                if (Util.isControlDown(e)) {
                    // (un)mark the box
                    BoxRepresentation boxRep = (BoxRepresentation)source;
                    boolean unmarked = false;
                    if (markedBoxReps.contains(boxRep)) {
                        // unmark box
                        unmarked = true;
                        boolean doReturn = false;
                        for (Iterator it = markedSubGraph.getNodesIterator();it.hasNext();) {
                            Node node = (Node)it.next();
                            BoxRepresentation theBoxRep = 
                                Util.getBox(node).getGraphicalRepresentation()
                                    .getRepresentation();
                            if (boxRep == theBoxRep) {
                                markedSubGraph.deleteNode(node);
                                markedCopyOrigMap.remove(node);
                                doReturn = true;
                                break;
                            }
                        }
                        unMarkBoxRep(boxRep);
                        markedBoxReps.remove(boxRep);
                        if (doReturn) {
                            return;
                        }
                    }

                    if (unmarked) {
                        JOptionPane.showMessageDialog(null, 
                            "Something bad happened in mouseClicked" +
                            " regarding the subQuery. Restart.", "Error:", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                    Node markedNode = boxRep.getIBoxRepresentation().getIBox()
                        .getNode();
                    Node copyMarkedNode = markedSubGraph.addNodeCopy(markedNode);
                    markedCopyOrigMap.put(copyMarkedNode, markedNode);

                    // see to / from which boxes exist edges
                    Iterator eIt = markedNode.getDirectedOutEdgesIterator();
                    while (eIt.hasNext()) {
                        Edge edge = (Edge)eIt.next();
                        for (Iterator it = markedSubGraph.getNodesIterator(); it.hasNext();) {
                            Node node = (Node)it.next();
                            if (markedCopyOrigMap.get(node) == edge.getTarget()) {
                                // is edge from markedNode to some node in graph
                                markedSubGraph.addEdgeCopy(edge, copyMarkedNode, node);
                            }
                        }
                    }
                    eIt = markedNode.getDirectedInEdgesIterator();
                    while (eIt.hasNext()) {
                        Edge edge = (Edge)eIt.next();
                        for (Iterator it = markedSubGraph.getNodesIterator(); it.hasNext();) {
                            Node node = (Node)it.next();
                            if (markedCopyOrigMap.get(node) == edge.getSource()) {
                                // is edge from some node in graph to markedNode
                                markedSubGraph.addEdgeCopy(edge, node, copyMarkedNode);
                                break;
                            }
                        }
                    }

                    // mark box
                    markedBoxReps.add(boxRep);
                    markBoxRep(boxRep);

                }
            }
        }
    }

    /**
     * Moves curently dragged component according to the mouse movement.
     * More precisely, a "virtual component" is moved and the real component
     * snaps to the next possible position (if there is one).
     *
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
//        if(!SwingUtilities.isLeftMouseButton(e) || e.isShiftDown() || 
//            e.isControlDown() || e.isAltDown()) {
//            return;
//        }
      if(!SwingUtilities.isLeftMouseButton(e)) {
          return;
      }

        if (relDrag != null && draggedComp instanceof BoxRepresentation) {
            dragged = true;
            selRectPt1 = null;
            Point newPos = new Point(relDrag.x + e.getPoint().x, 
                relDrag.y + e.getPoint().y);
            
            if (draggedComp instanceof 
                OneOneConnector_Rep.MyBoxRepresentation) {
                
                moveOneOneConn(e.getPoint(), QModes.placeFreely);
            
            } else {
                if (markedBoxReps.size() <= 1) {
                    ghostComp.setLocation(newPos);
                    moveBoxRepresentation(
                        (BoxRepresentation)draggedComp, newPos, QModes.placeFreely);
                    if (!markedBoxReps.isEmpty()) {
                        markedBoxRepsChanged();
                        draggedComp.getParent().repaint();
                    }

                } else {
                    for (Iterator it = markedBoxReps.iterator();it.hasNext();){
                        BoxRepresentation boxRep = 
                            (BoxRepresentation)it.next();
                        Point relDrag2 = (Point)relDragMap.get(boxRep);
                        Point newPt = new Point(relDrag2.x + e.getPoint().x,
                            relDrag2.y + e.getPoint().y);
                        moveBoxRepresentation(boxRep, newPt, true);
                    }
                    markedBoxRepsChanged();
                    draggedComp.getParent().repaint();
                }
            }
            ((JComponent)draggedComp.getParent())
                .scrollRectToVisible(ghostComp.getBounds());
            
        
        } else if (selRectPt1 != null) {
            dragged = true;

            // paint selection rectangle
//            JComponent mComp = (JComponent)draggedComp.getParent();
            selRectPt2 = e.getPoint();
            draggedComp.scrollRectToVisible(new Rectangle(
                (int)selRectPt2.getX(), (int)selRectPt2.getY(), 0, 0));

            GeneralEditorUtils.paintSelectionRectangle(
                draggedComp, selRectPt1, selRectPt2);
            
        }
    }

    /**
     * Equivalent of <code>moveBoxRepresentation</code> for 
     * <code>OneOneConnector_Box</code>es
     * 
     * @param pt
     * @param freePlacement
     */
    private void moveOneOneConn(Point pt, boolean freePlacement) {
        // remove edges from / to "floating" (i.e. dragged) box
        IBox iBox = ((BoxRepresentation)draggedComp)
            .getIBoxRepresentation().getIBox();
        Node boxNode = iBox.getNode();
        Graph queryGraph = qMain.getQueryGraph();
        for (Iterator it = boxNode.getEdgesIterator(); it.hasNext();) {
            queryGraph.deleteEdge((Edge)it.next());
        }
        
        if (dragSecondPoint) {
            if (freePlacement) {
                dragPoint2.setLocation(pt.getX() - draggedComp.getX(),
                    pt.getY() - draggedComp.getY());
            } else {
                boolean lookForFreeOutput = 
                    dragPoint2.getX() < dragPoint1.getX();
                dragPoint2 = Util.getNextFreeConnection(queryGraph,
                    pt, lookForFreeOutput, draggedComp);
                dragPoint2.setLocation(dragPoint2.getX() - draggedComp.getX(),
                    dragPoint2.getY() - draggedComp.getY());
            }
        } else {
            if (freePlacement) {
                dragPoint1.setLocation(pt.getX() - draggedComp.getX(),
                    pt.getY() - draggedComp.getY());
            } else {
                // move pt1
                boolean lookForFreeOutput = 
                    dragPoint1.getX() < dragPoint2.getX();
                dragPoint1 = Util.getNextFreeConnection(queryGraph,
                        pt, lookForFreeOutput, draggedComp);
                dragPoint1.setLocation(dragPoint1.getX() - draggedComp.getX(),
                    dragPoint1.getY() - draggedComp.getY());
            }
        }

        int x = draggedComp.getX() + 
            (int)Math.min(dragPoint1.getX(), dragPoint2.getX());
        int y = draggedComp.getY() + 
            (int)Math.min(dragPoint1.getY(), dragPoint2.getY());
        draggedComp.setLocation(x, y);
        ((CoordinateAttribute)boxNode.getAttribute
            (GraphicAttributeConstants.COORD_PATH)).setCoordinate
                (new Point(x + draggedComp.getWidth()/2 + QConstants.shiftX, 
                    y + draggedComp.getHeight()/2));

        double drag1x = dragPoint1.getX();
        double drag1y = dragPoint1.getY();
        dragPoint1.setLocation(
            dragPoint1.getX() - 
                (int)Math.min(dragPoint1.getX(), dragPoint2.getX()),// +3, 
            dragPoint1.getY() - 
                (int)Math.min(dragPoint1.getY(), dragPoint2.getY()));// +3);
        dragPoint2.setLocation(
            dragPoint2.getX() - 
                (int)Math.min(drag1x, dragPoint2.getX()),// +3, 
            dragPoint2.getY() - 
                (int)Math.min(drag1y, dragPoint2.getY()));// +3);

        ((OneOneConnector_Rep.MyBoxRepresentation)draggedComp).setDrawFromTL(
            (dragPoint1.getY() == dragPoint2.getY() || dragPoint1.getX() == dragPoint2.getX()) ||
            (dragPoint1.getX() < dragPoint2.getX() && dragPoint1.getY() < dragPoint2.getY()) ||
            (dragPoint1.getX() > dragPoint2.getX() && dragPoint1.getY() > dragPoint2.getY()));
                
        int maxX = (int)Math.max(dragPoint1.getX(), dragPoint2.getX());
        int maxY = (int)Math.max(dragPoint1.getY(), dragPoint2.getY());
        if (maxX < 2) {
            maxX = 2;
            draggedComp.setLocation(draggedComp.getX() - 1, draggedComp.getY());
            ((CoordinateAttribute)boxNode.getAttribute
                (GraphicAttributeConstants.COORD_PATH)).setCoordinate
                    (new Point(
                        draggedComp.getX() - 1 + 
                            draggedComp.getWidth()/2 + QConstants.shiftX, 
                        draggedComp.getY() + 
                            draggedComp.getHeight()/2));
        }
        if (maxY < 2) {
            maxY = 2;
            draggedComp.setLocation(draggedComp.getX(), draggedComp.getY() - 1);
            ((CoordinateAttribute)boxNode.getAttribute
                (GraphicAttributeConstants.COORD_PATH)).setCoordinate
                    (new Point(
                        draggedComp.getX() + draggedComp.getWidth()/2 + QConstants.shiftX, 
                        draggedComp.getY() - 1 + draggedComp.getHeight()/2));
        }
                
        draggedComp.setSize(maxX, maxY);
                
        qMain.checkConnections(false);
    }

    /**
     * If last parameter is <code>false</code>:
     * Calculate the next possible position for the given box representation
     * starting at the given position. Snaps the component to this position.
     * This method is quite expensive (at least with this rather
     * quadratic implementation).
     * 
     * If last parameter is <code>true</code>, the box is just moved according
     * to the mouse pointer's position.
     * 
     * @param rep
     * @param newPos
     * @param freePlacement
     */
    public void moveBoxRepresentation
        (BoxRepresentation rep, Point newPos, boolean freePlacement) {
        
        if (!freePlacement) {
            moveBoxRepresentation(rep, newPos);
        
        } else {
            IBoxRepresentation iBoxRep = rep.getIBoxRepresentation();
        
            boolean movedIsOutputBox = 
                iBoxRep.getIBox() instanceof IOutputBox ? true : false;

            // remove edges from / to "floating" (i.e. dragged) box
            Node boxNode = iBoxRep.getIBox().getNode();
            if (movedIsOutputBox) {
                try {
                    boxNode.removeAttribute(QConstants.OUTPUT_BOX_ID);
                } catch (AttributeNotFoundException anfe) {
                    // no problem
                }
            }
            Graph queryGraph = qMain.getQueryGraph();
            for (Iterator it = boxNode.getEdgesIterator(); it.hasNext();) {
                queryGraph.deleteEdge((Edge)it.next());
            }
        
            int x = newPos.x;
            int y = newPos.y;
            rep.setLocation(x, y);
            ((CoordinateAttribute)boxNode.getAttribute
                (GraphicAttributeConstants.COORD_PATH)).setCoordinate
                    (new Point(x + rep.getWidth()/2 + QConstants.shiftX, 
                        y + rep.getHeight()/2));
        
            qMain.checkConnections(false);
        }
    }

    /**
     * Calculate the next possible position for the given box representation
     * starting at the given position. Snaps the component to this position.
     * This method is quite expensive (at least with this rather
     * quadratic implementation).
     * 
     * @param rep
     * @param newPos
     */
    private void moveBoxRepresentation(BoxRepresentation rep, Point newPos) {
        double dist = Double.POSITIVE_INFINITY;
        Point2D moveMyInputTo = null;
        Point2D moveMyOutputTo = null;
        Point2D moveToHerInput = null;
        Point2D moveToHerOutput = null;
        IBoxRepresentation iBoxRep = rep.getIBoxRepresentation();
        
        boolean movedIsOutputBox = iBoxRep.getIBox() instanceof IOutputBox ? true : false;

        // calculate input / output positions for dragged box
        boolean movedHasInputs = iBoxRep.getIBox().getNumberOfInputs() > 0 ? true : false;
        Point2D[] inputs = null;
        Point2D[] absInputs = null;
        Point2D[] outputs = null;
        Point2D[] absOutputs = null;    
        
        if (movedHasInputs) {
            inputs = iBoxRep.getRelInputsPos();
            
            absInputs = new Point2D[inputs.length];
            for (int i = 0; i < inputs.length; i++) {
                Point2D pt = inputs[i];
                absInputs[i] = new Point2D.Double(
                    newPos.getX() + rep.getWidth()*pt.getX(), 
                    newPos.getY() + rep.getHeight()*pt.getY());
            }
        }
        
        boolean movedHasOutputs = 
            iBoxRep.getIBox().getNumberOfOutputs() > 0 ? true : false;
        if (movedHasOutputs) {
            outputs = iBoxRep.getRelOutputsPos();
            absOutputs = new Point2D[outputs.length];
            for (int i = 0; i < outputs.length; i++) {
                Point2D pt = outputs[i];
                absOutputs[i] = new Point2D.Double(
                    newPos.getX() + rep.getWidth()*pt.getX(), 
                    newPos.getY() + rep.getHeight()*pt.getY());
            }
        }
        
        // remove edges from / to "floating" (i.e. dragged) box
        Node boxNode = iBoxRep.getIBox().getNode();
        if (movedIsOutputBox) {
            try {
                boxNode.removeAttribute(QConstants.OUTPUT_BOX_ID);
            } catch (AttributeNotFoundException anfe) {
                // no problem
            }
        }
        Graph queryGraph = qMain.getQueryGraph();
        for (Iterator it = boxNode.getEdgesIterator(); it.hasNext();) {
            queryGraph.deleteEdge((Edge)it.next());
        }
        
        // find closest matching output / input
        for (Iterator it = queryGraph.getNodesIterator(); it.hasNext();) {
            Node node = (Node)it.next();
            IBox box = Util.getBox(node);
            iBoxRep = box.getGraphicalRepresentation();
            BoxRepresentation boxRep = iBoxRep.getRepresentation();

            boolean checkOutputs = true;
            
            if (boxRep == rep) continue;
            
//            if (box instanceof IOutputBox && movedIsOutputBox) {
//                continue;
//            }

//////            if (iBoxRep.getIBox() instanceof OutputBox) continue;
            
//            Node boxNode2 = iBoxRep.getIBox().getNode();
            
            int nrOutputs = iBoxRep.getIBox().getNumberOfOutputs();
            if (movedHasInputs && nrOutputs > 0) {
                // check if rep's input could be connected to boxRep's output
                if (!movedIsOutputBox && node.getOutDegree() == nrOutputs) {
                    // already full
                    checkOutputs = false;
                }
                
                if (movedIsOutputBox || checkOutputs) {
                    // see which outputs are free (no edge exists for these indices)
                    ArrayList occupied = new ArrayList(nrOutputs - 1);
                    for (Iterator edgeIt = node.getAllOutEdges().iterator(); edgeIt.hasNext();) {
                        Edge edge = (Edge)edgeIt.next();
                        occupied.add(new Integer(edge.getInteger(QConstants.OUTPUT_INDEX_ID)));
                    }
                    Point2D[] outputs2 = iBoxRep.getRelOutputsPos();
                    for (int i = 0; i < outputs2.length; i++) {
                        if (!movedIsOutputBox && occupied.contains(new Integer(i))) {
                            // dont use occupied outputs
                            continue;
                        }
                        Point2D output2 = outputs2[i];
        
                        Point2D absOutput2 = null;
                        if (iBoxRep instanceof Input_Box.InputBoxRepresentation) {
                            absOutput2 = new Point2D.Double(
                                0d, boxRep.getY() + boxRep.getHeight()*output2.getY());
                        } else {
                            absOutput2 = new Point2D.Double(
                                boxRep.getX() + boxRep.getWidth()*output2.getX(), 
                                boxRep.getY() + boxRep.getHeight()*output2.getY());
                        }
                        
                        for (int j = 0; j < absInputs.length; j++) {
                            Point2D absInput = absInputs[j];
                            
                            double d = absInput.distanceSq(absOutput2);
                            if (d < dist) {
                                dist = d;
                                moveMyInputTo = inputs[j];
                                moveMyOutputTo = null;
                                moveToHerInput = null;
                                moveToHerOutput = absOutput2;
                            }
                        }
                    }
                }
            }


            int nrInputs = iBoxRep.getIBox().getNumberOfInputs();
            if (movedHasOutputs && nrInputs > 0) {
                // check if rep's output could be connected to boxRep's input
                if (!movedIsOutputBox && node.getInDegree() == nrInputs) {
//                if (nrInputs == 0 || boxNode2.getInDegree() == nrInputs) {
                    // already full
                    continue;
                }
                
                // see which inputs are free (no edge exists for these indices)
                ArrayList occupied = new ArrayList(nrInputs - 1);
                for (Iterator edgeIt = node.getAllInEdges().iterator(); edgeIt.hasNext();) {
                    Edge edge = (Edge)edgeIt.next();
                    occupied.add(new Integer(edge.getInteger
                        (QConstants.INPUT_INDEX_ID)));
                }
                Point2D[] inputs2 = iBoxRep.getRelInputsPos();
                for (int i = 0; i < inputs2.length; i++) {
                    if (!movedIsOutputBox && occupied.contains(new Integer(i))) {
                        // dont use occupied inputs
                        continue;
                    }
                    Point2D input2 = inputs2[i];
    
                    Point2D absInput2 = new Point2D.Double(
                        boxRep.getX() + boxRep.getWidth()*input2.getX(), 
                        boxRep.getY() + boxRep.getHeight()*input2.getY());
                    
                    for (int j = 0; j < absOutputs.length; j++) {
                        Point2D absOutput = absOutputs[j];
                        
                        double d = absOutput.distanceSq(absInput2);
                        if (d < dist) {
                            dist = d;
                            moveMyInputTo = null;
                            moveMyOutputTo = outputs[j];
                            moveToHerInput = absInput2;
                            moveToHerOutput = null;
                        }
                    }
                }
            }
        }
        
        if (Double.isInfinite(dist)) {
            return;
        }
        
        int x = 0;
        int y = 0;
        if (moveMyInputTo != null) {
            x = (int)(moveToHerOutput.getX() - 
                rep.getWidth()*moveMyInputTo.getX());
            y = (int)(moveToHerOutput.getY() - 
                rep.getHeight()*moveMyInputTo.getY());
        } else {
            x = (int)(moveToHerInput.getX() - 
                rep.getWidth()*moveMyOutputTo.getX());
            y = (int)(moveToHerInput.getY() - 
                rep.getHeight()*moveMyOutputTo.getY());
        }
        rep.setLocation(x, y);
        ((CoordinateAttribute)boxNode.getAttribute
            (GraphicAttributeConstants.COORD_PATH)).setCoordinate
                (new Point(x + rep.getWidth()/2 + QConstants.shiftX, 
                    y + rep.getHeight()/2));
        
        qMain.checkConnections(false);
    }

    /**
     * Empty except <code>followMode</code> is switched on.
     * 
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
////        if (QModes.followMode) {
////            qMain.runQueryInFollowMode(e.getPoint());
////        }
    }

    /**
     * As it might be the start of a drag, set all relevant flags and fields.
     * If pressed on a "red sign", start a 1-1-connector there.
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        if(!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        draggedComp = null;
        Component mayBeSign = 
            ((JComponent)e.getSource()).getComponentAt(e.getPoint());

        if (mayBeSign instanceof ConnectedSign) {
            // see which box is connected to this ConnectedSign
            Graph queryGraph = qMain.getQueryGraph();
            for (Iterator it = queryGraph.getNodesIterator(); it.hasNext();) {
                Node node = (Node)it.next();
                IBox box = Util.getBox(node);
                IBoxRepresentation boxRep = box.getGraphicalRepresentation();
            
                Point[] absInputs = Util.getAbsoluteInputPos(boxRep);
                for (int i = 0; i < absInputs.length; i++) {
                    if (absInputs[i].distanceSq(e.getPoint()) < 
                        ConnectedSign.RADIUS * ConnectedSign.RADIUS) {
                        
                        draggedComp = boxRep.getRepresentation();
                        break;
                    }
                }
                if (draggedComp != null) {
                    break;
                }
            
                Point[] absOutputs = Util.getAbsoluteOutputPos(boxRep);
                for (int i = 0; i < absOutputs.length; i++) {
                    if (boxRep instanceof Input_Box.InputBoxRepresentation) {
                        absOutputs[i].setLocation(0, absOutputs[i].y);
                    }
                    if (absOutputs[i].distanceSq(e.getPoint()) < 
                        ConnectedSign.RADIUS * ConnectedSign.RADIUS) {
                        
                        draggedComp = boxRep.getRepresentation();
                        break;
                    }
                }
                if (draggedComp != null) {
                    break;
                }
            }
            if (draggedComp == null) {
                draggedComp = Util.getCorrectSource(e);
            }
        
            if (((ConnectedSign)mayBeSign).isGreen()) {

                if (draggedComp instanceof BoxRepresentation) {
                    ghostComp.setSize(draggedComp.getSize());
                    ghostComp.setLocation(draggedComp.getLocation());
                    ghostComp.setVisible(true);
                    draggedComp.getParent().validate();
                    draggedComp.getParent().repaint();
                    relDrag = new Point(draggedComp.getX() - e.getPoint().x,
                        draggedComp.getY() - e.getPoint().y);
                }
            } else {
                if (draggedComp instanceof OneOneConnector_Rep.MyBoxRepresentation) {
                    ghostComp.setSize(draggedComp.getSize());
                    ghostComp.setLocation(draggedComp.getLocation());
                    ghostComp.setVisible(true);
                    draggedComp.getParent().validate();
                    draggedComp.getParent().repaint();
                    relDrag = new Point(draggedComp.getX() - e.getPoint().x,
                        draggedComp.getY() - e.getPoint().y);
                    if (((OneOneConnector_Rep.MyBoxRepresentation)draggedComp)
                        .getDrawFromTL()) {

                        dragPoint1.setLocation(0, 0);
                        dragPoint2.setLocation(
                            draggedComp.getWidth(), 
                            draggedComp.getHeight()); 
                    } else {
                        dragPoint1.setLocation(0, draggedComp.getHeight());
                        dragPoint2.setLocation(draggedComp.getWidth(), 0);
                    }

                    Point2D corMousePos = new Point2D.Double(
                        e.getPoint().x - draggedComp.getX(),
                        e.getPoint().y - draggedComp.getY());
                    dragSecondPoint = corMousePos.distanceSq(dragPoint1) >
                        corMousePos.distanceSq(dragPoint2);
                } else {
                    // start 1-1-connector
                    IBox box = new OneOneConnector_Box();
                    box.setDefaultParameters();
                    box.setBoxNumber(QGraph.nextBoxNr++);
                    IBoxRepresentation boxRep = box.getGraphicalRepresentation();
                    BoxRepresentation rep = boxRep.getRepresentation();
                    Node node = qMain.addBoxRep(rep);
                    int x = mayBeSign.getLocation().x + mayBeSign.getWidth()/2;
                    int y = mayBeSign.getLocation().y + mayBeSign.getHeight()/2;
                    rep.setLocation(x, y);
                    rep.setSize(25, 15);
                    ((CoordinateAttribute)node.getAttribute
                        (GraphicAttributeConstants.COORD_PATH)).setCoordinate
                            (new Point(x + rep.getWidth()/2 + QConstants.shiftX, 
                                y + rep.getHeight()/2));
                    draggedComp = rep;
                    draggedComp.getParent().validate();
                    draggedComp.getParent().repaint();
                    relDrag = new Point(0, 0);
                    dragSecondPoint = true;
                    dragPoint1.setLocation(0, 0);
                    dragPoint2.setLocation(25, 15);
                }
            }

        } else {
            draggedComp = Util.getCorrectSource(e);
        
            if (draggedComp instanceof BoxRepresentation) {
                if (!(draggedComp instanceof OneOneConnector_Rep.MyBoxRepresentation)) {
                    ghostComp.setSize(draggedComp.getSize());
                    ghostComp.setLocation(draggedComp.getLocation());
                    ghostComp.setVisible(true);
                    ghostComp.getParent().validate();
                    ghostComp.getParent().repaint();
                } else {
                    if (((OneOneConnector_Rep.MyBoxRepresentation)draggedComp)
                        .getDrawFromTL()) {

                        dragPoint1.setLocation(0, 0);
                        dragPoint2.setLocation(
                            draggedComp.getWidth(), 
                            draggedComp.getHeight()); 
                    } else {
                        dragPoint1.setLocation(0, draggedComp.getHeight());
                        dragPoint2.setLocation(draggedComp.getWidth(), 0);
                    }

                    Point2D corMousePos = new Point2D.Double(
                        e.getPoint().x - draggedComp.getX(),
                        e.getPoint().y - draggedComp.getY());
                    dragSecondPoint = corMousePos.distanceSq(dragPoint1) >
                        corMousePos.distanceSq(dragPoint2);
                }
            
                relDrag = new Point(draggedComp.getX() - e.getPoint().x,
                    draggedComp.getY() - e.getPoint().y);
            }
        }

        if (markedBoxReps.contains(draggedComp)) {
            // move all
            relDragMap.clear();
            for (Iterator it = markedBoxReps.iterator(); it.hasNext();) {
                BoxRepresentation boxRep = (BoxRepresentation)it.next();
                Point relDrag2 = new Point(boxRep.getX() - e.getPoint().x,
                    boxRep.getY() - e.getPoint().y);
                relDragMap.put(boxRep, relDrag2);
            }
            
        } else {
            // might start selection rectangle here
            selRectPt1 = e.getPoint();
            if (!Util.isControlDown(e)) {
                markedSubGraph.clear();
                for (Iterator it = markedBoxReps.iterator(); it.hasNext(); ) {
                    unMarkBoxRep((BoxRepresentation)it.next(), false);
                }
                // remove old show order comps
                Container mPanel = null;
                for (Iterator it = showOrderComps.iterator(); it.hasNext();) {
                    Component comp = (Component)it.next();
                    mPanel = comp.getParent();
                    if (mPanel != null) {
                        mPanel.remove(comp);
                    }
                }
                showOrderComps.clear();
                if (mPanel != null) {
                    mPanel.validate();
                    mPanel.repaint();
                }
    
                markedBoxReps.clear();
            }
        }
    }

    /**
     * Calls <code>checkConnections(true)</code> if it succeeds a drag 
     * operation and removes any visible "virtual component".
     * Resets all flags etc. indicating a drag.
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        if(!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        if (dragged) {
            // check all connections between inputs and outputs
            qMain.checkConnections(true);
        }
        
        relDrag = null;
        ghostComp.setVisible(false);
        
        if (dragged && selRectPt1 != null) {
            // selection rectangle ended here
            selRectPt2 = e.getPoint();
            int tlx = (int) Math.min(selRectPt1.getX(), selRectPt2.getX());
            int tly = (int) Math.min(selRectPt1.getY(), selRectPt2.getY());
            int w = (int) Math.abs(selRectPt1.getX() - selRectPt2.getX());
            int h = (int) Math.abs(selRectPt1.getY() - selRectPt2.getY());

            Rectangle selRect = new Rectangle(tlx, tly, w, h);
            
            Map origCopyMap = new HashMap();
            markedCopyOrigMap.clear();
            
            // see which boxes are inside the rectangle
            Graph queryGraph = qMain.getQueryGraph();
            for (Iterator it = queryGraph.getNodesIterator(); it.hasNext();) {
                Node node = (Node)it.next();
                IBox box = Util.getBox(node);
                if (box instanceof Input_Box) {
                    continue;
                }
                BoxRepresentation boxRep = 
                    box.getGraphicalRepresentation().getRepresentation();
                if (selRect.contains(boxRep.getBounds())) {
                    markedBoxReps.add(boxRep);
                    Node copyNode = markedSubGraph.addNodeCopy(node);
                    origCopyMap.put(node, copyNode);
                    markedCopyOrigMap.put(copyNode, node);
                }
            }
            
            // add all existing edges between the marked nodes
            for (Iterator it = origCopyMap.keySet().iterator(); it.hasNext();) {
                Node sNode = (Node)it.next();
                for (Iterator eIt = sNode.getDirectedOutEdgesIterator(); eIt.hasNext();) {
                    Edge edge = (Edge)eIt.next();
                    Node tNode = edge.getTarget();
                    if (origCopyMap.containsKey(tNode)) {
                        markedSubGraph.addEdgeCopy(edge, 
                            (Node)origCopyMap.get(sNode),
                            (Node)origCopyMap.get(tNode));
                    }
                }
            }            
            
            for (Iterator it = markedBoxReps.iterator(); it.hasNext(); ) {
                markBoxRep((BoxRepresentation)it.next());
            }
            
            selRectPt1 = null;
            ghostComp.getParent().repaint();
        }

        dragged = false;
    }

    /**
     * Empty.
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Empty.
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Remove anything that highlighted the <code>boxRep</code>.
     * The given boolean value specifies whether or not the
     * <code>markedBoxRepsChanged</code> mthod is called.
     * 
     * @param boxRep
     */
    private void unMarkBoxRep(BoxRepresentation boxRep, boolean inform) {
        boxRep.setBorder(emptyBorder);
        if (inform && !QModes.followMode) {
            markedBoxRepsChanged();

        } else if (!inform) {
            IBox box = boxRep.getIBoxRepresentation().getIBox();
            Node node = box.getNode();
            try {
                node.removeAttribute(QConstants.MARKED_INPUT_NRS_ID);
            } catch (AttributeNotFoundException anfe) {
                // ignore
            }
            try {
                node.removeAttribute(QConstants.MARKED_OUTPUT_NRS_ID);
            } catch (AttributeNotFoundException anfe) {
                // ignore
            }
        }
    }
    
    /**
     * Remove anything that highlighted the <code>boxRep</code>.
     * 
     * @param boxRep
     */
    private void unMarkBoxRep(BoxRepresentation boxRep) {
        unMarkBoxRep(boxRep, true);
    }
    
    /**
     * Do something to highlight the <code>boxRep</code>.
     * 
     * @param boxRep
     */
    private void markBoxRep(BoxRepresentation boxRep) {
        boxRep.setBorder(markBorder);
        if (boxRep instanceof Input_Box.InputBox_Rep) {
//            boxRep.setBorder(new LineBorder(Color.YELLOW, 5));
            boxRep.setBackground(Color.YELLOW);
        }
        if (!QModes.followMode) {
            markedBoxRepsChanged();
        }
    }

    /**
     * Takes care to display / save order of IO for marked sub query.
     */
    private void markedBoxRepsChanged() {
        // calculate / show / save order of inputs / outputs (y-coord)
        SortedMap sortedInputs = new TreeMap();
        SortedMap sortedOutputs = new TreeMap();
        for (Iterator it = markedSubGraph.getNodesIterator(); it.hasNext();) {
            Node node = (Node)it.next();
            
            try {
                node.removeAttribute(QConstants.MARKED_INPUT_NRS_ID);
            } catch (AttributeNotFoundException anfe) {
                // ignore
            }
            try {
                node.removeAttribute(QConstants.MARKED_OUTPUT_NRS_ID);
            } catch (AttributeNotFoundException anfe) {
                // ignore
            }
            
            IBox box = Util.getBox(node);
            
            if (box instanceof IOutputBox) {
                // put those as outputs in front
////                BoxRepresentation boxRep = box.getGraphicalRepresentation()
////                    .getRepresentation();
////                double yCoord = -1E10 + boxRep.getX();
////                // well ... generate different values for really 
////                // equal coordinates
////                while (sortedOutputs.containsKey(new Double(yCoord))) {
////                    yCoord += 1E-2;
////                }
                int nr = ((OptionParameter)box.getParameters()[0]).getOptionNr();
                Point inPos = Util.getAbsPos
                    (box.getGraphicalRepresentation(), false, 0);
                Triple tri = new Triple(node, new Integer(0), inPos);
                sortedOutputs.put(new Integer(nr), tri);
////                sortedOutputs.put(new Double(yCoord), tri);
                
                continue;
            }
            
            // else (if its not an IOutputBox) ...
            int boxIONumber = box.getNumberOfInputs();
            // Node origNode = (Node)markedCopyOrigMap.get(node);
            if (node.getInDegree() < boxIONumber) {
                
                boolean[] free = new boolean[boxIONumber];
                for (int i = 0; i < free.length; i++) {
                    free[i] = true;
                }
                Iterator eIt = node.getDirectedInEdgesIterator();
                while (eIt.hasNext()) {
                    Edge edge = (Edge)eIt.next();
                    free[Util.getIOIndex(edge, false)] = false;
                }
                for (int i = 0; i < free.length; i++) {
                    if (free[i]) {
                        Point inPos = Util.getAbsPos
                            (box.getGraphicalRepresentation(), true, i);
                        Triple tri = new Triple(node, new Integer(i), inPos);
                        double yCoord = inPos.y + 1E-10 * inPos.x;
                        // well ... generate different values for really 
                        // equal coordinates
                        while (sortedInputs.containsKey(new Double(yCoord))) {
                            yCoord += 1E-10 * inPos.x;
                        }
                        sortedInputs.put(new Double(yCoord), tri);
                    }
                }
            }
////            boxIONumber = box.getNumberOfOutputs();
////            if (node.getOutDegree() < boxIONumber) {
////                
////                boolean[] free = new boolean[boxIONumber];
////                for (int i = 0; i < free.length; i++) {
////                    free[i] = true;
////                }
////                Iterator eIt = node.getDirectedOutEdgesIterator();
////                while (eIt.hasNext()) {
////                    Edge edge = (Edge)eIt.next();
////                    free[Util.getIOIndex(edge, true)] = false;
////                }
////                for (int i = 0; i < free.length; i++) {
////                    if (free[i]) {
////                        Point outPos = null;
//////                        if (box instanceof BoolPredicateEnd_Box) {
//////                            BoxRepresentation boxRep =
//////                                box.getGraphicalRepresentation()
//////                                    .getRepresentation();
//////                            outPos = new Point(boxRep.getX() + boxRep.getWidth(),
//////                                boxRep.getY() + boxRep.getHeight() / 2);
//////                        } else {
////                            outPos = Util.getAbsPos
////                                (box.getGraphicalRepresentation(), false, i);
//////                        }
////                        Triple tri = new Triple(node, new Integer(i), outPos);
////                        double yCoord = outPos.y + 1E-10 * outPos.x;
////                        // well ... generate different values for equal coordinates
////                        while (sortedInputs.containsKey(new Double(yCoord))) {
////                            yCoord += 1E-10 * outPos.x;
////                        }
////                        sortedOutputs.put(new Double(yCoord), tri);
////                    }
////                }
////            }            
        }
        // remove old show order comps
        for (Iterator it = showOrderComps.iterator(); it.hasNext();) {
            Component comp = (Component)it.next();
            Container mPanel = comp.getParent();
            if (mPanel != null) {
                mPanel.remove(comp);
            }
        }
        showOrderComps.clear();
                
        JPanel mainPanel = qMain.getMainPanel();

        int inCnt = 1;
        for (Iterator it = sortedInputs.values().iterator(); it.hasNext();) {
            Triple tri = (Triple)it.next();
            Node node = (Node)tri.getFst();
            int index = ((Integer)tri.getSnd()).intValue();

            // save as attribute in node
            CollectionAttribute nrsAttr = null;
            try {
                nrsAttr = (CollectionAttribute)
                    node.getAttribute(QConstants.MARKED_INPUT_NRS_ID);
            } catch (AttributeNotFoundException anfe) {
                nrsAttr = new HashMapAttribute(QConstants.MARKED_INPUT_NRS_ID);
                node.addAttribute(nrsAttr, "");
            }
            Attribute nrAttr = 
                new IntegerAttribute("i" + String.valueOf(index), inCnt);
            nrsAttr.add(nrAttr);

            // show as component in mainPanel
            JLabel label = new JLabel("i" + String.valueOf(inCnt));
            label.setSize(label.getPreferredSize());
            label.setPreferredSize(label.getPreferredSize());
            Point pt = (Point)tri.getThd();
            label.setLocation(pt.x + 5, pt.y + 8);
            showOrderComps.add(label);
            mainPanel.add(label, 0);
            inCnt++;
        }
        
        int outCnt = 1;
        for (Iterator it = sortedOutputs.values().iterator(); it.hasNext();) {
            Triple tri = (Triple)it.next();
            Node node = (Node)tri.getFst();
            int index = ((Integer)tri.getSnd()).intValue();

            // save as attribute in node
            CollectionAttribute nrsAttr = null;
            try {
                nrsAttr = (CollectionAttribute)
                    node.getAttribute(QConstants.MARKED_OUTPUT_NRS_ID);
            } catch (AttributeNotFoundException anfe) {
                nrsAttr = 
                    new HashMapAttribute(QConstants.MARKED_OUTPUT_NRS_ID);
                node.addAttribute(nrsAttr, "");
            }
            Attribute nrAttr = 
                new IntegerAttribute("o" + String.valueOf(index), outCnt);
            nrsAttr.add(nrAttr);

            // show as component in mainPanel
            JLabel label = new JLabel("o" + String.valueOf(outCnt));
            label.setSize(label.getPreferredSize());
            label.setPreferredSize(label.getPreferredSize());
            Point pt = (Point)tri.getThd();
            IBox box = Util.getBox(node);            
            if (box instanceof IOutputBox) {
                label.setLocation(pt.x + 8, 
                    pt.y + 2 * IBoxConstants.DEFAULT_OUTPUTBOX_IO_HEIGHT / 3);
            } else if (box instanceof BoolPredicateEnd_Box) {
                label.setLocation(pt.x, 
                    pt.y - label.getPreferredSize().height / 2);
            } else {
                label.setLocation(pt.x - 17, pt.y + 8);
            }
            showOrderComps.add(label);
            mainPanel.add(label, 0);
            outCnt++;
        }
      
        
        mainPanel.validate();
        mainPanel.repaint();
    }

    public void reset() {
        markedBoxReps.clear();
        markedCopyOrigMap.clear();
        markedSubGraph.clear();
        qMain.getMainPanel().add(ghostComp);
    }



////    /** Edges that have been temporarily deleted during followMode */
////    private Collection killedEdges = new ArrayList(5);
////    
////    /** Nodes that have been temporarily deleted during followMode */
////    private Collection killedNodes = new ArrayList(5);
////    
////    /** OutputBoxes added in followNode */
////    private Collection addedOutputBoxes = new ArrayList(1);
////    
////    /** BoxRepresentation where mouse pointed to last in followMode */
////    private BoxRepresentation lastFollowed = null;
////    
////    /** The selection that was active when followMode was activated */
////    private Selection followSelection = null;
////    
////    /** Nodes from which exists a path to currently followed node */
////    private Collection allPreNodes = new ArrayList(15);
////    
////    /** A dialog used in followMode to display non-GraphElement results */
////    private MyJDialog resultDialog =
////        new MyJDialog(null, "Result of query:", false);
////    
////      class MyJDialog extends JDialog {
////          
////          private JTextComponent text = new JTextArea(1, 40);
////  
////          /**
////       * @param owner
////       * @param title
////       * @param modal
////       * 
////       * @throws java.awt.HeadlessException
////       */
////          public MyJDialog(Dialog owner, String title, boolean modal)
////              throws HeadlessException {
////  
////              super(owner, title, modal);
////              getContentPane().add(text);
////              getContentPane().setSize(text.getPreferredSize());
////          }
////          
////  //        public JTextComponent getTextComponent() {
////  //            return text;
////  //        }
////  
////          /**
////       * Set the text displayed by this dialog.
////       * 
////       * @param str the text the dialog should show.
////       */
////          public void setText(String str) {
////              text.setText(str);
////          }
////  
////          /**
////       * Append the given string to the text displayed by this dialog.
////       * 
////       * @param str the string to be appended.
////       */
////          public void appendText(String str) {
////              text.setText(text.getText() + str);
////          }
////  
////          /**
////       * Append the given string to the text displayed by this dialog.
////       * 
////       * @param str the string to be appended.
////       */
////      public void appendTextWithComma(String str) {
////          String txt = text.getText();
////          if ("".equals(txt)) {
////              text.setText(str);
////          } else {
////              text.setText(text.getText() + ", " + str);
////              }
////          }
////  
////          /**
////       * @see javax.swing.JDialog#getDefaultCloseOperation()
////       */
////      public int getDefaultCloseOperation() {
////          return WindowConstants.DO_NOTHING_ON_CLOSE;
////      }
////      
////      /**
////       * @see java.awt.Window#getFocusableWindowState()
////       */
////          public boolean getFocusableWindowState() {
////              return false;
////          }
////  
////      }
    
}
