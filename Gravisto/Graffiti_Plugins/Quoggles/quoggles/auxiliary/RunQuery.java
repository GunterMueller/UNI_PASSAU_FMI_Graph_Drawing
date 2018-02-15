package quoggles.auxiliary;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.graffiti.graph.Graph;

import quoggles.exceptions.QueryExecutionException;

/**
 * Interface of classes that provide a <code>runQuery</code> method.
 */
public interface RunQuery {

    /**
     * Executes the query given by the queryGraph and returns the result in
     * a table (list of lists).
     * 
     * @param qGraph the query graph to be executed
     * @param sourceNodes the nodes where to start the query
     * @param internalUseOnly true if there should be no side effects to the
     * graphical system etc.
     * @param curResult the result table to which the new query results will be
     * added
     * @param nodesTodo the stack of nodes that still have to be processed
     * 
     * @return the result of the query; it is a table i.e. an 
     * <code>ArrayList</code> of rows that are <code>Collections</code> 
     * themselves.
     * 
     * @throws (all sorts of) <code>QueryExecutionException</code>s
     * if anything goes wrong
     */
    public List runQuery(Graph qGraph, Collection sourceNodes, 
        boolean internalUseOnly, List curResult, Stack nodesTodo) 
        throws QueryExecutionException;
}
