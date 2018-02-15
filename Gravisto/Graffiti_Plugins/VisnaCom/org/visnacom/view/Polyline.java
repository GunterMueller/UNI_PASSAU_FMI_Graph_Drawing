/*
 * Created on 13.12.2004
 *
 * 
 */

package org.visnacom.view;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.*;
import java.util.*;

import org.visnacom.controller.*;


/**
 * @author F. Pfeiffer
 * 
 * This class provides the graphical depiction of edges, whether be it smooth
 * curves or lines. Implementation uses java's general path concept.
 */
public class Polyline {

	//list of control points for quadratic curves
	private LinkedList controlPoints = new LinkedList();

	// clone of control point list
	private LinkedList controlClone = new LinkedList();

	// list of path points on line
	private LinkedList pathPoints = new LinkedList();

	// are endpoints fixed
	//	private boolean fixed = true;

	// shows whether end nodes have been relocated
	private static boolean reloc = false;

	// the two rectangles which are connected by polyline
	private Rectangle startRec, endRec;

	//	 clones of the points mentioned above
	private Rectangle startClone, endClone;

	//	 contains preference settings
	public Preferences prefs;

	/**
	 * Constructor.
	 * 
	 * @param start
	 *            Describes the start of an edge.
	 * @param end
	 *            Describes the end of an edge.
	 * @param pref
	 *            The preferences for this polyline.
	 */
	public Polyline(Rectangle start, Rectangle end, Preferences pref) {
		startRec = start;
		endRec = end;
		// makes clones
		startClone = (Rectangle) startRec.clone();
		endClone = (Rectangle) endRec.clone();
		prefs = pref;
	}

	/**
	 * Constructor. Initializes only one end of edge.
	 * 
	 * @param start
	 *            Describes the start of an edge.
	 * @param pref
	 *            The preferences for this polyline.
	 */
	public Polyline(Rectangle start, Preferences pref) {
		startRec = start;
		// makes clone
		startClone = (Rectangle) startRec.clone();
		endClone = new Rectangle();
		prefs = pref;
	}

	/**
	 * Gets the starting point.
	 * 
	 * @return Starting point of an edge.
	 */
	public Rectangle getStart() {
		return (Rectangle) startRec;
	}

	/**
	 * Gets the ending point.
	 * 
	 * @return Ending point of an edge.
	 */
	public Rectangle getEnd() {
		return (Rectangle) endRec;
	}

	/**
	 * Draws the edge.
	 * 
	 * @return The path representing the edge.
	 *  
	 */
	public GeneralPath draw() {
		if (startRec.x == endRec.x && startRec.y == endRec.y
				&& startRec.width == endRec.width
				&& startRec.height == endRec.height) {
			return new GeneralPath();
		}
		// checks whether one has a line
		if (controlPoints.isEmpty()) {
			if (endRec == null) {
				endRec = startRec;
			}
			Point2D.Double start = calcLineStartingCoord();
			Point2D.Double end = this.calcLineEndingCoord();
			GeneralPath path = new GeneralPath();
			path.moveTo((int) start.x + 2, (int) start.y + 2);
			path.lineTo((int) end.x + 2, (int) end.y + 2);
			return path;

		}
		// draws curve
		return drawPoly();
	}

	/**
	 * Draws a general path, connecting two nodes.
	 * 
	 * @return The path representing the edge.
	 */
	private GeneralPath drawPoly() {
		if (isCompleteRelocating() && reloc) {
			relocateContrPoints();
		}
		// delegates calculation of path points
		calcPathPoints();
		return makePath();
	}

	/**
	 * Draws the arrowhead of directed edges.
	 * 
	 * @return The path representing the arrowhead.
	 */
	public GeneralPath drawArrowHead() {
		if (startRec.x == endRec.x && startRec.y == endRec.y
				&& startRec.width == endRec.width
				&& startRec.height == endRec.height) {
			return new GeneralPath();
		}
		Point2D.Double end;
		//checks whether one has a mere line as an edge
		if (controlPoints.isEmpty()) {
			end = calcLineEndingCoord();
			end.setLocation(end.x + 2, end.y + 2);
		} else {
			end = (Point2D.Double) pathPoints.getLast();
		}
		end.setLocation(end.x, end.y);
		Point2D.Double lastPoint = new Point2D.Double();
		// checks whether one has a mere line as an edge
		if (controlPoints.isEmpty()) {
			lastPoint = new Point2D.Double(calcLineStartingCoord().x,
					calcLineStartingCoord().y);
		} else {
			lastPoint = (Point2D.Double) controlPoints.getLast();
		}

		// gets the angle between edge and horizontal line
		double alpha = Math.atan((end.y - lastPoint.y) / (end.x - lastPoint.x));

		if (lastPoint.y >= end.y) {
			alpha += Math.PI;
		}

		if (lastPoint.y >= end.y && lastPoint.x <= end.x) {
			alpha += Math.PI;
		}

		GeneralPath arrowHead = getArrowHead(alpha, end);
		//g2.draw(arrowHead);
		return arrowHead;

	}

