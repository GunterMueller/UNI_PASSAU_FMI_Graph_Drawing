// =============================================================================
//
//   SelectionParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectionParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

import org.graffiti.selection.Selection;

/**
 * This class contains a single <code>Node</code>.
 * 
 * @version $Revision: 5767 $
 */
public class SelectionParameter extends AbstractSingleParameter<Selection> {

    /**
     * 
     */
    private static final long serialVersionUID = 3996765209464318340L;

    /**
     * Constructs a new selection parameter.
     * 
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     */
    public SelectionParameter(String name, String description) {
        super(name, description);
    }

    /**
     * Constructs a new selection parameter.
     * 
     * @param name
     * @param description
     * @param sel
     */
    public SelectionParameter(String name, String description, Selection sel) {
        super(sel, name, description);
    }

    /**
     * Constructs a new selection parameter.
     * 
     * @param sel
     * @param name
     * @param description
     */
    public SelectionParameter(Selection sel, String name, String description) {
        super(sel, name, description);
    }

    /**
     * Sets the selection.
     * 
     * @param selection
     *            DOCUMENT ME!
     */
    public void setSelection(Selection selection) {
        setValue(selection);
    }

    /**
     * Returns the selection encapsulated in this parameter.
     * 
     * @return DOCUMENT ME!
     */
    public Selection getSelection() {
        return getValue();
    }

    @Override
    public SelectionParameter copy() {
        throw new UnsupportedOperationException(
                "Deep copying doesn't make sense.");
    }

    @Override
    public boolean canCopy() {
        return false;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
