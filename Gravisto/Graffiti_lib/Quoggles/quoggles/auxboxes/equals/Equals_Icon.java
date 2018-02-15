package quoggles.auxboxes.equals;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 *
 */
public class Equals_Icon extends AbstractBoxIcon {

    public Equals_Icon() {
        label.setText("equals");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new Equals_Box();
    }

}
