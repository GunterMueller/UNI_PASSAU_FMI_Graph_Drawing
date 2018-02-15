// =============================================================================
//
//   GraphElementsDeletionEdit.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphElementsDeletionEdit.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.undo;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * <code>GraphElementsDeletionEdit</code> makes deletion of graph elements
 * undoable.
 * 
 * @author $Author $
 * @version $Revision: 5779 $
 */
public class GraphElementsDeletionEdit extends GraphElementsEdit {

    /**
     * 
     */
    private static final long serialVersionUID = -5068619471988896312L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(GraphElementsDeletionEdit.class.getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /**
     * this flag assures that execute method will be invoked before calling undo
     * or redo methods
     */
    protected boolean executed = false;

    /** set of graph elements that were selected for deletion. */
    private LinkedHashSet<GraphElement> graphElements;

    /**
     * temporary graph element set. It is necessary for update of graph elements
     * contained in the selection set.
     */
    private Set<GraphElement> tempGraphElements;

    /**
     * the name of this edit
     */
    private String presentationName;

    /*
     * The Builder Pattern simulates named optional parameters. It's a good
     * choice when designing classes whose constructors or static factories
     * would have more than a handful of parameters.
     * 
     * Usage: GraphElementsDeletionEdit edit = new
     * GraphElementsDeletionEdit.Builder(graphElementMap, graph,
     * elements).representationName(representationName).build();
     * 
     * Calling the method Builder.representationName(String representationName)
     * is optional.
     * 
     * The Builder Pattern is a safe choice, because it allows to easily add
     * optional parameters. It produces less code than the Telescoping
     * Constructor pattern and includes the possibility of making a class
     * immutable.
     * 
     * Using the builder instead of multiple constructors should be preferred in
     * the future. At the moment we'll keep the old constructor until it's
     * certain that removing it won't cause trouble.
     */
    /**
     * A builder for the {@code GraphElementsDeletionEdit} class.
     * 
     * @author donig
     * @version $Revision: 5779 $ $Date: 2009-10-23 13:07:21 +0200 (Fr, 23 Okt
     *          2009) $
     */
    public static class Builder {
        // required parameters
        private final Map<GraphElement, GraphElement> geMap;
        private final Graph graph;
        private final Collection<? extends GraphElement> graphElements;

        // optional parameters
        private String presentationName = null;

        // the required parameters are set by the builders constructor
        public Builder(Map<GraphElement, GraphElement> geMap, Graph graph,
                Collection<? extends GraphElement> graphElements) {
            this.geMap = geMap;
            this.graph = graph;
            this.graphElements = graphElements;
        }

        // the optional parameters are set by methods returning the builder
        public Builder presentationName(String presentationName) {
            this.presentationName = presentationName;
            return this;
        }

        // the method build() returns the new object
        public GraphElementsDeletionEdit build() {
            return new GraphElementsDeletionEdit(this);
        }
    }

    /*
     * The old public constructor should not be used. Please use the builder so
     * the old constructor can be removed one day.
     */
    /**
     * @see GraffitiAbstractUndoableEdit#GraffitiAbstractUndoableEdit(Map)
     */
    public GraphElementsDeletionEdit(Collection<GraphElement> graphElements,
            Graph graph, Map<GraphElement, GraphElement> geMap) {
        super(graph, geMap);
        this.graphElements = new LinkedHashSet<GraphElement>(graphElements);
        tempGraphElements = new HashSet<GraphElement>();
        presentationName = coreBundle
                .getString(this.graphElements.size() == 1 ? (this.graphElements
                        .iterator().next() instanceof Node ? "undo.deleteNode"
                        : "undo.deleteEdge") : "undo.deleteGraphElements");
    }

    /**
     * A private constructor to be used by the builder.
     * 
     * @param builder
     *            {@Code GraphElementsDeletionEdit}'s builder
     */
    private GraphElementsDeletionEdit(Builder builder) {
        super(builder.graph, builder.geMap);
        graphElements = new LinkedHashSet<GraphElement>(builder.graphElements);
        tempGraphElements = new HashSet<GraphElement>();
        presentationName = (builder.presentationName == null ? coreBundle
                .getString(graphElements.size() == 1 ? (graphElements
                        .iterator().next() instanceof Node ? "undo.deleteNode"
                        : "undo.deleteEdge") : "undo.deleteGraphElements")
                : builder.presentationName);
    }

    /**
     * Used to display the name for this edit.
     * 
     * @return the name of this edit.
     * 
     * @see javax.swing.undo.UndoableEdit
     */
    @Override
    public String getPresentationName() {
        return presentationName;
    }

    /**
     * Executes the deletion of selected graph elements
     */
    @Override
    public void execute() {
        executed = true;

        /* saves adjacent edges of nodes in the common graph elements set. */
        Set<Edge> adjacentEdges = new HashSet<Edge>();

        for (GraphElement ge : graphElements) {
            if (ge instanceof Node) {
                Collection<Edge> edgeList = ((Node) ge).getEdges();
                adjacentEdges.addAll(edgeList);
            }
        }

        graphElements.addAll(adjacentEdges);

        for (GraphElement ge : graphElements) {
            if (ge instanceof Edge) {
                graph.deleteEdge((Edge) ge);
            }
        }

        for (GraphElement ge : graphElements) {
            if (ge instanceof Node) {
                graph.deleteNode((Node) ge);
            }
        }
    }

    /**
     * Deletes the GraphElements stored in this edit.
     */
    @Override
    public void redo() {
        super.redo();

        if (!executed) {
            logger.info("The execute method hasn't been invocated");

            return;
        }

        for (GraphElement ge : graphElements) {
            if (ge instanceof Edge) {
                Edge newEdge = (Edge) getCurrentGraphElement(ge);
                tempGraphElements.add(newEdge);
                graph.deleteEdge(newEdge);
            }
        }

        for (GraphElement ge : graphElements) {
            if (ge instanceof Node) {
                Node newNode = (Node) getCurrentGraphElement(ge);
                tempGraphElements.add(newNode);
                graph.deleteNode(newNode);
            }
        }

        /*
         * updates all graph elements references in the set containing graph
         * elements.
         */
        graphElements.clear();
        graphElements.addAll(tempGraphElements);
        tempGraphElements.clear();
    }

    /**
     * Adds the deleted GraphElements stored in this edit.
     */
    @Override
    public void undo() {
        super.undo();

        if (!executed) {
            logger.info("The execute method hasn't been invocated");

            return;
        }

        for (GraphElement ge : graphElements) {
            if (ge instanceof Node) {
                geMap.put(ge, graph.addNodeCopy((Node) ge));
            }
        }

        for (GraphElement ge : graphElements) {
            if (ge instanceof Edge) {
                logger.info("undo the edge deleting");

                Edge oldEdge = (Edge) ge;
                assert (oldEdge.getSource() != null);
                assert (oldEdge.getTarget() != null);

                Node source = (Node) getCurrentGraphElement(oldEdge.getSource());
                Node target = (Node) getCurrentGraphElement(oldEdge.getTarget());
                assert (source != null);
                assert (target != null);
                assert (source.getGraph() == graph);
                assert (target.getGraph() == graph);

                Edge newEdge = graph.addEdgeCopy(oldEdge, source, target);
                assert (newEdge.getGraph() == graph);

                geMap.put(oldEdge, newEdge);
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
