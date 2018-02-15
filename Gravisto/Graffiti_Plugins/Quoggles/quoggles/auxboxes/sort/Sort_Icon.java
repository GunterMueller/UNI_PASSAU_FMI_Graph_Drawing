package quoggles.auxboxes.sort;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 * Icon for <code>Sort_Box</code>
 */
public class Sort_Icon extends AbstractBoxIcon {

    public Sort_Icon() {
        label.setText("sort");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new Sort_Box();
    }

}
