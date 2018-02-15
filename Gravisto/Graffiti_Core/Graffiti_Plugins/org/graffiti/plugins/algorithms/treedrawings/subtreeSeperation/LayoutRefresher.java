// =============================================================================
//
//   LayoutRefresher.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.PreconditionException.Entry;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.hv.HVComposition;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.tipover.TipoverComposition;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.tipover_mod.TipoverCompositionMod;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.tipover_mod2.TipoverCompositionMod2;
import org.graffiti.selection.Selection;

/**
 * This reconstructs a layout using the information saved as layout-Attributes
 * in the nodes of the given Graph.
 * 
 * @author Andreas
 */
public class LayoutRefresher extends AbstractAlgorithm {

    /**
     * Selection
     */
    private Selection selection;

    /**
     * factory used if a HVComposition has to be reconstructed.
     */
    private HVComposition factoryHV;

    /**
     * factory used if a TipoverComposition has to be reconstructed.
     */
    private TipoverComposition factoryTipover;

    private TipoverCompositionMod factoryTipoverMod;

    private TipoverCompositionMod2 factoryTipoverMod2;

    /**
     * reconstructed LayoutComposition reconstructed in the method
     * <code>check()</code>
     */
    private LayoutComposition reconstructedComposition;

    /**
     * Constructs a new instance.
     */
    public LayoutRefresher() {
        this.factoryHV = new HVComposition();
        this.factoryTipover = new TipoverComposition();
        this.factoryTipoverMod = new TipoverCompositionMod();
        this.factoryTipoverMod2 = new TipoverCompositionMod2();
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "LayoutRefresher";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        this.selection = ((SelectionParameter) params[0]).getSelection();
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter seleParam = new SelectionParameter("Root:",
                "Root of this tree.");

        return new Parameter[] { seleParam };
    }

    /*
     * Reconstructs the layout using the Attributes in the Nodes.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        this.graph.getListenerManager().transactionStarted(this);

        LayoutConstants.setMinimumHeightAndWidth(this.graph);

        long startTime = System.currentTimeMillis();

        this.reconstructedComposition.layout(new Point2D.Double(0.0, 0.0));

        long timeNeededForLayout = System.currentTimeMillis() - startTime;
        try {
            GraffitiSingleton.getInstance().getMainFrame().getStatusBar()
                    .showInfo(
                            "Width: "
                                    + this.reconstructedComposition.getWidth()
                                    + ", Height: "
                                    + this.reconstructedComposition.getHeight()
                                    + " Time needed for positioning Nodes: "
                                    + timeNeededForLayout + "ms", 60000);
        } catch (NullPointerException n) {
            // Never mind
        }
        // System.out.println(this.reconstructedComposition.showStructure());

        this.graph.getListenerManager().transactionFinished(this);

    }

    public LayoutComposition reconstructComposition(Node root)
            throws PreconditionException {

        // Get the type of the layout and ignore upper/lower case...
        String layoutType;
        try {
            layoutType = root.getString("layout.type").toLowerCase();
        } catch (Exception a) {
            Selection selection = new Selection();
            selection.add(root);
            PreconditionException errors = new PreconditionException();
            errors.add(a.getMessage()
                    + " The corresponding node will be selected.", selection);

            throw errors;
        }
        LinkedList<LayoutComposition> childCompositions = new LinkedList<LayoutComposition>();

        for (Node currentChildNode : root.getAllOutNeighbors()) {
            childCompositions
                    .add(this.reconstructComposition(currentChildNode));
        }

        if (layoutType.startsWith("h")) {
            try {

                if (childCompositions.size() > 2) {
                    Selection selection = new Selection();
                    selection.add(root);
                    PreconditionException errors = new PreconditionException();
                    errors
                            .add(
                                    "Cannot draw HV-Layout for node with degree > 2. The corresponding node will be selected.",
                                    selection);

                    throw errors;
                }
                return this.factoryHV.instance(root, childCompositions);

            } catch (PreconditionException e) {
                throw e;
            }
        } else if (layoutType.startsWith("t")) {
            try {
                return this.factoryTipover.instance(root, childCompositions);
            } catch (PreconditionException e) {
                throw e;
            }
        } else if (layoutType.startsWith("m1")) {
            try {
                return this.factoryTipoverMod.instance(root, childCompositions);
            } catch (PreconditionException e) {
                throw e;
            }
        } else if (layoutType.startsWith("m2")) {
            try {
                return this.factoryTipoverMod2
                        .instance(root, childCompositions);
            } catch (PreconditionException e) {
                throw e;
            }
        } else {
            Selection selection = new Selection();
            selection.add(root);
            PreconditionException errors = new PreconditionException();
            errors
                    .add("Unknown Attribute layout.type found: \"" + layoutType
                            + "\". The corresponding node will be selected.",
                            selection);

            throw errors;
        }

    }

    /**
     * Checks the following:<BR>
     * <BR>
     * 1. The given graph must be a tree.<BR>
     * 2. The layout-Attributes needed for the specified LayoutComposition must
     * be present and valid.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {

        try {
            Node root = GraphChecker.checkTree(this.graph, Integer.MAX_VALUE);
            this.reconstructedComposition = this.reconstructComposition(root);
        } catch (PreconditionException p) {
            // can be null if applied incorrectly
            this.selection.clear();

            Iterator<Entry> itr = p.iterator();
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
