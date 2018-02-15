/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: DFSMOD.C                                                   ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This file contains all the functions to perform a dfs   ** */
/* **              on a given graph g.                                     ** */
/* **              dfs_main() makes use of the functions in BISECMOD.H to  ** */
/* **              find the largest face and bisect it. This bisecting     ** */
/* **              edge is returned to be used as first edge of the        ** */
/* **              following parts of the algorithm.                       ** */
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
#include "testmod.h"
#include "bisecmod.h"


/*                                                                            */
/* This function returns the node on the opposit side of the edge e           */
/*                                                                            */

Snode opposit_node(Snode n, Sedge e)
{
    if(n == e->snode)
    {
        return((Snode)e->tnode);
    }
    else
    {
        return((Snode)e->snode);
    }

}


/*                                                                            */
/* This function marks the edge e as tree-edge                                */
/*                                                                            */

static void mark_tree_edge(Sedge e)
{
    Edge_state  helpedge;
	
    helpedge = attr_data_of_type(e,Edge_state);
    helpedge->is_tree_edge = YES;
    helpedge->is_back_edge = NO;
}


/*                                                                            */
/* This function marks the edge e as back_edge                                */
/*                                                                            */

static void mark_back_edge(Sedge e)
{
    Edge_state  helpedge;

    helpedge = attr_data_of_type(e,Edge_state);
    helpedge->is_tree_edge = NO;
    helpedge->is_back_edge = YES;
}


/*                                                                            */
/* This function returns 1, if the edge e is either a tree- or a back-edge    */
/* and 0, if is has not been marked before                                    */
/*                                                                            */

static int marked(Sedge e)
{
    Edge_state  helpedge;
	
    helpedge = attr_data_of_type(e,Edge_state);
    if((helpedge->is_tree_edge + helpedge->is_back_edge) == 0)
    {
        return(0);
    }
    else
    {
        return(1);
    }
}


/*                                                                            */
/* This function returns the minimum of two values, where -1 is the highest   */
/* value.                                                                     */
/*                                                                            */

static int min__1(int x, int y)
{
    if(x == -1 && y == -1) return(-1);
    if(x == -1 && y >= 0)  return(y);
    if(x >= 0  && y == -1) return(x);
    if(x >= 0  && y >= 0)  return(minimum(x,y));
    return -1;
}


/*                                                                            */
/* This function returns the pointer to the list element of the ordered edge  */
/* list of firstnode, that contains firstedge.                                */
/*                                                                            */

static Slist get_firstedgelist(Snode firstnode, Sedge firstedge)
{
    Slist embed_list,
          l,
          list;
    Sedge edge;

    embed_list = get_embed_edges(firstnode);
    for_slist(embed_list,l)
    {
        edge = attr_data_of_type(l,Sedge);
        if(edge == firstedge)
        {
            list = l;
            break;
        }
    }
    end_for_slist(embed_list,l);

    return((Slist)list);
}


/*                                                                            */
/* This function initialises the node and edge attributes of the sgraph g.    */
/* Node attributes: dfs_num = 0, low_pt = -1, marked = 0,                     */
/*                  embed_edges is a pointer to the ordered edge list         */
/* Edge attributes: is_tree_edge = is_back_edge = 0,                          */
/*                  face_id (currently unused) = traversed = findlabel = 0    */
/*                                                                            */

static void init_nodes_and_edges(Sgraph g)
{
    Snode       n;
    Sedge       e;
    Slist       helpembed;
    Node_state  helpstate;
    Node_attr   helpnode;
    Edge_state  helpedge;

    for_all_nodes(g,n)
    {
        helpembed = attr_data_of_type(n,Slist);

        helpstate = (Node_state)malloc(sizeof(struct node_state));
        helpstate->dfs_num = 0;
        helpstate->low_pt  = -1;
        helpstate->marked  = 0;

        helpnode = (Node_attr)malloc(sizeof(struct node_attr));
        helpnode->embed_edges = helpembed;
        helpnode->dfs_state   = helpstate;

        set_nodeattrs(n,make_attr(ATTR_DATA,(char *)helpnode));

        for_sourcelist(n,e)
        {
            if(TEST_UNIQUE_EDGE(e))
            {
                helpedge = (Edge_state)malloc(sizeof(struct edge_state));
                helpedge->is_tree_edge  = NO;
                helpedge->is_back_edge  = NO;
                helpedge->face_id       = NO;   /* unused */
                helpedge->traversed     = NO;
                helpedge->findlabel     = NO;

                set_edgeattrs(e,make_attr(ATTR_DATA,(char *)helpedge));
            }
        }
        end_for_sourcelist(n,e);
    }
    end_for_all_nodes(g,n);
}


/*                                                                            */
/* This function preparess the node's and edge's attributest for a second DFS */
/*                                                                            */

