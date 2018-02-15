// =============================================================================
//
//   CompactorLevelling.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.compactor;

import java.util.ArrayList;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.levelling.LevellingAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.plugins.views.fast.AttributeUtil;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class CompactorLevelling extends AbstractAlgorithm implements LevellingAlgorithm {

    private static final String NAME = "Compactor Levelling";
    
    public static final String FIRST_LEVEL_KEY = "CompactorLevelling_first_level";
    
    private SugiyamaData data;
    
    private LevelNode firstLevel;
    
    public CompactorLevelling() {
    }
    
    public CompactorLevelling(LevelNode firstLevel) {
        this.firstLevel = firstLevel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SugiyamaData getData() {
        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setData(SugiyamaData data) {
        this.data = data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType.equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsBigNodes() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsConstraints() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        firstLevel = (LevelNode) data.getObject(FIRST_LEVEL_KEY);
        NodeLayers levels = data.getLayers();
        
        LevelNode levelNode = firstLevel;
        int levelCount = 0;
        
        while (levelNode != null) {
            for (RealNode node = levelNode.first.east; node != null; node = node.east) {
                node.makeDummy(graph);
            }
            
            levelNode = levelNode.southLevel;
            levelCount++;
        }
        
        for (int lvl = 0; lvl < levelCount; lvl++) {
            levels.addLayer();
        }
        
        levelNode = firstLevel;
        int lvl = 0;
        
        while (levelNode != null) {
            ArrayList<Node> level = levels.getLayer(lvl);
            int x = 0;
            for (RealNode node = levelNode.first.east; node != null; node = node.east) {
                node.wireDummy(graph);
                Node graphNode = node.getGraphNode();
                level.add(graphNode);
                graphNode.setInteger(SugiyamaConstants.PATH_LEVEL, lvl);
                graphNode.setDouble(SugiyamaConstants.PATH_XPOS, x);
                if (node.color != null) {
                    AttributeUtil.setFrameColor(graphNode, node.color);
                    AttributeUtil.setFillColor(graphNode, node.color);
                }
                x++;
            }
            
            levelNode = levelNode.southLevel;
            lvl++;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