	/**
	 * Creates the arrowhead for directed edges.
	 * 
	 * @param alpha
	 *            Rotation angle.
	 * @param peak
	 *            The position of the arrowhead.
	 * @return A GeneralPath representing the arrowhead.
	 */
	private GeneralPath getArrowHead(double alpha, Point2D.Double peak) {
		GeneralPath arrowHead = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		// constructs arrowhead
		arrowHead.moveTo((float) peak.x, (float) peak.y);
		arrowHead.lineTo((float) peak.x - 10, (float) peak.y);
		arrowHead.moveTo((float) peak.x, (float) peak.y);
		arrowHead.lineTo((float) peak.x, (float) peak.y + 10);

		// rotates arrowhead into correct orientation
		AffineTransform affTrans = new AffineTransform();

		if (alpha < 0) {
			alpha += Math.PI;
		}

		alpha += Math.PI / 4.0;

		//		System.out.println(alpha);
		affTrans.setToRotation(alpha, peak.x, peak.y);
		arrowHead.transform(affTrans);
		return arrowHead;
	}

	/**
	 * Calculates path points out of control points.
	 *  
	 */
	private void calcPathPoints() {
		if (!pathPoints.isEmpty()) {
			pathPoints.clear();
		}
		Point2D.Double lastContrPoint = (Point2D.Double) controlPoints
				.getFirst();
		// checks whether there are any control points
		Iterator it = controlPoints.iterator();
		if (it.hasNext()) {
			it.next();
		} else {
			pathPoints = null;
		}

		// calculates path points as being in the middle between two
		// control points
		while (it.hasNext()) {
			Point2D.Double nextContrPoint = (Point2D.Double) it.next();
			double nextX = lastContrPoint.x + 0.5
					* (nextContrPoint.x - lastContrPoint.x);
			double nextY = lastContrPoint.y + 0.5
					* (nextContrPoint.y - lastContrPoint.y);
			pathPoints.add(new Point2D.Double(nextX, nextY));
			lastContrPoint = nextContrPoint;
		}

		// adds the last point to the path

		Point2D.Double coord = calcEndingCoord();
		pathPoints.add(new Point2D.Double(coord.x, coord.y));

	}

	/**
	 * Adds a control point to the list.
	 * 
	 * @param p
	 *            The point to be added as control point.
	 */
	public void addControl(Point2D p) {
		controlPoints.add(new Point2D.Double(p.getX(), p.getY()));
		controlClone.add(new Point2D.Double(p.getX(), p.getY()));
	}

	/**
	 * Adds first control point.
	 * 
	 * @param p
	 *            New first control point.
	 */
	public void addFirstCtrlPoint(Point p) {
		controlPoints.add(0, new Point2D.Double(p.x, p.y));
		controlClone.add(0, new Point2D.Double(p.x, p.y));
	}

	/**
	 * Removes first control point.
	 *  
	 */
	public void delFirstCtrlPoint() {
		controlPoints.removeFirst();
		controlClone.removeFirst();
	}

	/**
	 * Removes last control point.
	 *  
	 */
	public void delLastCtrlPoint() {
		controlPoints.removeLast();
		controlClone.removeLast();
	}

	/**
	 * Sets the ending coordinates.
	 * 
	 * @param rec
	 *            Shape with ending coordinates
	 */
	public void setEnd(Rectangle rec) {
		Point location = rec.getLocation();
		Dimension size = rec.getSize();
		endRec.setLocation(location);
		endRec.setSize(size);
		endClone = (Rectangle) endRec.clone();
	}

	/**
	 * Sets the starting coordinates.
	 * 
	 * @param rec
	 *            Shape with starting coordinates
	 */
	public void setStart(Rectangle rec) {
		Point location = rec.getLocation();
		Dimension size = rec.getSize();
		startRec.setLocation(location);
		startRec.setSize(size);
		startClone = (Rectangle) startRec.clone();
	}

	/**
	 * Removes all control points from list.
	 *  
	 */
	public void clearControlPoints() {
		controlPoints.clear();
		controlClone.clear();
	}

	/**
	 * Gets a list with all control points.
	 * 
	 * @return List containing all control points.
	 */
	public List getControlPoints() {
		return (List) this.controlPoints.clone();
	}

	/**
	 * Checks whether the edge with both ending points has been relocated. ->
	 * curve connecting nodes remains unaltered due to equal movement of nodes
	 * 
	 * @return True if it is a complete relocating.
	 */
	private boolean isCompleteRelocating() {
		int xStartDiff = startRec.x - startClone.x;
		int yStartDiff = startRec.y - startClone.y;
		int xEndDiff = endRec.x - endClone.x;
		int yEndDiff = endRec.y - endClone.y;
		// have both nodes been moved the same way?
		return (xStartDiff == xEndDiff)
				&& (yStartDiff == yEndDiff)
				&& !(xStartDiff == 0 && yStartDiff == 0 && xEndDiff == 0 && yEndDiff == 0);
	}

