//=============================================================================
//
//   SemanticView.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: SemanticView.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.selection.Selection;

/**
 * This class splits the attributes of an attributable into several groups. A
 * group's attributes can be displayed by selecting its name in a JComboBox int
 * the tab's topPane.
 */
public class SemanticView extends ViewTab implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1004588527765923207L;

    /** The name of the group containing attributes related to geometry. */
    private final static String GEOMETRY = "Geometry";

    /** The name of the group containing attributes related to shape. */
    private final static String SHAPE = "Shape";

    /** The name of the group containing attributes related to stroke. */
    private final static String STROKE = "Stroke";

    /** The name of the group containing attributes related to fill. */
    private final static String FILL = "Fill";

    /** The name of the group containing attributes related to label. */
    private final static String LABEL = "Label";

    /** The name of the group containing attributes related to misc. */
    private final static String MISC = "Misc";

    /** The name of the group containing attributes related to arrow. */
    private final static String ARROW = "Arrow";

    /** The name of the group containing attributes related to line. */
    private final static String LINE = "Line";

    /** The name of the group containing attributes related to bend. */
    private final static String BENDS = "Bend";

    /** The name of the group containing attributes related to direction. */
    private final static String DIRECTION = "Direction";

    /**
     * Mapping of SemanticGroups describing the node groups and their names.
     */
    private Map<String, SemanticGroup> nodeGroups;

    /**
     * Mapping of SemanticGroups describing the edge groups and their names.
     */
    private Map<String, SemanticGroup> edgeGroups;

    /**
     * Mapping of SemanticGroups describing the graph groups and their names.
     */
    private Map<String, SemanticGroup> graphGroups;

    /**
     * The names of the node groups. This is needed to define in which order the
     * semantic groups will appear in the combobox.
     */
    private List<String> nodeGroupNames;

    /**
     * The names of the edge groups. This is needed to define in which order the
     * semantic groups will appear in the combobox.
     */
    private List<String> edgeGroupNames;

    /**
     * The names of the graph groups. This is needed to define in which order
     * the semantic groups will appear in the combobox.
     */
    private List<String> graphGroupNames;

    /** The currently displayed semantic group. */
    private SemanticGroup currentGroup;

    /**
     * Constructs a new SemanticView and builds the tabbed pane. Initializes the
     * semantic groups.
     */
    public SemanticView() {
        super();
        this.nodeGroups = new HashMap<String, SemanticGroup>();
        this.edgeGroups = new HashMap<String, SemanticGroup>();
        this.graphGroups = new HashMap<String, SemanticGroup>();
        this.nodeGroupNames = new LinkedList<String>();
        this.edgeGroupNames = new LinkedList<String>();
        this.graphGroupNames = new LinkedList<String>();
        defaultNodePaths = new HashSet<String>();
        defaultEdgePaths = new HashSet<String>();
        defaultGraphPaths = new HashSet<String>();

        this.graphGroups.put(DIRECTION, new GroupGraphDirection(DIRECTION));
        this.graphGroupNames.add(DIRECTION);
        this.graphGroupNames.add(MISC);

        this.nodeGroups.put(GEOMETRY, new GroupNodeGeometry(GEOMETRY));
        this.nodeGroups.put(SHAPE, new GroupNodeShape(SHAPE));
        this.nodeGroups.put(STROKE, new GroupNodeStroke(STROKE));
        this.nodeGroups.put(FILL, new GroupNodeFill(FILL));
        this.nodeGroups.put(LABEL, new GroupNodeLabel(LABEL));
        this.nodeGroupNames.add(GEOMETRY);
        this.nodeGroupNames.add(SHAPE);
        this.nodeGroupNames.add(STROKE);
        this.nodeGroupNames.add(FILL);
        this.nodeGroupNames.add(LABEL);
        this.nodeGroupNames.add(MISC);

        this.edgeGroups.put(LINE, new GroupEdgeLine(LINE));
        this.edgeGroups.put(ARROW, new GroupEdgeArrow(ARROW));
        this.edgeGroups.put(BENDS, new GroupEdgeBend(BENDS));
        this.edgeGroups.put(LABEL, new GroupEdgeLabel(LABEL));
        this.edgeGroupNames.add(LINE);
        this.edgeGroupNames.add(ARROW);
        this.edgeGroupNames.add(BENDS);
        this.edgeGroupNames.add(LABEL);
        this.edgeGroupNames.add(MISC);

        this.saveDefaults(this.nodeGroups, defaultNodePaths);
        this.saveDefaults(this.edgeGroups, defaultEdgePaths);
        this.saveDefaults(this.graphGroups, defaultGraphPaths);
        // add path to the bends-folder
        defaultEdgePaths.add(Attribute.SEPARATOR
                + GraphicAttributeConstants.BENDS_PATH);

        this.nodeGroups.put(MISC, new GroupMisc(MISC));
        this.edgeGroups.put(MISC, new GroupMisc(MISC));
        this.graphGroups.put(MISC, new GroupMisc(MISC));
    }

    /**
     * Makes the editPanel display the semantic group after the user selects it
     * from the comboBox.
     * 
     * @param event
     *            the ActionEvent
     */
    public void actionPerformed(ActionEvent event) {

        // get selected group's value
        Object item = ((JComboBox) event.getSource()).getSelectedItem();
        String value = ((ComboItem) item).getValue();

        if (value == null) {
            // user selected "choose group"
            this.currentGroup = null;

            // reset edit panel if no group selected
            Object firstItem = ((JComboBox) event.getSource()).getItemAt(1);
            if (firstItem == null)
                return;
            String val = ((ComboItem) firstItem).getValue();
            if (val.startsWith(String.valueOf(GRAPH))) {
                ((DefaultEditPanel) this.graphTab.getEditPanel()).reset();
            } else if (val.startsWith(String.valueOf(NODE))) {
                ((DefaultEditPanel) this.nodeTab.getEditPanel()).reset();
            } else if (val.startsWith(String.valueOf(EDGE))) {
                ((DefaultEditPanel) this.edgeTab.getEditPanel()).reset();
            }

            return;
        }

        DefaultEditPanel editPanel = null;
        SemanticGroup group = null;

        // check which comboBox item is selected
        if ((GRAPH + DIRECTION).equals(value)) {
            // graph groups
            this.attributables = currentGraph;
            editPanel = (DefaultEditPanel) this.graphTab.getEditPanel();
            group = this.graphGroups.get(DIRECTION);
        } else if ((GRAPH + MISC).equals(value)) {
            this.attributables = currentGraph;
            editPanel = (DefaultEditPanel) this.graphTab.getEditPanel();
            group = this.graphGroups.get(MISC);
            ((GroupMisc) group).findMiscPaths(this.attributables,
                    defaultGraphPaths);

        } else if ((NODE + GEOMETRY).equals(value)) {
            // node groups
            this.attributables = currentNodes;
            editPanel = (DefaultEditPanel) this.nodeTab.getEditPanel();
            group = this.nodeGroups.get(GEOMETRY);
        } else if ((NODE + SHAPE).equals(value)) {
            this.attributables = currentNodes;
            editPanel = (DefaultEditPanel) this.nodeTab.getEditPanel();
            group = this.nodeGroups.get(SHAPE);
        } else if ((NODE + STROKE).equals(value)) {
            this.attributables = currentNodes;
            editPanel = (DefaultEditPanel) this.nodeTab.getEditPanel();
            group = this.nodeGroups.get(STROKE);
        } else if ((NODE + FILL).equals(value)) {
            this.attributables = currentNodes;
            editPanel = (DefaultEditPanel) this.nodeTab.getEditPanel();
            group = this.nodeGroups.get(FILL);
        } else if ((NODE + LABEL).equals(value)) {
            this.attributables = currentNodes;
            editPanel = (DefaultEditPanel) this.nodeTab.getEditPanel();
            group = this.nodeGroups.get(LABEL);
            ((GroupNodeLabel) group).findLabelPaths(this.attributables, null);
        } else if ((NODE + MISC).equals(value)) {
            this.attributables = currentNodes;
            editPanel = (DefaultEditPanel) this.nodeTab.getEditPanel();
            group = this.nodeGroups.get(MISC);
            ((GroupMisc) group).findMiscPaths(this.attributables,
                    defaultNodePaths);

        } else if ((EDGE + LINE).equals(value)) {
            // edge groups
            this.attributables = currentEdges;
            editPanel = (DefaultEditPanel) this.edgeTab.getEditPanel();
            group = this.edgeGroups.get(LINE);
        } else if ((EDGE + ARROW).equals(value)) {
            this.attributables = currentEdges;
            editPanel = (DefaultEditPanel) this.edgeTab.getEditPanel();
            group = this.edgeGroups.get(ARROW);
        } else if ((EDGE + BENDS).equals(value)) {
            this.attributables = currentEdges;
            editPanel = (DefaultEditPanel) this.edgeTab.getEditPanel();
            group = this.edgeGroups.get(BENDS);
            ((GroupEdgeBend) group).findBendPaths(this.attributables);
        } else if ((EDGE + LABEL).equals(value)) {
            this.attributables = currentEdges;
            editPanel = (DefaultEditPanel) this.edgeTab.getEditPanel();
            group = this.edgeGroups.get(LABEL);
            ((GroupEdgeLabel) group).findLabelPaths(this.attributables, null);
        } else if ((EDGE + MISC).equals(value)) {
            this.attributables = currentEdges;
            editPanel = (DefaultEditPanel) this.edgeTab.getEditPanel();
            group = this.edgeGroups.get(MISC);
            ((GroupMisc) group).findMiscPaths(this.attributables,
                    defaultEdgePaths);
        }

        if (editPanel != null && group != null) {
            // set the attributables related to the selected group and show
            // its attributes
            editPanel.setAttributables(this.attributables);
            group.showVECs(editPanel, getBooledAttributes(group));
        } else {
            System.err.println("SemanticPanel.actionPerformed(): "
                    + "Unknown group " + value);
        }
        this.currentGroup = group;
    }

    /**
     * Returns the currently displayed semantic group.
     * 
     * @return the currently displaed semantic group
     */
    public SemanticGroup getCurrentGroup() {
        return this.currentGroup;
    }

    /**
     * Saves all attribute paths of the group's entries into the default list.
     * 
     * @param group
     *            the group whose attribute paths will be saved
     * @param defaults
     *            the list where the paths will be saved
     */
    private void saveDefaults(Map<String, SemanticGroup> group,
            HashSet<String> defaults) {
        for (SemanticGroup semanticGroup : group.values()) {
            defaults.addAll(semanticGroup.getAttributePaths());
        }
    }

    /**
     * Make the editPanel update its attributes.
     * 
     * @param type
     *            the type of the current selected tab (e.g. NODE)
     * @param sel
     *            the current selection
     */
    @Override
    public void rebuildTopPane(int type, Selection sel) {
        if (!sel.isEmpty()) {
            currentEdges = sel.getEdges();
            currentNodes = sel.getNodes();
        }

        // get topPanel
        JPanel topPanel = null;
        if (type == NODE) {
            topPanel = this.nodeTab.getTopPanel();
        } else if (type == EDGE) {
            topPanel = this.edgeTab.getTopPanel();
        } else if (type == GRAPH) {
            topPanel = this.graphTab.getTopPanel();
        }

        // update editPanel by simulating a comboBox selection
        JComboBox comboBox = (JComboBox) topPanel.getComponent(0);
        comboBox.setSelectedItem(comboBox.getSelectedItem());
    }

    /**
     * Shows the comboBox with the names of the semantic groups.
     * 
     * @param type
     *            the type of the current selected tab (e.g. NODE)
     * @param sel
     *            the current selection
     */
    @Override
    public void buildTopPane(int type, Selection sel) {

        if (!sel.isEmpty()) {
            // set the currently serlected nodes and edges
            currentEdges = sel.getEdges();
            currentNodes = sel.getNodes();
        }

        // get selected tab
        Iterator<String> it = null;
        AbstractTab tab = null;
        if (type == NODE) {
            it = this.nodeGroupNames.iterator();
            tab = this.nodeTab;
        } else if (type == EDGE) {
            it = this.edgeGroupNames.iterator();
            tab = this.edgeTab;
        } else if (type == GRAPH) {
            it = this.graphGroupNames.iterator();
            tab = this.graphTab;
        }

        JPanel topPanel = tab.getTopPanel();
        topPanel.removeAll();

        // build comboBox
        JComboBox comboBox = new JComboBox();
        comboBox.addItem(new ComboItem("", null));

        for (; it.hasNext();) {
            // add all groups to the comboBox
            String groupName = it.next();
            comboBox.addItem(new ComboItem(groupName, type + groupName));
        }

        comboBox.addActionListener(this);
        topPanel.add(comboBox);

        // resize topPane
        JSplitPane mainSplit = (JSplitPane) tab.getComponent(0);
        mainSplit.setDividerSize(10);
        mainSplit.setDividerLocation(47);
        mainSplit.setOneTouchExpandable(false);

        Dimension bigDim = new Dimension(10000, 10000);
        ((JScrollPane) mainSplit.getComponent(0)).setMinimumSize(bigDim);
        ((DefaultEditPanel) mainSplit.getComponent(1)).setMinimumSize(bigDim);
    }

    /**
     * Returns the BooledAttribute specified by a path. The boolean value will
     * be true if all graph elements (e.g. all selected graph edges) have this
     * attribute (if not, return null) and if they all have the same value.
     * 
     * @param path
     *            the path to the attribute
     * @return the BooledAttribute specified by the path
     */
    private BooledAttribute getBooled(String path) {

        if (attributables.size() == 1) {
            // just one node/edge selected
            BooledAttribute booled = null;
            try {
                booled = new BooledAttribute(((Attributable) attributables
                        .get(0)).getAttribute(path), true);
            } catch (AttributeNotFoundException anfe) {
                // do nothing
            }
            return booled;
        }

        boolean allSameValue = true;
        Attribute attribute = ((Attributable) this.attributables.get(0))
                .getAttribute(path);
        Collection<Attribute> attrs;

        // if treeNode is no leave, get children
        if (attribute instanceof CollectionAttribute) {
            attrs = ((CollectionAttribute) attribute).getCollection().values();
        } else if (attribute instanceof CompositeAttribute) {
            attrs = new LinkedList<Attribute>();
            attrs.add(attribute);
        } else {
            // attribute is a leave

            for (Attributable attributable : attributables) {
                // check if present in all selected graph elements
                try {
                    Attribute oAttr = attributable.getAttribute(path);

                    // check if the attribute has the same value in all
                    // selected graph elements
                    if (!attribute.getValue().equals(oAttr.getValue())) {
                        allSameValue = false;
                        break;
                    }
                } catch (AttributeNotFoundException anfe) {
                    // found graph element that has no such attribute
                    return null;
                }
            }
            return new BooledAttribute(attribute, allSameValue);
        }

        for (Attribute nextAttribute : attrs) {
            // iterate through the attribute's children
            // to see if they are present in all attributables and if they
            // have the same values

            for (Attributable attributable : attributables) {
                try {
                    Attribute oAttr = attributable.getAttribute(nextAttribute
                            .getPath());
                    if (allSameValue
                            && !nextAttribute.getValue().equals(
                                    oAttr.getValue())) {
                        // found graph element that has another value for
                        // this attribute
                        allSameValue = false;
                    }
                } catch (AttributeNotFoundException anfe) {
                    // found graph element that has no such attribute
                    return null;
                } catch (NullPointerException npe) {
                    // found graph element that has no such attribute
                    return null;
                }
            }

            if (!path.equals(nextAttribute.getPath())) {
                // all selected graph elements have this attribute

                // recursively call getBooled() to check all attributes
                // that are nextAttribute's children
                BooledAttribute nextBooled = getBooled(nextAttribute.getPath());
                if (nextBooled == null)
                    return null;
                else {
                    if (allSameValue) {
                        allSameValue = nextBooled.getBool();
                    }
                }
            }
        }
        return new BooledAttribute(attribute, allSameValue);
    }

    /**
     * Returns a list with the BooledAttributes of a semantic group.
     * 
     * @param group
     *            the semantic group
     * @return the BooledAttributes of the group's attributes
     */
    private List<BooledAttribute> getBooledAttributes(SemanticGroup group) {
        List<BooledAttribute> booledAttributes = new LinkedList<BooledAttribute>();

        for (String path : group.getAttributePaths()) {
            BooledAttribute booled = getBooled(path);

            if (booled != null) {
                booledAttributes.add(booled);
            }
        }
        return booledAttributes;
    }

    /**
     * This class represents the items that are displayed by the ComboBox. An
     * item contains a text (i.e.: the group's name) and a value (i.e.: the
     * attributable + the groups name).
     */
    private class ComboItem {

        /** The text to display in the comboBox. */
        private String text;

        /** The value of this item. */
        private String value;

        /**
         * Creates a new ComboItem and sets its parameters.
         * 
         * @param text
         *            the text of the item
         * @param value
         *            the value of the item
         */
        public ComboItem(String text, String value) {
            this.text = text;
            this.value = value;
        }

        /**
         * Returns this item's value.
         * 
         * @return this value
         */
        public String getValue() {
            return this.value;
        }

        /**
         * Returns a String-representation of this item.
         * 
         * @return this item's text
         */
        @Override
        public String toString() {
            return this.text;
        }
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
