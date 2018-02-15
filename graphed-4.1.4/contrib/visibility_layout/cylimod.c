/**************************************************************************/
/***                                                                    ***/
/*** Filename: CYLIMOD.C                                                ***/
/***                                                                    ***/
/**************************************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"

#include "listen.h"
#include "blockmod.h"
#include "tarjanmod.h"
#include "dualmod.h"

Local 	int	ordinate_cyl(Sgraph g);
Global  void    free_cy_graph(Sgraph g);
Global  void    init_visigraph_attrs(Sgraph g);
Global  void    init_visiedge_attrs(Sedge e);
Global  void    free_bab_graph(Sgraph g);
Global  int     x_transform(Sgraph g, int x, int dx, int x_gridsize, int mode);
Global  int     y_transform(Sgraph g, int y, int dy, int maxordinate, int y_gridsize, int node_height, int mode);


Local	int	number_of_nodes(Sgraph g)
{
Snode	n;
int	i=0;
for_all_nodes(g,n) { i++;
} end_for_all_nodes(g,n);
return(i);
}

Local	Snode	find_node(Sgraph g, int nr)
{
Snode	n;
for_all_nodes(g,n) {
	if(n->nr == nr)return(n);
} end_for_all_nodes(g,n)
printf("Error Knotennr %d nicht gefunden !!!\n",nr);
exit(1);
}

Local	void	make_tiny_layout(Sgraph g, Snode *cut)
{
Snode 	n,m;
Sedge	e;
int	nr;
/*printf("make_tiny_layout\n");*/
init_node_and_edges(g,ANYFACE,0,ANYFACE);
for_all_nodes (g,n) {
	nr = n->nr;
	n->nr = XY_NODE_OLDNR(n);
	XY_NODE_OLDNR(n) = nr;
	XY_NODE_DATA(n) = 0;
} end_for_all_nodes(g,n)
n = g->nodes;
while(!n->slist)n = n->suc;
m = n->suc;
if(m->suc != n) printf("Error in make_tiny_layout\n");
/*e = make_edge(m,n,make_attr(ATTR_DATA,NULL));*/
XY_GRAPH_HEIGTH(g) = 2;
XY_GRAPH_WIDTH(g) = 2;
n->x = 0;
m->x = 0;
n->y = 0;
m->y = 1;
XY_NODE_START(n)->x = 0;
XY_NODE_START(n)->y = 0;
XY_NODE_END(n)->x = 2;
XY_NODE_END(n)->y = 0;
XY_NODE_START(m)->x = 0;
XY_NODE_START(m)->y = 1;
XY_NODE_END(m)->x = 2;
XY_NODE_END(m)->y = 1;
for_sourcelist(n,e) {
	XY_EDGE_START(e)->y = 0;
	XY_EDGE_END(e)->y = 1;
	XY_EDGE_START(e)->x = 1;
	XY_EDGE_END(e)->x   = 1;
	XY_EDGE_DATA(e) = 0;
} end_for_sourcelist(n,e)
/*
for_targetlist(n,e) {
	XY_EDGE_START(e)->y = 0;
	XY_EDGE_END(e)->y = 1;
	XY_EDGE_START(e)->x = 1;
	XY_EDGE_END(e)->x   = 1;
	XY_EDGE_DATA(e) = BACKEDGE;
} end_for_targetlist(n,e)*/

for_all_nodes (g,n) {
	XY_NODE_DATA(n) = 0;
} end_for_all_nodes(g,n)
if(cut[0])
	{
	n = find_node(g,cut[0]->nr);
	cut[0]->y = n->y;
	/*printf("cutnode nr.:%d y:%d\n",cut[0]->nr,n->y);*/
	}
if(cut[1])
	{
	n = find_node(g,cut[1]->nr);
	cut[1]->y = n->y;
	/*printf("cutnode nr.:%d y:%d\n",cut[1]->nr,n->y);*/
	}
}

Local	void	set_external_faces(Sgraph dual, Snode *cut)
{
Snode	n,node,m;
LIST	liste,hilfe;
int	found;
/*printf("setze F1 und F2\n");*/
if(!cut[0])
	{ /* es gibt keine CUTs */
	DUAL_NODE_TYPE(dual->nodes) = F1;
	/*printf("F1 ist dualer Knoten Nr.: %d\n",dual->nodes->nr);*/
	DUAL_NODE_TYPE(dual->nodes->suc) = F2;
	/*printf("F2 ist dualer Knoten Nr.: %d\n",dual->nodes->suc->nr);*/
	return;
	}
found = 0;
for_all_nodes(dual,n) {
	/*printf("Gesucht Cut 1 mit Nr. %d\n",cut[0]->nr);*/
	liste = DUAL_NODE_BOUND(n);
	/*printf("Face boundary : ");*/
	for_slist(liste,hilfe) {
		node = attr_data_of_type(hilfe,Snode);
		/*printf("Nr. : %d ",node->nr);*/
		if(node->nr == cut[0]->nr) found = 1;
	} end_for_slist(liste,hilfe)
	/*printf("\n");*/
	if(found)
		{
		DUAL_NODE_TYPE(n) = F1;
		/*printf("F1 ist dualer Knoten Nr.: %d\n",n->nr);*/
		break;
		}
} end_for_all_nodes(dual,n);
if(!cut[1])
	 /* es gibt nur einen CUT */
	m = cut[0];
else  	m = cut[1];

/* 	for_all_nodes(dual,n) {
		if(DUAL_NODE_TYPE(n) == FACE)
			{
			DUAL_NODE_TYPE(n) = F2;
			printf("F2 ist dualer Knoten Nr.: %d\n",n->nr);
			break;
			}
	} end_for_all_nodes(dual,n);
	return;
	}*/

found = 0;
for_all_nodes(dual,n) {
	if(DUAL_NODE_TYPE(n) != FACE)continue;	
	liste = DUAL_NODE_BOUND(n);
	for_slist(liste,hilfe) {
		node = attr_data_of_type(hilfe,Snode);
		if(node->nr == m->nr) found = 1;
	} end_for_slist(liste,hilfe)
	if(found)
		{
		DUAL_NODE_TYPE(n) = F2;
		/*printf("F2 ist dualer Knoten Nr.: %d\n",n->nr);*/
		break;
		}
} end_for_all_nodes(dual,n);
}

