// =============================================================================
//
//   HbgfReader.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.hbgf;

import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.io.AbstractInputSerializer;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.transitional.BendsAdapter;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;
import org.graffiti.plugins.views.fast.AttributeUtil;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class HbgfReader extends AbstractInputSerializer {
    private static final String[] EXTENSIONS = { ".hbgf", ".hgbf" };
    private static final String NAME = "HGBF Reader";

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
    public void read(InputStream ins, Graph g) throws IOException {
        DataInputStream in = new DataInputStream(ins);
        String signature = in.readUTF();
        if (!signature.equals("hgbf") && !signature.equals("hbgf")) throw new IOException("Invalid signature");
        String origName = in.readUTF();
        int configIndex = in.readInt();
        int nodeCount = in.readInt();
        int edgeCount = in.readInt();
        
        g.setString("file", origName);
        g.setInteger("configurationIndex", configIndex);
        
        if (!g.containsAttribute(GraphicAttributeConstants.GRAPHICS)) {
            g.addAttribute(new GraphGraphicAttribute(), "");
        }
        
        Node[] nodes = new Node[nodeCount];
        int[] levels = new int[nodeCount];
        
        for (int i = 0; i < nodeCount; i++) {
            Node n = g.addNode();
            n.addAttribute(new NodeGraphicAttribute(), "");
            nodes[i] = n;
            
            int phi = in.readInt();
            int pi = in.readInt();

            levels[i] = phi;
            AttributeUtil.setPosition(n, new Point2D.Double(50 * pi, 50 * phi));
            n.setInteger(SugiyamaConstants.PATH_LEVEL, phi);
        }
        
        for (int i = 0; i < edgeCount; i++) {
            int sourceIndex = in.readInt();
            int targetIndex = in.readInt();
            int pi = in.readInt();
            
            Edge edge = g.addEdge(nodes[sourceIndex], nodes[targetIndex], true);
            EdgeGraphicAttribute egf = new EdgeGraphicAttribute();
            edge.addAttribute(egf, "");
            
            int phiNorth = levels[sourceIndex];
            int phiSouth = levels[targetIndex];
            
            if (phiSouth >= phiNorth + 2) {
                BendsAdapter bends = new BendsAdapter(edge);
                bends.add(new Point2D.Double(50 * pi, 50 * (phiNorth + 1)));
                
                if (phiSouth > phiNorth + 2) {
                    bends.add(new Point2D.Double(50 * pi, 50 * (phiSouth - 1)));
                }
                bends.commit();
            }
            
            egf.setShape(PolyLineEdgeShape.class.getName());
        }
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
