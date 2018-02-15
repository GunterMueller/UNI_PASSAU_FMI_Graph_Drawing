package org.graffiti.plugins.tools.stylemanager;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.core.Bundle;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.actions.EditRedoAction;
import org.graffiti.editor.actions.EditUndoAction;
import org.graffiti.editor.actions.PasteAction;
import org.graffiti.event.GraphEvent;
import org.graffiti.event.GraphListener;
import org.graffiti.event.ListenerNotFoundException;
import org.graffiti.event.TransactionEvent;
import org.graffiti.graph.Edge;
import org.graffiti.graph.FastEdge;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.FastNode;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.gui.GraffitiToolbar;
import org.graffiti.plugins.undo.NamedCompoundEdit;
import org.graffiti.plugins.views.fast.actions.FinishEdgeCreation;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionEvent;
import org.graffiti.selection.SelectionListener;
import org.graffiti.selection.SelectionModel;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;
import org.graffiti.undo.AttributeEdit;
import org.graffiti.undo.ChangeAttributesEdit;

public class StyleManager extends GraffitiToolbar implements SelectionListener,
        SessionListener, GraphListener {
    /**
     * 
     */
    private static final long serialVersionUID = 2343133759714108069L;

    private static String DEFAULT_STYLE = "DEFAULT_STYLE";

    private static char STYLE_PART_DELIMITER = '.';

    // see JavaDoc on class Preferences for the reason...
    private static int MAX_PREF_LENGTH = 3 * Preferences.MAX_VALUE_LENGTH / 4;

    private Graph graph = null;

    private List<Node> currentNodes = null;

    private List<Edge> currentEdges = null;

    private JComboBox nodesCombo;

    private JComboBox edgesCombo;

    private JButton applyButton, addStyleButton;

    private SelectionModel selectionModel = null;

    private Map<String, NodeStyle> nodeStyles;

    private Map<String, EdgeStyle> edgeStyles;

    private Preferences nodePrefs = Preferences.userNodeForPackage(getClass())
            .node(NodeStyle.class.getSimpleName());

    private Preferences edgePrefs = Preferences.userNodeForPackage(getClass())
            .node(EdgeStyle.class.getSimpleName());

    private NodeStyle defaultNodeStyle;

    private EdgeStyle defaultEdgeStyle;

    private int ignoredTransactions = 0;

    private List<GraphEvent> delayedEdgeEvents = new LinkedList<GraphEvent>();

    private int delayingTransactions = 0;

    private boolean skipDefaultPrefs = false;

    static final Bundle resourceBundle = Bundle.getBundle(StyleManager.class);

    public StyleManager() {
        super("StyleManager");

        nodesCombo = new JComboBox();
        edgesCombo = new JComboBox();
        nodesCombo.setRenderer(new ElementStyleRenderer());
        edgesCombo.setRenderer(new ElementStyleRenderer());
        applyButton = new JButton(resourceBundle.getString("button.apply"));
        applyButton.setEnabled(false);
        addStyleButton = new JButton(resourceBundle
                .getString("button.add_style"));
        addStyleButton.setEnabled(false);

        addActionListeners();
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(), "StyleManager"));

        FlowLayout layout = new FlowLayout();
        layout.setVgap(0);
        container.setLayout(layout);

        container.add(nodesCombo);
        container.add(edgesCombo);
        container.add(applyButton);
        container.add(addStyleButton);

        add(container);

        nodeStyles = new HashMap<String, NodeStyle>();
        edgeStyles = new HashMap<String, EdgeStyle>();

        resetStyles(GraffitiSingleton.getInstance().getMainFrame()
                .getActiveSession());
        // SpringUtilities.makeCompactGrid(this, 1, 4, 0, 0, 0, 0);
    }

    private void addActionListeners() {
        addStyleButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (currentNodes.size() + currentEdges.size() != 1)
                    return;

                String styleName;
                String message = resourceBundle.getString("message.stylename");
                boolean addNode = !currentNodes.isEmpty();

                do {
                    styleName = (String) JOptionPane.showInputDialog(
                            GraffitiSingleton.getInstance().getMainFrame(),
                            message, "StyleManager", JOptionPane.PLAIN_MESSAGE,
                            null, null, "");
                    if (styleName != null) {
                        message = resourceBundle.getString(
                                "message.stylename_exists").replace("%s",
                                styleName);
                    }
                } while (styleName != null
                        && (addNode && nodeStyles.keySet().contains(styleName) || !addNode
                                && edgeStyles.keySet().contains(styleName)));

                if (styleName == null)
                    return;

                if (addNode) {
                    addNodeStyle(styleName, currentNodes.get(0));
                } else {
                    addEdgeStyle(styleName, currentEdges.get(0));
                }
            }

        });

        applyButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Object selectedItem = nodesCombo.getSelectedItem();

                if (graph != null) {
                    graph.getListenerManager().transactionStarted(this);
                }

                if (currentNodes != null && selectedItem != null) {

                    NodeStyle nodeStyle = (NodeStyle) selectedItem;

                    if (nodeStyle != null) {
                        Map<Attribute, Object> oldValuesMap = new HashMap<Attribute, Object>();
                        List<Attribute> newAttributes = new LinkedList<Attribute>();
                        
                        for (Node node : currentNodes) {
                            nodeStyle.apply(node.getAttributes(), oldValuesMap, newAttributes);
                        }
                        
                        postUndoInfo(nodeStyle.getStyleName(), oldValuesMap, newAttributes);
                    }
                }

                selectedItem = edgesCombo.getSelectedItem();

                if (currentEdges != null && selectedItem != null) {

                    EdgeStyle edgeStyle = (EdgeStyle) selectedItem;

                    if (edgeStyle != null) {
                        
                        Map<Attribute, Object> oldValuesMap = new HashMap<Attribute, Object>();
                        List<Attribute> newAttributes = new LinkedList<Attribute>();
                        
                        for (Edge edge : currentEdges) {
                            edgeStyle.apply(edge.getAttributes(), oldValuesMap, newAttributes);
                        }
                        
                        postUndoInfo(edgeStyle.getStyleName(), oldValuesMap, newAttributes);
                    }
                }
                if (graph != null) {
                    graph.getListenerManager().transactionFinished(this);
                }
            }

        });

        nodesCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {

                ElementStyle selectedItem = (ElementStyle) nodesCombo
                        .getSelectedItem();
                
                applyButton.setEnabled(currentNodes != null
                        && selectedItem instanceof NodeStyle
                        && !currentNodes.isEmpty() || currentEdges != null
                        && edgesCombo.getSelectedItem() instanceof EdgeStyle
                        && !currentEdges.isEmpty());
                
                if (skipDefaultPrefs)
                    return;

                if (selectedItem != null) {
                    nodePrefs.put(DEFAULT_STYLE, selectedItem.getStyleName());
                    defaultNodeStyle = (NodeStyle) selectedItem;
                } else {
                    nodePrefs.put(DEFAULT_STYLE, "");
                    defaultNodeStyle = null;
                }
            }

        });

        edgesCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {

                ElementStyle selectedItem = (ElementStyle) edgesCombo
                        .getSelectedItem();

                applyButton.setEnabled(currentNodes != null
                        && nodesCombo.getSelectedItem() instanceof NodeStyle
                        && !currentNodes.isEmpty() || currentEdges != null
                        && selectedItem instanceof EdgeStyle
                        && !currentEdges.isEmpty());

                if (skipDefaultPrefs)
                    return;

                if (selectedItem != null) {
                    edgePrefs.put(DEFAULT_STYLE, selectedItem.getStyleName());
                    defaultEdgeStyle = (EdgeStyle) selectedItem;
                } else {
                    edgePrefs.put(DEFAULT_STYLE, "");
                    defaultEdgeStyle = null;
                }
            }
        });

        final JPopupMenu nodesPopup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem(resourceBundle
                .getString("menu.remove_style"));
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                NodeStyle nodeStyle = (NodeStyle) nodesCombo.getSelectedItem();
                if (nodeStyle != null) {
                    removePref(nodePrefs, nodeStyle.getStyleName());
                    nodesCombo.removeItem(nodeStyle);
                    nodeStyles.remove(nodeStyle);

                    if (defaultNodeStyle == nodeStyle) {
                        nodePrefs.put(DEFAULT_STYLE, "");
                        defaultNodeStyle = null;
                    }
                }
            }

        });
        nodesPopup.add(menuItem);

        final JPopupMenu edgesPopup = new JPopupMenu();
        menuItem = new JMenuItem(resourceBundle.getString("menu.remove_style"));
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                EdgeStyle edgeStyle = (EdgeStyle) edgesCombo.getSelectedItem();
                if (edgeStyle != null) {
                    removePref(edgePrefs, edgeStyle.getStyleName());
                    edgesCombo.removeItem(edgeStyle);
                    edgeStyles.remove(edgeStyle);

                    if (defaultEdgeStyle == edgeStyle) {
                        edgePrefs.put(DEFAULT_STYLE, "");
                        defaultEdgeStyle = null;
                    }
                }

            }

        });
        edgesPopup.add(menuItem);

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    if (e.getComponent() == nodesCombo
                            && nodesCombo.getSelectedIndex() > 1) {
                        nodesPopup.show(nodesCombo, e.getX(), e.getY());
                    } else if (e.getComponent() == edgesCombo
                            && edgesCombo.getSelectedIndex() > 1) {
                        edgesPopup.show(edgesCombo, e.getX(), e.getY());
                    }
                }
            }
        };

        nodesCombo.addMouseListener(mouseListener);
        edgesCombo.addMouseListener(mouseListener);
    }
    
    private void postUndoInfo(String name, Map<Attribute, Object> oldValuesMap,
            List<Attribute> newAttributes) {
        
        if (oldValuesMap.isEmpty() && newAttributes.isEmpty())
            return;

        NamedCompoundEdit ce = new NamedCompoundEdit(name);

        Map<GraphElement, GraphElement> geMap = GraffitiSingleton.getInstance()
                .getMainFrame().getActiveEditorSession().getGraphElementsMap();

        ce.addEdit(new ChangeAttributesEdit(oldValuesMap, geMap));
        
        for (Attribute a : newAttributes) {
            ce.addEdit(new AttributeEdit(a, a.getAttributable(), true, geMap));
        }

        ce.end();
        GraffitiSingleton.getInstance().getMainFrame().getUndoSupport()
                .postEdit(ce);
    }

    private void addNodeStyle(String styleName, Node node) {
        NodeStyle nodeStyle = new NodeStyle(styleName, node);

        nodeStyles.put(styleName, nodeStyle);

        removePref(nodePrefs, styleName);
        addPref(nodePrefs, nodeStyle);

        insertStyle(nodesCombo, nodeStyle);
    }

    private void addEdgeStyle(String styleName, Edge edge) {
        EdgeStyle edgeStyle = new EdgeStyle(styleName, edge);

        edgeStyles.put(styleName, edgeStyle);

        removePref(edgePrefs, styleName);
        addPref(edgePrefs, edgeStyle);

        insertStyle(edgesCombo, edgeStyle);
    }

    private static void insertStyle(JComboBox combo, ElementStyle style) {

        if (combo.getItemCount() <= 2) {
            combo.addItem(style);
        } else {
            boolean inserted = false;
            for (int i = 2; i < combo.getItemCount() && !inserted; i++) {
                if (((ElementStyle) combo.getItemAt(i)).getStyleName()
                        .compareToIgnoreCase(style.getStyleName()) >= 0) {
                    combo.insertItemAt(style, i);
                    inserted = true;
                }
            }
            if (!inserted) {
                combo.addItem(style);
            }
        }
    }

    private static void removePref(Preferences prefs, String key) {
        try {
            for (String s : prefs.keys()) {
                if (s.startsWith(key + STYLE_PART_DELIMITER)
                        || s.compareTo(key) == 0) {
                    prefs.remove(s);
                }
            }
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    private static void addPref(Preferences prefs, ElementStyle style) {
        byte[] bytes = style.toByteArray();
        if (bytes.length <= MAX_PREF_LENGTH) {
            prefs.putByteArray(style.getStyleName(), bytes);
        } else {
            int i;
            byte[] b = new byte[MAX_PREF_LENGTH];
            for (i = 0; i < bytes.length / MAX_PREF_LENGTH; i++) {
                System.arraycopy(bytes, i * MAX_PREF_LENGTH, b, 0,
                        MAX_PREF_LENGTH);
                prefs.putByteArray(style.getStyleName() + STYLE_PART_DELIMITER
                        + i, b);
            }
            b = new byte[bytes.length - i * MAX_PREF_LENGTH];
            System.arraycopy(bytes, i * MAX_PREF_LENGTH, b, 0, bytes.length - i
                    * MAX_PREF_LENGTH);
            prefs.putByteArray(style.getStyleName() + STYLE_PART_DELIMITER + i,
                    b);
        }
    }

    private void resetStyles(Session s) {

        skipDefaultPrefs = true;

        nodesCombo.removeAllItems();
        edgesCombo.removeAllItems();

        nodesCombo.addItem(null);
        edgesCombo.addItem(null);

        CollectionAttribute nodeAttr = null;
        CollectionAttribute edgeAttr = null;

        if (s != null) {
            nodeAttr = s.getActiveView().getNodeAttribute();
            edgeAttr = s.getActiveView().getDirectedEdgeAttribute();
        }

        if (nodeAttr == null) {
            nodeAttr = new NodeGraphicAttribute();
        }

        if (edgeAttr == null) {
            edgeAttr = new EdgeGraphicAttribute();
        }

        FastNode node = new FastNode(new FastGraph());
        node.addAttribute(nodeAttr, "");
        nodesCombo.addItem(new NodeStyle(resourceBundle
                .getString("default_style"), node));

        Edge edge = new FastEdge(new FastGraph(), node, node, true, null);
        edge.addAttribute(edgeAttr, "");
        edgesCombo.addItem(new EdgeStyle(resourceBundle
                .getString("default_style"), edge));

        skipDefaultPrefs = false;

        readSavedStyles();
    }

    private void readSavedStyles() {

        String nodeDefStyle = null;
        String edgeDefStyle = null;

        nodeStyles.clear();
        edgeStyles.clear();

        try {
            NodeStyle nodeStyle;
            EdgeStyle edgeStyle;
            byte[] byteArray;
            HashMap<String, HashMap<Integer, byte[]>> map = new HashMap<String, HashMap<Integer, byte[]>>();
            String styleName;
            int seq, index;
            int size, arraysize;

            for (String key : nodePrefs.keys()) {
                if (key.compareTo(DEFAULT_STYLE) == 0) {
                    nodeDefStyle = nodePrefs.get(key, "");
                    continue;
                }

                byteArray = nodePrefs.getByteArray(key, null);
                if (byteArray == null) {
                    continue;
                }

                index = key.lastIndexOf(STYLE_PART_DELIMITER);
                styleName = index == -1 ? key : key.substring(0, index);
                seq = index == -1 ? 0 : Integer.parseInt(key
                        .substring(index + 1));

                if (!map.containsKey(styleName)) {
                    map.put(styleName, new HashMap<Integer, byte[]>());
                }
                map.get(styleName).put(seq, byteArray);
            }

            for (String s : map.keySet()) {
                Map<Integer, byte[]> smap = map.get(s);
                size = smap.size();
                arraysize = size > 0 ? (size - 1) * MAX_PREF_LENGTH
                        + smap.get(size - 1).length : 0;
                byte[] b = new byte[arraysize];
                for (int i = 0; i < smap.size(); i++) {
                    System.arraycopy(smap.get(i), 0, b, i * MAX_PREF_LENGTH,
                            smap.get(i).length);
                }
                nodeStyle = new NodeStyle(s, b);
                nodeStyles.put(s, nodeStyle);

                insertStyle(nodesCombo, nodeStyle);
            }

            map.clear();
            for (String key : edgePrefs.keys()) {

                if (key.compareTo(DEFAULT_STYLE) == 0) {
                    edgeDefStyle = edgePrefs.get(key, "");
                    continue;
                }

                byteArray = edgePrefs.getByteArray(key, null);

                if (byteArray == null) {
                    continue;
                }

                index = key.lastIndexOf(STYLE_PART_DELIMITER);
                styleName = index == -1 ? key : key.substring(0, index);
                seq = index == -1 ? 0 : Integer.parseInt(key
                        .substring(index + 1));

                if (!map.containsKey(styleName)) {
                    map.put(styleName, new HashMap<Integer, byte[]>());
                }
                map.get(styleName).put(seq, byteArray);
            }

            for (String s : map.keySet()) {
                Map<Integer, byte[]> smap = map.get(s);
                size = smap.size();
                arraysize = size > 0 ? (size - 1) * MAX_PREF_LENGTH
                        + smap.get(size - 1).length : 0;
                byte[] b = new byte[arraysize];
                for (int i = 0; i < smap.size(); i++) {
                    System.arraycopy(smap.get(i), 0, b, i * MAX_PREF_LENGTH,
                            smap.get(i).length);
                }

                edgeStyle = new EdgeStyle(s, b);
                edgeStyles.put(s, edgeStyle);

                insertStyle(edgesCombo, edgeStyle);
            }

        } catch (BackingStoreException e) {
            System.err.println("Couldn't read saved styles.");
        }

        defaultNodeStyle = nodeStyles.get(nodeDefStyle);
        defaultEdgeStyle = edgeStyles.get(edgeDefStyle);

        nodesCombo.setSelectedItem(defaultNodeStyle);
        edgesCombo.setSelectedItem(defaultEdgeStyle);

    }

    public void selectionChanged(SelectionEvent e) {
        Selection selection = e.getSelection();

        currentNodes = selection.getNodes();
        currentEdges = selection.getEdges();

        addStyleButton.setEnabled(selection.getElements().size() == 1);

        applyButton.setEnabled(currentNodes != null
                && nodesCombo.getSelectedItem() != null
                && currentNodes.size() > 0 || currentEdges != null
                && edgesCombo.getSelectedItem() != null
                && currentEdges.size() > 0);
    }

    public void sessionChanged(Session s) {
        if (graph != null) {
            try {
                graph.getListenerManager().removeGraphListener(this);
            } catch (ListenerNotFoundException e) {
            }
        }
        if (selectionModel != null) {
            selectionModel.removeSelectionListener(this);
        }

        if (s == null) {
            graph = null;
            selectionModel = null;
            return;
        }

        graph = s.getGraph();
        graph.getListenerManager().addNonstrictGraphListener(this);

        SelectionModel model = ((EditorSession) s).getSelectionModel();
        model.addSelectionListener(this);

        resetStyles(s);
    }

    public void postNodeAdded(GraphEvent e) {
        if (ignoredTransactions == 0 && defaultNodeStyle != null) {
            defaultNodeStyle.apply(e.getNode().getAttributes());
        }
    }

    public void postEdgeAdded(GraphEvent e) {
        if (delayingTransactions > 0) {
            delayedEdgeEvents.add(e);
            return;
        }

        if (ignoredTransactions == 0 && defaultEdgeStyle != null) {
            defaultEdgeStyle.apply(e.getEdge().getAttributes());
        }
    }

    public void selectionListChanged(SelectionEvent e) {

    }

    public void sessionDataChanged(Session s) {

    }

    public void postEdgeRemoved(GraphEvent e) {
    }

    public void postGraphCleared(GraphEvent e) {

    }

    public void postNodeRemoved(GraphEvent e) {
    }

    public void preEdgeAdded(GraphEvent e) {

    }

    public void preEdgeRemoved(GraphEvent e) {

    }

    public void preGraphCleared(GraphEvent e) {

    }

    public void preNodeAdded(GraphEvent e) {

    }

    public void preNodeRemoved(GraphEvent e) {

    }

    public void transactionFinished(TransactionEvent e) {
        if (e.getSource() instanceof PasteAction
                 || e.getSource() instanceof EditUndoAction
                 || e.getSource() instanceof EditRedoAction) {
            ignoredTransactions--;
        } else if (e.getSource() instanceof FinishEdgeCreation) {
            delayingTransactions--;

            if (delayingTransactions == 0) {
                for (GraphEvent ev : delayedEdgeEvents) {
                    postEdgeAdded(ev);
                }
                delayedEdgeEvents.clear();
            }
        }
    }

    public void transactionStarted(TransactionEvent e) {
        if (e.getSource() instanceof PasteAction
                || e.getSource() instanceof EditUndoAction
                || e.getSource() instanceof EditRedoAction) {
            ignoredTransactions++;
        } else if (e.getSource() instanceof FinishEdgeCreation) {
            delayingTransactions++;
        }
    }
}
