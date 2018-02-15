package quoggles.deprecated.stdboxes.unpack;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 * Icon for <code>Unpack_Box</code>
 */
public class Unpack_Icon extends AbstractBoxIcon {

    public Unpack_Icon() {
        label.setText("unpack");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new Unpack_Box();
    }

}
