// =============================================================================
//
//   AddAttributeEdit.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.undo;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.graph.AbstractGraphElement;
import org.graffiti.graph.GraphElement;

/**
 * @author Kathrin Hanauer
 * @version $Revision$ $Date$
 */
public class AttributeEdit extends GraffitiAbstractUndoableEdit {
    /**
     * 
     */
    private static final long serialVersionUID = -1704065082224334376L;

    private Attribute attribute;

    private Attributable attributable;

    private String path;

    private boolean doAdd;

    private Logger logger = Logger.getLogger(getClass().getName());
    
    /**
     * @param geMap
     */
    public AttributeEdit(Attribute attribute, Attributable attributable,
            boolean doAdd, Map<GraphElement, GraphElement> geMap) {
        super(geMap);
        try {
            this.attribute = (Attribute) attribute.copy();
        } catch (UnsupportedOperationException e) {
            logger.warning(attribute.getClass() + " does not support copying!");
        }
        this.attributable = attributable;
        this.doAdd = doAdd;
        int dot = attribute.getPath().lastIndexOf('.') + 1;
        this.path = attribute.getPath().substring(1, dot);
    }

    /*
     * @see org.graffiti.undo.GraffitiAbstractUndoableEdit#execute()
     */
    @Override
    public void execute() {
    }

    private void addAttribute() {
        if (attribute == null)
            return;

        if (attributable instanceof AbstractGraphElement
                && ((AbstractGraphElement) attributable).getGraph() == null) {
            logger.log(Level.FINE, "addAttribute ignored on null graph");
            return;
        }
        if (!attributable.containsAttribute(path + attribute.getId())) {
            attributable.addAttribute(attribute, path);
        } else {
            attributable.getAttribute(path + attribute.getId()).setValue(
                    attribute.getValue());
        }
    }

    private void removeAttribute() {
        if (attribute == null)
            return;

        if (attributable instanceof AbstractGraphElement
                && ((AbstractGraphElement) attributable).getGraph() == null) {
            logger.log(Level.FINE, "removeAttribute ignored on null graph");
            return;
        }
        if (attributable.containsAttribute(path + attribute.getId())) {
            attributable.removeAttribute(path + attribute.getId());
        }
    }

    /**
     * @see javax.swing.undo.UndoableEdit#redo()
     */
    @Override
    public void redo() {
        super.redo();

        if (doAdd) {
            addAttribute();
        } else {
            removeAttribute();
        }
    }

    /**
     * @see javax.swing.undo.UndoableEdit#undo()
     */
    @Override
    public void undo() {
        super.undo();

        if (!doAdd) {
            addAttribute();
        } else {
            removeAttribute();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
