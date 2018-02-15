// =============================================================================
//
//   GroupNodeGeometry.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
//$Id: GroupNodeGeometry.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.graffiti.attributes.Attribute;
import org.graffiti.plugin.editcomponent.ValueEditComponent;

/**
 * Class for displaying the coordinate and dimension of nodes.
 */
public class GroupNodeGeometry extends SemanticGroup {

    /**
     * Constructs a new GroupNodeGeometry and sets its paths.
     * 
     * @param name
     *            the name of the group
     * 
     */
    public GroupNodeGeometry(String name) {

        super(name);
        // if you change this, don't forget to change doShowVECs!
        this.attributePaths.add(".graphics.coordinate.x");
        this.attributePaths.add(".graphics.coordinate.y");
        this.attributePaths.add(".graphics.coordinate.z");
        this.attributePaths.add(".graphics.dimension.width");
        this.attributePaths.add(".graphics.dimension.height");
    }

    /**
     * Shows the attributes of this semantic group.
     * 
     * @see org.graffiti.plugins.inspectors.yagi.SemanticGroup#showVECs(org.graffiti.plugins.inspectors.yagi.DefaultEditPanel,
     *      java.util.List)
     * @param editPanel
     *            the editPanel where the VECs will be added to
     * @param attributes
     *            the attributes of the semantic group
     */
    @Override
    public void doShowVECs(DefaultEditPanel editPanel,
            List<BooledAttribute> attributes) {

        JPanel vecPanel = new JPanel();
        vecPanel.setLayout(new BoxLayout(vecPanel, BoxLayout.Y_AXIS));

        // an array containing the JComponents
        JComponent[] components = new JComponent[this.attributePaths.size()];

        // an array containing the titles of the borders
        String[] titles = new String[this.attributePaths.size()];

        // get labels & vecs
        for (int i = 0; i < this.attributePaths.size(); i++) {

            BooledAttribute booled = attributes.get(i);
            if (booled == null) {
                // someone deleted a standard attribute...
                System.err.println("Can't display attribute: "
                        + this.attributePaths.get(i));
                titles[i] = "";
                components[i] = new JLabel();
            } else {

                // create VEC
                Attribute attribute = booled.getAttribute();
                titles[i] = attribute.getId();

                ValueEditComponent vec = editPanel.createVEC(attribute,
                        editPanel.getEditComponentMap().get(
                                attribute.getClass()));
                components[i] = vec.getComponent();
                vec.setShowEmpty(!booled.getBool());
                editPanel.addListener(vec);
            }
        }

        // create borders and add components
        // dimension panel
        Border dimBorder = BorderFactory.createTitledBorder("dimension");
        SpringLayout dimBorderLayout = new SpringLayout();
        JPanel dimBorderPanel = new JPanel(dimBorderLayout);
        JLabel widthLabel = new JLabel(titles[3] + ":", SwingConstants.RIGHT);
        JLabel heightLabel = new JLabel(titles[4] + ":", SwingConstants.RIGHT);
        dimBorderPanel.add(widthLabel);
        dimBorderPanel.add(components[3]);
        dimBorderPanel.add(heightLabel);
        dimBorderPanel.add(components[4]);
        dimBorderPanel.setBorder(dimBorder);

        dimBorderLayout.putConstraint(SpringLayout.NORTH, widthLabel, 0,
                SpringLayout.NORTH, dimBorderPanel);
        dimBorderLayout.putConstraint(SpringLayout.EAST, widthLabel, 0,
                SpringLayout.EAST, heightLabel);
        dimBorderLayout.putConstraint(SpringLayout.NORTH, components[3], 0,
                SpringLayout.NORTH, dimBorderPanel);
        dimBorderLayout.putConstraint(SpringLayout.WEST, components[3], SPACE,
                SpringLayout.EAST, widthLabel);
        dimBorderLayout.putConstraint(SpringLayout.NORTH, heightLabel, SPACE,
                SpringLayout.SOUTH, components[3]);
        dimBorderLayout.putConstraint(SpringLayout.WEST, heightLabel, 0,
                SpringLayout.WEST, dimBorderPanel);
        dimBorderLayout.putConstraint(SpringLayout.NORTH, components[4], SPACE,
                SpringLayout.SOUTH, components[3]);
        dimBorderLayout.putConstraint(SpringLayout.WEST, components[4], SPACE,
                SpringLayout.EAST, heightLabel);
        dimBorderLayout.putConstraint(SpringLayout.EAST, dimBorderPanel, 0,
                SpringLayout.EAST, components[4]);
        dimBorderLayout.putConstraint(SpringLayout.SOUTH, dimBorderPanel, 0,
                SpringLayout.SOUTH, components[4]);
        vecPanel.add(dimBorderPanel);

        // coordinate panel
        Border coordBorder = BorderFactory.createTitledBorder("coordinate");
        SpringLayout coordBorderLayout = new SpringLayout();
        JPanel coordBorderPanel = new JPanel(coordBorderLayout);
        JLabel xLabel = new JLabel(titles[0] + ":", SwingConstants.RIGHT);
        JLabel yLabel = new JLabel(titles[1] + ":", SwingConstants.RIGHT);
        JLabel zLabel = new JLabel(titles[2] + ":", SwingConstants.RIGHT);
        coordBorderPanel.add(xLabel);
        coordBorderPanel.add(components[0]);
        coordBorderPanel.add(yLabel);
        coordBorderPanel.add(components[1]);
        coordBorderPanel.add(zLabel);
        coordBorderPanel.add(components[2]);
        coordBorderPanel.setBorder(coordBorder);

        Dimension dim = dimBorderPanel.getPreferredSize();
        coordBorderPanel.setSize(dim);
        coordBorderPanel.setMinimumSize(dim);
        dim.height += 100;
        coordBorderPanel.setMaximumSize(dim);

        coordBorderLayout.putConstraint(SpringLayout.NORTH, xLabel, 0,
                SpringLayout.NORTH, coordBorderPanel);
        coordBorderLayout.putConstraint(SpringLayout.EAST, xLabel, 0,
                SpringLayout.EAST, yLabel);
        coordBorderLayout.putConstraint(SpringLayout.WEST, xLabel, 15,
                SpringLayout.WEST, coordBorderPanel);
        coordBorderLayout.putConstraint(SpringLayout.NORTH, components[0], 0,
                SpringLayout.NORTH, coordBorderPanel);
        coordBorderLayout.putConstraint(SpringLayout.WEST, components[0],
                SPACE, SpringLayout.EAST, xLabel);
        coordBorderLayout.putConstraint(SpringLayout.NORTH, yLabel, SPACE,
                SpringLayout.SOUTH, components[0]);
        coordBorderLayout.putConstraint(SpringLayout.WEST, yLabel, 15,
                SpringLayout.WEST, coordBorderPanel);
        coordBorderLayout.putConstraint(SpringLayout.NORTH, components[1],
                SPACE, SpringLayout.SOUTH, components[0]);
        coordBorderLayout.putConstraint(SpringLayout.WEST, components[1],
                SPACE, SpringLayout.EAST, yLabel);
        coordBorderLayout.putConstraint(SpringLayout.NORTH, zLabel, SPACE,
                SpringLayout.SOUTH, components[1]);
        coordBorderLayout.putConstraint(SpringLayout.WEST, zLabel, 15,
                SpringLayout.WEST, coordBorderPanel);
        coordBorderLayout.putConstraint(SpringLayout.NORTH, components[2],
                SPACE, SpringLayout.SOUTH, components[1]);
        coordBorderLayout.putConstraint(SpringLayout.WEST, components[2], 0,
                SpringLayout.WEST, components[1]);
        coordBorderLayout.putConstraint(SpringLayout.EAST, components[2], 0,
                SpringLayout.EAST, components[1]);
        coordBorderLayout.putConstraint(SpringLayout.SOUTH, coordBorderPanel,
                0, SpringLayout.SOUTH, components[2]);
        vecPanel.add(coordBorderPanel);

        editPanel.getEditPanel().add(vecPanel);
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
