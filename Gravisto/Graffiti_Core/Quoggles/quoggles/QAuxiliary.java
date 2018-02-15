package quoggles;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.crimson.jaxp.DocumentBuilderFactoryImpl;
import org.apache.crimson.tree.TextNode;
import org.graffiti.attributes.Attribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.io.OutputSerializer;
import org.graffiti.plugin.view.MessageListener;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;
import org.graffiti.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import quoggles.auxiliary.FileUtil;
import quoggles.auxiliary.Util;
import quoggles.auxiliary.gmlxml.GMLXMLWriter;
import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.constants.QConstants;
import quoggles.event.ListenerManager;
import quoggles.exceptions.BoxCreationFailedException;
import quoggles.exceptions.LoadFailedException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.icons.IBoxIcon;
import quoggles.querygraph.BoxAttribute;
import quoggles.stdboxes.complexfilter.ComplexFilter_Box;
import quoggles.stdboxes.input.Input_Box;
import quoggles.stdboxes.output.BoolPredicateEnd_Box;
import quoggles.stdboxes.output.NormalOutput_Icon;

/**
 *
 */
public class QAuxiliary {

    /** FileChooser for saving / loading queries */ 
    public final static JFileChooser fChooser = new JFileChooser();
    
    /**
     * The <code>ListenerManager</code> used to process representation change 
     * events from <code>IBoxRepresentation</code>s.
     */
    public static final ListenerManager listenerManager =
        new ListenerManager();
        

    private QMain qMain;
    
    
    public QAuxiliary(QMain q) {
        qMain = q;
    }
    
    
    /**
     * Read the file containing descriptions of boxes and load them into the
     * system.
     * 
     * @param panel the panel to which the icons are added
     * 
     * @throws BoxCreationFailedException
     */
    public void loadIconsInto(JPanel panel, MouseListener ml) 
        throws BoxCreationFailedException {

        // read in xml description of available box icons
        DocumentBuilderFactory dbf = new DocumentBuilderFactoryImpl();
        DocumentBuilder docBuilder = null;
        Document document = null;
        try {
            docBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        }
        try {
            document = docBuilder.parse(QConstants.BOXES_DESCRIPTION_FILE);
        } catch (SAXException e2) {
//            e2.printStackTrace();
            throw new BoxCreationFailedException(e2);
        } catch (IOException e2) {
//            e2.printStackTrace();
            throw new BoxCreationFailedException(e2);
        }

        String name;
        String main;
        String description = "";
        String version = "";

        Element root = document.getDocumentElement();

        NodeList boxes = root.getElementsByTagName("box");
        IBoxIcon boxIcon = null;
        
        String lastBoxType = "quoggles.stdboxes";
        boolean sep = false;
        
        for (int i = 0; i < boxes.getLength(); i++) {
            Element boxNode = (Element)boxes.item(i);

            Element elem =
                (Element)boxNode.getElementsByTagName("name").item(0);
            if (!elem.hasChildNodes()) {
                throw new BoxCreationFailedException
                    ("Element tagged \"name\" must not be empty");
            }
            name = ((TextNode)elem.getFirstChild()).getData();

            elem = (Element)boxNode.getElementsByTagName("main").item(0);
            if (!elem.hasChildNodes()) {
                throw new BoxCreationFailedException
                    ("Element tagged \"main\" must contain a class name");
            }
            main = ((TextNode)elem.getFirstChild()).getData();
            String newBoxType = 
                main.substring(0, main.indexOf(".", main.indexOf(".") + 1));
            sep = false;
            if (!newBoxType.equals(lastBoxType)) {
                lastBoxType = newBoxType;
                sep = true;
            }

            elem =
                (Element)boxNode.getElementsByTagName("description").item(
                    0);
            if (elem.hasChildNodes()) {
                description = ((TextNode)elem.getFirstChild()).getData();
            }

            elem = (Element)boxNode.getElementsByTagName("version").item(0);
            if (elem.hasChildNodes()) {
                version = ((TextNode)elem.getFirstChild()).getData();
            }

            // check if valid and get IBoxIcon instance
            boxIcon = checkBox(main);
            // display the box icon

            addBoxIcon(boxIcon, panel, name, description, version, ml, sep);
        }

        panel.validate();
    }

