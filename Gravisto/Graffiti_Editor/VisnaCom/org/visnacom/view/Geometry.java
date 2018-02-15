/*
 * Created on 12.12.2004
 *
 *
 */

package org.visnacom.view;

import java.util.*;
import java.awt.*;

import org.visnacom.controller.*;
import org.visnacom.model.*;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;


/**
 * @author F. Pfeiffer
 * 
 * This class is responsible for displaying the datastructure. In order to
 * achieve this, search-methods are implemented and Hashmaps map datastructure
 * objects to coordinate objects.
 * 
 * The observer pattern guarantees a GUI reaction to model changes.
 *  
 */
public class Geometry implements org.visnacom.model.Observer {

	// Hashmaps relating datastructure objects and drawing info
	private HashMap nodes, edges;

	// remembers position of leaves before dragging them so that translation
	// can be calculated
	private HashMap movingPoints;

	// remembers polylines for edges, used for updateExand and updateContract
	private HashMap rememberedEdges;

	// The view association is necessary for realizing the MVC pattern
	private View view;

	// current drawing style
	private DrawingStyle drawingStyle;

	// preferences
	private Preferences prefs;

	// geometry observers
	private LinkedList Observers;

	// used to prevent resizing due to deletion of subtree during contract
	private boolean resize;

	/**
	 * Standard constructor.
	 */
	public Geometry() {
		view = new View(new DynamicLeaves());
		nodes = new HashMap();
		edges = new HashMap();
		rememberedEdges = new HashMap();
		movingPoints = new HashMap();
		Observers = new LinkedList();
		getView().attach(this);
		this.nodes.put(view.getRoot(), new Rectangle());

		drawingStyle = new SugiyamaDrawingStyle(this);
		prefs = new Preferences();
		prefs.algorithm = "sugi";
		prefs.curveType = "polyline";
		resize = true;
	}

	/**
	 * Constructor with associated view and preferences.
	 * 
	 * @param v
	 *            Associated view instance.
	 * @param prefs
	 *            Preferences.
	 */
	public Geometry(View v, Preferences prefs) {
		view = v;
		getView().attach(this);
		nodes = new HashMap();
		edges = new HashMap();
		rememberedEdges = new HashMap();

		if (prefs.algorithm.equals("sugi")) {
			drawingStyle = new SugiyamaDrawingStyle(this);
		} else {
			drawingStyle = new DefaultDrawingStyle(this);
		}
		this.prefs = prefs;
		this.nodes.put(view.getRoot(), new Rectangle());
		movingPoints = new HashMap();
		Observers = new LinkedList();
		resize = true;
	}

	/**
	 * Search method, which looks for a node at a given Point.
	 * 
	 * @param p
	 *            A point at which one wants to look for a placed node.
	 * @return The node at point p; null if there is none.
	 */
	public Node findNodeAtPoint(Point p) {
		// Iterates the complete hashmap
		Iterator it = getView().getAllNodesIterator();
		while (it.hasNext()) {
			Node next = (Node) it.next();
			if (getView().isCluster(next)) {
				continue;
			}
			Rectangle value = shape(next);

			// if point lies inside node rectangle, we have the result
			if (value != null && value.contains(p)) {
				return next;
			}
		}
		return null;
	}

	/**
	 * Searches for an edge at a given point.
	 * 
	 * @param p
	 *            The point in the vicinity of which one looks for an edge.
	 * @return An edge which is close to p.
	 */
	public Edge findEdgeAtPoint(Point p) {
		Iterator it = getView().getAllEdgesIterator();
		while (it.hasNext()) {
			Edge edge = (Edge) it.next();
			if (shape(edge) != null && (shape(edge)).intersects(p.x, p.y)) {
				return edge;
			}
		}
		return null;
	}

	/**
	 * Gets deepest cluster at a given point
	 * 
	 * @param p
	 *            Point at which cluster is looked for.
	 * @param noValidResult
	 *            List of clusters which are not wanted as result.
	 * @return The node at given point.
	 */
	public Node findClusterAtPoint(Point p, java.util.List noValidResult) {
		Node node = getView().getRoot();

		Iterator it = getView().getAllNodesIterator();
		// iterates all clusters in search for possible results
		while (it.hasNext()) {
			Node next = (Node) it.next();
			if (!(getView().isCluster(next))) {
				continue;
			}
			Rectangle rec = shape(next);
			if (rec == null) {
				break;
			}

			// gets deepest cluster containing given point
			if (rec != null && p != null && rec.contains(p)
					&& !noValidResult.contains(next)) {
				// contracted clusters are preferred
				if (getView().isContracted(next)) {
					return next;
				}
				if (getView().inclusionDepth(next).compareTo(
						getView().inclusionDepth(node)) > 0)
					node = next;
			}
		}
		return node;
	}

