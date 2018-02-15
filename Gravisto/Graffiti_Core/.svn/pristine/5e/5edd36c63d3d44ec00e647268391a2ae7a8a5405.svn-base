package org.graffiti.plugins.algorithms.planarity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.graffiti.graph.Graph;

/**
 * Stores a biconnected component. The code to find a Kuratowsk subgraph is also
 * in here.
 * 
 * @author Wolfgang Brunner
 */
public class Bicomp {

    /**
     * The list of nodes in the biconnected component
     */
    public List<ArbitraryNode> nodes;

    /**
     * The boundary of the embedding
     */
    public List<ArbitraryNode> boundary;

    /**
     * The <code>ConnectedComponent</code> the <code>Bicomp</code> is in
     */
    private ConnectedComponent comp;

    /**
     * The <code>org.graffiti.graph.Graph</code> the <code>Bicomp</code> is in
     */
    private Graph graph;

    /**
     * The number of nodes in the biconnected component
     */
    public int numberOfNodes;

    /**
     * The number of edges in the biconnected component
     */
    public int numberOfEdges;

    /**
     * The number of edges already embedded in the biconnected component
     */
    public int numberOfEmbeddedEdges;

    /**
     * The Kuratowski subgraph (if the biconnected component is not planar)
     */
    public KuratowskiSubgraph kuratowskiSubgraph;

    /**
     * Constructs a new <code>Bicomp</code>
     * 
     * @param comp
     *            The <code>ConnectedComponent</code> the <code>Bicomp</code> is
     *            in
     * @param graph
     *            The <code>org.graffiti.graph.Graph</code> the
     *            <code>Bicomp</code> is in
     */
    public Bicomp(ConnectedComponent comp, Graph graph) {
        nodes = new LinkedList<ArbitraryNode>();
        boundary = new LinkedList<ArbitraryNode>();
        this.comp = comp;
        this.graph = graph;
        numberOfNodes = 0;
        numberOfEdges = 0;
        numberOfEmbeddedEdges = 0;
        kuratowskiSubgraph = null;
    }

    /**
     * Gives the result of the planarity test
     * 
     * @return <code>true</code> if the biconnected connected component is
     *         planar
     */
    public boolean isPlanar() {
        return (numberOfEdges == numberOfEmbeddedEdges);
    }

    /**
     * Add a <code>ArbitraryNode</code> to the biconnected component
     * 
     * @param node
     *            The node to add
     */
    public void addNode(ArbitraryNode node) {
        nodes.add(node);
        numberOfNodes++;
    }

    /**
     * Stores the boundary of the embedding in a list
     */
    public void buildBoundary() {
        if (numberOfNodes == 1) {
            boundary.add(nodes.get(0));
            return;
        }
        ArbitraryNode first = nodes.get(0);
        ArbitraryNode current = first;
        do {
            boundary.add(current);
            current = current.adjacencyList.getFirst();
        } while (current != first);
    }

    /**
     * Searches a pertinent path from the given node
     * 
     * @param node
     *            The node the path starts from
     * @param current
     *            The node in processing
     * 
     * @return The found pertinent path
     */
    private LinkedList<ArbitraryNode> findPertinentPath(ArbitraryNode node,
            ArbitraryNode current) {
        LinkedList<ArbitraryNode> part1 = new LinkedList<ArbitraryNode>();
        if (node.getRealNode().backedgeTarget == current.getRealNode()) {
            part1.addLast(node.getRealNode());
            part1.addLast(current.getRealNode());
            return part1;
        } else {
            ArbitraryNode aNode = node.getRealNode().pertinentRoots.getFirst();
            Position curr = comp.getSuccessorOnExternalFace(new Position(aNode,
                    0));
            part1.addLast(aNode.getRealNode());
            while (!comp.pertinent(curr.pos.getRealNode(), current
                    .getRealNode())) {
                part1.addLast(curr.pos.getRealNode());
                curr = comp.getSuccessorOnExternalFace(curr);
            }
            LinkedList<ArbitraryNode> part2 = findPertinentPath(curr.pos,
                    current);
            part1.addAll(part2);
            return part1;
        }
    }

    /**
     * Searches a externally active path from the given node
     * 
     * @param node
     *            The node the path starts from
     * @param current
     *            The node in processing
     * 
     * @return The found externally active path
     */
    private LinkedList<ArbitraryNode> findExternalPath(ArbitraryNode node,
            RealNode current) {
        LinkedList<ArbitraryNode> part1 = new LinkedList<ArbitraryNode>();
        if (node.getRealNode().leastAncestor < current.DFI) {
            for (Iterator<RealNode> i = node.getRealNode().completeAdjacencyList
                    .iterator(); i.hasNext();) {
                ArbitraryNode aNode = i.next();
                if (aNode.getRealNode().DFI == node.getRealNode().leastAncestor) {
                    part1.addLast(node.getRealNode());
                    part1.addLast(aNode.getRealNode());
                    return part1;
                }
            }
            return null;
        }
        ArbitraryNode next = node.getRealNode().separatedDFSChildList
                .getFirst().virtualParent;
        Position curr = comp.getSuccessorOnExternalFace(new Position(next, 0));
        part1.addLast(next.getRealNode());
        while (!comp.externallyActive(curr.pos.getRealNode(), current)) {
            part1.addLast(curr.pos.getRealNode());
            curr = comp.getSuccessorOnExternalFace(curr);
        }
        LinkedList<ArbitraryNode> part2 = findExternalPath(curr.pos, current);
        part1.addAll(part2);
        return part1;
    }

    /**
     * Gives the path in the depth first search tree between the two nodes. The
     * first node has to be a successor of the second.
     * 
     * @param from
     *            The lower node
     * @param to
     *            The upper node
     * 
     * @return The path between the two nodes
     */
    private LinkedList<ArbitraryNode> findDFSTree(ArbitraryNode from,
            ArbitraryNode to) {
        LinkedList<ArbitraryNode> result = new LinkedList<ArbitraryNode>();
        RealNode current = from.getRealNode().DFSParent;
        result.add(from.getRealNode());
        result.add(current);
        while (current != to.getRealNode()) {
            current = current.DFSParent;
            result.add(current);
        }
        return result;
    }

