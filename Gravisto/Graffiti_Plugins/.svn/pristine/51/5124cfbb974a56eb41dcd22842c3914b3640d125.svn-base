package org.graffiti.editor;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import org.graffiti.core.Bundle;
import org.graffiti.editor.actions.RunAlgorithm;
import org.graffiti.editor.dialog.DefaultParameterDialog;
import org.graffiti.editor.dialog.ParameterDialog;
import org.graffiti.graph.Graph;
import org.graffiti.managers.pluginmgr.DefaultPluginEntry;
import org.graffiti.managers.pluginmgr.Entry;
import org.graffiti.plugin.Parametrizable;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.AlgorithmWithContextMenu;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.EditorAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.view.MessageListener;
import org.graffiti.selection.Selection;
import org.graffiti.session.Session;

/**
 * Provides access to global variables, needed for various extensions to
 * Graffiti. Plugins can use the Preferences structure to save settings.
 */
public class GraffitiSingleton {

    /**
     * DOCUMENT ME!
     */
    private static GraffitiSingleton instance;

    /**
     * DOCUMENT ME!
     */
    private volatile boolean plugins_MoveSelectionsAllowed = true;

    /**
     * DOCUMENT ME!
     */
    public volatile Object selectionSyncObject = new Object();

    /**
     * A list of frames, which are used by the pattern editor.
     */
    private List<JInternalFrame> frames;

    private static LinkedList<String> errorMessages = new LinkedList<String>();

    private static final Bundle BUNDLE;

    /**
     * Gravisto's copyright notice.
     */
    public static final String COPYRIGHT_NOTICE;

    static {
        BUNDLE = Bundle.getCoreBundle();
        COPYRIGHT_NOTICE = String.format(BUNDLE
                .getString("splashScreen.copyright"), Calendar.getInstance()
                .get(Calendar.YEAR));
    }

    /**
     * DOCUMENT ME!
     */
    private List<Session> patternSessions;

    /**
     * Stores the <code>MainFrame</code> object reference.
     */
    private MainFrame mainFrame;

    /**
     * Returns the single instance of this class.
     * 
     * @return The single instance of this "Singleton".
     */
    public static synchronized GraffitiSingleton getInstance() {
        if (instance == null) {
            instance = new GraffitiSingleton();
        }

        return instance;
    }

    /**
     * A global variable, for communication between the IPK Editing Tools and
     * some IPK Layouter. This will be later eventually be removed.
     * 
     * @return If value is true, the selection should not be moved by the
     *         layouter algorithms.
     */
    public synchronized boolean pluginSelectionMoveAllowed() {
        return plugins_MoveSelectionsAllowed;
    }

    /**
     * A global variable, for communication between the IPK Editing Tools and
     * some IPK Layouter. This will be later eventually be removed.
     * 
     * @param value
     *            set to true, if the selection should not be moved by the
     *            layouter algorithms.
     */
    public synchronized void pluginSetMoveAllowed(boolean value) {
        plugins_MoveSelectionsAllowed = value;
    }

    /**
     * Memorize the main frame.
     * 
     * @param mainFrame
     *            The main frame.
     */
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    /**
     * Returns a <code>Vector</code> which contains a list of sessions, loaded
     * in the main view.
     * 
     * @return <code>Vector</code> with elements of the type
     *         <code>EditorSession</code>. Returns empty Vector, if no sessions
     *         are loaded.
     */
    public Vector<Session> getMainSessions() {
        Vector<Session> result = new Vector<Session>();

        Set<Session> sessions = getMainFrame().getSessions();

        for (Session s : sessions)
            if ((patternSessions == null) || (patternSessions.indexOf(s) < 0)) {
                result.add(s);
            }

        return result;
    }

    /**
     * A <code>List</code> of the pattern sessions. PatternSessions are
     * sessions, which are loaded in the pattern tab.
     * 
     * @return The pattern sessions.
     */
    public List<Session> getPatternSessionList() {
        return patternSessions;
    }

    /**
     * Returns the main frame (application window).
     * 
     * @return The main frame.
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Returns a <code>Vector</code> which contains a list of graphs from the
     * main view.
     * 
     * @return <code>Vector</code> with elements of the type <code>Graph</code>.
     */
    public Vector<Graph> getMainGraphs() {
        Vector<Graph> result = new Vector<Graph>();

        for (Iterator<Session> it = mainFrame.getSessionsIterator(); it
                .hasNext();) {
            Session curS = it.next();

            if ((patternSessions == null)
                    || (patternSessions.indexOf(curS) < 0)) {
                result.add(curS.getGraph());
            }
        }

        return result;
    }

    /**
     * Returns a <code>Vector</code> which contains a list of pattern graphs.
     * 
     * @return <code>Vector</code> with elements of the type <code>Graph</code>.
     *         If no patterns are loaded or available, this method returns an
     *         empty <code>Vector</code>.
     */
    public Vector<Graph> getPatternGraphs() {
        Vector<Graph> result = new Vector<Graph>();
        if (patternSessions != null) {
            for (int i = 0; i < patternSessions.size(); i++) {
                if (patternSessions.get(i) != null) {
                    result.add(patternSessions.get(i).getGraph());
                }
            }
        }
        return result;
    }

