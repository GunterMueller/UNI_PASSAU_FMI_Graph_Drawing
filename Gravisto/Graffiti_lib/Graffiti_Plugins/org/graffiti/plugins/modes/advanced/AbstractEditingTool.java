// =============================================================================
//
//   AbstractEditingTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractEditingTool.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.undo.UndoableEditSupport;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.DockingAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugin.view.View;
import org.graffiti.plugin.view.ViewListener;
import org.graffiti.plugins.modes.advanced.selection.align.AlignTool;
import org.graffiti.plugins.modes.deprecated.AbstractUndoableTool;
import org.graffiti.plugins.views.defaults.AbstractGraphElementComponent;
import org.graffiti.plugins.views.defaults.DummySupportView;
import org.graffiti.plugins.views.defaults.EdgeComponent;
import org.graffiti.plugins.views.defaults.NodeComponent;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionModel;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;

/**
 * Abstract superclass for all advanced editing-tools.
 * 
 * @deprecated
 */
@Deprecated
public abstract class AbstractEditingTool extends AbstractUndoableTool
        implements FunctionComponent, ViewListener {
    /** Constant for the default mode */
    private static final int DEFAULT_MODE = 0;

    /** The tool is in this mode while a multi-command-operation is active. */
    private static final int MULTI_COMMAND_MODE = 1;

    /** default mode */
    public static final int DEFAULT = 0;

    /** Constant for alignment top */
    public static final String TOP = "top";

    /** Constant for alignment center-horizontal */
    public static final String CENTER_HORIZONTAL = "center-horizontal";

    /** Constant for alignment bottom */
    public static final String BOTTOM = "bottom";

    /** Constant for alignment and cursor movement right */
    public static final String RIGHT = "right";

    /** Constant for alignment center-vertical */
    public static final String CENTER_VERTICAL = "center-vertical";

    /** Constant for alignment and cursor movement left */
    public static final String LEFT = "left";

    /** Constant for alignment center */
    public static final String CENTER = "center";

    /** Constant for cursor movement up */
    public static final String UP = "up";

    /** Constant for cursor movement down */
    public static final String DOWN = "down";

    /** Name of the selection to be used by default */
    private static final String DEFAULT_SELECTION_NAME = "defaultSelection";

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(AbstractEditingTool.class.getName());

    /** Reference to the FunctionManager of this tool */
    protected FunctionManager functionManager = new FunctionManager(this);

    /** Reference to MainFrame */
    private MainFrame mainFrame;

    /** JMenu in the popupMenu for the alignment */
    protected JMenu alignMenu = null;

    /** JMenuItem in the popupMenu for Cut */
    protected JMenuItem cutItem = null;

    /** JMenuItem in the popupMenu for Copy */
    protected JMenuItem copyItem = null;

    /** JMenuItem in the popupMenu for Paste */
    protected JMenuItem pasteItem = null;

    /** JMenuItem in the popupMenu for Delete */
    protected JMenuItem deleteItem = null;

    /** JMenuItem in the popupMenu for adding a bend to an edge */
    protected JMenuItem addBendItem = null;

    /** JMenuItem in the popupMenu for removing all bends of an edge */
    protected JMenuItem removeBendsItem = null;

    /** Reference to the JPopupMenu for this tool */
    protected JPopupMenu popupMenu = null;

    /** Map to save the attributes for undo/redo */
    protected Map<Attribute, Object> attributesMap;

    /** node coordinates can be stored in this array */
    private CoordinateAttribute[] nodeCoords;

    /** node dimesnions can be stored in this array */
    private DimensionAttribute[] nodeDims;

    /** bend coordinates can be stored in this array */
    private LinkedList<CoordinateAttribute> bendCoords;

    /**
     * Component where the last mouseMoved-event happened. Usually the
     * view-component.
     */
    private Container lastMouseMovedEventSource;

    /**
     * Reference to the currently highlighted GraphElementComponent, null if
     * nothing is highlighted
     */
    private GraphElementComponent highlightedGEC = null;

    /**
     * JComponent used for key-bindings - if null, no key-bindings are active at
     * the moment
     */
    private JComponent keyBindingsComponent = null;

    /**
     * JComponent used for mouse-bindings - if null, no mouse-bindings are
     * active at the moment
     */
    private JComponent mouseBindingsComponent = null;

    /** Stores position-info like the last mouse-position */
    private PositionInfo positionInfo = null;

    /** Reference to the SelectionModel (shared among all editing tools) */
    private SelectionModel selectionModel;

    /**
     * Here, all newly added nodes are stored until afterEvent is called. Sense:
     * The methods for getting objects at mouse-position can test if a node is
     * inside this set, and ignore it, if yes. Otherwise, start-add-edge might
     * use a newly added node as start-node.
     */
    private Set<Node> justAddedNodes = new HashSet<Node>();

    /** Reference to the ToolPlugin-instance which handles this tool. */
    private ToolPlugin toolPlugin;

    /**
     * Determines if elements (nodes, edges) the mouse-cursor points to should
     * be highlighted
     */
    private boolean highlightPointedElements = true;

    /** the mode of the tool (initial: default) */
    private int mode = DEFAULT;

    /**
     * Performs all construction-activities being unique to all editing tools.
     * 
     * @param toolPlugin
     *            ToolPlugin which handles this tool
     * @param positionInfo
     *            Information about the mouse position
     */
    public AbstractEditingTool(ToolPlugin toolPlugin, PositionInfo positionInfo) {
        this.toolPlugin = toolPlugin;
        this.positionInfo = positionInfo;
    }

    /**
     * Sets the attributeMap to map
     * 
     * @param map
     *            the given Map
     */
    public void setAttributesMap(Map<Attribute, Object> map) {
        attributesMap = map;
    }

    /**
     * Returns the attributeMap
     * 
     * @return attributeMap
     */
    public Map<Attribute, Object> getAttributesMap() {
        return attributesMap;
    }

    /**
     * Returns the CoordinateAttribute of a given node.
     * 
     * @param node
     *            The given node.
     * 
     * @return the CoordinateAttribute of a given node.
     */
    public CoordinateAttribute getCooAttNode(Node node) {
        CollectionAttribute attributes = node.getAttributes();

        NodeGraphicAttribute nodeGraphicAtt = (NodeGraphicAttribute) attributes
                .getAttribute(GraphicAttributeConstants.GRAPHICS);

        CoordinateAttribute cooAtt = nodeGraphicAtt.getCoordinate();

        return cooAtt;
    }

    /**
     * Get a list of all views, which support dummies.
     * 
     * @return List of all views supporting dummies.
     */
    public List<DummySupportView> getDummySupportViews() {
        List<DummySupportView> views = new LinkedList<DummySupportView>();

        for (Iterator<View> it = session.getViews().iterator(); it.hasNext();) {
            View view = it.next();

            if (view instanceof DummySupportView) {
                views.add((DummySupportView) view);
            }
        }

        return views;
    }

    public Point getZoomedPosition(Point p) {
        AffineTransform zoom = this.session.getActiveView().getZoomTransform();
        Point2D pt2d = zoom.transform(p, null);
        return new Point((int) pt2d.getX(), (int) pt2d.getY());
    }

    /**
     * Returns the name of the function (Here null, because the abstract tool
     * does not provide any functions
     * 
     * @param functionName
     *            The given function name
     * 
     * @return The action of the function
     */
    public FunctionAction getFunctionAction(String functionName) {
        return null;
    }

    /**
     * Returns the GEMap (The reference for the map between graph elements
     * recreated after undo processing and original graph elements)
     * 
     * @return The GEMap
     */
    public Map<GraphElement, GraphElement> getGEMap() {
        return geMap;
    }

    /**
     * Returns the graph this tool works on.
     * 
     * @return Returns the graph this tool works on.
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Returns if the tool currently is in {@link #DEFAULT_MODE}.
     * 
     * @return if the tool currently is in {@link #DEFAULT_MODE}.
     */
    public boolean isInDefaultMode() {
        return mode == DEFAULT_MODE;
    }

    /**
     * Returns if the tool currently is in {@link #MULTI_COMMAND_MODE}.
     * 
     * @return if the tool currently is in {@link #MULTI_COMMAND_MODE}
     */
    public boolean isInMultiCommandMode() {
        return mode == MULTI_COMMAND_MODE;
    }

    /**
     * Returns if the GraphElement displayed by the given GraphElementComponent
     * is marked.
     * 
     * @param gec
     *            any GraphElementComponent
     * 
     * @return if the GraphElement displayed by the given GraphElementComponent
     *         is marked.
     */
    public boolean isMarked(GraphElementComponent gec) {
        return isMarked(gec.getGraphElement());
    }

    /**
     * Returns if the given GraphElement is marked.
     * 
     * @param graphElement
     *            any GraphElement
     * 
     * @return if the given GraphElement is marked
     */
    public boolean isMarked(GraphElement graphElement) {
        return selection.contains(graphElement);
    }

    /**
     * Returns the mode of the actual tool
     * 
     * @return the mode of the actual tool
     */
    public int getMode() {
        return this.mode;
    }

    /**
     * Sets the mode to default
     */
    public void setModeToDefault() {
        this.mode = DEFAULT;
    }

    /**
     * Returns a list with the nodes under the given mouse position.
     * 
     * @param pos
     *            The actual mouse position
     * 
     * @return A list with the nodes under the given mouse position.
     */
    public List<Node> getNodesUnderMouse(Point pos) {
        LinkedList<Node> nodesUnderMouse = new LinkedList<Node>();

        if (lastMouseMovedEventSource == null)
            // mouse is not over the view-component
            return null;

        // compare position with the position-information of all NodeComponents
        Component[] components = lastMouseMovedEventSource.getComponents();

        for (int n = 0; n < components.length; n++) {
            if (components[n] instanceof NodeComponent) {
                NodeComponent nodeComp = (NodeComponent) components[n];

                // position relative to components origin
                Point relPos = new Point((int) (pos.getX() - components[n]
                        .getX()), (int) (pos.getY() - components[n].getY()));

                if (components[n].contains(relPos)
                        && (!justAddedNodes.contains((nodeComp
                                .getGraphElement())))) {
                    // add the underlying component to the componentsUnderMouse
                    // - List
                    nodesUnderMouse.add((Node) nodeComp.getGraphElement());
                }
            }
        }

        return nodesUnderMouse;
    }

    /**
     * Returns the position-info-object for this object.
     * 
     * @return the position-info-object for this object
     */
    public PositionInfo getPositionInfo() {
        return positionInfo;
    }

    /**
     * Returns the preferences of this tool.
     * 
     * @return the preferences of this tool.
     */
    public Preferences getPrefs() {
        return prefs;
    }

    /**
     * Returns the current selection.
     * 
     * @return the current selection.
     */
    public Selection getSelection() {
        return selection;
    }

    /**
     * Returns the sub component (here: null)
     * 
     * @param name
     *            The given name of the sub component
     * 
     * @return FunctionComponent
     */
    public FunctionComponent getSubComponent(String name) {
        return null;
    }

    /**
     * Returns the ToolPlugin which controls this tool.
     * 
     * @return the ToolPlugin which controls this tool
     */
    public ToolPlugin getToolPlugin() {
        return toolPlugin;
    }

    /**
     * Returns the top-edge at the given position, null if it does not exist.
     * 
     * @param position
     *            any position
     * 
     * @return the top-edge at the given position, null if it does not exist
     */
    public Edge getTopEdge(Point position) {
        EdgeComponent topEdgeComponent = getTopEdgeComponent(position);

        if (topEdgeComponent == null)
            return null;
        else
            return (Edge) (topEdgeComponent.getGraphElement());
    }

    /**
     * Returns the EdgeComponent of the top-edge at the given position, null if
     * it does not exist
     * 
     * @param position
     *            any position
     * 
     * @return the EdgeComponent of the top-edge at the given position, null if
     *         it does not exist
     */
    public EdgeComponent getTopEdgeComponent(Point position) {
        if (lastMouseMovedEventSource == null)
            // no (view-)component available
            return null;

        // compare position with the position-information of all EdgeComponents
        Component[] components = lastMouseMovedEventSource.getComponents();

        // position = getZoomedPosition(position);

        for (int n = 0; n < components.length; n++) {
            if ((components[n] instanceof EdgeComponent)
                    && components[n].getBounds().contains(position)
                    && components[n].contains(position))
                return (EdgeComponent) (components[n]);
        }

        return null;
    }

    /**
     * Returns the top GraphElementComponent at the given position, null if it
     * does not exist. NodeComponents are preferred upon EdgeComponents.
     * 
     * @param position
     *            any position
     * 
     * @return the top GEC at the given position, null if it doesn't exist
     */
    public AbstractGraphElementComponent getTopGEComponent(Point position) {
        NodeComponent topNodeComponent = getTopNodeComponent(position);

        if (topNodeComponent != null)
            // if there is a node-component, whether shadowed by an
            // edge-component or not, return it
            return topNodeComponent;
        else
            // otherwise take the edge-component (if it exists)
            return getTopEdgeComponent(position);
    }

    /**
     * Returns the top graph-element at the given position, null if it does not
     * exist. Preferres nodes upon edges.
     * 
     * @param position
     *            any position
     * 
     * @return the top graph-element at the given position, null if it does not
     *         exist.
     */
    public GraphElement getTopGraphElement(Point position) {
        AbstractGraphElementComponent component = getTopGEComponent(position);

        if (component == null)
            return null;
        else
            return component.getGraphElement();
    }

    /**
     * Returns the top-node at the given position, null if it doesn't exist
     * 
     * @param position
     *            any position
     * 
     * @return the top-node at the given position, null if it doesn't exist
     */
    public Node getTopNode(Point position) {
        NodeComponent topNodeComponent = getTopNodeComponent(position);

        if (topNodeComponent == null)
            return null;
        else
            return (Node) (topNodeComponent.getGraphElement());
    }

    /**
     * Returns the NodeComponent of the top-node at the given position, if it
     * exists, null otherwise.
     * 
     * @param position
     *            any position
     * 
     * @return NodeComponent of the top-node at the given position, null if it
     *         does not exist
     */
    public NodeComponent getTopNodeComponent(Point position) {
        if (lastMouseMovedEventSource == null)
            // mouse is not over the view-component
            return null;

        // compare position with the position-information of all NodeComponents
        Component[] components = lastMouseMovedEventSource.getComponents();

        position = getZoomedPosition(position);

        for (int n = 0; n < components.length; n++) {
            if (components[n] instanceof NodeComponent) {
                // position relative to components origin
                Point relPos = new Point((int) (position.getX() - components[n]
                        .getX()),
                        (int) (position.getY() - components[n].getY()));

                if (components[n].contains(relPos)
                        && (!justAddedNodes
                                .contains(((NodeComponent) (components[n]))
                                        .getGraphElement())))
                    // node-component is at the right place and does not
                    // show a node just added (see doc of justAddedNodes)
                    return (NodeComponent) (components[n]);
            }
        }

        // no match => no NodeComponent here
        return null;
    }

    /**
     * Returns the support object for doing undo properly.
     * 
     * @return The support for undo.
     */
    public UndoableEditSupport getUndoSupport() {
        return undoSupport;
    }

    /**
     * Returns a reference to the JComponent currently used for painting (all
     * bindings also work on it).
     * 
     * @return a reference to the JComponent currently used for painting
     */
    public JComponent getViewComponent() {
        return this.session.getActiveView().getViewComponent();
    }

    /**
     * Classes that overwrite this method should call super.active first.
     * 
     * @see org.graffiti.plugins.modes.deprecated.Tool#activate()
     */
    @Override
    public void activate() {
        super.activate();

        toolPlugin.setActiveTool(this);

        // fetch focus (necessary for key-bindings)
        getViewComponent().requestFocusInWindow();

        if (mouseBindingsComponent == null) {
            // register mouse-bindings, if not yet done
            JComponent viewComponent = getViewComponent();
            functionManager.activateAllMouseBindings(viewComponent);
            mouseBindingsComponent = viewComponent;
        }
        updatePopupMenu();
    }

    /**
     * Adds the parameters to the Map attributesMap
     * 
     * @param coordinateAttribute
     *            the given CoordinateAttribute
     * @param coordinateAttributeCopyValue
     *            Value of the copy of the CoordinateAttribute
     */
    public void addAttributesToMap(HashMapAttribute coordinateAttribute,
            Object coordinateAttributeCopyValue) {
        attributesMap.put(coordinateAttribute, coordinateAttributeCopyValue);
    }

    /**
     * Adds the parameters to the Map attributesMap (Copy)
     * 
     * @param coordinateAttribute
     *            the given CoordinateAttribute
     * @param coordinateAttributeCopyValue
     *            Value of the copy of the CoordinateAttribute
     */
    public void addAttributesToMap(CoordinateAttribute coordinateAttribute,
            Object coordinateAttributeCopyValue) {
        attributesMap.put(coordinateAttribute, coordinateAttributeCopyValue);
    }

    /**
     * Is called after an event
     * 
     * @param position
     *            The given mouse position
     */
    public void afterEvent(Point position) {
        // See doc of justAddedNodes
        justAddedNodes.clear();

        updateHighlighting(position);
        updatePopupMenu();
    }

    /**
     * Is called before an event.
     * 
     * @param position
     *            The given mouse position
     */
    public void beforeEvent(Point position) {
    }

    /**
     * Classes that overwrite this method should call super.deactive first.
     * 
     * @see org.graffiti.plugins.modes.deprecated.Tool#deactivate()
     */
    @Override
    public void deactivate() {
        super.deactivate();

        toolPlugin.setActiveTool(null);

        if (mouseBindingsComponent != null) {
            // unregister mouse-bindings, if they are registered
            functionManager.deactivateAllMouseBindings(getViewComponent());
            mouseBindingsComponent = null;
        }

    }

    /**
     * Informs the registered listeners that the active session has changed.
     */
    public void fireSelectionChanged() {
        selectionModel.selectionChanged();
    }

    /**
     * Marks the GraphElement displayed by the GraphElementComponent.
     * 
     * @param gec
     *            any GraphElementComponent
     */
    public void mark(GraphElementComponent gec) {
        mark(gec.getGraphElement());
    }

    /**
     * Marks the given GraphElement.
     * 
     * @param graphElement
     *            any GraphElement
     */
    public void mark(GraphElement graphElement) {
        selection.add(graphElement);
        fireSelectionChanged();
    }

    /**
     * Stores the mouse position and the component under the mouse
     * 
     * @param e
     *            The given MouseEvent
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        positionInfo.setMousePosition(e.getPoint());
        lastMouseMovedEventSource = (Container) (e.getSource());
    }

    /**
     * Activates key bindings of the view component.
     * 
     * @param e
     *            The given MouseEvent.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if (keyBindingsComponent == null) {
            // register key-bindings if necessary
            JComponent viewComponent = getViewComponent();

            if (e.getComponent() == viewComponent) {
                activateKeyBindings(viewComponent);
                viewComponent.requestFocusInWindow();
                keyBindingsComponent = viewComponent;
            }
        }
    }

    /**
     * Deactivates the key bindings.
     * 
     * @param e
     *            The given MouseEvent
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (keyBindingsComponent != null) {
            // unregister key-bindings if necessary
            deactivateKeyBindings(keyBindingsComponent);
            keyBindingsComponent = null;
        }
    }

    /**
     * Stores mouse-position and the component under the mouse
     * 
     * @param e
     *            The given MouseEvent
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        // 
        positionInfo.setMousePosition(e.getPoint());
        lastMouseMovedEventSource = (Container) (e.getSource());
    }

    /**
     * Performs operations that need to be done after a new node was added to
     * the graph. Must always be called after adding a new node!!!
     * 
     * @param node
     *            the newly added Node
     */
    public void nodeAdded(Node node) {
        // see doc of justAddedNode for an explanation
        justAddedNodes.add(node);
    }

    /**
     * Is called when session has changed.
     * 
     * @param s
     *            The given session.
     * 
     * @see org.graffiti.session.SessionListener#sessionChanged(Session)
     */
    @Override
    public void sessionChanged(Session s) {
        super.sessionChanged(s);

        if (keyBindingsComponent != null) {
            // We have registered key-bindings somewhere before => remove them
            deactivateKeyBindings(keyBindingsComponent);
            keyBindingsComponent = null;
        }

        if (mouseBindingsComponent != null) {
            // We have registered mouse-bindings somewhere => remove them
            functionManager.deactivateAllMouseBindings(mouseBindingsComponent);
            mouseBindingsComponent = null;
        }

        // I don't know an explanation for this cast except "it works" ;-)
        this.session = (EditorSession) s;

        if (s != null) {
            // there is a new active session
            // so we have a new selection
            this.selectionModel = this.session.getSelectionModel();
            this.selection = selectionModel.getActiveSelection();

            if (isActive()) {
                // register mouse-bindings only if _this_ tool is active
                JComponent viewComponent = getViewComponent();
                functionManager.activateAllMouseBindings(viewComponent);
                mouseBindingsComponent = viewComponent;
            }
        } else {
            // there is currently no active session.
            this.selectionModel = null;

            // seems that a selection is needed even if no session is active
            // (code copied from the old tools)
            this.selection = new Selection(DEFAULT_SELECTION_NAME);
        }
    }

    /**
     * Switches to {@link #DEFAULT_MODE}.
     */
    public void switchToDefaultMode() {
        // System.out.println("AbstractEditingTool: switched to default mode");
        mode = DEFAULT_MODE;
    }

    /**
     * Switches to {@link #MULTI_COMMAND_MODE}.
     */
    public void switchToMultiCommandMode() {
        mode = MULTI_COMMAND_MODE;
    }

    /**
     * Unmarks the GraphElement displayed by the given GraphElementComponent.
     * 
     * @param gec
     *            any GraphElementComponent
     */
    public void unmark(GraphElementComponent gec) {
        unmark(gec.getGraphElement());
    }

    /**
     * Unmarks the given GraphElement.
     * 
     * @param graphElement
     *            any GraphElement
     */
    public void unmark(GraphElement graphElement) {
        selection.remove(graphElement);
        fireSelectionChanged();
    }

    /**
     * Unmarks all (Graph)Elements being marked.
     */
    public void unmarkAll() {
        selection.clear();
        fireSelectionChanged();
    }

    /**
     * Activates all key-bindings, using the given JComponent.
     * 
     * @param viewComponent
     *            JComponent to be used
     */
    protected abstract void activateKeyBindings(JComponent viewComponent);

    /**
     * Deactivates all key-bindings, using the given JComponent.
     * 
     * @param viewComponent
     *            JComponent to be used
     */
    protected abstract void deactivateKeyBindings(JComponent viewComponent);

    /**
     * Sets the docking at source and target and edge.
     * 
     * @param edgeGraphics
     *            The given EdgeGraphicAttribute
     */
    protected void setDocking(EdgeGraphicAttribute edgeGraphics) {
        DockingAttribute dock = edgeGraphics.getDocking();
        dock.setSource("");
        dock.setTarget("");
        edgeGraphics.setDocking(dock);
    }

    // ---------------------------------------[MH]-----------------------------------------------

    /**
     * Adds a menu alignMenu to the popupMenu [MH]
     * 
     * @return popupMenu
     */
    protected JPopupMenu addSubAlignMenu() {
        try {
            // creates a new Menu (with subitems)
            alignMenu = new JMenu("Alignment");

            // Alignment
            MenuUtil
                    .addSubItemWith1Param(alignMenu, "Top", "align-tool.align",
                            toolPlugin, "alignDirection", TOP,
                            "Aligns the marked node to the top boarder of the actual node");

            MenuUtil
                    .addSubItemWith1Param(alignMenu, "Bottom",
                            "align-tool.align", toolPlugin, "alignDirection",
                            BOTTOM,
                            "Aligns the marked node to the bottom boarder of the actual node");
            MenuUtil
                    .addSubItemWith1Param(alignMenu, "Left",
                            "align-tool.align", toolPlugin, "alignDirection",
                            LEFT,
                            "Aligns the marked node to the left boarder of the actual node");
            MenuUtil
                    .addSubItemWith1Param(alignMenu, "Right",
                            "align-tool.align", toolPlugin, "alignDirection",
                            RIGHT,
                            "Aligns the marked node to the right boarder of the actual node");
            MenuUtil.addSubItemWith1Param(alignMenu, "Center horizontal",
                    "align-tool.align", toolPlugin, "alignDirection",
                    CENTER_HORIZONTAL,
                    "Aligns the marked node to the center horizontal boarder of the"
                            + " actual node");
            MenuUtil.addSubItemWith1Param(alignMenu, "Center vertical",
                    "align-tool.align", toolPlugin, "alignDirection",
                    CENTER_VERTICAL,
                    "Aligns the marked node to the center vertical boarder of the"
                            + " actual node");
            MenuUtil.addSubItemWith1Param(alignMenu, "Center",
                    "align-tool.align", toolPlugin, "alignDirection", CENTER,
                    "Aligns the marked node to the center of the actual node");

            // adds the menu to the popupMenu
            popupMenu.addSeparator();
            popupMenu.add(alignMenu);
        } catch (NoSuchFunctionActionException e) {
            logger.finer("AbstractEditingTool.addSubAlignMenu says:Function "
                    + e.getFunction() + " not found! ====> Aborting NOW!!!");
            System.exit(-1);
        } catch (InvalidParameterException e) {
            logger
                    .finer("When adding submenu to popup-menu: "
                            + e.getMessage());
            System.exit(-1);
        }

        return popupMenu;
    }

    /**
     * Constructs the popup-menu.
     * 
     * @return the newly constructed popup-menu
     */
    protected JPopupMenu constructPopupMenu() {
        GraffitiSingleton graffiti = GraffitiSingleton.getInstance();
        MainFrame mainFrame = graffiti.getMainFrame();
        JPopupMenu popupMenu = new JPopupMenu("");

        cutItem = new JMenuItem(mainFrame.getEditCut());
        popupMenu.add(cutItem);

        copyItem = new JMenuItem(mainFrame.getEditCopy());
        popupMenu.add(copyItem);

        pasteItem = new JMenuItem(mainFrame.getEditPaste());
        popupMenu.add(pasteItem);

        deleteItem = new JMenuItem(mainFrame.getEditDelete());
        popupMenu.add(deleteItem);

        popupMenu.addSeparator();

        AddBendToEdgeAction addBendToEdgeAction = new AddBendToEdgeAction(
                mainFrame, this);
        addBendItem = new JMenuItem(addBendToEdgeAction);
        popupMenu.add(addBendItem);

        RemoveBendsAction removeBendsAction = new RemoveBendsAction(mainFrame,
                this);
        removeBendsItem = new JMenuItem(removeBendsAction);
        popupMenu.add(removeBendsItem);

        return popupMenu;
    }

    /**
     * Returns a reference to the popup-menu.
     * 
     * @return a reference to the popup-menu
     */
    JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    /**
     * Returns if graph-elements the mouse points to should be highlighted
     * 
     * @return if graph-elements the mouse points to should be highlighted
     */
    private boolean highlightPointedElements() {
        return highlightPointedElements;
    }

    /**
     * Should graph-elements the mouse points to be highlighted.
     * 
     * @param highlightPointedElements
     *            <code>true</code> if they should be highlighted.
     */
    protected void setHighlightPointedElements(boolean highlightPointedElements) {
        this.highlightPointedElements = highlightPointedElements;
    }

    /**
     * Updates highlighting of graph-elements; tries to highlight an element at
     * the given position.
     * 
     * @param position
     *            any position
     */
    private void updateHighlighting(Point position) {
        if (highlightPointedElements()) {
            // If configuration says "no", do nothing...
            // determine GEC at the given position; for rules see below
            GraphElementComponent pointedComponent;
            NodeComponent topNodeComponent = getTopNodeComponent(position);
            EdgeComponent topEdgeComponent = getTopEdgeComponent(position);

            // With respect to highlighting, nodes are prefered upon edges.
            // This means, if an edge shadows a node, mouse pointing to both,
            // the node will be highlighted.
            if (topNodeComponent != null) {
                pointedComponent = topNodeComponent;
            } else if (topEdgeComponent != null) {
                pointedComponent = topEdgeComponent;
            } else {
                pointedComponent = null;
            }

            if (pointedComponent == null) {
                // nothing to be highlighted is at the given position, so the
                // objects already highlighted should not be highlighted any
                // longer
                if (highlightedGEC != null) {
                    unmark(highlightedGEC);
                    highlightedGEC = null;
                }
            } else if (pointedComponent != highlightedGEC) {
                // a new object must be highlighted => change highlighting
                if (highlightedGEC != null) {
                    unmark(highlightedGEC);
                    highlightedGEC = null;
                }

                if (!(isMarked(pointedComponent))) {
                    // marked objects are not highlighted
                    highlight(pointedComponent);
                    highlightedGEC = pointedComponent;
                }
            } else {
                // case: pointedComponent == highlightedGEC != null
                if (isMarked(pointedComponent)) {
                    // Happens, if pointedComponent was marked in the time
                    // since the mouse entered it. (marking removes the
                    // highlighting)
                    highlightedGEC = null;
                }
            }
        }
    }

    /**
     * Updates the popup-menu. Constructs it if not yet done. (Tests which tool
     * is active and constructs then the items of the popupMenu [MH])
     */
    private void updatePopupMenu() {
        if (popupMenu == null) {
            popupMenu = constructPopupMenu();

            popupMenu.addPopupMenuListener(new PopupMenuListener() {
                public void popupMenuCanceled(PopupMenuEvent e) {
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                }

                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    // store position so we can pass it to the
                    // FunctionAction when a menu-entry is invoced
                    Point mousePosition = positionInfo.getMousePosition();
                    positionInfo.setLastPopupPosition(mousePosition);
                }
            });
        }

        // change into selectionTool
        if (toolPlugin.getActiveTool() instanceof AlignTool) {
            // if alignMenu not exists yet, then add it
            if (alignMenu == null) {
                addSubAlignMenu();
            }

            if (toolPlugin.getActiveTool().getSelection().isEmpty()) {
                alignMenu.setEnabled(false);
            } else {
                alignMenu.setEnabled(true);
            }
        }
    }

    /**
     * Return the currently active JComponent, e.g. to set the cursor to use.
     * 
     * @return Returns the active JComponent.
     */
    public JComponent getActiveJComponent() {
        if (mainFrame == null) {
            this.mainFrame = GraffitiSingleton.getInstance().getMainFrame();
        }

        return mainFrame.getActiveEditorSession().getActiveView()
                .getViewComponent();
    }

    @Override
    public boolean isViewListener() {
        return true;
    }

    /**
     * This method is called when the view changes. This method is not called
     * when another session is activated. Implement <code>SessionListener</code>
     * if you are interested in session changed events.
     * 
     * @param newView
     *            the new View.
     */
    public void viewChanged(View newView) {
        if (keyBindingsComponent != null) {
            // We have registered key-bindings somewhere before => remove them
            deactivateKeyBindings(keyBindingsComponent);
            keyBindingsComponent = null;
        }

        if (mouseBindingsComponent != null) {
            // We have registered mouse-bindings somewhere => remove them
            functionManager.deactivateAllMouseBindings(mouseBindingsComponent);
            mouseBindingsComponent = null;
        }

        if (isActive()) {
            // register mouse-bindings only if _this_ tool is active
            JComponent viewComponent = getViewComponent();
            functionManager.activateAllMouseBindings(viewComponent);
            mouseBindingsComponent = viewComponent;
        }
    }

    /**
     * Returns the coordinates of the stored bends.
     * 
     * @return the coordinates of the stored bends.
     */
    public LinkedList<CoordinateAttribute> getBendCoords() {
        return bendCoords;
    }

    /**
     * Sets the bend coordinates.
     * 
     * @param bendCoords
     *            The bend coordinates to set.
     */
    public void setBendCoords(LinkedList<CoordinateAttribute> bendCoords) {
        this.bendCoords = bendCoords;
    }

    /**
     * Returns the node coordinates.
     * 
     * @return the node coordinates.
     */
    public CoordinateAttribute[] getNodeCoords() {
        return nodeCoords;
    }

    /**
     * Sets the node coordinates.
     * 
     * @param nodeCoords
     *            the node coordinates.
     */
    public void setNodeCoords(CoordinateAttribute[] nodeCoords) {
        this.nodeCoords = nodeCoords;
    }

    /**
     * Returns the node dimensions.
     * 
     * @return the node dimensions.
     */
    public DimensionAttribute[] getNodeDims() {
        return nodeDims;
    }

    /**
     * Sets the node dimensions.
     * 
     * @param nodeDims
     *            the node dimensions to set.
     */
    public void setNodeDims(DimensionAttribute[] nodeDims) {
        this.nodeDims = nodeDims;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
