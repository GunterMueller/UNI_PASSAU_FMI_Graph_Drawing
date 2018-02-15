/*
 * Created on 13.12.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford;

import java.util.Comparator;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * @author Beiqi
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class XCoordComparator implements Comparator<Object> {

    int xCoordCompare;
    Node node1, node2;

    public int compare(Object obj1, Object obj2) {

        Node node1 = (Node) obj1;
        CoordinateAttribute ca1 = (CoordinateAttribute) node1
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        CoordinateAttribute ca2 = (CoordinateAttribute) node2
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);

        xCoordCompare = new Double(ca1.getX())
                .compareTo(new Double(ca2.getX()));

        return xCoordCompare;
    }

}
