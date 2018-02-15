package quoggles.changeboxes.createparam;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 *
 */
public class CreateParam_Icon extends AbstractBoxIcon {

    public CreateParam_Icon() {
        label.setText("create Param");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new CreateParam_Box();
    }

}
