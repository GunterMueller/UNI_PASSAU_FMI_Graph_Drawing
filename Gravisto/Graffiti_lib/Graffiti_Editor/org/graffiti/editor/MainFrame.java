// =============================================================================
//
//   MainFrame.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MainFrame.java 5887 2011-05-03 10:39:41Z gleissner $

package org.graffiti.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEditSupport;

import org.graffiti.core.Bundle;
import org.graffiti.editor.actions.AboutAction;
import org.graffiti.editor.actions.CopyAction;
import org.graffiti.editor.actions.CutAction;
import org.graffiti.editor.actions.DeleteAction;
import org.graffiti.editor.actions.DeleteActionListener;
import org.graffiti.editor.actions.EditRedoAction;
import org.graffiti.editor.actions.EditUndoAction;
import org.graffiti.editor.actions.ExitAction;
import org.graffiti.editor.actions.FileCloseAction;
import org.graffiti.editor.actions.FileNewAction;
import org.graffiti.editor.actions.FileOpenAction;
import org.graffiti.editor.actions.FileSaveAction;
import org.graffiti.editor.actions.FileSaveAllAction;
import org.graffiti.editor.actions.FileSaveAsAction;
import org.graffiti.editor.actions.PasteAction;
import org.graffiti.editor.actions.PluginManagerEditAction;
import org.graffiti.editor.actions.RedrawViewAction;
import org.graffiti.editor.actions.RunAlgorithm;
import org.graffiti.editor.actions.SelectAllAction;
import org.graffiti.editor.actions.SelectGraphElementActionListener;
import org.graffiti.editor.actions.ViewNewAction;
import org.graffiti.editor.actions.cutcopypaste.CutCopyPasteListener;
import org.graffiti.event.ListenerManager;
import org.graffiti.event.ListenerNotFoundException;
import org.graffiti.graph.Graph;
import org.graffiti.managers.AlgorithmManager;
import org.graffiti.managers.AttributeComponentManager;
import org.graffiti.managers.DefaultAlgorithmManager;
import org.graffiti.managers.DefaultIOManager;
import org.graffiti.managers.DefaultViewManager;
import org.graffiti.managers.EditComponentManager;
import org.graffiti.managers.IOManager;
import org.graffiti.managers.ViewManager;
import org.graffiti.managers.ViewportEventDispatcher;
import org.graffiti.managers.pluginmgr.PluginDescription;
import org.graffiti.managers.pluginmgr.PluginManager;
import org.graffiti.managers.pluginmgr.PluginManagerException;
import org.graffiti.managers.pluginmgr.PluginManagerListener;
import org.graffiti.plugin.EditorPlugin;
import org.graffiti.plugin.GenericPlugin;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.editcomponent.NeedEditComponents;
import org.graffiti.plugin.gui.AnimationPanel;
import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugin.gui.GraffitiContainer;
import org.graffiti.plugin.gui.PluginPanel;
import org.graffiti.plugin.gui.ToolToolbar;
import org.graffiti.plugin.io.InputSerializer;
import org.graffiti.plugin.io.OutputSerializer;
import org.graffiti.plugin.tool.ToolRegistry;
import org.graffiti.plugin.view.MessageListener;
import org.graffiti.plugin.view.View;
import org.graffiti.plugin.view.View2D;
import org.graffiti.plugin.view.ViewListener;
import org.graffiti.plugin.view.Zoomable;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionListener;
import org.graffiti.selection.SelectionModel;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;
import org.graffiti.session.SessionManager;
import org.graffiti.util.DesktopMenuManager;
import org.graffiti.util.DisablingMenu;
import org.graffiti.util.MaximizeManager;
import org.graffiti.util.ToolBarLayout;

/**
 * Constructs a new graffiti frame, which contains the main GUI components.
 * 
 * @version $Revision: 5887 $
 */
