// =============================================================================
//
//   Experiment.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling.test;

import java.util.StringTokenizer;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.cyclicLeveling.AbstractCyclicLeveling;
import org.graffiti.plugins.algorithms.cyclicLeveling.CyclicBFSLeveling;
import org.graffiti.plugins.algorithms.cyclicLeveling.CyclicMSTLeveling;
import org.graffiti.plugins.algorithms.cyclicLeveling.CyclicSELeveling;
import org.graffiti.plugins.algorithms.cyclicLeveling.OptimalLeveling;

/**
 * Contains the results of an experiment
 * 
 * @author Gerg�
 * @version $Revision$ $Date$
 */
public class Experiment {

    /**
     * the current algorithm
     */
    private AbstractCyclicLeveling algorithm;

    /**
     * the name of the algorithm as defined in Config
     */
    private String algorithmName;

    /**
     * the current graph
     */
    private Graph graph;

    /**
     * the ID of the graph
     */
    private String graphID;

    /**
     * the number of levels
     */
    private int levels;

    /**
     * the number of nodes on each level
     */
    private int width;

    /**
     * Constructor
     * 
     * @param graph
     *            the name of the graph
     * @param algorithm
     *            the name of the Algorithm
     * @param levels
     *            number of levels
     * @param width
     *            the width of a level
     */
    public Experiment(String graph, String algorithm, int levels, int width) {

        this.graphID = graph;

        this.graph = GraphIO.loadGraph(graph);

        this.algorithmName = algorithm;

        StringTokenizer tokenizer = new StringTokenizer(algorithm, "_");
        String algName = tokenizer.nextToken();

        if (algName.equals("BFS")) {
            this.algorithm = CyclicBFSLeveling.getInstance(algorithm, levels,
                    width);
        } else if (algName.equals("MST")) {
            this.algorithm = CyclicMSTLeveling.getInstance(algorithm, levels,
                    width);
        } else if (algName.equals("OPTIMAL")) {
            this.algorithm = OptimalLeveling.getInstance(algorithm, levels,
                    width);
        } else if (algName.equals("SE")) {
            this.algorithm = CyclicSELeveling.getInstance(algorithm, levels,
                    width);
        }

        this.levels = levels;

        this.width = width;
    }

    /**
     * Constructor
     * 
     * @param graph
     *            the name of the graph
     * @param algorithm
     *            the name of the Algorithm
     * @param levels
     *            number of levels
     * @param width
     *            the width of a level
     */
    public Experiment(String graphID, Graph graph, String algorithm,
            int levels, int width) {

        this.graphID = graphID;

        this.graph = graph;

        this.algorithmName = algorithm;

        StringTokenizer tokenizer = new StringTokenizer(algorithm, "_");
        String algName = tokenizer.nextToken();

        if (algName.equals("BFS")) {
            this.algorithm = CyclicBFSLeveling.getInstance(algorithm, levels,
                    width);
        } else if (algName.equals("MST")) {
            this.algorithm = CyclicMSTLeveling.getInstance(algorithm, levels,
                    width);
        } else if (algName.equals("OPTIMAL")) {
            this.algorithm = OptimalLeveling.getInstance(algorithm, levels,
                    width);
        } else if (algName.equals("SE")) {
            this.algorithm = CyclicSELeveling.getInstance(algorithm, levels,
                    width);
        }

        this.levels = levels;

        this.width = width;
    }

    public ExperimentBean execute(Node sourceNode) {

        ExperimentBean eB = new ExperimentBean();

        algorithm.attach(graph);
        try {
            algorithm.check();
        } catch (PreconditionException e) {
            e.printStackTrace();
        }

        if (sourceNode != null) {
            algorithm.setSourceNode(sourceNode);
        }

        long time = algorithm.computeLevels();

        eB.setAlgorithm(algorithmName);
        eB.setGraphID(graphID);
        eB.setNumberOfEdges(graph.getNumberOfEdges());
        eB.setNumberOfLevels(levels);
        eB.setNumberOfNodes(graph.getNumberOfNodes());
        if (sourceNode != null) {
            eB.setSourceNode(sourceNode.getString("label.label"));
        }
        eB.setSumOfEdges(algorithm.lengthOfEdges());
        eB.setTime(time);
        eB.setWidth(width);
        eB.setSpec1("0");

        /* optimal leveling */
        if (algorithmName.equals(Config.OPTIMAL)) {
            eB.setSpec1(Long.toString(((OptimalLeveling) this.algorithm)
                    .getNumberOfRecursions()));
            /* MST leveling */
        } else {
            eB.setSpec1(Integer.toString(algorithm.getSourceInDegree()));
            eB.setSpec2(Integer.toString(algorithm.getSourceOutDegree()));
        }

        return eB;
    }