	/**
	 * Calculates the exact reloctaion of both nodes.
	 * 
	 * @return The difference between old and new coordinates.
	 */
	private Point getRelocating() {
		int xStartDiff = startRec.x - startClone.x;
		int yStartDiff = startRec.y - startClone.y;
		return new Point(xStartDiff, yStartDiff);
	}

	/**
	 * Moves control points due to result from getRelocation.
	 *  
	 */
	private void relocateContrPoints() {
		Iterator it = controlPoints.iterator();
		Iterator itClone = controlClone.iterator();
		Point p = getRelocating();
		while (it.hasNext() && itClone.hasNext()) {
			Point2D.Double clone = (Point2D.Double) ((Point2D.Double) itClone
					.next()).clone();
			double x = (clone.x);
			double y = (clone.y);
			// sets new location
			((Point2D.Double) it.next()).setLocation(x + p.x, y + p.y);
		}
	}

	/**
	 * Sets the flag determining whether nodes have been moved.
	 * 
	 * @param r
	 *            The new value of the flag.
	 */
	public static void setReloc(boolean r) {
		reloc = r;
	}

	/**
	 * Makes new clones of control points and ending points.
	 *  
	 */
	public void reclone() {
		// clones control points
		Iterator it = controlPoints.iterator();
		controlClone.clear();
		while (it.hasNext()) {
			controlClone.add((Point2D.Double) ((Point2D.Double) it.next())
					.clone());
		}
		// clones ending points
		startClone = (Rectangle) startRec.clone();
		endClone = (Rectangle) endRec.clone();
	}

	/**
	 * Builds the general path representing the polyline.
	 * 
	 * @return The general path.
	 */
	private GeneralPath makePath() {
		GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		// sets the starting point of path
		Point2D.Double coord = calcStartingCoord();
		path.moveTo((float) coord.x, (float) coord.y);

		// construction of general path
		Iterator it = controlPoints.iterator();
		Iterator pathIter = pathPoints.iterator();

		// if mouse points to middle of node during drawing of a new edge
		if (endRec.x <= 0.0 && endRec.y <= 0.0) {
			return path;
		}

		if (!controlPoints.isEmpty()) {
			try {
				while (it.hasNext()) {
					Point2D.Double p = (Point2D.Double) it.next();
					Point2D.Double lastCtrl = (Point2D.Double) controlPoints
							.getLast();
					// deals with the case that mouse pointer lies upon
					// controlpoint durin drawing of a new edge
					if (endRec.getLocation().x == (int) lastCtrl.x
							&& endRec.getLocation().y == (int) lastCtrl.y
							&& !it.hasNext()) {
						path.lineTo((float) p.x, (float) p.y);
						continue;
					}
					if (prefs.curveType.equals("smooth")) {
						if (pathIter.hasNext()) {
							Point2D.Double pathPoint = (Point2D.Double) pathIter
									.next();
							// quadratic curve segment
							path.quadTo((float) p.x, (float) p.y,
									(float) pathPoint.x, (float) pathPoint.y);
						} else {
							path.lineTo((float) p.x, (float) p.y);
						}
					} else {
						Point2D.Double last = (Point2D.Double) this.controlPoints
								.getLast();
						path.lineTo((float) p.x, (float) p.y);
						if (!it.hasNext()) {
							Point2D.Double end = calcEndingCoord();
							// deals with the case that mouse pointer lies upon
							// controlpoint durin drawing of a new edge
							if ((int) last.x != (int) (endRec.x + (float) (endRec.width * 0.5))
									|| (int) last.y != (int) ((float) endRec.y + (float) (endRec.height * 0.5))) {
								path.lineTo((float) end.x, (float) end.y);
							}
						}
					}
				}
			} catch (Exception exc) {
				System.err.println("debug_error");
			}
		} else {
			path.moveTo((float) this.calcLineStartingCoord().x, (float) this
					.calcLineStartingCoord().y);
			path.lineTo((float) this.calcLineEndingCoord().x, (float) this
					.calcLineEndingCoord().y);
		}
		return path;
	}

