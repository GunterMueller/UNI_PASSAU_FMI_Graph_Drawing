package de.uni_passau.fim.br.planarity.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Node {
	private final Graph graph;
	private final int index;
	private final Set<Node> neighbors;
	private String name;
	
	Node(Graph graph, int index, String name) {
		this.graph = graph;
		this.index = index;
		this.name = name;
		neighbors = new LinkedHashSet<Node>();
	}
	
	public Graph graph() {
		return graph;
	}
	
	public int index() {
		return index;
	}
	
	public String name() {
		return name;
	}
	
	public void connect(Node other) {
		if (other.graph != graph) {
			throw new IllegalArgumentException();
		}
		
		neighbors.add(other);
		other.neighbors.add(this);
	}
	
	public Collection<Node> neighbors() {
		return Collections.unmodifiableCollection(neighbors);
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name == null ? "n" + index : name;
	}
}
