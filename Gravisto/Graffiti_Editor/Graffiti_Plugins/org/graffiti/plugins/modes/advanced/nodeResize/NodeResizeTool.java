// =============================================================================
//
//   NodeResizeTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeResizeTool.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.nodeResize;

import java.awt.Cursor;
import java.awt.Point;

import javax.swing.JComponent;

import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.modes.advanced.AbstractEditingTool;
import org.graffiti.plugins.modes.advanced.FunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionComponent;
import org.graffiti.plugins.modes.advanced.InvalidInputEventException;
import org.graffiti.plugins.modes.advanced.InvalidParameterException;
import org.graffiti.plugins.modes.advanced.NoSuchFunctionActionException;
import org.graffiti.plugins.modes.advanced.PositionInfo;
import org.graffiti.plugins.modes.advanced.ShortCutAction;
import org.graffiti.plugins.modes.advanced.ShowPopupMenuAction;
import org.graffiti.plugins.modes.advanced.ToolPlugin;

/**
 * Tool for resizing nodes.
 * 
 * @deprecated
 */
@Deprecated
public class NodeResizeTool extends AbstractEditingTool {

    /** If the tool is in no special mode it is in DEFAULT_MODE */
    private static final int DEFAULT_MODE = 0;

    /** The tool is in this mode while a node is being resized */
    private static final int RESIZE_MODE = 1;

    /**
     * null if no Node is being resized at the moment, otherwise reference to
     * the NodeGraphicAttribute of the Node being resized
     */
    private NodeGraphicAttribute resizeNode = null;

    /** DOCUMENT ME! */
    private NodeResizer nodeResizer = null;

    /**
     * null if no Node is being resized at the moment, otherwise the
     * border/corner being resized is stored here ((1,1) for the SW-corner,
     * (1,0) for the W border, ...)
     */
    private Point direction = null;

    /**
     * Null if no Node is being resized at the moment, otherweise the "old"
     * position (resizing is done using an offset of newPosition - oldPosition)
     */
    private Point oldPosition = null;

    /** DOCUMENT ME! */
    private boolean isDuringNodeResize = false;

    /** DOCUMENT ME! */
    private double minNodeSize = 20;

    /** DOCUMENT ME! */
    private double sensitiveAreaSize = 10;

    /**
     * Current mode of the tool. Determines how (and if) some of the tools
     * functions work
     */
    private int mode = DEFAULT_MODE;

