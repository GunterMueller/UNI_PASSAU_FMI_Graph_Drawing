package org.graffiti.plugins.algorithms.fas;

import java.util.Iterator;

import org.graffiti.graph.Edge;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;

public class FindElementaryCircuits2 extends AbstractAlgorithm {

    /**
     * Returns the name of the algorithm
     * 
     * @return name of the algorithm
     */
    public String getName() {
        return "Label edges with number of related elementary circuits";
    }

    /**
     * Sets the parameters of the algorithm
     * 
     * @param params
     *            the parameters of the algorithm
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
    }

    /**
     * Gets the parameters of the algorithm
     * 
     * @return the parameters of the algorithm
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        Parameter<?>[] parameter = new Parameter[0];
        return parameter;
    }

    /**
     * Checks the algorithms preconditions: - graph is directed
     * 
     * @throws PreconditionException
     *             if any of the preconditions is not satisfied.
     */
    @Override
    public void check() throws PreconditionException {

        Iterator<Edge> edgesIt = this.graph.getEdgesIterator();
        while (edgesIt.hasNext()) {
            if (!edgesIt.next().isDirected())
                throw new PreconditionException(
                        "The graph is not dircected. Can't solve the minimum Feedback Arc Set Problem");

        }
        if (this.graph.getNodes().size() == 0)
            throw new PreconditionException("Graph contains no nodes.");
    }

    public void execute() {
        DeleteEdgesWithMaximumCircuitNumber cc = new DeleteEdgesWithMaximumCircuitNumber(
                this.graph);
        // cc.getCircuits();
        cc.getCircuits();

    }

}
