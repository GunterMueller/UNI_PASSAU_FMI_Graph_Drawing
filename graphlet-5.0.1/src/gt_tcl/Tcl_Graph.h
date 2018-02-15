/* This software is distributed under the Lesser General Public License */
#ifndef TCL_GRAPH_H
#define TCL_GRAPH_H

//
// Tcl_Graph.h
// 
// This module defines the class GT_Tcl_Graph
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl_Graph.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:46:12 $
// $Locker:  $
// $State: Exp $
// ----------------------------------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#include <gt_base/Graphlet.h>
#include <gt_base/Graph.h>

#include "Tcl.h"
#include "Tcl_Info.h"
#include "Tk_Device.h"

//////////////////////////////////////////
//
// class GT_Tcl_Graph
//
//////////////////////////////////////////

class GT_Device;
class GT_Tk_Device;

#include "Tk_UIObject.h"

class GT_Graphscript;
class GT_Tk_UIGraph;
class GT_Tk_UINode;
class GT_Tk_UIEdge;
class GT_Tk_UIGraphlabel;
class GT_Tk_UINodelabel;
class GT_Tk_UIEdgelabel;


class GT_Tcl_Graph : public GT_Graph
{
    typedef GT_Graph baseclass;

protected:

    GT_Graphscript* the_graphscript;
    int the_hook_return_code;

    list<string> the_editors;
    string the_editors_plain;

public:

    //
    // Constructor && Descructor
    //
	
    GT_Tcl_Graph();
    virtual ~GT_Tcl_Graph();

    //
    // Accessories
    //

    inline const GT_Graphscript* graphscript () const;

    //
    // The next two commands are static as they are passed via
    // function pointers in C (without ++) procedures.
    //

    // cmd is the command "graph"
	
    static int cmd (ClientData clientData,
	Tcl_Interp* interp,
	int argc,
	char** argv);

    // static_parser is the parser for the arguments of "graph"
	
    static int static_parser (ClientData clientData,
	Tcl_Interp* interp,
	int argc,
	char** argv);

    // parser is the non-static (i.e. customizable) part of static_parser
	
    virtual int parser (Tcl_Interp* interp, int argc, char** argv);

	
    //////////////////////////////////////////
    //
    // Graph
    //
    //////////////////////////////////////////

	
    virtual void new_graph ();

    //
    // Tcl Command interpreters
    //
	
    virtual int canvas_cmd (GT_Tcl_info& tcl_info, int argc);
    virtual int editor_cmd (GT_Tcl_info& tcl_info, int argc);
	
    virtual int configure_cmd (GT_Tcl_info& tcl_info,
	int argc,
	GT_Tcl::Configure_Mode mode);
	
    virtual int draw_cmd (GT_Tcl_info& tcl_info,
	int argc);

    virtual int save_cmd (GT_Tcl_info& tcl_info,
	int argc);
    virtual void print (ostream& out);	
	
    virtual int load_cmd (GT_Tcl_info& tcl_info,
	int argc);

    virtual int delete_cmd (GT_Tcl_info& tcl_info,
	int argc);

    virtual int scale_cmd (GT_Tcl_info& tcl_info,
	int argc);
    virtual int scale (double by,
	GT_Point& origin);
    
    virtual int nodes_cmd (GT_Tcl_info& tcl_info,
	int argc);
    virtual int edges_cmd (GT_Tcl_info& tcl_info,
	int argc);

    virtual int bbox_cmd (GT_Tcl_info& tcl_info,
	int argc);

    virtual int isnode_cmd (GT_Tcl_info& tcl_info,
	int argc);
    virtual int isedge_cmd (GT_Tcl_info& tcl_info,
	int argc);
    virtual int typeof_cmd (GT_Tcl_info& tcl_info,
	int argc);
    
    virtual int translate_cmd (GT_Tcl_info& tcl_info,
	int argc);

    virtual int mark_cmd (GT_Tcl_info& tcl_info,
	int argc);

    virtual int new_style_cmd (GT_Tcl_info& tcl_info,
	int argc);

    virtual int style_cmd (GT_Tcl_info& tcl_info,
	int argc);
    
    //
    // Configure procs
    //
	
