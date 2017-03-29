
/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: WOODSMOD.C                                                 ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This module is the essential part of the planar drawing ** */
/* **              algorithm by Donald R. Woods. It computes the           ** */
/* **              coordinates of the nodes and edge-bends and cares for   ** */
/* **              a proper drawing                                        ** */
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
#include "testmod.h"
#include "woodsmod.h"

static int get_st_number(Slist st_num, Snode v);

/*                                                                            */
/* ************************************************************************** */
/* **                      Some general functions                          ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* This function tests if a edge is a dummy edge. It returns 1 if so          */
/*                                                       and 0 if not.        */
/* If a sedge has no graphed_edge counterpart it is a dummy-edge              */
/*                                                                            */

static int is_dummy_edge(Sedge e)
{
    if(graphed_edge(e) == (Graphed_edge)NULL)
    {
        return((int)1);
    }
    else
    {
        return((int)0);
    }
}


/*                                                                            */
/* This function detects whether a edge e is a sideway edge or not            */
/*                                                                            */

static int is_sideway_edge(Sedge e)
{
    Slist *helplist, list;

    helplist = attr_data_of_type(e,Slist *);
    list     = *helplist;

    if(list == empty_slist || (list->pre == list))
    {
        return((int)1);
    }
    else
    {
        return((int)0);
    }
}


/*                                                                            */
/* This function computes the first/last up/down edges                        */
/*                                                                            */

static void get_up_and_down_edges(Snode s, Snode t, Snode n, Slist embed_list, Slist st_num, Slist *first_up, Slist *last_up, Slist *first_down, Slist *last_down)
{
    Slist help_embed_list,
          previous_edge;
    int st_number_of_n,
        st_number_of_opposit_node;
    Sedge edge;

    st_number_of_n = get_st_number(st_num,n);
    edge = attr_data_of_type(embed_list,Sedge);
    st_number_of_opposit_node = get_st_number(st_num,opposit_node(n,edge));

    if(n == s || n == t)
    {
        *first_up   = (Slist)NULL;
        *last_up    = (Slist)NULL;
        *first_down = (Slist)NULL;
        *last_down  = (Slist)NULL;
    }
    else
    {
        if(st_number_of_n > st_number_of_opposit_node)
        {
            for_slist(embed_list,help_embed_list)
            {
                edge = attr_data_of_type(help_embed_list,Sedge);
                st_number_of_opposit_node = get_st_number(st_num,
                                                   opposit_node(n,edge));

                if(st_number_of_n < st_number_of_opposit_node)
                {
                    *first_up  = help_embed_list;
                    *last_down = previous_edge;
                    break;
                }
                previous_edge = help_embed_list;
            }
            end_for_slist(embed_list,help_embed_list);

            for_slist(*first_up,help_embed_list)
            {
                edge = attr_data_of_type(help_embed_list,Sedge);
                st_number_of_opposit_node = get_st_number(st_num,
                                                   opposit_node(n,edge));

                if(st_number_of_n > st_number_of_opposit_node)
                {
                    *first_down = help_embed_list;
                    *last_up    = previous_edge;
                    break;
                }
                previous_edge = help_embed_list;
            }
            end_for_slist(*first_up,help_embed_list);
        }
        else
        {
            for_slist(embed_list,help_embed_list)
            {
                edge = attr_data_of_type(help_embed_list,Sedge);
                st_number_of_opposit_node = get_st_number(st_num,
                                                   opposit_node(n,edge));

                if(st_number_of_n > st_number_of_opposit_node)
                {
                    *first_down = help_embed_list;
                    *last_up    = previous_edge;
                    break;
                }
                previous_edge = help_embed_list;
            }
            end_for_slist(embed_list,help_embed_list);

            for_slist(*first_down,help_embed_list)
            {
                edge = attr_data_of_type(help_embed_list,Sedge);
                st_number_of_opposit_node = get_st_number(st_num,
                                                   opposit_node(n,edge));

                if(st_number_of_n < st_number_of_opposit_node)
                {
                    *first_up  = help_embed_list;
                    *last_down = previous_edge;
                    break;
                }
                previous_edge = help_embed_list;
            }
            end_for_slist(*first_up,help_embed_list);
        }
    }
}


/*                                                                            */
/* This function initializes the node attributes:                             */
/* 1. The ordered edgelist is connected to embed_list                         */
/* 2. first_up_edge,last_up_edge,first_down_edge,last_down_edge are           */
/*    computed                                                                */
/*                                                                            */

static void init_node_attributes(Sgraph g, Slist st_num, Snode s, Snode t)
{
    Snode n;
    Slist help_embed_list,
          first_up,last_up,
          first_down,last_down;
    Woods_node_attr node_attribute;

    for_all_nodes(g,n)
    {
        help_embed_list = attr_data_of_type(n,Slist);
        node_attribute = (Woods_node_attr)malloc(sizeof(struct woods_node_attr));
        node_attribute->embed_list = help_embed_list;

        get_up_and_down_edges(s,t,n,help_embed_list,st_num,&first_up,&last_up,&first_down,&last_down); 
        node_attribute->first_up_edge   = first_up;
        node_attribute->last_up_edge    = last_up;
        node_attribute->first_down_edge = first_down;
        node_attribute->last_down_edge  = last_down;   

        node_attribute->left_sideway_edge  = (Sedge)NULL;
        node_attribute->right_sideway_edge = (Sedge)NULL;

        set_nodeattrs(n,make_attr(ATTR_DATA,(char *)node_attribute));
    }
    end_for_all_nodes(g,n);
}


/*                                                                            */
/* This function returns the node, that has the st-number 1                   */
/*                                                                            */

static Snode get_first_node(Slist st_num)
{
    Slist   helplist;
    St_attr helpst;

    for_slist(st_num,helplist)
    {
        helpst = attr_data_of_type(helplist,St_attr);
        if(helpst->st_nr == 1)
        {
            return((Snode)helpst->node);
        }
    }
    end_for_slist(st_num,helplist);
    return NULL;
}


/*                                                                            */
/* This function returns the node with the highest st-number                  */
/*                                                                            */

static Snode get_last_snode(Slist st_num)
{
    St_attr helpst;

    st_num = st_num->suc;
    helpst = attr_data_of_type(st_num,St_attr);
    
    return((Snode)helpst->node);
}


/*                                                                            */
/* This function returns the coresponding edge to the given co_firstedge      */
/* It's the manifestation of the edge in the ordered edge list of the other   */
/* node (15870 is just a number to have a unique attribute to find the edge)  */
/*                                                                            */