    /**
     * Concats the two given paths
     * 
     * @param path1
     *            The first path
     * @param path2
     *            The second path
     * 
     * @return The concatenation of the two paths
     */
    private LinkedList<ArbitraryNode> concat(LinkedList<ArbitraryNode> path1,
            LinkedList<ArbitraryNode> path2) {
        LinkedList<ArbitraryNode> result = new LinkedList<ArbitraryNode>(path1);
        if ((path1.size() != 0) && (path2.size() != 0)) {
            path2.removeFirst();
        }
        result.addAll(path2);
        return result;
    }

    /**
     * Finds the tree paths connecting the ends of the three paths in the depth
     * first search tree
     * 
     * @param path1
     *            The first path
     * @param path2
     *            The second path
     * @param path3
     *            The third path
     * 
     * @return A list with three elements. Each element is a list representing a
     *         extended path to the node in the depth first search tree
     *         connecting the three paths
     */
    private LinkedList<LinkedList<ArbitraryNode>> connect(
            LinkedList<ArbitraryNode> path1, LinkedList<ArbitraryNode> path2,
            LinkedList<ArbitraryNode> path3) {

        RealNode rNode1 = path1.getLast().getRealNode();
        RealNode rNode2 = path2.getLast().getRealNode();
        RealNode rNode3 = path3.getLast().getRealNode();
        RealNode maxDFI = rNode1;
        RealNode midDFI = rNode2;
        RealNode minDFI = rNode3;
        RealNode swap = null;
        if (minDFI.DFI > midDFI.DFI) {
            swap = minDFI;
            minDFI = midDFI;
            midDFI = swap;
        }
        if (minDFI.DFI > maxDFI.DFI) {
            swap = minDFI;
            minDFI = maxDFI;
            maxDFI = swap;
        }
        if (midDFI.DFI > maxDFI.DFI) {
            swap = midDFI;
            midDFI = maxDFI;
            maxDFI = swap;
        }
        LinkedList<ArbitraryNode> minMidPath = new LinkedList<ArbitraryNode>();
        LinkedList<ArbitraryNode> maxMidPath = new LinkedList<ArbitraryNode>();
        if (minDFI != midDFI) {
            LinkedList<ArbitraryNode> midMinPath = findDFSTree(midDFI, minDFI);
            for (Iterator<ArbitraryNode> j = midMinPath.iterator(); j.hasNext();) {
                minMidPath.addFirst(j.next());
            }
        }
        if (midDFI != maxDFI) {
            maxMidPath = findDFSTree(maxDFI, midDFI);
        }

        LinkedList<ArbitraryNode> newPath1 = new LinkedList<ArbitraryNode>();
        LinkedList<ArbitraryNode> newPath2 = new LinkedList<ArbitraryNode>();
        LinkedList<ArbitraryNode> newPath3 = new LinkedList<ArbitraryNode>();
        LinkedList<LinkedList<ArbitraryNode>> result = new LinkedList<LinkedList<ArbitraryNode>>();
        if (rNode1 == maxDFI) {
            newPath1 = concat(path1, maxMidPath);
        } else if (rNode1 == minDFI) {
            newPath1 = concat(path1, minMidPath);
        } else {
            newPath1 = path1;
        }
        if (rNode2 == maxDFI) {
            newPath2 = concat(path2, maxMidPath);
        } else if (rNode2 == minDFI) {
            newPath2 = concat(path2, minMidPath);
        } else {
            newPath2 = path2;
        }
        if (rNode3 == maxDFI) {
            newPath3 = concat(path3, maxMidPath);
        } else if (rNode3 == minDFI) {
            newPath3 = concat(path3, minMidPath);
        } else {
            newPath3 = path3;
        }

        result.add(newPath1);
        result.add(newPath2);
        result.add(newPath3);

        return result;
    }

