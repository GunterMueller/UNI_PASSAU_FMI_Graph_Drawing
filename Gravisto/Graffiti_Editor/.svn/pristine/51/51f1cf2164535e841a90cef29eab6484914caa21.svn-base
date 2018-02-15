package org.graffiti.plugins.scripting;

import org.graffiti.plugin.view.interactive.GestureFeedbackProvider;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugins.scripting.delegates.ConsoleDelegate;

/**
 * Session providing access to a view.
 */
public class ViewScope<T extends InteractiveView<T>> extends Scope {
    /**
     * The view.
     */
    private InteractiveView<T> view;

    /**
     * Constructs a new view scope providing access to the specified view and
     * with the specified parent.
     * 
     * @param sessionScope
     *            the parent of the scope to construct.
     * @param view
     *            the view to access from the scope to construct.
     */
    ViewScope(SessionScope sessionScope, InteractiveView<T> view) {
        super(sessionScope);
        this.view = view;

        GestureFeedbackProvider gfp = view.getGestureFeedbackProvider();
        if (gfp instanceof ConsoleProvider) {
            consoleProvider = (ConsoleProvider) gfp;
            putConst("console", new ConsoleDelegate(this, consoleProvider));
        }
        seal();
    }

    /**
     * Returns the gesture feedback provider of the accessed view.
     * 
     * @return the gesture feedback provider of the accessed view or {@code
     *         null} if the view does not provide a console.
     */
    public ConsoleProvider getConsoleProvider() {
        return consoleProvider;
    }

    /**
     * Returns the view.
     * 
     * @return the view.
     */
    public InteractiveView<T> getView() {
        return view;
    }
}
