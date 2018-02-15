package quoggles.icons;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;

/**
 * Abstract super class of icons.<p>
 * Subclass this to create a customized icon. Each box should do this and
 * at least assign a sensible text to the protected <code>label</code> field.
 */
public abstract class AbstractBoxIcon 
    extends JPanel implements IBoxIcon {

    /** Standard implementation shows this label centered within the icon */
    protected JLabel label = new JLabel();
    
    private JPanel inPanel = new JPanel();
    
    private JPanel outPanel = new JPanel();
    
    
    /**
     * Constructs the standard icon.<p>
     * Calls <code>createIcon</code>. If you create a customized icon, override
     * the method createIcon.
     * 
     * @see createIcon() 
     */
    public AbstractBoxIcon() {
        createIcon();
        
        validate();
    }

    /**
     * This method defines how the icon looks like.<p>
     * If it is overridden, a call to addIO should be considered.
     * 
     * @see addIO()
     */
    protected void createIcon() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        JPanel panel = new JPanel();
        
        Dimension size = new Dimension(IBoxConstants.DEFAULT_ICON_WIDTH, 
            IBoxConstants.DEFAULT_ICON_HEIGHT);
        panel.setSize(size);
        panel.setPreferredSize(size);
        
        panel.setBackground(IBoxConstants.ICON_BACKGROUND);

        panel.setLayout(new BorderLayout());
        panel.setBorder(new LineBorder(Color.BLACK, 2, true));

        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        add(addIO(panel), BorderLayout.CENTER);
        
        setSize(panel.getPreferredSize());
        setPreferredSize(panel.getPreferredSize());
    }
    
    /**
     * Adds small IO components showing the number of inputs / outputs the box
     * created via this icon will have.
     * 
     * @param comp the component around which the IOs should be built
     * 
     * @return the new component
     */
    protected JPanel addIO(JPanel comp) {
        IBox box = getNewBoxInstance();
        int inNr = box.getNumberOfInputs();
        int outNr = box.getNumberOfOutputs();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        
        inPanel = new JPanel();
        inPanel.setLayout(new BoxLayout(inPanel, BoxLayout.Y_AXIS));
        inPanel.setOpaque(false);
        inPanel.add(Box.createVerticalGlue());
        for (int i = 0; i < inNr - 1; i++) {
            inPanel.add(new SmallIO());
            inPanel.add(Box.createVerticalGlue());
        }
        if (inNr >= 1) {
            inPanel.add(new SmallIO());
        }
        inPanel.add(Box.createVerticalGlue());
        
        outPanel = new JPanel();
        outPanel.setLayout(new BoxLayout(outPanel, BoxLayout.Y_AXIS));
        outPanel.setOpaque(false);
        outPanel.add(Box.createVerticalGlue());
        for (int i = 0; i < outNr - 1; i++) {
            outPanel.add(new SmallIO());
            outPanel.add(Box.createVerticalGlue());
        }
        if (outNr >= 1) {
            outPanel.add(new SmallIO());
        }
        outPanel.add(Box.createVerticalGlue());
        
        Dimension size = new Dimension(inPanel.getPreferredSize().width,
            comp.getPreferredSize().height);
        inPanel.setSize(size);
        inPanel.setPreferredSize(size);
        size = new Dimension(outPanel.getPreferredSize().width,
            comp.getPreferredSize().height);
        outPanel.setSize(size);
        outPanel.setPreferredSize(size);
        
        panel.add(inPanel);
        panel.add(comp);
        panel.add(outPanel);
        
        size = new Dimension(
            inPanel.getPreferredSize().width +
                comp.getPreferredSize().width +
                outPanel.getPreferredSize().width,
            comp.getPreferredSize().height);
        panel.setSize(size);
        panel.setPreferredSize(size);
        
        return panel;
    }
    
    /**
     * Adjusts the size of the icon when the label text has changed.
     */
    protected void adjustSize() {
        Dimension size = new Dimension(
            Math.max(getPreferredSize().width, 
                label.getPreferredSize().width +
                    inPanel.getPreferredSize().width +
                    outPanel.getPreferredSize().width +10),
            getPreferredSize().height);
        setSize(size);
        setPreferredSize(size);
        
    }

    /**
     * Abstract method. Must be overridden.
     * 
     * @see quoggles.representation.IBoxIcon#getNewBoxInstance()
     */
    public abstract IBox getNewBoxInstance();


    private class SmallIO extends JPanel {
        
        public SmallIO() {
            Dimension size = new Dimension(
                IBoxConstants.SMALL_IO_LENGTH, IBoxConstants.SMALL_IO_WIDTH);
            setSize(size);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setBackground(Color.WHITE);
            setBorder(new LineBorder(Color.BLACK, 1));
        }
        
    }
}
