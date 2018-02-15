// =============================================================================
//
//   ToolContext.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugin.view.interactive.SlotMap;
import org.graffiti.plugin.view.interactive.ToolAction;
import org.graffiti.plugin.view.interactive.Trigger;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;

/**
 * The environment the tools operate on. It holds the performed user gesture,
 * the current view, graph and session and provides methods to match the user
 * gesture against triggers and issue actions. The {@code put}-methods, which
 * fill the input and parameter slots of triggers and actions, may be
 * concatenated as in the following example: <code>
 * ToolEnvironment env = ...
 * env.putIn(ZoomRotation.snapSlot, true)
 *    .putIn(ZoomRotation.rawPositionSlot, new Point2D.Double())
 *    .execute(ZoomRotation.class);
 * </code>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Tool
 * @see UserGesture
 * @see Trigger
 * @see ToolAction
 * @param <T>
 *            The superclass of all views belonging to the view family.
 */
public final class ToolEnvironment<T extends InteractiveView<T>> {
    /**
     * Class that is used to allow for the concatenation of several {@code put}
     * -Methods, which fill the slots.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    public class SlotHelper {
        /**
         * Constructs a new {@code SlotHelper}
         */
        private SlotHelper() {
        }

        /**
         * Returns if the performed user gesture matches the trigger with the
         * specified id.
         * 
         * @param triggerId
         *            the id of the trigger the performed user gesture to match
         *            against.
         * @return if the performed user gesture matches the trigger with the
         *         specified id.
         */
        public boolean matches(String triggerId) {
            return matches(getTrigger(triggerId));
        }

        /**
         * Returns if the performed user gesture matches the specified trigger.
         * 
         * @param trigger
         *            the trigger the performed user gesture to match against.
         * @return if the performed user gesture matches the specified trigger.
         */
        public boolean matches(Trigger trigger) {
            if (userGesture == null)
                return false;
            boolean result = trigger.matches(userGesture, parameters, inSlots);
            if (result) {
                outSlots.clear();
                trigger.apply(userGesture, parameters, inSlots, outSlots);
            }
            inSlots.clear();
            parameters.clear();
            return result;
        }

        /**
         * Executes the action with the specified id.
         * 
         * @param actionId
         *            the id of the action to execute.
         * @see ToolAction
         */
        public void execute(String actionId) {
            execute(view.getFamily().getAction(actionId));
        }

        /**
         * Executes the action implemented by the specified class.
         * 
         * @param toolClass
         *            the class implementing the action to execute.
         * @see ToolAction
         */
        public void execute(Class<? extends ToolAction<?>> toolClass) {
            execute(view.getFamily().getAction(toolClass));
        }

        /**
         * Executes the specified action.
         * 
         * @param action
         *            the action to execute.
         */
        public void execute(ToolAction<T> action) {
            outSlots.clear();
            action.perform(inSlots, outSlots, graph, view, session);
            inSlots.clear();
        }

        /**
         * Fills the specified parameter slot with the specified value.
         * 
         * @param <S>
         *            the type of the parameter slot.
         * @param slot
         *            the parameter slot to fill.
         * @param value
         *            the value to assign to the slot.
         * @return a reference to this object.
         */
        public <S> SlotHelper putParam(Slot<S> slot, S value) {
            parameters.put(slot, value);
            return this;
        }

