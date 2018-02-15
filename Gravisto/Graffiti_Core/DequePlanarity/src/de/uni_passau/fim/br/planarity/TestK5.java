package de.uni_passau.fim.br.planarity;

import de.uni_passau.fim.br.planarity.graph.Graph;
import de.uni_passau.fim.br.planarity.graph.Vertex;


public class TestK5 {
	
	private TestK5() {
	}
	
	public void start() {
		Graph graph = new Graph();
		Vertex[] vertices = new Vertex[5];
		for (int i = 0; i < 5; i++) {
		    vertices[i] = graph.addVertex("v" + i);
		}
		for (int i = 0; i < 5; i++) {
		    for (int j = i + 1; j < 5; j++) {
		        vertices[i].connect(vertices[j]);
		    }
		}
		
		PlanarityTest test = new PlanarityTest(graph);
		boolean isPlanar = test.test();
		if (isPlanar) {
			System.out.println("Planar!");
		} else {
			System.out.println("Not planar!");
		}
	}
	
	public static void main(String[] args) {
		new TestK5().start();
	}
}
