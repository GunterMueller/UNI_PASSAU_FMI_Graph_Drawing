/*
 * 
 */
package quoggles.stdboxes.isinstanceof;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 * 
 */
public class IsInstanceof_Icon extends AbstractBoxIcon {

    /**
     * 
     */
    public IsInstanceof_Icon() {
        label.setText("instanceof");
        adjustSize();
    }

    /**
     * @see quoggles.representation.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new IsInstanceof_Box();
    }

}
