
/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: BLOCKMOD.C                                                 ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This module provides the start-functions for producing  ** */
/* **              a planar cylindric drawing                              ** */
/* **                                                                      ** */
/* ** Date: 18.5.1994                                                      ** */
/* **                                                                      ** */
/* ************************************************************************** */
/*                                                                            */

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "algorithms.h"
#include "listen.h"
#include "blockmod.h"
#include "tarjanmod.h"


/******************* Alloc - Routinen ******************/

Local char *alloc_cyl_graph_attrs(void)
{return (malloc(sizeof(struct cyl_graph_attr)));}
Local char *alloc_cyl_node_attrs(void)
{return (malloc(sizeof(struct cyl_node_attr)));}
Local char *alloc_cyl_edge_attrs(void)
{return (malloc(sizeof(struct cyl_edge_attr)));}

Local char *alloc_bab_node_attrs(void)
{return (malloc(sizeof(struct bab_node_attr)));}
Local char *alloc_bab_edge_attrs(void)
{return (malloc(sizeof(struct bab_edge_attr)));}

static	void init_cyl_edge_attrs(Cyl_Edge_attr e)
{
e->u = 0;
}

static	void init_bab_edge_attrs(Bab_Edge_attr e)
{
e->marked = 0;
}

static	void init_cyl_node_attrs(Cyl_Node_attr n)
{
n->nr = 0;
n->p = NULL;
n->is_cut_node = 0;
n->L = 0;
}

static	void init_bab_node_attrs(Bab_Node_attr n)
{
n->block = NULL;
n->cut = NULL;
n->is_cut_node = 0;
n->leave = 0;
}

static	void init_cyl_graph_attrs(Cyl_Graph_attr g)
{
g->cutnodes = NULL;
g->components = NULL;
g->bab = NULL;
g->nr_of_comp = 0;
g->width = 0;
g->height = 0;
}

static 	void 	init_cyl_node_and_edges(Sgraph g)
      	  
	/* create structures  */
{
Snode       n;
Sedge       e;

attr_data(g) = nil;
attr_data(g) = alloc_cyl_graph_attrs();
init_cyl_graph_attrs(CY_GRAPH_ATTRS(g));

for_all_nodes(g,n) {
	attr_data(n) = nil;
	attr_data(n) = alloc_cyl_node_attrs();
	init_cyl_node_attrs(CY_NODE_ATTRS(n));
        for_sourcelist(n,e) {	
		attr_data(e) = alloc_cyl_edge_attrs();
		init_cyl_edge_attrs(CY_EDGE_ATTRS(e));
        }  end_for_sourcelist(n,e);
}  end_for_all_nodes(g,n);
}

Local	int	teste(Snode v)
{
Sedge	e;
int	test = 0;
if(CY_NODE_P(v) != NULL)test = 1;
for_sourcelist(v,e) {
	if(CY_EDGE_U(e) != 1)test = 1;
} end_for_sourcelist(v,e)
if(v->graph->directed)
	{
	for_targetlist(v,e) {
		if(CY_EDGE_U(e) != 1)test = 1;
	} end_for_targetlist(v,e)
	}
return(test);
}

Local	Sedge	find_edge(Snode v)
{
Sedge	e,w = NULL;
for_sourcelist(v,e) {
	if(CY_EDGE_U(e) == 0)w = e;
} end_for_sourcelist(v,e)
if(v->graph->directed)
	{
	for_targetlist(v,e) {
		if(CY_EDGE_U(e) == 0)w = e;
	} end_for_targetlist(v,e)
	}
return(w);
}