Local	void	search_path(Snode f1, Snode f2)
{
LIST 	queue;
Snode	n,m;
Sedge 	e;
INIT_LIST(queue);
QUEUE_NODE(queue,f1);
while(! IS_EMPTY_LIST(queue))
	{
	POP_NODE(queue,n);
	/*printf("suche Vaeter von Dual Nr. %d\n",n->nr);*/
	for_sourcelist(n,e) {
		m = OTHER_NODE(n,e);
		/*printf("z. B. Dual Nr. %d\n",m->nr);*/
		if(!DUAL_NODE_FATHER(m))
			{
			DUAL_NODE_FATHER(m) = n;
			if(DUAL_NODE_TYPE(m) == F2)
				{
				CLEAR_LIST(queue);
				/*printf("Pfad von f1 nach f2 gefunden\n");*/
				return;
				}
			QUEUE_NODE(queue,m);
			}
	} end_for_sourcelist(n,e)
	for_targetlist(n,e) {
		m = OTHER_NODE(n,e);
		/*printf("z. B. Dual Nr. %d\n",m->nr);*/
		if(!DUAL_NODE_FATHER(m))
			{
			DUAL_NODE_FATHER(m) = n;
			if(DUAL_NODE_TYPE(m) == F2)
				{
				CLEAR_LIST(queue);
				/*printf("Pfad von f1 nach f2 gefunden\n");*/
				return;
				}
			QUEUE_NODE(queue,m);
			}
	} end_for_targetlist(n,e)
	}
printf("Error !Kein Pfad von f1 nach f2\n");
exit(0);
}

Local	Sedge	find_edge_from_to(Snode x, Snode y)
{
Sedge	e;
for_sourcelist(x,e) {
	if(OTHER_NODE(x,e)->nr == y->nr)return(e);
} end_for_sourcelist(x,e)
if(x->graph->directed)
	{
	for_targetlist(x,e) {
		if(OTHER_NODE(x,e)->nr == y->nr)return(e);
	} end_for_targetlist(x,e)
	}
printf("Kante im dualen Graphen nicht mehr auffindbar\n");
exit(0);
}

Local	Sedge	find_diedge_from_to(Snode x, Snode y)
{
Sedge	e;
for_sourcelist(x,e) {
	if(OTHER_NODE(x,e)->nr == y->nr)return(e);
} end_for_sourcelist(x,e)
printf("Kante im Graphen nicht mehr auffindbar\n");
exit(0);
}
/*
Local	void	mark_edges(Sgraph g, Sedge e)
{
Snode	n;
Sedge	f;
for_all_nodes(g,n) {
	for_sourcelist(n,f) {
		if(XY_EDGE_EDGE(f) == e)XY_EDGE_DATA(f) = BACKEDGE;
	} end_for_sourcelist(n,f)
	if(g->directed)
		{
		for_targetlist(n,f) {
			if(XY_EDGE_EDGE(f) == e)XY_EDGE_DATA(f) = BACKEDGE;
		} end_for_targetlist(n,f)
		}
} end_for_all_nodes(g,n);
}
*/

Local	void	mark_back_edges(Sgraph g)
{
Sgraph	dual;
Snode	n,m,f1=NULL,f2=NULL;
Sedge	e;
dual = XY_GRAPH_GRAPH(g);
for_all_nodes(dual,n) {
	if(DUAL_NODE_TYPE(n) == F1) f1 = n;
	if(DUAL_NODE_TYPE(n) == F2) f2 = n;
} end_for_all_nodes(dual,n);
if(!f1 || !f2)
	{
	printf("F1 und F2 nicht gefunden\n");
	exit(1);
	} 
search_path(f1,f2);
n = f2;
m = f2;
do {
	n = DUAL_NODE_FATHER(n);
	e = find_edge_from_to(n,m);
	XY_EDGE_TYPE(DUAL_EDGE_EDGE(e)) = BACKEDGE;
	/*printf("Backedge von %d nach %d\n",DUAL_EDGE_EDGE(e)->snode->nr,DUAL_EDGE_EDGE(e)->tnode->nr);
	printf("Backedge markiert\n");*/
	/*mark_edges(g,e);*/
	m = DUAL_NODE_FATHER(m);
} while(n->nr != f1->nr);
}

Local 	void	orient_f1(Sgraph dual)
{
Snode	n,tar,sou;
Sedge	e;
LIST	hilfe,liste;
sou = NULL;
for_all_nodes(dual,n) {
	if(DUAL_NODE_TYPE(n) != F1) continue;
	/*printf("Orientiere jetzt F1-face nr.%d\n",n->nr);*/
	liste = DUAL_NODE_BOUND(n);
	sou = attr_data_of_type(liste->pre,Snode);
	for_slist(liste,hilfe) {
		tar = attr_data_of_type(hilfe,Snode);
		e = find_edge_from_to(sou,tar);
		XY_EDGE_SOURCE(e) = tar;
		XY_EDGE_TARGET(e) = sou;
		/*printf("Gerichtete Kanten von %d nach %d\n",XY_EDGE_SOURCE(e)->nr,XY_EDGE_TARGET(e)->nr);*/
		sou = tar;
	} end_for_slist(liste,hilfe)
	return;
} end_for_all_nodes(dual,n);
printf("F1 konnte nicht orientiert werden\n");
exit(1);
}

Local 	Snode	unmarked_face(Sgraph dual)
{
Snode	n,m = NULL;
Sedge	e;
LIST	hilfe,liste;
for_all_nodes(dual,n) {
	if(DUAL_NODE_TYPE(n) == FACE) 
		{
		liste = DUAL_NODE_BOUNDEDGE(n);
		for_slist(liste,hilfe) { /* eine Kante muss aber schon gerichtet sein */
			e = attr_data_of_type(hilfe,Sedge);
			if(XY_EDGE_SOURCE(e)||XY_EDGE_TARGET(e))return(n);
		} end_for_slist(liste,hilfe)
		}
	if(DUAL_NODE_TYPE(n) == UNORIENTED) m = n;
} end_for_all_nodes(dual,n);
if(m)	{
	for_all_nodes(dual,n) {
	if(DUAL_NODE_TYPE(n) == UNORIENTED)DUAL_NODE_TYPE(n) = FACE;
	} end_for_all_nodes(dual,n);
	return(m);
	}
/*printf("Kein freies Face gefunden\n");*/
return(NULL);
}

