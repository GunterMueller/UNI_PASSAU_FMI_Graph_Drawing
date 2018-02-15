// =============================================================================
//
//   BendAdapter.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.transitional;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * Simplifies the use of bend attributes. Warning: This is a temporary class,
 * which will be deleted once the new attribute system is in place. Usage
 * example:
 * 
 * <pre>
 * 
 * Edge e = ...
 * 
 * // Create the adapter. 
 * BendsAdapter bends = new BendsAdapter(e);
 * 
 * // Modify bends.
 * ListIterator<Point2D> iter = bends.listIterator();
 * iter.next().next();
 * iter.remove();
 * iter.add(new Point2D.Double(100.0, 200.0));
 * Point2D point = bends.removeLast();
 * bends.addFirst(point);
 * 
 * // Apply changes.
 * bends.commit();
 * 
 * </pre>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class BendsAdapter extends LinkedList<Point2D> {
    /**
     * 
     */
    private static final long serialVersionUID = -5441078049472028293L;
    private EdgeGraphicAttribute ega;

    /**
     * Constructs a new {@code BendAdapter} for the specified edge. Changes to
     * this list of bends are adopted by the edge on the call of
     * {@link #commit()}.
     * 
     * @param edge
     */
    public BendsAdapter(Edge edge) {
        ega = (EdgeGraphicAttribute) edge.getAttribute("graphics");
        SortedCollectionAttribute bendsAtt = (SortedCollectionAttribute) edge
                .getAttribute(GraphicAttributeConstants.BENDS_PATH);

        for (Map.Entry<String, Attribute> entry : bendsAtt.getCollection()
                .entrySet()) {
            addLast(((CoordinateAttribute) entry.getValue()).getCoordinate());
        }
    }

    /**
     * Applies the changes.
     */
    public void commit() {
        SortedCollectionAttribute bends = new LinkedHashMapAttribute("bends");

        int i = 0;

        for (Point2D point : this) {
            bends.add(new CoordinateAttribute("bend" + i, point));
            i++;
        }

        ega.setBends(bends);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
