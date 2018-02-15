/* This software is distributed under the Lesser General Public License */
#ifndef GT_GRAPH_H
#define GT_GRAPH_H

//
// Graph.h
//
// This module defines the class GT_Graph.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Graph.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:43:41 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#ifndef GT_GRAPHLET_H
#include "Graphlet.h"
#endif


#include <GTL/graph.h>
#include <GTL/node_map.h>
#include <GTL/edge_map.h>
#include <map>

#include "Attributes.h"
#include "Tagged_Attributes.h"
#include "Common_Attributes.h"
#include "Common_Graphics.h"
#include "Graph_Attributes.h"
#include "Node_Attributes.h"
#include "Edge_Attributes.h"

#include "Shuttle.h"

//////////////////////////////////////////
//
// GT_Graph;
//
//////////////////////////////////////////


class GT_Device;
class GT_UIObject;

class GT_Graph {

    graph* the_graph;
	
    GT_Graph_Attributes*          the_graph_attrs;
    node_map<GT_Node_Attributes*> the_node_attrs;
    edge_map<GT_Edge_Attributes*> the_edge_attrs;

    // This is for performance
    map<int,node> the_nodes_by_id;
    map<int,edge> the_edges_by_id;

    GT_Key the_default_node_style;
    GT_Key the_default_edge_style;

    // the following privates are used to initialize new attributes.
    GT_Node_Attributes* new_node_attributes_template;
    GT_Edge_Attributes* new_edge_attributes_template;
    GT_Copy the_new_attributes_template_copy;

protected:

    map<GT_Key,GT_Node_Attributes*> the_node_styles;
    map<GT_Key,GT_Edge_Attributes*> the_edge_styles;

    list<GT_Device*> the_devices;

public:

    //
    // Constructors and Accessors
    //
	
    GT_Graph();
    virtual ~GT_Graph();

    //
    // Attached (GTL) graph
    //
	
    inline graph* GT_Graph::attached() const;
    virtual void attach (graph* g);
    virtual void attach (GT_Shuttle& g);

    //
    // leda
    //
    // leda is a synomym for the attached graph. The leda methods
    // access the graph through references, while attach uses
    // pointers.
    //

    inline void leda (graph* g);
    inline void leda (graph& g);
    inline void leda (GT_Shuttle& g);
    inline graph& leda ();
    inline const graph& leda () const;

    //
    // gtl
    //
    // gtl is a synomym for the attached graph. The gtl methods
    // access the graph through references, while attach uses
    // pointers.
    //

    inline void gtl (graph* g);
    inline void gtl (graph& g);
    inline void gtl (GT_Shuttle& g);
    inline graph& gtl ();
    inline const graph& gtl () const;

    //
    // nodes_by_id
    // edges_by_id
    //

    inline node node_by_id (int id) const;
    inline edge edge_by_id (int id) const;
    virtual void node_by_id (node n, int id);
    virtual void edge_by_id (edge e, int id);
    virtual void undefine_node_by_id (int id);
    virtual void undefine_edge_by_id (int id);

    //
    // graph/node/edge attributes
    //

    virtual GT_Graph_Attributes* new_graph_attributes ();
    virtual GT_Node_Attributes* new_node_attributes ();
    virtual GT_Edge_Attributes* new_edge_attributes ();
	
    //
    // graphics customization
    //

    virtual GT_Graph_Graphics* new_graph_graphics ();
    virtual GT_Node_Graphics* new_node_graphics ();
    virtual GT_Edge_Graphics* new_edge_graphics ();
	
    virtual GT_Graph_Label_Graphics* new_graph_label_graphics ();
    virtual GT_Node_Label_Graphics* new_node_label_graphics ();
    virtual GT_Edge_Label_Graphics* new_edge_label_graphics ();

    virtual GT_Node_NEI* new_node_nei ();
    virtual GT_Edge_NEI* new_edge_nei ();

    //////////////////////////////////////////
    //
    // node/edge styles
    //
    //////////////////////////////////////////

