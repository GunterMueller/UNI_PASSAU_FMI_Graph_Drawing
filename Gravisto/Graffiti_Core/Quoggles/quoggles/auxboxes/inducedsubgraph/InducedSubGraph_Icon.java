/*
 * Created on 13.08.2003
 *
 */
package quoggles.auxboxes.inducedsubgraph;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 *
 */
public class InducedSubGraph_Icon extends AbstractBoxIcon {

    public InducedSubGraph_Icon() {
        label.setText("subgraph");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new InducedSubGraph_Box();
    }

}