    public static void main(String[] args) {

        // runAll();
        // runOptimal();
        // runPaper();
        // runOptimal();

    }

    // private static void runPaper() {
    // /* establish the connection */
    // Connector conn = new Connector();
    // conn.createConnection();
    //
    // String[] algorithms = {Config.MST_MAX, Config.MST_MIN, Config.SE_MST,
    // Config.SE_RANDOM, Config.BFS};
    //        
    // Experiment exp = null;
    // ExperimentBean expBean = null;
    //   
    // String fileName = "";
    // Graph g = null;
    // int levels = 0;
    // int width = 0;
    // int limit = 10;
    // Iterator<Node> it;
    // Iterator<String> fileIt = new PaperIterator();
    //        
    // while(fileIt.hasNext()) {
    // fileName = fileIt.next();
    // g = GraphIO.loadGraph(fileName);
    //         
    // limit = 10;
    // if(g.getNumberOfNodes() <= 20) {
    // limit = g.getNumberOfNodes();
    // }
    //            
    // /* sqrt(n)sqrt(2) | sqrt(n)sqrt(2) */
    // levels = (int)Math.ceil((Math.sqrt(g.getNumberOfNodes()) *
    // Math.sqrt(2)));
    // width = levels;
    //            
    // for (int i = 0; i < algorithms.length; i++)
    // {
    // exp = new Experiment(fileName, g, algorithms[i], levels, width);
    // it = exp.graph.getNodesIterator();
    // int counter = 0;
    // while (it.hasNext() && counter < limit)
    // {
    // Node source = it.next();
    //                    
    // if (!conn.inDatabase(fileName, algorithms[i], levels, width,
    // source.getString("label.label")))
    // {
    // expBean = exp.execute(source);
    // conn.addResults(expBean);
    // }
    // counter++;
    // }
    // }
    //            
    //          
    // 
    //
    //            
    // System.out.println(fileName + " fertig");
    // }
    // }

