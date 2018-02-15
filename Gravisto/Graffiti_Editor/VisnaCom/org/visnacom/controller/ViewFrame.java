package org.visnacom.controller;

import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import javax.swing.*;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.visnacom.dom.DOMWriter;
import org.visnacom.model.*;
import org.visnacom.view.*;
import org.w3c.dom.Document;


/**
 * 
 * @author F. Pfeiffer
 * 
 * This class represents the internal frames containing the drawing space.
 */
public class ViewFrame extends JInternalFrame {

	// the drawing and editing space
	private ViewPanel viewPanel;

	// default parser name for xerces
	private final String DEFAULT_PARSER_NAME = "dom.wrappers.DOMParser";

	// current filename associated with frame
	private String filename = "";

	// true if graph in this frame has been saved
	private boolean saved;

	/**
	 * Constructor.
	 * 
	 * @param title
	 *            Title of frame.
	 * @param vPanel
	 *            Associated editing space.
	 * @param tog
	 *            Current editing mode.
	 */
	public ViewFrame(String title, ViewPanel vPanel, int tog) {
		super(title + " - View 1", true, true);
		filename = title;
		setIconifiable(true);
		setMaximizable(true);
		viewPanel = vPanel;
		// sets editing mode
		viewPanel.setToggle(tog);
		saved = false;
		pack();
	}

	/**
	 * Changes editing mode.
	 * 
	 * @param toggleButton
	 *            The new editing mode.
	 */
	public void setToggle(int toggleButton) {
		viewPanel.setToggle(toggleButton);
	}

	/**
	 * Repaints the complete drawing space.
	 */
	public void drawAll() {
		viewPanel.repaint();
	}

	/**
	 * Deletes all selected items.
	 *  
	 */
	public void delSelection() {
		viewPanel.delSelection();
	}

	/**
	 * Gets the selected nodes of a viewframe.
	 * 
	 * @return List of selected nodes.
	 */
	public LinkedList getSelectedNodes() {
		return viewPanel.getSelectedNodes();
	}

	/**
	 * Pastes the copied nodes into the active frame.
	 * 
	 * @param nodes
	 *            A hashmap containing information about the nodes to be pasted.
	 * @param clusters
	 *            A hashmap containing information about the inner nodes to be
	 *            pasted.
	 * @param coll
	 *            List containing contracted (contracted) nodes.
	 * @param geoCoords
	 *            A hashmap containing geometrical information about the nodes
	 *            to be pasted.
	 * @param edges
	 *            A hashmap containing information about the edges to be pasted.
	 *            (Edges and polylines)
	 * @param copiedClusDepth
	 *            Cluster depths.
	 */
	public void pasteNodes(LinkedList nodes, HashMap clusters, LinkedList coll,
			HashMap geoCoords, HashMap edges, HashMap copiedClusDepth) {
		viewPanel.pasteNodes(nodes, clusters, coll, geoCoords, edges,
				copiedClusDepth);
	}

	/**
	 * Gets the preference setting for view panel.
	 * 
	 * @return Preference settings.
	 */
	public Preferences getPrefs() {
		return viewPanel.getPrefs();
	}

	/**
	 * Gets the panel in this frame.
	 * 
	 * @return The panel.
	 */
	public ViewPanel getViewPanel() {
		return viewPanel;
	}

