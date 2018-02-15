// =============================================================================
//
//   TreeJugglerCLI.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.treeJuggler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.LogManager;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.editor.GraffitiEditor;
import org.graffiti.graph.Edge;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.treedrawings.DAGSplitter.DAGSplitter;
import org.graffiti.plugins.algorithms.treedrawings.TreeKNaryMaker.HelperNodeStripper;
import org.graffiti.plugins.algorithms.treedrawings.TreeKNaryMaker.TreeKNaryMaker;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.LayoutComposition;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.LayoutRefresher;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.sorter.SubtreeSorter;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.tipover_mod2.TipoverLayoutMod2;
import org.graffiti.plugins.ios.exporters.graphics.PdfSerializer;
import org.graffiti.plugins.ios.exporters.graphics.PngSerializer;
import org.graffiti.plugins.ios.exporters.graphml.GraphMLWriter;
import org.graffiti.plugins.ios.importers.dot.DotReader;

;

/**
 * @author Andreas
 * @version $Revision$ $Date$
 */
public class TreeJugglerCLI {
    private static HashMap<String, String> parameters = new HashMap<String, String>();

    public static void main(String[] argv) {

        setParameters(argv);

        setLogging();

        Graph graph = new FastGraph();

        loadGraph(graph);

        cleanGraph(graph);

        addGraphicsAttributes(graph);

        if (parameters.containsKey("a")) {
            setArrowHeads(graph);

        }

        makeTree(graph);

        TipoverLayoutMod2 layouter = new TipoverLayoutMod2();
        layouter.attach(graph);

        double nodeDistance = 0.0;

        try {
            nodeDistance = Double.parseDouble(parameters.get("d"));
        } catch (NullPointerException n) {
            System.err
                    .println("Node distance is not specified. Example: -d 25");
            displayHelp();
            System.exit(1);
        }

        layouter.setNodeDistance(nodeDistance);

        double aspectRatio = 0.0;

        layouter.setCostFunctionName("SIZE_WITH_ASPECT_RATIO");
        try {
            aspectRatio = Double.parseDouble(parameters.get("r"));

        } catch (NullPointerException n) {

            System.err.println("Ascpect ratio not specified. Example: -r 1.41");
            displayHelp();
            System.exit(1);
        }
        layouter.setAdditionalVariable(aspectRatio);

        layouter.setNodesWithDimensions(true);

        try {
            layouter.check();
        } catch (PreconditionException e) {
            System.err
                    .println("There is a problem with the graph data-structure. Please contact Gravisto support.");
            e.printStackTrace();
            System.exit(1);
        }

        layouter.root.setBoolean("layout.isHorizontal", true);
        layouter.root.setBoolean("layout.forceDirection", true);

        TreeKNaryMaker compacter = new TreeKNaryMaker();
        compacter.attach(graph);
        compacter.setDegree(2);
        compacter.setSelectedNodesPolicy(TreeKNaryMaker.NONE_TAKE_ALL);
        compacter.setStrategy(TreeKNaryMaker.BALANCED);

        try {
            compacter.check();
        } catch (PreconditionException e) {
            System.err
                    .println("There is a problem with the graph data-structure. Please contact Gravisto support.");
            e.printStackTrace();
            System.exit(1);
        }

        compacter.execute();

        addGraphicsAttributes(graph);

        layouter.execute();

        SubtreeSorter sorter = new SubtreeSorter();
        sorter.attach(graph);
        try {
            sorter.check();
        } catch (PreconditionException e1) {
            System.err
                    .println("There is a problem with the graph data-structure. Please contact Gravisto support.");
            e1.printStackTrace();
            System.exit(1);
        }

        sorter.execute();

        layouter.execute();

        HelperNodeStripper stripper = new HelperNodeStripper();
        stripper.attach(graph);
        stripper.setStrategy(HelperNodeStripper.REMOVE_UNNECESSARY);
        stripper.setSelectedNodesPolicy(HelperNodeStripper.NONE_TAKE_ALL);

        try {
            stripper.check();
        } catch (PreconditionException e) {
            System.err
                    .println("There is a problem with the graph data-structure. Please contact Gravisto support.");
            e.printStackTrace();
            System.exit(1);
        }

        LayoutComposition current = layouter.getCurrentComposition();
        double currentWidth = 0.0;
        double currentHeight = 0.0;
        do {
            currentWidth = current.getWidth();
            currentHeight = current.getHeight();
            stripper.execute();
            layouter.execute();
            current = layouter.getCurrentComposition();
        } while (Math.abs((current.getWidth() - currentWidth)) > 0.1
                || Math.abs((current.getHeight() - currentHeight)) > 0.1);

        stripper.execute();

        LayoutRefresher layoutRefresher = new LayoutRefresher();

        layoutRefresher.attach(graph);
        try {
            layoutRefresher.check();
        } catch (PreconditionException e) {
            System.err
                    .println("There is a problem with the graph data-structure. Please contact Gravisto support.");
            e.printStackTrace();
            System.exit(1);
        }
        layoutRefresher.execute();

        if (!parameters.containsKey("h")) {
            stripper.setStrategy(HelperNodeStripper.SUBSTITUTE_BY_BENDS);
            stripper.execute();
        }

        saveGraph(graph);
    }