    // private static void runAll() {
    // /* establish the connection */
    // Connector conn = new Connector();
    // conn.createConnection();
    //
    // String[] algorithms = {Config.MST_MAXA, Config.MST_MAXA_QUAD,
    // Config.MST_MIN, Config.MST_MIN_QUAD, Config.SE_MST, Config.SE_RANDOM};
    //        
    // Experiment exp = null;
    // ExperimentBean expBean = null;
    //   
    // String fileName = "";
    // Graph g = null;
    // int levels = 0;
    // int width = 0;
    // int limit = 10;
    // Iterator<Node> it;
    // Iterator<String> fileIt = new TestdataIterator();
    //        
    // while(fileIt.hasNext()) {
    // fileName = fileIt.next();
    // g = GraphIO.loadGraph(fileName);
    //         
    // limit = 10;
    // if(g.getNumberOfNodes() <= 20) {
    // limit = g.getNumberOfNodes();
    // }
    //            
    //            
    // levels = (int)Math.ceil(Math.sqrt(g.getNumberOfNodes())*2);
    // width = (int)Math.ceil(Math.sqrt(g.getNumberOfNodes())/2);
    //            
    // for (int i = 0; i < algorithms.length; i++)
    // {
    // exp = new Experiment(fileName, g, algorithms[i], levels, width);
    // it = exp.graph.getNodesIterator();
    // int counter = 0;
    // while (it.hasNext() && counter < limit)
    // {
    // Node source = it.next();
    //                    
    // if (!conn.inDatabase(fileName, algorithms[i], levels, width,
    // source.getString("label.label")))
    // {
    // expBean = exp.execute(source);
    // conn.addResults(expBean);
    // }
    // counter++;
    // }
    // }
    //
    //            
    // levels = (int)Math.ceil(Math.sqrt(g.getNumberOfNodes())/2);
    // width = (int)Math.ceil(Math.sqrt(g.getNumberOfNodes())*2);
    //            
    // for (int i = 0; i < algorithms.length; i++)
    // {
    // exp = new Experiment(fileName, g, algorithms[i], levels, width);
    // it = exp.graph.getNodesIterator();
    // int counter = 0;
    // while (it.hasNext() && counter < limit)
    // {
    // Node source = it.next();
    //                    
    // if (!conn.inDatabase(fileName, algorithms[i], levels, width,
    // source.getString("label.label")))
    // {
    // expBean = exp.execute(source);
    // conn.addResults(expBean);
    // }
    // counter++;
    // }
    // }
    //            
    // /* sqrt(n) | sqrt(n) */
    // levels = (int)Math.ceil(Math.sqrt(g.getNumberOfNodes()));
    // width = levels;
    //            
    // for (int i = 0; i < algorithms.length; i++)
    // {
    // exp = new Experiment(fileName, g, algorithms[i], levels, width);
    // it = exp.graph.getNodesIterator();
    // int counter = 0;
    // while (it.hasNext() && counter < limit)
    // {
    // Node source = it.next();
    //                    
    // if (!conn.inDatabase(fileName, algorithms[i], levels, width,
    // source.getString("label.label")))
    // {
    // expBean = exp.execute(source);
    // conn.addResults(expBean);
    // }
    // counter++;
    // }
    // }
    //            
    // /* sqrt(n)sqrt(2) | sqrt(n)sqrt(2) */
    // levels = (int)Math.ceil((Math.sqrt(g.getNumberOfNodes()) *
    // Math.sqrt(2)));
    // width = levels;
    //            
    // for (int i = 0; i < algorithms.length; i++)
    // {
    // exp = new Experiment(fileName, g, algorithms[i], levels, width);
    // it = exp.graph.getNodesIterator();
    // int counter = 0;
    // while (it.hasNext() && counter < limit)
    // {
    // Node source = it.next();
    //                    
    // if (!conn.inDatabase(fileName, algorithms[i], levels, width,
    // source.getString("label.label")))
    // {
    // expBean = exp.execute(source);
    // conn.addResults(expBean);
    // }
    // counter++;
    // }
    // }
    //            
    // levels = (int)Math.ceil(Math.sqrt(g.getNumberOfNodes()));
    // width = (int)Math.ceil(Math.sqrt(g.getNumberOfNodes())*2);
    //            
    // for (int i = 0; i < algorithms.length; i++)
    // {
    // exp = new Experiment(fileName, g, algorithms[i], levels, width);
    // it = exp.graph.getNodesIterator();
    // int counter = 0;
    // while (it.hasNext() && counter < limit)
    // {
    // Node source = it.next();
    //                    
    // if (!conn.inDatabase(fileName, algorithms[i], levels, width,
    // source.getString("label.label")))
    // {
    // expBean = exp.execute(source);
    // conn.addResults(expBean);
    // }
    // counter++;
    // }
    // }
    //
    //            
    // levels = (int)Math.ceil(Math.sqrt(g.getNumberOfNodes())*2);
    // width = (int)Math.ceil(Math.sqrt(g.getNumberOfNodes()));
    //            
    // for (int i = 0; i < algorithms.length; i++)
    // {
    // exp = new Experiment(fileName, g, algorithms[i], levels, width);
    // it = exp.graph.getNodesIterator();
    // int counter = 0;
    // while (it.hasNext() && counter < limit)
    // {
    // Node source = it.next();
    //                    
    // if (!conn.inDatabase(fileName, algorithms[i], levels, width,
    // source.getString("label.label")))
    // {
    // expBean = exp.execute(source);
    // conn.addResults(expBean);
    // }
    // counter++;
    // }
    // }
    //            
    // levels = (int)Math.ceil(Math.sqrt(g.getNumberOfNodes())/2);
    // width = g.getNumberOfNodes();
    //            
    // for (int i = 0; i < algorithms.length; i++)
    // {
    // exp = new Experiment(fileName, g, algorithms[i], levels, width);
    // it = exp.graph.getNodesIterator();
    // int counter = 0;
    // while (it.hasNext() && counter < limit)
    // {
    // Node source = it.next();
    //                    
    // if (!conn.inDatabase(fileName, algorithms[i], levels, width,
    // source.getString("label.label")))
    // {
    // expBean = exp.execute(source);
    // conn.addResults(expBean);
    // }
    // counter++;
    // }
    // }
    //            
    //
    // levels = (int)Math.ceil(Math.sqrt(g.getNumberOfNodes()));
    // width = g.getNumberOfNodes();
    //            
    // for (int i = 0; i < algorithms.length; i++)
    // {
    // exp = new Experiment(fileName, g, algorithms[i], levels, width);
    // it = exp.graph.getNodesIterator();
    // int counter = 0;
    // while (it.hasNext() && counter < limit)
    // {
    // Node source = it.next();
    //                    
    // if (!conn.inDatabase(fileName, algorithms[i], levels, width,
    // source.getString("label.label")))
    // {
    // expBean = exp.execute(source);
    // conn.addResults(expBean);
    // }
    // counter++;
    // }
    // }
    //
    //
    // levels = (int)Math.ceil(Math.sqrt(g.getNumberOfNodes())*2);
    // width = g.getNumberOfNodes();
    //            
    // for (int i = 0; i < algorithms.length; i++)
    // {
    // exp = new Experiment(fileName, g, algorithms[i], levels, width);
    // it = exp.graph.getNodesIterator();
    // int counter = 0;
    // while (it.hasNext() && counter < limit)
    // {
    // Node source = it.next();
    //                    
    // if (!conn.inDatabase(fileName, algorithms[i], levels, width,
    // source.getString("label.label")))
    // {
    // expBean = exp.execute(source);
    // conn.addResults(expBean);
    // }
    // counter++;
    // }
    // }
    //            
    // 
    //
    //            
    // System.out.println(fileName + " fertig");
    // }
    // }

