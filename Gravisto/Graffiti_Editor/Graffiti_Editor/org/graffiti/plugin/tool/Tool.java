// =============================================================================
//
//   Tool.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Tool.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.tool;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import org.graffiti.editor.GraffitiEditor;
import org.graffiti.plugin.gui.ToolButton;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.ToolAction;
import org.graffiti.plugin.view.interactive.Trigger;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugin.view.interactive.ViewFamily;

/**
 * Tools define a mode of interaction by relating triggers to actions. To
 * provide a specific behavior for the user, create a subclass of {@code Tool}
 * and register an instance of it at the {@link ToolRegistry}, which
 * automatically creates and assigns a {@link ToolButton}. Each tool must be
 * identifiable by an unique id matchable by {@link #ID_PATTERN}. The set of
 * currently available tools, that is, the set of visible tool buttons, is
 * controlled by {@link ToolFilter}s. Each tool supports exactly one
 * {@link ViewFamily}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision: 5768 $ $Date: 2009-01-14 21:12:38 +0100 (Mi, 14 Jan 2009)
 *          $
 * @see Trigger
 * @see ToolAction
 * @param <T>
 *            The superclass of all views belonging to the view family supported
 *            by this tool.
 */
public class Tool<T extends InteractiveView<T>> implements Comparable<Tool<?>> {
    /**
     * The pattern of admissible ids.
     */
    public static final Pattern ID_PATTERN = Pattern.compile("\\w+");

    /**
     * The preferences root node of the tool system.
     */
    private static final Preferences PREFERENCES = Preferences
            .userNodeForPackage(Tool.class);

    /**
     * Denotes if this tool is currently active. At each point of time, at most
     * one tool is active. The active tool receives the user gestures generated
     * by the active view.
     */
    private boolean isActive;

    /**
     * The preferences of this tool.
     */
    protected final ToolPreferences preferences;

    /**
     * The view family supported by this tool.
     */
    protected final ViewFamily<T> viewFamily;

    /**
     * The id of this tool. Different instances of {@code Tool} must have
     * different ids.
     */
    protected final String id;

    /**
     * Imposes the order of tool buttons in the tool bar.
     */
    private int position;

    /**
     * The name of this tool as seen by the user.
     */
    private String name;

    /**
     * The description of this tool as seen by the user.
     */
    private String description;

    /**
     * The icon for the tool button representing this tool.
     */
    private ImageIcon icon;

    /**
     * The path to the image file storing the icon for the tool button
     * representing this tool. May be null. The icon can alternatively be
     * attached to this tool by setIcon.
     */
    private String iconPath;

    /**
     * Denotes if this tool is hidden by the user. Note that isHidden directly
     * only controls the behavior of HiddenToolFilter. Even if isHidden is
     * false, the tool may still be actually invisible because of other reasons.
     * To get the effective visibility, check the visibility of the related tool
     * button.
     */
    private boolean isHidden;

    /**
     * Denotes if this tool should be visible by default.
     */
    boolean isDefaultMode;

    /**
     * Denotes if this tool is editable by the user.
     */
    boolean isReadOnly;

    /**
     * Denotes if this tool is marked for deletion.
     */
    private boolean isDeleted;

    /**
     * The tool button representing this tool.
     */
    ToolButton toolButton;

    /**
     * The tool that was activated when this tool was deactivated the last time.
     * May be null.
     */
    Tool<T> nextActiveTool;

    /**
     * The tool that was deactivated when this tool was activated the last time.
     * May be null.
     */
    Tool<T> prevActiveTool;

    /**
     * Constructs a {@code Tool} with the specified id and for the specified
     * view family.
     * 
     * @param viewFamily
     *            the view family supported by the new tool.
     * @param id
     *            the id of the new tool.
     */
    protected Tool(ViewFamily<T> viewFamily, String id) {
        if (!ID_PATTERN.matcher(id).matches())
            throw new IllegalArgumentException("Illegal tool id.");

        isReadOnly = true;
        isActive = false;
        preferences = getToolPreferences(viewFamily, id);
        this.viewFamily = viewFamily;
        this.id = id;
        snatchPosition();
        name = preferences.get("name", "");
        description = preferences.get("description", "");
        isHidden = preferences.getBoolean("hidden", false);
        iconPath = preferences.get("icon", null);
        isDefaultMode = preferences.getBoolean("defaultMode", isDefaultMode());
    }

    /**
     * Returns the preferences root node for the tools supporting the specified
     * view family.
     * 
     * @param viewFamily
     *            the view family supported by the tools, whose preferences root
     *            node is to be returned.
     * @return the preferences root node for the tools supporting the specified
     *         view family.
     */
    static Preferences getViewFamilyPreferences(ViewFamily<?> viewFamily) {
        return PREFERENCES.node("views/" + viewFamily.getId());
    }