    /**
     * Creates a new NodeResizeTool object.
     * 
     * @param toolPlugin
     *            DOCUMENT ME!
     * @param positionInfo
     *            DOCUMENT ME!
     */
    public NodeResizeTool(ToolPlugin toolPlugin, PositionInfo positionInfo) {
        super(toolPlugin, positionInfo);

        try {
            // functionManager.addFunction("mouse 1x clicked button1",
            // "add-node",
            // "over-existing-node", "no",
            // "mark-node", "not");
            // functionManager.addFunction("mouse 1x not_shift not_ctrl pressed
            // button1",
            // "start-node-resize");
            functionManager.addFunction("movedMouse not_button1",
                    "start-node-resize");

            functionManager.addFunction("movedMouse button1",
                    "perform-node-resize");

            functionManager.addFunction("mouse 0x released button1",
                    "finish-node-resize");

            functionManager.addFunction("mouse 1x released button1",
                    "finish-node-resize");

            functionManager.addFunction("popupTrigger", "show-popup-menu");

            functionManager.addFunction("shift DELETE", "linux-shortcuts",
                    "action", "cut");
            functionManager.addFunction("ctrl DELETE", "linux-shortcuts",
                    "action", "copy");
            functionManager.addFunction("shift INSERT", "linux-shortcuts",
                    "action", "paste");
        } catch (InvalidInputEventException e) {
            System.err.println("NodeResizeTool() says: Input-event "
                    + e.getEvent() + ", assigned to function "
                    + e.getFunction() + " has invalid syntax!");
            System.exit(-1);
        } catch (NoSuchFunctionActionException e) {
            System.err.println("NodeResizeTool() says: Can't find "
                    + " Action assigned to function " + e.getFunction());
            System.exit(-1);
        } catch (InvalidParameterException e) {
            System.err.println("NodeResizeTool() says: " + e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param isDuringNodeResize
     *            The isDuringNodeResize to set.
     */
    public void setDuringNodeResize(boolean isDuringNodeResize) {
        this.isDuringNodeResize = isDuringNodeResize;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the isDuringNodeResize.
     */
    public boolean isDuringNodeResize() {
        return isDuringNodeResize;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param functionName
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public FunctionAction getFunctionAction(String functionName) {
        FunctionAction superAction = super.getFunctionAction(functionName);

        if (superAction != null)
            return superAction;
        else if (functionName.equals("show-popup-menu"))
            return new ShowPopupMenuAction(this);
        else if (functionName.equals("start-node-resize"))
            return new StartNodeResizeAction(this);
        else if (functionName.equals("perform-node-resize"))
            return new PerformNodeResizeAction(this);
        else if (functionName.equals("finish-node-resize"))
            return new FinishNodeResizeAction(this);
        else if (functionName.equals("linux-shortcuts"))
            return new ShortCutAction(this);
        else
            return null;
    }

    /**
     * Implementation of FunctionComponent
     * 
     * @param name
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public FunctionComponent getSubComponent(String name) {
        return null;
    }

    /**
     * Classes that overwrite this method should call super.deactive first.
     * 
     * @see org.graffiti.plugins.modes.deprecated.Tool#deactivate()
     */
    @Override
    public void deactivate() {
        super.deactivate();

        Cursor c = new Cursor(Cursor.DEFAULT_CURSOR);
        getActiveJComponent().setCursor(c);
        switchToDefaultMode();
    }

    // public void switchToDefaultMode() {
    // // System.out.println("switched to default mode");
    // mode = DEFAULT_MODE;
    // }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the resizeNode.
     */
    protected NodeGraphicAttribute getResizeNode() {
        return resizeNode;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param viewComponent
     *            DOCUMENT ME!
     */
    @Override
    protected void activateKeyBindings(JComponent viewComponent) {
        functionManager.activateAllKeyBindings(viewComponent);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param viewComponent
     *            DOCUMENT ME!
     */
    @Override
    protected void deactivateKeyBindings(JComponent viewComponent) {
        functionManager.deactivateAllKeyBindings(viewComponent);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    Point getDirection() {
        return direction;
    }

    /**
     * Returns if the tool currently is in {@link #RESIZE_MODE}.
     * 
     * @return if the tool currently is in {@link #RESIZE_MODE}.
     */
    boolean isInResizeMode() {
        return mode == RESIZE_MODE;
    }

    // public boolean isInDefaultMode() {
    // return mode == DEFAULT_MODE;
    // }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    double getMinNodeSize() {
        return minNodeSize;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    NodeGraphicAttribute getNodeGraphicAttribute() {
        return resizeNode;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    NodeResizer getNodeResizer() {
        return nodeResizer;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param oldPosition
     *            DOCUMENT ME!
     */
    void setOldPosition(Point oldPosition) {
        this.oldPosition = oldPosition;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    Point getOldPosition() {
        return oldPosition;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    boolean isResizing() {
        return (nodeResizer != null);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    double getSensitiveAreaSize() {
        return sensitiveAreaSize;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param resizeNode
     *            DOCUMENT ME!
     * @param position
     *            DOCUMENT ME!
     */
    void startResizing(NodeGraphicAttribute resizeNode, Point position) {
        String shape = resizeNode.getShape();

        this.resizeNode = resizeNode;

        // horrible, I know... But as NodeGraphicAttribute uses those
        // Strings and not references to Shape-objects...
        if (shape
                .equals("org.graffiti.plugins.views.defaults.RectangleNodeShape")) {
            // System.out.println("rect");
            nodeResizer = RectNodeResizer.createResizer(resizeNode,
                    sensitiveAreaSize, position, this);
        } else if (shape
                .equals("org.graffiti.plugins.views.defaults.CircleNodeShape")) {
            // System.out.println("circle");
            nodeResizer = CircleNodeResizer.createResizer(resizeNode,
                    sensitiveAreaSize, position, this);
        } else if (shape
                .equals("org.graffiti.plugins.views.defaults.EllipseNodeShape")) {
            // System.out.println("ellipse");
            nodeResizer = EllipseNodeResizer.createResizer(resizeNode,
                    sensitiveAreaSize, position, this);
        } else {
            System.err.println("NodeResizeTool.startResizing says: " + "Shape "
                    + shape + " not supported!!!");
        }

        // if(nodeResizer != null)
        // {
        // //switchToResizeMode();
        // }
        // else
        // {
        // // if not in default then important
        // switchToDefaultMode();
        //            
        // }
    }

    /**
     * DOCUMENT ME!
     */
    void stopResizing() {
        nodeResizer = null;
        resizeNode = null;

        Cursor c = new Cursor(Cursor.DEFAULT_CURSOR);
        getActiveJComponent().setCursor(c);
    }

    /**
     * Switches to {@link #RESIZE_MODE}.
     */
    void switchToResizeMode() {
        // System.out.println("switched to node resize mode");
        mode = RESIZE_MODE;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
