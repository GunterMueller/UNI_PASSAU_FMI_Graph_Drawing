// =============================================================================
//
//   Gml.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Gml.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

import java.util.List;

/**
 * The class <code>Gml</code> represents a graph in the graph modelling language
 * GML.
 * 
 * @author ruediger
 */
public class Gml extends GmlParsableItem {

    /** The declared edges style. */
    private GmlEdgeStyle edgeStyle;

    /** The list of attributes, nodes and edges. */
    private GmlList list;

    /** The declared node style. */
    private GmlNodeStyle nodeStyle;

    /**
     * The list of errors that occured on parsing the GML file. If the list is
     * not empty the graph might not have been read correctly.
     */
    private List<String> errors;

    /**
     * Constructs a new Gml instance.
     * 
     * @param line
     *            the line in which the graph was declared.
     * @param nodeStyle
     *            the node style of the graph.
     * @param edgeStyle
     *            the edge style of the graph.
     * @param list
     *            the list of errors that occured during parsing.
     * @param errors
     *            DOCUMENT ME!
     */
    public Gml(int line, GmlNodeStyle nodeStyle, GmlEdgeStyle edgeStyle,
            GmlList list, List<String> errors) {
        super(line);

        // nodeStyle and edgeStyle may be null
        this.nodeStyle = nodeStyle;
        this.edgeStyle = edgeStyle;

        assert list != null;
        this.list = list;
        assert errors != null;
        this.errors = errors;
    }

    /**
     * Returns the edge style declared in the GML file, <code>null</code> if
     * there was none declared.
     * 
     * @return the edge style declared in the GML file, <code>null</code> if
     *         there was none declared.
     */
    public GmlEdgeStyle getEdgeStyle() {
        return this.edgeStyle;
    }

    /**
     * Returns the list of errors the parser detected in the file.
     * 
     * @return the list of errors the parser detected in the file.
     */
    public List<String> getErrors() {
        return this.errors;
    }

    /**
     * Returns the list of attributes, nodes and edges declared in the GML file.
     * 
     * @return the list of attributes, nodes and edges declared in the GML file.
     */
    public GmlList getList() {
        return this.list;
    }

    /**
     * Returns the node style declared in the GML file, <code>null</code> if
     * there was none declared.
     * 
     * @return the node style declared in the GML file, <code>null</code> if
     *         there was none declared.
     */
    public GmlNodeStyle getNodeStyle() {
        return this.nodeStyle;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
