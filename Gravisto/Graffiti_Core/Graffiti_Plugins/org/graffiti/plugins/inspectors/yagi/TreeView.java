//=============================================================================
//
//   TreeView.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: TreeView.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.event.ListenerManager;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.selection.Selection;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;

/**
 * This class provides a tree with the attribute hierarchy of the currently
 * selected attributable(s). The tree allows adding of new attributes to
 * CollectionAttributes, removing of non-default attributes selecting a tree
 * node to edit/view it's attributes values in the editPanel.
 */
public class TreeView extends ViewTab implements TreeSelectionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 9221961531867530293L;

    /** The text to display in the popup menu for adding an attribute. */
    private final String ADD_TEXT = "Add";

    /** The text to display in the popup menu for removing an attribute. */
    private final String REMOVE_TEXT = "Remove";

    /** The text to display in the popup menu for adding a new folder. */
    private final String ADD_FOLDER_TEXT = "Create folder";

    /**
     * Constructs a new TreeView and builds the tabbed pane.
     */
    public TreeView() {
        super();
    }

    /**
     * Builds the tree of all attributes of the given list of attributables
     * (e.g. several selected nodes).
     * 
     * @param type
     *            the type of the current selected tab (e.g. NODE)
     * @param sel
     *            the current selection
     */
    @Override
    public void buildTopPane(int type, Selection sel) {

        if (!sel.isEmpty()) {
            // store selected attributables
            currentEdges = sel.getEdges();
            currentNodes = sel.getNodes();
        }

        AbstractTab tab = null;
        if (type == NODE) {
            tab = this.nodeTab;
            this.attributables = currentNodes;
        } else if (type == EDGE) {
            tab = this.edgeTab;
            this.attributables = currentEdges;
        } else if (type == GRAPH) {
            tab = this.graphTab;
            this.attributables = currentGraph;
        } else
            return;

        // get root attribute of the selection's first attributable
        Attribute rootAttr = ((Attributable) attributables.get(0))
                .getAttributes();

        // build root node
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
                new BooledAttribute(rootAttr, true));
        JTree attributeTree = new JTree(rootNode);

        // set tree to refuse selection of multiple tree nodes
        attributeTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);

        // build the rest of the tree
        fillNode(rootNode);

        // listen for selection changes and mouse events
        attributeTree.addTreeSelectionListener(this);
        attributeTree.addMouseListener(new PopupListener());
        attributeTree.expandRow(0);

        // get path to "graphics"
        TreePath selectPath = attributeTree.getNextMatch(
                GraphicAttributeConstants.GRAPHICS, 0,
                javax.swing.text.Position.Bias.Forward);

        if (selectPath == null) {
            // select root node
            attributeTree.setSelectionRow(0);
        } else {
            // if "graphics" is available, select it
            attributeTree.setSelectionPath(selectPath);
            attributeTree.scrollPathToVisible(selectPath);
            attributeTree.expandPath(selectPath);
            attributeTree.makeVisible(selectPath);
        }

        // add tree to the panel
        JPanel topPanel = tab.getTopPanel();
        topPanel.removeAll();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(attributeTree);
    }

    /**
     * Rebuild the tree of the selected attributables.
     * 
     * @param type
     *            the type of the current selected tab (e.g. NODE)
     * @param sel
     *            the current selection
     */
    @Override
    public void rebuildTopPane(int type, Selection sel) {

        if (!sel.isEmpty()) {
            // store selected attributables
            currentEdges = sel.getEdges();
            currentNodes = sel.getNodes();
        }

        AbstractTab tab = null;
        if (type == NODE) {
            tab = this.nodeTab;
            this.attributables = currentNodes;
        } else if (type == EDGE) {
            tab = this.edgeTab;
            this.attributables = currentEdges;
        } else if (type == GRAPH) {
            tab = this.graphTab;
            this.attributables = currentGraph;
        } else
            return;

        // reset attribute tree
        JPanel topPanel = tab.getTopPanel();
        topPanel.removeAll();

        if (attributables.size() == 0) {
            topPanel.revalidate();
            return;
        }
        // build new tree
        Attribute newAttr = ((Attributable) attributables.get(0))
                .getAttributes();

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
                new BooledAttribute(newAttr, true));
        JTree attributeTree = new JTree(rootNode);
        attributeTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        attributeTree.addTreeSelectionListener(this);
        attributeTree.addMouseListener(new PopupListener());

        // build tree and return the currently selected tree node
        DefaultMutableTreeNode selectedNode = fillNode(rootNode);
        TreePath selectedTreePath = null;

        if (selectedNode != null) {
            selectedTreePath = new TreePath(selectedNode.getPath());
        }

        if (selectedTreePath == null) {
            // select root node
            attributeTree.setSelectionRow(0);
            attributeTree.expandRow(0);
            selectedTreePath = attributeTree.getNextMatch(
                    GraphicAttributeConstants.GRAPHICS, 0,
                    javax.swing.text.Position.Bias.Forward);
        }
        if (selectedTreePath != null) {
            // select last selected node
            attributeTree.setSelectionPath(selectedTreePath);
            attributeTree.scrollPathToVisible(selectedTreePath);
            attributeTree.expandPath(selectedTreePath);
            attributeTree.makeVisible(selectedTreePath);
        }
        topPanel.add(attributeTree);

        topPanel.revalidate();
    }

    /**
     * Adds a new node to treeNode. If treeNode is a CollectionAttribute or a
     * CompositeAttribute, fillNode will recursively build the tree hierarchy.
     * FillNode checks if all attributables (e.g. all selected graph edges) have
     * this attribute (if not, skip this attribute) and if so, if they all have
     * the same value (if not, display dummy ("---")).
     * 
     * @param treeNode
     *            the node where the new node/subtree will be added to
     * @return the tree node of the current selection
     */
    private DefaultMutableTreeNode fillNode(DefaultMutableTreeNode treeNode) {
        DefaultMutableTreeNode returnTreeNode = null;
        DefaultMutableTreeNode newNode;
        Attribute attr = ((BooledAttribute) treeNode.getUserObject())
                .getAttribute();
        String currentSelectionPath = ((AbstractTab) this
                .getSelectedComponent()).getCurrentSelectionPath();

        boolean allHave = true;
        boolean allSameValue = true;
        Collection<Attribute> attrs;

        // if treeNode is no leave, get children
        if (attr instanceof CollectionAttribute) {
            attrs = ((CollectionAttribute) attr).getCollection().values();
        } else {
            // attribute is a leave

            if (attributables.size() > 1) {
                for (Attributable attributable : attributables) {
                    try {
                        Attribute oAttr = attributable.getAttribute(attr
                                .getPath());

                        // check if this attribute has the same value in all
                        // selected attributables
                        if (!attr.getValue().equals(oAttr.getValue())) {
                            allSameValue = false;
                            break;
                        }
                    } catch (AttributeNotFoundException anfe) {
                        // found attributable that has no such attribute
                        // -> skip this node
                        allHave = false;
                        break;
                    }
                }
            }

            if (allHave) {
                // all selected attributables have this attribute
                newNode = new DefaultMutableTreeNode(new BooledAttribute(attr,
                        allSameValue));
                if (attr.getPath().equals(currentSelectionPath)) {
                    returnTreeNode = newNode;
                }
            }
            return returnTreeNode;
        }

        for (Attribute attribute : attrs) {
            allHave = true;
            allSameValue = true;

            // check if present in all attributables
            if (attributables.size() > 1) {
                for (Iterator<? extends Attributable> ait = attributables
                        .iterator(); ait.hasNext();) {
                    try {
                        Attribute oAttr = ((Attributable) ait.next())
                                .getAttribute(attribute.getPath());

                        if (allSameValue
                                && !attribute.getValue().equals(
                                        oAttr.getValue())) {
                            // found attributable that has another value
                            allSameValue = false;
                        }
                    } catch (AttributeNotFoundException anfe) {
                        // found attributable that has no such attribute
                        allHave = false;
                        break;
                    } catch (NullPointerException npe) {
                        // found attributable that has no such attribute
                        allHave = false;
                        break;
                    }
                }
            }

            if (allHave) {
                // all selected attributables have this attribute
                newNode = new DefaultMutableTreeNode(new BooledAttribute(
                        attribute, allSameValue));

                if (returnTreeNode == null) {
                    returnTreeNode = fillNode(newNode);
                } else {
                    // last selection already found
                    fillNode(newNode);
                }

                if (!allSameValue && newNode.getChildCount() > 0) {
                    // if all children of newNode have the same value,
                    // change the boolean of newNode to true.
                    // otherwise collectionAttributes which have a
                    // registered VEC (like color-attributes) would not
                    // display properly
                    boolean childrenAreTrue = true;
                    Enumeration<?> children = newNode.children();
                    for (; children.hasMoreElements();) {
                        DefaultMutableTreeNode child = (DefaultMutableTreeNode) children
                                .nextElement();
                        BooledAttribute booled = (BooledAttribute) child
                                .getUserObject();
                        if (booled.getBool() == false) {
                            // child with boolean "false" found
                            childrenAreTrue = false;
                            break;
                        }
                    }
                    if (childrenAreTrue) {
                        // change newNode's boolean
                        newNode.setUserObject(new BooledAttribute(attribute,
                                true));
                    }
                }

                // add node to the attribute tree
                treeNode.add(newNode);

                if (attribute.getPath().equals(currentSelectionPath)) {
                    returnTreeNode = newNode;
                }
            }
        }
        return returnTreeNode;
    }

    /**
     * Adds a new attribute to the selected attributable(s). If an attribute
     * exists at the desired path in one or more attributables, nothing will be
     * changed in these attributables.
     * 
     * @param newAttribute
     *            the attribute will be added to the attributable(s)
     */
    public void addAttribute(Attribute newAttribute) {
        AbstractTab selected = (AbstractTab) this.getSelectedComponent();
        Attributable attributable = this.attributables.get(0);
        String currentSelectionPath = selected.getCurrentSelectionPath();

        Graph graph = null;
        if (!(attributable instanceof Graph)) {
            graph = ((GraphElement) attributable).getGraph();
        } else {
            graph = (Graph) attributable;
        }

        ListenerManager lm = graph.getListenerManager();
        lm.transactionStarted(this);

        String parentPath = "";
        if (!currentSelectionPath.equals("")) {
            parentPath = currentSelectionPath.substring(Attribute.SEPARATOR
                    .length());
        }

        int failed = 0;
        int done = 0;
        for (Attributable atbl : attributables) {
            try {
                atbl.addAttribute((Attribute) newAttribute.copy(), parentPath);
                done++;
            } catch (AttributeExistsException ex) {
                failed++;
            }
        }
        if (failed > 0) {
            // attribute already exists in one or more attributables
            // -> show message
            String donePostfix = "";
            String failedPostfix = "";
            if (done > 1) {
                donePostfix = "s";
            }
            if (failed > 1) {
                failedPostfix = "s";
            }
            JOptionPane.showMessageDialog(this, "Could not add attribute to "
                    + failed + " attributable" + failedPostfix
                    + ":\nAttribute already exists.\nAttribute " + "added to "
                    + done + " attributable" + donePostfix + ".",
                    "Attribute added", JOptionPane.OK_OPTION);
        }

        // set currentSelectionPath to the new attribute
        selected.setCurrentSelectionPath(currentSelectionPath
                + Attribute.SEPARATOR + newAttribute.getId());
        lm.transactionFinished(this);
        ((DefaultEditPanel) selected.getEditPanel()).postUndoInfo();
    }

    /**
     * Removes the currently selected attribute from the attributable(s).
     */
    public void removeAttribute() {

        AbstractTab selected = (AbstractTab) this.getSelectedComponent();
        Attributable attributable = this.attributables.get(0);
        String currentSelectionPath = selected.getCurrentSelectionPath();
        String path = currentSelectionPath.substring(Attribute.SEPARATOR
                .length());
        Graph graph = null;

        if (!(attributable instanceof Graph)) {
            graph = ((GraphElement) attributable).getGraph();
        } else {
            graph = (Graph) attributable;
        }
        ListenerManager lm = graph.getListenerManager();

        lm.transactionStarted(this);

        for (Attributable atbl : attributables) {
            try {
                atbl.removeAttribute(path);
            } catch (NullPointerException npe) {
                // do nothing
            } catch (ClassCastException cce) {
                // do nothing
            }
        }

        // set new currentSelectionPath to the parent of the deleted node
        int indexOfLastSeparator = currentSelectionPath
                .lastIndexOf(Attribute.SEPARATOR);
        assert indexOfLastSeparator >= 0; // correct path may also be .label0 or
                                          // similar
        String parentPath = currentSelectionPath.substring(0,
                indexOfLastSeparator);

        selected.setCurrentSelectionPath(parentPath);

        lm.transactionFinished(this);
        ((DefaultEditPanel) selected.getEditPanel()).postUndoInfo();
    }

    /**
     * Reacts to changes in the tree selection. Displays the attributes of the
     * new selection in the editPanel.
     * 
     * @param event
     *            the event describing the change of the selection
     */
    public void valueChanged(TreeSelectionEvent event) {

        TreePath treePath = event.getNewLeadSelectionPath();

        if (treePath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath
                    .getLastPathComponent();

            // set new currentSelectionPath
            String newPath = ((BooledAttribute) node.getUserObject())
                    .getAttribute().getPath();
            ((AbstractTab) this.getSelectedComponent())
                    .setCurrentSelectionPath(newPath);

            // display attributes in the editPanel
            AbstractTab currentTab = (AbstractTab) this.getSelectedComponent();
            currentTab.getEditPanel().buildTable(node, attributables);
        }
    }

    /**
     * This class provides a dialog for adding attributes to an attributable.
     * The user can enter the name (label) of the new attribute and select its
     * type.
     */
    private static class AddDialog extends JDialog {

        /**
         * 
         */
        private static final long serialVersionUID = 2789084391979770792L;

        /** The class name of the new attribute. */
        private String attrType;

        /** The name of the new attribute. */
        private String attrName;

        /** The TextField where the user enters the new attribute name. */
        private JTextField nameTextField;

        /**
         * The Combobox where the user chooses the type of the new attribute.
         */
        private JComboBox typeCombo;

        /** The attribute types for the comboBox. */
        private Object[] types = new String[] {
                "org.graffiti.attributes.StringAttribute",
                "org.graffiti.attributes.IntegerAttribute",
                "org.graffiti.attributes.BooleanAttribute",
                "org.graffiti.attributes.DoubleAttribute",
                "org.graffiti.attributes.HashMapAttribute",
                "org.graffiti.graphics.ColorAttribute",
                "org.graffiti.graphics.NodeLabelAttribute",
                "org.graffiti.graphics.EdgeLabelAttribute",
                "org.graffiti.graphics.RenderedImageAttribute",
                "org.graffiti.graphics.PortAttribute",
                "org.graffiti.graphics.PortsAttribute" };

        /**
         * Creates a dialog for adding a new label.
         * 
         * @param frame
         *            the owner of the dialog
         */
        public AddDialog(Frame frame) {

            super(frame, "Add attribute", true);
            this.attrName = null;
            this.attrType = null;

            JLabel nameLabel = new JLabel(
                    "Please enter the name (label) of the new " + "attribute:");

            nameTextField = new JTextField();

            JLabel typeLabel = new JLabel(
                    "Please enter or select an attribute class " + "name.\n");

            typeCombo = new JComboBox(types);
            typeCombo.setEditable(true);

            JButton okButton = new JButton("OK");
            // set name and type of this AddDialog if the user pressed OK
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    attrName = nameTextField.getText();
                    if (attrName.equals("")) {
                        JOptionPane.showMessageDialog(null,
                                "Please enter the name of the attribute!",
                                "Error!", JOptionPane.OK_OPTION);
                        return;
                    }
                    attrType = typeCombo.getSelectedItem().toString();
                    if (attrType.equals("")) {
                        JOptionPane
                                .showMessageDialog(
                                        null,
                                        "Please enter the class name of the attribute!",
                                        "Error!", JOptionPane.OK_OPTION);
                        return;
                    }
                    setVisible(false);
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });

            // create and fill panel
            JPanel buttonPanel = new JPanel();

            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new BorderLayout());
            labelPanel.add(nameLabel, BorderLayout.NORTH);
            labelPanel.add(nameTextField, BorderLayout.SOUTH);

            JPanel classPanel = new JPanel();
            classPanel.setLayout(new BorderLayout());
            classPanel.add(typeLabel, BorderLayout.NORTH);
            classPanel.add(typeCombo, BorderLayout.SOUTH);

            getRootPane().setDefaultButton(okButton);
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(labelPanel, BorderLayout.NORTH);
            getContentPane().add(classPanel, BorderLayout.CENTER);
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);

            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }

        /**
         * Returns the type of the new attribute.
         * 
         * @return the type of the new attribute
         */
        public String getAttrType() {
            return this.attrType;
        }

        /**
         * Returns the name of the new attribute.
         * 
         * @return the name of the new attribute
         */
        public String getAttrName() {
            return this.attrName;
        }
    }

    /**
     * This class provides a popup menu after the user right-clicks on a tree
     * node.
     */
    class PopupListener extends MouseAdapter implements ActionListener {

        /** The popup menu with the menu-items "add" and "remove". */
        private JPopupMenu popupMenu;

        /** The add menu item. */
        private JMenuItem addItem;

        /** The add folder menu item. */
        private JMenuItem addFolderItem;

        /** The remove menu item. */
        private JMenuItem removeItem;

        /**
         * Creates a new popup menu and enables/disables the menu items
         * add/remove depending on showAdd/showRemove.
         */
        public PopupListener() {
            popupMenu = new JPopupMenu();
            addItem = new JMenuItem(ADD_TEXT);
            addItem.addActionListener(this);
            popupMenu.add(addItem);
            removeItem = new JMenuItem(REMOVE_TEXT);
            removeItem.addActionListener(this);
            popupMenu.add(removeItem);
            addFolderItem = new JMenuItem(ADD_FOLDER_TEXT);
            addFolderItem.addActionListener(this);
            popupMenu.add(addFolderItem);
        }

        /**
         * Displays the popup menu if the user right-clicks on a tree node.
         * 
         * @param event
         *            the event indicating the mouse action
         */
        @Override
        public void mousePressed(MouseEvent event) {
            String currentSelectionPath = ((AbstractTab) getSelectedComponent())
                    .getCurrentSelectionPath();
            assert currentSelectionPath != null;

            if (event.isPopupTrigger()) {
                Attributable atbl = attributables.get(0);
                if (currentSelectionPath.equals("")) {
                    // add to root
                    addItem.setEnabled(true);
                    removeItem.setEnabled(false);
                    addFolderItem.setEnabled(true);
                } else {
                    // add to some inner node
                    currentSelectionPath = currentSelectionPath
                            .substring(Attribute.SEPARATOR.length());
                    Attribute attribute = atbl
                            .getAttribute(currentSelectionPath);

                    if (attribute instanceof CollectionAttribute) {
                        // enable add menuItem
                        addItem.setEnabled(true);
                        addFolderItem.setEnabled(true);
                    } else {
                        // disable adding subattributes to leaves
                        addItem.setEnabled(false);
                        addFolderItem.setEnabled(false);
                    }

                    // get default paths
                    Attributable attributable = attribute.getAttributable();
                    HashSet<String> defaultPaths = null;
                    if (attributable instanceof Edge) {
                        defaultPaths = ViewTab.getDefaultEdgePaths();
                    } else if (attributable instanceof Node) {
                        defaultPaths = ViewTab.getDefaultNodePaths();
                    } else if (attributable instanceof Graph) {
                        defaultPaths = ViewTab.getDefaultGraphPaths();
                    }

                    if (isDefaultAttribute(attribute, defaultPaths)) {
                        // disable remove menuItem for defaultAttributes
                        removeItem.setEnabled(false);
                    } else {
                        // enable remove menuItem
                        removeItem.setEnabled(true);
                    }
                }
                popupMenu
                        .show(event.getComponent(), event.getX(), event.getY());
            }
        }

        /**
         * @see #mousePressed(MouseEvent)
         * @param event
         *            the event indicating the mouse action
         */
        @Override
        public void mouseReleased(MouseEvent event) {
            mousePressed(event);
        }

        /**
         * Called after the selection of a menu item (add/remove) of the popup
         * menu. Displays a dialog for adding/removing an attribute.
         * 
         * @param event
         *            the event describing the action
         */
        public void actionPerformed(ActionEvent event) {
            JMenuItem source = (JMenuItem) event.getSource();
            if (source.getText().equals(ADD_TEXT)) {
                // display dialog for adding an attribute

                AddDialog addDialog = new AddDialog(null);
                String attrType = addDialog.getAttrType();
                String attrName = addDialog.getAttrName();
                if (attrType != null && attrName != null) {
                    try {
                        // create and add new attribute
                        Attribute newAttr = (Attribute) InstanceLoader
                                .createInstance(attrType, attrName);
                        addAttribute(newAttr);

                    } catch (InstanceCreationException ice) {
                        JOptionPane
                                .showMessageDialog(
                                        null,
                                        "Could not create attribute: invalid class name",
                                        "Error!", JOptionPane.OK_OPTION);
                    }
                }
            } else if (source.getText().equals(REMOVE_TEXT)) {
                // display dialog for removing an attribute
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                        null, "This will remove the attribute.\n"
                                + "Are you sure?", "Remove attribute",
                        JOptionPane.YES_NO_OPTION)) {
                    removeAttribute();
                }
            } else if (source.getText().equals(ADD_FOLDER_TEXT)) {
                // display dialog for adding a new folder
                String name = JOptionPane.showInputDialog(null,
                        "Please enter the name of the new folder:",
                        "Create new folder", JOptionPane.PLAIN_MESSAGE);
                if (name != null) {
                    try {
                        HashMapAttribute newFolder = (HashMapAttribute) InstanceLoader
                                .createInstance(
                                        "org.graffiti.attributes.HashMapAttribute",
                                        name);
                        addAttribute(newFolder);
                    } catch (InstanceCreationException ice) {
                        // should never happen
                        JOptionPane
                                .showMessageDialog(
                                        null,
                                        "Could not create attribute: invalid class name",
                                        "Error!", JOptionPane.OK_OPTION);
                    }
                }
            }
        }

        /**
         * Checks if an attribute or at least one of its children is a default
         * attribute.
         * 
         * @param attribute
         *            the attribute to check
         * @param defaultPaths
         *            the paths of the default attributes
         * @return <code>true</code> if the attribute or an attribute's child is
         *         a default attribute, <code>false</code> if not
         */
        private boolean isDefaultAttribute(Attribute attribute,
                HashSet<String> defaultPaths) {

            String path = attribute.getPath();

            if (defaultPaths.contains(path))
                // attribute is a default attribute
                return true;

            // get children
            Collection<Attribute> attributes;
            if (attribute instanceof CollectionAttribute) {
                attributes = ((CollectionAttribute) attribute).getCollection()
                        .values();
            } else
                // attribute is a leaf and is not a default attribute
                return false;

            for (Attribute attr : attributes)
                if (isDefaultAttribute(attr, defaultPaths))
                    return true;

            return false;
        }
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
