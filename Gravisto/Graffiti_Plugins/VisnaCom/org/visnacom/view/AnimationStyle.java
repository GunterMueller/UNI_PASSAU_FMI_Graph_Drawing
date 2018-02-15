/*
 * Created on 02.02.2005
 *
 *
 */
package org.visnacom.view;

import javax.swing.*;
import java.util.*;

/**
 * @author F. Pfeiffer
 * 
 * This abstract class provides an outline of the functionality needed for
 * animating expansion and contraction.
 */
public abstract class AnimationStyle {

	// the original geometry object which is to be changed
	protected Geometry originalGeo;

	// the geometry object where the new final data is stored
	protected Geometry temporaryGeo;

	// the panel in which animation is performed
	protected JPanel panel;

	// the timer
	protected java.util.Timer timer;

	// the animation task
	protected TimerTask task;

	// duration of animation step
	protected long period = 100;

	// flag signalling whether animation is still running
	protected boolean isRunning = false;

	/**
	 * Constructor.
	 * 
	 * @param geo
	 *            The original geometry object which is to be transformed.
	 * @param viewPanel
	 *            The panel in which animation is performed.
	 */
	public AnimationStyle(Geometry geo, JPanel viewPanel) {
		originalGeo = geo;
		panel = viewPanel;
	}

	/**
	 * Starts a new animation.
	 * 
	 * @param nGeo
	 *            Geometry object with new data.
	 */
	public void start(Geometry nGeo) {
		temporaryGeo = nGeo;
		task = new AnimationTask();
		timer = new java.util.Timer();
		timer.schedule(task, 0, period);
		isRunning = true;
	}

	/**
	 * Finishes animation.
	 *  
	 */
	protected void finish() {
		task.cancel();
		isRunning = false;

	}

	/**
	 * Makes next frame of animation
	 *  
	 */
	abstract public void nextFrame();

	/**
	 * Checks if animation is currently running.
	 * 
	 * @return True if animation is running, false otherwise.
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Clones animation. Needed for reproducing old animation type (avoids
	 * instanceof) .
	 * 
	 * @param geo
	 *            The original geometry object which is to be transformed.
	 * @param p
	 *            The panel in which animation is performed.
	 * @return The cloned object.
	 */
	abstract public Object clone(Geometry geo, JPanel p);

	/**
	 * 
	 * @author F. Pfeiffer
	 * 
	 * Internal class, extanding TimerTask.
	 */
	public class AnimationTask extends TimerTask {

		/**
		 * Paints next frame.
		 */
		public void run() {
			nextFrame();
			panel.repaint();
		}

		/**
		 * Cancels TimerTask.
		 * @return True if task is cancelled.
		 */
		public boolean cancel() {
			return super.cancel();
		}
	}

}