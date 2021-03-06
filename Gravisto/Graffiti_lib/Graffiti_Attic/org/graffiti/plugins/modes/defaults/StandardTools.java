// =============================================================================
//
//   StandardTools.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StandardTools.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.modes.defaults;

import java.util.prefs.Preferences;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugin.gui.ToolButton;

/**
 * This plugin contains the standard editing tools.
 * 
 * @version $Revision: 5772 $
 * @deprecated
 */
@Deprecated
public class StandardTools extends EditorPluginAdapter {

    /** The <code>ImageBundle</code> of the main frame. */
    private ImageBundle iBundle = ImageBundle.getInstance();

    /** DOCUMENT ME! */
    private Tool label;

    /** The tools this plugin provides. */
    private Tool megaCreate;

    /** DOCUMENT ME! */
    private Tool megaMove;

    /** DOCUMENT ME! */
    private ToolButton labelButton;

    /** The buttons for the tools this plugin provides. */
    private ToolButton megaCreateButton;

    /** DOCUMENT ME! */
    private ToolButton megaMoveButton;

    /**
     * Creates a new StandardTools object.
     */
    public StandardTools() {
        label = new AdvancedLabelTool();
        megaCreate = new MegaCreateTool();
        megaMove = new MegaMoveTool();
        Tool[] tools = new Tool[3];
        tools[0] = megaCreate;
        tools[1] = megaMove;
        tools[2] = label;

        megaCreateButton = new ToolButton(megaCreate,
                "org.graffiti.plugins.modes.defaultEditMode", iBundle
                        .getImageIcon("tool.megaCreate"));
        megaMoveButton = new ToolButton(megaMove,
                "org.graffiti.plugins.modes.defaultEditMode", iBundle
                        .getImageIcon("tool.megaMove"));
        labelButton = new ToolButton(label,
                "org.graffiti.plugins.modes.defaultEditMode", iBundle
                        .getImageIcon("tool.label"));
        guiComponents = new GraffitiComponent[3];
        guiComponents[0] = megaCreateButton;
        guiComponents[1] = megaMoveButton;
        guiComponents[2] = labelButton;
    }

    /**
     * Sets the preferences in all tools this plugin provides.
     * 
     * @param prefs
     *            the preferences node for this plugin.
     * 
     * @see org.graffiti.plugin.GenericPlugin#configure(Preferences)
     */
    @Override
    public void configure(Preferences prefs) {
        super.configure(prefs);
        megaCreate.setPrefs(this.prefs.node("megaCreateTool"));
        megaMove.setPrefs(this.prefs.node("megaMoveTool"));
        label.setPrefs(this.prefs.node("labelTool"));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