static Sedge get_firstedge(Snode s, Sedge co_firstedge)
{
    Slist embed_list,
          l;
    Sedge edge,
          firstedge;
    int   flag;

    set_edgeattrs(co_firstedge,make_attr(ATTR_FLAGS,15870));

    embed_list=GET_EMBED_LIST(s);

    for_slist(embed_list,l)           /* running through the embed_list and   */
    {                                 /* looking for the firstedge            */
        edge = attr_data_of_type(l,Sedge);
        flag = attr_flags(edge);

        if(flag == 15870)
        {
            firstedge = edge;         /* remember the Slist-element that      */
            break;                    /* contains the firstedge               */
        }
    }
    end_for_slist(embed_list,l);

    set_edgeattrs(co_firstedge,make_attr(ATTR_DATA,NULL));

    return((Sedge)firstedge);         /* returning the embed_list-element     */
                                      /* containing the firstedge             */
}


/*                                                                            */
/* This function finds the corresponding edge in the edgelist of the          */
/* opposit node (only necessary in undirected graphs)                         */
/*                                                                            */

static Sedge get_co_edge(Snode n, Sedge edge)
{
    Slist embed_list,
          l;
    Sedge co_edge,
          e;
    int flag;

    set_edgeattrs(edge,make_attr(ATTR_FLAGS,15870));
    
    embed_list = GET_EMBED_LIST(opposit_node(n,edge));

    for_slist(embed_list,l)
    {
        e=attr_data_of_type(l,Sedge);
        flag=attr_flags(e);

        if(flag == 15870)
        {
            co_edge = e;
            break;
        }
    }
    end_for_slist(embed_list,l);

    set_edgeattrs(edge,make_attr(ATTR_DATA,(char *)NULL));

    return((Sedge)co_edge);
}


/*                                                                            */
/* Tests if e1 and e2 are two manifestations of the same edge                 */
/*                                                                            */

static int test_equal_edges(Snode v, Sedge e1, Sedge e2)
{
    return((int)(e2 == get_co_edge(v,e1)));
    
}


/*                                                                            */
/* Nearly the same as the previous function. Input are the pointers to the    */
/* list elements that contain the edges to be compared                        */
/*                                                                            */

static int equal_edges(Snode v, Slist edge_list_1, Snode w, Slist edge_list_2)
{
    Sedge e1,e2;

#if FALSE
    /* Changed MH 6/6/94 */
    e1=attr_data_of_type(edge_list_1,Sedge);
    e2=attr_data_of_type(edge_list_2,Sedge);

    if(v->graph->directed)
    {
        return(e1==e2);
    }
    else
    {
        return(test_equal_edges(v,e1,e2));
    }
#endif

    if (edge_list_1 == NULL) {
      return (edge_list_1 == edge_list_2);
    } else if (edge_list_2 == NULL) {
      return (edge_list_1 == edge_list_2);
    }

    e1=attr_data_of_type(edge_list_1,Sedge);
    e2=attr_data_of_type(edge_list_2,Sedge);

    if(v->graph->directed)
    {
        return(e1==e2);
    }
    else
    {
        return(test_equal_edges(v,e1,e2));
    }
}


/*                                                                            */
/* Nearly the same function as get_co_edge()                                  */
/* Difference: The pointer to the list element that contains the coresponding */
/*             edge is returned                                               */
/*                                                                            */

static Slist get_co_edge_list(Snode opnode, Sedge edge)
{
    Sedge e;
    Slist embed_list,
          l,
          co_edge_list;
    int   flag;

    set_edgeattrs(edge,make_attr(ATTR_FLAGS,15870));
    embed_list=GET_EMBED_LIST(opnode);

    for_slist(embed_list,l)
    {
        e=attr_data_of_type(l,Sedge);
        flag=attr_flags(e);
        if(flag == 15870)
        {
            co_edge_list = l;
            break;
        }
    }
    end_for_slist(embed_list,l);
    set_edgeattrs(edge,make_attr(ATTR_DATA,(char *)NULL));

    return((Slist)co_edge_list);
} 


/*                                                                            */
/* This function returns the ordinate of a given vertex, if already           */
/* computed and -1 else.                                                       */
/*                                                                            */

static int level(Slist ordinate_list, Snode v)
{
    Slist       helplist;
    Ord_attr    helpord;

    for_slist(ordinate_list,helplist)
    {
        helpord = attr_data_of_type(helplist,Ord_attr);
        if(helpord->node == v)
        {
            return((int)helpord->level);
        }
    }
    end_for_slist(ordinate_list,helplist);

    return((int)-1);
}


/*                                                                            */
/* This function initializes the edge-attributes of the graph g, that will    */
/* be Slists which contain the coordinates of the edge-bends                  */
/*                                                                            */

static void init_bends(Sgraph g)
{
    Snode   n;
    Sedge   e;
    Slist   *helpedge;

    for_all_nodes(g,n)
    {
        for_sourcelist(n,e)
        {
            if(TEST_UNIQUE_EDGE(e))
            {
                if(!(is_dummy_edge(e)))
                {
                    helpedge = (Slist *)malloc(sizeof(char *)); 
                    *helpedge = empty_slist;
                    set_edgeattrs(e,make_attr(ATTR_DATA,(char *)helpedge));
                }
            }
        }
        end_for_sourcelist(n,e);
    }
    end_for_all_nodes(g,n);
}


/*                                                                            */
/* This function sets the x and y coordinate of a given node                  */
/*                                                                            */

static void set_node_coord(Snode n, int x, int y)
{
    n->x = x;
    n->y = y;

    node_set(graphed_node(n),NODE_POSITION,x,y,0);
}


/*                                                                            */
/* This function returns the upnode of the next edge in the main_list         */
/*                                                                            */

static Snode next_upnode(Slist l)
{
    Slist       next_list;
    Snode       upnode;
    Main_attr   helpmain;

    next_list = l->suc;
    helpmain = attr_data_of_type(next_list,Main_attr);
    upnode = helpmain->upnode;

    return(upnode);
}


/*                                                                            */
/* This function adds the coordinates (abszissa,level) of a new bend to the   */
/* attribute-list of the edge e                                               */
/*                                                                            */

static void add_new_bend_to_edge(Sedge e, int x, int y)
{
    Slist       helplist;
    Slist       *helpedge;
    Bend_attr   helpbend;

    if(!(is_dummy_edge(e)))
    {
        helpedge = attr_data_of_type(e,Slist *);
        helplist = *helpedge;

        helpbend = (Bend_attr)malloc(sizeof(struct bend_attr));
        helpbend->x = x;
        helpbend->y = y;

        if(helplist == empty_slist)
        {
            helplist = add_to_slist(helplist,make_attr(ATTR_DATA,
                                                       (char *)helpbend));
            (*helpedge) = helplist;
        }
        else
        {
            add_to_slist(helplist,make_attr(ATTR_DATA,(char *)helpbend));
        }
    }
}


/*                                                                            */
/* This functions frees the ordinate-list                                     */
/*                                                                            */

void free_ordinate_list(Slist ordinate_list)
{
    Slist l;

    for_slist(ordinate_list,l)
    {
        free(attr_data_of_type(l,Ord_attr));
    }
    end_for_slist(ordinate_list,l);
    free_slist(ordinate_list);
}


