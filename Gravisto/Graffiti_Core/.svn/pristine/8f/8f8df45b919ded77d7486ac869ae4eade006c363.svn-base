// =============================================================================
//
//   CliqueSet.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

/**
 * This class is used to store cliques.
 * 
 * @author struckmeier
 */
public class CliqueSet {

    private int cliqueCount = 0;

    private CliqueSet pre;

    private CliqueSet post;

    private LexBFSClique first;

    private LexBFSClique last;

    private Boolean isInterval = true;

    /**
     * Sets the pre.
     * 
     * @param pre
     *            the pre to set.
     */
    public void setPre(CliqueSet pre) {
        this.pre = pre;
    }

    /**
     * Returns the pre.
     * 
     * @return the pre.
     */
    public CliqueSet getPre() {
        return pre;
    }

    /**
     * Sets the post.
     * 
     * @param post
     *            the post to set.
     */
    public void setPost(CliqueSet post) {
        this.post = post;
    }

    /**
     * Returns the post.
     * 
     * @return the post.
     */
    public CliqueSet getPost() {
        return post;
    }

    /**
     * Sets the first.
     * 
     * @param first
     *            the first to set.
     */
    public void setFirst(LexBFSClique first) {
        this.first = first;
    }

    /**
     * Returns the first.
     * 
     * @return the first.
     */
    public LexBFSClique getFirst() {
        return first;
    }

    /**
     * Sets the last.
     * 
     * @param last
     *            the last to set.
     */
    public void setLast(LexBFSClique last) {
        this.last = last;
    }

    /**
     * Returns the last.
     * 
     * @return the last.
     */
    public LexBFSClique getLast() {
        return last;
    }

    /**
     * Sets the isInterval.
     * 
     * @param isInterval
     *            the isInterval to set.
     */
    public void setIsInterval(Boolean isInterval) {
        this.isInterval = isInterval;
    }

    /**
     * Returns the isInterval.
     * 
     * @return the isInterval.
     */
    public Boolean IsInterval() {
        return isInterval;
    }

    public void increaseCliqueCount() {
        cliqueCount++;
    }

    /**
     * Sets the cliqueCount.
     * 
     * @param cliqueCount
     *            the cliqueCount to set.
     */
    public void setCliqueCount(int cliqueCount) {
        this.cliqueCount = cliqueCount;
    }

    /**
     * Returns the cliqueCount.
     * 
     * @return the cliqueCount.
     */
    public int getCliqueCount() {
        return cliqueCount;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
