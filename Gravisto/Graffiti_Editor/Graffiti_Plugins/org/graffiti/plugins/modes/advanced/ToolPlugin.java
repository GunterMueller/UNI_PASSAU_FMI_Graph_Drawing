// =============================================================================
//
//   ToolPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ToolPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.Point;
import java.util.prefs.Preferences;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugins.modes.advanced.create.CreateTool;
import org.graffiti.plugins.modes.advanced.label.LabelTool;
import org.graffiti.plugins.modes.advanced.nodeResize.NodeResizeTool;
import org.graffiti.plugins.modes.advanced.rotate.RotationTool;
import org.graffiti.plugins.modes.advanced.selection.SelectionTool;
import org.graffiti.plugins.modes.advanced.selection.align.AlignTool;
import org.graffiti.plugins.modes.deprecated.ToolButton;

/**
 * Plugin-class for the so-called advanced editing tools.
 * 
 * @deprecated
 */
@Deprecated
public class ToolPlugin extends EditorPluginAdapter implements
        FunctionComponent {

    /** The active tool. */
    private AbstractEditingTool activeTool = null;

    /** Reference to the CreateTool */
    private CreateTool createTool;

    /** DOCUMENT ME! */
    private Bundle bundle = Bundle.getCoreBundle();

    /** Reference to the LabelTool */
    private LabelTool labelTool;

    /** Reference to the NodeResizeTool */
    private NodeResizeTool nodeResizeTool;

    /** Reference to the RotationRool */
    private RotationTool rotationTool;

    /**
     * Dummy-PositionInfo as enforced by the FunctionComponent-interface.
     * Contains only null-values indicating "position not available"
     */
    private PositionInfo positionInfo = new PositionInfo();

    /** Reference to the SelectionTool */
    private SelectionTool selectionTool;

    /** Reference to the AlignTool */
    private AlignTool alignTool;

    /**
     * Constructs the plugin
     */
    public ToolPlugin() {
        // construct tools
        createTool = new CreateTool(this, positionInfo);
        selectionTool = new SelectionTool(this, positionInfo);
        labelTool = new LabelTool(this, positionInfo);
        nodeResizeTool = new NodeResizeTool(this, positionInfo);
        alignTool = new AlignTool(this, positionInfo);
        rotationTool = new RotationTool(this, positionInfo);

        // ... and tool-buttons
        ToolButton createButton = new ToolButton(createTool,
                "org.graffiti.plugins.modes.defaultEditMode", bundle
                        .getIcon("tool.megaCreate"));

        ToolButton selectionButton = new ToolButton(selectionTool,
                "org.graffiti.plugins.modes.defaultEditMode", bundle
                        .getIcon("tool.megaMove"));

        ToolButton labelButton = new ToolButton(labelTool,
                "org.graffiti.plugins.modes.defaultEditMode", bundle
                        .getIcon("tool.label"));

        ToolButton nodeResizeButton = new ToolButton(nodeResizeTool,
                "org.graffiti.plugins.modes.defaultEditMode", bundle
                        .getIcon("tool.nodeResize"));

        ToolButton alignButton = new ToolButton(alignTool,
                "org.graffiti.plugins.modes.defaultEditMode", bundle
                        .getIcon("tool.megaAlign"));

        ToolButton rotateButton = new ToolButton(rotationTool,
                "org.graffiti.plugins.modes.defaultEditMode", bundle
                        .getIcon("tool.rotate"));

        createButton.setToolTipText("CreateTool");
        selectionButton.setToolTipText("SelectionTool");
        labelButton.setToolTipText("LabelTool");
        nodeResizeButton.setToolTipText("NodeResizeTool");
        alignButton.setToolTipText("AlignTool");
        rotateButton.setToolTipText("RotationTool");

        guiComponents = new GraffitiComponent[] { createButton,
                selectionButton, labelButton, nodeResizeButton, alignButton,
                rotateButton };

    }

    /**
     * Sets the tool currently active.
     * 
     * @param tool
     *            the new tool currently active, null if none exists
     */
    public void setActiveTool(AbstractEditingTool tool) {
        this.activeTool = tool;
    }

    /**
     * Returns the tool currently active, if any, null otherwise.
     * 
     * @return returns the tool currently active, if any, null otherwise.
     */
    public AbstractEditingTool getActiveTool() {
        return activeTool;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param functionName
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public FunctionAction getFunctionAction(String functionName) {
        // ToolPlugin itself provides no functions
        return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public PositionInfo getPositionInfo() {
        return positionInfo;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param name
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public FunctionComponent getSubComponent(String name) {
        if (name.equals("create-tool"))
            return createTool;
        else if (name.equals("selection-tool"))
            return selectionTool;
        else if (name.equals("label-tool"))
            return labelTool;
        else if (name.equals("node-resize-tool"))
            return nodeResizeTool;
        else if (name.equals("align-tool"))
            return alignTool;
        else if (name.equals("rotation-tool"))
            return rotationTool;
        else
            return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param position
     *            DOCUMENT ME!
     */
    public void afterEvent(Point position) {
    }

    /**
     * DOCUMENT ME!
     * 
     * @param position
     *            DOCUMENT ME!
     */
    public void beforeEvent(Point position) {
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
        createTool.setPrefs(this.prefs.node("megaCreateTool"));
        selectionTool.setPrefs(this.prefs.node("megaMoveTool"));
        labelTool.setPrefs(this.prefs.node("labelTool"));

        // NodeResizeTool not yet has preferences
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
