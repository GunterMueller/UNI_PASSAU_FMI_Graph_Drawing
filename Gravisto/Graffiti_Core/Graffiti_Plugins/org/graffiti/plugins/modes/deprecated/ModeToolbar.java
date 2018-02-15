// =============================================================================
//
//   ModeToolbar.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ModeToolbar.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.deprecated;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.SwingConstants;

import org.graffiti.plugin.gui.GraffitiToolbar;
import org.graffiti.util.ObjectReferenceComparator;

/**
 * This toolbar is designed to be used as a representation of
 * <code>ogr.graffiti.plugin.mode.Modey</code>. It handles toolbuttons in a
 * special way.
 * 
 * @version $Revision: 5766 $
 * 
 * @see org.graffiti.plugins.modes.deprecated.Mode
 * @deprecated
 */
@Deprecated
public class ModeToolbar extends GraffitiToolbar {

    /**
     * 
     */
    private static final long serialVersionUID = 6459237212578281824L;

    /** The button group in which the toolButtons should be added. */
    private GraffitiButtonGroup group;

    /**
     * Guarantees a stable ordering of components that are not
     * {@link ToolButton}s, when the tool buttons are sorted.
     */
    protected ObjectReferenceComparator<Component> referenceComparator;

    /**
     * Constructor that sets the id of this toolbar. The id is set to the name
     * of the mode. Tools can be added to the mode represented by this toolbar
     * by adding their ToolButtons to this toolbar. The orientation is set to
     * vertical by default.
     * 
     * @param m
     *            the mode this toolbar represents.
     */
    public ModeToolbar(Mode m) {
        super(m.getId());
        group = new GraffitiButtonGroup();
        this.setOrientation(SwingConstants.VERTICAL);
        referenceComparator = new ObjectReferenceComparator<Component>();
    }

    /**
     * Constructor that sets the id of this toolbar to the name of the given
     * mode and the orientation to the given value.
     * 
     * @param m
     *            the mode this toolbar represents.
     * @param o
     *            the orientation of this toolbar.
     */
    public ModeToolbar(Mode m, int o) {
        super(m.getId());
        group = new GraffitiButtonGroup();
        this.setOrientation(o);
        referenceComparator = new ObjectReferenceComparator<Component>();
    }

    /**
     * Returns the tool that is selected in this ModeToolbar's button group.
     * 
     * @return the tool that is selected in this ModeToolbar's button group.
     */
    public Tool getActiveTool() {
        for (Enumeration<AbstractButton> e = group.getElements(); e
                .hasMoreElements();) {
            ToolButton tb = (ToolButton) e.nextElement();

            if (group.isSelected(tb.getModel()))
                return tb.getTool();
        }
        try {
            ToolButton t1 = (ToolButton) group.getElements().nextElement();
            t1.getTool().activate();
            t1.setSelected(true);
            return t1.getTool();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the tools that are represented by buttons in this toolbar.
     * 
     * @return the tools that are represented by buttons in this toolbar.
     */
    public Tool[] getTools() {
        Tool[] tools = new Tool[group.getButtonCount()];
        int i = 0;

        for (Enumeration<AbstractButton> e = group.getElements(); e
                .hasMoreElements();) {
            tools[i++] = ((ToolButton) e.nextElement()).getTool();
        }

        return tools;
    }

    /**
     * This function add the specified component to this toolbar. Additionaly,
     * if the component is of type <code>ToolButton</code> it is also added to
     * the button group this toolbar contains. If the component is no
     * <code>ToolButton</code> it is added to the end, else it is added at the
     * end of the <code>ToolButtons</code> already added.
     * 
     * @param comp
     *            the component to be added.
     * 
     * @return the component <code>comp</code>.
     * 
     * @see java.awt.Container#add(Component)
     */
    @Override
    public Component add(Component comp) {
        // return super.add(comp);
        if (comp instanceof ToolButton) {
            group.addButton((ToolButton) comp);

            return super.add(comp); // , group.getButtonCount() - 1 // CK, IPK
        } else
            return super.add(comp);
    }

    // /////////////////////////////////////////////////
    // // TODO: overwriting the other add() methods ////
    // /////////////////////////////////////////////////

    public void sortChildren(final Comparator<Tool> comparator) {
        LinkedList<Component> list = new LinkedList<Component>(Arrays
                .asList(getComponents()));

        Comparator<Component> comp = new Comparator<Component>() {
            public int compare(Component first, Component second) {
                if (first instanceof ToolButton) {
                    if (second instanceof ToolButton)
                        return comparator.compare(((ToolButton) first)
                                .getTool(), ((ToolButton) second).getTool());
                    else
                        return -1;
                } else {
                    if (second instanceof ToolButton)
                        return 1;
                    else
                        return referenceComparator.compare(first, second);
                }
            }
        };

        Collections.sort(list, comp);

        removeAll();
        for (Component component : list) {
            addImpl(component, null, -1);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
