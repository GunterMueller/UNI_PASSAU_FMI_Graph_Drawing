/*
 * Created on 31.12.2004
 *
 */
package org.visnacom.controller;

import java.awt.Color;
import java.io.*;


/**
 * @author F. Pfeiffer
 *
 * This class contains the preferences as static variables.
 * In order to save chosen preferences this object is serialized.
 */
public class Preferences implements Serializable{
	
	// the chosen colors
	public Color nFill; 
	public Color nFrame;
	public Color lCol;
	public Color clusFill;
	public Color clusFrame;
	public Color select;
	public Color selected;
	public Color noExpansion;
	public Color noContraction;
	
	// chosen editing specs
	public String LAF;
	public String algorithm;
	public String animation;
	public String edgeType;
	public String curveType;
	public int scrollSpeed;
	
	// dimensions of nodes
	public int leafWidth = 26;
	public int leafHeight = 26;
	
	// distance from border of cluster to children
	public int clusOffset = 20;
	
	/**
	 * Constructor.
	 *
	 */
	public Preferences() {
		
	}
	
	/**
	 * Copy Constructor.
	 * @param pref To be copied.
	 */
	public Preferences(Preferences pref) {
		this.nFill = pref.nFill; 
		this.nFrame = pref.nFrame;
		this.lCol = pref.lCol;
		this.clusFill = pref.clusFill;
		this.clusFrame = pref.clusFrame;
		this.select = pref.select;
		this.selected = pref.selected;
		this.noExpansion = pref.noExpansion;
		this.noContraction = pref.noContraction;
		
		
		this.LAF = pref.LAF;
		this.algorithm = pref.algorithm;
		this.animation = pref.animation;
		this.edgeType = pref.edgeType;
		this.curveType = pref.curveType;
		this.scrollSpeed = pref.scrollSpeed;
	}
	
	/**
	 * Sets all preferences.
	 * @param pref The preferences to be set for this object.
	 */
	public void setPrefs(Preferences pref) {
		this.nFill = pref.nFill; 
		this.nFrame = pref.nFrame;
		this.lCol = pref.lCol;
		this.clusFill = pref.clusFill;
		this.clusFrame = pref.clusFrame;
		this.select = pref.select;
		this.selected = pref.selected;
		this.noExpansion = pref.noExpansion;
		this.noContraction = pref.noContraction;
		
		
		this.LAF = pref.LAF;
		this.algorithm = pref.algorithm;
		this.animation = pref.animation;
		this.edgeType = pref.edgeType;
		this.curveType = pref.curveType;
		this.scrollSpeed = pref.scrollSpeed;
	}

}
