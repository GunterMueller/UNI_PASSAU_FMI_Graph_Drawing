//=============================================================================
//
//   GroupMisc.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: GroupMisc.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.editcomponent.ValueEditComponent;

/**
 * Class for displaying the miscellaneous attributes. Misc attributes are those
 * who are no standard attributes and thus are not contained in
 * SemanticView.defaultNodePaths for example.
 */
public class GroupMisc extends SemanticGroup {

    /**
     * Constructs a new GroupMisc.
     * 
     * @param name
     *            the name of the group
     */
    public GroupMisc(String name) {
        super(name);
        // the paths are set by calling findMiscPaths(List, HashSet)
        this.attributePaths = new LinkedList<String>();
    }

    /**
     * Finds the misc (i.e. non default) attributes and adds their paths to
     * <code>attributePaths</code>. Attributes that are not present in all
     * attributables will not be added.
     * 
     * @param attributables
     *            the attributables to be searched for misc attributes
     * @param defaults
     *            the paths to the default attributes
     */
    public void findMiscPaths(List<? extends Attributable> attributables,
            HashSet<String> defaults) {
        this.attributePaths = new LinkedList<String>();
        Attribute attribute = attributables.get(0).getAttributes();

        LinkedList<Attribute> collectionAttributes = new LinkedList<Attribute>();
        // add root attribute, which is a CollectionAttribute
        collectionAttributes.add(attribute);

        while (!collectionAttributes.isEmpty()) {
            attribute = collectionAttributes.removeFirst();
            // get sub-attributes
            Collection<Attribute> attrs = ((CollectionAttribute) attribute)
                    .getCollection().values();
            if (attrs.isEmpty()) {
                // attribute has no sub-attributes
                String path = attribute.getPath();
                if (!defaults.contains(path)
                        && !(attribute instanceof LabelAttribute)
                        && !path.equals(".graphics.bends")
                        && presentInAll(path, attributables)) {
                    // we don't want to get labels, bends or
                    // default attributes or attributes that are not
                    // present in all attributables
                    this.attributePaths.add(path);
                }
            } else {
                for (Attribute nextAttribute : attrs) {
                    String path = nextAttribute.getPath();

                    if (!defaults.contains(path)
                            && !(nextAttribute instanceof LabelAttribute)
                            && !path.equals(".graphics.bends")) {
                        // we don't want to get labels, bends or
                        // default attributes
                        if (presentInAll(path, attributables)) {
                            // we don't want to get attributes that are not
                            // present in all attributables
                            if (nextAttribute instanceof CollectionAttribute) {
                                collectionAttributes.add(nextAttribute);
                            } else {
                                this.attributePaths.add(path);
                            }
                        }
                    }
                }
            }
        }
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

    /**
     * Checks if all attributables have an attribute at the given path.
     * 
     * @param path
     *            the path of the attribute
     * @param attributables
     *            the attributables that will be checked
     * @return <code>true</code>, if the attribute is contained in all
     *         attributables, <code>false</code> if not
     */
    private static boolean presentInAll(String path,
            List<? extends Attributable> attributables) {
        for (Attributable attributable : attributables) {
            try {
                attributable.getAttribute(path);
            } catch (AttributeNotFoundException anfe) {
                // at least one attributable does not contain this attribute
                // so don't add its path
                return false;
            }
        }
        return true;
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