	/**
	 * Checks if the edge intersects a rectangle at (x,y)
	 * 
	 * @param x
	 *            x-coordinate of (x,y)
	 * @param y
	 *            y-coordinate of (x,y)
	 * @return True iff edge intersects rectangle.
	 */
	public boolean intersects(int x, int y) {
		boolean intersects = false;
		GeneralPath path = new GeneralPath();
		if (controlPoints.isEmpty()) {
			// creates the general path for the edge
			path = makePath();
			// checks if there is an intersection
			return path.intersects(x - 5, y - 5, 10, 10);
		} else if (prefs.curveType.equals("polyline")) {
			// checks polyline intersection by checking intersection
			// for each curve segment
			Point2D.Double last = calcStartingCoord();
			Iterator it = controlPointsIterator();
			while (it.hasNext()) {
				path = new GeneralPath();
				Point2D.Double ctrl = (Point2D.Double) it.next();
				path.moveTo((float) last.x, (float) last.y);
				path.lineTo((float) ctrl.x, (float) ctrl.y);

				if (path.intersects(x - 5, y - 5, 10, 10)) {
					intersects = true;
					break;
				}
				last = ctrl;
			}
			path = new GeneralPath();
			path.moveTo((float) last.x, (float) last.y);
			Point2D.Double end = calcEndingCoord();
			path.lineTo((float) end.x, (float) end.y);
			if (path.intersects(x - 5, y - 5, 10, 10)) {
				intersects = true;
			}
			return intersects;
		} else {
			// checks polyline intersection by checking intersection
			// for each curve segment; this time for smooth curves
			Point2D.Double last = calcStartingCoord();
			Iterator it = controlPointsIterator();
			Iterator it2 = pathPoints.iterator();
			while (it.hasNext()) {
				Point2D.Double ctrl = (Point2D.Double) it.next();
				path = new GeneralPath();
				path.moveTo((float) last.x, (float) last.y);
				Point2D.Double pathPoint = new Point2D.Double();
				if (it2.hasNext()) {
					pathPoint = (Point2D.Double) it2.next();
				} else {
					pathPoint = calcEndingCoord();
				}
				path.quadTo((float) ctrl.x, (float) ctrl.y,
						(float) pathPoint.x, (float) pathPoint.y);
				if (path.intersects(x - 5, y - 5, 10, 10)) {
					intersects = true;
					break;
				}
				last = pathPoint;
			}
			return intersects;
		}
	}

	/**
	 * Marks edges that would be selected if appropriate actions were performed
	 * (e.g. mouse click, mouse release).
	 * 
	 * @return List with path points and control points.
	 */
	public ArrayList drawWithinSelection() {
		ArrayList result = new ArrayList();
		LinkedList pPoints = new LinkedList();
		LinkedList connPoints = new LinkedList();
		// checks whether edge is a line, a quadratic curve or a general path
		if (controlPoints.isEmpty()) {
			connPoints = drawConnectionsLine();
		} else if (controlPoints.size() == 1) {
			connPoints = drawConnections();
		} else {
			if (prefs.curveType.equals("smooth")) {
				Object tmp = pathPoints.removeLast();
				Iterator it = pathPoints.iterator();
				while (it.hasNext()) {
					Point2D.Double p = (Point2D.Double) it.next();
					pPoints.add(p);
				}
				pathPoints.addLast(tmp);
			}
			connPoints = drawConnections();
		}
		result.add(pPoints);
		result.add(connPoints);
		return result;
	}

	/**
	 * Marks selected edges.
	 * 
	 * @return List containing path points, control points and connection
	 *         points.
	 */
	public ArrayList drawSelection() {
		ArrayList result = new ArrayList();
		LinkedList pPoints = new LinkedList();
		LinkedList cPoints = new LinkedList();
		LinkedList connPoints = new LinkedList();

		// checks whether edge is a line a quadratic curve or a general path
		if (controlPoints.isEmpty()) {
			connPoints = drawConnectionsLine();
		} else if (controlPoints.size() == 1) {
			connPoints = drawConnections();
		} else {
			if (prefs.curveType.equals("smooth")) {
				Object tmp = null;
				if (!pathPoints.isEmpty()) {
					tmp = pathPoints.removeLast();
				}

				Iterator it = pathPoints.iterator();
				while (it.hasNext()) {
					Point2D.Double p = (Point2D.Double) it.next();
					pPoints.add(p);
				}
				if (tmp != null) {
					pathPoints.addLast(tmp);
				}
			}
			connPoints = drawConnections();
		}
		// draws control points (this differs from drawWithinSelection)
		Iterator it2 = controlPoints.iterator();
		while (it2.hasNext()) {
			Point2D.Double p = (Point2D.Double) it2.next();
			cPoints.add(p);
		}

		result.add(pPoints);
		result.add(cPoints);
		result.add(connPoints);
		return result;
	}

	/**
	 * Gets the bounding rectangle of the edge.
	 * 
	 * @return The bounding rectangle of the edge.
	 */
	public Rectangle getBoundingRect() {
		GeneralPath path = makePath();

		if (controlPoints.isEmpty()) {
			path.lineTo((float) endRec.x + (float) (endRec.width * 0.5),
					(float) endRec.y + (float) (endRec.height * 0.5));
		}

		//returns the bounding rectangle
		return path.getBounds();
	}

