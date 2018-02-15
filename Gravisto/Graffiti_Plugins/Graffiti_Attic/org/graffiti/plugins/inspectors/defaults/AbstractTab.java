// =============================================================================
//
//   AbstractTab.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractTab.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.inspectors.defaults;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.event.AttributeEvent;
import org.graffiti.event.TransactionEvent;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.inspector.InspectorTab;

/**
 * Represents an inspector tab.
 * 
 * @version $Revision: 5772 $
 */
public abstract class AbstractTab extends InspectorTab {
    /**
     * 
     */
    private static final long serialVersionUID = 4583682136272080800L;

    /** The attribute used to display the tree. */
    protected Attribute collAttr;

    /** The root node of the displayed tree. */
    protected DefaultMutableTreeNode rootNode;

    /** Stacks tree above table. */
    protected JSplitPane mainSplit;

    /** The elements that are displayed by this tab. */
    protected List<? extends Attributable> attributables;

    /** DOCUMENT ME! */
    private Dimension defaultDim = new Dimension(Inspector.DEFAULT_WIDTH, 300);

    /** DOCUMENT ME! */
    private Dimension zeroDim = new Dimension(0, 0);

    /** Holds the tree view. */
    private JPanel treePanel;

    /** Scrolls the tree. */
    private JScrollPane treeScroll;

    /** The tree view of the attribute hierarchy. */
    private JTree attributeTree;

    // /** DOCUMENT ME! */
    // private TreeCellRenderer myMultiRenderer;
    // private Attribute newSelectedAttribute = null;
    // /** DOCUMENT ME! */
    // private TreeCellRenderer myRenderer;

    /** DOCUMENT ME! */
    private TreeSelectionListener myTreeSelectionListener = new MyTreeSelectionListener();

    /**
     * Creates a new AbstractTab object.
     */
    public AbstractTab() {
        super();

        // tree
        // myMultiRenderer = new MyMultiRenderer();
        // myRenderer = new DefaultTreeCellRenderer();
        treePanel = new JPanel(new BorderLayout());
        treeScroll = new JScrollPane(treePanel);
        treeScroll.setMinimumSize(zeroDim);
        treeScroll.setPreferredSize(defaultDim);
        treeScroll.setAlignmentX(0f);
        treeScroll.getVerticalScrollBar().setUnitIncrement(10);

        editPanel = new DefaultEditPanel();
        mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treeScroll,
                editPanel);
        mainSplit.setDividerSize(10);
        mainSplit.setDividerLocation(200);
        mainSplit.setMinimumSize(zeroDim);
        mainSplit.setOneTouchExpandable(true);

        this.setLayout(new BorderLayout());
        this.add(mainSplit, BorderLayout.CENTER);

