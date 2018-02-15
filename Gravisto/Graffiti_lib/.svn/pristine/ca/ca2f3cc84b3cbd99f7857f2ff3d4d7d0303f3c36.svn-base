package de.uni_passau.fim.br.planarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uni_passau.fim.br.planarity.graph.Graph;
import de.uni_passau.fim.br.planarity.graph.Vertex;
import de.uni_passau.fim.br.planarity.gravisto.Tabulator;
import de.uni_passau.fim.br.planarity.mdeque.DequeException;
import de.uni_passau.fim.br.planarity.mdeque.DequeNode;
import de.uni_passau.fim.br.planarity.mdeque.EdgeNode;
import de.uni_passau.fim.br.planarity.mdeque.MergeNode;

public class PlanarityTest {
    private Graph graph;
    
    private int[] dfsNum;
    
    private int nextDfsNum;
    
    private List<List<EdgeNode>> backEdges;
    
    public PlanarityTest(Graph graph) {
        this.graph = graph;
        dfsNum = new int[graph.size()];
        backEdges = new ArrayList<List<EdgeNode>>();
        for (int i = 0; i < graph.size(); i++) {
            backEdges.add(new ArrayList<EdgeNode>());
        }
        nextDfsNum = 1;
    }
    
    public boolean test() {
        try {
            for (Vertex vertex : graph.vertices()) {
                if (dfsNum[vertex.index()] == 0) {
                    dfs(vertex, null);
                }
            }
            return true;
        } catch (DequeException e) {
            return false;
        }
    }
    
    private DequeNode dfs(Vertex vertex, Vertex parent) throws DequeException {
        Tabulator.out.println("dfs(" + vertex + ", " + parent + ")");
        int currentDfs = nextDfsNum;
        nextDfsNum++;
        dfsNum[vertex.index()] = currentDfs;

        Collection<DequeNode> deques = new ArrayList<DequeNode>();
        
        for (Vertex neighbor : vertex.neighbors()) {
            if (neighbor == parent) continue;
            
            int neighborDfs = dfsNum[neighbor.index()];
            if (neighborDfs == 0) {
                deques.add(dfs(neighbor, vertex));
            } else if (neighborDfs < currentDfs) {
                EdgeNode backEdge = new EdgeNode(vertex, neighbor);
                Tabulator.out.println("pushing " + backEdge);
                backEdges.get(neighbor.index()).add(backEdge);
                deques.add(backEdge);
            }
        }
        
        List<EdgeNode> bs = backEdges.get(vertex.index());
        for (EdgeNode backEdge : bs) {
            backEdge.mark();
        }
        for (EdgeNode backEdge : bs) {
            backEdge.triggerExtraction(currentDfs, null);
        }
        
        return new MergeNode(deques, vertex.toString());
    }
}