    virtual int common_configure_set (GT_Tcl_info& info,
	GT_Common_Attributes& attrs, const GT_Key& key, char* value,
	bool& found);
    virtual int common_configure_get (
	const GT_Common_Attributes& attrs,
	const GT_Key& key, Tcl_DString& result,
	const GT_Tcl::Configure_Mode mode,
	bool &found);

    virtual int graphconfigure_set (GT_Tcl_info& info,
	GT_Graph_Attributes& attrs, const GT_Key& key, char* value,
	bool& found);
    virtual int graphconfigure_get (
	const GT_Graph_Attributes& attrs,
	const GT_Key& key, Tcl_DString& result,
	const GT_Tcl::Configure_Mode mode,
	bool& found);


    //////////////////////////////////////////
    //
    // Node
    //
    //////////////////////////////////////////

	
    virtual int create_node_cmd (GT_Tcl_info& tcl_info,
	int argc);

    //
    // Tcl Command procs
    //
	
    virtual int nodemove_cmd (GT_Tcl_info& tcl_info,
	int argc);
	
    //
    // Node Configuration procs
    //
	
    virtual int nodeconfigure_set (GT_Tcl_info& tcl_info,
	GT_Node_Attributes& attrs, const GT_Key& key, char* value,
	bool& found);
    virtual int nodeconfigure_get (
	const GT_Node_Attributes& attrs,
	const GT_Key& key, Tcl_DString& result,
	const GT_Tcl::Configure_Mode mode,
	bool& found);


	
    //////////////////////////////////////////
    //
    // Edge 
    //
    //////////////////////////////////////////


    virtual int create_edge_cmd (
	GT_Tcl_info& tcl_info,
	int argc);

    //
    // Edge Configuration procs
    //
	
    virtual int edgeconfigure_set (GT_Tcl_info& tcl_info,
	GT_Edge_Attributes& attrs, const GT_Key& key, char* value,
	bool& found);
    virtual int edgeconfigure_get (
	const GT_Edge_Attributes& attrs, const GT_Key& key,Tcl_DString& result,
	const GT_Tcl::Configure_Mode mode,
	bool& found);


    //////////////////////////////////////////
    //
    // Cut & Paste
    //
    //////////////////////////////////////////
    
    virtual int copy_cmd (GT_Tcl_info& tcl_info, int argc);
    virtual int copy_node_cmd (GT_Tcl_info& tcl_info, int argc);
    virtual int copy_edge_cmd (GT_Tcl_info& tcl_info, int argc);

    
    //////////////////////////////////////////
    //
    // Hook commands
    //
    //////////////////////////////////////////
	
    // Low level version

    static int run_hooks (Tcl_Interp* interp, int& the_hook_return_code,
	char* graph_name,
	char* handler_name,
	char* additional_args1 = 0,
	char* additional_args2 = 0,
	char* additional_args3 = 0,
	char* additional_args4 = 0,
	char* additional_args5 = 0);

    // High Level versions

    virtual int run_hooks (const GT_Key& handler_name,
	char* additional_args1 = 0,
	char* additional_args2 = 0,
	char* additional_args3 = 0,
	char* additional_args4 = 0,
	char* additional_args5 = 0);

    virtual int run_hooks (const GT_Key& handler_name,
	node n,
	char* additional_args = 0);

    virtual int run_hooks (const GT_Key& handler_name,
	const list<node>& nodes,
	char* additional_args = 0);

    virtual int run_hooks (const GT_Key& handler_name,
	node n1,
	node n2,
	char* additional_args = 0);

    virtual int run_hooks (const GT_Key& handler_name,
	edge e,
	node n1,
	node n2,
	char* additional_args = 0);

    virtual int run_hooks (const GT_Key& handler_name,
	edge e,
	char* additional_args = 0);

    virtual int run_hooks (const GT_Key& handler_name,
	const list<edge>& edges,
	char* additional_args = 0);

    virtual int run_hooks (const GT_Key& handler_name,
	const list<node>& nodes,
	const list<edge>& edges,
	char* additional_args = 0);

    virtual int run_hooks (const GT_Key& handler_name,
	const list<string>& strings,
	char* additional_args = 0);

	
    //////////////////////////////////////////
    //
    // Drawing
    //
    //////////////////////////////////////////

    //
    // Configure Common Graphics
    //
	
    virtual int graphicsconfigure_set (GT_Tcl_info& tcl_info,
	GT_Common_Graphics& graphics, const GT_Key& key, char* value,
	bool& found);

