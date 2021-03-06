// =============================================================================
//
//   ZoomChangeComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ZoomChangeComponent.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.tools.enhancedzoomtool;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import org.graffiti.plugin.gui.AbstractGraffitiComponent;
import org.graffiti.plugin.view.View;
import org.graffiti.plugin.view.ViewListener;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;

/**
 * DOCUMENT ME!
 */
public class ZoomChangeComponent extends AbstractGraffitiComponent implements
        ActionListener, ViewListener, SessionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 6164758999280638792L;

    /** DOCUMENT ME! */
    private JButton combo;

    // /** DOCUMENT ME! */
    // private MainFrame mainframe;
    // /** DOCUMENT ME! */
    // private Object zoomValue;

    /** DOCUMENT ME! */
    private JPanel matrixPanel = new JPanel();

    /** DOCUMENT ME! */
    private Session activeSession;

    // private JFormattedTextField m12 = new JFormattedTextField();

    /** DOCUMENT ME! */
    private JButton okButton;

    /** DOCUMENT ME! */
    private JDialog dialog;

    /** DOCUMENT ME! */
    private JFormattedTextField m00 = new JFormattedTextField();

    /** DOCUMENT ME! */
    private JFormattedTextField m01 = new JFormattedTextField();

    /** DOCUMENT ME! */

    // private JFormattedTextField m02 = new JFormattedTextField();
    /** DOCUMENT ME! */
    private JFormattedTextField m10 = new JFormattedTextField();

    /** DOCUMENT ME! */
    private JFormattedTextField m11 = new JFormattedTextField();

    /**
     * Constructor for ZoomChangeComponent.
     * 
     * @param prefComp
     *            DOCUMENT ME!
     */
    public ZoomChangeComponent(String prefComp) {
        super(prefComp);

        m00.setValue(new Integer(1));
        m01.setValue(new Integer(0));

        // m02.setValue(new Integer(0));
        m10.setValue(new Integer(0));

        m11.setValue(new Integer(1));

        // m12.setValue(new Integer(0));
        matrixPanel.setLayout(new GridLayout(3, 3));
        matrixPanel.add(m00);
        matrixPanel.add(m01);

        // matrixPanel.add(m02);
        matrixPanel.add(m10);

        matrixPanel.add(m11);

        // matrixPanel.add(m12);
        matrixPanel.add(new JPanel());
        okButton = new JButton("OK");
        matrixPanel.add(okButton);

        okButton.addActionListener(this);

        combo = new JButton("Zoom: " + m00.getValue().toString() + ", "
                + m01.getValue().toString() + ", " + m10.getValue().toString()
                + ", " + m11.getValue().toString());
        add(combo);

        combo.addActionListener(this);
    }

    // /**
    // * @see
    // org.graffiti.plugin.gui.AbstractGraffitiComponent#setMainFrame(org.graffiti.editor.MainFrame)
    // */
    // public void setMainFrame(MainFrame mf)
    // {
    // super.setMainFrame(mf);
    // this.mainframe = mf;
    // }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (combo.equals(e.getSource())) {
            if (dialog == null) {
                dialog = new JDialog();
                dialog.setModal(true);
                dialog.getContentPane().add(matrixPanel);
                dialog.pack();
            }

            dialog.setVisible(true);
        }

        if (okButton.equals(e.getSource())) {
            dialog.setVisible(false);

            ZoomListener zoomView = activeSession.getActiveView();
            AffineTransform at = new AffineTransform(((Number) m00.getValue())
                    .doubleValue(), ((Number) m10.getValue()).doubleValue(),
                    ((Number) m01.getValue()).doubleValue(), ((Number) m11
                            .getValue()).doubleValue(), 0d, 0d);
            zoomView.zoomChanged(at);

            combo.setText("Zoom: " + m00.getValue().toString() + ", "
                    + m01.getValue().toString() + ", "
                    + m10.getValue().toString() + ", "
                    + m11.getValue().toString());
        }
    }

    /**
     * @see org.graffiti.session.SessionListener#sessionChanged(org.graffiti.session.Session)
     */
    public void sessionChanged(Session s) {
        activeSession = s;

        if (s != null) {
            viewChanged(s.getActiveView());
        }
    }

    /**
     * @see org.graffiti.session.SessionListener#sessionDataChanged(org.graffiti.session.Session)
     */
    public void sessionDataChanged(Session s) {
        activeSession = s;
        viewChanged(s.getActiveView());
    }

    /**
     * @see org.graffiti.plugin.view.ViewListener#viewChanged(org.graffiti.plugin.view.View)
     */
    public void viewChanged(View newView) {
        Object newZoom = newView.getZoom();

        // String zoomStr;
        // if(newZoom instanceof Point2D)
        // {
        // zoomStr = String.valueOf((int) (((Point2D) newZoom).getX() * 100)) +
        // "%";
        // } else
        if (newZoom instanceof AffineTransform) {
            AffineTransform at = (AffineTransform) newZoom;
            double[] matrix = new double[6];
            at.getMatrix(matrix);
            m00.setValue(new Double(matrix[0]));
            m10.setValue(new Double(matrix[1]));
            m01.setValue(new Double(matrix[2]));

            m11.setValue(new Double(matrix[3]));

            // m02.setValue(new Double(matrix[4]));
            // m12.setValue(new Double(matrix[5]));
        }

        // else
        // {
        // zoomStr = newZoom.toString();
        // }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
