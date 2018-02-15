//=============================================================================
//
//   SemanticGroup.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: SemanticGroup.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

/**
 * The super-class of all semantic groups. A semantic group contains attributes
 * that belong together in some way and thus are displayed in the SemanticView
 * by selecting the semantic group .
 */
public abstract class SemanticGroup {

    /** The name of this group. */
    protected String name;

    /** The paths (Strings) to the attributes of this group. */
    protected List<String> attributePaths = new LinkedList<String>();

    /** The default size of labels. */
    public final static Dimension LABEL_DIM = new Dimension(70, 15);

    /** The size of small labels. */
    public final static Dimension SMALL_LABEL_DIM = new Dimension(45, 15);

    /** The size of big labels. */
    public final static Dimension BIG_LABEL_DIM = new Dimension(130, 15);

    /** The size of small components. */
    public final static Dimension SMALL_COMPONENT_DIM = new Dimension(110, 20);

    /** The default size of components. */
    public final static Dimension COMPONENT_DIM = new Dimension(110, 35);

    /** The size of big components. */
    public final static Dimension BIG_COMPONENT_DIM = new Dimension(220, 20);

    /** The size of huge components. */
    public final static Dimension HUGE_COMPONENT_DIM = new Dimension(250, 20);

    /** The size of higher huge components. */
    public final static Dimension HIGHER_HUGE_COMPONENT_DIM = new Dimension(
            250, 24);

    /** The size of highest huge components. */
    public final static Dimension HIGHEST_HUGE_COMPONENT_DIM = new Dimension(
            250, 66);

    /** The space between components. */
    public final static int SPACE = 5;

    /** The double SPACE between components. */
    public final static int DSPACE = 2 * SPACE;

    /** The double DSPACE between components. */
    public final static int QSPACE = 2 * DSPACE;

    /**
     * Builds a new SemanticGroup and sets its name.
     * 
     * @param name
     *            the name of the group
     */
    public SemanticGroup(String name) {
        this.name = name;
    }

    /**
     * Returns this semantic group's name.
     * 
     * @return the name of this group
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the paths to all attributes of this group.
     * 
     * @return the paths to this group's attributes
     */
    public List<String> getAttributePaths() {
        return this.attributePaths;
    }

    /**
     * Calls doShowVECs to draw the VECs belonging to this group's attributes
     * into the editPanel.
     * 
     * @param editPanel
     *            the editPanel where the VECs will be added to
     * @param booledAttributes
     *            the booledAttributes of the semantic group
     */
    public final void showVECs(DefaultEditPanel editPanel,
            List<BooledAttribute> booledAttributes) {
        editPanel.reset();
        doShowVECs(editPanel, booledAttributes);
        editPanel.revalidate();
    }

    /**
     * Draws the VECs belonging to this group's attributes into the editPanel.
     * 
     * @param editPanel
     *            the editPanel where the VECs will be added to
     * @param booledAttributes
     *            the booledAttributes of the semantic group
     */
    protected abstract void doShowVECs(DefaultEditPanel editPanel,
            List<BooledAttribute> booledAttributes);
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
