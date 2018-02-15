// =============================================================================
//
//   AlaaReader.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.importers.alaa;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.io.AbstractInputSerializer;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.views.fast.AttributeUtil;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class AlaaReader extends AbstractInputSerializer {
    private static final String[] EXTENSIONS = { ".txt" };
    private static final String NAME = "Alaa Reader";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getExtensions() {
        return EXTENSIONS;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void read(InputStream in, Graph g) throws IOException {
        if (!g.containsAttribute(GraphicAttributeConstants.GRAPHICS)) {
            g.addAttribute(new GraphGraphicAttribute(), "");
        }
        
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<List<Integer>> lists = new LinkedList<List<Integer>>();
        int levelCount = Integer.valueOf(reader.readLine());
        int nodeCount = 0;
        
        for (int i = 0; i < levelCount; i++) {
            List<Integer> list = new LinkedList<Integer>();
            lists.add(list);
            String line = reader.readLine();
            String[] nodes = line.split("\\s");
            for (int n = 0; n < nodes.length; n++) {
                list.add(Integer.valueOf(nodes[n]));
                nodeCount++;
            }
        }
        
        Node[] nodes = new Node[nodeCount];
        int nextNodeIndex = 0;
        Iterator<List<Integer>> listIter = lists.iterator();
        for (int level = 0; level < levelCount; level++) {
            List<Integer> list = listIter.next();
            int levelSize = list.size();
            Iterator<Integer> nodeIter = list.iterator();
            for (int xpos = 0; xpos < levelSize; xpos++) {
                int nodeIndex = nodeIter.next();
                Node node = g.addNode();
                node.addAttribute(new NodeGraphicAttribute(), "");
                node.setInteger("index", nodeIndex);
                node.setInteger(SugiyamaConstants.PATH_LEVEL, level);
                node.setDouble(SugiyamaConstants.PATH_XPOS, xpos);
                CoordinateAttribute ca = (CoordinateAttribute) node
                        .getAttribute(GraphicAttributeConstants.COORD_PATH);
                ca.setCoordinate(new Point2D.Double(xpos * 50, level * 50));
                nodes[nextNodeIndex] = node;
                map.put(nodeIndex, nextNodeIndex);
                nextNodeIndex++;
            }
        }
        
        String line;
        
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s");
            int sourceIndex = Integer.valueOf(parts[0]);
            int targetIndex = Integer.valueOf(parts[1]);
            g.addEdge(nodes[map.get(sourceIndex)], nodes[map.get(targetIndex)], true);
        }
        //System.out.println("Line 1: " + line);
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
