
/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: DFSMOD.C                                                   ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This module contains the functions for finding and      ** */
/* **              bisecting the largest face in a biconnected planar      ** */
/* **              graph                                                   ** */
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
/* ************************************************************************** */
/* **              Functions for finding the largest face                  ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* This function initializes the structure face_info and returns a pointer    */
/* to it                                                                      */
/*                                                                            */

static Face_info init_face_info(void)
{
    Face_info face; 

    face = (Face_info)malloc(sizeof(struct face_info));
    face->direction  = NO;
    face->size       = 0;
    face->firstnode  = (Snode)NULL;
    face->thirdnode  = (Snode)NULL;
    face->firstedge  = (Sedge)NULL;
    face->secondedge = (Sedge)NULL;
    face->facenodes  = empty_slist;

    return((Face_info)face);
}


/*                                                                            */
/* This function frees a face_info structure                                  */
/*                                                                            */

static Face_info free_face_info(Face_info face)
{
    Slist face_list;

    face_list = face->facenodes;
    free_slist(face_list);
    free(face);
    return((Face_info)NULL);
}


/*                                                                            */
/* This function returns the number of nodes in a graph                       */
/*                                                                            */

static int size_of_graph(Sgraph g)
{
    int   i = 0;
    Snode n;

    for_all_nodes(g,n)
    {
        i++;
    }
    end_for_all_nodes(g,n);

    return((int)i);
}


/*                                                                            */
/* This function finds a tree-edge in the ordered edge list, that leads to a  */
/* higher dfs-numbered node and is either NON-marked or PRE-marked            */
/*                                                                            */

static Sedge get_traverse_tree_edge(Snode n, int mode)
{
    Slist       embed_list,
                l;
    Snode       opnode;
    Node_state	helpnode,
                helpopnode;
    int         dfs_of_node,
                dfs_of_opnode;
    Sedge       edge;
    Edge_state	helpedge;
        
    helpnode    = get_dfs_state(n);
    dfs_of_node = helpnode->dfs_num;

    embed_list = get_embed_edges(n);
    for_slist(embed_list,l)
    {
        edge          = attr_data_of_type(l,Sedge);
        helpedge      = attr_data_of_type(edge,Edge_state);
        opnode        = opposit_node(n,edge);
        helpopnode    = get_dfs_state(opnode);
        dfs_of_opnode = helpopnode->dfs_num;
	
        if((helpedge->is_tree_edge == YES) &&
           (helpedge->is_back_edge == NO))
        {
            if(dfs_of_node < dfs_of_opnode)
            {
                if(mode == PRE)
                {
                    if(helpedge->traversed == NO)
                    {
                        return((Sedge)edge);
                    }
                }

                if(mode == SUC)
                {
                    if(helpedge->traversed == PRE)
                    {
                        return((Sedge)edge);
                    }
                }
            }
        }
    }
    end_for_slist(embed_list,l);

    return((Sedge)NULL);
}


/*                                                                            */
/* This function finds a face by running along the edges clockwise (PRE)      */
/*                                                                            */

static Face_info pre_traverse(Snode n, Sedge e, Face_info face)
{
    Snode   opnode;
    Slist   embed_list = empty_slist,
            l          = empty_slist,
            list       = empty_slist;
    Sedge   	edge;
    Edge_state	helpedge;
    int         count = 1;

    opnode = opposit_node(n,e);

    /* face updating          */
    face->direction = PRE;
    face->firstnode = n;
    face->firstedge = e;
    /* end of face updating   */

    MARK_PRE_TRAVERSED(e);
    MARK_FINDLABEL(e);

    while(opnode != n)
    {
        count++;
        embed_list = get_embed_edges(opnode);
        for_slist(embed_list,l)
        {
            edge = attr_data_of_type(l,Sedge);
            helpedge = attr_data_of_type(edge,Edge_state);
            if(helpedge->findlabel == IAM)
            {
                UNMARK_FINDLABEL(e);
                list = l->pre;
                break;
            }
        }
        end_for_slist(embed_list,l);

        e = attr_data_of_type(list,Sedge);

        if((attr_data_of_type(e,Edge_state)->traversed) == NO)
        {
            MARK_PRE_TRAVERSED(e);
        }

        MARK_FINDLABEL(e);

        /* face updating              */
        if(count == 3)
        {
            face->thirdnode  = opnode;
            face->secondedge = edge;
        }
        /* end of face updating       */

        opnode = opposit_node(opnode,e);
    }

    UNMARK_FINDLABEL(e);
    face->size = count;

    return((Face_info)face);
}


/*                                                                            */
/* This function finds a face by running along the edges counter-             */
/* clockwise (SUC)                                                            */
/*                                                                            */

