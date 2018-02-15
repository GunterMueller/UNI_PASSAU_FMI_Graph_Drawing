package quoggles.auxboxes.truemaker;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 *
 */
public class MakeTrue_Icon extends AbstractBoxIcon {

    public MakeTrue_Icon() {
        label.setText("make true");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new MakeTrue_Box();
    }

}
