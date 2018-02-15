package org.visnacom.controller;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.Shape;

import javax.swing.*;

import org.visnacom.model.*;
import org.visnacom.sugiyama.*;
import org.visnacom.view.*;

import java.awt.event.*;
import java.util.*;
import java.awt.geom.*;


/**
 * 
 * @author F. Pfeiffer
 * 
 * This class implements the drawing space and its editing functions. Therefore
 * listeners are implemented. A thread manages scrolling out of the current
 * viewport.
 */
public class ViewPanel extends JPanel implements MouseListener,
		MouseMotionListener, Runnable, GeometryObserver {

	// contains preference settings
	private Preferences prefs;

	// Flags which represent the different graph editing modes
	private final static int TOG_CREATE = 0, TOG_MOVE = 1, TOG_LABEL = 2,
			TOG_INCLUDE = 3, TOG_COLL_EXP = 4;

	private Thread th;

	// flags indication dragging during selection and scrolling direction
	private boolean dragging = false, xScroll, yScroll, negXScroll, negYScroll;

	// flag indicating drawing of an edge
	private boolean drawingMode = false, autoClusterResize = true,
			clusEdge = false;

	// variables containg mouse locations
	private Point start, end;

	// variable for edge control point
	private Point2D.Double controlPoint = null;

	// edge that is not yet completely drawn
	private Object drawEdge;

	// association to corresponding Geometry object
	private Geometry geometry;

	// lists for selected edges,nodes and moving nodes
	private LinkedList selection, withinSelection, edgesWithinSel,
			edgesSelection, clusWithinSel, clusSelection, movingClus;

	// the current editing mode
	private int toggle;

	// the currently visible part of the scrollable drawing space
	private JViewport viewport;

	// necessary for enabling/disabling JButtons/menu items
	private GUIFrame gui;

	// animation style
	private AnimationStyle animStyle;

	// current coordinates of mouse pointer
	private Point mousePointer;

	// shows if frame is active
	private boolean active;

	/**
	 * Constructor.
	 *  
	 */
	public ViewPanel() {
		this.addMouseListener(this);
		geometry = new Geometry();
		geometry.attach(this);
		animStyle = new NoAnimation(geometry, this);
		prefs = geometry.getPrefs();
	}

	/**
	 * Constructor that sets preferences.
	 * 
	 * @param p
	 *            The preferences to be set.
	 * @param gui
	 *            Referenced GUI.
	 */
	public ViewPanel(Preferences p, GUIFrame gui) {
		// adds mouse listeners
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.gui = gui;
		this.prefs = p;

		DynamicLeaves dyn = new DynamicLeaves();

		View v = new View(dyn);
		geometry = new Geometry(v, prefs);
		geometry.attach(this);

		// creates lists
		selection = new LinkedList();
		withinSelection = new LinkedList();
		edgesWithinSel = new LinkedList();
		edgesSelection = new LinkedList();
		clusWithinSel = new LinkedList();
		clusSelection = new LinkedList();
		movingClus = new LinkedList();

		setDoubleBuffered(true);
		if (prefs.animation.equals("linear")) {
			animStyle = new AffinLinearAnimation(geometry, this);
		} else {
			animStyle = new NoAnimation(geometry, this);
		}

		//		animStyle = new NoAnimation(geometry, this);

		drawEdge = new Object();
		start = new Point();
		end = new Point();

		mousePointer = new Point();
		active = false;
	}

	/**
	 * The paint method. Visualizes the graph structure.
	 * 
	 * @param g
	 *            Graphics context.
	 */
	public void paint(Graphics g) {
		Iterator it;
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		// first paints the edges
		g2.setColor(prefs.lCol);
		it = getView().getAllEdges().iterator();
		g2.setStroke(new BasicStroke(1.0f));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		while (it.hasNext()) {
			Edge key = (Edge) it.next();
			Polyline pol = geometry.shape(key);
			if (pol != null) {
				g2.draw(pol.draw());

				if (prefs.edgeType.equals("directed")) {
					GeneralPath path = pol.drawArrowHead();
					if (path != null) {
						g2.draw(path);
					}
				}
			}
		}

		// paints the clusters (preorder traversal)
		ArrayList order = geometry.getClusterDrawingOrder();
		it = order.iterator();
		while (it.hasNext()) {
			Node clus = (Node) it.next();
			if (geometry.getView().isContracted(clus)) {
				LinkedList l = new LinkedList();
				l.add(clus);
				paintLeaves(g2, l);
			} else {
				if (getView().hasChildren(clus)) {
					paintCluster(g2, clus);
				}
			}
		}

		// marks items within selection focus
		if (toggle == TOG_CREATE || toggle == TOG_MOVE || toggle == TOG_INCLUDE
				|| toggle == TOG_COLL_EXP || toggle == TOG_LABEL) {
			drawSelection(g2, true);
		}

		// marks selected items
		drawSelection(g2, false);

		// draws the selection rectangle
		if ((toggle == TOG_MOVE || toggle == TOG_INCLUDE) && dragging) {
			drawSelectionRect(g2);
			drawSelection(g2, true);
		}

		g2.setColor(prefs.lCol);
		Polyline pol = geometry.drawEdgeShape(drawEdge);
		if (pol != null) {
			g2.setStroke(new BasicStroke(1.0f));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.draw(pol.draw());
		}

		// shows label if necessary
		if (toggle == TOG_LABEL) {
			Edge edg = geometry.findEdgeAtPoint(mousePointer);
			Node n = geometry.findNodeAtPoint(mousePointer);
			Node clus = geometry.findClusterAtPoint(mousePointer,
					new LinkedList());
			this.showLabel(n, clus, edg, g2);
		}
	}

	/**
	 * Paints leaves.
	 * 
	 * @param g2
	 *            Graphics context.
	 * @param l
	 *            List of leaves to be painted.
	 */
	private void paintLeaves(Graphics g2, java.util.List l) {
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			Point point = new Point();
			Rectangle rec = geometry.shape(n);
			if (rec != null) {
				point = rec.getLocation();
			} else {
				point = null;
			}
			if (point != null) {
				g2.setColor(prefs.nFrame);
				g2.fillRect((int) point.getX(), (int) point.getY(), rec.width,
						2);
				g2.fillRect((int) point.getX(), (int) point.getY(), 2,
						rec.height);
				g2.fillRect((int) point.getX() + rec.width - 2, (int) point
						.getY(), 2, rec.height);
				g2.fillRect((int) point.getX(), (int) point.getY() + rec.height
						- 2, rec.width, 2);
				if (getView().isContracted(n)) {
					if (geometry.getDrawingStyle().canBeExpanded(n)) {
						g2.setColor(prefs.clusFill);
					} else {
						g2.setColor(prefs.noExpansion);
					}
				} else {
					g2.setColor(prefs.nFill);
				}
				g2.fillRect((int) point.getX() + 2, (int) point.getY() + 2,
						rec.width - 4, rec.height - 4);
			}
		}
	}

	/**
	 * Paints clusters. (See above remark).
	 * 
	 * @param g2
	 *            Graphics context.
	 * @param n
	 *            Node to be painted.
	 */
	private void paintCluster(Graphics2D g2, Node n) {
		Iterator it;
		java.util.List nodes = getView().getChildLeaves(n);

		// gets shape of cluster
		Rectangle rect = geometry.shape(n);

		// paints cluster
		if (rect != null && n != getView().getRoot()) {
			g2.setColor(prefs.clusFrame);
			g2.setStroke(new BasicStroke(3.0f));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			if (geometry.getDrawingStyle().canBeContracted(n)) {
				g2.setColor(prefs.clusFrame);
			} else {
				g2.setColor(prefs.noContraction);
			}
			g2
					.drawRect(rect.x + 1, rect.y + 1, rect.width - 2,
							rect.height - 2);
			g2.setColor(new Color(prefs.clusFill.getRed(), prefs.clusFill
					.getGreen(), prefs.clusFill.getBlue(), 200));
			g2
					.fillRect(rect.x + 1, rect.y + 1, rect.width - 2,
							rect.height - 2);
		}

		g2.setStroke(new BasicStroke(1.0f));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// paints the leaves contained in cluster
		paintLeaves(g2, nodes);
		g2.setColor(prefs.lCol);
		//		it = clusterNodes.iterator();
		it = getView().getChildrenIterator(n);

		// paints edges
		while (it.hasNext()) {
			Node no = (Node) it.next();
			LinkedList l = (LinkedList) getView().getAdjEdges(no);
			Iterator it2 = l.iterator();
			while (it2.hasNext()) {
				Edge ed = (Edge) it2.next();
				Polyline poly = geometry.shape(ed);
				if (poly != null) {
					g2.setStroke(new BasicStroke(1.0f));
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					g2.draw(poly.draw());

					// paints arrowhead for directed edges
					if (prefs.edgeType.equals("directed")) {
						GeneralPath path = poly.drawArrowHead();
						if (path != null) {
							g2.draw(path);
						}
					}
				}
			}
		}
	}

	/**
	 * Draws the selection rectangle.
	 * 
	 * @param g
	 *            Graphics context.
	 */
	private void drawSelectionRect(Graphics g) {
		g.setColor(Color.black);
		Graphics2D g2 = (Graphics2D) g;
		int width = Math.abs(end.x - start.x);
		int height = Math.abs(end.y - start.y);

		Stroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0, new float[] { 12, 12 }, 0);
		g2.setStroke(stroke);

		// draws rectangle according to mouse locations
		if (start.x <= end.x && start.y <= end.y) {
			g.drawRect(start.x, start.y, width, height);
		} else if (start.x <= end.x && start.y >= end.y) {
			g.drawRect(start.x, start.y - height, width, height);
		} else if (start.x >= end.x && start.y <= end.y) {
			g.drawRect(end.x, start.y, width, height);
		} else if (start.x >= end.x && start.y >= end.y) {
			g.drawRect(end.x, end.y, width, height);
		}
	}

	/**
	 * Marks selected items.
	 * 
	 * @param g
	 *            Graphics context.
	 * @param within
	 *            True iff elements are only within selection focus.
	 */
	private void drawSelection(Graphics g, boolean within) {
		if (!within) {
			g.setColor(prefs.selected);
		} else {
			g.setColor(prefs.select);
		}
		// paints markings for nodes
		Iterator it;
		if (!within) {
			it = selection.iterator();
		} else {
			it = withinSelection.iterator();
		}
		while (it.hasNext()) {
			this.drawSelectionNode(g, (Node) it.next());
		}
		// marks edges
		if (!within) {
			it = edgesSelection.iterator();
		} else {
			it = edgesWithinSel.iterator();
		}
		while (it.hasNext()) {
			Object next = it.next();
			Polyline poly = geometry.shape((Edge) next);
			if (poly != null) {
				ArrayList l = poly.drawSelection();
				LinkedList pPoints = (LinkedList) l.get(0);
				LinkedList cPoints = (LinkedList) l.get(1);
				LinkedList connPoints = (LinkedList) l.get(2);

				// path points
				Iterator it2 = pPoints.iterator();
				while (it2.hasNext()) {
					Point2D.Double p2D = (Point2D.Double) it2.next();
					g.fillRect(((int) p2D.x) - 2, ((int) p2D.y) - 2, 5, 5);
				}

				// control points
				it2 = cPoints.iterator();
				while (it2.hasNext()) {
					Point2D.Double p2D = (Point2D.Double) it2.next();
					g.fillRect(((int) p2D.x) - 1, ((int) p2D.y) - 1, 3, 3);
				}

				// connection points
				it2 = connPoints.iterator();
				while (it2.hasNext()) {
					Point2D.Double p2D = (Point2D.Double) it2.next();
					g.fillRect((int) p2D.x, (int) p2D.y, 5, 5);
				}
			}
		}
		// draws selected clusters
		if (!within) {
			drawClusSelection(g, 1);
		} else {
			drawClusSelection(g, 0);
		}
	}

	/**
	 * Draws markings for selected nodes.
	 * 
	 * @param g
	 *            Graphics context.
	 * @param n
	 *            The node to be displayed as selected.
	 */
	private void drawSelectionNode(Graphics g, Node n) {
		if (n != getView().getRoot()) {
			Rectangle rec = geometry.shape(n);
			Point p = null;
			if (rec != null) {
				p = rec.getLocation();
			} else {
				p = null;
			}

			// paints four small markings
			if (p != null) {
				g.fillRect((int) p.getX(), (int) p.getY(), 5, 5);
				g
						.fillRect((int) p.getX() + rec.width - 5, (int) p
								.getY(), 5, 5);
				g.fillRect((int) p.getX(), (int) p.getY() + rec.height - 5, 5,
						5);
				g.fillRect((int) p.getX() + rec.width - 5, (int) p.getY()
						+ rec.height - 5, 5, 5);
			}
		}
	}

	/**
	 * Draws the selection markings for clusters.
	 * 
	 * @param g
	 *            Graphics context.
	 * @param selType
	 *            The type of selection (within selection range or already
	 *            selected).
	 */
	private void drawClusSelection(Graphics g, int selType) {

		Iterator it = null;
		// sets color according to selection type
		if (selType == 0) {
			it = clusWithinSel.iterator();
			g.setColor(prefs.select);
		} else {
			it = clusSelection.iterator();
			g.setColor(prefs.selected);
		}

		while (it.hasNext()) {
			Node n = (Node) it.next();
			if (n == getView().getRoot()) {
				continue;
			}
			Rectangle rec = geometry.shape(n);
			int x, y;
			if (!geometry.getView().isContracted(n)) {
				// paints four small rectangles as markings
				if (!(rec == null)) {
					Point point = rec.getLocation();
					Point point2 = new Point((int) (point.x + rec.getWidth()),
							(int) (point.y + rec.getHeight()));
					x = point.x;
					y = point.y;
					g.fillRect(x, y, 7, 7);
					y = (point.y) + (point2.y - point.y);
					g.fillRect(x, y - 7, 7, 7);
					x = (point.x) + (point2.x - point.x);
					g.fillRect(x - 7, point.y, 7, 7);
					g.fillRect(x - 7, y - 7, 7, 7);
				}
			} else {
				this.drawSelectionNode(g, n);
			}
		}
	}

	/**
	 * Creates a new node on the panel.
	 * 
	 * @param coord
	 *            Coordinates of the node.
	 * @param par
	 *            The parent of the node to be created.
	 */
	private void placeNode(Point coord, Node par) {
		Node n = getView().newLeaf(par);
		geometry.shape(n).setLocation(coord);
		geometry.shape(n).setSize(
				new Dimension(prefs.leafWidth, prefs.leafHeight));
		selection.clear();
		edgesSelection.clear();
		geometry.getMovingPoints().clear();
		// new node is selected
		selection.add(n);
		enableCutCopy();
		Point p = (geometry.shape(n)).getLocation();
		geometry.getMovingPoints().put(n, (Point) p.clone());
		resizeViewPanel(geometry.shape(n));
		geometry.resizeTillRoot(par);
	}

	/**
	 * Completes drawing of an edge.
	 * 
	 * @param n
	 *            The target node.
	 */
	private void completeEdge(Node n) {

		// gets start shape
		Node polyStart = null;
		if (!clusEdge) {
			polyStart = geometry.findNodeAtPoint(start.getLocation());
		} else {
			polyStart = geometry.findClusterAtPoint(start.getLocation(),
					new LinkedList());
		}
		Polyline polyline = geometry.drawEdgeShape(drawEdge);

		try {
			Edge edg = geometry.getView().newEdge(polyStart, n);

			List ctrlPoints = polyline.getControlPoints();
			Polyline poly = geometry.shape(edg);

			// adds control points
			Iterator it = ctrlPoints.iterator();
			while (it.hasNext()) {
				Point2D.Double p = (Point2D.Double) it.next();
				poly.addControl(p);
			}

			geometry.removeDrawEdge(drawEdge);
			drawingMode = false;
			edgesSelection.clear();
			// new edge is selected
			edgesSelection.add(edg);
			enableDel();
		} catch (InvalidEdgeException exc) {
			gui.showError("Invalid edge", exc.getMessage());
		}
	}

	/**
	 * Starts drawing an edge.
	 * 
	 * @param e
	 *            Mouse event triggering drawing of an edge.
	 * @param n
	 *            The source node.
	 */
	private void beginEdge(MouseEvent e, Node n) {
		selection.clear();
		disableCutCopy();
		edgesSelection.clear();
		withinSelection.clear();
		clusWithinSel.clear();
		if (getView().isCluster(n)) {
			clusWithinSel.add(n);
		} else {
			withinSelection.add(n);
		}
		// sets the flag for drawing edges
		drawingMode = true;
		Rectangle startRec = new Rectangle();
		Rectangle startNode = geometry.shape(n);

		// sets starting shape
		if (startNode != null) {
			start = e.getPoint().getLocation();
			startRec = startNode;
		}

		// checks whether starting node is cluster
		if (getView().isCluster(n)) {
			clusEdge = true;
		} else {
			clusEdge = false;
		}
		//		geometry.getEdges().remove(drawEdge);
		geometry.removeDrawEdge(drawEdge);
		// makes a new drawEdge
		geometry.putDrawEdge(drawEdge, new Polyline(startRec, new Rectangle(),
				prefs));
		drawEdge(e.getPoint());
	}

	/**
	 * Initiates movement of a single item.
	 * 
	 * @param e
	 *            Mouse event triggering movement.
	 */
	private void initiateMovementSingle(MouseEvent e) {
		// gets elements at coordinate
		Node n = geometry.findNodeAtPoint(e.getPoint());
		Edge edge = geometry.findEdgeAtPoint(e.getPoint());
		Node clus = geometry.findClusterAtPoint(e.getPoint(), new LinkedList());

		// movement of leaves
		if (n != null) {
			selection.clear();
			disableCutCopy();
			edgesSelection.clear();
			geometry.getMovingPoints().clear();
			selection.add(n);
			enableCutCopy();
			//adds node to the list of items to be moved
			Point p = (geometry.shape(n)).getLocation();
			geometry.getMovingPoints().put(n, (Point) p.clone());

			// initiates cluster movement (of contracted cluster)
		} else if (clus != getView().getRoot() && getView().isContracted(clus)) {
			selection.clear();
			clusSelection.clear();
			disableCutCopy();
			edgesSelection.clear();
			geometry.getMovingPoints().clear();
			clusSelection.add(clus);
			geometry.getMovingPoints().clear();
			movingClus.clear();
			LinkedList l = new LinkedList();

			// contained leaves are moved likewise
			if (!geometry.getView().isContracted(clus)) {
				getView().getLeaves(clus, l);
			}

			// contained leaves are selected, prepared for movement
			Iterator it = l.iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				selection.add(node);
				Point p = (geometry.shape(node)).getLocation();
				if (p != null) {
					geometry.getMovingPoints().put(node, p.clone());
				}
			}

			// clusters prepared for movement
			it = clusSelection.iterator();
			while (it.hasNext()) {
				Node cl = (Node) it.next();
				Rectangle rec = geometry.shape(cl);
				Point p = rec.getLocation();
				geometry.getMovingPoints().put(cl, p.clone());
				movingClus.add(cl);
			}

			clusSelection.clear();
			it = selection.iterator();
			while (it.hasNext()) {
				Node no = (Node) it.next();
				Node noPar = getView().getParent(no);
				if (!clusSelection.contains(noPar)) {
					clusSelection.add(noPar);
				}
			}

			// contained edges are selected
			edgesSelection = (LinkedList) getContainedEdges(clus);

			it = movingClus.iterator();
			while (it.hasNext()) {
				clusSelection.add((Node) it.next());
			}
			enableCutCopy();

			// edge selection
		} else if (edge != null) {
			// selects an edge
			edgesSelection.clear();
			edgesSelection.add(edge);
			enableDel();

			// initiates cluster movement
		} else if (clus != getView().getRoot() && !getView().isContracted(clus)) {
			Iterator it;
			selection.clear();
			clusSelection.clear();
			disableCutCopy();
			edgesSelection.clear();
			geometry.getMovingPoints().clear();
			clusSelection.add(clus);
			geometry.getMovingPoints().clear();
			movingClus.clear();
			List l = new LinkedList();
			if (!geometry.getView().isContracted(clus)) {
				l = getView().allDescendants(clus);
			}

			it = l.iterator();
			while (it.hasNext()) {
				Node cl = (Node) it.next();
				if (!clusSelection.contains(cl) && getView().isCluster(cl)) {
					clusSelection.add(cl);
				}
			}

			// contained leaves are moved likewise
			l.clear();
			if (!geometry.getView().isContracted(clus)) {
				geometry.getView().getLeaves(clus, l);
			}
			it = l.iterator();
			while (it.hasNext()) {
				Node cl = (Node) it.next();
				// contracted clusters as leaves
				if (getView().isCluster(cl)) {
					clusSelection.add(cl);
				}
			}
			// leaves which are no clusters get prepared
			it = l.iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				if (getView().isCluster(node)) {
					continue;
				}
				Point p = null;
				if (geometry.shape(node) != null) {
					selection.add(node);
					p = ((geometry.shape(node)).getLocation());
				} else {
					p = null;
				}
				if (p != null) {
					geometry.getMovingPoints().put(node, p.clone());

				}
			}

			// selected clusters are prepared for movement
			it = clusSelection.iterator();
			while (it.hasNext()) {
				Node cl = (Node) it.next();
				Rectangle rec = geometry.shape(cl);
				Point p = rec.getLocation();
				geometry.getMovingPoints().put(cl, p.clone());
				movingClus.add(cl);
			}

			clusSelection.clear();
			it = selection.iterator();
			while (it.hasNext()) {
				Node no = (Node) it.next();
				Node noPar = getView().getParent(no);
				if (!clusSelection.contains(noPar)) {
					clusSelection.add(noPar);
				}
			}

			// contained edges are selected
			edgesSelection = (LinkedList) getContainedEdges(clus);

			it = movingClus.iterator();
			while (it.hasNext()) {
				Node next = (Node) it.next();
				if (!clusSelection.contains(next)) {
					clusSelection.add(next);
				}
			}
			enableCutCopy();
		}
	}

	/**
	 * Moves all selected items due to mouse dragging.
	 *  
	 */
	private void moveSelection() {
		boolean newScrolling = !(xScroll || yScroll || negXScroll || negYScroll);
		yScroll = false;
		xScroll = false;
		negXScroll = false;
		negYScroll = false;
		LinkedList sel = new LinkedList();
		sel.addAll(selection);
		sel.addAll(movingClus);
		Iterator it = sel.iterator();

		// flags indicating scrolling in x-/y-direction
		boolean globalX = true;
		boolean globalY = true;
		Iterator itGlobal = geometry.getMovingPoints().entrySet().iterator();

		// makes sure that scrooling does not lead to negative coordinates
		while (itGlobal.hasNext()) {
			Map.Entry entry = (Map.Entry) itGlobal.next();
			Point globalP = (Point) entry.getValue();
			if (globalP.x <= 0) {
				globalX = false;
			} else if (globalP.y <= 0) {
				globalY = false;
			}
		}

		// determines scrolling direction and resizes ViewPanel
		while (it.hasNext()) {
			Node n = (Node) it.next();
			Point p = new Point();
			Rectangle shape = geometry.shape(n);

			if (shape != null) {
				p = (shape).getLocation();
			} else {
				p = null;
			}

			// getting scrolling directions
			if (p != null) {
				if (p.x + shape.width >= viewport.getSize().width
						+ viewport.getViewPosition().x) {
					xScroll = true;
				}
				if (p.y + shape.height >= viewport.getSize().height
						+ viewport.getViewPosition().y) {
					yScroll = true;
				}
				if (p.x + shape.width <= viewport.getViewPosition().x
						&& viewport.getViewPosition().x > 0 && globalX) {
					negXScroll = true;
				}
				if (p.y + shape.height <= viewport.getViewPosition().y
						&& viewport.getViewPosition().y > 0 && globalY) {
					negYScroll = true;
				}
				Point p2 = (Point) geometry.getMovingPoints().get(n);
				if (p2 == null) {
					continue;
				}
				int newX = p2.x + end.x - start.x;
				int newY = p2.y + end.y - start.y;
				resizeViewPanel(new Rectangle(new Point(newX, newY),
						new Dimension(shape.width, shape.height)));
				//moves incident edges of nodes
				//				if (movingClus.contains(n)||true) {
				(geometry.shape(n)).setLocation(newX, newY);
				LinkedList adj = (LinkedList) geometry.getView().getAdjEdges(n);
				Iterator adjIt = adj.iterator();
				while (adjIt.hasNext()) {
					Edge edg = (Edge) adjIt.next();
					if (n == edg.getSource()) {
						Polyline poly = geometry.shape(edg);
						if (poly != null && poly.getStart() != null) {
							poly.getStart().setLocation(newX, newY);
						}
					} else if (n == edg.getTarget()) {
						Polyline poly = geometry.shape(edg);
						if (poly != null && poly.getEnd() != null) {
							poly.getEnd().setLocation(newX, newY);
						}
					}
				}
				repaint();
			}
		}
		// scrolling thread is started
		if ((xScroll || yScroll || negXScroll || negYScroll) && newScrolling) {
			th = new Thread(this);
			th.start();
		}

		// gets parent with smallest depth (needed for automatic resizing)
		LinkedList all = new LinkedList();
		all.addAll(selection);
		all.addAll(clusSelection);
		Node level = null;
		if (!selection.isEmpty()) {
			level = (Node) selection.getFirst();
		} else {
			level = (Node) clusSelection.getFirst();
		}
		it = all.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			//			this.ancClusEdges(n);
			if (getView().inclusionDepth(n).compareTo(
					getView().inclusionDepth(level)) < 0) {
				level = n;
			}
		}

		// resizes parent nodes if necessary
		if (this.autoClusterResize) {
			resizeAnc(level);
		}
	}

	/**
	 * Resizes all nodes from given node upwards to root.
	 * 
	 * @param clus
	 *            The node where the resizing is to be started.
	 */
	private void resizeAnc(Node clus) {
		Node par = getView().getParent(clus);
		while (par != getView().getRoot() && par != null) {
			geometry.autoResize(par);
			par = getView().getParent(par);
		}
	}

	/**
	 * Draws an edge during drawing mode.
	 * 
	 * @param e
	 *            Mouse event triggering action.
	 */
	private void drawEdge(Point e) {
		end = e;
		Rectangle startRec = geometry.drawEdgeShape(drawEdge).getStart();

		// nothing to draw during drawing of new edge
		if (!(startRec.x + 0.5 * startRec.width == e.x && start.y + 0.5
				* startRec.height == e.y)) {

			// scrolls and enlarges drawing space when necessary
			(geometry.drawEdgeShape(drawEdge)).setEnd(new Rectangle(e,
					new Dimension(0, 0)));
			if (end.x >= this.getPreferredSize().width - 35) {
				this.enlargeViewPanel(100, 0);
			}
			if (end.y >= this.getPreferredSize().height - 35) {
				this.enlargeViewPanel(0, 100);
			}
			if (e.getX() >= viewport.getViewPosition().x
					+ viewport.getSize().width - 20) {
				xScroll = true;
			} else {
				xScroll = false;
			}
			if (e.getY() >= viewport.getViewPosition().y
					+ viewport.getSize().height - 20) {
				yScroll = true;
			} else {
				yScroll = false;
			}
			if (e.getX() <= viewport.getViewPosition().x + 20 && e.getX() > 20) {
				negXScroll = true;
			} else {
				negXScroll = false;
			}
			if (e.getY() <= viewport.getViewPosition().y + 20 && e.getY() > 20) {
				negYScroll = true;
			} else {
				negYScroll = false;
			}
		}
	}

	/**
	 * Implements reactions to mouse clicks.
	 * 
	 * @param e
	 *            The event.
	 */
	public void mouseClicked(MouseEvent e) {
		// in order to avoid any actions before activating
		// internal frame
		if (active) {
			withinSelection.clear();
			clusWithinSel.clear();
			if (e.getButton() == MouseEvent.BUTTON1) {
				clusSelection.clear();
				// create
				if (toggle == TOG_CREATE) {
					Node n = geometry.findNodeAtPoint(e.getPoint());
					Node clus = geometry.findClusterAtPoint(e.getPoint(),
							new LinkedList());
					// puts a new node
					if (n == null && !drawingMode
							&& clus.equals(getView().getRoot())) {
						Point coord = new Point(e.getX() - prefs.leafWidth / 2,
								e.getY() - prefs.leafHeight / 2);
						placeNode(coord, getView().getRoot());
						//makes node the ending node of edge
					} else if (n != null && drawingMode) {
						completeEdge(n);
						// makes node the starting of edge
					} else if (n != null && !drawingMode) {
						beginEdge(e, n);
						// adds new control point to edge
					} else if (n == null && clus == this.getView().getRoot()
							&& drawingMode) {
						(geometry.drawEdgeShape(drawEdge)).addControl(e
								.getPoint());
					} else if (clus != this.getView().getRoot() && !drawingMode) {
						beginEdge(e, clus);
					} else if (clus != this.getView().getRoot() && drawingMode) {
						completeEdge(clus);
					}

					// move
				} else if (toggle == TOG_MOVE) {
					selection.clear();
					initiateMovementSingle(e);

					// text labels
				} else if (toggle == TOG_LABEL) {
					this.textLabel(e);

					// preparations for split/merge
				} else if (toggle == TOG_INCLUDE) {
					Node n = geometry.findNodeAtPoint(e.getPoint());
					Node clus = geometry.findClusterAtPoint(e.getPoint(),
							new LinkedList());
					if (n != null && !selection.contains(n)) {
						selection.clear();
						disableCutCopy();
						edgesSelection.clear();
						geometry.getMovingPoints().clear();
						selection.add(n);
						enableCutCopy();
						repaint();
					} else if (clus != getView().getRoot()) {
						selection.clear();
						clusSelection.clear();
						disableCutCopy();
						edgesSelection.clear();
						geometry.getMovingPoints().clear();
						clusSelection.add(clus);
						enableCutCopy();
						repaint();
					}

					// expand/collapse
				} else if (toggle == TOG_COLL_EXP) {
					clusSelection.clear();
					selection.clear();
					Node clus = geometry.findClusterAtPoint(e.getPoint(),
							new LinkedList());
					if (clus != getView().getRoot()
							&& getView().isCluster(clus)) {
						if (!geometry.getView().isContracted(clus)) {
							if (geometry.getDrawingStyle()
									.canBeContracted(clus)) {
								geometry.setResize(false);
								geometry.getView().contract(clus);
								geometry.setResize(true);
								geometry.resizeAll(getView().getRoot());
							}
						} else {
							if (geometry.getDrawingStyle().canBeExpanded(clus)) {
								geometry.getView().expand(clus);
								selection.clear();
								clusSelection.clear();
							}
						}
						edgesSelection.clear();
					}
				}

				// right mouse button
			} else {
				// all selection are removed
				if (toggle == TOG_CREATE || toggle == TOG_MOVE
						|| toggle == TOG_LABEL) {
					Node clus = geometry.findClusterAtPoint(e.getPoint(),
							new LinkedList());
					if (clus != null && !drawingMode && toggle == TOG_CREATE) {
						Point coord = new Point(e.getX() - prefs.leafWidth / 2,
								e.getY() - prefs.leafHeight / 2);
						placeNode(coord, clus);
					} else {
						selection.clear();
						clusSelection.clear();
						disableCutCopy();
						edgesSelection.clear();
						drawingMode = false;
						dragging = false;
						geometry.getMovingPoints().clear();
						geometry.removeDrawEdge(drawEdge);
					}

					// split/merge
				} else if (toggle == TOG_INCLUDE) {
					if (e.isControlDown()) {
						if (clusSelection.size() != 1) {
							gui
									.showError(
											"Invalid number of selected clusters",
											"You need to select exactly one cluster \nwhich is to be merged!");
							selection.clear();
						} else {
							Node clus = (Node) clusSelection.getFirst();
							// merge
							getView().merge(clus);
							selection.clear();
							geometry.getMovingPoints().clear();
							repaint();
						}
					} else if (!e.isControlDown()) {
						// splitting
						Node cluster = include();
						edgesSelection.clear();
						clusSelection.clear();
						selection.clear();
						if (cluster != null) {
							geometry.autoResize(cluster);
						}
						repaint();
					} else {
						selection.clear();
					}
				}
				clusSelection.clear();
			}
		}
		// redraws if sugi is current drawing style
		if (!prefs.algorithm.equals("default")
				&& (toggle == TOG_CREATE || (toggle == TOG_INCLUDE && e
						.getButton() != MouseEvent.BUTTON1)) && !drawingMode) {
			geometry.setDrawingStyle(new SugiyamaDrawingStyle(geometry));
			redraw();
		}
		repaint();
	}

	/**
	 * Gets text label for a graph element.
	 * 
	 * @param e
	 *            MouseEvent.
	 */
	private void textLabel(MouseEvent e) {
		String label = "";
		Node n = geometry.findNodeAtPoint(e.getPoint());
		Node clus = geometry.findClusterAtPoint(e.getPoint(), new LinkedList());
		Edge edge = geometry.findEdgeAtPoint(e.getPoint());

		// opens correct dialogs according to selected element
		if (n != null) {
			label = gui.showLabelDialog("leaf", getBaseGraph().getLeafLabel(n));
			if (checkLabel(label)) {
				getBaseGraph().putLeafLabel(n, label);
			}
		} else if (clus != getView().getRoot()
				&& geometry.getView().isContracted(clus)) {
			label = gui.showLabelDialog("cluster", getBaseGraph().getClusLabel(
					clus));
			if (checkLabel(label)) {
				getBaseGraph().putClusLabel(clus, label);
			}
		} else if (edge != null) {
			if (!getBaseGraph().getEdge(edge.getSource(), edge.getTarget())
					.isEmpty()) {
				label = gui.showLabelDialog("edge", getBaseGraph()
						.getEdgeLabel(edge));
				if (checkLabel(label)) {
					getBaseGraph().putEdgeLabel(edge, label);
				}
			} else {
				gui.showError("Label error", "Derived edge cannot be labelled");
			}
		} else if (clus != getView().getRoot()
				&& !geometry.getView().isContracted(clus)) {
			label = gui.showLabelDialog("cluster", getBaseGraph().getClusLabel(
					clus));
			if (checkLabel(label)) {
				getBaseGraph().putClusLabel(clus, label);
			}
		}
	}

	/**
	 * Checks if label is valid.
	 * 
	 * @param label
	 *            The label to be checked.
	 * @return True iff label has at most 30 characters.
	 */
	private boolean checkLabel(String label) {
		if (label == null || label.length() <= 30) {
			return true;
		}
		gui.showError("Invalis label", "Label has more than 30 characters!");
		return false;
	}

	/**
	 * Implements reactions to a pressed mouse button.
	 * 
	 * @param e
	 *            The event.
	 */
	public void mousePressed(MouseEvent e) {
		controlPoint = null;
		// in order to avoid any actions before activating
		// internal frame
		if (active && e.getButton() != MouseEvent.NOBUTTON) {
			if (toggle == TOG_CREATE) {

				// move
			} else if (toggle == TOG_MOVE) {
				start = e.getPoint();
				end = e.getPoint();

				// supresses automatic cluster resizing while dragging items
				// out of cluster
				if (e.getButton() != MouseEvent.BUTTON1) {
					autoClusterResize = false;
				}

				Node n = geometry.findNodeAtPoint(start.getLocation());
				Point2D.Double contrPoint = getContrPoint(start.getLocation());
				Node clus = getView().getRoot();

				if (n == null) {
					clus = geometry.findClusterAtPoint(start.getLocation(),
							new LinkedList());
				}

				if (n == null && clus == getView().getRoot()
						&& contrPoint == null) {
					dragging = true;
				}

				// moves control point
				if (contrPoint != null) {
					controlPoint = contrPoint;

					// moves a single node
				} else if ((n != null && !selection.contains(n))
						|| (clus != getView().getRoot() && !clusSelection
								.contains(clus))) {
					this.selection.clear();
					this.clusSelection.clear();
					this.withinSelection.clear();
					this.clusWithinSel.clear();
					this.edgesSelection.clear();
					this.edgesWithinSel.clear();
					initiateMovementSingle(e);

					// move all selected items
				} else if (n != null && selection.contains(n)) {
					dragging = false;

					// moving cluster
				} else if (clus != null && clusSelection.contains(clus)) {
					dragging = false;

					// selection rectangle
				} else {
					dragging = true;
					edgesSelection.clear();
				}

				// starts dragging for selection rectangle
			} else if (toggle == TOG_INCLUDE
					&& (e.getButton() == MouseEvent.BUTTON1)) {
				start = e.getPoint();
				end = e.getPoint();
				dragging = true;
				edgesSelection.clear();
				edgesWithinSel.clear();
				selection.clear();
			}
		}
	}

	/**
	 * Realizes reactions releassed mouse buttons.
	 * 
	 * @param e
	 *            The event.
	 */
	public void mouseReleased(MouseEvent e) {
		// in order to avoid any actions before activating
		// internal frame
		if (active) {
			withinSelection.clear();
			end = new Point();
			Iterator it;
			if (toggle == TOG_CREATE) {
				// move
			} else if (toggle == TOG_MOVE) {
				end = e.getPoint();

				// gets selected nodes within selection rectangle
				LinkedList l = geometry.nodeRangeQuery(start.getLocation(), end
						.getLocation());
				if (dragging) {
					selection = l;
					clusSelection.clear();
					it = clusWithinSel.iterator();
					while (it.hasNext()) {
						clusSelection.add((Node) it.next());
					}

					// gets selected edges within selection rectangle
					edgesSelection = geometry.edgeRangeQuery(start
							.getLocation(), end.getLocation(), true);
					it = clusSelection.iterator();
					// adds contained edges to selection
					while (it.hasNext()) {
						Node cNode = (Node) it.next();
						LinkedList cList = (LinkedList) getContainedEdges(cNode);
						Iterator it2 = cList.iterator();
						while (it2.hasNext()) {
							Edge edg = (Edge) it2.next();
							if (!edgesSelection.contains(edg)) {
								edgesSelection.add(edg);
							}
						}
					}
					if (!selection.isEmpty() || !edgesSelection.isEmpty()) {
						enableCutCopy();
					}
				}
				prepareMovement();

				// flag indication enlargement due to contracted cluster
				boolean flag = false;
				it = clusSelection.iterator();
				while (it.hasNext()) {
					Node n = (Node) it.next();
					if (getView().isContracted(n)) {
						flag = true;
					}
				}

				// enlarges cluster if necessary
				if ((!selection.isEmpty() || flag)
						&& e.getButton() != MouseEvent.BUTTON1) {
					enlargeCluster(e);
				}

				// selection in split/merge mode
			} else if (toggle == TOG_INCLUDE && dragging) {
				end = e.getPoint();

				// gets selected items within selection rectangle
				LinkedList l = geometry.nodeRangeQuery(start.getLocation(), end
						.getLocation());
				if (dragging) {
					selection = l;
					clusSelection.clear();
					it = clusWithinSel.iterator();
					while (it.hasNext()) {
						clusSelection.add((Node) it.next());
					}
					edgesSelection = geometry.edgeRangeQuery(start
							.getLocation(), end.getLocation(), true);
					if (!edgesSelection.isEmpty()) {
						enableCutCopy();
					}
				}
				geometry.getMovingPoints().clear();
			}

			// clones polylines
			it = getView().getAllEdgesIterator();
			while (it.hasNext()) {
				(geometry.shape((Edge) it.next())).reclone();
			}

			// no editing modes at all
			clusWithinSel.clear();
			dragging = false;
			xScroll = false;
			yScroll = false;
			negXScroll = false;
			negYScroll = false;
			Polyline.setReloc(false);
			edgesWithinSel.clear();
			autoClusterResize = true;

			repaint();
		}
	}

	/**
	 * Implements reactions to mouse event.
	 * 
	 * @param e
	 *            The event.
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Implements reactions to mouse event.
	 * 
	 * @param e
	 *            The event.
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Implements reactions to dragged mouse pointer.
	 * 
	 * @param e
	 *            The event.
	 */
	public void mouseDragged(MouseEvent e) {
		if (active) {
			Polyline.setReloc(true);
			end = e.getPoint();
			// dragging due to selection rectangle
			if (toggle == TOG_MOVE && dragging && controlPoint == null) {
				// gets edges, nodes within selection rectangle
				withinSelection = geometry.nodeRangeQuery(start.getLocation(),
						end.getLocation());
				edgesWithinSel = geometry.edgeRangeQuery(start.getLocation(),
						end.getLocation(), true);
				this.clusWithinSel = geometry.clusterRangeQuery(start
						.getLocation(), end.getLocation());
				Iterator it = clusWithinSel.iterator();
				while (it.hasNext()) {
					Node c = (Node) it.next();
					LinkedList cEdges = (LinkedList) getContainedEdges(c);
					Iterator it2 = cEdges.iterator();
					while (it2.hasNext()) {
						Edge edg = (Edge) it2.next();
						edgesWithinSel.add(edg);
					}
				}
				repaint();
				// moving selected items
			} else if (toggle == TOG_MOVE && !dragging && controlPoint == null
					&& (!clusSelection.isEmpty() || !selection.isEmpty())) {
				moveSelection();
				// moving control point
			} else if (toggle == TOG_MOVE && controlPoint != null) {
				controlPoint.setLocation(end.x, end.y);
				repaint();
			} else if (toggle == TOG_INCLUDE && dragging) {
				// gets edges, nodes within selection rectangle
				withinSelection = geometry.nodeRangeQuery(start.getLocation(),
						end.getLocation());
				this.clusWithinSel = geometry.clusterRangeQuery(start
						.getLocation(), end.getLocation());
				Iterator it = clusWithinSel.iterator();
				edgesWithinSel.clear();
				while (it.hasNext()) {
					Node c = (Node) it.next();
					LinkedList cEdges = (LinkedList) getContainedEdges(c);
					Iterator it2 = cEdges.iterator();
					while (it2.hasNext()) {
						Edge edg = (Edge) it2.next();
						edgesWithinSel.add(edg);
					}
				}
				repaint();
			}
		}
	}

	/**
	 * Implements reactions to mouse movings.
	 * 
	 * @param e
	 *            The event.
	 */
	public void mouseMoved(MouseEvent e) {
		if (active) {
			mousePointer = e.getPoint();
			Node n = geometry.findNodeAtPoint(e.getPoint());
			Node clus = geometry.findClusterAtPoint(e.getPoint(),
					new LinkedList());

			boolean newScrolling = !(xScroll || yScroll || negXScroll || negYScroll);

			// shows node within selection range of mouse pointer
			if (n != null && !selection.contains(n)) {
				withinSelection.clear();
				clusWithinSel.clear();
				edgesWithinSel.clear();
				withinSelection.add(n);
			} else if (clus != null && clus != getView().getRoot()) {
				clusWithinSel.clear();
				withinSelection.clear();
				edgesWithinSel.clear();
				clusWithinSel.add(clus);
				repaint();
			} else {
				withinSelection.clear();
				clusWithinSel.clear();
				edgesWithinSel.clear();
			}
			// moving the mouse while drawing an edge
			if (drawingMode) {
				drawEdge(e.getPoint());
				// shows edge within selection range of mouse pointer
			} else {
				if (n == null && !geometry.getView().isContracted(clus)
						&& toggle != TOG_INCLUDE && toggle != TOG_COLL_EXP) {
					edgesWithinSel.clear();
					Edge edg = geometry.findEdgeAtPoint(e.getPoint());
					if (edg != null) {
						edgesWithinSel.add(edg);
						this.withinSelection.clear();
						this.clusWithinSel.clear();
					}
				}
			}
			repaint();
			// starts scrolling thread
			if ((xScroll || yScroll || negXScroll || negYScroll) && drawingMode
					&& newScrolling) {
				th = new Thread(this);
				th.start();
			}
		}
	}

	/**
	 * Shows text label associated with given element.
	 * 
	 * @param n
	 *            Leaf.
	 * @param clus
	 *            Cluster.
	 * @param edge
	 *            Edge.
	 * @param g2
	 *            Graphics context.
	 */
	private void showLabel(Node n, Node clus, Edge edge, Graphics g2) {
		if (n != null) {
			String label = getBaseGraph().getLeafLabel(n);
			paintLabel(label, g2);
		} else if (edge != null) {
			String label = getBaseGraph().getEdgeLabel(edge);
			paintLabel(label, g2);
		} else if (clus != null) {
			String label = getBaseGraph().getClusLabel(clus);
			paintLabel(label, g2);
		}
	}

	/**
	 * Paints labels for graph elements during labeling mode.
	 * 
	 * @param label
	 *            The label to be displayed.
	 * @param g
	 *            Graphics context.
	 */
	private void paintLabel(String label, Graphics g) {
		if (label != null && !label.equals("")) {
			Shape shape = labelShape(label, g);
			Rectangle rec = shape.getBounds();
			int l = rec.width;
			int h = rec.height;
			g.setColor(Color.CYAN);
			g.fillRect(mousePointer.x - 1, mousePointer.y - 14, l + 5, h);
			g.setColor(Color.black);
			g.drawRect(mousePointer.x - 1, mousePointer.y - 14, l + 5, h);
			g.drawString(label, mousePointer.x + 2, mousePointer.y - 1);
		}
	}

	/**
	 * Calculates the total graphical length of a string label.
	 * 
	 * @param label
	 *            The label.
	 * @param g
	 *            Graphics context.
	 * @return Number of pixels determining length of label.
	 */
	private Shape labelShape(String label, Graphics g) {
		if (label == null) {
			return null;
		}
		FontMetrics fm = g.getFontMetrics();
		Shape shape = fm.getStringBounds(label, g);
		return shape;
	}

	/**
	 * Sets the editing mode.
	 * 
	 * @param toggleButton
	 *            The new editing mode.
	 */
	public void setToggle(int toggleButton) {
		toggle = toggleButton;
		clearSelections();
		repaint();
	}

	/**
	 * Enlarges the drawing space.
	 * 
	 * @param rec
	 *            The rectangle which is supposed to be close to boundaries.
	 */
	private void resizeViewPanel(Rectangle rec) {
		// gets the current size
		int oldSizeX = getPreferredSize().width;
		int oldSizeY = getPreferredSize().height;
		if (rec != null) {
			// which dimension is to be enlargened?
			if (rec.x + prefs.leafWidth >= oldSizeX
					&& rec.y + prefs.leafHeight < oldSizeY) {
				this.setPreferredSize(new Dimension(oldSizeX + 200, oldSizeY));
				revalidate();
			} else if (rec.x + prefs.leafWidth < oldSizeX
					&& rec.y + prefs.leafHeight >= oldSizeY) {
				this.setPreferredSize(new Dimension(oldSizeX, oldSizeY + 200));
				revalidate();
			} else if (rec.x + prefs.leafWidth >= oldSizeX
					&& rec.y + prefs.leafHeight >= oldSizeY) {
				this.setPreferredSize(new Dimension(oldSizeX + 200,
						oldSizeY + 200));
				revalidate();
			}
		}
	}

	/**
	 * Sets new dimensions for ViewPanel.
	 * 
	 * @param width
	 *            New width.
	 * @param height
	 *            New height.
	 */
	private void enlargeViewPanel(int width, int height) {
		Dimension d = this.getPreferredSize();
		d.setSize(new Dimension(d.width + width, d.height + height));
		this.setPreferredSize(d);
	}

	/**
	 * Sets viewport.
	 * 
	 * @param vport
	 *            The new viewport.
	 */
	public void setViewport(JViewport vport) {
		this.viewport = vport;
	}

	/**
	 * Gets control point at point p.
	 * 
	 * @param p
	 *            The coordinates at which a control point is looked for.
	 * @return Control point at point p.
	 */
	private Point2D.Double getContrPoint(Point p) {
		Point2D.Double result = null;
		Iterator it = edgesSelection.iterator();
		while (it.hasNext()) {
			Polyline poly = geometry.shape((Edge) it.next());
			if (poly != null) {
				result = poly.stabbedContrPoint(p);
			}
			if (result != null) {
				break;
			}
		}
		return result;
	}

	/**
	 * Removes all selected items.
	 *  
	 */
	public void delSelection() {
		Node highestPar = null;
		Iterator it;

		it = edgesSelection.iterator();
		// removes edges
		while (it.hasNext()) {
			Edge edge = (Edge) it.next();
			getView().deleteEdge(edge);
		}

		it = selection.iterator();
		// removes leafs
		while (it.hasNext()) {
			Node leaf = (Node) it.next();
			Node par = this.getView().getParent(leaf);

			// gets parent with smallest depths
			if (highestPar == null) {
				highestPar = par;
			} else if (getView().inclusionDepth(highestPar).compareTo(
					getView().inclusionDepth(highestPar)) > 0) {
				highestPar = par;
			}
			getView().deleteLeaf(leaf);
			geometry.autoResize(par);
		}

		// list with roots of subtrees to be deleted
		LinkedList roots = new LinkedList();

		it = clusSelection.iterator();
		while (it.hasNext()) {
			boolean toBeRemoved = true;
			Node clus = (Node) it.next();
			LinkedList copy = new LinkedList(clusSelection);
			Iterator it2 = copy.iterator();
			while (it2.hasNext()) {
				Node anc = (Node) it2.next();
				if (anc != clus && getView().isAncestor(anc, clus)) {
					toBeRemoved = false;
				}
			}
			if (toBeRemoved) {
				roots.add(clus);
			}
		}

		// removes clusters / merge
		it = roots.iterator();
		while (it.hasNext()) {
			Node clus = (Node) it.next();
			getView().delClus(clus);
		}

		// clears selections after deletion
		selection.clear();
		clusSelection.clear();
		movingClus.clear();
		disableCutCopy();
		edgesSelection.clear();
		withinSelection.clear();
		this.clusWithinSel.clear();
		edgesWithinSel.clear();
		redraw();
		repaint();
	}

	/**
	 * Run method of thread. Here is where the actual scrolling takes place.
	 */
	public void run() {
		int runCount = 0;
		int factor = (gui.getScrollSpeed() / 100);
		// thread runs as long as there needs to be a scrolling
		// in at least one direction
		while (xScroll || yScroll || negXScroll || negYScroll) {

			// after some scrolling the mouse needs to be moved again
			if (runCount >= 100 && drawingMode) {
				xScroll = false;
				yScroll = false;
				negXScroll = false;
				negYScroll = false;
				break;
			}
			runCount++;

			try {
				Thread.sleep((long) ((double) 20));
			} catch (Exception exc) {
				System.exit(0);
			}

			// during drawing of edges
			if (drawingMode) {
				Point viewPos = viewport.getViewPosition();
				// scrolling in various directions
				if (xScroll && !yScroll) {
					viewPos.translate(1 * factor, 0);
					Rectangle p = (geometry.drawEdgeShape(drawEdge)).getEnd();
					p.translate(1 * factor, 0);
				} else if (yScroll && !xScroll) {
					viewPos.translate(0, factor * 1);
					Rectangle p = (geometry.drawEdgeShape(drawEdge)).getEnd();
					p.translate(0, 1 * factor);
				} else if (xScroll && yScroll) {
					viewPos.translate(1 * factor, 1 * factor);
					Rectangle p = (geometry.drawEdgeShape(drawEdge)).getEnd();
					p.translate(1 * factor, 1 * factor);
				}
				if (negXScroll && viewPos.x > 0) {
					viewPos.translate(-1 * factor, 0);
					Rectangle p = (geometry.drawEdgeShape(drawEdge)).getEnd();
					p.translate(-1 * factor, 0);
				}
				if (negYScroll && viewPos.y > 0) {
					viewPos.translate(0, -1 * factor);
					Rectangle p = (geometry.drawEdgeShape(drawEdge)).getEnd();
					p.translate(0, -1 * factor);
				}
				viewport.setViewPosition(viewPos);
				revalidate();
				repaint();

				// dragging
			} else {
				LinkedList l = new LinkedList();
				l.addAll(selection);
				l.addAll(clusSelection);
				Iterator it = l.iterator();
				while (it.hasNext()) {
					Rectangle rec = geometry.shape((Node) it.next());
					Point p = rec.getLocation();
					if (xScroll) {
						p.x += 1 * factor;
					}
					if (yScroll) {
						p.y += 1 * factor;
					}
					if (negXScroll) {
						p.x -= 1 * factor;
						if (p.x < 0) {
							negXScroll = false;
						}
					}
					if (negYScroll) {
						p.y -= 1 * factor;
						if (p.y < 0) {
							negYScroll = false;
						}
					}
					rec.setLocation(p.x, p.y);
					// enlarge drawing space
					resizeViewPanel(rec);
				}
				Point viewPos = viewport.getViewPosition();

				// translates viewport position
				if (xScroll) {
					viewPos.translate(1 * factor, 0);
				}
				if (yScroll) {
					viewPos.translate(0, 1 * factor);
				}
				if (negXScroll) {
					viewPos.translate(-1 * factor, 0);
				}
				if (negYScroll) {
					viewPos.translate(0, -1 * factor);
				}
				viewport.setViewPosition(viewPos);
				revalidate();

				repaint();
			}
		}
	}

	/**
	 * Sets new node color.
	 * 
	 * @param c
	 *            The new node color.
	 */
	public void setNodeFill(Color c) {
		prefs.nFill = c;
	}

	/**
	 * Set new node frame color.
	 * 
	 * @param c
	 *            The new node frame color.
	 */
	public void setNodeFrame(Color c) {
		prefs.nFrame = c;
	}

	/**
	 * Sets new edge color.
	 * 
	 * @param c
	 *            The new edge color.
	 */
	public void setLineColor(Color c) {
		prefs.lCol = c;
	}

	/**
	 * Gets the selected nodes of a viewpanel.
	 * 
	 * @return List containing coordinate and inclusion informations.
	 */
	public LinkedList getSelectedNodes() {
		LinkedList result = new LinkedList();
		LinkedList leaves = new LinkedList();
		// maps clusters to children
		HashMap clusAndChildren = new HashMap();
		// maps clusters to depth
		HashMap clusAndDepth = new HashMap();
		// mapping nodes to coordinates
		HashMap geoCoords = new HashMap();
		// maps edges to polylines
		HashMap edges = new HashMap();
		// contains contracted nodes
		LinkedList contracted = new LinkedList();
		leaves.addAll(selection);

		LinkedList allDesc = new LinkedList();

		// makes list of all selected nodes and all their descendants
		Iterator it = selection.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			java.util.List l = getView().getSubtree(n);
			Iterator it2 = ((java.util.List) l.get(3)).iterator();
			while (it2.hasNext()) {
				Node n2 = (Node) it2.next();
				if (!allDesc.contains(n2)) {
					allDesc.add(n2);
				}
			}
		}
		it = clusSelection.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			java.util.List l = getView().getSubtree(n);
			Iterator it2 = ((java.util.List) l.get(3)).iterator();
			while (it2.hasNext()) {
				Node n2 = (Node) it2.next();
				if (!allDesc.contains(n2)) {
					allDesc.add(n2);
				}
			}
		}

		// makes clusAndChildren, clusAndDepth and contracted
		it = clusSelection.iterator();
		while (it.hasNext()) {
			Node clus = (Node) it.next();
			if (getView().hasChildren(clus)) {
				clusAndChildren.put(clus, (java.util.List) ((List) getView()
						.getChildren(clus)));
				Iterator adj = getView().getBaseGraph().getOutEdgesIterator(
						clus);
				while (adj.hasNext()) {
					Edge edge = (Edge) adj.next();
					Node target = edge.getTarget();
					if (allDesc.contains(target)) {
						edges.put(edge, null);
					}
				}
			} else {
				// contracted clusters need subtree method
				java.util.List l = getView().getSubtree(clus);
				clusAndChildren.putAll((HashMap) l.get(0));
				leaves.addAll((java.util.List) l.get(1));
				HashMap e = (HashMap) l.get(2);
				Iterator edgeIter = e.entrySet().iterator();
				while (edgeIter.hasNext()) {
					Map.Entry entry = (Map.Entry) edgeIter.next();
					List value = (List) entry.getValue();
					Iterator adjIter = value.iterator();
					while (adjIter.hasNext()) {
						Edge edg = (Edge) adjIter.next();
						Node tar = edg.getTarget();
						if (allDesc.contains(tar)) {
							edges.put(edg, null);
						}
					}

				}
				contracted.add(clus);
			}
			clusAndDepth.put(clus, getView().inclusionDepth(clus));
		}

		// marks contracted clusters for collapsing them when pasted
		it = clusSelection.iterator();
		while (it.hasNext()) {
			Node clus = (Node) it.next();
			if (!getView().hasChildren(clus)) {
				java.util.List l = getView().getSubtree(clus);
				Iterator it2 = ((HashMap) l.get(2)).entrySet().iterator();
				while (it2.hasNext()) {
					Map.Entry entry = (Map.Entry) it2.next();
					Node c = (Node) entry.getKey();
					clusAndDepth.put(c, getView().baseInclusionDepth(c));
					java.util.List checkClus = getView().getSubtree(c);
					List allDescOfC = (List) checkClus.get(3);
					if (allDescOfC.size() > 1) {
						contracted.add(c);
					}
				}
			}
		}

		// deals with edges between leaves in basegraph
		it = selection.iterator();
		while (it.hasNext()) {
			Node leaf = (Node) it.next();
			List l = getView().getSubtree(leaf);
			Iterator it2 = ((HashMap) l.get(2)).entrySet().iterator();
			while (it2.hasNext()) {
				Map.Entry entry = (Map.Entry) it2.next();
				List value = (List) entry.getValue();
				Iterator adjIt = value.iterator();
				while (adjIt.hasNext()) {
					Edge incEdge = (Edge) adjIt.next();
					// checks if edge needs to be inserted
					if ((leaves.contains(incEdge.getSource()) || clusAndChildren
							.containsKey(incEdge.getSource()))
							&& (leaves.contains(incEdge.getTarget()) || clusAndChildren
									.containsKey(incEdge.getTarget()))) {
						edges.put(incEdge, null);
					}
				}
			}
		}

		LinkedList alreadyUsedPolys = new LinkedList();
		// checks whether a polyline can be taken as edge
		it = (new HashMap(edges)).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Edge key = (Edge) entry.getKey();
			Node source = key.getSource();
			Node target = key.getTarget();

			Iterator it2 = edgesSelection.iterator();
			while (it2.hasNext()) {
				Edge selEdge = (Edge) it2.next();
				Polyline poly = null;
				if (source == selEdge.getSource()
						&& target == selEdge.getTarget()) {
					poly = geometry.shape(selEdge);
				}
				if (poly != null && !alreadyUsedPolys.contains(poly)) {
					alreadyUsedPolys.add(poly);
					Polyline polyClone = (Polyline) poly.clone(poly.getStart(),
							poly.getEnd(), prefs);
					edges.put(key, polyClone);
					break;
				}
			}
		}

		it = edgesSelection.iterator();
		while (it.hasNext()) {
			Edge edg = (Edge) it.next();
			if (!edges.containsKey(edg)) {
//				edges.put(edg, geometry.shape(edg));
			}
		}

		// sets geometrical positions of nodes
		it = selection.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			if (this.geometry.shape(n) == null) {
				System.exit(0);
			}
			geoCoords.put(n, (Rectangle) this.geometry.shape(n).clone());
		}
		it = clusSelection.iterator();
		while (it.hasNext()) {
			Node clus = (Node) it.next();
			if (getView().isContracted(clus)) {
				geoCoords.put(clus, (Rectangle) this.geometry.shape(clus)
						.clone());
			}
		}

		result.add(leaves);
		result.add(clusAndChildren);
		result.add(contracted);
		result.add(geoCoords);
		result.add(edges);
		result.add(clusAndDepth);
		return result;
	}

	/**
	 * Pastes nodes and edges.
	 * 
	 * @param nodes
	 *            Nodes to be pasted.
	 * @param clusters
	 *            Clusters to be pasted.
	 * @param coll
	 *            nodes to be contracted.
	 * @param geoCoords
	 *            Coordinates of nodes.
	 * @param edges
	 *            Edges and polylines.
	 * @param copiedClusDepth
	 *            Cluster depths.
	 */
	public void pasteNodes(LinkedList nodes, HashMap clusters, LinkedList coll,
			HashMap geoCoords, HashMap edges, HashMap copiedClusDepth) {
		boolean sugi = false;
		boolean linear = false;
		if (prefs.algorithm.equals("sugi")) {
			sugi = true;
		}
		if (prefs.animation.equals("linear")) {
			linear = true;
			setAnimationStyle(new NoAnimation(geometry, this));
		}
		geometry.setDrawingStyle(new DefaultDrawingStyle(geometry));
		Iterator it;
		HashMap nodeMapping = new HashMap();
		HashMap clusterMapping = new HashMap();
		clusSelection.clear();
		selection.clear();
		edgesSelection.clear();
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;

		//looks for minimal coordinates for putting pasted
		//elements in upper left corner of panel
		it = geoCoords.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Rectangle value = (Rectangle) entry.getValue();
			if (value.x < minX) {
				minX = value.x;
			}
			if (value.y < minY) {
				minY = value.y;
			}
		}
		Point upperLeft = viewport.getViewPosition();
		int transX = minX - upperLeft.x - 30;
		int transY = minY - upperLeft.y - 30;

		// sorts clusters according to depth
		TreeSet bst = new TreeSet(new DepthComparator(copiedClusDepth));
		it = clusters.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Node clus = (Node) entry.getKey();
			bst.add(clus);
		}

		// inserts nodes
		it = nodes.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			Node newNode = getView().newLeaf(getView().getRoot());
			nodeMapping.put(n, newNode);
			if (geoCoords.containsKey(n)) {
				this.setShapeParameters(newNode, (Rectangle) geoCoords.get(n));
			}
		}
		// inserts new clusters
		it = bst.iterator();
		while (it.hasNext()) {
			Node clus = (Node) it.next();
			java.util.List l = (java.util.List) clusters.get(clus);
			LinkedList mapped = new LinkedList();
			Iterator map = l.iterator();
			while (map.hasNext()) {
				Node n = (Node) map.next();
				Node mapLeaf = (Node) nodeMapping.get(n);
				Node mapClus = (Node) clusterMapping.get(n);
				if (mapLeaf != null) {
					mapped.add(mapLeaf);
				} else {
					mapped.add(mapClus);
				}
			}
			Node newClus = getView().split(mapped);
			clusterMapping.put(clus, newClus);
			if (geoCoords.containsKey(clus)) {
				this.setShapeParameters(newClus, (Rectangle) geoCoords
						.get(clus));
			}
		}

		// maps new edges to corresponding polylines
		HashMap polyMapping = new HashMap();

		// pastes edges
		it = edges.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Edge key = (Edge) entry.getKey();
			Node source = key.getSource();
			Node target = key.getTarget();
			Node sourceMap = null;
			Node targetMap = null;
			// gets shapes for start/end of polyline
			if (nodeMapping.containsKey(source)) {
				sourceMap = (Node) nodeMapping.get(source);
			} else {
				sourceMap = (Node) clusterMapping.get(source);
			}
			if (nodeMapping.containsKey(target)) {
				targetMap = (Node) nodeMapping.get(target);
			} else {
				targetMap = (Node) clusterMapping.get(target);
			}
			Polyline value = (Polyline) entry.getValue();
			// checks if edge needs to be pasted
			if (sourceMap != null && targetMap != null) {
				if (getView().getEdge(sourceMap, targetMap).isEmpty()) {
					if (value == null) {
						getView().newEdge(sourceMap, targetMap);
					} else {
						Edge newEdge = getView().newEdge(sourceMap, targetMap);
						polyMapping.put(newEdge, value);
					}
				}
			} else {
				it.remove();
			}
		}

		// collapses nodes, starts with deepest nodes
		it = bst.iterator();
		while (it.hasNext()) {
			Node clus = (Node) it.next();
			if (coll.contains(clus)) {
				getView().contract((Node) clusterMapping.get(clus));
			}
			if (geoCoords.containsKey(clus)) {
				this.setShapeParameters((Node) clusterMapping.get(clus),
						(Rectangle) geoCoords.get(clus));
			}
		}

		// translation and selection of nodes, so that they are in upper left
		// and can be dragged
		geometry.getMovingPoints().clear();
		it = nodes.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			Node realNode = (Node) nodeMapping.get(n);
			if (getView().containsNode(realNode) && !sugi) {
				selection.add(realNode);
				Rectangle shape = geometry.shape(realNode);
				shape.translate(-transX, -transY);
			}
		}
		movingClus.clear();
		it = clusters.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Node c = (Node) entry.getKey();
			Node realClus = (Node) clusterMapping.get(c);
			if (getView().containsNode(realClus) && !sugi) {
				clusSelection.add(realClus);
				Rectangle shape = geometry.shape(realClus);
				shape.translate(-transX, -transY);
			}
			geometry.resizeTillRoot(realClus);
		}

		// adds control points to pasted edges
		it = getView().getAllEdgesIterator();
		while (it.hasNext()) {
			Edge edge = (Edge) it.next();
			Polyline poly = (Polyline) polyMapping.get(edge);
			if (poly == null) {
				Iterator it2 = polyMapping.entrySet().iterator();
				while (it2.hasNext()) {
					Map.Entry entry = (Map.Entry) it2.next();
					Edge key = (Edge) entry.getKey();
					if (key.getSource() == edge.getSource()
							&& key.getTarget() == edge.getTarget()) {
						poly = (Polyline) entry.getValue();
					}
				}
			}
			if (poly != null) {
				Polyline origPoly = geometry.shape(edge);
				origPoly.clearControlPoints();
				Iterator ctrl = poly.controlPointsIterator();
				while (ctrl.hasNext()) {
					Point2D p = (Point2D) ((Point2D) ctrl.next()).clone();
					p.setLocation(p.getX() - transX, p.getY() - transY);
					origPoly.addControl(p);
				}
				origPoly.reclone();
			}
		}

		// drawing style may need redraw
		if (sugi) {
			geometry.setDrawingStyle(new SugiyamaDrawingStyle(geometry));
			redraw();
			it = getView().getAllEdgesIterator();
			while (it.hasNext()) {
				Edge edge = (Edge) it.next();
				geometry.shape(edge).reclone();
			}
		} else {
			prepareMovement();
		}
		if (linear) {
			setAnimationStyle(new AffinLinearAnimation(geometry, this));
		}
		enableDel();
		repaint();
	}

	/**
	 * Sets coordinates of rectangles.
	 * 
	 * @param node
	 *            The node which will get new coordinates.
	 * @param newShape
	 *            The shape representing the new coordinates.
	 */
	private void setShapeParameters(Node node, Rectangle newShape) {
		Rectangle oldShape = geometry.shape(node);
		oldShape.setLocation(newShape.getLocation());
		oldShape.setSize(newShape.getSize());
	}

	/**
	 * Enables the cut and copy buttons in the toolbar and the menu items.
	 *  
	 */
	private void enableCutCopy() {
		gui.enableCutCopy();
	}

	/**
	 * Enables gui items for deletion.
	 *  
	 */
	private void enableDel() {
		gui.enableDel();
	}

	/**
	 * Disables the cut and copy buttons in the toolbar and the menu items.
	 *  
	 */
	private void disableCutCopy() {
		gui.disableCutCopy();
	}

	/**
	 * Gets those selected nodes which have no selected ancestor.
	 * 
	 * @param n
	 *            The node to be tested for adding to result.
	 * @param res
	 *            The list with the result.
	 */
	private void getHigherSelNodes(Node n, List res) {
		//added if selected, otherwise proceed with children
		if (clusSelection.contains(n) || selection.contains(n)) {
			res.add(n);
		} else {
			Iterator it = getView().getChildrenIterator(n);
			while (it.hasNext()) {
				Node child = (Node) it.next();
				getHigherSelNodes(child, res);
			}
		}
	}

	/**
	 * Makes a new cluster.
	 * 
	 * @return The new cluster.
	 *  
	 */
	private Node include() {
		// calls split
		Node n = null;
		try {
			LinkedList clusChildren = new LinkedList();
			getHigherSelNodes(getView().getRoot(), clusChildren);

			try {
				n = getView().split(clusChildren);
			} catch (EmptyClusterException exc) {
				gui.showError("User Error", exc.getMessage());
			}

			// if null is returned, no common parent was found
			if (n != null) {
				selection.clear();
				repaint();
			}
		} catch (NoCommonParentException exc) {
			gui.showError("User Error", exc.getMessage());
			return null;
		}
		return n;
	}

	/**
	 * Method for adding nodes to cluster.
	 * 
	 * @param evt
	 *            The event.
	 *  
	 */
	private void enlargeCluster(MouseEvent evt) {
		Iterator it;

		// gets contracted nodes
		LinkedList contracted = new LinkedList();
		it = clusSelection.iterator();
		while (it.hasNext()) {
			Node clus = (Node) it.next();
			if (getView().isContracted(clus)) {
				contracted.add(clus);
			}
		}

		// a cluster with only one node is treated as cluster
		// even if only node is selected
		if (selection.size() == 1) {
			Node n = (Node) selection.getFirst();
			Node clus = getView().getParent(n);
			if (clus != getView().getRoot()
					&& getView().getChildren(clus).size() == 1) {
				clusSelection.add(clus);
			}
		}

		// former parent nodes are saved
		LinkedList formerParents = new LinkedList();
		it = clusSelection.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			Node nPar = getView().getParent(n);
			if (!formerParents.contains(nPar)) {
				formerParents.add(nPar);
			}
		}

		// a new common parent node is calculated
		Node newCommonParent = getView().getRoot();

		newCommonParent = geometry.findClusterAtPoint(evt.getPoint(),
				new LinkedList(clusSelection));

		boolean correctMove = true;

		Node newParAnc = newCommonParent;
		while (newParAnc != getView().getRoot() && correctMove) {
			// checks if moving of node causes violation of edge preconditions
			Iterator itIn = getView().getInEdgesIterator(newParAnc);
			while (itIn.hasNext()) {
				Edge e = (Edge) itIn.next();
				Node n = e.getSource();
				if (selection.contains(n) || clusSelection.contains(n)) {
					gui.showError("Move Error", "Moving of nodes would create"
							+ " incorrect edges");
					correctMove = false;
					break;
				}
			}
			Iterator itOut = getView().getOutEdgesIterator(newParAnc);
			while (itOut.hasNext()) {
				Edge e = (Edge) itOut.next();
				Node n = e.getTarget();
				if (selection.contains(n) || clusSelection.contains(n)) {
					gui.showError("Move Error", "Moving of nodes would create"
							+ " incorrect edges");
					correctMove = false;
					break;
				}
			}
			newParAnc = getView().getParent(newParAnc);
		}

		if (!formerParents.contains(newCommonParent) && correctMove) {

			// sets new parent and children relations for selected clusters
			it = clusSelection.iterator();
			while (it.hasNext()) {
				Node clus = (Node) it.next();
				Node formerParent = getView().getParent(clus);
				if (!clusSelection.contains(formerParent)) {
					LinkedList toBeRemoved = new LinkedList();
					toBeRemoved.add(clus);
					geometry.getView().moveNode(clus, newCommonParent);
				}
			}

			// sets new parent and children relations for selected leaves
			it = selection.iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				Node nodePar = getView().getParent(node);
				if (!clusSelection.contains(nodePar)
						&& newCommonParent != getView().getParent(node)) {
					LinkedList l = new LinkedList();
					l.add(node);
					geometry.getView().moveNode(node, newCommonParent);
				}

			}
			geometry.resizeAll(getView().getRoot());
			redraw();
			repaint();
		}
	}

	/**
	 * Gets the preference setting for view panel.
	 * 
	 * @return Preference settings.
	 */
	public Preferences getPrefs() {
		return prefs;
	}

	/**
	 * Redraws view. Uses current layout algorithm.
	 *  
	 */
	public void redraw() {
		geometry.redraw();
	}

	/**
	 * Gets the associated geometry object.
	 * 
	 * @return Associated geometry object.
	 */
	public Geometry getGeometry() {
		return geometry;
	}

	/**
	 * Makes this panel contain a new view of a given basegraph.
	 * 
	 * @param v
	 *            ViewPanel displaying basegraph.
	 */
	public void newView(ViewPanel v) {
		toggle = v.toggle;
		Geometry geo = v.geometry;
		v.drawingMode = false;
		prefs.setPrefs(v.prefs);
		geo.removeDrawEdge(v.drawEdge);
		View vi = v.geometry.getView();
		View viewClone = new View(vi);
		Geometry geoClone = (Geometry) geo.clone(viewClone, prefs);
		geometry = geoClone;
		geoClone.detach(v);
		geoClone.attach(this);
		viewClone.detach(geo);
		viewClone.attach(geoClone);
		animStyle = (AnimationStyle) v.animStyle.clone(geoClone, this);
		if (prefs.algorithm.equals("sugi")) {
			geometry.setDrawingStyle(new SugiyamaDrawingStyle(geometry));
		}
		repaint();
	}

	/**
	 * Gets the view displayed in this panel.
	 * 
	 * @return The view.
	 */
	public View getView() {
		return geometry.getView();
	}

	/**
	 * Used for saving graphs.
	 * 
	 * @return The basegraph.
	 */
	public BaseCompoundGraph getBaseGraph() {
		return getView().getBaseGraph();
	}

	/**
	 * Update method. (Observer pattern)
	 * 
	 * @param action
	 *            The action object.
	 */
	public void update(org.visnacom.model.Action action) {
		if (action.getAffected() == null) {
			repaint();
		} else if (action.getAffected() instanceof Geometry) {
			animStyle.start((Geometry) action.getAffected());
		}
	}

	/**
	 * Gets all edges which lie entirely inside a cluster, i.e. source and
	 * target node have to lie inside said cluster.
	 * 
	 * @param clus
	 *            Cluster which contains the edges.
	 * @return The edges contained by clus.
	 */
	private List getContainedEdges(Node clus) {
		LinkedList result = new LinkedList();
		LinkedList descLeaves = new LinkedList();
		// gets all leaves within cluster
		getView().getLeaves(clus, descLeaves);
		Iterator it = descLeaves.iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			// gets edges from adjacency list
			List adjEdges = (List) getView().getAdjEdges(node);
			Iterator it2 = adjEdges.iterator();
			while (it2.hasNext()) {
				Edge edge = (Edge) it2.next();
				Node source = edge.getSource();
				Node target = edge.getTarget();
				// checks if source and target are both within cluster
				if (descLeaves.contains(source) && descLeaves.contains(target)
						&& !result.contains(edge)) {
					result.add(edge);
				}
			}
		}
		return result;
	}

	/**
	 * Repaint, overwritten. Resizes viewpanel.
	 */
	public void repaint() {
		super.repaint();
		if (geometry != null) {
			Iterator it = getView().getAllNodesIterator();
			while (it.hasNext()) {
				Node next = (Node) it.next();
				this.resizeViewPanel(geometry.shape(next));
			}
		}
	}

	/**
	 * Creates new internal frame.
	 * 
	 * @return The created frame.
	 */
	public ViewFrame createNewFrame() {
		return gui.createNewInternalFrame();
	}

	/**
	 * Selects all elements in panel.
	 *  
	 */
	public void selectAll() {
		selection.clear();
		clusSelection.clear();
		withinSelection.clear();
		clusWithinSel.clear();
		edgesSelection.clear();
		edgesWithinSel.clear();
		Iterator it;

		// selects nodes
		it = getView().getAllNodesIterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			if (getView().isCluster(n) && n != getView().getRoot()) {
				clusSelection.add(n);
			} else if (n != getView().getRoot()) {
				selection.add(n);
			}
		}

		// selects edges
		it = getView().getAllEdgesIterator();
		while (it.hasNext()) {
			Edge edge = (Edge) it.next();
			edgesSelection.add(edge);
		}
		enableDel();
		enableCutCopy();
		enableMovement();
		repaint();
	}

	/**
	 * Makes selected elements capable of being dragged by user.
	 *  
	 */
	private void enableMovement() {
		// makes leaves capable of dragging
		geometry.getMovingPoints().clear();
		Iterator it = selection.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			Point point = (geometry.shape(n)).getLocation();
			if (point != null) {
				geometry.getMovingPoints().put(n, point.clone());
			} else {
				geometry.getMovingPoints().clear();
			}
		}

		// makes clusters capable
		movingClus.clear();
		it = clusSelection.iterator();
		while (it.hasNext()) {
			Node clus = (Node) it.next();
			movingClus.add(clus);
			Point p = (geometry.shape(clus)).getLocation();
			geometry.getMovingPoints().put(clus, p.clone());
		}
	}

	/**
	 * Sets the anmation style.
	 * 
	 * @param as
	 *            The new animation style.
	 */
	public void setAnimationStyle(AnimationStyle as) {
		animStyle = as;
	}

	/**
	 * Clears all selections.
	 *  
	 */
	public void clearSelections() {
		selection.clear();
		clusSelection.clear();
		edgesSelection.clear();
		withinSelection.clear();
		clusWithinSel.clear();
		edgesWithinSel.clear();
	}

	/**
	 * Displays error dialog.
	 * 
	 * @param title
	 *            Title of dialog.
	 * @param message
	 *            Message in dialog.
	 */
	public void showError(String title, String message) {
		gui.showError(title, message);
	}

	/**
	 * Sets the active flag showing whether corresponding frame is active.
	 * 
	 * @param a
	 *            True iff corresponding frame is active.
	 */
	public void setActive(boolean a) {
		active = a;
	}

	/**
	 * Stops drawing of edge.
	 *  
	 */
	public void removeDrawEdge() {
		drawingMode = false;
		geometry.removeDrawEdge(drawEdge);
	}

	/**
	 * Prepares selected items for being dragged.
	 *  
	 */
	private void prepareMovement() {
		Iterator it;
		// makes items capable of dragging
		geometry.getMovingPoints().clear();
		it = selection.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			Point point = (geometry.shape(n)).getLocation();
			if (point != null) {
				geometry.getMovingPoints().put(n, point.clone());
			} else {
				geometry.getMovingPoints().clear();
			}
		}

		// prepares movement of selected elements
		movingClus.clear();
		it = clusSelection.iterator();
		while (it.hasNext()) {
			Node clus = (Node) it.next();
			movingClus.add(clus);
			Point p = (geometry.shape(clus)).getLocation();
			geometry.getMovingPoints().put(clus, p.clone());
		}
	}
}