// =============================================================================
//
//   OutputBuilder.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.xml;

import java.io.File;

import org.graffiti.plugins.ios.exporters.graphics.PdfSerializer;
import org.graffiti.plugins.ios.exporters.graphics.PngSerializer;
import org.graffiti.plugins.ios.exporters.graphml.GraphMLWriter;
import org.graffiti.plugins.ios.exporters.graphviz.DOTSerializer;
import org.graffiti.plugins.ios.gml.gmlWriter.GmlWriter;
import org.graffiti.plugins.tools.benchmark.Benchmark;
import org.graffiti.plugins.tools.benchmark.output.BenchmarkOutput;
import org.graffiti.plugins.tools.benchmark.output.Column;
import org.graffiti.plugins.tools.benchmark.output.ConfigInfoOutput;
import org.graffiti.plugins.tools.benchmark.output.CsvOutput;
import org.graffiti.plugins.tools.benchmark.output.GraphOutput;
import org.graffiti.plugins.tools.benchmark.output.LoggingOutput;
import org.graffiti.plugins.tools.benchmark.output.ProgressOutput;
import org.graffiti.plugins.tools.benchmark.output.PsTricksOutput;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
final class OutputBuilder extends XmlDecoder {
    protected OutputBuilder(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    protected void parseOutput(Node node) throws FormatException {
        String target = getAttribute(node, "target");
        File outputDirectory = benchmark.getOutputDirectory();
        if (!target.equals("stdout") && outputDirectory == null)
            throw new FormatException("error.noOutputDirectory", target);
        String options = getAttribute(node, "options");
        final BenchmarkOutput output = createOutput(node);
        enumChildren(node, new XmlElementVisitor() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void visit(Element child) throws FormatException {
                String nodeName = child.getNodeName();
                if (nodeName.equals("column")) {
                    parseColumn(child, output);
                } else if (nodeName.equals("option")) {
                    parseOption(child, output);
                } else
                    throw new FormatException("error.unknownTag", nodeName);
            }
        });
        output.initialize(target, options == null ? "" : options,
                getTopText(node), outputDirectory);
        benchmark.addOutput(output);
    }

    private BenchmarkOutput createOutput(Node node) throws FormatException {
        String format = getAttribute(node, "format");
        if (format.equals("config"))
            return new ConfigInfoOutput();
        if (format.equals("csv"))
            return new CsvOutput();
        else if (format.equals("gml"))
            return new GraphOutput(new GmlWriter());
        else if (format.equals("graphml"))
            return new GraphOutput(new GraphMLWriter());
        else if (format.equals("log"))
            return new LoggingOutput();
        else if (format.equals("graphviz"))
            return new GraphOutput(new DOTSerializer());
        else if (format.equals("pdf"))
            return new GraphOutput(new PdfSerializer());
        else if (format.equals("png"))
            return new GraphOutput(new PngSerializer());
        else if (format.equals("progress"))
            return new ProgressOutput();
        else if (format.equals("pstricks"))
            return new PsTricksOutput();
        else
            throw new FormatException("error.unknownFormat", format);
    }

    private void parseColumn(Node node, BenchmarkOutput output) {
        Column column = new Column(node.getTextContent());
        String name = getAttribute(node, "name");
        if (name != null) {
            column.setName(name);
        }
        output.addColumn(column);
    }

    private void parseOption(Node node, BenchmarkOutput output) {
        output.setOption(getAttribute(node, "key"), getTopText(node));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