	/**
	 * Returns the associated view instance.
	 * 
	 * @return The associated view instance.
	 */
	public View getView() {
		return view;
	}

	/**
	 * Gets the preferences.
	 * 
	 * @return Current preferences.
	 */
	public Preferences getPrefs() {
		return prefs;
	}

	/**
	 * The update method for reacting to model changes. Part of the observer
	 * pattern.
	 * 
	 * @param action
	 *            The kind of change which has been performed.
	 */
	public void update(org.visnacom.model.Action action) {
		synchronized (getClass()) {
			assert checkConsistency();

			// deletes node
			if (action instanceof ActionDelete
					&& action.getAffected() instanceof Node) {
				Node node = (Node) action.getAffected();
				nodes.remove(node);
				if (resize) {
					this.resizeAll(view.getRoot());
				}
				// deletes edge
			} else if (action instanceof ActionDelete
					&& action.getAffected() instanceof Edge) {
				Edge edge = (Edge) action.getAffected();
				if (((ActionDelete) action).remember()) {
					rememberedEdges.put(edge, shape(edge));
				}
				edges.remove(edge);
				if (prefs.algorithm.equals("sugi")) {
				}
				// contracts node
			} else if (action instanceof ActionContract
					&& action.getAffected() instanceof Node) {
				Node clus = (Node) action.getAffected();
				updateContract(clus, (ActionContract) action);

				// new node
			} else if (action instanceof ActionCreate
					&& action.getAffected() instanceof Node) {
				ActionCreate cre = (ActionCreate) action;

				// node shape not in nodes hashmap
				if (!nodes.containsKey((Node) action.getAffected())
						&& cre.getPar() == null) {
					Point loc = new Point(20, 20);
					nodes.put((Node) action.getAffected(), new Rectangle(loc,
							new Dimension(prefs.leafWidth, prefs.leafHeight)));
				} else if (!nodes.containsKey((Node) action.getAffected())
						&& cre.getPar() != null) {
					Rectangle par = shape((Node) cre.getPar());
					Point loc = new Point(par.x + 20, par.y + 20);
					nodes.put((Node) action.getAffected(), new Rectangle(loc,
							new Dimension(prefs.leafWidth, prefs.leafHeight)));
				}
				this.resizeTillRoot((Node) action.getAffected());
				redraw();
				// new edge
			} else if (action instanceof ActionCreate
					&& action.getAffected() instanceof Edge) {
				Edge edge = (Edge) action.getAffected();
				// puts new edge into hashmap if necessary
				if (!edges.containsKey(edge)) {
					Node source = edge.getSource();
					Node target = edge.getTarget();
					Rectangle startRec = (Rectangle) nodes.get(source);
					Rectangle endRec = (Rectangle) nodes.get(target);
					if (startRec != null && endRec != null) {
						Polyline poly = new Polyline(shape(source),
								shape(target), prefs);
						edges.put(edge, poly);
					}

				}
				// split
			} else if (action instanceof ActionInclude
					&& action.getAffected() instanceof Node) {
				Node clus = (Node) action.getAffected();
				nodes.put(clus, new Rectangle());
				this.resizeTillRoot(clus);
				redraw();
				// expand
			} else if (action instanceof ActionExpand
					&& action.getAffected() instanceof Node) {
				updateExpand((Node) action.getAffected(), (ActionExpand) action);

				// move
			} else if (action instanceof ActionMove
					&& action.getAffected() instanceof Node) {
				updateMove(action);
			}
			notify(new Action(null));
			assert checkConsistency();
		}
	}

