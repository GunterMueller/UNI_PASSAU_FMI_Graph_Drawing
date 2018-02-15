package org.graffiti.plugins.algorithms.fas;

import java.util.Iterator;
import java.util.Stack;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugins.algorithms.SchnyderRealizer.HashList;

public class GetStronglyConnectedComponents {
    private CircuitCounter cc;

    private Graph graph;

    private HashList<Edge> treeEdges = new HashList<Edge>();

    private HashList<Edge> forwardEdges = new HashList<Edge>();

    private HashList<Edge> backEdges = new HashList<Edge>();

    private HashList<Edge> crossEdges = new HashList<Edge>();

    private HashList<Node> markedNodes = new HashList<Node>();

    private HashList<Edge> markedEdges = new HashList<Edge>();

    private HashList<Node> stack = new HashList<Node>();

    private HashList<Node> nodesInOpenComponents = new HashList<Node>();

    private Stack<Node> openComponents = new Stack<Node>();

    private int[] dfsNum;

    private int currentDFSNum = 1;

    private int[] componentNum;

    private int currentComponentNum = 1;

    public GetStronglyConnectedComponents(Graph g, CircuitCounter counter) {
        this.cc = counter;
        this.graph = g;
        this.dfsNum = new int[this.graph.getNodes().size()];
        this.componentNum = new int[this.graph.getNodes().size()];
    }

    public void execute() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node start = nodesIt.next();
            if (!this.markedNodes.contains(start)) {
                markedNodes.append(start);
                stack.append(start);
                this.dfsNum[this.cc.nodeToNumber(start)] = currentDFSNum++;
                this.root(start);
                this.executeOneStep();
            }
        }
        this.labelComponents();
    }

    private void executeOneStep() {
        while (!stack.isEmpty()) {
            Node current = stack.getPredecessor(stack.getFirst());
            this.markedNodes.append(current);
            Iterator<Edge> edgesIt = current.getAllOutEdges().iterator();
            boolean edgeFound = false;
            while (edgesIt.hasNext()) {
                Edge e = edgesIt.next();
                if (!markedEdges.contains(e)) {
                    markedEdges.append(e);
                    this.traverse(e);
                    if (!this.markedNodes.contains(e.getTarget())) {
                        edgeFound = true;
                        stack.append(e.getTarget());
                        this.dfsNum[this.cc.nodeToNumber(e.getTarget())] = currentDFSNum++;
                        break;
                    }
                }
            }
            if (!edgeFound) {
                current = stack.getPredecessor(stack.getFirst());
                stack.remove(current);
                if (!stack.isEmpty()) {
                    this.backtrack(current, stack.getPredecessor(stack
                            .getFirst()));
                }
            }
        }
    }

    private void root(Node s) {
        this.nodesInOpenComponents.append(s);
        this.openComponents.push(s);
    }

    private void traverse(Edge e) {
        this.classifyEdge(e);
        if (this.treeEdges.contains(e)) {
            this.openComponents.push(e.getTarget());
            this.nodesInOpenComponents.append(e.getTarget());
        } else if (this.backEdges.contains(e)
                || (this.crossEdges.contains(e) && this.nodesInOpenComponents
                        .contains(e.getTarget()))) {
            int targetDFS = this.dfsNum[this.cc.nodeToNumber(e.getTarget())];
            while (targetDFS < this.dfsNum[this.cc
                    .nodeToNumber(this.openComponents.peek())]) {
                this.openComponents.pop();
            }
        }
    }

    private void backtrack(Node target, Node source) {
        if (this.openComponents.peek().equals(target)) {
            this.openComponents.pop();
            Node n = nodesInOpenComponents.getLast();
            do {
                n = nodesInOpenComponents.getLast();
                nodesInOpenComponents.remove(n);
                this.componentNum[cc.nodeToNumber(n)] = currentComponentNum;
            } while (!n.equals(target));
            currentComponentNum++;
        }
    }

    private void classifyEdge(Edge e) {
        if (!this.markedNodes.contains(e.getTarget())) {
            this.treeEdges.append(e);
        } else if (this.dfsNum[this.cc.nodeToNumber(e.getSource())] < this.dfsNum[this.cc
                .nodeToNumber(e.getTarget())]) {
            this.forwardEdges.append(e);
        } else if (this.stack.contains(e.getTarget())) {
            this.backEdges.append(e);
        } else {
            this.crossEdges.append(e);
        }
    }

    private void labelComponents() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            String labelString = (this.componentNum[this.cc.nodeToNumber(n)] + "");

            NodeLabelAttribute nodeLabel = new NodeLabelAttribute("label");
            try {
                nodeLabel = (NodeLabelAttribute) n.getAttributes()
                        .getAttribute("label");

                nodeLabel.setLabel(labelString);
            } catch (AttributeNotFoundException e) {
                nodeLabel.setLabel(labelString);
                n.getAttributes().add(nodeLabel);
            }
        }
    }

    public HashList<Edge> getBackEdges() {
        return backEdges;
    }

    public HashList<Edge> getCrossEdges() {
        return crossEdges;
    }

    public HashList<Edge> getForwardEdges() {
        return forwardEdges;
    }

    public HashList<Edge> getTreeEdges() {
        return treeEdges;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