    /**
     * Gives the counter clock wise boundary of a face
     * 
     * @param start
     *            The node to start
     * @param next
     *            The second node on the boundary
     * 
     * @return The boundary of the face
     */
    private LinkedList<ArbitraryNode> findInternalCounterClockWiseCircle(
            ArbitraryNode start, ArbitraryNode next) {
        LinkedList<ArbitraryNode> result = new LinkedList<ArbitraryNode>();
        result.add(start);
        ArbitraryNode last = start;
        ArbitraryNode current = next;
        result.add(current);
        while (current != start) {
            for (Iterator<ArbitraryNode> i = current.adjacencyList.iterator(); i
                    .hasNext();) {
                ArbitraryNode node = i.next();
                if (node == last) {
                    if (i.hasNext()) {
                        last = current;
                        current = i.next();
                        result.add(current);
                        break;
                    } else {
                        last = current;
                        current = current.adjacencyList.get(0);
                        result.add(current);
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Searches a path on the external face to a stopping vertex
     * 
     * @param node
     *            The node to start from
     * @param cNode
     *            The node in processing
     * @param clockWise
     *            The direction of the search
     * 
     * @return The path to the stopping vertex
     */
    private LinkedList<ArbitraryNode> findPathToStoppingVertex(
            ArbitraryNode node, RealNode cNode, boolean clockWise) {
        LinkedList<ArbitraryNode> result = new LinkedList<ArbitraryNode>();
        ArbitraryNode current = null;
        result.add(node);
        if (clockWise) {
            current = node.adjacencyList.getFirst();
        } else {
            current = node.adjacencyList.getLast();
        }
        result.add(current);
        while (!comp.externallyActive(current.getRealNode(), cNode)) {
            if (clockWise) {
                current = current.adjacencyList.getFirst();
            } else {
                current = current.adjacencyList.getLast();
            }
            result.add(current);
        }
        return result;
    }

    /**
     * Searches a path on the external face to a pertinent vertex
     * 
     * @param node
     *            The node to start from
     * @param cNode
     *            The node in processing
     * @param clockWise
     *            The direction of the search
     * 
     * @return The path to the stopping vertex
     */
    private LinkedList<ArbitraryNode> findPathToPertinentVertex(
            ArbitraryNode node, RealNode cNode, boolean clockWise) {
        LinkedList<ArbitraryNode> result = new LinkedList<ArbitraryNode>();
        ArbitraryNode current = null;
        result.add(node);
        if (clockWise) {
            current = node.adjacencyList.getFirst();
        } else {
            current = node.adjacencyList.getLast();
        }
        result.add(current);
        while (!comp.pertinent(current.getRealNode(), cNode)) {
            if (clockWise) {
                current = current.adjacencyList.getFirst();
            } else {
                current = current.adjacencyList.getLast();
            }
            result.add(current);
        }
        return result;
    }

    /**
     * Searches the clock wise path on the external face between the two nodes
     * 
     * @param node1
     *            The first node
     * @param node2
     *            The second node
     * 
     * @return The path between the two nodes
     */
    private LinkedList<ArbitraryNode> findPathOnBoundary(ArbitraryNode node1,
            ArbitraryNode node2) {
        LinkedList<ArbitraryNode> result = new LinkedList<ArbitraryNode>();
        result.add(node1);
        if (node1 == node2)
            return result;
        ArbitraryNode current = node1.adjacencyList.getFirst();
        result.add(current);
        while (current != node2) {
            current = current.adjacencyList.getFirst();
            result.add(current);
        }
        return result;
    }

    /**
     * Marks a path on the external face between two nodes with the given value
     * 
     * @param node1
     *            The first node
     * @param node2
     *            The second node
     * @param border
     *            The value to set
     */
    private void markPathToVertex(ArbitraryNode node1, ArbitraryNode node2,
            int border) {
        ArbitraryNode current = node1.adjacencyList.getFirst();
        while (current != node2) {
            current.quadrant = border;
            current = current.adjacencyList.getFirst();
        }
    }

    /**
     * Removes nodes appearing more than once and all nodes between
     * 
     * @param path
     *            The path to remove the nodes from
     * 
     * @return The simplyfied path
     */
    private LinkedList<ArbitraryNode> removeDoubles(
            LinkedList<ArbitraryNode> path) {
        LinkedList<ArbitraryNode> newPath = new LinkedList<ArbitraryNode>();
        for (Iterator<ArbitraryNode> i = path.iterator(); i.hasNext();) {
            ArbitraryNode node = i.next();
            node.amountOnPath = 0;
        }
        for (Iterator<ArbitraryNode> i = path.iterator(); i.hasNext();) {
            ArbitraryNode node = i.next();
            node.amountOnPath++;
        }
        boolean toDelete = false;
        ArbitraryNode delNode = null;
        for (Iterator<ArbitraryNode> i = path.iterator(); i.hasNext();) {
            ArbitraryNode node = i.next();
            node.amountOnPath--;
            if ((node == delNode) && (node.amountOnPath == 0)) {
                toDelete = false;
                delNode = null;
            }
            if ((node.amountOnPath > 0) && (!toDelete)) {
                toDelete = true;
                delNode = node;
            }
            if (!toDelete) {
                newPath.add(node);
            }
        }
        return newPath;
    }

    /**
     * Returns the last node of the path which is on the left boundary of the
     * external face
     * 
     * @param path
     *            The path to search through
     * 
     * @return The last node on the boundary
     */
    private ArbitraryNode findLastOnLeftBoundary(LinkedList<ArbitraryNode> path) {
        ArbitraryNode lastOnBoundary = null;
        for (Iterator<ArbitraryNode> i = path.iterator(); i.hasNext();) {
            ArbitraryNode pNode = i.next();
            if ((pNode.quadrant == ArbitraryNode.TOP_LEFT)
                    || (pNode.quadrant == ArbitraryNode.BOTTOM_LEFT)
                    || (pNode.quadrant == ArbitraryNode.CENTER_LEFT)) {
                lastOnBoundary = pNode;
            }
        }
        return lastOnBoundary;
    }

    /**
     * Returns the first node on the path which is on the right boundary of the
     * external face
     * 
     * @param path
     *            The path to search through
     * 
     * @return The first node on the boundary
     */
    private ArbitraryNode findFirstOnRightBoundary(
            LinkedList<ArbitraryNode> path) {
        for (Iterator<ArbitraryNode> i = path.iterator(); i.hasNext();) {
            ArbitraryNode pNode = i.next();
            if ((pNode.quadrant == ArbitraryNode.TOP_RIGHT)
                    || (pNode.quadrant == ArbitraryNode.BOTTOM_RIGHT)
                    || (pNode.quadrant == ArbitraryNode.CENTER_RIGHT))
                return pNode;
        }
        return null;
    }

    /**
     * Returns the subpath between the two nodes
     * 
     * @param path
     *            The given path
     * @param first
     *            The first node of the subpath
     * @param last
     *            The last node of the subpath
     * 
     * @return The subpath
     */
    private LinkedList<ArbitraryNode> getSubpath(
            LinkedList<ArbitraryNode> path, ArbitraryNode first,
            ArbitraryNode last) {
        LinkedList<ArbitraryNode> result = new LinkedList<ArbitraryNode>();
        boolean inSubpath = false;
        for (Iterator<ArbitraryNode> i = path.iterator(); i.hasNext();) {
            ArbitraryNode node = i.next();
            if (node == first) {
                inSubpath = true;
            }
            if (inSubpath) {
                result.add(node);
            }
            if (node == last) {
                break;
            }
        }
        return result;
    }

    /**
     * Reverses the given path
     * 
     * @param path
     *            The path
     * 
     * @return The reversed path
     */
    private LinkedList<ArbitraryNode> reverse(LinkedList<ArbitraryNode> path) {
        LinkedList<ArbitraryNode> result = new LinkedList<ArbitraryNode>();
        for (Iterator<ArbitraryNode> i = path.iterator(); i.hasNext();) {
            result.addFirst(i.next());
        }
        return result;
    }

    /**
     * Indicates whether a Kuratowski subgraph has already been found
     * 
     * @return <code>true</code> if a Kuratowsk subgraph has been found
     */
    public boolean foundKuratowskiSubgraph() {
        return (kuratowskiSubgraph != null);
    }

    /**
     * Searches a Kuratowski subgraph
     * 
     * @param rNode
     *            The root of the biconnected component in which the Walk Down
     *            failed
     * @param vNode
     *            The node in processing
     */
    public void findKuratowskiSubgraph(VirtualNode rNode, VirtualNode vNode) {
        comp.buildAdjacencyLists(rNode, 1);
        LinkedList<ArbitraryNode> rvPath = new LinkedList<ArbitraryNode>();

        if (rNode != vNode) {
            rvPath = findDFSTree(rNode, vNode);
        }

        LinkedList<ArbitraryNode> rxPath = findPathToStoppingVertex(rNode,
                rNode.getRealNode(), false);
        LinkedList<ArbitraryNode> ryPath = findPathToStoppingVertex(rNode,
                rNode.getRealNode(), true);
        ArbitraryNode xNode = rxPath.getLast();
        ArbitraryNode yNode = ryPath.getLast();
        LinkedList<ArbitraryNode> ywPath = findPathToPertinentVertex(yNode,
                vNode.getRealNode(), true);
        ArbitraryNode wNode = ywPath.getLast();

        LinkedList<ArbitraryNode> wxPath = findPathOnBoundary(wNode, xNode);
        LinkedList<ArbitraryNode> xUxPath = findExternalPath(xNode, vNode
                .getRealNode());
        LinkedList<ArbitraryNode> yUyPath = findExternalPath(yNode, vNode
                .getRealNode());
        ArbitraryNode uxNode = xUxPath.getLast();
        ArbitraryNode uyNode = yUyPath.getLast();

        LinkedList<ArbitraryNode> vPath = new LinkedList<ArbitraryNode>();
        vPath.add(vNode);

        // case 1
        if (vNode != rNode) {
            LinkedList<ArbitraryNode> wvPath = findPertinentPath(wNode, vNode);
            LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(xUxPath,
                    yUyPath, vPath);
            LinkedList<ArbitraryNode> xcPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> ycPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> vcPath = cPaths.removeFirst();
            ArbitraryNode cNode = xcPath.getLast();

            kuratowskiSubgraph = new KuratowskiSubgraph(KuratowskiSubgraph.K33,
                    graph);

            kuratowskiSubgraph.addNode(vNode);
            kuratowskiSubgraph.addNode(xNode);
            kuratowskiSubgraph.addNode(yNode);
            kuratowskiSubgraph.addNode(rNode);
            kuratowskiSubgraph.addNode(wNode);
            kuratowskiSubgraph.addNode(cNode);

            kuratowskiSubgraph.addPath(rxPath);
            kuratowskiSubgraph.addPath(ryPath);
            kuratowskiSubgraph.addPath(rvPath);

            kuratowskiSubgraph.addPath(ywPath);
            kuratowskiSubgraph.addPath(wxPath);
            kuratowskiSubgraph.addPath(wvPath);

            kuratowskiSubgraph.addPath(xcPath);
            kuratowskiSubgraph.addPath(ycPath);
            kuratowskiSubgraph.addPath(vcPath);

            return;
        }

        LinkedList<ArbitraryNode> vxPath = rxPath;
        LinkedList<ArbitraryNode> vyPath = ryPath;

        // case 2.1
        if ((!wNode.getRealNode().pertinentRoots.isEmpty())
                && (!wNode.getRealNode().separatedDFSChildList.isEmpty())) {
            for (Iterator<RealNode> i = wNode.getRealNode().separatedDFSChildList
                    .getList().iterator(); i.hasNext();) {
                RealNode childNode = i.next();
                VirtualNode node = childNode.virtualParent;
                if ((childNode.lowPoint < vNode.getRealNode().DFI)
                        && (wNode.getRealNode().pertinentRoots.contains(node))) {
                    comp.buildAdjacencyLists(node, 1);
                    LinkedList<ArbitraryNode> zvPath = findPathToPertinentVertex(
                            node, vNode.getRealNode(), true);
                    LinkedList<ArbitraryNode> zUzPath = findPathToStoppingVertex(
                            node, vNode.getRealNode(), true);

                    zvPath
                            .addAll(findPertinentPath(zvPath.removeLast(),
                                    vNode));

                    zUzPath.addAll(findExternalPath(zUzPath.removeLast(), vNode
                            .getRealNode()));
                    zvPath.removeFirst();
                    zUzPath.removeFirst();
                    zvPath.addFirst(node.getRealNode());
                    zUzPath.addFirst(node.getRealNode());
                    RealNode zNode = wNode.getRealNode();
                    LinkedList<ArbitraryNode> wzPath = new LinkedList<ArbitraryNode>();

                    while (zvPath.getFirst() == zUzPath.getFirst()) {
                        RealNode currNode = (RealNode) zvPath.getFirst();
                        zNode = currNode;
                        wzPath.add(zvPath.getFirst());
                        zvPath.removeFirst();
                        zUzPath.removeFirst();
                    }
                    zvPath.addFirst(wzPath.getLast());
                    zUzPath.addFirst(wzPath.getLast());
                    LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(
                            xUxPath, yUyPath, zUzPath);
                    LinkedList<ArbitraryNode> xcPath = cPaths.removeFirst();
                    LinkedList<ArbitraryNode> ycPath = cPaths.removeFirst();
                    LinkedList<ArbitraryNode> zcPath = cPaths.removeFirst();
                    ArbitraryNode cNode = xcPath.getLast();

                    kuratowskiSubgraph = new KuratowskiSubgraph(
                            KuratowskiSubgraph.K33, graph);

                    kuratowskiSubgraph.addNode(xNode);
                    kuratowskiSubgraph.addNode(yNode);
                    kuratowskiSubgraph.addNode(zNode);
                    kuratowskiSubgraph.addNode(vNode);
                    kuratowskiSubgraph.addNode(wNode);
                    kuratowskiSubgraph.addNode(cNode);

                    kuratowskiSubgraph.addPath(vxPath);
                    kuratowskiSubgraph.addPath(vyPath);
                    kuratowskiSubgraph.addPath(zvPath);

                    kuratowskiSubgraph.addPath(ywPath);
                    kuratowskiSubgraph.addPath(wxPath);
                    kuratowskiSubgraph.addPath(wzPath);

                    kuratowskiSubgraph.addPath(xcPath);
                    kuratowskiSubgraph.addPath(ycPath);
                    kuratowskiSubgraph.addPath(zcPath);
                    return;

                }
            }
        }

        // case 2.2
        LinkedList<ArbitraryNode> xyPath = new LinkedList<ArbitraryNode>();
        for (Iterator<ArbitraryNode> i = vNode.adjacencyList.iterator(); i
                .hasNext();) {
            ArbitraryNode next = i.next();
            if (next == vNode.adjacencyList.getFirst()) {
                continue;
            }
            LinkedList<ArbitraryNode> l = findInternalCounterClockWiseCircle(
                    vNode, next);
            l.removeFirst();
            l.removeLast();
            xyPath.addAll(0, l);
        }
        for (Iterator<ArbitraryNode> i = vxPath.iterator(); i.hasNext();) {
            ArbitraryNode node = i.next();
            if (node != rNode) {
                xyPath.addFirst(node);
            }
        }
        for (Iterator<ArbitraryNode> i = vyPath.iterator(); i.hasNext();) {
            ArbitraryNode node = i.next();
            if (node != rNode) {
                xyPath.addLast(node);
            }
        }
        xyPath = removeDoubles(xyPath);

        markPathToVertex(vNode, yNode, ArbitraryNode.TOP_RIGHT);
        markPathToVertex(yNode, wNode, ArbitraryNode.BOTTOM_RIGHT);
        markPathToVertex(wNode, xNode, ArbitraryNode.BOTTOM_LEFT);
        markPathToVertex(xNode, vNode, ArbitraryNode.TOP_LEFT);
        yNode.quadrant = ArbitraryNode.CENTER_RIGHT;
        xNode.quadrant = ArbitraryNode.CENTER_LEFT;

        ArbitraryNode pxNode = findLastOnLeftBoundary(xyPath);
        ArbitraryNode pyNode = findFirstOnRightBoundary(xyPath);
        ArbitraryNode leftSecond = xyPath.get(1);
        ArbitraryNode rightSecond = xyPath.get(xyPath.size() - 2);

        if ((pxNode.quadrant == ArbitraryNode.BOTTOM_LEFT)
                && ((leftSecond.quadrant == ArbitraryNode.TOP_LEFT) || (leftSecond.quadrant == ArbitraryNode.NOT_ON_BORDER))) {
            while (xyPath.getFirst() != pxNode) {
                xyPath.removeFirst();
            }
            ArbitraryNode current = pxNode;
            while (current != xNode) {
                current = current.adjacencyList.getFirst();
                xyPath.addFirst(current);
            }
            xyPath.addFirst(xNode);
            leftSecond = xyPath.get(1);
        }
        if ((pyNode.quadrant == ArbitraryNode.BOTTOM_RIGHT)
                && ((rightSecond.quadrant == ArbitraryNode.TOP_RIGHT) || (rightSecond.quadrant == ArbitraryNode.NOT_ON_BORDER))) {
            while (xyPath.getLast() != pyNode) {
                xyPath.removeLast();
            }
            ArbitraryNode current = pyNode;
            while (current != yNode) {
                current = current.adjacencyList.getLast();
                xyPath.addLast(current);
            }
            xyPath.addLast(yNode);
            rightSecond = xyPath.get(xyPath.size() - 2);
        }

        LinkedList<ArbitraryNode> pxPyPath = new LinkedList<ArbitraryNode>();
        boolean leftFound = false;
        for (Iterator<ArbitraryNode> i = xyPath.iterator(); i.hasNext();) {
            ArbitraryNode pNode = i.next();
            if (pNode == pxNode) {
                leftFound = true;
            }
            if (leftFound) {
                pxPyPath.add(pNode);
            }
            if (pNode == pyNode) {
                break;
            }
        }

        // case 2.2.1
        if (pxNode.quadrant == ArbitraryNode.TOP_LEFT) {
            kuratowskiSubgraph = new KuratowskiSubgraph(KuratowskiSubgraph.K33,
                    graph);

            LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(xUxPath,
                    yUyPath, vPath);

            LinkedList<ArbitraryNode> xcPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> ycPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> vcPath = cPaths.removeFirst();
            ArbitraryNode cNode = xcPath.getLast();

            kuratowskiSubgraph.addNode(cNode);
            kuratowskiSubgraph.addNode(pxNode);
            kuratowskiSubgraph.addNode(wNode);
            kuratowskiSubgraph.addNode(vNode);
            kuratowskiSubgraph.addNode(xNode);

            if (pyNode.quadrant == ArbitraryNode.TOP_RIGHT) {
                // case 2.2.1.1
                kuratowskiSubgraph.addNode(yNode);

                LinkedList<ArbitraryNode> pxyPath = concat(pxPyPath,
                        findPathOnBoundary(pyNode, yNode));

                kuratowskiSubgraph.addPath(findPathOnBoundary(xNode, pxNode));
                kuratowskiSubgraph.addPath(findPathOnBoundary(pxNode, vNode));
                kuratowskiSubgraph.addPath(pxyPath);

                kuratowskiSubgraph.addPath(ywPath);
                kuratowskiSubgraph.addPath(wxPath);
                kuratowskiSubgraph.addPath(findPertinentPath(wNode, vNode));

                kuratowskiSubgraph.addPath(xcPath);
                kuratowskiSubgraph.addPath(ycPath);
                kuratowskiSubgraph.addPath(vcPath);

            } else {
                // case 2.2.1.2
                kuratowskiSubgraph.addNode(pyNode);

                LinkedList<ArbitraryNode> yPyPath = findPathOnBoundary(yNode,
                        pyNode);

                LinkedList<ArbitraryNode> pyyPath = new LinkedList<ArbitraryNode>();
                for (Iterator<ArbitraryNode> i = yPyPath.iterator(); i
                        .hasNext();) {
                    pyyPath.addFirst(i.next());
                }
                LinkedList<ArbitraryNode> pycPath = concat(pyyPath, ycPath);

                kuratowskiSubgraph.addPath(findPathOnBoundary(xNode, pxNode));
                kuratowskiSubgraph.addPath(findPathOnBoundary(pxNode, vNode));
                kuratowskiSubgraph.addPath(pxPyPath);

                kuratowskiSubgraph.addPath(findPathOnBoundary(pyNode, wNode));
                kuratowskiSubgraph.addPath(wxPath);
                kuratowskiSubgraph.addPath(findPertinentPath(wNode, vNode));

                kuratowskiSubgraph.addPath(xcPath);
                kuratowskiSubgraph.addPath(pycPath);
                kuratowskiSubgraph.addPath(vcPath);

            }
            return;
        }

        // case 2.2.2
        if (pyNode.quadrant == ArbitraryNode.TOP_RIGHT) {

            LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(xUxPath,
                    yUyPath, vPath);

            LinkedList<ArbitraryNode> xcPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> ycPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> vcPath = cPaths.removeFirst();
            ArbitraryNode cNode = xcPath.getLast();

            kuratowskiSubgraph = new KuratowskiSubgraph(KuratowskiSubgraph.K33,
                    graph);

            kuratowskiSubgraph.addNode(cNode);
            kuratowskiSubgraph.addNode(pyNode);
            kuratowskiSubgraph.addNode(wNode);
            kuratowskiSubgraph.addNode(vNode);
            kuratowskiSubgraph.addNode(yNode);
            kuratowskiSubgraph.addNode(pxNode);

            LinkedList<ArbitraryNode> pxxPath = findPathOnBoundary(pxNode,
                    xNode);

            LinkedList<ArbitraryNode> pxcPath = concat(pxxPath, xcPath);

            kuratowskiSubgraph.addPath(findPathOnBoundary(pyNode, yNode));
            kuratowskiSubgraph.addPath(findPathOnBoundary(vNode, pyNode));
            kuratowskiSubgraph.addPath(pxPyPath);

            kuratowskiSubgraph.addPath(findPathOnBoundary(wNode, pxNode));
            kuratowskiSubgraph.addPath(ywPath);
            kuratowskiSubgraph.addPath(findPertinentPath(wNode, vNode));

            kuratowskiSubgraph.addPath(ycPath);
            kuratowskiSubgraph.addPath(pxcPath);
            kuratowskiSubgraph.addPath(vcPath);

            return;
        }

        // case 2.2.3
        LinkedList<ArbitraryNode> zvPath = new LinkedList<ArbitraryNode>();
        ArbitraryNode secondOnPxPyPath = pxPyPath.get(1);
        for (ListIterator<ArbitraryNode> i = vNode.adjacencyList
                .listIterator(vNode.adjacencyList.size()); i.hasPrevious();) {
            zvPath = findInternalCounterClockWiseCircle(vNode, i.previous());
            if ((zvPath.contains(pxNode))
                    && (zvPath.contains(secondOnPxPyPath))) {
                break;
            }
        }

        while (zvPath.getFirst() != pxNode) {
            zvPath.removeFirst();
        }
        ArbitraryNode last = null;
        ArbitraryNode current = null;
        boolean middleFound = false;
        for (Iterator<ArbitraryNode> i = pxPyPath.iterator(); i.hasNext();) {
            last = current;
            current = i.next();
            if (current != zvPath.getFirst()) {
                middleFound = true;
                break;
            }
            zvPath.removeFirst();
        }
        zvPath.addFirst(last);
        if (middleFound) {
            ArbitraryNode zNode = zvPath.getFirst();

            LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(xUxPath,
                    yUyPath, vPath);

            LinkedList<ArbitraryNode> xcPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> ycPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> vcPath = cPaths.removeFirst();
            ArbitraryNode cNode = xcPath.getLast();

            kuratowskiSubgraph = new KuratowskiSubgraph(KuratowskiSubgraph.K33,
                    graph);

            kuratowskiSubgraph.addNode(cNode);
            kuratowskiSubgraph.addNode(zNode);
            kuratowskiSubgraph.addNode(wNode);
            kuratowskiSubgraph.addNode(vNode);
            kuratowskiSubgraph.addNode(pxNode);
            kuratowskiSubgraph.addNode(pyNode);

            LinkedList<ArbitraryNode> pxzPath = getSubpath(pxPyPath, pxNode,
                    zNode);
            LinkedList<ArbitraryNode> zPyPath = getSubpath(pxPyPath, zNode,
                    pyNode);
            LinkedList<ArbitraryNode> pxcPath = concat(findPathOnBoundary(
                    pxNode, xNode), xcPath);
            LinkedList<ArbitraryNode> pycPath = concat(
                    reverse(findPathOnBoundary(yNode, pyNode)), ycPath);

            kuratowskiSubgraph.addPath(pxzPath);
            kuratowskiSubgraph.addPath(zPyPath);
            kuratowskiSubgraph.addPath(zvPath);

            kuratowskiSubgraph.addPath(findPathOnBoundary(wNode, pxNode));
            kuratowskiSubgraph.addPath(findPathOnBoundary(pyNode, wNode));
            kuratowskiSubgraph.addPath(findPertinentPath(wNode, vNode));

            kuratowskiSubgraph.addPath(pxcPath);
            kuratowskiSubgraph.addPath(pycPath);
            kuratowskiSubgraph.addPath(vcPath);

            return;
        }

        // case 2.2.4
        current = pyNode;
        ArbitraryNode zNode = null;
        boolean onTheRight = true;
        while (true) {
            current = current.adjacencyList.getFirst();
            if (current == pxNode) {
                break;
            }
            if (current == wNode) {
                onTheRight = false;
                continue;
            }
            if (comp.externallyActive(current.getRealNode(), vNode
                    .getRealNode())) {
                zNode = current;
                break;
            }

        }
        if (zNode != null) {
            // case 2.2.4.1

            kuratowskiSubgraph = new KuratowskiSubgraph(KuratowskiSubgraph.K33,
                    graph);
            kuratowskiSubgraph.addNode(vNode);
            kuratowskiSubgraph.addNode(zNode);
            ArbitraryNode cNode;
            LinkedList<ArbitraryNode> zUzPath = findExternalPath(zNode, vNode
                    .getRealNode());
            if (onTheRight) {
                kuratowskiSubgraph.addNode(pxNode);
                kuratowskiSubgraph.addNode(pyNode);

                LinkedList<ArbitraryNode> pxUxPath = concat(findPathOnBoundary(
                        pxNode, xNode), xUxPath);
                LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(zUzPath,
                        pxUxPath, vPath);
                LinkedList<ArbitraryNode> zcPath = cPaths.removeFirst();
                LinkedList<ArbitraryNode> pxcPath = cPaths.removeFirst();
                LinkedList<ArbitraryNode> vcPath = cPaths.removeFirst();
                cNode = vcPath.getLast();

                kuratowskiSubgraph.addPath(findPathOnBoundary(wNode, pxNode));
                kuratowskiSubgraph.addPath(findPathOnBoundary(zNode, wNode));
                kuratowskiSubgraph.addPath(findPertinentPath(wNode, vNode));

                kuratowskiSubgraph.addPath(findPathOnBoundary(pyNode, zNode));
                kuratowskiSubgraph.addPath(findPathOnBoundary(vNode, pyNode));
                kuratowskiSubgraph.addPath(pxPyPath);

                kuratowskiSubgraph.addPath(zcPath);
                kuratowskiSubgraph.addPath(pxcPath);
                kuratowskiSubgraph.addPath(vcPath);

            } else {
                kuratowskiSubgraph.addNode(pyNode);
                kuratowskiSubgraph.addNode(pxNode);

                LinkedList<ArbitraryNode> pyUyPath = concat(
                        reverse(findPathOnBoundary(yNode, pyNode)), yUyPath);
                LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(zUzPath,
                        pyUyPath, vPath);
                LinkedList<ArbitraryNode> zcPath = cPaths.removeFirst();
                LinkedList<ArbitraryNode> pycPath = cPaths.removeFirst();
                LinkedList<ArbitraryNode> vcPath = cPaths.removeFirst();
                cNode = vcPath.getLast();

                kuratowskiSubgraph.addPath(findPathOnBoundary(pyNode, wNode));
                kuratowskiSubgraph.addPath(findPathOnBoundary(wNode, zNode));
                kuratowskiSubgraph.addPath(findPertinentPath(wNode, vNode));

                kuratowskiSubgraph.addPath(findPathOnBoundary(zNode, pxNode));
                kuratowskiSubgraph.addPath(findPathOnBoundary(pxNode, vNode));
                kuratowskiSubgraph.addPath(pxPyPath);

                kuratowskiSubgraph.addPath(zcPath);
                kuratowskiSubgraph.addPath(pycPath);
                kuratowskiSubgraph.addPath(vcPath);

            }
            kuratowskiSubgraph.addNode(wNode);
            kuratowskiSubgraph.addNode(cNode);
            return;

        }

        LinkedList<ArbitraryNode> wUwPath = findExternalPath(wNode, vNode
                .getRealNode());
        ArbitraryNode uwNode = wUwPath.getLast();
        LinkedList<ArbitraryNode> uwPath = new LinkedList<ArbitraryNode>();
        uwPath.add(uwNode);

        if ((uwNode.getRealNode().DFI > uxNode.getRealNode().DFI)
                && (uwNode.getRealNode().DFI > uyNode.getRealNode().DFI)) {
            // case 2.2.4.2.1

            kuratowskiSubgraph = new KuratowskiSubgraph(KuratowskiSubgraph.K33,
                    graph);

            LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(xUxPath,
                    yUyPath, uwPath);
            LinkedList<ArbitraryNode> xcPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> ycPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> uwcPath = cPaths.removeFirst();
            ArbitraryNode cNode = xcPath.getLast();

            kuratowskiSubgraph.addNode(vNode);
            kuratowskiSubgraph.addNode(cNode);
            kuratowskiSubgraph.addNode(wNode);
            kuratowskiSubgraph.addNode(uwNode);
            kuratowskiSubgraph.addNode(xNode);
            kuratowskiSubgraph.addNode(yNode);

            kuratowskiSubgraph.addPath(xcPath);
            kuratowskiSubgraph.addPath(ycPath);
            kuratowskiSubgraph.addPath(uwcPath);

            kuratowskiSubgraph.addPath(findPathOnBoundary(xNode, vNode));
            kuratowskiSubgraph.addPath(findPathOnBoundary(vNode, yNode));
            kuratowskiSubgraph.addPath(findDFSTree(vNode, uwNode));

            kuratowskiSubgraph.addPath(findPathOnBoundary(yNode, wNode));
            kuratowskiSubgraph.addPath(findPathOnBoundary(wNode, xNode));
            kuratowskiSubgraph.addPath(wUwPath);

            return;
        }

        LinkedList<ArbitraryNode> uyPath = new LinkedList<ArbitraryNode>();
        uyPath.add(uyNode);

        if ((uyNode.getRealNode().DFI > uwNode.getRealNode().DFI)
                && (uyNode.getRealNode().DFI > uxNode.getRealNode().DFI)) {
            // 2.2.4.2.2 with u_y being lowest node

            kuratowskiSubgraph = new KuratowskiSubgraph(KuratowskiSubgraph.K33,
                    graph);

//            System.out.println(xUxPath);
            LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(uyPath,
                    xUxPath, wUwPath);
            LinkedList<ArbitraryNode> uycPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> xcPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> wcPath = cPaths.removeFirst();
            ArbitraryNode cNode = wcPath.getLast();
//            System.out.println(xcPath);

            kuratowskiSubgraph.addNode(vNode);
            kuratowskiSubgraph.addNode(cNode);
            kuratowskiSubgraph.addNode(pyNode);
            kuratowskiSubgraph.addNode(uyNode);
            kuratowskiSubgraph.addNode(xNode);
            kuratowskiSubgraph.addNode(wNode);

            kuratowskiSubgraph.addPath(uycPath);
            kuratowskiSubgraph.addPath(xcPath);
            kuratowskiSubgraph.addPath(wcPath);

            kuratowskiSubgraph.addPath(findPathOnBoundary(xNode, vNode));
            kuratowskiSubgraph.addPath(findPertinentPath(wNode, vNode));
            kuratowskiSubgraph.addPath(findDFSTree(vNode, uyNode));

            kuratowskiSubgraph.addPath(findPathOnBoundary(pyNode, wNode));
            kuratowskiSubgraph.addPath(concat(reverse(findPathOnBoundary(
                    pxNode, xNode)), pxPyPath));
            kuratowskiSubgraph.addPath(concat(reverse(findPathOnBoundary(yNode,
                    pyNode)), yUyPath));

            return;
        }

        LinkedList<ArbitraryNode> uxPath = new LinkedList<ArbitraryNode>();
        uxPath.add(uxNode);

        if ((uxNode.getRealNode().DFI > uwNode.getRealNode().DFI)
                && (uxNode.getRealNode().DFI > uyNode.getRealNode().DFI)) {
            // case 2.2.4.2.2 with u_x being lowest node

            kuratowskiSubgraph = new KuratowskiSubgraph(KuratowskiSubgraph.K33,
                    graph);

            LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(uxPath,
                    yUyPath, wUwPath);
            LinkedList<ArbitraryNode> uxcPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> ycPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> wcPath = cPaths.removeFirst();
            ArbitraryNode cNode = wcPath.getLast();

            kuratowskiSubgraph.addNode(vNode);
            kuratowskiSubgraph.addNode(cNode);
            kuratowskiSubgraph.addNode(pxNode);
            kuratowskiSubgraph.addNode(uxNode);
            kuratowskiSubgraph.addNode(yNode);
            kuratowskiSubgraph.addNode(wNode);

            kuratowskiSubgraph.addPath(uxcPath);
            kuratowskiSubgraph.addPath(ycPath);
            kuratowskiSubgraph.addPath(wcPath);

            kuratowskiSubgraph.addPath(findPathOnBoundary(vNode, yNode));
            kuratowskiSubgraph.addPath(findPertinentPath(wNode, vNode));
            kuratowskiSubgraph.addPath(findDFSTree(vNode, uxNode));

            kuratowskiSubgraph.addPath(findPathOnBoundary(wNode, pxNode));
            kuratowskiSubgraph.addPath(concat(pxPyPath,
                    reverse(findPathOnBoundary(yNode, pyNode))));
            kuratowskiSubgraph.addPath(concat(
                    findPathOnBoundary(pxNode, xNode), xUxPath));

            return;
        }

        if (pxNode != xNode) {
            // case 2.2.4.2.3.1 with p_x being lower than x

            kuratowskiSubgraph = new KuratowskiSubgraph(KuratowskiSubgraph.K33,
                    graph);

            LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(xUxPath,
                    yUyPath, wUwPath);
            LinkedList<ArbitraryNode> xcPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> ycPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> wcPath = cPaths.removeFirst();
            ArbitraryNode cNode = xcPath.getLast();

            kuratowskiSubgraph.addNode(vNode);
            kuratowskiSubgraph.addNode(cNode);
            kuratowskiSubgraph.addNode(pxNode);
            kuratowskiSubgraph.addNode(xNode);
            kuratowskiSubgraph.addNode(yNode);
            kuratowskiSubgraph.addNode(wNode);

            kuratowskiSubgraph.addPath(xcPath);
            kuratowskiSubgraph.addPath(ycPath);
            kuratowskiSubgraph.addPath(wcPath);

            kuratowskiSubgraph.addPath(findPathOnBoundary(xNode, vNode));
            kuratowskiSubgraph.addPath(findPathOnBoundary(vNode, yNode));
            kuratowskiSubgraph.addPath(findPertinentPath(wNode, vNode));

            kuratowskiSubgraph.addPath(findPathOnBoundary(pxNode, xNode));
            kuratowskiSubgraph.addPath(findPathOnBoundary(wNode, pxNode));
            kuratowskiSubgraph.addPath(concat(pxPyPath,
                    reverse(findPathOnBoundary(yNode, pyNode))));

            return;
        }
        if (pyNode != yNode) {
            // case 2.2.4.2.3.1 with p_y begin lower than y

            kuratowskiSubgraph = new KuratowskiSubgraph(KuratowskiSubgraph.K33,
                    graph);

            LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(xUxPath,
                    yUyPath, wUwPath);
            LinkedList<ArbitraryNode> xcPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> ycPath = cPaths.removeFirst();
            LinkedList<ArbitraryNode> wcPath = cPaths.removeFirst();
            ArbitraryNode cNode = xcPath.getLast();

            kuratowskiSubgraph.addNode(vNode);
            kuratowskiSubgraph.addNode(cNode);
            kuratowskiSubgraph.addNode(pyNode);
            kuratowskiSubgraph.addNode(yNode);
            kuratowskiSubgraph.addNode(xNode);
            kuratowskiSubgraph.addNode(wNode);

            kuratowskiSubgraph.addPath(xcPath);
            kuratowskiSubgraph.addPath(ycPath);
            kuratowskiSubgraph.addPath(wcPath);

            kuratowskiSubgraph.addPath(findPathOnBoundary(xNode, vNode));
            kuratowskiSubgraph.addPath(findPathOnBoundary(vNode, yNode));
            kuratowskiSubgraph.addPath(findPertinentPath(wNode, vNode));

            kuratowskiSubgraph.addPath(findPathOnBoundary(yNode, pyNode));
            kuratowskiSubgraph.addPath(findPathOnBoundary(pyNode, wNode));
            kuratowskiSubgraph.addPath(concat(reverse(findPathOnBoundary(
                    pxNode, xNode)), pxPyPath));

            return;
        }

        // case 2.2.4.2.3.2
        kuratowskiSubgraph = new KuratowskiSubgraph(KuratowskiSubgraph.K5,
                graph);

        LinkedList<LinkedList<ArbitraryNode>> cPaths = connect(xUxPath,
                yUyPath, wUwPath);
        LinkedList<ArbitraryNode> xcPath = cPaths.removeFirst();
        LinkedList<ArbitraryNode> ycPath = cPaths.removeFirst();
        LinkedList<ArbitraryNode> wcPath = cPaths.removeFirst();
        ArbitraryNode cNode = xcPath.getLast();

        kuratowskiSubgraph.addNode(vNode);
        kuratowskiSubgraph.addNode(xNode);
        kuratowskiSubgraph.addNode(yNode);
        kuratowskiSubgraph.addNode(wNode);
        kuratowskiSubgraph.addNode(cNode);

        kuratowskiSubgraph.addPath(xcPath);
        kuratowskiSubgraph.addPath(ycPath);
        kuratowskiSubgraph.addPath(wcPath);
        kuratowskiSubgraph.addPath(findDFSTree(vNode, cNode));

        kuratowskiSubgraph.addPath(findPathOnBoundary(xNode, vNode));
        kuratowskiSubgraph.addPath(findPathOnBoundary(vNode, yNode));
        kuratowskiSubgraph.addPath(findPertinentPath(wNode, vNode));

        kuratowskiSubgraph.addPath(findPathOnBoundary(yNode, wNode));
        kuratowskiSubgraph.addPath(xyPath);

        kuratowskiSubgraph.addPath(findPathOnBoundary(wNode, xNode));
    }

}
