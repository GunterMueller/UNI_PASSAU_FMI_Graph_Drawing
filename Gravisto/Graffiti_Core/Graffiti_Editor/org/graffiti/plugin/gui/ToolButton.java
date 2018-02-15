// =============================================================================
//
//   ToolButton.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ToolButton.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPopupMenu;

import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.tool.Tool;
import org.graffiti.plugin.tool.ToolPopupMenuProvider;
import org.graffiti.plugin.tool.ToolRegistry;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision: 5768 $ $Date: 2008-12-31 05:00:21 +0100 (Mi, 31 Dez 2008)
 *          $
 */
public class ToolButton extends GraffitiToggleButton {
    /**
     * 
     */
    private static final long serialVersionUID = 8312965573111169625L;
    private Tool<?> tool;

    public ToolButton(final Tool<?> tool) {
        super(ToolToolbar.ID);
        this.tool = tool;

        setIcon(tool.getIcon());
        updateToolTipText();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopupIfApt(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupIfApt(e);
            }

            private void showPopupIfApt(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    List<ToolPopupMenuProvider> list = ToolRegistry.get()
                            .getPopupMenuProviders();
                    LinkedList<GraffitiAction> actions = new LinkedList<GraffitiAction>();
                    for (ToolPopupMenuProvider provider : list) {
                        GraffitiAction action = provider
                                .provideActionForTool(tool);
                        if (action != null) {
                            actions.add(action);
                        }
                    }
                    if (!actions.isEmpty()) {
                        JPopupMenu menu = new JPopupMenu();
                        for (GraffitiAction action : actions) {
                            menu.add(action);
                        }
                        menu.show(ToolButton.this, e.getX(), e.getY());
                    }
                }
            }
        });

        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tool.activate();
            }
        });
    }

    public void updateToolTipText() {
        String name = tool.getName();
        String description = tool.getDescription();
        StringBuffer buf = new StringBuffer("<html><body>");
        if (name.length() != 0) {
            buf.append(name);
            if (description.length() != 0) {
                buf.append("<br><br>");
            }
        }
        buf.append(description.replace("\n", "<br>"));
        buf.append("</body></html>");
        setToolTipText(buf.toString());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