static Face_info suc_traverse(Snode n, Sedge e, Face_info face)
{
    Snode   opnode;
    Slist   embed_list = empty_slist,
            l          = empty_slist,
            list       = empty_slist;
    Sedge   	edge;
    Edge_state	helpedge;
    int         count = 1;

    opnode = opposit_node(n,e);

    /* face updating          */
    face->direction = SUC;
    face->firstnode = n;
    face->firstedge = e;
    /* end of face updating   */

    MARK_SUC_TRAVERSED(e);
    MARK_FINDLABEL(e);

    while(opnode != n)
    {
        count++;
        embed_list = get_embed_edges(opnode);
        for_slist(embed_list,l)
        {
            edge = attr_data_of_type(l,Sedge);
            helpedge = attr_data_of_type(edge,Edge_state);
            if(helpedge->findlabel == IAM)
            {
                UNMARK_FINDLABEL(e);
                list = l->suc;
                break;
            }
        }
        end_for_slist(embed_list,l);

        e = attr_data_of_type(list,Sedge);
        MARK_SUC_TRAVERSED(e);
        MARK_FINDLABEL(e);

        /* face updating              */
        if(count == 3)
        {
            face->thirdnode  = opnode;
            face->secondedge = edge;
        }
        /* end of face updating       */

        opnode = opposit_node(opnode,e);
    }

    UNMARK_FINDLABEL(e);
    face->size      = count;

    return((Face_info)face);
}


/*                                                                            */
/* This function sets the face-id of the nodes and edges on a face to 1. All  */
/* others remain 0.                                                           */
/*                                                                            */

static void set_face_id_of_face(Face_info face)
{
    Snode n,
          opnode;
    Sedge e,
          edge;
    Slist embed_list,
          l,
          list;
    Edge_state helpedge;
    int   i,
          halfsize;

    n = face->firstnode;
    e = face->firstedge;
    opnode = opposit_node(n,e);
    
    halfsize = (face->size)/2;
    MARK_FINDLABEL(e);

    if(face->direction == PRE)
    {
        for(i=1;i<=halfsize;i++)
        {
            embed_list = get_embed_edges(opnode);
            for_slist(embed_list,l)
            {
                edge = attr_data_of_type(l,Sedge);
                helpedge = attr_data_of_type(edge,Edge_state);
                if(helpedge->findlabel == IAM)
                {
                    UNMARK_FINDLABEL(e);
                    list = l->pre;
                    break;
                }
            }
            end_for_slist(embed_list,l);

            e = attr_data_of_type(list,Sedge);
            MARK_FINDLABEL(e);

            if(i == halfsize)
            {
                face->thirdnode = opnode;
                face->secondedge = edge;
            }
            opnode = opposit_node(opnode,e);
        }
        UNMARK_FINDLABEL(e);
    }
    else
    {
        for(i=1;i<=halfsize;i++)
        {
            embed_list = get_embed_edges(opnode);
            for_slist(embed_list,l)
            {
                edge = attr_data_of_type(l,Sedge);
                helpedge = attr_data_of_type(edge,Edge_state);
                if(helpedge->findlabel == IAM)
                {
                    UNMARK_FINDLABEL(e);
                    list = l->suc;
                    break;
                }
            }
            end_for_slist(embed_list,l);

            e = attr_data_of_type(list,Sedge);
            MARK_FINDLABEL(e);

            if(i == halfsize)
            {
                face->thirdnode = opnode;
                face->secondedge = edge;
            }
            opnode = opposit_node(opnode,e);
        }
        UNMARK_FINDLABEL(e);
    }
}


/*                                                                            */
/* This function finds the largest face in the graph and returns a pointer to */
/* the structure face_info                                                    */
/*                                                                            */

static Face_info get_largest_face(Sgraph g)
{
    Snode   n;
    Sedge   tree_edge;
    int     max_facesize = 0,
            facesize     = 0,
            graphsize;
    Face_info max_face,
              face,
              helpface;
    
    max_face = init_face_info();
    face     = init_face_info();

    graphsize = size_of_graph(g);

    for_all_nodes(g,n)
    {
        while((tree_edge = get_traverse_tree_edge(n,PRE)) != NULL)
        {
            face = pre_traverse(n,tree_edge,face);
            facesize = face->size;
            if(facesize > max_facesize)
            {
                helpface = max_face;
                max_face = face;
                face     = helpface;
                max_facesize = facesize;

                if(max_facesize == graphsize)
                {
                    break;      /* no face can be bigger */
                }
            }
            else
            {
                /* nothing needs to be done */
            }
        }

        if(max_facesize != graphsize)
        {
            while((tree_edge = get_traverse_tree_edge(n,SUC)) != NULL)
            {
                face = suc_traverse(n,tree_edge,face);
                facesize = face->size;
                if(facesize > max_facesize)
                {
                    helpface = max_face;;
                    max_face = face;
                    face     = helpface;
                    max_facesize = facesize;

                    if(max_facesize == graphsize)
                    {
                        break;      /* no face can be bigger */
                    }
                }
                else
                {
                    /* nothing needs to be done */
                }
            }
        }

        if(max_facesize == graphsize)
        {
            break;      /* no face can be bigger */
        }
    }
    end_for_all_nodes(g,n);

    free_face_info(face);
    set_face_id_of_face(max_face);

    return((Face_info)max_face);
}

