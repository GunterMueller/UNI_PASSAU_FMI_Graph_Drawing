package quoggles.changeboxes.changeattributes;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 *
 */
public class ChangeAttributes_Icon extends AbstractBoxIcon {

    /**
     * 
     */
    public ChangeAttributes_Icon() {
        label.setText("change attributes");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new ChangeAttributes_Box();
    }

}