    /**
     * Checks whether or not the main class and the representation class can 
     * be loaded and returns the iconicPanel gotten from the representation.
     * 
     * @param main
     * @param rep
     * @return
     */
    private IBoxIcon checkBox(String main) throws BoxCreationFailedException {
        IBoxIcon box = null;
        try {
            box = (IBoxIcon)InstanceLoader.createInstance(main);
        } catch (InstanceCreationException e) {
            throw new BoxCreationFailedException(
                "Could not instantiate " + "box: " + e.getMessage());
        } catch (ClassCastException cce) {
            throw new BoxCreationFailedException(
                "Element \"main\" did not "
                    + "describe a valid box icon: "
                    + cce.getMessage());
        }
        return box;
    }

    /**
     * Adds a new <code>IBoxIcon</code> to the icon panel.
     * 
     * @param boxIcon
     */
    public void addBoxIcon(IBoxIcon boxIcon, JPanel iconPanel,
        String name, String description, String version,
        MouseListener ml, boolean firstAux) {

        JPanel icon = new JPanel();
        icon.setOpaque(false);
        icon.setLayout(new BorderLayout());
        String labelText = name;
//        if (!version.equals("") ) {
//            labelText = name + " (" + version + ")";
//            labelText = name;
//        }
        JLabel label = new JLabel(labelText);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setSize(IBoxConstants.DEFAULT_ICON_WIDTH, 20);

        JComponent boxIconComp = (JComponent)boxIcon;
        boxIconComp.setToolTipText(description);
        
        icon.add(label, BorderLayout.NORTH);
        icon.add(boxIconComp, BorderLayout.CENTER);

        Dimension size = new Dimension(Math.max(boxIconComp.getPreferredSize().width,
            (int)label.getPreferredSize().getWidth()), 
            boxIconComp.getHeight() + label.getHeight());
        icon.setSize(size);
        icon.setMaximumSize(size);
        icon.setPreferredSize(size);
        icon.setMinimumSize(size);
        
        boxIconComp.addMouseListener(ml);
//        boxIconComp.addMouseMotionListener(this);

        // place icons in front of glue (last comp, to the right)
        if (boxIcon instanceof NormalOutput_Icon) {
//            iconPanel.add(icon, iconPanel.getComponentCount());
            iconPanel.add(icon, 0);
            iconPanel.add(Box.createRigidArea(new Dimension(20, 0)), 1);
        } else {
            if (firstAux) { 
                iconPanel.add(Box.createRigidArea(new Dimension(100, 0)), 
                    iconPanel.getComponentCount()-1);
            } else {
                iconPanel.add(Box.createRigidArea(new Dimension(20, 0)), 
                    iconPanel.getComponentCount()-1);
            }
            iconPanel.add(icon, iconPanel.getComponentCount()-1);
        }
    }

    /**
     * Load a query from a file. Discards active query.
     */
    public Graph loadQuery() throws LoadFailedException {
        // save assignment if nothing is loaded
        boolean[] assignedRowsCopy = QAssign.getAssignedRowsCopy();
        boolean[] assignedBEPRowsCopy = QAssign.getAssignedBEPRowsCopy();
        QAssign.resetAssignedRows();

        Graph newQueryGraph = null;
        try {
            Pair graphFilePair = FileUtil.getQueryFromSomeFile();
            if (graphFilePair == null) {
                // reset
                QAssign.setAssignedRows(assignedRowsCopy);
                QAssign.setAssignedBEPRows(assignedBEPRowsCopy);
                return null;
            } else {
                newQueryGraph = (Graph)graphFilePair.getFst();
            }
        } catch (Exception ex) {
            // reset
            QAssign.setAssignedRows(assignedRowsCopy);
            QAssign.setAssignedBEPRows(assignedBEPRowsCopy);
            throw new LoadFailedException(ex.getLocalizedMessage());
        }
        // successfully loaded
        
        // save assignments since they will be reset
        assignedRowsCopy = QAssign.getAssignedRowsCopy();
        assignedBEPRowsCopy = QAssign.getAssignedBEPRowsCopy();

        qMain.reset();
        
        QAssign.setAssignedRows(assignedRowsCopy);
        QAssign.setAssignedBEPRows(assignedBEPRowsCopy);
        
        return newQueryGraph;
    }

