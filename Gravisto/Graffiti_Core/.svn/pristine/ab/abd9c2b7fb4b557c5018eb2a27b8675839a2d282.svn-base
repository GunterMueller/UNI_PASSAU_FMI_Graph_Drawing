// =============================================================================
//
//   OpenGLConfiguration.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.util.prefs.Preferences;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLConfiguration {
    private static OpenGLConfiguration singleton;
    private static final String PREF_AA_POINTS = "aaPoints";
    private static final String PREF_AA_LINES = "aaLines";
    private static final String PREF_AA_POLY = "aaPolygons";
    private static final String PREF_SAMPLE_BUFFERS = "sampleBuffers";
    private static final String PREF_NODDRAW = "noddraw";

    public static OpenGLConfiguration get() {
        if (singleton == null) {
            singleton = new OpenGLConfiguration();
        }
        return singleton;
    }

    private Preferences preferences;
    private boolean isAntialiasingPoints;
    private boolean isAntialiasingLines;
    private boolean isAntialiasingPolygons;
    private int sampleBuffers;
    private boolean isNoDirectDraw;

    private OpenGLConfiguration() {
        preferences = OpenGLPlugin.getPreferences();
        isAntialiasingPoints = preferences.getBoolean(PREF_AA_POINTS, false);
        isAntialiasingLines = preferences.getBoolean(PREF_AA_LINES, false);
        isAntialiasingPolygons = preferences.getBoolean(PREF_AA_POLY, false);
        sampleBuffers = preferences.getInt(PREF_SAMPLE_BUFFERS, 0);
        isNoDirectDraw = preferences.getBoolean(PREF_NODDRAW, false);
    }

    public boolean isAntialiasingPoints() {
        return isAntialiasingPoints;
    }

    public boolean isAntialiasingLines() {
        return isAntialiasingLines;
    }

    public boolean isAntialiasingPolygons() {
        return isAntialiasingPolygons;
    }

    public boolean isAntialiasing() {
        return isAntialiasingPoints | isAntialiasingLines
                | isAntialiasingPolygons;
    }

    public int getSampleBuffers() {
        return sampleBuffers;
    }

    public boolean isNoDirectDraw() {
        return isNoDirectDraw;
    }

    public void setAntialiasingPoints(boolean isAntialiasing) {
        isAntialiasingPoints = isAntialiasing;
        preferences.putBoolean(PREF_AA_POINTS, isAntialiasing);
    }

    public void setAntialiasingLines(boolean isAntialiasing) {
        isAntialiasingLines = isAntialiasing;
        preferences.putBoolean(PREF_AA_LINES, isAntialiasing);
    }

    public void setAntialiasingPolygons(boolean isAntialiasing) {
        isAntialiasingPolygons = isAntialiasing;
        preferences.putBoolean(PREF_AA_POLY, isAntialiasing);
    }

    public void setSampleBuffers(int sampleBuffers) {
        this.sampleBuffers = sampleBuffers;
        preferences.putInt(PREF_SAMPLE_BUFFERS, sampleBuffers);
    }

    public void setNoDirectDraw(boolean isNoDirectDraw) {
        this.isNoDirectDraw = isNoDirectDraw;
        preferences.putBoolean(PREF_NODDRAW, isNoDirectDraw);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
