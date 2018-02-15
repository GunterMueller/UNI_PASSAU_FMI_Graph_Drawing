// =============================================================================
//
//   AboutAction.java
//
//   Copyright (c) 2001-2007 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AboutAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;

import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;

/**
 * The action for the about dialog showing license information.
 * 
 * @version $Revision$
 */
public class AboutAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = 4993594979376136282L;

    /**
     * Creates a new AboutAction object.
     * 
     * @param mainFrame
     *            Reference to main frame.
     */
    public AboutAction(MainFrame mainFrame) {
        super("help.about", mainFrame);

        // Used for better description in the context menu
        putValue(NAME, coreBundle.getString("menu." + getName()));
        putValue(SHORT_DESCRIPTION, getName());
    }

    /**
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * Show the about dialog containing the license information for gravisto and
     * its third party tools.
     * 
     * @param event
     *            Event for this action.
     */
    public void actionPerformed(ActionEvent event) {
        JPanel about = new JPanel();
        about.setLayout(new BorderLayout());

        // add our logo
        ImageIcon icon = coreBundle.getIcon("editor.splash");
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setBorder(new EtchedBorder());
        about.add(imageLabel, BorderLayout.NORTH);

        // and add the license text
        JComponent comp;
        try {
            JTextComponent license = new JEditorPane(getClass()
                    .getClassLoader().getResource(
                            "org/graffiti/editor/license.html"));
            license.setEditable(false);
            license.setOpaque(false);
            comp = new JScrollPane(license,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            comp.setMaximumSize(imageLabel.getPreferredSize());
            comp.setPreferredSize(imageLabel.getPreferredSize());
            comp.setBorder(null);
        } catch (IOException e) {
            comp = new JTextArea(e.toString());
        }
        about.add(comp, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this.mainFrame, about, coreBundle
                .getString("menu." + getName()), JOptionPane.PLAIN_MESSAGE);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