    virtual void default_node_style (GT_Key);
    virtual void default_edge_style (GT_Key);
    GT_Key default_node_style () const;
    GT_Key default_edge_style () const;

    virtual void node_style (node n, GT_Key);
    virtual void edge_style (edge e, GT_Key);

    virtual GT_Node_Attributes* node_style (GT_Key) const;
    virtual GT_Edge_Attributes* edge_style (GT_Key) const;

    inline const map<GT_Key,GT_Node_Attributes*>& node_styles() const;
    inline const map<GT_Key,GT_Edge_Attributes*>& edge_styles() const;

    virtual GT_Key node_find_style (node n) const;
    virtual GT_Key edge_find_style (edge e) const;

    virtual GT_Node_Attributes* add_node_style (const char* name,
	GT_Node_Attributes* attributes_template = 0);
    virtual GT_Edge_Attributes* add_edge_style (const char* name,
	GT_Edge_Attributes* attributes_template = 0);
    virtual GT_Node_Attributes* add_node_style (GT_Key key,
	GT_Node_Attributes* attributes_template = 0);
    virtual GT_Edge_Attributes* add_edge_style (GT_Key key,
	GT_Edge_Attributes* attributes_template = 0); 

    //////////////////////////////////////////
    //
    // new GTL handlers
    //
    //////////////////////////////////////////
    
    //
    // graph handler
    //
    
    // before creating a graph
    virtual void pre_new_graph_handler();
    // after creating a graph
    virtual void post_new_graph_handler();
    // before deleting graph
    virtual void pre_clear_handler();
    // after deleting graph
    virtual void post_clear_handler();

    //
    // node handler
    //
    
    // before inserting a node
    virtual void pre_new_node_handler();
    // after inserting node v
    virtual void post_new_node_handler(node);
    // before deleting node v
    virtual void pre_del_node_handler(node);
    // after deleting a node
    virtual void post_del_node_handler();

    //
    // edge handler
    //
    
    // before creating (v,w)
    virtual void pre_new_edge_handler(node, node);
    // after insertion of e
    virtual void post_new_edge_handler(edge);
    
    // before deleteing edge e
    virtual void pre_del_edge_handler(edge);
    // after deletion of (v,w)
    virtual void post_del_edge_handler(node, node);

    //
    // Moving edges
    //
    
    // before moving e to (v,w)
    virtual void pre_move_edge_handler(edge,node,node);
    // after moved e from (v,w)
    virtual void post_move_edge_handler(edge,node,node);

    //
    // Hiding edges
    //
    
    // before hiding edge e
    virtual void pre_hide_edge_handler(edge);
    // after hiding edge e
    virtual void post_hide_edge_handler(edge);
    // before restoring edge e
    virtual void pre_restore_edge_handler(edge);
    // after restoring edge e
    virtual void post_restore_edge_handler(edge);


    //
    // Animation Handlers
    //
    // Note: these are slightly different from the GTL
    // handlers. First, they are NOT const. Second, they use
    // const string& instead of string.
    //

    virtual void touch (node, const string&);
    virtual void touch (edge, const string&);

    virtual void comment (const string&);

    virtual bool query ();
    virtual bool query (node, node);
    virtual bool query (edge);


    //
    // Cut & Paste
    //

    virtual GT_Graph& copy (GT_Graph& into_graph);
    virtual node copy (node n, GT_Graph& into_graph);
    virtual edge copy (edge e,node source, node target, GT_Graph& into_graph);
    
    //
    // Utilities
    //

    GT_Rectangle bbox (bool include_width = true) const;
    GT_Rectangle bbox (const node n, bool include_width = true) const;
    GT_Rectangle bbox (const edge e, bool include_width = true) const;
    GT_Rectangle bbox (const list<node>& nodes,
	bool include_width = true) const;
    GT_Rectangle bbox (const list<edge>& edges,
	bool include_width = true) const;
    
