// =============================================================================
//
//   AbstractOptionPane.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractOptionPane.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.options;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.graffiti.core.Bundle;

/**
 * The default implementation of the option pane interface. It lays out
 * components in a vertical fashion.
 * 
 * @version $Revision: 5767 $
 */
public abstract class AbstractOptionPane extends JPanel implements OptionPane {
    /**
     * 
     */
    private static final long serialVersionUID = -6995534376627500978L;

    /** The <code>Bundle</code> of this option pane. */
    protected static final Bundle bundle = Bundle.getCoreBundle();

    /** The layout of this panel. */
    protected GridBagLayout gridBag;

    /** <code>true</code>, if this option pane has been initialized. */
    protected boolean initialized;

    /** The number of components already added to the layout manager. */
    protected int y;

    /** The internal name of this option pane. */
    private String name;

    /**
     * Creates a new option pane.
     * 
     * @param name
     *            the internal name. The option pane's label is set to the value
     *            of the property named <code>options.<i>name</i>.label</code>.
     */
    protected AbstractOptionPane(String name) {
        this.name = name;
        setLayout(gridBag = new GridBagLayout());
    }

    /**
     * Returns the component that should be displayed for this option pane.
     * Because this class implements component, it simply returns
     * <code>this</code>.
     * 
     * @return DOCUMENT ME!
     */
    public Component getComponent() {
        return this;
    }

    /**
     * Returns the internal name of the option pane.
     * 
     * @return the internal name of the option pane.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Adds the given label and component to the option pane. Components are
     * added in vertical fashion, one per row. The label is displayed to the
     * left of the component.
     * 
     * @param label
     *            the label.
     * @param component
     *            the component.
     */
    public void addComponent(String label, Component component) {
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.gridy = y++;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.weightx = 0.0f;
        constraint.insets = new Insets(1, 0, 1, 0);
        constraint.fill = GridBagConstraints.BOTH;

        JLabel l = new JLabel(label, SwingConstants.RIGHT);
        l.setBorder(new EmptyBorder(0, 0, 0, 12));
        gridBag.setConstraints(l, constraint);
        add(l);

        constraint.gridx = 1;
        constraint.weightx = 1.0f;
        gridBag.setConstraints(component, constraint);
        add(component);
    }

    /**
     * Adds the given component to the option pane. Components are added in
     * vertical fashion., one per row.
     * 
     * @param component
     *            the component.
     */
    public void addComponent(Component component) {
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.gridy = y++;
        constraint.gridheight = 1;
        constraint.gridwidth = GridBagConstraints.REMAINDER;
        constraint.fill = GridBagConstraints.NONE;
        constraint.anchor = GridBagConstraints.WEST;
        constraint.weightx = 1.0f;
        constraint.insets = new Insets(1, 0, 1, 0);

        gridBag.setConstraints(component, constraint);
        add(component);
    }

    /**
     * Adds a separator to the option pane.
     * 
     * @param label
     *            the separator label.
     */
    public void addSeparator(String label) {
        Box box = new Box(BoxLayout.X_AXIS);
        Box box2 = new Box(BoxLayout.Y_AXIS);
        box2.add(Box.createGlue());
        box2.add(new JSeparator(SwingConstants.HORIZONTAL));
        box2.add(Box.createGlue());
        box.add(box2);

        JLabel l = new JLabel(label);
        l.setMaximumSize(l.getPreferredSize());
        box.add(l);

        Box box3 = new Box(BoxLayout.Y_AXIS);
        box3.add(Box.createGlue());
        box3.add(new JSeparator(SwingConstants.HORIZONTAL));
        box3.add(Box.createGlue());
        box.add(box3);

        GridBagConstraints cons = new GridBagConstraints();
        cons.gridy = y++;
        cons.gridheight = 1;
        cons.gridwidth = GridBagConstraints.REMAINDER;
        cons.fill = GridBagConstraints.BOTH;
        cons.anchor = GridBagConstraints.WEST;
        cons.weightx = 1.0f;
        cons.insets = new Insets(1, 0, 1, 0);

        gridBag.setConstraints(box, cons);
        add(box);
    }

    /**
     * This method is called every time this option pane is displayed. The
     * <code>AbstractOptionPane</code> class uses this to create the option
     * pane's GUI only when needed.
     */
    public void init() {
        if (!initialized) {
            initialized = true;
            initDefault();
        }
    }

    /**
     * Called when the options dialog's "ok" button is clicked. This should save
     * any properties being edited in this option pane.
     */
    public void save() {
        if (initialized) {
            saveDefault();
        }
    }

    /**
     * Creates this option pane's dialog. Implement this method, to create your
     * option pane's dialog.
     */
    protected abstract void initDefault();

    /**
     * Should save the properties being edited in this option panel. Implement
     * this method, to save your option pane's preferences.
     */
    protected abstract void saveDefault();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