Global	void block_cut(Sgraph g)
{
int	i=1;
Snode	n,v,w,s;
Sedge	e = NULL;
Slist	B,S = NULL;
init_cyl_node_and_edges(g);
s = g->nodes;
v = s;
CY_NODE_NR(v) = 1;
PUSH_NODE(S,v);
do {
	while((e = find_edge(v)))
		{
		CY_EDGE_U(e) = 1;
		if(!g->directed)CY_EDGE_U(e->tsuc) = 1;
		w = OTHER_NODE(v,e);
		if(CY_NODE_NR(w) == 0)
			{
			CY_NODE_P(w) = v;
			i++;
			CY_NODE_NR(w) = i;
			CY_NODE_L(w) = i;
			PUSH_NODE(S,w);
			v = w;
		} else  CY_NODE_L(v) = minimum(CY_NODE_L(v),CY_NODE_NR(w));
		}
	if(CY_NODE_P(v) != s)
		{
		if(CY_NODE_L(v) < CY_NODE_NR(CY_NODE_P(v)))
			CY_NODE_L(CY_NODE_P(v)) = 
			minimum(CY_NODE_L(CY_NODE_P(v)),CY_NODE_L(v));
		else	{
			PUSH_ONCE_NODE(CY_GRAPH_CUTS(g),CY_NODE_P(v));
			CY_NODE_CUT(CY_NODE_P(v)) = 1;
			INIT_LIST(B);
			POP_NODE(S,n);
			QUEUE_NODE(B,n);
			while(n != v)
				{
				POP_NODE(S,n);
				QUEUE_NODE(B,n);
				}
			QUEUE_NODE(B,CY_NODE_P(v));
			QUEUE_LIST(CY_GRAPH_COMP(g),B);
			CY_GRAPH_COMP_NR(g)++;
			}
	} else	{
		if((e = find_edge(s)))
			{
			PUSH_ONCE_NODE(CY_GRAPH_CUTS(g),s);
			CY_NODE_CUT(s) = 1;
			}
		INIT_LIST(B);
		POP_NODE(S,n);
		QUEUE_NODE(B,n);
		while(n != v)
			{
			POP_NODE(S,n);
			QUEUE_NODE(B,n);
			}
		QUEUE_NODE(B,s);
		QUEUE_LIST(CY_GRAPH_COMP(g),B);
		CY_GRAPH_COMP_NR(g)++;
		}
	v = CY_NODE_P(v);
} while(teste(v));
/*printf("Folgende Liste der Cuts : ");
print_my_stack(CY_GRAPH_CUTS(g));*/
/*printf("Ende von block-cut\n");*/
}

Local	Sgraph	create_bab_graph(Sgraph g)
{
Sgraph	bab;
bab = make_graph(make_attr(ATTR_DATA,NULL));
bab->directed = 1;
set_graphlabel(bab,"Block-Artikulations-Graph");
CY_GRAPH_GRAPH(g) = (Sgraph)bab;
return(bab);
}

Local	void	create_cut_node(Sgraph g, Snode cut)
{
Snode 	n;
init_visinode_attrs(cut);
cut->y = -1;
n = make_node(g,make_attr(ATTR_DATA,NULL));
/*printf("Nodenr.: %d ist Cut mit Knoten nr.:%d lab:%s x:%d y:%d\n",n->nr,cut->nr,cut->label,cut->x,cut->y);*/
attr_data(n) = nil;
attr_data(n) = alloc_bab_node_attrs();
init_bab_node_attrs(BAB_NODE_ATTRS(n));
BAB_NODE_CUT(n) = cut;
BAB_NODE_IS_CUT(n) = 1;
}

Local	Sgraph	create_block_graph(Sgraph g)
{
Sgraph	Blockgraph;
Snode	n;
n = make_node(g,make_attr(ATTR_DATA,NULL));
/*printf("Nodenr.: %d ist Block mit Knoten : ",n->nr);*/
attr_data(n) = nil;
attr_data(n) = alloc_bab_node_attrs();
init_bab_node_attrs(BAB_NODE_ATTRS(n));
Blockgraph = make_graph(make_attr(ATTR_DATA,NULL));
Blockgraph->directed = 1;
set_graphlabel(Blockgraph,"Block-Graph");
BAB_NODE_BLOCK(n) = Blockgraph;
BAB_NODE_IS_CUT(n) = 0;
return(Blockgraph);
}


Local	void	create_block_nodes(Sgraph blockgraph, Slist nodelist)
{
LIST	hilfe;
Snode	node,n;
for_slist(nodelist,hilfe) {
	n = attr_data_of_type(hilfe,Snode);
 	node = make_node(blockgraph,make_attr(ATTR_DATA,NULL));
	node->y = 0;
	node->x = 0;
	node->nr = n->nr;
	node->label = strsave(n->label);
	/*printf("Nr:%d lab:%s ",node->nr,node->label);*/
} end_for_slist(nodelist,hilfe)
/*printf("\n");*/
}

Local	Snode	find_node(Sgraph g, int nr)
{
Snode	n;
for_all_nodes(g,n) {
	if(n->nr == nr)
		{
		return(n);
		}
} end_for_all_nodes(g,n);
return(NULL);
}