    virtual void bbox (GT_Rectangle& bbox,
	bool include_width = true) const;
    virtual void bbox (const node n, GT_Rectangle& bbox,
	bool include_width = true) const;
    virtual void bbox (const edge e, GT_Rectangle& bbox,
	bool include_width = true) const;
    virtual void bbox (const list<node>& nodes, GT_Rectangle& bbox,
	bool include_width = true) const;
    virtual void bbox (const list<edge>& edges, GT_Rectangle& bbox,
	bool include_width = true) const;
    
    virtual node find_node (const int id) const;
    virtual edge find_edge (const int id) const;
    virtual node find_node_with_uid (const int node_id) const;
    virtual node find_node_with_label_uid (const int node_id) const;
    virtual edge find_edge_with_uid (const int edge_id) const;
    virtual edge find_edge_with_label_uid (const int edge_id) const;

    //
    // attrs accessors for attribute lists
    //
	
    inline GT_List_of_Attributes* attrs();
    inline GT_List_of_Attributes* attrs (const node v);
    inline GT_List_of_Attributes* attrs (const edge e);

    inline const GT_List_of_Attributes* attrs() const;
    inline const GT_List_of_Attributes* attrs (const edge e) const;
    inline const GT_List_of_Attributes* attrs (const node v) const;
    
    virtual void attrs (GT_Graph_Attributes* attrs);
    virtual void attrs (const node v, GT_Node_Attributes* attrs);
    virtual void attrs (const edge e, GT_Edge_Attributes* attrs);
    
    //
    // gt accessors
    //
	
    GT_Node_Attributes& gt(node v);
    GT_Edge_Attributes& gt(edge e);
    GT_Graph_Attributes& gt();

    const GT_Node_Attributes& gt(const node v) const;
    const GT_Edge_Attributes& gt(const edge e) const;
    const GT_Graph_Attributes& gt() const;

    //
    // extract
    //
	
    virtual int extract (GT_List_of_Attributes* list, string& message);

    //
    // Graph drawing operations
    //
	
    virtual int draw (const list<node>& nodes, const list<edge>& edges,
	bool force = false);
    int draw (bool force = false);
    virtual int draw (node n, bool force = false);
    virtual int draw (edge e, bool force = false);	

    virtual int move_node (node n,
	const double x_diff,
	const double y_diff,
	GT_Device* fast = 0);
    virtual int move_edge (edge e,
	const double x_diff,
	const double y_diff,
	GT_Device* fast = 0);
    virtual int move_nodes (const list<node>& nodes,
	const double x_diff,
	const double y_diff,
	GT_Device* fast = 0);

    virtual int scale (double by,
	GT_Point& origin);
    
    virtual int begin_draw ();
    virtual int end_draw ();

    virtual void reset_all_changes (const list<node>& nodes,
	const list<edge>& edges);
    virtual int update_marks (const list<node>& nodes,
	const list<edge>& edges);
    virtual int update_marks (node);
    virtual int update_marks (edge);
    virtual bool test_update_marks (node) const;
    virtual bool test_update_marks (edge) const;

    virtual GT_UIObject* new_uiobject (GT_Device* device, GT_Graph&);
    virtual GT_UIObject* new_uiobject_label (GT_Device* device, GT_Graph&);
    virtual GT_UIObject* new_uiobject (GT_Device* device, GT_Graph&,
	node);
    virtual GT_UIObject* new_uiobject_label (GT_Device* device, GT_Graph&,
	node);
    virtual GT_UIObject* new_uiobject (GT_Device* device, GT_Graph&,
	edge);
    virtual GT_UIObject* new_uiobject_label (GT_Device* device, GT_Graph&,
	edge);

    //
    // update
    //
	
    virtual void update ();
    virtual void update (node n);
    virtual void update (edge e);

    virtual void update_styles ();
    virtual void update_styles (node n);
    virtual void update_styles (edge e);

    virtual void update_coordinates (); // unused
    virtual void update_coordinates (node n);
    virtual void update_coordinates (edge e);
	
    virtual void update_label (); // unused
    virtual void update_label (node n);
    virtual void update_label (edge e);

    virtual void update_label_coordinates (); // unused
    virtual void update_label_coordinates (node n);
    virtual void update_label_coordinates (edge e);

    //
    // Friends
    //

