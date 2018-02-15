package de.uni_passau.fim.br.planarity.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Graph {
	private List<Vertex> vertices;
	private int size;
	
	public Graph() {
		vertices = new ArrayList<Vertex>();
		size = 0;
	}
	
	public Vertex addVertex() {
		return addVertex(String.valueOf(size));
	}
	
	public Vertex addVertex(String name) {
		Vertex vertex = new Vertex(this, size, name);
		vertices.add(vertex);
		size++;
		return vertex;
	}
	
	public Collection<Vertex> vertices() {
		return Collections.unmodifiableCollection(vertices);
	}
	
	public int size() {
		return size;
	}
}
