/**
 * 
 */
package org.graffiti.plugins.algorithms.graviso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;

/**
 * This class implements the refinement part of the isomorphism algorithm for
 * the version taking edge labels into account
 * 
 * @author lenhardt
 * @version $Revision: 1002 $
 */
public class IsoGraphLabelsEqual extends IsoGraphSimple {

    // it's possible that the graphs are NOT isomorphic, even if
    // each node is assigned one color, and there is a
    // corresponding color in the other graph, because if
    // let c: v.g1 -> v.g2 be the matching function
    // for isomorphism, it has to hold that
    // if label.v1 = label.v2 => label(c(v1)) = label(c(v2))
    // as an example, consider 2 triangles, the nodes labeled
    // with the degree opened by its 2 edges
    // clearly, 2 triangles with angles 30�-120�-30� and
    // 30�-30�-120� resp. are isomorphic,
    // whereas 2 triangles with angles 60�-60�-60� and
    // 30�-30�-120� resp. are not isomorphic
    // so, we look at the colors of all nodes carrying the same
    // label

    /**
     * Wrapper class to distinguish nodes by their degree vectors and labels
     */
    protected class SortableNodeLabelsEqual implements SortableNode {
        private int nodeNumber;

        public SortableNodeLabelsEqual(int number) {
            nodeNumber = number;
        }

        @Override
        public SortableNodeLabelsEqual clone() {
            return this.clone();
        }

        public int compareTo(SortableNode o) {
            if (o instanceof SortableNodeLabelsEqual) {
                SortableNodeLabelsEqual newO = (SortableNodeLabelsEqual) o;
                return compareTo(newO);
            } else
                throw new ClassCastException();
        }

        /**
         * For labels, the nodes are compared by their degree vector (like
         * simple) and additionally by their labels.
         * 
         * @param o
         */
        public int compareTo(SortableNodeLabelsEqual o) {
            int res = 0;
            for (int i = 0; i < getDegreeVector().length; i++) {
                if (getDegreeVector()[i] < o.getDegreeVector()[i]) {
                    res = -1;
                    break;
                } else if (getDegreeVector()[i] > o.getDegreeVector()[i]) {
                    res = 1;
                    break;
                }
            }

            if (res != 0)
                return res;
            else {
                if (regardNodeLabels) {
                    res = getNodeLabel() - o.getNodeLabel();
                    if (res != 0)
                        return res;
                }
                if (regardEdgeLabels) {
                    // At this point, we know that the degree vectors of the
                    // nodes are the same, meaning they have the same number of
                    // nodes per color adjacent; therefore, as further
                    // tie-breaking criteria, we have to compare the "values" of
                    // the edges, i.e. the attributes; we do this ordered by
                    // color, because this keeps the process independent of
                    // nodenumbers

                    // compare the edge labels, which connect this node to
                    // the nodes with color color
                    // within the color, compare them by their values
                    ArrayList<LinkedList<Integer>> degreeLabelVector1 = computeDegreeLabelVector();
                    ArrayList<LinkedList<Integer>> degreeLabelVector2 = o
                            .computeDegreeLabelVector();
                    for (int color = 0; color < numberOfColors; color++) {
                        LinkedList<Integer> atts1 = degreeLabelVector1
                                .get(color);
                        LinkedList<Integer> atts2 = degreeLabelVector2
                                .get(color);
                        if (atts1.size() != atts2.size()) {
                            // throw new GravIsoException("Program Error: The
                            // number of neighbours in one color differs
                            // although that case should have been caught
                            // earlier");

                        }
                        for (int index = 0; index < atts1.size(); index++) {
                            if (atts1.get(index) > atts2.get(index))
                                return -1;
                            else if (atts1.get(index) < atts2.get(index))
                                return 1;
                        }
                        color++;
                    }
                    return 0;
                }
                return 0;
            }
        }

