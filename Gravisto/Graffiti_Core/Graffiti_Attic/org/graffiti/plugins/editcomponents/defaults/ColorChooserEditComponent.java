// =============================================================================
//
//   ColorChooserEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ColorChooserEditComponent.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.editcomponents.defaults;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;

import org.graffiti.graphics.ColorAttribute;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;

/**
 * This class provides an edit component for editing color attributes by
 * offering a <code>JColorChooser</code>.
 * 
 * @version $Revision: 5772 $
 * 
 * @see javax.swing.JColorChooser
 * @see org.graffiti.graphics.ColorAttribute
 */
public class ColorChooserEditComponent extends AbstractValueEditComponent {

    /** DOCUMENT ME! */
    public Color emptyColor = Color.LIGHT_GRAY;

    /** DOCUMENT ME! */
    public String buttonText = "Choose";

    /** DOCUMENT ME! */
    private JButton button;

    /**
     * The <code>JColorChooser</code> for editing the
     * <code>ColorAttribute</code>.
     */
    private JColorChooser colorChooser = null;

    /** The dialog that is displayed by the ColorChooser. */
    private JDialog dialog;

    /** DOCUMENT ME! */
    private int opacity = 255;

    /**
     * Constructs a new <code>ColorChooserEditComponent</code>.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public ColorChooserEditComponent(Displayable<?> disp) {
        super(disp);
        this.button = new JButton(buttonText);
        this.button.setMinimumSize(new Dimension(0, 25));
        this.button.setPreferredSize(new Dimension(100, 25));
        this.button.setMaximumSize(new Dimension(2000, 25));

        this.button.setOpaque(true);

        // this.button.setBackground(((ColorAttribute)this.attribute).getColor());
        this.button.setAlignmentX(0.5f);
    }

    /**
     * Returns the <code>ValueEditComponent</code>'s <code>JComponent</code>.
     * 
     * @return DOCUMENT ME!
     */
    public JComponent getComponent() {
        if (colorChooser == null) {
            // //Set up the dialog that the button brings up.
            // colorChooser = new JColorChooser();
            this.button.addActionListener(new ActionListener() {
                ActionListener okListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        button.setBackground(colorChooser.getColor());
                        button.setText(buttonText);

                        // setValue();
                    }
                };

                /**
                 * DOCUMENT ME!
                 * 
                 * @param e
                 *            DOCUMENT ME!
                 */
                public void actionPerformed(ActionEvent e) {
                    // button = (JButton)e.getSource();
                    if (colorChooser == null) {
                        colorChooser = new JColorChooser();
                    }

                    colorChooser.setColor(button.getBackground());
                    dialog = JColorChooser.createDialog(
                            ColorChooserEditComponent.this.button,
                            "Pick a color", true, colorChooser, okListener,
                            null);
                    dialog.setVisible(true);
                }
            });
        }

        return this.button;
    }

    /**
     * Sets the current value of the <code>Attribute</code> in the corresponding
     * <code>JComponent</code>.
     */
    @Override
    protected void setDispEditFieldValue() {
        if (showEmpty) {
            this.button.setBackground(emptyColor);
            this.button.setText(EMPTY_STRING);
        } else {
            this.button.setText(buttonText);

            Color attrColor = ((ColorAttribute) this.displayable).getColor();
            // Color attrColor = (Color) this.displayable.getValue();

            // save opacity value
            this.opacity = attrColor.getAlpha();

            // use opaque color for button
            Color newColor = new Color(attrColor.getRed(),
                    attrColor.getGreen(), attrColor.getBlue());

            this.button.setBackground(newColor);
        }
    }

    /**
     * Sets the value of the displayable specified in the
     * <code>JComponent</code>. But only if it is different.
     */
    @Override
    protected void setDispValue() {
        if (!this.button.getText().equals(EMPTY_STRING)) {
            Color buttonColor = this.button.getBackground();

            if (!((ColorAttribute) this.displayable).getColor().equals(
                    buttonColor)) {
                Color newColor = new Color(buttonColor.getRed(), buttonColor
                        .getGreen(), buttonColor.getBlue(), this.opacity);

                // ((ColorAttribute)this.displayable)
                // .setColor(this.button.getBackground());
                // ((ColorAttribute)this.displayable).setOpacity(this.opacity);
                ((ColorAttribute) this.displayable).setColor(newColor);
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
