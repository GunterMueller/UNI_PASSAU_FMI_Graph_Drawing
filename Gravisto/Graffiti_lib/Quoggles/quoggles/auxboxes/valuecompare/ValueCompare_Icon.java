package quoggles.auxboxes.valuecompare;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 *
 */
public class ValueCompare_Icon extends AbstractBoxIcon {

    /**
     * 
     */
    public ValueCompare_Icon() {
        label.setText("value compare");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new ValueCompare_Box();
    }

}