Local 	void	orient_face(Snode n)
{
Snode	tar,sou,m;
Sedge	e;
LIST	hilfe,liste;
int	orientation = 0,hoehe;
sou = NULL;
/*printf("Orientiere jetzt face nr.%d\n",n->nr);*/
DUAL_NODE_TYPE(n) = MARKED;
liste = DUAL_NODE_BOUND(n);
tar = attr_data_of_type(liste->pre,Snode);
for_slist(liste,hilfe) {
	sou = tar;
	tar = attr_data_of_type(hilfe,Snode);
	e = find_edge_from_to(sou,tar);
	if((XY_EDGE_SOURCE(e) == NULL)&&(XY_EDGE_TARGET(e) == NULL))continue;
	if((XY_EDGE_SOURCE(e) == sou)&&(XY_EDGE_TARGET(e) == tar))
		if((orientation == 0)||(orientation == 1))orientation = 1;
		else	{
			printf("Orientierung widerspruechlich\n");
			exit(0);
			}
	if((XY_EDGE_SOURCE(e) == tar)&&(XY_EDGE_TARGET(e) == sou))
		if((orientation == 0)||(orientation == 2))orientation = 2;
		else	{
			printf("Orientierung widerspruechlich\n");
			exit(0);
			}
} end_for_slist(liste,hilfe)
if(orientation == 0)
	{
	printf("Keine Orientierung gefunden\n");
	exit(0);
	}
/*if(orientation == 2)
	printf("Orientierung laeuft im Uhrzeigersinn\n");
if(orientation == 1)
	printf("Orientierung laeuft gegen den Uhrzeigersinn\n");
*/
sou = NULL;
liste = DUAL_NODE_BOUND(n);
sou = attr_data_of_type(liste->pre,Snode);
for_slist(liste,hilfe) {
	tar = attr_data_of_type(hilfe,Snode);
	e = find_edge_from_to(sou,tar);
	if(XY_EDGE_SOURCE(e))
		{
		sou = tar;
		continue;
		}
	if(orientation == 2)
		{
		XY_EDGE_SOURCE(e) = sou;
		XY_EDGE_TARGET(e) = tar;
		XY_EDGE_TYPE(e) = TEST;
		/*printf("Gerichtete Kanten von %d nach %d\n",sou->nr,tar->nr);*/
	} else 	{
		XY_EDGE_SOURCE(e) = tar;
		XY_EDGE_TARGET(e) = sou;
		XY_EDGE_TYPE(e) = TEST;
		/*printf("Gerichtete Kanten von %d nach %d\n",tar->nr,sou->nr);*/
		}
	sou = tar;
} end_for_slist(liste,hilfe)

hoehe = ordinate_cyl(DUAL_GRAPH_GRAPH(n->graph));
if( (hoehe == -1)||(hoehe == 1))
	{
	/*printf("Schleife entdeckt\n");*/
	DUAL_NODE_TYPE(n) = UNORIENTED;
	liste = DUAL_NODE_BOUND(n);
	sou = attr_data_of_type(liste->pre,Snode);
	for_slist(liste,hilfe) {
		tar = attr_data_of_type(hilfe,Snode);
		e = find_edge_from_to(sou,tar);
		if(XY_EDGE_TYPE(e) == TEST)
			{
			/*printf("Gerichtete Kanten von %d nach %d ist jetzt wieder ungerichtet\n",tar->nr,sou->nr);*/
			XY_EDGE_SOURCE(e) = NULL;
			XY_EDGE_TARGET(e) = NULL;
			}
		sou = tar;
	} end_for_slist(liste,hilfe)
	}

liste = DUAL_NODE_BOUND(n);
sou = attr_data_of_type(liste->pre,Snode);
for_slist(liste,hilfe) {
	tar = attr_data_of_type(hilfe,Snode);
	e = find_edge_from_to(sou,tar);
	XY_EDGE_TYPE(e) = NORMAL;
	sou = tar;
} end_for_slist(liste,hilfe)

for_all_nodes(DUAL_GRAPH_GRAPH(n->graph),m) {
	m->y = 0;
} end_for_all_nodes(DUAL_GRAPH_GRAPH(n->graph),m)
}

Local 	void	orient_dual_graph(Sgraph g)
{
Snode	n;
Sedge	e;
Sgraph	dual;
dual = XY_GRAPH_GRAPH(g);
for_all_nodes(dual,n) {
	for_sourcelist(n,e) {
	    if(XY_NODE_OLDNR(XY_EDGE_SOURCE(DUAL_EDGE_EDGE(e))) >
			XY_NODE_OLDNR(XY_EDGE_TARGET(DUAL_EDGE_EDGE(e))))
		{
		DUAL_EDGE_SOURCE(e) = XY_EDGE_LNODE(DUAL_EDGE_EDGE(e));
		DUAL_EDGE_TARGET(e) = XY_EDGE_RNODE(DUAL_EDGE_EDGE(e));
		}
	    if(XY_NODE_OLDNR(XY_EDGE_SOURCE(DUAL_EDGE_EDGE(e))) <
			XY_NODE_OLDNR(XY_EDGE_TARGET(DUAL_EDGE_EDGE(e))))
		{
		DUAL_EDGE_SOURCE(e) = XY_EDGE_RNODE(DUAL_EDGE_EDGE(e));
		DUAL_EDGE_TARGET(e) = XY_EDGE_LNODE(DUAL_EDGE_EDGE(e));
		}
	    /*if((XY_NODE_OLDNR(DUAL_EDGE_EDGE(e)->tnode) == 1)&&
		(XY_NODE_OLDNR(DUAL_EDGE_EDGE(e)->snode) == XY_GRAPH_MAX_NR(g)))
		{
		printf("duale ST-Kante umgedreht\n");
		m = DUAL_EDGE_SOURCE(e);
		DUAL_EDGE_SOURCE(e) = DUAL_EDGE_TARGET(e);
		DUAL_EDGE_TARGET(e) = m;
		}
	    if((XY_NODE_OLDNR(DUAL_EDGE_EDGE(e)->snode) == 1)&&
		(XY_NODE_OLDNR(DUAL_EDGE_EDGE(e)->tnode) == XY_GRAPH_MAX_NR(g)))
		{
		printf("duale ST-Kante umgedreht\n");
		m = DUAL_EDGE_SOURCE(e);
		DUAL_EDGE_SOURCE(e) = DUAL_EDGE_TARGET(e);
		DUAL_EDGE_TARGET(e) = m;
		}*/
	} end_for_sourcelist(n,e)
} end_for_all_nodes(dual,n);

for_all_nodes(dual,n) {
	for_sourcelist(n,e) {
		if((DUAL_EDGE_SOURCE(e) == NULL)&&(DUAL_EDGE_TARGET(e) == NULL))
			{
			printf("Im dualen Graphen sind nicht alle Kanten orientiert\n");
			exit(0);
		} /* else 
			printf("Gerichtete duale Kanten von %d nach %d\n",DUAL_EDGE_SOURCE(e)->nr,DUAL_EDGE_TARGET(e)->nr);*/
	} end_for_sourcelist(n,e)
} end_for_all_nodes(dual,n);
}

