// =============================================================================
//
//   SlotEditableEnum.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.util.List;

import javax.swing.JComboBox;

import org.graffiti.core.Bundle;

/**
 * Enumerations implementing {@code SlotEditableEnum} are supported by the tool
 * system to be used as the type of parameter slots. The tool system creates a
 * component (e.g. a {@link JComboBox}) for them, which allows the user to
 * select the enumeration constant.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Slot
 * @see SlotEditorComponentBuilder
 */
public interface SlotEditableEnum {
    /**
     * Returns the name of this enumeration constant as seen by the user when
     * graphically editing the tools. The name is stored in the specified
     * bundle.
     * 
     * @param bundle
     *            the resource bundle that contains the name of this enumeration
     *            constant.
     * @return the name of this enumeration constant as seen by the user when
     *         graphically editing the tools.
     */
    public String getName(Bundle bundle);

    /**
     * Returns a list of all enumeration constants.
     * 
     * @return a list of all enumeration constants.
     */
    public List<? extends SlotEditableEnum> getValues();

    /**
     * Returns the ordinal of this enumeration constant.
     * 
     * @return the ordinal of this enumeration constant.
     * @see Enum#ordinal()
     */
    public int ordinal();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
