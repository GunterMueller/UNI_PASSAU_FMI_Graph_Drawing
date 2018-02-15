//=============================================================================
//
//   GroupEdgeBend.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
// $Id: GroupEdgeBend.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import static org.graffiti.plugins.editcomponents.yagi.GraffitiValueEditComponents.VEC_VALUE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.editcomponent.ValueEditComponent;

/**
 * Class for displaying the attributes of edge bends. Additional buttons allow
 * the removal of a bend. Another button makes it possible to add new bends.
 */
public class GroupEdgeBend extends SemanticGroup implements ActionListener,
        ListSelectionListener, PropertyChangeListener {

    /** The "Remove bend" button. */
    private JButton removeButton;

    /** The booled attributes of the currently available bends. */
    private List<BooledAttribute> attributes;

    /** The list containing the bends. */
    private JList list;

    /** The label of the current list selection. */
    private String currentListSelection;

    /** The panel containting the VECs. */
    private JPanel vecPanel;

    /** The VECs that are displayed by this group. */
    private ValueEditComponent[] vecs;

    /** The list with the relative paths to the bend attributes. */
    private List<String> relPaths = new LinkedList<String>();

    /** The number of attributes of each bend. */
    private int oneBendAttributesCount;

    /** The editPanel of this group. */
    private DefaultEditPanel defaultEditPanel;

    /**
     * Constructs a new GroupEdgeBend and sets its paths.
     * 
     * @param name
     *            the name of the group
     */
    public GroupEdgeBend(String name) {
        super(name);
        // if you change this, don't forget to change doShowVECs!
        // these paths are relative as we don't know exactly where the bends
        // are
        this.relPaths.add(".x");
        this.relPaths.add(".y");

        this.attributePaths = new LinkedList<String>();
    }

    /**
     * Finds the bends of edges and adds their paths to
     * <code>attributePaths</code>. Bends that are not present in all
     * attributables (i.e. edges) will not be added.
     * 
     * @param attributables
     *            the attributables to be searched for bends
     */
    public void findBendPaths(List<? extends Attributable> attributables) {
        this.attributePaths = new LinkedList<String>();
        Attribute attribute;
        try {
            attribute = attributables.get(0).getAttribute(
                    Attribute.SEPARATOR + GraphicAttributeConstants.BENDS_PATH);
        } catch (AttributeNotFoundException anfe) {
            return;
        }
        Collection<Attribute> attrs = ((CollectionAttribute) attribute)
                .getCollection().values();

        // iterate through all attributes of the first attributable
        for (Attribute nextAttribute : attrs) {
            if (nextAttribute instanceof CoordinateAttribute) {
                String path = nextAttribute.getPath();

                // add absolute paths to attributePaths
                for (int i = 0; i < this.relPaths.size(); i++) {
                    boolean doAdd = false;
                    String absPath = path + relPaths.get(i);

                    for (Attributable attributable : attributables) {
                        try {
                            attributable.getAttribute(absPath);
                            doAdd = true;
                        } catch (AttributeNotFoundException anfe) {
                            // at least one attributable does not contain
                            // this attribute; so don't add its path
                            doAdd = false;
                            break;
                        }
                    }
                    if (doAdd) {
                        this.attributePaths.add(absPath);
                    } else {
                        // skip all other bend attributes as at least one
                        // is not present in all attributables
                        break;
                    }
                }
            }
        }
    }

    /**
     * Shows the attributes of this semantic group.
     * 
     * @see org.graffiti.plugins.inspectors.yagi.SemanticGroup#showVECs(org.graffiti.plugins.inspectors.yagi.DefaultEditPanel,
     *      java.util.List)
     * @param editPanel
     *            the editPanel where the VECs will be added to
     * @param attributes
     *            the booled attributes of the semantic group
     */
    @Override
    protected void doShowVECs(DefaultEditPanel editPanel,
            List<BooledAttribute> attributes) {

        this.vecPanel = new JPanel();
        this.vecPanel.setLayout(new BoxLayout(this.vecPanel, BoxLayout.Y_AXIS));

        // the number of attributes of one bend
        this.oneBendAttributesCount = this.relPaths.size();

        // the number of bends
        int bendCount = 0;
        if (this.oneBendAttributesCount != 0) {
            bendCount = (this.attributePaths.size() / oneBendAttributesCount);
        }

        this.defaultEditPanel = editPanel;
        this.attributes = attributes;

        // create list and put it into a scroll pane.
        DefaultListModel listModel = new DefaultListModel();
        this.list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);

        JScrollPane listScrollPane = new JScrollPane(list);

        Dimension listDim = new Dimension(100, 54);
        listScrollPane.setPreferredSize(listDim);
        listScrollPane.setMinimumSize(listDim);
        listScrollPane.setMaximumSize(listDim);
        listScrollPane.setSize(listDim);

        int bendPrefixLength = GraphicAttributeConstants.BENDS_PATH.length()
                + 2 * Attribute.SEPARATOR.length();

        // fill list
        for (int i = 0; i < bendCount; i++) {
            BooledAttribute booled = attributes.get(i * oneBendAttributesCount);
            String bendName = booled.getAttribute().getPath();
            int indexOfLastSeparator = bendName
                    .lastIndexOf(Attribute.SEPARATOR);
            if (indexOfLastSeparator > 0) {
                bendName = bendName.substring(bendPrefixLength,
                        indexOfLastSeparator);
            }
            ListItem item = new ListItem(bendName, i);

            // add item at the correct alphabetical position
            int index;
            for (index = 0; index < listModel.getSize(); index++) {
                String aItem = ((ListItem) listModel.get(index)).getLabel();
                if (aItem.compareTo(bendName) > 0) {
                    break;
                }
            }
            listModel.add(index, item);

            // reselect list item
            if (bendName.equals(this.currentListSelection)) {
                this.list.ensureIndexIsVisible(index);
                this.list.setSelectedIndex(index);
            }
        }

        // create the panel containing list & buttons
        JPanel listPanel = new JPanel(new BorderLayout());

        JButton addButton = new JButton("Add new bend");
        addButton.setActionCommand("add");
        addButton.addActionListener(this);

        this.removeButton = new JButton("Remove bend");
        if (list.isSelectionEmpty() && !listModel.isEmpty()) {
            this.list.ensureIndexIsVisible(0);
            this.list.setSelectedIndex(0);
        }

        // enable or disable remove button
        Object selected = this.list.getSelectedValue();
        if (selected == null) {
            this.removeButton.setEnabled(false);
        } else {
            this.removeButton.setEnabled(true);
            int index = ((ListItem) selected).getIndex();
            this.removeButton.setEnabled(true);
            this.removeButton.setActionCommand("" + index);
        }
        this.removeButton.addActionListener(this);

        // fill editPanel
        listPanel.add(listScrollPane, BorderLayout.NORTH);
        listPanel.add(addButton, BorderLayout.WEST);
        listPanel.add(this.removeButton, BorderLayout.EAST);

        listPanel
                .setBorder(BorderFactory.createTitledBorder("available bends"));
        JComponent target = this.defaultEditPanel.getEditPanel();
        target.setLayout(new BoxLayout(target, BoxLayout.Y_AXIS));

        listPanel.setMaximumSize(listPanel.getPreferredSize());
        vecPanel.setMaximumSize(vecPanel.getPreferredSize());
        target.add(listPanel);
        target.add(vecPanel);
    }

    /**
     * Called whenever the value of the list selection changes. Shows the
     * attributes of the selected bend.
     * 
     * @param event
     *            the event describing the selection
     */
    public void valueChanged(ListSelectionEvent event) {

        if (this.attributes.isEmpty() || event.getValueIsAdjusting())
            return;

        Object selected = this.list.getSelectedValue();
        if (selected == null) {
            // nothing selected
            this.removeButton.setEnabled(false);
            return;
        }
        int index = ((ListItem) selected).getIndex();
        this.removeButton.setEnabled(true);
        this.removeButton.setActionCommand("" + index);
        this.vecPanel.removeAll();
        this.vecs = new ValueEditComponent[oneBendAttributesCount];

        // an array containing the JComponents for the selected bend
        JComponent[] components = new JComponent[oneBendAttributesCount];

        // an array containing the titles of the borders
        String[] titles = new String[oneBendAttributesCount];

        for (int j = 0; j < oneBendAttributesCount; j++) {

            int currentIndex = index * oneBendAttributesCount + j;

            BooledAttribute booled = this.attributes.get(currentIndex);
            if (booled == null) {
                // someone deleted a standard attribute...
                System.err.println("Can't display attribute: "
                        + this.attributePaths.get(currentIndex));
                titles[j] = "";
                components[j] = new JLabel();
            } else {

                // create VEC
                Attribute attribute = booled.getAttribute();
                titles[j] = attribute.getId();

                ValueEditComponent vec = this.defaultEditPanel.createVEC(
                        attribute, this.defaultEditPanel.getEditComponentMap()
                                .get(attribute.getClass()));
                components[j] = vec.getComponent();
                vec.setShowEmpty(!booled.getBool());
                components[j].addPropertyChangeListener(this);
                defaultEditPanel.addListener(vec);
                this.vecs[j] = vec;
            }
        }

        // add borders
        for (int i = 0; i < this.oneBendAttributesCount; i++) {
            Border comBorder = BorderFactory.createTitledBorder(titles[i]);
            JPanel borderPanel = new JPanel();
            borderPanel.add(components[i]);
            borderPanel.setBorder(comBorder);
            borderPanel.setMaximumSize(borderPanel.getPreferredSize());
            vecPanel.add(borderPanel);
        }

        this.defaultEditPanel.validate();
        this.defaultEditPanel.repaint();
    }

    /**
     * Updates the booledAttribute in this object's <code>attributes</code>
     * list. If a VEC changed its <code>showEmpty</code>-value, the
     * corrseponding booledAttribute needs to be updated; otherwise it would
     * make the vec display a wrong showEmpty-status.
     * 
     * @param event
     *            the event describing the change
     */
    public void propertyChange(PropertyChangeEvent event) {
        if (!event.getPropertyName().equals(VEC_VALUE))
            return;
        JComponent source = (JComponent) event.getSource();
        int index = ((ListItem) this.list.getSelectedValue()).getIndex();

        for (int i = 0; i < this.oneBendAttributesCount; i++) {
            ValueEditComponent vec = this.vecs[i];
            if (vec != null && vec.getComponent().equals(source)) {
                BooledAttribute attribute = this.attributes.get(index
                        * this.oneBendAttributesCount + i);
                boolean showEmpty = vec.getShowEmpty();
                if (attribute.getBool() == showEmpty) {
                    attribute.setBool(!showEmpty);
                }
                return;
            }
        }
    }

    /**
     * Called after a user clicks the "Add new bend" or "Remove bend" button.
     * 
     * @param event
     *            the event describing the action
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("add")) {
            // add new bend
            String id = GraphicAttributeConstants.BEND;
            if (list.getSelectedIndex() >= 0) {
                String currentBendLabel = ((ListItem) list.getSelectedValue())
                        .getLabel();
                int currentBendId = Integer.parseInt(currentBendLabel
                        .substring(GraphicAttributeConstants.BEND.length()));
                id += "" + (currentBendId + 1);
                this.currentListSelection = id;
                this.defaultEditPanel.addBends(id, currentBendLabel);

            } else {
                id += "" + 0;
                this.currentListSelection = null;
                this.defaultEditPanel.addBends(id, "");
            }
        } else {
            // remove bend
            String command = ((JButton) event.getSource()).getActionCommand();
            int index = (new Integer(command)).intValue();

            // get the path to the bend by reading the path
            // to "xxx.x" and skipping the ".x"-part
            // and the leading SEPARATOR
            String pathToBendX = attributePaths.get(index
                    * oneBendAttributesCount);
            String pathToBend = pathToBendX.substring(Attribute.SEPARATOR
                    .length(), pathToBendX.lastIndexOf(Attribute.SEPARATOR));
            this.currentListSelection = null;
            defaultEditPanel.removeBend(pathToBend);
        }
    }

    /**
     * A class combining a String and an int. Objects of this class will be
     * added to the list.
     */
    class ListItem {

        /** The label of the item that will be displayed in the list. */
        private String label;

        /** The index of the item. */
        private int index;

        /**
         * Constructs a new ListItem and sets its class variables.
         * 
         * @param label
         *            the new label
         * @param index
         *            the new index
         */
        public ListItem(String label, int index) {
            this.label = label;
            this.index = index;
        }

        /**
         * Returns this ListItem's label.
         * 
         * @return the label of this ListItem
         */
        public String getLabel() {
            return this.label;
        }

        /**
         * Returns this ListItem's index.
         * 
         * @return the index of this ListItem
         */
        public int getIndex() {
            return this.index;
        }

        /**
         * Returns a String-representation of this ListItem.
         * 
         * @return the label of this ListItem
         */
        @Override
        public String toString() {
            return this.label;
        }
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
