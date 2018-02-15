// =============================================================================
//
//   GraphTab.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphTab.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.inspectors.defaults;

import org.graffiti.attributes.Attribute;
import org.graffiti.event.AttributeEvent;
import org.graffiti.event.TransactionEvent;
import org.graffiti.graph.Graph;

/**
 * Represents the tab, which contains the functionality to edit the attributes
 * of the current graph object.
 * 
 * @version $Revision: 5772 $
 */
public class GraphTab extends AbstractTab {

    /**
     * 
     */
    private static final long serialVersionUID = -4870451339564844407L;
    /** Flag preventing that setDirected is called several times */
    private boolean fromTransaction = false;

    /**
     * Constructs a <code>GraphTab</code> and sets the title.
     */
    public GraphTab() {
        this.title = "Graph";
    }

    /**
     * If the path to the changed attribute is ".directed", then the directed
     * property of the graph (attributable of the attribute) is updated.
     * 
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
                // nothing todo then, but should not occur ...
            }
        }
    }

    /**
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

                        // if(graph.isDirected() != directed)
                        // {
                        fromTransaction = true;
                        graph.setDirected(directed, true);
                        fromTransaction = false;
                        // }

                        return;
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
