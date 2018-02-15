// =============================================================================
//
//   SplashScreen.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SplashScreen.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.graffiti.core.Bundle;
import org.graffiti.util.ProgressViewer;

/**
 * A frame that is displayed while Gravisto is loading. The progress of loading
 * is displayed with a progress bar and a description of the current loading
 * action.
 * 
 * @author Michael Forster
 * @version $Revision: 5768 $ $Date: 2006-01-26 09:54:27 +0100 (Thu, 26 Jan
 *          2006) $
 */
public class SplashScreen extends JFrame implements ProgressViewer {

    /**
     * 
     */
    private static final long serialVersionUID = 113350437825255907L;

    /** The <code>Bundle</code> of this class. */
    private static final Bundle bundle = Bundle.getCoreBundle();

    /** Gravisto's copyright notice */
    private static String copyright = bundle.format("splashScreen.copyright",
            Calendar.getInstance().get(Calendar.YEAR));

    /** Display for the progress made in loading Gravisto. */
    private static JProgressBar progressBar;

    /**
     * Display for the description of the current action for loading Gravisto.
     */
    private static JLabel progressLabel;

    /**
     * Creates a new SplashScreen object.
     */
    public SplashScreen() {
        super(bundle.getString("splashScreen.startupMessage"));
        init();
    }

    private void init() {
        setUndecorated(true);
        setBackground(Color.WHITE);

        // content pane
        JComponent contentPane = new JPanel();
        contentPane.setBorder(new LineBorder(Color.BLACK, 1));
        contentPane.setBackground(null);
        contentPane.setLayout(new GridBagLayout());
        setContentPane(contentPane);

        // logo image
        ImageIcon icon = bundle.getIcon("editor.splash");
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints c = new GridBagConstraints();
        contentPane.add(imageLabel, c);

        // copyright label
        JLabel copyrightLabel = new JLabel(copyright);
        copyrightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        c = new GridBagConstraints();
        c.gridx = 0;
        contentPane.add(copyrightLabel, c);

        // progress bar
        progressBar = new JProgressBar();
        Dimension prefSize = progressBar.getPreferredSize();
        prefSize.width = 0;
        progressBar.setPreferredSize(prefSize);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 10, 0, 10);
        contentPane.add(progressBar, c);

        // progress label
        progressLabel = new JLabel(bundle
                .getString("splashScreen.progressLabel"));
        progressLabel.setBackground(null);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(2, 0, 2, 0);
        contentPane.add(progressLabel, c);

        pack();

        // center on display
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenDim.width - getWidth()) / 2,
                (screenDim.height - getHeight()) / 2);
    }

    /*
     * @see org.graffiti.util.ProgressViewer#setMaximum(int)
     */
    public void setMaximum(int maximum) {
        progressBar.setMaximum(maximum);
    }

    /*
     * @see org.graffiti.util.ProgressViewer#setText(java.lang.String)
     */
    public void setText(String text) {
        progressLabel.setText(text);
    }

    /*
     * @see org.graffiti.util.ProgressViewer#setValue(int)
     */
    public void setValue(int value) {
        progressBar.setValue(value);
    }

    /*
     * @see org.graffiti.util.ProgressViewer#getValue()
     */
    public int getValue() {
        return progressBar.getValue();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
