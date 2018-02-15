// =============================================================================
//
//   UpdateSelectionRectAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: UpdateSelectionRectAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.selection;

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
    private static final long serialVersionUID = -5293268595669479072L;

    /** Reference to the selectionTool */
    public SelectionTool selectionTool;

    private static final Logger logger = Logger
            .getLogger(UpdateSelectionRectAction.class.getName());

    /**
     * Creates a new UpdateSelectionRectAction
     * 
     * @param selectionTool
     *            the given selectionTool
     */
    public UpdateSelectionRectAction(SelectionTool selectionTool) {
        this.selectionTool = selectionTool;
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
        } else if (selectionTool.getMode() == SelectionTool.RECT) {
            selectionTool.setCurrentSelectRectPosition(position);

            Rectangle selectionRect = selectionTool.getSelectionRectangle();
            JComponent viewComponent = selectionTool.getViewComponent();
            Set<Component> components = selectionTool
                    .getContainerComponents(viewComponent);
            selectionTool.filterRectComponents(components, selectionRect);

            Set<GraphElementComponent> newElements = selectionTool
                    .filterGEComponents(components);

            if (!removeOldSelection()) {
                // TODO: I cannot see how this is supposed to work. the former
                // elements are of the class "GraphElement" (see
                // StartSelectRectAction.actionPerformed). It does not make
                // sense to compare them to this set of
                // "GraphElemenetComponents" ??
                //
                // newElements = SetOperations.minus(newElements, selectionTool
                // .getFormerSelectedElements());
            }

            Set<GraphElementComponent> oldElements = selectionTool
                    .getCurrentSelectedElements();
            Set<GraphElementComponent> oldButNotNew = SetOperations.minus(
                    oldElements, newElements);
            Set<GraphElementComponent> newButNotOld = SetOperations.minus(
                    newElements, oldElements);

            for (GraphElementComponent gec : oldButNotNew) {
                selectionTool.unmark(gec);
            }

            for (GraphElementComponent gec : newButNotOld) {
                selectionTool.mark(gec);
            }

            selectionTool.setCurrentSelectedElements(newElements);

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

    /**
     * Checks the current value of the parameter "remove-old-selection"
     * 
     * @return true for "yes", else false
     */
    private boolean removeOldSelection() {
        Object value = this.getValue("remove-old-selection");

        if (value == null) {
            value = "yes";
        }

        return value.equals("yes");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
