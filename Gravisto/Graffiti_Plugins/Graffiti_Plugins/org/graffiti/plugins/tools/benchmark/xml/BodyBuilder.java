// =============================================================================
//
//   BodyBuilder.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.xml;

import org.graffiti.plugins.tools.benchmark.Benchmark;
import org.graffiti.plugins.tools.benchmark.body.BodyAlgorithm;
import org.graffiti.plugins.tools.benchmark.body.InternalTimerMap;
import org.graffiti.plugins.tools.benchmark.body.Sequence;
import org.graffiti.plugins.tools.benchmark.body.Switch;
import org.graffiti.plugins.tools.benchmark.body.Timer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
final class BodyBuilder extends XmlDecoder {
    protected BodyBuilder(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    protected void parseSequence(Node node, final Sequence sequence)
            throws FormatException {
        setFixedSeed(node, sequence);
        enumChildren(node, new XmlElementVisitor() {
            @Override
            public void visit(Element child) throws FormatException {
                String nodeName = child.getNodeName();
                if (nodeName.equals("algorithm")) {
                    sequence.addElement(parseAlgorithm(child));
                } else if (nodeName.equals("switch")) {
                    sequence.addElement(parseSwitch(child));
                } else if (nodeName.equals("start-timer")) {
                    sequence.addElement(new Timer(getAttribute(child, "id"),
                            true));
                } else if (nodeName.equals("stop-timer")) {
                    sequence.addElement(new Timer(getAttribute(child, "id"),
                            false));
                } else
                    throw new FormatException("error.unknownTag", nodeName);
            }
        });
    }

    private BodyAlgorithm parseAlgorithm(Node node) throws FormatException {
        String className = getAttribute(node, "class");
        final BodyAlgorithm algorithm = new BodyAlgorithm(className, benchmark);
        setFixedSeed(node, algorithm);
        enumChildren(node, new XmlElementVisitor() {
            @Override
            public void visit(Element child) throws FormatException {
                String nodeName = child.getNodeName();
                if (nodeName.equals("param")) {
                    parseParameter(child, algorithm);
                } else if (nodeName.equals("algorithm-param")) {
                    parseAlgorithmParameter(child, algorithm);
                }
            }
        });

        String resultId = getAttribute(node, "result");
        if (resultId != null) {
            algorithm.setResultId(resultId);
        }

        return algorithm;
    }

    private Switch parseSwitch(Node node) throws FormatException {
        final Switch element = new Switch(getAttribute(node, "variable"),
                benchmark.getAssignment());
        setFixedSeed(node, element);
        enumChildren(node, new XmlElementVisitor() {
            @Override
            public void visit(Element child) throws FormatException {
                String nodeName = child.getNodeName();
                if (nodeName.equals("case")) {
                    Sequence sequence = element.addCase(getAttribute(child,
                            "value"), benchmark.getAssignment());
                    setFixedSeed(child, sequence);
                    parseSequence(child, sequence);
                } else
                    throw new FormatException("error.unknownTag", nodeName);
            }
        });
        return element;
    }

    private void parseAlgorithmParameter(Node node,
            final BodyAlgorithm parentAlgorithm) throws FormatException {
        final String name = getAttribute(node, "name");
        final InternalTimerMap itm = new InternalTimerMap();
        enumChildren(node, new XmlElementVisitor() {
            private boolean isBefore = true;

            /**
             * {@inheritDoc}
             */
            @Override
            public void visit(Element child) throws FormatException {
                String nodeName = child.getNodeName();
                if (nodeName.equals("algorithm")) {
                    BodyAlgorithm algorithm = parseAlgorithm(child);
                    parentAlgorithm.addAlgorithmParameter(name, algorithm);
                    algorithm.setInternalTimerMap(itm);
                    isBefore = false;
                } else if (nodeName.equals("start-timer")) {
                    itm.set(getAttribute(child, "id"), isBefore, true);
                } else if (nodeName.equals("stop-timer")) {
                    itm.set(getAttribute(child, "id"), isBefore, false);
                } else
                    throw new FormatException("error.unknownTag", nodeName);
            }
        });
        itm.check();
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