Local 	int	ordinate_cyl(Sgraph g)
{
Snode 	n,m;
Sedge 	e;
int   	max_y = 0,kanten;
LIST 	queue,liste,hilfe;
INIT_LIST(queue);
for_all_nodes(g,n) {
	n->y = 0;
	kanten = 0;
	for_sourcelist(n,e) {
		if(XY_EDGE_TYPE(e) == BACKEDGE)
			{
			/* printf("Backedge skipped\n"); */
			continue;
			}
		if(n == XY_EDGE_TARGET(e))kanten++;
	} end_for_sourcelist(n,e)
	for_targetlist(n,e) {
		if(XY_EDGE_TYPE(e) == BACKEDGE)
			{
			/* printf("Backedge skipped\n"); */
			continue;
			}
		if(n == XY_EDGE_TARGET(e))kanten++;
	} end_for_targetlist(n,e)
	if(kanten == 0)
		{
		n->y = 0;
		QUEUE_NODE(queue,n);
		/*printf("Knotennr. %d mit Hoehe 0 \n",n->nr);*/
		}
} end_for_all_nodes(g,n);
while(! IS_EMPTY_LIST(queue))
	{
	POP_NODE(queue,n);
	if(n->y > number_of_nodes(g)) 
		{
		/*printf("Schleife entdeckt\n");*/
		return(-1);
		}
	liste = XY_NODE_EDGES(n);
	for_slist(liste,hilfe) {
		e = attr_data_of_type(hilfe,Sedge);
		m = OTHER_NODE(n,e);
		/*printf("Othernode %d \n",m->nr);*/
		if(XY_EDGE_TYPE(e) == BACKEDGE)
			{
			/*printf("Backedge skipped\n"); */
			continue;
			}
		if((n == XY_EDGE_SOURCE(e))&&(n->y >= m->y))
			{
			m->y = n->y + 1;
			max_y = m->y;
			QUEUE_NODE(queue,m);
			/*printf("Knotennr. %d mit Hoehe %d \n",m->nr,m->y);*/
			}
	} end_for_slist(liste,hilfe)
	}
/*printf("Graphhoehe = %d\n",max_y+1);*/
return(max_y+1);
}

Local 	int	ordinate_cyl_dual(Sgraph dual)
{
Snode 	n,m;
Sedge 	e;
int   	max_y = 0,kanten;
LIST 	queue;
INIT_LIST(queue);
for_all_nodes(dual,n) {
	n->y = 0;
	kanten = 0;
	for_sourcelist(n,e) {
		if(n == DUAL_EDGE_TARGET(e))kanten++;
	} end_for_sourcelist(n,e)
	for_targetlist(n,e) {
		if(n == DUAL_EDGE_TARGET(e))kanten++;
	} end_for_targetlist(n,e)
	if(kanten == 0)
		{
		n->y = 0;
		QUEUE_NODE(queue,n);
		/*printf("Knotennr. %d mit Hoehe 0 \n",n->nr);*/
		}
} end_for_all_nodes(dual,n);
while(! IS_EMPTY_LIST(queue))
	{
	POP_NODE(queue,n);
	for_sourcelist(n,e) {
		m = OTHER_NODE(n,e);
		if((n == DUAL_EDGE_SOURCE(e))&&(n->y >= m->y))
			{
			m->y = n->y + 1;
			max_y = m->y;
			QUEUE_NODE(queue,m);
			/*printf("Knotennr. %d mit Hoehe %d \n",m->nr,m->y);*/
			}
	} end_for_sourcelist(n,e)
	for_targetlist(n,e) {
		m = OTHER_NODE(n,e);
		if((n == DUAL_EDGE_SOURCE(e))&&(n->y >= m->y))
			{
			m->y = n->y + 1;
			max_y = m->y;
			QUEUE_NODE(queue,m);
			/*printf("Knotennr. %d mit Hoehe %d \n",m->nr,m->y);*/
			}
	} end_for_targetlist(n,e)
	}
/*printf("Dual-Graphhoehe = %d\n",max_y+1); */
return(max_y+1);}

