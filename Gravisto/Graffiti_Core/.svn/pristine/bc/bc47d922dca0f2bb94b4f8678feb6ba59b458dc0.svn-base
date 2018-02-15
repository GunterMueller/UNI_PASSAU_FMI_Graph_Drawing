// =============================================================================
//
//   ToolAction.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.lang.reflect.Field;
import java.util.LinkedList;

import org.graffiti.graph.Graph;
import org.graffiti.session.EditorSession;

/**
 * Subclasses of {@code ToolAction} represent basic operations on the graph, the
 * attribute system and the view. In addition to the actions that are always
 * available, such as creating a new node, every view class provides a list of
 * view-specific actions like zooming or drawing a selection rectangle. Each
 * action has a list of ingoing {@link Slot}s, which must be filled with values
 * before the action is executed.
 * <p>
 * <a href="package-summary.html#ConceptOverview"> Overview of the
 * trigger/action paradigm</a>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class ToolAction<T extends InteractiveView<?>> {
    /**
     * The id to address this action from script code.
     * 
     * @see #getId()
     */
    protected final String id;

    /**
     * The name of this action as seen by the user when graphically editing the
     * tools.
     * 
     * @see #getName()
     */
    protected String name;

    /**
     * The description of this action as seen by the user when graphically
     * editing the tools.
     * 
     * @see #getDescription()
     */
    protected String description;

    /**
     * The list of ingoing slots that specify parameters for this action before
     * it is executed.
     */
    private LinkedList<Slot<?>> inSlots;

    /**
     * The list of outgoing slots that are filled with values resulting from the
     * execution of this action.
     */
    private LinkedList<Slot<?>> outSlots;

    protected ToolAction() {
        ActionId actionId = getClass().getAnnotation(ActionId.class);
        if (actionId == null)
            throw new IllegalArgumentException(
                    "ActionId annotation is not present");
        id = actionId.value();
        addAnnotatedSlots();
    }

    /**
     * Constructs an action with the specified, name and description. The id
     * must be annotated to the subclass by {@link ActionId}.
     * 
     * @param name
     *            the name of the action as seen by the user when graphically
     *            editing the tools.
     * @param description
     *            the description of the action as seen by the user when
     *            graphically editing the tools.
     * @see #getId()
     */
    protected ToolAction(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    /**
     * Constructs an action with the specified id, name and description.
     * 
     * @param id
     *            the id to address the action from script code.
     * @param name
     *            the name of the action as seen by the user when graphically
     *            editing the tools.
     * @param description
     *            the description of the action as seen by the user when
     *            graphically editing the tools.
     * @see #getId()
     */
    protected ToolAction(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        addAnnotatedSlots();
    }

    private void addAnnotatedSlots() {
        Class<?> thisClass = getClass();
        for (Field field : thisClass.getFields()) {
            try {
                InSlot inSlot = field.getAnnotation(InSlot.class);
                if (inSlot != null) {
                    if (Slot.class.isAssignableFrom(field.getType())) {
                        addInSlot((Slot<?>) field.get(null));
                    } else
                        throw new RuntimeException(
                                "Annotated field must be a slot.");
                    continue;
                }
                OutSlot outSlot = field.getAnnotation(OutSlot.class);
                if (outSlot != null) {
                    if (Slot.class.isAssignableFrom(field.getType())) {
                        addOutSlot((Slot<?>) field.get(null));
                    } else
                        throw new RuntimeException(
                                "Annotated field must be a slot.");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "Annotated field must be accessible.");
            }
        }
    }

    /**
     * Returns the id to address this action from script code.
     * 
     * @return the id to address this action from script code.
     * @see #id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of this action as seen by the user when graphically
     * editing the tools.
     * 
     * @return the name of this action as seen by the user when graphically
     *         editing the tools.
     * @see #name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this action as seen by the user when graphically editing
     * the tools.
     * 
     * @param name
     *            the name of this action as seen by the user when graphically
     *            editing the tools.
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of this action as seen by the user when
     * graphically editing the tools.
     * 
     * @return the description of this action as seen by the user when
     *         graphically editing the tools.
     * @see #description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this action as seen by the user when graphically
     * editing the tools.
     * 
     * @param description
     *            the description of this action as seen by the user when
     *            graphically editing the tools.
     */
    protected void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc} Returns the name of this action as seen by the user when
     * graphically editing the tools.
     * 
     * @return the name of this action as seen by the user when graphically
     *         editing the tools.
     * @see #getName()
     * @see #name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Adds an ingoing slot, which can hold a value to specify this action
     * before it executed.
     * 
     * @param slot
     *            the ingoing slot to be added.
     */
    protected void addInSlot(Slot<?> slot) {
        if (inSlots == null) {
            inSlots = new LinkedList<Slot<?>>();
        }
        inSlots.add(slot);
    }

    /**
     * Adds an outgoing slot, which can hold a value resulting from the
     * execution of this action.
     * 
     * @param slot
     *            the outgoing slot to be added.
     */
    protected void addOutSlot(Slot<?> slot) {
        if (outSlots == null) {
            outSlots = new LinkedList<Slot<?>>();
        }
        outSlots.add(slot);
    }

    /**
     * Executes this action.
     * 
     * @param in
     *            a {@link SlotMap}, which must maintain the values for all
     *            ingoing slots of this action.
     * @param out
     *            a {@link SlotMap}, which will maintain the values for all
     *            outgoing slots of this action.
     * @param graph
     *            the graph on which this action is executed.
     * @param view
     *            the view on which this action is executed.
     * @param session
     *            the session on which this action is executed.
     */
    public abstract void perform(InSlotMap in, OutSlotMap out, Graph graph,
            T view, EditorSession session);

    /**
     * Resets this action.
     */
    public void reset() {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
