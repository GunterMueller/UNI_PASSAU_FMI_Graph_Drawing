
/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: GETPMOD.C                                                  ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This module is used by stnummod.c to get a path for     ** */
/* **              the st-numbering algorithm                              ** */
/* **                                                                      ** */
/* ** Date: 18.5.1994                                                      ** */
/* **                                                                      ** */
/* ************************************************************************** */
/*                                                                            */


#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "dfsmod.h"
#include "stnummod.h"
#include "getpmod.h"
#include "testmod.h"


/*                                                                            */
/* ************************************************************************** */
/* **                      Some general help-functions                     ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* This function marks a edge as old edge                                     */
/*                                                                            */

void mark_old_edge(Sedge e)
{
	Edge_state	helpedge;

	helpedge = attr_data_of_type(e,Edge_state);
	helpedge->is_tree_edge=1;
	helpedge->is_back_edge=1;
}


/*                                                                            */
/* This function marks a node as old node                                     */
/*                                                                            */

void mark_old_node(Snode n)
{
	Node_state	helpnode;

	helpnode=get_dfs_state(n);
	helpnode->marked=1;
}


/*                                                                            */
/* This function adds a new node to the current path                          */
/*                                                                            */

static Slist push_node_to_path(Slist path, Snode v)
{
    path=add_immediately_to_slist(path,make_attr(ATTR_DATA,(char *)v));
    path=path->pre;
    return(path);
}


/*                                                                            */
/* This function finds a tree-edge adjacent to the node v. It returns a       */
/* Sedge, that is the edge. If no tree-edge does exist, NULL is returned      */
/*                                                                            */
/*
static Sedge get_tree_edge(Snode v)
{
    Sedge       e;
    Edge_state  helpedge;
    Slist       embed_edges,
                helpembed;

    embed_edges = get_embed_edges(v);    

    for_slist(embed_edges,helpembed)
    {
        e = attr_data_of_type(helpembed,Sedge);
        helpedge=attr_data_of_type(e,Edge_state);
        if((helpedge->is_tree_edge==1) &&
           (helpedge->is_back_edge==0))
        {
            return(e);
        }
    }
    end_for_slist(embed_edges,helpembed);

    return((Sedge)NULL);
}
*/

/*                                                                            */
/* This function finds a tree-edge adjacent to the node v, that leads to a    */
/* higher dfs-numbered node. It returns a Sedge, that is the edge. If no      */
/* tree-edge does exist, NULL is returned                                     */
/*                                                                            */

static Sedge get_up_tree_edge(Snode v)
{
    Sedge       e;
    Edge_state  helpedge;
    Node_state  helpnode_v,helpnode_w;
    Slist       embed_edges,
                helpembed;

    embed_edges = get_embed_edges(v);
    helpnode_v  = get_dfs_state(v);

    for_slist(embed_edges,helpembed)
    {
        e = attr_data_of_type(helpembed,Sedge);
        helpnode_w=get_dfs_state(opposit_node(v,e));
        helpedge=attr_data_of_type(e,Edge_state);
        if((helpedge->is_tree_edge==1) &&
           (helpedge->is_back_edge==0) &&
           (helpnode_v->dfs_num < helpnode_w->dfs_num))
        {
            return(e);
        }
    }
    end_for_slist(embed_edges,helpembed);

    return((Sedge)NULL);
}


/*                                                                            */
/* This function finds a tree-edge adjacent to the node v, that leads to a    */
/* higher dfs-numbered node with low-point lowpoint. It returns a Sedge,      */
/* that is the edge. If no tree-edge does exist, NULL is returned             */
/*                                                                            */

static Sedge get_up_tree_edge_with_low_pt(Snode v, int lowpoint)
{
    Sedge       e;
    Edge_state  helpedge;
    Node_state  helpnode_v,helpnode_w;
    Slist       embed_edges,
                helpembed;

    embed_edges = get_embed_edges(v);
    helpnode_v = get_dfs_state(v);

    for_slist(embed_edges,helpembed)
    {
        e = attr_data_of_type(helpembed,Sedge);
        helpnode_w=get_dfs_state(opposit_node(v,e));
        helpedge=attr_data_of_type(e,Edge_state);
        if((helpedge->is_tree_edge==1) &&
           (helpedge->is_back_edge==0) &&
           (helpnode_v->dfs_num < helpnode_w->dfs_num) &&
           (helpnode_w->low_pt == lowpoint))
        {
            return(e);
        }
    }
    end_for_slist(embed_edges,helpembed);

    return((Sedge)NULL);
}


