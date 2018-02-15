package quoggles.changeboxes.changeattribute;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 *
 */
public class ChangeAttribute_Icon extends AbstractBoxIcon {

    /**
     * 
     */
    public ChangeAttribute_Icon() {
        label.setText("change attribute");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new ChangeAttribute_Box();
    }

}