/*                                                                            */
/* This function frees the node attributes (plus the edge list created by     */
/* embed) and the edge attributes (the coordinates of the edge-bends)         */
/*                                                                            */

void free_woods_graph_attributes(Sgraph g)
{
    Snode   n;
    Sedge   e;
    Slist   *helpedge,
            helplist,
            l;
    Woods_node_attr  helpnode;

	for_all_nodes(g,n)
	{
		helpnode=attr_data_of_type(n,Woods_node_attr);
		free_slist(helpnode->embed_list);
        free(helpnode);

        set_nodeattrs(n,make_attr(ATTR_DATA,(char *)NULL));

        for_sourcelist(n,e)
		{
			if(TEST_UNIQUE_EDGE(e))
			{
                if(!(is_dummy_edge(e)))
                {
                    helpedge=attr_data_of_type(e,Slist *);
				    helplist=*helpedge;
				    for_slist(helplist,l)
				    {
                        free(attr_data_of_type(l,Bend_attr));
				    }
				    end_for_slist(helplist,l);

                    free_slist(helplist);
				    free(helpedge);

                    set_edgeattrs(e,make_attr(ATTR_DATA,(char *)NULL));
                }
			}
		}
		end_for_sourcelist(n,e);
	}
	end_for_all_nodes(g,n);
}	


/*                                                                            */
/* ************************************************************************** */
/* **                   End of the general functions                       ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* ************************************************************************** */
/* **                  Functions used by get_ordinates()                   ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* This function initializes the list of nodes that contains the nodes that   */
/* do not yet have a ordinate and returns a pointer to the list               */
/*                                                                            */

static Slist init_node_list(Sgraph g, Snode s)
{
    Snode   v;
    Slist   nodelist = empty_slist;

    for_all_nodes(g,v)
    {
        if(v != s)
        {
            nodelist = add_to_slist(nodelist,make_attr(ATTR_DATA,(char *)v));
            nodelist = nodelist->pre;
        }
    }
    end_for_all_nodes(g,v);

    return((Slist)nodelist);
}


/*                                                                            */
/* This function initializes a Slist thet contains the nodes and              */
/* their respective ordinate                                                  */
/*                                                                            */

static Slist init_ordinate_list(Snode s)
{
    Slist       ordinate_list = empty_slist;
    Ord_attr    helpord;

    helpord = (Ord_attr)malloc(sizeof(struct ord_attr));
    helpord->node  = s;
    helpord->level = 0;
    ordinate_list = add_to_slist(ordinate_list,make_attr(ATTR_DATA,
                                                         (char *)helpord));
    ordinate_list = ordinate_list->pre;

    return((Slist)ordinate_list);
}


/*                                                                            */
/* This function returns the next node in the node_list. No                   */
/* subtract_from_slist() is performed                                         */
/*                                                                            */

static Snode next_node(Slist node_list)
{
    Snode   helpnode;

    if(node_list != empty_slist)
    {
        helpnode = attr_data_of_type(node_list,Snode);
        return((Snode)helpnode);
    }
    return NULL;
}


/*                                                                            */
/* This function returns the st-number of a given node                        */
/*                                                                            */

static int get_st_number(Slist st_num, Snode v)
{
    Slist   helplist;
    St_attr helpst;
    int     st;

    for_slist(st_num,helplist)
    {
        helpst = attr_data_of_type(helplist,St_attr);
        if(helpst->node == v)
        {
            st=helpst->st_nr;
            return(st);
        }
    }
    end_for_slist(st_num,helplist);
    return 0;
}


/*                                                                            */
/* This function add's another node with it's ordinate to the ordinate_list   */
/* and returns the pointer to it                                              */
/*                                                                            */

static Slist push_ordinate(Slist ordinate_list, Snode v, int maxlevel)
{
    Ord_attr    helpord;

    helpord = (Ord_attr)malloc(sizeof(struct ord_attr));

    helpord->node  = v;
    helpord->level = maxlevel;

    ordinate_list = add_to_slist(ordinate_list,make_attr(ATTR_DATA,
                                                         (char *)helpord));
    return((Slist)ordinate_list);
}


/*                                                                            */
/* This function removes immediately the given node from the node_list (when  */
/* used, the ordinate is already computed)                                    */
/*                                                                            */

static Slist sremove(Slist node_list, Snode v)
{
    Slist   dummy = node_list;

    if(node_list != empty_slist)
    {
        if(node_list != node_list->suc)
        {
            node_list = node_list->suc;
            subtract_immediately_from_slist(node_list,dummy);
        }
        else
        {
            node_list = empty_slist;
            free_slist(dummy);
        }
        
        return((Slist)node_list);
    }
    else
    {
        return((Slist)NULL);
    }
}


/*                                                                            */
/* This function returns the pointer to the list element, that contains the   */
/* co_edge in the embed_list of the opnode                                    */
/*                                                                            */
/*
static Slist get_co_list(Sedge e, Snode opnode)
{
    Slist embed_list,
          l,
          list_element = (Slist)NULL;
    Sedge edge;
    int   flag;

    set_edgeattrs(e,make_attr(ATTR_FLAGS,15870));
    embed_list = GET_EMBED_LIST(opnode);
    for_slist(embed_list,l)
    {
        edge = attr_data_of_type(l,Sedge);
        flag = attr_flags(edge);
        if(flag == 15870)
        {
            list_element = l;
            break;
        }
    }
    end_for_slist(embed_list,l);

    set_edgeattrs(e,make_attr(ATTR_DATA,(char *)NULL));
    return((Slist)list_element);
}
*/

/*                                                                            */
/* This function tests if a dummy edge needs to be inserted. If so, the dummy */
/* edge is inserted. It leads from a node which has a sideway edge to a node  */
/* that is below this node                                                    */
/*                                                                            */

