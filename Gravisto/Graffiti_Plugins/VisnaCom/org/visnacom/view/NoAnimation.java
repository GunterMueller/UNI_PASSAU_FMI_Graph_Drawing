/*
 * Created on 02.02.2005
 *
 *
 */

package org.visnacom.view;

import javax.swing.*;

import org.visnacom.model.*;

import java.util.*;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * @author F. Pfeiffer
 * 
 * This class implements an animation performing only one animation frame.
 */

public class NoAnimation extends AnimationStyle {

	// only one frame per animation
	static final int numberOfFrames = 1;

	// current number of frames
	static int currentFrameNumber = 0;

	/**
	 * Constructor.
	 * 
	 * @see org.visnacom.view.AnimationStyle#AnimationStyle(org.visnacom.view.Geometry,
	 *      CPG.Controller.ViewPanel)
	 */
	public NoAnimation(Geometry geo, JPanel panel) {
		super(geo, panel);
	}

	/**
	 * @see org.visnacom.view.AnimationStyle#start(org.visnacom.view.Geometry)
	 */
	public void start(Geometry nGeo) {
		temporaryGeo = nGeo;
		isRunning = true;
		nextFrame();
	}

	/**
	 * @see org.visnacom.view.AnimationStyle#nextFrame()
	 */
	public void nextFrame() {

		// sets new node parameters
		Iterator it;
		it = this.originalGeo.getView().getAllNodesIterator();
		while (it.hasNext()) {
			Node next = (Node) it.next();
			Rectangle value = originalGeo.shape(next);
			Rectangle newShape = temporaryGeo.shape(next);
			value.setLocation(newShape.getLocation());
			value.setSize(newShape.getSize());
		}

		// sets new edge parameters
		it = this.originalGeo.getView().getAllEdgesIterator();
		while (it.hasNext()) {
			Edge key = (Edge) it.next();
			Polyline value = originalGeo.shape(key);
			Polyline newShape = temporaryGeo.shape(key);
			value.getStart().setLocation(newShape.getStart().getLocation());
			value.getEnd().setLocation(newShape.getEnd().getLocation());

			value.getStart().setSize(newShape.getStart().getSize());
			value.getEnd().setSize(newShape.getEnd().getSize());

			value.clearControlPoints();
			Iterator ctrl = newShape.controlPointsIterator();
			while (ctrl.hasNext()) {
				value.addControl((Point2D) ctrl.next());
			}
		}

		currentFrameNumber++;

		// animation finished after one frame
		if (currentFrameNumber >= numberOfFrames) {
			finish();
			currentFrameNumber = 0;
		}
	}

	/**
	 * @see org.visnacom.view.AnimationStyle#finish()
	 */
	protected void finish() {
		isRunning = false;
	}

	/**
	 * @see org.visnacom.view.AnimationStyle#clone(org.visnacom.view.Geometry,
	 *      CPG.Controller.ViewPanel)
	 */
	public Object clone(Geometry geo, JPanel p) {
		AnimationStyle cloned = new NoAnimation(geo, p);
		return cloned;
	}

}