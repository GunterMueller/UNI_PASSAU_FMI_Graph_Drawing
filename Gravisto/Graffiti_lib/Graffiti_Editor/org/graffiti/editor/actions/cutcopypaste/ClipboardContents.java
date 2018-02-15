// =============================================================================
//
//   ClipboardContents.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ClipboardContents.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions.cutcopypaste;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graph.Edge;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.view.View;
import org.graffiti.plugin.view.View2D;
import org.graffiti.selection.Selection;

/**
 * Class is a subclass of the Selection class that implements the Transferable
 * interface due to be written to the system clipboard
 * 
 * @author MH
 */
public class ClipboardContents extends Selection implements Transferable {

    /**
     * The DataFlavor that implements the kind of data that will be written to
     * the system clipboard and will be pasted into Gravisto
     */
    public static DataFlavor selectionFlavor = new DataFlavor(
            ClipboardContents.class, "Selection");

    /**
     * The DataFlavor that implements the kind of data that will be written to
     * the system clipboard and will be pasted as an image into other
     * applications, e.g. MS Word or MS Powerpoint
     */
    public static DataFlavor imageFlavor = DataFlavor.imageFlavor;

    /** The image that will be pasted into the application. */
    private static Image image;

    // /** The space for the border of the image */
    // private static final int BORDER = 30;
    //
    // /**
    // * Saves edges that are not to be saved in the system clipboard during the
    // * creation of the image
    // */
    // private List<Edge> edgesNotShown = new LinkedList<Edge>();
    //
    // /**
    // * Saves nodes that are not to be saved in the system clipboard during the
    // * creation of the image
    // */
    // private List<Node> nodesNotShown = new LinkedList<Node>();

    /** Indicates if this selection was cut or copied from the graph */
    private int numOfCopiesInGraph;

    /**
     * Creates a new instance of the ClipboardContents class
     * 
     * @param selection
     *            is saved in the elements of the ClipboardContents
     * @param numOfCopies
     *            DOCUMENT ME!
     */
    public ClipboardContents(Selection selection, int numOfCopies) {
        super();
        this.addSelection(selection);
        this.numOfCopiesInGraph = numOfCopies;
        image = createImage();
    }

    /**
     * Returns true if the DataFlavor is supported
     * 
     * @param arg0
     *            the DataFlavor that is tested
     * 
     * @return true if the DataFlavor is suppported
     */
    public boolean isDataFlavorSupported(DataFlavor arg0) {
        return (arg0.equals(selectionFlavor) || arg0.equals(imageFlavor)
                && image != null);
    }

    /**
     * Returns if this selection was cut or copied from the graph
     * 
     * @return 0 if the selection was cut
     */
    public int getNumOfCopiesInGraph() {
        return numOfCopiesInGraph;
    }

    /**
     * Gets the data that is saved in this class. In this case the data is
     * represented by the class itself
     * 
     * @param arg0
     *            identifies if the class itself or the image should be returned
     * 
     * @return the class itself or the image of the graph
     * 
     * @throws UnsupportedFlavorException
     *             if DataFlavor is not supported
     * @throws IOException
     *             if the data is no longer present for some reason
     */
    public Object getTransferData(DataFlavor arg0)
            throws UnsupportedFlavorException, IOException {
        // Check if the DataFlavor is supported
        if (isDataFlavorSupported(arg0)) {
            // Gravisto itself is calling the clipboard
            if (arg0.equals(selectionFlavor))
                return this;
            else if (arg0.equals(imageFlavor))
                return image;

            // throw an exception if DataFlavor is not supported
        } else
            throw new UnsupportedFlavorException(arg0);

        return null;
    }

    /**
     * Returns all dataFlavors that are supported by Gravisto
     * 
     * @return array of DataFlavors
     */
    public DataFlavor[] getTransferDataFlavors() {
        if (image != null)
            return new DataFlavor[] { selectionFlavor, imageFlavor };
        return new DataFlavor[] { selectionFlavor };
    }

    /**
     * DOCUMENT ME!
     */
    public void decreaseNumOfCopies() {
        this.numOfCopiesInGraph--;
    }

    /**
     * DOCUMENT ME!
     */
    public void increaseNumOfCopies() {
        this.numOfCopiesInGraph++;
    }

