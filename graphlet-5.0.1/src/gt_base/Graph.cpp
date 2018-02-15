/* This software is distributed under the Lesser General Public License */
//
// Graph.cc
//
// This module implements the class GT_Graph.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Graph.cpp,v $
// $Author: himsolt $
// $Revision: 1.13 $
// $Date: 1999/07/14 10:53:21 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#include "Graphlet.h"
#include "Graph.h"
#include "GTL_Shuttle.h"
#include "Device.h"
#include "UIObject.h"

#include "NEI.h"
#include "Ports.h"
#include <strstream>

#ifndef WIN32
#include <unistd.h>
#endif



//
//  class GT_Graph
//


GT_Graph::GT_Graph() :
	the_default_node_style (GT_Keys::default_node_style),
	the_default_edge_style (GT_Keys::default_edge_style)
{
    the_graph = 0;

    the_node_styles[GT_Keys::default_node_style] = new_node_attributes();
    the_edge_styles[GT_Keys::default_edge_style] = new_edge_attributes();

    new_node_attributes_template =
	the_node_styles[GT_Keys::default_node_style];
    new_edge_attributes_template =
	the_edge_styles[GT_Keys::default_edge_style];
    the_new_attributes_template_copy = GT_Copy (GT_Copy::deep);
}

//
// Destructor of GT_Graph
//

GT_Graph::~GT_Graph()
{
    the_graph = 0;
}


//
// Attatch a LEDA Graph to a GT_Graph
//


void GT_Graph::attach (graph* g)
{
    assert (the_graph == 0);
	
    the_graph = g;

    the_node_attrs.init (*g, 0);
    the_edge_attrs.init (*g, 0);
    attrs (new_graph_attributes());
}


void GT_Graph::attach (GT_Shuttle& g)
{
    gtl (g.gtl());
    g.attach (*this);
}


//
// node_by_id
// edge_by_id
//


void GT_Graph::node_by_id (node n, int id)
{
    the_nodes_by_id[id] = n;
}


void GT_Graph::edge_by_id (edge e, int id)
{
    the_edges_by_id[id] = e;
}


void GT_Graph::undefine_node_by_id (int id)
{
    the_nodes_by_id.erase (id);
}


void GT_Graph::undefine_edge_by_id (int id)
{
    the_edges_by_id.erase (id);
}



//////////////////////////////////////////
//
// new attribute constructors
//
//////////////////////////////////////////


GT_Graph_Attributes* GT_Graph::new_graph_attributes ()
{
    GT_Graph_Attributes* new_attrs = new GT_Graph_Attributes ();

#ifndef WIN32
    new_attrs->creator(cuserid(NULL));
#endif
    // NOTE: GT_Graph::pre_clear_handler accesses the attributes directly.
    // Any changes here MUST be synchrinized with GT_Graph::pre_clear_handler.

    if (new_attrs->graphics() == 0) {
	new_attrs->graphics (new_graph_graphics());
    }

    if (new_attrs->label_graphics() == 0) {
	new_attrs->label_graphics (new_graph_label_graphics());
    }

    return new_attrs;
}


GT_Node_Attributes* GT_Graph::new_node_attributes ()
{
    GT_Node_Attributes* new_attrs = new GT_Node_Attributes ();

    if (new_attrs->graphics() == 0) {
	new_attrs->graphics (new_node_graphics());
    }
    if (new_attrs->label_graphics() == 0) {
	new_attrs->label_graphics (new_node_label_graphics());
    }
    if (new_attrs->node_nei() == 0) {
	new_attrs->node_nei (new_node_nei());
    }

    return new_attrs;
}


GT_Edge_Attributes* GT_Graph::new_edge_attributes ()
{
    GT_Edge_Attributes* new_attrs = new GT_Edge_Attributes ();

    if (new_attrs->graphics() == 0) {
	new_attrs->graphics (new_edge_graphics());
    }
    if (new_attrs->label_graphics() == 0) {
	new_attrs->label_graphics (new_edge_label_graphics());
    }
    if (new_attrs->edge_nei() == 0) {
	new_attrs->edge_nei (new_edge_nei());
    }

    // An edge is a line by default
    // other types subject to later extension
	
    new_attrs->graphics()->type (GT_Keys::type_line);

    return new_attrs;
}

	
//////////////////////////////////////////
//
// graphics customization
//
//////////////////////////////////////////


GT_Graph_Graphics* GT_Graph::new_graph_graphics ()
{
    return new GT_Graph_Graphics;
}

GT_Graph_Label_Graphics* GT_Graph::new_graph_label_graphics ()
{
    return new GT_Graph_Label_Graphics;
}


GT_Node_Graphics* GT_Graph::new_node_graphics ()
{
    return new GT_Node_Graphics;
}


GT_Node_Label_Graphics* GT_Graph::new_node_label_graphics ()
{
    return new GT_Node_Label_Graphics;
}


GT_Edge_Graphics* GT_Graph::new_edge_graphics ()
{
    return new GT_Edge_Graphics;
}

GT_Edge_Label_Graphics* GT_Graph::new_edge_label_graphics ()
{
    return new GT_Edge_Label_Graphics;
}


//////////////////////////////////////////
//
// Edge Anchor Customization
//
//////////////////////////////////////////


GT_Node_NEI* GT_Graph::new_node_nei ()
{
    return new GT_Node_NEI ();
}


GT_Edge_NEI* GT_Graph::new_edge_nei ()
{
    return new GT_Edge_NEI ();
}



//////////////////////////////////////////
//
// Cupy, Cut & Paste
//
//////////////////////////////////////////

	
//
// Cut & Paste
//

GT_Graph& GT_Graph::copy (GT_Graph& /* into_graph */)
{
    assert ("Not Implemented" == 0);
    return *this;
}


node GT_Graph::copy (node old_node, GT_Graph& into_graph)
{
    GT_Node_Attributes* saved_node_attributes_template =
	into_graph.new_node_attributes_template;

    into_graph.new_node_attributes_template = the_node_attrs[old_node];
    node new_node = into_graph.gtl().new_node ();

    into_graph.new_node_attributes_template = saved_node_attributes_template;

    return new_node;
}


edge GT_Graph::copy (edge old_edge, node new_source, node new_target,
    GT_Graph& into_graph)
{
    GT_Edge_Attributes* saved_edge_attributes_template =
	into_graph.new_edge_attributes_template;

    into_graph.new_edge_attributes_template = the_edge_attrs[old_edge];
    edge new_edge = into_graph.gtl().new_edge (new_source, new_target);

    into_graph.new_edge_attributes_template = saved_edge_attributes_template;

    return new_edge;
}



//////////////////////////////////////////
//
// Boundong box computation
//
//////////////////////////////////////////


