package quoggles.auxboxes.contains;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 *
 */
public class Contains_Icon extends AbstractBoxIcon {

    public Contains_Icon() {
        label.setText("contains");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new Contains_Box();
    }

}
