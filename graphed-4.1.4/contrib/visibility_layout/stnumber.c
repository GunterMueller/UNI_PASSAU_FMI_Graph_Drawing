/*************************************************************/
/*     							     */
/*                        STNUMBER.C			     */
/*							     */
/*     ( berechnet  ST_Numerierung eines Graphen )    	     */
/*  Uebernommen von R. Gallersdoerfer und leicht veraendert  */
/*  Aenderungen: die uebergebenen Strukturen in attrs.data   */
/*         werden gesichert und zurueckgegeben, MAX_NR wird  */
/*	   in test_st_number initialisiert und die erste     */
/*   	   Kante im ersten Knoten wird als st-Kante benutzt  */
/*							     */
/*************************************************************/

#include <values.h> /* MAXINT */

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "tarjanmod.h"
#include "listen.h"

#define UNDEF		-1
#define REACHED		0
#define NEW		0
#define OLD		1
#define CYCLE		0
#define TREE		1


#define ST_GRAPH_ATTRS(g) \
	(((st_graph_attributes *)(attr_data(g))))
#define ST_GRAPH_MAKR(g) \
	( ST_GRAPH_ATTRS(g)->mark)
#define ST_GRAPH_DATA(g) \
	( ST_GRAPH_ATTRS(g)->data)
#define ST_NODE_ATTRS(n) \
	(((st_node_attributes *)(attr_data(n))))
#define ST_NODE_MARK(n) \
	(ST_NODE_ATTRS(n)->mark)
#define PRE_ORDER_NUM(n) \
	(ST_NODE_ATTRS(n)->pre_order_num )
#define LV(n) \
	(ST_NODE_ATTRS(n)->lv)
#define CYCLE_W(n) \
	(ST_NODE_ATTRS(n)->cycle_w)
#define W_CYCLE(n) \
	(ST_NODE_ATTRS(n)->w_cycle)
#define TREE_W(n) \
	(ST_NODE_ATTRS(n)->tree_w )
#define PARENT(n) \
	(ST_NODE_ATTRS(n)->parent)
#define FITTING(n) \
	(ST_NODE_ATTRS(n)->fitting)
#define ST_NODE_DATA(n) \
	(ST_NODE_ATTRS(n)->data)
#define ST_EDGE_ATTRS(e) \
	(((st_edge_attributes *)(attr_data(e))))
#define ST_EDGE_MARK(e) \
	(ST_EDGE_ATTRS(e)->mark)
#define ST_EDGE_TYPE(e) \
	(ST_EDGE_ATTRS(e)->type)
#define ST_EDGE_DATA(e) \
	(ST_EDGE_ATTRS(e)->data)

typedef struct
	{
	int 		mark;
	Graph_attr	data;
	} st_graph_attributes;
typedef struct
	{
	int 		mark,pre_order_num,lv;
	LIST 		cycle_w,w_cycle,tree_w;
	Snode 		parent,fitting;
	Node_attr 	data;
	} st_node_attributes;
typedef struct
	{
	int 		mark,type;
	Edge_attr 	data;
	} st_edge_attributes;

Graph_attr	Graphdata;
Node_attr 	Nodedata;
Edge_attr 	Edgedata;

Global int my_test_st_number(Sgraph graph)
{
/*extern int MAX_NR;*/
Snode n;
Sedge e;
int   is_st,has_lower,has_higher;
int   max_num = 1;
int   min_num = MAXINT;
int   highest = 0;

for_all_nodes(graph,n)
	{
	min_num = minimum( min_num, n->nr );
	max_num = maximum( max_num, n->nr );
} end_for_all_nodes(graph,n)
is_st = (min_num == 1);
for_all_nodes(graph,n)
	{
	has_lower = FALSE;
	has_higher = FALSE;
	for_sourcelist(n,e)
		{
		if(e->tnode->nr > n->nr) has_higher = TRUE;
		if(e->tnode->nr < n->nr) has_lower  = TRUE;
		highest = maximum(highest, e->tnode->nr);
	} end_for_sourcelist(n,e)
	if(graph->directed)
		{
		for_targetlist(n,e)
			{
			if(e->snode->nr > n->nr) has_higher = TRUE;
			if(e->snode->nr < n->nr) has_lower  = TRUE;
			highest = maximum(highest, e->snode->nr);
		} end_for_targetlist(n,e)
		}
	if(n->nr == min_num)
		{
		is_st = is_st && has_higher && !has_lower;
		is_st = is_st && (highest == max_num);
		continue;
		}
	if(n->nr == max_num)
		{
		is_st = is_st && !has_higher && has_lower;
		continue;
		}
	is_st = is_st && has_higher && has_lower;
	if(!is_st) break;
} end_for_all_nodes(graph,n)
/*MAX_NR = highest;*/
return(is_st);
}

static char *alloc_graph_attrs(void)
{ return(malloc(sizeof(st_graph_attributes))); }
static char *alloc_st_node_attrs(void)
{ return(malloc(sizeof(st_node_attributes))); }
static char *alloc_st_edge_attrs(void)
{ return(malloc(sizeof(st_edge_attributes))); }