GT_Rectangle GT_Graph::bbox (bool include_width) const
{
    GT_Rectangle rect;
    bbox (rect, include_width);
    return rect;
}


GT_Rectangle GT_Graph::bbox (const node n, bool include_width) const
{
    assert (n != node());
    
    GT_Rectangle rect;
    bbox (n, rect, include_width);
    return rect;
}


GT_Rectangle GT_Graph::bbox (const list<node>& nodes, bool include_width) const
{
    GT_Rectangle rect;
    bbox (nodes, rect, include_width);
    return rect;
}


GT_Rectangle GT_Graph::bbox (const edge e, bool include_width) const
{
    assert (e != edge());
    
    GT_Rectangle rect;
    bbox (e, rect, include_width);
    return rect;
}


GT_Rectangle GT_Graph::bbox (const list<edge>& edges, bool include_width) const
{
    GT_Rectangle rect;
    bbox (edges, rect, include_width);
    return rect;
}


void GT_Graph::bbox (GT_Rectangle& rect, bool include_width) const
{
    const graph& g = gtl();
    rect = GT_Rectangle (0.0, 0.0, 0.0, 0.0);
    
    node n;
    forall_nodes (n, g) {	
	rect.union_with (bbox (n, include_width));
    }

    edge e;
    forall_edges (e, g) {
	rect.union_with (bbox (e, include_width));
    }
}


void GT_Graph::bbox (const node n, GT_Rectangle& rect,
    bool include_width) const
{
    const GT_Common_Graphics* cg = gt(n).graphics();
    rect = cg->center();
    if (include_width) {
	rect.expand (cg->width()/2.0, cg->width()/2.0);
    }
}


void GT_Graph::bbox (const list<node>& nodes, GT_Rectangle& rect,
    bool include_width) const
{
    double minx = 1.0E38;
    double miny = 1.0E38;
    double maxx = 0.0;
    double maxy = 0.0;
    
    for(list<node>::const_iterator it = nodes.begin();
	it != nodes.end(); ++it)
    {
	const GT_Common_Graphics* cg = gt(*it).graphics();

	double width2 = cg->width()/2.0;
	
	double left = cg->center().left();
	if (include_width) {
	    left -= width2;
	}
	if (minx > left) {
	    minx = left;
	}
	
	double top = cg->center().top();
	if (include_width) {
	    top -= width2;
	}
	if (miny > top) {
	    miny = top;
	}
	
	double right = cg->center().right();
	if (include_width) {
	    right += width2;
	}
	if (maxx < right) {
	    maxx = right;
	}
	
	double bottom = cg->center().bottom();
	if (include_width) {
	    bottom += width2;
	}
	if (maxy < bottom) {
	    maxy = bottom;
	}	
    }

    rect = GT_Rectangle (
	(minx + maxx) / 2.0,
	(miny + maxy) / 2.0,
	(maxx - minx),
	(maxy - miny));
}


void GT_Graph::bbox (const edge e, GT_Rectangle& rect,
    bool include_width) const
{
    const GT_Common_Graphics* cg = gt(e).graphics();
    rect = cg->center();
    if (include_width) {
	rect.expand (cg->width()/2.0, cg->width()/2.0);
    }
}


void GT_Graph::bbox (const list<edge>& edges, GT_Rectangle& rect,
    bool include_width) const
{    
    double minx = 1.0E38;
    double miny = 1.0E38;
    double maxx = 0.0;
    double maxy = 0.0;

    for(list<edge>::const_iterator it = edges.begin();
	it != edges.end(); ++it)
    {
	const GT_Common_Graphics* cg = gt(*it).graphics();

	double width2 = cg->width()/2.0;
	
	double left = cg->center().left();
	if (include_width) {
	    left -= width2;
	}
	if (minx > left) {
	    minx = left;
	}
	
	double top = cg->center().top();
	if (include_width) {
	    top -= width2;
	}
	if (miny > top) {
	    miny = top;
	}
	
	double right = cg->center().right();
	if (include_width) {
	    right += width2;
	}
	if (maxx < right) {
	    maxx = right;
	}
	
	double bottom = cg->center().bottom();
	if (include_width) {
	    bottom += width2;
	}
	if (maxy < bottom) {
	    maxy = bottom;
	}	
    }

    rect = GT_Rectangle (
	(minx + maxx) / 2.0,
	(miny + maxy) / 2.0,
	(maxx - minx),
	(maxy - miny));
}



//////////////////////////////////////////
//
// Search for nodes and edges with a certain id, uid, label_uid
//
//////////////////////////////////////////


node GT_Graph::find_node (const int id) const
{
    map<int,node>::const_iterator it = the_nodes_by_id.find(id);
    
    if (it != the_nodes_by_id.end()) {
	return (*it).second;
    } else {
	return node();
    }
}


edge GT_Graph::find_edge (const int id) const
{
    map<int,edge>::const_iterator it = the_edges_by_id.find(id);
    
    if (it != the_edges_by_id.end()) {
	return (*it).second;
    } else {
	return edge();
    }
}


node GT_Graph::find_node_with_uid (const int uid) const
{
    const graph& g = gtl();
    node n;

    forall_nodes (n, g) {
	if (gt(n).graphics() != 0 &&
	    gt(n).graphics()->uid() == uid) {
	    return n;
	}
    }
    return node();
}


node GT_Graph::find_node_with_label_uid (const int uid) const
{
    const graph& g = gtl();
    node n;

    forall_nodes (n, g) {
	if (gt(n).label_graphics() != 0 &&
	    gt(n).label_graphics()->uid() == uid) {
	    return n;
	}
    }
    return node();
}


edge GT_Graph::find_edge_with_uid (const int uid) const
{
    const graph& g = gtl();
    edge e;
	
    forall_edges (e, g) {
	if (gt(e).graphics() != 0 &&
	    gt(e).graphics()->uid() == uid) {
	    return e;
	}
    }
    return edge();
}


edge GT_Graph::find_edge_with_label_uid (const int uid) const
{
    const graph& g = gtl();
    edge e;
	
    forall_edges (e, g) {
	if (gt(e).label_graphics() != 0 &&
	    gt(e).label_graphics()->uid() == uid) {
	    return e;
	}
    }
    return edge();
}



//////////////////////////////////////////
//
// attrs
//
// Procedures that install Attribute lists
//
//////////////////////////////////////////


void GT_Graph::attrs (GT_Graph_Attributes* attrs)
{
    the_graph_attrs = attrs;

    if (attrs != 0) {	
	attrs->g (this);
	attrs->id (graphlet->id.next_id());
    }
}


