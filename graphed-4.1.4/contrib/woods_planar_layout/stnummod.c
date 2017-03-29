/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: STNUMMOD.C                                                 ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This module contains the procedures to compute an       ** */
/* **              st-numbering for a Sgraph. It makes especially use of   ** */
/* **              the module GETPMOD.H                                    ** */
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
#include "stnummod.h"
#include "getpmod.h"


/*                                                                            */
/* This function returns a pointer to the first node of firstedge (it's the   */
/* node with the dfs_num == 1                                                 */
/*                                                                            */

static Snode first_node(Sedge firstedge)
{
    Node_state  helpnode;

    helpnode = get_dfs_state(firstedge->snode);
	
    if(helpnode->dfs_num == 1)
    {
        return(firstedge->snode);
    }
    else
    {
        return(firstedge->tnode);
    }
}


/*                                                                            */
/* This function returns a pointer to the second node (it's the node with the */
/* dfs_num == 2                                                               */
/*                                                                            */

static Snode second_node(Sedge firstedge)
{
    Node_state  helpnode;

    helpnode = get_dfs_state(firstedge->tnode);

    if(helpnode->dfs_num == 2)
    {
        return(firstedge->tnode);
    }
    else
    {
        return(firstedge->snode);
    }
}


/*                                                                            */
/* This function initializes a stack, realized as a Slist. It pushes the      */
/* first two nodes and returns a pointer to the second (this will be the      */
/* first node for get_path()                                                  */
/*                                                                            */

static Slist init_stack(Snode t, Snode s)
{
    Slist   stack = empty_slist;
    Snode   helpstack;

    helpstack = t;
    stack = add_to_slist(stack,make_attr(ATTR_DATA,(char *)helpstack));
    helpstack = s;
    stack = add_to_slist(stack,make_attr(ATTR_DATA,(char *)helpstack));
    stack = stack->pre;
    return(stack);
}


/*                                                                            */
/* This function is used to pop the "top" value from a Slist.It returns the   */
/* node                                                                       */
/*                                                                            */

static Snode pop(Slist *stackpointer)
{
    Slist   dummy;
    Snode   helpstack;

    if(*stackpointer != empty_slist)
    {
        dummy = *stackpointer;
        helpstack = attr_data_of_type(*stackpointer,Snode);
        if(*stackpointer != (*stackpointer)->suc)
        {
            *stackpointer = (*stackpointer)->suc;
            subtract_immediately_from_slist(*stackpointer,dummy);
            return(helpstack);
        }
        else
        {
            *stackpointer = empty_slist;
            free_slist(dummy);
            return(helpstack);
        }
    }
    else
    {
        return((Snode)NULL);
    }
}


/*                                                                            */
/* This function pushes the found path on the stack (in the right sequence)   */
/*                                                                            */

static Slist push_path_to_stack(Slist stack, Slist path)
{
    Snode   helppath;

    helppath = pop(&path);
    helppath = pop(&path);

    while(helppath != (Snode)NULL)
    {
        stack = add_to_slist(stack,make_attr(ATTR_DATA,(char *)helppath));
        stack = stack->pre;
        helppath = pop(&path);
    }
    return((Slist)stack);
}


/*                                                                            */
/* This function puts another st-number to the st-numbering Slist             */
/*                                                                            */

static Slist put_st_num(Slist st_num, Snode v, int i)
{
    St_attr helpst;
	
    helpst = (St_attr)malloc(sizeof(struct st_attr));
    helpst->node = v;
    helpst->st_nr = i;
    st_num = add_to_slist(st_num,make_attr(ATTR_DATA,(char *)helpst));
    st_num = st_num->pre;
    return(st_num);
}


/*                                                                            */
/* The free procedure for the list of st-numbers                              */
/*                                                                            */

void free_st_number(Slist st_num)
{
    Slist l;

    for_slist(st_num,l)
    {
        free(attr_data_of_type(l,St_attr));
    }
    end_for_slist(st_num,l);
    free_slist(st_num);
}

/*                                                                            */
/* ************************************************************************** */
/* This is the "main-function" of the st-numbering. It returns a Slist, that  */
/* contains the st-numbers and their respective node                          */
/* ************************************************************************** */
/*                                                                            */

Slist st_number(Sgraph g, Sedge firstedge)
{
    int     i=1;
    Snode   v,t,s;
    Slist   stack,path,st_num;
	
    t = first_node(firstedge);
    s = second_node(firstedge);
    mark_old_node(t);
    mark_old_node(s);
    mark_old_edge(firstedge);
    stack = init_stack(t,s);
    st_num = empty_slist;

    while((v = pop(&stack)) != t)
    {
        path = get_path(v);
        if(path == empty_slist)
        {
            st_num = put_st_num(st_num,v,i);
            i++;
        }
        else
        {
            stack = push_path_to_stack(stack,path);
        }
    }
    free_slist(stack);

    st_num = put_st_num(st_num,t,i);
    st_num = st_num->pre;

    return(st_num);
}

/*                                                                            */
/* ************************************************************************** */
/* **                        END OF FILE: STNUMMOD.C                       ** */
/* ************************************************************************** */

