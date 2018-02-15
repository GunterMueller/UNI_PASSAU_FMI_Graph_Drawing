package quoggles.deprecated.stdboxes.flatten;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 * Icon for <code>Flatten_Box</code>
 */
public class Flatten_Icon extends AbstractBoxIcon {

    public Flatten_Icon() {
        label.setText("flatten");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new Flatten_Box();
    }

}
