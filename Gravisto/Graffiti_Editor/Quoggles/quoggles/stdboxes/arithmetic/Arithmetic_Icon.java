package quoggles.stdboxes.arithmetic;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 * 
 */
public class Arithmetic_Icon extends AbstractBoxIcon {

    /**
     * 
     */
    public Arithmetic_Icon() {
        label.setText("arithmetic");
        adjustSize();
    }

    /**
     * @see quoggles.representation.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new Arithmetic_Box();
    }

}
