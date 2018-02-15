// =============================================================================
//
//   StackQueueToolbar.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.event.AbstractGraphListener;
import org.graffiti.event.GraphEvent;
import org.graffiti.event.GraphListener;
import org.graffiti.event.TransactionEvent;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.gui.GraffitiToolbar;
import org.graffiti.plugins.tools.toolcustomizer.ToolCustomizerPlugin;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class StackQueueToolbar extends GraffitiToolbar {
    /**
     * 
     */
    private static final long serialVersionUID = -1729009955594028966L;

    public static final String ID = "stackQueueToolbarID";

    private JButton configureButton;

    private WeakReference<Session> currentSession;

    private WeakHashMap<Graph, Boolean> knownGraphs;

    private GraphListener graphListener;

    private StackQueueWorker worker;

    private StackQueueButton stackButton;
    private StackQueueButton twoStackButton;
    private StackQueueButton threeStackButton;
    private StackQueueButton fourStackButton;
    
    private StackQueueButton queueButton;
    private StackQueueButton twoQueueButton;
    private StackQueueButton threeQueueButton;
    private StackQueueButton fourQueueButton;
    
    private StackQueueButton stackQueueButton;
    
    private StackQueueButton twoStackOneQueueButton;
    private StackQueueButton oneStackTwoQueueButton;
    private StackQueueButton twoStackTwoQueueButton;
    
    private StackQueueButton threeStackOneQueueButton;
    private StackQueueButton threeStackTwoQueueButton;
    private StackQueueButton oneStackThreeQueueButton;
    private StackQueueButton twoStackThreeQueueButton;
    private StackQueueButton threeStackThreeQueueButton;

    public StackQueueToolbar() {
        super(ID);

        knownGraphs = new WeakHashMap<Graph, Boolean>();

        setOrientation(SwingConstants.HORIZONTAL);

        configureButton = new JButton(ToolCustomizerPlugin
                .createIcon("images/edit.png"));

        add(configureButton);

        GraffitiSingleton.getInstance().getMainFrame().addSessionListener(
                new SessionListener() {
                    @Override
                    public void sessionDataChanged(Session s) {
                        sessionChanged(s);
                    }

                    @Override
                    public void sessionChanged(Session s) {
                        if (s == null)
                            return;
                        currentSession = new WeakReference<Session>(s);
                        Graph graph = s.getGraph();
                        if (!knownGraphs.containsKey(graph)) {
                            knownGraphs.put(graph, true);
                            graph.getListenerManager()
                                    .addNonstrictGraphListener(graphListener);
                        }
                        graphChanged();
                    }
                });

        graphListener = new AbstractGraphListener() {
            @Override
            public void postEdgeAdded(GraphEvent e) {
                graphChanged();
            }

            @Override
            public void postEdgeRemoved(GraphEvent e) {
                graphChanged();
            }

            @Override
            public void postGraphCleared(GraphEvent e) {
                graphChanged();
            }

            @Override
            public void postNodeAdded(GraphEvent e) {
                graphChanged();
            }

            @Override
            public void postNodeRemoved(GraphEvent e) {
                graphChanged();
            }

            @Override
            public void transactionFinished(TransactionEvent e) {
                graphChanged();
            }
        };
        
        stackButton = new StackQueueButton(1, 0);
        add(stackButton);
        twoStackButton = new StackQueueButton(2, 0);
        add(twoStackButton);
        threeStackButton = new StackQueueButton(3, 0);
        add(threeStackButton);
        fourStackButton = new StackQueueButton(4, 0);
        add(fourStackButton);
        
        queueButton = new StackQueueButton(0, 1);
        add(queueButton);
        twoQueueButton = new StackQueueButton(0, 2);
        add(twoQueueButton);
        threeQueueButton = new StackQueueButton(0, 3);
        add(threeQueueButton);
        fourQueueButton = new StackQueueButton(0, 4);
        add(fourQueueButton);
        
        stackQueueButton = new StackQueueButton(1, 1);
        add(stackQueueButton);
        
        twoStackOneQueueButton = new StackQueueButton(2, 1);
        add(twoStackOneQueueButton);
        oneStackTwoQueueButton = new StackQueueButton(1, 2);
        add(oneStackTwoQueueButton);
        twoStackTwoQueueButton = new StackQueueButton(2, 2);
        add(twoStackTwoQueueButton);
        
        threeStackOneQueueButton = new StackQueueButton(3, 1);
        add(threeStackOneQueueButton);
        threeStackTwoQueueButton = new StackQueueButton(3, 2);
        add(threeStackTwoQueueButton);
        oneStackThreeQueueButton = new StackQueueButton(1, 3);
        add(oneStackThreeQueueButton);
        twoStackThreeQueueButton = new StackQueueButton(2, 3);
        add(twoStackThreeQueueButton);
        threeStackThreeQueueButton = new StackQueueButton(3, 3);
        add(threeStackThreeQueueButton);

        worker = new StackQueueWorker();
        worker.start();
    }

    private void graphChanged() {
        disableAll();

        if (currentSession == null)
            return;

        Session session = currentSession.get();

        if (session == null)
            return;

        Graph graph = session.getGraph();

        List<Node> nodes = new LinkedList<Node>(graph.getNodes());
        List<Edge> edges = new LinkedList<Edge>(graph.getEdges());

        List<StackQueueJob> jobs = new LinkedList<StackQueueJob>();
        
        jobs.add(new StackQueueJob(graph, nodes, edges, 0, 1, queueButton));
        jobs.add(new StackQueueJob(graph, nodes, edges, 0, 2, twoQueueButton));
        jobs.add(new StackQueueJob(graph, nodes, edges, 0, 3, threeQueueButton));
        jobs.add(new StackQueueJob(graph, nodes, edges, 0, 4, fourQueueButton));
        
        jobs.add(new StackQueueJob(graph, nodes, edges, 1, 0, stackButton));
        jobs.add(new StackQueueJob(graph, nodes, edges, 2, 0, twoStackButton));
        jobs.add(new StackQueueJob(graph, nodes, edges, 3, 0, threeStackButton));
        jobs.add(new StackQueueJob(graph, nodes, edges, 4, 0, fourStackButton));
        
        jobs.add(new StackQueueJob(graph, nodes, edges, 1, 1, stackQueueButton));
        
        jobs.add(new StackQueueJob(graph, nodes, edges, 2, 1, twoStackOneQueueButton));
        jobs.add(new StackQueueJob(graph, nodes, edges, 1, 2, oneStackTwoQueueButton));
        jobs.add(new StackQueueJob(graph, nodes, edges, 2, 2, twoStackTwoQueueButton));

        jobs.add(new StackQueueJob(graph, nodes, edges, 3, 1, threeStackOneQueueButton));
        jobs.add(new StackQueueJob(graph, nodes, edges, 3, 2, threeStackTwoQueueButton));
        jobs.add(new StackQueueJob(graph, nodes, edges, 1, 3, oneStackThreeQueueButton));
        jobs.add(new StackQueueJob(graph, nodes, edges, 2, 3, twoStackThreeQueueButton));
        jobs.add(new StackQueueJob(graph, nodes, edges, 3, 3, threeStackThreeQueueButton));
        
        
        worker.setNextJobs(jobs);
        worker.interrupt();
        // graph.
        /*
         * if (currentSession != null) { Session se currentSession.get() }
         */
    }

    private void disableAll() {
        stackQueueButton.set(false, null);
        oneStackTwoQueueButton.set(false, null);
        twoStackOneQueueButton.set(false, null);
        twoStackTwoQueueButton.set(false, null);
        threeStackOneQueueButton.set(false, null);
        threeStackTwoQueueButton.set(false, null);
        oneStackThreeQueueButton.set(false, null);
        twoStackThreeQueueButton.set(false, null);
        threeStackThreeQueueButton.set(false, null);
        
        stackButton.set(false, null);
        twoStackButton.set(false, null);
        threeStackButton.set(false, null);
        fourStackButton.set(false, null);
        
        queueButton.set(false, null);
        twoQueueButton.set(false, null);
        threeQueueButton.set(false, null);
        fourQueueButton.set(false, null);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
