// =============================================================================
//
//   OptionPane.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: OptionPane.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.options;

import java.awt.Component;

/**
 * The interface all option panes must implement. The <i>name</i> of an option
 * pane is returned by the <code>getName()</code> method. the label displayed in
 * the option pane's tab is obtained from the
 * <code>options.<i>name</i>.label</code> property.
 * 
 * <p>
 * Note that in most cases, it is easier to extend
 * <code>AbstractOptionPane</code>.
 * </p>
 * 
 * @author flierl
 * @version $Revision: 5767 $
 */
public interface OptionPane {

    /**
     * Returns the component, that should be displayed for this option pane.
     * 
     * @return DOCUMENT ME!
     */
    Component getComponent();

    /**
     * Returns the internal name of this option pane. The option pane's label is
     * set to the value of the property <code>options.<i>name</i>.label</code>.
     * 
     * @return DOCUMENT ME!
     */
    String getName();

    /**
     * This method is called every time this option pane is displayed. The
     * <code>AbstractOptionPane</code> class uses this to create the option
     * pane's GUI only when needed.
     */
    void init();

    /**
     * Called when the options dialog's "ok" button is clicked. This should save
     * any properties being edited in this option pane.
     */
    void save();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
