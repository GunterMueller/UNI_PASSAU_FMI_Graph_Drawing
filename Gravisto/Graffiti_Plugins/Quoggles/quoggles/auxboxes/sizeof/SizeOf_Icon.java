/*
 * Created on 13.08.2003
 *
 */
package quoggles.auxboxes.sizeof;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 *
 */
public class SizeOf_Icon extends AbstractBoxIcon {

    public SizeOf_Icon() {
        label.setText("size of");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new SizeOf_Box();
    }

}
