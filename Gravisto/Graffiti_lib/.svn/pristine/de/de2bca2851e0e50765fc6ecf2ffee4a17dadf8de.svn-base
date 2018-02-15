// =============================================================================
//
//   SourceBuilder.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.xml;

import java.net.MalformedURLException;
import java.net.URL;

import org.graffiti.plugins.tools.benchmark.Benchmark;
import org.graffiti.plugins.tools.benchmark.sampler.AssignmentList;
import org.graffiti.plugins.tools.benchmark.sampler.Parser;
import org.graffiti.plugins.tools.benchmark.sampler.Scanner;
import org.graffiti.plugins.tools.benchmark.source.CompositeSource;
import org.graffiti.plugins.tools.benchmark.source.GeneratorSource;
import org.graffiti.plugins.tools.benchmark.source.PathSource;
import org.graffiti.plugins.tools.benchmark.source.SourceTransformation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
final class SourceBuilder extends XmlDecoder {
    protected SourceBuilder(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    protected void parseCompositeSource(Node node,
            final CompositeSource composite) throws FormatException {
        setFixedSeed(node, composite);
        enumChildren(node, new XmlElementVisitor() {
            @Override
            public void visit(Element child) throws FormatException {
                String nodeName = child.getNodeName();
                if (nodeName.equals("source")) {
                    CompositeSource compositeSource = new CompositeSource();
                    parseCompositeSource(child, compositeSource);
                    composite.addSource(compositeSource);
                } else if (nodeName.equals("file")) {
                    try {
                        PathSource pathSource = new PathSource(new URL(child
                                .getTextContent()), getAttribute(child,
                                "format"), getAttribute(child, "filter"));
                        setFixedSeed(child, pathSource);
                        composite.addSource(pathSource);
                    } catch (MalformedURLException e) {
                        throw new FormatException(e);
                    }
                } else if (nodeName.equals("generator")) {
                    parseGenerator(child, composite);
                } else if (nodeName.equals("transformation")) {
                    parseTransformation(child, composite);
                } else
                    throw new FormatException("error.unknownTag", nodeName);
            }
        });
    }

    private void parseTransformation(Node node, CompositeSource source)
            throws FormatException {
        String className = getAttribute(node, "class");
        final SourceTransformation transformation = new SourceTransformation(
                className);
        setFixedSeed(node, transformation);
        source.addTransformation(transformation);
        enumChildren(node, new XmlElementVisitor() {
            @Override
            public void visit(Element child) throws FormatException {
                parseParameter(child, transformation);
            }
        });
    }

    private void parseGenerator(Node node, CompositeSource composite)
            throws FormatException {
        String className = getAttribute(node, "class");
        Integer quantity = getIntAttribute(node, "quantity");
        if (quantity == null)
            throw new FormatException("error.intFormat", "quantity");
        String assignmentString = getTopText(node).replace('\n', ' ').trim();
        AssignmentList list = null;
        if (assignmentString.isEmpty()) {
            list = new AssignmentList();
        } else {
            try {
                list = (AssignmentList) new Parser(
                        new Scanner(assignmentString)).parse().value;
            } catch (Exception e) {
                throw new FormatException("error.generatorFormat",
                        assignmentString);
            }
        }

        GeneratorSource generator = new GeneratorSource(className, quantity,
                list);
        setFixedSeed(node, generator);
        composite.addSource(generator);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
