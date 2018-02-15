// =============================================================================
//
//   IncrementalSugiyama.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Sugiyama.java 2147 2007-11-19 23:13:16Z brunner $

package org.graffiti.plugins.algorithms.sugiyama.incremental;

import java.util.Iterator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.dialog.ParameterDialog;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.animation.Animation;
import org.graffiti.plugins.algorithms.sugiyama.Sugiyama;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAnimation;
import org.graffiti.plugins.algorithms.sugiyama.constraints.ConstraintBuilder;
import org.graffiti.plugins.algorithms.sugiyama.dialog.LegacyAlgorithmConfigurationDialog;
import org.graffiti.plugins.algorithms.sugiyama.util.DummyNodeUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.IncrementalSugiyamaData;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.selection.Selection;

/**
 * This class extends the sugiyama-framework class <code>Sugiyama</code> and
 * handles the possibility to alter the graph after the sugiyama algorithm has
 * run in a way that contains the 'mental map' a good as possible.
 * 
 * It uses <tt>Animation</tt> to be notified of the modifications done to the
 * graph and to compute the necessary changes to keep the drawing well arranged.
 * 
 * @author Christian Brunnermeier
 */
@SuppressWarnings("deprecation")
public class IncrementalSugiyama extends Sugiyama {

    /* The name of this algorithm */
    private final String ALGORITHM_NAME = "Incremental Sugiyama";

    /* The <tt>Animation</tt>-Object used by the incremental sugiyama */
    private Animation animation = null;

    /* The minimal x distance of two nodes. */
    private static int minimal_offset_x = 100;

    /* The minimal y distance of two nodes. */
    private static int minimal_offset_y = 100;

    /** toggle debug mode */
    public static boolean DEBUG = false;

    /*  ********************* FUNCTIONS ******************************** */

    /**
     * Default constructor.
     * 
     * Creates a new <code>SugiyamaData</code>-bean and loads saved preferences.
     */
    public IncrementalSugiyama() {
        super(true);
        data = new IncrementalSugiyamaData();
        initData();
    }

    /**
     * This methods delegates most of it's work to an
     * <tt>IncrementalSugiyamaAnimation</tt> object which handles the four
     * phases and the incremental part.
     */
    @Override
    public void execute() {
        data.setGraph(this.graph);
        DummyNodeUtil.collectDummies(data, graph);
        ConstraintBuilder constraintBuilder = new ConstraintBuilder(graph, data);
        constraintBuilder.buildConstraints();

        if (data.getBigNodesPolicy() == SugiyamaConstants.BIG_NODES_SHRINK) {
            shrinkBigNodes();
        }

        // From here on an IncrementalSugiyamaAnimation-object will do the rest
        // of the work.
        animation = getAnimation();
    }

    /**
     * Calls the function reset() of <tt>Sugiyama</tt> and resets the data used
     * for the incremental part.
     */
    @Override
    public void reset() {
        if (graph != null) {
            Iterator<Node> it = graph.getNodesIterator();
            while (it.hasNext()) {
                Node node = it.next();
                Iterator<Edge> it2 = node.getDirectedOutEdgesIterator();
                while (it2.hasNext()) {
                    Edge edge = it2.next();
                    try {
                        edge.removeAttribute(SugiyamaConstants.PATH_INC_EDGE);
                    } catch (AttributeNotFoundException e) {
                        // there was no SugiyamaEdge set
                    }
                }
                try {
                    node.removeAttribute(SugiyamaConstants.PATH_INC_NODE);
                } catch (AttributeNotFoundException e) {
                    // there was no SugiyamaNode set
                }
            }

            if (animation != null) {
                ((IncrementalSugiyamaAnimation) animation).reset();
            }
        }
        super.reset();
    }