        this.validate();
    }

    /**
     * Creates a new tree displaying the hierarchy contained in the given
     * attribute. It does not compare any attribute with those of any selected
     * graph elements. Used for example to display the root attribute of a graph
     * where no second graph must be taken into account.
     * 
     * @param attr
     *            the attribute to display.
     */
    public void buildTree(Attribute attr) {
        List<Attributable> l = new LinkedList<Attributable>();
        l.add(attr.getAttributable());
        this.buildTree(l);
    }

    /**
     * Shows the hierarchy beginning at the given attribute in a tree.
     * 
     * @param attributables
     *            root attribute for this tree.
     */
    public void buildTree(List<? extends Attributable> attributables) {
        this.attributables = attributables;

        Attribute rootAttr = attributables.get(0).getAttributes();
        this.collAttr = rootAttr;

        // assume every graph element has a root collection attribute
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
                new BooledAttribute(rootAttr, true));
        this.rootNode = root;
        this.attributeTree = new JTree(root);

        fillNode(root, rootAttr, attributables);

        // if(graphElements.size() > 1)
        // {
        // this.attributeTree.setCellRenderer(myMultiRenderer);
        // }
        // else
        // {
        // this.attributeTree.setCellRenderer(myRenderer);
        // }
        // Listen for when the selection changes.
        attributeTree.addTreeSelectionListener(myTreeSelectionListener);

        // if available, expand "graphics" part of tree
        this.attributeTree.expandRow(0);

        TreePath pathToGraphics = this.attributeTree.getNextMatch(
                GraphicAttributeConstants.GRAPHICS, 0,
                javax.swing.text.Position.Bias.Forward);

        if (pathToGraphics != null) {
            // System.out.println("expanding path: " + pathToGraphics);
            // this.attributeTree.expandPath(pathToGraphics);
            this.attributeTree.setSelectionPath(pathToGraphics);
        }

        attributeTree.putClientProperty("JTree.lineStyle", "Angled");

        // attributeTree.setShowsRootHandles(false);
        // attributeTree.setRootVisible(false);
        attributeTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);

        treePanel.add(this.attributeTree);
        treePanel.revalidate();

        // if graphics not found display everything
        if (pathToGraphics == null) {
            this.attributeTree.setSelectionRow(0);
            editPanel.buildTable(root, attributables);
        }
    }

    /**
     * Called after an attribute has been added.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void postAttributeAdded(AttributeEvent e) {
        // System.out.println("rebuilding with " + this.collAttr + "
        // ("+e.getAttribute()+")");
        // TODO: this is just too expensive when many elements are selected;
        // must search through all of them each time (to maybe display "---")
        if (attributables.contains(e.getAttribute().getAttributable())) {
            rebuildTree();
            validate();
        }
    }

    /**
     * Called after an attribute has been changed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void postAttributeChanged(AttributeEvent e) {
        // System.out.println("abstractpanel: postAttributeChanged:
        // updateTable(" + e.getAttribute() + ")");
        // editPanel.updateTable(this.collAttr);
        // editPanel.updateTable(e.getAttribute());
        // TODO: use a faster updateTable method instead
        if (attributables.contains(e.getAttribute().getAttributable())) {
            editPanel.updateTable(e.getAttribute());
        }
    }

    /**
     * Called after an attribute has been removed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void postAttributeRemoved(AttributeEvent e) {
        if (attributables.contains(e.getAttribute().getAttributable())) {
            rebuildTree();
            validate();
        }
    }

    /**
     * Called just before an attribute is added.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void preAttributeAdded(AttributeEvent e) {
    }

    /**
     * Called before a change of an attribute takes place.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void preAttributeChanged(AttributeEvent e) {
    }

    /**
     * Called just before an attribute is removed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void preAttributeRemoved(AttributeEvent e) {
    }

    /**
     * Calls <code>buildTree</code> using the set attribute.
     */
    public void rebuildTree() {
        // save current selection
        String oldMarkedPath = null;

        // Attribute markedAttr = (Attribute)this.attributeTree
        // .getSelectionPath().getLastPathComponent();
        // if (markedAttr != null) {
        // oldMarkedPath = markedAttr.getPath();
        // }
        // save last marked node / path
        DefaultMutableTreeNode treeNode = null;

        if (attributeTree == null) {
            System.err.println("Error: attributeTree is null. (CK)");
            System.err.println("Method rebuildTree is aborted.");

            return;
        }

        TreePath treePath = this.attributeTree.getSelectionPath();

        if (treePath != null) {
            treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        }

        if (treeNode != null) {
            Attribute markedAttr = ((BooledAttribute) treeNode.getUserObject())
                    .getAttribute();

            if (markedAttr != null) {
                oldMarkedPath = markedAttr.getPath();

                // System.out.println("oldMarkedPath = " + oldMarkedPath);
            }
        }

        // start new tree with given attribute at root
        Attribute newAttr = collAttr;

        if (!attributables.isEmpty()) {
            newAttr = ((Attributable) attributables.get(0)).getAttributes();
            this.collAttr = newAttr;
        }

        // assume every graph element has a root collection attribute
        // DefaultMutableTreeNode root =
        // new DefaultMutableTreeNode(new BooledAttribute(newAttr, true));
        // this.rootNode = root;
        // save the attribute that is the root for the current tree
        // this.collAttr = newAttr;
        // new tree
        this.attributeTree.removeTreeSelectionListener(myTreeSelectionListener);
        treePanel.remove(this.attributeTree);

        this.rootNode = new DefaultMutableTreeNode(new BooledAttribute(newAttr,
                true));
        this.attributeTree = new JTree(this.rootNode);
        this.attributeTree.putClientProperty("JTree.lineStyle", "Angled");
        this.attributeTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);

        /*
         * build attribute hierarchy of newAttr starting at root and mark
         * oldMarkedPath
         */
        TreePath selectedTreePath = null;
        DefaultMutableTreeNode selectedNode = fillNode(this.rootNode, newAttr,
                attributables, oldMarkedPath);

        if (selectedNode != null) {
            selectedTreePath = new TreePath(selectedNode.getPath());
        }

        this.attributeTree.addTreeSelectionListener(myTreeSelectionListener);

        // if old marked attr not available, expand "graphics"
        // part of tree (if exists)
        // DefaultMutableTreeNode selectedTreeNode = null;
        if (selectedTreePath == null) {
            this.attributeTree.setSelectionRow(0);
            this.attributeTree.expandRow(0);
            selectedTreePath = this.attributeTree.getNextMatch(
                    GraphicAttributeConstants.GRAPHICS, 0,
                    javax.swing.text.Position.Bias.Forward);

            if (selectedTreePath != null) {
                this.attributeTree.setSelectionPath(selectedTreePath);
                if (this.attributeTree.getLastSelectedPathComponent() != null) {
                    selectedTreePath = new TreePath(
                            ((DefaultMutableTreeNode) this.attributeTree
                                    .getLastSelectedPathComponent()).getPath());
                }
            }
        } else {
            this.attributeTree.setSelectionPath(selectedTreePath);

            //
            // this.attributeTree.expandPath(selectedTreePath.getParentPath());
            //
            // // TODO: remove next lines:
            // TreePath path = selectedTreePath;
            // TreePath[] paths = new TreePath[selectedTreePath.getPathCount()];
            // int i = 0;
            // while (path != null) {
            // paths[i] = path;
            // path = path.getParentPath();
            // i++;
            // }
            // this.attributeTree.expandRow(0);
            // for (int k = i-1; k >= 0; k--) {
            // this.attributeTree.expandPath(paths[k]);
            // }
            //
            // //
            // this.attributeTree.collapsePath(selectedTreePath.getParentPath());
            // this.attributeTree.makeVisible(selectedTreePath);
            this.attributeTree.scrollPathToVisible(selectedTreePath);

            // // this.attributeTree.setLeadSelectionPath(selectedTreePath);
        }

        treePanel.add(this.attributeTree);
        this.attributeTree.makeVisible(selectedTreePath);

        // (mark as did last selection; do this before the listener is added)
        // System.out.println("selecting = " + selectedTreePath);
        // this.attributeTree.setExpandsSelectedPaths(true);
        // this.attributeTree.setSelectionPath(selectedTreePath);
        treePanel.validate();

        // JFrame test = new JFrame("Test");
        // this.attributeTree.setSelectionPath(selectedTreePath);
        // this.attributeTree.collapsePath(selectedTreePath);
        // this.attributeTree.makeVisible(selectedTreePath);
        // this.attributeTree.scrollPathToVisible(selectedTreePath);
        // this.attributeTree.setLeadSelectionPath(selectedTreePath);
        // this.attributeTree.expandPath(selectedTreePath);
        //
        // this.attributeTree.expandRow(this.attributeTree.getRowForPath(selectedTreePath.getParentPath()));
        //
        // test.getContentPane().add(this.attributeTree);
        // test.pack();
        // test.setVisible(true);
        //
        // ////////
        // this.attributeTree.addTreeSelectionListener(myTreeSelectionListener);
        // ////////
        // //////// // TODO: check if could be done faster
        // //////// // editPanel.buildTable(newAttr);
        // ////////
        // //////// if (selectedTreePath == null) {
        // //////// //System.out.println("updating table with " +
        // newAttr.getPath());
        // //////// editPanel.buildTable(root, attributables);
        // //////// } else {
        // //////// //System.out.println("updating table with " +
        // newSelectedAttribute.getPath());
        // ////////
        // editPanel.buildTable((DefaultMutableTreeNode)selectedTreePath.
        // //////// getLastPathComponent(), attributables);
        // //////// }
        // newSelectedAttribute = null;
        // try {
        // Attribute subAttr =
        // ((CollectionAttribute)newAttr).getAttribute(oldMarkedPath);
        // editPanel.buildTable(subAttr);
        // } catch (ClassCastException cce) {
        // editPanel.buildTable(newAttr);
        // } catch (AttributeNotFoundException anfe) {
        // editPanel.buildTable(newAttr);
        // }
        // updateTable would be much faster. The problem is that one can't know
        // or see
        // easily that / if the new GraphicAttribute has some new or some less
        // sub attributes like bends.....
        // editPanel.updateTable(newAttr);
        // if (oldMarkedPath != null) {
        // // had a selection
        // // if (oldMarkedPath.equals(Attribute.SEPARATOR +
        // // GraphicAttributeConstants.GRAPHICS)) {
        // // // selection was graphics root, don't bother any longer
        // // // just rebuild
        // // editPanel.buildTable(newAttr);
        // // } else {
        //
        // //// TODO: update this (not only graphic attributes are displayed)
        // // remove ".graphics" from path
        // // oldMarkedPath = oldMarkedPath.substring
        // // (oldMarkedPath.indexOf(Attribute.SEPARATOR, 2)+1,
        // // oldMarkedPath.length());
        // try {
        // // see if we have such a sub attribute
        // Attribute newMarkedAttribute =
        // ((CollectionAttribute)newAttr)
        // .getAttribute(oldMarkedPath);
        //
        // // rebuild with last marked attribute only
        // editPanel.buildTable(newMarkedAttribute);
        // } catch (AttributeNotFoundException anfe) {
        // // new selection has no such attribute, have to rebuild
        // //// TODO: check if build table with graphics only as standard
        // (applies to next 3):
        // editPanel.buildTable(newAttr);
        // } catch (ClassCastException cce) {
        // // did not get a CollectionAttribute, just rebuild then
        // editPanel.buildTable(newAttr);
        // }
        // ////// }
        // } else {
        // // had no selection, have to rebuild all
        // editPanel.buildTable(newAttr);
        // }
        //
        // // TreePath pathToGraphics = attributeTree.getNextMatch
        // // (GraphicAttributeConstants.GRAPHICS, 0,
        // // javax.swing.text.Position.Bias.Forward);
        // // if (pathToGraphics != null) {
        // // System.out.println("expanding path: " + pathToGraphics);
        // // attributeTree.expandPath(pathToGraphics);
        // // }
        //
        // // attributeTree.putClientProperty("JTree.lineStyle", "Angled");
        // // attributeTree.setShowsRootHandles(false);
        // // attributeTree.setRootVisible(false);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void transactionFinished(TransactionEvent e) {
        // TODO: maybe see if event was fired by applyButton; could then react
        // by calling updateTable or sth like that?!
        rebuildTree();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void transactionStarted(TransactionEvent e) {
    }

    /**
     * DOCUMENT ME!
     * 
     * @param attributables
     *            DOCUMENT ME!
     */
    protected void rebuildTree(List<? extends Attributable> attributables) {
        this.attributables = attributables;
        this.rebuildTree();
    }

    /**
     * Builds a tree of the hierarchy starting at attr and appends it to
     * treeNode.
     * 
     * @param treeNode
     *            DOCUMENT ME!
     * @param attr
     *            DOCUMENT ME!
     * @param graphElements
     *            DOCUMENT ME!
     */
    private void fillNode(DefaultMutableTreeNode treeNode, Attribute attr,
            List<? extends Attributable> graphElements) {
        this.fillNode(treeNode, attr, graphElements, null);
    }

    // /**
    // * Builds a tree of the hierarchy starting at attr and appends it to
    // * treeNode.
    // */
    // private void fillNode(DefaultMutableTreeNode treeNode, Attribute attr,
    // List attributables) {
    //
    // DefaultMutableTreeNode newNode;
    // if (attr instanceof CollectionAttribute) {
    // boolean allHave = true;
    // boolean allSameValue = true;
    // Collection attrs =
    // ((CollectionAttribute) attr).getCollection().values();
    // for (Iterator it = attrs.iterator(); it.hasNext();) {
    // Attribute attribute = (Attribute) it.next();
    // Object attributeValue = attribute.getValue();
    //                
    // // check if present in all graph elements
    // if (attributables.size() > 1) {
    // for (Iterator geit = attributables.iterator(); geit.hasNext();) {
    // try {
    // Attribute oAttr = ((GraphElement)geit.next()).
    // getAttribute(attribute.getPath().substring(1));
    // if (!attributeValue.equals(oAttr.getValue())) {
    // allSameValue = false;
    // break;
    // }
    //		                	
    // } catch (AttributeNotFoundException anfe) {
    // // found graph element that has no such attribute
    // allHave = false;
    // break;
    // }
    // }
    // }
    //                
    // if (allHave) {
    // newNode = new DefaultMutableTreeNode
    // (new BooledAttribute(attribute, allSameValue));
    //
    // fillNode(newNode, attribute, attributables);
    //
    // treeNode.add(newNode);
    // }
    // }
    // }
    // }

    /**
     * Same as <code>fillNode(DefaultMutableTreeNode treeNode, Attribute attr)
     * </code> but additionally selects the given attribute at
     * markedPath.
     * 
     * @param treeNode
     *            DOCUMENT ME!
     * @param attr
     *            DOCUMENT ME!
     * @param attributables
     *            DOCUMENT ME!
     * @param markedPath
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private DefaultMutableTreeNode fillNode(DefaultMutableTreeNode treeNode,
            Attribute attr, List<? extends Attributable> attributables,
            String markedPath) {
        DefaultMutableTreeNode returnTreeNode = null;
        DefaultMutableTreeNode newNode;

        boolean allHave = true;
        boolean allSameValue = true;
        Collection<Attribute> attrs;

        if (attr instanceof CollectionAttribute) {
            attrs = ((CollectionAttribute) attr).getCollection().values();
        } else if (attr instanceof CompositeAttribute) {
            attrs = new LinkedList<Attribute>();

            try {
                attrs.add(((CompositeAttribute) attr).getAttributes());
            } catch (RuntimeException e) {
                Object attributeValue = attr.getValue();

                // check if present in all graph elements
                if (attributables.size() > 1) {
                    for (Attributable a : attributables) {
                        try {
                            Attribute oAttr = a.getAttribute(attr.getPath()
                                    .substring(1));

                            if (!attributeValue.equals(oAttr.getValue())) {
                                allSameValue = false;

                                break;
                            }
                        } catch (AttributeNotFoundException anfe) {
                            // found graph element that has no such attribute
                            allHave = false;

                            break;
                        }
                    }
                }

                if (allHave) {
                    newNode = new DefaultMutableTreeNode(new BooledAttribute(
                            attr, allSameValue));

                    // treeNode.add(newNode);
                    if (attr.getPath().equals(markedPath)) {
                        // returnTreePath = new TreePath(newNode.getPath());
                        returnTreeNode = newNode;
                    }
                }

                return returnTreeNode;
            }
        } else {
            allHave = true;
            allSameValue = true;

            Attribute attribute = attr;
            Object attributeValue = attribute.getValue();

            // check if present in all graph elements
            if (attributables.size() > 1) {
                for (Attributable a : attributables) {
                    try {
                        Attribute oAttr = a.getAttribute(attribute.getPath()
                                .substring(1));

                        if (!attributeValue.equals(oAttr.getValue())) {
                            allSameValue = false;

                            break;
                        }
                    } catch (AttributeNotFoundException anfe) {
                        // found graph element that has no such attribute
                        allHave = false;

                        break;
                    }
                }
            }

            if (allHave) {
                newNode = new DefaultMutableTreeNode(new BooledAttribute(
                        attribute, allSameValue));

                // treeNode.add(newNode);
                // if (attribute.getPath().equals(markedPath)) {
                // returnTreePath = new TreePath(newNode.getPath());
                // }
            }

            return returnTreeNode;
        }

        for (Attribute attribute : attrs) {
            allHave = true;
            allSameValue = true;

            Object attributeValue = attribute.getValue();

            // check if present in all graph elements
            if (attributables.size() > 1) {
                for (Attributable a : attributables) {
                    try {
                        Attribute oAttr = a.getAttribute(attribute.getPath()
                                .substring(1));

                        if (allSameValue
                                && !attributeValue.equals(oAttr.getValue())) {
                            allSameValue = false;

                            // break;
                        }
                    } catch (AttributeNotFoundException anfe) {
                        // found graph element that has no such attribute
                        allHave = false;
                        break;
                    } catch (NullPointerException e) {
                        // found graph element that has no such attribute
                        allHave = false;
                        break;
                    }
                }
            }

            if (allHave) {
                newNode = new DefaultMutableTreeNode(new BooledAttribute(
                        attribute, allSameValue));

                if (returnTreeNode == null) {
                    returnTreeNode = fillNode(newNode, attribute,
                            attributables, markedPath);
                } else {
                    fillNode(newNode, attribute, attributables, null);
                }

                treeNode.add(newNode);

                if (attribute.getPath().equals(markedPath)) {
                    returnTreeNode = newNode;
                }
            }
        }

        // if (attr.getPath().equals(markedPath)) {
        // returnTreePath = new TreePath(treeNode.getPath());
        // }
        return returnTreeNode;
    }

    // /**
    // * DOCUMENT ME!
    // *
    // * @author $Author: gleissner $
    // * @version $Revision: 5772 $ $Date: 2006-01-05 17:11:23 +0100 (Do, 05 Jan
    // 2006) $
    // */
    // class MyMultiRenderer
    // extends DefaultTreeCellRenderer
    // {
    // /**
    // * @see javax.swing.tree.DefaultTreeCellRenderer#
    // * getTreeCellRendererComponent(javax.swing.JTree,
    // * java.lang.Object, boolean, boolean, boolean, int, boolean)
    // */
    // public Component getTreeCellRendererComponent(JTree tree, Object value,
    // boolean sel, boolean expanded, boolean leaf, int row,
    // boolean hasFocus)
    // {
    // super.getTreeCellRendererComponent(tree, value, sel, expanded,
    // leaf, row, hasFocus);
    //
    // setLayout(new BorderLayout());
    //
    // JCheckBox check = new JCheckBox();
    // check.setSelected(((BooledAttribute) ((DefaultMutableTreeNode)
    // value).getUserObject()).getBool());
    //
    // add(check, BorderLayout.WEST);
    //
    // // validate();
    // return this;
    // }
    // }

    /**
     * Implements valueChanged method that updates the table according to the
     * selection in the tree.
     */
    class MyTreeSelectionListener implements TreeSelectionListener {
        /**
         * DOCUMENT ME!
         * 
         * @param e
         *            DOCUMENT ME!
         */
        public void valueChanged(TreeSelectionEvent e) {
            TreePath treePath = e.getNewLeadSelectionPath();

            if (treePath != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath
                        .getLastPathComponent();

                if (node == null)
                    return;

                // Attribute attr = (Attribute)node.getUserObject();
                editPanel.buildTable(node, attributables);

                // repaint();
                // collAttr = attr;
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