/*                                                                            */
/* This function finds a back-edge that leads to a node with lower dfs-number */
/* If no such edge does exist, NULL is returned                               */
/*                                                                            */

static Sedge get_back_edge_v_w(Snode v)
{
    Sedge       e;
    Edge_state  helpedge;
    Node_state  helpnode_v,helpnode_w;
    Slist       embed_edges,
                helpembed;

    embed_edges = get_embed_edges(v);
    helpnode_v = get_dfs_state(v);

    for_slist(embed_edges,helpembed)
    {
        e = attr_data_of_type(helpembed,Sedge);
        helpnode_w=get_dfs_state(opposit_node(v,e));
        helpedge=attr_data_of_type(e,Edge_state);
        if((helpedge->is_tree_edge==0) &&
           (helpedge->is_back_edge==1) &&
           (helpnode_v->dfs_num > helpnode_w->dfs_num))
        {
            return(e);
        }
    }
    end_for_slist(embed_edges,helpembed);

    return((Sedge)NULL);
}


/*                                                                            */
/* This function finds a back-edge adjacent to the node v that lead to a node */
/* with a higher dfs-number. It returns a Sedge, that is the edge. If no      */
/* back-edge does exist, NULL is returned                                     */
/*                                                                            */

static Sedge get_back_edge_w_v(Snode v)
{
    Sedge   e;              /* variable for source-/targetlist */
    Edge_state  helpedge;
    Node_state  helpnode_v,helpnode_w;
    Slist       embed_edges,
                helpembed;

    embed_edges = get_embed_edges(v);
    helpnode_v =get_dfs_state(v);

    for_slist(embed_edges,helpembed)
    {
        e = attr_data_of_type(helpembed,Sedge);    
        helpedge=attr_data_of_type(e,Edge_state);
        helpnode_w = get_dfs_state(opposit_node(v,e));
        if((helpedge->is_tree_edge==0) &&
           (helpedge->is_back_edge==1) &&
           (helpnode_v->dfs_num < helpnode_w->dfs_num))
        {
            return(e);
        }
    }
    end_for_slist(embed_edges,helpembed);

    return((Sedge)NULL);
}


/*                                                                            */
/* This function is used for finding the first backpath                       */
/*                                                                            */

static Slist first_back_path(Snode v, Snode w, Sedge firstedge)
{
    Slist path = empty_slist;

    path = push_node_to_path(path,v);
    path = push_node_to_path(path,w);

    return(path);
}


/*                                                                            */
/* ************************************************************************** */
/* **        The following part is used for finding a tree-path            ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* This function is used for recursively finding the "second" part of the     */
/* tree path                                                                  */
/*                                                                            */

static Slist search_tree_path(Slist path, Snode v, int lowpoint)
{
    Node_state  helpnode_v,helpnode_w;
    Sedge   e;
    Edge_state  helpedge;
    Slist       embed_edges,
                helpembed;

    embed_edges = get_embed_edges(v);
    helpnode_v = get_dfs_state(v);

    for_slist(embed_edges,helpembed)
    {
        e = attr_data_of_type(helpembed,Sedge);
        helpedge=attr_data_of_type(e,Edge_state);
        helpnode_w=get_dfs_state(opposit_node(v,e));
        if((helpedge->is_tree_edge==0)   &&
           (helpedge->is_back_edge==1)   &&
           (helpnode_w->dfs_num==lowpoint) /* &&
           (helpnode_v->dfs_num > helpnode_w->dfs_num) */ )
        {
            mark_old_edge(e);
            path=push_node_to_path(path,opposit_node(v,e));
            return(path);
        }
    }
    end_for_slist(embed_edges,helpembed);

    e=get_up_tree_edge_with_low_pt(v,lowpoint);
    if(e!=NULL)
    {
        if(v==e->snode)
        {
            mark_old_node(e->tnode);
            mark_old_edge(e);
            path=push_node_to_path(path,e->tnode);
            path=search_tree_path(path,e->tnode,lowpoint);
            return(path);
        }
        else
        {
            mark_old_node(e->snode);
            mark_old_edge(e);
            path=push_node_to_path(path,e->snode);
            path=search_tree_path(path,e->snode,lowpoint);
            return(path);
        }
    }
    return NULL;
}


/*                                                                            */
/* This function finds a tree-path, where the first edge is given. Slist is   */
/* returned                                                                   */
/*                                                                            */