static void test_necessary_dummy_edge(Snode up_node, Snode down_node, Slist up_down_edge_list, Sedge down_up_edge, int direction)
{
    Slist embed_list,
          l,
          down_up_edge_list,
          opnode_list;
    Sedge edge,
          new_edge;    
    Snode opnode;

    embed_list=GET_EMBED_LIST(down_node);
    for_slist(embed_list,l)
    {
        if(attr_data_of_type(l,Sedge) == down_up_edge)
        {
            down_up_edge_list = l;
            break;
        }
    }
    end_for_slist(embed_list,l);

    if(direction == 0)
    {
        down_up_edge_list = down_up_edge_list->pre;
        edge = attr_data_of_type(down_up_edge_list,Sedge);
        opnode = opposit_node(down_node,edge);
        opnode_list = get_co_edge_list(opnode,edge);
        opnode_list = opnode_list->pre;

        edge=attr_data_of_type(opnode_list,Sedge);
        if(opposit_node(opnode,edge) != up_node)
        {
            new_edge = make_edge(opnode,up_node,make_attr(ATTR_DATA,
                                                           (char *)NULL));
            opnode_list = opnode_list->suc;
            add_immediately_to_slist(opnode_list,make_attr(ATTR_DATA,
                                                           (char *)new_edge));
            up_down_edge_list = up_down_edge_list->suc;
            add_immediately_to_slist(up_down_edge_list,make_attr(ATTR_DATA,
                                                           (char *)new_edge));
        }
    }
    else
    {
        down_up_edge_list = down_up_edge_list->suc;
        edge   = attr_data_of_type(down_up_edge_list,Sedge);
        opnode = opposit_node(down_node,edge);
        opnode_list = get_co_edge_list(opnode,edge);
        opnode_list = opnode_list->suc;

        edge=attr_data_of_type(opnode_list,Sedge);
        if(opposit_node(opnode,edge) != up_node)
        {
            new_edge = make_edge(opnode,up_node,make_attr(ATTR_DATA,
                                                          (char *)NULL));
            add_immediately_to_slist(opnode_list,make_attr(ATTR_DATA,
                                                          (char *)new_edge));
            add_immediately_to_slist(up_down_edge_list,make_attr(ATTR_DATA,
                                                          (char *)new_edge));
        }
    }
}


/*                                                                            */
/* ************************************************************************** */
/* This function is the "main-function" for computing the ordinates of the    */
/* nodes. Horizontal edges are permitted. It returns a Slist, that contains   */
/* the nodes and their respective ordinate (y-coordinate)                     */
/* ************************************************************************** */
/*                                                                            */

static Slist get_ordinates(Sgraph g, Snode s, Snode t, Slist st_num)
{
    Snode   v,
            opnode,
            highest_lower_node;
    Sedge   e,
            next_down_edge,
            first_edge_to_check,
            second_edge_to_check,
            down_up_edge;
    Slist   node_list      = empty_slist,
            ordinate_list  = empty_slist,
            embed_list,l,
            highest_lower_node_list,
            down_list;
 
    int     badnode   = 0,
            bad       = 0,
            maxlevel  = 0,
            nodelevel = 0,
            st_number_of_v,
            st_number_of_opnode,
            st_number_of_highest_lower_node,
            second_sideway_edge = 0;

    Woods_node_attr attr_of_v,
                    attr_of_highest_lower_node,
                    attr_of_opnode;
 
    node_list = init_node_list(g,s);
    ordinate_list = init_ordinate_list(s);

    while(node_list != empty_slist)
    {
        v = next_node(node_list);
        st_number_of_v = get_st_number(st_num,v);
        embed_list = GET_EMBED_LIST(v);

        for_slist(embed_list,l)
        {
            e=attr_data_of_type(l,Sedge);
            opnode = opposit_node(v,e);
            st_number_of_opnode = get_st_number(st_num,opnode);

            if(st_number_of_opnode < st_number_of_v)
            /* opposit node lies below v */
            {
                if((nodelevel = level(ordinate_list,opnode)) != -1)
                /* opposit node is already set (has ordinate) */
                {
                    if(maxlevel < nodelevel)
                    /* not yet reached the top of the graph   */
                    /* the node v can be placed at this level */
                    {
                        maxlevel                = nodelevel;
                        highest_lower_node      = opnode;
                        highest_lower_node_list = l;
                        first_edge_to_check     = e;
                        st_number_of_highest_lower_node = st_number_of_opnode;
                    }
                }
                else
                /* v cnnot be placed at this level */
                {
                    badnode = 1;
                    break;
                }
            }
        }
        end_for_slist(embed_list,l);

        if(badnode != 1)
        /* all adjacent nodes with lower st-number are already set */
        {
            if(v == t)      /* the top of the graph is reached   */
                            /* t is placed one stage higher than */
                            /* its adjacent nodes                */
            {
                ordinate_list = push_ordinate(ordinate_list,v,maxlevel+1);
                node_list = sremove(node_list,v);
            }
            else
            {
                if(maxlevel == 0)    /* the node is to be placed on the first */
                                     /* stage because it's only adjacent to   */
                                     /* the first node s (placed on stage 0)  */
                                     /* and no horizontal edges on stage 0    */
                                     /* are permittet (definition)            */
                {
                    ordinate_list = push_ordinate(ordinate_list,v,maxlevel+1);
                    node_list = sremove(node_list,v);
                }
                else     /* the node is to be placed on any stage higher than */
                         /* 1 and so it must be checked if any horizontal     */
                         /* edges are possible                                */
                {
                    attr_of_v = attr_data_of_type(v,Woods_node_attr);
                    attr_of_highest_lower_node = attr_data_of_type(highest_lower_node,Woods_node_attr);
                    
                    if(((attr_of_highest_lower_node->first_up_edge) != (Slist)NULL) &&
                       ((attr_of_v->first_down_edge) != (Slist)NULL) && 
                        (equal_edges(v,attr_of_v->first_down_edge,
                                     highest_lower_node,attr_of_highest_lower_node->first_up_edge)))
                    /* 1. Condition: first down edge == first up edge  */
                    /*               (NULL means that there is already */
                    /*               a horizontal edge on the right    */
                    /*               side of v                         */
                    {
                        down_list = highest_lower_node_list->suc;
                        next_down_edge = attr_data_of_type(down_list,Sedge);
                        opnode = opposit_node(v,next_down_edge);
                        while(st_number_of_v > get_st_number(st_num,opnode))
                        {
                            if(level(ordinate_list,opnode) == maxlevel)
                            /* for all adjacent nodes on the level of the */
                            /* highest lower node                         */
                            {
                                attr_of_opnode = attr_data_of_type(opnode,Woods_node_attr);
                                if(!equal_edges(opnode,attr_of_opnode->last_up_edge,
                                               v,attr_of_v->last_down_edge))
                                /* 2. Condition: opnode violates the first  */
                                /*               condition                  */
                                {
                                    bad = 1;
                                    break;
                                }
                                else
                                {
                                    /* a second sideway edge on the other */
                                    /* side of v is necessary             */
                                    second_sideway_edge = 1;
                                    second_edge_to_check = next_down_edge;
                                    break;
                                }
                            }
                            down_list = down_list->suc;
                            next_down_edge = attr_data_of_type(down_list,Sedge);
                            opnode = opposit_node(v,next_down_edge);
                        }
                                 
                        if(bad != 1)
                        /* ok ! v can be placed on the level of the highest */
                        /* lower node of v                                  */
                        {
                            attr_of_v->first_down_edge = (Slist)NULL;
                            attr_of_v->last_up_edge    = (Slist)NULL;
                            attr_of_highest_lower_node->first_up_edge  = (Slist)NULL;
                            attr_of_highest_lower_node->last_down_edge = (Slist)NULL;
                            attr_of_v->right_sideway_edge                 = first_edge_to_check;
                            attr_of_highest_lower_node->left_sideway_edge = down_up_edge 
                                                                          = get_co_edge(v,first_edge_to_check);
                        
                            test_necessary_dummy_edge(v,highest_lower_node,
                                                      highest_lower_node_list,down_up_edge,0);
                        
                            if(second_sideway_edge == 1)
                            /* handle the second sideway edge if necessary */
                            {
                                attr_of_v->last_down_edge = (Slist)NULL;
                                attr_of_v->first_up_edge  = (Slist)NULL;
                                attr_of_opnode->last_up_edge    = (Slist)NULL;
                                attr_of_opnode->first_down_edge = (Slist)NULL;
                                attr_of_v->left_sideway_edge       = second_edge_to_check;
                                attr_of_opnode->right_sideway_edge = get_co_edge(v,second_edge_to_check);
                            }
                            ordinate_list = push_ordinate(ordinate_list,v,maxlevel);
                            node_list = sremove(node_list,v);
                                
                        }
                        else
                        /* v cannot be placed on the level of the highest */
                        /* lower node. It's placed on stage higher        */
                        {
                            ordinate_list = push_ordinate(ordinate_list,v,maxlevel+1);
                            node_list = sremove(node_list,v);
                        }
                    }   
                    else
                    /* The same as above but last down edge == last up edge */
                    /* is checked (1. Condition)                            */
                    {
                        if(((attr_of_highest_lower_node->last_up_edge) != (Slist)NULL) &&
                           ((attr_of_v->last_down_edge               ) != (Slist)NULL) &&
                            (equal_edges(v,attr_of_v->last_down_edge,
                                         highest_lower_node,attr_of_highest_lower_node->last_up_edge)))
                        {
                            down_list = highest_lower_node_list->pre;
                            next_down_edge = attr_data_of_type(down_list,Sedge);
                            opnode = opposit_node(v,next_down_edge);
                            while(st_number_of_v > get_st_number(st_num,opnode))
                            {
                                if(level(ordinate_list,opnode) == maxlevel)
                                /* for all adjacent nodes on the level of the */
                                /* highest lower node                         */
                                {
                                    attr_of_opnode = attr_data_of_type(opnode,Woods_node_attr);
                                    if(!equal_edges(opnode,attr_of_opnode->first_up_edge,
                                                   v,attr_of_v->first_down_edge))
                                    /* 2. Condition: opnode violates the first  */
                                    /*               condition                  */
                                    {
                                        bad = 1;
                                        break;
                                    }
                                    else
                                    {
                                        second_sideway_edge = 1;
                                        second_edge_to_check = next_down_edge;
                                        break;
                                    }
                                } 
                                down_list = down_list->pre;
                                next_down_edge = attr_data_of_type(down_list,Sedge);
                                opnode = opposit_node(v,next_down_edge);
                            }
                        
                            if(bad != 1)
                            /* ok ! v can be placed on the level of the */
                            /* highest lower node of v                  */
                            {
                                attr_of_v->last_down_edge = (Slist)NULL;
                                attr_of_v->first_up_edge  = (Slist)NULL;
                                attr_of_highest_lower_node->last_up_edge    = (Slist)NULL;
                                attr_of_highest_lower_node->first_down_edge = (Slist)NULL;
                                attr_of_v->left_sideway_edge                   = first_edge_to_check;
                                attr_of_highest_lower_node->right_sideway_edge = down_up_edge
                                                                             = get_co_edge(v,first_edge_to_check);
                            
                                test_necessary_dummy_edge(v,highest_lower_node,highest_lower_node_list,down_up_edge,1);
                            
                                if(second_sideway_edge == 1)
                                /* handle a second sideway edge if necessary */
                                {
                                    attr_of_v->first_down_edge = (Slist)NULL;
                                    attr_of_v->last_up_edge    = (Slist)NULL;
                                    attr_of_opnode->first_up_edge  = (Slist)NULL;
                                    attr_of_opnode->last_down_edge = (Slist)NULL;
                                    attr_of_v->right_sideway_edge     = second_edge_to_check;
                                    attr_of_opnode->left_sideway_edge = get_co_edge(v,second_edge_to_check);
                                }
                                ordinate_list = push_ordinate(ordinate_list,v,maxlevel);
                                node_list = sremove(node_list,v);
                            
                            }
                            else
                            /* v must be placed one stage higher as the */
                            /* highest lower node                       */
                            {
                                ordinate_list = push_ordinate(ordinate_list,v,maxlevel+1);
                                node_list = sremove(node_list,v);
                            }
                        }
                        else
                        /* the first condition is violated and the node v   */
                        /* must be placed one level higher than the highest */
                        /* lower node                                       */
                        {
                            ordinate_list = push_ordinate(ordinate_list,v,maxlevel+1);
                            node_list = sremove(node_list,v);
                        }
                    }   
                }
            }    
        }
        else
        /* v cannot yet be placed */
        {
            node_list = node_list->suc;
            badnode = 0;
        }
        second_sideway_edge = 0;
        bad = 0;
        maxlevel = 0;
    }
    return((Slist)ordinate_list);
}