    // /**
    // * Gets the current graph element from the GEMap
    // *
    // * @param ge the graph element whose current version is searched
    // * @param geMap the map in which the graph elements are saved
    // *
    // * @return the current graph element
    // */
    // private GraphElement getCurrentGraphElement(GraphElement ge,
    // Map<GraphElement, GraphElement> geMap)
    // {
    // GraphElement newGE = ge;
    //
    // if (geMap.containsKey(ge))
    // {
    // newGE = geMap.get(ge);
    //
    // while (geMap.containsKey(newGE))
    // {
    // newGE = geMap.get(newGE);
    // }
    // }
    //
    // return newGE;
    // }

    // /**
    // * Gets the maximum coordinate of the graphs nodes and bends. This is
    // * necessary for the size of the image
    // *
    // * @param height true if the maximum height is needed, false for width
    // *
    // * @return maximum value for height and width
    // */
    // private int getMax(boolean height)
    // {
    // MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();
    // Graph graph = mainFrame.getActiveEditorSession().getGraph();
    //
    // Iterator<Node> checkNodes = graph.getNodesIterator();
    // Iterator<Edge> checkEdges = graph.getEdgesIterator();
    //
    // int max = 0;
    //
    // while (checkNodes.hasNext())
    // {
    // Node node = checkNodes.next();
    // CollectionAttribute attributes = node.getAttributes();
    // NodeGraphicAttribute nodeGraphicAtt = (NodeGraphicAttribute)attributes
    // .getAttribute(GraphicAttributeConstants.GRAPHICS);
    // CoordinateAttribute cooAtt = nodeGraphicAtt.getCoordinate();
    // double thickness = nodeGraphicAtt.getFrameThickness();
    //
    // if (height)
    // {
    // int nodeHeight = (int)(cooAtt.getX() + (nodeGraphicAtt
    // .getDimension().getHeight() / 2))
    // + (int)thickness;
    // max = Math.max(max, nodeHeight);
    // }
    // else
    // {
    // int nodeWidth = (int)(cooAtt.getY() + (nodeGraphicAtt
    // .getDimension().getWidth() / 2))
    // + (int)thickness;
    // max = Math.max(max, nodeWidth);
    // }
    // }
    //
    // while (checkEdges.hasNext())
    // {
    // Edge edge = checkEdges.next();
    // CollectionAttribute attributes = edge.getAttributes();
    // EdgeGraphicAttribute edgeGraphicAtt = (EdgeGraphicAttribute)attributes
    // .getAttribute(GraphicAttributeConstants.GRAPHICS);
    // SortedCollectionAttribute bends = edgeGraphicAtt.getBends();
    // double thickness = edgeGraphicAtt.getFrameThickness();
    //
    // for (String s: bends.getCollection().keySet())
    // {
    // CoordinateAttribute bend = (CoordinateAttribute)bends
    // .getCollection().get(s);
    //
    // if (height)
    // {
    // max = Math.max(max, (int)bend.getX() + (int)thickness);
    // }
    // else
    // {
    // max = Math.max(max, (int)bend.getY() + (int)thickness);
    // }
    // }
    // }
    //
    // return max + BORDER;
    // }

    // /**
    // * Gets the minimum coordinate of the graphs nodes and bends. This is
    // * necessary for the size of the image
    // *
    // * @param height true if the maximum height is needed, false for width
    // *
    // * @return minimum value for height and width
    // */
    // private int getMin(boolean height)
    // {
    // MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();
    // Graph graph = mainFrame.getActiveEditorSession().getGraph();
    //
    // Iterator<Node> checkNodes = graph.getNodesIterator();
    // Iterator<Edge> checkEdges = graph.getEdgesIterator();
    //
    // int min = Integer.MAX_VALUE;
    //
    // while (checkNodes.hasNext())
    // {
    // Node node = checkNodes.next();
    // CollectionAttribute attributes = node.getAttributes();
    // NodeGraphicAttribute nodeGraphicAtt = (NodeGraphicAttribute)attributes
    // .getAttribute(GraphicAttributeConstants.GRAPHICS);
    // CoordinateAttribute cooAtt = nodeGraphicAtt.getCoordinate();
    // double thickness = nodeGraphicAtt.getFrameThickness();
    //
    // if (height)
    // {
    // int nodeHeight = (int)(cooAtt.getX() - (nodeGraphicAtt
    // .getDimension().getHeight() / 2))
    // - (int)thickness;
    // min = Math.min(min, nodeHeight);
    // }
    // else
    // {
    // int nodeWidth = (int)(cooAtt.getY() - (nodeGraphicAtt
    // .getDimension().getWidth() / 2))
    // - (int)thickness;
    // min = Math.min(min, nodeWidth);
    // }
    // }
    //
    // while (checkEdges.hasNext())
    // {
    // Edge edge = checkEdges.next();
    // CollectionAttribute attributes = edge.getAttributes();
    // EdgeGraphicAttribute edgeGraphicAtt = (EdgeGraphicAttribute)attributes
    // .getAttribute(GraphicAttributeConstants.GRAPHICS);
    // SortedCollectionAttribute bends = edgeGraphicAtt.getBends();
    // Iterator<String> checkBends = bends.getCollection().keySet()
    // .iterator();
    // double thickness = edgeGraphicAtt.getFrameThickness();
    //
    // while (checkBends.hasNext())
    // {
    // CoordinateAttribute bend = (CoordinateAttribute)bends
    // .getCollection().get(checkBends.next());
    //
    // if (height)
    // {
    // min = Math.min(min, (int)bend.getX() - (int)thickness);
    // }
    // else
    // {
    // min = Math.min(min, (int)bend.getY() - (int)thickness);
    // }
    // }
    // }
    //
    // return Math.max(0, min - BORDER);
    // }