	/**
	 * Gets the control point located in the vicinity of given point.
	 * 
	 * @param p
	 *            Point near supposed control point.
	 * @return Control point near p, null if there is none.
	 */
	public Point2D.Double stabbedContrPoint(Point p) {
		Point2D.Double result = null;
		// checks all control points
		Iterator it = controlPoints.iterator();
		while (it.hasNext()) {
			Point2D.Double cPoint = (Point2D.Double) it.next();
			if ((p.x <= cPoint.x + 5 && p.x >= cPoint.x - 5)
					&& (p.y <= cPoint.y + 5 && p.y >= cPoint.y - 5)) {
				result = cPoint;
				break;
			}
		}
		return result;
	}

	/**
	 * Calculates the coordinate of the starting point for edges, which needs to
	 * be located at the border of the rectangle representing a node.
	 * 
	 * @return Coordinates of starting point.
	 */
	private Point2D.Double calcStartingCoord() {
		// starting coordinates for sugiyama
		if (this.prefs.algorithm.equals("sugi")) {
			Point2D.Double p = null;
			if (!controlPoints.isEmpty()) {
				p = new Point2D.Double(((Point2D.Double) controlPoints
						.getFirst()).x, ((Point2D.Double) controlPoints
						.getFirst()).y);
			} else {
				p = new Point2D.Double(endRec.x, endRec.y);
			}
			if (p.y <= startRec.y) {
				return new Point2D.Double(startRec.x + 0.5 * startRec.width,
						startRec.y);
			}
			return new Point2D.Double(startRec.x + 0.5 * startRec.width,
					startRec.y + startRec.height);
		}

		// default starting coordinates
		Point2D.Double top = new Point2D.Double();
		Point2D.Double bottom = new Point2D.Double();
		Point2D.Double left = new Point2D.Double();
		Point2D.Double right = new Point2D.Double();

		if (!controlPoints.isEmpty()) {
			Point2D.Double cPoint = ((Point2D.Double) controlPoints.getFirst());
			double lambda = (startRec.width * 0.5)
					/ ((double) ((cPoint.x) - (startRec.x + 0.5 * startRec.width)));
			// gets the x and y coordinate
			double x = lambda
					* (cPoint.x - (startRec.x + startRec.width * 0.5));
			double y = lambda
					* (cPoint.y - (startRec.y + startRec.height * 0.5));

			left = new Point2D.Double(startRec.x + x + startRec.width * 0.5,
					startRec.y + y + startRec.height * 0.5);
			right = new Point2D.Double(startRec.x - x + startRec.width * 0.5,
					startRec.y - y + startRec.height * 0.5);

			// using line equation

			lambda = (startRec.height * 0.5)
					/ ((double) ((cPoint.y) - (startRec.y + 0.5 * startRec.height)));
			x = lambda * (cPoint.x - (startRec.x + startRec.width * 0.5));
			y = lambda * (cPoint.y - (startRec.y + startRec.height * 0.5));

			top = new Point2D.Double(startRec.x + x + startRec.width * 0.5,
					startRec.y + y + startRec.height * 0.5);
			bottom = new Point2D.Double(startRec.x - x + startRec.width * 0.5,
					startRec.y - y + startRec.height * 0.5);

			// checks which point is correct
			if (cPoint.x > startRec.x && left.y >= startRec.y
					&& left.y <= startRec.y + startRec.height) {
				return left;
			} else if (cPoint.x <= startRec.x && right.y >= startRec.y
					&& right.y <= startRec.y + startRec.height) {
				return right;
			} else if (cPoint.y > startRec.y && top.x >= startRec.x
					&& top.x <= startRec.x + startRec.width) {
				return top;
			} else {
				return bottom;
			}
		}
		return new Point2D.Double(startRec.x + startRec.width * 0.5, startRec.y
				+ startRec.height * 0.5);
	}