/*                                                                            */
/* ************************************************************************** */
/* **          End of the functions for computing the ordinates            ** */
/* ************************************************************************** */
/*                                                                            */


/*                                                                            */
/* ************************************************************************** */
/* **     Functions for initializing the main working data structure       ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* This function returns the start of the main_list. This is the pointer      */
/* to the (s,t)-edge (first edge at the far left).                            */
/*                                                                            */

static Slist start_of_main_list(Snode s, Sedge firstedge)
{
    Slist   dummy_list,           /* will be the pointer to the Slist-element */
                                  /* that contains the (s,t)-edge             */
            embed_list,           /* Slist, that contains the edges around s  */
            l;                    /* used for running through the embed_list  */
    Sedge   helpedge;             /* the edge contained by the current        */
                                  /* embed_list-element                       */

    embed_list=GET_EMBED_LIST(s);

    for_slist(embed_list,l)           /* running through the embed_list and   */
    {                                 /* looking for the firstedge            */
        helpedge=attr_data_of_type(l,Sedge);
        if(helpedge == firstedge)
        {
            dummy_list=l;             /* remember the Slist-element that      */
            break;                    /* contains the firstedge               */
        }
    }
    end_for_slist(embed_list,l);

    return(dummy_list);               /* returning the embed_list-element     */
                                      /* containing the firstedge             */
}


/*                                                                            */
/* This function returns the node on the other side of n. In this context     */
/* the upnode of e is returned.                                               */
/*                                                                            */

static Snode get_upnode(Snode n, Sedge e)
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
/* This function returns the initialized main_list                            */
/*                                                                            */

