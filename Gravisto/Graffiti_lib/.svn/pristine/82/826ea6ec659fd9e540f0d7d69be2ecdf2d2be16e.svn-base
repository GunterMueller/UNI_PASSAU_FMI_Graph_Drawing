// =============================================================================
//
//   FileSource.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.source;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Pattern;

import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.InputSerializer;
import org.graffiti.plugins.ios.gml.gmlReader.GmlReader;
import org.graffiti.plugins.ios.hbgf.HbgfReader;
import org.graffiti.plugins.ios.importers.graphml.GraphMLReader;
import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.AttributeUtil;
import org.graffiti.plugins.tools.benchmark.Benchmark;
import org.graffiti.util.VoidCallback;

/**
 * A source of graphs that are contained in a file or directory.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class PathSource extends AbstractGraphSource {
    private URL path;
    private InputSerializer serializer;
    private FileFilter filter;
    private String filterString;

    public PathSource(URL path, String format) {
        this(path, format, null);
    }

    public PathSource(URL path, String format, String filterString) {
        this(path, createSerializer(format), filterString);
    }

    public PathSource(URL path, InputSerializer serializer) {
        this(path, serializer, null);
    }

    public PathSource(URL path, InputSerializer serializer, String filterString) {
        this.path = path;
        this.serializer = serializer;
        this.filterString = filterString == null ? ".*" : filterString;
        filter = createFilter(this.filterString);
    }

    private static InputSerializer createSerializer(String format) {
        if (format.equals("gml"))
            return new GmlReader();
        else if (format.equals("graphml"))
            return new GraphMLReader();
        else if (format.equals("hbgf"))
            return new HbgfReader();
        else
            throw new IllegalArgumentException(String.format(Benchmark
                    .getString("error.graphFileFormat"), format));
    }

    private static FileFilter createFilter(final String filterString) {
        return new FileFilter() {
            private final Pattern PATTERN = Pattern.compile(filterString);

            @Override
            public boolean accept(File file) {
                return file.isFile()
                        && PATTERN.matcher(file.getName()).matches();
            }

        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void contribute(Collection<GraphFactory> collection,
            Random random, final Assignment assignment) {
        File file = new File(path.getFile());
        if (file.isDirectory()) {
            File[] fileList = file.listFiles(filter).clone();

            Collections.sort(Arrays.asList(fileList));

            for (final File child : fileList) {
                collection.add(createGraphFactory(child, assignment));
            }
        } else {
            collection.add(createGraphFactory(file, assignment));
        }
    }

    private GraphFactory createGraphFactory(final File file,
            final Assignment assignment) {
        return new GraphFactory() {

            @Override
            protected Graph makeGraph() {
                try {
                    return load(file.toURI().toURL());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected Assignment getAssignment() {
                return assignment;
            }
        };
    }

    private Graph load(final URL url) throws IOException {
        Graph graph = serializer.read(url.openStream());
        AttributeUtil.removeBenchmarkAttribute(graph);
        AttributeUtil.provideBenchmarkAttribute(graph);
        AttributeUtil.addSourceAttribute(graph, true,
                new VoidCallback<SourceAttribute>() {
                    @Override
                    public void call(SourceAttribute sourceAttribute) {
                        sourceAttribute.setType("singleFile");
                        sourceAttribute.add(new StringAttribute("path", url
                                .toString()));
                    }
                });
        return graph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void provideSourceInfo(SourceAttribute sourceAttribute) {
        sourceAttribute.setType("pathSource");
        sourceAttribute.add(new StringAttribute("path", path.toString()));
        sourceAttribute.add(new StringAttribute("filter", filterString));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