	/**
	 * Updates geometry due to movement of node to another parent.
	 * 
	 * @param action
	 *            Action object with information about movement.
	 */
	private void updateMove(Action action) {
		ActionMove actMov = (ActionMove) action;

		// update only necessary if movement is not due to splitting
		if (!actMov.isSplitting()) {
			Node newPar = (Node) actMov.getNewPar();
			Node oldPar = (Node) actMov.getOldPar();
			// resizing of clusters
			resizeTillRoot(newPar);
			resizeTillRoot(oldPar);
			notify(new Action(null));
			redraw();
		}
	}

	/**
	 * Update method for expanding nodes.
	 * 
	 * @param clus
	 *            The node to be expanded.
	 * @param action
	 *            The observer action.
	 */
	private void updateExpand(Node clus, ActionExpand action) {
		assert checkConsistency();
		Iterator it, it2;

		// puts new polylines if necessary
		it = view.getAdjEdges(clus).iterator();
		while (it.hasNext()) {
			Edge edg = (Edge) it.next();
			if (!edges.containsKey(edg)) {
				this.edges.put(edg, new Polyline(new Rectangle(),
						new Rectangle(), this.prefs));
			}
		}

		it = view.getChildrenIterator(clus);
		while (it.hasNext()) {
			Node n = (Node) it.next();
			// sets coordinates of new children
			Rectangle childRec = new Rectangle();
			childRec.setLocation(shape(clus).getLocation());
			childRec.setSize(shape(clus).getSize());
			this.nodes.put(n, childRec);

			// inserts edges between children of expanded node
			java.util.List adj = view.getAdjEdges(n);
			it2 = adj.iterator();
			while (it2.hasNext()) {
				Edge edg = (Edge) it2.next();
				Node source = edg.getSource();
				Node target = edg.getTarget();
				if (getView().getParent(source) == clus
						&& getView().getParent(target) == clus) {
					this.edges.put(edg, new Polyline(new Rectangle(),
							new Rectangle(), this.prefs));
				}
			}
		}

		// preserves old polyline parameters
		it = action.getMappinsIterator();
		while (it.hasNext()) {
			ActionExpand.Mapping map = (ActionExpand.Mapping) it.next();
			Edge oldEdge = map.oldEdge;
			Node oSource = oldEdge.getSource();
			Node oTarget = oldEdge.getTarget();
			java.util.List newEdges = map.newEdges;
			it2 = newEdges.iterator();
			while (it2.hasNext()) {
				Edge nEdge = (Edge) it2.next();
				Node nSource = nEdge.getSource();
				Node nTarget = nEdge.getTarget();
				Polyline poly = shape(oldEdge);
				if (poly == null) {
					poly = (Polyline) rememberedEdges.get(oldEdge);
				}
				edges.put(nEdge, poly.clone(shape(nSource), shape(nTarget),
						prefs));
				shape(nSource).setLocation(shape(oSource).getLocation());
				shape(nTarget).setLocation(shape(oTarget).getLocation());
				shape(nSource).setSize(shape(oSource).getSize());
				shape(nTarget).setSize(shape(oTarget).getSize());
			}
		}

		// sets edges between children
		Collection intEdges = action.contDerEdges;
		it = intEdges.iterator();
		while (it.hasNext()) {
			Edge e = (Edge) it.next();
			Node source = (Node) e.getSource();
			Node target = (Node) e.getTarget();
			edges.put(e, new Polyline(shape(source), shape(target), prefs));
		}

		Geometry newGeometry = (Geometry) this.clone(view, new Preferences(
				prefs));

		assert checkConsistency();
		assert newGeometry.checkConsistency();

		// layout algorithm
		this.drawingStyle.expand(clus, newGeometry, action);

		// animation
		notify(new Action(newGeometry));

		rememberedEdges.clear();

		//		assert (getView().getAllEdges().size() == edges.size());

		// repaint
		notify(new Action(null));
	}

	/**
	 * Update method for collapsing nodes.
	 * 
	 * @param clus
	 *            The contracted node.
	 * @param action
	 *            Observer action.
	 */
	private void updateContract(Node clus, ActionContract action) {
		Iterator it;

		// removes nodes
		java.util.List desc = action.getChildren();
		it = desc.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			this.nodes.remove(n);
		}

		// removes edges
		it = getView().getAllEdgesIterator();
		while (it.hasNext()) {
			Edge key = (Edge) it.next();
			Node source = key.getSource();
			Node target = key.getTarget();
			if (desc.contains(source) || desc.contains(target)) {
				it.remove();
			}
		}

