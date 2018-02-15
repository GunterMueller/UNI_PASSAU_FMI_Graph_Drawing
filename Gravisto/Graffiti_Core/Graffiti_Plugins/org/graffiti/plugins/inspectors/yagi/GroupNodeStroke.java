//=============================================================================
//
//   GroupNodeStroke.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
//$Id: GroupNodeStroke.java 5766 2010-05-07 18:39:06Z gleissner $

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
 * Class for displaying the node attributes "linemode", "frameThickness" and
 * "framecolor".
 */
public class GroupNodeStroke extends SemanticGroup {

    /**
     * Constructs a new GroupNodeStroke and sets its paths.
     * 
     * @param name
     *            the name of the group
     */
    public GroupNodeStroke(String name) {
        super(name);
        // if you change this, don't forget to change doShowVECs!
        this.attributePaths.add(".graphics.linemode");
        this.attributePaths.add(".graphics.frameThickness");
        this.attributePaths.add(".graphics.framecolor");
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
    protected void doShowVECs(DefaultEditPanel editPanel,
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

        // these titles are better...
        titles[0] = "style";
        titles[1] = "width";
        titles[2] = "colour";

        // create borders and add components
        for (int i = 0; i < this.attributePaths.size(); i++) {
            Border comBorder = BorderFactory.createTitledBorder(titles[i]);
            JPanel borderPanel = new JPanel();
            borderPanel.add(components[i]);
            borderPanel.setBorder(comBorder);
            vecPanel.add(borderPanel);
        }

        editPanel.getEditPanel().add(vecPanel);
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
