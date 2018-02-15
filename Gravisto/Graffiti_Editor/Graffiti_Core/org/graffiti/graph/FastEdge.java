// =============================================================================
//
//   FastEdge.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FastEdge.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.graph;

import static org.graffiti.graph.FastEdge.End.SOURCE;
import static org.graffiti.graph.FastEdge.End.TARGET;
import static org.graffiti.graph.FastEdge.Incidency.DIR_IN;
import static org.graffiti.graph.FastEdge.Incidency.DIR_LOOP;
import static org.graffiti.graph.FastEdge.Incidency.DIR_OUT;
import static org.graffiti.graph.FastEdge.Incidency.UNDIR_IN;
import static org.graffiti.graph.FastEdge.Incidency.UNDIR_LOOP;
import static org.graffiti.graph.FastEdge.Incidency.UNDIR_OUT;

import java.util.EnumMap;
import java.util.Map;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.util.MultiLinkNode;
import org.graffiti.util.MultiLinkable;

/**
 * @author forster
 * @version $Revision: 5767 $ $Date: 2009-06-24 00:16:53 +0200 (Mi, 24 Jun 2009)
 *          $
 */
public class FastEdge extends AbstractEdge implements
        MultiLinkable<FastEdge, FastEdge.End> {
    enum End {
        SOURCE, TARGET
    }

    enum Incidency {
        DIR_IN(TARGET), DIR_OUT(SOURCE), DIR_LOOP(SOURCE), UNDIR_IN(TARGET), UNDIR_OUT(
                SOURCE), UNDIR_LOOP(SOURCE);

        private End edgeEnd;

        Incidency(End edgeEnd) {
            this.edgeEnd = edgeEnd;
        }

        /**
         * Returns the edgeEnd.
         * 
         * @return the edgeEnd.
         */
        public End getEdgeEnd() {
            return edgeEnd;
        }
    }

    FastNode source;

    FastNode target;

    boolean directed;

    MultiLinkNode<FastEdge> globalLink;

    Map<End, MultiLinkNode<FastEdge>> incidencyLinks = new EnumMap<End, MultiLinkNode<FastEdge>>(
            End.class);

    public FastEdge(Graph graph, FastNode source, FastNode target,
            boolean directed, CollectionAttribute coll) {
        super(graph, coll);

        this.directed = directed;
        this.source = source;
        this.target = target;
    }

    /*
     * @see org.graffiti.graph.AbstractEdge#doReverse()
     */
    @Override
    protected void doReverse() {
        if (source == target)
            return;

        unlink();

        FastNode tmp = source;
        source = target;
        target = tmp;

        link();

        getGraph().setModified(true);
    }

    /*
     * @see org.graffiti.graph.AbstractEdge#doSetDirected(boolean)
     */
    @Override
    protected void doSetDirected(boolean directed) {
        unlink();
        this.directed = directed;
        link();
        getGraph().setModified(true);
    }

    /*
     * @see org.graffiti.graph.AbstractEdge#doSetSource(org.graffiti.graph.Node)
     */
    @Override
    protected void doSetSource(Node source)
            throws GraphElementNotFoundException {
        if (source.getGraph() != getGraph())
            throw new GraphElementNotFoundException("");

        unlink();
        this.source = (FastNode) source;
        link();
        getGraph().setModified(true);
    }

    /*
     * @see org.graffiti.graph.AbstractEdge#doSetTarget(org.graffiti.graph.Node)
     */
    @Override
    protected void doSetTarget(Node target)
            throws GraphElementNotFoundException {
        if (target.getGraph() != getGraph())
            throw new GraphElementNotFoundException("");

        unlink();
        this.target = (FastNode) target;
        link();
        getGraph().setModified(true);
    }

    public MultiLinkNode<FastEdge> getLinkNode(End incidency) {
        if (incidency == null)
            return globalLink;
        else
            return incidencyLinks.get(incidency);
    }

    /*
     * @see org.graffiti.graph.Edge#getSource()
     */
    public Node getSource() {
        return source;
    }

    /*
     * @see org.graffiti.graph.Edge#getTarget()
     */
    public Node getTarget() {
        return target;
    }

    /*
     * @see org.graffiti.graph.Edge#isDirected()
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * 
     */
    void link() {
        if (directed) {
            if (source != target) {
                source.addEdge(DIR_OUT, this);
                target.addEdge(DIR_IN, this);
            } else {
                source.addEdge(DIR_LOOP, this);
            }

            ++((FastGraph) getGraph()).numberOfDirectedEdges;
        } else {
            if (source != target) {
                source.addEdge(UNDIR_OUT, this);
                target.addEdge(UNDIR_IN, this);
            } else {
                source.addEdge(UNDIR_LOOP, this);
            }
        }
    }

    /*
     * @see org.graffiti.util.Linkable#setLink(java.lang.Object,
     * org.graffiti.util.ListLink)
     */
    public void setLinkNode(End e, MultiLinkNode<FastEdge> l) {
        if (e == null) {
            globalLink = l;
        } else {
            incidencyLinks.put(e, l);
        }
    }

    /**
     * 
     */
    void unlink() {
        if (directed) {
            if (source != target) {
                source.removeEdge(DIR_OUT, this);
                target.removeEdge(DIR_IN, this);
            } else {
                source.removeEdge(DIR_LOOP, this);
            }

            --((FastGraph) getGraph()).numberOfDirectedEdges;
        } else {
            if (source != target) {
                source.removeEdge(UNDIR_OUT, this);
                target.removeEdge(UNDIR_IN, this);
            } else {
                source.removeEdge(UNDIR_LOOP, this);
            }

        }

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
