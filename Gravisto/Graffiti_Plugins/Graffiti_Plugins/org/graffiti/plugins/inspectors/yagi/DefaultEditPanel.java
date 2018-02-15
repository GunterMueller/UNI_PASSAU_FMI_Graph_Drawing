//=============================================================================
//
//   DefaultEditPanel.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: DefaultEditPanel.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.event.ListenerManager;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.VECChangeEvent;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugin.editcomponent.ValueEditComponentListener;
import org.graffiti.plugin.inspector.EditPanel;
import org.graffiti.plugins.editcomponents.yagi.CollectionEditComponent;
import org.graffiti.plugins.editcomponents.yagi.SelfLabelingComponent;
import org.graffiti.plugins.editcomponents.yagi.ValueEditComponentFactory;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * The editPanel of graph/edge/node tabs. Provides methods to display VECs in
 * dependence of the topPanel (e.g. the selected tree node) and the currently
 * selected graph elements.
 */
public class DefaultEditPanel extends EditPanel implements
        GraphicAttributeConstants, ValueEditComponentListener {

    /**
     * 
     */
    private static final long serialVersionUID = -3966807789067047118L;

    /** The path where new labels will be added. */
    public static final String LABEL_PATH = "";

    /** The prefix of new labels. */
    public static final String LABEL_PREFIX = "label";

    /** The prefix of new bends. */
    public static final String BEND_PREFIX = "bend";

    /** The preferred size of labels. */
    public static final Dimension LABEL_SIZE = new Dimension(90, 15);

    /** A mapping of VECs and attributes that are displayed by these VECs. */
    private Map<ValueEditComponent, Collection<Attribute>> vecAttrs;

    private List<Attribute> attributeList;

    /** The panel containing the VECs. */
    private JPanel editPanel;

    /** The currently displayed VECs. */
    private List<ValueEditComponent> displayedVEC;

    /**
     * The list containing the currently selected attributables of the selected
     * tab. Thus they are all from the same type (e.g. nodes).
     */
    private List<? extends Attributable> attributables;

    /**
     * A mapping of attribute classes and VECs that are linked to these
     * attributes.
     */
    private Map<Class<?>, Class<?>> editComponentMap;

    /** Holds the ListenerManager where the panel is registered. */
    private ListenerManager listenerManager;

    /**
     * Factory for value edit components
     */
    private ValueEditComponentFactory vecFactory = new ValueEditComponentFactory();

    /**
     * Instantiates a new edit panel.
     */
    public DefaultEditPanel() {
        super();
        this.displayedVEC = new LinkedList<ValueEditComponent>();
        this.vecAttrs = new HashMap<ValueEditComponent, Collection<Attribute>>();
        this.editComponentMap = new HashMap<Class<?>, Class<?>>();
        this.setLayout(new BorderLayout());
    }

    /**
     * Builds the table that is used for editing attributes from scratch.
     * 
     * @param rootNode
     *            the root attribute
     * @param attributables
     *            the new attributables
     */
    @Override
    public void buildTable(DefaultMutableTreeNode rootNode,
            List<? extends Attributable> attributables) {
        // initialize panel
        this.reset();
        this.attributables = attributables;

        // build tree by recursive calls of showTreeNode starting with the root
        showTreeNode(rootNode);
        this.validate();
        this.repaint();
    }

    /**
     * Returns this editComponentMap.
     * 
     * @return this editComponentMap
     */
    public Map<Class<?>, Class<?>> getEditComponentMap() {
        return this.editComponentMap;
    }

    /**
     * Sets a new editComponentMap.
     * 
     * @param map
     *            the new editComponentMap
     */
    @Override
    public void setEditComponentMap(Map<Class<?>, Class<?>> map) {
        this.editComponentMap = map;
    }

    /**
     * Sets new attributables.
     * 
     * @param attributables
     *            the new attributables
     */
    public void setAttributables(List<? extends Attributable> attributables) {
        this.attributables = attributables;
    }

    /**
     * Sets the ListenerManager.
     * 
     * @param lm
     *            the new ListenerManager
     */
    @Override
    public void setListenerManager(ListenerManager lm) {
        this.listenerManager = lm;
    }

    /**
     * Returns the ListenerManager.
     * 
     * @return the ListenerManager
     */
    public ListenerManager getListenerManager() {
        return this.listenerManager;
    }

    /**
     * Updates the VECs after an attribute has changed.
     * 
     * @param changedAttribute
     *            the changed attribute
     */
    @Override
    public void updateTable(Attribute changedAttribute) {
        String childPath = changedAttribute.getPath();
        for (ValueEditComponent vec : displayedVEC) {
            if (childPath.startsWith(((Attribute) vec.getDisplayable())
                    .getPath())) {
                if (vec instanceof CollectionEditComponent) {
                    ((CollectionEditComponent) vec)
                            .setEditFieldValue(childPath);
                } else {
                    vec.setEditFieldValue();
                }
            }
        }
    }

    /**
     * Shows one row (i.e. one attribute) in the editFieldPanel.
     * 
     * @param attribute
     *            the attribute to show
     * @param ecClass
     *            the class of the attribute's vec
     * @param showValue
     *            <code>true</code>, if all graph elements have the same value,
     *            <code>false</code>, if not
     */
    private void showRow(Attribute attribute, Class<?> ecClass,
            boolean showValue) {
        JComponent row;
        ValueEditComponent vec = vecFactory.createComponent(attribute);
        JComponent component = vec.getComponent();
        if (vec instanceof SelfLabelingComponent
                && ((SelfLabelingComponent) vec).isSelfLabeling()) {
            row = component;
        } else {
            row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.LINE_AXIS));
            row.add(createIdLabel(attribute));
            row.add(Box.createRigidArea(new Dimension(SemanticGroup.SPACE, 0)));
            row.add(vec.getComponent());
        }

        if (this.attributables.size() > 1) {
            Attribute[] attrs = new Attribute[this.attributables.size()];
            String path = attribute.getPath();
            int i = 0;
            for (Attributable a : this.attributables) {
                attrs[i] = a.getAttribute(path);
                i++;
            }
            vec.setDisplayables(attrs);
            saveAttributes(attrs);
        } else {
            saveAttribute(attribute);
        }

        vec.setShowEmpty(!showValue);
        addListener(vec);
        displayedVEC.add(vec);

        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row
                .getPreferredSize().height));

        editPanel.add(row);
        editPanel.add(Box
                .createRigidArea(new Dimension(0, SemanticGroup.DSPACE)));
        editPanel.revalidate();
        revalidate();
    }

    /**
     * Returns a label with the id of an attribute.
     * 
     * @param attribute
     *            the attribute
     * @return the label with the id
     */
    public static JLabel createIdLabel(Attribute attribute) {
        JLabel label = new JLabel(attribute.getId());
        String tttext = attribute.getPath();

        if (!"".equals(attribute.getDescription())) {
            tttext = "<html>" + tttext + "<p>" + attribute.getDescription()
                    + "</html>";
        }

        // label.setPreferredSize(LABEL_SIZE);

        label.setToolTipText(tttext);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    /**
     * Returns the VEC belonging to an attribute.
     * 
     * @param attribute
     *            the attribute
     * @param ecClass
     *            the class of the attribute's editComponent
     * @return the VEC of the attribute
     */
    public ValueEditComponent createVEC(Attribute attribute, Class<?> ecClass) {
        ValueEditComponent vec = vecFactory.createComponent(attribute);

        if (this.attributables.size() > 1) {
            Attribute[] attrs = new Attribute[this.attributables.size()];
            String path = attribute.getPath();
            int i = 0;
            for (Attributable a : this.attributables) {
                attrs[i] = a.getAttribute(path);
                i++;
            }
            vec.setDisplayables(attrs);
        }

        displayedVEC.add(vec);
        return vec;
    }

    /**
     * Displays the attribute(s) belonging to the currently selected tree node.
     * 
     * @param node
     *            the tree node whose attributes are to be displayed
     */
    private void showTreeNode(DefaultMutableTreeNode node) {

        BooledAttribute booledAttr = (BooledAttribute) node.getUserObject();
        Attribute attr = booledAttr.getAttribute();
        Class<?> ecClass = this.editComponentMap.get(attr.getClass());

        if (attr instanceof CollectionAttribute
                || attr instanceof CompositeAttribute) {
            // if attr is a Collection- or CompositeAttribute, we check if
            // there is a component registered
            if (ecClass != null) {
                // if we have a registered component to display it, add it
                this.showRow(attr, ecClass, booledAttr.getBool());
            } else {
                // if no component is registered, we iterate through its
                // collection and check if these attributes have a registered
                // component

                int childCount = node.getChildCount();
                if (childCount == 0) {
                    this.showRow(attr, null, booledAttr.getBool());
                } else {
                    for (int i = 0; i < childCount; i++) {
                        DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
                                .getChildAt(i);
                        showTreeNode(child);
                    }
                }
            }
        } else {
            // attribute is a leave
            showRow(attr, ecClass, booledAttr.getBool());
        }
    }

    /**
     * Adds a bend to the currently selected edge(s).
     * 
     * @param attribute
     *            the coordinateAttribute representing the bend
     */
    public void addBend(CoordinateAttribute attribute) {
        ListenerManager lm = (((GraphElement) this.attributables.get(0))
                .getGraph()).getListenerManager();
        lm.transactionStarted(this);

        for (Attributable attbl : attributables) {
            try {
                attbl.addAttribute((CoordinateAttribute) attribute.copy(),
                        GraphicAttributeConstants.BENDS_PATH
                                + Attribute.SEPARATOR);

                if (attbl
                        .getAttribute(GraphicAttributeConstants.SHAPE_PATH)
                        .getValue()
                        .equals(
                                GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME)) {
                    // change line shape to "polyline" if current shape
                    // is "straight line"
                    attbl.changeString(GraphicAttributeConstants.SHAPE_PATH,
                            GraphicAttributeConstants.POLYLINE_CLASSNAME);
                }
            } catch (AttributeExistsException ex) {
                // this should not happen as we create a valid bend
                // with getNewBendId()
                JOptionPane.showMessageDialog(this, "Could not add attribute: "
                        + ex, "Error!", JOptionPane.OK_OPTION);
                return;
            }
        }

        lm.transactionFinished(this);
        this.postUndoInfo();
    }

    /**
     * Adds bend to the currently selected edge(s).
     * 
     * @param id
     *            The id of the new bends.
     * @param currentBendLabel
     */
    public void addBends(String id, String currentBendLabel) {
        CoordinateAttribute attribute;
        ListenerManager lm = (((GraphElement) this.attributables.get(0))
                .getGraph()).getListenerManager();
        lm.transactionStarted(this);

        for (Attributable attbl : attributables) {
            Edge edge = (Edge) attbl;
            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            SortedCollectionAttribute bends = ega.getBends();
            Collection<Attribute> bendsColl = bends.getCollection().values();
            LinkedList<CoordinateAttribute> bendsList = new LinkedList<CoordinateAttribute>();
            int positionOfBend = -10;
            int i = 0;
            for (Attribute attr : bendsColl) {
                bendsList.add((CoordinateAttribute) attr);
                if (((CoordinateAttribute) attr).getId().equals(
                        currentBendLabel)) {
                    positionOfBend = i;
                }
                i++;
            }

            double targetX = 0;
            double targetY = 0;

            double sourceX = 0;
            double sourceY = 0;

            if (positionOfBend == bendsList.size() - 1 || bends.isEmpty()) {
                CoordinateAttribute targetCa = (CoordinateAttribute) edge
                        .getTarget().getAttribute(
                                GraphicAttributeConstants.GRAPHICS
                                        + Attribute.SEPARATOR
                                        + GraphicAttributeConstants.COORDINATE);

                targetX = targetCa.getX();
                targetY = targetCa.getY();
            } else if (positionOfBend == -10) {
                targetX = bendsList.getFirst().getX();
                targetY = bendsList.getFirst().getY();
            } else {
                targetX = bendsList.get(positionOfBend + 1).getX();
                targetY = bendsList.get(positionOfBend + 1).getY();
            }

            if (bendsList.isEmpty() || positionOfBend < 0) {
                CoordinateAttribute sourceCa = (CoordinateAttribute) edge
                        .getSource().getAttribute(
                                GraphicAttributeConstants.GRAPHICS
                                        + Attribute.SEPARATOR
                                        + GraphicAttributeConstants.COORDINATE);
                sourceX = sourceCa.getX();
                sourceY = sourceCa.getY();
            } else {
                sourceX = bendsList.get(positionOfBend).getX();
                sourceY = bendsList.get(positionOfBend).getY();
            }

            double x = (targetX + sourceX) / 2.0;
            double y = (targetY + sourceY) / 2.0;

            attribute = new CoordinateAttribute(id, x, y);
            if (positionOfBend >= 0) {
                bendsList.add(positionOfBend + 1, attribute);
            } else {
                bendsList.addFirst(attribute);
            }
            int index = 0;
            SortedCollectionAttribute newBends = new LinkedHashMapAttribute(ega
                    .getBends().getId());

            for (CoordinateAttribute coordAttr : bendsList) {
                newBends.add(new CoordinateAttribute(
                        GraphicAttributeConstants.BEND + index, coordAttr
                                .getX(), coordAttr.getY()));
                index++;
            }

            try {
                ega.setBends(newBends);
                if (attbl
                        .getAttribute(GraphicAttributeConstants.SHAPE_PATH)
                        .getValue()
                        .equals(
                                GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME)) {
                    // change line shape to "polyline" if current shape
                    // is "straight line"
                    attbl.changeString(GraphicAttributeConstants.SHAPE_PATH,
                            GraphicAttributeConstants.POLYLINE_CLASSNAME);
                }
            } catch (AttributeExistsException ex) {
                // this should not happen as we create a valid bend
                // with getNewBendId()
                JOptionPane.showMessageDialog(this, "Could not add attribute: "
                        + ex, "Error!", JOptionPane.OK_OPTION);
                return;
            }
        }

        lm.transactionFinished(this);
        this.postUndoInfo();
    }

    /**
     * Removes a bend from the currently selected edge(s).
     * 
     * @param path
     *            the path to the coordinateAttribute representing the bend
     */
    public void removeBend(String path) {
        ListenerManager lm = (((GraphElement) this.attributables.get(0))
                .getGraph()).getListenerManager();
        lm.transactionStarted(this);

        for (Attributable attbl : attributables) {
            attbl.removeAttribute(path);
        }

        lm.transactionFinished(this);
        this.postUndoInfo();
    }

    /**
     * Adds a label to the currently selected graph element(s).
     * 
     * @param attribute
     *            the new label
     */
    public void addLabel(LabelAttribute attribute) {
        ListenerManager lm = (((GraphElement) this.attributables.get(0))
                .getGraph()).getListenerManager();
        lm.transactionStarted(this);

        for (Attributable attbl : attributables) {
            try {
                attbl.addAttribute((LabelAttribute) attribute.copy(),
                        LABEL_PATH);
            } catch (AttributeExistsException ex) {
                // this should not happen as we create a valid label
                // with getNewLabelId()
                JOptionPane.showMessageDialog(this, "Could not add attribute: "
                        + ex, "Error!", JOptionPane.OK_OPTION);
                return;
            }
        }
        lm.transactionFinished(this);
        this.postUndoInfo();
    }

    /**
     * Removes a label from the currently selected graph element(s).
     * 
     * @param path
     *            the path to the label to remove
     */
    public void removeLabel(String path) {
        ListenerManager lm = (((GraphElement) this.attributables.get(0))
                .getGraph()).getListenerManager();
        lm.transactionStarted(this);

        for (Attributable attbl : attributables) {
            attbl.removeAttribute(path);
        }

        lm.transactionFinished(this);
        this.postUndoInfo();
    }

    /**
     * Returns a new (i.e. not yet used) id for adding a new label. The id will
     * be "labelX" where X is an integer starting from 0. When a path is found
     * where no attribute exists in all attributables this path will be
     * returned.
     * 
     * @return the new path of the label
     */
    public String getNewLabelId() {

        int index = -1;
        boolean accepted = false;

        while (!accepted) {
            index++;
            for (Attributable attbl : attributables) {
                try {
                    // check if label at the specified path exists
                    attbl.getAttribute(LABEL_PATH + LABEL_PREFIX + index);
                    accepted = false;
                    break;
                } catch (AttributeNotFoundException anfe) {
                    // do nothing
                    accepted = true;
                }
            }
        }
        return LABEL_PREFIX + index;
    }

    /**
     * Returns a new (i.e. not yet used) id for adding a new bend. The id will
     * be "bendX" where X is an integer starting from 0. When a path is found
     * where no attribute exists in all attributables this path will be
     * returned.
     * 
     * @return the new path of the bend TODO obsolete?
     */
    public String getNewBendId() {

        int index = -1;
        boolean accepted = false;

        while (!accepted) {
            index++;
            for (Attributable attributable : attributables) {
                Attributable attbl = attributable;
                try {
                    // check if bend at the specified path exists
                    attbl.getAttribute(GraphicAttributeConstants.BENDS_PATH
                            + Attribute.SEPARATOR + BEND_PREFIX + index);
                    accepted = false;
                    break;
                } catch (AttributeNotFoundException anfe) {
                    accepted = true;
                }
            }
        }
        return BEND_PREFIX + index;
    }

    /**
     * Returns the number of bends of the currently selected edge with the
     * smallest number of bends of all selected edges.
     * 
     * @return the smallest number of bends TODO obsolete?
     */
    public int getBendsCount() {
        int bendsCount = Integer.MAX_VALUE;
        for (Attributable attributable : attributables) {
            Edge edge = (Edge) attributable;
            try {
                // get CollectionAttribute "bend"
                CollectionAttribute bend = (CollectionAttribute) edge
                        .getAttribute(GraphicAttributeConstants.BENDS_PATH);
                int bendNum = bend.getCollection().values().size();
                if (bendsCount > bendNum) {
                    bendsCount = bendNum;
                }
            } catch (AttributeNotFoundException anfe) {
                // at least one selected edge has no bends
                return 0;
            }
        }
        return bendsCount;
    }

    private void saveAttributes(Attribute[] attrs) {
        if (attributeList == null) {
            attributeList = new LinkedList<Attribute>();
        }
        for (Attribute attr : attrs) {
            attributeList.add(attr);
        }
    }

    private void saveAttribute(Attribute attr) {
        if (attributeList == null) {
            attributeList = new LinkedList<Attribute>();
        }
        attributeList.add(attr);
    }

    /**
     * Reacts on a vec change event by updating the attributables and posting
     * undo information.
     * 
     * @param event
     *            the event describing the action
     */
    public void vecChanged(VECChangeEvent event) {
        ValueEditComponent source = event.getSource();
        listenerManager.transactionStarted(source);
        postUndoInfo(source.getDisplayables());
        source.setValue();
        listenerManager.transactionFinished(source);
    }

    /**
     * Post the information of the currently displayed attributes for allowing
     * undo/redo of changes.
     */
    public void postUndoInfo() {
        HashMap<Attribute, Object> attributeToOldValueMap = new LinkedHashMap<Attribute, Object>();

        for (Attribute attr : attributeList) {
            if (attr.getAttributable() instanceof GraphElement) {
                attributeToOldValueMap.put(attr, ((Attribute) attr.copy())
                        .getValue());
            }
        }
        if (attributeToOldValueMap.isEmpty())
            return;

        ChangeAttributesEdit aEdit = new ChangeAttributesEdit(
                attributeToOldValueMap, this.geMap);
        this.undoSupport.postEdit(aEdit);
    }

    public void postUndoInfo(Displayable<?>[] displayables) {
        HashMap<Attribute, Object> attributeToOldValueMap = new LinkedHashMap<Attribute, Object>();
        Attribute attr;
        for (Displayable<?> disp : displayables) {
            attr = (Attribute) disp;
            if (attr.getAttributable() instanceof GraphElement) {
                attributeToOldValueMap.put(attr, ((Attribute) attr.copy())
                        .getValue());
            }
        }
        if (attributeToOldValueMap.isEmpty())
            return;

        ChangeAttributesEdit aEdit = new ChangeAttributesEdit(
                attributeToOldValueMap, this.geMap);
        this.undoSupport.postEdit(aEdit);
    }

    public void addListener(ValueEditComponent vec) {
        vec.addVECChangeListener(this);
    }

    /**
     * Resets the panel. Should be called before new VECs are displayed in the
     * editPanel.
     */
    public void reset() {
        this.vecAttrs = new HashMap<ValueEditComponent, Collection<Attribute>>();
        this.removeAll();
        this.editPanel = new JPanel();
        this.editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.PAGE_AXIS));
        editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.displayedVEC = new LinkedList<ValueEditComponent>();
        JScrollPane tableScroll = new JScrollPane(this.editPanel);
        tableScroll.getVerticalScrollBar().setUnitIncrement(10);

        this.add(tableScroll, BorderLayout.CENTER);
        this.attributeList = null;
    }

    /**
     * Returns a list containing the currently displayed VECs.
     * 
     * @return this displayedVEC
     */
    public List<ValueEditComponent> getDisplayedVEC() {
        return this.displayedVEC;
    }

    /**
     * Returns this editPanel.
     * 
     * @return this editPanel
     */
    public JPanel getEditPanel() {
        return this.editPanel;
    }

    /**
     * Returns the mapping of VECs and corresponding attributes.
     * 
     * @return this vecAttrs
     */
    public Map<ValueEditComponent, Collection<Attribute>> getVecAttrs() {
        return this.vecAttrs;
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