static Slist init_main_list(Snode s, Sedge firstedge, Slist ordinate_list)
{
    Slist   main_list  = empty_slist,     /* points to the first element      */
            dummy_list = empty_slist,     /* points to the last element of    */
                                          /* main-list                        */
            embed_list = empty_slist,     /* ordered list created by embed    */
            l          = empty_slist;     /* used for going through the list  */
    Sedge   helpedge;                     /* one edge of the embed_list       */
    Snode	upnode;
    Main_attr   helplist;                 /* one structure of the main_list   */

    embed_list = start_of_main_list(s,firstedge);     /* find the (s,t)-edge  */

    init_bends(s->graph);

    for_slist(embed_list,l)           /* put all the edges of s to main_list  */
    {
        helpedge=attr_data_of_type(l,Sedge);
        helplist=(Main_attr)malloc(sizeof(struct main_attr));
        upnode=get_upnode(s,helpedge);

        helplist->upnode=upnode;
        helplist->ordinate=level(ordinate_list,upnode);
        helplist->downnode=s;
        helplist->edge=helpedge;

        dummy_list=add_to_slist(dummy_list,
                                make_attr(ATTR_DATA,(char *)helplist));

        if(dummy_list == dummy_list->pre)     /* needed to remember the first */
        {                                     /* element of the main_list     */
            main_list=dummy_list;
        }
        add_new_bend_to_edge(helpedge,s->x,s->y);
    }
    end_for_slist(embed_list,l);

    return(main_list);
}


/*                                                                            */
/* ************************************************************************** */
/* **               End of the functions for init_main_list                ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* ************************************************************************** */
/* **                    Functions for the first pass                      ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* This function performs the first pass to compute the number of abszissa    */
/* points needed for this level                                               */
/*                                                                            */

static int first_pass(Slist main_list, int level)
{
    Slist       l;
    Snode       upnode,
                next_node;
    int         i = 0;
    Main_attr   helpmain;

    for_slist(main_list,l)
    {
        helpmain=attr_data_of_type(l,Main_attr);
        upnode=helpmain->upnode;
        if((helpmain->ordinate) != level)   /* the upnode of the edge is */
                                            /* located higher than that  */
                                            /* level                     */
        {
            if(!(is_dummy_edge(helpmain->edge)))
            /* if it is not a dummy edge one lattice point */
            /* is to be reserved                           */
            {
                i++;
            }
        }
        else
        {
            if((next_node=next_upnode(l)) != upnode)
            /* the next edge leads to a different node, so */
            /* a lattice point is to be reserved           */
            {
                i++;
            }
            else
            {
                /* two edges to the same node, abszissa-point is */
                /* counted the next time                         */
            }
        }
    }
    end_for_slist(main_list,l);

    return(i);
}

/*                                                                            */
/* ************************************************************************** */
/* **               End of the functions for the first pass                ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* ************************************************************************** */
/* **                The functions for the second pass                     ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* This function deletes (and frees) a given element l from the main_list     */
/* and returns a pointer to the next element in the list                      */
/*                                                                            */

static Slist remove_from_main_list(Slist main_list, Slist l)
{
    Slist   dummy = l;

    if(l != empty_slist)
    {
        if(l != l->suc)
        {
            l = l->suc;
        }
        else
        {
            l = empty_slist;
        }

        free(attr_data_of_type(dummy,Main_attr));
        subtract_immediately_from_slist(main_list,dummy);

        return((Slist)l);
    }
    else
    {
        return((Slist)NULL);
    }
}


/*                                                                            */
/* This function adds new elements to the main_list. That are the edges       */
/* heading upwards (to nodes with higher st-number) from the given node n     */
/*                                                                            */

static void add_edges_to_main_list(Snode n, Slist l, Slist st_num, Slist ordinate_list)
{
    Sedge       e;
    Snode       upnode;
    int         st_of_n,
                level_of_n;
    Main_attr   helpmain;
    Slist       first_embed_list,
                embed_list,
                help_embed_list;
    static int  found_down_edge = 0;

    st_of_n = get_st_number(st_num,n);
    
    first_embed_list = GET_EMBED_LIST(n);
    embed_list = first_embed_list;

    for_slist(first_embed_list,help_embed_list)
    /* searching the first edge that is leading upwards */
    {
        e = attr_data_of_type(help_embed_list,Sedge);
        if((st_of_n > get_st_number(st_num,opposit_node(n,e))) && 
           (found_down_edge == 0))
        { 
            found_down_edge = 1;
        }
        else
        {
            if((st_of_n > get_st_number(st_num,opposit_node(n,e))) && 
               (found_down_edge == 1))
            { 
                found_down_edge = 1;
            }
            else
            {
                if((st_of_n < get_st_number(st_num,opposit_node(n,e))) && 
                   (found_down_edge == 0))
                { 
                    found_down_edge = 0;
                }
                else
                {
                    if((st_of_n < get_st_number(st_num,opposit_node(n,e))) && 
                       (found_down_edge == 1))
                    { 
                        found_down_edge = 0;
                        embed_list = help_embed_list;
                        break;
                    }
                }
            }
        }
    }
    end_for_slist(first_embed_list,help_embed_list);
    found_down_edge = 0;    

    for_slist(embed_list,help_embed_list)
    {
        
        e = attr_data_of_type(help_embed_list,Sedge);
        upnode = get_upnode(n,e);
        
        if(level(ordinate_list,upnode) > level_of_n)
        /* only really upward leading edges are inserted */
        /* (no sideway edges)                            */
        {
            helpmain = (Main_attr)malloc(sizeof(struct main_attr));

            helpmain->upnode   = upnode;
            helpmain->ordinate = level(ordinate_list,upnode);
            helpmain->downnode = n;
            helpmain->edge     = e;

            l = add_to_slist(l,make_attr(ATTR_DATA,(char *)helpmain));
            add_new_bend_to_edge(e,n->x,n->y);
        }
    }
    end_for_slist(embed_list,help_embed_list);
}


/*                                                                            */
/* This function performs the second pass of the woods algorithm. The         */
/* relative coordinates of the nodes and the edge-bends are computed          */
/* The (perhaps) new beginning of the main_list is returned                   */
/*                                                                            */

