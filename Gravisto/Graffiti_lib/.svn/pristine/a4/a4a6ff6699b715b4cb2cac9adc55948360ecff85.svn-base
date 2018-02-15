package org.graffiti.plugins.algorithms.labeling;

import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.plugins.algorithms.springembedderFR.FREdge;

public class FREdgeLabelNode extends FRLabelNode {
    private FREdge correspondingFREdge;

    public FREdgeLabelNode(Node originalNode, FREdge correspondingFREdge,
            boolean movable, String label, String font, ColorAttribute textcolor) {
        super(originalNode, movable, label, font, textcolor);
        this.correspondingFREdge = correspondingFREdge;
    }

    public FREdge getCorrespondingFREdge() {
        return correspondingFREdge;
    }

}