    /**
     * Returns the frameworks parameter-dialog.
     * 
     * @see org.graffiti.plugins.algorithms.sugiyama.dialog.AlgorithmConfigurationDialog
     */
    @Override
    public ParameterDialog getParameterDialog(Selection sel) {
        GraffitiSingleton gSingleton = GraffitiSingleton.getInstance();
        ParameterDialog paramDialog;

        paramDialog = new LegacyAlgorithmConfigurationDialog(gSingleton
                .getMainFrame().getEditComponentManager(), gSingleton
                .getMainFrame(), getAlgorithmParameters(), sel,
                this.ALGORITHM_NAME, this.data);

        return paramDialog;
    }

    /**
     * The animation framework is always used for the incremental sugiyama
     * algorithm.
     * 
     * @return true
     * 
     */
    @Override
    public boolean supportsAnimation() {
        return true;
    }

    /**
     * An <tt>IncrementalSugiyamaAnimation</tt> is returned witch handles the
     * four regular phases as well as the incremental part of this algorithm.
     */
    @Override
    public Animation getAnimation() {
        SugiyamaAlgorithm[] copiedAlgorithms = new SugiyamaAlgorithm[NUMBER_OF_PHASES];
        SugiyamaData copiedData = data.copy();
        copiedData.setGraph(this.graph);

        for (int i = 0; i < NUMBER_OF_PHASES; i++) {
            try {
                SugiyamaAlgorithm a = algorithms[i].getClass().newInstance();
                a.attach(this.graph);
                a.setData(copiedData);
                if (algorithms[i].getParameters() != null) {
                    a.setParameters(algorithms[i].getParameters());
                }
                copiedAlgorithms[i] = a;
            } catch (IllegalAccessException iae) {
                copiedAlgorithms[i] = algorithms[i];
                System.err.println("ERROR: Cannot copy algorithm for phase "
                        + i);
            } catch (InstantiationException ie) {
                copiedAlgorithms[i] = algorithms[i];
                System.err.println("ERROR: Cannot copy algorithm for phase "
                        + i);
            }
        }
        copiedData.setSelectedAlgorithms(copiedAlgorithms);
        IncrementalSugiyamaAnimation animation = new IncrementalSugiyamaAnimation(
                copiedAlgorithms, copiedData, this.graph);
        setAnimation(animation);
        return animation;
    }

    /**
     * Adds the <tt>HashMapAttribute</tt> sugiyama to the given
     * <tt>GraphElement</tt> if it doesn't exist, yet.
     */
    public static void addSugiyamaAttribute(GraphElement element) {
        try {
            element.getAttribute(SugiyamaConstants.PATH_SUGIYAMA);
        } catch (AttributeNotFoundException anfe) {
            element.addAttribute(new HashMapAttribute(
                    SugiyamaConstants.PATH_SUGIYAMA), "");
        }
    }

    /**
     * Writes the given <tt>String</tt> to the console if <code>DEBUG</code> is
     * set to <code>true</code>.
     */
    public static void debug(String text) {
        if (DEBUG) {
            System.out.println(text);
        }
    }

    /*  ***************** GETTER AND SETTER **************************** */

    /**
     * Getter-method to access this algorithm's name
     * 
     * @return Returns the name of this algorithm
     */
    @Override
    public String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    public boolean supportsBigNodes() {
        return true;
    }

    @Override
    public boolean supportsConstraints() {
        return false;
    }

    public boolean supportsHorizontalSugiyama() {
        return true;
    }

    public boolean supportsRadialSugiyama() {
        return false;
    }

    /**
     * Sets the animation.
     * 
     * @param animation
     *            the animation to set.
     */
    public void setAnimation(SugiyamaAnimation animation) {
        this.animation = animation;
    }

    /**
     * Returns the minimal_offset_x.
     * 
     * @return the minimal_offset_x.
     */
    public static int getMinimal_offset_x() {
        return minimal_offset_x;
    }

    /**
     * Returns the minimal_offset_y.
     * 
     * @return the minimal_offset_y.
     */
    public static int getMinimal_offset_y() {
        return minimal_offset_y;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
