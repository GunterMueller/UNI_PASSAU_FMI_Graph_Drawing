// =============================================================================
//
//   DefaultEditPanel.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DefaultEditPanel.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.inspectors.defaults;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.event.ListenerManager;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.ToolTipHelper;
import org.graffiti.plugin.editcomponent.StandardValueEditComponent;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugin.inspector.EditPanel;
import org.graffiti.undo.ChangeAttributesEdit;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;

/**
 * Represents the edit panel in the inspector.
 * 
 * @version $Revision: 5772 $
 */
public class DefaultEditPanel extends EditPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 8273933593758133201L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(DefaultEditPanel.class.getName());

    /** Action for the apply button. */
    private Action applyAction;

    /** The attribute that was last specified by buildTable. */
    private Attribute displayedAttr;

    // /** DOCUMENT ME! */
    // private Dimension maxEditFieldSize = new Dimension(
    // Inspector.DEFAULT_WIDTH / 2, 0);
    //
    // /** DOCUMENT ME! */
    // private Dimension maxIdSize = new Dimension(
    // ((Inspector.DEFAULT_WIDTH * 2) / 3) - 3, 0);
    //
    // /** DOCUMENT ME! */
    // private Dimension minEditFieldSize = new Dimension(
    // Inspector.DEFAULT_WIDTH / 2, 0);
    //
    // /** DOCUMENT ME! */
    // private Dimension minIdSize = new Dimension(
    // (Inspector.DEFAULT_WIDTH / 3) - 3, 0);
    //
    // /** DOCUMENT ME! */
    // private Dimension prefEditFieldSize = new Dimension(
    // Inspector.DEFAULT_WIDTH / 2, 0);
    //
    // /** DOCUMENT ME! */
    // private Dimension prefIdSize = new Dimension(
    // ((Inspector.DEFAULT_WIDTH * 2) / 3) - 3, 0);

    /**
     * Mapping between a ValueEditComponent and the List of attributes that are
     * linked to it.
     */
    private HashMap<ValueEditComponent, Collection<Attribute>> vecAttrs;

    /** Button used to add an attribute. */
    private JButton addAttributeButton;

    /** Button used to apply all changes. */
    private JButton applyButton;

    /** Button used to remove an attribute. */
    private JButton removeAttributeButton;

    /** A panel for the add and removeAttributeButtons. */
    private JPanel applyButtonPanel;

    /** A panel for the add and removeAttributeButtons. */
    private JPanel attributeButtonPanel;

    /** Holds the edit fields cells. */
    private JPanel editFieldPanel;

    /** Holds the id cells. */
    private JPanel idPanel;

    /** DOCUMENT ME! */
    private JScrollPane tableScroll;

    /** Splits table into id and edit field. */
    private JSplitPane tableSplit;

    /** DOCUMENT ME! */
    private List<ValueEditComponent> displayedVEC;

    /** DOCUMENT ME! */
    private List<? extends Attributable> graphElements;

    /** Holds the ListenerManager where the panel is registered. */
    private ListenerManager listenerManager;

    /** Stores all edit components. */
    private Map<Class<?>, Class<?>> editComponentsMap;

    /**
     * Instantiates a new edit panel.
     */
    public DefaultEditPanel() {
        super();

        this.displayedVEC = new LinkedList<ValueEditComponent>();

        // this.attributeTypeMap = new HashMap();
        this.editComponentsMap = new HashMap<Class<?>, Class<?>>();

        applyButton = new JButton("Apply");
        applyButton.setDefaultCapable(true);
        applyButton.setMnemonic(1);
        applyAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // TODO: see if this can be avoided ...:
                // emulate ENTER that has been caught to call this action
                // only for JSpinners (they dont update getValue())
                try {
                    ((JFormattedTextField) e.getSource()).commitEdit();
                } catch (ParseException pe) {
                    // is of no interest if it occurs
                } catch (ClassCastException cce) {
                    // is of no interest if it occurs
                }

                // send transactionStarted event or remove inspector as
                // attributelistener
                listenerManager.transactionStarted(applyButton);

                HashMap<Attribute, Object> attributeToOldValueMap = new LinkedHashMap<Attribute, Object>();

                for (ValueEditComponent vec : displayedVEC) {
                    Collection<Attribute> attributes = vecAttrs.get(vec);

                    for (Attribute attr : attributes) {
                        // originalAttributes.add(attr);
                        // TODO:fix finally the access to the attribute values
                        // over the getValue().
                        // It is currently only a temporary solution for
                        // nonfixed
                        // access.
                        attributeToOldValueMap.put(attr, ((Attribute) attr
                                .copy()).getValue());
                        // attributeToOldValueMap.put(attr, attr.getValue());

                        logger
                                .finer("path of an attribute displayed in inspector "
                                        + attr.getPath());

                    }

                    setValues(vec);
                }

                assert (geMap != null);

                ChangeAttributesEdit aEdit = new ChangeAttributesEdit(
                        attributeToOldValueMap, geMap);
                undoSupport.postEdit(aEdit);
                listenerManager.transactionFinished(applyButton);

                // send transactionFinished event or add inspector as
                // attributelistener
            }
        };
        applyButton.addActionListener(applyAction);

        addAttributeButton = new JButton("Add");
        addAttributeButton.addActionListener(new AddListener());

        // addLabelAttributeButton = new JButton("Add label");
        // addLabelAttributeButton.addActionListener(new AddLabelListener());
        removeAttributeButton = new JButton("Remove");
        removeAttributeButton.addActionListener(new RemoveListener());

        idPanel = new JPanel();

        // idPanel.setMaximumSize(maxIdSize);
        // idPanel.setMinimumSize(minIdSize);
        // idPanel.setPreferredSize(prefIdSize);
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.Y_AXIS));

        editFieldPanel = new JPanel();
        editFieldPanel
                .setLayout(new BoxLayout(editFieldPanel, BoxLayout.Y_AXIS));

        // editFieldPanel.setPreferredSize(prefEditFieldSize);

        // editFieldPanel.setMaximumSize(maxEditFieldSize);
        // editFieldPanel.setPreferredSize(prefEditFieldSize);
        // editFieldPanel.setMinimumSize(minEditFieldSize);

        tableSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, idPanel,
                editFieldPanel);
        // tableSplit.resetToPreferredSizes();
        tableSplit.setDividerLocation(150);
        tableSplit.setDividerSize(3);
        tableSplit.setOneTouchExpandable(true);
        // tableSplit.setPreferredSize(new Dimension(Inspector.DEFAULT_WIDTH,
        // 1000));

        // tableSplit.setPreferredSize(null);
        // tableSplit.setMinimumSize(new Dimension(Inspector.DEFAULT_WIDTH,
        // 200));
        // tableSplit.setMaximumSize(new Dimension(Inspector.DEFAULT_WIDTH,
        // 300));
        // tableSplit.revalidate();
        tableScroll = new JScrollPane(tableSplit);
        // tableScroll.setPreferredSize(new Dimension(Inspector.DEFAULT_WIDTH,
        // 200));
        // setPreferredSize(new Dimension(Inspector.DEFAULT_WIDTH, 200));

        // tableScroll.setMaximumSize(new Dimension(Inspector.DEFAULT_WIDTH,
        // 2000));
        tableScroll.getVerticalScrollBar().setUnitIncrement(10);

        this.attributeButtonPanel = new JPanel();
        this.attributeButtonPanel.setLayout(new BorderLayout());
        this.attributeButtonPanel.add(this.addAttributeButton,
                BorderLayout.WEST);

        // this.attributeButtonPanel.add
        // (this.addLabelAttributeButton, BorderLayout.CENTER);
        this.attributeButtonPanel.add(this.removeAttributeButton,
                BorderLayout.EAST);

        this.applyButtonPanel = new JPanel();
        this.applyButtonPanel.setLayout(new BorderLayout());
        this.applyButtonPanel.add(this.applyButton, BorderLayout.CENTER);

        this.setLayout(new BorderLayout());
        this.add(this.attributeButtonPanel, BorderLayout.NORTH);
        this.add(tableScroll, BorderLayout.CENTER);
        this.add(this.applyButtonPanel, BorderLayout.SOUTH);

        this.revalidate();

        // call apply when user hits enter
        this.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0),
                "apply");
        this.getActionMap().put("apply", applyAction);

        // tableSplit.addComponentListener(new MyComponentListener());
    }

    /**
     * Sets the map of editcomponents to the given map.
     * 
     * @param map
     *            DOCUMENT ME!
     */
    @Override
    public void setEditComponentMap(Map<Class<?>, Class<?>> map) {
        this.editComponentsMap = map;
    }

    /**
     * Sets the ListenerManager.
     * 
     * @param lm
     *            DOCUMENT ME!
     */
    @Override
    public void setListenerManager(ListenerManager lm) {
        this.listenerManager = lm;
    }

    /**
     * Builds the table that is used for editing attributes from scratch.
     * 
     * @param treeNode
     *            root attribute.
     * @param attributables
     *            DOCUMENT ME!
     */
    @Override
    public void buildTable(DefaultMutableTreeNode treeNode,
            List<? extends Attributable> attributables) {
        Attribute collAttr = ((BooledAttribute) treeNode.getUserObject())
                .getAttribute();

        // if (this.displayedAttr != collAttr) {
        this.displayedAttr = collAttr;
        this.graphElements = attributables;

        this.vecAttrs = new HashMap<ValueEditComponent, Collection<Attribute>>();

        idPanel.removeAll();
        editFieldPanel.removeAll();
        this.displayedVEC = new LinkedList<ValueEditComponent>();
        addValueEditComponents(idPanel, editFieldPanel, treeNode, graphElements);
        validate();
        repaint();

        // }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param attr
     *            DOCUMENT ME!
     */
    @Override
    public void updateTable(Attribute attr) {
        // if (this.graphElements.size() > 1) {
        // buildTable(this.rootNode, this.graphElements);
        // } else {
        updateVECs(attr);

        // }
    }

    // /**
    // * @param className a String containing the absolute path to the Attribute
    // * to be edited.
    // * @param ecClass a Class instance of a sublcass of EditComponent for
    // * editing the Attribute.
    // */
    // public void addAttributeType(String className, Class ecClass) {
    // this.attributeTypeMap.put(className, ecClass);
    // }
    // /**
    // * Sets the map of attribute types to the given map.
    // */
    // public void setAttributeTypeMap(Map map) {
    // this.attributeTypeMap = map;
    // }

    /**
     * Updates all attributes linked with the given ValueEditComponent to the
     * value displayed by the ValueEditComponent.
     * 
     * @param vec
     *            DOCUMENT ME!
     */
    private void setValues(ValueEditComponent vec) {
        if (!vec.isEnabled())
            return;

        for (Attribute attr : vecAttrs.get(vec)) {
            vec.setDisplayable(attr);
            vec.setValue();
        }
    }

    /**
     * Adds one row of the table.
     * 
     * @param idPanel
     *            DOCUMENT ME!
     * @param editFieldPanel
     *            DOCUMENT ME!
     * @param attribute
     *            DOCUMENT ME!
     * @param ecClass
     *            DOCUMENT ME!
     * @param showValue
     *            DOCUMENT ME!
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    private void addRow(JPanel idPanel, JPanel editFieldPanel,
            Attribute attribute, Class<?> ecClass, boolean showValue) {
        JTextField textField = new JTextField(attribute.getId());
        String tttext = attribute.getPath();

        if (!"".equals(attribute.getDescription())) {
            tttext = "<html>" + tttext + ":<p>" + attribute.getDescription()
                    + "</html>";
        }

        textField.setToolTipText(tttext);

        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.setEditable(false);

        ValueEditComponent editComponent = null;
        try {
            // editComp = (ValueEditComponent)ecClass.newInstance();
            editComponent = (ValueEditComponent) InstanceLoader.createInstance(
                    ecClass, "org.graffiti.plugin.Displayable", attribute);

            JComponent addToolTipTo = editComponent.getComponent();
            ToolTipHelper.addToolTip(addToolTipTo, tttext);
        } catch (InstanceCreationException ice) {
            throw new RuntimeException(
                    "Could not create an instance of a ValueEditComponent class. "
                            + "This should not have happened. Possible reason: "
                            + "ValueEditComponent class has only constructor with "
                            + "parameters. " + ice);
        }

        editComponent.setEditFieldValue();

        JComponent editComponentViewComponent = editComponent.getComponent();

        textField.setMinimumSize(new Dimension(0, editComponentViewComponent
                .getMinimumSize().height));
        textField.setMaximumSize(new Dimension(
                textField.getMaximumSize().width, editComponentViewComponent
                        .getMaximumSize().height));
        textField.setPreferredSize(new Dimension(
                textField.getPreferredSize().width, editComponentViewComponent
                        .getPreferredSize().height));
        textField.setSize(new Dimension(textField.getSize().width,
                editComponentViewComponent.getSize().height));
        idPanel.add(textField);
        editFieldPanel.add(editComponentViewComponent);
        displayedVEC.add(editComponent);

        /*
         * when a spinner is used then its editor (or the textfield within the
         * editor) must be connected to the action event
         */
        JComponent inputComp = null;

        if (editComponentViewComponent instanceof JSpinner) {
            // get editor of spinner
            inputComp = ((JSpinner) editComponentViewComponent).getEditor();

            if (inputComp instanceof JSpinner.DefaultEditor) {
                // in this case, the TextField inside the editor has to be used
                inputComp = ((JSpinner.DefaultEditor) inputComp).getTextField();
            } else {
                inputComp = editComponentViewComponent;
            }
        } else {
            inputComp = editComponentViewComponent;
        }

        inputComp.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0),
                "apply");
        inputComp.getActionMap().put("apply", applyAction);
        inputComp.getInputMap(WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0),
                "apply");
        inputComp.getActionMap().put("apply", applyAction);
        inputComp.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0),
                "apply");
        inputComp.getActionMap().put("apply", applyAction);

        // save which attributes are dependent on this vec
        Collection<Attribute> attrList = this.vecAttrs.get(editComponent);

        if (attrList == null) {
            attrList = new HashSet<Attribute>();
            this.vecAttrs.put(editComponent, attrList);
        }

        String attPath = (attribute.getPath() + " ").substring(1).trim();

        for (Attributable a : graphElements) {
            attrList.add(a.getAttribute(attPath));
        }

        // editComp.setEnabled(showValue);
        editComponent.setShowEmpty(!showValue);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param attr
     *            DOCUMENT ME!
     * @param idPanel
     *            DOCUMENT ME!
     * @param editFieldPanel
     *            DOCUMENT ME!
     * @param showValue
     *            DOCUMENT ME!
     */
    private void addStandardRow(Attribute attr, JPanel idPanel,
            JPanel editFieldPanel, boolean showValue) {
        JTextField textField;

        ValueEditComponent standardVEC = new StandardValueEditComponent(attr);
        standardVEC.setDisplayable(attr);
        standardVEC.setEditFieldValue();

        // System.out.println("setattr = "+attr);
        JComponent editComponent = standardVEC.getComponent();
        textField = new JTextField(attr.getId());
        textField.setToolTipText(attr.getPath());
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.setEditable(false);

        textField.setMinimumSize(new Dimension(0, editComponent
                .getMinimumSize().height));
        textField.setMaximumSize(new Dimension(
                textField.getMaximumSize().width, editComponent
                        .getMaximumSize().height));
        textField.setPreferredSize(new Dimension(
                textField.getPreferredSize().width, editComponent
                        .getPreferredSize().height));
        textField.setSize(new Dimension(textField.getSize().width,
                editComponent.getSize().height));
        idPanel.add(textField);
        editFieldPanel.add(editComponent);
        displayedVEC.add(standardVEC);

        Collection<Attribute> attrList = this.vecAttrs.get(standardVEC);

        if (attrList == null) {
            attrList = new HashSet<Attribute>();
            this.vecAttrs.put(standardVEC, attrList);
        }

        String attPath = (attr.getPath() + " ").substring(1).trim();

        for (Attributable a : graphElements) {
            attrList.add(a.getAttribute(attPath));
        }

        standardVEC.setShowEmpty(!showValue);
    }

    /**
     * Puts text fields for the IDs in a panel.
     * 
     * @param idPanel
     * @param editFieldPanel
     * @param treeNode
     *            DOCUMENT ME!
     * @param attributables
     *            DOCUMENT ME!
     */
    private void addValueEditComponents(JPanel idPanel, JPanel editFieldPanel,
            DefaultMutableTreeNode treeNode,
            List<? extends Attributable> attributables) {
        // Attribute attr =
        // ((GraphElement)attrSel.getElements().get(0)).getAttributes();
        BooledAttribute booledAttr = (BooledAttribute) treeNode.getUserObject();
        Attribute attr = booledAttr.getAttribute();

        if (attr instanceof CollectionAttribute) {
            /*
             * if it is a CollectionAttribute, we check if there is a component
             * registered.
             */
            Class<?> ecClass = this.editComponentsMap.get(attr.getClass());

            if (ecClass != null) {
                // if we have a registered component to display it, add it
                this.addRow(idPanel, editFieldPanel, attr, ecClass, booledAttr
                        .getBool());
            } else {
                /*
                 * If no component is registered, we iterate through its
                 * collection and check if these attributes have a registered
                 * component
                 */

                DefaultMutableTreeNode child;
                BooledAttribute booledChild;

                for (int i = 0; i < treeNode.getChildCount(); i++) {
                    child = (DefaultMutableTreeNode) treeNode.getChildAt(i);
                    booledChild = (BooledAttribute) child.getUserObject();

                    Attribute attribute = booledChild.getAttribute();

                    ecClass = this.editComponentsMap.get(attribute.getClass());

                    if (ecClass != null) {
                        // if we have a registered component, add it
                        this.addRow(idPanel, editFieldPanel, attribute,
                                ecClass, booledChild.getBool());
                    } else {
                        // recursive call if no special component is registered
                        addValueEditComponents(idPanel, editFieldPanel, child,
                                attributables);
                    }
                }
            }
        } else if (attr instanceof CompositeAttribute) {
            /*
             * nearly the same for CompositeAttributes. Check is a component is
             * registered. If not, recursive call with its hierarchy form.
             */

            Class<?> ecClass = this.editComponentsMap.get(attr.getClass());

            if (ecClass != null) {
                this.addRow(idPanel, editFieldPanel, attr, ecClass, booledAttr
                        .getBool());
            } else {
                DefaultMutableTreeNode child;
                BooledAttribute booledChild;

                if (treeNode.getChildCount() == 0) {
                    addStandardRow(attr, idPanel, editFieldPanel, booledAttr
                            .getBool());
                } else {
                    for (int i = 0; i < treeNode.getChildCount(); i++) {
                        child = (DefaultMutableTreeNode) treeNode.getChildAt(i);
                        booledChild = (BooledAttribute) child.getUserObject();

                        Attribute attribute = booledChild.getAttribute();

                        ecClass = this.editComponentsMap.get(attribute
                                .getClass());
                        if (ecClass != null) {
                            // if we have a registered component, add it
                            this.addRow(idPanel, editFieldPanel, attribute,
                                    ecClass, booledChild.getBool());
                        } else {
                            // recursive call if no special component is
                            // registered
                            addValueEditComponents(idPanel, editFieldPanel,
                                    child, attributables);
                        }
                    }
                }
            }
        } else {
            /*
             * for non CollectionAttributes and non CompositeAttributes check
             * whether there exists a ValueEditComponent, if not use standard
             * edit component
             */

            Class<?> ecClass = this.editComponentsMap.get(attr.getClass());

            if (ecClass != null) {
                // if we have a registered component to display it, add it
                this.addRow(idPanel, editFieldPanel, attr, ecClass, booledAttr
                        .getBool());
            } else {
                addStandardRow(attr, idPanel, editFieldPanel, booledAttr
                        .getBool());
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param changedAttr
     *            DOCUMENT ME!
     */
    private void updateVECs(Attribute changedAttr) {
        boolean allSame;

        for (ValueEditComponent vec : displayedVEC) {
            Collection<Attribute> attrs = vecAttrs.get(vec);

            // if changedAttr and vec have nothing to do with each other ...
            if (changedAttr == null || attrs == null
                    || !attrs.contains(changedAttr)) {
                continue;
            }

            // good idea but one would have to update the vecs when the
            // scrollpane changes
            // Rectangle visRect = vec.getComponent().getVisibleRect();
            // if (visRect.getHeight() == 0 && visRect.getWidth() == 0) {
            // //System.out.println("skipping " + vec.getDisplayable());
            // continue;
            // }
            Object attrValue = changedAttr.getValue();

            allSame = true;

            for (Attribute att : attrs) {
                if (!att.getValue().equals(attrValue)) {
                    allSame = false;

                    break;
                }
            }

            if (allSame) {
                vec.setShowEmpty(false);
            } else {
                vec.setShowEmpty(true);
            }
        }
    }

    // private class MyComponentListener extends ComponentAdapter{
    // /**
    // * @see java.awt.event.ComponentAdapter#
    // * componentResized(java.awt.event.ComponentEvent)
    // */
    // public void componentMoved(ComponentEvent e) {
    // super.componentMoved(e);
    // updateAllVECs();
    // }
    //
    // public void componentResized(ComponentEvent e) {
    // super.componentResized(e);
    // updateAllVECs();
    // }
    //
    // }
    private class AddListener implements ActionListener {
        /**
         * DOCUMENT ME!
         * 
         * @param e
         *            DOCUMENT ME!
         */
        public void actionPerformed(ActionEvent e) {
            if (!(displayedAttr instanceof CollectionAttribute)) {
                JOptionPane.showMessageDialog(DefaultEditPanel.this,
                        "Can't add a sub attribute to a non "
                                + "CollectionAttribute like " + displayedAttr,
                        "Error!", JOptionPane.OK_OPTION);
            } else {
                // String response = JOptionPane.showInputDialog
                // (editPanel,
                // "New attribute will be appended to \"" +
                // displayedAttr.getPath() + Attribute.SEPARATOR +
                // "\": \nType ID, a space, a fully quantified attribute " +
                // "class:", "Add an attribute",
                // JOptionPane.PLAIN_MESSAGE);
                //
                // String[] strArray = response.split(" ");
                // String attrName = strArray[0];
                // String typeName = strArray[1];
                AttributeSelector attrSelector = new AttributeSelector(null,
                        displayedAttr.getPath());
                String attrName = attrSelector.getAttributeLabel();
                String typeName = attrSelector.getAttributeClassname();

                if (!typeName.equals("")) {
                    Attributable attributable = displayedAttr.getAttributable();
                    Graph graph = null;

                    if (attributable instanceof Graph) {
                        graph = (Graph) attributable;
                    } else {
                        graph = ((GraphElement) attributable).getGraph();
                    }

                    graph.getListenerManager().transactionStarted(this);

                    if (typeName.indexOf(".") == -1) {
                        try {
                            Attribute newAttr = (Attribute) InstanceLoader
                                    .createInstance("org.graffiti.graphics."
                                            + typeName, attrName);

                            String path = (displayedAttr.getPath() + " ")
                                    .substring(1).trim();

                            for (Attributable a : graphElements) {
                                a
                                        .addAttribute((Attribute) newAttr
                                                .copy(), path);
                            }

                            // ((CollectionAttribute)
                            // displayedAttr).add(newAttr);
                        } catch (InstanceCreationException ice) {
                            try {
                                Attribute newAttr = (Attribute) InstanceLoader
                                        .createInstance(
                                                "org.graffiti.attributes."
                                                        + typeName, attrName);

                                String path = (displayedAttr.getPath() + " ")
                                        .substring(1).trim();

                                for (Attributable a : graphElements) {
                                    a.addAttribute((Attribute) newAttr.copy(),
                                            path);
                                }

                                // ((CollectionAttribute)
                                // displayedAttr).add(newAttr);
                            } catch (InstanceCreationException ice2) {
                                JOptionPane.showMessageDialog(
                                        DefaultEditPanel.this,
                                        "Could not instantiate class: " + ice2,
                                        "Error!", JOptionPane.OK_OPTION);
                            }
                        }
                    } else {
                        try {
                            Attribute newAttr = (Attribute) InstanceLoader
                                    .createInstance(typeName, attrName);

                            String path = (displayedAttr.getPath() + " ")
                                    .substring(1).trim();

                            for (Attributable a : graphElements) {
                                a
                                        .addAttribute((Attribute) newAttr
                                                .copy(), path);
                            }

                            // ((CollectionAttribute)
                            // displayedAttr).add(newAttr);
                        } catch (InstanceCreationException ice) {
                            JOptionPane.showMessageDialog(
                                    DefaultEditPanel.this,
                                    "Wrong input! Nothing happend!", "Error!",
                                    JOptionPane.OK_OPTION);
                        }
                    }

                    graph.getListenerManager().transactionFinished(this);
                }
            }
        }

        /**
         * DOCUMENT ME!
         * 
         * @author $Author: gleissner $
         * @version $Revision: 5772 $ $Date: 2006-08-14 17:42:21 +0200 (Mo, 14
         *          Aug 2006) $
         */
        private class AttributeSelector extends JDialog {
            /**
             * 
             */
            private static final long serialVersionUID = 6548853080526065322L;

            /** DOCUMENT ME! */
            private JButton cancelButton;

            /** DOCUMENT ME! */
            private JButton okButton;

            /** DOCUMENT ME! */
            private JButton searchButton;

            /** DOCUMENT ME! */
            private JComboBox attrComboBox;

            /** DOCUMENT ME! */
            private JLabel selectACNText;

            /** DOCUMENT ME! */
            private JLabel selectACNText2;

            /** DOCUMENT ME! */
            private JLabel selectALNText;

            /** DOCUMENT ME! */
            private JPanel buttons;

            /** DOCUMENT ME! */
            private JTextField labelTextField;

            /** DOCUMENT ME! */
            private String attrClassname;

            /** DOCUMENT ME! */
            private String attrLabel;

            /** DOCUMENT ME! */
            private Object[] standardAttributes = new Object[] {
                    new String("org.graffiti.attributes.StringAttribute"),
                    new String("org.graffiti.attributes.IntegerAttribute"),
                    new String("org.graffiti.attributes.BooleanAttribute"),
                    new String("org.graffiti.attributes.DoubleAttribute"),
                    new String("org.graffiti.attributes.HashMapAttribute"),
                    new String("org.graffiti.graphics.ColorAttribute"),
                    new String("org.graffiti.graphics.NodeLabelAttribute"),
                    new String("org.graffiti.graphics.EdgeLabelAttribute"),
                    new String("org.graffiti.graphics.RenderedImageAttribute"),
                    new String("org.graffiti.graphics.PortAttribute"),
                    new String("org.graffiti.graphics.PortsAttribute") };

            /**
             * Creates a new AttributeSelector object.
             * 
             * @param frame
             *            DOCUMENT ME!
             * @param parentAttr
             *            DOCUMENT ME!
             */
            public AttributeSelector(Frame frame, String parentAttr) {
                // this = (new JOptionPane()).createDialog(EditPanel.this,
                // "Attribute selection");
                super(frame, "Attribute selection", true);

                selectALNText = new JLabel(
                        "<html>Please enter a <i>name (label)</i> for the new "
                                + "attribute:</html>");

                labelTextField = new JTextField();

                selectACNText = new JLabel(
                        "<html>Please enter or select an <i>attribute class "
                                + "name</i>.\n");
                selectACNText2 = new JLabel(
                        "<html>It will be added to attribute named \"<i>"
                                + parentAttr + "</i>\":");

                attrComboBox = new JComboBox(standardAttributes);
                attrComboBox.setEditable(true);

                searchButton = new JButton("Search for more");
                searchButton.setMnemonic(1);
                searchButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // TODO: look at pluginmanager how to find all classes
                        // that implement Attribute
                        // removing TODO, first try:
                        ClassPathAttributeCollector attrCollector = new ClassPathAttributeCollector();
                        List<String> attrs = attrCollector.collectAttributes();

                        // end of try
                        attrComboBox.removeAllItems();

                        for (String string : attrs) {
                            attrComboBox.addItem(string);
                        }

                        attrComboBox.setEditable(true);
                    }
                });

                okButton = new JButton("OK");
                okButton.setMnemonic(1);
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // TODO: search if classname is correct
                        attrLabel = labelTextField.getText();
                        attrClassname = attrComboBox.getSelectedItem()
                                .toString();

                        // EditPanel.this.setVisible(false);
                        setVisible(false);
                    }
                });

                cancelButton = new JButton("Cancel");
                cancelButton.setMnemonic(1);
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // TODO: search if classname is correct
                        attrLabel = "";
                        attrClassname = "";

                        // EditPanel.this.setVisible(false);
                        setVisible(false);
                    }
                });

                buttons = new JPanel();

                JPanel ocButtons = new JPanel();
                ocButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
                ocButtons.add(okButton);
                ocButtons.add(cancelButton);
                buttons.setLayout(new BorderLayout());
                buttons.add(searchButton, BorderLayout.NORTH);
                buttons.add(ocButtons, BorderLayout.SOUTH);

                JPanel labelPanel = new JPanel();
                labelPanel.setLayout(new BorderLayout());
                labelPanel.add(selectALNText, BorderLayout.NORTH);
                labelPanel.add(labelTextField, BorderLayout.SOUTH);

                JPanel classPanel = new JPanel();
                classPanel.setLayout(new BorderLayout());
                classPanel.add(selectACNText, BorderLayout.NORTH);
                classPanel.add(selectACNText2, BorderLayout.CENTER);
                classPanel.add(attrComboBox, BorderLayout.SOUTH);

                getRootPane().setDefaultButton(okButton);
                getContentPane().setLayout(new BorderLayout());
                getContentPane().add(labelPanel, BorderLayout.NORTH);
                getContentPane().add(classPanel, BorderLayout.CENTER);
                getContentPane().add(buttons, BorderLayout.SOUTH);

                // getContentPane().add(selectALNText);
                // getContentPane().add(labelTextField);
                // getContentPane().add(selectACNText);
                // getContentPane().add(selectACNText2);
                // getContentPane().add(attrComboBox);
                // getContentPane().add(searchButton);
                // getContentPane().add(buttons, BorderLayout.SOUTH);
                pack();
                setLocationRelativeTo(DefaultEditPanel.this);

                // setLocation(300, 300);
                // setLocation(EditPanel.this.getLocation());
                // setLocationRelativeTo(null);
                setVisible(true);
            }

            /**
             * DOCUMENT ME!
             * 
             * @return DOCUMENT ME!
             */
            public String getAttributeClassname() {
                return attrClassname;
            }

            /**
             * DOCUMENT ME!
             * 
             * @return DOCUMENT ME!
             */
            public String getAttributeLabel() {
                return attrLabel;
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @author $Author: gleissner $
     * @version $Revision: 5772 $ $Date: 2006-08-14 17:42:21 +0200 (Mo, 14 Aug
     *          2006) $
     */
    private class RemoveListener implements ActionListener {
        /**
         * DOCUMENT ME!
         * 
         * @param e
         *            DOCUMENT ME!
         * 
         * @throws RuntimeException
         *             DOCUMENT ME!
         */
        public void actionPerformed(ActionEvent e) {
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                    DefaultEditPanel.this, "Attribute that will be removed: \""
                            + displayedAttr.getPath() + "\"",
                    "Remove an attribute", JOptionPane.YES_NO_OPTION)) {
                Attributable attributable = displayedAttr.getAttributable();
                Graph graph = null;

                if (!(attributable instanceof Graph)) {
                    graph = ((GraphElement) attributable).getGraph();
                }

                graph.getListenerManager().transactionStarted(this);

                try {
                    String attrPath = (displayedAttr.getPath() + " ")
                            .substring(1).trim();

                    for (Attributable a : graphElements) {
                        a.removeAttribute(attrPath);
                    }

                    // displayedAttr.getParent().remove(displayedAttr);
                } catch (AttributeNotFoundException anfe) {
                    throw new RuntimeException("Impossible:" + anfe);
                } catch (NullPointerException nully) {
                    JOptionPane.showMessageDialog(DefaultEditPanel.this,
                            "Can't remove root attribute!", "Error!",
                            JOptionPane.OK_OPTION);

                    return;
                } finally {
                    graph.getListenerManager().transactionFinished(this);
                }
            }
        }
    }

    // private class AddLabelListener implements ActionListener {
    //		
    // public void actionPerformed(ActionEvent e) {
    // String attrLabel = JOptionPane.showInputDialog
    // (editPanel, "Type the name (label) of the LabelAttribute that will be
    // inserted:",
    // "Add a LabelAttribute to " + displayedAttr.getPath(),
    // JOptionPane.OK_CANCEL_OPTION);
    // if (attrLabel != null) {
    //
    // try {
    // Attributable ge = displayedAttr.getAttributable();
    // Attribute labelAttr = null;
    // if (ge instanceof Node) {
    // labelAttr = new NodeLabelAttribute(attrLabel);
    // } else if (ge instanceof Edge) {
    // labelAttr = new EdgeLabelAttribute(attrLabel);
    // } else {
    // labelAttr = new LabelAttribute(attrLabel);
    // }
    //
    // ((CollectionAttribute)displayedAttr).add(labelAttr);
    // } catch (AttributeExistsException aee) {
    // JOptionPane.showMessageDialog
    // (editPanel, "An attribute with this ID already exists!", "Error!",
    // JOptionPane.OK_OPTION);
    // return;
    // } catch (Exception ex) {
    // JOptionPane.showMessageDialog
    // (editPanel, "Could not add attribute: " + ex, "Error!",
    // JOptionPane.OK_OPTION);
    // return;
    // }
    // }
    // }
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
