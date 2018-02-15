// =============================================================================
//
//   ToolDummy.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.ViewFamily;

/**
 * Tool dummies represent tools that are present in the preferences tree but
 * have not yet been added by their providing plugins so they are currently
 * unavailable. When the respective tool is added, this dummy will be removed.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
final class ToolDummy<T extends InteractiveView<T>> extends Tool<T> {
    /**
     * The id of the factory creating the represented tool. If the represented
     * tool will not be automatically created by a factory, id is the empty
     * string.
     */
    private String factoryId;

    /**
     * Constructs a tool dummy representing the tool with the specified id.
     * 
     * @param viewFamily
     *            the view family supported by the tool to represent.
     * @param id
     *            the id of the tool to represent.
     */
    public ToolDummy(ViewFamily<T> viewFamily, String id) {
        super(viewFamily, id);
        factoryId = preferences.get("factory", "");
        isReadOnly = false;
    }

    /**
     * Returns if the represented tool will be created by the factory with the
     * specified id.
     * 
     * @param factoryId
     *            the id of the factory in question.
     * @return if the represented tool will be created by the factory with the
     *         specified id.
     */
    public boolean hasFactoryId(String factoryId) {
        return this.factoryId.equals(factoryId);
    }

    /**
     * Creates the represented tool using the specified factory.
     * 
     * @param viewFamily
     *            the view family supported by the tool to create.
     * @param factory
     *            the factory to create the represented tool
     * @return the represented tool created using the specified factory.
     */
    public Tool<T> create(ViewFamily<T> viewFamily, ToolFactory factory) {
        preferences.put("factory", factory.getId());
        Tool<T> tool = factory.create(id, viewFamily);
        tool.isReadOnly = false;
        return tool;
    }

    /**
     * {@inheritDoc} This implementation returns {@code true}.
     */
    @Override
    boolean isDummy(int i) {
        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