	/**
	 * Calculates the coordinate of the ending point for edges, which needs to
	 * be located at the border of the rectangle representing a node.
	 * 
	 * @return Coordinates of ending point.
	 */
	private Point2D.Double calcEndingCoord() {
		// ending coordinates for sugiyama
		if (this.prefs.algorithm.equals("sugi")) {
			Point2D.Double p = null;
			if (!controlPoints.isEmpty()) {
				p = new Point2D.Double(((Point2D.Double) controlPoints
						.getLast()).x, ((Point2D.Double) controlPoints
						.getLast()).y);
			} else {
				p = new Point2D.Double(startRec.x, startRec.y);
			}
			if (p.y <= endRec.y) {
				return new Point2D.Double(endRec.x + 0.5 * endRec.width - 2,
						endRec.y - 1);
			}
			return new Point2D.Double(endRec.x + 0.5 * endRec.width - 2,
					endRec.y + endRec.height);
		}

		Point2D.Double top = new Point2D.Double();
		Point2D.Double bottom = new Point2D.Double();
		Point2D.Double left = new Point2D.Double();
		Point2D.Double right = new Point2D.Double();

		if (controlPoints.isEmpty()) {
			return new Point2D.Double(endRec.x, endRec.y);
		}
		Point2D.Double cPoint = ((Point2D.Double) controlPoints.getLast());
		// calculates the lambda factor for parametric line equation
		double lambda = (endRec.width * 0.5)
				/ ((double) ((cPoint.x) - (endRec.x + 0.5 * endRec.width)));
		// gets the x and y coordinate
		double x = lambda * (cPoint.x - (endRec.x + 0.5 * endRec.width));
		double y = lambda * (cPoint.y - (endRec.y + 0.5 * endRec.height));

		left = new Point2D.Double(endRec.x + x + endRec.width * 0.5, endRec.y
				+ y + endRec.height * 0.5);

		right = new Point2D.Double(endRec.x - x + endRec.width * 0.5, endRec.y
				- y + endRec.height * 0.5);

		lambda = (endRec.height * 0.5)
				/ ((double) ((cPoint.y) - (endRec.y + 0.5 * endRec.height)));
		x = lambda * (cPoint.x - (endRec.x + 0.5 * endRec.width));
		y = lambda * (cPoint.y - (endRec.y + 0.5 * endRec.height));

		top = new Point2D.Double(endRec.x + x + endRec.width * 0.5, endRec.y
				+ y + endRec.height * 0.5);

		bottom = new Point2D.Double(endRec.x - x + endRec.width * 0.5, endRec.y
				- y + endRec.height * 0.5);

		// checks which point is correct
		if (cPoint.x > endRec.x && left.y >= endRec.y
				&& left.y <= endRec.y + endRec.height) {
			return left;
		} else if (cPoint.x <= endRec.x && right.y >= endRec.y
				&& right.y <= endRec.y + endRec.height) {
			return right;
		} else if (cPoint.y > endRec.y && top.x >= endRec.x
				&& top.x <= endRec.x + endRec.width) {
			return top;
		} else {
			return bottom;
		}

	}

	/**
	 * Draws the small rectangles at the intersections of edges and
	 * corresponding nodes.
	 * 
	 * @return List containing info about connections.
	 */
	private LinkedList drawConnections() {
		LinkedList result = new LinkedList();
		Point2D.Double startingCoord = calcStartingCoord();
		Point2D.Double endingCoord = calcEndingCoord();
		startingCoord.setLocation(startingCoord.x - 2, startingCoord.y - 2);
		endingCoord.setLocation(endingCoord.x - 2, endingCoord.y - 2);
		result.add(startingCoord);
		result.add(endingCoord);
		return result;
	}

	/**
	 * Calculates the intersection point of edge and node for edges being mere
	 * lines.
	 * 
	 * @return The intersection point of ending node and edge.
	 */
	private Point2D.Double calcLineEndingCoord() {
		// ending coordinates for sugiyama
		if (this.prefs.algorithm.equals("sugi")) {
			Point2D.Double p = new Point2D.Double(startRec.x, startRec.y);
			if (p.y <= endRec.y) {
				return new Point2D.Double(endRec.x + 0.5 * endRec.width - 2,
						endRec.y - 3);
			}
			return new Point2D.Double(endRec.x + 0.5 * endRec.width - 2,
					endRec.y + endRec.height);
		}

		double lambda = 0;
		double x = 0;
		double y = 0;

		Point2D.Double top = new Point2D.Double();
		Point2D.Double bottom = new Point2D.Double();
		Point2D.Double left = new Point2D.Double();
		Point2D.Double right = new Point2D.Double();

		double endXCenter = endRec.x + endRec.width * 0.5;
		double endYCenter = endRec.y + endRec.height * 0.5;
		double startXCenter = startRec.x + startRec.width * 0.5;
		double startYCenter = startRec.y + startRec.height * 0.5;

		// determines line equation (parametric)
		lambda = (endRec.width * 0.5)
				/ ((double) ((startXCenter) - endXCenter));
		x = lambda * (startXCenter - (endXCenter));
		y = lambda * (startYCenter - (endYCenter));

		// checks which side of node rectangle is intersected, then paints
		// the intersection markings

		left = new Point2D.Double((int) (endRec.x - x) + endRec.width * 0.5,
				(int) (endRec.y - y) + endRec.height * 0.5);

		right = new Point2D.Double((int) (endRec.x + x + endRec.width * 0.5),
				(int) (endRec.y + y) + endRec.height * 0.5);

		lambda = (endRec.height * 0.5)
				/ ((double) ((startYCenter) - endYCenter));
		x = lambda * (startXCenter - (endXCenter));
		y = lambda * (startYCenter - (endYCenter));

		bottom = new Point2D.Double((int) (endRec.x + x) + +endRec.width * 0.5,
				(int) (endRec.y + y) + endRec.height * 0.5);

		top = new Point2D.Double((int) (endRec.x - x) + endRec.width * 0.5,
				(int) (endRec.y - y) + endRec.height * 0.5);

		// checks which point is correct
		if (endRec.contains(startXCenter, startYCenter)) {
			if (left.y <= endRec.y + endRec.height && left.y >= endRec.y
					&& startXCenter <= endXCenter) {
				left.setLocation(left.x - 2, left.y - 2);
				return left;
			} else if (right.y <= endRec.y + endRec.height
					&& right.y >= endRec.y && endXCenter <= startXCenter) {
				right.setLocation(right.x - 2, right.y - 2);
				return right;
			} else if (bottom.x <= endRec.x + endRec.width && top.x >= endRec.x
					&& endYCenter <= startYCenter) {
				bottom.setLocation(bottom.x - 2, bottom.y - 2);
				return bottom;
			} else {
				top.setLocation(top.x - 2, top.y - 2);
				return top;
			}
		} else if (startRec.contains(endXCenter, endYCenter)) {
			if (left.y <= endRec.y + endRec.height && left.y >= endRec.y
					&& endXCenter <= startXCenter) {
				left.setLocation(left.x - 2, left.y - 2);
				return left;
			} else if (right.y <= endRec.y + endRec.height
					&& right.y >= endRec.y && startXCenter <= endXCenter) {
				right.setLocation(right.x - 2, right.y - 2);
				return right;
			} else if (bottom.x <= endRec.x + endRec.width && top.x >= endRec.x
					&& startYCenter <= endYCenter) {
				bottom.setLocation(bottom.x - 2, bottom.y - 2);
				return bottom;
			} else {
				top.setLocation(top.x - 2, top.y - 2);
				return top;
			}
		}
		if (left.y <= endRec.y + endRec.height && left.y >= endRec.y
				&& startXCenter <= left.x) {
			left.setLocation(left.x - 2, left.y - 2);
			return left;
		} else if (right.y <= endRec.y + endRec.height && right.y >= endRec.y
				&& startXCenter >= right.x) {
			right.setLocation(right.x - 2, right.y - 2);
			return right;
		} else if (bottom.x <= endRec.x + endRec.width && top.x >= endRec.x
				&& startYCenter >= bottom.y) {
			bottom.setLocation(bottom.x - 2, bottom.y - 2);
			return bottom;
		} else {
			top.setLocation(top.x - 2, top.y - 2);
			return top;
		}
	}

