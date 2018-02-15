// =============================================================================
//
//   UpdateSelectionRectAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: UpdateSelectionRectAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.rotate;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.plugins.modes.advanced.SetOperations;

/**
 * Implementation of an action to update the rectangle selection
 * 
 * @author MH
 * @deprecated
 */
@Deprecated
public class UpdateSelectionRectAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = -4070592455099369953L;

    /** Reference to the selectionTool */
    public RotationTool rotationTool;

    private static final Logger logger = Logger
            .getLogger(UpdateSelectionRectAction.class.getName());

    /**
     * Creates a new UpdateSelectionRectAction
     * 
     * @param rotationTool
     *            the given selectionTool
     */
    public UpdateSelectionRectAction(RotationTool rotationTool) {
        this.rotationTool = rotationTool;
    }

    /**
     * Creates a Map with parameter names and their correct values
     * 
     * @return Map with parameter names and their correct values
     */
    @Override
    public Map<String, Set<Object>> getValidParameters() {
        return construct1To2ParamMap("remove-old-selection", "yes", "no");
    }

    /**
     * Performs the action of this class
     * 
     * @param e
     *            the given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        Point position = e.getPosition();

        // Check if the mouse position is inside the editor
        if (position == null) {
            logger.finer("Warning: " + "Can't operate without position!");
        } else if (rotationTool.getMode() == RotationTool.RECT) {
            rotationTool.setCurrentSelectRectPosition(position);

            Rectangle selectionRect = rotationTool.getSelectionRectangle();
            JComponent viewComponent = rotationTool.getViewComponent();
            Set<Component> components = rotationTool
                    .getContainerComponents(viewComponent);
            rotationTool.filterRectComponents(components, selectionRect);

            Set<GraphElementComponent> newElements = rotationTool
                    .filterGEComponents(components);
            newElements.remove(rotationTool.getReferenceNode());

            Set<GraphElementComponent> oldElements = rotationTool
                    .getCurrentSelectedElements();
            Set<GraphElementComponent> oldButNotNew = SetOperations.minus(
                    oldElements, newElements);
            Set<GraphElementComponent> newButNotOld = SetOperations.minus(
                    newElements, oldElements);

            for (GraphElementComponent gec : oldButNotNew) {
                rotationTool.unmark(gec);
            }

            for (GraphElementComponent gec : newButNotOld) {
                rotationTool.mark(gec);
            }

            rotationTool.setCurrentSelectedElements(newElements);

            paintSelectionRect(viewComponent, selectionRect);
        }
    }

    /**
     * Shows the rectangle in the editor
     * 
     * @param component
     *            component which draws the rectangle
     * @param rect
     *            rectangle to draw
     */
    private void paintSelectionRect(JComponent component, Rectangle rect) {
        Graphics graphics = component.getGraphics();

        component.paintImmediately(component.getVisibleRect());
        graphics.drawRect((int) rect.getX(), (int) rect.getY(), (int) rect
                .getWidth(), (int) rect.getHeight());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
