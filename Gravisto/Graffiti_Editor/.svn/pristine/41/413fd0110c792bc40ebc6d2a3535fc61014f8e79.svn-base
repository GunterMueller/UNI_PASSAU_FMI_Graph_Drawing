// =============================================================================
//
//   DMFReader.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.importers.dmf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.io.AbstractInputSerializer;

public class DMFReader extends AbstractInputSerializer {

    /** The supported extension. */
    private String[] extensions = { ".dmf" };

    public String[] getExtensions() {
        return this.extensions;
    }

    private void setLabel(Node n, String val) {
        LabelAttribute labelAttr;
        try {
            labelAttr = (LabelAttribute) n.getAttribute("label");
            labelAttr.setLabel(val);
        } catch (AttributeNotFoundException e) {
            labelAttr = new NodeLabelAttribute("label");
            labelAttr.setLabel(val);
            n.addAttribute(labelAttr, "");
        }
    }

    private void setLabel(Edge e, String val) {
        LabelAttribute labelAttr;
        try {
            labelAttr = (LabelAttribute) e.getAttribute("label");
            labelAttr.setLabel(val);
        } catch (AttributeNotFoundException ex) {
            labelAttr = new EdgeLabelAttribute("label");
            labelAttr.setLabel(val);
            e.addAttribute(labelAttr, "");
        }
    }

    @Override
    public void read(InputStream in, Graph g) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        for (int i = 0; i < 10; i++) {
            br.readLine();
        }
        ArrayList<Node> nodes = new ArrayList<Node>();
        String text;
        int node1 = 0;
        while ((text = br.readLine()) != null) {
            if (text.equals(""))
                return;
            Node node = g.addNode();
            nodes.add(node);
            StringTokenizer st = new StringTokenizer(text, "\t;");
            String name = st.nextToken();
            System.out.println(name);
            setLabel(node, name);
            node.addAttribute(new NodeGraphicAttribute(), "");
            // CoordinateAttribute ca = (CoordinateAttribute)
            // node.getAttribute("graphics.coordinate");
            // ca.setX(Math.random()*300);
            // ca.setY(Math.random()*300);
            for (int node2 = 0; node2 < node1; node2++) {
                String dist = st.nextToken();
                if (Double.parseDouble(dist) != 0.0) {
                    Edge edge = g.addEdge(nodes.get(node1), nodes.get(node2),
                            false);
                    setLabel(edge, dist);
                    System.out.println(dist);
                }

            }
            node1++;
        }
    }

    public String getName() {
        return "DMF Importer";
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
