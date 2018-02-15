/*==============================================================================
*
*   RunTests.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: RunTests.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.eval;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.visnacom.controller.ViewPanel;
import org.visnacom.model.CompoundGraph;
import org.visnacom.model.Edge;
import org.visnacom.model.Node;
import org.visnacom.model.View;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.sugiyama.algorithm.CrossCount;
import org.visnacom.sugiyama.algorithm.EdgeIteratorOfLevel;
import org.visnacom.sugiyama.model.CompoundLevel;
import org.visnacom.sugiyama.model.DummyNode;
import org.visnacom.sugiyama.model.LocalHierarchy;
import org.visnacom.sugiyama.model.SugiCompoundGraph;
import org.visnacom.sugiyama.model.SugiNode;
import org.visnacom.view.DefaultDrawingStyle;
import org.visnacom.view.Geometry;


/**
 * this class executes the evaluation testruns
 */
public class RunTests {
    //~ Static fields/initializers =============================================

    /**
     * this value indicates, whether each single expand is measured. if true,
     * the test are much slower. And a different db-table is used.
     */
    public static final boolean singleExpandMode = false;
    /**
     * this value indicates, which creation algorihtm is used.
     */
    private static boolean alternativeTests = true;
    
    //if true, each graph is printed.
    private static final boolean printGraphs = false;

    /* parameters for usual tests */

    //        private static int[] numNodes = {100, 75, 50, 35, 20};
    private static int[] numNodes = {20, 35, 50, 75, 100};

    private static double[] density = {0.3, 0.2, 0.1, 0.05, 0.01};

    private static double[] meanDegree = {2.0, 4.0, 6.0, 8.0, 10.0, 15.0};

    /* parameters for modified creation-algorithm */

    private static int[] numNodesAlternative =
        {20, 50, 100, 200, 400, 600, 800, 1000};
    private static double[] meanDegreeAlternative = {15.0};

    private static double[] absolute_complexity = {2.3};

    //    private static double[] absolute_complexity = {-1.0};
    private static double[] relNumEdges = {1.0};

    /*valid for both tests */
    private static final int num_runs = 1;
    private static int numOfGraphs = 1; //20

    //~ Methods ================================================================

    /**
     *
     * @return the used table. depends on singleExpandMode
     */
    public static String getTableNameToWrite() {
        if(RunTests.singleExpandMode) {
            return "  myschema.test_results2 ";
        } else {
            return "  myschema.test_results ";
        }
    }

    /**
     * recursive method to contract the whole view.
     * 
     * @param view DOCUMENT ME!
     * @param node DOCUMENT ME!
     */
    public static void contractAll(View view, Node node) {
        if(view.hasChildren(node)) {
            for(Iterator it = view.getChildrenIterator(node); it.hasNext();) {
                contractAll(view, (Node) it.next());
            }

            view.contract(node);
        }
    }

