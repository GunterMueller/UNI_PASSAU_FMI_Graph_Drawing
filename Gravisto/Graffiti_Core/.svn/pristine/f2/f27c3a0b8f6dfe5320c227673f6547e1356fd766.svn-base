package org.graffiti.plugins.algorithms.sugiyama.gridsifting.benchmark;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.ios.gml.gmlWriter.GmlWriter;
import org.graffiti.plugins.tools.math.Permutation;


public class RandomPA2 {
    private static final double SPREAD = 200.0;
    
    private Random random;
    
    private File directory;
    
    private boolean randomizeCoordinates;
    
    /*
     * java -server -cp Benchmarks/bin:Graffiti_Core/build/classes:Graffiti_Editor/build/classes:Graffiti_Plugins/build/classes:Graffiti_lib/bcel-5.2.jar:Graffiti_lib/collections-generic-4.01.jar:Graffiti_lib/commons-beanutils.jar:Graffiti_lib/commons-collections-3.2.jar:Graffiti_lib/commons-collections-testframework-3.2.jar:Graffiti_lib/commons-collections-testframework-4.01.jar:Graffiti_lib/commons-digester-1.7.jar:Graffiti_lib/commons-logging-api-1.1.jar:Graffiti_lib/core-renderer.jar:Graffiti_lib/gluegen-rt.jar:Graffiti_lib/itext-1.4.7.jar:Graffiti_lib/java-cup-11a.jar:Graffiti_lib/JFlex.jar:Graffiti_lib/jogl.jar:Graffiti_lib/js.jar:Graffiti_lib/junit.jar:Graffiti_lib/jython.jar:Graffiti_lib/looks-2.2.1.jar:Graffiti_lib/lpsolve55j.jar:Graffiti_lib/tidy.jar -Djava.library.path=Graffiti_lib/64bit_libraries org.graffiti.plugins.algorithms.sugiyama.gridsifting.benchmark.RandomPA2 /path/to/target/directory
     */
    
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("RandomPA2 <DIR>");
            System.exit(1);
        }
        
        new RandomPA2(new File(args[0])).execute();
    }
    
    private RandomPA2(File directory) {
        randomizeCoordinates = true;
        this.directory = directory; 
    }
    
    private void execute() throws IOException {
        Random globalRandom = new Random(411);
        int maxDensity = 25;
        
        for (int edgeDensity = 15; edgeDensity <= maxDensity; edgeDensity += 1) {
            random = new Random(globalRandom.nextLong());
            
            int nodeCount = 100;
            int edgeCount = nodeCount * edgeDensity / 10;
            System.out.println("EC " + edgeCount);
            
            for (int index = 0; index < 10; index++) {
                produceGraph(nodeCount, edgeCount, index);
            }
        }
    }
    
    private void produceGraph(int nodeCount, int edgeCount, int index) throws IOException {
        String fileName = String.format("%s/randomPA2_n%03d_e%04d_i%01d.gml", directory.getCanonicalPath(), nodeCount, edgeCount, index);
        Graph graph = makeGraph(nodeCount, edgeCount);
        GmlWriter writer = new GmlWriter();
        try {
            writer.write(graph, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Graph makeGraph(int nodeCount, int edgeCount) {
        Graph graph;
        
        graph = new FastGraph();

        Permutation permutation = new Permutation(nodeCount * (nodeCount - 1) / 2);
        permutation.shuffle(random);

        Node[] nodes = new Node[nodeCount];
        
        Set<Integer> connectedNodes = new HashSet<Integer>();
        Set<Integer> unconnectedNodes = new HashSet<Integer>();

        for (int i = 0; i < nodeCount; i++) {
            nodes[i] = graph.addNode();
            unconnectedNodes.add(i);
        }
        
        int initialNode = drawFromSet(unconnectedNodes);
        unconnectedNodes.remove(initialNode);
        connectedNodes.add(initialNode);
        int currentEdgeCount = 0;
        
        while (!unconnectedNodes.isEmpty()) {
            int conNode = drawFromSet(connectedNodes);
            int unconNode = drawFromSet(unconnectedNodes);
            unconnectedNodes.remove(unconNode);
            connectedNodes.add(unconNode);
            int source = Math.min(conNode, unconNode);
            int target = Math.max(conNode, unconNode);
            
            graph.addEdge(nodes[source], nodes[target], true);
            currentEdgeCount++;
        }
        
        while (currentEdgeCount < edgeCount) {
            while (true) {
                int a = random.nextInt(nodeCount);
                int b = random.nextInt(nodeCount);
                if (a == b) continue;
                int source = Math.min(a, b);
                int target = Math.max(a, b);
                if (graph.getEdges(nodes[source], nodes[target]).isEmpty()) {
                    graph.addEdge(nodes[source], nodes[target], true);
                    currentEdgeCount++;
                    break;
                }
            }
        }
        
        addGraphicAttribute(graph);

        return graph;
    }
    
    private void addGraphicAttribute(Graph graph) {
        for (Node node : graph.getNodes()) {
            if (!node.containsAttribute(GraphicAttributeConstants.GRAPHICS)) {
                NodeGraphicAttribute nga = new NodeGraphicAttribute();
                if (randomizeCoordinates) {
                    nga.getCoordinate().setCoordinate(
                            new Point2D.Double(random.nextDouble() * SPREAD,
                                    random.nextDouble() * SPREAD));
                }
                node.addAttribute(nga, "");
            }
        }

        for (Edge edge : graph.getEdges()) {
            if (!edge.containsAttribute(GraphicAttributeConstants.GRAPHICS)) {
                EdgeGraphicAttribute ega = new EdgeGraphicAttribute();
                edge.addAttribute(ega, "");
            }
        }

        if (!graph.containsAttribute(GraphicAttributeConstants.GRAPHICS)) {
            graph.addAttribute(new GraphGraphicAttribute(), "");
        }
    }
    
    private int drawFromSet(Set<Integer> set) {
        if (set.size() == 0) throw new IllegalArgumentException();
        
        int i = random.nextInt(set.size());
        Iterator<Integer> iter = set.iterator();
        int element = iter.next();
        
        for (int j = 0; j < i; j++) {
            element = iter.next();
        }
        
        return element;
    }
}