    /**
     * Returns the preferences of the tool with the specified id and supporting
     * the specified view family.
     * 
     * @param viewFamily
     *            the view family supported by the tool, whose preferences are
     *            to be returned.
     * @param id
     *            the id of the tool.
     * @return the preferences of the tool with the specified id and supporting
     *         the specified view family.
     */
    private static ToolPreferences getToolPreferences(ViewFamily<?> viewFamily,
            String id) {
        return new ToolPreferences(getViewFamilyPreferences(viewFamily)
                .node(id));
    }

    /**
     * Sets if this tool is currently active. To activate this tool, rather call
     * {@link #activate()}. At each point of time, at most one tool is active.
     * The active tool receives the {@link UserGesture}s generated by the active
     * view.
     * 
     * @param active
     *            denotes if this tool is to be set to active or inactive.
     */
    final void setActive(boolean active) {
        boolean prevActive = isActive;
        isActive = active;
        if (!prevActive && active) {
            toolButton.setSelected(true);
            activated();
        } else if (prevActive && !active) {
            deactivated();
        }
        if (active) {
            reset();
        }
    }

    /**
     * Returns if this tool is currently active. At each point of time, at most
     * one tool is active. The active tool receives the {@link UserGesture}s
     * generated by the active view.
     * 
     * @return if this tool is currently active.
     */
    public final boolean isActive() {
        return isActive;
    }

    /**
     * Is called when this tool is activated. Overwrite this method to define
     * the specific tool behavior.
     */
    protected void activated() {
    }

    /**
     * Is called when this tool is deactivated. Overwrite this method to define
     * the specific tool behavior.
     */
    protected void deactivated() {
    }

    /**
     * Is called when this tool is reseted. Overwrite this method to define the
     * specific tool behavior. A tool is reseted after it has been activated,
     * when it is active and the currently active view changes or in response to
     * a call to {@link #reset()}.
     * 
     * @param env
     *            the tool environment.
     */
    protected void reseted(ToolEnvironment<T> env) {
    }

    /**
     * Resets this tool.
     */
    public final void reset() {
        ToolRegistry.get().resetTool(this);
    }

    /**
     * Activates this tool.
     * 
     * @throws IllegalStateException
     *             if there is no active view or if the view family of this tool
     *             does not equal the currently active view family.
     */
    public final void activate() {
        ToolRegistry.get().setActiveTool(this);
    }

    /**
     * Moves the related tool button one position up in the tool bar.
     */
    public final void moveUp() {
        ToolRegistry.get().moveUp(this);
    }

    /**
     * Moves the related tool button one position down in the tool bar.
     */
    public final void moveDown() {
        ToolRegistry.get().moveDown(this);
    }

    /**
     * Returns the view family supported by this tool.
     * 
     * @return the view family supported by this tool.
     */
    public final ViewFamily<T> getViewFamily() {
        return viewFamily;
    }

    /**
     * Is called when an {@code UserGesture} is performed by the user if this
     * tool is active. The performed gesture can be obtained from the specified
     * environment. Overwrite this method to define the specific tool behavior.
     * 
     * @param env
     *            the tool environment.
     */
    protected void gesturePerformed(ToolEnvironment<T> env) {
    }

    /**
     * Sets the position of the related tool button in the tool bar. To change
     * the position, rather call {@link #moveDown()} or {@link #moveUp()}.
     * 
     * @param position
     *            the new position.
     */
    final void setPosition(int position) {
        this.position = position;
        preferences.putInt("position", position);
    }

    /**
     * Returns the position of the related tool button in the tool bar.
     * 
     * @return the position of the related tool button in the tool bar.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns the name of this tool as seen by the user.
     * 
     * @return the name of this tool as seen by the user.
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets the name of this tool as seen by the user.
     * 
     * @param name
     *            the name to set.
     */
    public final void setName(String name) {
        this.name = name;
        preferences.put("name", name);
        if (toolButton != null) {
            toolButton.updateToolTipText();
        }
    }

    /**
     * Returns the description of this tool as seen by the user.
     * 
     * @return the description of this tool.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the description of this tool as seen by the user.
     * 
     * @param description
     *            the description to set.
     */
    public final void setDescription(String description) {
        this.description = description;
        preferences.put("description", description);
        if (toolButton != null) {
            toolButton.updateToolTipText();
        }
    }

    /**
     * Returns the unique id of this tool.
     * 
     * @return the unique id of this tool.
     */
    public final String getId() {
        return id;
    }

    /**
     * {@inheritDoc} This implementation compares the tools by their position.
     * If different tools share the same position, they are compared by their
     * id.
     */
    public final int compareTo(Tool<?> other) {
        if (this.position < other.position)
            return -1;
        else if (this.position > other.position)
            return 1;
        else
            return this.id.compareTo(other.id);
    }

