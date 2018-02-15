/*
 * Created on 12.12.2004
 *
 */

package org.visnacom.model;

import java.util.*;
import java.awt.geom.Point2D;
import java.math.*;

import org.visnacom.view.Geometry;
import org.visnacom.view.Polyline;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import java.awt.Rectangle;

/**
 * @author F. Pfeiffer
 * 
 * Implements the view of a hierarchical graph structure.
 */
public class View extends ObservableCompoundGraph implements Observer {

	// the base graph of this view
	private BaseCompoundGraph baseGraph;

	// shows if node is contracted in view
	private HashMap contracted;

	/**
	 * Constructor. Root is contracted and sets the basegraph.
	 * 
	 * @param bGraph
	 *            The initial base graph.
	 */
	public View(BaseCompoundGraph bGraph) {
		super(bGraph.getRoot());

		baseGraph = bGraph;
		baseGraph.attach(this);
		contracted = new HashMap();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param v
	 *            View to be copied.
	 */
	public View(View v) {
		super(v);
		this.baseGraph = v.baseGraph;
		v.getBaseGraph().attach(this);
		contracted = new HashMap(v.contracted);
	}

	/**
	 * Keeps up edge references.
	 * 
	 * @see org.visnacom.model.CompoundGraph#copyAdjEdges(org.visnacom.model.CompoundGraph)
	 */
	protected void copyAdjEdges(CompoundGraph cpg) {
		Iterator it = cpg.getAllEdgesIterator();
		while (it.hasNext()) {
			Edge edg = (Edge) it.next();
			newGivenEdge(edg);
		}
	}

	/**
	 * Gets the base graph.
	 * 
	 * @return The associated base graph.
	 */
	public BaseCompoundGraph getBaseGraph() {
		return baseGraph;
	}

	/**
	 * The update method. Part of observer pattern.
	 * 
	 * @param action
	 *            The kind of change which has been performed.
	 */
	public void update(Action action) {
		if (action instanceof ActionCreate
				&& action.getAffected() instanceof Node) {
			updateNewNode((Node) action.getAffected());
		} else if (action instanceof ActionCreate
				&& action.getAffected() instanceof Edge) {
			updateNewEdge((Edge) action.getAffected());
		} else if (action instanceof ActionDelete
				&& action.getAffected() instanceof Node) {
			updateDelNode((Node) action.getAffected());
		} else if (action instanceof ActionDelete
				&& action.getAffected() instanceof Edge) {
			updateDelEdge((Edge) action.getAffected());
		} else if (action instanceof ActionInclude
				&& action.getAffected() instanceof Node) {
			updateInclude((Node) action.getAffected());
		} else if (action instanceof ActionMove
				&& action.getAffected() instanceof Node) {
			updateMove((ActionMove) action);
		} else if (action == null) {

		}
	}

	/**
	 * Update method for creation of new cluster.
	 * 
	 * @param clus
	 *            The new cluster node.
	 */
	private void updateInclude(Node clus) {
		List l = baseGraph.getChildren(clus);
		if (this.containsAllNodes(l)) {
			Node par = baseGraph.getParent(clus);
			super.splitGivenCluster(l, par, clus);
		}
		notify(new ActionInclude(clus));
	}

	/**
	 * Update method for deletion of edge.
	 * 
	 * @param edge
	 *            The edge that has been deleted.
	 */
	private void updateDelEdge(Edge edge) {
		Iterator it;
		Node source = edge.getSource();
		Node target = edge.getTarget();

		// if nodes of edge are not contained in view, the ancestors
		// of those edges need to be checked for derived edges
		if (!this.containsNode(source)) {
			source = this.ancMax(source);
		}
		if (!this.containsNode(target)) {
			target = this.ancMax(target);
		}

		// if there are no derived edges anymore, they need to be
		// deleted in view
		if (!baseGraph.derivedEdge(source, target)) {
			// gets edges which need to be deleted
			List edges = this.getEdge(source, target);
			it = edges.iterator();

			while (it.hasNext()) {
				Edge edg = (Edge) it.next();
				super.deleteEdge(edg);
			}
		}

		// if edge is contained in view, this edge is simply removed
		if (this.containsEdge(edge)) {
			super.deleteEdge(edge);
		}
	}

	/**
	 * Update method for deletion of node.
	 * 
	 * @param n
	 *            The node that has been deleted.
	 */
	private void updateDelNode(Node n) {
		if (this.containsNode(n)) {
			if (!hasChildren(n) && !isContracted(n)) {
				super.deleteLeaf(n);
			} else if (isContracted(n) && baseGraph.containsNode(n)) {
				if (n != getRoot()) {
					expand(n);
					super.merge(n);
					notify(new ActionDelete(n));
				}
			} else {
				if (n != getRoot()) {
					super.merge(n);
					notify(new ActionDelete(n));
				}
			}
		}
	}

	/**
	 * Inserts new node into view.
	 * 
	 * @param newNode
	 *            The node to be inserted.
	 */

	private void updateNewNode(Node newNode) {
		Node par = baseGraph.getParent(newNode);
		if (!isContracted(par) || par == getRoot()) {
			super.newGivenNode(newNode, par);
		}
		notify(new ActionCreate(newNode));
	}

	/**
	 * Inserts new edge.
	 * 
	 * @param newEdge
	 *            The edge to be inserted.
	 */
	private void updateNewEdge(Edge newEdge) {
		// check for derived edges
		Node source = newEdge.getSource();
		Node target = newEdge.getTarget();
		if (!this.containsNode(source)) {
			source = this.ancMax(source);
		}
		if (!this.containsNode(target)) {
			target = this.ancMax(target);
		}

		// if edge reference can be kept, this is done with newGivenEdge
		if (source != newEdge.getSource() || target != newEdge.getTarget()) {
			// may have common contracted ancestor in view
			if (source != target && getEdge(source, target).isEmpty()) {
				super.newEdge(source, target);
			}
		} else {
			super.newGivenEdge(newEdge);
			notify(new ActionCreate(newEdge));
		}
	}

	/**
	 * Deals with new nodes due to movement of node into another cluster.
	 * 
	 * @param n
	 *            The moved node.
	 */
	private void newNodeDueToMov(Node n) {
		// inserts new edges if necessary
		Iterator it = baseGraph.getAdjEdges(n).iterator();
		while (it.hasNext()) {
			Edge e = (Edge) it.next();
			Node source = e.getSource();
			Node target = e.getTarget();
			Node sourceAnc = ancMax(source);
			Node targetAnc = ancMax(target);

			if ((containsNode(sourceAnc) && containsNode(targetAnc))
					&& getEdge(sourceAnc, targetAnc).isEmpty()
					&& sourceAnc != targetAnc) {
				super.newEdge(sourceAnc, targetAnc);
			}
		}

		// recursive calling
		it = baseGraph.getChildrenIterator(n);
		while (it.hasNext()) {
			Node child = (Node) it.next();
			newNodeDueToMov(child);
		}
	}

	/**
	 * Updates view if nodes are dragged into another node by user.
	 * 
	 * @param action
	 *            The action causing this update.
	 */
	private void updateMove(ActionMove action) {
		Node node = (Node) action.getAffected();
		Node newPar = (Node) action.getNewPar();
		Node oldPar = (Node) action.getOldPar();

		// update only performed if movement is not due to splitting
		if (!action.isSplitting()) {
			if (!isContracted(newPar) && containsNode(node)) {
				super.moveNode(node, newPar);
			} else if (!isContracted(newPar) && !containsNode(node)) {
				super.newGivenNode(node, newPar);
				notify(new ActionCreate(node, newPar));
				newNodeDueToMov(node);
			}

			// if new parent is contracted in this view, subtree with root
			// 'node' needs to be removed
			if (this.containsNode(node) && isContracted(newPar)) {
				this.delSubTreeWithoutRoot(node);
				super.deleteLeaf(node);
				if (baseGraph.hasChildren(node)) {
					contracted.put(node, "true");
				}
			}

			Node ancOldPar = this.ancMax(oldPar);
			Node ancNewPar = this.ancMax(newPar);

			// if former parent is contracted in this view, it needs to be
			// checked whether derived edges are lost due to dragging
			if (isContracted(ancOldPar)) {
				Iterator it = getAdjEdges(ancOldPar).iterator();
				while (it.hasNext()) {
					Edge edg = (Edge) it.next();
					if (!baseGraph
							.derivedEdge(edg.getSource(), edg.getTarget())) {
						super.deleteEdge(edg);
					}
				}
			}

			// if new parent is contracted, new derived edges may be inserted
			if (isContracted(ancNewPar)) {
				List desc = baseGraph.allDescendants(node);
				desc.add(node);
				Iterator it = desc.iterator();
				while (it.hasNext()) {
					Node n = (Node) it.next();
					Iterator it2 = baseGraph.getAdjEdges(n).iterator();
					while (it2.hasNext()) {
						Edge edge = (Edge) it2.next();
						Node source = edge.getSource();
						Node target = edge.getTarget();
						Node ancSource = this.ancMax(source);
						Node ancTarget = this.ancMax(target);
						List existEdges = this.getEdge(ancSource, ancTarget);
						if (ancSource != ancTarget && existEdges.isEmpty()) {
							super.newEdge(ancSource, ancTarget);
						}
					}
				}
			}
			if(baseGraph.getChildren(oldPar).isEmpty()) {
				contracted.remove(oldPar);
			}
			notify(action);
		}
	}

	/**
	 * Collapses a cluster.
	 * 
	 * @param clus
	 *            The cluster to be contracted.
	 */
	public void contract(Node clus) {
		synchronized (getClass()) {
			ActionContract action = new ActionContract(clus);

			// checks if all children are leaves
			boolean onlyLeavesAsChildren = true;
			Iterator it = this.getChildrenIterator(clus);
			while (it.hasNext()) {
				Node n = (Node) it.next();
				if (this.hasChildren(n)) {
					onlyLeavesAsChildren = false;
					break;
				}
			}

			// if not all children are leaves, nothing is done
			if (onlyLeavesAsChildren) {
				if (!isContracted(clus)) {
					contracted.put(clus, "true");
				}

				// inserts derived edges into view
				it = this.getChildren(clus).iterator();
				while (it.hasNext()) {
					Node n = (Node) it.next();
					LinkedList adj = (LinkedList) getAdjEdges(n);
					Iterator it2 = adj.iterator();
					while (it2.hasNext()) {
						Edge edge = (Edge) it2.next();
						if (edge.getSource() == n
								&& !(getParent(edge.getTarget()) == clus)) {
							LinkedList l = new LinkedList();
							l.add(clus);
							l.add(edge.getTarget());
							List existEdges = this.getEdge(clus, edge
									.getTarget());
							if (existEdges.isEmpty()) {
								Edge newEdge = super.newEdge(clus, edge
										.getTarget());
								action.addMapping(edge, newEdge, true);
							} else {
								action.addMapping(edge, (Edge) existEdges
										.get(0), false);
							}

						} else if (edge.getTarget() == n
								&& !(getParent(edge.getSource()) == clus)) {
							LinkedList l = new LinkedList();
							l.add(edge.getSource());
							l.add(clus);
							List existEdges = this.getEdge(edge.getSource(),
									clus);
							if (existEdges.isEmpty()) {
								Edge newEdge = super.newEdge(edge.getSource(),
										clus);
								action.addMapping(edge, newEdge, true);
							} else {
								action.addMapping(edge, (Edge) existEdges
										.get(0), false);
							}
						}
					}

				}

				action.addAll(getChildren(clus));

				// deletes all descendants of contracted node
				delSubTreeWithoutRoot(clus);
				notify(action);
			}
		}
	}

	/**
	 * Expands a cluster.
	 * 
	 * 
	 * @param clus
	 *            The cluster to be expanded.
	 */
	public void expand(Node clus) {
		// only contracted clusters may be expanded
		if (isContracted(clus)) {

			Iterator it;
			ActionExpand actionExpand = new ActionExpand(clus);
			contracted.remove(clus);

			// inserts child nodes
			it = baseGraph.getChildrenIterator(clus);
			while (it.hasNext()) {
				Node n = (Node) it.next();
				newGivenNode(n, clus);
				if (baseGraph.hasChildren(n) && !isContracted(n)) {
					contracted.put(n, "true");
				}
			}

			// performs expandEdge for all incident derived edges
			List adj = getAdjEdges(clus);
			it = adj.iterator();
			while (it.hasNext()) {
				LinkedList newEdges = new LinkedList();
				Iterator it2;
				Edge edg = (Edge) it.next();
				Node source = edg.getSource();
				Node target = edg.getTarget();
				Node secNode = null;

				if (clus == source) {
					secNode = target;
				} else {
					secNode = source;
					assert (clus == target);
				}

				List expEdge = baseGraph.expandEdge(edg, clus,
						!hasChildren(secNode));

				// inserts new edges incident to children
				it2 = expEdge.iterator();
				while (it2.hasNext()) {
					Node n = (Node) it2.next();
					Edge newEdge = null;
					if (clus == source) {
						newEdge = super.newEdge(n, secNode);
						newEdges.add(newEdge);
					} else {
						newEdge = super.newEdge(secNode, n);
						newEdges.add(newEdge);
					}
				}

				// checks if edge incident to expanded cluster must be removed
				boolean toBeRemoved = true;
				List l = baseGraph.getAdjEdges(clus);
				Iterator adjIter = l.iterator();
				while (adjIter.hasNext()) {
					Edge next = (Edge) adjIter.next();
					// if no node is contracted, edge needs to be in basegraph;
					// otherwise it is checked if descendant of contracted node
					// can cause derived edge
					if (!isContracted(source) && !isContracted(target)) {
						if (source == next.getSource()
								&& target == next.getTarget()) {
							toBeRemoved = false;
						}
					} else if (next.getSource() == clus) {
						if (target == next.getTarget()
								|| (baseGraph.isAncestor(target, next
										.getTarget()))) {
							toBeRemoved = false;
						}
					} else {
						if (source == next.getSource()
								|| (baseGraph.isAncestor(source, next
										.getSource()))) {
							toBeRemoved = false;
						}
					}
				}

				// removes incident edge if necessary
				if (toBeRemoved) {
					actionExpand.addMapping(edg, newEdges, true);
					notify(new ActionDelete(edg, true));
					super.deleteEdge(edg);
				} else {
					if (!newEdges.isEmpty()) {
						actionExpand.addMapping(edg, newEdges, false);
					}

				}
			}

			// inserts edges connection children of expanded cluster
			it = baseGraph.contDerEdges(clus).iterator();
			LinkedList contEdges = new LinkedList();
			while (it.hasNext()) {
				Edge edg = (Edge) it.next();
				super.newGivenEdge(edg);
				contEdges.add(edg);
			}

			actionExpand.contDerEdges = contEdges;
			notify(actionExpand);
		}
		assert testExpand();
	}

	/**
	 * Adds a new node (leaf). Calls respective method in base graph.
	 * 
	 * @param par
	 *            The parent node of the new leaf.
	 * @return The new leaf.
	 */
	public Node newLeaf(Node par) {
		Node leaf = baseGraph.newLeaf(par);
		return leaf;
	}

	/**
	 * Adds a new edge. Calls respective method in base graph.
	 * 
	 * @param source
	 *            The source node.
	 * @param target
	 *            The target node.
	 * @return The new edge.
	 */
	public Edge newEdge(Node source, Node target) {
		List edges = baseGraph.getEdge(source, target);
		//supress multiple edges
		if (!edges.isEmpty()) {
			throw new InvalidEdgeException("No multiple edges allowed!");
		}
		Edge edge = baseGraph.newEdge(source, target);
		return edge;
	}

	/**
	 * Deletes edge. Calls respective method in base graph.
	 * 
	 * @param edge
	 *            The edge which is to be deleted.
	 */
	public void deleteEdge(Edge edge) {
		boolean coll = isContracted(edge.getSource())
				|| isContracted(edge.getTarget());
		Collection rep = baseGraph.edgeReport(edge);
		if (!coll && rep.contains(edge)) {
			rep.clear();
			rep.add(edge);
		}
		Iterator base = rep.iterator();
		while (base.hasNext()) {
			Edge baseEdge = (Edge) base.next();
			baseGraph.deleteEdge(baseEdge);
		}
	}

	/**
	 * Deletes a leaf. Calls respective method in base graph.
	 * 
	 * @param leaf
	 *            The leaf to be deleted.
	 */
	public void deleteLeaf(Node leaf) {
		baseGraph.deleteLeaf(leaf);
	}

	/**
	 * Makes a new cluster. Calls respective method in base graph.
	 * 
	 * @param c
	 *            The nodes one wants to make a cluster of.
	 * @return The node representing the cluster.
	 */
	public Node split(List c) {
		Node result = baseGraph.split(c);
		return result;
	}

	/**
	 * Merges a cluster with the rest of the graph. Calls respective method in
	 * base graph.
	 * 
	 * @param innerNode
	 *            The cluster node.
	 */
	public void merge(Node innerNode) {
		if (!isContracted(innerNode)) {
			baseGraph.merge(innerNode);
		}
	}

	/**
	 * Gets the contracted ancestor of a given node.
	 * 
	 * @param desc
	 *            The ancestor of this node is looked for.
	 * @return The contracted ancestor if one exists; node itself otherwise.
	 */
	private Node ancMax(Node desc) {
		Node result = desc;
		Node par = baseGraph.getParent(desc);
		while (par != getRoot() && par != null) {
			if (getParent(par) != null && isContracted(par)) {
				result = par;
			}
			par = baseGraph.getParent(par);
		}
		return result;
	}

	/**
	 * Moves node into another cluster. Simply calls basegraph method.
	 * 
	 * @see org.visnacom.model.CompoundGraph#moveNode(org.visnacom.model.Node, org.visnacom.model.Node)
	 */
	public void moveNode(Node node, Node newParent) {
		baseGraph.moveNode(node, newParent);
	}

	/**
	 * Simply calls basegraph method.
	 * 
	 * @see org.visnacom.model.CompoundGraph#getSubtree(org.visnacom.model.Node)
	 */
	public List getSubtree(Node rootSubtree) {
		return baseGraph.getSubtree(rootSubtree);
	}

	/**
	 * Gets the depth of a node in basegraph.
	 * 
	 * @param n
	 *            The node the depth of which one wants.
	 * @return The depth of node n in basegraph.
	 */
	public BigInteger baseInclusionDepth(Node n) {
		return baseGraph.inclusionDepth(n);
	}

	/**
	 * Checks if a node is contracted.
	 * 
	 * @param clus
	 *            The node to be checked.
	 * @return True iff node is contracted.
	 */
	public boolean isContracted(Node clus) {
		return contracted.get(clus) != null;
	}

	/**
	 * Checks if a node is an inner node.
	 * 
	 * @param n
	 *            Node to be checked for being an inner node.
	 * @return True iff node is inner node.
	 */
	public boolean isCluster(Node n) {
		return baseGraph.hasChildren(n);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString();
	}

	/**
	 * Adds views of a basegraph to the document.
	 * 
	 * @param doc
	 *            The document.
	 * @param root
	 *            The part of the document where the edges should be appended.
	 * @param mIds
	 *            Relates real node objects with node ids in XML document.
	 * @param eIds
	 *            Relates real edge objects with edge ids in XML document.
	 * @return Flag signalling whether this view can be saved.
	 *  
	 */
	public boolean saveView(Document doc, Element root, HashMap mIds,
			HashMap eIds) {
		BigInteger derCounter = new BigInteger("0");
		if (doc == null
				|| root == null
				|| (mIds.isEmpty() && bigNumOfNodes().compareTo(
						new BigInteger("1")) > 0)
				|| (eIds.isEmpty() && !getAllEdges().isEmpty())) {
			return false;
		}

		Element extension = doc.createElement("data");
		extension.setAttribute("key", "view");
		Element e = doc.createElement("view");
		e.setAttribute("id", "newView");

		Geometry geometry = (Geometry) getObservers().get(0);

		Iterator nodes = getAllNodesIterator();
		while (nodes.hasNext()) {
			Node n = (Node) nodes.next();
			if (n == getRoot()) {
				continue;
			}
			Rectangle rec = geometry.shape(n);
			Element nodeItem = doc.createElement("node");
			nodeItem.setAttribute("id", mIds.get(n).toString());

			// writes x-coordinate of node
			Element dataX = doc.createElement("data");
			dataX.setAttribute("key", "x");
			Integer xInt = new Integer(0);

			xInt = new Integer(rec.x);

			dataX.appendChild(doc.createTextNode(xInt.toString()));

			// writes y-coordinate of node
			Element dataY = doc.createElement("data");
			dataY.setAttribute("key", "y");
			Integer yInt = new Integer(0);

			yInt = new Integer(rec.y);

			dataY.appendChild(doc.createTextNode(yInt.toString()));

			// writes width of node
			Element dataWidth = doc.createElement("data");
			dataWidth.setAttribute("key", "width");
			Integer widthInt = new Integer(0);
			if (rec == null) {
				widthInt = new Integer(26);
			} else {
				widthInt = new Integer(rec.width);
			}
			dataWidth.appendChild(doc.createTextNode(widthInt.toString()));

			// writes height of node
			Element dataHeight = doc.createElement("data");
			dataHeight.setAttribute("key", "height");
			Integer heightInt = new Integer(0);
			if (rec == null) {
				heightInt = new Integer(26);
			} else {
				heightInt = new Integer(rec.height);
			}
			dataHeight.appendChild(doc.createTextNode(heightInt.toString()));

			nodeItem.appendChild(dataX);
			nodeItem.appendChild(dataY);
			nodeItem.appendChild(dataWidth);
			nodeItem.appendChild(dataHeight);
			e.appendChild(nodeItem);
		}

		Iterator edges = getAllEdgesIterator();
		while (edges.hasNext()) {
			Edge edg = (Edge) edges.next();
			Polyline poly = geometry.shape(edg);
			List cPoints = null;
			// checks if edge is visible in view
			if (poly != null) {
				cPoints = poly.getControlPoints();
			} else {
				cPoints = new LinkedList();
			}

			// adds control point coordinates
			if (!cPoints.isEmpty()) {
				org.visnacom.model.Node source = edg.getSource();
				org.visnacom.model.Node target = edg.getTarget();
				Element elEdge = doc.createElement("edge");
				if (eIds.get(edg) == null) {
					elEdge.setAttribute("id", "derived" + derCounter);
					derCounter = derCounter.add(new BigInteger("1"));
				} else {
					elEdge.setAttribute("id", eIds.get(edg).toString());
				}
				elEdge.setAttribute("source", (String) mIds.get(source));
				elEdge.setAttribute("target", (String) mIds.get(target));
				Iterator it2 = cPoints.iterator();
				while (it2.hasNext()) {
					Point2D.Double p = (Point2D.Double) it2.next();
					Integer x = new Integer((int) p.x);
					Integer y = new Integer((int) p.y);
					Element e_x = doc.createElement("data");
					e_x.setAttribute("key", "poly");
					e_x.appendChild(doc.createTextNode(x.toString()));

					Element e_y = doc.createElement("data");
					e_y.setAttribute("key", "poly");
					e_y.appendChild(doc.createTextNode(y.toString()));

					elEdge.appendChild(e_x);
					elEdge.appendChild(e_y);
				}
				e.appendChild(elEdge);
			}
		}
		root.appendChild(extension);
		extension.appendChild(e);
		return true;
	}

	/**
	 * Deletes an inner node and all of its descendants.
	 * 
	 * @param clus
	 *            The inner node to be removed.
	 */
	public void delClus(Node clus) {
		baseGraph.delSubTree(clus);
		// notify here, since subtree deletion is also used during contract
		notify(new ActionDelete(null));
	}

	/**
	 * Removes this view from observer list due to closing of internal frame.
	 *  
	 */
	public void closeView() {
		baseGraph.detach(this);
	}

	/**
	 * Tests correctness after expansion of all nodes.
	 * 
	 * @return True if test is passed.
	 */
	private boolean testExpand() {
		//		 test purpose only

		boolean correct = true;
		boolean allExpanded = true;
		Iterator testIt = baseGraph.getAllNodesIterator();
		while (testIt.hasNext()) {
			Node nextNode = (Node) testIt.next();
			if (isContracted(nextNode)) {
				allExpanded = false;
			}
		}

		if (allExpanded) {
			if (baseGraph.getAllEdges().size() != this.getAllEdges().size()) {
				correct = false;
				System.out.println("\nERROR: Edge number differs!: "
						+ (baseGraph.getAllEdges().size() - this.getAllEdges()
								.size()));
				System.out.println("Edges pre-collapse: "
						+ baseGraph.getAllEdges().size() + " post: "
						+ this.getAllEdges().size());
			}
			if (baseGraph.getAllNodes().size() != this.getAllNodes().size()) {
				correct = false;
				System.out.println("\nERROR: Node number differs!");
			}
			if (correct || true) {
				Iterator i1 = baseGraph.getAllEdges().iterator();
				while (i1.hasNext()) {
					Edge nextE = (Edge) i1.next();
					Node s1 = nextE.getSource();
					Node t1 = nextE.getTarget();
					boolean exists = false;
					Iterator i2 = this.getAllEdges().iterator();
					while (i2.hasNext()) {
						Edge nextE2 = (Edge) i2.next();
						if (nextE2.getSource() == s1
								&& nextE2.getTarget() == t1) {
							exists = true;
						}
					}
					if (!exists) {
						correct = false;
						System.out.println("\nERROR: " + nextE
								+ " of basegraph does not exist!");
					}
				}

				i1 = getAllEdges().iterator();
				while (i1.hasNext()) {
					Edge nextE = (Edge) i1.next();
					Node s1 = nextE.getSource();
					Node t1 = nextE.getTarget();
					boolean exists = false;
					Iterator i2 = baseGraph.getAllEdges().iterator();
					while (i2.hasNext()) {
						Edge nextE2 = (Edge) i2.next();
						if (nextE2.getSource() == s1
								&& nextE2.getTarget() == t1) {
							exists = true;
						}
					}
					if (!exists) {
						correct = false;
						System.out.println("\nERROR: " + nextE
								+ " of view does not exist!");
					}
				}
			}
			if (!correct) {
				System.out.println("Basegraph: " + baseGraph.toString());
				System.out.println("\n\nView: " + this.toString());
			}
		}
		return correct;
	}

}