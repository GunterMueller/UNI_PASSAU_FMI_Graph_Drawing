// =============================================================================
//
//   DummyCrossMin.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DummyCrossMin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.crossmin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class is a dummy-implementation. This class checks, if every
 * <tt>Node</tt> in the <tt>Graph</tt> has an <tt>IntegerAttribute</tt>
 * sugiyama.xpos, that represents and order of the <tt>Node</tt>s on one level.<br>
 * On one level, each <tt>Node</tt> must have a unique x-pos, and the x-pos must
 * not be negative.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class DummyCrossMin extends AbstractAlgorithm implements
        CrossMinAlgorithm {
    private final String ALGORITHM_NAME = "Dummy-CrossMin (check for the attribute xpos)";
    private SugiyamaData data;

    public void setData(SugiyamaData data) {
        this.data = data;
    }

    public SugiyamaData getData() {
        return this.data;
    }

    public String getName() {
        return this.ALGORITHM_NAME;
    }

    public void execute() {
        boolean error = false;
        boolean missing = false;
        boolean negative = false;
        boolean same = false;
        HashSet<String> messages = new HashSet<String>();
        int[] currentLevel;
        Node current;

        NodeLayers layers = data.getLayers();
        for (int i = 0; i < layers.getNumberOfLayers(); i++) {
            currentLevel = new int[layers.getLayer(i).size()];
            for (int j = 0; j < layers.getLayer(i).size(); j++) {
                current = layers.getLayer(i).get(j);
                try {
                    currentLevel[j] = (int) current
                            .getDouble(SugiyamaConstants.PATH_XPOS);
                } catch (AttributeNotFoundException anfe) {
                    if (!missing) {
                        messages
                                .add("- At least one node is missing the attribute "
                                        + SugiyamaConstants.PATH_XPOS + ".");
                        error = true;
                        missing = true;
                    }
                }
            }
            Arrays.sort(currentLevel);
            for (int j = 0; j < currentLevel.length; j++) {
                if (currentLevel[j] < 0 && !negative) {
                    negative = true;
                    messages.add("- The attribute "
                            + SugiyamaConstants.PATH_XPOS
                            + " must not be negative.");
                    error = true;
                } else {
                    if (j != 0) {
                        if (currentLevel[j] == currentLevel[j - 1] && !same) {
                            same = true;
                            messages.add("- At least two nodes on on level "
                                    + "have the same x-pos.");
                            error = true;
                        }
                    } else if (j != currentLevel.length - 1) {
                        if (currentLevel[j] == currentLevel[j + 1] && !same) {
                            same = true;
                            messages.add("- At least two nodes on one level "
                                    + "have the same x-pos.");
                            error = true;
                        }
                    }
                }
            }
        }
        if (error) {
            String message = "Cannot proceed due to the following errors:\n\n";
            Iterator<String> iter = messages.iterator();
            while (iter.hasNext()) {
                message += iter.next() + "\n";
            }
            throw new RuntimeException(message);
        }

    }

    public boolean supportsBigNodes() {
        return false;
    }

    public boolean supportsConstraints() {
        return false;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)
                || algorithmType
                        .equals(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