        /**
         * like a degree vector, this method returns a linked list of edge
         * labels from the node to the other nodes with a certain color at
         * position i
         * 
         * @return the degree label vector
         */
        public ArrayList<LinkedList<Integer>> computeDegreeLabelVector() {
            ArrayList<LinkedList<Integer>> vecs = new ArrayList<LinkedList<Integer>>(
                    numberOfColors);
            vecs.ensureCapacity(numberOfColors);
            for (int i = 0; i < numberOfColors; i++) {
                vecs.add(i, new LinkedList<Integer>());
            }

            // vecs[i] contains list of attributes of edges leading to color i
            // from this node
            for (int targetNode = 0; targetNode < nodeColors.length; targetNode++) {
                int color = nodeColors[targetNode];
                vecs.get(color).add(edgeLabels[getNodeNumber()][targetNode]);
            }
            for (LinkedList<Integer> atts : vecs) {
                Collections.sort(atts);
            }
            return vecs;
        }

        public boolean equals(SortableNode o) {
            if (o instanceof SortableNodeLabelsEqual) {
                SortableNodeLabelsEqual newO = (SortableNodeLabelsEqual) o;
                return equals(newO);
            } else
                throw new ClassCastException();
        }

        public boolean equals(SortableNodeLabelsEqual o) {
            if (!super.equals(o))
                return false;
            else {
                if (regardNodeLabels) {
                    if (getNodeLabel() != o.getNodeLabel())
                        return false;
                }

                if (regardEdgeLabels) {
                    ArrayList<LinkedList<Integer>> degreeLabelVector1 = computeDegreeLabelVector();
                    ArrayList<LinkedList<Integer>> degreeLabelVector2 = o
                            .computeDegreeLabelVector();
                    for (int color = 0; color < numberOfColors; color++) {
                        LinkedList<Integer> atts1 = degreeLabelVector1
                                .get(color);
                        LinkedList<Integer> atts2 = degreeLabelVector2
                                .get(color);
                        if (atts1.size() != atts2.size())
                            return false;
                        for (int index = 0; index < atts1.size(); index++) {
                            if (atts1.get(index) != atts2.get(index))
                                return false;
                        }
                    }
                }
            }
            return true;
        }

        public int[] getDegreeVector() {
            return degreeVectors[nodeNumber];
        }

        public int getNodeLabel() {
            return nodeLabels[nodeNumber];
        }

        public int getNodeNumber() {
            return nodeNumber;
        }
    }

    // take node labels into account
    private boolean regardNodeLabels;
    // take edge labels into account
    private boolean regardEdgeLabels;

    // array containing the node labels
    private int[] nodeLabels;

    // array continaing the edge labels
    private int[][] edgeLabels;

    /**
     * In the constructor, the parameters are translated from Gravisto's data
     * structures to faster matrix representations. Further more, the initial
     * degree vectors are computed.
     * 
     * 
     * @param g
     * @param directed
     *            true if edge direction should be regarded
     * @param regardEdgeLabels
     *            true if edge labels should be regarded
     * @param edgeLabelPath
     *            path, where the label resides
     * @param regardNodeLabels
     *            true if node labels should be regarded
     * @param nodeLabelPath
     *            true if node labels should be regarded
     * @throws GravIsoException
     */
    public IsoGraphLabelsEqual(Graph g, boolean directed,
            boolean regardEdgeLabels, String edgeLabelPath,
            boolean regardNodeLabels, String nodeLabelPath)
            throws GravIsoException {
        super(g, directed);
        this.regardNodeLabels = regardNodeLabels;
        this.regardEdgeLabels = regardEdgeLabels;
        if (regardEdgeLabels) {
            edgeLabels = new int[g.getNumberOfNodes()][g.getNumberOfNodes()];
            for (Edge e : g.getEdges()) {
                Attribute a = e.getAttribute(edgeLabelPath);
                Object val;
                if (a instanceof EdgeLabelAttribute) {
                    val = ((EdgeLabelAttribute) a).getLabel();
                } else {
                    val = a.getValue();
                }

                edgeLabels[e.getSource().getInteger("node number")][e
                        .getTarget().getInteger("node number")] = val
                        .hashCode();
            }
        }
        if (regardNodeLabels) {
            nodeLabels = new int[g.getNumberOfNodes()];
            for (Node n : g.getNodes()) {

                Attribute a = n.getAttribute(nodeLabelPath);
                Object val;
                if (a instanceof NodeLabelAttribute) {
                    val = ((NodeLabelAttribute) a).getLabel();
                } else {
                    val = a.getValue();
                }
                nodeLabels[n.getInteger("node number")] = val.hashCode();
            }
        }
    }

