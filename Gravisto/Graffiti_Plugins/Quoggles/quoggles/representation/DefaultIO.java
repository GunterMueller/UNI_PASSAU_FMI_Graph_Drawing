package quoggles.representation;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import quoggles.constants.IBoxConstants;

/**
 * A small panel representing a standardized input / output connection. 
 */
public class DefaultIO extends JPanel {

    /**
     * Constructs a small horizontal standardized input / output connection.
     */
    public DefaultIO() {
        Dimension size =
            new Dimension(IBoxConstants.IO_LENGTH, IBoxConstants.IO_THICKNESS);
        setSize(size);
        setMaximumSize(size);
        setPreferredSize(size);
        setBackground(IBoxConstants.IO_FILL_COLOR);
        setBorder(
            new LineBorder(
                IBoxConstants.IO_BORDER_COLOR,
                IBoxConstants.IO_BORDER_WIDTH));
    }

    /**
     * Constructs a small standardized input / output connection. The parameter
     * specifies whether it is displayed vertically or horizontally.
     */
    public DefaultIO(boolean vertical) {
        Dimension size = null;
        if (vertical) {
            size =
                new Dimension(
                    IBoxConstants.IO_THICKNESS,
                    IBoxConstants.IO_LENGTH);
        } else {
            size =
                new Dimension(
                    IBoxConstants.IO_LENGTH,
                    IBoxConstants.IO_THICKNESS);
        }
        setSize(size);
        setMaximumSize(size);
        setPreferredSize(size);
        setBackground(IBoxConstants.IO_FILL_COLOR);
        setBorder(
            new LineBorder(
                IBoxConstants.IO_BORDER_COLOR,
                IBoxConstants.IO_BORDER_WIDTH));
    }

    /**
     * Constructs a small standardized input / output connection. The first 
     * parameter specifies whether it is displayed vertically or horizontally.
     * The second whether or not it should be especially long.
     */
    public DefaultIO(boolean vertical, boolean extraLong) {
        Dimension size = null;
        int length =
            extraLong ? IBoxConstants.IO_LONG_LENGTH : IBoxConstants.IO_LENGTH;

        if (vertical) {
            size = new Dimension(IBoxConstants.IO_THICKNESS, length);
        } else {
            size = new Dimension(length, IBoxConstants.IO_THICKNESS);
        }
        setSize(size);
        setMaximumSize(size);
        setPreferredSize(size);
        setBackground(IBoxConstants.IO_FILL_COLOR);
        setBorder(new LineBorder(
            IBoxConstants.IO_BORDER_COLOR,
            IBoxConstants.IO_BORDER_WIDTH));
    }

}
