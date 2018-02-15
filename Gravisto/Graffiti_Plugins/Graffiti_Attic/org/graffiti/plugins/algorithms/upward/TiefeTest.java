/**
 * this class test, weather the graph have long edges or quer edges.
 * 
 * @author jin
 */
package org.graffiti.plugins.algorithms.upward;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

public class TiefeTest {
    /**
     * the graph
     */
    private Graph graph;

    /**
     * constructor
     */
    public TiefeTest(Graph graph) {
        this.graph = graph;
    }

    /**
     * test, weather the graph have long edges or quer edges.
     */
    public boolean isSimpleUpwardPlanar() {

        Node root = null;
        Iterator<Node> nodes = this.graph.getNodesIterator();

        Calendar ca = Calendar.getInstance();
        long time1 = ca.getTimeInMillis();

        while (nodes.hasNext()) {
            Node node = nodes.next();
            node.setInteger("tief", 0);
            node.setBoolean("visited", false);
        }

        ca = Calendar.getInstance();
        long time2 = ca.getTimeInMillis();

        nodes = this.graph.getNodesIterator();

        while (nodes.hasNext()) {
            Node node = nodes.next();
            if (node.getAllOutEdges().isEmpty()) {
                root = node;
                break;
            }
        }

        ca = Calendar.getInstance();
        long time3 = ca.getTimeInMillis();

        LinkedList<Node> queue = new LinkedList<Node>();
        root.setBoolean("visited", true);
        queue.add(root);

        while (!queue.isEmpty()) {
            Node target = queue.remove();
            Iterator<Node> inNodes = target.getInNeighborsIterator();
            while (inNodes.hasNext()) {
                Node source = inNodes.next();
                if (!source.getBoolean("visited")) {
                    source.setInteger("tief", 1 + target.getInteger("tief"));
                    source.setBoolean("visited", true);
                    queue.add(source);
                } else if (source.getInteger("tief") != (1 + target
                        .getInteger("tief")))
                    return false;
            }
        }

        return true;
    }
}