Local	void	create_block_edges(Sgraph g, Sgraph blockgraph)
{
Snode	n,snode,tnode;
Sedge	f;
for_all_nodes(g,n) {
	for_sourcelist(n,f) {
		if((g->directed)||(f->tnode->nr > f->snode->nr))
			{
			snode = find_node(blockgraph,f->snode->nr);
			tnode = find_node(blockgraph,f->tnode->nr);
			if(snode && tnode)
				{
				make_edge(snode,tnode,make_attr(ATTR_DATA,NULL));
	/*printf("Kante sou nr:%d lab:%s tou nr:%d lab:%s neu erzeugt\n",snode->nr,snode->label,tnode->nr,tnode->label);*/
				}
			}
	} end_for_sourcelist(n,f)
} end_for_all_nodes(g,n);
}

Local	void	create_bab_edges(Sgraph bab)
{
Snode	n,b;
Sedge	e;
for_all_nodes(bab,n) {
	/*printf("%d. Knoten im BaB\n",i++);*/
	if(BAB_NODE_IS_CUT(n) == 0)continue; /* n soll ein Cut sein */
	/*printf("Einen Cut gefunden\n");*/
	for_all_nodes(bab,b) {
		if(BAB_NODE_IS_CUT(b) == 1)continue; /* b soll ein Block sein */
		if(find_node(BAB_NODE_BLOCK(b),BAB_NODE_CUT(n)->nr))
			{
			/*printf("Cutnode nr:%d mit Block nr.%d verbunden\n",n->nr,b->nr);*/
			e = make_edge(n,b,make_attr(ATTR_DATA,NULL));
			attr_data(e) = nil;
			attr_data(e) = alloc_bab_edge_attrs();
			init_bab_edge_attrs(BAB_EDGE_ATTRS(e));
			}
	} end_for_all_nodes(bab,b);
} end_for_all_nodes(bab,n);
}


Local 	int	make_block_graphs(Sgraph g)
{
Sgraph	bab,block;
Snode	n;
LIST	liste,hilfe,nodelist;
int	i=1;
	
bab = create_bab_graph(g);
liste = CY_GRAPH_COMP(g);
for_slist(liste,hilfe) {
	nodelist = attr_data_of_type(hilfe,Slist);
	block = create_block_graph(bab);
	create_block_nodes(block,nodelist);
	create_block_edges(g,block);
	
        switch(embed(block))
        	{
                case NONPLANAR    : error("graph is nonplanar \n");
                                     return(0);
                case SELF_LOOP    : error("graph contains self-loops \n");
                                     return(0);
                case MULTIPLE_EDGE: error("graph contains multiple edges \n");
                                     return(0);
                case NO_MEM       : error("not enough memory for embed \n");
                                     return(0);
		default           : break;
               }
	/*printf("Block ist eingebettet\n");*/
	/*print_simple_graph(block);*/
} end_for_slist(liste,hilfe)
liste = CY_GRAPH_CUTS(g);
i=1;
for_slist(liste,hilfe) {
	n = attr_data_of_type(hilfe,Snode);
	create_cut_node(bab,n);
} end_for_slist(liste,hilfe)
create_bab_edges(bab);
return(1);
}

Local	int	all_edges_marked(Sgraph g)
{
Snode	n;
Sedge	f;
for_all_nodes(g,n) {
	if(BAB_NODE_LEAVE(n) == 1)continue;
	for_sourcelist(n,f) {
		if(BAB_NODE_LEAVE(OTHER_NODE(n,f)) == 1)continue;
		if(BAB_EDGE_MARKED(f) == 0)return(0);
	} end_for_sourcelist(n,f)
	for_targetlist(n,f) {
		if(BAB_NODE_LEAVE(OTHER_NODE(n,f)) == 1)continue;
		if(BAB_EDGE_MARKED(f) == 0)return(0);
	} end_for_targetlist(n,f)
} end_for_all_nodes(g,n);
return(1);
}

Local	Snode	find_node_with_one_marked_edge(Sgraph g)
{
Snode	n;
Sedge	f;
int	i;
for_all_nodes(g,n) {
	i = 0;
	if(BAB_NODE_LEAVE(n) == 1)continue;
	for_sourcelist(n,f) {
		if(BAB_NODE_LEAVE(OTHER_NODE(n,f)) == 1)continue;
		if(BAB_EDGE_MARKED(f) == 0)i++;
	} end_for_sourcelist(n,f)
	for_targetlist(n,f) {
		if(BAB_NODE_LEAVE(OTHER_NODE(n,f)) == 1)continue;
		if(BAB_EDGE_MARKED(f) == 0)i++;
	} end_for_targetlist(n,f)
	if(i == 1)return(n);
} end_for_all_nodes(g,n);
return(NULL);
}