void GT_Graph::attrs (const node n, GT_Node_Attributes* attrs)
{
    the_node_attrs[n] = attrs;

    if (attrs != 0) {
	attrs->g (this);
	attrs->n (n);	
	attrs->id (graphlet->id.next_id());
    }
}


void GT_Graph::attrs (const edge e, GT_Edge_Attributes* attrs)
{
    the_edge_attrs[e] = attrs;

    if (attrs != 0) {
	attrs->g (this);
	attrs->e (e);	
	attrs->id (graphlet->id.next_id());
    }
}


//////////////////////////////////////////
//
// output
//
//////////////////////////////////////////


ostream& operator<< (ostream& out, const GT_Graph& g)
{
    GT_List_of_Attributes::print_list_head (out, GT_Keys::graph);

    if (g.attrs() != 0) {
	g.gt().print (out);
    }

    GT_print (out, GT_Keys::directed, g.gtl().is_directed() ? 1 : 0);

    for(map<GT_Key,GT_Node_Attributes*>::const_iterator it_node =
	g.the_node_styles.begin(); it_node != g.the_node_styles.end(); ++it_node)
    {
	GT_Key style = it_node->first;

	GT_List_of_Attributes::print_list_head (out, GT_Keys::node_style);
	GT_print (out, GT_Keys::name, style);
	GT_Attribute_Base::print (out, GT_Keys::style);
	GT_print (out, g.node_style (style));
	GT_List_of_Attributes::print_list_tail (out);
    }
    
    for(map<GT_Key,GT_Edge_Attributes*>::const_iterator it_edge =
	g.the_edge_styles.begin(); it_edge != g.the_edge_styles.end(); ++it_edge)
    {
	GT_Key style = it_edge->first;

	GT_List_of_Attributes::print_list_head (out, GT_Keys::edge_style);
	GT_print (out, GT_Keys::name, style);
	GT_Attribute_Base::print (out, GT_Keys::style);
	GT_print (out, g.edge_style (style));
	GT_List_of_Attributes::print_list_tail (out);
    }

    if (g.default_node_style() != GT_Keys::undefined &&
	g.default_node_style() != GT_Keys::default_node_style) {
	GT_print (out, GT_Keys::default_node_style, g.default_node_style());
    }

    if (g.default_edge_style() != GT_Keys::undefined &&
	g.default_edge_style() != GT_Keys::default_edge_style) {
	GT_print (out, GT_Keys::default_edge_style, g.default_edge_style());
    }

    node n;
    forall_nodes (n, g.gtl()) {
	GT_Attribute_Base::print (out, GT_Keys::node);
	GT_print (out, g.attrs(n));
    }

    edge e;
    forall_edges (e, g.gtl()) {
	GT_Attribute_Base::print (out, GT_Keys::edge);
	GT_print (out, g.attrs(e));
    }

    GT_List_of_Attributes::print_list_tail (out);
    return out;
}



//////////////////////////////////////////
//
// bool GT_Graph::extract
//
//////////////////////////////////////////


int GT_Graph::extract (GT_List_of_Attributes* top_attrs, string& message)
{
    int max_id = 0;
    int code;
    
    GT_List_of_Attributes* graph_list;
    if (top_attrs->extract (GT_Keys::graph, graph_list)) {

	//
	// Create and attach a new GTL graph
	//
	
	if (the_graph == 0) {
	    attach (new graph);
	}
	graph& gtl = this->gtl();

	//
	// extract "directed" information
	//
	
	int xtr_directed;
	if (graph_list->extract (GT_Keys::directed, xtr_directed)) {
	    if (xtr_directed == 0) {
		gtl.make_undirected();
	    } else {
		gtl.make_directed();
	    }
	} else {
	    gtl.make_undirected();
	}

	int xtr_version;
	if (graph_list->extract (GT_Keys::version, xtr_version)) {
	    // ignore
	}

	GT_List_of_Attributes* xtr_node_style;
	while (graph_list->extract (GT_Keys::node_style, xtr_node_style)) {

	    GT_Node_Attributes* style;

	    GT_Key style_key;
	    string xtr_name;
	    if (xtr_node_style->extract (GT_Keys::name, xtr_name)) {
		style = add_node_style (graphlet->keymapper.add (xtr_name));
	    } else {
		style = node_style (GT_Keys::default_node_style);
	    }

	    GT_List_of_Attributes* xtr_style_style;
	    if (xtr_node_style->extract (GT_Keys::style, xtr_style_style)) {
		code = style->extract (xtr_style_style, message);
		if (code != GT_OK) {
		    return code;
		}
		
	    }
	}

	GT_Key xtr_default_node_style;
	if (graph_list->extract (GT_Keys::default_node_style,
	    xtr_default_node_style)) {
	    if (the_node_styles.find(xtr_default_node_style) !=
		the_node_styles.end()) {
		default_node_style (xtr_default_node_style);
	    } else {
		message =
		    "Default node style "+
		    xtr_default_node_style.name() +
		    " not defined";
		return GT_ERROR;
	    }
	}


	GT_List_of_Attributes* xtr_edge_style;
	while (graph_list->extract (GT_Keys::edge_style, xtr_edge_style)) {

	    GT_Edge_Attributes* style;

	    GT_Key style_key;
	    string xtr_name;
	    if (xtr_edge_style->extract (GT_Keys::name, xtr_name)) {
		style = add_edge_style (graphlet->keymapper.add (xtr_name));
	    } else {
		style = edge_style (GT_Keys::default_edge_style);
	    }

	    GT_List_of_Attributes* xtr_style_style;
	    if (xtr_edge_style->extract (GT_Keys::style, xtr_style_style)) {
		code = style->extract (xtr_style_style, message);
		if (code != GT_OK) {
		    return code;
		}
		
	    }
	}

	GT_Key xtr_default_edge_style;
	if (graph_list->extract (GT_Keys::default_edge_style,
	    xtr_default_edge_style)) {
	    if (the_edge_styles.find(xtr_default_edge_style) !=
		the_edge_styles.end()) {
		default_edge_style (xtr_default_edge_style);
	    } else {
		message =
		    "Default edge style " +
		    xtr_default_edge_style.name() +
		    " not defined";
		return GT_ERROR;
	    }
	}

	the_graph_attrs->extract (graph_list, message);

	//
	// Map for node -> id's from file and reverse
	//
	// Note: the id fields in graph, node and edge attributes
	// are different from those in the file.
	//
	
	node_map<int> id_of_node (*the_graph);
	map<int,node> node_with_id;

	//
	// Extract the nodes
	//
	
	GT_List_of_Attributes* node_list;
	while (graph_list->extract (GT_Keys::node, node_list)) {

	    node n = gtl.new_node();

	    // Extract the id of the node

	    int xtr_id;
	    if (node_list->extract (GT_Keys::id, xtr_id)) {

		// Adjust the maximum id
		max_id = max (xtr_id, max_id);

		// Assign an id if the node does not already have an one
		id_of_node[n] = xtr_id;
		node_with_id[xtr_id] = n;
	    }

	    // Extract attributes
	    
	    code = the_node_attrs[n]->extract (node_list, message);
	    if (code != GT_OK) {
		return code;
	    }
	    the_node_attrs[n]->splice(the_node_attrs[n]->begin(), *node_list);
	}


	//
	// Extract the edges
	//
	
	GT_List_of_Attributes* edge_list;
	while (graph_list->extract (GT_Keys::edge, edge_list)) {
			
	    int source;
	    int target;

	    //
	    // Extract source and target and check numbers
	    //
	    
	    if (!edge_list->extract (GT_Keys::source, source)) {
		message = "Edge missing source";
		return GT_ERROR;
	    } else if (!edge_list->extract (GT_Keys::target, target)) {
		message = "Edge missing target";
		return GT_ERROR;
	    } else if (node_with_id.find(source) == node_with_id.end()) {
		ostrstream s;
		s << "Undefined source number " << source;
		message = s.str();
		return GT_ERROR;
	    } else if (node_with_id.find(target) == node_with_id.end()) {
		ostrstream s;
		s << "Undefined target number " << target;
		message = s.str();
		return GT_ERROR;
	    } else {

		// Create a new edge
		
		edge e = gtl.new_edge (node_with_id[source],
		    node_with_id[target]);

		// Extract attributes
		
		code = the_edge_attrs[e]->extract (edge_list, message);
		if (code != GT_OK) {
		    return code;
		}
		the_edge_attrs[e]->splice(the_edge_attrs[e]->begin(), *edge_list);
	    }
	}

	the_graph_attrs->splice(the_graph_attrs->begin(), *graph_list);
				
	graphlet->id.adjust_maximum_id (max_id);

	return GT_OK;
    }

    return GT_ERROR;
}



