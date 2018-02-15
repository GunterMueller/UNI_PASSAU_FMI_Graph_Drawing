// =============================================================================
//
//   Compactor.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.compactor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.Sugiyama;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAnimation;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.DummyCrossMin;
import org.graffiti.plugins.algorithms.sugiyama.decycling.SCCDecyclingWithoutDeletion;
import org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.CyclicBrandesKoepf;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class CompactorAlgorithm extends AbstractAlgorithm implements CalculatingAlgorithm {
    private static final String NAME = "Compactor";
    
    private static boolean SINGLE_WALK = true;
    
    private LevelNode firstLevel;
    private int nodesCount;
    private AlgorithmResult result;
    
    private int levelReduction;
    private long time;

    public CompactorAlgorithm() {
        Logger.getLogger(Sugiyama.class.getCanonicalName()).setLevel(Level.OFF);
        Logger.getLogger(SugiyamaAnimation.class.getCanonicalName()).setLevel(
                Level.OFF);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        
        initialize();
        
        compact();
        
        for (GraphElement element : graph.getGraphElements()) {
            if (element.containsAttribute(SugiyamaConstants.PATH_SUGIYAMA)) {
                element.removeAttribute(SugiyamaConstants.PATH_SUGIYAMA);
            }
        }
        
        result = new DefaultAlgorithmResult();
        
        Sugiyama sugiyama = new Sugiyama();
        Parameter<?>[] p = sugiyama.getAlgorithmParameters();
        SugiyamaData data = sugiyama.getSugiyamaData();
        data.putObject(CompactorLevelling.FIRST_LEVEL_KEY, firstLevel);
        SugiyamaAlgorithm[] algs = new SugiyamaAlgorithm[4];
        algs[0] = new SCCDecyclingWithoutDeletion();
        algs[1] = new CompactorLevelling();
        algs[2] = new DummyCrossMin();
        algs[3] = new CyclicBrandesKoepf();
        //algs[3] = new DummyLayout();
        for (int i = 0; i < 4; i++) {
            algs[i].setData(data);
            algs[i].attach(graph);
        }
        data.setSelectedAlgorithms(algs);
        ((BooleanParameter) data.getAlgorithmParameters()[0]).setValue(true);
        sugiyama.setAlgorithmParameters(p);
        sugiyama.reset();
        sugiyama.attach(graph);
        try {
            sugiyama.check();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        SugiyamaAnimation animation = (SugiyamaAnimation) sugiyama.getAnimation();
        animation.nextStep();
        animation.nextStep();
        
        while (animation.hasNextStep()) animation.nextStep();
        
        graph.getListenerManager().transactionFinished(this);

        result.addToResult("levelReduction", levelReduction);
        result.addToResult("time", String.format((Locale) null, "%f", time / 1000000000.0));
    }
    
    private void initialize() {
        Map<Integer, ArrayList<IncubatorNode>> levelMap = new HashMap<Integer, ArrayList<IncubatorNode>>();
        TreeSet<Double> levelYs = new TreeSet<Double>();
        Map<Node, IncubatorNode> nodeMap = new HashMap<Node, IncubatorNode>();
        int minLevel = Integer.MAX_VALUE;
        int maxLevel = Integer.MIN_VALUE;
        
        for (Node node : graph.getNodes()) {
            double x = node.getDouble(GraphicAttributeConstants.COORDX_PATH);
            double y = node.getDouble(GraphicAttributeConstants.COORDY_PATH);
            levelYs.add(y);
            IncubatorNode incubatorNode = new IncubatorNode(new VertexNode(node), x);
            int level = node.getInteger(SugiyamaConstants.PATH_LEVEL);
            minLevel = Math.min(minLevel, level);
            maxLevel = Math.max(maxLevel, level);
            ArrayList<IncubatorNode> list = levelMap.get(level);

            if (list == null) {
                list = new ArrayList<IncubatorNode>();
                levelMap.put(level, list);
            }
            
            list.add(incubatorNode);
            nodeMap.put(node, incubatorNode);
        }
        
        assert(minLevel == 0);
        
        @SuppressWarnings("unchecked")
        ArrayList<IncubatorNode>[] levels = (ArrayList<IncubatorNode>[]) new ArrayList<?>[maxLevel + 1];
        
        for (int level = 0; level <= maxLevel; level++) {
            ArrayList<IncubatorNode> list = levelMap.get(level);
            
            if (list == null) {
                list = new ArrayList<IncubatorNode>();
            }
            
            levels[level] = list;
        }
        
        for (int level = maxLevel - 1; level >= 0; level--) {
            ArrayList<IncubatorNode> list = levels[level];
            for (IncubatorNode incubatorNode : list) {
                incubatorNode.initEdges(level, levels, nodeMap, levelYs);
            }
        }
        
        firstLevel = null;
        LevelNode prevLevel = null;
        
        for (int lvl = 0; lvl <= maxLevel; lvl++) {
            ArrayList<IncubatorNode> level = levels[lvl];
            Collections.sort(level, IncubatorNode.INITIAL_X_COMPARATOR);
            
            for (IncubatorNode node : levels[lvl]) {
                node.sort();
            }
            
            LevelNode levelNode = new LevelNode();
            levelNode.northLevel = prevLevel;
            
            if (lvl == 0) {
                firstLevel = levelNode;
            } else {
                prevLevel.southLevel = levelNode;
            }
            
            prevLevel = levelNode;
            
            levelNode.first = new StartNode();
            levelNode.first.east = level.get(0).node;
            
            int size = level.size();
            
            for (int x = 1; x < size; x++) {
                RealNode n1 = level.get(x - 1).node;
                RealNode n2 = level.get(x).node;
                n1.east = n2;
                n2.west = n1;
            }
            
            levelNode.last = level.get(size - 1).node;
        }
        
        numberNodes();
    }
    
    private void numberNodes() {
        int nextIndex = 0;
        
        LevelNode levelNode = firstLevel;
        
        while (levelNode != null) {
            int x = 0;
            AnyNode node = levelNode.first;
            
            while (node != null) {
                node.x = x;
                node.index = nextIndex;
                
                x++;
                nextIndex++;
                RealNode nextNode = node.east;
                
                if (nextNode == null) {
                    levelNode.last = (RealNode) node;
                }
                
                node = nextNode;
            }
            
            levelNode = levelNode.southLevel;
        }
        
        nodesCount = nextIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }
    
    private void compact() {
        long startTime = System.nanoTime();
        int reduceCount = 0;
        
        LinkedList<LinkedList<Step>> list = findWalks();
        
        int col = 0;
        Color[] colors = new Color[] { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN };
        
        while (list.size() > 0) {
            reduceCount += list.size();
            for (LinkedList<Step> walk : list) {
                for (Step step : walk) {
                    step.contract();
                }
                
                if (firstLevel.southLevel != null) {
                    if (firstLevel.southLevel.northLevel != firstLevel) {
                        firstLevel = firstLevel.southLevel;
                    }
                }
                col = (col + 1) % colors.length;
            }
            
            numberNodes();
            list = findWalks();
        }
        
        long stopTime = System.nanoTime();
        time = stopTime - startTime;
        levelReduction = reduceCount;
//        System.out.println("Stopping compaction (" + reduceCount + " reduced in " + time / 1000000000.0 + "s)");
    }
    
    private LinkedList<LinkedList<Step>> findWalks() {
        LinkedList<LinkedList<Step>> list = new LinkedList<LinkedList<Step>>();
        Walker walker = new Walker(firstLevel, nodesCount);
        LinkedList<Step> steps = walker.findWalk();
        while (steps != null) {
            list.add(steps);
            if (SINGLE_WALK) return list;
            steps = walker.findWalk();
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlgorithmResult getResult() {
        return result;
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
