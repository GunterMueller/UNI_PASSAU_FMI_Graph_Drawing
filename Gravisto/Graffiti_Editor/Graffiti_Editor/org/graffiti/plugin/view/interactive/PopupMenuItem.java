// =============================================================================
//
//   PopupMenuEntry.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JMenuItem;

/**
 * Represents an item in a popup menu that is shown by {@code
 * PopupMenuCompatibleProvider}. The actual AWT/Swing objects for displaying the
 * menu are automatically created by the tool system and transparent to the
 * views and tools. {@code PopupMenuItem} employs the composite pattern and
 * represents single items as well as complete submenus. If this has no
 * children, it will be automatically treated as a leaf. Creators of {@code
 * PopupMenuItem} may either pass an {@link ActionListener} that is directly
 * called once the item is selected by the user, or else a
 * {@link PopupMenuSelectionGesture} that allows for dynamic processing by the
 * tool system will be created.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see PopupMenuCompatibleProvider
 * @see PopupMenuSelectionGesture
 */
public class PopupMenuItem {
    /**
     * {@code PopupMenuItem}s with that id represent menu separators.
     * 
     * @see #setId(String)
     */
    public static final String SEPARATOR_ID = "SEPARATOR_ID";

    /**
     * The id of this item. If id equals {@link #SEPARATOR_ID}, this is treated
     * as a menu separator.
     */
    private String id;

    /**
     * The text of this menu item.
     * 
     */
    private String label;

    /**
     * The slots passed through this item.
     * 
     * @see #getSlots()
     */
    private SlotMap slots;

    /**
     * If there are children, this represents a submenu.
     */
    private SortedMap<Integer, PopupMenuItem> children;

    /**
     * A ActionListener that will be attached to the {@link JMenuItem} created
     * from this. If {@link #actionListener} is {@code null}, selecting this
     * menu item will instead lead to the creation of a
     * {@link PopupMenuSelectionGesture} that is passed to the trigger/action
     * tool system for further processing.
     */
    private ActionListener actionListener;

    /**
     * Constructs a {@code PopupMenuItem} representing a menu separator.
     * 
     * @return a new {@code PopupMenuItem} representing a menu separator.
     */
    public static PopupMenuItem createSeparator() {
        PopupMenuItem result = new PopupMenuItem();
        result.setId(SEPARATOR_ID);
        return result;
    }

    /**
     * Constructs a {@code PopupMenuItem}. As no {@link ActionListener} is
     * specified, selecting this menu item will lead to the creation of a
     * {@link PopupMenuSelectionGesture}.
     */
    public PopupMenuItem() {
    }

    /**
     * Constructs a {@code PopupMenuItem} and initializes its label with the
     * specified text. As no {@link ActionListener} is specified, selecting this
     * menu item will lead to the creation of a
     * {@link PopupMenuSelectionGesture}.
     * 
     * @param label
     *            the text of this menu item.
     */
    public PopupMenuItem(String label) {
        this.label = label;
    }

    /**
     * Constructs a {@code PopupMenuItem} and initializes its label with the
     * specified text. When the user selects this item, the specified {@code
     * ActionListener} will be activated.
     * 
     * @param label
     *            the text of this menu item.
     * @param actionListener
     *            the {@code ActionListener} to be activated when the user
     *            selects this menu item.
     */
    public PopupMenuItem(String label, ActionListener actionListener) {
        this.label = label;
        this.actionListener = actionListener;
    }

    /**
     * Sets the id of this menu item. If id equals {@value #SEPARATOR_ID}, this
     * represents a menu separator. Otherwise, its id will be used by the
     * {@link PopupMenuSelectionGesture} for identification purposes.
     * 
     * @see #getId()
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the text of this menu item.
     * 
     * @param label
     *            the text of this menu item.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the id of this menu item. If id equals {@value #SEPARATOR_ID},
     * this represents a menu separator. Otherwise, its id will be used by the
     * {@link PopupMenuSelectionGesture} for identification purposes.
     * 
     * @return the id of this menu item.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns if this represents a menu separator.
     * 
     * @return {@code true} if this represents a menu separator.
     */
    public boolean isSeparator() {
        return id != null && id.equals(SEPARATOR_ID);
    }

    /**
     * Returns the text of this menu item.
     * 
     * @return the text of this menu item.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the slots attached to this menu item. They will be passed to the
     * tools system via a new {@link PopupMenuSelectionGesture} when the user
     * selects this item. To understand their utility, suppose there is a view
     * where new nodes are created by right-clicking somewhere in the view and
     * then selecting "Create Node" in a popup menu. The output slot of the
     * MouseTrigger carrying the mouse position cannot directly be connected to
     * the input slot of a CreateNode action, as the mouse click should not
     * immediately lead to the node creation but rather to displaying the menu,
     * but on the other hand, the mouse position should determine the position
     * of the node that is potentially created afterwards. To address this
     * problem, connect the position slot of the mouse trigger with the input
     * slot of this {@code PopupMenuItem} and the output slot of a trigger
     * matching the respective {@link PopupMenuSelectionGesture} with the input
     * slot of the CreateNode action.
     * 
     * @return the slots connected to this menu items.
     */
    public SlotMap getSlots() {
        return slots;
    }

    /**
     * Sets the slots passed through this item.
     * 
     * @param slots
     *            the slots passed through this item.
     * @see #getSlots()
     */
    public void setSlots(SlotMap slots) {
        this.slots = slots;
    }

    /**
     * Returns if this has children.
     * 
     * @return {@code true} if this has children, i.e. represents a submenu.
     */
    public boolean hasChildren() {
        return children != null;
    }

    /**
     * Adds the specified menu item to the end of this submenu.
     * 
     * @param child
     *            the item to be added to this submenu.
     */
    public void addLast(PopupMenuItem child) {
        if (children == null) {
            children = new TreeMap<Integer, PopupMenuItem>();
        }
        if (children.isEmpty()) {
            children.put(0, child);
        } else {
            children.put(children.lastKey() + 1, child);
        }
    }

    /**
     * Inserts the specified menu item into this submenu at the specified
     * position.
     * 
     * @param index
     *            the position of the specified item in this submenu. The index
     *            is not interpreted as an absolute position but rather used as
     *            a sorting key.
     * @param child
     *            the item to be added to this submenu.
     */
    public void add(int index, PopupMenuItem child) {
        if (children == null) {
            children = new TreeMap<Integer, PopupMenuItem>();
        }
        children.put(index, child);
    }

    /**
     * Returns the children of this submenu.
     * 
     * @return the children of this submenu. The returned collection must not be
     *         modified outside this class.
     */
    public Collection<PopupMenuItem> getChildren() {
        if (children == null)
            return Collections.emptySet();
        else
            return children.values();
    }

    /**
     * Returns the {@code ActionListener} attached to this menu item.
     * 
     * @return the {@code ActionListener} attached to this menu item. If there
     *         is no such {@code ActionListener}, {@code null} is returned and
     *         selecting this menu item leads to the creation of a
     *         {@link PopupMenuSelectionGesture}.
     */
    public ActionListener getActionListener() {
        return actionListener;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
