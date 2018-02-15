/*
 * 
 */
package quoggles.auxiliary;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

/**
 *
 */
public class GhostBox extends JPanel {
    
    private final Dimension SIZE = new Dimension(100, 40);
    
    private final Color COLOR = new Color(255, 255, 0, 150);

    /**
     * 
     */
    public GhostBox() {
        setLayout(null);
        setSize(SIZE);
        setMinimumSize(SIZE);
        setMaximumSize(SIZE);
        setPreferredSize(SIZE);
        setBackground(COLOR);
    }

}
