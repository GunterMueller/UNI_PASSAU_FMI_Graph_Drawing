// =============================================================================
//
//   PortsAdapter.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.transitional;

import static org.graffiti.graphics.GraphicAttributeConstants.COORD_PATH;
import static org.graffiti.graphics.GraphicAttributeConstants.DIM_PATH;
import static org.graffiti.graphics.GraphicAttributeConstants.PORTS_PATH;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.Port;
import org.graffiti.graphics.PortAttribute;
import org.graffiti.graphics.PortsAttribute;

/**
 * @author Gravisto Team
 * @version $Revision$ $Date$
 */
public class PortsAdapter extends LinkedList<Port> {
    private abstract class Accessor {
        public abstract Map<String, Attribute> getCollection();
        public abstract void commit();
    }
    
    private class InAccessor extends Accessor {
        @Override
        public Map<String, Attribute> getCollection() {
            return portsAttribute.getIngoing().getCollection();
        }

        @Override
        public void commit() {
            portsAttribute.setIngoingPorts(PortsAdapter.this);
        }
    }
    
    private class OutAccessor extends Accessor {
        @Override
        public Map<String, Attribute> getCollection() {
            return portsAttribute.getOutgoing().getCollection();
        }
        
        @Override
        public void commit() {
            portsAttribute.setOutgoingPorts(PortsAdapter.this);
        }
    }
    
    private class CommonAccessor extends Accessor {
        @Override
        public Map<String, Attribute> getCollection() {
            return portsAttribute.getCommon().getCollection();
        }
        
        @Override
        public void commit() {
            portsAttribute.setCommonPorts(PortsAdapter.this);
        }
    }
    
    
    private PortsAttribute portsAttribute;
    private Accessor accessor;
    private double x;
    private double y;
    private double width;
    private double height;
    
    /**
     * @param node
     * @param portsType
     *            one of {@link GraphicAttributeConstants#IN},
     *            {@link GraphicAttributeConstants#OUT}, and
     *            {@link GraphicAttributeConstants#COMMON}.
     */
    public PortsAdapter(Node node, String portsType) {
        portsAttribute = (PortsAttribute) node.getAttribute(PORTS_PATH);
        
        if (portsType.equals(GraphicAttributeConstants.IN)) {
            accessor = new InAccessor();
        } else if (portsType.equals(GraphicAttributeConstants.OUT)) {
            accessor = new OutAccessor();
        } else if (portsType.equals(GraphicAttributeConstants.COMMON)) {
            accessor = new CommonAccessor();
        } else {
            throw new IllegalArgumentException();
        }
        
        for (Attribute attribute : accessor.getCollection().values()) {
            if (attribute instanceof PortAttribute) {
                add(((PortAttribute) attribute).getPort());
            }
        }
        
        CoordinateAttribute coordinateAttribute =
                (CoordinateAttribute) node.getAttribute(COORD_PATH);
        x = coordinateAttribute.getX();
        y = coordinateAttribute.getY();
        
        DimensionAttribute dimensionAttribute =
                (DimensionAttribute) node.getAttribute(DIM_PATH);
        width = dimensionAttribute.getWidth();
        height = dimensionAttribute.getHeight();
    }
    
    public Port addRelativePort(String name, Point2D position) {
        Port port = new Port(name, position.getX(), position.getY());
        add(port);
        return port;
    }
    
    public Port addAbsolutePort(String name, Point2D position) {
        double tx = (position.getX() - x) / width * 2.0;
        double ty = (position.getY() - y) / height * 2.0;
        return addRelativePort(name, new Point2D.Double(tx, ty));
    }
    
    public void commit() {
        accessor.commit();
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
