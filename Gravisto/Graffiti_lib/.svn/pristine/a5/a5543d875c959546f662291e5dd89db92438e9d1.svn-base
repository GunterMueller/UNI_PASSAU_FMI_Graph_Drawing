// =============================================================================
//
//   Experiment.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.core.Bundle;
import org.graffiti.graph.Graph;
import org.graffiti.plugins.tools.benchmark.body.Sequence;
import org.graffiti.plugins.tools.benchmark.body.TerminalElement;
import org.graffiti.plugins.tools.benchmark.constraint.Constraint;
import org.graffiti.plugins.tools.benchmark.output.BenchmarkOutput;
import org.graffiti.plugins.tools.benchmark.source.AbstractGraphSource;
import org.graffiti.plugins.tools.benchmark.source.CompositeSource;
import org.graffiti.plugins.tools.benchmark.source.GraphFactory;
import org.graffiti.plugins.tools.benchmark.xml.BenchmarkBuilder;
import org.graffiti.plugins.tools.benchmark.xml.FormatException;
import org.graffiti.plugins.tools.math.Permutation;

/**
 * Represents a benchmark for evaluating graph algorithms.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class Benchmark implements Seedable {
    /**
     * Denotes if the benchmark framework uses bytecode patches for accurate
     * timer measures.
     */
    public static final boolean IS_PATCHING = false;

    /**
     * Explicit seed for the random number generator. If it is {@code null}, a
     * random seed is used.
     */
    private Long fixedSeed;

    /**
     * Explicit seed for the tiebreaker.
     */
    private Long fixedTiebreakerSeed;

    /**
     * Denotes if the tiebreaker is used.
     */
    private boolean usesTiebreaker;

    /**
     * The source from which the graphs are taken.
     */
    private CompositeSource source;

    /**
     * 
     */
    private Level coreLogging;
    private ErrorPolicy errorPolicy;

    private Assignment assignment;
    private Sequence body;

    private List<BenchmarkOutput> outputs;
    private File outputDirectory;
    private OutputDirectoryPolicy outputDirectoryPolicy;

    public Benchmark() {
        this(new Random().nextLong());
    }

    public Benchmark(long seed) {
        this.fixedSeed = seed;
        fixedTiebreakerSeed = null;
        usesTiebreaker = true;
        source = new CompositeSource();
        coreLogging = Level.WARNING;
        assignment = new Assignment();
        body = new Sequence();
        outputs = new LinkedList<BenchmarkOutput>();
        outputDirectoryPolicy = OutputDirectoryPolicy.STOP;
        errorPolicy = ErrorPolicy.STOP;
    }

    public static String getString(String key, Object... args) {
        return Bundle.getBundle(Benchmark.class).format(key, args);
    }

    /**
     * Performs the benchmark described by the specified file.
     * 
     * @param args
     *            array of arguments. The first argument specifies the path to
     *            the file describing the benchmark to perform.
     */
    public static void main(String[] args) {
        boolean clearDirectory = args.length > 1 && args[0].equals("--clear");
        if (args.length < 1 || args.length > 3 || !clearDirectory
                && args.length == 3) {
            System.err.println("Usage: Benchmark FILE [OUTPUT_DIRECTORY]");
            return;
        }
        int startIndex = clearDirectory ? 1 : 0;

        try {
            BenchmarkBuilder.build(
                    new File(args[startIndex]),
                    args.length == startIndex + 2 ? new File(
                            args[startIndex + 1]) : null, clearDirectory).run();
        } catch (FormatException e) {
            System.err.println(e);
        } catch (BenchmarkException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSource(AbstractGraphSource source) {
        this.source.addSource(source);
    }

    public CompositeSource getRootSource() {
        return source;
    }

    public Sequence getBody() {
        return body;
    }

    public void setLoggingLevel(Level coreLogging) {
        this.coreLogging = coreLogging;
    }

    /**
     * Runs this benchmark.
     */
    public void run() throws BenchmarkException {
        LoggingUtil.push(coreLogging);
        Logger logger = LoggingUtil.getLogger();

        checkOutputDirectory(logger);

        Random random = new Random(fixedSeed);
        logger.info("Global Seed: " + fixedSeed);

        List<GraphFactory> graphs = new LinkedList<GraphFactory>();
        source.contribute(graphs, random.nextLong(), assignment);

        long tiebreakerSeed = random.nextLong();

        int graphCount = graphs.size();
        String indexFormat = "%0"
                + Math.max(1, (int) Math.ceil(Math.log10(graphCount))) + "d";
        assignment.prepare();

        for (BenchmarkOutput output : outputs) {
            output.prepare(graphCount);
        }
        LoggingUtil.begin();

        TerminalElement terminalElement = new TerminalElement(outputs);
        body.setNext(terminalElement);

        int sourceIndex = 0;
        for (GraphFactory graphFactory : graphs) {
            Graph graph = graphFactory.createGraph();
            equipGraph(graph, tiebreakerSeed);
            assignment.resetConfigurationIndex();
            body.updateSeed(random.nextLong());
            body.execute(new Data(graph, String
                    .format(indexFormat, sourceIndex)), assignment);
            sourceIndex++;
        }

        for (BenchmarkOutput output : outputs) {
            output.finish();
        }

        LoggingUtil.pop();
    }

    private void checkOutputDirectory(Logger logger) throws BenchmarkException {
        if (outputDirectory != null && outputDirectory.list().length != 0) {
            switch (outputDirectoryPolicy) {
            case WARNING:
                logger.warning(getString("warning.outputDirectoryNotEmpty"));
                break;
            case STOP:
                throw new BenchmarkException("error.outputDirectoryNotEmpty");
            case CLEAR:
                for (File file : outputDirectory.listFiles()) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
                break;
            default:
                // IGNORE
                break;
            }
        }
    }

    private void equipGraph(Graph graph, long tiebreakerSeed) {

        AttributeUtil.provideBenchmarkAttribute(graph);
        Permutation nodeTiebreaker = new Permutation(graph.getNumberOfNodes());
        Permutation edgeTiebreaker = new Permutation(graph.getNumberOfEdges());
        if (usesTiebreaker) {
            Random random = new Random(
                    fixedTiebreakerSeed == null ? tiebreakerSeed
                            : fixedTiebreakerSeed);
            nodeTiebreaker.shuffle(random);
            edgeTiebreaker.shuffle(random);
        }
        AttributeUtil.addElementAttributes(graph, nodeTiebreaker,
                edgeTiebreaker);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFixedSeed(long fixedSeed) {
        this.fixedSeed = fixedSeed;
    }

    public void setFixedTiebreakerSeed(long fixedTiebreakerSeed) {
        this.fixedTiebreakerSeed = fixedTiebreakerSeed;
    }

    public void setUsesTiebreaker(boolean usesTiebreaker) {
        this.usesTiebreaker = usesTiebreaker;
    }

    public void setOutputDirectory(File outputDirectory) {
        if (outputDirectory != null && !outputDirectory.isDirectory())
            throw new IllegalArgumentException(
                    "The specified path is not a directory");

        this.outputDirectory = outputDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectoryPolicy(OutputDirectoryPolicy policy) {
        this.outputDirectoryPolicy = policy;
    }

    public ErrorPolicy getErrorPolicy() {
        return errorPolicy;
    }

    public void setErrorPolicy(ErrorPolicy policy) {
        this.errorPolicy = policy;
    }

    /**
     * See {@link Assignment#addAlias(String, String)}.
     */
    public void addAlias(String name, String value) {
        assignment.addAlias(name, value);
    }

    /**
     * See {@link Assignment#addVariable(String, Set)}.
     */
    public void addVariable(String name, Set<String> domain) {
        assignment.addVariable(name, domain);
    }

    /**
     * See {@link Assignment#addConstraint(Constraint)}.
     */
    public void addConstraint(Constraint constraint) {
        assignment.addConstraint(constraint);
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void addOutput(BenchmarkOutput output) {
        outputs.add(output);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