        /**
         * Fills the specified input slot with the specified value.
         * 
         * @param <S>
         *            the type of the input slot.
         * @param slot
         *            the input slot to fill.
         * @param value
         *            the value to assign to the slot.
         * @return a reference to this object.
         */
        public <S> SlotHelper putIn(Slot<S> slot, S value) {
            inSlots.put(slot, value);
            return this;
        }
    };

    /**
     * The input slots, which are used to specify the next executed action.
     */
    private SlotMap inSlots;

    /**
     * The output slots, which are filled by the results of matching triggers
     * and executed actions.
     */
    private SlotMap outSlots;

    /**
     * The parameter slots, which are used to narrow the scope of the next
     * trigger to match the performed user gesture against.
     */
    private SlotMap parameters;

    /**
     * Is used to allow for the concatenation of several put-Methods, which fill
     * the slots.
     */
    private SlotHelper slotHelper;

    /**
     * The performed user gesture.
     */
    private UserGesture userGesture;

    /**
     * The current view where the user gesture occurred.
     */
    private T view;

    /**
     * The current graph to operate on.
     */
    private Graph graph;

    /**
     * The current session, which contains the graph to operate on and the view
     * where the user gesture occurred.
     */
    private EditorSession session;

    /**
     * Constructs a tool environment.
     */
    public ToolEnvironment() {
        inSlots = new SlotMap();
        outSlots = new SlotMap();
        parameters = new SlotMap();
        slotHelper = new SlotHelper();
    }

    /**
     * Returns the input slots.
     * 
     * @return the input slots, which are used to specify the next executed
     *         action.
     */
    public SlotMap getIn() {
        return inSlots;
    }

    /**
     * Returns the output slots.
     * 
     * @return the output slots, which are filled by the results of matching
     *         triggers and executed actions.
     */
    public SlotMap getOut() {
        return outSlots;
    }

    /**
     * Returns the value of the specified output slot.
     * 
     * @param <S>
     *            the type of the output slot.
     * @param outSlot
     *            the output slot whose value is to be returned.
     * @return the value of the specified output slot.
     */
    public <S> S getOut(Slot<S> outSlot) {
        return outSlots.get(outSlot);
    }

    /**
     * Return the parameter slots.
     * 
     * @return the parameter slots, which are used to narrow the scope of the
     *         next trigger to match the performed user gesture against.
     */
    public SlotMap getParam() {
        return parameters;
    }

    /**
     * Returns the performed user gesture.
     * 
     * @return the performed user gesture. May be {@code null} if the
     */
    public UserGesture getGesture() {
        return userGesture;
    }

    /**
     * Returns the view where the user gesture occurred.
     * 
     * @return the view where the user gesture occurred.
     */
    public T getView() {
        return view;
    }

    /**
     * Returns the graph to operate on.
     * 
     * @return the graph to operate on.
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Returns the current session.
     * 
     * @return the current session, which contains the graph to operate on and
     *         the view where the user gesture occurred.
     */
    public EditorSession getSession() {
        return session;
    }

    /**
     * Fills the specified parameter slot with the specified value.
     * 
     * @param <S>
     *            the type of the parameter slot.
     * @param slot
     *            the parameter slot to fill.
     * @param value
     *            the value to assign to the slot.
     * @return a reference to this object.
     */
    public <S> SlotHelper putParam(Slot<S> slot, S value) {
        return slotHelper.putParam(slot, value);
    }

    /**
     * Fills the specified input slot with the specified value.
     * 
     * @param <S>
     *            the type of the input slot.
     * @param slot
     *            the input slot to fill.
     * @param value
     *            the value to assign to the slot.
     * @return a reference to this object.
     */
    public <S> SlotHelper putIn(Slot<S> slot, S value) {
        return slotHelper.putIn(slot, value);
    }

    /**
     * Returns the trigger with the specified id.
     * 
     * @param triggerId
     *            the id of the trigger to return.
     * @return the trigger with the specified id.
     */
    public Trigger getTrigger(String triggerId) {
        return view.getFamily().getTrigger(triggerId);
    }

    /**
     * Returns if the performed user gesture matches the specified trigger.
     * 
     * @param trigger
     *            the trigger the performed user gesture to match against.
     * @return if the performed user gesture matches the specified trigger.
     */
    public boolean matches(Trigger trigger) {
        return slotHelper.matches(trigger);
    }

    /**
     * Returns if the performed user gesture matches the trigger with the
     * specified id.
     * 
     * @param triggerId
     *            the id of the trigger the performed user gesture to match
     *            against.
     * @return if the performed user gesture matches the trigger with the
     *         specified id.
     */
    public boolean matches(String triggerId) {
        return slotHelper.matches(triggerId);
    }

    /**
     * Executes the action with the specified id.
     * 
     * @param actionId
     *            the id of the action to execute.
     * @see ToolAction
     */
    public void execute(String actionId) {
        slotHelper.execute(actionId);
    }

    /**
     * Executes the action implemented by the specified class.
     * 
     * @param toolClass
     *            the class implementing the action to execute.
     * @see ToolAction
     */
    public void execute(Class<? extends ToolAction<?>> toolClass) {
        slotHelper.execute(toolClass);
    }

    /**
     * Executes the specified action.
     * 
     * @param action
     *            the action to execute.
     */
    public void execute(ToolAction<T> action) {
        slotHelper.execute(action);
    }

    /**
     * Updates the references to the currently active view, graph and session.
     * 
     * @param view
     *            the current view.
     * @see Graph
     * @see Session
     */
    protected void setView(T view) {
        this.view = view;
        session = view.getEditorSession();
        graph = session.getGraph();

    }

    /**
     * Sets the performed user gesture.
     * 
     * @param userGesture
     *            the gesture that was performed by the user.
     */
    public void setUserGesture(UserGesture userGesture) {
        this.userGesture = userGesture;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
