package quoggles.auxboxes.interpretasboolean;

import quoggles.boxes.IBox;
import quoggles.icons.AbstractBoxIcon;

/**
 *
 */
public class InterpretAsBoolean_Icon extends AbstractBoxIcon {

    public InterpretAsBoolean_Icon() {
        label.setText("as bool");
        adjustSize();
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new InterpretAsBoolean_Box();
    }

}
