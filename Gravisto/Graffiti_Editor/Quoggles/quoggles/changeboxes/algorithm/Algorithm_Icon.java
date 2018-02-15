package quoggles.changeboxes.algorithm;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 * Icon for <code>SubQuery_Box</code>.
 */
public class Algorithm_Icon extends AbstractBoxIcon {

    public Algorithm_Icon() {
        label.setText("algorithms");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new Algorithm_Box();
    }

}
