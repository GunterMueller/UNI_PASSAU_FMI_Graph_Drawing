//=============================================================================
//
//   ColorChooserEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
// $Id: ColorChooserEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
 * @see javax.swing.JColorChooser
 * @see org.graffiti.graphics.ColorAttribute
 */
public class ColorChooserEditComponent extends AbstractValueEditComponent {

    /** The dummy-colour. */
    public final Color EMPTY_COLOR = Color.LIGHT_GRAY;

    /** The button's dimension. */
    private final Dimension DIM = new Dimension(66, 22);

    /** The button to change the colour. */
    private JButton button;

    /**
     * The <code>JColorChooser</code> for editing the
     * <code>ColorAttribute</code>.
     */
    private JColorChooser colorChooser = null;

    /** The dialog that is displayed by the ColorChooser. */
    private JDialog dialog;

    /** The current colour value. */
    private Color current;

    /** The default opacity value. */
    private int opacity = 255;

    /**
     * Constructs a new <code>ColorChooserEditComponent</code>.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public ColorChooserEditComponent(Displayable<?> disp) {
        super(disp);
        this.button = new JButton() {
            /**
             * 
             */
            private static final long serialVersionUID = -8021050458877188557L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(getForeground());
                g.fillRect(4, 4, this.getWidth() - 8, getHeight() - 8);
            }
        };
        this.button.setOpaque(true);
        this.button.setPreferredSize(DIM);
        this.button.setMaximumSize(DIM);
        this.button.setMinimumSize(DIM);

        this.button.addActionListener(new ActionListener() {
            ActionListener okListener = new ActionListener() {

                // called after the user clicks OK in the colorChooser
                public void actionPerformed(ActionEvent e) {
                    Color newColor = colorChooser.getColor();
                    button.setForeground(newColor);

                    current = newColor;

                    if (showEmpty) {
                        showEmpty = false;
                    }

                    fireVECChanged();
                }
            };

            // called after the user clicks the choose-button
            public void actionPerformed(ActionEvent e) {
                if (colorChooser == null) {
                    colorChooser = new JColorChooser();
                }

                colorChooser.setColor(button.getBackground());
                dialog = JColorChooser.createDialog(
                        ColorChooserEditComponent.this.button, "Pick a color",
                        true, colorChooser, okListener, null);
                dialog.setVisible(true);
            }
        });
    }

    /**
     * Returns the <code>JComponent</code> for editing this edit component.
     * 
     * @return the <code>JComponent</code> for editing this edit component.
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#getComponent()
     */
    public JComponent getComponent() {
        return this.button;
    }

    /**
     * Sets the current value of the <code>Attribute</code> in the corresponding
     * <code>JComponent</code>.
     */
    @Override
    protected void setDispEditFieldValue() {
        if (this.showEmpty) {
            this.button.setForeground(EMPTY_COLOR);
            this.current = EMPTY_COLOR;
        } else {

            Color attrColor = ((ColorAttribute) this.displayable).getColor();
            this.current = attrColor;

            // save opacity value
            this.opacity = attrColor.getAlpha();

            // use opaque color for button
            Color newColor = new Color(attrColor.getRed(),
                    attrColor.getGreen(), attrColor.getBlue());

            this.button.setForeground(newColor);
        }
    }

    /**
     * Sets the value of the displayable specified in the
     * <code>JComponent</code>.
     */
    @Override
    protected void setDispValue() {
        if (!this.showEmpty
                && !((ColorAttribute) this.displayable).getColor().equals(
                        this.current)) {

            this.current = new Color(current.getRed(), current.getGreen(),
                    current.getBlue(), this.opacity);

            ((ColorAttribute) this.displayable).setColor(this.current);
        }
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
