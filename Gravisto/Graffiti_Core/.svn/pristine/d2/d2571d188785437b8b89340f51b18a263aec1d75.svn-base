/*
 * Created on 31.01.2005
 *
 *
 */

package org.visnacom.view;


import java.util.*;
import java.awt.Rectangle;
import java.awt.Point;

import org.visnacom.model.*;

/**
 * @author F. Pfeiffer
 * 
 * Default drawing style. No real layout algorithm.
 */
public class DefaultDrawingStyle extends DrawingStyle {

	/**
	 * Constructor.
	 * 
	 * @param geo
	 *            Geometry object.
	 */
	public DefaultDrawingStyle(Geometry geo) {
		super(geo);
	}

	/**
	 * @see org.visnacom.view.DrawingStyle#contract(org.visnacom.model.Node, org.visnacom.view.Geometry,
	 *      org.visnacom.model.ActionContract)
	 */
	public void contract(Node clus, Geometry geo, ActionContract action) {
		Iterator it;
		
		// gives cluster dimensions of a leaf
		geo.shape(clus).setSize(geo.getPrefs().leafWidth,
				geo.getPrefs().leafHeight);

		// clears control points if necessary
		LinkedList adj = (LinkedList) geo.getView().getAdjEdges(clus);
		it = adj.iterator();
		while (it.hasNext()) {
			Edge edg = (Edge) it.next();
			Polyline poly = geo.shape(edg);
			if (clus == edg.getSource()) {
				Rectangle rec = geo.shape(edg.getTarget());
				if (rec != null) {
					poly = new Polyline((geo.shape(clus)), rec, geo
							.getPrefs());
					poly.clearControlPoints();
				}

			} else {
				Rectangle rec = geo.shape(edg.getSource());
				if (rec != null) {
					poly = new Polyline(rec, (geo.shape(clus)), geo
							.getPrefs());
				}
			}
		}

		Node c = geo.getView().getParent(clus);
		while (c != null && c != geo.getView().getRoot()) {
			// resizes clusters
			geo.autoResize(c);
			List adjpar = geo.getView().getAdjEdges(c);
			it = adjpar.iterator();
			// sets edge coordinates
			while (it.hasNext()) {
				Edge edg = (Edge) it.next();
				Polyline poly = geo.shape(edg);
				poly.setStart(geo.shape(edg.getSource()));
				poly.setEnd(geo.shape(edg.getTarget()));
				//				poly.clearControlPoints();
			}
			c = geo.getView().getParent(c);
		}
	}

	/**
	 * @see org.visnacom.view.DrawingStyle#expand(org.visnacom.model.Node, org.visnacom.view.Geometry,
	 *      org.visnacom.model.ActionExpand)
	 */
	public void expand(Node clus, Geometry geo, ActionExpand action) {

		int xCount = 0;
		int yCount = 0;
		Point p = geo.shape(clus).getLocation();

		java.util.List children = geo.getView().getChildren(clus);

		int noChildren = children.size();
		int xDim = (int) Math.round(Math.sqrt(noChildren));

		// puts children into cluster
		Iterator it = children.iterator();
		while (it.hasNext()) {
			if (xCount == xDim) {
				xCount = 0;
				yCount++;
			}
			// x-/y-distances betwenn children
			int disX = (geo.getPrefs().clusOffset / 2)
					+ geo.getPrefs().leafWidth;
			int disY = (geo.getPrefs().clusOffset / 2)
					+ geo.getPrefs().leafHeight;
			Node n = (Node) it.next();
			Rectangle rec = geo.shape(n);

			// coordinates of child
			int x = p.x + xCount * disX + geo.getPrefs().clusOffset;
			int y = p.y + yCount * disY;

			rec.setLocation(x, y + geo.getPrefs().clusOffset);
			rec.setSize(geo.getPrefs().leafWidth, geo.getPrefs().leafHeight);
			xCount++;
		}

		// sets location of expanded clusters
		Rectangle expRec = geo.shape(clus);
		Point loc = expRec.getLocation();
		expRec.setLocation(loc);

		// resizing of clusters
		Node c = clus;
		while (c != geo.getView().getRoot()) {
			geo.autoResize(c);
			c = geo.getView().getParent(c);
		}

		// sets edge coordinates
		it = geo.getView().getAllEdgesIterator();
		while (it.hasNext()) {
			Edge key = (Edge) it.next();
			Polyline value = geo.shape(key);
			value.setStart(geo.shape(key.getSource()));
			value.setEnd(geo.shape(key.getTarget()));
		}
	}

	/**
	 * @see org.visnacom.view.DrawingStyle#draw(org.visnacom.view.Geometry)
	 */
	public void draw(Geometry newGeo) {
		// does nothing
	}

}