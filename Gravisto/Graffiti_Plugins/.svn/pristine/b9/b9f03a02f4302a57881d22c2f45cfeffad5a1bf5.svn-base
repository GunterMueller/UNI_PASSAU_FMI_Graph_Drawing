package org.graffiti.plugins.algorithms.labeling.labelGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

/**
 * Generates labels for a given graph randomly.
 * 
 * @author scholz
 */
public class RandomLabelGenerator extends AbstractAlgorithm {

    /** randomizes where to place labels */
    private Random placeRandom;
    private long placeRandomSeed;
    private IntegerParameter placeRandomSeedParam;
    /** randomizes the length of names */
    private Random lengthRandom;
    private long lengthRandomSeed;
    private IntegerParameter lengthRandomSeedParam;
    /** randomizes letters in names */
    private Random letterRandom;
    private long letterRandomSeed;
    private IntegerParameter letterRandomSeedParam;

    private int minLabelLength = 1;
    private IntegerParameter minLabelLengthParam;
    private int maxLabelLength = 3;
    private IntegerParameter maxLabelLengthParam;

    private int numNodeLabels = 1;
    private IntegerParameter numNodeLabelsParam;
    private int numNodeLabelsRandom = 0;
    private IntegerParameter numNodeLabelsRandomParam;
    private String nodeLabelPrefix = "node";
    private StringParameter nodeLabelPrefixParam;

    private int numEdgeLabels = 1;
    private IntegerParameter numEdgeLabelsParam;
    private int numEdgeLabelsRandom = 0;
    private IntegerParameter numEdgeLabelsRandomParam;
    private String edgeLabelPrefix = "edge";
    private StringParameter edgeLabelPrefixParam;