//////////////////////////////////////////
//
// Drawing operations
//
//////////////////////////////////////////


int GT_Graph::draw (bool force)
{
    return draw (gtl().all_nodes(), gtl().all_edges(), force);
}



int GT_Graph::draw (const list<node>& nodes, const list<edge>& edges,
    bool force)
{
    //
    // Draw the graph
    //
	
    const GT_Graph_Attributes& attrs = gt();
  
    for(list<GT_Device*>::const_iterator it_dev = the_devices.begin();
	it_dev != the_devices.end(); ++it_dev)
    {
	GT_Device* device = *it_dev;

	int uid = attrs.graphics()->uid();
	GT_UIObject* uigraph = device->get (uid);
	if (uigraph == 0) {
				
	    uigraph = new_uiobject (device, *this);

	    if (uigraph->create() == false) {
		delete uigraph;
		return GT_ERROR;
	    } else {
		device->insert (uid, uigraph);
	    }
			
	} else {
	    uigraph->update (force);
	}
    }

    //
    // Draw all nodes and edges
    //

    if (graphlet->draw_edges_above()) {
	for(list<edge>::const_iterator it_edge = edges.begin();
	    it_edge != edges.end(); ++it_edge)
	{
	    int code = draw (*it_edge, force);
	    if (code != GT_OK) {
		return code;
	    }	
	}
    }

    for(list<node>::const_iterator it_node = nodes.begin();
	it_node != nodes.end(); ++it_node)
    {
	int code = draw (*it_node, force);
	if (code != GT_OK) {
	    return code;
	}
    }

    if (!graphlet->draw_edges_above()) {
	for(list<edge>::const_iterator it_edge = edges.begin();
	    it_edge != edges.end(); ++it_edge)
	{
	    int code = draw (*it_edge, force);
	    if (code != GT_OK) {
		return code;
	    }	
	}
    }

    //
    // Update all marked UIObjects
    //

    int code = update_marks (nodes, edges);
    if (code != GT_OK) {
	return code;
    }

    //
    // Reset all changes
    //

    reset_all_changes (nodes, edges);

    return GT_OK;
}


void GT_Graph::reset_all_changes (const list<node>& nodes,
    const list<edge>& edges)
{
    for(list<node>::const_iterator it_node = nodes.begin();
	it_node != nodes.end(); ++it_node)
    {
	GT_Node_Attributes& attrs = gt(*it_node);
	for (int tag = GT_Common_Graphics::common_graphics_tag_min;
	     tag <= GT_Common_Graphics::common_graphics_tag_max;
	     tag = tag << 1) {	    
	    attrs.graphics()->reset_changed (tag);
	    attrs.label_graphics()->reset_changed (tag);
	}
	attrs.reset_changed (GT_Common_Attributes::tag_label);
	attrs.reset_changed (GT_Common_Attributes::tag_label_anchor);
    }

    for(list<edge>::const_iterator it_edge = edges.begin();
	it_edge != edges.end(); ++it_edge)
    {
	GT_Edge_Attributes& attrs = gt(*it_edge);
	for (int tag = GT_Common_Graphics::common_graphics_tag_min;
	     tag <= GT_Common_Graphics::common_graphics_tag_max;
	     tag = tag << 1) {	    
	    attrs.graphics()->reset_changed (tag);
	    attrs.label_graphics()->reset_changed (tag);
	}
	attrs.reset_changed (GT_Common_Attributes::tag_label);
	attrs.reset_changed (GT_Common_Attributes::tag_label_anchor);
    }

    for (int tag = GT_Common_Graphics::common_graphics_tag_min;
	 tag <= GT_Common_Graphics::common_graphics_tag_max;
	 tag = tag << 1) {	    
	gt().graphics()->reset_changed (tag);
	gt().label_graphics()->reset_changed (tag);
    }
    gt().reset_changed (GT_Common_Attributes::tag_label);
}


int GT_Graph::update_marks (const list<node>& nodes,
    const list<edge>& edges)
{
    int code = GT_OK;
    
    for(list<node>::const_iterator it_node = nodes.begin();
	it_node != nodes.end(); ++it_node)
    {
	code = update_marks (*it_node);
	if (code != GT_OK) {
	    return code;
	}
    }

    for(list<edge>::const_iterator it_edge = edges.begin();
	it_edge != edges.end(); ++it_edge)
    {
	code = update_marks (*it_edge);
	if (code != GT_OK) {
	    return code;
	}
    }

    return code;
}


int GT_Graph::update_marks (node n)
{
    if (test_update_marks (n)) {
	
	for(list<GT_Device*>::const_iterator it = the_devices.begin();
	    it != the_devices.end(); ++it)
	{
	    GT_UIObject* uiobject = (*it)->get (gt(n).graphics()->uid());
	    if (uiobject != 0) {
		if (!uiobject->update_mark ("selected")) {
		    return GT_ERROR;
		}
	    }
	}
    }

    return GT_OK;
}


