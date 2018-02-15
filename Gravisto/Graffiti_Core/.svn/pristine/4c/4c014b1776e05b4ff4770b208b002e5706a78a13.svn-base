// =============================================================================
//
//   HelperNodeStripper.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: HelperNodeStripper.java 1549 2006-11-07 23:45:24Z keilhaue $

package org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.sorter;

import java.util.Iterator;
import java.util.TreeSet;

import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.PreconditionException.Entry;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.CostFunction;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.LayoutComposition;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.LayoutRefresher;
import org.graffiti.selection.Selection;

/**
 * This is the algorithm responsible for removing HelperNodes.
 * 
 * @author Andreas
 * @version $Revision: 1549 $ $Date: 2006-11-08 00:45:24 +0100 (Mi, 08 Nov 2006)
 *          $
 */
public class SubtreeSorter extends AbstractAlgorithm {

    /**
     * The root of the tree given in the attached Graph. This field is populated
     * using the method GraphChecker.checkTree()
     */
    private Node root = null;

    private Selection selection;

    private LayoutComposition reconstructedComposition = null;

    private LayoutRefresher layoutRefresher = null;

    /**
     * Constructs a new instance.
     */
    public SubtreeSorter() {
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "SubtreeSorter";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new Parameter[] {};
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
    }

    /*
     * Executes the HelperNodeStripper considering the parameters described
     * above.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        this.graph.getListenerManager().transactionStarted(this);

        this.layoutRefresher = new LayoutRefresher();

        SubtreeComparator comp = new SubtreeComparator(new CostFunction(
                "SIZE_WITH_ASPECT_RATIO", 1.41));
        this.sortSubtrees(comp, this.reconstructedComposition);

        this.layoutRefresher.attach(graph);
        try {
            this.layoutRefresher.check();
        } catch (PreconditionException e) {
            // This should never happen, because we did a check
            e.printStackTrace();
        }
        this.layoutRefresher.execute();

        this.graph.getListenerManager().transactionFinished(this);

    }

    private void sortSubtrees(SubtreeComparator comp,
            LayoutComposition composition) {
        TreeSet<LayoutComposition> orderedSubtrees = new TreeSet<LayoutComposition>(
                comp);
        for (LayoutComposition currentComposition : composition.getSubtrees()) {
            orderedSubtrees.add(currentComposition);
        }

        for (LayoutComposition currentComposition : orderedSubtrees) {
            Node currentRoot = currentComposition.getRoot();
            DoubleAttribute orderNumberAttribute = (DoubleAttribute) currentRoot
                    .getAttribute("layout.orderNumber");
            orderNumberAttribute
                    .setValue((double) LayoutComposition.orderSequenceNumber++);
            sortSubtrees(comp, currentComposition);
        }
    }

    /**
     * Check the following:<BR>
     * <BR>
     * 1. Is the given Graph a directed tree.<BR>
     * 2. If <code>selectedNodesPolicy</code> is <code>ONLY_LOCAL</code> or
     * <code>DOWNWARDS</code>: Is at least one Node selected?
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        this.layoutRefresher = new LayoutRefresher();
        try {
            this.root = GraphChecker.checkTree(this.graph, Integer.MAX_VALUE);
            this.reconstructedComposition = this.layoutRefresher
                    .reconstructComposition(root);
        } catch (PreconditionException p) {

            Iterator<Entry> itr = p.iterator();

            this.selection.clear();

            while (itr.hasNext()) {
                Selection selection = (Selection) itr.next().source;
                if (selection != null) {
                    this.selection.addSelection(selection);
                }
            }
            throw p;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