    // /**
    // * Gets all graph elements that should not be painted (all graph elements
    // * that are in the graph but not in the clipboard) and saved it into
    // * nodesNotShown and edgesNotShown
    // */
    // private void getNotMarkedElements()
    // {
    // nodesNotShown = new LinkedList<Node>();
    // edgesNotShown = new LinkedList<Edge>();
    //
    // MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();
    // Graph graph = mainFrame.getActiveEditorSession().getGraph();
    //
    // // Test every edge of the graph if it is saved in the clipboard
    // Iterator<Edge> getNotMarkedEdges = graph.getEdgesIterator();
    //
    // while (getNotMarkedEdges.hasNext())
    // {
    // Edge edge = getNotMarkedEdges.next();
    //
    // if (!this.getEdges().contains(edge))
    // {
    // edgesNotShown.add(edge);
    // }
    // }
    //
    // // Test every node of the graph if it is saved in the clipboard
    // Iterator<Node> getNotMarkedNodes = graph.getNodesIterator();
    //
    // while (getNotMarkedNodes.hasNext())
    // {
    // Node node = getNotMarkedNodes.next();
    //
    // if (!this.getNodes().contains(node))
    // {
    // nodesNotShown.add(node);
    // }
    // }
    // }

    // /**
    // * Addes nodes and edges to the graph
    // *
    // * @param nodes the nodes to be added
    // * @param edges the edges to be added
    // */
    // private void addElements(List<Node> nodes, List<Edge> edges)
    // {
    // MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();
    // Graph graph = mainFrame.getActiveEditorSession().getGraph();
    // Map<GraphElement, GraphElement> geMap = mainFrame
    // .getActiveEditorSession().getGraphElementsMap();
    //
    // for (Node node: nodes)
    // geMap.put(node, graph.addNodeCopy(node));
    //
    // for (Edge edge: edges)
    // {
    // Node source = (Node)getCurrentGraphElement(edge.getSource(), geMap);
    // Node target = (Node)getCurrentGraphElement(edge.getTarget(), geMap);
    // geMap.put(edge, graph.addEdgeCopy(edge, source, target));
    // }
    // }

    // /**
    // * Creates an image of the cut or copied graph elements, by removing all
    // * other elements from the graph, painting the viewcomponent and readding
    // * all elements
    // *
    // * @return the image of the cut or copied elements
    // */
    // private Image _createImage()
    // {
    // MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();
    // Graph graph = mainFrame.getActiveEditorSession().getGraph();
    // JComponent viewComponent = mainFrame.getActiveEditorSession()
    // .getActiveView().getViewComponent();
    //
    // // Gets all elements from the graph that are not cut or copied
    // getNotMarkedElements();
    //
    // // Removes all elements that are not cut or copied from the graph
    // removeElements(edgesNotShown, nodesNotShown);
    // mainFrame.getActiveEditorSession().getSelectionModel()
    // .getActiveSelection().clear();
    // mainFrame.getActiveEditorSession().getSelectionModel()
    // .selectionChanged();
    //
    // // Tests if the elements to be drawn are cut or copied
    // if (this.numOfCopiesInGraph == 0)
    // {
    // // If they are cut, re-add them to the graph
    // addElements(this.getNodes(), this.getEdges());
    // }
    //
    // int minHeight = getMin(true);
    // int minWidth = getMin(false);
    //
    // moveElements(-minWidth, -minHeight);
    //
    // int height = getMax(true);
    // int width = getMax(false);
    //
    // // Creates and paints the image
    // Image image = viewComponent.createImage(height, width);
    //
    // Graphics graphics = image.getGraphics();
    //
    // graphics.setColor(viewComponent.getBackground());
    // graphics.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
    //
    // viewComponent.print(graphics);
    //
    // moveElements(minWidth, minHeight);
    //
    // // Tests if the elements to be drawn are cut or copied
    // if (this.numOfCopiesInGraph == 0)
    // {
    // // If they were cut, remove them from the graph
    // removeElements(this.getEdges(), this.getNodes());
    // }
    //
    // mainFrame.getActiveEditorSession().getSelectionModel()
    // .getActiveSelection().addAll(graph.getGraphElements());
    // mainFrame.getActiveEditorSession().getSelectionModel()
    // .selectionChanged();
    //
    // // Readd all other elements to the graph
    // addElements(nodesNotShown, edgesNotShown);
    //
    // return image;
    // }