static Slist tree_path(Snode v, Sedge firstedge)
{
    Node_state  helpnode;
    Slist   path = empty_slist;
    int     lowpoint;

    path=push_node_to_path(path,v);

    if(v==firstedge->snode)
    {
        helpnode=get_dfs_state(firstedge->tnode);
        if(helpnode->marked==1)
        {
            path=push_node_to_path(path,firstedge->tnode);
            return(path);
        }
        else
        {
            mark_old_node(firstedge->tnode);
            path=push_node_to_path(path,firstedge->tnode);
            lowpoint=helpnode->low_pt;
            path=search_tree_path(path,firstedge->tnode,lowpoint);
            return(path);
        }
    }
    else
    {
        helpnode=get_dfs_state(firstedge->snode);
        if(helpnode->marked==1)
        {
            path=push_node_to_path(path,firstedge->snode);
            return(path);
        }
        else
        {
            mark_old_node(firstedge->snode);
            path=push_node_to_path(path,firstedge->snode);
            lowpoint=helpnode->low_pt;
            path=search_tree_path(path,firstedge->snode,lowpoint);
            return(path);
        }
    }
}


/*                                                                            */
/* ************************************************************************** */
/* **         The following part is used for finding a back-path           ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* This function is used for recursively finding the "second" part of the     */
/* back-path                                                                  */
/*                                                                            */

static Slist search_back_path(Slist path, Snode v)
{
    Node_state  helpnode_v;
    Node_state  helpnode_w;
    Sedge       e;
    Edge_state  helpedge;
    Slist	embed_edges,
            helpembed;

    embed_edges = get_embed_edges(v);

    helpnode_v=get_dfs_state(v);

    for_slist(embed_edges,helpembed)
    {
        e = attr_data_of_type(helpembed,Sedge);
        helpedge=attr_data_of_type(e,Edge_state);
        helpnode_w=get_dfs_state(opposit_node(v,e));
        if((helpedge->is_tree_edge==1) &&
           (helpedge->is_back_edge==0) &&
            (helpnode_v->dfs_num > helpnode_w->dfs_num))
        {
            if((helpnode_w->marked)==1)
            {
                mark_old_edge(e);
                path=push_node_to_path(path,opposit_node(v,e));
                return(path);
            }
            else
            {
                mark_old_edge(e);
                mark_old_node(opposit_node(v,e));
                path=push_node_to_path(path,opposit_node(v,e));
                path=search_back_path(path,opposit_node(v,e));
                return(path);
            }
        }
    }
    end_for_slist(embed_edges,helpembed);
    return NULL;
}


/*                                                                            */
/* This function finds a back_path, where the first edge is given. Slist is   */
/* returned                                                                   */
/*                                                                            */

static Slist back_path(Snode v, Sedge firstedge)
{
    Node_state  helpnode;
    Slist       path = empty_slist;

    path=push_node_to_path(path,v);

    if(v==firstedge->snode)
    {
        helpnode=get_dfs_state(firstedge->tnode);
        if(helpnode->marked==1)
        {
            path=push_node_to_path(path,firstedge->tnode);
            return(path);
        }
        else
        {
            mark_old_node(firstedge->tnode);
            path=push_node_to_path(path,firstedge->tnode);
            path=search_back_path(path,firstedge->tnode);
            return(path);
        }
    }
    else
    {
        helpnode=get_dfs_state(firstedge->snode);
        if(helpnode->marked==1)
        {
            path=push_node_to_path(path,firstedge->snode);
            return(path);
        }
        else
        {
            mark_old_node(firstedge->snode);
            path=push_node_to_path(path,firstedge->snode);
            path=search_back_path(path,firstedge->snode);
            return(path);
        }
    }
}


/*                                                                            */
/* ************************************************************************** */
/* This function is the "main-function" of the path-finding algorithm. It is  */
/* used by the function st_number from the module STNUMMOD.C and returns a    */
/* Slist, that contains the node-numbers and their st-number                  */
/* ************************************************************************** */
/*                                                                            */

Slist get_path(Snode v)
{
    Slist   path = empty_slist;
    Sedge   dummy_edge;

    if((dummy_edge = get_back_edge_v_w(v)) != NULL)
    {
        mark_old_edge(dummy_edge);
        path = first_back_path(v,opposit_node(v,dummy_edge),dummy_edge);
        return(path);
    }
    
    if((dummy_edge = get_up_tree_edge(v)) != NULL)
    {
        mark_old_edge(dummy_edge);
        path = tree_path(v,dummy_edge);
        return(path);
    }
    
    if((dummy_edge = get_back_edge_w_v(v)) != NULL)
    {
        mark_old_edge(dummy_edge);
        path = back_path(v,dummy_edge);
        return(path);    
    }
    
    return(empty_slist);
}


/*                                                                            */
/* ************************************************************************** */
/* **                       END OF FILE: GETPMOD.C                         ** */
/* ************************************************************************** */