Local	int	make_layout(Sgraph g, Snode *cut)
{
Sgraph	dual;
Snode	n,left,right;
Sedge	e;
int	max,min,x,nr;
/*printf("make_layout\n");*/
init_node_and_edges(g,ANYFACE,0,ANYEDGE);
make_dual_graph(g);
dual = XY_GRAPH_GRAPH(g);
for_all_nodes (g,n) {
	nr = n->nr;
	n->nr = XY_NODE_OLDNR(n);
	XY_NODE_OLDNR(n) = nr;
	XY_NODE_DATA(n) = 0;
} end_for_all_nodes(g,n)


set_external_faces(dual,cut);
mark_back_edges(g);

orient_f1(dual);
while((n = unmarked_face(dual))) orient_face(n);
orient_dual_graph(g);
XY_GRAPH_HEIGTH(g) = ordinate_cyl(g);
XY_GRAPH_WIDTH(g) = ordinate_cyl_dual(dual);

for_all_nodes (g,n) {
	max = 0;
	min = 2*XY_GRAPH_WIDTH(g);
	for_sourcelist(n,e) {
	    	if(XY_NODE_OLDNR(e->snode) >
			XY_NODE_OLDNR(e->tnode))
			{
			left = XY_EDGE_LNODE(e);
			right = XY_EDGE_RNODE(e); 
	   	} else {
			left = XY_EDGE_RNODE(e);
			right = XY_EDGE_LNODE(e); 
			}
	    	x =  right->y + left->y;
		/*message("Kante mit left->y %d und right->y %d\n",left->y,right->y); */
            	max = maximum(max,2*right->y);
            	min = minimum(min,2*left->y);

	    	XY_EDGE_START(e)->y = n->y;
		XY_EDGE_END(e)->y = e->tnode->y;

		XY_EDGE_START(e)->x = x;
		XY_EDGE_END(e)->x   = x;
		/*if(XY_EDGE_TYPE(e) == BACKEDGE)message("Backedge-");
		message("Kante von (%d,%d) nach (%d,%d)\n",
			XY_EDGE_START(e)->x,XY_EDGE_START(e)->y,
			XY_EDGE_END(e)->x,XY_EDGE_END(e)->y);
		message("lb =  von  %s nach %s\n",
			e->snode->label,e->tnode->label);
		message("nr =  von  %d nach %d\n",e->snode->nr,e->tnode->nr);*/
                /*message("dual  von  %d nach %d\n",
			XY_EDGE_EDGE(e)->snode->nr,XY_EDGE_EDGE(e)->tnode->nr);*/
	} end_for_sourcelist(n,e)
	if(g->directed)
	    {
	    for_targetlist(n,e) {
	    	if(XY_NODE_OLDNR(e->snode) >
			XY_NODE_OLDNR(e->tnode))
			{
			left = XY_EDGE_RNODE(e);
			right = XY_EDGE_LNODE(e); 
	    	} else {
			left = XY_EDGE_LNODE(e);
			right = XY_EDGE_RNODE(e); 
			}
		/*message("Kante mit left->y %d und right->y %d\n",left->y,right->y);*/
            	max = maximum(max,2*right->y);
            	min = minimum(min,2*left->y);
	    	} end_for_targetlist(n,e)
	    }
	/*if((min % 2) == 1)min--;*/
	XY_NODE_START(n)->x = min;
	XY_NODE_START(n)->y = n->y;
	/*if((max % 2) == 1)max++;*/
	XY_NODE_END(n)->x = max;
	XY_NODE_END(n)->y = n->y;
	n->x = min;

	/*message("Knoten %s von (%d,%d) nach (%d,%d)\n\n",n->label,
		XY_NODE_START(n)->x,XY_NODE_START(n)->y,
		XY_NODE_END(n)->x,XY_NODE_END(n)->y);*/
} end_for_all_nodes(g,n)

if(cut[0])
	{
	n = find_node(g,cut[0]->nr);
	XY_NODE_DATA(cut[0]) = n->y;
	/*printf("cutnode nr.:%d y:%d\n",cut[0]->nr,n->y);*/
	}
if(cut[1])
	{
	n = find_node(g,cut[1]->nr);
	XY_NODE_DATA(cut[1]) = n->y;
	/*printf("cutnode nr.:%d y:%d\n",cut[1]->nr,n->y);*/
	}
/*message("make_layout fertig\n");*/
return(XY_GRAPH_HEIGTH(g));
}

