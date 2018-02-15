// =============================================================================
//
//   ToolToolbar.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.gui;

import java.awt.Component;

import javax.swing.ButtonGroup;
import javax.swing.SwingConstants;

import org.graffiti.plugin.tool.Tool;
import org.graffiti.util.ObjectReferenceComparator;

/**
 * {@code GraffitiToolbar} that contains the tool buttons used for tool
 * activation.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Tool
 * @see ToolButton
 */
public class ToolToolbar extends GraffitiToolbar {
    /**
     * 
     */
    private static final long serialVersionUID = -7227880366941062587L;

    /**
     * The id of the tool bar.
     * 
     * @see GraffitiToolbar#GraffitiToolbar(String)
     */
    public static final String ID = "toolToolbarID";

    /**
     * The button group where the tool buttons should be added to.
     */
    private ButtonGroup group;

    /**
     * Guarantees a stable ordering of components that are not
     * {@link ToolButton}s, when the tool buttons are sorted.
     */
    protected ObjectReferenceComparator<Component> referenceComparator;

    /**
     * Constructs a {@code ToolToolbar}.
     */
    public ToolToolbar() {
        super(ID);
        group = new ButtonGroup();
        this.setOrientation(SwingConstants.VERTICAL);
        referenceComparator = new ObjectReferenceComparator<Component>();
    }

    /**
     * Inserts the button of the specified tool in this bar before the button of
     * the other tool.
     * 
     * @param tool
     *            the tool whose button is to be inserted.
     * @param beforeTool
     *            the button of {@code tool} is inserted before the button of
     *            {@code beforeTool}.
     */
    public void add(Tool<?> tool, Tool<?> beforeTool) {
        ToolButton button = tool.getToolButton();
        group.add(button);

        if (beforeTool == null) {
            add(button);
        } else {
            ToolButton beforeButton = beforeTool.getToolButton();
            int index = getComponentIndex(beforeButton);
            add(button, index);
        }
        validate();
    }

    /**
     * Removes the button of the specified tool from this bar and reinserts it
     * before the button of the other tool.
     * 
     * @param tool
     *            the tool whose button is to be reinserted.
     * @param beforeTool
     *            the button of {@code tool} is inserted before the button of
     *            {@code beforeTool}.
     */
    public void update(Tool<?> tool, Tool<?> beforeTool) {
        ToolButton button = tool.getToolButton();
        if (button == null)
            return;
        remove(button);
        add(tool, beforeTool);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
