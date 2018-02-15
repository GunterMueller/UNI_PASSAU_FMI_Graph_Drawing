package org.graffiti.plugin.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.graffiti.event.AbstractGraphListener;
import org.graffiti.event.AttributeEvent;
import org.graffiti.event.AttributeListener;
import org.graffiti.event.GraphEvent;
import org.graffiti.event.GraphListener;
import org.graffiti.event.TransactionEvent;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.animation.Animation;
import org.graffiti.plugin.algorithm.animation.Animation.GraphModificationPolicy;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;
import org.graffiti.session.SessionManager;

/**
 * The five-button panel that appears as soon as an animated version of an
 * algorithm is chosen.
 * 
 * @author Harald Frankenberger
 * @version $Revision$ $Date$
 */
public class AnimationPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -9203670587401085765L;
    /**
     * The session manager used to obtain notifications of session changes and
     * session modifications.
     */
    private SessionManager sessionManager = null;

    /**
     * Default constructor; creates an empty panel.
     * <p>
     * Note that as long the session manager of this panel is not assigned a non
     * null value, this panel stays empty. Buttons are added to this panel only
     * if a session has an active animation.
     * 
     * @see org.graffiti.session.SessionManager
     * @see org.graffiti.session.Session
     * @see org.graffiti.session.Session#hasActiveAnimation()
     * @see #setSessionManager(SessionManager)
     * 
     */
    public AnimationPanel() {
    }

    /**
     * Initializes this panel with the specified <tt>SessionManager</tt>.
     * 
     * @param s
     *            the session manager used to obtain notifications of session
     *            changes and session modifications
     */
    public AnimationPanel(SessionManager s) {
        s.addSessionListener(new PanelUpdate());
        sessionManager = s;
    }

    /**
     * Sets this panel's session manager to the specified value.
     * 
     * @param s
     *            the session manager used to obtain information about session
     *            changes and session modifiactions.
     */
    public void setSessionManager(SessionManager s) {
        s.addSessionListener(new PanelUpdate());
        sessionManager = s;
    }

    /**
     * Returns Gravisto's active session.
     * 
     * @return Gravisto's active session.
     */
    private Session getActiveSession() {
        /*
         * Calls to this method are always initially triggered by notifications
         * of this panel's session listener class. So this panel's session
         * manager must've already been initialized.
         */
        assert sessionManager != null;
        return sessionManager.getActiveSession();
    }

    /**
     * Returns Gravisto's active animation.
     * 
     * @return Gravisto's active animation.
     */
    private Animation getActiveAnimation() {
        return getActiveSession().getActiveAnimation();
    }

    /**
     * Adds buttons to this panel that allow navigating an animation.
     * <p>
     * This implementation proceeds as follows: First it adds a new button
     * configured with <tt>ToStartAction</tt> to this panel. If Gravisto's
     * active animation supports steps to previous states it adds a new button
     * configured with <tt>PreviousStepAction</tt> to this panel. Then it adds
     * two buttons configured with <tt>NextStepAction</tt> and
     * <tt>ToEndAction</tt> to this panel.
     * 
     * @see javax.swing.JButton
     * @see AnimationPanel.ToStartAction
     * @see AnimationPanel.PreviousStepAction
     * @see AnimationPanel.NextStepAction
     * @see AnimationPanel.ToEndAction
     */
    private void addButtons() {
        add(new JButton(new ExceptionHandlingAction(new ToStartAction())));
        add(new JButton(new ExceptionHandlingAction(new PreviousStepAction())));
        add(new JButton(new ExceptionHandlingAction(new NextStepAction())));
        add(new JButton(new ExceptionHandlingAction(new ToEndAction())));
        add(new JButton(new ExceptionHandlingAction(new SkipAnimationAction())));
    }

    /**
     * Updates this panel.
     * <p>
     * This implementation iterates over the components of this panel, assuming
     * all components are <tt>javax.swing.JButton</tt>s. Every button's enabled
     * status is set to its corresponding animation action's enabled status.
     * 
     * @see javax.swing.JButton
     * @see javax.swing.JButton#setEnabled(boolean)
     * @see AnimationPanel.ToStartAction#isEnabled()
     * @see AnimationPanel.PreviousStepAction#isEnabled()
     * @see AnimationPanel.NextStepAction#isEnabled()
     * @see AnimationPanel.ToEndAction#isEnabled()
     */
    private void updatePanel() {
        for (Component button : getComponents()) {
            button.setEnabled(((JButton) button).getAction().isEnabled());
        }
    }

    /**
     * Returns the currently active graph modification policy.
     * 
     * @see org.graffiti.plugin.algorithm.animation.Animation#getGraphModificationPolicy()
     * 
     * @return the currently active graph modification policy
     */
    private GraphModificationPolicy getActiveGraphModificationPolicy() {
        return getActiveAnimation().getGraphModificationPolicy();
    }

    /**
     * Returns <tt>true</tt> if the currently active session specifies a graph
     * modification policy. In other words: returns <tt>true</tt> if the
     * currently active session has an active animation.
     * 
     * @see org.graffiti.session.Session#hasActiveAnimation()
     * @see org.graffiti.plugin.algorithm.animation.Animation.GraphModificationPolicy
     * 
     * @return <tt>true</tt> if the currently active session specifies a graph
     *         modification policy.
     */
    private boolean graphModificationPolicySpecified() {
        return getActiveSession().hasActiveAnimation();
    }

    //
    // Action classes
    //

    /**
     * Moves an animation to its first step. This action is triggered by the
     * To-start button in the editor's animation panel.
     * 
     * @see AnimationPanel
     * 
     * @author Harald Frankenberger
     * @version $Revision$ $Date$
     */
    private class ToStartAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 3616624234774239139L;

        /**
         * Creates a new action with its name set to "To start".
         * <p>
         * This implementation calls <tt>super("ToStart")</tt>.
         */
        public ToStartAction() {
            super("To start");
        }

        /**
         * Returns <tt>true</tt> if this action is enabled.
         * <p>
         * This action is enabled whenever Gravisto's active animation is ready.
         * 
         * @see org.graffiti.plugin.algorithm.animation.Animation#isReady()
         * 
         * @return <tt>true</tt> if this action is enabled.
         */
        @Override
        public boolean isEnabled() {
            if (!getActiveSession().hasActiveAnimation())
                return false;
            Animation a = getActiveAnimation();
            return a.isReady()
                    && ((a.supportsClear() && !a.isCleared()) || a
                            .supportsPreviousStep()
                            && a.hasPreviousStep());
        }

        /**
         * Moves Gravisto's active animation to its first step.
         * <p>
         * Let <tt>a</tt> be Gravisto's active animation. This implementation
         * first checks whether <tt>a</tt> supports steps to previous states. If
         * so it iterates backwards to the active animation's first step.
         * Otherwise <tt>a</tt> is cleared. Finally the editor's animation panel
         * is updated.
         * 
         * @see org.graffiti.plugin.algorithm.animation.Animation#supportsPreviousStep()
         * @see org.graffiti.plugin.algorithm.animation.Animation#hasPreviousStep()
         * @see org.graffiti.plugin.algorithm.animation.Animation#previousStep()
         * @see org.graffiti.plugin.algorithm.animation.Animation#clear()
         * 
         * @param e
         *            the action event that triggered this action.
         */
        public void actionPerformed(ActionEvent e) {
            Animation a = getActiveAnimation();
            if (a.supportsPreviousStep()) {
                while (a.hasPreviousStep()) {
                    a.previousStep();
                }
            } else {
                a.clear();
            }
            AnimationPanel.this.updatePanel();
        }

    }

    /**
     * Moves an animation one step forward. This action is triggered by the
     * next-step button in the editor's animation panel.
     * 
     * @see AnimationPanel
     * 
     * @author Harald Frankenberger
     * @version $Revision$ $Date$
     */
    private class NextStepAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = -1059027735131726028L;

        /**
         * Creates a new animation action with its name set to "Next step".
         * <p>
         * This implementation calls <tt>super("Next step")</tt>.
         * 
         */
        public NextStepAction() {
            super("Next step");
        }

        /**
         * Returns <tt>true</tt> if this action is enabled.
         * <p>
         * This action is enabled whenever Gravisto's active animation is ready
         * and has a next step.
         * 
         * @see org.graffiti.plugin.algorithm.animation.Animation#isReady()
         * @see org.graffiti.plugin.algorithm.animation.Animation#hasNextStep()
         * 
         * @return <tt>true</tt> if this action is enabled.
         */
        @Override
        public boolean isEnabled() {
            if (!getActiveSession().hasActiveAnimation())
                return false;
            Animation a = getActiveAnimation();
            return a.isReady() && a.hasNextStep();
        }

        /**
         * Moves the currently active animation one step forward and updates the
         * editor's animation panel.
         * 
         * @see org.graffiti.plugin.algorithm.animation.Animation#nextStep()
         */
        public void actionPerformed(ActionEvent e) {
            getActiveAnimation().nextStep();
            updatePanel();
        }
    }

    /**
     * Moves an animation one step backwards. This action is triggered by the
     * previous-step button in the editor's animation panel.
     * 
     * @see AnimationPanel
     * 
     * @author Harald Frankenberger
     * @version $Revision$ $Date$
     */
    private class PreviousStepAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 7839746877514879473L;

        public PreviousStepAction() {
            super("Previous step");
        }

        /**
         * Returns <tt>true</tt> if this action is enabled.
         * <p>
         * This action is enabled whenever Gravisto's active animation is ready,
         * supports steps to previous states and has a previous step.
         * 
         * @see AnimationPanel#getActiveAnimation()
         * @see org.graffiti.plugin.algorithm.animation.Animation#isReady()
         * @see org.graffiti.plugin.algorithm.animation.Animation#supportsPreviousStep()
         * @see org.graffiti.plugin.algorithm.animation.Animation#hasPreviousStep()
         * 
         * @return <tt>true</tt> if this action is enabled.
         */
        @Override
        public boolean isEnabled() {
            if (!getActiveSession().hasActiveAnimation())
                return false;
            Animation a = getActiveAnimation();
            return a.isReady() && a.supportsPreviousStep()
                    && a.hasPreviousStep();
        }

        /**
         * Moves the currently active animation one step backwards and updates
         * the editor's animation panel.
         * 
         * @see org.graffiti.plugin.algorithm.animation.Animation#previousStep()
         */
        public void actionPerformed(ActionEvent e) {
            getActiveAnimation().previousStep();
            updatePanel();
        }
    }

    /**
     * Moves an animation to its last step. This action is triggered by the
     * to-end button in the editor's animation panel.
     * 
     * @see AnimationPanel
     * 
     * @author Harald Frankenberger
     * @version $Revision$ $Date$
     */
    private class ToEndAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 5437397830177612153L;

        public ToEndAction() {
            super("To end");
        }

        /**
         * Returns <tt>true</tt> if this action is enabled.
         * <p>
         * This action is enabled whenever Gravisto's active animation is ready.
         * 
         * @see AnimationPanel#getActiveAnimation()
         * @see org.graffiti.plugin.algorithm.animation.Animation#isReady()
         * 
         * @return <tt>true</tt> if this action is enabled.
         */
        @Override
        public boolean isEnabled() {
            if (!getActiveSession().hasActiveAnimation())
                return false;
            Animation a = getActiveAnimation();
            return a.isReady() && a.hasNextStep();
        }

        /**
         * Moves the currently active animation to its last state and updates
         * the editor's animation panel.
         * <p>
         * Let a be the currently active animation. This implementation executes
         * <tt>a.nextStep</tt> as long as <tt>a.hasNextStep</tt> returns
         * <tt>true</tt>.
         * 
         * @see org.graffiti.plugin.algorithm.animation.Animation#hasNextStep()
         * @see org.graffiti.plugin.algorithm.animation.Animation#nextStep()
         */
        public void actionPerformed(ActionEvent e) {
            Animation a = getActiveAnimation();
            while (a.hasNextStep()) {
                a.nextStep();
            }
            updatePanel();
        }
    }

    /**
     * Skips the current animation and switches to the previous active animation
     * in this session (if there is one).
     * 
     * @author Harald Frankenberger
     * @version $Revision$ $Date$
     */
    private class SkipAnimationAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -4013882512713673737L;

        public SkipAnimationAction() {
            super("Skip animation");
        }

        /**
         * Returns <tt>true</tt> if this action is enabled.
         * <p>
         * This action is enabled, if the currently active session has an active
         * animation.
         * 
         * @return <tt>true</tt> if this action is enabled.
         */
        @Override
        public boolean isEnabled() {
            return getActiveSession().hasActiveAnimation();
        }

        /**
         * Skips the current animation and switches to the previous one that was
         * active in the current session.
         */
        public void actionPerformed(ActionEvent e) {
            if (getActiveAnimation().hasNextStep()) {
                int selected = JOptionPane.showConfirmDialog(null,
                        "This will leave the animation in an unfinished state.\n"
                                + "Do you want to proceed?", "Confirm to skip",
                        JOptionPane.YES_NO_OPTION);
                if (selected == JOptionPane.NO_OPTION)
                    return;
            }
            getActiveSession().discardActiveAnimation();
            sessionManager.fireSessionDataChanged(getActiveSession());
            updatePanel();
        }
    }

    //
    // Listener classes
    //

    /**
     * Updates the editor's animation panel when sessions change or a session's
     * active algorithm changes.
     * 
     * @see AnimationPanel
     * @see org.graffiti.session.Session
     * @author Harald Frankenberger
     * @version $Revision$ $Date$
     */
    private class PanelUpdate implements SessionListener {
        /**
         * This method is called when the session changes.
         * <p>
         * This implementation first clears the editor's animation panel. If the
         * specified session is not <tt>null</tt>, has an active algorithm and
         * an active animation it proceeds as follows:
         * <ul>
         * <li>The session's graph is configured with a graph listener that
         * triggers the active animation's graph modification policy.
         * <li>The appropriate buttons are added to the editor's animation
         * panel.
         * </ul>
         * 
         * @see AnimationPanel
         * @see Session#hasActiveAnimation()
         * @see org.graffiti.event.GraphListener
         * @see org.graffiti.plugin.algorithm.animation.Animation.GraphModificationPolicy
         */
        public void sessionChanged(Session s) {
            removeAll();
            if (s != null) {
                if (s.hasStartedNewAnimation()) {
                    s.approveStartOfNewAnimation();
                    addGraphModificationPolicyWrapper(s.getGraph());
                }
                if (s.hasActiveAnimation()) {
                    addButtons();
                }
            }
            revalidate();
        }

        /**
         * Ensures the specified graph is observed by a graph modification
         * policy wrapper.
         * <p>
         * This implementation first tries to add the the specified graph to the
         * set of observed graphs. It this succeeds, it adds a new graph
         * modification policy wrapper to the specified graph and returns
         * <tt>true</tt>. Otherwise, it returns false.
         * 
         * @param g
         *            the graph to be observed by a graph modification policy
         *            wrapper.
         */
        private void addGraphModificationPolicyWrapper(Graph g) {
            GraphModificationPolicyWrapper w = new GraphModificationPolicyWrapper();
            g.getListenerManager().addNonstrictGraphListener(w);
            g.getListenerManager().addNonstrictAttributeListener(w);
        }

        /**
         * This method is called when the data (except the graph data) are
         * changed. Ensures that the editor's animation panel is updated every
         * time a new algorithm is selected.
         * <p>
         * First this implementation removes all buttons from the animation
         * panel. If the specified session has an active algorithm and an active
         * animation it re-adds the appropriate buttons to the animation panel.
         * Finally the animation panel is revalidated.
         * 
         * @see AnimationPanel#removeAll()
         * @see org.graffiti.session.Session#hasActiveAnimation()
         * @see AnimationPanel#revalidate()
         */
        public void sessionDataChanged(Session s) {
            if (s.hasStartedNewAnimation()) {
                removeAll();
                s.approveStartOfNewAnimation();
                addGraphModificationPolicyWrapper(s.getGraph());
                addButtons();
            }
            revalidate();
        }
    }

    /**
     * Wrapper around a graph modification policy. Forwards all post-prefixed
     * method calls to the corresponding methods in
     * <tt>GraphModificationPolicy</tt>.
     * 
     * @see org.graffiti.plugin.algorithm.animation.Animation.GraphModificationPolicy
     * @author Harald Frankenberger
     * @version $Revision$ $Date$
     */
    private class GraphModificationPolicyWrapper extends AbstractGraphListener
            implements GraphListener, AttributeListener {

        /**
         * Default constructor; does nothing.
         * 
         */
        public GraphModificationPolicyWrapper() {
        }

        /**
         * Called after an edge has been added to the graph.
         * <p>
         * If the currently active session specifies a graph modification policy
         * it is notified of the edge having been added. Then the editor's
         * animation panel is updated. Otherwise this implementation does
         * nothing.
         * 
         * @see AnimationPanel#graphModificationPolicySpecified()
         * @see AnimationPanel#getActiveGraphModificationPolicy()
         * @see org.graffiti.plugin.algorithm.animation.Animation.GraphModificationPolicy#edgeAdded(org.graffiti.graph.Edge)
         * 
         * @param e
         *            the GraphEvent detailing the changes.
         */
        @Override
        public void postEdgeAdded(GraphEvent e) {
            if (graphModificationPolicySpecified()) {
                getActiveGraphModificationPolicy().edgeAdded(e.getEdge());
                updatePanel();
            }
        }

        /**
         * Called after an edge has been removed from the graph.
         * <p>
         * If the currently active session specifies a graph modification policy
         * it is notified of the edge having been removed. Then the editor's
         * animation panel is updated. Otherwise this implementation does
         * nothing.
         * 
         * @see AnimationPanel#graphModificationPolicySpecified()
         * @see AnimationPanel#getActiveGraphModificationPolicy()
         * @see org.graffiti.plugin.algorithm.animation.Animation.GraphModificationPolicy#edgeRemoved(org.graffiti.graph.Edge)
         * 
         * @param e
         *            the GraphEvent detailing the changes.
         */
        @Override
        public void postEdgeRemoved(GraphEvent e) {
            if (graphModificationPolicySpecified()) {
                getActiveGraphModificationPolicy().edgeRemoved(e.getEdge());
                updatePanel();
            }
        }

        /**
         * Called after method <code>clear()</code> has been called on a graph.
         * No other events (like remove events) are generated.
         * <p>
         * If the currently active session specifies a graph modification policy
         * it is notified of the graph having been cleared. Then the editor's
         * animation panel is updated. Otherwise this implementation does
         * nothing.
         * 
         * @see AnimationPanel#graphModificationPolicySpecified()
         * @see AnimationPanel#getActiveGraphModificationPolicy()
         * @see org.graffiti.plugin.algorithm.animation.Animation.GraphModificationPolicy#graphCleared(Graph)
         * 
         * @param e
         *            the GraphEvent detailing the changes.
         */
        @Override
        public void postGraphCleared(GraphEvent e) {
            if (graphModificationPolicySpecified()) {
                getActiveGraphModificationPolicy().graphCleared(e.getGraph());
                updatePanel();
            }
        }

        /**
         * Called after a node has been added to the graph.
         * <p>
         * If the currently active session specifies a graph modification policy
         * it is notified of the node having been added. Then the editor's
         * animation panel is updated. Otherwise this implementation does
         * nothing.
         * 
         * @see AnimationPanel#graphModificationPolicySpecified()
         * @see AnimationPanel#getActiveGraphModificationPolicy()
         * @see org.graffiti.plugin.algorithm.animation.Animation.GraphModificationPolicy#nodeAdded(org.graffiti.graph.Node)
         * 
         * @param e
         *            the GraphEvent detailing the changes.
         */
        @Override
        public void postNodeAdded(GraphEvent e) {
            if (graphModificationPolicySpecified()) {
                getActiveGraphModificationPolicy().nodeAdded(e.getNode());
                updatePanel();
            }
        }

        /**
         * Called after node has been removed to the graph.
         * <p>
         * If the currently active session specifies a graph modification policy
         * it is notified of the node having been removed. Then the editor's
         * animation panel is updated. Otherwise this implementation does
         * nothing.
         * 
         * @see AnimationPanel#graphModificationPolicySpecified()
         * @see AnimationPanel#getActiveGraphModificationPolicy()
         * @see org.graffiti.plugin.algorithm.animation.Animation.GraphModificationPolicy#nodeRemoved(org.graffiti.graph.Node)
         * 
         * @param e
         *            the GraphEvent detailing the changes.
         */
        @Override
        public void postNodeRemoved(GraphEvent e) {
            if (graphModificationPolicySpecified()) {
                getActiveGraphModificationPolicy().nodeRemoved(e.getNode());
                updatePanel();
            }
        }

        private void objectModified(Object o) {
            assert o != null : "Invalid value: null";
            if (o instanceof Graph) {
                getActiveGraphModificationPolicy().graphModified((Graph) o);
            }
            if (o instanceof Node) {
                getActiveGraphModificationPolicy().nodeModified((Node) o);
            }
            if (o instanceof Edge) {
                getActiveGraphModificationPolicy().edgeModified((Edge) o);
            }
            // Ignore all other objects.
            updatePanel();
        }

        public void postAttributeAdded(AttributeEvent e) {
            if (graphModificationPolicySpecified()) {
                objectModified(e.getAttribute().getAttributable());
            }
        }

        public void postAttributeChanged(AttributeEvent e) {
            if (graphModificationPolicySpecified()) {
                objectModified(e.getAttribute().getAttributable());
            }
        }

        /*
         * @see
         * org.graffiti.event.AttributeListener#postAttributeRemoved(org.graffiti
         * .event.AttributeEvent)
         */
        public void postAttributeRemoved(AttributeEvent e) {
            if (graphModificationPolicySpecified()) {
                objectModified(e.getAttribute().getAttributable());
            }
        }

        @Override
        public void transactionFinished(TransactionEvent e) {
            if (graphModificationPolicySpecified()) {
                for (Object o : e.getChangedObjects()) {
                    objectModified(o);
                }
            }
        }

        /*
         * @see
         * org.graffiti.event.AttributeListener#preAttributeAdded(org.graffiti
         * .event.AttributeEvent)
         */
        public void preAttributeAdded(AttributeEvent e) {
        }

        /*
         * @see
         * org.graffiti.event.AttributeListener#preAttributeChanged(org.graffiti
         * .event.AttributeEvent)
         */
        public void preAttributeChanged(AttributeEvent e) {
        }

        /*
         * @see
         * org.graffiti.event.AttributeListener#preAttributeRemoved(org.graffiti
         * .event.AttributeEvent)
         */
        public void preAttributeRemoved(AttributeEvent e) {
        }
    }

}
