// =============================================================================
//
//   Java2DLabel.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d.label;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugins.views.fast.java2d.DrawingSet;
import org.graffiti.plugins.views.fast.java2d.label.commands.CommandContext;
import org.graffiti.plugins.views.fast.java2d.label.commands.Java2DLabelCommand;
import org.graffiti.plugins.views.fast.label.Label;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Java2DLabel extends Label<Java2DLabel, Java2DLabelCommand> {
    private LinkedList<Java2DLabelCommand> commands;

    Java2DLabel(GraphElement element, LabelAttribute attribute,
            Java2DLabelManager factory) {
        super(element, attribute, factory);
    }

    @Override
    protected Java2DLabel getThis() {
        return this;
    }

    protected void setCommands(LinkedList<Java2DLabelCommand> commands) {
        this.commands = commands;
    }

    public void draw(Graphics2D graphics, DrawingSet set) {
        set.addToBounds(new Rectangle2D.Double(left, top, width, height));

        AffineTransform af = null;

        // do rotation
        if (rotation != 0d) {
            af = (AffineTransform) set.affineTransform.clone();
            set.affineTransform.rotate(rotation, rotationCenter.getX(),
                    rotationCenter.getY());
        }

        graphics.setTransform(set.affineTransform);
        graphics.translate(left, top);

        // undo rotation
        if (rotation != 0d) {
            set.affineTransform = af;
        }

        // graphics.drawLine(0, 0, (int) width, (int) height);
        // graphics.drawLine(0, (int) height, (int) width, 0);
        CommandContext commandContext = new CommandContext(graphics);
        for (Java2DLabelCommand command : commands) {
            command.execute(commandContext);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
