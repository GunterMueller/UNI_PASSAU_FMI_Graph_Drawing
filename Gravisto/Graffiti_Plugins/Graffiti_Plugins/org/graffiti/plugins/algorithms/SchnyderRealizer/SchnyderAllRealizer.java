package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * This class just implements a little extension to the algorithm in
 * <code>SchnyderAllCanonicalOrders</code>. As there are canonical orders, which
 * calculate the same realizer, the extension checks for every calculated
 * canonical order if the created realizer already exists, and if yes does not
 * add it to the list of realizers.
 * 
 * @author hofmeier
 * @version $Revision: 5766 $ $Date: 2006-05-24 20:57:04 +0200 (Mi, 24 Mai 2006)
 *          $
 */
public class SchnyderAllRealizer extends SchnyderAllCanonicalOrders {
    /**
     * Creates an instance of this class
     * 
     * @param g
     *            the graph to be drawn
     * @param m
     *            the maximum number of realizers
     */
    public SchnyderAllRealizer(Graph g, int m) {
        super(g, m);
    }

    /**
     * Overrides the method from the superclass. Checks for each found realizer
     * if it already exists and only adds it to the list of realizers if not.
     */
    @Override
    public void createOrder(ECNode leaf) {
        LinkedList<Node> order = new LinkedList<Node>();
        while (leaf != null) {
            order.addFirst(leaf.wrapped);
            leaf = leaf.father;
        }
        this.canonicalOrder = order;
        this.enumerateAngles();
        Realizer realizer = this.createRealizer();
        if (!this.compareRealizer(realizer)) {
            orders.add(order);
            realizers.add(realizer);
            this.barycentricReps.add(new BarycentricRepresentation(realizer,
                    this.graph, this.outerNodes));
        }
    }

    /**
     * Method primitively runs through the list of already found realizers and
     * compares every realizer with the current one.
     * 
     * @param realizer
     *            the current realizer
     * @return true if the realizer already exists
     */
    protected boolean compareRealizer(Realizer realizer) {
        Iterator<Realizer> it = this.realizers.iterator();
        while (it.hasNext()) {
            Realizer comp = it.next();
            if (comp.compareTo(realizer) == 0)
                return true;
        }
        return false;
    }

    /**
     * Executes the algorithm. Just calls the method to create the edge
     * contraction tree from the superclass.
     */
    @Override
    public void execute() {
        this.createEdgeContractionTree();
    }
}