    friend ostream& operator<< (ostream& out, const GT_Graph& G);
};

ostream& operator<< (ostream& out, const GT_Graph& G);


//
// inline definitions
//

//
// attached
//

inline graph* GT_Graph::attached() const
{
    return the_graph;
}



//
// leda
//


inline void GT_Graph::leda (graph* g)
{
    attach (g);
}


inline void GT_Graph::leda (graph& g)
{
    attach (&g);
}


inline void GT_Graph::leda (GT_Shuttle& g)
{
    attach (g);
    g.attach (this);
}


inline graph& GT_Graph::leda ()
{
    assert (the_graph != 0);
    return *(the_graph);
}

inline const graph& GT_Graph::leda () const
{
    assert (the_graph != 0);
    return *(the_graph);
}
	


//
// gtl
//

inline void GT_Graph::gtl (graph* g)
{
    attach (g);
}


inline void GT_Graph::gtl (graph& g)
{
    attach (&g);
}


inline void GT_Graph::gtl (GT_Shuttle& g)
{
    attach (g);
    g.attach (this);
}


inline graph& GT_Graph::gtl ()
{
    assert (the_graph != 0);
    return *(the_graph);
}
	

inline const graph& GT_Graph::gtl () const
{
    assert (the_graph != 0);
    return *(the_graph);
}


//
// node_by_id
// edge_by_id
//


inline node GT_Graph::node_by_id (int id) const
{
    return (*the_nodes_by_id.find(id)).second;
    // return the_nodes_by_id[id];
}


inline edge GT_Graph::edge_by_id (int id) const
{
    return (*the_edges_by_id.find(id)).second;
    // return the_edges_by_id[id];
}



//
// attrs
//

inline GT_List_of_Attributes* GT_Graph::attrs()
{
    assert (the_graph != 0);
    return the_graph_attrs;
}

inline GT_List_of_Attributes* GT_Graph::attrs (const node v)
{
    assert (the_graph != 0);
    return the_node_attrs[v];
}

inline GT_List_of_Attributes* GT_Graph::attrs (const edge e)
{
    assert (the_graph != 0);
    return the_edge_attrs[e];
}



inline const GT_List_of_Attributes* GT_Graph::attrs() const
{
    assert (the_graph != 0);
    return the_graph_attrs;
}

inline const GT_List_of_Attributes* GT_Graph::attrs (const node n) const
{
    assert (the_graph != 0);
    return the_node_attrs [n];
}

inline const GT_List_of_Attributes* GT_Graph::attrs (const edge e) const
{
    assert (the_graph != 0);
    return the_edge_attrs [e];
}


//
// gt
//

inline GT_Graph_Attributes& GT_Graph::gt()
{
    assert (the_graph != 0);
    assert (the_graph_attrs != 0);

    return *the_graph_attrs;
}


inline GT_Node_Attributes& GT_Graph::gt(node n)
{
    assert (the_graph != 0);
    assert (the_node_attrs[n] != 0);

    return *the_node_attrs[n];
}


inline GT_Edge_Attributes& GT_Graph::gt(edge e)
{
    assert (the_graph != 0);
    assert (the_edge_attrs[e] != 0);

    return *the_edge_attrs[e];
}



inline const GT_Graph_Attributes& GT_Graph::gt() const
{
    assert (the_graph != 0);
    assert (the_graph_attrs != 0);

    return *the_graph_attrs;
}


inline const GT_Node_Attributes& GT_Graph::gt(const node n) const
{
    assert (the_graph != 0);
    assert (the_node_attrs[n] != 0);

    return *the_node_attrs[n];
}


inline const GT_Edge_Attributes& GT_Graph::gt(const edge e) const
{
    assert (the_graph != 0);
    assert (the_edge_attrs[e] != 0);

    return *the_edge_attrs[e];
}


inline const map<GT_Key,GT_Node_Attributes*>& GT_Graph::node_styles() const
{
    return the_node_styles;
}


inline const map<GT_Key,GT_Edge_Attributes*>& GT_Graph::edge_styles() const
{
    return the_edge_styles;
}

#endif
