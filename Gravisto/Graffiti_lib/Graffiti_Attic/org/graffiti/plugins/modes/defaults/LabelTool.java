// =============================================================================
//
//   LabelTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LabelTool.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.modes.defaults;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * A tool for creating and editing labels of graphelements.
 * 
 * @author Holleis
 * @version $Revision: 5772 $
 * @deprecated
 */
@Deprecated
public class LabelTool extends MegaTools {

    // maybe put this somewhere else?

    /** DOCUMENT ME! */
    protected final String labelConst = "label";

    /**
     * Invoked if user presses mouse button.
     * 
     * @param e
     *            the mouse event
     */
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e))
            return;

        Component clickedComp = this.findComponentAt(e);

        if (clickedComp instanceof GraphElementComponent) {
            GraphElement ge = ((GraphElementComponent) clickedComp)
                    .getGraphElement();
            LabelAttribute labelAttr = (LabelAttribute) searchForAttribute(ge
                    .getAttribute(""), LabelAttribute.class);

            ChangeAttributesEdit edit;

            if (labelAttr != null) {
                String oldLabel = labelAttr.getLabel();
                String newLabel = showEditDialog(clickedComp, oldLabel);

                if (!oldLabel.equals(newLabel)) {
                    edit = new ChangeAttributesEdit(labelAttr, geMap);
                    labelAttr.setLabel(newLabel);
                    undoSupport.postEdit(edit);
                }

                clickedComp.getParent().repaint();
            } else { // no label found

                String newLabel = showEditDialog(clickedComp, "");

                if (ge instanceof Node) {
                    labelAttr = new NodeLabelAttribute(labelConst);
                } else {
                    labelAttr = new EdgeLabelAttribute(labelConst);
                }

                ge.addAttribute(labelAttr, "");
                edit = new ChangeAttributesEdit(labelAttr, geMap);
                labelAttr.setLabel(newLabel);

                undoSupport.postEdit(edit);
                clickedComp.getParent().repaint();
            }

        }
    }

    /**
     * DOCUMENT ME!
     */
    public void reset() {
    }

    /**
     * DOCUMENT ME!
     * 
     * @param attr
     *            DOCUMENT ME!
     * @param attributeType
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected Attribute searchForAttribute(Attribute attr,
            Class<?> attributeType) {
        if (attributeType.isInstance(attr))
            return attr;
        else {
            if (attr instanceof CollectionAttribute) {
                for (Attribute attribute : ((CollectionAttribute) attr)
                        .getCollection().values()) {
                    Attribute newAttr = searchForAttribute(attribute,
                            attributeType);

                    if (newAttr != null)
                        return newAttr;
                }
            } else if (attr instanceof CompositeAttribute)
                // TODO: treat those correctly; some of those have not yet
                // been correctly implemented
                return null;
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param parent
     *            DOCUMENT ME!
     * @param initialText
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected String showEditDialog(Component parent, String initialText) {
        String returnValue = JOptionPane.showInputDialog(parent,
                "Enter new label:", initialText);

        if (returnValue == null)
            return initialText;
        else
            return returnValue;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