    private Image createImage() {

        MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();
        View curView = mainFrame.getActiveEditorSession().getActiveView();

        if (!(curView instanceof View2D))
            return null;

        Class<? extends View2D> c = ((View2D) curView).getClass();
        View2D view;
        try {
            view = c.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }

        Graph graph = new FastGraph();
        HashMap<Node, Node> nodes = new HashMap<Node, Node>();

        for (Node node : getNodes()) {
            nodes.put(node, graph.addNodeCopy(node));
        }
        for (Edge edge : getEdges()) {
            graph.addEdgeCopy(edge, nodes.get(edge.getSource()), nodes.get(edge
                    .getTarget()));
        }

        view.setGraph(graph);

        Graphics2D graphics = (new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB)).createGraphics();
        view.print(graphics, 1, 1);
        graphics.dispose();
        Rectangle2D rect = view.getViewport().getLogicalElementsBounds();
        int height = (int) rect.getHeight();
        int width = (int) rect.getWidth();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        graphics.translate(-rect.getMinX(), -rect.getMinY());
        try {
            view.print(graphics, width, height);
        } catch (UnsupportedOperationException ex) {
            return null;
        }

        return image;
    }

    // /**
    // * Moves all nodes to the top left of the viewComponent in order to get a
    // * smaller picture (and moves them back) afterwards
    // *
    // * @param height the value the height should change
    // * @param width the value the width should change
    // */
    // private void moveElements(int height, int width)
    // {
    // MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();
    // Graph graph = mainFrame.getActiveEditorSession().getGraph();
    //
    // Iterator<Node> checkNodes = graph.getNodesIterator();
    // Iterator<Edge> checkEdges = graph.getEdgesIterator();
    //
    // while (checkNodes.hasNext())
    // {
    // Node node = checkNodes.next();
    // CollectionAttribute attributes = node.getAttributes();
    // NodeGraphicAttribute nodeGraphicAtt = (NodeGraphicAttribute)attributes
    // .getAttribute(GraphicAttributeConstants.GRAPHICS);
    // CoordinateAttribute cooAtt = nodeGraphicAtt.getCoordinate();
    // cooAtt.setX(cooAtt.getX() + width);
    // cooAtt.setY(cooAtt.getY() + height);
    // }
    //
    // while (checkEdges.hasNext())
    // {
    // Edge edge = checkEdges.next();
    // CollectionAttribute attributes = edge.getAttributes();
    // EdgeGraphicAttribute edgeGraphicAtt = (EdgeGraphicAttribute)attributes
    // .getAttribute(GraphicAttributeConstants.GRAPHICS);
    // SortedCollectionAttribute bends = edgeGraphicAtt.getBends();
    //
    // for (String string: bends.getCollection().keySet())
    // {
    // CoordinateAttribute bend = (CoordinateAttribute)bends
    // .getCollection().get(string);
    //
    // bend.setX(bend.getX() + width);
    // bend.setY(bend.getY() + height);
    // }
    // }
    // }

    // /**
    // * Removes nodes and edges from the graph
    // *
    // * @param edges the edges to be removed
    // * @param nodes the nodes to be removed
    // */
    // private void removeElements(List<Edge> edges, List<Node> nodes)
    // {
    // MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();
    // Graph graph = mainFrame.getActiveEditorSession().getGraph();
    // Map<GraphElement, GraphElement> geMap = mainFrame
    // .getActiveEditorSession().getGraphElementsMap();
    //
    // for (Edge edge: edges)
    // graph.deleteEdge((Edge)getCurrentGraphElement(edge, geMap));
    //
    // for (Node node: nodes)
    // graph.deleteNode((Node)getCurrentGraphElement(node, geMap));
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