Local	void	rotate(Sgraph g, Snode *cut, int maxheight, int *X)
{
Snode	n;
Sedge	e;
int	offset;
/*printf("begin rotating Start x = %d\n",(*X));
printf("Cutnode nr.: %d\n",cut[0]->nr);*/
n = find_node(g,cut[0]->nr);
/*printf("cutnode nr.:%d y:%d im Block nr: %d y:%d\n",cut[0]->nr,cut[0]->y,n->nr,n->y);
printf("cutnode nr.%d von (%d,%d ) nach (%d,%d)\n",cut[0]->nr,
XY_NODE_START(cut[0])->x,XY_NODE_START(cut[0])->y,XY_NODE_END(cut[0])->x,XY_NODE_END(cut[0])->y);*/
offset = cut[0]->y - n->y;
if(cut[0]->y == -1)
	{
	/*printf("first Block offset = 0\n");*/
	offset = 0;
	}
/*printf("offset %d\n",offset);*/
for_all_nodes(g,n) {
	n->y = n->y + offset;
	XY_NODE_START(n)->y = n->y;
	XY_NODE_END(n)->y = n->y;
	n->x = n->x + (*X);
	XY_NODE_START(n)->x = XY_NODE_START(n)->x + (*X);
	XY_NODE_END(n)->x = XY_NODE_END(n)->x +(*X);
} end_for_all_nodes(g,n)
for_all_nodes(g,n) {
	for_sourcelist(n,e) {
	    	XY_EDGE_START(e)->y = n->y;
		XY_EDGE_END(e)->y = e->tnode->y;
	    	XY_EDGE_START(e)->x = XY_EDGE_START(e)->x + (*X);
		XY_EDGE_END(e)->x = XY_EDGE_END(e)->x + (*X);
		if(XY_EDGE_START(e)->y < 0)
			{
			XY_EDGE_START(e)->y = XY_EDGE_START(e)->y + maxheight;
			if(XY_EDGE_TYPE(e) == BACKEDGE)
				XY_EDGE_TYPE(e) = NORMAL;
			else XY_EDGE_TYPE(e) = BACKEDGE;
			}
		if(XY_EDGE_END(e)->y < 0)
			{
			XY_EDGE_END(e)->y = XY_EDGE_END(e)->y + maxheight;
			if(XY_EDGE_TYPE(e) == BACKEDGE)
				XY_EDGE_TYPE(e) = NORMAL;
			else XY_EDGE_TYPE(e) = BACKEDGE;
			}
		if(XY_EDGE_START(e)->y > maxheight)
			{
			XY_EDGE_START(e)->y = XY_EDGE_START(e)->y - maxheight;
			if(XY_EDGE_TYPE(e) == BACKEDGE)
				XY_EDGE_TYPE(e) = NORMAL;
			else XY_EDGE_TYPE(e) = BACKEDGE;
			}
		if(XY_EDGE_END(e)->y >maxheight)
			{
			XY_EDGE_END(e)->y = XY_EDGE_END(e)->y - maxheight;
			if(XY_EDGE_TYPE(e) == BACKEDGE)
				XY_EDGE_TYPE(e) = NORMAL;
			else XY_EDGE_TYPE(e) = BACKEDGE;
			}
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)
for_all_nodes(g,n) {
	if(n->y < 0) n->y = n->y + maxheight;
	if(n->y > maxheight) n->y = n->y - maxheight;
	XY_NODE_START(n)->y = n->y;
	XY_NODE_END(n)->y = n->y;
} end_for_all_nodes(g,n)

/*for_all_nodes (g,n) {
	for_sourcelist(n,e) {
		if(XY_EDGE_TYPE(e) == BACKEDGE)printf("Backedge-");
		printf("Kante von (%d,%d) nach (%d,%d)\n",
			XY_EDGE_START(e)->x,XY_EDGE_START(e)->y,
			XY_EDGE_END(e)->x,XY_EDGE_END(e)->y);
		printf("lb =  von  %s nach %s\n",
			e->snode->label,e->tnode->label);
		printf("nr =  von  %d nach %d\n",e->snode->nr,e->tnode->nr);
	} end_for_sourcelist(n,e)
	printf("Knoten nr.:%d von (%d,%d) nach (%d,%d)\n\n",n->nr,
		XY_NODE_START(n)->x,XY_NODE_START(n)->y,
		XY_NODE_END(n)->x,XY_NODE_END(n)->y);
} end_for_all_nodes(g,n)*/
if(cut[0])
	{
	n = find_node(g,cut[0]->nr);
	if((XY_NODE_END(cut[0])->y == 0)&&(XY_NODE_END(cut[0])->x == 0))
		{
		/*printf("first Block with cut nr.:%d\n",n->nr);*/
		XY_NODE_START(cut[0])->x = XY_NODE_START(n)->x;
		XY_NODE_START(cut[0])->y = XY_NODE_START(n)->y;
		XY_NODE_END(cut[0])->x = XY_NODE_END(n)->x;
		XY_NODE_END(cut[0])->y = XY_NODE_END(n)->y;
		cut[0]->x = n->x;
		}
	else	{
		XY_NODE_START(cut[0])->x = cut[0]->x;
		XY_NODE_START(cut[0])->y = cut[0]->y;
		/*XY_NODE_START(cut[0])->x = minimum(XY_NODE_START(cut[0])->x,
						XY_NODE_START(n)->x);
		XY_NODE_START(cut[0])->y = minimum(XY_NODE_START(cut[0])->y,
						XY_NODE_START(n)->y);*/
		XY_NODE_END(cut[0])->x = maximum(XY_NODE_END(cut[0])->x,
						XY_NODE_END(n)->x);
		XY_NODE_END(cut[0])->y = maximum(XY_NODE_END(cut[0])->y,
						XY_NODE_END(n)->y);
		}
	/*printf("Cutnode1 nr.:%d extended von (%d,%d) nach (%d,%d)\n\n",n->nr,
		XY_NODE_START(cut[0])->x,XY_NODE_START(cut[0])->y,
		XY_NODE_END(cut[0])->x,XY_NODE_END(cut[0])->y);*/
	cut[0]->y = n->y;
	}
if(cut[1])
	{
	n = find_node(g,cut[1]->nr);
	cut[1]->y = n->y;
	cut[1]->x = n->x;
	/*printf("first Block with cut nr.:%d\n",n->nr);*/
	XY_NODE_START(cut[1])->x = XY_NODE_START(n)->x;
	XY_NODE_START(cut[1])->y = XY_NODE_START(n)->y;
	XY_NODE_END(cut[1])->x = XY_NODE_END(n)->x;
	XY_NODE_END(cut[1])->y = XY_NODE_END(n)->y;
	/*printf("Cutnode2 nr.:%d extended von (%d,%d) nach (%d,%d)\n\n",n->nr,
		XY_NODE_START(cut[1])->x,XY_NODE_START(cut[1])->y,
		XY_NODE_END(cut[1])->x,XY_NODE_END(cut[1])->y);*/
	}
(*X) = (*X) + 2*XY_GRAPH_WIDTH(g);
/*printf("rotating ended\n");*/
}


Global	void	cylindric_draw(Sgraph g, int verbose)
{
Sgraph	bab,h;
Snode 	n,cut[2],block,o,m,sou,tar;
Sedge	e,f;
int	i,height,maxheight = 0,x = 0;
bab = CY_GRAPH_GRAPH(g);

for_all_nodes(bab,n) {
	for_sourcelist(n,e) { 
		BAB_EDGE_MARKED(e) = 0;
	} end_for_sourcelist(n,e)
} end_for_all_nodes(bab,n)

for_all_nodes(bab,n) {
	if(BAB_NODE_IS_CUT(n) == 1)continue; /* n soll ein Block sein */
	cut[0] = NULL;
	cut[1] = NULL;
	i = 0;
	/*printf("Working with Block nr.%d\n",n->nr);*/
	for_targetlist(n,f) {
		/*printf("Connected to Cut nr.%d\n",f->snode->nr);*/
		if(i>1)
			{
			printf("Error in cycldraw: no path\n");
			exit(0);
			}
		cut[i] = BAB_NODE_CUT(f->snode);
		if(BAB_NODE_IS_CUT(f->snode) != 1) 
			{
			printf("Error in cycldraw 1: block to block edge\n");
			exit(0);
			}
		i++;
	} end_for_targetlist(n,f)
	/*printf("%d cutnodes found\n",i);*/
	if(number_of_nodes(BAB_NODE_BLOCK(n)) == 2)
		{
		make_tiny_layout(BAB_NODE_BLOCK(n),cut);
		if(maxheight < 1)maxheight = 1;
	} else	{
		height = make_layout(BAB_NODE_BLOCK(n),cut);
		maxheight = maximum(maxheight,height);
		}
} end_for_all_nodes(bab,n)
/*printf("Maxheight : %d\n",maxheight);*/

/*------------------------------------------------------------------*/
/* jetzt muessen alle Bloecke rotiert werden */
n = bab->nodes;
if(n->suc == n)
	{
	x = (2 * (XY_GRAPH_WIDTH(BAB_NODE_BLOCK(n)))) - 1;
	n = NULL;
	/*printf("Der BAB enthaelt nur einen Knoten\n");*/
	}
while(n)
	{
	if(BAB_NODE_IS_CUT(n) == 0)
		{
		printf("Cut expected !\n");
		exit(1);
		}
	cut[0] = BAB_NODE_CUT(n);
	block = NULL;
	for_sourcelist(n,e) { /* zuerst alle Blaetter */
		if(BAB_NODE_LEAVE(OTHER_NODE(n,e)) == 0)
			{
			if(BAB_EDGE_MARKED(e) == 1)continue;
			block = OTHER_NODE(n,e);
			continue;
			}
		cut[1] = NULL;
		/*printf("rotierte eine Blatt\n");*/
		rotate(BAB_NODE_BLOCK(OTHER_NODE(n,e)),cut,maxheight,&x);
	} end_for_sourcelist(n,e)
	cut[1] = NULL;
	o = NULL;
	if(block)
		{
		/*printf("Block mit 2 Cuts gefunden\n");*/
		for_targetlist(block,e) {
			if(OTHER_NODE(block,e) == n)continue;
			BAB_EDGE_MARKED(e) = 1;
			o = OTHER_NODE(block,e);
			cut[1] = BAB_NODE_CUT(OTHER_NODE(block,e));
		} end_for_targetlist(block,e)
		rotate(BAB_NODE_BLOCK(block),cut,maxheight,&x);
		}
	n = o;
	}

/*------------------------------------------------------------------*/
/* die Strukturen werden nun auf den Originalgraphen uebertragen */
bab = CY_GRAPH_GRAPH(g);
for_all_nodes(bab,n) {
	if(BAB_NODE_IS_CUT(n) == 1)
		{
		m = BAB_NODE_CUT(n);
		for_sourcelist(n,e) {
			h = BAB_NODE_BLOCK(OTHER_NODE(n,e));
			if(BAB_NODE_IS_CUT(OTHER_NODE(n,e)) != 0) 
				{
				printf("Error in cycldraw 2: block to block edge\n");
				exit(0);
				}
			o = find_node(h,m->nr);
			XY_NODE_START(o)->x = XY_NODE_START(m)->x;
			XY_NODE_START(o)->y = XY_NODE_START(m)->y;
			XY_NODE_END(o)->x = XY_NODE_END(m)->x;
			XY_NODE_END(o)->y = XY_NODE_END(m)->y;
			/*printf("Daten von Cut nr.%d auf Nachbarblock uebertragen\n",m->nr);*/
		} end_for_sourcelist(n,e)
		}
} end_for_all_nodes(bab,n)

free_cy_graph(g);
init_visigraph_attrs(g);
for_all_nodes(g,n)  {
	init_visinode_attrs(n);
	for_sourcelist(n,e)
		{
		init_visiedge_attrs(e);
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)

maxheight = 0;
for_all_nodes(bab,n) {
	if(BAB_NODE_IS_CUT(n) == 0)
		{
		h = BAB_NODE_BLOCK(n);
		for_all_nodes(h,m) {
			o = find_node(g,m->nr);
			XY_NODE_START(o)->x = XY_NODE_START(m)->x;
			XY_NODE_START(o)->y = XY_NODE_START(m)->y;
			XY_NODE_END(o)->x = XY_NODE_END(m)->x;
			XY_NODE_END(o)->y = XY_NODE_END(m)->y;
			o->x = m->x;
			o->y = m->y;
			maxheight = maximum(maxheight,o->y);
			/*printf("Daten von Knoten nr.%d auf Originalknoten nr.%d uebertragen\n",m->nr,o->nr);*/
		} end_for_all_nodes(h,m)
		for_all_nodes(h,m) {
			for_sourcelist(m,e) {
				sou = find_node(g,e->snode->nr);
				tar = find_node(g,e->tnode->nr);
				f = find_diedge_from_to(sou,tar);
				XY_EDGE_START(f)->x = XY_EDGE_START(e)->x;
				XY_EDGE_START(f)->y = XY_EDGE_START(e)->y;
				XY_EDGE_END(f)->x = XY_EDGE_END(e)->x;
				XY_EDGE_END(f)->y = XY_EDGE_END(e)->y;
				XY_EDGE_TYPE(f) = XY_EDGE_TYPE(e);
				/*printf("Daten von Kante sou.%d tar.%d auf Originalkante sou.%d tar.%d uebertragen\n",e->snode->nr,e->tnode->nr,f->snode->nr,f->tnode->nr);*/
				if(!g->directed)
					{
					XY_EDGE_START(f->tsuc)->x = XY_EDGE_END(e)->x;
					XY_EDGE_START(f->tsuc)->y = XY_EDGE_END(e)->y;
					XY_EDGE_END(f->tsuc)->x = XY_EDGE_START(e)->x;
					XY_EDGE_END(f->tsuc)->y = XY_EDGE_START(e)->y;
					XY_EDGE_TYPE(f->tsuc) = XY_EDGE_TYPE(e);
					}
			} end_for_sourcelist(m,e)
		} end_for_all_nodes(h,m)
		}
} end_for_all_nodes(bab,n)

free_bab_graph(bab);
XY_GRAPH_WIDTH(g) = x;
XY_GRAPH_HEIGTH(g) = maxheight + 1;
if(verbose)
	{
	message("graph is %d units high\n",XY_GRAPH_HEIGTH(g));
	message("graph is %d units width\n",(XY_GRAPH_WIDTH(g)-1)/2);
	message("graph area is %d units\n",((XY_GRAPH_WIDTH(g)-1)/2)*XY_GRAPH_HEIGTH(g));
	}
/*printf("Ende von cylindric draw\n");*/
}


Local	int	nr_of_edges(Sgraph g)
{
Snode	n;
Sedge	e;
int	nr = 0;
for_all_nodes(g,n)  {
	for_sourcelist(n,e) {
		if((g->directed)||(e->snode->nr > e->tnode->nr))
			nr++;
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)
return(nr);
}

Local	int	nr_of_nodes(Sgraph g)
{
Snode	n;
int	nr = 0;
for_all_nodes(g,n)  {
	nr++;
} end_for_all_nodes(g,n)
return(nr);
}


Global void cylindric_set(Sgraph g, int x_gridsize, int y_gridsize, int node_height, int verbose)
{
Snode 		n;
Sedge 		e;
int		xnew,ynew;
int		dx,dy;
int		node_width;
int		gesamtknotenbreite = 0;
int		maxknotenbreite = 0;
int		gesamtkantenlaenge = 0;
int		maxkantenlaenge = 0;
Edgeline	eline;
x_transform(g,0,0,x_gridsize,SET);
y_transform(g,0,0,XY_GRAPH_HEIGTH(g),y_gridsize,node_height,SET);
for_all_nodes(g,n)  {
	dx = XY_NODE_END(n)->x - XY_NODE_START(n)->x;
	xnew = x_transform(g,XY_NODE_START(n)->x,dx,0,NODE);
	ynew = y_transform(g,XY_NODE_START(n)->y,0,0,0,0,NODE);
	/*message("Node at %d %d\n",xnew,ynew);*/
	n->x = xnew;
	n->y = ynew;
	if(dx < 1) dx = 1;
	gesamtknotenbreite = gesamtknotenbreite + dx/2;
	maxknotenbreite = maximum(maxknotenbreite,dx/2);
	node_width = dx*2*(x_gridsize/2);
	if(!(node_width % 2))node_width++;
	node_set(graphed_node(n),
	NODE_SIZE,node_width,node_height,
	NODE_NLP,NODELABEL_MIDDLE,0);
	node_set(graphed_node(n),
	NODE_TYPE,find_nodetype("#box"),0);
	node_set(graphed_node(n),
	/*NODE_NEI,SPECIAL_NODE_EDGE_INTERFACE,*/
	NODE_NEI,NO_NODE_EDGE_INTERFACE,
	NODE_POSITION,xnew,ynew,0);
} end_for_all_nodes(g,n)

if(verbose)
	{
	message("Total node width : %d\n",gesamtknotenbreite);
	message("Average node width : %f\n",(double)gesamtknotenbreite/nr_of_nodes(g));
	message("Maximal node width : %d\n",maxknotenbreite);
	}

for_all_nodes(g,n)  {
	for_sourcelist(n,e)  {
		if(n->nr == ((Snode)sedge_real_source(e))->nr)
			{
			dy = XY_EDGE_END(e)->y - XY_EDGE_START(e)->y;
			eline = (Edgeline)edge_get(graphed_edge(e),EDGE_LINE);
			free_edgeline(eline);
			xnew = (int)node_get(graphed_node(e->snode),NODE_X);
			ynew = (int)node_get(graphed_node(e->snode),NODE_Y);
			/*eline = new_edgeline(xnew,ynew);
			message("Edge from %d %d",xnew,ynew);*/
			xnew = x_transform(g,XY_EDGE_START(e)->x,0,0,EDGE);
			if(dy > 0)
				ynew = y_transform(g,XY_EDGE_START(e)->y,0,0,
				0,0,EDGE);
			else	ynew = y_transform(g,XY_EDGE_END(e)->y,-dy,0,
				0,0,EDGE);
			/*message("to %d %d",xnew,ynew);
			eline = add_to_edgeline(eline,xnew,ynew);*/
			eline = new_edgeline(xnew,ynew);
			if(XY_EDGE_TYPE(e) == BACKEDGE)
				{
				edge_set(graphed_edge(e),EDGE_TYPE,find_edgetype("#dashed"),0);
				xnew = xnew + x_gridsize/2;
				if(dy > 0) ynew = ynew - y_gridsize/2;
				else ynew = ynew + y_gridsize/2;
				eline = add_to_edgeline(eline,xnew,ynew);
			} else  edge_set(graphed_edge(e),EDGE_TYPE,find_edgetype("#solid"),0);
			xnew = x_transform(g,XY_EDGE_START(e)->x,0,0,EDGE);
			if(dy > 0)
				ynew = y_transform(g,XY_EDGE_START(e)->y,dy,0,
				0,0,EDGE);
			else	ynew = y_transform(g,XY_EDGE_END(e)->y,0,0,
				0,0,EDGE);
			/*message(" to %d %d",xnew,ynew);*/
			if(XY_EDGE_TYPE(e) == BACKEDGE)
				{
				xnew = xnew + x_gridsize/2;
				if(dy > 0) ynew = ynew + y_gridsize/2;
				else ynew = ynew - y_gridsize/2;
				eline = add_to_edgeline(eline,xnew,ynew);
				}
			xnew = x_transform(g,XY_EDGE_START(e)->x,0,0,EDGE);
			if(dy > 0)
				ynew = y_transform(g,XY_EDGE_START(e)->y,dy,0,
				0,0,EDGE);
			else	ynew = y_transform(g,XY_EDGE_END(e)->y,0,0,
				0,0,EDGE);
			/*message(" to %d %d",xnew,ynew);*/
			eline = add_to_edgeline(eline,xnew,ynew);
			xnew = (int)node_get(graphed_node(e->tnode),NODE_X);
			ynew = (int)node_get(graphed_node(e->tnode),NODE_Y);
			/*message(" to %d %d\n",xnew,ynew);
			eline = add_to_edgeline(eline,xnew,ynew);*/
			eline = eline->suc;
			edge_set(graphed_edge(e),EDGE_LINE,eline,0);
			dy = maximum(dy,-dy);
			gesamtkantenlaenge = gesamtkantenlaenge + dy;
			maxkantenlaenge = maximum(maxkantenlaenge,dy);
			}
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)

if(verbose)
	{
	message("Total edge length : %d\n",gesamtkantenlaenge);
	message("Average edge length : %f\n",(double)gesamtkantenlaenge/nr_of_edges(g));
	message("Maximal edge lentgth : %d\n",maxkantenlaenge);
	}

}
/**************************************************************************/
/***                      END OF FILE: CYLIMOD.C                        ***/
/**************************************************************************/
