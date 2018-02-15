// =============================================================================
//
//   AbstractGraffitiValueEditContainer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractGraffitiValueEditContainer.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.dialog;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.graffiti.plugin.editcomponent.ValueEditComponent;

/**
 * Provides an abstract implementation of the interface
 * <code>ValueEditContainer</code> in graffiti style.
 * 
 * @see ValueEditContainer
 * @see AbstractValueEditContainer
 */
public class AbstractGraffitiValueEditContainer extends
        AbstractValueEditContainer {

    /**
     * 
     */
    private static final long serialVersionUID = 7465463694726902155L;
    /** The table containing the component for editing values. */
    private NameValueTable nvt;

    /**
     * Constructor for AbstractGraffitiValueEditContainer.
     */
    public AbstractGraffitiValueEditContainer() {
        super();
        nvt = new NameValueTable(javax.swing.JSplitPane.VERTICAL_SPLIT);
    }

    /**
     * Returns a <code>java.util.List</code> containing all the edit components
     * of this <code>ValueEditContainer</code>.
     * 
     * @return a <code>java.util.List</code> containing all the edit components
     *         of this <code>ValueEditContainer</code>.
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    public List<ValueEditComponent> getEditComponents() {
        throw new RuntimeException("implement me");
    }

    /**
     * Adds a <code>ValueEditComponent</code> to the value edit container.
     * 
     * @param vec
     *            the <code>ValueEditComponent</code> to be added.
     */
    @Override
    public void addValueEditComponent(ValueEditComponent vec) {
        nvt.addValueEditComponent(vec);
    }

    /**
     * Adds the specified <code>ValueEditComponent</code> to the container.
     * 
     * @param vec
     *            the <code>ValueEditComponent</code> to be added to the
     *            container.
     */
    @Override
    protected void doAddValueEditComponent(ValueEditComponent vec) {
    }

    /**
     * <code>NameValueTable</code> provides a graffiti style table for editing
     * name value pairs packed into a <code>ValueEditComponent</code>.
     * 
     * @see javax.swing.JSplitPane
     */
    protected class NameValueTable extends JSplitPane {
        /**
         * 
         */
        private static final long serialVersionUID = 4111403064206902630L;

        /** DOCUMENT ME! */
        private JPanel left = new JPanel();

        /** DOCUMENT ME! */
        private JPanel right = new JPanel();

        /**
         * Constructor for NameValueTable.
         * 
         * @param newOrientation
         *            the orientation of the split pane.
         */
        public NameValueTable(int newOrientation) {
            super(newOrientation);
            left.setLayout(new GridLayout(0, 1));
            right.setLayout(new GridLayout(0, 1));
            setLeftComponent(left);
            setRightComponent(right);
            setDividerSize(3);
        }

        /**
         * Adds a <code>ValueEditComponent</code> to the value edit container.
         * 
         * @param vec
         *            the <code>ValueEditComponent</code> to be added.
         */
        public void addValueEditComponent(ValueEditComponent vec) {
            left.add(new JTextField(vec.getDisplayable().getName()));
            right.add(vec.getComponent());
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