    /**
     * this method relates on the properness of the compoundgraph. all edges
     * normalized, no empty levels.
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static IntPair computeCrossingsAndCuts(SugiCompoundGraph s) {
        List parents = new LinkedList();
        parents.add(s.getMetricRoot());

        int result = computeCrossingsRec(s, parents);
        IntPair crossCut = new IntPair();
        crossCut.int1 = result;
        computeCuts(s, crossCut);
        return crossCut;
    }

    /**
     * there are two scenarios now, usual tests and an alternative test with
     * mean complexity using
     *
     * @param numNodes the index in the numNodes array
     * @param density the index in the density array
     * @param meanDegree DOCUMENT ME!
     * @param relNumEdges DOCUMENT ME!
     * @param absoluteComplexity DOCUMENT ME!
     * @param seed DOCUMENT ME!
     * @param alternativeModus DOCUMENT ME!
     */
    public static void executeSingleTest(int numNodes, double density,
        double meanDegree, double relNumEdges, double absoluteComplexity,
        int seed, boolean alternativeModus) {
        Random expandRan = new Random(seed);

        for(int run = 1; run <= num_runs; run++) {
            ViewPanel viewPanel = new ViewPanel();
            viewPanel.getGeometry().detach(viewPanel);
            viewPanel.getGeometry().setDrawingStyle(new DefaultDrawingStyle(
                    viewPanel.getGeometry()), false);

            Profiler.reset();

            try {
                Profiler.start(Profiler.TIME_GRAPH_CREATION);
                if(alternativeModus) {
                    System.out.println(numNodes + ";" + meanDegree + ";"
                        + relNumEdges + ";" + absoluteComplexity + ";" + seed);

                    int numEdges = (int) (numNodes * relNumEdges);
                    RandomGraphs.constructRandomGraphComplexitydependant(numNodes,
                        meanDegree, numEdges, viewPanel.getView(),
                        absoluteComplexity, seed);
                } else {
                    System.out.println(numNodes + ";" + density + ";"
                        + meanDegree + ";" + seed);
                    RandomGraphs.constructRandomGraph(viewPanel.getView(),
                        numNodes, density, meanDegree, seed);
                }

                Profiler.stop(Profiler.TIME_GRAPH_CREATION);
            } catch(RandomGraphs.CreationError e) {
                System.out.println(e);
                return;
            }

            /*introduce additional node as single child of root, that works as root
              in further actions. is done to avoid exceptions at contracting the root */
            //attention! this node pollutes the value 'mean_degree_act',
            //'density_act'(does not directly exist), ...
            viewPanel.getView().split(viewPanel.getView().getChildren(viewPanel.getView()
                                                                               .getRoot()));
            assert viewPanel.getView().getChildren(viewPanel.getView().getRoot())
                            .size() == 1;

            /*contract completely*/
            /* but only up to children of actual root */
            for(Iterator it =
                    viewPanel.getView().getChildrenIterator(viewPanel.getView()
                                                                     .getRoot());
                it.hasNext();) {
                contractAll(viewPanel.getView(), (Node) it.next());
            }

            viewPanel.getGeometry().setDrawingStyle(new SugiyamaDrawingStyle(
                    viewPanel.getGeometry(), SugiyamaDrawingStyle.FINAL_STYLE),
                true);

            // that is important , so that the animation does transfer the new 
            //coordinates to the original geometry object
            viewPanel.getGeometry().attach(viewPanel);

            Geometry geoclone = null;
            if(RunTests.singleExpandMode) {
                /* create a copy of the view attached to the same basegraph */
                ViewPanel viewPanelclone = new ViewPanel();
                viewPanelclone.newView(viewPanel);
                geoclone = viewPanelclone.getGeometry();
            }

            if(!RunTests.singleExpandMode) {
                Profiler.start(Profiler.TIME_EXPAND);
            }

            /*expand completely */
            int num_expands =
                expandAll(viewPanel.getGeometry(), geoclone, expandRan.nextInt());

            if(!RunTests.singleExpandMode) {
                Profiler.stop(Profiler.TIME_EXPAND);
            }

            Profiler.set(Profiler.NUM_EXPANDS, new Integer(num_expands));

            Geometry geo = viewPanel.getGeometry();

            if(printGraphs) {
                //                            DummyPicture.show(geo);
                DummyPicture.write(geo,
                    "expanded" + numNodes + "-" + "-" + meanDegree + "-"
                    + absoluteComplexity + "-" + seed, "pdf");
                //                DummyPicture.show(((SugiyamaDrawingStyle) geo.getDrawingStyle()).s);
                //                DummyPicture.write(((SugiyamaDrawingStyle) geo.getDrawingStyle()).s,
            }

            Rectangle rootRect = geo.shape(geo.getView().getRoot());
            Profiler.set(Profiler.EXPANDED_AREA_WIDTH,
                new Integer(rootRect.width));
            Profiler.set(Profiler.EXPANDED_AREA_HEIGHT,
                new Integer(rootRect.height));

            IntPair crossingsCutsExp =
                computeCrossingsAndCuts(((SugiyamaDrawingStyle) geo
                    .getDrawingStyle()).s);
            Profiler.set(Profiler.EXPAND_CROSSINGS,
                new Integer(crossingsCutsExp.int1));
            Profiler.set(Profiler.EXPAND_CUTS,
                new Integer(crossingsCutsExp.int2));

            /* static layout */
            IntPair crossingsCutsST;
            if(!RunTests.singleExpandMode) {
                geo.redraw();
                crossingsCutsST =
                    computeCrossingsAndCuts(((SugiyamaDrawingStyle) geo
                        .getDrawingStyle()).s);
            } else {
                /*otherwise the redraw call is not necessary, because there were already some redraw calls
                 * during expandAll */
                crossingsCutsST =
                    computeCrossingsAndCuts(((SugiyamaDrawingStyle) geoclone
                        .getDrawingStyle()).s);
            }

            Profiler.set(Profiler.STATIC_CROSSINGS,
                new Integer(crossingsCutsST.int1));
            Profiler.set(Profiler.STATIC_CUTS, new Integer(crossingsCutsST.int2));
            Profiler.saveTestResult();
            if(printGraphs) {
                //            DummyPicture.show(singleExpandMode?geoclone:geo);
                DummyPicture.write(singleExpandMode ? geoclone : geo,
                    "static" + numNodes + "-" + "-" + meanDegree + "-"
                    + absoluteComplexity + "-" + seed, "pdf");
            }
        }
    }

