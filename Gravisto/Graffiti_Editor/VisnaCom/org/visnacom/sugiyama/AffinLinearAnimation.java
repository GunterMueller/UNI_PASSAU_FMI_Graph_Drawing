/*==============================================================================
*
*   AffinLinearAnimation.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: AffinLinearAnimation.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JPanel;

import org.visnacom.model.Edge;
import org.visnacom.model.Node;
import org.visnacom.view.*;


/**
 * implements a affin linear animation.
 */
public class AffinLinearAnimation extends AnimationStyle {
    //~ Static fields/initializers =============================================

    private static final int minNumberOfFrames = 1;

    //~ Instance fields ========================================================

    /**
     * if true, the timer functionality of the superclass is not used. Instead
     * the method nextFrame must be called manually. for testing purposes only
     */
    public boolean stepMode;
    private int currentFrame;

    /* in the latest specification the width of a leaf */
    private int maxDelta;
    private int totalNumOfFrames;

    //~ Constructors ===========================================================

    /**
     * Creates a new AffinLinearAnimation object.
     *
     * @param geo DOCUMENT ME!
     * @param viewPanel DOCUMENT ME!
     */
    public AffinLinearAnimation(Geometry geo, JPanel viewPanel) {
        super(geo, viewPanel);
        maxDelta = geo.getPrefs().leafWidth;
        stepMode = false;
    }

    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param geo DOCUMENT ME!
     * @param p DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object clone(Geometry geo, JPanel p) {
        return new AffinLinearAnimation(geo, p);
    }

    /**
     * @see org.visnacom.view.AnimationStyle#nextFrame()
     */
    public void nextFrame() {
        //this will be set to false, if any animated point needs moving.
        Flag finished = new Flag(true);

        //nodes
        for(Iterator it = originalGeo.getView().getAllNodesIterator();
            it.hasNext();) {
            Node key = (Node) it.next();
            Rectangle origRect = originalGeo.shape(key);
            Rectangle targetValues = temporaryGeo.shape(key);

            adjust(origRect, targetValues, finished);
        }

        //edges
        for(Iterator it = originalGeo.getView().getAllEdgesIterator();
            it.hasNext();) {
            Edge edge = (Edge) it.next();
            Polyline origPoly = originalGeo.shape(edge);
            Polyline targetPoly = temporaryGeo.shape(edge);

            assert origPoly.getControlPoints().size() == targetPoly.getControlPoints()
                                                                   .size();

            Iterator origIt = origPoly.controlPointsIterator();
            Iterator targetIt = targetPoly.controlPointsIterator();
            while(origIt.hasNext() && targetIt.hasNext()) {
                Point2D origP = (Point2D) origIt.next();
                Point2D targetP = (Point2D) targetIt.next();
                double x =
                    nextAdjust((int) origP.getX(), (int) targetP.getX(),
                        finished);
                double y =
                    nextAdjust((int) origP.getY(), (int) targetP.getY(),
                        finished);
                origP.setLocation(x, y);
            }
        }

        if(finished.get()) {
            finish();
        }

        currentFrame++;
    }

    /**
     * calculates the total number of frames.
     *
     * @see org.visnacom.view.AnimationStyle#start(org.visnacom.view.Geometry)
     */
    public void start(Geometry nGeo) {
        currentFrame = 1;
        if(stepMode) {
            temporaryGeo = nGeo;
            isRunning = true;
        }

        assert testConsistence(originalGeo, nGeo);

        /* determine the maximum movement of a node */
        int foundMaxDeltaX = 0;
        int foundMaxDeltaY = 0;
        int foundMaxDeltaW = 0;
        int foundMaxDeltaH = 0;

        for(Iterator it = originalGeo.getView().getAllNodesIterator();
            it.hasNext();) {
            Node key = (Node) it.next();
            Rectangle origRect = originalGeo.shape(key);
            Rectangle targetValues = nGeo.shape(key);

            foundMaxDeltaX =
                Math.max(foundMaxDeltaX, Math.abs(origRect.x - targetValues.x));
            foundMaxDeltaY =
                Math.max(foundMaxDeltaY, Math.abs(origRect.y - targetValues.y));
            foundMaxDeltaW =
                Math.max(foundMaxDeltaW,
                    Math.abs(origRect.width - targetValues.width));
            foundMaxDeltaH =
                Math.max(foundMaxDeltaH,
                    Math.abs(origRect.height - targetValues.height));
        }

        totalNumOfFrames = minNumberOfFrames;
        totalNumOfFrames =
            Math.max(totalNumOfFrames,
                (int) Math.ceil((double) foundMaxDeltaX / (double) maxDelta));
        totalNumOfFrames =
            Math.max(totalNumOfFrames,
                (int) Math.ceil((double) foundMaxDeltaY / (double) maxDelta));
        totalNumOfFrames =
            Math.max(totalNumOfFrames,
                (int) Math.ceil((double) foundMaxDeltaW / (double) maxDelta));
        totalNumOfFrames =
            Math.max(totalNumOfFrames,
                (int) Math.ceil((double) foundMaxDeltaH / (double) maxDelta));

        if(!stepMode) {
            super.start(nGeo);
        }
    }

