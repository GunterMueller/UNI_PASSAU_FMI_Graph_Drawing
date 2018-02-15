package org.graffiti.plugins.algorithms.core;

import java.awt.geom.Point2D;

/**
 * Class for needed parameters
 * 
 * @author Matthias H�llm�ller
 * 
 */
public class Params {

    /**
     * the quality of the approximation of the spiral edge
     */
    private static int quality = 500;

    /**
     * the maximum level number
     */
    private static int maxLevel = 9;

    /**
     * the distance between two levels
     */
    private static double levelDist = 1;

    /**
     * the minimum radius
     */
    private static double minRadius = 1;

    /**
     * direction of core-leveling - highest level in center or not
     */
    private static boolean direction;

    /**
     * just color the nodes?
     */
    private static boolean color;

    /**
     * is it the core algorithm?
     */
    private static boolean core;

    /**
     * the center point
     */
    private static Point2D center;

    /**
     * the constructor
     */
    public Params() {
        super();
    }

    /**
     * get the maximum level number
     * 
     * @return the maximum level number
     */
    public int getMaxLevel() {
        return Params.maxLevel;
    }

    /**
     * set the maximum level
     * 
     * @param maxLevel
     */
    public void setMaxLevel(int maxLevel) {
        Params.maxLevel = maxLevel;
    }

    /**
     * get the quality parameter
     * 
     * @return the quality parameter
     */
    public int getQuality() {
        return Params.quality;
    }

    /**
     * set the quality
     * 
     * @param quality
     */
    public void setQuality(int quality) {
        Params.quality = quality;
    }

    /**
     * get the level distance
     * 
     * @return the level distance
     */
    public double getLevelDist() {
        return Params.levelDist;
    }

    /**
     * set the level distance
     * 
     * @param levelDist
     */
    public void setLevelDist(double levelDist) {
        Params.levelDist = levelDist;
    }

    /**
     * get the minimum radius
     * 
     * @return the minimum radius
     */
    public double getMinRadius() {
        return Params.minRadius;
    }

    /**
     * set the minimum radius
     * 
     * @param minRadius
     */
    public void setMinRadius(double minRadius) {
        Params.minRadius = minRadius;
    }

    /**
     * get the direction parameter
     * 
     * @return the direction parameter
     */
    public boolean getDirection() {
        return Params.direction;
    }

    /**
     * set the direction
     * 
     * @param direction
     */
    public void setDirection(boolean direction) {
        Params.direction = direction;
    }

    /**
     * get the color parameter
     * 
     * @return the color parameter
     */
    public boolean getColor() {
        return Params.color;
    }

    /**
     * set the color parameter
     * 
     * @param color
     */
    public void setColor(boolean color) {
        Params.color = color;
    }

    /**
     * get the core parameter
     * 
     * @return the core parameter
     */
    public boolean getCore() {
        return Params.core;
    }

    /**
     * set the core parameter
     * 
     * @param core
     */
    public void setCore(boolean core) {
        Params.core = core;
    }

    /**
     * get the center point
     * 
     * @return the center point
     */
    public Point2D getCenter() {
        return center;
    }

    /**
     * set the center point
     * 
     * @param center
     */
    public void setCenter(Point2D center) {
        Params.center = center;
    }

    /**
     * set default values
     */
    public void setDefault() {
        quality = 500;
        maxLevel = 9;
        levelDist = 1;
        minRadius = 1;
        direction = true;
        color = false;
    }

}
