/*
 * 
 */
package quoggles.stdboxes.output;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 *
 */
public class NormalOutput_Icon extends AbstractBoxIcon {

    /**
     * 
     */
    public NormalOutput_Icon() {
        label.setText("output");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new NormalOutput_Box();
    }
}