    public RandomLabelGenerator() {
        this.numNodeLabelsParam = new IntegerParameter(numNodeLabels,
                "number of labels per node",
                "constant number of labels generated for every node", 0, 4, 0,
                Integer.MAX_VALUE);
        this.numNodeLabelsRandomParam = new IntegerParameter(
                numNodeLabelsRandom, "additional random node labels",
                "number of additionally generated node labels", 0, 50, 0,
                Integer.MAX_VALUE);
        this.nodeLabelPrefixParam = new StringParameter(nodeLabelPrefix,
                "nodel label prefix",
                "first letters of every generated node label");

        this.numEdgeLabelsParam = new IntegerParameter(numEdgeLabels,
                "number of labels per edge",
                "constant number of labels generated for every edge", 0, 4, 0,
                Integer.MAX_VALUE);
        this.numEdgeLabelsRandomParam = new IntegerParameter(
                numEdgeLabelsRandom, "additional random edge labels",
                "number of additionally generated edge labels", 0, 50, 0,
                Integer.MAX_VALUE);
        this.edgeLabelPrefixParam = new StringParameter(edgeLabelPrefix,
                "edge label prefix",
                "first letters of every generated edge label");

        this.minLabelLengthParam = new IntegerParameter(minLabelLength,
                "min label length", "minimum length of labels in characters",
                0, 40, 0, Integer.MAX_VALUE);
        this.maxLabelLengthParam = new IntegerParameter(maxLabelLength,
                "max label length", "maximum length of labels in characters",
                0, 40, 0, Integer.MAX_VALUE);

        this.placeRandomSeedParam = new IntegerParameter(0, "placement seed",
                "initialization for the pseudorandom generator which places "
                        + "random labels", 0, 100, Integer.MIN_VALUE,
                Integer.MAX_VALUE);
        this.lengthRandomSeedParam = new IntegerParameter(0,
                "label length seed",
                "initialization for the pseudorandom generator which determines "
                        + "label lengths", 0, 100, Integer.MIN_VALUE,
                Integer.MAX_VALUE);
        this.letterRandomSeedParam = new IntegerParameter(0, "letter seed",
                "initialization for the pseudorandom generator which determines "
                        + "characters of names", 0, 100, Integer.MIN_VALUE,
                Integer.MAX_VALUE);
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new Parameter[] { numNodeLabelsParam, numNodeLabelsRandomParam,
                nodeLabelPrefixParam, numEdgeLabelsParam,
                numEdgeLabelsRandomParam, edgeLabelPrefixParam,
                minLabelLengthParam, maxLabelLengthParam, placeRandomSeedParam,
                lengthRandomSeedParam, letterRandomSeedParam };
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;

        int numbersPos = 0;
        numNodeLabels = ((IntegerParameter) params[numbersPos]).getInteger()
                .intValue();
        numNodeLabelsRandom = ((IntegerParameter) params[numbersPos + 1])
                .getInteger().intValue();
        nodeLabelPrefix = ((StringParameter) params[numbersPos + 2])
                .getString();
        numEdgeLabels = ((IntegerParameter) params[numbersPos + 3])
                .getInteger().intValue();
        numEdgeLabelsRandom = ((IntegerParameter) params[numbersPos + 4])
                .getInteger().intValue();
        edgeLabelPrefix = ((StringParameter) params[numbersPos + 5])
                .getString();

        int lengthParamsPos = numbersPos + 6;
        minLabelLength = ((IntegerParameter) params[lengthParamsPos])
                .getInteger().intValue();
        maxLabelLength = ((IntegerParameter) params[lengthParamsPos + 1])
                .getInteger().intValue();

        int seedParamsPos = lengthParamsPos + 2;
        placeRandomSeed = ((IntegerParameter) params[seedParamsPos])
                .getInteger().intValue();
        lengthRandomSeed = ((IntegerParameter) params[seedParamsPos + 1])
                .getInteger().intValue();
        letterRandomSeed = ((IntegerParameter) params[seedParamsPos + 2])
                .getInteger().intValue();

    }

    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        // The graph is inherited from AbstractAlgorithm.
        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (minLabelLength > maxLabelLength) {
            errors.add("minimum label length has to be smaller "
                    + "than maximum label length (momentarily "
                    + minLabelLength + " and " + maxLabelLength + ").");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    public void execute() {
        // Initialize number generators
        placeRandom = new Random(placeRandomSeed);
        lengthRandom = new Random(lengthRandomSeed);
        letterRandom = new Random(letterRandomSeed);

        // Step 1: generate node labels
        for (Node node : this.graph.getNodes()) {
            for (int i = 0; i < numNodeLabels; i++) {
                addLabel(node, nodeLabelPrefix
                        + generateName(minLabelLength, maxLabelLength,
                                lengthRandom, letterRandom));
            }
        }
        {
            int numNodes = this.graph.getNodes().size();
            Node node;
            List<Node> graphNodes = this.graph.getNodes();
            for (int i = 0; i < numNodeLabelsRandom; i++) {
                node = graphNodes.get(placeRandom.nextInt(numNodes));
                addLabel(node, nodeLabelPrefix
                        + generateName(minLabelLength, maxLabelLength,
                                lengthRandom, letterRandom));
            }
        }

        // Step 2: generate edge labels
        for (Edge edge : this.graph.getEdges()) {
            for (int i = 0; i < numEdgeLabels; i++) {
                addLabel(edge, edgeLabelPrefix
                        + generateName(minLabelLength, maxLabelLength,
                                lengthRandom, letterRandom));
            }
        }
        {
            int numEdges = this.graph.getEdges().size();
            Edge edge;
            ArrayList<Edge> graphEdges = new ArrayList<Edge>(this.graph
                    .getEdges());
            for (int i = 0; i < numEdgeLabelsRandom; i++) {
                edge = graphEdges.get(placeRandom.nextInt(numEdges));
                addLabel(edge, edgeLabelPrefix
                        + generateName(minLabelLength, maxLabelLength,
                                lengthRandom, letterRandom));
            }
        }
    }

    public String getName() {
        return "Random label generator";
    }

    public static void addLabel(Node node, String label) {
        // Generate label, but don't overwrite old ones
        String labelName;
        int i = 0;
        boolean isLabelExisting;
        do {
            labelName = "label" + String.valueOf(i);
            i++;
            isLabelExisting = true;
            try {
                node.getAttribute(labelName);
            } catch (AttributeNotFoundException e) {
                isLabelExisting = false;
            }
        } while (isLabelExisting);

        NodeLabelAttribute labelAttribute = new NodeLabelAttribute(labelName,
                label);
        node.addAttribute(labelAttribute, "");
    }

    protected void addLabel(Edge edge, String label) {
        // Generate label, but don't overwrite old ones
        String labelName;
        int i = 0;
        boolean isLabelExisting;
        do {
            labelName = "label" + String.valueOf(i);
            i++;
            isLabelExisting = true;
            try {
                edge.getAttribute(labelName);
            } catch (AttributeNotFoundException e) {
                isLabelExisting = false;
            }
        } while (isLabelExisting);

        EdgeLabelAttribute labelAttribute = new EdgeLabelAttribute(labelName,
                label);
        edge.addAttribute(labelAttribute, "");
    }

    /**
     * generates several random letters
     */
    public static String generateName(int minLength, int maxLength,
            Random lengthRandom, Random letterRandom) {
        assert (minLength <= maxLength);

        String name = "";
        int length = minLength
                + lengthRandom.nextInt(maxLength + 1 - minLength);
        for (int i = 0; i < length; i++) {
            name = name + generateRandomChar(letterRandom);
        }

        return name;
    }

    /**
     * generates a single random letter
     * <p>
     * Momentarily, only digits are generated.
     */
    public static char generateRandomChar(Random letterRandom) {
        return Character.forDigit(letterRandom.nextInt(9), 10);
    }

}
