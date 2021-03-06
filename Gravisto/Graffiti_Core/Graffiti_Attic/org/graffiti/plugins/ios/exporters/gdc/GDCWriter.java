package org.graffiti.plugins.ios.exporters.gdc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.io.OutputSerializer;

/**
 * Provides a GDC writer.
 * 
 * @version $Revision: 5772 $
 */
public class GDCWriter implements OutputSerializer {

    /**
     * Constructs a new GDC writer.
     */
    public GDCWriter() {
    }

    /**
     * @see org.graffiti.plugin.io.Serializer#getExtensions()
     */
    public String[] getExtensions() {
        return new String[] { ".gdc" };
    }

    /**
     * @see org.graffiti.plugin.io.OutputSerializer#write(OutputStream, Graph)
     */
    public void write(OutputStream o, Graph g) throws IOException {
        PrintStream p = new PrintStream(o);

        p.println("# begin graph");

        // number of nodes
        p.println(g.getNumberOfNodes());

        // nodes
        Map<Node, Integer> nidMap = new HashMap<Node, Integer>(g
                .getNumberOfNodes());
        Iterator<Node> nit = g.getNodesIterator();
        int id = 0;
        while (nit.hasNext()) {
            Node node = nit.next();
            nidMap.put(node, id);
            id++;
            CoordinateAttribute coords = (CoordinateAttribute) node
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            p.print(coords.getX());
            p.print(" ");
            p.println(coords.getY());
        }

        // edges
        Iterator<Edge> eit = g.getEdgesIterator();
        while (eit.hasNext()) {
            Edge edge = eit.next();
            Node source = edge.getSource();
            Node target = edge.getTarget();
            int sid = nidMap.get(source);
            int tid = nidMap.get(target);
            p.print(sid);
            p.print(" ");
            p.println(tid);
        }

        p.println("# end graph");
        p.close();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