    /**
     * Returns the icon.
     * 
     * @return the icon.
     */
    public final ImageIcon getIcon() {
        if (icon == null) {
            if (iconPath != null) {
                // Load from path.
                try {
                    icon = new ImageIcon(iconPath);
                    if (icon.getIconWidth() == 24 && icon.getIconHeight() == 24)
                        return icon;
                } catch (Exception e) {
                }
                icon = new ImageIcon(GraffitiEditor.class
                        .getResource("images/errorTool.png"));
                return icon;
            }
            icon = new ImageIcon(GraffitiEditor.class
                    .getResource("images/defaultTool.png"));
        }
        return icon;
    }

    /**
     * Returns the file path of the icon of this tool.
     * 
     * @return the file path of the icon of this tool. May return {@code null}
     *         if the icon was set by {@link #setIcon(ImageIcon)}.
     */
    public final String getIconPath() {
        return iconPath;
    }

    /**
     * Sets the icon for the related tool button. It may alternatively specified
     * by setting the path to an image file by {@link #setIconPath(String)}.
     * 
     * @param icon
     *            the icon to set.
     */
    public final void setIcon(ImageIcon icon) {
        this.icon = icon;
        if (toolButton != null) {
            toolButton.setIcon(icon);
        }
    }

    /**
     * Sets the file path of the icon for the related tool button and loads the
     * icon. The icon may alternatively set by {@link #setIcon(ImageIcon)}.
     * 
     * @param iconPath
     *            the path to the image file containing the icon to set.
     */
    public final void setIconPath(String iconPath) {
        this.iconPath = iconPath;
        preferences.put("icon", iconPath);
        icon = null;
        getIcon();
        if (toolButton != null) {
            toolButton.setIcon(icon);
        }
    }

    /**
     * Returns the related tool button.
     * 
     * @return the related tool button, which is used to activate this tool by
     *         the user.
     */
    public final ToolButton getToolButton() {
        return toolButton;
    }

    /**
     * Returns if this tool is hidden. Note that {@code isHidden} directly only
     * controls the behavior of HiddenToolFilter. Even if {@code isHidden} is
     * {@code false}, the tool may still be actually invisible because of other
     * reasons. To get the effective visibility, check the visibility of the
     * related tool button.
     * 
     * @return if this tool is hidden.
     * @see #getToolButton()
     */
    public final boolean isHidden() {
        return isHidden;
    }

    /**
     * Sets if this tool shall be hidden.
     * 
     * @param isHidden
     *            denotes if this tool shall be hidden.
     * @see #isHidden()
     */
    public final void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
        preferences.putBoolean("hidden", isHidden);
        if (toolButton != null) {
            boolean isVisible = ToolRegistry.get().isVisible(this);
            if (!isVisible) {
                setActive(false);
            }
            toolButton.setVisible(isVisible);
        }
    }

    /**
     * Returns if this tool should be visible by default.
     * 
     * @return if this tool should be visible by default.
     * @see DefaultModeFilter
     */
    protected boolean isDefaultMode() {
        return true;
    }

    /**
     * Returns if this tool may be modified by the user.
     * 
     * @return if this tool may be modified by the user.
     */
    public final boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Returns if this tool is marked for deletion.
     * 
     * @return if this tool is marked for deletion.
     * @see #delete()
     */
    public final boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Marks this tool for deletion. The tool becomes inactive and invisible and
     * is removed from the preferences tree. It ceases to exist on the next
     * start of the program if it is not added again by a plugin.
     */
    public final void delete() {
        isDeleted = true;
        if (toolButton != null) {
            setActive(false);
            toolButton.setVisible(false);
        }
        preferences.delete();
    }

    /**
     * Returns if this tool is a tool dummy. Tool dummies represent tools that
     * are present in the preferences tree but have not yet been added by their
     * providing plugins so they are currently unavailable. When the respective
     * tool is added, this dummy will be removed.
     * 
     * @return if this tool is a tool dummy.
     * @see ToolDummy
     */
    public final boolean isDummy() {
        return isDummy(0);
    }

    /**
     * Returns if this tool is a tool dummy. Tool dummies represent tools that
     * are present in the preferences tree but have not yet been added by their
     * providing plugins so they are currently unavailable. When the respective
     * tool is added, this dummy will be removed.
     * 
     * @return if this tool is a tool dummy.
     * @see ToolDummy
     */
    boolean isDummy(int i) {
        return false;
    }

    /**
     * Returns a string representing the plugin providing this tool.
     * 
     * @return a string representing the plugin providing this tool. May be
     *         null.
     */
    public final String getProvidingPlugin() {
        return preferences.get("plugin", null);
    }

    private void snatchPosition() {
        Preferences prefs = getViewFamilyPreferences(viewFamily);

        position = preferences.getInt("position", -1);
        int nextFreePosition = prefs.getInt("nextFreePosition", 0);
        if (position == -1) {
            position = nextFreePosition;
            preferences.putInt("position", position);
        }
        if (position >= nextFreePosition) {
            prefs.putInt("nextFreePosition", position + 1);
            try {
                prefs.flush();
            } catch (BackingStoreException e) {
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