Global 	int	caterpillar(Sgraph g)
{
Sgraph	bab;
Snode	n,cut = NULL;
Sedge	f;
LIST	liste;
int	i;
if(make_block_graphs(g) == 0)return(0); /* Bloecke von G waren nicht planar */

bab = CY_GRAPH_GRAPH(g);
for_all_nodes(bab,n) {
	i=0;
	if(BAB_NODE_IS_CUT(n) == 1)cut = n; /* merke irgendeinen cut */
	for_sourcelist(n,f) {
		i++;
	} end_for_sourcelist(n,f)
	for_targetlist(n,f) {
		i++;
	} end_for_targetlist(n,f)
	if(i == 1)
		{
		/*printf("Knoten nr:%d ist Blatt\n",n->nr);*/
		BAB_NODE_LEAVE(n) = 1;
		}
} end_for_all_nodes(bab,n);
if(cut) bab->nodes = cut;
INIT_LIST(liste);
if(all_edges_marked(bab))
	{
	/*printf("Alle Kanten sind markiert\n");*/
	return(TRUE);
	}
n = find_node_with_one_marked_edge(bab);
if(n == NULL)return(FALSE);
/*printf("Knoten mit einer markierten Kante ist Nr. %d\n",n->nr);*/
bab->nodes = n;
PUSH_NODE(liste,n);
while(!IS_EMPTY_LIST(liste))
	{
	POP_NODE(liste,n);
	i=0;
	for_sourcelist(n,f) {
		if(BAB_NODE_LEAVE(OTHER_NODE(n,f)) == 1)continue;
		if(BAB_EDGE_MARKED(f) == 0)
			{
			i++;
			BAB_EDGE_MARKED(f) = 1;
			/*printf("Neuer Knoten in Liste ist Nr.%d\n",OTHER_NODE(n,f)->nr);*/
			PUSH_NODE(liste,OTHER_NODE(n,f));
			}
	} end_for_sourcelist(n,f)
	for_targetlist(n,f) {
		if(BAB_NODE_LEAVE(OTHER_NODE(n,f)) == 1)continue;
		if(BAB_EDGE_MARKED(f) == 0)
			{
			i++;
			BAB_EDGE_MARKED(f) = 1;
		 	/*printf("Neuer Knoten in Liste ist Nr.%d\n",OTHER_NODE(n,f)->nr);*/
			PUSH_NODE(liste,OTHER_NODE(n,f));
			}
	} end_for_targetlist(n,f)
	if(i > 1) 
		{
		/*printf("Pfad verzweigt sich hier\n");*/
		return(FALSE);
		}
	}
return(TRUE);
}

/********************************************************************/
/********************** die FREE-Prozedur ***************************/
/********************************************************************/

Global	void	free_cy_graph(Sgraph g)
{
Snode 	n;
Sedge 	e;
LIST	liste,hilfe,nodelist;

CLEAR_LIST(CY_GRAPH_CUTS(g));
liste = CY_GRAPH_COMP(g);
for_slist(liste,hilfe) {
	nodelist = attr_data_of_type(hilfe,Slist);
	CLEAR_LIST(nodelist);
} end_for_slist(liste,hilfe)

free(attr_data(g));
for_all_nodes(g,n)
	{
	free(attr_data(n));
	for_sourcelist(n,e)
		{
		free(attr_data(e));
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)
}

Global	void	free_bab_graph(Sgraph g)
{
Snode 	n;
Sedge 	e;
for_all_nodes(g,n) {
	if(BAB_NODE_IS_CUT(n) == 0)free_node_and_edges(BAB_NODE_BLOCK(n));
	/*else	free_xy_cutnode(n);*/
	free(attr_data(n));
	for_sourcelist(n,e)
		{
		free(attr_data(e));
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)
}

Local	void	free_only_bab_graph(Sgraph g)
{
Snode 	n;
Sedge 	e;
for_all_nodes(g,n) {
	if(BAB_NODE_IS_CUT(n) == 0)remove_graph(BAB_NODE_BLOCK(n));
	/*else	free_xy_cutnode(n);*/
	free(attr_data(n));
	for_sourcelist(n,e)
		{
		free(attr_data(e));
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)
}

Global	void	free_cyl_node_and_edges(Sgraph g)
{
Snode	n;
if(CY_GRAPH_GRAPH(g))
	free_only_bab_graph(CY_GRAPH_GRAPH(g));
free_cy_graph(g);
for_all_nodes(g,n) {
	n->x = (int)node_get(graphed_node(n),NODE_X);
	n->y = (int)node_get(graphed_node(n),NODE_Y);
} end_for_all_nodes(g,n)
}


/********************************************************************/
/***                 END OF FILE: BLOCKMOD.C                      ***/
/********************************************************************/