int GT_Graph::update_marks (edge e)
{
    if (test_update_marks (e)) {
	for(list<GT_Device*>::const_iterator it = the_devices.begin();
	    it != the_devices.end(); ++it)
	{
	    GT_UIObject* uiobject = (*it)->get (gt(e).graphics()->uid());
	    if (uiobject != 0) {
		if (!uiobject->update_mark ("selected")) {
		    return GT_ERROR;
		}
	    }
	}
    }

    return GT_OK;
}


bool GT_Graph::test_update_marks (node n) const
{
    return gt(n).graphics()->is_changed (
	GT_Common_Graphics::tag_geometry |
	GT_Common_Graphics::tag_width);
}


bool GT_Graph::test_update_marks (edge e) const
{
    return gt(e).graphics()->is_changed (
	GT_Common_Graphics::tag_geometry |
	GT_Common_Graphics::tag_arrow |
	GT_Common_Graphics::tag_width |
	GT_Common_Graphics::tag_smooth);
}




//
// GT_Graph::draw (node n, bool force)
//


int GT_Graph::draw (node n, bool force)
{
    GT_Node_Attributes& attrs = gt(n);
    GT_Common_Graphics& graphics = *attrs.graphics();
    GT_Common_Graphics& label_graphics = *attrs.label_graphics();

    //
    // Check wether the label needs to be updated
    //
	
    if (graphics.is_changed (GT_Common_Graphics::tag_type) ||
	attrs.is_changed (GT_Common_Attributes::tag_label) ||
	label_graphics.is_changed (GT_Common_Graphics::tag_font) ||
	label_graphics.is_changed (GT_Common_Graphics::tag_font_size) ||
	label_graphics.is_changed (GT_Common_Graphics::tag_font_style)) {

	update_label (n);
    }

    //
    // Check wether coordinates need to be updated
    //
	
    if (graphics.is_changed (GT_Common_Graphics::tag_geometry) ||
	attrs.is_changed (GT_Common_Attributes::tag_label)) {

	update_coordinates (n);
    }
   
    //
    // Check wether the label coordinates need to be updated
    //
	
    if (graphics.is_changed (GT_Common_Graphics::tag_geometry) ||
	attrs.is_changed (GT_Common_Attributes::tag_label) ||
	attrs.is_changed (GT_Common_Attributes::tag_label_anchor)) {
	update_label_coordinates (n);	 
    }

    //
    // If the type has changed, then we must re-create the label
    //

    bool label_is_empty = (attrs.label().length() == 0);

    //
    // Draw the node
    //
	
    for(list<GT_Device*>::const_iterator it = the_devices.begin();
	it != the_devices.end(); ++it)
    {
	GT_Device* device = *it;

	if (attrs.is_changed (GT_Common_Attributes::tag_visible)) {
	    if (attrs.visible() == false) {

		GT_UIObject* uiobject;
		uiobject = device->get (graphics.uid());
		if (uiobject != 0) {
		    uiobject->del();
		    uiobject->del_mark("selected");
		    delete uiobject;
		    device->del (graphics.uid());
		}
		
		uiobject = device->get (label_graphics.uid());
		if (uiobject != 0) {
		    uiobject->del();
		    delete uiobject;
		    device->del (label_graphics.uid());
		}
		break;

	    } else {

		graphics.reset ();
		label_graphics.reset ();
		attrs.set_changed (GT_Common_Attributes::tag_label);
		attrs.reset_changed (GT_Common_Attributes::tag_visible);
	    }
	}

	if (!attrs.graphics()->nothing_changed () ||
	    attrs.is_changed (GT_Common_Attributes::tag_visible)) {

	    int uid = graphics.uid();
	    GT_UIObject* uinode = device->get (uid);

	    if (uinode == 0) {
				
		uinode = new_uiobject (device, *this, n);

		if (uinode->create() == false) {
		    delete uinode;
		    return GT_ERROR;
		} else {
		    device->insert (uid, uinode);
		}
		
	    } else {
		uinode->update (force);
	    }
	}
		
	if (attrs.is_changed (GT_Common_Attributes::tag_label) ||
	    !label_graphics.nothing_changed ()) {

	    int uid = label_graphics.uid();
	    GT_UIObject* uilabel = device->get (uid);

	    if (uilabel == 0) {

		if (!label_is_empty) {

		    attrs.label_graphics()->reset ();
		    
		    uilabel = new_uiobject_label (device, *this, n);

		    if (uilabel->create() == false) {
			delete uilabel;
			return GT_ERROR;
		    } else {
			device->insert (uid, uilabel);
		    }
		}
				
	    } else {
		
		if (!label_is_empty) {
		    uilabel->update (force);
		} else {
		    uilabel->del ();
		    delete uilabel;
		    device->del (uid);
		    attrs.label_graphics()->reset_initialized (
			GT_Common_Graphics::tag_geometry);
		    attrs.label_graphics()->reset_initialized (
			GT_Common_Graphics::tag_type);
		}
	    }
	}
    }

    attrs.graphics()->old_center (attrs.graphics()->center());
    
    return GT_OK;
}



//
// GT_Graph::draw (edge e, bool force)
//