    virtual int graphicsconfigure_get (
	const GT_Common_Graphics& graphics,
	const GT_Key& key, Tcl_DString& result,
	const GT_Tcl::Configure_Mode mode,
	bool& found);

    //
    // draw procs
    //
	
    virtual int draw (const list<node>& nodes, const list<edge>& edges,
	bool force = false);
    virtual int draw (node n, bool force = false);
    virtual int draw (edge e, bool force = false);	
    virtual int draw (bool force = false); // Compatibility ??

//     virtual int move_node (node n,
// 	const double x_diff,
// 	const double y_diff);
    
    virtual int move_nodes (const list<node>& nodes,
	const double x_diff,
	const double y_diff,
	GT_Device* fast = 0);
    virtual int move_edge (edge e,
	const double x_diff,
	const double y_diff,
	GT_Device* fast = 0);

    virtual int begin_draw ();
    virtual int end_draw ();


    //
    // update
    //

    virtual void update ();
    virtual void update (node n);
    virtual void update (edge e);

    virtual void update_coordinates ();
    virtual void update_coordinates (node n);
    virtual void update_coordinates (edge e);
	
    virtual void update_label ();
    virtual void update_label (node n);
    virtual void update_label (edge e);
	
    virtual void update_label_coordinates ();
    virtual void update_label_coordinates (node n);
    virtual void update_label_coordinates (edge e);


    //
    // Customization
    //
    
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

    virtual GT_Tk_Device* new_tcl_device (const string& name);
	

    //////////////////////////////////////////
    //
    // LEDA handlers
    //
    //////////////////////////////////////////

    //
    // graph handler
    //

    // before creating a new graph
    virtual void pre_new_graph_handler();
    // after creating a new graph
    virtual void post_new_graph_handler ();
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
    // Note: these are slightly different from the LEDA
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
    // GT_Tcl_Graph related keys
    //

    static GT_Key pre_new_graph_hook;
    static GT_Key post_new_graph_hook;
    static GT_Key pre_new_node_hook;
    static GT_Key post_new_node_hook;
    static GT_Key pre_new_edge_hook;
    static GT_Key post_new_edge_hook;

    static GT_Key pre_delete_graph_hook;
    static GT_Key post_delete_graph_hook;
    static GT_Key pre_delete_node_hook;
    static GT_Key post_delete_node_hook;
    static GT_Key pre_delete_edge_hook;
    static GT_Key post_delete_edge_hook;

    static GT_Key pre_copy_graph_hook;
    static GT_Key post_copy_graph_hook;
    static GT_Key pre_copy_node_hook;
    static GT_Key post_copy_node_hook;
    static GT_Key pre_copy_edge_hook;
    static GT_Key post_copy_edge_hook;

    static GT_Key pre_configure_graph_hook;
    static GT_Key post_configure_graph_hook;
    static GT_Key pre_configure_node_hook;
    static GT_Key post_configure_node_hook;
    static GT_Key pre_configure_edge_hook;
    static GT_Key post_configure_edge_hook;
    static GT_Key pre_configure_style_hook;
    static GT_Key post_configure_style_hook;

    static GT_Key pre_directed_hook;
    static GT_Key post_directed_hook;

    static GT_Key pre_canvas_hook;
    static GT_Key post_canvas_hook;
    static GT_Key pre_editor_hook;
    static GT_Key post_editor_hook;

//     static GT_Key pre_configure_hook;
//     static GT_Key post_configure_hook;
//     static GT_Key pre_get_hook;
//     static GT_Key post_get_hook;
//     static GT_Key pre_set_hook;
//     static GT_Key post_set_hook;

    static GT_Key pre_draw_hook;
    static GT_Key post_draw_hook;

    static GT_Key pre_save_hook;
    static GT_Key post_save_hook;
    static GT_Key pre_load_hook;
    static GT_Key post_load_hook;

    static GT_Key pre_scale_hook;
    static GT_Key post_scale_hook;

//     static GT_Key pre_nodemove_hook;
//     static GT_Key post_nodemove_hook;

//     static GT_Key pre_mark_hook;
//     static GT_Key post_mark_hook;

    static GT_Key pre_style_hook;
    static GT_Key post_style_hook;
};


//
// Accessories
//

inline const GT_Graphscript* GT_Tcl_Graph::graphscript () const
{
    return the_graphscript;
}

#endif