    // private static void sortSubtrees(Comparator comp,
    // LayoutComposition composition)
    // {
    // TreeSet<LayoutComposition> orderedSubtrees = new
    // TreeSet<LayoutComposition>(
    // comp);
    // for (LayoutComposition currentComposition: composition.getSubtrees())
    // {
    // orderedSubtrees.add(currentComposition);
    // }
    //
    // for (LayoutComposition currentComposition: orderedSubtrees)
    // {
    // Node currentRoot = currentComposition.getRoot();
    // DoubleAttribute orderNumberAttribute = (DoubleAttribute)currentRoot
    // .getAttribute("layout.orderNumber");
    // orderNumberAttribute
    // .setValue((double)LayoutComposition.orderSequenceNumber++);
    // sortSubtrees(comp, currentComposition);
    // }
    // }

    /**
     * @param graph
     */
    private static void saveGraph(Graph graph) {

        String outputFilename = parameters.get("o");

        if (outputFilename == null) {
            System.err.println("Output file not specified.");
            displayHelp();
            System.exit(1);
        }

        if (outputFilename.toLowerCase().endsWith(".graphml")) {
            saveToGraphML(outputFilename, graph);
        } else if (outputFilename.toLowerCase().endsWith(".pdf")) {
            saveToPDF(outputFilename, graph);
        } else if (outputFilename.toLowerCase().endsWith(".png")) {
            saveToPNG(outputFilename, graph);
        } else {
            System.err
                    .println("File for output must be one of: .graphml, .pdf or .png");
            displayHelp();
            System.exit(1);
        }

    }

    /**
     * @param graph
     */
    private static void makeTree(Graph graph) {
        DAGSplitter dagSplitter = new DAGSplitter();
        dagSplitter
                .setDuplicationStrategy(DAGSplitter.ONLY_ONE_DUPLICATE_WITH_SUBTREE);

        boolean doColouring = false;
        if (parameters.containsKey("c")) {
            doColouring = true;
        }
        dagSplitter.setColouring(doColouring);
        dagSplitter.attach(graph);
        try {
            dagSplitter.check();
        }

        catch (PreconditionException e) {
            System.err
                    .println("There is a problem with the graph data-structure. Please contact Gravisto support.");
            e.printStackTrace();
        }

        dagSplitter.execute();
    }

    /**
     * @param graph
     */
    private static void setArrowHeads(Graph graph) {
        // Set the arrowhead...
        for (Edge currentEdge : graph.getEdges()) {
            ((StringAttribute) currentEdge.getAttribute("graphics.arrowhead"))
                    .setString("org.graffiti.plugins.views.defaults.StandardArrowShape");
        }
    }

