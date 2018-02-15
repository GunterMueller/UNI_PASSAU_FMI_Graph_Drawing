// =============================================================================
//
//   DebugPlugin.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.debug;

import java.awt.event.ActionEvent;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graph.Graph;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugin.gui.GraffitiContainer;
import org.graffiti.plugin.gui.GraffitiMenu;
import org.graffiti.plugin.gui.GraffitiMenuItem;
import org.graffiti.session.Session;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class DebugMenu extends GraffitiMenu implements GraffitiContainer {
    /**
     * 
     */
    private static final long serialVersionUID = 8761293252907975842L;
    public static final String ID = "org.graffiti.plugins.tools.debug.menus.Debug";

    DebugMenu() {
        super();
        setName("Debug Utilities");
        setText("Debug Utilities");
        setEnabled(true);
    }

    /*
     * @see org.graffiti.plugin.gui.GraffitiMenu#getPreferredComponent()
     */
    @Override
    public String getPreferredComponent() {
        return "menu.plugin";
    }

    public String getId() {
        return ID;
    }
}

class ShowDebugWindowMenuItem extends GraffitiMenuItem {
    /**
     * 
     */
    private static final long serialVersionUID = 6344984610932201497L;

    public ShowDebugWindowMenuItem() {
        super(DebugMenu.ID, new ShowDebugWindowAction());
    }
}

class ShowDebugWindowAction extends GraffitiAction {
    /**
     * 
     */
    private static final long serialVersionUID = -1170406677176923821L;

    public ShowDebugWindowAction() {
        super("Show Debug Window", null);
    }

    /*
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /*
     * @see org.graffiti.plugin.actions.GraffitiAction#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /*
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        DebugWindow.get().setVisible(true);
    }
}

class AttachDebugLabelsMenuItem extends GraffitiMenuItem {
    /**
     * 
     */
    private static final long serialVersionUID = -2031385218413893220L;

    public AttachDebugLabelsMenuItem() {
        super(DebugMenu.ID, new AttachDebugLabelsAction());
    }
}

class AttachDebugLabelsAction extends GraffitiAction {
    /**
     * 
     */
    private static final long serialVersionUID = -1553855212315939220L;

    public AttachDebugLabelsAction() {
        super("Attach Debug Labels", null);
    }

    /*
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /*
     * @see org.graffiti.plugin.actions.GraffitiAction#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /*
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();
        if (mainFrame == null)
            return;
        Session session = mainFrame.getActiveSession();
        if (session == null)
            return;
        Graph graph = GraffitiSingleton.getInstance().getMainFrame()
                .getActiveSession().getGraph();
        if (graph == null)
            return;
        DebugUtil.attachDebugLabels(graph);
    }
}

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DebugPlugin extends EditorPluginAdapter {
    public DebugPlugin() {
        guiComponents = new GraffitiComponent[] { new DebugMenu(),
                new ShowDebugWindowMenuItem(), new AttachDebugLabelsMenuItem() };
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
