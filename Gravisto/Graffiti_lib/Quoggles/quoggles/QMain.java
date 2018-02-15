package quoggles;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.graffiti.attributes.AttributeConsumer;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.UnificationException;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graph.OptAdjListGraph;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionEvent;
import org.graffiti.selection.SelectionListener;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;

import quoggles.auxiliary.GMLFileFilter;
import quoggles.auxiliary.Util;
import quoggles.boxes.IBox;
import quoggles.boxes.IOutputBox;
import quoggles.exceptions.BoxCreationFailedException;
import quoggles.exceptions.LoadFailedException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.icons.IBoxIcon;
import quoggles.parameters.OptionParameter;
import quoggles.representation.BoxRepresentation;
import quoggles.representation.IBoxRepresentation;
import quoggles.stdboxes.output.BoolPredicateEnd_Box;

/**
 * Main component. it delegats work to and communicates with several others.
 */
public class QMain implements SessionListener, SelectionListener,
    AttributeConsumer {

    /** The dialog holding the result table */
    private QResultDialog resultTableDialog;
    
    private QDialog qDialog;
    
    private QGraph qGraph;
    
    private QGraphMouse qMouse;
    
    private QGraphConnect qConnect;
    
    private QAuxiliary qAux;
    
    private QRunQuery qRunQuery;
    
    
    public QMain() {
        QModes.standAlone = false;
        
        qGraph = new QGraph(this);
        qMouse = new QGraphMouse(this);
        qConnect = new QGraphConnect(this);
        QAuxiliary.listenerManager.addListener(qGraph);
        qAux = new QAuxiliary(this);
        qRunQuery = new QRunQuery(this);
        
        FileFilter gmlFilter = new GMLFileFilter();
        QAuxiliary.fChooser.addChoosableFileFilter(gmlFilter);
        QAuxiliary.fChooser.setFileFilter(gmlFilter);
    }
    
    
    /**
     * Set a new graph on which the query will work.
     * 
     * @param graph
     */
    public void setGraph(Graph graph) {
        qRunQuery.setGraph(graph);
    }
    
    /**
     * Returns the query graph.
     * 
     * @return the query graph
     */
    public Graph getQueryGraph() {
        return qGraph.getQueryGraph();
    }
    
    public JPanel getMainPanel() {
        return qDialog.getMainPanel();
    }
    
    public void runQueryInFollowMode(Point pt) {
        QRunQuery.minOrderNr = 1;

        qRunQuery.runQueryInFollowMode(pt);
    }
    
    /**
     * Returns the main dialog.
     * 
     * @return the main dialog
     */
    public QDialog getDialog() {
        return qDialog;
    }
    
    
    /**
     * Show the main dialog.
     */
    public void showQDialog(boolean modal) {
        if (qDialog == null) {
            try {
                qDialog = new QDialog(this);
                qAux.loadIconsInto(qDialog.getIconPanel(), qMouse);
            } catch (BoxCreationFailedException bcfe) {
                JOptionPane.showMessageDialog
                    (null, "Error loading the dialog: " +
                        bcfe.getLocalizedMessage(), "Error:",
                        JOptionPane.ERROR_MESSAGE);
//                return;
            }
            qDialog.adjustIconPanelSize();
            qDialog.getMainPanel().addMouseListener(qMouse);
            qDialog.getMainPanel().addMouseMotionListener(qMouse);
            reset();
        }

        if (!QModes.standAlone) {
            MainFrame mf =
                GraffitiSingleton.getInstance().getMainFrame();
            mf.addSelectionListener(this);
            mf.addSessionListener(this);
        }

        qDialog.setVisible(true);
        qDialog.setModal(modal);
    }

//    public void addInputBoxes() {
//        qDialog.addInputBox(qGraph.addGraphInputBox());
//        checkSelectionInputBox();
//    }

    private IBox selInputBox = null;

    public void checkSelectionInputBox() {
        if (QModes.followMode || QModes.dontCheck || QModes.standAlone) {
            return;
        }
        
        Selection sel = GraffitiSingleton.getInstance()
            .getMainFrame().getActiveEditorSession()
                .getSelectionModel().getActiveSelection();
        if (sel != null && !sel.isEmpty()) {
            // there is a selection
            if (selInputBox == null) {
                // add sel box
                selInputBox = qGraph.addSelectionInputBox();
                qDialog.addInputBox(selInputBox.getGraphicalRepresentation()
                    .getRepresentation());
                
                checkConnections(true);
            }
        
        } else if (selInputBox != null) {
            // no selection but have an input box; remove it
            try {
                qGraph.removeBox(selInputBox);
            } catch (Exception e) {
                // ignore
            }
            qDialog.removeInputBox(
                selInputBox.getGraphicalRepresentation().getRepresentation());
            selInputBox = null;
            checkConnections(true);
        }
    }
    
    public void checkConnections(boolean addEdges) {
        qConnect.checkConnections(addEdges);
    }

    /**
     * Reset the query system.
     */
    public void reset() {
        // qMouse.reset must be after qDialog.reset!
        qDialog.reset();
        qAux.reset();
        qGraph.reset();
        qMouse.reset();
        qConnect.reset();
        qRunQuery.reset();
        if (resultTableDialog != null) {
            resultTableDialog.clearTable();
        }

        QAssign.resetAssignedRows();

        qDialog.addInputBox(qGraph.addGraphInputBox());
        checkSelectionInputBox();
    }

    public void runQuery() throws QueryExecutionException {
        QRunQuery.minOrderNr = 1;
        
        Util.resetAllBoxes(qGraph.getQueryGraph());
        
        qRunQuery.runQuery(qGraph.getQueryGraph(), qGraph.getInputNodes(), 
            false, new ArrayList(), new Stack());
        Util.resetAllBoxesFrom(qGraph.getQueryGraph(), qGraph.getInputNodes());
    }
    
    public void close() {
        if (QModes.standAlone) {
            System.exit(0);
        } else {
            qDialog.setVisible(false);
            clearQuery();
        }
    }
    
    public void saveQuery() throws IOException {
        qAux.saveQuery(getQueryGraph());
    }
    
    public void loadQuery() throws LoadFailedException {
        Graph newQueryGraph = qAux.loadQuery();

        if (newQueryGraph == null) {
            return;
            //throw new LoadFailedException("Loader returned null?!");
        }
        
        qGraph.setQueryGraph(newQueryGraph);
        qDialog.addInputBox(qGraph.addGraphInputBox());
        checkSelectionInputBox();

        checkConnections(true);
    }
    
    public void saveSubQuery() throws QueryExecutionException, IOException {
        qAux.saveSubQuery(qMouse.getMarkedSubGraph());        
    }
    
    public void clearQuery() {
        reset();
    }
    
    public void addBox() {
        System.out.println("not yet implemented");
    }
    
    public void setPlaceFreely(boolean isPFOn) {
        QModes.placeFreely = isPFOn;
    }
    
    public void setFollowMode(boolean isFMOn) {
        System.out.println("not yet implemented");
        //QModes.followMode = isFMOn;
            
////        // reset everything if switched out of followMode
////        if (!QModes.followMode) {
////            Map nodeOrigCopyMap = new HashMap();
////            for (Iterator it = killedNodes.iterator(); it.hasNext();) {
////                Node node = (Node)it.next();
////                nodeOrigCopyMap.put(node, queryGraph.addNodeCopy(node));
////            }
////            killedNodes.clear();
////            for (Iterator it = killedEdges.iterator(); it.hasNext();) {
////                Edge edge = (Edge)it.next();
////                Node source = (Node)nodeOrigCopyMap.get(edge.getSource());
////                if (source == null) {
////                    source = edge.getSource();
////                }
////                Node target = (Node)nodeOrigCopyMap.get(edge.getTarget());
////                if (target == null) {
////                    target = edge.getTarget();
////                }
////                Edge tmpedge = queryGraph.addEdgeCopy(edge, source, target);
////                try {
////                    tmpedge.getInteger(INPUT_INDEX_ID);
////                } catch (AttributeNotFoundException anfe) {
////                    System.out.println("badbadbad");
////                }
////            }
////            killedEdges.clear();
////            nodeOrigCopyMap = null;
////                
////            for (Iterator it = addedOutputBoxes.iterator(); it.hasNext();) {
////                Node node = (Node)it.next();
////                if (queryGraph.containsNode(node)) {
////                    queryGraph.deleteNode(node);
////                }
////            }
////            addedOutputBoxes.clear();
////
////            lastFollowed = null;
////
////            // unmark all box reps
////            for (Iterator it = boxReps.iterator(); it.hasNext(); ) {
////                unMarkBoxRep((BoxRepresentation)it.next());
////            }
////            checkSelectionInputBox();
////            resultDialog.setVisible(false);
////        } else {
////            // followMode activated
////            if (!standAlone) {
////                try {
////                    followSelection = (Selection)GraffitiSingleton
////                        .getInstance()
////                            .getMainFrame().getActiveEditorSession()
////                                .getSelectionModel().getActiveSelection()
////                                    .clone();
////                } catch (CloneNotSupportedException e1) {
////                    // Selections are clonable
////                    e1.printStackTrace();
////                }
////            }
////        }
    }
        
    public void addBox(IBoxIcon boxIcon) {
        IBox box = boxIcon.getNewBoxInstance();
        box.setDefaultParameters();
        box.setBoxNumber(QGraph.nextBoxNr++);
        IBoxRepresentation boxRep = box.getGraphicalRepresentation();
        BoxRepresentation rep = boxRep.getRepresentation();
        addBoxRep(rep);
                
        Rectangle rect = qDialog.getMainPanel().getVisibleRect();
        rep.setLocation(
            (int)rect.getCenterX()- rep.getWidth() / 2,
            (int)rect.getCenterY() - rep.getHeight() / 2);
        qMouse.moveBoxRepresentation
            (rep, rep.getLocation(), QModes.placeFreely);
    }

    /**
     * Sets the graph from the new session via 
     * <code>setGraph(s.getGraph())</code>.
     * 
     * @see org.graffiti.session.SessionListener#sessionChanged
     * (org.graffiti.session.Session)
     */
    public void sessionChanged(Session s) {
        if (s != null) {
            setGraph(s.getGraph());
        }
    }

    /**
     * Sets the graph from the changed session via 
     * <code>setGraph(s.getGraph())</code>.
     * 
     * @see org.graffiti.session.SessionListener#sessionDataChanged
     * (org.graffiti.session.Session)
     */
    public void sessionDataChanged(Session s) {
        setGraph(s.getGraph());
    }

    /**
     * Calls <code>checkSelectionInputBox()</code> to see if a selection
     * InputBox must be added /removed.
     * 
     * @see org.graffiti.selection.SelectionListener#selectionChanged
     * (org.graffiti.selection.SelectionEvent)
     */
    public void selectionChanged(SelectionEvent e) {
        checkSelectionInputBox();
    }

    /**
     * Delegates to <code>selectionChanged</code>.
     * 
     * @see org.graffiti.selection.SelectionListener#selectionListChanged
     * (org.graffiti.selection.SelectionEvent)
     */
    public void selectionListChanged(SelectionEvent e) {
        selectionChanged(e);
    }

    /**
     * Removes a <code>BoxRepresentation</code> from the panel, the query graph
     * and all relevant lists.
     * 
     * @param boxRep
     */
    public void removeBoxRep(BoxRepresentation boxRep) {
        IBox iBox = boxRep.getIBoxRepresentation().getIBox();
        if (iBox instanceof IOutputBox) { 
            // remove assignment of row since this box disappears
            int assRow = 
                ((OptionParameter)iBox.getParameters()[0]).getOptionNr();
            QAssign.assignRow(assRow, false);
        } else if (iBox instanceof BoolPredicateEnd_Box) {
            // remove assignment of row since this box disappears
            int assRow = ((IntegerParameter)iBox.getParameters()[0])
                .getInteger().intValue();
            QAssign.assignBEPRow(assRow, false);
        }
        
        qDialog.removeBoxRep(boxRep);
        qGraph.removeBox(boxRep.getIBoxRepresentation().getIBox());
        
        checkConnections(true);
    }

    /**
     * Calls <code>addBoxRep(boxRep, null);</code>
     * 
     * @param boxRep new representation
     */
    public Node addBoxRep(BoxRepresentation boxRep) {
        return addBoxRep(boxRep, null);
    }
    
    /**
     * Adds a <code>BoxRepresentation</code> to the panel, the query graph
     * and all relevant lists. If the given node is <code>null</code>, a new
     * node is added to the query graph.
     * 
     * @param boxRep new representation
     * @param boxNode the node associated with the box
     */
    public Node addBoxRep(BoxRepresentation boxRep, Node boxNode) {
        qDialog.addBoxRep(boxRep);
        boxNode = qGraph.addBoxRep(boxRep, boxNode);
        
        // done in qGraph.addBoxRep(...)
        //boxRep.getIBoxRepresentation().getIBox().setNode(boxNode);

        return boxNode;
    }

    /**
     * @see org.graffiti.attributes.AttributeConsumer#getNodeAttribute()
     */
    public CollectionAttribute getNodeAttribute() {
        return new NodeGraphicAttribute();
    }

    /**
     * @see org.graffiti.attributes.AttributeConsumer#getEdgeAttribute()
     */
    public CollectionAttribute getEdgeAttribute() {
        return new EdgeGraphicAttribute();
    }

    /**
     * @see org.graffiti.attributes.AttributeConsumer#getGraphAttribute()
     */
    public CollectionAttribute getGraphAttribute() {
        return null;
    }
    

    /**
     * <code>main</code> method for debugging purposes and 
     * start of standalone mode.
     * 
     * @param args
     */
    public static void main(String[] args) {
        QMain qDel = new QMain();

        QModes.standAlone = true;
        qDel.setGraph(createExampleGraph(qDel));

        qDel.showQDialog(true);
    }

    /**
     * Create a small example graph to work on when in standalone mode.
     */
    private static Graph createExampleGraph(AttributeConsumer ac) {
        Graph gr = new OptAdjListGraph();
        gr.addString("", "label", "g");

        try {
            gr.addAttributeConsumer(ac);
        } catch (UnificationException e) {
        }

        Node n1 = gr.addNode();
        Node n2 = gr.addNode();
        Node n3 = gr.addNode();
//        Node n4 = gr.addNode();
//        Node n5 = gr.addNode();
//        Node n6 = gr.addNode();
//        Node n7 = gr.addNode();
//        Node n8 = gr.addNode();
//        Node n9 = gr.addNode();
//        Node n10 = gr.addNode();
        gr.addEdge(n1, n2, true);
//        gr.addEdge(n2, n6, true);
//        gr.addEdge(n2, n5, true);
        gr.addEdge(n1, n3, true);
//        gr.addEdge(n3, n4, true);
//        gr.addEdge(n4, n7, true);
//        gr.addEdge(n8, n9, true);
//        gr.addEdge(n9, n4, true);
//        gr.addEdge(n9, n10, true);

        n1.addAttribute(new NodeLabelAttribute("nlabel", "1"), "");
        n2.addAttribute(new NodeLabelAttribute("nlabel", "2"), "");
        n3.addAttribute(new NodeLabelAttribute("nlabel", "3"), "");
        n1.addString("", "label", "1");
        n2.addString("", "label", "2");
        n3.addString("", "label", "3");
//        n4.addString("", "label", "1");
//        n5.addString("", "label", "2");
//        n6.addString("", "label", "2");
//        n7.addString("", "label", "2");
//        n8.addString("", "label", "1");
//        n9.addString("", "label", "2");
//        n10.addString("", "label", "1");

//        e1.addString("", "label", "3");
//        e2.addString("", "label", "3");
//        e3.addString("", "label", "4");
//        e4.addString("", "label", "5");
//        e5.addString("", "label", "3");
//        e6.addString("", "label", "3");
//        e7.addString("", "label", "4");
//        e8.addString("", "label", "5");

        return gr;
    }

}
