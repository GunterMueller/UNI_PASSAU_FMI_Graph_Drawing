package org.graffiti.plugins.algorithms.bfs;

import java.util.HashMap;
import java.util.Iterator;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;

public class BFSNumberVisitor implements BFSNodeVisitor {

    HashMap<Node, Integer> nodeToBFSNumber = new HashMap<Node, Integer>();

    private int actualBFSNumber = -1;

    public void processNode(Node v) {
        actualBFSNumber = nodeToBFSNumber.get(v);

    }

    public void processNeighbor(Node v) {
        nodeToBFSNumber.put(v, actualBFSNumber + 1);
        setLabel(v, "" + (actualBFSNumber + 1));
    }

    public void reset() {
        nodeToBFSNumber = new HashMap<Node, Integer>();

        actualBFSNumber = -1;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param n
     *            DOCUMENT ME!
     * @param val
     *            DOCUMENT ME!
     */
    private void setLabel(Node n, String val) {
        LabelAttribute labelAttr = (LabelAttribute) searchForAttribute(n
                .getAttribute(""), LabelAttribute.class);

        if (labelAttr != null) {
            labelAttr.setLabel(val);
        } else { // no label found
            labelAttr = new NodeLabelAttribute("label");
            labelAttr.setLabel(val);
            n.addAttribute(labelAttr, "");
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param attr
     *            DOCUMENT ME!
     * @param attributeType
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private Attribute searchForAttribute(Attribute attr,
            Class<? extends Attribute> attributeType) {
        if (attributeType.isInstance(attr))
            return attr;
        else {
            if (attr instanceof CollectionAttribute) {
                Iterator<Attribute> it = ((CollectionAttribute) attr)
                        .getCollection().values().iterator();

                while (it.hasNext()) {
                    Attribute newAttr = searchForAttribute(it.next(),
                            attributeType);

                    if (newAttr != null)
                        return newAttr;
                }
            } else if (attr instanceof CompositeAttribute)
                // TODO: treat those correctly; some of those have not yet
                // been correctly implemented
                return null;
        }

        return null;
    }

}
