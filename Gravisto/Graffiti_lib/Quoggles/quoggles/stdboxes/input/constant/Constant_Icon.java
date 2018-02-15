package quoggles.stdboxes.input.constant;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 *
 */
public class Constant_Icon extends AbstractBoxIcon {

    public Constant_Icon() {
        label.setText("constant");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new Constant_Box();
    }

}
