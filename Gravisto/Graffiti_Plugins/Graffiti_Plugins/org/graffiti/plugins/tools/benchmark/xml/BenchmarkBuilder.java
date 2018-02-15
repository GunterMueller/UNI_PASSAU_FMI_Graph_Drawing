// =============================================================================
//
//   XmlExperiment.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.xml;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.graffiti.plugins.tools.benchmark.Benchmark;
import org.graffiti.plugins.tools.benchmark.ErrorPolicy;
import org.graffiti.plugins.tools.benchmark.OutputDirectoryPolicy;
import org.graffiti.plugins.tools.benchmark.Seedable;
import org.graffiti.plugins.tools.benchmark.constraint.AbstractConstraint;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class BenchmarkBuilder extends XmlDecoder {
    private SourceBuilder sourceBuilder;
    private BodyBuilder bodyBuilder;
    private OutputBuilder outputBuilder;

    public static Benchmark build(File file) throws IOException,
            FormatException {
        return build(file, null, false);
    }

    /**
     * Creates a new experiment from the specified description file.
     * 
     * @param file
     *            the XML file describing the experiment to create.
     * @return a new experiment.
     * @throws IOException
     *             if any IO errors occur.
     * @throws FormatException
     *             if the file is not correctly formatted.
     */
    public static Benchmark build(File file, File outputDirectory,
            boolean clearDirectory) throws IOException, FormatException {
        if (outputDirectory != null && !outputDirectory.isDirectory())
            throw new FormatException("error.pathNotDirectory");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setValidating(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void error(SAXParseException exception)
                        throws SAXException {
                    throw new SAXException(new FormatException(exception));
                }

                @Override
                public void fatalError(SAXParseException exception)
                        throws SAXException {
                    throw new SAXException(new FormatException(exception));
                }

                @Override
                public void warning(SAXParseException exception)
                        throws SAXException {
                    System.err.println(exception);
                    ;
                }
            });
            builder.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId,
                        String systemId) throws SAXException, IOException {
                    if (systemId.endsWith("benchmark.dtd"))
                        return new InputSource(BenchmarkBuilder.class
                                .getResourceAsStream("benchmark.dtd"));
                    else
                        return null;
                }
            });
            return new BenchmarkBuilder().parseBenchmark(builder.parse(file)
                    .getLastChild(), outputDirectory, clearDirectory);
        } catch (SAXException e) {
            Exception ex = e.getException();
            if (ex instanceof FormatException)
                throw (FormatException) ex;
            else
                throw new FormatException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Benchmark parseBenchmark(Node node, File outputDirectory,
            boolean clearDirectory) throws FormatException {
        benchmark = new Benchmark();
        sourceBuilder = new SourceBuilder(benchmark);
        bodyBuilder = new BodyBuilder(benchmark);
        outputBuilder = new OutputBuilder(benchmark);
        benchmark.setOutputDirectory(outputDirectory);

        if (clearDirectory) {
            benchmark.setOutputDirectoryPolicy(OutputDirectoryPolicy.CLEAR);
        } else {
            parseOutputDirectoryPolicy(node);
        }

        parseErrorPolicy(node);
        setFixedSeed(node, benchmark);
        benchmark.setLoggingLevel(Level
                .parse(getAttribute(node, "coreLogging")));
        enumChildren(node, new XmlElementVisitor() {
            @Override
            public void visit(Element child) throws FormatException {
                String nodeName = child.getNodeName();
                if (nodeName.equals("alias")) {
                    benchmark.addAlias(getAttribute(child, "name"), child
                            .getTextContent());
                } else if (nodeName.equals("source")) {
                    sourceBuilder.parseCompositeSource(child, benchmark
                            .getRootSource());
                } else if (nodeName.equals("tiebreaker")) {
                    parseTiebreaker(child);
                } else if (nodeName.equals("variable")) {
                    parseVariable(child);
                } else if (nodeName.equals("constraint")) {
                    benchmark.addConstraint(AbstractConstraint.parse(child
                            .getTextContent()));
                } else if (nodeName.equals("body")) {
                    bodyBuilder.parseSequence(child, benchmark.getBody());
                } else if (nodeName.equals("output")) {
                    outputBuilder.parseOutput(child);
                } else
                    throw new FormatException("error.unknownTag", nodeName);
            }
        });
        return benchmark;
    }

    /**
     * @param node
     */
    private void parseOutputDirectoryPolicy(Node node) {
        String policy = getAttribute(node, "checkOutputDirectory");
        if (policy.equals("ignore")) {
            benchmark.setOutputDirectoryPolicy(OutputDirectoryPolicy.IGNORE);
        } else if (policy.equals("warning")) {
            benchmark.setOutputDirectoryPolicy(OutputDirectoryPolicy.WARNING);
        } else if (policy.equals("clear")) {
            benchmark.setOutputDirectoryPolicy(OutputDirectoryPolicy.CLEAR);
        } else {
            benchmark.setOutputDirectoryPolicy(OutputDirectoryPolicy.STOP);
        }
    }

    private void parseErrorPolicy(Node node) {
        String policy = getAttribute(node, "onError");
        if (policy.equals("ignore")) {
            benchmark.setErrorPolicy(ErrorPolicy.IGNORE);
        } else if (policy.equals("warning")) {
            benchmark.setErrorPolicy(ErrorPolicy.WARNING);
        } else {
            benchmark.setErrorPolicy(ErrorPolicy.STOP);
        }
    }

    private void parseTiebreaker(Node node) throws FormatException {
        setFixedSeed(node, new Seedable() {
            @Override
            public void setFixedSeed(long fixedSeed) {
                benchmark.setFixedTiebreakerSeed(fixedSeed);
            }
        });
        benchmark.setUsesTiebreaker(getAttribute(node, "mode")
                .equals("shuffle"));
    }

    private void parseVariable(Node node) throws FormatException {
        String name = getAttribute(node, "name");
        String domainString = getAttribute(node, "domain");
        Set<String> domain = new LinkedHashSet<String>();
        for (String value : domainString.split("\\,")) {
            domain.add(value.trim());
        }
        benchmark.addVariable(name, domain);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