int GT_Graph::draw (edge e, bool force)
{
    GT_Edge_Attributes& attrs = gt(e);
    GT_Common_Graphics& graphics = *attrs.graphics();
    GT_Common_Graphics& label_graphics = *attrs.label_graphics();

    const GT_Node_Attributes& source_attrs = gt(attrs.source());
    const GT_Common_Graphics& source_graphics = *source_attrs.graphics();
    const GT_Node_Attributes& target_attrs = gt(attrs.target());
    const GT_Common_Graphics& target_graphics = *target_attrs.graphics();

    //
    // Update coordinates if neccessary
    //
	
    bool geometry_initialized =
	graphics.is_initialized (GT_Common_Graphics::tag_geometry);
    bool geometry_changed =
	graphics.is_changed (GT_Common_Graphics::tag_geometry) ||
	attrs.edge_nei()->is_changed (GT_Edge_NEI::tag_edge_nei);
    bool source_geometry_changed =
	source_graphics.is_changed (GT_Common_Graphics::tag_geometry) ||
	source_graphics.is_changed (GT_Common_Graphics::tag_type) ||
	attrs.is_changed (GT_Edge_Attributes::tag_source_port) ||
	source_attrs.is_changed (GT_Node_Attributes::tag_ports) ||
	source_attrs.node_nei()->is_changed (GT_Node_NEI::tag_node_nei);
    bool target_geometry_changed =
	target_graphics.is_changed (GT_Common_Graphics::tag_geometry) ||
	target_graphics.is_changed (GT_Common_Graphics::tag_type) ||
	attrs.is_changed (GT_Edge_Attributes::tag_target_port) ||
	target_attrs.is_changed (GT_Node_Attributes::tag_ports) ||
	target_attrs.node_nei()->is_changed (GT_Node_NEI::tag_node_nei);
    bool label_changed =
	attrs.is_changed (GT_Common_Attributes::tag_label);

    if (!geometry_initialized || geometry_changed ||
	source_geometry_changed || target_geometry_changed ||
	label_changed) {

	update_coordinates(e);
	update_label (e);
	update_label_coordinates(e);
    }

    //
    // If the type has changed, then re-create the label
    //

    bool label_is_empty = (attrs.label().length() == 0);

    //
    // Draw on all devices
    //
    
    for(list<GT_Device*>::const_iterator it = the_devices.begin();
	it != the_devices.end(); ++it)
    {
	GT_Device* device = *it;

	if (attrs.is_changed (GT_Common_Attributes::tag_visible)) {
	    if (!attrs.visible()) {

		GT_UIObject* uiobject;
		uiobject = device->get (graphics.uid());
		if (uiobject != 0) {
		    uiobject->del();
		    uiobject->del_mark("selected");
		    delete uiobject;
		    device->del (graphics.uid());
		}
		
		uiobject = device->get (label_graphics.uid());
		if (uiobject != 0) {
		    uiobject->del();
		    delete uiobject;
		    device->del (label_graphics.uid());
		}

		break;

	    } else {

		graphics.reset ();
		label_graphics.reset ();
		attrs.set_changed (GT_Common_Attributes::tag_label);
		attrs.reset_changed (GT_Common_Attributes::tag_visible);
	    }
	}

	if (!graphics.nothing_changed()) {
	    
	    int uid = graphics.uid ();
	    GT_UIObject* uiedge = device->get (uid);
	    
	    if (uiedge == 0) {
		
		uiedge = new_uiobject (device, *this, e);
			
		if (uiedge->create() == false) {
		    delete uiedge;
		    return GT_ERROR;
		} else {
		    device->insert (uid, uiedge);
		}
			
	    } else {
		uiedge->update (force);
	    }
	}

	if (label_changed || !label_graphics.nothing_changed ()) {

	    int uid = label_graphics.uid();
	    GT_UIObject* uilabel = device->get (uid);
			
	    if (uilabel == 0) {

		if (!label_is_empty) {
			
		    label_graphics.reset ();

		    uilabel = new_uiobject_label (device, *this, e);

		    if (uilabel->create() == false) {
			delete uilabel;
			return GT_ERROR;
		    } else {
			device->insert (uid, uilabel);
		    }
		}
				
	    } else {
		if (!label_is_empty) {
		    uilabel->update (force);
		} else {
		    uilabel->del ();
		    delete uilabel;
		    device->del (uid);
		    label_graphics.reset_initialized (
			GT_Common_Graphics::tag_geometry);
		    label_graphics.reset_initialized (
			GT_Common_Graphics::tag_type);
		}
	    }
	}
    }

    graphics.old_center (graphics.center());
    
    return GT_OK;
}


//
// begin_draw, egde_draw are obsolete
//

int GT_Graph::begin_draw ()
{
    return GT_OK;
}


int GT_Graph::end_draw ()
{
    return GT_OK;
}


//
// new UIObject customization
//


GT_UIObject* GT_Graph::new_uiobject (GT_Device* /* device */,
    GT_Graph& /* g */)
{
    return 0;
}


GT_UIObject* GT_Graph::new_uiobject_label (GT_Device* /* device */,
    GT_Graph& /* g */)
{
    return 0;
}

GT_UIObject* GT_Graph::new_uiobject (GT_Device* /* device */,
    GT_Graph& /* g */, node  /* n */)
{
    return 0;
}


GT_UIObject* GT_Graph::new_uiobject_label (GT_Device* /* device */,
    GT_Graph& /* g */, node /* n */)
{
    return 0;
}


GT_UIObject* GT_Graph::new_uiobject (GT_Device* /* device */,
    GT_Graph& /* g */, edge /* e */)
{
    return 0;
}


GT_UIObject* GT_Graph::new_uiobject_label (GT_Device* /* device */,
    GT_Graph& /* g */, edge /* e */)
{
    return 0;
};



//////////////////////////////////////////
//
// int GT_Graph::move_node (node n, const double move_x, const double move_y)
//
// Device independend part of fast node moving
//
//////////////////////////////////////////


int GT_Graph::move_node (node n,
    const double move_x,
    const double move_y,
    GT_Device* /* fast */)
{
    GT_Point move_xy (move_x, move_y);
    GT_Node_Attributes& node_attrs = gt(n);

    if (node_attrs.graphics() != 0) {
	node_attrs.graphics()->move (move_xy);
    }
    
    if (node_attrs.label_graphics() != 0) {
	node_attrs.label_graphics()->move (move_xy);
    }

    return GT_OK;
}



int GT_Graph::move_nodes (const list<node>& nodes,
    const double move_x,
    const double move_y,
    GT_Device* fast)
{
    for(list<node>::const_iterator it = nodes.begin();
	it != nodes.end(); ++it)
    {
	move_node (*it, move_x, move_y, fast);
    }
    
    return GT_OK;
}



int GT_Graph::move_edge (edge e,
    const double move_x,
    const double move_y,
    GT_Device* /* fast */)
{
    GT_Point move_xy (move_x, move_y);
    GT_Edge_Attributes& edge_attrs = gt(e);

    if (edge_attrs.graphics() != 0) {
	edge_attrs.graphics()->move (move_xy);
    }
    
    if (edge_attrs.label_graphics() != 0) {
	edge_attrs.label_graphics()->move (move_xy);
    }

    return GT_OK;
}


int GT_Graph::scale (double by, GT_Point& origin)
{
    graph& g = gtl();
    
    node n;
    forall_nodes (n, g) {
	GT_Node_Attributes& node_attrs = gt(n);
	if (node_attrs.graphics() != 0) {
	    node_attrs.graphics()->scale (by, origin);
	}
	if (node_attrs.label_graphics() != 0) {
	    node_attrs.label_graphics()->scale (by, origin);
	}	
    }

    edge e;
    forall_edges (e, g) {
	GT_Edge_Attributes& edge_attrs = gt(e);
	if (edge_attrs.graphics() != 0) {
	    edge_attrs.graphics()->scale (by, origin);
	}
	if (edge_attrs.label_graphics() != 0) {
	    edge_attrs.label_graphics()->scale (by, origin);
	}
    }
    
    return GT_OK;
}
 