	/**
	 * Calculates the intersection point of edge and node for edges being mere
	 * lines.
	 * 
	 * @return The intersection point of starting node and edge.
	 */
	private Point2D.Double calcLineStartingCoord() {
		// calculates the lambda factor for parametric line equation
		if (this.prefs.algorithm.equals("sugi")) {
			Point2D.Double p = new Point2D.Double(endRec.x, endRec.y);
			if (p.y <= startRec.y) {
				return new Point2D.Double(
						startRec.x + 0.5 * startRec.width - 2, startRec.y - 2);
			}
			return new Point2D.Double(startRec.x + 0.5 * startRec.width - 2,
					startRec.y + startRec.height - 2);
		}

		double endXCenter = endRec.x + endRec.width * 0.5;
		double endYCenter = endRec.y + endRec.height * 0.5;
		double startXCenter = startRec.x + startRec.width * 0.5;
		double startYCenter = startRec.y + startRec.height * 0.5;

		Point2D.Double top = new Point2D.Double();
		Point2D.Double bottom = new Point2D.Double();
		Point2D.Double left = new Point2D.Double();
		Point2D.Double right = new Point2D.Double();

		double lambda = (startRec.width * 0.5)
				/ ((double) ((endRec.x + 0.5 * endRec.width) - (startRec.x + 0.5 * startRec.width)));
		// gets the x and y coordinate
		double x = lambda
				* ((endRec.x + endRec.width * 0.5) - (startRec.x + startRec.width * 0.5));
		double y = lambda
				* ((endRec.y + endRec.height * 0.5) - (startRec.y + startRec.height * 0.5));

		// checks which side of node rectangle is intersected, then paints
		// the intersection markings

		left = new Point2D.Double(
				(int) (startRec.x - x) + startRec.width * 0.5,
				(int) (startRec.y - y) + startRec.height * 0.5);

		right = new Point2D.Double((int) (startRec.x + x) + startRec.width
				* 0.5, (int) (startRec.y + y) + startRec.height * 0.5);

		lambda = (startRec.height * 0.5)
				/ ((double) ((endRec.y + 0.5 * endRec.height) - (startRec.y + 0.5 * startRec.height)));
		x = lambda
				* ((endRec.x + endRec.width * 0.5) - (startRec.x + startRec.width * 0.5));
		y = lambda
				* ((endRec.y + endRec.height * 0.5) - (startRec.y + startRec.height * 0.5));

		bottom = new Point2D.Double((int) (startRec.x + x) + startRec.width
				* 0.5, (int) (startRec.y + y) + startRec.height * 0.5);

		top = new Point2D.Double((int) (startRec.x - x) + startRec.width * 0.5,
				(int) (startRec.y - y) + startRec.height * 0.5);

		// checks which point is correct
		if (endRec.contains(startXCenter, startYCenter)) {
			if (left.y <= startRec.y + startRec.height && left.y >= startRec.y
					&& startXCenter <= endXCenter) {
				left.setLocation(left.x - 2, left.y - 2);
				return left;
			} else if (right.y <= startRec.y + startRec.height
					&& right.y >= startRec.y && endXCenter <= startXCenter) {
				right.setLocation(right.x - 2, right.y - 2);
				return right;
			} else if (bottom.x <= startRec.x + startRec.width
					&& top.x >= startRec.x && endYCenter <= startYCenter) {
				bottom.setLocation(bottom.x - 2, bottom.y - 2);
				return bottom;
			} else {
				top.setLocation(top.x - 2, top.y - 2);
				return top;
			}
		} else if (startRec.contains(endXCenter, endYCenter)) {
			if (left.y <= startRec.y + startRec.height && left.y >= startRec.y
					&& endXCenter <= startXCenter) {
				left.setLocation(left.x - 2, left.y - 2);
				return left;
			} else if (right.y <= startRec.y + startRec.height
					&& right.y >= startRec.y && startXCenter <= endXCenter) {
				right.setLocation(right.x - 2, right.y - 2);
				return right;
			} else if (bottom.x <= startRec.x + startRec.width
					&& top.x >= startRec.x && startYCenter <= endYCenter) {
				bottom.setLocation(bottom.x - 2, bottom.y - 2);
				return bottom;
			} else {
				top.setLocation(top.x - 2, top.y - 2);
				return top;
			}
		}
		if (left.y <= startRec.y + startRec.height && left.y >= startRec.y
				&& endXCenter <= left.x) {
			left.setLocation(left.x - 2, left.y - 2);
			return left;
		} else if (right.y <= startRec.y + startRec.height
				&& right.y >= startRec.y && endXCenter >= right.x) {
			right.setLocation(right.x - 2, right.y - 2);
			return right;
		} else if (bottom.x <= startRec.x + startRec.width
				&& bottom.x >= startRec.x && endYCenter >= bottom.y) {
			bottom.setLocation(bottom.x - 2, bottom.y - 2);
			return bottom;
		} else {
			top.setLocation(top.x - 2, top.y - 2);
			return top;
		}
	}