static void init_graph_attrs(st_graph_attributes *g)
{ g->mark = NEW; }
static void init_st_node_attrs(st_node_attributes *n)
{
n->mark = NEW;
n->pre_order_num = UNDEF;
n->lv = MAXINT;
INIT_LIST(n->cycle_w);
INIT_LIST(n->w_cycle);
INIT_LIST(n->tree_w);
n->parent = NULL;
n->fitting = NULL;
n->data = NULL;
}

static void init_st_edge_attrs(st_edge_attributes *e)
{
e->mark = NEW;
e->type = CYCLE;
e->data = NULL;
}

static void init_st_attrs(Sgraph g)
{
Snode n;
Sedge e;
Graphdata = attr_data_of_type(g,Graph_attr);
attr_data(g) = alloc_graph_attrs();
init_graph_attrs(ST_GRAPH_ATTRS(g));
ST_GRAPH_DATA(g) = Graphdata;
for_all_nodes(g,n)
	{
	Nodedata = attr_data_of_type(n,Node_attr);
	attr_data(n) = nil;
	attr_data(n) = alloc_st_node_attrs();
	init_st_node_attrs(ST_NODE_ATTRS(n));
	ST_NODE_DATA(n) = Nodedata;
	for_sourcelist(n,e)
		{
		Edgedata = attr_data_of_type(e,Edge_attr);
		attr_data(e) = nil;
		attr_data(e) = alloc_st_edge_attrs();
		init_st_edge_attrs(ST_EDGE_ATTRS(e));
		ST_EDGE_DATA(e) = Edgedata;
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)
}

static void free_graph_attrs(char *p)
{ free(p); }

static void free_node_attrs(char *p)
{
CLEAR_LIST(((st_node_attributes *)p)->cycle_w);
CLEAR_LIST(((st_node_attributes *)p)->w_cycle);
CLEAR_LIST(((st_node_attributes *)p)->tree_w);
free(p);
}

static void free_edge_attrs(char *p)
{ free(p); }

static void free_st_attrs(Sgraph g)
{
Snode n;
Sedge e;
Graphdata = (Graph_attr)(ST_GRAPH_DATA(g));
free_graph_attrs(attr_data(g));
set_graphattrs(g,make_attr(ATTR_DATA,Graphdata));
for_all_nodes(g,n)
	{
	Nodedata = (Node_attr)(ST_NODE_DATA(n));
	free_node_attrs(attr_data(n));
        set_nodeattrs(n,make_attr(ATTR_DATA,Nodedata));
	for_sourcelist(n,e)
		{
		Edgedata = (Edge_attr)(ST_EDGE_DATA(e));
	        free_edge_attrs(attr_data(e)); 
		set_attr_data(e, Edgedata);
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)
}

static void assign_pre_order(Snode n, int *pre, int *min_lv)
{
Sedge e;
int help_lv,is_leaf;
PRE_ORDER_NUM(n) = (*pre)++;
is_leaf = TRUE;
help_lv = *pre - 1;
for_sourcelist(n,e);
	{
	if(PRE_ORDER_NUM(e->tnode ) == UNDEF) 
		{
		is_leaf = FALSE;
		break;
		}
	else help_lv = minimum(help_lv, PRE_ORDER_NUM(e->tnode));
} end_for_sourcelist(n,e)
if(n->graph->directed)
	{
	for_targetlist(n,e);
		{
		if(PRE_ORDER_NUM(e->snode ) == UNDEF) 
			{
			is_leaf = FALSE;
			break;
			}
		else help_lv = minimum(help_lv, PRE_ORDER_NUM(e->snode));
	} end_for_targetlist(n,e)
	}
if(!is_leaf)
	{
	for_sourcelist(n,e)
		{
		if(PRE_ORDER_NUM(e->tnode) == UNDEF)
			{
			ST_EDGE_TYPE(e) = TREE;
			if( !n->graph->directed)ST_EDGE_TYPE(e->tpre) = TREE;
			assign_pre_order(e->tnode,pre,min_lv);
			help_lv = minimum(help_lv, *min_lv);
			}
		else help_lv = minimum(help_lv, PRE_ORDER_NUM(e->tnode));
	} end_for_sourcelist(n,e)
	if(n->graph->directed)
		{
		for_targetlist(n,e)
			{
			if(PRE_ORDER_NUM(e->snode) == UNDEF)
				{
				ST_EDGE_TYPE(e) = TREE;
				assign_pre_order(e->snode,pre,min_lv);
				help_lv = minimum(help_lv, *min_lv);
				}
			else help_lv = minimum(help_lv, PRE_ORDER_NUM(e->snode));
		} end_for_targetlist(n,e)
		}
	}
LV(n) = help_lv;
*min_lv = help_lv;
for_sourcelist(n,e)
	{
	if(LV(n) == PRE_ORDER_NUM(e->tnode)||LV(n) == LV(e->tnode))
		FITTING(n) = e->tnode;
	if(ST_EDGE_TYPE(e) == TREE)
		{
		if(PRE_ORDER_NUM(e->tnode) < PRE_ORDER_NUM(n) )
		  PARENT(n) = e->tnode; else PUSH_NODE(TREE_W(n),e->tnode);
		}
	else    {
		if(PRE_ORDER_NUM(e->tnode) < PRE_ORDER_NUM(n) )
		   PUSH_NODE(W_CYCLE(n),e->tnode); else PUSH_NODE(CYCLE_W(n),e->tnode);
		}
} end_for_sourcelist(n,e)
if(n->graph->directed)
	{
	for_targetlist(n,e)
		{
		if(LV(n) == PRE_ORDER_NUM(e->snode)||LV(n) == LV(e->snode))
			FITTING(n) = e->snode;
		if(ST_EDGE_TYPE(e) == TREE)
			{
			if(PRE_ORDER_NUM(e->snode) < PRE_ORDER_NUM(n) )
			  PARENT(n) = e->snode; else PUSH_NODE(TREE_W(n),e->snode);
			}
		else    {
			if(PRE_ORDER_NUM(e->snode) < PRE_ORDER_NUM(n) )
			   PUSH_NODE(W_CYCLE(n),e->snode); else PUSH_NODE(CYCLE_W(n),e->snode);
			}
	} end_for_targetlist(n,e)
	}
}
static void pre_order(Sgraph g)
{
int pre = 1;
int lv = MAXINT;
assign_pre_order(g->nodes, &pre, &lv);
}

static void mark_old (Snode sn, Snode tn)
{
Sedge e;
for_sourcelist(sn,e)
	{
	if(e->tnode == tn ) ST_EDGE_MARK( e) = OLD;
} end_for_sourcelist(sn,e)
if(sn->graph->directed)
	{
	for_targetlist(sn,e)
		{
		if(e->snode == tn ) ST_EDGE_MARK( e) = OLD;
	} end_for_targetlist(sn,e)
	}
}

static LIST path_finder (Snode v)
{
LIST path;
Snode w;
INIT_LIST(path);
if(! IS_EMPTY_LIST(W_CYCLE(v)))
	{
	PUSH_NODE(path,v);
	POP_NODE(W_CYCLE(v),w);
	mark_old(v,w);
	PUSH_NODE(path,w);
	return(path);
	}
if(! IS_EMPTY_LIST(TREE_W(v)))
	{
	PUSH_NODE(path,v);
	POP_NODE(TREE_W(v),w);
	mark_old(v,w);
	PUSH_NODE(path,w);
	while(ST_NODE_MARK(w) == NEW)
		{
		Snode x;
		x = FITTING(w);
		ST_NODE_MARK(w) = OLD;
		mark_old(w,x);
		PUSH_NODE(path,x);
		w = x;
		}
	return(path);
	}
if(! IS_EMPTY_LIST(CYCLE_W(v)))
	{
	PUSH_NODE(path,v);
	POP_NODE(CYCLE_W(v),w);
	mark_old(v,w);
	PUSH_NODE(path,w);
	while(ST_NODE_MARK(w) == NEW)
		{
		Snode x;
		x = PARENT(w);
		ST_NODE_MARK(w) = OLD;
		mark_old(w,x);
		PUSH_NODE(path,x);
		w = x;
		}
	return(path);
	}
return(path);
}

Global void my_st_number(Sgraph graph)
{
Snode n;
Sedge e;
int st_num = 1;
LIST stack,path;
init_st_attrs(graph);
pre_order(graph);
INIT_LIST(stack);
n = graph->nodes;
ST_NODE_MARK(n) = OLD;
CLEAR_LIST(TREE_W(n));
for_sourcelist(n,e)
	{
	if(ST_EDGE_TYPE(e) == TREE)
		{
		ST_EDGE_MARK(e) = OLD;
		ST_NODE_MARK(e->tnode) = OLD;
		PUSH_NODE(stack,e->tnode);
		}
} end_for_sourcelist(n,e)
if(graph->directed)
	{
	for_targetlist(n,e)
		{
		if(ST_EDGE_TYPE(e) == TREE)
			{
			ST_EDGE_MARK(e) = OLD;
			ST_NODE_MARK(e->snode) = OLD;
			PUSH_NODE(stack,e->snode);
			}
	} end_for_targetlist(n,e)
	}
PUSH_NODE(stack,n);
while(! IS_EMPTY_LIST(stack))
	{
	POP_NODE(stack,n);
	path = path_finder(n);
	if(IS_EMPTY_LIST(path))
		{
		n->nr = st_num++;
		/*printf("Knotennummer %d vergeben\n",n->nr);*/
		}
	else {
		Snode help;
		POP_NODE(path,help);
		while(! IS_EMPTY_LIST(path))
			{
			POP_NODE(path,help);
			PUSH_NODE(stack,help);
			}
		}
	}
free_st_attrs(graph);
}


