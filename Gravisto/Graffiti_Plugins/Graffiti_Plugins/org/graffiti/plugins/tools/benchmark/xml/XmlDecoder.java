// =============================================================================
//
//   XmlUtil.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.xml;

import org.graffiti.plugins.tools.benchmark.Benchmark;
import org.graffiti.plugins.tools.benchmark.BoundAlgorithm;
import org.graffiti.plugins.tools.benchmark.BoundParameter;
import org.graffiti.plugins.tools.benchmark.Seedable;
import org.graffiti.plugins.tools.benchmark.constraint.AbstractConstraint;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class XmlDecoder {
    protected Benchmark benchmark;

    protected final String getAttribute(Node node, String name) {
        Node namedNode = node.getAttributes().getNamedItem(name);
        return namedNode == null ? null : namedNode.getNodeValue();
    }

    protected final Integer getIntAttribute(Node node, String name) {
        String value = getAttribute(node, name);
        if (value != null) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
            }
        }
        return null;
    }

    protected final void enumChildren(Node parent, XmlElementVisitor visitor)
            throws FormatException {
        NodeList list = parent.getChildNodes();
        int length = list.getLength();
        for (int i = 0; i < length; i++) {
            Node child = list.item(i);
            if (child instanceof Element) {
                visitor.visit((Element) child);
            }
        }
    }

    protected final String getTopText(Node parent) {
        StringBuilder builder = new StringBuilder();
        NodeList list = parent.getChildNodes();
        int length = list.getLength();
        for (int i = 0; i < length; i++) {
            Node child = list.item(i);
            if (child instanceof Text) {
                builder.append(child.getNodeValue().trim());
            }
        }
        return builder.toString();
    }

    protected final void setFixedSeed(Node node, Seedable seedable)
            throws FormatException {
        String seedString = getAttribute(node, "seed");
        if (!seedString.equals("default")) {
            try {
                seedable.setFixedSeed(Long.valueOf(seedString));
            } catch (NumberFormatException e) {
                throw new FormatException("error.seed");
            }
        }
    }

    protected final void parseParameter(Node node, BoundAlgorithm algorithm)
            throws FormatException {
        Integer index = null;
        String indexString = getAttribute(node, "index");
        if (indexString != null && !indexString.isEmpty()) {
            index = Integer.valueOf(indexString);
        }
        String name = getAttribute(node, "name");
        BoundParameter parameter = new BoundParameter();
        if (index != null) {
            parameter.setIndex(index);
        }
        if (name != null) {
            parameter.setName(name);
        }
        String onlyIf = getAttribute(node, "onlyIf");
        if (onlyIf != null) {
            parameter.setOnlyIf(AbstractConstraint.parse(onlyIf));
        }
        parameter.setValue(node.getTextContent());
        algorithm.addParameter(parameter);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
