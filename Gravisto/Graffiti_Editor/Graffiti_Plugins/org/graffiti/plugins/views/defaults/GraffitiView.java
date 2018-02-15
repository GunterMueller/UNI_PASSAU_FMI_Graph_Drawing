// =============================================================================
//
//   GraffitiView.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraffitiView.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JScrollPane;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeConsumer;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.core.Bundle;
import org.graffiti.editor.AttributeComponentNotFoundException;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.actions.RedrawViewAction;
import org.graffiti.event.AttributeEvent;
import org.graffiti.event.AttributeListener;
import org.graffiti.event.EdgeEvent;
import org.graffiti.event.EdgeListener;
import org.graffiti.event.GraphEvent;
import org.graffiti.event.GraphListener;
import org.graffiti.event.NodeListener;
import org.graffiti.event.TransactionEvent;
import org.graffiti.event.TransactionListener;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.AbstractView;
import org.graffiti.plugin.view.AttributeComponent;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugin.view.MessageListener;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugin.view.View2D;
import org.graffiti.plugin.view.Viewport;

/**
 * An implementation of <code>org.graffiti.plugin.view.View2D</code>, that
 * displays a graph. Since it also shows changes in the graph it listens for
 * changes in the graph, attributes, nodes and edges.
 * 
 * @see javax.swing.JPanel
 * @see org.graffiti.plugin.view.View2D
 */