		//	inserts derived edges
		it = action.getMappinsIterator();
		while (it.hasNext()) {
			ActionContract.Mapping map = (ActionContract.Mapping) it.next();
			Edge newEdge = map.newEdge;
			Node nSource = newEdge.getSource();
			Node nTarget = newEdge.getTarget();
			java.util.List oldEdges = map.oldEdges;
			Edge first = (Edge) oldEdges.get(0);
			Polyline poly = shape(first);
			if (poly == null) {
				poly = (Polyline) rememberedEdges.get(first);
			}
			edges.put(newEdge, poly
					.clone(shape(nSource), shape(nTarget), prefs));
		}

		Geometry newGeometry = (Geometry) this.clone(view, new Preferences(
				prefs));

		// layout algorithm
		this.drawingStyle.contract(clus, newGeometry, action);

		// animation
		notify(new Action(newGeometry));

		rememberedEdges.clear();
	}

	/**
	 * Fully redraws view using layout algorithm.
	 *  
	 */
	public void redraw() {
		assert checkConsistency();
		Preferences prefsClone = new Preferences(prefs);
		Geometry newGeometry = (Geometry) this.clone(view, prefsClone);
		assert newGeometry.checkConsistency();

		// redraw with layout algorithm
		this.drawingStyle.draw(newGeometry);
		assert checkConsistency();

		// no animation for redraw
		AnimationStyle anim = new NoAnimation(this, null);
		anim.start(newGeometry);
		while (anim.isRunning()); // busy waiting
		assert checkConsistency();
		Iterator it = getView().getAllEdgesIterator();
		while (it.hasNext()) {
			shape((Edge) it.next()).reclone();
		}
		notify(new Action(null));
	}

	/**
	 * Returns the mapping of Nodes to rectangles.
	 * 
	 * @return The Hashmap which realizes said mapping.
	 */
	public HashMap getNodes() {
		return (HashMap) nodes.clone();
	}

	/**
	 * Returns the mapping of edges to polylines.
	 * 
	 * @return The Hashmap which realizes said mapping.
	 */
	public HashMap getEdges() {
		return (HashMap) edges.clone();
	}

	/**
	 * Performs a range query for leaves.
	 * 
	 * @param p1
	 *            First coordinate point specifying query rectangle.
	 * @param p2
	 *            Second coordinate point specifying query rectangle.
	 * @return A list of all nodes within the rectangle which is specified by
	 *         the two given points.
	 */
	public LinkedList nodeRangeQuery(Point p1, Point p2) {
		return rangeQueryNodes(p1, p2, false);
	}

	/**
	 * Performs a range query for clusters.
	 * 
	 * @param p1
	 *            First coordinate point specifying query rectangle.
	 * @param p2
	 *            Second coordinate point specifying query rectangle.
	 * @return A list of all nodes within the rectangle which is specified by
	 *         the two given points.
	 */
	public LinkedList clusterRangeQuery(Point p1, Point p2) {
		return rangeQueryNodes(p1, p2, true);
	}

	/**
	 * Performs a range query for nodes..
	 * 
	 * @param p1
	 *            First coordinate point specifying query rectangle.
	 * @param p2
	 *            Second coordinate point specifying query rectangle.
	 * @param innerNode
	 *            If innerNode is true only clusters are returned, otherwise
	 *            only leaves are returned.
	 * @return A list of all nodes within the rectangle which is specified by
	 *         the two given points.
	 */
	private LinkedList rangeQueryNodes(Point p1, Point p2, boolean innerNode) {
		LinkedList result = new LinkedList();
		if (p1 != null && p2 != null) {
			// The rectangle description is normalized, so that norm1 describes
			// the upper left, and norm2 the lower right of the rectangle
			Point norm1 = new Point();
			Point norm2 = new Point();
			normPoints(p1, p2, norm1, norm2);

			Rectangle range = new Rectangle(norm1, new Dimension(norm2.x
					- norm1.x, norm2.y - norm1.y));

			// for each node it is checked, whether it is contained in the given
			// rectangular region
			Iterator it = getView().getAllNodesIterator();
			while (it.hasNext()) {
				Node next = (Node) it.next();
				if (!innerNode && getView().isCluster(next)) {
					continue;
				}
				if (innerNode && !(getView().isCluster(next))) {
					continue;
				}
				if (range.contains(shape(next))) {
					result.add(next);
				}
			}
		}
		return result;
	}

	/**
	 * Normalizes selection rectangle description.
	 * 
	 * @param p1
	 *            First point of selection rectangle.
	 * @param p2
	 *            Second point of selection rectangle.
	 * @param norm1
	 *            Point in which upper left of selection rectangle will be
	 *            stored.
	 * @param norm2
	 *            Point in which lower right of selection rectangle will be
	 *            stored.
	 */
	private void normPoints(Point p1, Point p2, Point norm1, Point norm2) {
		// The rectangle description is normalized, so that norm1 describes
		// the upper left, and norm2 the lower right of the rectangle
		if (p1.y >= p2.y && p1.x >= p2.x) {
			norm1.setLocation(p2.getLocation());
			norm2.setLocation(p1.getLocation());
		} else if (p1.y <= p2.y && p1.x >= p2.x) {
			norm1.move(p2.x, p1.y);
			norm2.move(p1.x, p2.y);
		} else if (p1.y >= p2.y && p1.x <= p2.x) {
			norm1.move(p1.x, p2.y);
			norm2.move(p2.x, p1.y);
		} else {
			norm1.setLocation(p1.getLocation());
			norm2.setLocation(p2.getLocation());
		}
	}

	/**
	 * Performs a range query for edges.
	 * 
	 * @param p1
	 *            First coordinate point specifying query rectangle.
	 * @param p2
	 *            Second coordinate point specifying query rectangle.
	 * @param edgeFlag
	 *            True if a list of edges should be returned, false if a list of
	 *            polylines is needed.
	 * @return A list of edges lying within rectangle.
	 */
	public LinkedList edgeRangeQuery(Point p1, Point p2, boolean edgeFlag) {
		LinkedList result = new LinkedList();
		Point norm1 = new Point();
		Point norm2 = new Point();

		normPoints(p1, p2, norm1, norm2);

		Dimension d = new Dimension(norm2.x - norm1.x, norm2.y - norm1.y);

		Rectangle selRect = new Rectangle(norm1, d);

		// for each edge it is checked, whether it is contained in the given
		// rectangular region
		Iterator it = getView().getAllEdgesIterator();
		while (it.hasNext()) {
			Edge next = (Edge) it.next();
			Polyline poly = shape(next);
			Rectangle bounds = poly.getBoundingRect();
			if (selRect.contains(bounds)) {
				if (!edgeFlag) {
					result.add(poly);
				} else {
					result.add(next);
				}
			}
		}
		return result;
	}

	/**
	 * Gets preorder traversal of inclusion tree for drawing.
	 * 
	 * @return Preorder traversal of clusters.
	 */
	public ArrayList getClusterDrawingOrder() {
		ArrayList order = new ArrayList();

		// recursion beginning with root
		getClusOrder(getView().getRoot(), order);
		return order;
	}

	/**
	 * Recursive method for preorder traversal.
	 * 
	 * @param clus
	 *            Root of subtree.
	 * @param order
	 *            List for recursive call.
	 */
	private void getClusOrder(Node clus, ArrayList order) {
		if (getView().isCluster(clus)) {
			order.add(clus);
		}
		if (getView().hasChildren(clus)) {
			Iterator it = getView().getChildrenIterator(clus);
			while (it.hasNext()) {
				Node n = (Node) it.next();
				getClusOrder(n, order);
			}
		}
	}

	/**
	 * Sets the drawing style.
	 * 
	 * @param ds
	 *            The new drawing style.
	 */
	public void setDrawingStyle(DrawingStyle ds) {
		drawingStyle = ds;
		if (ds instanceof SugiyamaDrawingStyle) {
			prefs.algorithm = "sugi";
		} else {
			prefs.algorithm = "default";
		}
		redraw();
	}

	/**
	 * Clones geometry object.
	 * 
	 * @param v
	 *            The associated view.
	 * @param p
	 *            The preferences to be set for clone.
	 * @return The cloned object.
	 */
	public Object clone(View v, Preferences p) {
		Geometry geo = new Geometry(view, p);

		geo.view = v;
		view.detach(geo);

		Iterator it;

		// clones nodes
		it = nodes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Rectangle rec = (Rectangle) entry.getValue();
			Point p1 = rec.getLocation();
			Rectangle newRect = new Rectangle();
			newRect.setLocation(new Point(p1.x, p1.y));
			newRect.setSize(new Dimension((int) rec.getWidth(), (int) rec
					.getHeight()));
			geo.nodes.put((Node) entry.getKey(), newRect);
		}

		// clones edges
		it = edges.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Edge edge = (Edge) entry.getKey();

			Polyline poly = ((Polyline) entry.getValue());
			Rectangle start = new Rectangle();
			if (nodes.containsKey(edge.getSource())) {
				start = ((Rectangle) geo.shape(edge.getSource()));
			} else {
				throw new InvalidEdgeException("No source rectangle!");
			}
			Rectangle end = new Rectangle();
			if (nodes.containsKey(edge.getTarget())) {
				Rectangle rec = (Rectangle) geo.shape(edge.getTarget());
				end = rec;
			} else {
				throw new InvalidEdgeException("No target rectangle!");
			}

			if (start != null && end != null) {
				Polyline polyClone = (Polyline) poly.clone(start, end, p);
				geo.edges.put((Edge) entry.getKey(), polyClone);
			}
		}

		geo.Observers = (LinkedList) this.Observers.clone();
		return geo;
	}

	/**
	 * Gets the shape of an edge.
	 * 
	 * @param edg
	 *            The edge.
	 * @return The shape.
	 */
	public Polyline shape(Edge edg) {
		return (Polyline) edges.get(edg);
	}

	/**
	 * Gets the shape of uncompleted edge during drawing.
	 * 
	 * @param drawEdge
	 *            Uncompleted edge.
	 * @return Shape of uncompleted edge.
	 */
	public Polyline drawEdgeShape(Object drawEdge) {
		return (Polyline) edges.get(drawEdge);
	}

	/**
	 * Removes uncompleted edge.
	 * 
	 * @param drawEdge
	 *            Edge to be removed.
	 */
	public void removeDrawEdge(Object drawEdge) {
		if (!(drawEdge instanceof Edge)) {
			edges.remove(drawEdge);
		}
	}

	/**
	 * Puts uncompleted edge into hashmap.
	 * 
	 * @param drawEdge
	 *            Uncompleted edge.
	 * @param poly
	 *            Corresponding polyline.
	 */
	public void putDrawEdge(Object drawEdge, Polyline poly) {
		if (!(drawEdge instanceof Edge)) {
			edges.put(drawEdge, poly);
		}
	}

	/**
	 * Gets the shape of a node.
	 * 
	 * @param n
	 *            The node.
	 * @return The shape.
	 */
	public Rectangle shape(Node n) {
		return (Rectangle) nodes.get(n);
	}

	/**
	 * Resizes all ancestor nodes of a given node.
	 * 
	 * @param n
	 *            The node that starts resizing.
	 */
	public void resizeTillRoot(Node n) {
		while (n != view.getRoot()) {
			if (getView().containsNode(n)) {
				this.autoResize(n);
				n = view.getParent(n);
			} else {
				break;
			}
		}
	}

	/**
	 * Resizes cluster according to its descendants.
	 * 
	 * @param n
	 *            The node to be resized.
	 * @return True iff resizing was successfull.
	 */
	public boolean autoResize(Node n) {
		// root is not resized
		if (n == getView().getRoot() || shape(n) == null) {
			return false;
		}

		java.util.List clusterNodes = getView().getChildren(n);
		if (!clusterNodes.isEmpty()) {
			Point min = new Point(0, 0);
			Point max = new Point(0, 0);

			Iterator clusIt = clusterNodes.iterator();

			// automatically paints clusters according to location
			// of contained nodes

			if (clusIt.hasNext()) {
				Node node = (Node) clusIt.next();
				Rectangle rec = shape(node);
				Point p = new Point();
				if (rec != null) {
					p = rec.getLocation();
				} else {
					p = null;
				}
				if (p != null) {
					min.x = p.x;
					min.y = p.y;
					max.x = p.x + rec.width;
					max.y = p.y + rec.height;
				}
			} else {
			}

			// gets cluster dimensions
			if (n != getView().getRoot()) {
				while (clusIt.hasNext()) {
					Node node = (Node) clusIt.next();
					Rectangle rec = shape(node);
					Point p = new Point();
					if (rec != null) {
						p = rec.getLocation();
					} else {
						p = null;
					}
					if (p != null) {
						if (p.x < min.x) {
							min.x = p.x;
						}
						if (p.y < min.y) {
							min.y = p.y;
						}
						if (p.x + rec.width > max.x) {
							max.x = p.x + rec.width;
						}
						if (p.y + rec.height > max.y) {
							max.y = p.y + rec.height;
						}
					}
				}
			}

			// sets new dimensions
			Rectangle rec = new Rectangle(new Point(min.x - prefs.clusOffset,
					min.y - prefs.clusOffset));
			rec.setSize(new Dimension(max.x - min.x + 2 * prefs.clusOffset,
					max.y - min.y + 2 * prefs.clusOffset));

			Rectangle oldRec = shape(n);
			oldRec.setSize(rec.getSize());
			oldRec.setLocation(rec.getLocation());
			if (getMovingPoints().containsKey(n)) {
				getMovingPoints().put(n, rec.getLocation());
			}

			// changes polylines due to different rectangle
			Iterator it = getView().getInEdges(n).iterator();
			while (it.hasNext()) {
				Edge edg = (Edge) it.next();
				shape(edg).setEnd(shape(n));
			}
			it = getView().getOutEdgesIterator(n);
			while (it.hasNext()) {
				Edge edg = (Edge) it.next();
				shape(edg).setStart(shape(n));
			}
		} else {
			Rectangle rec = shape(n);
			rec.setSize(prefs.leafWidth, prefs.leafHeight);
		}
		return true;
	}

	/**
	 * Gets the movingPoints hashmap.
	 * 
	 * @return The movingPoints hashmap.
	 */
	public HashMap getMovingPoints() {
		return movingPoints;
	}

	/**
	 * Attaches new observer.
	 * 
	 * @param observer
	 *            The new observer.
	 */
	public void attach(GeometryObserver observer) {
		Observers.add(observer);
	}

	/**
	 * Removes an observer.
	 * 
	 * @param observer
	 *            The observer to be removed.
	 */
	public void detach(GeometryObserver observer) {
		Observers.remove(observer);
	}

	/**
	 * Notifies all observer of changes.
	 * 
	 * @param action
	 *            The kind of change which was performed.
	 */
	private void notify(Action action) {
		Iterator it = Observers.iterator();
		while (it.hasNext()) {
			((GeometryObserver) it.next()).update(action);
		}
	}

	/**
	 * Gets the current drawing style.
	 * 
	 * @return Current drawing style.
	 */
	public DrawingStyle getDrawingStyle() {
		return drawingStyle;
	}

	/**
	 * Resizes an entire subtree.
	 * 
	 * @param n
	 *            Root at which resizing will start.
	 */
	public void resizeAll(Node n) {
		Iterator it = getView().getChildrenIterator(n);
		while (it.hasNext()) {
			Node child = (Node) it.next();
			if (getView().hasChildren(child)) {
				resizeAll(child);
				autoResize(child);
			}
		}
	}

	/**
	 * Sets resize flag.
	 * 
	 * @param r
	 *            The new value of flag.
	 */
	public void setResize(boolean r) {
		resize = r;
	}

	/**
	 * Test purpose only.
	 * 
	 * @return True if test is passed.
	 */
	public boolean checkConsistency() {
		Iterator it = edges.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (!(entry.getKey() instanceof Edge)) {
				continue;
			}
			Edge edge = (Edge) entry.getKey();
			Polyline poly = ((Polyline) entry.getValue());
			Rectangle sourceShape = shape(edge.getSource());
			Rectangle tarshape = shape(edge.getTarget());
			assert (sourceShape == poly.getStart()) || sourceShape == null;
			assert tarshape == poly.getEnd() || tarshape == null;
		}
		return true;

	}

    /**
     * proepste: only for testing purposes. allows a change of  drawingstyle
     * without redrawing the entire view
     * @param ds
     * @param redraw
     */
    public void setDrawingStyle(DrawingStyle ds, boolean redraw) {
        drawingStyle = ds;
		if (ds instanceof SugiyamaDrawingStyle) {
			prefs.algorithm = "sugi";
		} else {
			prefs.algorithm = "default";
		}
		if(redraw) {
		    redraw();
		}
    }
}