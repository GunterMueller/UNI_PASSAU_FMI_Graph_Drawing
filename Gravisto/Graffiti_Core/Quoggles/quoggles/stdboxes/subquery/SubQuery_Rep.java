package quoggles.stdboxes.subquery;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.ObjectParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.util.Pair;

import quoggles.QAssign;
import quoggles.QAuxiliary;
import quoggles.auxiliary.FileUtil;
import quoggles.auxiliary.Util;
import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.event.RepChangeEvent;
import quoggles.exceptions.LoadFailedException;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.stdboxes.input.Input_Box;

/**
 * Represents a sub query saved in a file.
 */
public class SubQuery_Rep extends DefaultBoxRepresentation 
    implements ActionListener {

    private JButton chooseButton;
    
    private File file;
    
    
    /**
     * Constructor of the box.
     * 
     * @param representedBox
     */
    public SubQuery_Rep(IBox representedBox) {
        super(representedBox);
    }

    
    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {
        String filePath = 
            ((StringParameter)parameters[0]).getValue().toString();
        int index = filePath.lastIndexOf(System.getProperty("file.separator"));
        String fileName = filePath;
        if (index > 0) {
            fileName = filePath.substring(index + 1);
        }
        
        chooseButton = new JButton(fileName);
        chooseButton.setBackground(IBoxConstants.PARAM_BACKGROUND);
        Dimension size = chooseButton.getPreferredSize();
        chooseButton.setSize(size);
        chooseButton.setPreferredSize(size);
        chooseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        // chooseButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        
        chooseButton.addActionListener(this);
        
        graphicalRep = standardizeBoxRep(graphicalRep, chooseButton);
        
//        size = graphicalRep.getPreferredSize();
//        if (size.width > IBoxConstants.MAX_BOX_WIDTH) {
//            size.setSize(IBoxConstants.MAX_BOX_WIDTH, size.height);
//        }
//        graphicalRep.setSize(size);
//        graphicalRep.setPreferredSize(size);
        
        graphicalRep.validate();
        QAuxiliary.listenerManager.repChanged(new RepChangeEvent(this));
    }
    
    
    /**
     * @see quoggles.representation.IBoxRepresentation#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        super.setParameters(params, fromBox);
        String filePath = 
            ((StringParameter)parameters[0]).getValue().toString();
        int index = filePath.lastIndexOf(System.getProperty("file.separator"));
        String fileName = filePath;
        if (index > 0) {
            fileName = filePath.substring(index + 1);
        }
        
        chooseButton.setText(fileName);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        // save assignment of rows since all new assignment will be ignored
        boolean[] assignedRowsCopy = QAssign.getAssignedRowsCopy();
        boolean[] assignedBEPRowsCopy = QAssign.getAssignedBEPRowsCopy();
////////        QAssign.resetAssignedRows();
//        int maxRowNr = QAssign.getMaxAssignedRowNr();
//        int maxBEPRowNr = QAssign.getMaxAssignedBEPRowNr();
//        boolean[] assignedRowsCopy = new boolean[maxBEPRowNr];
//        for (int i = 0; i < maxRowNr; i++) {
//            assignedRowsCopy[i] = QAssign.getRowAssignment(i);
//        }
//        boolean[] assignedBEPRowsCopy = new boolean[maxBEPRowNr - maxRowNr];
//        for (int i = maxRowNr; i < maxBEPRowNr; i++) {
//            assignedBEPRowsCopy[i - maxRowNr] = 
//                QAssign.getBEPRowAssignment(i);
//        }
        
        Graph subQueryGraph = null;
        try {
            Pair graphFilePair = FileUtil.getQueryFromSomeFile(false);
            if (graphFilePair == null) {
// probably just cancelled by user
//                JOptionPane.showMessageDialog(null, 
//                    "Error loading query: Loader returned \"null\"",
//                    "Error:", JOptionPane.ERROR_MESSAGE);
                    return;
            } else {
                subQueryGraph = (Graph)graphFilePair.getFst();
                file = (File)graphFilePair.getSnd();
            }
        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, 
                "Error loading query: " + fnfe.getLocalizedMessage(),
                "Error:", JOptionPane.ERROR_MESSAGE);
            // reset all assignments of rows
            QAssign.setAssignedRows(assignedRowsCopy);
            QAssign.setAssignedBEPRows(assignedBEPRowsCopy);
                return;
        } catch (LoadFailedException lfe) {
            JOptionPane.showMessageDialog(null, 
                "Error loading query: " + lfe.getLocalizedMessage(),
                "Error:", JOptionPane.ERROR_MESSAGE);
            // reset all assignments of rows
            QAssign.setAssignedRows(assignedRowsCopy);
            QAssign.setAssignedBEPRows(assignedBEPRowsCopy);
            return;
        }
        
        boolean ioutRemoved = false;
        boolean boolPredRemoved = false;

//        // remove any IOutputBoxes from the loaded graph
////        // remove any BoolPredicateEnd_Boxes 
//        for (Iterator it = subQueryGraph.getNodesIterator(); it.hasNext();) {
//            Node node = (Node)it.next();
//            IBox nodeBox = Util.getBox(node);
//            if (nodeBox instanceof IOutputBox) {
//                // remove iOutputBox, concat the boxes it separates
//                Collection inEdges = node.getAllInEdges();
//                Collection outEdges = node.getAllOutEdges();
//                if (inEdges.size() > 1 || outEdges.size() > 1) {
//                    throw new RuntimeException("Error: an output box has" +
//                        "more than one in- or out-neighbor");
//                } else if (inEdges.size() + outEdges.size() == 2) {
//                    // output box separates two boxes; unite those
//                    Edge inEdge = (Edge)inEdges.iterator().next();
//                    Edge outEdge = (Edge)outEdges.iterator().next();
//                    Edge edge = subQueryGraph.addEdge(
//                        inEdge.getSource(), outEdge.getTarget(), true);
//                    edge.addInteger("", QAssign.INPUT_INDEX_ID, 
//                        outEdge.getInteger(QAssign.INPUT_INDEX_ID));
//                    edge.addInteger("", QAssign.OUTPUT_INDEX_ID,
//                        inEdge.getInteger(QAssign.OUTPUT_INDEX_ID));
//                    int rowNr = ((OptionParameter)nodeBox
//                        .getParameters()[0]).getOptionNr();
//                    QAssign.assignRow(rowNr, false);
//                }
//                
//                subQueryGraph.deleteNode(node);
//                ioutRemoved = true;
//            
//            } 
////           else if (nodeBox instanceof BoolPredicateEnd_Box) {
////                // remove BoolPredicateEnd_Box
////                subQueryGraph.deleteNode(node);
////                int rowNr = ((OptionParameter)nodeBox
////                    .getParameters()[0]).getOptionNr();
////                QAssign.assignBEPRow(rowNr, false);
////
////                boolPredRemoved = true;
////                
//////                // add edge from
//////                // this SubQuery_Box to the new Bool...Box
//////                
//////                // TODO remove this box from sub query
//////                BoxRepresentation boxRep = nodeBox.getGraphicalRepresentation()
//////                    .getRepresentation();
//////                subQueryGraph.deleteNode(node);
//////                
//////                
//////                // place new one directly after this SubQuery_Box
//////                BoxRepresentation subBoxRep = getRepresentation();
//////                boxRep.setLocation(
//////                    subBoxRep.getX() + subBoxRep.getWidth(),
//////                    subBoxRep.getY() + 
//////                        (subBoxRep.getHeight() - boxRep.getHeight())/2);
//////
//////                // re-add it to the system
//////                quoggles.addBoxRep(boxRep, null);
////            }
//        }
        
// next lines: must find better solution: incorporate sub query
//        // check that a complex filter box has always at least the very first
//        // node of its predicate
//        for (Iterator it = subQueryGraph.getNodesIterator(); it.hasNext();) {
//            Node node = (Node)it.next();
//            IBox nodeBox = Util.getBox(node);
//            if (nodeBox instanceof ComplexFilter_Box) {
//                boolean ok = false;
//                for (Iterator oIt = node.getDirectedOutEdgesIterator(); oIt.hasNext();) {
//                    Edge edge = (Edge)oIt.next();
//                    int index = Util.getIOIndex(edge, true);
//                    if (index == 1) {
//                        // predicate sub query starts here
//                        ok = true;
//                        break;
//                    }
//                }
//                if (!ok) {
//                    JOptionPane.showMessageDialog(null,
//                        getIBox().getId() +
//                            ": Loaded sub query must have at least one box" +
//                            " attached to the predicate output of all" +
//                            " ComplexFilter_Box-es.",
//                        "Error loading sub query:", JOptionPane.ERROR_MESSAGE);
//                    return;
//                }
//            }
//        }

        // remove any input boxes (are no longer saved)
        // TODO can remove this some time
        for (Iterator it = subQueryGraph.getNodesIterator(); it.hasNext();) {
            Node node = (Node)it.next();
            if (Util.getBox(node) instanceof Input_Box) {
                subQueryGraph.deleteNode(node);
            }
        }

        ((StringParameter)parameters[0]).setValue(file.getPath());
        chooseButton.setText(file.getName());
        ((ObjectParameter)parameters[1]).setValue(subQueryGraph);
        box.setParameters(parameters, true);

        updateInputOutput();
        
        if (ioutRemoved || boolPredRemoved) {
            String message = null;
            if (ioutRemoved && boolPredRemoved) {
                message = "Some IOutputBox-es and BoolPredicateEnd_Box-es" +
                    " have been removed from the sub query.";
            } else if (ioutRemoved) {
                message = "One or some IOutputBox-es" +
                    " have been removed from the sub query.";
            } else if (boolPredRemoved) {
                message = "One or some BoolPredicate_Box-es" +
                    " have been removed from the sub query.";
            }

            JOptionPane.showMessageDialog
                (null, message, "Warning:", JOptionPane.WARNING_MESSAGE);
        }
        
        // reset all assignments of rows
        QAssign.setAssignedRows(assignedRowsCopy);
        QAssign.setAssignedBEPRows(assignedBEPRowsCopy);
//        for (int i = 0; i < maxRowNr; i++) {
//            QAssign.assignRow(i, assignedRowsCopy[i]);
//        }
//        for (int i = maxRowNr; i < maxBEPRowNr; i++) {
//            QAssign.assignBEPRow(i, assignedBEPRowsCopy[i - maxRowNr]);
//        }
    }
}
