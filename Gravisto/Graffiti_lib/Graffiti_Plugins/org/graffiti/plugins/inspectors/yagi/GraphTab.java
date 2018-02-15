//=============================================================================
//
//   GraphTab.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: GraphTab.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import org.graffiti.attributes.Attribute;
import org.graffiti.event.AttributeEvent;
import org.graffiti.event.TransactionEvent;
import org.graffiti.graph.Graph;

/**
 * Represents the tab, which contains the functionality to edit the attributes
 * of the current graph object.
 */
public class GraphTab extends AbstractTab {

    /**
     * 
     */
    private static final long serialVersionUID = 821592490636779267L;
    /** Flag preventing that setDirected is called several times */
    private boolean fromTransaction = false;

    /**
     * Constructs a GraphTab and sets the title.
     */
    public GraphTab() {
        this.title = "Graph";
        this.type = ViewTab.GRAPH;
        // GraffitiSingleton.getInstance().getMainFrame().getActiveEditorSession()
        // .getGraph().getListenerManager().addStrictAttributeListener(this);
    }

    /**
     * If the path to the changed attribute is ".directed", then the directed
     * property of the graph (attributable of the attribute) is updated.
     * 
     * @param e
     *            the AttributeEvent
     * @see org.graffiti.event.AttributeListener#postAttributeChanged(org.graffiti.event.AttributeEvent)
     */
    @Override
    public void postAttributeChanged(AttributeEvent e) {
        super.postAttributeChanged(e);

        if (fromTransaction)
            return;

        Attribute attr = e.getAttribute();

        if (".directed".equals(attr.getPath())) {
            String dirstr = attr.getValue().toString();
            boolean directed = true;

            if ("0".equals(dirstr) || "false".equals(dirstr)) {
                directed = false;
            }

            try {
                Graph graph = (Graph) attr.getAttributable();

                if (graph.isDirected() != directed) {
                    graph.setDirected(directed, true);
                }
            } catch (ClassCastException cce) {
                // should not occur
            }
        }
    }

    /**
     * If the path to the changed attribute is ".directed", then the directed
     * property of the graph (attributable of the attribute) is updated.
     * 
     * @param e
     *            the TransactionEvent
     * @see org.graffiti.event.TransactionListener#transactionFinished(org.graffiti.event.TransactionEvent)
     */
    @Override
    public void transactionFinished(TransactionEvent e) {
        super.transactionFinished(e);

        for (Object obj : e.getChangedObjects()) {
            if (obj instanceof Attribute) {
                Attribute attr = (Attribute) obj;

                if (attr.getAttributable() instanceof Graph) {
                    if (".directed".equals(attr.getPath())) {
                        String dirstr = attr.getValue().toString();
                        boolean directed = true;

                        if ("0".equals(dirstr) || "false".equals(dirstr)) {
                            directed = false;
                        }

                        Graph graph = (Graph) attr.getAttributable();

                        fromTransaction = true;
                        graph.setDirected(directed, true);
                        fromTransaction = false;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Returns a copy of this GraphTab.
     */
    @Override
    public Object clone() {
        return new GraphTab();
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
