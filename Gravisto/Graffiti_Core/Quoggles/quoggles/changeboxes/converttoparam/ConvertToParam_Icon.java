package quoggles.changeboxes.converttoparam;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 * Icon for <code>SubQuery_Box</code>.
 */
public class ConvertToParam_Icon extends AbstractBoxIcon {

    public ConvertToParam_Icon() {
        label.setText("convertTOParam");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new ConvertToParam_Box();
    }

}
