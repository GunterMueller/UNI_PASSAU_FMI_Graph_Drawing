//=============================================================================
//
//   GroupNodeLabel.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
// $Id: GroupNodeLabel.java 5766 2010-05-07 18:39:06Z gleissner $

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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.editcomponent.ValueEditComponent;

/**
 * Class for displaying the attributes of node labels. Additional buttons allow
 * the removal of a label. Another button makes it possible to add new labels.
 */
public class GroupNodeLabel extends SemanticGroup implements ActionListener,
        ListSelectionListener, PropertyChangeListener {

    /** The length of the path where labels are located at. */
    private final int LABEL_PREFIX_LENGTH = GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH
            .length()
            + Attribute.SEPARATOR.length();

    /** The "Remove label" button. */
    private JButton removeButton;

    /** The list with the relative paths to the label attributes. */
    private List<String> relPaths = new LinkedList<String>();

    /** The number of attributes of each label. */
    private int oneLabelAttributesCount;

    /** The editPanel of this group. */
    private DefaultEditPanel defaultEditPanel;

    /** The booled attributes of the currently available labels. */
    private List<BooledAttribute> attributes;

    /** The path to the attribute of the current list selection. */
    private String currentListSelection;

    /** The list containing the labels. */
    private JList list;

    /** The panel containting the VECs. */
    private JPanel vecPanel;

    /** The VECs that are displayed by this group. */
    private ValueEditComponent[] vecs;

    /** The label for the horizontal offset. */
    JLabel horLabel = new JLabel("horizontal:", SwingConstants.RIGHT);

    /** The label for the vertical offset. */
    JLabel vertLabel = new JLabel("vertical:", SwingConstants.RIGHT);

    /**
     * Constructs a new GroupNodeLabel and sets its paths.
     * 
     * @param name
     *            the name of the group
     */
    public GroupNodeLabel(String name) {
        super(name);
        // if you change this, don't forget to change doShowVECs!
        // these paths are relative as we don't know exactly where the labels
        // are
        this.relPaths.add(".label");
        this.relPaths.add(".position.relHor");
        this.relPaths.add(".position.relVert");
        this.relPaths.add(".position.localAlign"); // not displayed
        this.relPaths.add(".alignment");
        this.relPaths.add(".textcolor");
        this.relPaths.add(".font");
        this.relPaths.add(".fontSize");

        this.attributePaths = new LinkedList<String>();
    }

    /**
     * Finds the labels of nodes and adds their paths to
     * <code>attributePaths</code>. Labels that are not present in all
     * attributables (i.e. nodes) will not be added.
     * 
     * @param attributables
     *            the attributables to be searched for labels
     * @param attrs
     *            the attributes that will be searched for labels (needed for
     *            recursion)
     */
    public void findLabelPaths(List<? extends Attributable> attributables,
            Collection<Attribute> attrs) {
        if (attrs == null) {
            this.attributePaths = new LinkedList<String>();
            CollectionAttribute attribute = attributables.get(0)
                    .getAttributes();
            attrs = attribute.getCollection().values();
        }

        for (Attribute nextAttribute : attrs) {
            if (nextAttribute instanceof NodeLabelAttribute) {
                String path = nextAttribute.getPath();

                // add absolute paths to attributePaths
                for (int i = 0; i < this.relPaths.size(); i++) {
                    boolean doAdd = false;
                    String absPath = path + relPaths.get(i);

                    for (Attributable attributable : attributables) {
                        try {
                            attributable.getAttribute(absPath);
                        } catch (AttributeNotFoundException anfe) {
                            // at least one attributable does not contain
                            // this attribute; so don't add its path
                            doAdd = false;
                            break;
                        }
                        doAdd = true;
                    }
                    if (doAdd) {
                        this.attributePaths.add(absPath);
                    } else {
                        // skip all other label attributes as at least one
                        // is not present in all attributables
                        break;
                    }
                }
            } else if (nextAttribute instanceof CollectionAttribute) {
                // recursively iterate through collection
                findLabelPaths(attributables,
                        ((CollectionAttribute) nextAttribute).getCollection()
                                .values());
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

        // the number of attributes of one label
        this.oneLabelAttributesCount = this.relPaths.size();

        this.defaultEditPanel = editPanel;
        this.attributes = attributes;

        // create list and put it into a scroll pane.
        DefaultListModel listModel = new DefaultListModel();
        this.list = new JList(listModel);
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScrollPane = new JScrollPane(list);

        Dimension listDim = new Dimension(150, 54);
        listScrollPane.setPreferredSize(listDim);
        listScrollPane.setMinimumSize(listDim);
        listScrollPane.setMaximumSize(listDim);
        listScrollPane.setSize(listDim);

        // create the panel containing list & buttons
        JPanel listPanel = new JPanel(new BorderLayout());

        JButton addButton = new JButton("Add new label");
        addButton.setActionCommand("add");
        addButton.addActionListener(this);

        this.removeButton = new JButton("Remove label");

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
        this.buildList(false);

        // fill editPanel
        listPanel.add(listScrollPane, BorderLayout.NORTH);
        listPanel.add(addButton, BorderLayout.WEST);
        listPanel.add(this.removeButton, BorderLayout.EAST);

        listPanel.setBorder(BorderFactory
                .createTitledBorder("available labels"));
        JComponent target = this.defaultEditPanel.getEditPanel();
        target.setLayout(new BoxLayout(target, BoxLayout.Y_AXIS));

        listPanel.setMaximumSize(listPanel.getPreferredSize());
        vecPanel.setMaximumSize(vecPanel.getPreferredSize());
        target.add(listPanel);
        target.add(vecPanel);
    }

    /**
     * Builds the list with the labels' values. If a label has an empty value
     * ("---"), the label's path is displayed.
     * 
     * @param calledByPropertyChange
     *            states if buildList is called by the propertyChanged function
     */
    private void buildList(boolean calledByPropertyChange) {

        // get number of labels
        int labelCount = 0;
        if (this.oneLabelAttributesCount != 0) {
            labelCount = (this.attributePaths.size() / oneLabelAttributesCount);
        }

        DefaultListModel listModel = (DefaultListModel) this.list.getModel();
        this.list.removeListSelectionListener(this);
        listModel.removeAllElements();
        this.list.removeAll();
        if (!calledByPropertyChange) {
            this.list.addListSelectionListener(this);
        }

        ListItem currentSelectionItem = null;

        // fill list
        for (int i = 0; i < labelCount; i++) {
            BooledAttribute booled = attributes
                    .get(i * oneLabelAttributesCount);
            Attribute attribute = booled.getAttribute();

            String path = attribute.getPath();
            String id = "";
            int indexOfLastSeparator = path.lastIndexOf(Attribute.SEPARATOR);
            if (indexOfLastSeparator > 0) {
                id = path.substring(LABEL_PREFIX_LENGTH, indexOfLastSeparator);
            }

            String labelLabel;

            if (calledByPropertyChange
                    && vecs != null
                    && ((Attribute) vecs[0].getDisplayable()).getPath().equals(
                            path)) {
                // update the list entry if the label name changed
                labelLabel = ((JTextField) vecs[0].getComponent()).getText();
            } else if (booled.getBool()) {
                // display the label's label value
                labelLabel = attribute.getValue().toString();
            } else {
                // display the label's path if not all attributables have the
                // same label value
                labelLabel = "(" + id + ")";
            }
            ListItem item = new ListItem(labelLabel, i);

            // add item at the correct alphabetical position
            int index;
            for (index = 0; index < listModel.getSize(); index++) {
                String aItem = ((ListItem) listModel.get(index)).getLabel();
                if (aItem.compareTo(labelLabel) > 0) {
                    break;
                }
            }
            listModel.add(index, item);

            if (id.equals(this.currentListSelection)) {
                currentSelectionItem = item;
            }
        }
        if (list.isSelectionEmpty() && !listModel.isEmpty()) {
            int currentSelectionIndex = 0;
            for (int i = 0; i < listModel.getSize(); i++) {
                if (listModel.get(i) == currentSelectionItem) {
                    currentSelectionIndex = i;
                }
            }
            // (re-)select list item
            this.list.ensureIndexIsVisible(currentSelectionIndex);
            this.list.setSelectedIndex(currentSelectionIndex);
        }
    }

    /**
     * Called whenever the value of the selection changes. Shows the attributes
     * of the selected label.
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
        this.vecs = new ValueEditComponent[oneLabelAttributesCount];

        // update currentListSelection
        String path = this.attributes.get(index * oneLabelAttributesCount)
                .getAttribute().getPath();
        int indexOfLastSeparator = path.lastIndexOf(Attribute.SEPARATOR);
        if (indexOfLastSeparator > 0) {
            this.currentListSelection = path.substring(LABEL_PREFIX_LENGTH,
                    indexOfLastSeparator);
        }

        // an array containing the JComponents for the selected label
        JComponent[] components = new JComponent[oneLabelAttributesCount];

        // an array containing the titles of the borders
        String[] titles = new String[this.oneLabelAttributesCount];

        for (int j = 0; j < oneLabelAttributesCount; j++) {

            int currentIndex = index * oneLabelAttributesCount + j;

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
        // attribute "label"
        Border labelBorder = BorderFactory.createTitledBorder(titles[0]);
        JPanel labelBorderPanel = new JPanel();
        labelBorderPanel.add(components[0]);
        labelBorderPanel.setBorder(labelBorder);
        vecPanel.add(labelBorderPanel);

        // attribute "alignment"
        Border alignBorder = BorderFactory.createTitledBorder(titles[4]);
        JPanel alignBorderPanel = new JPanel();
        alignBorderPanel.add(components[4]);
        alignBorderPanel.setBorder(alignBorder);
        vecPanel.add(alignBorderPanel);

        // attributes "absHor" and "absVert"
        Border offsBorder = BorderFactory.createTitledBorder("offset");
        SpringLayout offsBorderLayout = new SpringLayout();
        JPanel offsBorderPanel = new JPanel(offsBorderLayout);
        offsBorderPanel.add(horLabel);
        offsBorderPanel.add(components[1]);
        offsBorderPanel.add(vertLabel);
        offsBorderPanel.add(components[2]);
        offsBorderPanel.setBorder(offsBorder);

        offsBorderLayout.putConstraint(SpringLayout.NORTH, horLabel, 0,
                SpringLayout.NORTH, offsBorderPanel);
        offsBorderLayout.putConstraint(SpringLayout.WEST, horLabel, QSPACE,
                SpringLayout.WEST, offsBorderPanel);
        offsBorderLayout.putConstraint(SpringLayout.NORTH, components[1], 0,
                SpringLayout.NORTH, offsBorderPanel);
        offsBorderLayout.putConstraint(SpringLayout.WEST, components[1], SPACE,
                SpringLayout.EAST, horLabel);
        offsBorderLayout.putConstraint(SpringLayout.NORTH, vertLabel, SPACE,
                SpringLayout.SOUTH, components[1]);
        offsBorderLayout.putConstraint(SpringLayout.EAST, vertLabel, 0,
                SpringLayout.EAST, horLabel);
        offsBorderLayout.putConstraint(SpringLayout.NORTH, components[2],
                SPACE, SpringLayout.SOUTH, components[1]);
        offsBorderLayout.putConstraint(SpringLayout.WEST, components[2], SPACE,
                SpringLayout.EAST, vertLabel);
        offsBorderLayout.putConstraint(SpringLayout.EAST, offsBorderPanel, 0,
                SpringLayout.EAST, labelBorderPanel);
        offsBorderLayout.putConstraint(SpringLayout.SOUTH, offsBorderPanel, 0,
                SpringLayout.SOUTH, components[2]);
        vecPanel.add(offsBorderPanel);

        // attributes "textcolor" and "font"
        Border fontBorder = BorderFactory.createTitledBorder("font");
        SpringLayout fontBorderLayout = new SpringLayout();
        JPanel fontBorderPanel = new JPanel(fontBorderLayout);
        JLabel faceLabel = new JLabel("face:");
        fontBorderPanel.add(faceLabel);
        fontBorderPanel.add(components[6]);
        JLabel sizeLabel = new JLabel("size:");
        fontBorderPanel.add(sizeLabel);
        fontBorderPanel.add(components[7]);
        JLabel colLabel = new JLabel("colour:");
        fontBorderPanel.add(colLabel);
        fontBorderPanel.add(components[5]);
        fontBorderPanel.setBorder(fontBorder);

        fontBorderLayout.putConstraint(SpringLayout.NORTH, faceLabel, 0,
                SpringLayout.NORTH, fontBorderPanel);
        fontBorderLayout.putConstraint(SpringLayout.NORTH, components[6], 0,
                SpringLayout.NORTH, fontBorderPanel);
        fontBorderLayout.putConstraint(SpringLayout.EAST, faceLabel, 0,
                SpringLayout.EAST, colLabel);
        fontBorderLayout.putConstraint(SpringLayout.WEST, components[6], SPACE,
                SpringLayout.EAST, colLabel);

        fontBorderLayout.putConstraint(SpringLayout.NORTH, sizeLabel, SPACE,
                SpringLayout.SOUTH, components[6]);
        fontBorderLayout.putConstraint(SpringLayout.NORTH, components[7],
                SPACE, SpringLayout.SOUTH, components[6]);
        fontBorderLayout.putConstraint(SpringLayout.EAST, sizeLabel, 0,
                SpringLayout.EAST, colLabel);
        fontBorderLayout.putConstraint(SpringLayout.WEST, components[7], SPACE,
                SpringLayout.EAST, colLabel);

        fontBorderLayout.putConstraint(SpringLayout.NORTH, colLabel, SPACE,
                SpringLayout.SOUTH, components[7]);
        fontBorderLayout.putConstraint(SpringLayout.NORTH, components[5],
                SPACE, SpringLayout.SOUTH, components[7]);
        fontBorderLayout.putConstraint(SpringLayout.WEST, colLabel, 0,
                SpringLayout.WEST, fontBorderPanel);
        fontBorderLayout.putConstraint(SpringLayout.WEST, components[5], SPACE,
                SpringLayout.EAST, colLabel);

        fontBorderLayout.putConstraint(SpringLayout.EAST, fontBorderPanel, 0,
                SpringLayout.EAST, components[6]);
        fontBorderLayout.putConstraint(SpringLayout.SOUTH, fontBorderPanel,
                SPACE, SpringLayout.SOUTH, components[5]);
        vecPanel.add(fontBorderPanel);

        components[4].addPropertyChangeListener(new PropertyChangeListener() {
            // add a listener to the comboBox to enable or disable
            // the offset-vecs if the user changes the comboBox-value
            public void propertyChange(PropertyChangeEvent event) {
                if (!event.getPropertyName().equals(VEC_VALUE))
                    return;
                JComboBox combo = (JComboBox) vecs[4].getComponent();
                if (combo.getSelectedIndex() == 0) {
                    // enable fields
                    if (!vecs[1].isEnabled()) {
                        vecs[1].setEnabled(true);
                        vecs[2].setEnabled(true);
                        vertLabel.setEnabled(true);
                        horLabel.setEnabled(true);
                    }
                } else if (vecs[1].isEnabled()) {
                    // disable fields
                    vecs[1].setEnabled(false);
                    vecs[2].setEnabled(false);
                    vertLabel.setEnabled(false);
                    horLabel.setEnabled(false);
                }
            }
        });
        components[4].firePropertyChange(VEC_VALUE, true, false);

        this.defaultEditPanel.validate();
        this.defaultEditPanel.repaint();
    }

    /**
     * Updates the booledAttribute in this object's <code>attributes</code>
     * list. If a vec changed its <code>showEmpty</code>-value, the
     * corresponding booledAttribute needs to be updated; otherwise it would
     * make the vec display a wrong showEmpty-state. Rebuilds the list if the
     * label's label changed.
     * 
     * @param event
     *            the event describing the change
     */
    public void propertyChange(PropertyChangeEvent event) {

        if (!event.getPropertyName().equals(VEC_VALUE))
            return;
        ListItem item = (ListItem) this.list.getSelectedValue();
        if (item == null)
            return;
        int index = item.getIndex();
        JComponent component = (JComponent) event.getSource();

        for (int i = 0; i < this.oneLabelAttributesCount; i++) {
            ValueEditComponent vec = this.vecs[i];
            BooledAttribute booled = this.attributes.get(index
                    * this.oneLabelAttributesCount + i);

            Attribute attribute = booled.getAttribute();

            if (vec != null && vec.getComponent().equals(component)) {

                boolean showEmpty = vec.getShowEmpty();
                if (booled.getBool() == showEmpty) {
                    booled.setBool(!showEmpty);
                }
                if (attribute.getId().equals(GraphicAttributeConstants.LABEL)
                        && (vec.getDisplayable().equals(attribute))) {
                    // update list if the label's name changed
                    buildList(true);
                    this.list.addListSelectionListener(this);
                    this.list.repaint();
                }
                return;
            }
        }
    }

    /**
     * Displayes a dialog for adding a new label after the user clicks the "Add
     * new label" button and adds the new label.
     * 
     * @param event
     *            the event describing the action
     */
    public void actionPerformed(ActionEvent event) {

        if (event.getActionCommand().equals("add")) {
            // add new label
            String id = this.defaultEditPanel.getNewLabelId();

            String value = JOptionPane.showInputDialog(null,
                    "Enter new label:", "Add a new label",
                    JOptionPane.PLAIN_MESSAGE);

            if (value != null) {
                // create new label
                NodeLabelAttribute newLabel = new NodeLabelAttribute(id, value);
                this.currentListSelection = id;
                this.defaultEditPanel.addLabel(newLabel);
            }
        } else {
            // remove label
            String command = ((JButton) event.getSource()).getActionCommand();
            int index = (new Integer(command)).intValue();

            // get the path to the label by reading the path
            // to "XXX.label" and skipping the ".label"-part
            // and the leading SEPARATOR
            String pathToLabelLabel = attributePaths.get(index
                    * oneLabelAttributesCount);
            String pathToLabel = pathToLabelLabel.substring(Attribute.SEPARATOR
                    .length(), pathToLabelLabel
                    .lastIndexOf(Attribute.SEPARATOR));
            this.currentListSelection = null;
            defaultEditPanel.removeLabel(pathToLabel);
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