public class MainFrame extends JFrame implements ComponentListener,
        EditorDefaultValues, IOManager.IOManagerListener, MessageListener,
        PluginManagerListener, SessionManager, SessionListener,
        UndoableEditListener, ViewManager.ViewManagerListener, WindowListener {

    /**
     * 
     */
    private static final long serialVersionUID = -5921785067381722055L;

    /** The size of an internal frame for first displaying. */
    public static final Dimension PREFERRED_INTERNALFRAME_SIZE = new Dimension(
            1000, 1000);

    /** The <code>Bundle</code> of the main frame. */
    private static final Bundle bundle = Bundle.getCoreBundle();

    /**
     * The preferences of the editor's main frame. (e.g.: position and size of
     * the main frame.
     */
    protected Preferences uiPrefs;

    /** The core's preferences containing the localization settings. */
    private Preferences corePrefs = Preferences
            .userNodeForPackage(Bundle.class);

    /** The current active session. */
    EditorSession activeSession;

    /** Holds all active frames. */
    List<GraffitiInternalFrame> activeFrames = new LinkedList<GraffitiInternalFrame>();

    /** The list of registered <code>Zoomable</code>s. */
    List<Zoomable> zoomListener;

    /** Maps from views to internal frames. */
    Map<View, GraffitiInternalFrame> viewFrameMapper;

    /** Contains all <code>Session</code>s. */
    Set<Session> sessions = new HashSet<Session>();

    /** Handles the algorithms. */
    private AlgorithmManager algorithmManager;

    /** Handles the list of attribute components. */
    private AttributeComponentManager attributeComponentManager;

    /** The main frame's static actions */
    private CopyAction editCopy;

    /** The main frame's static actions */
    private CutAction editCut;

    /** The main frame's static actions */
    private DeleteAction editDelete;

    /** Handles the list of value edit components. */
    private EditComponentManager editComponentManager;

    /** The main frame's static actions */
    private GraffitiAction editRedo;

    /** The main frame's static actions */
    private SelectionAction editSelectAll;

    /** The main frame's static actions */
    private GraffitiAction editUndo;

    /** The main frame's static actions */
    private GraffitiAction fileClose;

    /** The main frame's static actions */
    private GraffitiAction fileExit;

    /** The main frame's static actions */
    private GraffitiAction fileOpen;

    /** The main frame's static actions */
    private GraffitiAction fileSave;

    /** The main frame's static actions */
    private GraffitiAction fileSaveAll;

    /** The main frame's static actions */
    private GraffitiAction fileSaveAs;

    /** The main frame's static actions */
    private GraffitiAction newGraph;

    /** The main frame's static actions */
    private GraffitiAction pluginManagerEdit;

    /** The main frame's static actions */
    private GraffitiAction redrawView;

    /** The main frame's static actions */
    private GraffitiAction viewNew;

    /** The main frame's static actions */
    private GraffitiAction about;

    /** Reference to help menu. */
    private JMenu helpMenu;

    /** The listener for the internal frames. */
    private GraffitiInternalFrameListener gift;

    /** The manager for IO serializers. */
    private IOManager ioManager;

    /** The desktop pane for the internal frames. */
    private JDesktopPane desktop;

    /** The main frame's menu entries. */
    private JMenu pluginMenu;

    /** The main frame's options entries. */
    private JMenu optionsMenu;

    /** The main frame's menu entries. */
    private JMenu windowMenu;

    /** Container for toolbars at the top of the main frame. */
    private JPanel leftToolBarPanel;

    /** Container for toolbars at the left of the main frame. */
    private JPanel topToolBarPanel;

    /** The split pane between the center and the pluginPanel. */
    private JSplitPane vertSplitter;

    /** The localization settings. */
    private String localeString;

    /** The look and feel settings. */
    private LookAndFeel lookAndFeel;

    /**
     * The list of algorithm actions.
     * 
     * @see org.graffiti.editor.actions.RunAlgorithm
     */
    private List<GraffitiAction> algorithmActions;

    /** The list of registered <code>SelectionListener</code>s. */
    private List<SelectionListener> selectionListeners;

    /** The list of registered <code>SessionListener</code>s. */
    private List<SessionListener> sessionListeners;

    /**
     * Contains a mapping between the identifiers of GUI-components and the
     * corresponding GUI-component.
     */
    private Map<String, Object> guiMap = new HashMap<String, Object>();

    // The dispatcher of zoom events.
    private ViewportEventDispatcher viewportEventDispatcher;

    /** The main frame's static actions */
    private PasteAction editPaste;

    /** A reference to the graffiti plugin manager. */
    private PluginManager pluginmgr;

    /** The panel for the plugins. */
    private PluginPanel pluginPanel;

    /** The main frame's status bar. */
    private StatusBar statusBar;

    /**
     * The default view type, that will be always displayed if the user
     * deactivates the view chooser dialog. This variable is initialized with
     * null per default. for setting the default view this member variable have
     * to be initialized with a valid view type by the method
     * <code>getDefaultView()</code>.
     */
    private String defaultView = null;

    /** This object is listener of all undoable actions. */
    private UndoableEditSupport undoSupport;

    /** The manager, which maps view type names to view types. */
    private ViewManager viewManager;

    /**
     * Constructs a new <code>MainFrame</code>.
     * 
     * @param pluginmgr
     *            DOCUMENT ME!
     * @param prefs
     *            DOCUMENT ME!
     */
    public MainFrame(PluginManager pluginmgr, Preferences prefs) {
        super();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.setTitle(getDefaultTitle());

        this.sessionListeners = new LinkedList<SessionListener>();
        this.selectionListeners = new LinkedList<SelectionListener>();
        this.viewFrameMapper = new HashMap<View, GraffitiInternalFrame>();
        this.algorithmActions = new LinkedList<GraffitiAction>();
        this.addSessionListener(this);

        viewManager = new DefaultViewManager();
        algorithmManager = new DefaultAlgorithmManager();
        viewportEventDispatcher = new ViewportEventDispatcher();
        ioManager = new DefaultIOManager(prefs);
        attributeComponentManager = new AttributeComponentManager();
        editComponentManager = new EditComponentManager();

        ToolRegistry toolRegistry = ToolRegistry.get();
        addSessionListener(toolRegistry);
        viewManager.addViewListener(toolRegistry);

        pluginmgr.addPluginManagerListener(viewManager);
        pluginmgr.addPluginManagerListener(algorithmManager);
        pluginmgr.addPluginManagerListener(ioManager);
        pluginmgr.addPluginManagerListener(attributeComponentManager);
        pluginmgr.addPluginManagerListener(editComponentManager);

        ioManager.addListener(this);
        viewManager.addListener(this);

        undoSupport = new UndoableEditSupport();

        // undoSupport.addUndoableEditListener(this);
        gift = new GraffitiInternalFrameListener();

        this.pluginmgr = pluginmgr;

        this.uiPrefs = prefs;

        GraffitiSingleton.getInstance().setMainFrame(this);

        createActions();

        // initialize map of GUI components and create menu bar
        guiMap = new Hashtable<String, Object>();

        localeString = corePrefs.get("locale", "null");
        lookAndFeel = LookAndFeel.valueOf(prefs.get("lookAndFeel",
                LookAndFeel.SUN_CROSS_PLATFORM.toString()));

        // create and set the menu bar
        setJMenuBar(createMenuBar());

        // the editor's status bar
        statusBar = new StatusBar();
        addSessionListener(statusBar);
        selectionListeners.add(statusBar);
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        // create the desktop
        desktop = new JDesktopPane();
        new MaximizeManager(desktop, getJMenuBar());
        new DesktopMenuManager(desktop, windowMenu,
                new GraffitiInternalFrameOrder());

        // create a panel, which will contain the views for plugins
        pluginPanel = new PluginPanel();
        guiMap.put(pluginPanel.getId(), pluginPanel);

        vertSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, desktop,
                pluginPanel);

        vertSplitter.setContinuousLayout(true);
        vertSplitter.setOneTouchExpandable(true);
        vertSplitter.setDividerLocation(uiPrefs.getInt("vertSplitter",
                VERT_SPLITTER));

        getContentPane().add(vertSplitter, BorderLayout.CENTER);

        // top toolbars
        topToolBarPanel = new JPanel();
        topToolBarPanel.setLayout(new ToolBarLayout());
        guiMap.put("toolbarPanel", topToolBarPanel);
        getContentPane().add(topToolBarPanel, BorderLayout.NORTH);

        JToolBar toolBar = this.createToolBar();
        guiMap.put("defaultToolbar", toolBar);
        topToolBarPanel.add(toolBar);

        // left toolbars
        leftToolBarPanel = new JPanel();
        leftToolBarPanel.setLayout(new BoxLayout(leftToolBarPanel,
                BoxLayout.Y_AXIS));
        getContentPane().add(leftToolBarPanel, BorderLayout.WEST);

        // window settings like position and size
        setSize(uiPrefs.getInt("sizeWidth", SIZE_WIDTH), uiPrefs.getInt(
                "sizeHeight", SIZE_HEIGHT));

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int xpos = (screenDim.width / 2) - (getWidth() / 2);
        int ypos = (screenDim.height / 2) - (getHeight() / 2);

        setLocation(uiPrefs.getInt("positionX", xpos), uiPrefs.getInt(
                "positionY", ypos));

        // just for debugging - rs
        // System.err.println("entries in the guiMap:");
        // for (Iterator itr = guiMap.keySet().iterator(); itr.hasNext();) {
        // System.err.println(itr.next());
        // }
        // register the main frame to the plugin manager.
        pluginmgr.addPluginManagerListener(this);

        this.addComponentListener(this);

        addWindowListener(this);

        ToolToolbar toolToolbar = toolRegistry.getToolbar();
        guiMap.put(toolToolbar.getId(), toolToolbar);
        leftToolBarPanel.add(toolToolbar);
    }

    /**
     * Get attribute component manager. This is used by some export/import
     * modules.
     * 
     * @return The used attribute component manager.
     */
    public AttributeComponentManager getAttributeComponentManager() {
        return attributeComponentManager;
    }

    /**
     * Get default title for the main frame.
     * 
     * @return The default title.
     */
    protected String getDefaultTitle() {
        return String.format("%s %s %s %s %s", bundle.getString("name"), bundle
                .getString("version"), bundle.getString("version.Release"),
                bundle.getString("version.Major"), bundle
                        .getString("version.Minor"));
    }

    /**
     * Returns the current active editor session.
     * 
     * @return the current active editor session.
     */
    public EditorSession getActiveEditorSession() {
        return activeSession;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public List<GraffitiInternalFrame> getActiveFrames() {
        return activeFrames;
    }

    /**
     * Sets the current active session.
     * 
     * @param s
     *            The session to be activated.
     */
    public void setActiveSession(Session s) {
        activeSession = (EditorSession) s;
        fireSessionChanged(s);
    }

    /**
     * Returns the current active session.
     * 
     * @return the current active session.
     */
    public Session getActiveSession() {
        return activeSession;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public List<GraffitiAction> getAlgorithmActions() {
        return algorithmActions;
    }

    /**
     * Get the algorithm manager.
     * 
     * @return the algorithm manager.
     */
    public AlgorithmManager getAlgorithmManager() {
        return algorithmManager;
    }

    /**
     * Sets the defaultView.
     * 
     * @param defaultView
     *            The defaultView to set
     */
    public void setDefaultView(String defaultView) {
        this.defaultView = defaultView;
    }

    /**
     * Returns the defaultView.
     * 
     * @return String
     */
    public String getDefaultView() {
        return defaultView;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public EditComponentManager getEditComponentManager() {
        return editComponentManager;
    }

    /**
     * Returns the editCopy.
     * 
     * @return Returns the editCopy.
     */
    public CopyAction getEditCopy() {
        return editCopy;
    }

    /**
     * Returns the editCut.
     * 
     * @return Returns the editCut.
     */
    public CutAction getEditCut() {
        return editCut;
    }

    /**
     * Returns the editDelete.
     * 
     * @return Returns the editDelete.
     */
    public DeleteAction getEditDelete() {
        return editDelete;
    }

    /**
     * Returns the editPaste.
     * 
     * @return Returns the editPaste.
     */
    public PasteAction getEditPaste() {
        return editPaste;
    }

    /**
     * Returns the editRedo.
     * 
     * @return Returns the editRedo.
     */
    public GraffitiAction getEditRedo() {
        return editRedo;
    }

    /**
     * Returns the editSelectAll.
     * 
     * @return Returns the editSelectAll.
     */
    public GraffitiAction getEditSelectAll() {
        return editSelectAll;
    }

    /**
     * Returns the editUndo.
     * 
     * @return Returns the editUndo.
     */
    public GraffitiAction getEditUndo() {
        return editUndo;
    }

    /**
     * Returns the fileOpen.
     * 
     * @return Returns the fileOpen.
     */
    public GraffitiAction getFileOpen() {
        return fileOpen;
    }

    /**
     * Returns the fileSave.
     * 
     * @return Returns the fileSave.
     */
    public GraffitiAction getFileSave() {
        return fileSave;
    }

    /**
     * Returns the fileSaveAll.
     * 
     * @return Returns the fileSaveAll.
     */
    public GraffitiAction getFileSaveAll() {
        return fileSaveAll;
    }

    /**
     * Returns the fileSaveAs.
     * 
     * @return Returns the fileSaveAs.
     */
    public GraffitiAction getFileSaveAs() {
        return fileSaveAs;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public IOManager getIoManager() {
        return ioManager;
    }

    /**
     * Returns the newGraph.
     * 
     * @return Returns the newGraph.
     */
    public GraffitiAction getNewGraph() {
        return newGraph;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public PluginManager getPluginManager() {
        return pluginmgr;
    }

    /**
     * Returns <code>true</code>, if a session is active.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isSessionActive() {
        return getActiveSession() != null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public SessionManager getSessionManager() {
        return this;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Set<Session> getSessions() {
        return sessions;
    }

    /**
     * Returns an iterator over all sessions.
     * 
     * @return an iterator over all sessions.
     * 
     * @see org.graffiti.session.Session
     */
    public Iterator<Session> getSessionsIterator() {
        return sessions.iterator();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Preferences getUiPrefs() {
        return uiPrefs;
    }

    /**
     * Returns the class for undoSupport.
     * 
     * @return the class for undoSupport.
     */
    public UndoableEditSupport getUndoSupport() {
        return undoSupport;
    }

    /**
     * Returns the viewManager.
     * 
     * @return ViewManager
     */
    public ViewManager getViewManager() {
        return viewManager;
    }

    /**
     * Adds the <code>JComponent</code> component to the gui-component specified
     * by id. If the specified gui-component does not exist a new one will be
     * created with id id and the newly created gui-component will be added to
     * the gui of the editor.
     * 
     * @param id
     *            the id of the gui-component where the component shall be
     *            added.
     * @param component
     *            the <code>JComponent</code> which shall be added to the
     *            specified gui-component.
     */
    public void addGUIComponent(String id, JComponent component) {
        // all GraffitiContainers should be JComponents
        JComponent container = (JComponent) guiMap.get(id);

        if (container != null) {
            // if the component is itself a container, then add it to the guiMap
            if (component instanceof GraffitiContainer) {
                GraffitiContainer con = (GraffitiContainer) component;
                guiMap.put(con.getId(), con);
            }

            if (component instanceof GraffitiComponent) {
                GraffitiComponent gComp = ((GraffitiComponent) component);

                gComp.setMainFrame(this);

                if (component instanceof ViewListener) {
                    this.viewManager.addViewListener((ViewListener) component);
                }

                if (component instanceof SessionListener) {
                    this.sessionListeners.add((SessionListener) component);
                }
            }

            if (container == getJMenuBar()) {
                // if we add a new menu make sure help is still the last one
                // problem is: there are three more buttons (minimize, maximize
                // and close) after the first frame got maximized
                for (int i = 0; i < container.getComponentCount(); i++)
                    if (container.getComponent(i) == helpMenu) {
                        container.add(component, null, i);
                        break;
                    }
            } else {
                container.add(component);
            }
            container.validate();

            if (container.getParent() instanceof JSplitPane) {
                // adjust divider location
                JSplitPane pane = (JSplitPane) container.getParent();
                pane.setResizeWeight(1.0);

                if (pane.getWidth() != 0) {
                    pane.setDividerLocation(1 - ((container.getPreferredSize()
                            .getWidth() + 10) / pane.getWidth()));
                }

                if (container.getPreferredSize().getWidth() < 20) {
                    container.setMinimumSize(new Dimension(160, pane
                            .getHeight()));
                } else {
                    container.setMinimumSize(container.getPreferredSize());
                }
            }
        } else {
            // TODO: intelligente Fallunterscheidung bzgl des ?bergebenen
            // components - also bei JMenuItem neues Menu anlegen usw
        }
    }

    /**
     * Adds a <code>SelectionListener</code>.
     * 
     * @param sl
     *            DOCUMENT ME!
     */
    public void addSelectionListener(SelectionListener sl) {
        this.selectionListeners.add(sl);

        for (Session session : getSessions()) {
            try {
                EditorSession eSession = (EditorSession) session;
                eSession.getSelectionModel().addSelectionListener(sl);
            } catch (ClassCastException cce) {
                // ok: non-editor sessions have no selection model
            }
        }
    }

    /**
     * Adds the given session to the list of sessions.
     * 
     * @param s
     *            the new session to add.
     */
    public void addSession(Session s) {
        sessions.add(s);

        if (s instanceof EditorSession) {
            SelectionModel selModel = new SelectionModel();
            ((EditorSession) s).setSelectionModel(selModel);
            s.getGraph().getListenerManager().addNonstrictGraphListener(
                    selModel);

            for (SelectionListener l : selectionListeners) {
                selModel.addSelectionListener(l);
            }

            selModel.add(new Selection(bundle.getString("activeSelection")));
            selModel.setActiveSelection(bundle.getString("activeSelection"));
        }
    }

    /**
     * Adds a <code>SessionListener</code>.
     * 
     * @param sl
     *            DOCUMENT ME!
     */
    public void addSessionListener(SessionListener sl) {
        this.sessionListeners.add(sl);
    }

    /**
     * Removes any messages displayed by calls to <code>showMessage</code> or
     * <code>showError</code>.
     */
    public void clearMessages() {
        statusBar.clear();
    }

    /**
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    public void componentHidden(ComponentEvent e) {
    }

    /**
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    public void componentMoved(ComponentEvent e) {
    }

    /**
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(ComponentEvent e) {
        for (GraffitiInternalFrame frame : activeFrames) {
            if (frame.isMaximum()) {
                // frame.setBounds(0, 0, desktop.getWidth(),
                // desktop.getHeight());
                invalidate();

                // try {
                // frame.setMaximum(false);
                // frame.setMaximum(true);
                // } catch (PropertyVetoException pve) {
                // // does not matter if not allowed
                // }
            }
        }
    }

    /**
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    public void componentShown(ComponentEvent e) {
    }

    /**
     * Creates and adds a new internal frame to the desktop within an existing
     * session.
     * 
     * @param viewName
     *            a name of the new view
     * @param newFrameTitle
     *            the title for the frame, if <code>null</code> or the empty
     *            String no title will be set.
     * @param returnScrollpane
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public JComponent createInternalFrame(String viewName,
            String newFrameTitle, boolean returnScrollpane) {
        return createInternalFrame(viewName, newFrameTitle, activeSession,
                returnScrollpane);
    }

    /**
     * Creates and adds a new internal frame to the desktop within a new
     * session.
     * 
     * @param viewName
     *            a name of the new view
     * @param newFrameTitle
     *            the title for the frame, if <code>null</code> or the empty
     *            String no title will be set.
     * @param session
     *            a new session.
     * @param returnScrollPane
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    public JComponent createInternalFrame(String viewName,
            String newFrameTitle, EditorSession session,
            boolean returnScrollPane) {
        View view;

        try {
            view = viewManager.createView(viewName);
            session.getGraph().addAttributeConsumer(view);
            view.setAttributeComponentManager(this.attributeComponentManager);
            view.setId(session.getNextViewId());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), bundle
                    .getString("view.notFound"), JOptionPane.ERROR_MESSAGE);

            return null;
        }

        GraffitiInternalFrame frame = null;

        if (!returnScrollPane) {
            frame = new GraffitiInternalFrame(session, view, newFrameTitle);
            frame.addInternalFrameListener(gift);
            frame.addPropertyChangeListener(gift);
        }

        view.addMessageListener(this);

        if (session == activeSession) {
            view.setGraph(activeSession.getGraph());
        }

        ListenerManager lm = session.getGraph().getListenerManager();
        lm.addNonstrictAttributeListener(view);
        lm.addStrictEdgeListener(view);
        lm.addStrictNodeListener(view);
        lm.addStrictGraphListener(view);
        view.setGraph(session.getGraph());

        session.addView(view);
        session.setActiveView(view);

        this.activeSession = session;

        sessions.add(session);

        SelectionModel selModel = new SelectionModel();
        session.setSelectionModel(selModel);
        session.getGraph().getListenerManager().addNonstrictGraphListener(
                selModel);

        this.fireSessionChanged(session);

        for (SelectionListener l : selectionListeners) {
            selModel.addSelectionListener(l);
        }

        selModel.add(new Selection(bundle.getString("activeSelection")));
        selModel.setActiveSelection(bundle.getString("activeSelection"));

        JComponent viewComponent = view.getViewComponent();
        if (view.embedsInJScrollPane()) {
            // this.addSession(session);
            JScrollPane scrollPane = new JScrollPane(viewComponent,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
            scrollPane.getVerticalScrollBar().setUnitIncrement(10);
            scrollPane.getViewport().setBackground(Color.WHITE);
            viewComponent = scrollPane;
        }

        if (!returnScrollPane) {
            // this.fireSessionChanged(session);
            int sizeX = 300;
            int sizeY = 200;
            frame.setPreferredSize(new Dimension(sizeX, sizeY));
            frame.getContentPane().add(viewComponent);
            frame.pack();
            frame.setVisible(true);
            this.desktop.add(frame);

            // maximize view at beginning
            try {
                frame.setSelected(true);
                frame.setMaximum(true);
            } catch (PropertyVetoException pve) {
                // should not happen
                throw new RuntimeException(pve);
            }

            viewFrameMapper.put(view, frame);
            activeFrames.add(frame);
        }

        if (view instanceof View2D) {
            View2D view2D = (View2D) view;
            view2D.zoomToFitAfterRedraw();
        }
        return viewComponent;
    }

    /**
     * Creates and returns a new editor session.
     * 
     * @return New session
     */
    public Session createNewSession() {
        return new EditorSession();
    }

    /**
     * Disposes the main frame. Closes all sessions and saves the state (e.g.
     * size and position) of the frame's GUI elements.
     */
    @Override
    public void dispose() {
        while (getActiveSession() != null) {
            if (!removeSession(getActiveSession()))
                return;
        }

        // this.getActiveSession()

        // make sure all events are processed before
        // we exit the application
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                savePreferences();
                System.exit(0);
            }
        });
    }

    /**
     * Check whether the session is modified, ask the user whether to save the
     * session and finally save it.
     * 
     * @param session
     *            Session to save.
     * @param onlyIfLastView
     *            Only ask if there is one view left.
     * 
     */
    public boolean saveSessionCancelled(Session session, boolean onlyIfLastView) {
        boolean askToSave = !onlyIfLastView || (session.getViews().size() == 1);

        boolean cancel = false;

        if (session.isModified() && askToSave) {
            if (session != getActiveSession()) {
                fireSessionChanged(session);
            }
            String fileName = ((EditorSession) session)
                    .getFileNameForSaveDialog();

            int res = JOptionPane.showConfirmDialog(GraffitiSingleton
                    .getInstance().getMainFrame(), bundle
                    .getString("frame.close_save")
                    + " " + fileName + "?", bundle
                    .getString("frame.close_save_title"),
                    JOptionPane.YES_NO_CANCEL_OPTION);

            cancel = (res == JOptionPane.CANCEL_OPTION);

            if (res == JOptionPane.YES_OPTION) {
                // save current graph
                // if (fileName.contains(".")) {
                // fileSave.actionPerformed(new ActionEvent(this, 0, null));
                // } else {
                fileSaveAs.actionPerformed(new ActionEvent(this, 0, null));
                // }
                cancel = (((FileSaveAsAction) fileSaveAs).getReturnValue() == JFileChooser.CANCEL_OPTION);
            } else if (res == JOptionPane.NO_OPTION) {
                // if the user does not want to save the active session
                // and also does not abort the save operation, act as if
                // the graph was never modified at all
                session.getGraph().setModified(false);
            }

            // continue, close view/session
        }

        return cancel;
    }

    /**
     * Informs all <code>SessionListener</code> s that the active session has
     * changed.
     * 
     * @param session
     *            DOCUMENT ME!
     */
    public void fireSessionChanged(Session session) {
        for (SessionListener l : this.sessionListeners) {
            l.sessionChanged(session);
        }
    }

    /**
     * Called, if the session or data (except graph data) in the session have
     * been changed.
     * 
     * @param session
     *            DOCUMENT ME!
     */
    public void fireSessionDataChanged(Session session) {
        for (SessionListener l : this.sessionListeners) {
            l.sessionDataChanged(session);
        }

    }

    /*
     * @see
     * org.graffiti.managers.IOManager.IOManagerListener#inputSerializerAdded
     * (org.graffiti.plugin.io.InputSerializer)
     */
    public void inputSerializerAdded(InputSerializer is) {
        updateActions();
    }

    /**
     * Loads a graph from a file;
     * 
     * @param file
     *            File containing the graph;
     * @return if the loading succeeded.
     */
    public boolean loadGraph(File file) {
        String fileName = file.getName();
        String ext = fileName.substring(fileName.lastIndexOf("."));

        try {
            InputSerializer is = ioManager.getInputSerializer(ext);

            boolean okSelected = GraffitiSingleton.showParameterDialog(is);
            if (!okSelected) return false;

            Graph g = is.read(new FileInputStream(file));
            
            if (g == null) {
                return false;
            }

            EditorSession es = new EditorSession(g);
            es.setFileName(file.toURI());
            showViewChooserDialog(es, false);
            
            return true;
        } catch (org.graffiti.plugin.io.ParserException e1) {
            JOptionPane.showMessageDialog(null, bundle.getString(
                    "fileFormatError").replaceAll("\\[err\\]",
                    e1.getLocalizedMessage()), bundle
                    .getString("fileFormatErrorTitle"),
                    JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        } catch (FileNotFoundException fnfe) {
            showError(bundle.getString("fileNotFound"));
        } catch (Exception e1) {
            showError(e1.getLocalizedMessage());
            e1.printStackTrace();
        }
        
        return false;
    }

    /*
     * @see
     * org.graffiti.managers.IOManager.IOManagerListener#outputSerializerAdded
     * (org.graffiti.plugin.io.OutputSerializer)
     */
    public void outputSerializerAdded(OutputSerializer os) {
        updateActions();
    }

    /**
     * Called by the plugin manager, iff a plugin has been added.
     * 
     * @param plugin
     *            the added plugin.
     * @param desc
     *            the description of the new plugin.
     */
    public void pluginAdded(GenericPlugin plugin, PluginDescription desc) {
        if (plugin instanceof EditorPlugin) {
            EditorPlugin eplugin = (EditorPlugin) plugin;

            // add gui component to gui and if necessary update the mapping
            GraffitiComponent[] gcs = eplugin.getGUIComponents();

            if (gcs != null) {
                for (GraffitiComponent gc : gcs) {
                    addGUIComponent(gc.getPreferredComponent(), (JComponent) gc);
                }
            }
        }

        Algorithm[] algorithms = plugin.getAlgorithms();
        if (plugin.getPathInformation() != null) {
            processPathInformation(pluginMenu, plugin.getPathInformation());
        } else if (plugin.getName() != null) {
            pluginMenu.add(addSubMenu(plugin));
        } else {
            // for every algorithm: add a menu entry
            for (int i = algorithms.length - 1; i >= 0; i--) {
                Algorithm a = algorithms[i];
                GraffitiAction action = new RunAlgorithm(
                        a.getClass().getName(), a.getName(), this,
                        editComponentManager, a);

                algorithmActions.add(action);

                JMenuItem item = new JMenuItem(action);

                if (item.getText() != null) {
                    pluginMenu.add(item);
                }
            }
        }
        sortMenuItems(pluginMenu, 2 + 2);

        // Registers all plugins that are session listeners.
        if (plugin.isSessionListener() && (plugin instanceof SessionListener)) {
            addSessionListener((SessionListener) plugin);
        }

        // Registers all plugins that are view listeners.
        if (plugin.isViewListener() && (plugin instanceof ViewListener)) {
            viewManager.addViewListener((ViewListener) plugin);
        }

        if (plugin.isSelectionListener()) {
            selectionListeners.add((SelectionListener) plugin);

            for (Session sess : sessions) {
                if (sess instanceof EditorSession) {
                    ((EditorSession) sess).getSelectionModel()
                            .addSelectionListener((SelectionListener) plugin);
                }

                // TODO: check what todo if non-EditorSession ...
            }
        }

        if (plugin.needsEditComponents()) {
            ((NeedEditComponents) plugin)
                    .setEditComponentMap(editComponentManager
                            .getEditComponents());
        }

        updateActions();
    }

    private void sortMenuItems(JMenu menu, int offset) {

        List<JMenuItem> items = new LinkedList<JMenuItem>();
        JMenuItem current;

        // remove all entries and memorize them
        while (menu.getItemCount() > offset) {
            current = menu.getItem(offset);
            items.add(current);
            menu.remove(offset);
        }

        Collections.sort(items, new Comparator<JMenuItem>() {

            public int compare(JMenuItem o1, JMenuItem o2) {
                return o1.getText().compareToIgnoreCase(o2.getText());
            }
        });

        for (JMenuItem item : items) {
            menu.add(item);
        }

    }

    private void processPathInformation(JMenu menu, PluginPathNode n) {

        if (n == null)
            return;

        String path = n.getPathLabel();

        Component[] components = menu.getMenuComponents();
        JMenu subMenu = null;
        for (Component c : components) {
            if (c instanceof DisablingMenu
                    && ((DisablingMenu) c).getText().equalsIgnoreCase(path)) {
                subMenu = (DisablingMenu) c;
                break;
            }
        }
        if (subMenu == null) {
            subMenu = new DisablingMenu(path);
            menu.add(subMenu);
        }

        Algorithm[] algorithms = n.getAlgorithms();
        if (algorithms != null) {
            for (int i = 0; i < algorithms.length; i++) {
                Algorithm a = algorithms[i];
                GraffitiAction action = new RunAlgorithm(
                        a.getClass().getName(), a.getName(), this,
                        editComponentManager, a);

                algorithmActions.add(action);

                JMenuItem item = new JMenuItem(action);

                if (item.getText() != null) {
                    subMenu.add(item);
                }
            }
        }

        if (n.getChildren() != null) {
            for (PluginPathNode c : n.getChildren()) {
                processPathInformation(subMenu, c);
            }
        }

        sortMenuItems(subMenu, 0);
    }

    private JMenu addSubMenu(GenericPlugin plugin) {
        Algorithm[] algorithms = plugin.getAlgorithms();
        String name = plugin.getName();
        if (name == null) {
            name = "";
        }
        JMenu subMenu = new DisablingMenu(name);
        for (int i = 0; i < algorithms.length; i++) {
            Algorithm a = algorithms[i];
            GraffitiAction action = new RunAlgorithm(a.getClass().getName(), a
                    .getName(), this, editComponentManager, a);

            algorithmActions.add(action);

            JMenuItem item = new JMenuItem(action);

            if (item.getText() != null) {
                subMenu.add(item);
                if (plugin.addJSeparatorAfterAlgorithm(i)) {
                    subMenu.addSeparator();
                }
            }

        }
        sortMenuItems(subMenu, 0);
        return subMenu;
    }

    /**
     * Removes a <code>SelectionListener</code>.
     * 
     * @param sl
     *            DOCUMENT ME!
     */
    public void removeSelectionListener(SelectionListener sl) {
        this.selectionListeners.remove(sl);

        for (Session session : getSessions()) {
            try {
                EditorSession eSession = (EditorSession) session;
                eSession.getSelectionModel().removeSelectionListener(sl);
            } catch (ClassCastException cce) {
                // ok: non-editor sessions have no selection model
            }
        }
    }

    /**
     * Closes all views of the given session and removes the session from the
     * list of sessions.
     * 
     * @param session
     *            the session to be removed.
     */
    public boolean removeSession(Session session) {
        if (saveSessionCancelled(session, false))
            return false;

        // check if changes have been made
        List<View> views = new LinkedList<View>();

        // close all views and remove this session
        // clone the views list of this session, because it is modified
        // during the iteration and we do not want
        // ConcurrentModificationExceptions
        views.addAll(session.getViews());

        for (View view : views) {
            GraffitiInternalFrame frame = viewFrameMapper.get(view);

            viewFrameMapper.remove(view);
            activeFrames.remove(frame);

            if (frame != null) {
                frame.doDefaultCloseAction();
            }
        }
        try {
            session.getGraph().getListenerManager().removeGraphListener(
                    ((EditorSession) session).getSelectionModel());
        } catch (ListenerNotFoundException e) {
            System.err
                    .println("selection model was not registered as listener!");
        }

        sessions.remove(session);
        updateActions();
        return true;
    }

    /**
     * Removes a <code>SessionListener</code>.
     * 
     * @param sl
     *            DOCUMENT ME!
     */
    public void removeSessionListener(SessionListener sl) {
        this.sessionListeners.remove(sl);
    }

    /**
     * Saves the preferences of the main frame.
     */
    public void savePreferences() {
        try {
            int screenX = (int) (getLocationOnScreen().getX());
            int screenY = (int) (getLocationOnScreen().getY());

            uiPrefs.putInt("sizeWidth", getWidth());
            uiPrefs.putInt("sizeHeight", getHeight());
            uiPrefs.putInt("positionX", screenX);
            uiPrefs.putInt("positionY", screenY);

            uiPrefs.putInt("vertSplitter", vertSplitter.getDividerLocation());

            uiPrefs.put("lookAndFeel", lookAndFeel.toString());

            uiPrefs.sync();

            corePrefs.put("locale", localeString == null ? "" : localeString);

            corePrefs.sync();
        } catch (IllegalComponentStateException icse) {
            icse.printStackTrace();
        } catch (BackingStoreException bse) {
            bse.printStackTrace(System.out);
        }
    }

    /**
     * Invoked when the session changed.
     * 
     * @param s
     *            the new session.
     */
    public void sessionChanged(Session s) {
        if (isSessionActive()) {
            // removing the old sesion from undoSupport
            undoSupport.removeUndoableEditListener(activeSession
                    .getUndoManager());

            // removing the MainFrame from undoSupport
            undoSupport.removeUndoableEditListener(this);
        }

        if (s != null) {
            // registering the new session at undoSupport
            undoSupport.addUndoableEditListener(((EditorSession) s)
                    .getUndoManager());

            // registering the MainFrame at undoSupport
            undoSupport.addUndoableEditListener(this);

            for (View view : s.getViews()) {
                if (view instanceof InteractiveView<?>) {
                    continue;
                }
                MouseListener[] ml = view.getViewComponent()
                        .getMouseListeners();

                // System.out.println("#MouseListeners: " + ml.length);
                for (int i = ml.length - 1; i >= 0; i--) {
                    view.getViewComponent().removeMouseListener(ml[i]);

                    // System.out.println("deleting listeners");
                }

                MouseMotionListener[] mml = view.getViewComponent()
                        .getMouseMotionListeners();

                // System.out.println("#MouseListeners: " + mml.length);
                for (int i = mml.length; --i >= 0;) {
                    view.getViewComponent().removeMouseMotionListener(mml[i]);

                    // System.out.println("deleting listeners");
                }
            }

            this.activeSession = (EditorSession) s;
            updateActions();
        } else {
            // System.out.println("Deactivating tools");
            // TODO what do we have to do here? (jf)
        }
    }

    /**
     * Invoked when the session data changed.
     * 
     * @param s
     *            DOCUMENT ME!
     */
    public void sessionDataChanged(Session s) {
        EditorSession es = (EditorSession) s;

        for (View view : es.getViews()) {
            GraffitiInternalFrame frame = viewFrameMapper.get(view);

            if (es != null) {
                if (es.getFileNameAsString() != null) {
                    if (frame != null) {
                        frame.setTitle(es.getFileNameAsString());
                    }
                }
            }
        }

        updateActions();
    }

    /**
     * Shows an arbitrary message dialog.
     * 
     * @param msg
     *            the message to be shown.
     */
    public void showMessageDialog(String msg) {
        showMessageDialog(msg, bundle.getString("message.dialog.title"));
    }

    /**
     * Shows an arbitrary message dialog.
     * 
     * @param msg
     *            the message to be shown.
     * @param title
     *            DOCUMENT ME!
     */
    public void showMessageDialog(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, title,
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Method <code>showMesssage</code> displays a message on GUI components
     * according to the specified type. The message will be displayed for some
     * defined number of seconds.
     * 
     * @param message
     *            a message string to be displayed
     * @param type
     *            a type of the message (e.g. MessageListener.INFO)
     */
    public void showMesssage(String message, int type) {
        if (type == MessageListener.ERROR) {
            this.statusBar.showError(message);
        } else if (type == MessageListener.INFO) {
            this.statusBar.showInfo(message);
        } else if (type == MessageListener.PERMANENT_INFO) {
            this.statusBar.showInfo(message, Integer.MAX_VALUE);
        }
    }

    /**
     * Method <code>showMesssage</code> displays a message on GUI components
     * according to the specified type for the given interval.
     * 
     * @param message
     *            a message string to be displayed
     * @param type
     *            a type of the message (e.g. ERROR)
     * @param timeMillis
     *            number of milliseconds the message should be displayed
     */
    public void showMesssage(String message, int type, int timeMillis) {
        if (type == MessageListener.ERROR) {
            this.statusBar.showError(message, timeMillis);
        } else if (type == MessageListener.INFO) {
            this.statusBar.showInfo(message, timeMillis);
        }
    }

    /**
     * Method <code>showViewChooserDialog </code> invokes a view chooser dialog
     * for choosing view types. The parameter withNewSession specifies whether
     * the new view starts within an existing session or within a new session.
     * 
     * @param returnScrollpane
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public JComponent showViewChooserDialog(boolean returnScrollpane) {
        return showViewChooserDialog(activeSession, returnScrollpane);
    }

    /**
     * Method <code>showViewChooserDialog </code> invokes a view chooser dialog
     * for choosing view types. The parameter withNewSession specifies whether
     * the new view starts within an existing session or within a new session.
     * 
     * @param session
     *            the session in which to open the new view.
     * @param returnScrollPane
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public JComponent showViewChooserDialog(EditorSession session,
            boolean returnScrollPane) {
        String[] views = viewManager.getViewNames();

        if (views.length == 0) {
            JOptionPane.showMessageDialog(this, bundle
                    .getString("viewchooser.pluginNotAdded"), bundle
                    .getString("viewchooser.errorDialog.title"),
                    JOptionPane.ERROR_MESSAGE);
        } else if (viewManager.getViewNames().length == 1) {
            if (sessions.contains(session))
                return createInternalFrame(views[0], session
                        .getFileNameAsString(), returnScrollPane);
            else
                return createInternalFrame(views[0], session
                        .getFileNameAsString(), session, returnScrollPane);
        } else {
            ViewTypeChooser viewChooser = new ViewTypeChooser(this, bundle
                    .getString("viewchooser.title"), viewManager.getViewNames());

            // The user did not select a view.
            if (viewChooser.getSelectedView() == -1)
                return null;

            String selectedView = views[viewChooser.getSelectedView()];

            if (selectedView != null) {
                if (sessions.contains(session))
                    return createInternalFrame(selectedView, session
                            .getFileNameAsString(), returnScrollPane);
                else
                    return createInternalFrame(selectedView, session
                            .getFileNameAsString(), session, returnScrollPane);
            }
        }

        return null;
    }

    /**
     * This method is called when an undoableEdit happened.
     * 
     * @see javax.swing.event.UndoableEditListener
     */
    public void undoableEditHappened(UndoableEditEvent e) {
        editUndo.update();
        editRedo.update();
    }

    /**
     * Updates the state of the actions.
     */
    public void updateActions() {
        newGraph.update();

        fileOpen.update();
        fileClose.update();
        fileSave.update();
        fileSaveAs.update();
        fileSaveAll.update();
        viewNew.update();
        editSelectAll.update();

        editCut.update();

        // ???
        editDelete.update();

        editUndo.update();
        editRedo.update();

        for (GraffitiAction action : algorithmActions) {
            action.update();
        }
    }

    /*
     * @see
     * org.graffiti.managers.ViewManager.ViewManagerListener#viewTypeAdded(java
     * .lang.String)
     */
    public void viewTypeAdded(String viewType) {
        updateActions();
    }

    /**
     * Invoked when the Window is set to be the active Window.
     * 
     * @param e
     *            the <code>WindowEvent</code> to be processed.
     */
    public void windowActivated(WindowEvent e) {
    }

    /**
     * Invoked when a window has been closed as the result of calling dispose on
     * the window.
     * 
     * @param e
     *            the <code>WindowEvent</code> to be processed.
     */
    public void windowClosed(WindowEvent e) {
    }

    /**
     * Invoked when the user attempts to close the window from the window's
     * system menu.
     * 
     * @param e
     *            the <code>WindowEvent</code> to be processed.
     */
    public void windowClosing(WindowEvent e) {
        dispose();
    }

    /**
     * Invoked when a Window is no longer the active Window.
     * 
     * @param e
     *            the <code>WindowEvent</code> to be processed.
     */
    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     * 
     * @param e
     *            the <code>WindowEvent</code> to be processed.
     */
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a normal to a minimized state.
     * 
     * @param e
     *            the <code>WindowEvent</code> to be processed.
     */
    public void windowIconified(WindowEvent e) {
    }

    /**
     * Invoked the first time a window is made visible.
     * 
     * @param e
     *            the <code>WindowEvent</code> to be processed.
     */
    public void windowOpened(WindowEvent e) {
    }

    /**
     * Shows an error in a modal dialog box.
     * 
     * @param msg
     *            the message to be shown.
     */
    protected void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, bundle
                .getString("message.dialog.title"), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Method fireViewChanged.
     * 
     * @param newView
     */
    void fireViewChanged(View newView) {
        activeSession.setActiveView(newView);
        viewManager.viewChanged(newView);
    }

    /**
     * Sets the accel key of the given item. The accel key information is
     * gathered from the <code>bundle</code>.
     * 
     * @param item
     *            DOCUMENT ME!
     * @param action
     *            DOCUMENT ME!
     */
    private void setAccelKey(JMenuItem item, GraffitiAction action) {
        String accel = bundle.getString("menu." + action.getName() + ".accel");

        if (accel != null) {
            try {
                int mask = 0;

                if (accel.startsWith("CTRL")) {
                    mask += ActionEvent.CTRL_MASK;
                    accel = accel.substring(5);
                }

                if (accel.startsWith("SHIFT")) {
                    mask += ActionEvent.SHIFT_MASK;
                    accel = accel.substring(6);
                }

                if (accel.startsWith("ALT")) {
                    mask += ActionEvent.ALT_MASK;
                    accel = accel.substring(4);
                }

                int key = KeyEvent.class.getField("VK_" + accel).getInt(null);
                item.setAccelerator(KeyStroke.getKeyStroke(key, mask));
            } catch (Exception e) {
                // ignore
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates the action instances.
     */
    private void createActions() {
        newGraph = new FileNewAction(this, viewManager);
        fileOpen = new FileOpenAction(this, ioManager, viewManager, bundle);
        fileSave = new FileSaveAction(this, ioManager, this);
        fileClose = new FileCloseAction(this);

        fileSaveAs = new FileSaveAsAction(this, ioManager, this, bundle);
        fileSaveAll = new FileSaveAllAction(this, ioManager);

        fileExit = new ExitAction(this);

        viewNew = new ViewNewAction(this, bundle);

        pluginManagerEdit = new PluginManagerEditAction(this, pluginmgr);

        editUndo = new EditUndoAction(this);
        editRedo = new EditRedoAction(this);

        // ???
        editDelete = new DeleteAction(this);
        addSelectionListener(new DeleteActionListener(editDelete));
        editCut = new CutAction(this);
        editCopy = new CopyAction(this);
        editPaste = new PasteAction(this);
        addSelectionListener(new CutCopyPasteListener(editCut));
        addSelectionListener(new CutCopyPasteListener(editCopy));
        addSelectionListener(new CutCopyPasteListener(editPaste));

        editSelectAll = new SelectAllAction(this);

        addSelectionListener(new SelectGraphElementActionListener(editSelectAll));

        redrawView = new RedrawViewAction(this);

        about = new AboutAction(this);
    }

    /**
     * Constructs a menu, and returns the menu. &quot;menu.&quot; <tt>name</tt>
     * is read from the string bundle. &uqot;menu.&quot; <tt>name</tt>
     * &quot;.icon&quot; is read from the image bundle.
     * 
     * @param name
     *            the name of the menu item.
     * 
     * @return DOCUMENT ME!
     */
    private JMenu createMenu(String name) {
        JMenu menu = new JMenu(bundle.getString("menu." + name));

        guiMap.put("menu." + name, menu);

        try {
            String mnem = bundle.getString("menu." + name + ".mnemonic");

            if (mnem != null) {
                menu.setMnemonic(Class.forName("java.awt.event.KeyEvent")
                        .getField(mnem).getInt(null));
            }
        } catch (Exception e) {
        }

        return menu;
    }

    /**
     * Creates and returns the menu bar.
     * 
     * @return the menu bar.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        guiMap.put("menu", menuBar);

        // menu for file operations
        JMenu fileMenu = createMenu("file");
        menuBar.add(fileMenu);

        fileMenu.add(createMenuItem(newGraph));
        fileMenu.add(createMenuItem(viewNew));
        fileMenu.add(createMenuItem(fileOpen));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem(fileSave));
        fileMenu.add(createMenuItem(fileSaveAs));

        // fileMenu.add(createMenuItem(fileSaveAll)); TODO
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem(fileClose));
        fileMenu.add(createMenuItem(fileExit));

        JMenu editMenu = createMenu("edit");
        menuBar.add(editMenu);

        editMenu.add(createMenuItem(editUndo));
        editMenu.add(createMenuItem(editRedo));
        editMenu.addSeparator();
        editMenu.add(createMenuItem(editSelectAll));
        editMenu.add(createMenuItem(editCut));
        editMenu.add(createMenuItem(editCopy));
        editMenu.add(createMenuItem(editPaste));
        editMenu.addSeparator();
        editMenu.add(createMenuItem(editDelete));
        editMenu.addSeparator();
        editMenu.add(createMenuItem(redrawView));

        pluginMenu = createMenu("plugin");
        menuBar.add(pluginMenu);
        pluginMenu.add(createMenuItem(pluginManagerEdit));

        optionsMenu = createMenu("options");
        menuBar.add(optionsMenu);
        JMenu localeMenu = new JMenu(bundle.getString("menu.options.locale"));
        JRadioButtonMenuItem englishLocale = new JRadioButtonMenuItem(bundle
                .getString("menu.options.locale.english"));
        JRadioButtonMenuItem germanLocale = new JRadioButtonMenuItem(bundle
                .getString("menu.options.locale.german"));
        englishLocale.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                localeString = "en";
                showMessageDialog(bundle.getString("message.restart.editor"),
                        bundle.getString("message.dialog.title"));
            }
        });
        germanLocale.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                localeString = "de";
                showMessageDialog(bundle.getString("message.restart.editor"),
                        bundle.getString("message.dialog.title"));
            }
        });
        ButtonGroup localeGroup = new ButtonGroup();
        localeGroup.add(germanLocale);
        localeGroup.add(englishLocale);
        if (localeString.equals("en")) {
            englishLocale.setSelected(true);
        } else if (localeString.equals("de")) {
            germanLocale.setSelected(true);
        }
        localeMenu.add(englishLocale);
        localeMenu.add(germanLocale);
        optionsMenu.add(localeMenu);

        JMenu lookAndFeelMenu = new JMenu(bundle
                .getString("menu.options.lookandfeel"));
        JRadioButtonMenuItem windows = new JRadioButtonMenuItem(bundle
                .getString("menu.options.lookandfeel.windows"));
        JRadioButtonMenuItem windowsJGoodies = new JRadioButtonMenuItem(bundle
                .getString("menu.options.lookandfeel.windowsjgoodies"));
        JRadioButtonMenuItem crossPlatform = new JRadioButtonMenuItem(bundle
                .getString("menu.options.lookandfeel.crossplatform"));
        if (!UIManager.getSystemLookAndFeelClassName().equals(
                LookAndFeel.SUN_WINDOWS.getClassName())) {
            windows.setEnabled(false);
            windowsJGoodies.setEnabled(false);
        }

        windows.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lookAndFeel = LookAndFeel.SUN_WINDOWS;
                showMessageDialog(bundle.getString("message.restart.editor"),
                        bundle.getString("message.dialog.title"));
            }
        });
        windowsJGoodies.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lookAndFeel = LookAndFeel.JGOODIES_WINDOWS;
                showMessageDialog(bundle.getString("message.restart.editor"),
                        bundle.getString("message.dialog.title"));
            }
        });
        crossPlatform.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lookAndFeel = LookAndFeel.SUN_CROSS_PLATFORM;
                showMessageDialog(bundle.getString("message.restart.editor"),
                        bundle.getString("message.dialog.title"));
            }
        });
        ButtonGroup lookAndFeelGroup = new ButtonGroup();
        lookAndFeelGroup.add(windows);
        lookAndFeelGroup.add(windowsJGoodies);
        lookAndFeelGroup.add(crossPlatform);
        if (lookAndFeel == LookAndFeel.SUN_WINDOWS) {
            windows.setSelected(true);
        } else if (lookAndFeel == LookAndFeel.JGOODIES_WINDOWS) {
            windowsJGoodies.setSelected(true);
        } else if (lookAndFeel == LookAndFeel.SUN_CROSS_PLATFORM) {
            crossPlatform.setSelected(true);
        }
        lookAndFeelMenu.add(windows);
        lookAndFeelMenu.add(windowsJGoodies);
        lookAndFeelMenu.add(crossPlatform);
        optionsMenu.add(lookAndFeelMenu);

        // NEW SAVE / LOAD PREFRENCES ***************************************
        JMenuItem pluginPrefsSave = new JMenuItem(bundle
                .getString("menu.plugin.pluginPrefsSave"));
        JMenuItem pluginPrefsLoad = new JMenuItem(bundle
                .getString("menu.plugin.pluginPrefsLoad"));
        pluginPrefsLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.showOpenDialog(null);

                File selFile = fc.getSelectedFile();
                String err = null;

                try {
                    Preferences.importPreferences(new FileInputStream(selFile));
                } catch (FileNotFoundException e1) {
                    err = e1.getLocalizedMessage();
                } catch (IOException e1) {
                    err = e1.getLocalizedMessage();
                } catch (InvalidPreferencesFormatException e1) {
                    err = e1.getLocalizedMessage();
                }

                if (err != null) {
                    JOptionPane.showMessageDialog(null,
                            "Error while reading preferences: " + err, "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

                try {
                    getPluginManager().loadStartupPlugins();
                } catch (PluginManagerException e2) {
                    String errm = e2.getLocalizedMessage();
                    JOptionPane.showMessageDialog(null,
                            "Error while loading plugins: " + errm, "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        pluginPrefsSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.showSaveDialog(null);

                File selFile = fc.getSelectedFile();
                String err = null;

                try {
                    Preferences prefs = Preferences
                            .userNodeForPackage(GraffitiEditor.class);
                    prefs.exportSubtree(new FileOutputStream(selFile));
                } catch (FileNotFoundException e1) {
                    err = e1.getLocalizedMessage();
                } catch (IOException e1) {
                    err = e1.getLocalizedMessage();
                } catch (BackingStoreException e1) {
                    err = e1.getLocalizedMessage();
                }

                if (err != null) {
                    JOptionPane.showMessageDialog(null,
                            "Error while saving preferences: " + err, "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        pluginMenu.add(pluginPrefsSave);
        pluginMenu.add(pluginPrefsLoad);

        // ******************************************************************
        pluginMenu.addSeparator();

        windowMenu = createMenu("window");
        menuBar.add(windowMenu);

        helpMenu = createMenu("help");
        menuBar.add(helpMenu);
        helpMenu.add(createMenuItem(about));

        return menuBar;
    }

    /**
     * Constructs a menu item, registers this class as action listener and
     * returns the menu item. &quot;menu.&quot; <tt>name</tt> is read from the
     * string bundle. &uqot;menu.&quot; <tt>name</tt> &quot;.icon&quot; is read
     * from the image bundle.
     * 
     * @param action
     *            the action, which should be executed by this menu item.
     * 
     * @return DOCUMENT ME!
     */
    private JMenuItem createMenuItem(GraffitiAction action) {
        String actionName = action.getName();

        JMenuItem item = new JMenuItem(action);
        item.setText(bundle.getString("menu." + actionName));
        item.setIcon(bundle.getIcon("menu." + actionName + ".icon"));

        try {
            String mnem = bundle.getString("menu." + actionName + ".mnemonic");

            if (mnem != null) {
                item.setMnemonic(Class.forName("java.awt.event.KeyEvent")
                        .getField(mnem).getInt(null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setAccelKey(item, action);

        return item;
    }

    /**
     * Creates the editor's tool bar.
     * 
     * @return the toolbar for the editor.
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(createToolBarButton(newGraph));
        toolBar.add(createToolBarButton(fileOpen));
        toolBar.addSeparator();
        toolBar.add(createToolBarButton(fileSave));
        toolBar.add(createToolBarButton(fileSaveAs));

        // toolBar.add(createToolBarButton(fileSaveAll)); TODO
        toolBar.addSeparator();
        toolBar.add(createToolBarButton(editCut));
        toolBar.add(createToolBarButton(editCopy));
        toolBar.add(createToolBarButton(editPaste));
        toolBar.addSeparator();
        toolBar.add(createToolBarButton(editDelete));
        toolBar.addSeparator();
        toolBar.add(createToolBarButton(editUndo));
        toolBar.add(createToolBarButton(editRedo));
        toolBar.addSeparator();

        toolBar.add(createToolBarButton(pluginManagerEdit));
        toolBar.addSeparator();
        toolBar.add(createAnimationPanel());
        return toolBar;
    }

    /**
     * Creates a new animation panel.
     * 
     * @see org.graffiti.plugin.gui.AnimationPanel
     * 
     * @return a new animation panel.
     */
    private Component createAnimationPanel() {
        AnimationPanel a = new AnimationPanel();
        a.setSessionManager(this);
        return a;
    }

    /**
     * Constructs and returns a button.
     * 
     * @param action
     *            the action, which is associated with this button.
     * 
     * @return DOCUMENT ME!
     */
    private JButton createToolBarButton(GraffitiAction action) {
        JButton button = new JButton(action);
        button.setText(bundle.getString("toolbar." + action.getName()));
        button.setToolTipText(bundle.getString("toolbar." + action.getName()
                + ".tooltip"));
        button.setIcon(bundle.getIcon("toolbar." + action.getName() + ".icon"));

        // a little bag of tricks: java developers use a string value for this
        // property
        // instead of a constant, moreover is this string value not
        // documented.ww
        button.putClientProperty("hideActionText", new Boolean(true));

        try {
            String mnem = bundle.getString("toolbar." + action.getName()
                    + ".mnemonic");

            if (mnem != null) {
                button.setMnemonic(Class.forName("java.awt.event.KeyEvent")
                        .getField(mnem).getInt(null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return button;
    }

    /**
     * Order the GraffitiInternalFrames according to their session and view id.
     */
    class GraffitiInternalFrameOrder implements Comparator<JInternalFrame> {
        public int compare(JInternalFrame if0, JInternalFrame if1) {
            if (if0 instanceof GraffitiInternalFrame
                    && if1 instanceof GraffitiInternalFrame) {
                GraffitiInternalFrame gif0 = (GraffitiInternalFrame) if0;
                GraffitiInternalFrame gif1 = (GraffitiInternalFrame) if1;

                int diff = gif0.getSession().getId()
                        - gif1.getSession().getId();

                if (diff == 0) {
                    diff = gif0.getView().getId() - gif1.getView().getId();
                }

                return diff;
            } else if (if0 instanceof GraffitiInternalFrame)
                return -1;
            else if (if1 instanceof GraffitiInternalFrame)
                return +1;
            else
                return 0;
        }
    }

    /**
     * Listener for the internal frames.
     */
    class GraffitiInternalFrameListener extends InternalFrameAdapter implements
            PropertyChangeListener {

        /**
         * @see javax.swing.event.InternalFrameListener#internalFrameClosing(InternalFrameEvent)
         */
        @Override
        public void internalFrameClosing(InternalFrameEvent e) {
            GraffitiInternalFrame f = (GraffitiInternalFrame) e.getSource();
            EditorSession session = f.getSession();

            if (!saveSessionCancelled(session, true)) {
                f.dispose();
            }
        }

        /**
         * @see javax.swing.event.InternalFrameListener#internalFrameActivated(InternalFrameEvent)
         */
        @Override
        public void internalFrameActivated(InternalFrameEvent e) {
            super.internalFrameActivated(e);

            GraffitiInternalFrame iframe = (GraffitiInternalFrame) e
                    .getInternalFrame();
            EditorSession frameSession = iframe.getSession();

            if (!(frameSession == activeSession)) {
                fireSessionChanged(frameSession);
            } else {
                fireViewChanged(iframe.getView());
            }
        }

        /**
         * @see javax.swing.event.InternalFrameListener#internalFrameClosed(InternalFrameEvent)
         */
        @Override
        public void internalFrameClosed(InternalFrameEvent e) {
            GraffitiInternalFrame f = (GraffitiInternalFrame) e
                    .getInternalFrame();

            EditorSession session = ((GraffitiInternalFrame) e
                    .getInternalFrame()).getSession();

            // remove this view only if there are other open views
            if (session.getViews().size() >= 2) {
                f.getView().close();
                session.removeView(f.getView());
                viewFrameMapper.remove(f.getView());
                activeFrames.remove(f);
            } else {
                // remove the session if we are closing the last view
                f.getView().close();

                session.setClosing();
                session.close();
                sessions.remove(session);
                session = null;

                viewFrameMapper.remove(f.getView());
                activeFrames.remove(f);

                if (sessions.size() == 0) {
                    // System.out.println("last session closed.");
                    fireSessionChanged(null);
                    activeSession = null;
                }
            }

            // the update mechanism after the close of the last session
            // still has some bugs...
            updateActions();

            super.internalFrameClosed(e);
        }

        /**
         * Listen to IS_SELECTED_PROPERTY and set title of mainframe.
         * 
         * @param evt
         *            Event for the property change.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(
                    JInternalFrame.IS_SELECTED_PROPERTY)) {
                String title = getDefaultTitle();
                boolean addPrefix = ((Boolean) evt.getNewValue())
                        .booleanValue();
                JInternalFrame frame = (JInternalFrame) evt.getSource();

                if (addPrefix) {
                    title = title + " [" + frame.getTitle() + "]";
                }

                setTitle(title);
            }
        }
    }

    /**
     * Returns the statusBar.
     * 
     * @return the statusBar.
     */
    public StatusBar getStatusBar() {
        return statusBar;
    }

    public ViewportEventDispatcher getViewportEventDispatcher() {
        return viewportEventDispatcher;
    }

    public Session addNewSession() {
        String dv = getDefaultView();

        if (dv != null) {
            createInternalFrame(dv, "", false);
        } else {
            showViewChooserDialog(new EditorSession(), false);
        }

        updateActions();

        return getSessionManager().getActiveSession();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
