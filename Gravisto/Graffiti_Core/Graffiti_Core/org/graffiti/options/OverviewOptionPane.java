// =============================================================================
//
//   OverviewOptionPane.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: OverviewOptionPane.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

/**
 * The overview pane for the options dialog.
 * 
 * @version $Revision: 5767 $
 */
public class OverviewOptionPane extends AbstractOptionPane {

    /**
     * 
     */
    private static final long serialVersionUID = -80461817083264792L;

    /**
     * Constructor for OverviewOptionPane.
     */
    public OverviewOptionPane() {
        super(bundle.getString("options.overview.title"));
    }

    /*
     * @see org.graffiti.options.AbstractOptionPane#initDefault()
     */
    @Override
    protected void initDefault() {
        setLayout(new BorderLayout());

        // add a JEditorPane, which contains an overview html page.
        JEditorPane ep = new JEditorPane();

        try {
            ep.setPage(bundle.getResource("options.overview.html"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        ep.setEditable(false);

        JScrollPane scroller = new JScrollPane(ep);
        scroller.setPreferredSize(new Dimension(400, 0));

        add(BorderLayout.CENTER, scroller);
    }

    /*
     * @see org.graffiti.options.AbstractOptionPane#saveDefault()
     */
    @Override
    protected void saveDefault() {
        /* do nothing */
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
