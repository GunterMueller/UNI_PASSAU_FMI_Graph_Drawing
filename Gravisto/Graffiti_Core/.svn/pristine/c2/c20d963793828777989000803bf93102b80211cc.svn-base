package de.uni_passau.fim.br.planarity;

import de.uni_passau.fim.br.planarity.graph.Graph;
import de.uni_passau.fim.br.planarity.graph.Vertex;


public class Test {
	
	private Test() {
	}
	
	public void start() {
		Graph graph = new Graph();
		Vertex white1 = graph.addVertex("w1");
		Vertex white2 = graph.addVertex("w2");
		Vertex white3 = graph.addVertex("w3");
		Vertex black1 = graph.addVertex("b1");
		Vertex black2 = graph.addVertex("b2");
		Vertex black3 = graph.addVertex("b3");
		white1.connect(black1);
		white1.connect(black2);
		white1.connect(black3);
		white2.connect(black1);
		white2.connect(black2);
		white2.connect(black3);
		white3.connect(black1);
		white3.connect(black2);
		white3.connect(black3);
		
		PlanarityTest test = new PlanarityTest(graph);
		boolean isPlanar = test.test();
		if (isPlanar) {
			System.out.println("Planar!");
		} else {
			System.out.println("Not planar!");
		}
	}
	
	public static void main(String[] args) {
		new Test().start();
	}
}