    /**
     * Adds a new internal frame to the list of pattern editor frames. Can be
     * used by the method <code>isEditorFrameSelected</code> for the decission,
     * whether a given frame is a editor frame or a pattern editor frame.
     * 
     * @param frame
     *            New pattern editor frame.
     */
    public void addFrame(GraffitiInternalFrame frame) {
        if (frames == null) {
            frames = new ArrayList<JInternalFrame>();
        }

        frames.add(frame);
    }

    /**
     * Adds a Session to the list of patternSessions. This method is called by
     * the patternInspector in the action handler for the load and new button
     * action.
     * 
     * @param session
     *            The new session, which should be known as a session,
     *            containing a pattern graph.
     */
    public void addPatternSession(Session session) {
        if (patternSessions == null) {
            patternSessions = new ArrayList<Session>();
        }

        patternSessions.add(session);
    }

    /**
     * Checks if an editor frame in the main view is selected.
     * 
     * @return True, if an editor frame is selected.
     */
    public boolean isEditorFrameSelected() {
        boolean result = false;

        if (frames != null) {
            for (JInternalFrame frame : frames) {
                // TODO check: a saved frame should never be null!
                if (frame != null) {
                    if (frame instanceof GraffitiInternalFrame) {
                        if (frame.isSelected()) {
                            result = true;
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Unselects all editor frames in the main view.
     */
    public void framesDeselect() {
        if (frames != null) {
            for (JInternalFrame frame : frames) {
                // TODO check: a saved frame should never be null!
                if (frame != null) {
                    try {
                        if (frame instanceof GraffitiInternalFrame) {
                            frame.setSelected(false);
                        }
                    } catch (PropertyVetoException e) {
                        // ignore, no problem
                    }
                }
            }
        }
    }

    /**
     * Returns a algorithm instance, defined by its name (e.g. menu item text)
     * 
     * @param name
     *            The menu item text.
     * 
     * @return The algorithm instance.
     */
    public Algorithm getAlgorithmInstanceFromFriendlyName(String name) {
        Collection<Entry> plugins = GraffitiSingleton.getInstance()
                .getMainFrame().getPluginManager().getPluginEntries();

        for (Entry entry : plugins) {
            DefaultPluginEntry curPluginEntry = (DefaultPluginEntry) entry;
            Algorithm[] myAlgos = curPluginEntry.getPlugin().getAlgorithms();

            if (myAlgos.length > 0) {
                for (Algorithm alg : myAlgos) {
                    if (alg instanceof AlgorithmWithContextMenu
                            && alg instanceof RunAlgorithm) {
                        AlgorithmWithContextMenu acm = (AlgorithmWithContextMenu) alg;

                        if (acm.getCurrentContextMenuItem().toString()
                                .equalsIgnoreCase(name)
                                || alg.getName().equalsIgnoreCase(name))
                            return alg;
                    }
                    Algorithm algo = alg;

                    if (algo.getName() != null
                            && algo.getName().equalsIgnoreCase(name))
                        return alg;
                }
            }
        }

        return null;
    }

    /**
     * Returns a plugin instance, given by its classname.
     * 
     * @param pluginDescription
     * @return The plugin instance, if the plugin is loaded.
     */
    public DefaultPluginEntry getPluginInstanceFromPluginDescription(
            String pluginDescription) {
        Collection<Entry> plugins = GraffitiSingleton.getInstance()
                .getMainFrame().getPluginManager().getPluginEntries();

        for (Entry e : plugins) {
            if (e.getDescription().getName().toUpperCase().indexOf(
                    pluginDescription.toUpperCase()) >= 0)
                return (DefaultPluginEntry) e;
        }

        return null;
    }

    /**
     * Starts a plugin and returns, as soon as the plugin execution has
     * finished.
     * 
     * @param pluginNameOrClassName
     *            of Algorithm to execute or Menu Item Text (from PluginMenu or
     *            Context Menu) or Classname of Plugin.
     * @param g
     *            Graph instance the plugin should work with.
     * 
     * @return Empty String if success, otherwise Error message
     */
    public String runPlugin(final String pluginNameOrClassName, final Graph g) {

        class Exec {
            public void myRun(GraffitiSingleton gs) {
                Algorithm algo = gs
                        .getAlgorithmInstanceFromFriendlyName(pluginNameOrClassName);

                gs.getMainFrame().showMesssage(
                        "Execute plugin " + pluginNameOrClassName + "",
                        MessageListener.INFO);
                runAlgorithm(algo);
            }
        }
        (new Exec()).myRun(this);

        /*
         * try { if (SwingUtilities.isEventDispatchThread()) { (new
         * Exec()).myRun(this); } else { SwingUtilities.invokeAndWait(new
         * Runnable() { public void run() { (new
         * Exec()).myRun(GraffitiSingleton.getInstance()); } }); } } catch
         * (Exception e) { return e.getLocalizedMessage(); }
         */

        return null;
    }

    public static boolean showParameterDialog(Parametrizable parametrizable) {
        Parameter<?>[] parameters = parametrizable.getParameters();
        ParameterDialog paramDialog = null;

        Selection activeSel;
        try {
            activeSel = getInstance().getMainFrame().getActiveEditorSession()
                    .getSelectionModel().getActiveSelection();
        } catch (NullPointerException e) {
            activeSel = new Selection();
        }
        ;

        int parametersWithoutSelectionParameters = 0;
        if (parameters != null) {
            for (int pos = 0; pos < parameters.length; pos++) {
                if (parameters[pos] instanceof SelectionParameter) {
                    // use currently active (given) selection instead
                    SelectionParameter sel = (SelectionParameter) parameters[pos];
                    parameters[pos] = new SelectionParameter(sel.getName(), sel
                            .getDescription(), activeSel);
                } else {
                    parametersWithoutSelectionParameters++;
                }
            }
        }

        if ((parameters != null) && (parametersWithoutSelectionParameters > 0)) {
            if (parametrizable instanceof EditorAlgorithm) {
                paramDialog = ((EditorAlgorithm) parametrizable)
                        .getParameterDialog(activeSel);
            }

            if (paramDialog == null) {
                paramDialog = new DefaultParameterDialog(getInstance()
                        .getMainFrame().getEditComponentManager(),
                        getInstance().getMainFrame(), parametrizable, activeSel);
            }

            // TODO load and save the preferences for this algorithm
            // TODO validate edited values
            if (!paramDialog.isOkSelected())
                return false;
        }
        if (parameters == null && parametrizable instanceof EditorAlgorithm) {
            /*
             * Selection activeSel =
             * getInstance().getMainFrame().getActiveEditorSession()
             * .getSelectionModel() .getActiveSelection();
             */
            paramDialog = ((EditorAlgorithm) parametrizable)
                    .getParameterDialog(activeSel);
        }
        Parameter<?>[] params = (paramDialog == null) ? parameters
                : paramDialog.getEditedParameters();
        parametrizable.setParameters(params);
        return true;
    }

    public static void runAlgorithm(Algorithm algorithm) {
        boolean okSelected = showParameterDialog(algorithm);
        if (!okSelected)
            return;

        algorithm.attach(getInstance().getMainFrame().getActiveSession()
                .getGraph());

        try {
            algorithm.check();
            Session s = activeSession();
            s.setActiveAlgorithm(algorithm);
            mainFrame().fireSessionDataChanged(s);
            algorithm.execute();

            if (algorithm instanceof CalculatingAlgorithm) {
                AlgorithmResult result = ((CalculatingAlgorithm) algorithm)
                        .getResult();
                Object[] comps = result.getComponentsForJDialog();
                if (comps != null) {
                    JOptionPane.showMessageDialog(getInstance().getMainFrame(),
                            comps, "Result", JOptionPane.INFORMATION_MESSAGE);

                } else {
                    JOptionPane.showMessageDialog(getInstance().getMainFrame(),
                            result.toString(), "Result",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (PreconditionException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "Precondition(s) failed", JOptionPane.ERROR_MESSAGE);
        }

        getInstance().getMainFrame().getActiveEditorSession()
                .getSelectionModel().selectionChanged();

        // ensure the algorithm is reset
        algorithm.reset();
    }

    /**
     * 
     */
    private static Session activeSession() {
        return mainFrame().getActiveSession();
    }

    private static MainFrame mainFrame() {
        if (getInstance().mainFrame == null)
            throw new IllegalStateException();
        return getInstance().mainFrame;
    }

    /**
     * Write an error message to the console and add an entry to the logger.
     * 
     * @param logger
     * @param errorMessage
     */
    public void reportError(Logger logger, String errorMessage) {
        System.err.println(errorMessage);
        logger.log(Level.SEVERE, errorMessage);
        // setStatusText(errorMessage, 3);
    }

    /**
     * Adds a errorMessage to a global list. The error messages can be retreived
     * with <code>getErrorMessages</code> and cleared with
     * <code>clearErrorMessages</code>.
     * 
     * @param errorMsg
     */
    public void addErrorMessage(String errorMsg) {
        synchronized (errorMessages) {
            errorMessages.add(errorMsg);
        }
    }

    /**
     * Removes the current error messages. E.g. after showing them to the user.
     */
    public void clearErrorMessages() {
        synchronized (errorMessages) {
            errorMessages.clear();
        }
    }

    /**
     * Returns pending error messages that were not shown to the user
     * immediatly.
     * 
     * @return Pending Error Messages
     */
    public String[] getErrorMessages() {
        synchronized (errorMessages) {
            String[] result = new String[errorMessages.size()];
            int i = 0;
            for (String msg : errorMessages) {
                result[i++] = msg;
            }
            return result;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
