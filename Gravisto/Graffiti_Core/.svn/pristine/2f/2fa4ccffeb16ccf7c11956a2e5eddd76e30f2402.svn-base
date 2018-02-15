// =============================================================================
//
//   Util.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.debug;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LabelAttribute;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DebugUtil {
    public static void attachDebugLabels(Graph graph) {
        graph.getListenerManager().transactionStarted(DebugUtil.class);
        int i = 0;
        for (Node node : graph.getNodes()) {
            LabelAttribute attr;
            try {
                attr = (LabelAttribute) node.getAttribute("debug_label");
            } catch (AttributeNotFoundException e) {
                attr = new LabelAttribute("debug_label");
                node.addAttribute(attr, "");
            }
            attr.setLabel("" + i);
            i++;
        }
        graph.getListenerManager().transactionFinished(DebugUtil.class);
    }

    public static String getDebugLabel(Node node) {
        try {
            return ((LabelAttribute) node.getAttribute("debug_label"))
                    .getLabel();
        } catch (AttributeNotFoundException e) {
            return "";
        }
    }

    private DebugUtil() {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