public class GraffitiView extends AbstractView implements View2D,
        GraphListener, AttributeListener, AttributeConsumer, NodeListener,
        EdgeListener, TransactionListener, DummySupportView {

    // /** The logger for the current class. */
    // private static final Logger logger =
    // Logger.getLogger(GraffitiView.class.getName());

    /**
     * 
     */
    private static final long serialVersionUID = -2704699706741649450L;

    /** The <code>StringBundle</code> of the exceptions. */
    protected static final Bundle bundle = Bundle.getCoreBundle();

    /** Count active transactions like ListenerManager does */
    private int activeTransactions = 0;

    /** Maps MouseListeners to their corresponding ZoomedMouseListeners */
    private Map<MouseListener, ZoomedMouseListener> zoomedMouseListeners = new HashMap<MouseListener, ZoomedMouseListener>();

    /**
     * Maps MouseMotionListeners to their corresponding
     * ZoomedMouseMotionListeners
     */
    private Map<MouseMotionListener, ZoomedMouseMotionListener> zoomedMouseMotionListeners = new HashMap<MouseMotionListener, ZoomedMouseMotionListener>();

    private Viewport dummyViewport;

    /**
     * Constructs a new <code>GraffitiView</code>. The graph is initialized with
     * an instance of the default implementation.
     */
    public GraffitiView() {
        super();
        dummyViewport = new Viewport() {
            public AffineTransform getZoomTransform() {
                return zoom;
            }

            public void setZoom(double factor) {
                GraffitiView.this.setZoom(factor);
            }
        };
        if (GraffitiSingleton.getInstance().getMainFrame() != null) {
            this.addMouseMotionListener(GraffitiSingleton.getInstance()
                    .getMainFrame().getStatusBar());
        }
    }

    /**
     * Constructs a new <code>GraffitiView</code> for the specified
     * <code>Graph</code>.
     * 
     * @param currentGraph
     *            the <code>Graph</code> for which to construct the new
     *            <code>GraffitiView</code>.
     */
    public GraffitiView(Graph currentGraph) {
        super(currentGraph);
    }

    /**
     * @see java.awt.Container#getComponentAt(int, int)
     */
    @Override
    public Component getComponentAt(int x, int y) {
        // return super.getComponentAt(x, y);
        // return super.getComponentAt((int) (x * ((Point2D) zoom).getX()),
        // (int) (y * ((Point2D) zoom).getY()));
        Point2D pt2d = zoom.transform(new Point(x, y), null);
        Point zoomedPoint = new Point((int) pt2d.getX(), (int) pt2d.getY());

        return super.getComponentAt(zoomedPoint.x, zoomedPoint.y);
    }

    // /**
    // * @see java.awt.Container#getComponentAt(int, int)
    // */
    // public Component getComponentAt(int x, int y)
    // {
    // return super.getComponentAt((int) (x * p2dZoom.getX()),
    // (int) (y * p2dZoom.getY()));
    // }

    /**
     * @see org.graffiti.attributes.AttributeConsumer#getUndirectedEdgeAttribute()
     */
    public CollectionAttribute getUndirectedEdgeAttribute() {
        return new EdgeGraphicAttribute(false);
    }

    /**
     * @see org.graffiti.attributes.AttributeConsumer#getDirectedEdgeAttribute()
     */
    public CollectionAttribute getDirectedEdgeAttribute() {
        return new EdgeGraphicAttribute(true);
    }

    /**
     * Sets the graph this view displays.
     * 
     * @param g
     *            graph this view should display.
     */
    @Override
    public void setGraph(Graph g) {
        currentGraph = g;

        // graphElementComponents.clear();
        this.completeRedraw();

        // for (Iterator it = g.getNodesIterator(); it.hasNext();) {
        // Node n = (Node) it.next();
        // NodeComponent nc = createNodeComponent(n);
        // try {
        // nc.createNewShape();
        // } catch (ShapeNotFoundException snfe) {
        // nc.createStandardShape();
        // informMessageListener(
        // "statusbar.error.graphelement.ShapeNotFoundException",
        // MessageListener.ERROR);
        // }
        //
        // // addLabelComponent(n, nc, 0);
        // this.add(nc, 0);
        // addAttributeComponents(n, nc);
        // }
        // for (Iterator it = g.getEdgesIterator(); it.hasNext();) {
        // Edge e = (Edge) it.next();
        // EdgeComponent ec = createEdgeComponent(e);
        // try {
        // ec.createNewShape();
        // } catch (ShapeNotFoundException snfe) {
        // ec.createStandardShape();
        // informMessageListener(
        // "statusbar.error.graphelement.ShapeNotFoundException",
        // MessageListener.ERROR);
        // }
        //
        // // addLabelComponent(e, ec, 0);
        // this.add(ec, 0);
        // addAttributeComponents(e, ec);
        // }
    }

    /**
     * @see org.graffiti.attributes.AttributeConsumer#getGraphAttribute()
     */
    public CollectionAttribute getGraphAttribute() {
        return null; // TODO
    }

    /**
     * @see javax.swing.JComponent#getGraphics()
     */
    @Override
    public Graphics getGraphics() {
        Graphics2D sg = (Graphics2D) super.getGraphics();

        if (sg == null)
            return null;
        else {
            sg.transform(zoom);

            return sg;
        }
    }

    /**
     * @see org.graffiti.attributes.AttributeConsumer#getNodeAttribute()
     */
    public CollectionAttribute getNodeAttribute() {
        return new NodeGraphicAttribute();
    }

    /**
     * Adds a message listener to the view.
     * 
     * @param ml
     *            a message listener
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    @Override
    public void addMessageListener(MessageListener ml) {
        if (ml == null)
            throw new IllegalArgumentException("The argument may not be null");

        this.messageListeners.add(ml);
    }

    /**
     * @see java.awt.Component#addMouseListener(java.awt.event.MouseListener)
     */
    @Override
    public synchronized void addMouseListener(MouseListener l) {
        ZoomedMouseListener zoomedListener = new ZoomedMouseListener(l);
        zoomedMouseListeners.put(l, zoomedListener);
        super.addMouseListener(zoomedListener);
    }

    @Override
    public synchronized void removeMouseListener(MouseListener listener) {
        super.removeMouseListener(zoomedMouseListeners.get(listener));
        zoomedMouseListeners.remove(listener);
    }

    /**
     * @see java.awt.Component#addMouseMotionListener(java.awt.event.MouseMotionListener)
     */
    @Override
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        ZoomedMouseMotionListener zoomedListener = new ZoomedMouseMotionListener(
                l);
        zoomedMouseMotionListeners.put(l, zoomedListener);
        super.addMouseMotionListener(zoomedListener);
    }

    @Override
    public synchronized void removeMouseMotionListener(MouseMotionListener l) {
        super.removeMouseMotionListener(zoomedMouseMotionListeners.get(l));
        zoomedMouseMotionListeners.remove(l);
    }

    /**
     * Closes the current view.
     */
    @Override
    public void close() {
        setVisible(false);
    }

    /**
     * @see org.graffiti.plugin.view.View#completeRedraw()
     */
    public void completeRedraw() {
        removeAll();
        Map<GraphElement, GraphElementComponent> gecMap = new HashMap<GraphElement, GraphElementComponent>();

        for (Iterator<Node> it = currentGraph.getNodesIterator(); it.hasNext();) {
            Node n = it.next();
            NodeComponent nc = createNodeComponent(gecMap, n);

            try {
                nc.createNewShape();
            } catch (ShapeNotFoundException e) {
                nc.createStandardShape();
                informMessageListener(
                        "statusbar.error.graphelement.ShapeNotFoundException",
                        MessageListener.ERROR);
            }

            this.add(nc, new Integer(0));
            addAttributeComponents(n, nc);
        }

        for (Iterator<Edge> it = currentGraph.getEdgesIterator(); it.hasNext();) {
            Edge e = it.next();
            EdgeComponent ec = createEdgeComponent(gecMap, e);

            try {
                ec.createNewShape();
            } catch (ShapeNotFoundException snf) {
                ec.createStandardShape();
                informMessageListener(
                        "statusbar.error.graphelement.ShapeNotFoundException",
                        MessageListener.ERROR);
            }

            this.add(ec, new Integer(1));
            addAttributeComponents(e, ec);
        }

        this.graphElementComponents = gecMap;

        //		
        adjustPreferredSize(true);

        Rectangle visRect = new Rectangle();
        this.computeVisibleRect(visRect);

        this.paintImmediately(visRect);
    }

    /**
     * @see javax.swing.JComponent#contains(int, int)
     */
    @Override
    public boolean contains(int x, int y) {
        return super.contains(x, y);
    }

    /**
     * @see java.awt.Container#findComponentAt(int, int)
     */
    @Override
    public Component findComponentAt(int x, int y) {
        Point2D pt2d = zoom.transform(new Point(x, y), null);
        Point zoomedPoint = new Point((int) pt2d.getX(), (int) pt2d.getY());

        return super.findComponentAt(zoomedPoint.x, zoomedPoint.y);
    }

    /**
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        ((Graphics2D) g).transform(zoom);

        super.paintComponent(g);
    }

    /**
     * @see java.awt.Container#paintComponents(java.awt.Graphics)
     */
    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        ((Graphics2D) g).transform(zoom);

        super.paintComponent(g);
    }

    /**
     * Called after an attribute has been added.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    @Override
    public void postAttributeAdded(AttributeEvent e) {
        Attribute attr = e.getAttribute();

        GraphElement ge = null;

        try {
            ge = (GraphElement) attr.getAttributable();
        } catch (ClassCastException cce) {
            // added an attribute to the graph
            // since view does not display any attribute of graphs, nothing
            // needs to be done
            return;
        }

        GraphElementComponent gec = graphElementComponents.get(ge);

        if (gec == null)
            // transaction is active and attribute of a newly created element
            // is added -> ignore it
            return;

        recurseAttributes(attr, gec);

        try {
            gec.attributeChanged(attr);
        } catch (ShapeNotFoundException snfe) {
            informMessageListener(
                    "statusbar.error.attribute.ShapeNotFoundException",
                    MessageListener.ERROR);
        } catch (NullPointerException snfe) {
            // no shape set
            // TODO: check if this catch is ok
        }

        repaint();
    }

    /**
     * Called after an attribute has been changed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    @Override
    public void postAttributeChanged(AttributeEvent e) {
        if (activeTransactions > 0)
            return;

        Attribute attr = e.getAttribute();
        try {
            GraphElementComponent gec = graphElementComponents.get(attr
                    .getAttributable());
            gec.attributeChanged(attr);

            // this.adjustPreferredSize(gec);
            adjustPreferredSize();
        } catch (ShapeNotFoundException snfe) {
            informMessageListener(
                    "statusbar.error.attribute.ShapeNotFoundException",
                    MessageListener.ERROR);
        }

        repaint();
    }

    /**
     * Called after an attribute has been removed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    @Override
    public void postAttributeRemoved(AttributeEvent e) {
        Attribute attr = e.getAttribute();
        Attributable attributable = attr.getAttributable();
        AttributeComponent ac = null;

        GraphElementComponent gec = graphElementComponents.get(attributable);

        if (gec == null)
            // an attribute is removed for an element which was added during a
            // transaction
            // -> ignore it
            return;
        ac = gec.getAttributeComponent(attr);
        gec.removeAttributeComponent(attr);

        try {
            gec.attributeChanged(attr.getParent());
        } catch (ShapeNotFoundException snfe) {
            informMessageListener(
                    "statusbar.error.attribute.ShapeNotFoundException",
                    MessageListener.ERROR);
        }

        if (ac != null) {
            this.remove(ac);
            this.validate();
        }

        this.repaint();
    }

    /**
     * Called after the edge was set directed or undirected.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    @Override
    public void postDirectedChanged(EdgeEvent e) {
        if (activeTransactions > 0)
            return;

        Edge edge = e.getEdge();
        EdgeComponent ec = (EdgeComponent) graphElementComponents.get(edge);
        ec.updateShape();
    }

    /**
     * Adds a view for the given edge to this GraffitiView. The edge may be part
     * of the graph or not.
     * 
     * @param edge
     *            any Edge
     */
    public void addViewForEdge(Edge edge) {
        EdgeComponent component = createEdgeComponent(graphElementComponents,
                edge);

        // graphElementComponents.put(edge, component);
        try {
            component.createNewShape();
        } catch (ShapeNotFoundException snfe) {
            component.createStandardShape();
            informMessageListener(
                    "statusbar.error.graphelement.ShapeNotFoundException",
                    MessageListener.ERROR);
        }

        // Node node1 = edge.getSource();
        // NodeComponent nc = (NodeComponent) graphElementComponents.get(node1);
        // nc.addDependentComponent(component);
        // Node node2 = edge.getTarget();
        // nc = (NodeComponent) graphElementComponents.get(node2);
        // nc.addDependentComponent(component);
        // addLabelComponent(edge, component, 0);
        this.add(component, new Integer(1));
        addAttributeComponents(edge, component);

        invalidate();
        repaint(edge);
    }

    /**
     * Called after an edge has been added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    @Override
    public void postEdgeAdded(GraphEvent e) {
        if (activeTransactions > 0)
            return;

        Edge edge = e.getEdge();
        addViewForEdge(edge);
    }

    /**
     * Removes the view for the given edge from this GraffitiView.
     * 
     * @param edge
     *            any Edge
     */
    public void removeViewForEdge(Edge edge) {
        EdgeComponent ec = (EdgeComponent) graphElementComponents.get(edge);
        graphElementComponents.remove(edge);

        // remove attributeComponents (like label)
        for (Iterator<AttributeComponent> it = ec
                .getAttributeComponentIterator(); it.hasNext();) {
            remove(it.next());
        }

        ec.clearAttributeComponentList();

        // Node node1 = edge.getSource();
        // NodeComponent nc = (NodeComponent) graphElementComponents.get(node1);
        // nc.removeDependentComponent(ec);
        // Node node2 = edge.getSource();
        // nc = (NodeComponent) graphElementComponents.get(node2);
        // nc.removeDependentComponent(ec);
        remove(ec);
        adjustPreferredSize();
        repaint();
    }

    /**
     * Called after an edge has been removed from the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    @Override
    public void postEdgeRemoved(GraphEvent e) {
        if (activeTransactions > 0)
            return;

        Edge edge = e.getEdge();
        removeViewForEdge(edge);
    }

    /**
     * Called after the edge has been reversed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    @Override
    public void postEdgeReversed(EdgeEvent e) {
        if (activeTransactions > 0)
            return;

        Edge edge = e.getEdge();
        EdgeComponent ec = (EdgeComponent) graphElementComponents.get(edge);

        ec.reverse();
        ec.updateShape();
    }

    /**
     * Called after method <code>clear()</code> has been called on a graph. No
     * other events (like remove events) are generated.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    @Override
    public void postGraphCleared(GraphEvent e) {
        if (activeTransactions > 0)
            return;

        graphElementComponents.clear();
        removeAll();
        repaint();
    }

    /**
     * Adds a view for the given Node to this GraffitiView. The Node may be in
     * the graph or not.
     * 
     * @param node
     *            any Node
     */
    public void addViewForNode(Node node) {
        NodeComponent component = createNodeComponent(graphElementComponents,
                node);

        // graphElementComponents.put(node, component);
        try {
            component.createNewShape();
        } catch (ShapeNotFoundException snfe) {
            component.createStandardShape();
            informMessageListener(
                    "statusbar.error.graphelement.ShapeNotFoundException",
                    MessageListener.ERROR);
        }

        // addLabelComponent(node, component, 0);
        this.add(component, new Integer(0));
        addAttributeComponents(node, component);

        validate();
        repaint(node);
    }

    /**
     * Called after an edge has been added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    @Override
    public void postNodeAdded(GraphEvent e) {
        if (activeTransactions > 0)
            return;

        Node node = e.getNode();
        addViewForNode(node);
    }

    /**
     * Removes the view for the given Node from this GraffitiView.
     * 
     * @param node
     *            any Node
     */
    public void removeViewForNode(Node node) {
        NodeComponent nc = (NodeComponent) graphElementComponents.get(node);

        graphElementComponents.remove(node);
        // remove attributeComponents (like label)
        for (Iterator<AttributeComponent> it = nc
                .getAttributeComponentIterator(); it.hasNext();) {
            remove(it.next());
        }

        nc.clearAttributeComponentList();

        remove(nc);
        adjustPreferredSize();
        repaint();
    }

    /**
     * Called after a node has been removed from the graph. All edges incident
     * to this node have already been removed (preEdgeRemoved and
     * postEdgeRemoved have been called).
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    @Override
    public void postNodeRemoved(GraphEvent e) {
        if (activeTransactions > 0)
            return;

        Node node = e.getNode();
        removeViewForNode(node);
    }

    /**
     * Called after the source node of an edge has changed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    @Override
    public void postSourceNodeChanged(EdgeEvent e) {
        if (activeTransactions > 0)
            return;

        Edge edge = e.getEdge();
        Node newSource = edge.getSource();
        EdgeComponent ec = (EdgeComponent) graphElementComponents.get(edge);
        NodeComponent nc = (NodeComponent) graphElementComponents
                .get(newSource);
        ec.setSourceComponent(nc);
        nc.addDependentComponent(ec);

        ec.updateShape();
    }

    /**
     * Called after the target node of an edge has changed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    @Override
    public void postTargetNodeChanged(EdgeEvent e) {
        if (activeTransactions > 0)
            return;

        Edge edge = e.getEdge();
        Node newTarget = edge.getTarget();
        EdgeComponent ec = (EdgeComponent) graphElementComponents.get(edge);
        NodeComponent nc = (NodeComponent) graphElementComponents
                .get(newTarget);
        ec.setTargetComponent(nc);
        nc.addDependentComponent(ec);

        ec.updateShape();
    }

    /**
     * Called before a change of the source node of an edge takes place.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    @Override
    public void preSourceNodeChanged(EdgeEvent e) {
        if (activeTransactions > 0)
            return;

        Edge edge = e.getEdge();
        EdgeComponent ec = (EdgeComponent) graphElementComponents.get(edge);
        Node node = edge.getSource();
        NodeComponent nc = (NodeComponent) graphElementComponents.get(node);
        nc.removeDependentComponent(ec);
    }

    /**
     * Called before a change of the target node of an edge takes place.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    @Override
    public void preTargetNodeChanged(EdgeEvent e) {
        if (activeTransactions > 0)
            return;

        Edge edge = e.getEdge();
        EdgeComponent ec = (EdgeComponent) graphElementComponents.get(edge);
        Node node = edge.getTarget();
        NodeComponent nc = (NodeComponent) graphElementComponents.get(node);
        nc.removeDependentComponent(ec);
    }

    /**
     * Removes a message listener from the view.
     * 
     * @param ml
     *            a message listener
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    @Override
    public void removeMessageListener(MessageListener ml) {
        if (ml == null)
            throw new IllegalArgumentException("The argument may not be null");

        this.messageListeners.remove(ml);
    }

    /**
     * Repaints the given graph element
     * 
     * @param ge
     *            the<code>GraphElement</code> to repaint.
     */
    public void repaint(GraphElement ge) {
        graphElementComponents.get(ge).repaint();
    }

    /**
     * Called when a transaction has stopped.
     * 
     * @param event
     *            the EdgeEvent detailing the changes.
     */
    @Override
    public void transactionFinished(TransactionEvent event) {
        activeTransactions--;

        // used to prevent updating an element several times
        Set<Attributable> attributables = new HashSet<Attributable>();

        // must add edges AFTER nodes ...
        Set<Edge> edgesToAdd = new HashSet<Edge>();

        for (Object obj : event.getChangedObjects()) {
            Attributable atbl = null;

            if (obj instanceof Attributable) {
                // if the object is an Attributable, use it ...
                atbl = (Attributable) obj;
            } else {
                // ... else it is an Attribute; use its Attributable
                Attribute attr = null;

                try {
                    attr = (Attribute) obj;
                } catch (ClassCastException cce) {
                    System.err.println("Only Attributables and Attributes "
                            + "are allowed to be put into the set of changed "
                            + "objects during a transaction! (not "
                            + obj.getClass() + ")");

                    continue;
                }

                atbl = attr.getAttributable();
            }

            if (!attributables.contains(atbl)) {
                attributables.add(atbl);

                if (atbl instanceof Graph) {
                    // information not helpful
                    completeRedraw();
                }

                try {
                    if (atbl instanceof GraphElement) {
                        if (((GraphElement) atbl).getGraph() == null) {
                            GraphElementComponent gec = graphElementComponents
                                    .get(atbl);

                            if (gec != null) {
                                // graph element has been DELETED
                                if (atbl instanceof Node) {
                                    postNodeRemoved(new GraphEvent((Node) atbl));
                                } else if (atbl instanceof Edge) {
                                    postEdgeRemoved(new GraphEvent((Edge) atbl));
                                }
                            }
                            // if gec == null -> element was added and removed
                            // during transaction
                            // -> just ignore it
                        } else {
                            // graph element has been CHANGED
                            GraphElementComponent gec = graphElementComponents
                                    .get(atbl);

                            if (gec != null) {
                                // (this will change dependent components, too
                                // e.g. edges if a node has changed)
                                gec.attributeChanged(atbl.getAttribute(""));
                            } else {
                                // graph element has been ADDED
                                if (atbl instanceof Node) {
                                    postNodeAdded(new GraphEvent((Node) atbl));
                                } else {
                                    if (atbl instanceof Edge) {
                                        edgesToAdd.add((Edge) atbl);
                                    }
                                }
                            }
                        }
                    } else {
                        // TODO check: an Attributable that is neither a Graph
                        // nor a GraphElement; that should not be possible
                        completeRedraw();
                    }
                } catch (ShapeNotFoundException snfe) {
                    informMessageListener(
                            "statusbar.error.attribute.ShapeNotFoundException",
                            MessageListener.ERROR);
                }
            }
        }

        for (Edge edge : edgesToAdd) {
            postEdgeAdded(new GraphEvent(edge));
        }

        // TODO: QUICKFIX: The refresh is only necessary, because their is a BUG
        // involving transaction system and the attribute system.
        new RedrawViewAction(GraffitiSingleton.getInstance().getMainFrame())
                .actionPerformed(null);
        repaint();
        adjustPreferredSize();
    }

    /**
     * @see org.graffiti.event.TransactionListener#transactionStarted(org.graffiti.event.TransactionEvent)
     */
    @Override
    public void transactionStarted(TransactionEvent e) {
        super.transactionStarted(e);
        activeTransactions++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setZoom(double factor) {
        super.setZoom(factor);
        adjustPreferredSize(true);
    }

    /**
     * @see java.awt.Container#addImpl(Component, Object, int)
     */
    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index);
        this.adjustPreferredSize(comp);
    }

    /**
     * Creates a new <code>NodeComponent</code>. It first checks if the
     * <code>graphElementComponents</code> map already (/ still) contains a
     * component for that very edge. If yes, this component is used. Otherwise,
     * a new component is created and entered into the given map. The original
     * map is not altered. Therefore, the caller is responsible to use the
     * gecMap correctly: Either provide the <code>graphElementComponents</code>,
     * then everything is as expected. Or provide a new map; then only the new
     * map is updated. This is used for a complete redraw. for example. There,
     * it is not necessary to create new components if a component already
     * exists (in fact it would be dangerous since the component might have
     * changed, like a border added).
     * 
     * @param gecMap
     * @param node
     *            the node for which the component is built.
     * 
     * @return DOCUMENT ME!
     * 
     * @see #completeRedraw() for an example.
     */
    protected NodeComponent createNodeComponent(
            Map<GraphElement, GraphElementComponent> gecMap, Node node) {
        NodeComponent nodeComponent = (NodeComponent) graphElementComponents
                .get(node);

        if (nodeComponent == null) {
            nodeComponent = (NodeComponent) gecMap.get(node);

            if (nodeComponent == null) {
                nodeComponent = new NodeComponent(node);
            }
        }

        nodeComponent.clearDependentComponentList();
        graphElementComponents.put(node, nodeComponent);
        gecMap.put(node, nodeComponent);

        return nodeComponent;
    }

    /**
     * Extracts the name of this view class. It has to be overridden by all
     * extended subclasses of this class.
     * 
     * @return DOCUMENT ME!
     */
    @Override
    protected String extractName() {
        return this.getClass().getName();
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        ((Graphics2D) g).transform(zoom);
        super.paintComponent(g);
    }

    /**
     * Get index of component (in the <code>getComponents()</code> array) within
     * the view.
     * 
     * @param comp
     * 
     * @return int
     */
    private int getComponentIndex(Component comp) {
        Component[] comps = this.getComponents();

        for (int i = comps.length - 1; i >= 0; i--) {
            if (comp.equals(comps[i]))
                return i;
        }

        return -1; // should never reach this since a comp must be found
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private MouseEvent getZoomedEvent(MouseEvent e) {
        Point2D invZoomedPoint = null;

        try {
            invZoomedPoint = zoom.inverseTransform(e.getPoint(), null);
        } catch (NoninvertibleTransformException nite) {
            // when setting the zoom, it must have been checked that
            // the transform is invertible
        }

        MouseEvent newME = new MouseEvent((Component) e.getSource(), e.getID(),
                e.getWhen(), e.getModifiers(), (int) (invZoomedPoint.getX()),
                (int) (invZoomedPoint.getY()), e.getClickCount(), e
                        .isPopupTrigger());

        return newME;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private MouseEvent getZoomedEventDB(MouseEvent e) {
        Point2D invZoomedPoint = null;

        try {
            invZoomedPoint = zoom.inverseTransform(e.getPoint(), null);
        } catch (NoninvertibleTransformException nite) {
            // when setting the zoom, it must have been checked that
            // the transform is invertible
        }

        MouseEvent newME = new MouseEvent((Component) e.getSource(), e.getID(),
                e.getWhen(), e.getModifiers(), (int) (invZoomedPoint.getX()),
                (int) (invZoomedPoint.getY()), e.getClickCount(), e
                        .isPopupTrigger());

        return newME;
    }

    /**
     * Just calls <code>recurseAttributes</code>.
     * 
     * @param ge
     * @param gec
     */
    private void addAttributeComponents(GraphElement ge,
            GraphElementComponent gec) {
        recurseAttributes(ge.getAttributes(), gec);

        // // works only if attrComps map attr path -> attrComp class:
        // // (would be much faster, though)
        // //
        // for (Iterator it = acm.getAttributeComponents().keySet().iterator();
        // it.hasNext(); ) {
        //
        // String attrPath = (String)it.next();
        // try {
        // Attribute attribute = ge.getAttribute(attrPath);
        // AttributeComponent attrComp = acm.getAttributeComponent(attrPath);
        // attrComp.setShift(gec.getLocation());
        //
        // this.add(attrComp, 0);
        //
        // gec.addAttributeComponent(attribute, attrComp);
        //
        // attrComp.setAttribute(attribute);
        // attrComp.setGraphElementShape(gec.getShape());
        // try {
        // attrComp.createNewShape();
        // } catch (ShapeNotFoundException snfe) {
        // throw new RuntimeException
        // ("Should not happen since no shape is used here" + snfe);
        // }
        //
        // } catch (AttributeNotFoundException anfe) {
        // } catch (AttributeComponentNotFoundException acnfe) {
        // }
        // }
    }

    /**
     * Adjusts the preferred size of the view, so that it covers the given
     * component within the new preferred size - good for automatic scrolling
     * capability.
     * 
     * @param comp
     *            component to cover by the view
     */
    private void adjustPreferredSize(Component comp) {
        int newWidth = (int) (comp.getLocation().getX() + comp.getSize()
                .getWidth());
        int newHeight = (int) (comp.getLocation().getY() + comp.getSize()
                .getHeight());

        newWidth = Math.max(newWidth, 50);
        newHeight = Math.max(newHeight, 50);
    }

    /**
     * Adjusts the preferred size of the view, so that it covers all components
     * with the new preferred size - good for automatic scrolling capability.
     */
    private void adjustPreferredSize() {
        adjustPreferredSize(false);
    }

    /**
     * Adjusts the preferred size of the view, so that it covers all components
     * with the new preferred size - good for automatic scrolling capability.
     * 
     * @param shrink
     *            DOCUMENT ME!
     */
    private void adjustPreferredSize(boolean shrink) {
        Component[] components = getComponents();
        Point maxPos = new Point(50, 50);
        int compDownRightX;
        int compDownRightY;

        // calculates the size of the area has to be scrolled.
        for (int i = components.length - 1; i >= 0; i--) {
            compDownRightX = components[i].getX() + components[i].getWidth();
            compDownRightY = components[i].getY() + components[i].getHeight();

            maxPos.setLocation(Math.max(compDownRightX, maxPos.x), Math.max(
                    compDownRightY, maxPos.y));

            // zoom.transform(maxPos, maxPos);
        }

        if (shrink) {
            // shrink if necessary
            Point2D zoomedMax = zoom.transform(maxPos, null);
            Dimension minSize = new Dimension((int) zoomedMax.getX(),
                    (int) zoomedMax.getY());
            this.setSize(minSize);
            this.setPreferredSize(minSize);
        }

        autoresize(maxPos);

        repaint();
    }

    /**
     * Creates a new <code>EdgeComponent</code> and sets the NodeComponents
     * associated with this edge.
     * 
     * @param gecMap
     *            see createNodeComponent for its use
     * @param edge
     *            an edge for which this component will be built.
     * 
     * @return an edge component with associated node components, which
     *         represent components of source and target of the contained edge.
     * 
     * @see #completeRedraw() for an example.
     */
    protected EdgeComponent createEdgeComponent(
            Map<GraphElement, GraphElementComponent> gecMap, Edge edge) {
        EdgeComponent edgeComponent = (EdgeComponent) graphElementComponents
                .get(edge);

        Node s = edge.getSource();
        Node t = edge.getTarget();
        NodeComponent source = ((NodeComponent) gecMap.get(s));
        NodeComponent target = ((NodeComponent) gecMap.get(t));

        if (edgeComponent == null) {
            edgeComponent = (EdgeComponent) gecMap.get(edge);

            if (edgeComponent == null) {
                edgeComponent = new EdgeComponent(edge, source, target);
            }
        }

        gecMap.put(edge, edgeComponent);
        graphElementComponents.put(edge, edgeComponent);
        source.addDependentComponent(edgeComponent);
        target.addDependentComponent(edgeComponent);

        return edgeComponent;
    }

    /**
     * If there is a registered <code>AttributeComponent</code> for the given
     * attribute, add it to the view. If not, do nothing. Returns
     * <code>true</code> if a component was added, false otherwise.
     * 
     * @param attribute
     * @param gec
     * 
     * @return boolean
     */
    private boolean maybeAddAttrComponent(Attribute attribute,
            GraphElementComponent gec) {
        try {
            AttributeComponent attrComp = acm.getAttributeComponent(attribute
                    .getClass());

            if (gec.getAttributeComponent(attribute) != null) {
                // if attribute component already exists and
                // a completeRedraw was executed make sure the
                // component is drawn at the right moment
                attrComp = gec.getAttributeComponent(attribute);
                if (attrComp.getParent() == null) {
                    this.add(attrComp, getComponentIndex(gec));
                }
                return false;
            }

            gec.addAttributeComponent(attribute, attrComp);

            attrComp.setShift(gec.getLocation());
            attrComp.setAttribute(attribute);
            attrComp.setGraphElementShape(gec.getShape());

            try {
                attrComp.createNewShape();
            } catch (ShapeNotFoundException snfe) {
                throw new RuntimeException(
                        "Should not happen since no shape is used here" + snfe);
            }

            this.add(attrComp, getComponentIndex(gec));

            return true;
        } catch (AttributeComponentNotFoundException acnfe) {
            return false;
        }
    }

    /**
     * Recursively checks all attributes in the attribute tree with the given
     * attribute as root (using <code>maybeAddAttrComponent</code>).
     * 
     * @param attribute
     * @param gec
     */
    private void recurseAttributes(Attribute attribute,
            GraphElementComponent gec) {
        if (maybeAddAttrComponent(attribute, gec))
            return;

        if (attribute instanceof CollectionAttribute) {
            for (Attribute subAttribute : ((CollectionAttribute) attribute)
                    .getCollection().values()) {
                // if (!maybeAddAttrComponent(subAttribute, gec)) {
                recurseAttributes(subAttribute, gec);

                // }
            }

            // next call is correct decision but since e.g.
            // RenderedImageAttribute
            // throws
            // an exception when calling getAttributes, just treat it as a
            // normal attribute
            // } else if (attribute instanceof CompositeAttribute) {
            // recurseAttributes(
            // ((CompositeAttribute)attribute).getAttributes(), gec);
        }
    }

    /**
     * {@code MouseListener}, which translates the mouse coordinates according
     * to the current zoom.
     */
    class ZoomedMouseListener implements MouseListener {
        /** DOCUMENT ME! */
        private MouseListener listener;

        /**
         * Creates a new ZoomedMouseListener object.
         * 
         * @param l
         *            DOCUMENT ME!
         */
        public ZoomedMouseListener(MouseListener l) {
            this.listener = l;
        }

        /**
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e) {
            listener.mouseClicked(getZoomedEvent(e));
        }

        /**
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        public void mouseEntered(MouseEvent e) {
            listener.mouseEntered(getZoomedEvent(e));
        }

        /**
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(MouseEvent e) {
            listener.mouseExited(getZoomedEvent(e));
        }

        /**
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent e) {
            listener.mousePressed(getZoomedEvent(e));
        }

        /**
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent e) {
            listener.mouseReleased(getZoomedEvent(e));
        }
    }

    /**
     * {@code MouseMotionListener}, which translates the mouse coordinates
     * according to the current zoom.
     */
    class ZoomedMouseMotionListener implements MouseMotionListener {
        /** DOCUMENT ME! */
        private MouseMotionListener listener;

        /**
         * Creates a new ZoomedMouseMotionListener object.
         * 
         * @param l
         *            DOCUMENT ME!
         */
        public ZoomedMouseMotionListener(MouseMotionListener l) {
            this.listener = l;
        }

        /**
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged(MouseEvent e) {
            listener.mouseDragged(getZoomedEvent(e));
        }

        /**
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         */
        public void mouseMoved(MouseEvent e) {
            listener.mouseMoved(getZoomedEventDB(e));
        }
    }

    /**
     * See {@link JScrollPane}.
     * 
     * @return <code>true</code>.
     */
    public boolean embedsInJScrollPane() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Viewport getViewport() {
        return dummyViewport;
    }

    public void zoomToFitAfterRedraw() {
    }

    /*
     * @see org.graffiti.plugin.view.View2D#print(java.awt.Graphics2D, int, int)
     */
    public void print(Graphics2D g, int width, int height) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
