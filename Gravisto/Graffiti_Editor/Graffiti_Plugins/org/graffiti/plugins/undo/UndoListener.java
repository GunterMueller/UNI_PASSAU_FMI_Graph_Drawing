package org.graffiti.plugins.undo;

import java.util.LinkedList;
import java.util.List;

import javax.swing.undo.CompoundEdit;

import org.graffiti.attributes.Attribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.event.AttributeEvent;
import org.graffiti.event.AttributeListener;
import org.graffiti.event.EdgeEvent;
import org.graffiti.event.EdgeListener;
import org.graffiti.event.GraphEvent;
import org.graffiti.event.GraphListener;
import org.graffiti.event.ListenerManager;
import org.graffiti.event.ListenerNotFoundException;
import org.graffiti.event.NodeEvent;
import org.graffiti.event.NodeListener;
import org.graffiti.event.TransactionEvent;
import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;
import org.graffiti.undo.AddEdgeEdit;
import org.graffiti.undo.AddNodeEdit;
import org.graffiti.undo.AttributeEdit;
import org.graffiti.undo.ChangeAttributesEdit;
import org.graffiti.undo.EdgeDirectedEdit;
import org.graffiti.undo.EdgeReverseEdit;
import org.graffiti.undo.EdgeSourceNodeEdit;
import org.graffiti.undo.EdgeTargetNodeEdit;

