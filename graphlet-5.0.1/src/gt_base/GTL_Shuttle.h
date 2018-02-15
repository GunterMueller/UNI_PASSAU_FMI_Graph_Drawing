/* This software is distributed under the Lesser General Public License */
#ifndef GT_GTL_SHUTTLE_H
#define GT_GTL_SHUTTLE_H

//
// GTL_Shuttle.h
//
// This file defines the class GT_GTL_Shuttle
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/GTL_Shuttle.h,v $
// $Author: himsolt $
// $Revision: 1.1 $
// $Date: 1999/06/24 11:13:04 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Shuttle.h"



template<class T>
class GT_GTL_Shuttle : protected T, public GT_Shuttle {

    GT_CLASS (GT_GTL_Shuttle, GT_Shuttle);

public:
	
    GT_GTL_Shuttle ()
    {
    }
	
    virtual graph& gtl()
    {
	return *this;
    }
	

    //////////////////////////////////////////
    //
    // new LEDA handlers
    //
    //////////////////////////////////////////
    
    //
    // node handler
    //
    
    // before inserting a node
    virtual void pre_new_node_handler();
    // after inserting node v
    virtual void post_new_node_handler (node);
    // before deleting node v
    virtual void pre_del_node_handler (node);
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
    // clear handler
    //
    
    // before deleting graph
    virtual void pre_clear_handler();
    // after deleting graph
    virtual void post_clear_handler();

    //
    // Animation Handlers
    //
    
    virtual void touch(node, string) const;
    virtual void touch(edge, string) const;
    virtual void comment(string) const;
    virtual bool query() const;
    virtual bool query(node, node) const;
    virtual bool query(edge) const;
};


//
// Handler definitions
//

template<class T>
void GT_GTL_Shuttle<T>::pre_new_node_handler()
{
    the_graph->pre_new_node_handler ();
}

template<class T>
void GT_GTL_Shuttle<T>::post_new_node_handler (node n)
{
    the_graph->post_new_node_handler (n);
}

template<class T>
void GT_GTL_Shuttle<T>::pre_del_node_handler (node n)
{
    the_graph->pre_del_node_handler (n);
}

template<class T>
void GT_GTL_Shuttle<T>::post_del_node_handler ()
{
    the_graph->post_del_node_handler ();
}

template<class T>
void GT_GTL_Shuttle<T>::pre_new_edge_handler (node n1, node n2)
{
    the_graph->pre_new_edge_handler (n1, n2);
}

template<class T>
void GT_GTL_Shuttle<T>::post_new_edge_handler (edge e)
{
    the_graph->post_new_edge_handler (e);
}

template<class T>
void GT_GTL_Shuttle<T>::pre_del_edge_handler (edge e)
{
    the_graph->pre_del_edge_handler (e);
}

template<class T>
void GT_GTL_Shuttle<T>::post_del_edge_handler (node n1, node n2)
{
    the_graph->post_del_edge_handler (n1, n2);
}

template<class T>
void GT_GTL_Shuttle<T>::pre_move_edge_handler (edge e, node n1, node n2)
{
    the_graph->pre_move_edge_handler (e, n1,n2);
}

template<class T>
void GT_GTL_Shuttle<T>::post_move_edge_handler (edge e, node n1, node n2)
{
    the_graph->post_move_edge_handler (e, n1, n2);
}

template<class T>
void GT_GTL_Shuttle<T>::pre_hide_edge_handler (edge e)
{
    the_graph->pre_hide_edge_handler (e);
}

template<class T>
void GT_GTL_Shuttle<T>::post_hide_edge_handler (edge e)
{
    the_graph->post_hide_edge_handler (e);
}

template<class T>
void GT_GTL_Shuttle<T>::pre_restore_edge_handler (edge e)
{
    the_graph->pre_restore_edge_handler (e);
}

template<class T>
void GT_GTL_Shuttle<T>::post_restore_edge_handler (edge e)
{
    the_graph->post_restore_edge_handler (e);
}

template<class T>
void GT_GTL_Shuttle<T>::pre_clear_handler ()
{
    the_graph->pre_clear_handler ();
}

template<class T>
void GT_GTL_Shuttle<T>::post_clear_handler ()
{
    the_graph->post_clear_handler ();
}

template<class T>
void GT_GTL_Shuttle<T>::touch (node n, string s) const
{
    the_graph->touch (n, s);
}

template<class T>
void GT_GTL_Shuttle<T>::touch (edge e, string s) const
{
    the_graph->touch (e, s);
}

template<class T>
void GT_GTL_Shuttle<T>::comment (string s) const
{
    the_graph->comment (s);
}

template<class T>
bool GT_GTL_Shuttle<T>::query () const
{
    return the_graph->query ();
}

template<class T>
bool GT_GTL_Shuttle<T>::query (node n1, node n2) const
{
    return the_graph->query (n1, n2);
}

template<class T>
bool GT_GTL_Shuttle<T>::query (edge e) const
{
    return the_graph->query (e);
}


#endif
