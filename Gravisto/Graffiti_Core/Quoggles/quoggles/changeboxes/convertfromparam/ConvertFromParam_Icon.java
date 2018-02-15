package quoggles.changeboxes.convertfromparam;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 * Icon for <code>SubQuery_Box</code>.
 */
public class ConvertFromParam_Icon extends AbstractBoxIcon {

    public ConvertFromParam_Icon() {
        label.setText("convertFROMParam");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new ConvertFromParam_Box();
    }

}
