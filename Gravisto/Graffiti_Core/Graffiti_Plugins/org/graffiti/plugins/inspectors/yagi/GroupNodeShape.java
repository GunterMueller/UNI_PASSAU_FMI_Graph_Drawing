//=============================================================================
//
//   GroupNodeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: GroupNodeShape.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.graffiti.attributes.Attribute;
import org.graffiti.plugin.editcomponent.ValueEditComponent;

/**
 * Class for displaying the shape of nodes.
 */
public class GroupNodeShape extends SemanticGroup {

    /**
     * Constructs a new GroupNodeShape and sets its paths.
     * 
     * @param name
     *            the name of the group
     * 
     */
    public GroupNodeShape(String name) {
        super(name);
        // if you change this, don't forget to change doShowVECs!
        this.attributePaths.add(".graphics.shape");
    }

    /**
     * Shows the attributes of this semantic group.
     * 
     * @see org.graffiti.plugins.inspectors.yagi.SemanticGroup#showVECs(org.graffiti.plugins.inspectors.yagi.DefaultEditPanel,
     *      java.util.List)
     * @param editPanel
     *            the editPanel where the VECs will be added to
     * @param attributes
     *            the booled attributes of the semantic group
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
        for (int i = 0; i < this.attributePaths.size(); i++) {
            Border comBorder = BorderFactory.createTitledBorder(titles[i]);
            JPanel borderPanel = new JPanel();
            borderPanel.add(components[i]);
            borderPanel.setBorder(comBorder);
            borderPanel.setMaximumSize(borderPanel.getPreferredSize());
            vecPanel.add(borderPanel);
        }

        editPanel.getEditPanel().add(vecPanel);
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