	/**
	 * Loads a graph from a file.
	 * 
	 * @param fileName
	 *            The file which is to be opened.
	 */
	public void loadFile(String fileName) {
		saved = true;
		viewPanel.getPrefs().algorithm = "default";
		viewPanel.getGeometry().setDrawingStyle(
				new DefaultDrawingStyle(viewPanel.getGeometry()));
		viewPanel.getPrefs().animation = "none";
		viewPanel.setAnimationStyle(new NoAnimation(viewPanel.getGeometry(),
				viewPanel));
		DOMWriter dom = null;
		try {
			dom = new DOMWriter("UTF8", false);
		} catch (Exception exc) {
			System.err.println(exc);
		}
		if (!fileName.equals("")) {
			this.setFilename(fileName);
			dom.print(fileName, DEFAULT_PARSER_NAME);

			// gets saved parent relations
			HashMap parMap = dom.getParMap();

			java.util.List ids = dom.getIds();

			// maps saved ids to new nodes
			HashMap idMapping = new HashMap();
			// maps saved edge ids to new edges
			HashMap idMappingEdges = new HashMap();

			java.util.List edges = dom.getEdges();
			Iterator it;
			Iterator itId = ids.iterator();

			// creates new nodes according to file
			while (itId.hasNext()) {
				String nextId = (String) itId.next();
				// gets the parent node (which has already been created)
				Node parent = (Node) idMapping.get(parMap.get(nextId));
				if (parent == null) {
					parent = viewPanel.getView().getRoot();
				}
				Node n = viewPanel.getView().newLeaf(parent);
				idMapping.put(nextId, n);
			}

			// creates new edges according to file
			it = edges.iterator();
			while (it.hasNext()) {
				java.util.List l = (java.util.List) it.next();
				String source = (String) l.get(0);
				String target = (String) l.get(1);
				String edgeId = (String) l.get(2);
				Node sourceNode = null;
				Node targetNode = null;
				sourceNode = (Node) idMapping.get(source);
				targetNode = (Node) idMapping.get(target);
				Edge edge = viewPanel.getView().newEdge(sourceNode, targetNode);
				idMappingEdges.put(edgeId, edge);
			}

			// makes views
			int noViews = dom.getViews().size();
			it = dom.getViews().iterator();
			for (int i = 0; i < noViews - 1; i++) {
				java.util.List l = (java.util.List) it.next();
				this.makeView(idMapping, idMappingEdges, dom, l, false);
			}
			java.util.List l = (java.util.List) it.next();
			this.makeView(idMapping, idMappingEdges, dom, l, true);
			viewPanel.repaint();
		}
	}

	/**
	 * Makes a view during loading of file.
	 * 
	 * @param idMapping
	 *            Maps saved ids to real nodes.
	 * @param idMappingEdges
	 *            Maps saved edge ids to real edges.
	 * @param dom
	 *            DOMWriter object.
	 * @param l
	 *            List containing view information.
	 * @param origView
	 *            True iff view in this frame is to be dealt with.
	 */
	private void makeView(HashMap idMapping, HashMap idMappingEdges,
			DOMWriter dom, java.util.List l, boolean origView) {

		// list of contracted nodes in view
		LinkedList collNodes = new LinkedList();
		HashMap nodes = (HashMap) l.get(1);
		HashMap polys = (HashMap) l.get(2);
		Iterator itColl = idMapping.entrySet().iterator();
		while (itColl.hasNext()) {
			Map.Entry entry = (Map.Entry) itColl.next();
			Node value = (Node) entry.getValue();
			String key = (String) entry.getKey();
			Node parent = viewPanel.getBaseGraph().getParent(value);
			if (!nodes.containsKey(key) && !collNodes.contains(parent)
					&& parent != null
					&& parent != viewPanel.getView().getRoot()) {
				collNodes.add(parent);
			}
		}

		ViewFrame f = null;
		if (!origView) {
			f = viewPanel.createNewFrame();
			f.newView(this);
			f.saved = true;
		} else {
			f = this;
		}

		// collapses nodes according to saved view
		collView(f.getViewPanel().getView(), collNodes, f.getViewPanel()
				.getView().getRoot());
		if (dom.getEdgeDefault()) {
			f.getViewPanel().getPrefs().edgeType = "directed";
		}

		// sets coordinates of loaded nodes
		Iterator it = nodes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Node n = (Node) idMapping.get(entry.getKey());
			Rectangle loadedShape = (Rectangle) entry.getValue();
			Rectangle oldShape = f.getViewPanel().getGeometry().shape(n);
			oldShape.setLocation(loadedShape.getLocation());
			oldShape.setSize(loadedShape.getSize());
			Iterator adj = f.getViewPanel().getView().getOutEdgesIterator(n);
			while (adj.hasNext()) {
				Edge e = (Edge) adj.next();
				Polyline p = f.getViewPanel().getGeometry().shape(e);
				p.setStart(oldShape);
			}
			adj = f.getViewPanel().getView().getInEdges(n).iterator();
			while (adj.hasNext()) {
				Edge e = (Edge) adj.next();
				Polyline p = f.getViewPanel().getGeometry().shape(e);
				p.setEnd(oldShape);
			}
		}