    @Override
    public IsoGraphLabelsEqual clone() throws CloneNotSupportedException {
        IsoGraphLabelsEqual c = (IsoGraphLabelsEqual) super.clone();
        c.nodeLabels = this.nodeLabels;
        c.edgeLabels = this.edgeLabels;
        return c;
    }

    @Override
    protected void computeColorClasses() {
        colorClasses = new ArrayList<LinkedList<SortableNode>>((numberOfColors));
        colorClasses.ensureCapacity(numberOfColors);
        for (int i = 0; i < numberOfColors; i++) {
            colorClasses.add(i, new LinkedList<SortableNode>());
        }
        for (int nodeNum = 0; nodeNum < nodeColors.length; nodeNum++) {
            int color = nodeColors[nodeNum];
            colorClasses.get(color).add(new SortableNodeLabelsEqual(nodeNum));
        }
        for (LinkedList<SortableNode> nodes : colorClasses) {
            Collections.sort(nodes);
        }
    }

    /**
     * Computes an array, at position i containing a list of the edge labels of
     * the nodes of color i
     */
    public ArrayList<ArrayList<LinkedList<Integer>>> getEdgeLabelsByColor() {

        ArrayList<ArrayList<LinkedList<Integer>>> l = new ArrayList<ArrayList<LinkedList<Integer>>>(
                numberOfColors);
        // "initialize"
        for (int i = 0; i < numberOfColors; i++) {
            l.add(new ArrayList<LinkedList<Integer>>());
            for (int j = 0; j < numberOfColors; j++) {
                l.get(i).add(new LinkedList<Integer>());
            }
        }

        for (int i = 0; i < nodeColors.length; i++) {
            for (int j = 0; j < nodeColors.length; j++) {
                l.get(nodeColors[i]).get(nodeColors[j]).add(edgeLabels[i][j]);
            }
        }

        for (int i = 0; i < numberOfColors; i++) {
            for (int j = 0; j < numberOfColors; j++) {
                Collections.sort(l.get(i).get(j));
            }
        }

        return l;
    }

    /**
     * returns an array, it position i containing a list of the node labels of
     * nodes with color i
     */
    public ArrayList<LinkedList<Integer>> getNodeLabelsByColor() {

        ArrayList<LinkedList<Integer>> l = new ArrayList<LinkedList<Integer>>(
                numberOfColors);
        for (int i = 0; i < numberOfColors; i++) {
            l.add(new LinkedList<Integer>());
        }

        for (int i = 0; i < nodeColors.length; i++) {
            l.get(nodeColors[i]).add(nodeLabels[i]);
        }

        // we have to sort them so that comparing them to the labels of the
        // other graph is easier
        for (int i = 0; i < numberOfColors; i++) {
            Collections.sort(l.get(i));
        }

        return l;
    }