    // private static void runOptimal() {
    //        
    // /* establish the connection */
    // Connector conn = new Connector();
    // conn.createConnection();
    //           
    // Experiment exp = null;
    // ExperimentBean expBean = null;
    //   
    // String fileName = "";
    // Graph g = null;
    // int levels = 0;
    // int width = 0;
    // <<<<<<< .mine
    //        
    // Iterator<String> fileIt = new OptimalIterator(14);
    //        
    // while(fileIt.hasNext()) {
    // fileName = fileIt.next();
    // =======
    //        
    // Iterator<String> fileIt = new OptimalIterator(20);
    //        
    // while(fileIt.hasNext()) {
    // fileName = fileIt.next();
    // >>>>>>> .r2519
    //
    //            
    // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName))/2);
    // width = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName))*2);
    //            
    // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // {
    // <<<<<<< .mine
    // g = GraphIO.loadGraph(fileName);
    // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // expBean = exp.execute(null);
    // conn.createConnection();
    // conn.addResults(expBean);
    // =======
    // g = GraphIO.loadGraph(fileName);
    // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // expBean = exp.execute(null);
    // conn.addResults(expBean);
    // >>>>>>> .r2519
    // }
    // <<<<<<< .mine
    //            
    // // /* sqrt(n) | sqrt(n) */
    // // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName)));
    // // width = levels;
    // //
    // // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // // {
    // // g = GraphIO.loadGraph(fileName);
    // // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // // expBean = exp.execute(null);
    // // conn.createConnection();
    // // conn.addResults(expBean);
    // // }
    //            
    // /* sqrt(n)sqrt(2) | sqrt(n)sqrt(2) */
    // levels = (int)Math.ceil((Math.sqrt(getNumberOfNodes(fileName)) *
    // Math.sqrt(2)));
    // =======
    //            
    // /* sqrt(n) | sqrt(n) */
    // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName)));
    // width = levels;
    //            
    // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // {
    // g = GraphIO.loadGraph(fileName);
    // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // expBean = exp.execute(null);
    // conn.addResults(expBean);
    // }
    //            
    // /* sqrt(n)sqrt(2) | sqrt(n)sqrt(2) */
    // levels = (int)Math.ceil((Math.sqrt(getNumberOfNodes(fileName)) *
    // Math.sqrt(2)));
    // >>>>>>> .r2519
    // width = levels;
    //            
    // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // {
    // <<<<<<< .mine
    // g = GraphIO.loadGraph(fileName);
    // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // expBean = exp.execute(null);
    // conn.createConnection();
    // conn.addResults(expBean);
    // =======
    // g = GraphIO.loadGraph(fileName);
    // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // expBean = exp.execute(null);
    // conn.addResults(expBean);
    // >>>>>>> .r2519
    // }
    // <<<<<<< .mine
    // //
    // // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName)));
    // // width = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName))*2);
    // //
    // // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // // {
    // // g = GraphIO.loadGraph(fileName);
    // // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // // expBean = exp.execute(null);
    // // conn.createConnection();
    // // conn.addResults(expBean);
    // // }
    // //
    // //
    // // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName))*2);
    // // width = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName)));
    // //
    // // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // // {
    // // g = GraphIO.loadGraph(fileName);
    // // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // // expBean = exp.execute(null);
    // // conn.createConnection();
    // // conn.addResults(expBean);
    // // }
    // //
    // // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName))/2);
    // // width = getNumberOfNodes(fileName);
    // //
    // // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // // {
    // // g = GraphIO.loadGraph(fileName);
    // // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // // expBean = exp.execute(null);
    // // conn.createConnection();
    // // conn.addResults(expBean);
    // // }
    // //
    // //
    // // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName)));
    // // width = getNumberOfNodes(fileName);
    // //
    // // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // // {
    // // g = GraphIO.loadGraph(fileName);
    // // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // // expBean = exp.execute(null);
    // // conn.createConnection();
    // // conn.addResults(expBean);
    // // }
    // //
    // //
    // // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName))*2);
    // // width = getNumberOfNodes(fileName);
    // //
    // // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // // {
    // // g = GraphIO.loadGraph(fileName);
    // // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // // expBean = exp.execute(null);
    // // conn.createConnection();
    // // conn.addResults(expBean);
    // // }
    //            
    //            
    // System.out.println(fileName + " fertig (Optimal)");
    // }
    // =======
    //            
    // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName)));
    // width = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName))*2);
    //            
    // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // {
    // g = GraphIO.loadGraph(fileName);
    // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // expBean = exp.execute(null);
    // conn.addResults(expBean);
    // }
    //
    //            
    // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName))*2);
    // width = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName)));
    //            
    // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // {
    // g = GraphIO.loadGraph(fileName);
    // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // expBean = exp.execute(null);
    // conn.addResults(expBean);
    // }
    //            
    // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName))/2);
    // width = getNumberOfNodes(fileName);
    //            
    // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // {
    // g = GraphIO.loadGraph(fileName);
    // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // expBean = exp.execute(null);
    // conn.addResults(expBean);
    // }
    //            
    //
    // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName)));
    // width = getNumberOfNodes(fileName);
    //            
    // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // {
    // g = GraphIO.loadGraph(fileName);
    // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // expBean = exp.execute(null);
    // conn.addResults(expBean);
    // }
    // >>>>>>> .r2519
    // <<<<<<< .mine
    //        
    // =======
    //
    //
    // levels = (int)Math.ceil(Math.sqrt(getNumberOfNodes(fileName))*2);
    // width = getNumberOfNodes(fileName);
    //            
    // if (!conn.inDatabase(fileName, Config.OPTIMAL, levels, width))
    // {
    // g = GraphIO.loadGraph(fileName);
    // exp = new Experiment(fileName, g, Config.OPTIMAL, levels, width);
    // expBean = exp.execute(null);
    // conn.addResults(expBean);
    // }
    //            
    //            
    // System.out.println(fileName + " fertig (Optimal)");
    // }
    //        
    // >>>>>>> .r2519
    // }

    // /**
    // * @param fileName
    // * @return
    // */
    // private static int getNumberOfNodes(String fileName)
    // {
    // StringTokenizer tokenizer = new StringTokenizer(fileName, "_");
    // return Integer.parseInt(tokenizer.nextToken());
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
