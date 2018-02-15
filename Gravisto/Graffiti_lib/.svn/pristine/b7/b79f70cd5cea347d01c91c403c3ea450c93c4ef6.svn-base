package org.visnacom.model;

import java.util.*;
import java.math.*;

/*
 * Created on 12.12.2004
 *
 */

//
// Not used anymore !!!!
//
/**
 * @author F. Pfeiffer
 * 
 * A concrete implementation of a compound graph. Extends the abstract class
 * BaseCompoundGraph.
 */
public class Static extends BaseCompoundGraph {

	/**
	 * Standard constructor.
	 */
	public Static() {
		super();

	}

	/**
	 * Copy-Constructor.
	 * 
	 * @param s
	 *            To be copied.
	 */
	public Static(Static s) {
		super(s);

	}

	/**
	 * Gets the edges between children of a given inner node.
	 * 
	 * @param innerNode
	 *            The given inner node.
	 * @return The edges between given inner node.
	 */
	public Collection contDerEdges(Node innerNode) {
		return this.getChildEdges(innerNode);
	}

	/**
	 * @param source
	 *            Source node.
	 * @param target
	 *            Target node.
	 * @return New Edge.
	 */
	protected Edge newEdgeSupressDynBinding(Node source, Node target) {
		Edge edg = super.newEdgeSupressDynBinding(source, target);
		return edg;
	}

	/**
	 * Additional method which is used for supressing dynamic binding in View.
	 * 
	 * @param edge
	 *            The edge to be deleted.
	 */
	protected void deleteEdgeSupressDynBinding(Edge edge) {
		super.deleteEdgeSupressDynBinding(edge);
	}

	/**
	 * Makes a new cluster.
	 * 
	 * @param l
	 *            The nodes one wants to make a cluster of.
	 * @return The node representing the cluster.
	 */
	public Node split(List l) {
		Node n = super.split(l);
		return n;
	}

	/**
	 * Initially calculates all interior edges (for split).
	 * 
	 * @param innerNode
	 *            The cluster.
	 * @return List of interior edges.
	 */
	private List getChildEdges(Node innerNode) {
		BigInteger innerNodeLevel = this.inclusionDepth(innerNode);
		List edges = new LinkedList();
		List child = this.getChildren(innerNode);
		Iterator it = child.iterator();
		while (it.hasNext()) {
			Node cNode = (Node) it.next();
			LinkedList targets = new LinkedList();
			List allDesc = allDescendants(cNode);
			allDesc.addAll(child);
			allDesc.remove(cNode);
			Iterator it2 = allDesc.iterator();
			while (it2.hasNext()) {
				Node n = (Node) it2.next();
				List adj = this.getAdjEdges(n);
				Iterator it3 = adj.iterator();

				while (it3.hasNext()) {
					Edge edg = (Edge) it3.next();
					Node source = edg.getSource();
					Node target = edg.getTarget();

					Node sourcePar = this.getAncAtLevel(source, innerNodeLevel
							.add(new BigInteger("1", 2)));
					Node targetPar = this.getAncAtLevel(target, innerNodeLevel
							.add(new BigInteger("1", 2)));
					//					System.err.println(targetPar == root);
					//System.err.println(source == sourcePar && target ==
					// targetPar);

					if (sourcePar == cNode && !targets.contains(targetPar)
							&& this.isAncestor(innerNode, target)
							&& targetPar != cNode) {
						//System.out.println("TEST2");
						targets.add(targetPar);
					}
				}
			}
			it2 = targets.iterator();
			while (it2.hasNext()) {
				Node target = (Node) it2.next();
				//System.err.println(cNode);
				edges.add(this.newEdgeFac(cNode, target));
			}
		}
		return edges;
	}

	/**
	 * Calculates all children of a given node for which there is an induced
	 * edge from the child to the corresponding end node of given edge.
	 * 
	 * @param edge
	 *            The given edge which is to be 'expanded'.
	 * @param node
	 *            The node for the children of which we are looking.
	 * @param isLeaf
	 *            True if node is leaf in view.
	 * @return List of children for which there is an induced edge.
	 */
	public List expandEdge(Edge edge, Node node, boolean isLeaf) {
		Node secNode = null;
		if (node == edge.getTarget()) {
			secNode = edge.getSource();
		} else {
			secNode = edge.getTarget();
		}

		Iterator it;
		LinkedList result = new LinkedList();

		//actually leaves and clusters
		List childNodes = getChildren(node);

		it = childNodes.iterator();
		while (it.hasNext()) {

			Node n = (Node) it.next();

			//			System.out.println("Edge_exp: "+edge);

			if (node == edge.getTarget()) {
				//				System.out.println("Test1: ");
				if (this.derivedEdge(secNode, n)) {
					result.add(n);
				}
			} else {
				//				System.out.println("Test2: "+n+" "+secNode);
				if (this.derivedEdge(n, secNode)) {
					//					System.out.println("add: "+n);
					result.add(n);
				}
			}
		}

		System.out.println("Static_exp: " + result.size());
		return result;
	}

	/**
	 * Gets the ancestor of a given node at a specified level.
	 * 
	 * @param desc
	 *            The node for the ancestor of which one is looking.
	 * @param level
	 *            The level of the ancestor.
	 * @return The ancestor at given level.
	 */
	private Node getAncAtLevel(Node desc, BigInteger level) {
		Node result = null;
		// efficient implementation later
		result = dummyAncAtLevel(desc, level);
		return result;
	}

	/**
	 * Dummy method. To be removed by efficient alg.
	 * 
	 * @param desc
	 * @param level
	 * @return Ancestor at given level.
	 */
	private Node dummyAncAtLevel(Node desc, BigInteger level) {

		if (desc == getRoot()) {
			return getRoot();
		} else if (level.equals(this.inclusionDepth(desc))) {
			return desc;
		}
		Node par = getParent(desc);
		while (this.inclusionDepth(par).compareTo(level) > 0
				&& par != getRoot()) {
			par = getParent(par);
		}

		return par;
	}


	/**
	 * Checks if there is a derived edge from u to v. Does not work!!
	 * 
	 * @param u
	 *            Source of potential derived edge.
	 * @param v
	 *            Target of potential derived edge.
	 * @return True if there is a derived edge from u to v; false otherwise.
	 */
	public boolean derivedEdge(Node u, Node v) {
		List allDesc = allDescendants(u);
		allDesc.add(u);
		Iterator it = allDesc.iterator();
		while (it.hasNext()) {
			//			System.out.println("Test3");
			Node n = (Node) it.next();
			List adj = this.getAdjEdges(n);
			Iterator it2 = adj.iterator();

			while (it2.hasNext()) {
				Edge edg = (Edge) it2.next();
				Node source = edg.getSource();
				Node target = edg.getTarget();
				//				System.out.println("Test4: "+u+" "+source+(this.isAncestor(u,
				// source)) + (this.isAncestor(v, target)));
				//				System.out.println("Test4_2: "+(this.isAncestor(u, target)) +
				// (this.isAncestor(v, source)));
				if ((this.isAncestor(u, source) && this.isAncestor(v, target))
						|| (this.isAncestor(u, target) && this.isAncestor(v,
								source))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets all edges in basegraph that case given derived edge.
	 * 
	 * @param u
	 *            Given derived edge.
	 * @return List of edges that result in given derived edge.
	 */
	public Collection edgeReport(Edge u) {
		List result = new LinkedList();

		return result;
	}

}