package de.uni_passau.fim.br.planarity.gravisto;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.FastGraph;

import de.uni_passau.fim.br.planarity.graph.Graph;
import de.uni_passau.fim.br.planarity.graph.Vertex;

public class Converter {
	public static Graph convert(org.graffiti.graph.Graph graph) {
		Graph result = new Graph();
		Map<org.graffiti.graph.Node, Vertex> nodeMap = new HashMap<org.graffiti.graph.Node, Vertex>();
		for (org.graffiti.graph.Node node : graph.getNodes()) {
			String name = null;
			if (node.containsAttribute("label0")) {
				name = node.getString("label0");
			}
			Vertex nn = result.addVertex(name);
			nodeMap.put(node, nn);
		}
		for (Edge edge : graph.getEdges()) {
			nodeMap.get(edge.getSource()).connect(nodeMap.get(edge.getTarget()));
		}
		return result;
	}
	
	public static org.graffiti.graph.Graph convert(Graph graph) {
		org.graffiti.graph.Graph result = new FastGraph();
		org.graffiti.graph.Node[] nodes = new org.graffiti.graph.Node[graph.size()];
		for (Vertex node : graph.vertices()) {
			org.graffiti.graph.Node nn = result.addNode();
			nodes[node.index()] = nn;
			if (node.name() != null) {
				nn.setString("label.label", node.name());
			}
		}
		for (Vertex node : graph.vertices()) {
			for (Vertex neighbor : node.neighbors()) {
				if (node.index() < neighbor.index()) {
					result.addEdge(nodes[node.index()], nodes[neighbor.index()], false);
				}
			}
		}
		return result;
	}
}
