// =============================================================================
//
//   OptionsDialog.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: OptionsDialog.java 5778 2010-05-10 14:15:18Z gleissner $

package org.graffiti.options;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.graffiti.core.Bundle;

/**
 * Represents the options dialog.
 * 
 * @version $Revision: 5778 $
 */
public class OptionsDialog extends JDialog implements ActionListener,
        TreeSelectionListener {

    /**
     * 
     */
    private static final long serialVersionUID = -3954631210255742589L;

    /** The <code>Bundle</code> of this options dialog. */
    private static final Bundle bundle = Bundle.getCoreBundle();

    /** DOCUMENT ME! */
    private JButton apply;

    /** DOCUMENT ME! */
    private JButton cancel;

    /** DOCUMENT ME! */
    private JButton ok;

    /** DOCUMENT ME! */
    private JLabel currentLabel;

    /** DOCUMENT ME! */
    private JPanel cardPanel;

    /** DOCUMENT ME! */
    private JTree paneTree;

    /** DOCUMENT ME! */
    private OptionGroup pluginsGroup;

    /**
     * Constructor for OptionsDialog.
     * 
     * @param parent
     *            the parent of this dialog
     * 
     * @throws HeadlessException
     *             DOCUMENT ME!
     */
    public OptionsDialog(Frame parent) throws HeadlessException {
        super(parent, bundle.getString("options.dialog.title"), true);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(content);

        content.setLayout(new BorderLayout());

        JPanel stage = new JPanel(new BorderLayout());
        stage.setBorder(new EmptyBorder(0, 6, 0, 0));
        content.add(stage, BorderLayout.CENTER);

        /*
         * currentLabel displays the path of the currently selected OptionPane
         * at the top of the stage area
         */
        currentLabel = new JLabel();
        currentLabel.setHorizontalAlignment(SwingConstants.LEFT);
        currentLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                Color.black));
        stage.add(currentLabel, BorderLayout.NORTH);

        cardPanel = new JPanel(new CardLayout());
        cardPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        stage.add(cardPanel, BorderLayout.CENTER);

        paneTree = new JTree(createOptionTreeModel());

        paneTree.setCellRenderer(new PaneNameRenderer());
        paneTree.putClientProperty("JTree.lineStyle", "Angled");
        paneTree.setShowsRootHandles(true);
        paneTree.setRootVisible(false);
        content.add(new JScrollPane(paneTree,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                BorderLayout.WEST);

        JPanel buttons = new JPanel();
        buttons.setBorder(new EmptyBorder(12, 0, 0, 0));
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(Box.createGlue());

        ok = new JButton(bundle.getString("common.ok"));
        ok.addActionListener(this);
        ok.setIcon(bundle.getIcon("icon.common.ok"));
        buttons.add(ok);
        buttons.add(Box.createHorizontalStrut(6));
        getRootPane().setDefaultButton(ok);
        apply = new JButton(bundle.getString("common.apply"));
        apply.addActionListener(this);
        apply.setIcon(bundle.getIcon("icon.common.apply"));
        buttons.add(apply);
        buttons.add(Box.createHorizontalStrut(6));
        cancel = new JButton(bundle.getString("common.cancel"));
        cancel.addActionListener(this);
        cancel.setIcon(bundle.getIcon("icon.common.cancel"));
        buttons.add(cancel);

        buttons.add(Box.createGlue());

        content.add(buttons, BorderLayout.SOUTH);

        // register the Options dialog as a TreeSelectionListener.
        // this is done before the initial selection to ensure that the
        // first selected OptionPane is displayed on startup.
        paneTree.getSelectionModel().addTreeSelectionListener(this);

        // paneTree.expandPath(new TreePath( TODO
        // new Object[] { paneTree.getModel().getRoot(), editGroup }));
        paneTree.setSelectionRow(0);

        // parent.hideWaitCursor(); TODO
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * Called, if a button in the dialog is pressed.
     * 
     * @param e
     *            the action event.
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == ok) {
            ok();
        } else if (src == cancel) {
            cancel();
        } else if (src == apply) {
            ok(false);
        }
    }

    /**
     * Adds the given option group to the list of option groups.
     * 
     * @param group
     *            the option group to add.
     */
    public void addOptionGroup(OptionGroup group) {
        addOptionGroup(group, pluginsGroup);
    }

    /**
     * Adds the given option pane to the list of option panes.
     * 
     * @param pane
     *            the option pane to add to the list.
     */
    public void addOptionPane(OptionPane pane) {
        addOptionPane(pane, pluginsGroup);
    }

    /**
     * Handles the &quot;cancel&quot; button.
     */
    public void cancel() {
        dispose();
    }

    /**
     * Handles the &quot;ok%quot; button.
     */
    public void ok() {
        ok(true);
    }

    /**
     * Handles the &quot;ok&quot;- and &quot;apply&quot;-buttons.
     * 
     * @param dispose
     *            DOCUMENT ME!
     */
    public void ok(boolean dispose) {
        OptionTreeModel m = (OptionTreeModel) paneTree.getModel();
        ((OptionGroup) m.getRoot()).save();

        /* This will fire the PROPERTIES_CHANGED event */

        // editor.propertiesChanged(); TODO
        // Save settings to disk
        // editor.prefs.sync(); TODO
        // get rid of this dialog if necessary
        if (dispose) {
            dispose();
        }
    }

    /**
     * Called, iff a value in the tree was selected.
     * 
     * @param e
     *            the tree selection event.
     */
    public void valueChanged(TreeSelectionEvent e) {
        TreePath path = e.getPath();

        if ((path == null)
                || !(path.getLastPathComponent() instanceof OptionPane))
            return;

        Object[] nodes = path.getPath();

        StringBuffer buf = new StringBuffer();

        OptionPane optionPane = null;
        String name = null;

        int lastIdx = nodes.length - 1;

        for (int i = paneTree.isRootVisible() ? 0 : 1; i <= lastIdx; i++) {
            if (nodes[i] instanceof OptionPane) {
                optionPane = (OptionPane) nodes[i];
                name = optionPane.getName();
            } else if (nodes[i] instanceof OptionGroup) {
                name = ((OptionGroup) nodes[i]).getName();
            } else {
                continue;
            }

            if (name != null) {
                String label = bundle.getString("options." + name + ".label");

                if (label == null) {
                    buf.append(name);
                } else {
                    buf.append(label);
                }
            }

            if (i != lastIdx) {
                buf.append(": ");
            }
        }

        currentLabel.setText(buf.toString());

        optionPane.init();

        pack();

        ((CardLayout) cardPanel.getLayout()).show(cardPanel, name);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param child
     *            DOCUMENT ME!
     * @param parent
     *            DOCUMENT ME!
     */
    private void addOptionGroup(OptionGroup child, OptionGroup parent) {
        Enumeration<Object> members = child.getMembers();

        while (members.hasMoreElements()) {
            Object elem = members.nextElement();

            if (elem instanceof OptionPane) {
                addOptionPane((OptionPane) elem, child);
            } else if (elem instanceof OptionGroup) {
                addOptionGroup((OptionGroup) elem, child);
            }
        }

        parent.addOptionGroup(child);
    }

    /**
     * Adds the given option pane to the tree of option panes.
     * 
     * @param pane
     *            the pane to add to the tree
     * @param parent
     *            the parent option group of the give option pane.
     */
    private void addOptionPane(OptionPane pane, OptionGroup parent) {
        String name = pane.getName();
        cardPanel.add(pane.getComponent(), name);
        parent.addOptionPane(pane);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private OptionTreeModel createOptionTreeModel() {
        OptionTreeModel paneTreeModel = new OptionTreeModel();
        OptionGroup rootGroup = (OptionGroup) paneTreeModel.getRoot();

        addOptionPane(new OverviewOptionPane(), rootGroup);

        // initialize the jEdit branch of the options tree
        // editGroup = new OptionGroup("editor");
        // addOptionPane(new LoadSaveOptionPane(), editGroup);

        /* add other default option panes above this line. */

        // OptionGroup browserGroup = new OptionGroup("browser");
        // addOptionPane(new BrowserOptionPane(), browserGroup);
        // addOptionPane(new BrowserColorsOptionPane(), browserGroup);
        // addOptionGroup(browserGroup, editGroup);
        // addOptionGroup(editGroup, rootGroup);
        // initialize the Plugins branch of the options tree
        // pluginsGroup = new OptionGroup("plugins");
        // Query plugins for option panes
        // GenericPlugin[] plugins = editor.pluginManager.getPlugins();
        // for(int i = 0; i < plugins.length; i++) {
        // GenericPlugin ep = plugins[i];
        // try {
        // ep.createOptionPanes(this);
        // } catch(Throwable t){
        // Log.log(Log.ERROR, ep,
        // "Error creating option pane");
        // Log.log(Log.ERROR, ep, t);
        // }
        // }
        // only add the Plugins branch if there are OptionPanes
        // if (pluginsGroup.getMemberCount() > 0) {
        // addOptionGroup(pluginsGroup, rootGroup);
        // }
        return paneTreeModel;
    }

    /**
     * Represents the tree of option panes.
     * 
     * @version $Revision: 5778 $
     */
    class OptionTreeModel implements TreeModel {
        /** The list of event listeners. */
        private EventListenerList listenerList = new EventListenerList();

        /** The root node. */
        private OptionGroup root = new OptionGroup(null);

        /**
         * Returns the child of parent at index index.
         * 
         * @param parent
         *            DOCUMENT ME!
         * @param index
         *            DOCUMENT ME!
         * 
         * @return DOCUMENT ME!
         */
        public Object getChild(Object parent, int index) {
            if (parent instanceof OptionGroup)
                return ((OptionGroup) parent).getMember(index);
            else
                return null;
        }

        /**
         * Returns the number of childs of the given parent.
         * 
         * @param parent
         *            DOCUMENT ME!
         * 
         * @return DOCUMENT ME!
         */
        public int getChildCount(Object parent) {
            if (parent instanceof OptionGroup)
                return ((OptionGroup) parent).getMemberCount();
            else
                return 0;
        }

        /**
         * Returns the index of the given child.
         * 
         * @param parent
         *            DOCUMENT ME!
         * @param child
         *            DOCUMENT ME!
         * 
         * @return DOCUMENT ME!
         */
        public int getIndexOfChild(Object parent, Object child) {
            if (parent instanceof OptionGroup)
                return ((OptionGroup) parent).getMemberIndex(child);
            else
                return -1;
        }

        /**
         * Returns <code>true</code>, iff the specified node is a leaf. Leafs
         * are option panes.
         * 
         * @param node
         *            DOCUMENT ME!
         * 
         * @return DOCUMENT ME!
         */
        public boolean isLeaf(Object node) {
            return node instanceof OptionPane;
        }

        /**
         * Returns the root node.
         * 
         * @return DOCUMENT ME!
         */
        public Object getRoot() {
            return root;
        }

        /**
         * Adds the given tree model listener.
         * 
         * @param l
         *            DOCUMENT ME!
         */
        public void addTreeModelListener(TreeModelListener l) {
            listenerList.add(TreeModelListener.class, l);
        }

        /**
         * Removes the given tree model listener.
         * 
         * @param l
         *            DOCUMENT ME!
         */
        public void removeTreeModelListener(TreeModelListener l) {
            listenerList.remove(TreeModelListener.class, l);
        }

        /**
         * DOCUMENT ME!
         * 
         * @param path
         *            DOCUMENT ME!
         * @param newValue
         *            DOCUMENT ME!
         */
        public void valueForPathChanged(TreePath path, Object newValue) {
            /* this model may not be changed by the TableCellEditor */
        }

        /**
         * Called, if a number of nodes changed their state.
         * 
         * @param source
         *            DOCUMENT ME!
         * @param path
         *            DOCUMENT ME!
         * @param childIndices
         *            DOCUMENT ME!
         * @param children
         *            DOCUMENT ME!
         */
        protected void fireNodesChanged(Object source, Object[] path,
                int[] childIndices, Object[] children) {
            Object[] listeners = listenerList.getListenerList();

            TreeModelEvent modelEvent = null;

            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] != TreeModelListener.class) {
                    continue;
                }

                if (modelEvent == null) {
                    modelEvent = new TreeModelEvent(source, path, childIndices,
                            children);
                }

                ((TreeModelListener) listeners[i + 1])
                        .treeNodesChanged(modelEvent);
            }
        }

        /**
         * Called, iff some nodes are inserted in the tree model.
         * 
         * @param source
         *            DOCUMENT ME!
         * @param path
         *            DOCUMENT ME!
         * @param childIndices
         *            DOCUMENT ME!
         * @param children
         *            DOCUMENT ME!
         */
        protected void fireNodesInserted(Object source, Object[] path,
                int[] childIndices, Object[] children) {
            Object[] listeners = listenerList.getListenerList();

            TreeModelEvent modelEvent = null;

            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] != TreeModelListener.class) {
                    continue;
                }

                if (modelEvent == null) {
                    modelEvent = new TreeModelEvent(source, path, childIndices,
                            children);
                }

                ((TreeModelListener) listeners[i + 1])
                        .treeNodesInserted(modelEvent);
            }
        }

        /**
         * Called, iff some nodes are removed from the tree model.
         * 
         * @param source
         *            DOCUMENT ME!
         * @param path
         *            DOCUMENT ME!
         * @param childIndices
         *            DOCUMENT ME!
         * @param children
         *            DOCUMENT ME!
         */
        protected void fireNodesRemoved(Object source, Object[] path,
                int[] childIndices, Object[] children) {
            Object[] listeners = listenerList.getListenerList();

            TreeModelEvent modelEvent = null;

            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] != TreeModelListener.class) {
                    continue;
                }

                if (modelEvent == null) {
                    modelEvent = new TreeModelEvent(source, path, childIndices,
                            children);
                }

                ((TreeModelListener) listeners[i + 1])
                        .treeNodesRemoved(modelEvent);
            }
        }

        /**
         * Called, iff the tree structure changed.
         * 
         * @param source
         *            DOCUMENT ME!
         * @param path
         *            DOCUMENT ME!
         * @param childIndices
         *            DOCUMENT ME!
         * @param children
         *            DOCUMENT ME!
         */
        protected void fireTreeStructureChanged(Object source, Object[] path,
                int[] childIndices, Object[] children) {
            Object[] listeners = listenerList.getListenerList();

            TreeModelEvent modelEvent = null;

            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] != TreeModelListener.class) {
                    continue;
                }

                if (modelEvent == null) {
                    modelEvent = new TreeModelEvent(source, path, childIndices,
                            children);
                }

                ((TreeModelListener) listeners[i + 1])
                        .treeStructureChanged(modelEvent);
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @author $Author: gleissner $
     * @version $Revision: 5778 $ $Date: 2009-10-23 13:07:21 +0200 (Fr, 23 Okt
     *          2009) $
     */
    class PaneNameRenderer extends DefaultTreeCellRenderer {
        /**
         * 
         */
        private static final long serialVersionUID = 2292349156565417208L;

        /** DOCUMENT ME! */
        private Font groupFont;

        /** DOCUMENT ME! */
        private Font paneFont;

        /**
         * Creates a new PaneNameRenderer object.
         */
        public PaneNameRenderer() {
            paneFont = UIManager.getFont("Tree.font");
            groupFont = paneFont.deriveFont(Font.BOLD);
        }

        /**
         * DOCUMENT ME!
         * 
         * @param tree
         *            DOCUMENT ME!
         * @param value
         *            DOCUMENT ME!
         * @param selected
         *            DOCUMENT ME!
         * @param expanded
         *            DOCUMENT ME!
         * @param leaf
         *            DOCUMENT ME!
         * @param row
         *            DOCUMENT ME!
         * @param hasFocus
         *            DOCUMENT ME!
         * 
         * @return DOCUMENT ME!
         */
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded,
                    leaf, row, hasFocus);

            String name = null;

            if (value instanceof OptionGroup) {
                name = ((OptionGroup) value).getName();
                this.setFont(groupFont);
            } else if (value instanceof OptionPane) {
                name = ((OptionPane) value).getName();
                this.setFont(paneFont);
            }

            if (name == null) {
                setText(null);
            } else {
                String label = bundle.getString("options." + name + ".label");

                if (label == null) {
                    setText(name);
                } else {
                    setText(label);
                }
            }

            setIcon(null);

            return this;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