	/**
	 * Draws the small rectangles at the intersections of edges and
	 * corresponding nodes if edges are mere lines.
	 * 
	 * @return List containing connection points.
	 */
	private LinkedList drawConnectionsLine() {
		LinkedList result = new LinkedList();
		Point2D.Double start = calcLineStartingCoord();
		// similarly gets markings for second node
		Point2D.Double end = calcLineEndingCoord();
		result.add(new Point2D.Double(start.x, start.y));
		result.add(new Point2D.Double(end.x, end.y));
		return result;
	}

	/**
	 * Sets the list of control points.
	 * 
	 * @param ctrl
	 *            The new list of control points.
	 */
	public void setCtrlPoints(LinkedList ctrl) {
		controlPoints = ctrl;
		controlClone = new LinkedList(controlPoints);
	}

	/**
	 * Sets the list of path points.
	 * 
	 * @param path
	 *            The new list path points.
	 */
	public void setPathPoints(LinkedList path) {
		pathPoints = path;
	}

	/**
	 * Clones an instance of this class.
	 * 
	 * @param start
	 *            The starting point of the clone.
	 * @param end
	 *            The ending point of the clone.
	 * @param pref
	 *            The preferences to be set for clone.
	 * @return The object clone.
	 */
	public Object clone(Rectangle start, Rectangle end, Preferences pref) {
		Polyline poly = new Polyline(start, end, pref);

		// copies all instance variables
		LinkedList ctrlClone = new LinkedList();
		Iterator it = controlPoints.iterator();
		while (it.hasNext()) {
			ctrlClone
					.add((Point2D.Double) ((Point2D.Double) it.next()).clone());
		}

		LinkedList pathClone = new LinkedList();
		it = pathPoints.iterator();
		while (it.hasNext()) {
			pathClone
					.add((Point2D.Double) ((Point2D.Double) it.next()).clone());
		}

		poly.setCtrlPoints(ctrlClone);
		poly.setPathPoints(pathClone);
		return poly;
	}

	/**
	 * Translates polyline.
	 * 
	 * @param point
	 *            Point containing translation coordinates.
	 */
	public void translate(Point point) {
		Iterator it = controlPoints.iterator();
		while (it.hasNext()) {
			Point2D.Double p = (Point2D.Double) it.next();
			double x = p.x;
			double y = p.y;
			p.setLocation(new Point2D.Double(x + point.x, y + point.y));
		}
		startRec.translate(point.x, point.y);
		endRec.translate(point.x, point.y);
	}

	/**
	 * Gets an iterator for control points.
	 * 
	 * @return Control points iterator.
	 */
	public Iterator controlPointsIterator() {
		return controlPoints.iterator();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String result = "Polyline";
		result += startRec.toString();
		result += controlPoints.toString();
		result += endRec.toString();
		result += "curve: " + prefs.curveType;
		return result;
	}
}