/*                                                                            */
/* ************************************************************************** */
/* **          END OF THE FUNCTIONS FOR GETTING THE LARGEST FACE           ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* ************************************************************************** */
/* **              BISECTING THE LARGEST FACE BY A DUMMY-EDGE              ** */
/* ************************************************************************** */
/*                                                                            */

/*                                                                            */
/* This function looks for a existing edge that devides the face into two     */
/* parts. If there is one, this edge is returned, else NULL is returned.      */
/*                                                                            */

static Sedge find_existing_cutedge(Face_info face)
{
    Slist embed_list,
          elist;
    Snode startnode,
          endnode,
          opnode;
    Sedge edge;

    startnode  = face->firstnode;
    endnode    = face->thirdnode;
    embed_list = get_embed_edges(startnode);
    for_slist(embed_list,elist)
    {
        edge     = attr_data_of_type(elist,Sedge);
        opnode   = opposit_node(startnode,edge);
        if(opnode == endnode)
        {
            return((Sedge)edge);
        }
    }
    end_for_slist(embed_list,elist);

    startnode  = face->thirdnode;
    endnode    = face->firstnode;
    embed_list = get_embed_edges(startnode);
    for_slist(embed_list,elist)
    {
        edge     = attr_data_of_type(elist,Sedge);
        opnode   = opposit_node(startnode,edge);
        if(opnode == endnode)
        {
            return((Sedge)edge);
        }
    }
    end_for_slist(embed_list,elist);

    return((Sedge)NULL);
}


/*                                                                            */
/* This function inserts the dummy-edge in the embed-lists of the first node  */
/* and the third node of the face (the right ordering is maintained)          */
/*                                                                            */

static void insert_new_edge(Face_info face, Sedge new_edge)
{
    Snode snode,
          tnode;
    Sedge find_edge,
          edge;
    Slist embed_list,
          l,
          list;
    
    snode = face->firstnode;
    find_edge = face->firstedge;
    
    embed_list = get_embed_edges(snode);
    for_slist(embed_list,l)
    {
        edge = attr_data_of_type(l,Sedge);
        if(edge == find_edge)
        {
            list = l;
            break;
        }
    }
    end_for_slist(embed_list,l);

    if(face->direction == PRE)
    {
        list = list->suc;
        add_immediately_to_slist(list,make_attr(ATTR_DATA,(char *)new_edge));
    }
    else
    {
        add_immediately_to_slist(list,make_attr(ATTR_DATA,(char *)new_edge));
    }

    tnode = face->thirdnode;
    find_edge = face->secondedge;
    
    embed_list = get_embed_edges(tnode);
    for_slist(embed_list,l)
    {
        edge = attr_data_of_type(l,Sedge);
        if(edge == find_edge)
        {
            list = l;
            break;
        }
    }
    end_for_slist(embed_list,l);

    if(face->direction == PRE)
    {
        add_immediately_to_slist(list,make_attr(ATTR_DATA,(char *)new_edge));
    }
    else
    {
        list = list->suc;
        add_immediately_to_slist(list,make_attr(ATTR_DATA,(char *)new_edge));
    }
}


/*                                                                            */
/* This function cares about the bisection of the largest face by a           */
/* dummy-edge or finding a existing bisecting edge                            */
/*                                                                            */

static Sedge bisect_face(Face_info face)
{
    Edge_state helpedge;
    Sedge      new_edge;

    if(face->size <= 3)
    {
        return(face->firstedge);
    }
    else
    {
        if((new_edge = find_existing_cutedge(face)) != (Sedge)NULL)
        {
            return((Sedge)new_edge);
        }
        else
        {
            helpedge = (Edge_state)malloc(sizeof(struct edge_state));
            helpedge->is_tree_edge  = NO;
            helpedge->is_back_edge  = NO;
            helpedge->face_id       = NO;
            helpedge->traversed     = NO;
            helpedge->findlabel     = NO;

            new_edge = make_edge(face->firstnode,
                                 face->thirdnode,
                                 make_attr(ATTR_DATA,(char *)helpedge));
        
            insert_new_edge(face,new_edge);

            return((Sedge)new_edge);
        }
    }
}


/*                                                                            */
/* ************************************************************************** */
/* The "main-function" of bisecting the largest face returns the bisecting    */
/* edge. If there is no such edge, a dummy-edge (sedge with no graphed edge   */
/* equivalent) is inserted.                                                   */
/* ************************************************************************** */
/*                                                                            */

Sedge bisection_of_largest_face(Sgraph g)
{
    Face_info largest_face;
    Sedge firstedge;

    /* Gets necessary information about the largest face */
       largest_face = get_largest_face(g);

    /* Bisection of this face */
       firstedge = bisect_face(largest_face);

    return((Sedge)firstedge);
}


/*                                                                            */
/* ************************************************************************** */
/* **                         END OF FILE: BISECMOD.C                      ** */
/* ************************************************************************** */

