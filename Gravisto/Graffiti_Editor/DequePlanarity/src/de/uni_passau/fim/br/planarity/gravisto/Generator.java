package de.uni_passau.fim.br.planarity.gravisto;

import java.util.ArrayList;
import java.util.Random;

import de.uni_passau.fim.br.planarity.graph.Graph;
import de.uni_passau.fim.br.planarity.graph.Vertex;

/**
 * Naive cubic graph generator.
 * @author aglx
 *
 */
public class Generator {
	private Random random;
	
	public Generator(long seed) {
		random = new Random(seed);
	}
	
	public Graph create(int size) {
		if (size % 2 != 0) throw new IllegalArgumentException();
		Graph graph = null;
		while (graph == null) {
			graph = tryCreate(size);
		}
		return graph;
	}
	
	private Graph tryCreate(int size) {
	    int edgeCount = 0;
		Graph graph = new Graph();
		ArrayList<Vertex> candidates = new ArrayList<Vertex>();
		for (int i = 0; i < size; i++) {
			candidates.add(graph.addVertex());
		}
		int candidateCount = size;
		while (edgeCount < 1.5 * size) {
//		while (candidateCount > 0) {
			if (candidateCount == 1) return null;
			int first = random.nextInt(candidateCount);
			Vertex n1 = candidates.get(first);
			Vertex n2;
			int second;
			int trials = 0;
			do {
				second = random.nextInt(candidateCount);
				n2 = candidates.get(second);
				trials++;
				if (trials > 20000) return null;
			} while (second == first || n1.neighbors().contains(n2));
			
			n1.connect(n2);
			if (n1.neighbors().size() == size - 1) {
				candidates.set(first, candidates.get(candidateCount - 1));
				if (second == candidateCount - 1) {
					second = first;
				}
				candidateCount--;
			}
			if (n2.neighbors().size() == size - 1) {
				candidates.set(second, candidates.get(candidateCount - 1));
				candidateCount--;
			}
			
			edgeCount++;
		}
		return graph;
	}
}
