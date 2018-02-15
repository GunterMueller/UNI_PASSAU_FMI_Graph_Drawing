/*
 * 
 */
package quoggles.stdboxes.getattributevalue;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 * 
 */
public class GetAttributeValue_Icon extends AbstractBoxIcon {

    /**
     * 
     */
    public GetAttributeValue_Icon() {
        label.setText("get attr. value");
        adjustSize();
    }

    /**
     * @see quoggles.representation.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new GetAttributeValue_Box();
    }

}