		// sets shapes of loaded edges
		it = polys.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String ent = (String) entry.getKey();
			Edge edg = null;
			java.util.List value = (java.util.List) entry.getValue();
			Iterator itCtrl = value.iterator();
			// deals with derived edges
			if (ent.startsWith("derived")) {
				String sourceString = (String) itCtrl.next();
				String targetString = (String) itCtrl.next();
				Node source = (Node) idMapping.get(sourceString);
				Node target = (Node) idMapping.get(targetString);
				edg = (Edge) f.viewPanel.getView().getEdge(source, target).get(
						0);
			} else {
				edg = (Edge) idMappingEdges.get(ent);
				itCtrl.next();
				itCtrl.next();
			}
			int count = 0;
			int x = 0, y = 0;
			Polyline poly = f.getViewPanel().getGeometry().shape(edg);
			//			if(poly == null) continue;
			poly.clearControlPoints();

			// adds control points for polylines
			while (itCtrl.hasNext()) {
				String s = (String) itCtrl.next();
				Double d = Double.valueOf(s);
				if (count == 0) {
					x = d.intValue();
				} else {
					y = d.intValue();
				}
				if (count == 1) {
					poly.addControl(new Point(x, y));
					count = 0;
				} else {
					count++;
				}
			}

		}
		f.getViewPanel().repaint();
	}

	/**
	 * Collapses nodes in a view so that view matches save-file.
	 * 
	 * @param v
	 *            The view in which nodes are to be contracted.
	 * @param l
	 *            List of nodes which are to be contracted.
	 * @param n
	 *            Root of view v.
	 */
	private void collView(View v, java.util.List l, Node n) {
		java.util.List children = v.getChildren(n);
		Iterator it = children.iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			collView(v, l, node);
			if (l.contains(node)) {
				v.contract(node);
			}
		}

	}

	/**
	 * Saves a graph and its views to a file.
	 * 
	 * @param file
	 *            Name of save-file.
	 */
	public void saveFile(String file) {
		if (!file.equals("") && file != null) {
			this.setFilename(file);

			// begins the GraphML document
			Document doc = new DocumentImpl();
			boolean b;
			if (viewPanel.getPrefs().edgeType.equals("directed")) {
				b = viewPanel.getBaseGraph().save(doc, true);
			} else {
				b = viewPanel.getBaseGraph().save(doc, false);
			}

			// makes string and saves it to file
			OutputFormat format = new OutputFormat(doc);
			format.setIndenting(true);
			StringWriter stringOut = new StringWriter();
			XMLSerializer serial = new XMLSerializer(stringOut, format);
			try {
				serial.asDOMSerializer();

				serial.serialize(doc.getDocumentElement());

				System.out.println("GraphML_SAVE: \n" + stringOut);

				PrintWriter output = new PrintWriter(new FileWriter(file), true);
				output.print(stringOut);
				output.close();

			} catch (Exception exc) {
				viewPanel.showError("Error while saving", exc.getMessage());
			}
			if (!b) {
				viewPanel.showError("Error while saving",
						"Only basegraph can be saved.");
			}
		}
	}

	/**
	 * Sets the filename of the graph in this frame.
	 * 
	 * @param f
	 *            The filename.
	 */
	public void setFilename(String f) {
		filename = f;
	}

	/**
	 * Sets the title of this frame.
	 * 
	 * @param t
	 *            The new title.
	 */
	public void setTitle(String t) {
		super.setTitle(t);
	}

	/**
	 * Gets the filename of this frame.
	 * 
	 * @return The filename of the graph in this frame.
	 */
	public String getFilename() {
		return new String(filename);
	}

	/**
	 * Closes this internal frame.
	 *  
	 */
	public void closeFrame() {
		viewPanel.getView().closeView();
	}

	/**
	 * Makes this frame contain a new view of a basegraph.
	 * 
	 * @param v
	 *            The frame containing a view of the basegraph.
	 */
	public void newView(ViewFrame v) {
		saved = v.saved;
		viewPanel.newView(v.getViewPanel());
		filename = v.getFilename();
		this.setTitle(filename + " - View "
				+ viewPanel.getBaseGraph().numOfViews());
	}

	/**
	 * Checks if graph belonging to this frame has been saved.
	 * 
	 * @return True iff graph has been saved.
	 */
	public boolean saved() {
		return saved;
	}

	/**
	 * Marks this frame as containing graph which has been saved.
	 *  
	 */
	public void setSaved() {
		saved = true;
	}

}