    /**
     * In addition to comparing the color class size vectors and the color class
     * adjacency matrix, this method does the following:
     * 
     * It's possible that the graphs are NOT isomorphic, even if each node is
     * assigned one color, and there is a corresponding color in the other
     * graph, because if let c: v.g1 -> v.g2 be the matching function for
     * isomorphism, it has to hold that if label.v1 = label.v2 => label(c(v1)) =
     * label(c(v2)) as an example, consider 2 triangles, the nodes labeled with
     * the degree opened by its 2 edges clearly, 2 triangles with angles
     * 30�-120�-30� and 30�-30�-120� resp. are isomorphic, whereas 2 triangles
     * with angles 60�-60�-60� and 30�-30�-120� resp. are not isomorphic so, we
     * look at the colors of all nodes carrying the same label
     * 
     * @param o
     *            another colored graph with labels
     * @throws GravIsoException
     */
    protected int isIsomorphicTo(IsoGraphLabelsEqual o) throws GravIsoException {

        int res = super.isIsomorphicTo(o);
        if (res == R_NOT_ISO)
            // when they're not isomorphic without regarding the labels, then
            // they surely aren't with the labels either
            return R_NOT_ISO;
        else {

            if (regardNodeLabels) {
                // until now, we know that there is only 1 color per node in
                // each graph, the degrees and neighbourhoods are the same; the
                // only thing left to show is that v1.label = c(v1).label
                ArrayList<LinkedList<Integer>> l1 = getNodeLabelsByColor();
                ArrayList<LinkedList<Integer>> l2 = o.getNodeLabelsByColor();
                if (res == R_ISO && l1.size() != l2.size())
                    // this means the number of colors is different in g1 and
                    // g2; however, this has been checked in
                    // super.isIsomorphicTo => therefore, we have a programming
                    // error here
                    throw new GravIsoException("Nodes: l1.size: " + l1.size()
                            + ", l1.size: " + l2.size());
                // now we compare labels
                for (int i = 0; i < l1.size(); i++) {
                    // color by color
                    LinkedList<Integer> c1 = l1.get(i);
                    LinkedList<Integer> c2 = l2.get(i);
                    if ((res == R_ISO)
                            && (c1.size() != c2.size() || c1.size() != 1 || c2
                                    .size() != 1))
                        throw new GravIsoException("Nodes: c1.size: "
                                + c1.size() + ", c2.size: " + c2.size());
                    if (!c1.equals(c2))
                        return R_NOT_ISO;
                }
            }

            if (regardEdgeLabels && res == R_ISO) {
                // like the nodes, we have to verify that e(v1, v2).label =
                // e(c(v1), c(v2)).label (for the case that super.isIso said
                // ISO), or at least that it's possible (if super said MAYBE)

                ArrayList<ArrayList<LinkedList<Integer>>> l1 = getEdgeLabelsByColor();
                ArrayList<ArrayList<LinkedList<Integer>>> l2 = getEdgeLabelsByColor();

                if (res == R_ISO && l1.size() != l2.size())
                    throw new GravIsoException("Edges: l1.size: " + l1.size()
                            + ", l2.size: " + l2.size());

                for (int i = 0; i < l1.size(); i++) {
                    for (int j = 0; j < l1.size(); j++) {
                        LinkedList<Integer> c1 = l1.get(i).get(j);
                        LinkedList<Integer> c2 = l2.get(i).get(j);
                        if ((res == R_ISO)
                                && (c1.size() != c2.size() || c1.size() != 1 || c2
                                        .size() != 1))
                            throw new GravIsoException("Edges: c1.size: "
                                    + c1.size() + ", c2.size: " + c2.size());
                        if (!c1.equals(c2))
                            return R_NOT_ISO;
                    }

                }
            }

            // tests on labels also passed => really ISO or MAYBE, according to
            // super.isIso:
            return res;
        }
    }

    @Override
    protected void recolour() {
        for (LinkedList<SortableNode> nodes : colorClasses) {
            // analyze nodes: all nodes with the same degree vector
            // receive the same color
            SortableNode node = nodes.getFirst();

            for (SortableNode nextNode : nodes) {
                if (!((SortableNodeLabelsEqual) node)
                        .equals((SortableNodeLabelsEqual) nextNode)) {
                    // we have a new color
                    // set this and all following nodes in the list to the
                    // new color. this works, because the node list is sorted
                    // according to the nodes' degree vectors
                    int startIndex = nodes.indexOf(nextNode);
                    int endIndex = nodes.size();
                    for (SortableNode cNode : nodes.subList(startIndex,
                            endIndex)) {
                        nodeColors[cNode.getNodeNumber()] = numberOfColors;
                    }
                    numberOfColors++;
                }
                node = nextNode;
            }

        }
    }
}