//////////////////////////////////////////
//
// Update Operations 
//
//////////////////////////////////////////


void GT_Graph::update ()
{
    return;
}


void GT_Graph::update (node /* n */)
{
    return;
}


void GT_Graph::update (edge /* e */)
{
    return;
}
	

void GT_Graph::update_coordinates ()
{
    return;
}

	
void GT_Graph::update_coordinates (node /* n */)
{
    return;
}

	
void GT_Graph::update_coordinates (edge e)
{
    GT_Edge_Attributes& attrs = gt(e);
    GT_Common_Graphics& graphics = *(attrs.graphics());
    const GT_Node_Attributes& source_attrs = gt(attrs.source());
    const GT_Node_Attributes& target_attrs = gt(attrs.target());
    const GT_Common_Graphics& source_graphics = *(source_attrs.graphics());
    const GT_Common_Graphics& target_graphics = *(target_attrs.graphics());

    // initialize line if neccessary.
    
    GT_Polyline line;

    if (graphics.line().empty()) {
	const GT_Node_Attributes& source_attrs = gt (attrs.source());
	const GT_Node_Attributes& target_attrs = gt (attrs.target());
	line.push_back (source_attrs.graphics()->center());
	line.push_back (target_attrs.graphics()->center());
	graphics.line (line);
    } else {
	line = graphics.line();
    }

    //
    // Update NEI
    //

    GT_Edge_NEI *the_nei = attrs.edge_nei ();

    the_nei->update_edgeanchor ();
    
    // reset all tags
    attrs.edge_nei()->reset_changed (GT_Edge_NEI::tag_edge_nei);
    gt(attrs.source()).node_nei()->reset_changed (
	GT_Node_NEI::tag_node_nei);
    gt(attrs.target()).node_nei()->reset_changed (
	GT_Node_NEI::tag_node_nei);
    
    *(line.begin()) = the_nei->clip_edge (GT_Source);
    *(--line.end()) = the_nei->clip_edge (GT_Target);

    //
    // Update Ports
    //

    if (attrs.source_port().active() &&
	attrs.source_port() != GT_Keys::empty) {

	const GT_Point* source_port = source_attrs.ports().find (
	    attrs.source_port());
	if (source_port == 0) {
	    source_port = &(source_graphics.center());
	}

	double source_x = source_graphics.x() +
	    (source_graphics.w()/2.0) * source_port->x();
	double source_y = source_graphics.y() +
	    (source_graphics.h()/2.0) * source_port->y();

	*(line.begin()) = GT_Point (source_x, source_y);
    }

    if (attrs.target_port().active() &&
	attrs.target_port() != GT_Keys::empty) {

	const GT_Point* target_port = target_attrs.ports().find (
	    attrs.target_port());
	if (target_port == 0) {
	    target_port = &(target_graphics.center());
	}

	double target_x = target_graphics.x() +
	    (target_graphics.w()/2.0) * target_port->x();
	double target_y = target_graphics.y() +
	    (target_graphics.h()/2.0) * target_port->y();

	*(--line.end()) = GT_Point (target_x, target_y);
    }

    //
    // Re-assign line
    //

    graphics.line (line);
    graphics.center (graphics.line().bbox());
}


void GT_Graph::update_label ()
{
    return;
}


void GT_Graph::update_label (node n)
{
    GT_Common_Graphics& cg = *(gt(n).graphics());

    if (cg.is_changed (GT_Common_Graphics::tag_geometry) &&
	!cg.is_changed (GT_Common_Graphics::tag_line) &&
	cg.line().size() > 0) {

	double x = cg.center().x();
	double y = cg.center().y();
	double w = cg.center().w();
	double h = cg.center().h();

	double old_x = cg.old_center().x();
	double old_y = cg.old_center().y();
	double old_w = cg.old_center().w();
	double old_h = cg.old_center().h();

	double scale_x = w / old_w;
	double scale_y = h / old_h;

	GT_Polyline line;
	for(GT_Polyline::const_iterator it = cg.line().begin();
	    it != cg.line().end(); ++it)
	{
	    double new_x = x + (it->x() - old_x) * scale_x;
	    double new_y = y + (it->y() - old_y) * scale_y;
	    line.push_back (GT_Point (new_x, new_y));
	}
	cg.line (line);
    }
}


void GT_Graph::update_label (edge /* e */)
{
    return;
}


	
void GT_Graph::update_label_coordinates ()
{
    return;
}
	
void GT_Graph::update_label_coordinates (node n)
{
    GT_Node_Attributes& node_attrs = gt(n);
    const GT_Common_Graphics* graphics =
	node_attrs.graphics();
    GT_Common_Graphics* label_graphics =
	node_attrs.label_graphics();

    if (node_attrs.label().length() > 0 && label_graphics != 0) {

	double w = graphics->w();
	double h = graphics->h();

	const GT_Key anchor = node_attrs.label_anchor();
	const GT_Rectangle& center = graphics->center();

	GT_Point p;
	if (anchor == GT_Keys::anchor_center) {
	    p = center.anchor_c();
	} else if (anchor == GT_Keys::anchor_n) {
	    p = center.anchor_n();
	} else if (anchor == GT_Keys::anchor_ne) {
	    p = center.anchor_ne();
	} else if (anchor == GT_Keys::anchor_e) {
	    p = center.anchor_e();
	} else if (anchor == GT_Keys::anchor_se) {
	    p = center.anchor_se();
	} else if (anchor == GT_Keys::anchor_s) {
	    p = center.anchor_s();
	} else if (anchor == GT_Keys::anchor_sw) {
	    p = center.anchor_sw();
	} else if (anchor == GT_Keys::anchor_w) {
	    p = center.anchor_w();
	} else if (anchor == GT_Keys::anchor_nw) {
	    p = center.anchor_nw();
	} else {
	    p = center;
	}
		
	node_attrs.label_graphics()->center (GT_Rectangle (p, w, h));
    }
}
	