static Slist second_pass(Slist main_list, Slist st_num, Slist ordinate_list, int start_abszissa, int level, int maxordinate)
{
    Slist       l;
    int         ordinate_of_upnode;
    Main_attr   helpmain;

    l = main_list;

    do
    {
        do
        {
            helpmain = attr_data_of_type(l,Main_attr);
            ordinate_of_upnode = helpmain->ordinate;
  
            add_new_bend_to_edge(helpmain->edge,start_abszissa,level);

            if(ordinate_of_upnode < level)
            /* only possible with a sideway edge */
            {
                if(l == main_list)
                {
                    l = remove_from_main_list(main_list,l);
                    main_list = l;
                }
                else
                {
                    l = remove_from_main_list(main_list,l);
                }
            }
            else
            {
                if(ordinate_of_upnode > level)
                /* edge bend */
                {
                    if(!(is_dummy_edge(helpmain->edge)))
                    /* only set edge bend if it is not a dummy edge */
                    {
                        start_abszissa++;
                        l = l->suc;
                    }
                    else
                    {
                        l = l->suc;
                    }
                }
                else
                {
                    if(ordinate_of_upnode == level)
                    /* set node */
                    {
                        if(level == maxordinate)
                        /* only t must be set */
                        {
                            set_node_coord(helpmain->upnode,start_abszissa,level);
                            if(l == main_list)
                            {
                                l = remove_from_main_list(main_list,l);
                                main_list = l;
                            }
                            else
                            {
                                l = remove_from_main_list(main_list,l);
                            }
                        }
                        else
                        /* node is different from t */
                        {
                            if(next_upnode(l) == helpmain->upnode)
                            /* the next edge leads to the same node */
                            /* node is set the next time            */
                            {
                                if(l == main_list)
                                {
                                    l = remove_from_main_list(main_list,l);
                                    main_list = l;
                                }
                                else
                                {
                                    l = remove_from_main_list(main_list,l);
                                }
                            }
                            else
                            /* the node is to be set */
                            {
                                set_node_coord(helpmain->upnode,start_abszissa,level);
                                add_edges_to_main_list(helpmain->upnode,l,
                                                       st_num,ordinate_list);
                                start_abszissa++;
                                if(l == main_list)
                                {
                                    l = remove_from_main_list(main_list,l);
                                    main_list = l;
                                }
                                else
                                {
                                    l = remove_from_main_list(main_list,l);
                                }
                            }
                        }
                    }
                }
            }
        }
        while(l != main_list);
    }
    while((l != empty_slist) && (level == maxordinate));

    return(main_list);
}

/*                                                                            */
/* ************************************************************************** */
/* **            End of the functions for the second pass                  ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* ************************************************************************** */
/* **         Functions for drawing the graph (draw_graph)                 ** */
/* ************************************************************************** */
/*                                                                            */


/*                                                                            */
/* This function returns the maximum width of the nodes in the graph          */
/* (not used anymore)                                                         */
/*                                                                            */

int get_max_nodewidth(Sgraph g)
{
    Snode n;
    int   nodewidth,
          max_nodewidth;
 
    for_all_nodes(g,n)
    {
        nodewidth = (int)node_get(graphed_node(n),NODE_WIDTH);
        max_nodewidth = maximum(max_nodewidth,nodewidth);
    }
    end_for_all_nodes(g,n);

    return(max_nodewidth);
}


/*                                                                            */
/* This function returns the maximum hight of the nodes in the graph          */
/* (not used anymore)                                                         */
/*                                                                            */

int get_max_nodehight(Sgraph g)
{
    Snode n;
    int   nodehight,
          max_nodehight;
 
    for_all_nodes(g,n)
    {
        nodehight = (int)node_get(graphed_node(n),NODE_HEIGHT);
        max_nodehight = maximum(max_nodehight,nodehight);
    }
    end_for_all_nodes(g,n);

    return(max_nodehight);
}


/*                                                                            */
/* Dummy-functions for the gridsize (not used anymore)                        */
/*                                                                            */
/*
static int get_gridsize(Sgraph g)
{
    extern int wac_buffer;
    int help_grid = wac_buffer,
        origin_grid,new_grid,
        max_nodewidth, max_nodehight,
        max_nodesize;

    max_nodewidth = get_max_nodewidth(g);
    max_nodehight = get_max_nodehight(g);
    max_nodesize  = maximum(max_nodewidth,max_nodehight);

    origin_grid = get_gridwidth(help_grid);

    if(origin_grid == 0)
    {
        return(max_nodesize);
    }
    else
    {
        if(origin_grid >= max_nodesize)
        {
            return(origin_grid);
        }
        else
        {
            new_grid = origin_grid;
            while(new_grid < max_nodesize)
            {
                new_grid = new_grid + origin_grid;
            }

            return(new_grid);
        }
    }        
}
*/

/*                                                                            */
/*                    TRANSFORMATION OF THE X-COORDINATE                      */
/* This function contains the transformation-function for the x-coordinate    */
/* mode switches between SET and WORK:                                        */
/* 1. SET sets the function that transforms the relative coordinates to       */
/*    absolute coordinates                                                    */
/* 2. WORK transforms the coordinates                                         */
/*                                                                            */

static int x_transform(int x, int minabszissa, int x_gridsize, int mode)
{
    static int a = 0;
    static int x_grid;

    if(mode == SET)
    {
        x_grid = x_gridsize/2;
        a = minabszissa - 1;

        return(-1);
    }
    else
    {
        if(mode == WORK)
        {
            return((int)(2*x_grid*(x - a)));
        }
        else
        {
            message("ERROR in x-transform \n");
        }
    }
    return -1; /*should not be reached */
}


/*                                                                            */
/*                    TRANSFORMATION OF Y-COORDINATE                          */
/*  This function contains the transformation-function for the y-coordinate   */
/*  mode switches between SET and WORK:                                       */
/*  1. SET sets the function that transforms the relative coordinates to      */
/*     absolute coordinates                                                   */
/*  2. WORK transforms the coordinates                                        */
/*                                                                            */

static int y_transform(int y, int maxordinate, int y_gridsize, int mode)
{
    static int b = 0;
    static int y_grid;

    if(mode == SET)
    {
        y_grid = y_gridsize/2;
        b = 2*y_grid*(1+maxordinate);

        return(-1);
    }
    else
    {
        if(mode == WORK)
        {
            return(b-(2*y_grid*y));
        }
        else
        {
            message("ERROR in y-transform \n");
        }
    }
    return -1; /* should not be reached */
}


/*                                                                            */
/* This function tests if the start of the edge-line is equal to the node's   */
/* coordinate. Necessary for finding the direction of the edge-line           */
/*                                                                            */

static int equal_coord(Snode n, Sedge e)
{
    Slist       * helpedge;
    Slist       helplist;
    Bend_attr   helpbend;

    if(is_sideway_edge(e))
    {
        return(0);
    }

    helpedge = attr_data_of_type(e,Slist *);
    helplist = *helpedge;
    helpbend = attr_data_of_type(helplist,Bend_attr);

    if(((n->x) == (x_transform(helpbend->x,0,0,WORK))) &&
       ((n->y) == (y_transform(helpbend->y,0,0,WORK))))
    {
        if(n == (Snode)sedge_real_source(e))
        {
            return(1);
        }
        else
        {
            return(0);
        }
    }
    else
    {
        if(n == (Snode)sedge_real_source(e))
        {
            return(0);
        }
        else
        { 
            return(1);
        }
    }
}


/*                                                                            */
/* This function updates the edge-lines in the graphed_graph                  */
/*                                                                            */