    /**
     * expands the view completely. if singleExpandMode, expands the clone, too. 
     *
     * @param geo the view to expand
     * @param clone the geometry object of the clone view. might be null, if
     *        not each single expand should be measured.
     * @param seed DOCUMENT ME!
     *
     * @return how many expand operations have been performed
     */
    public static int expandAll(Geometry geo, Geometry clone, int seed) {
        List nodes = new LinkedList();

        View view = geo.getView();
        nodes.add(view.getRoot());

        if((clone == null) == RunTests.singleExpandMode) {
            System.err.println("internal error in RunTests.expandAll");
            System.exit(-1);
        }

        int result = 0;
        Random ran = new Random(seed);

        /*this is used to switch the sugiyama algorithm off at expanding the clone.
         * the clone is only interesting at the static layout */
        DefaultDrawingStyle dds;
        SugiyamaDrawingStyle sds;
        if(RunTests.singleExpandMode) {
            dds = new DefaultDrawingStyle(clone);
            sds = (SugiyamaDrawingStyle) clone.getDrawingStyle();
            sds.setDrawingStyle(SugiyamaDrawingStyle.FINAL_STYLE);
        } else {
            ((SugiyamaDrawingStyle) geo.getDrawingStyle())
            .supressMetricLayoutAtExpand();
        }
        while(!nodes.isEmpty()) {
            //breath-first-search
            Node n = (Node) nodes.remove(0);

            if(!view.hasChildren(n)) {
                Profiler.start(Profiler.TIME_SINGLE_EXPAND);
                view.expand(n);
                Profiler.stop(Profiler.TIME_SINGLE_EXPAND);
                if(view.hasChildren(n)) {
                    result++;

                    if(RunTests.singleExpandMode) {
                        clone.setDrawingStyle(dds, false);
                        clone.getView().expand(n);
                        Profiler.start(Profiler.TIME_SINGLE_STATIC);
                        clone.setDrawingStyle(sds, true);
                        Profiler.stop(Profiler.TIME_SINGLE_STATIC);
                        Profiler.transferExpandTimes(view.getNumOfNodes());
                    }
                }
            }

            //only for orientation at the very large test cases
            if(view.getNumOfNodes() > 75 && singleExpandMode
                && !alternativeTests) {
                System.out.println("expanded view has " + view.getNumOfNodes()
                    + " nodes");
            }

            nodes.addAll(view.getChildren(n));
        }

        if(!RunTests.singleExpandMode) {
            ((SugiyamaDrawingStyle) geo.getDrawingStyle())
            .doMetricLayoutAfterExpand(geo);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        Profiler.turnOnDatabase();
        evaluateRandomGraphs(alternativeTests);
    }

    /**
     *  if the given node is a real node,
     * there is a usual cut, but if it is a dummynode, it possibly is a
     * crossing with another edge path or nothing.
     *
     * @param s DOCUMENT ME!
     * @param e a edge that cuts through the given node
     * @param sn a node
     * @param crossCut the pair of cuts and crossings to increment
     */
    private static void analyseCut(SugiCompoundGraph s, Edge e, SugiNode sn,
        IntPair crossCut) {
        if(!sn.isDummyNode()) {
            crossCut.int2++;
            return;
        }

        if(((DummyNode) sn).getType() == DummyNode.HORIZONTAL) {
            assert s.getAdjEdges(sn).isEmpty();
            //nothing
            return;
        }

        if(!s.hasChildren(sn)) {
            //if a dummy node is cut and has no children, it surely
            // represents
            //a edge segment.
            crossCut.int1++;
            return;
        }

        assert ((DummyNode) sn).getType() == DummyNode.LOCAL_OR_EXTERNAL;
        assert s.getAdjEdges(sn).size() == 1;
        assert s.getChildren(sn).size() == 1;

        DummyNode child = (DummyNode) s.getChildren(sn).get(0);
        CompoundLevel sourceClev = ((SugiNode) e.getSource()).getClev();

        //there are three possible areas for cutting
        if(child.getClev().isInitialSubstringOf(sourceClev)) {
            assert !child.getClev().equals(sourceClev);
            //nothing
            return;
        }

        if(sourceClev.compareTo(child.getClev()) < 0) {
            if(s.getInEdgesIterator(sn).hasNext()) {
                crossCut.int1++;
            }
        } else {
            if(s.getOutEdgesIterator(sn).hasNext()) {
                crossCut.int1++;
            }
        }
    }

    /**
     * computes the crossings of a single level
     *
     * @param cpg DOCUMENT ME!
     * @param level_i DOCUMENT ME!
     * @param level_iP1 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static int computeCrossingsLocal(CompoundGraph cpg, List level_i,
        List level_iP1) {
      
        int crossings2 =
            CrossCount.simpleAndEfficientCrossCount(level_i, level_iP1,
                new EdgeIteratorOfLevel(level_i, cpg));

        return crossings2;
    }

    /**
     * computes the crossings of edges between the children of the given nodes.
     * the given nodes must lie on the same level. recursively computes the
     * crossings of descendants, too.
     *
     * @param s the graph containing the nodes
     * @param nodes a list of nodes
     *
     * @return the crossings of edges between the nodes and all its descendants
     */
    private static int computeCrossingsRec(SugiCompoundGraph s, List nodes) {
        List upperChildren;
        List lowerChildren;
        int i = 0;
        int crossings = 0;
        upperChildren = SugiCompoundGraph.getChildrenAtSubLevel(0, nodes);
        if(upperChildren.isEmpty()) {
            upperChildren = SugiCompoundGraph.getChildrenAtSubLevel(1, nodes);
            i = 1;
        }

        LocalHierarchy.updatePositions(upperChildren);
        lowerChildren = SugiCompoundGraph.getChildrenAtSubLevel(i + 1, nodes);
        while(!upperChildren.isEmpty()) {
            crossings += computeCrossingsRec(s, upperChildren);
            if(!lowerChildren.isEmpty()) {
                LocalHierarchy.updatePositions(lowerChildren);
                crossings += computeCrossingsLocal(s, upperChildren,
                    lowerChildren);
            }

            i++;
            upperChildren = lowerChildren;
            lowerChildren =
                SugiCompoundGraph.getChildrenAtSubLevel(i + 1, nodes);
        }

        return crossings;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     * @param crossCut DOCUMENT ME!
     */
    private static void computeCuts(SugiCompoundGraph s, IntPair crossCut) {
        //        int cuts = 0;
        //        int crossings = 0;
        for(Iterator it = s.getAllEdgesIterator(); it.hasNext();) {
            Edge e = (Edge) it.next();
            if(s.getParent(e.getSource()) == s.getParent(e.getTarget())) {
                continue;
            }

            //fetch ancestors
            Stack leftStack = new Stack();
            Stack rightStack = new Stack();
            SugiNode leftParent = (SugiNode) s.getParent(e.getSource());
            SugiNode rightParent = (SugiNode) s.getParent(e.getTarget());
            if(leftParent.getAbsoluteX() > rightParent.getAbsoluteX()) {
                SugiNode temp = leftParent;
                leftParent = rightParent;
                rightParent = temp;
            }

            while(leftParent != null) {
                leftStack.push(leftParent);
                leftParent = (SugiNode) s.getParent(leftParent);
            }

            while(rightParent != null) {
                rightStack.push(rightParent);
                rightParent = (SugiNode) s.getParent(rightParent);
            }

            //pop until nca is reached
            SugiNode nca = (SugiNode) leftStack.pop();
            rightStack.pop();
            assert nca == s.getRoot();

            SugiNode lowerBound = (SugiNode) leftStack.pop();
            SugiNode upperBound = (SugiNode) rightStack.pop();
            while(lowerBound == upperBound) {
                nca = lowerBound;
                lowerBound = (SugiNode) leftStack.pop();
                upperBound = (SugiNode) rightStack.pop();
            }

            assert leftStack.size() == rightStack.size();

            List nodesBetween =
                new LinkedList(nca.getChildrenAtLevel(
                        lowerBound.getClev().getTail()));
            while(true) {
                //filter
                for(Iterator it2 = nodesBetween.iterator(); it2.hasNext();) {
                    SugiNode sn = (SugiNode) it2.next();
                    if(sn.getAbsoluteX() < lowerBound.getAbsoluteX()
                        || sn.getAbsoluteX() > upperBound.getAbsoluteX()) {
                        it2.remove();
                    } else {
                        if(sn != lowerBound && sn != upperBound) {
                            analyseCut(s, e, sn, crossCut);
                        }
                    }
                }

                //proceed
                if(!leftStack.isEmpty()) {
                    lowerBound = (SugiNode) leftStack.pop();
                    upperBound = (SugiNode) rightStack.pop();
                    nodesBetween =
                        SugiCompoundGraph.getChildrenAtSubLevel(lowerBound.getClev()
                                                                          .getTail(),
                            nodesBetween);
                } else {
                    break;
                }
            }
        }

        //        return cuts;
    }

    /**
     * DOCUMENT ME!
     *
     * @param alternativeModus
     */
    private static void evaluateRandomGraphs(boolean alternativeModus) {
        Random seedDistributor = new Random(1);
        for(int graph = 1; graph <= numOfGraphs; graph++) {
            if(!alternativeModus) {
                for(int i = 0; i < numNodes.length; i++) {
                    for(int j = 0; j < density.length; j++) {
                        for(int k = 0; k < meanDegree.length; k++) {
                            int seed = seedDistributor.nextInt();
                            executeSingleTest(numNodes[i], density[j],
                                meanDegree[k], -1, -1, seed, false);
                        }
                    }
                }
            } else {
                for(int i = 0; i < numNodesAlternative.length; i++) {
                    for(int k = 0; k < meanDegreeAlternative.length; k++) {
                        for(int j = 0; j < relNumEdges.length; j++) {
                            //                            for(int l = 0; l < rel_edgesBetweensiblings.length; l++) {
                            for(int m = 0; m < absolute_complexity.length;
                                m++) {
                                int seed = seedDistributor.nextInt();
                                executeSingleTest(numNodesAlternative[i], -1,
                                    meanDegreeAlternative[k], relNumEdges[j],
                                    absolute_complexity[m], seed, true);
                            }
                        }
                    }
                }
            }
        }
    }
}
