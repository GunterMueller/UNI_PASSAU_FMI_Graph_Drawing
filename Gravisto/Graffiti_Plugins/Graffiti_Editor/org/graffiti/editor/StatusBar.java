// =============================================================================
//
//   StatusBar.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StatusBar.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.graffiti.core.Bundle;
import org.graffiti.event.GraphEvent;
import org.graffiti.event.GraphListener;
import org.graffiti.event.ListenerManager;
import org.graffiti.event.ListenerNotFoundException;
import org.graffiti.event.TransactionEvent;
import org.graffiti.selection.SelectionEvent;
import org.graffiti.selection.SelectionListener;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;

/**
 * Represents a status line ui component, which can display info and error
 * messages.
 * 
 * @version $Revision: 5768 $
 */
public class StatusBar extends JPanel implements SessionListener,
        SelectionListener, GraphListener, MouseMotionListener {

    /**
     * 
     */
    private static final long serialVersionUID = -5178040943952140426L;

    /** The time, a message is displayed in the status line. */
    private static final int DELAY = 5000;

    /** The font, which is used to display an info message. */
    private static final Font PLAIN_FONT = new Font("dialog", Font.PLAIN, 12);

    /** The font, which is used to display an error message. */
    private static final Font BOLD_FONT = new Font("dialog", Font.BOLD, 12);

    /** The <code>StringBundle</code> of this class. */
    private static final Bundle bundle = Bundle.getCoreBundle();

    /** The nodes- and edges label in the status bar. */
    private JLabel edgesLabel;

    /** The nodes- and edges label in the status bar. */
    private JLabel nodesLabel;

    private JLabel mousePositionLabel;

    /** The ui component, which contains the status text. */
    private JLabel statusLine;

    /** Indicates the currently running algorithm */
    private JLabel activeAlgorithmLabel;

    /** The number format for the nodes and edges displays. */
    private NumberFormat f3;

    /** The current session, this status bar is listening to. */
    private Session currentSession;

    /** The number of edges. */
    private int edges;

    /** The number of nodes. */
    private int nodes;

    /**
     * Constructs a new status bar.
     */
    public StatusBar() {
        super();

        nodes = 0;
        edges = 0;

        setLayout(new GridBagLayout());

        statusLine = new JLabel(" ");

        statusLine.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createLoweredBevelBorder(), statusLine.getBorder()));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.insets = new Insets(0, 0, 0, 0);

        add(statusLine, c);

        activeAlgorithmLabel = new JLabel(bundle
                .getString("statusBar.activeAlgorithmLabel"));
        activeAlgorithmLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(), activeAlgorithmLabel
                        .getBorder()));

        mousePositionLabel = new JLabel(" ");
        mousePositionLabel.setToolTipText(bundle
                .getString("statusBar.mousePositionLabel.tooltip"));
        mousePositionLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(), mousePositionLabel
                        .getBorder()));

        nodesLabel = new JLabel(" ");
        nodesLabel.setToolTipText(bundle.getString("statusBar.nodes.tooltip"));
        nodesLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createLoweredBevelBorder(), nodesLabel.getBorder()));

        edgesLabel = new JLabel(" ");
        edgesLabel.setToolTipText(bundle.getString("statusBar.edges.tooltip"));
        edgesLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createLoweredBevelBorder(), edgesLabel.getBorder()));

        c.gridx = 1;
        // c.weightx = 0.0;
        add(activeAlgorithmLabel, c);

        c.gridx = 2;
        c.weightx = 0.0;
        add(mousePositionLabel, c);

        c.gridx = 3;
        c.weightx = 0.0;
        add(nodesLabel, c);

        c.gridx = 4;
        add(edgesLabel, c);

        activeAlgorithmLabel.setVisible(true);
        mousePositionLabel.setVisible(false);
        nodesLabel.setVisible(false);
        edgesLabel.setVisible(false);

        f3 = new DecimalFormat(" ### ");

        updateGraphInfo();
    }

    /**
     * Clears the current text of the status bar.
     */
    public synchronized void clear() {
        statusLine.setText(" ");
        setToolTipText(null);
    }

    /**
     * @see org.graffiti.event.GraphListener#postEdgeAdded(GraphEvent)
     */
    public void postEdgeAdded(GraphEvent e) {
        edges++;
        updateGraphInfo();
    }

    /**
     * @see org.graffiti.event.GraphListener#postEdgeRemoved(GraphEvent)
     */
    public void postEdgeRemoved(GraphEvent e) {
        edges--;
        updateGraphInfo();
    }

    /**
     * @see org.graffiti.event.GraphListener#postGraphCleared(GraphEvent)
     */
    public void postGraphCleared(GraphEvent e) {
        edges = 0;
        nodes = 0;
        updateGraphInfo();
    }

    /**
     * @see org.graffiti.event.GraphListener#postNodeAdded(GraphEvent)
     */
    public void postNodeAdded(GraphEvent e) {
        nodes++;
        updateGraphInfo();
    }

    /**
     * @see org.graffiti.event.GraphListener#postNodeRemoved(GraphEvent)
     */
    public void postNodeRemoved(GraphEvent e) {
        nodes--;
        updateGraphInfo();
    }

    /**
     * @see org.graffiti.event.GraphListener#preEdgeAdded(GraphEvent)
     */
    public void preEdgeAdded(GraphEvent e) {
    }

    /**
     * @see org.graffiti.event.GraphListener#preEdgeRemoved(GraphEvent)
     */
    public void preEdgeRemoved(GraphEvent e) {
    }

    /**
     * @see org.graffiti.event.GraphListener#preGraphCleared(GraphEvent)
     */
    public void preGraphCleared(GraphEvent e) {
    }

    /**
     * @see org.graffiti.event.GraphListener#preNodeAdded(GraphEvent)
     */
    public void preNodeAdded(GraphEvent e) {
    }

    /**
     * @see org.graffiti.event.GraphListener#preNodeRemoved(GraphEvent)
     */
    public void preNodeRemoved(GraphEvent e) {
    }

    /**
     * @see org.graffiti.selection.SelectionListener#selectionChanged(SelectionEvent)
     */
    public void selectionChanged(SelectionEvent e) {
    }

    /**
     * @see org.graffiti.selection.SelectionListener#selectionListChanged(org.graffiti.selection.SelectionEvent)
     */
    public void selectionListChanged(SelectionEvent e) {
    }

    /**
     * @see org.graffiti.session.SessionListener#sessionChanged(Session)
     */
    public synchronized void sessionChanged(Session session) {
        ListenerManager lm = null;

        if (currentSession != null) {
            // remove the status bar from the graph listener list of the
            // old session ...
            lm = currentSession.getGraph().getListenerManager();

            try {
                lm.removeGraphListener(this);
            } catch (ListenerNotFoundException lnfe) {
                lnfe.printStackTrace();
            }
        }

        // remember the new session
        currentSession = session;

        if (session != null) {
            lm = session.getGraph().getListenerManager();

            // and add the status bar to the listener list of the new session.
            lm.addNonstrictGraphListener(this);

            nodes = currentSession.getGraph().getNumberOfNodes();
            edges = currentSession.getGraph().getNumberOfEdges();
            updateAlgorithmLabel(currentSession);
            mousePositionLabel.setVisible(true);
            nodesLabel.setVisible(true);
            edgesLabel.setVisible(true);
        } else {
            mousePositionLabel.setVisible(false);
            nodesLabel.setVisible(false);
            edgesLabel.setVisible(false);
        }

        updateGraphInfo();
    }

    /**
     * @param s
     */
    private void updateAlgorithmLabel(Session s) {
        if (s.hasActiveAnimation()) {
            activeAlgorithmLabel.setText(s.getActiveAnimation().getName());
        } else {
            activeAlgorithmLabel.setText(bundle
                    .getString("statusBar.activeAlgorithmLabel"));
        }
    }

    /**
     * @see org.graffiti.session.SessionListener#sessionDataChanged(Session)
     */
    public void sessionDataChanged(Session s) {
        updateAlgorithmLabel(s);
    }

    /**
     * Shows the given error message in the status bar for <tt>DELAY</tt>
     * seconds.
     * 
     * @param status
     *            the message to display in the status bar.
     */
    public synchronized void showError(String status) {
        showError(status, DELAY);
    }

    /**
     * Shows the given error message in the status bar for the given interval.
     * 
     * @param status
     *            the message to display in the status bar.
     * @param timeMillis
     *            DOCUMENT ME!
     */
    public synchronized void showError(final String status, int timeMillis) {
        Timer timer = new Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isShowing()) {
                    // FIXED, CK: This avoids flickering
                    if (statusLine.getText().equals(status)) {
                        clear();
                    }
                }
            }
        });

        statusLine.setFont(BOLD_FONT);
        statusLine.setForeground(Color.red);
        statusLine.setText(status);
        timer.setInitialDelay(timeMillis);
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Shows the given message in the status bar for <tt>DELAY</tt> seconds.
     * 
     * @param message
     *            the message to display in the status bar.
     */
    public synchronized void showInfo(String message) {
        showInfo(message, DELAY);
    }

    /**
     * Shows the given message in the status bar for the given interval.
     * 
     * @param message
     *            the message to display in the status bar.
     * @param timeMillis
     *            DOCUMENT ME!
     */
    public synchronized void showInfo(final String message, int timeMillis) {
        Timer timer = new Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isShowing()) {
                    // FIXED, CK: This avoids flickering
                    if (statusLine.getText().equals(message)) {
                        clear();
                    }
                }
            }
        });

        statusLine.setFont(PLAIN_FONT);
        statusLine.setForeground(Color.black);
        statusLine.setText(message);
        timer.setInitialDelay(timeMillis);
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * @see org.graffiti.event.TransactionListener#transactionFinished(TransactionEvent)
     */
    public void transactionFinished(TransactionEvent e) {
        nodes = currentSession.getGraph().getNumberOfNodes();
        edges = currentSession.getGraph().getNumberOfEdges();
        updateGraphInfo();
    }

    /**
     * @see org.graffiti.event.TransactionListener#transactionStarted(TransactionEvent)
     */
    public void transactionStarted(TransactionEvent e) {
    }

    /**
     * Updates the graph information ui components.
     */
    private synchronized void updateGraphInfo() {
        nodesLabel.setText(String.format("%s N", f3.format(nodes)));
        edgesLabel.setText(String.format("%s E", f3.format(edges)));
    }

    private void updateMousePosition(MouseEvent e) {
        mousePositionLabel
                .setText(String.format("%d : %d", e.getX(), e.getY()));
    }

    /*
     * @see
     * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
     * )
     */
    public void mouseDragged(MouseEvent e) {
        updateMousePosition(e);
    }

    /*
     * @see
     * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
        updateMousePosition(e);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