void GT_Graph::update_label_coordinates (edge e)
{
    const GT_Edge_Attributes& edge_attrs = gt(e);
    const GT_Common_Graphics* graphics = edge_attrs.graphics();

    const GT_Key label_anchor = edge_attrs.label_anchor();
    GT_Point p;

    if (edge_attrs.label().length() > 0 && edge_attrs.label_graphics() != 0) {
    
	if (label_anchor == GT_Keys::anchor_first) {

	    const GT_Polyline& line = graphics->line();
	    const GT_Segment s = line.nth_segment (0);
	    
	    p = GT_Point ((s.xcoord1()  + s.xcoord2()) / 2,
		((s.ycoord1()  + s.ycoord2()) / 2));
	    
	} else if (label_anchor == GT_Keys::anchor_last) {
	    
	    const GT_Polyline& line = graphics->line();
	    const GT_Segment s = line.nth_segment (line.size()-2);
	    
	    p = GT_Point ((s.xcoord1()  + s.xcoord2()) / 2,
		((s.ycoord1()  + s.ycoord2()) / 2));
	    
	} else if (label_anchor == GT_Keys::anchor_bend) {

	    const GT_Polyline& line = graphics->line();
	    const GT_Segment s = line.nth_segment (
		edge_attrs.label_anchor_bend());
	    const double relative_x = edge_attrs.label_anchor_x();
	    const double relative_y = edge_attrs.label_anchor_y();
	    
	    p = GT_Point (
		s.xcoord1() + (s.xcoord2()-s.xcoord1()) * relative_x,
		s.ycoord1() + (s.ycoord2()-s.ycoord1()) * relative_y);

	} else { // Assume "middle"

	    const GT_Polyline& line = graphics->line();
	    const int l = line.size();
	    const GT_Segment s = line.nth_segment ((l>2) ? (l/2 - 1) : 0);
	    
	    p = GT_Point ((s.xcoord1()  + s.xcoord2()) / 2,
		((s.ycoord1()  + s.ycoord2()) / 2));
	}
	
	gt(e).label_graphics()->center(
	    GT_Rectangle (p,
		edge_attrs.label_graphics()->w(),
		edge_attrs.label_graphics()->h()));
    }
}



//////////////////////////////////////////
//
// node/edge styles
//
//////////////////////////////////////////


void GT_Graph::default_node_style (GT_Key k)
{
    assert (the_node_styles.find(k) != the_node_styles.end());

    the_default_node_style = k;
    new_node_attributes_template = the_node_styles[the_default_node_style];
}


void GT_Graph::default_edge_style (GT_Key k)
{
    assert (the_edge_styles.find (k) != the_edge_styles.end());

    the_default_edge_style = k;
    new_edge_attributes_template = the_edge_styles[the_default_edge_style];
}


GT_Key GT_Graph::default_node_style () const
{
    return the_default_node_style;
}


GT_Key GT_Graph::default_edge_style () const
{
    return the_default_edge_style;
}


void GT_Graph::node_style (node n, GT_Key k)
{
    assert (the_node_styles.find (k) != the_node_styles.end());

    if (the_node_styles[k] == gt(n).parent()) {
	update_styles (n);
    } else {
	gt(n).parent (the_node_styles[k]);
    }
}


void GT_Graph::edge_style (edge e, GT_Key k)
{
    assert (the_edge_styles.find(k) != the_edge_styles.end());

    if (the_edge_styles[k] == gt().parent()) {
	update_styles (e);
    } else {
	gt(e).parent (the_edge_styles[k]);
    }
}


GT_Node_Attributes* GT_Graph::node_style (GT_Key k) const
{
    if (the_node_styles.find (k) != the_node_styles.end()) {
	return (*the_node_styles.find(k)).second;
	// return the_node_styles[k];
    } else {
	return 0;
    }
}


GT_Edge_Attributes* GT_Graph::edge_style (GT_Key k) const
{
    if (the_edge_styles.find(k) != the_edge_styles.end()) {
	return (*the_edge_styles.find(k)).second;
	//	return the_edge_styles[k];
    } else {
	return 0;
    }
}


GT_Key GT_Graph::node_find_style (node n) const
{
    if (n != node()) {

	GT_Node_Attributes* n_attrs = the_node_attrs[n];
	
	map<GT_Key,GT_Node_Attributes*>::const_iterator it;
	for(it = the_node_styles.begin(); it != the_node_styles.end(); ++it)
	{	
	    if (n_attrs->parent() == it->second) {
		return it->first;
	    }
	}
    }

    return GT_Keys::undefined;
}


GT_Key GT_Graph::edge_find_style (edge e) const
{
    if (e != edge()) {

	GT_Edge_Attributes* e_attrs = the_edge_attrs[e];
	
	map<GT_Key,GT_Edge_Attributes*>::const_iterator it;
	for(it = the_edge_styles.begin(); it != the_edge_styles.end(); ++it)
	{
	    if (e_attrs->parent() == it->second) {
		return it->first;
	    }
	}
    }

    return GT_Keys::undefined;
}


//
// add styles
//


GT_Node_Attributes* GT_Graph::add_node_style (const char* name,
    GT_Node_Attributes* attributes_template) 
{
    return add_node_style (graphlet->keymapper.add (name),
	attributes_template);
}


GT_Edge_Attributes* GT_Graph::add_edge_style (const char* name,
    GT_Edge_Attributes* attributes_template) 
{
    return add_edge_style (graphlet->keymapper.add (name),
	attributes_template);
}


GT_Node_Attributes* GT_Graph::add_node_style (GT_Key key,
    GT_Node_Attributes* attributes_template)
{
    if (the_node_styles.find (key) != the_node_styles.end()) {
	return the_node_styles[key];
    } else {
	if (attributes_template == 0) {
	    attributes_template = new_node_attributes();
	}
	the_node_styles[key] = attributes_template;
    }

    return attributes_template;
}


GT_Edge_Attributes* GT_Graph::add_edge_style (GT_Key key,
    GT_Edge_Attributes* attributes_template)
{
    if (the_edge_styles.find (key) != the_edge_styles.end()) {
	return the_edge_styles[key];
    } else {
	if (attributes_template == 0) {
	    attributes_template = new_edge_attributes();
	}
	the_edge_styles[key] = attributes_template;
    }

    return attributes_template;
}


void GT_Graph::update_styles ()
{
    graph& g = gtl();

    node n;
    forall_nodes (n, g) {
	update_styles (n);
    }

    edge e;
    forall_edges (e, g) {
	update_styles (e);
    }

    for(map<GT_Key,GT_Node_Attributes*>::const_iterator it_node =
	the_node_styles.begin(); it_node != the_node_styles.end(); ++it_node)
    {
	it_node->second->set_changed (0);
    }

    for(map<GT_Key,GT_Edge_Attributes*>::const_iterator it_edge =
	the_edge_styles.begin(); it_edge != the_edge_styles.end(); ++it_edge)
    {
	it_edge->second->set_changed (0);
    }
}


void GT_Graph::update_styles (node n)
{
    if (gt(n).parent() != 0) {
	gt(n).update_from_parent (GT_Copy::deep_update_from_parent);
    }
}


void GT_Graph::update_styles (edge e)
{
    if (gt(e).parent() != 0) {
	gt(e).update_from_parent (GT_Copy::deep_update_from_parent);
    }
}
