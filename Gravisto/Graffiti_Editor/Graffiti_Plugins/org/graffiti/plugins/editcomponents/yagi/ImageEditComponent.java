// =============================================================================
//
//   ImageEditComponent.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ImageEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.graffiti.attributes.Attribute;
import org.graffiti.graphics.ImageAttribute;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;
import org.graffiti.plugins.inspectors.yagi.SemanticGroup;

/**
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2009-03-12 00:27:21 +0100 (Do, 12 Mrz 2009)
 *          $
 */
public class ImageEditComponent extends AbstractValueEditComponent implements
        ActionListener, SelfLabelingComponent {

    /** The button for choosing a file. */
    private JButton browseButton;

    /** The JPanel containing the textfield and the button. */
    protected JPanel awtImagePanel;

    protected JPanel imagePanel;

    protected JLabel label;

    protected JFileChooser fileChooser;

    protected JTextField referenceField;

    protected JCheckBox tiledCheckBox;

    protected JCheckBox maximizeCheckBox;

    private final static int PREVIEW_IMAGE_SIZE = 32;

    private final static Dimension TEXTFIELD_SIZE = new Dimension(120, 20);

    private ImageAttribute currentImageAttribute;

    protected BufferedImage currentImageIcon;

    /**
     * @param disp
     */
    public ImageEditComponent(Displayable<?> disp) {
        super(disp);

        showEmpty = true;
        currentImageAttribute = (ImageAttribute) (((ImageAttribute) displayable)
                .copy());

        // image component
        this.awtImagePanel = new JPanel(null);
        this.awtImagePanel.setPreferredSize(new Dimension(80, 70));
        this.label = new JLabel();
        label.setBorder(BorderFactory.createEtchedBorder());
        this.label.setBounds(0, 0, 35, 35);
        this.browseButton = new JButton("Browse");
        this.browseButton.setBounds(0, 40,
                browseButton.getPreferredSize().width, browseButton
                        .getPreferredSize().height);
        this.awtImagePanel.add(this.label);
        this.awtImagePanel.add(this.browseButton);
        this.setEditFieldValue();
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new ImageFilter());
        this.browseButton.addActionListener(this);

        // reference component
        referenceField = new JTextField();
        referenceField.setEditable(false);
        referenceField.setMinimumSize(TEXTFIELD_SIZE);
        referenceField.setPreferredSize(TEXTFIELD_SIZE);
        referenceField.setMaximumSize(TEXTFIELD_SIZE);

        // tiled component
        tiledCheckBox = new JCheckBox();
        tiledCheckBox.addActionListener(this);

        // maximize component
        maximizeCheckBox = new JCheckBox();
        maximizeCheckBox.addActionListener(this);

        String[] titles = { "reference", "tiled", "        adjust", "image" };
        JComponent[] components = { referenceField, tiledCheckBox,
                maximizeCheckBox, awtImagePanel };

        // Layout
        JLabel[] imageLabels = new JLabel[4];
        SpringLayout imageBorderLayout = new SpringLayout();
        this.imagePanel = new JPanel(imageBorderLayout);

        for (int i = 0; i < imageLabels.length; i++) {
            imageLabels[i] = new JLabel(titles[i] + ":", SwingConstants.RIGHT);
            imagePanel.add(imageLabels[i]);
            imagePanel.add(components[i]);
        }

        imageBorderLayout.putConstraint(SpringLayout.NORTH, imageLabels[0], 0,
                SpringLayout.NORTH, imagePanel);
        imageBorderLayout.putConstraint(SpringLayout.EAST, imageLabels[0], 0,
                SpringLayout.EAST, imageLabels[2]);
        imageBorderLayout.putConstraint(SpringLayout.NORTH, components[0], 0,
                SpringLayout.NORTH, imagePanel);
        imageBorderLayout.putConstraint(SpringLayout.WEST, components[0],
                SemanticGroup.SPACE, SpringLayout.EAST, imageLabels[2]);
        imageBorderLayout.putConstraint(SpringLayout.NORTH, imageLabels[1],
                SemanticGroup.SPACE, SpringLayout.SOUTH, components[0]);
        imageBorderLayout.putConstraint(SpringLayout.EAST, imageLabels[1], 0,
                SpringLayout.EAST, imageLabels[2]);
        imageBorderLayout.putConstraint(SpringLayout.NORTH, components[1],
                SemanticGroup.SPACE, SpringLayout.SOUTH, components[0]);
        imageBorderLayout.putConstraint(SpringLayout.WEST, components[1],
                SemanticGroup.SPACE, SpringLayout.EAST, imageLabels[2]);
        imageBorderLayout.putConstraint(SpringLayout.NORTH, imageLabels[2],
                SemanticGroup.SPACE, SpringLayout.SOUTH, components[1]);
        imageBorderLayout.putConstraint(SpringLayout.WEST, imageLabels[2], 0,
                SpringLayout.WEST, imagePanel);
        imageBorderLayout.putConstraint(SpringLayout.NORTH, components[2],
                SemanticGroup.SPACE, SpringLayout.SOUTH, components[1]);
        imageBorderLayout.putConstraint(SpringLayout.WEST, components[2],
                SemanticGroup.SPACE, SpringLayout.EAST, imageLabels[2]);
        imageBorderLayout.putConstraint(SpringLayout.NORTH, imageLabels[3],
                SemanticGroup.SPACE, SpringLayout.SOUTH, components[2]);
        imageBorderLayout.putConstraint(SpringLayout.EAST, imageLabels[3], 0,
                SpringLayout.EAST, imageLabels[2]);
        imageBorderLayout.putConstraint(SpringLayout.NORTH, components[3],
                SemanticGroup.SPACE, SpringLayout.SOUTH, components[2]);
        imageBorderLayout.putConstraint(SpringLayout.WEST, components[3],
                SemanticGroup.SPACE, SpringLayout.EAST, imageLabels[2]);
        imageBorderLayout.putConstraint(SpringLayout.EAST, imagePanel, 0,
                SpringLayout.EAST, components[0]);
        imageBorderLayout.putConstraint(SpringLayout.SOUTH, imagePanel, 0,
                SpringLayout.SOUTH, components[3]);

        imagePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createTitledBorder(disp.getName()), BorderFactory
                .createEmptyBorder(5, 5, 5, 5)));
        if (!disp.getDescription().isEmpty()) {
            imagePanel.setToolTipText(disp.getDescription());
        } else if (disp instanceof Attribute) {
            imagePanel
                    .setToolTipText(((Attribute) disp).getPath().substring(1));
        }
        showEmpty = false;
    }

    /**
     * Returns the <code>JComponent</code> for editing this edit component.
     * 
     * @return the <code>JComponent</code> for editing this edit component.
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#getComponent()
     */
    public JComponent getComponent() {
        return this.imagePanel;
    }

    /**
     * 
     * @param event
     *            the event describing the action
     */
    public void actionPerformed(ActionEvent event) {
        if (this.showEmpty) {
            this.showEmpty = false;
        }

        if (event.getSource() == browseButton) {
            int returnVal = fileChooser.showDialog(null, "Browse");

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String reference = fileChooser.getSelectedFile().getName();

                String path = fileChooser.getSelectedFile().getAbsolutePath();
                try {
                    currentImageIcon = ImageIO.read(new FileImageInputStream(
                            new File(path)));

                    label.setIcon(new ImageIcon(currentImageIcon
                            .getScaledInstance(PREVIEW_IMAGE_SIZE,
                                    PREVIEW_IMAGE_SIZE, Image.SCALE_SMOOTH)));
                    referenceField.setText(reference);
                    currentImageAttribute = new ImageAttribute(
                            currentImageAttribute.getId(), tiledCheckBox
                                    .isSelected(), maximizeCheckBox
                                    .isSelected(), currentImageIcon, reference);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // awtImagePanel.firePropertyChange(VEC_VALUE, true, false);
        } else if (event.getSource() == tiledCheckBox) {
            currentImageAttribute.setTiled(tiledCheckBox.isSelected());
        } else if (event.getSource() == maximizeCheckBox) {
            currentImageAttribute.setMaximize(maximizeCheckBox.isSelected());
        }

        fireVECChanged();
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.ValueEditComponent#setEditFieldValue()
     */
    @Override
    protected void setDispEditFieldValue() {
        if (!showEmpty) {
            ImageAttribute ia = (ImageAttribute) this.displayable;
            if (displayable != null) {
                referenceField.setText(ia.getReference());
                tiledCheckBox.setSelected(ia.getTiled());
                maximizeCheckBox.setSelected(ia.getMaximize());
                Image i = ia.getImage().getImage();
                label.setIcon(new ImageIcon(i.getScaledInstance(
                        PREVIEW_IMAGE_SIZE, PREVIEW_IMAGE_SIZE,
                        Image.SCALE_SMOOTH)));
            }
        }

    }

    /*
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#setValue()
     */
    @Override
    protected void setDispValue() {
        if (currentImageAttribute != null) {
            @SuppressWarnings("unchecked")
            Displayable<ImageAttribute> displayable = (Displayable<ImageAttribute>) this.displayable;
            displayable.setValue(currentImageAttribute);
        }
    }

    class ImageFilter extends FileFilter {

        /*
         * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
         */
        @Override
        public boolean accept(File f) {
            boolean accept = f.isDirectory();

            if (!accept) {
                String suffix = getSuffix(f);

                if (suffix != null) {
                    accept = suffix.equals("png") || suffix.equals("jpg")
                            || suffix.equals("gif");
                }
            }
            return accept;
        }

        /*
         * @see javax.swing.filechooser.FileFilter#getDescription()
         */
        @Override
        public String getDescription() {
            return "Image Files (*.png, *.jpg, *.gif)";
        }

        private String getSuffix(File f) {
            String s = f.getPath();
            String suffix = null;
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                suffix = s.substring(i + 1).toLowerCase();
            }
            return suffix;
        }

    }

    public boolean isSelfLabeling() {
        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
