//=============================================================================
//
//   FileChooserEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: FileChooserEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.graffiti.plugin.Displayable;

/**
 * This VEC provides a editable textfield and a button to choose files.
 */
public class FileChooserEditComponent extends StringEditComponent {

    /** The button for choosing a file. */
    private JButton button;

    /** The JPanel containing the textfield and the button. */
    protected JPanel panel;

    /**
     * Constructs a new <code>FileChooserEditComponent</code>.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public FileChooserEditComponent(Displayable<?> disp) {
        super(disp);

        SpringLayout layout = new SpringLayout();
        this.panel = new JPanel(layout);
        this.button = new JButton("Choose");
        this.panel.add(this.textField);
        this.panel.add(this.button);
        this.button.addActionListener(new ActionListener() {
            // called after the user clicks the choose-button
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showDialog(null, "Choose");

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    // set chosen file path in the textField
                    textField.setText(fileChooser.getSelectedFile()
                            .getAbsolutePath());
                }

                fireVECChanged();
            }
        });

        layout.putConstraint(SpringLayout.NORTH, this.textField, 0,
                SpringLayout.NORTH, this.panel);
        layout.putConstraint(SpringLayout.WEST, this.textField, 0,
                SpringLayout.WEST, this.panel);
        layout.putConstraint(SpringLayout.NORTH, this.button, 5,
                SpringLayout.SOUTH, this.textField);
        layout.putConstraint(SpringLayout.WEST, this.button, 0,
                SpringLayout.WEST, this.panel);
        layout.putConstraint(SpringLayout.EAST, this.panel, 0,
                SpringLayout.EAST, this.textField);
        layout.putConstraint(SpringLayout.SOUTH, this.panel, 0,
                SpringLayout.SOUTH, this.button);
    }

    /**
     * Returns the <code>JComponent</code> for editing this edit component.
     * 
     * @return the <code>JComponent</code> for editing this edit component.
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#getComponent()
     */
    @Override
    public JComponent getComponent() {
        return this.panel;
    }

    /**
     * Is called after the textField changes. Informs the editPanel about the
     * change.
     * 
     * @param event
     *            the event describing the action
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (this.showEmpty) {
            this.showEmpty = false;
        }

        fireVECChanged();
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