public class UndoListener extends EditorPluginAdapter implements
        SessionListener, GraphListener, AttributeListener, NodeListener,
        EdgeListener {

    private int activeTransactions = 0;

    private CompoundEdit ce;

    /** The current session, this undo listener is listening to. */
    private EditorSession currentSession;

    public UndoListener() {
        super();
        GraffitiSingleton.getInstance().getMainFrame().addSessionListener(this);
    }

    /**
     * @see org.graffiti.session.SessionListener#sessionChanged(Session)
     */
    public synchronized void sessionChanged(Session session) {
        ListenerManager lm = null;

        if (currentSession != null) {
            lm = currentSession.getGraph().getListenerManager();
            try {
                lm.removeGraphListener(this);
                lm.removeAttributeListener(this);

                lm.removeNodeListener(this);
                lm.removeEdgeListener(this);
            } catch (ListenerNotFoundException lnfe) {
                lnfe.printStackTrace();
            }
        }

        // remember the new session
        currentSession = (EditorSession) session;

        if (session != null) {
            lm = session.getGraph().getListenerManager();
            lm.addNonstrictGraphListener(this);
            lm.addNonstrictAttributeListener(this);

            lm.addNonstrictNodeListener(this);
            lm.addNonstrictEdgeListener(this);
        }
    }

    public void sessionDataChanged(Session s) {
    }

    public void postEdgeAdded(GraphEvent e) {
        if (activeTransactions > 0) {
            // System.out.println("Edge added: "+e.getEdge());
            ce.addEdit(new AddEdgeEdit(e.getEdge(), e.getEdge().getGraph(),
                    currentSession.getGraphElementsMap()));
        }
    }

    public void postEdgeRemoved(GraphEvent e) {
    }

    public void postGraphCleared(GraphEvent e) {
    }

    public void postNodeAdded(GraphEvent e) {
        if (activeTransactions > 0) {
            // System.out.println("Node added: "+e.getNode());
            ce.addEdit(new AddNodeEdit(e.getNode(), e.getNode().getGraph(),
                    currentSession.getGraphElementsMap()));
        }
    }

    public void postNodeRemoved(GraphEvent e) {
    }

    public void preEdgeAdded(GraphEvent e) {
    }

    public void preEdgeRemoved(GraphEvent e) {
        if (activeTransactions > 0) {
            // System.out.println("Edge removed: "+ e.getEdge());
            List<GraphElement> elements = new LinkedList<GraphElement>();
            elements.add(e.getEdge());
            ce.addEdit(new GraphElementsDeletedEdit(elements, e.getEdge()
                    .getGraph(), currentSession.getGraphElementsMap()));
        }
    }

    public void preGraphCleared(GraphEvent e) {
        if (activeTransactions > 0) {
            List<GraphElement> elements = new LinkedList<GraphElement>();
            elements.addAll(e.getGraph().getGraphElements());
            ce.addEdit(new GraphElementsDeletedEdit(elements, e.getGraph(),
                    currentSession.getGraphElementsMap()));
        }
    }

    public void preNodeAdded(GraphEvent e) {
    }

    public void preNodeRemoved(GraphEvent e) {
        if (activeTransactions > 0) {
            // System.out.println("Node removed: "+ e.getNode());
            List<GraphElement> elements = new LinkedList<GraphElement>();
            elements.add(e.getNode());
            ce.addEdit(new GraphElementsDeletedEdit(elements, e.getNode()
                    .getGraph(), currentSession.getGraphElementsMap()));
        }
    }

    public void transactionFinished(TransactionEvent e) {
        // System.out.println("Transaction finished: "+e);
        if (e.getSource() instanceof Algorithm) {
            // System.out.println(((Algorithm)e.getSource()).getName());
            activeTransactions--;
            if (activeTransactions == 0) {
                ce.end();
                GraffitiSingleton.getInstance().getMainFrame().getUndoSupport()
                        .postEdit(ce);
            }
        }
    }

    public void transactionStarted(TransactionEvent e) {
        // System.out.println("Transaction started: "+e);
        if (e.getSource() instanceof Algorithm) {
            activeTransactions++;
            if (activeTransactions == 1) {
                ce = new NamedCompoundEdit(((Algorithm) e.getSource())
                        .getName());
            }
        }
    }

    public void postAttributeAdded(AttributeEvent e) {
        if (activeTransactions > 0) {
            // System.out.println("post attribute added");
            ce.addEdit(new AttributeEdit(e.getAttribute(), e.getAttribute()
                    .getAttributable(), true, currentSession
                    .getGraphElementsMap()));
        }
    }

    public void postAttributeChanged(AttributeEvent e) {
        // System.out.println(e.getAttribute()+" changed in "+e.getAttribute().getAttributable()+". New value: "+e.getAttribute().getValue());
    }

    public void postAttributeRemoved(AttributeEvent e) {
    }

    public void preAttributeAdded(AttributeEvent e) {
    }

    public void preAttributeChanged(AttributeEvent e) {
        // System.out.println(e.getAttribute()+" changed in "+e.getAttribute().getAttributable()+". Old value: "+e.getAttribute().getValue());
        if (activeTransactions > 0) {
            Attribute attr = e.getAttribute();
            if (attr instanceof org.graffiti.plugins.algorithms.mst.adapters.attribute.NodeAttribute
                    || attr instanceof org.graffiti.plugins.algorithms.mst.adapters.attribute.HeapEntryAttribute
                    || attr instanceof org.graffiti.plugins.algorithms.mst.adapters.attribute.WeightAttribute)
                return;

            ce.addEdit(new ChangeAttributesEdit(e.getAttribute(),
                    currentSession.getGraphElementsMap()));
        }
    }

    public void preAttributeRemoved(AttributeEvent e) {
        if (activeTransactions > 0) {
            // System.out.println("pre attribute removed");
            ce.addEdit(new AttributeEdit(e.getAttribute(), e.getAttribute()
                    .getAttributable(), false, currentSession
                    .getGraphElementsMap()));
        }
    }

    /*
     * @see
     * org.graffiti.event.NodeListener#postInEdgeAdded(org.graffiti.event.NodeEvent
     * )
     */
    @Override
    public void postInEdgeAdded(NodeEvent e) {
        // System.out.println("postInEdgeAdded: source " + e.getSource() +
        // " node " + e.getNode());
    }

    /*
     * @see
     * org.graffiti.event.NodeListener#postInEdgeRemoved(org.graffiti.event.
     * NodeEvent)
     */
    @Override
    public void postInEdgeRemoved(NodeEvent e) {
        // System.out.println("postInEdgeRemoved: source " + e.getSource() +
        // " node " + e.getNode());
    }

    /*
     * @see
     * org.graffiti.event.NodeListener#postOutEdgeAdded(org.graffiti.event.NodeEvent
     * )
     */
    @Override
    public void postOutEdgeAdded(NodeEvent e) {
        // System.out.println("postOutEdgeAdded: source " + e.getSource() +
        // " node " + e.getNode());
    }

    /*
     * @see
     * org.graffiti.event.NodeListener#postOutEdgeRemoved(org.graffiti.event
     * .NodeEvent)
     */
    @Override
    public void postOutEdgeRemoved(NodeEvent e) {
        // System.out.println("postOutEdgeRemoved: source " + e.getSource() +
        // " node " + e.getNode());
    }

    /*
     * @see
     * org.graffiti.event.NodeListener#postUndirectedEdgeAdded(org.graffiti.
     * event.NodeEvent)
     */
    @Override
    public void postUndirectedEdgeAdded(NodeEvent e) {
        // System.out.println("postUndirectedEdgeAdded: source " + e.getSource()
        // + " node " + e.getNode());
    }

    /*
     * @see
     * org.graffiti.event.NodeListener#postUndirectedEdgeRemoved(org.graffiti
     * .event.NodeEvent)
     */
    @Override
    public void postUndirectedEdgeRemoved(NodeEvent e) {
        // System.out.println("postUndirectedEdgeRemoved: source " +
        // e.getSource() + " node " + e.getNode());
    }

    /*
     * @see
     * org.graffiti.event.NodeListener#preInEdgeAdded(org.graffiti.event.NodeEvent
     * )
     */
    @Override
    public void preInEdgeAdded(NodeEvent e) {
        // System.out.println("preInEdgeAdded: source " + e.getSource() +
        // " node " + e.getNode());
    }

    /*
     * @see
     * org.graffiti.event.NodeListener#preInEdgeRemoved(org.graffiti.event.NodeEvent
     * )
     */
    @Override
    public void preInEdgeRemoved(NodeEvent e) {
        // System.out.println("preInEdgeRemoved: source " + e.getSource() +
        // " node " + e.getNode());
    }

    /*
     * @see
     * org.graffiti.event.NodeListener#preOutEdgeAdded(org.graffiti.event.NodeEvent
     * )
     */
    @Override
    public void preOutEdgeAdded(NodeEvent e) {
        // System.out.println("postOutEdgeAdded: source " + e.getSource() +
        // " node " + e.getNode());
    }

    /*
     * @see
     * org.graffiti.event.NodeListener#preOutEdgeRemoved(org.graffiti.event.
     * NodeEvent)
     */
    @Override
    public void preOutEdgeRemoved(NodeEvent e) {
        // System.out.println("preOutEdgeRemoved: source " + e.getSource() +
        // " node " + e.getNode());
    }

    /*
     * @see
     * org.graffiti.event.NodeListener#preUndirectedEdgeAdded(org.graffiti.event
     * .NodeEvent)
     */
    @Override
    public void preUndirectedEdgeAdded(NodeEvent e) {
        // System.out.println("preUndirectedEdgeAdded: source " + e.getSource()
        // + " node " + e.getNode());
    }

    /*
     * @see
     * org.graffiti.event.NodeListener#preUndirectedEdgeRemoved(org.graffiti
     * .event.NodeEvent)
     */
    @Override
    public void preUndirectedEdgeRemoved(NodeEvent e) {
        // System.out.println("preUndirectedEdgeRemoved: source " +
        // e.getSource() + " node " + e.getNode());
    }

    /*
     * @see
     * org.graffiti.event.EdgeListener#postDirectedChanged(org.graffiti.event
     * .EdgeEvent)
     */
    @Override
    public void postDirectedChanged(EdgeEvent e) {
        if (activeTransactions > 0) {
            ce.addEdit(new EdgeDirectedEdit(e.getEdge(), currentSession
                    .getGraphElementsMap()));
        }
    }

    /*
     * @see
     * org.graffiti.event.EdgeListener#postEdgeReversed(org.graffiti.event.EdgeEvent
     * )
     */
    @Override
    public void postEdgeReversed(EdgeEvent e) {
        if (activeTransactions > 0) {
            ce.addEdit(new EdgeReverseEdit(e.getEdge(), currentSession
                    .getGraphElementsMap()));
        }
    }

    /*
     * @see
     * org.graffiti.event.EdgeListener#postSourceNodeChanged(org.graffiti.event
     * .EdgeEvent)
     */
    @Override
    public void postSourceNodeChanged(EdgeEvent e) {
        // System.out.println("postSourceNodeChanged: source " + e.getSource() +
        // " node " + e.getEdge());
    }

    /*
     * @see
     * org.graffiti.event.EdgeListener#postTargetNodeChanged(org.graffiti.event
     * .EdgeEvent)
     */
    @Override
    public void postTargetNodeChanged(EdgeEvent e) {
        // System.out.println("postTargetNodeChanged: source " + e.getSource() +
        // " node " + e.getEdge());
    }

    /*
     * @see
     * org.graffiti.event.EdgeListener#preDirectedChanged(org.graffiti.event
     * .EdgeEvent)
     */
    @Override
    public void preDirectedChanged(EdgeEvent e) {
        // System.out.println("preDirectedChanged: source " + e.getSource() +
        // " node " + e.getEdge());
    }

    /*
     * @see
     * org.graffiti.event.EdgeListener#preEdgeReversed(org.graffiti.event.EdgeEvent
     * )
     */
    @Override
    public void preEdgeReversed(EdgeEvent e) {
        // System.out.println("preEdgeReversed: source " + e.getSource() +
        // " node " + e.getEdge());
    }

    /*
     * @see
     * org.graffiti.event.EdgeListener#preSourceNodeChanged(org.graffiti.event
     * .EdgeEvent)
     */
    @Override
    public void preSourceNodeChanged(EdgeEvent e) {
        if (activeTransactions > 0) {
            ce.addEdit(new EdgeSourceNodeEdit(e.getEdge(), currentSession
                    .getGraphElementsMap()));
        }
    }

    /*
     * @see
     * org.graffiti.event.EdgeListener#preTargetNodeChanged(org.graffiti.event
     * .EdgeEvent)
     */
    @Override
    public void preTargetNodeChanged(EdgeEvent e) {
        if (activeTransactions > 0) {
            ce.addEdit(new EdgeTargetNodeEdit(e.getEdge(), currentSession
                    .getGraphElementsMap()));
        }
    }
}