    /**
     * stepMode is for testing only. this method usually calls just
     * super.finish
     *
     * @see org.visnacom.view.AnimationStyle#finish()
     */
    protected void finish() {
        if(!stepMode) {
            super.finish();
        } else {
            isRunning = false;
        }
    }

    /**
     * adjusts the coordinates of a rectangle
     *
     * @param origRect the rectangle to move
     * @param targetValues the target coordinates
     * @param finished the flag to set to false, if the coordinates change.
     */
    private void adjust(Rectangle origRect, Rectangle targetValues,
        Flag finished) {
        origRect.x = nextAdjust(origRect.x, targetValues.x, finished);

        origRect.y = nextAdjust(origRect.y, targetValues.y, finished);

        origRect.width =
            nextAdjust(origRect.width, targetValues.width, finished);
        origRect.height =
            nextAdjust(origRect.height, targetValues.height, finished);
    }

    /**
     * DOCUMENT ME!
     *
     * @param originalGeo DOCUMENT ME!
     * @param tempGeo DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static boolean testConsistence(Geometry originalGeo,
        Geometry tempGeo) {
        assert originalGeo.getNodes().keySet().containsAll(tempGeo.getNodes()
                                                                  .keySet());
        assert tempGeo.getNodes().keySet().containsAll(originalGeo.getNodes()
                                                                  .keySet());
        assert originalGeo.getNodes().size() == tempGeo.getNodes().size();

        assert originalGeo.getEdges().keySet().containsAll(tempGeo.getEdges()
                                                                  .keySet());
        assert tempGeo.getEdges().keySet().containsAll(originalGeo.getEdges()
                                                                  .keySet());
        assert originalGeo.getEdges().size() == tempGeo.getEdges().size();

        for(Iterator it = originalGeo.getEdges().entrySet().iterator();
            it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            Edge edge = (Edge) entry.getKey();
            Polyline origPoly = (Polyline) entry.getValue();
            Polyline targetPoly = tempGeo.shape(edge);
            assert origPoly.getControlPoints().size() == targetPoly.getControlPoints()
                                                                   .size();
            for(Iterator it2 = origPoly.getControlPoints().iterator();
                it2.hasNext();) {
                assert !((Point2D) it2.next()).equals(new Point2D.Double(0, 0));
            }
        }

        return true;
    }

    /**
     * calculates a single coordinate for the next frame. The distance between
     * start and target has been divided in <code>totalNumOfFrames</code>
     * frames. As this formula does not use the start point, but the current
     * point, the divisor gets smaller with increasing current frame.
     *
     * @param oldValue the position so far
     * @param targetValue the target position
     * @param reachedTarget a flag, that will be set to false if the
     *        targetValue could not be reached. It will NOT be set to true in
     *        the opposite case.
     *
     * @return the new coordinate value
     */
    private int nextAdjust(int oldValue, int targetValue, Flag reachedTarget) {
        if(oldValue == targetValue) {
            return oldValue;
        }

        double nextDelta =
            Math.abs((double) targetValue - (double) oldValue) / (totalNumOfFrames
            - (currentFrame - 1));

        if(oldValue < targetValue - nextDelta) {
            reachedTarget.reset();
            return oldValue + (int) Math.floor(nextDelta);
        } else if(oldValue > targetValue + nextDelta) {
            reachedTarget.reset();
            return oldValue - (int) Math.floor(nextDelta);
        } else {
            return targetValue;
        }
    }

    //~ Inner Classes ==========================================================

    /**
     *
     */
    private static class Flag {
        private boolean value;

        /**
         * Creates a new Flag object.
         *
         * @param value the initial value
         */
        public Flag(boolean value) {
            this.value = value;
        }

        /**
         * Returns the value.
         *
         * @return the value of the flag
         */
        public boolean get() {
            return value;
        }

        /**
         * sets the flag to false
         */
        public void reset() {
            value = false;
        }

        /**
         * sets the flag to true
         */
        public void set() {
            value = true;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return value + "";
        }
    }
}