    /**
     * @param graph
     */
    private static void loadGraph(Graph graph) {
        DotReader dotReader = new DotReader();

        String inputFilename = parameters.get("i");

        if (inputFilename == null) {
            System.err.println("Input file not specified.");
            displayHelp();
            System.exit(1);
        }

        try {
            dotReader.read(new FileInputStream(new File(inputFilename)), graph);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Input file \"" + inputFilename
                    + "\" not found.");
        } catch (IOException e) {
            throw new RuntimeException("File \"" + inputFilename
                    + "\" could not be read.");
        }
    }

    /**
     * 
     */
    private static void setLogging() {
        // reading the logging config file
        try {
            LogManager.getLogManager().readConfiguration(
                    GraffitiEditor.class
                            .getResourceAsStream("Logging.properties"));
        } catch (IOException e) {
            System.err
                    .println("Could not read Logging.properties. Please contact Gravisto support.");
            e.printStackTrace();
        }
    }

    /**
     * @param argv
     */
    private static void setParameters(String[] argv) {
        String currentParam = null;

        for (int i = 0; i < argv.length; i++) {
            if (argv[i].startsWith("-")) {
                currentParam = argv[i].substring(1);
                parameters.put(currentParam, "");
            } else {
                parameters.put(currentParam, argv[i]);
            }
        }
    }

    private static void saveToGraphML(String fileName, Graph graph) {
        GraphMLWriter graphMLWriter = new GraphMLWriter();
        try {
            graphMLWriter
                    .write(new FileOutputStream(new File(fileName)), graph);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void saveToPNG(String fileName, Graph graph) {
        PngSerializer pngSerializer = new PngSerializer();
        try {
            pngSerializer
                    .write(new FileOutputStream(new File(fileName)), graph);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void saveToPDF(String fileName, Graph graph) {
        PdfSerializer pdfSerializer = new PdfSerializer();
        try {
            pdfSerializer
                    .write(new FileOutputStream(new File(fileName)), graph);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("File not found: " + fileName);
            System.exit(1);
        }

        System.out.println("Output file " + fileName + " generated");
    }

    private static void addGraphicsAttributes(Graph graph) {
        for (Node currentNode : graph.getNodes()) {
            try {
                currentNode.getAttribute(GraphicAttributeConstants.GRAPHICS);
            } catch (AttributeNotFoundException e) {
                currentNode.addAttribute(new NodeGraphicAttribute(), "");
            }
        }

        for (Edge currentEdge : graph.getEdges()) {
            try {
                currentEdge.getAttribute(GraphicAttributeConstants.GRAPHICS);
            } catch (AttributeNotFoundException e) {
                currentEdge.addAttribute(new EdgeGraphicAttribute(), "");
            }

        }
    }

    private static void cleanGraph(Graph graph) {
        List<Node> allNodes = graph.getNodes();

        // make a copy...
        LinkedList<Node> allNodesCopy = new LinkedList<Node>();
        for (Node currentNode : allNodes) {
            allNodesCopy.add(currentNode);
        }

        for (Node currentNode : allNodesCopy) {
            String label = "";

            try {
                NodeLabelAttribute labelAttr = (NodeLabelAttribute) currentNode
                        .getAttribute(GraphicAttributeConstants.LABEL);
                label = labelAttr.getLabel();

            } catch (AttributeNotFoundException anfe) {
            }

            if (label.equals("")) {
                graph.deleteNode(currentNode);
            }

        }
    }

    private static void displayHelp() {
        System.out
                .println("\n\nExample Syntax: TreeJugglerCLI -i example.dot -o example.pdf -d 25 -r 1.41 -c\n\n"
                        + "Options (The ones marked with an asterisk are mandatory):\n"
                        + "-i: To specify the DOT input file (*).\n"
                        + "-o: Specify the .graphml, .pdf or .png output file (*).\n"
                        + "-d: To specify the node distance option (*).\n"
                        + "-r: Specify the aspect ratio of width and height (*).\n"
                        + "-c: Whether to colour the duplicated nodes.\n"
                        + "-a: Draw arrows.\n"
                        + "-h: Do not substitute HelperNodes by bends.\n");

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