static void refresh_nodes_and_edges(Sgraph g)
{
    Snode       n;
    Sedge       e;
    Node_state  helpstate;
    Edge_state  helpedge;

    for_all_nodes(g,n)
    {
        helpstate = get_dfs_state(n);
        helpstate->dfs_num = 0;
        helpstate->low_pt  = -1;
        helpstate->marked  = 0;

        for_sourcelist(n,e)
        {
            if(TEST_UNIQUE_EDGE(e))
            {
                helpedge = attr_data_of_type(e,Edge_state);
                helpedge->is_tree_edge  = NO;
                helpedge->is_back_edge  = NO;
                helpedge->face_id       = NO;   /* unused */
                helpedge->traversed     = NO;
                helpedge->findlabel     = NO;
            }
        }
        end_for_sourcelist(n,e);
    }
    end_for_all_nodes(g,n);
}


/*                                                                            */
/* This function frees the node and edge attributes (node attribute will be   */
/* the pointer to the ordered edge list, edge attributes are NULL).           */
/*                                                                            */

void free_dfs_graph_attributes(Sgraph g)
{
    Snode       n;
    Node_attr   helpnode;
    Sedge       e;
    Edge_state  helpedge;
    Slist       helpembed;
    Node_state  helpstate;
	
    for_all_nodes(g,n)
    {
        helpnode  = attr_data_of_type(n,Node_attr);
        helpembed = helpnode->embed_edges;
        helpstate = helpnode->dfs_state;

        free(helpstate);
        free(helpnode);
        
        set_nodeattrs(n,make_attr(ATTR_DATA,(char *)helpembed));

        for_sourcelist(n,e)
        {
            if(TEST_UNIQUE_EDGE(e))
            {
                helpedge=attr_data_of_type(e,Edge_state);
                free(helpedge);

                set_edgeattrs(e,make_attr(ATTR_DATA,(char *)NULL));
            }
        }
        end_for_sourcelist(n,e);
    }
    end_for_all_nodes(g,n);
}


/*                                                                            */
/* This function is the "main-function" of the dfs-algorithm. Input is the    */
/* start-node and a slist pointer to the list element containing the first    */
/* edge to use.                                                               */
/*                                                                            */

static void dfs_mark(Snode n, Slist firstedgelist)
{
    static int   i = 0;       /* counter for dfs_num */
    Sedge        e;
    Node_state   helpnode;
    Slist        embed_edges,
                 helpembed;

    i++;
       
    helpnode = get_dfs_state(n);
    helpnode->dfs_num = i;
    helpnode->low_pt  = i;

    if(i != 1)
    {
        embed_edges = get_embed_edges(n);
    }
    else
    {
        embed_edges = firstedgelist;
    }

    for_slist(embed_edges,helpembed)
    {
        e = attr_data_of_type(helpembed,Sedge);
	
        if((get_dfs_state(opposit_node(n,e))->dfs_num) == 0)
        {
            mark_tree_edge(e);
            dfs_mark(opposit_node(n,e),(Slist)NULL);
            helpnode->low_pt = min__1(helpnode->low_pt,
                                  get_dfs_state(opposit_node(n,e))->low_pt);
        }
        else
        {
            if(!marked(e) == 1)
            {
                mark_back_edge(e);
                helpnode->low_pt = min__1(helpnode->low_pt,
                                      get_dfs_state(opposit_node(n,e))->dfs_num);
            }
        }
    }
    end_for_slist(embed_edges,helpembed);

    if(firstedgelist != NULL)
    {
        i = 0;
    }
}


/*                                                                            */
/* ************************************************************************** */
/* This function performes two dfs-passes on g. The information of the first  */
/* dfs-pass is used to find the largest face and if there is no bisecting     */
/* edge, to insert a dummy edge.                                              */
/* The second dfs-pass starts with the new found (bisecting) edge and is used */
/* for computing the st-numbering.                                            */
/* The first edge is returned.                                                */
/* ************************************************************************** */
/*                                                                            */

Sedge dfs_main(Sgraph g)
{
    Snode   firstnode;
    Sedge   firstedge;
    Slist   firstedgelist;


    init_nodes_and_edges(g);

    /* First part: used for finding and eventually */
    /*             bisecting the largest face      */

    firstnode = first_node_in_graph(g);
    firstedgelist = get_embed_edges(firstnode);
    dfs_mark(firstnode,firstedgelist);

    firstedge = bisection_of_largest_face(g);           /* in BISECMOD.H */
    firstnode = firstedge->snode;

    firstedgelist = get_firstedgelist(firstnode,firstedge);

    /* Second part: used for getting dfs-information to */
    /*              compute the st-numbering            */                  

    refresh_nodes_and_edges(g);
    dfs_mark(firstnode,firstedgelist);

    return((Sedge)firstedge);
}


/*                                                                            */
/* ************************************************************************** */
/* **                        END OF FILE: DFSMOD.C                         ** */
/* ************************************************************************** */

