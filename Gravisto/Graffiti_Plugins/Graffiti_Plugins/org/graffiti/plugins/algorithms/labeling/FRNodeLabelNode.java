package org.graffiti.plugins.algorithms.labeling;

import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.plugins.algorithms.springembedderFR.FRNode;

public class FRNodeLabelNode extends FRLabelNode {

    private FRNode correspondingFRNode;

    public FRNodeLabelNode(Node originalNode, FRNode correspondingFRNode,
            boolean movable, String label, String font, ColorAttribute textcolor) {
        super(originalNode, movable, label, font, textcolor);
        this.correspondingFRNode = correspondingFRNode;
    }

    public FRNode getCorrespondingFRNode() {
        return correspondingFRNode;
    }

}