static void draw_edge_line(Sedge e, int direction)
{
    Edgeline    eline;
    Slist       line,
                helpline;
    Slist       *helplist;
    Bend_attr   helpbend;
    int         x_new,y_new;

    helplist = attr_data_of_type(e,Slist *);
    line = *helplist;

    eline = (Edgeline)edge_get(graphed_edge(e),EDGE_LINE);
    free_edgeline(eline);

    if(is_sideway_edge(e))
    {
        x_new = e->snode->x;
        y_new = e->snode->y;
        eline = new_edgeline(x_new,y_new);

        x_new = e->tnode->x;
        y_new = e->tnode->y;
        eline = add_to_edgeline(eline,x_new,y_new);

        edge_set(graphed_edge(e),EDGE_LINE,eline,0);
    }
    else
    {
        if(direction == 1)
        {
            helpline = line;
            helpbend = attr_data_of_type(helpline,Bend_attr);
        
            x_new = x_transform(helpbend->x,0,0,WORK);
            y_new = y_transform(helpbend->y,0,0,WORK);

            eline = new_edgeline(x_new,y_new);
            helpline = helpline->suc;

            while(helpline != line)
            {
                helpbend = attr_data_of_type(helpline,Bend_attr);
            
                x_new = x_transform(helpbend->x,0,0,WORK);
                y_new = y_transform(helpbend->y,0,0,WORK);

                eline = add_to_edgeline(eline,x_new,y_new);
                helpline = helpline->suc;
            }

            eline = eline->suc;

            edge_set(graphed_edge(e),EDGE_LINE,eline,0);
        }
        else
        {
            line = line->pre;
         
            helpline = line;
            helpbend = attr_data_of_type(helpline,Bend_attr);
 
            x_new = x_transform(helpbend->x,0,0,WORK);
            y_new = y_transform(helpbend->y,0,0,WORK);

            eline = new_edgeline(x_new,y_new);
            helpline = helpline->pre;
	
            while(helpline != line)
            {
                helpbend = attr_data_of_type(helpline,Bend_attr);
            
                x_new = x_transform(helpbend->x,0,0,WORK);
                y_new = y_transform(helpbend->y,0,0,WORK);

                eline = add_to_edgeline(eline,x_new,y_new);
                helpline = helpline->pre;
            }

            eline = eline->suc;

            edge_set(graphed_edge(e),EDGE_LINE,(Edgeline)eline,0);
        }
    }
}


/*                                                                            */
/* This function draws the graph                                              */
/*                                                                            */

static void draw_graph(Sgraph g)
{
    Snode   n;
    Sedge   e;
    int	    xnew,ynew;
    Sedge dummy_edge = (Sedge)NULL;
    Slist l,
          dummy_edge_list = empty_slist;

    for_all_nodes(g,n)
    /* transformation of the node coordinates */
    {
        xnew = x_transform(n->x,0,0,WORK);
        ynew = y_transform(n->y,0,0,WORK);
        set_node_coord(n,xnew,ynew);
    }
    end_for_all_nodes(g,n);

    for_all_nodes(g,n)
    {
        for_sourcelist(n,e)
        {
            if(TEST_UNIQUE_EDGE(e))
            /* each edge only one time */
            {
                if(!(is_dummy_edge(e)))
                /* dummy edges have no graphed counterpart and */
                /* so they have no coordinates                 */
                {
                    if(equal_coord(n,e)==1)
                    /* detect the direction in which the */
                    /* edgeline has to be built          */
                    {
                        draw_edge_line(e,1);
                    }
                    else
                    {
                        draw_edge_line(e,0);
                    }
                }
                else
                /* dummy edges are collected in an slist */
                {
                    dummy_edge_list = add_immediately_to_slist(dummy_edge_list,make_attr(ATTR_DATA,(char *)e));
                    dummy_edge_list = dummy_edge_list->pre;
                }
            }
        }
        end_for_sourcelist(n,e);
    }
    end_for_all_nodes(g,n);

    if(dummy_edge_list != empty_slist)
    /* remove the dummy edges from the graph */
    /* and free the slist                    */
    {
        for_slist(dummy_edge_list,l)
        {
            dummy_edge=attr_data_of_type(l,Sedge);
            remove_edge(dummy_edge);
        }
        end_for_slist(dummy_edge_list,l);

        free_slist(dummy_edge_list);
    }
}


/*                                                                            */
/* ************************************************************************** */
/* **          End of the functions for drawing the graph                  ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* ************************************************************************** */
/* **        This function is the mainfunction of the woods planar         ** */
/* **                          drawing algorithm                           ** */
/* ************************************************************************** */
/*                                                                            */

void maxwoods(Sgraph g, Sedge co_firstedge, Slist st_num, int x_gridsize, int y_gridsize)
{
    Snode   s,                          /* node with st-number 1            */
            t;                          /* node with highest st-number      */
    Sedge   firstedge;
    Slist   ordinate_list,              /* list of nodes & their ordinates  */
            main_list;                  /* main working data structur       */
    Main_attr  helpmain;
    int     start_abszissa,
            minabszissa = 0,            /* maximum number of abszissa-point */
            abszissa,                   /* number of needed abszissa-points */
            maxordinate,                /* highest level,i.e. ordinate of t */
            level;                      /* used for counting the level,     */
                                        /* that is currently computed       */

    s=get_first_node(st_num);
    t=get_last_snode(st_num);
    init_node_attributes(g,st_num,s,t);
    firstedge = get_firstedge(s,co_firstedge);
    set_node_coord(s,0,0);
    ordinate_list=get_ordinates(g,s,t,st_num);           /* computing the ordinates      */

    main_list=init_main_list(s,firstedge,ordinate_list); /* initializing the main_list   */
    
    helpmain=attr_data_of_type(main_list,Main_attr);
    maxordinate=helpmain->ordinate;

    for(level=1;level<=maxordinate;level++)              /* computing each level */
    {
        if(level == maxordinate)
        {                                                /* at the last step     */
            second_pass(main_list,st_num,
                        ordinate_list,0,maxordinate,     /* there is only the    */
                        maxordinate);                    /* node t               */
	}
        else
        {
            abszissa=first_pass(main_list,level);              /* used for centering   */
            start_abszissa = -((abszissa-1) / 2);
            minabszissa = minimum(minabszissa,start_abszissa);
            main_list = second_pass(main_list,
                    st_num,ordinate_list,                      /* used for computing   */
                                    start_abszissa,level,      /* the graph's stages   */
				    maxordinate);
        }
    }

    /*  Setting the transformation functions   */
    x_transform(0,minabszissa,x_gridsize,SET);
    y_transform(0,maxordinate,y_gridsize,SET);

    draw_graph(g);                            /* draws the graph with a given */
                                              /* transformation               */

    /*                                                                        */
    /* The free-procedures of memory allocated in woodsmod.c                  */
    /*                                                                        */

    free_ordinate_list(ordinate_list);
    free_woods_graph_attributes(g);

    /* The node-list for computing the ordinates is freed while removing nodes */
    /* The main-list is freed by deleting an element in remove_from_main_list  */
    /* The list of the st-numbers is freed in the main module mymain.c         */
}


/*                                                                            */
/* ************************************************************************** */
/* **                       END OF FILE: WOODSMOD.C                        ** */
/* ************************************************************************** */