    /**
     * Save the active query into a file chosen by the user.
     */
    public void saveQuery(Graph queryGraph) throws IOException {
        OutputSerializer writer = new GMLXMLWriter();
        int returnVal = fChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File saveFile = fChooser.getSelectedFile();

            boolean doWrite = false;
            if(saveFile.exists()) {
                if(JOptionPane.showConfirmDialog(null,
                        "Do you want to overwrite the existing file " +
                        saveFile.getName() + "?", "Overwrite File?",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    doWrite = true;
                }
            } else {
                doWrite = true;
            }
            if (!doWrite) {
                return;
            }

            FileOutputStream outStream = new FileOutputStream(saveFile);
            
            // copy graph, remove input boxes
            Graph copyGraph = (Graph)queryGraph.copy();
            for (Iterator it = copyGraph.getNodesIterator(); it.hasNext();) {
                Node node = (Node)it.next();
                if (Util.getBox(node) instanceof Input_Box) {
                    copyGraph.deleteNode(node);
                }
            }
            
            writer.write(outStream, copyGraph);

            if (!QModes.standAlone) {
                GraffitiSingleton.getInstance().getMainFrame().showMesssage
                    ("Saved query to file " + saveFile.getName(), 
                    MessageListener.INFO);
            }
        }
    }
    
    /**
     * Save the active SUB query into a file chosen by the user.
     */
    public void saveSubQuery(Graph markedSubGraph) 
        throws QueryExecutionException, IOException {
            
        if (markedSubGraph == null || markedSubGraph.getNumberOfNodes() == 0) {
            JOptionPane.showMessageDialog(null, 
                "Need to select sub query first.", 
                "Hint:", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // assure that all predicates of all complex filter boxes are
        // correctly terminated
        boolean added = false;
        for (Iterator it = markedSubGraph.getNodesIterator(); it.hasNext();) {
            Node node = (Node)it.next();
            IBox box = Util.getBox(node);
            if (box instanceof ComplexFilter_Box) {
                boolean noPredicate = true;
                Iterator nit = node.getDirectedOutEdgesIterator();
                while (nit.hasNext()) {
                    Edge edge = (Edge)nit.next();
                    int index = Util.getIOIndex(edge, true);
                    if (index == 1) {
                        // predicate sub query starts here
                        noPredicate = false;
                        try {
                            Pair pair = ComplexFilter_Box
                                .checkSinks(edge.getTarget(), box);
                            added = ((Boolean)pair.getSnd()).booleanValue();
                        } catch (QueryExecutionException qee) {
                            throw new QueryExecutionException("Cannot save" +
                                " subquery. All predicates of ComplexFilter" +
                                "_Box-es must be terminated by a Bool" +
                                "PredicateEnd_Box.\n The error has been:" + qee);
                        }
                        break;
                    }
                }
                if (noPredicate) {
                    // add a BPE_Box as predicate
                    IBox outputBox = new BoolPredicateEnd_Box();
                    Node outputNode = markedSubGraph.addNode();
                    outputBox.setNode(outputNode);
                    Attribute boxAttr = 
                        new BoxAttribute(IBoxConstants.BOX_ATTR_ID, outputBox);
                    outputNode.addAttribute(boxAttr, "");
                    Edge edge = markedSubGraph.addEdge(node, outputNode, true);
                    edge.addInteger("", QConstants.INPUT_INDEX_ID, 0);
                    edge.addInteger("", QConstants.OUTPUT_INDEX_ID, 1);
                    added = true;
                }                    
            }
        }
        if (added) {
            JOptionPane.showMessageDialog(null, 
                "The predicate(s) of one or more ComplexFilter_Boxes have" +
                " been terminated by a BoolPredicateEnd_Box. \nThis was" +
                " necessary since in a sub query, no not-terminated" +
                " predicates are allowed.", 
                "Message:", JOptionPane.INFORMATION_MESSAGE);
        }
        
        
        OutputSerializer writer = new GMLXMLWriter();
        int returnVal = fChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File saveFile = fChooser.getSelectedFile();

            boolean doWrite = false;
            if(saveFile.exists()) {
                if(JOptionPane.showConfirmDialog(null,
                        "Do you want to overwrite the existing file " +
                        saveFile.getName() + "?", "Overwrite File?",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    doWrite = true;
                }
            } else {
                doWrite = true;
            }
            if (!doWrite) {
                return;
            }

            FileOutputStream outStream = new FileOutputStream(saveFile);

            writer.write(outStream, markedSubGraph);

            if (!QModes.standAlone) {
                GraffitiSingleton.getInstance().getMainFrame().showMesssage
                    ("Saved SUB query to file " + saveFile.getName(), 
                    MessageListener.INFO);
            }
        }
    }
    
    public void reset() {
        
    }

}
