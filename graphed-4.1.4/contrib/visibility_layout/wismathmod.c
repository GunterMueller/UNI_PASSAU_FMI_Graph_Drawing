/**************************************************************************/
/***                                                                    ***/
/*** Filename: WISMATHMOD.C                                             ***/
/***                                                                    ***/
/**************************************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"

#include "listen.h"
#include "tarjanmod.h"

Global	void	remove_dummy_edge(Sedge e)
{
XY_NODE_EDGES(e->snode) = DELETE_EDGE(XY_NODE_EDGES(e->snode),e);
XY_NODE_EDGES(e->tnode) = DELETE_EDGE(XY_NODE_EDGES(e->tnode),e);
remove_edge(e);
}
Global	void	wismath_draw(Sgraph g, int verbose)
      	  
   	        

	/* compute x,y,dx,dy for nodes and edges */
{
Snode n,m;
Sedge e,f;
int   y = 1,maxx = 0,quot;
double	x = 0.0;
double  qmax = 1.0;
LIST queue,liste,hilfe,new_list;
if(XY_GRAPH_DUMMY(g)) remove_dummy_edge(XY_GRAPH_DUMMY(g));
INIT_LIST(queue);
n = g->nodes;
liste = XY_NODE_EDGES(n);
XY_NODE_DSTART(n)->x = 0.0;
XY_NODE_WIDTH(n) = 1.0;
XY_NODE_DEND(n)->x = XY_NODE_DSTART(n)->x + XY_NODE_WIDTH(n);
for_slist(liste,hilfe) {
	e = attr_data_of_type(hilfe,Sedge);
	m = OTHER_NODE(n,e);
	if(m->y > n->y)
		{
		XY_EDGE_WIDTH(e) = XY_NODE_WIDTH(n)/
				   XY_NODE_UP(n);
		XY_EDGE_DSTART(e)->x = x + (XY_EDGE_WIDTH(e)/2);
		XY_EDGE_DEND(e)->x = XY_EDGE_DSTART(e)->x;
		XY_EDGE_DATA(e) = 1;
		x = x + XY_EDGE_WIDTH(e);
/*		printf("x = %f\n",x);*/
		QUEUE_EDGE(queue,e);
		}
} end_for_slist(liste,hilfe)
while(! IS_EMPTY_LIST(queue))
	{
/*	printf("Level : %d\n",y);*/
	x = 0.0;
/*	printf("x = %f\n",x);*/
	INIT_LIST(new_list);
/*	print_edge_queue(queue);*/
	while(! IS_EMPTY_LIST(queue))
		{
		POP_EDGE(queue,f);
		n = HIGH_NODE(f);
		m = LOW_NODE(f);
		if(n->y == y)
		    	{
			XY_NODE_DSTART(n)->x = minimum(x,XY_NODE_DSTART(n)->x);
			XY_NODE_WIDTH(n) = XY_NODE_WIDTH(n) + 
					XY_EDGE_WIDTH(f);
			XY_NODE_QUOT(n) = maximum(XY_NODE_QUOT(n),
					(XY_NODE_QUOT(m)*XY_NODE_UP(m)));
			quot = (int) ( (XY_NODE_UP(n)/(XY_NODE_WIDTH(n))) + 0.5);
			XY_NODE_QUOT(n) = quot;
			/*printf("quot = %f\n",XY_NODE_QUOT(n));*/
			if((XY_NODE_DOWN(n) == 1)&&(XY_NODE_UP(n) > 0))
			   {
			   liste = XY_NODE_EDGES(n);
		    	   for_slist(liste,hilfe) {
				e = attr_data_of_type(hilfe,Sedge);
				m = OTHER_NODE(n,e);
				if(m->y > n->y)
					{
					XY_EDGE_WIDTH(e) = XY_NODE_WIDTH(n)/
				   			XY_NODE_UP(n);
					XY_EDGE_DSTART(e)->x = x + (XY_EDGE_WIDTH(e)/2);
					XY_EDGE_DEND(e)->x = XY_EDGE_DSTART(e)->x;
					XY_EDGE_DATA(e) = 1;
					x = x + XY_EDGE_WIDTH(e);
/*					printf("x = %f\n",x);*/
					QUEUE_EDGE(new_list,e);
					}
		    	    } end_for_slist(liste,hilfe)
			} else {
				XY_NODE_DOWN(n)--;
				if(XY_NODE_UP(n) == 0) x = x + XY_EDGE_WIDTH(f);
/*				printf("x = %f\n",x);*/
				}
			XY_NODE_DEND(n)->x = maximum(x,XY_NODE_DEND(n)->x);
		} else {
			x = x + XY_EDGE_WIDTH(f);
/*			printf("x = %f\n",x);*/
			QUEUE_EDGE(new_list,f);
		    	}	
		}
	y++;
	INIT_LIST(queue);
	queue = new_list;
	}

for_all_nodes(g,n) {
	qmax = maximum(qmax,XY_NODE_QUOT(n));
/*	printf("node nr: %d (lb: %s) x: %f-%f y: %d\n",n->nr,n->label,
		XY_NODE_DSTART(n)->x,XY_NODE_DEND(n)->x,n->y);*/
} end_for_all_nodes(g,n)
qmax = qmax*2.0;
/*printf("quotient max = %f\n",qmax);*/

for_all_nodes (g,n) {
	XY_NODE_DSTART(n)->x = XY_NODE_DSTART(n)->x * qmax + 0.5;
	XY_NODE_DEND(n)->x   = XY_NODE_DEND(n)->x * qmax + 0.5;
	maxx = maximum(maxx,XY_NODE_DEND(n)->x);
	XY_NODE_DSTART(n)->y = n->y;
	XY_NODE_DEND(n)->y   = n->y;

	XY_NODE_START(n)->x = (int)XY_NODE_DSTART(n)->x;
	XY_NODE_END(n)->x   = (int)XY_NODE_DEND(n)->x;
	XY_NODE_START(n)->y = n->y;
	XY_NODE_END(n)->y   = n->y;
	for_sourcelist(n,e) {
	    m = (Snode)sedge_real_source(e);
	    if(n->nr == m->nr)
		{
		if(XY_EDGE_DATA(e) == 0)
			{
			XY_EDGE_DSTART(e)->x = XY_EDGE_DSTART(e->tsuc)->x;
			XY_EDGE_DEND(e)->x   = XY_EDGE_DEND(e->tsuc)->x;
			XY_EDGE_WIDTH(e)   = XY_EDGE_WIDTH(e->tsuc);
			XY_EDGE_DATA(e) = 1;
			}
		XY_EDGE_DSTART(e)->y = (float)n->y;
		XY_EDGE_DEND(e)->y = (float)e->tnode->y;
		XY_EDGE_DSTART(e)->x = XY_EDGE_DSTART(e)->x * qmax + 0.5;
		XY_EDGE_DEND(e)->x   = XY_EDGE_DEND(e)->x * qmax + 0.5;


		XY_EDGE_START(e)->y = n->y;
		XY_EDGE_END(e)->y = e->tnode->y;
		XY_EDGE_START(e)->x = (int)XY_EDGE_DSTART(e)->x;
		XY_EDGE_END(e)->x   = (int)XY_EDGE_DEND(e)->x;
		/*print_my_edge(e);
		printf("Kante von (%d,%d) nach (%d,%d)\n",
			XY_EDGE_START(e)->x,XY_EDGE_START(e)->y,
			XY_EDGE_END(e)->x,XY_EDGE_END(e)->y);*/
		}
	} end_for_sourcelist(n,e)
	/*printf("Knoten %s von (%d,%d) nach (%d,%d)\n\n",n->label,
		XY_NODE_START(n)->x,XY_NODE_START(n)->y,
		XY_NODE_END(n)->x,XY_NODE_END(n)->y);*/
} end_for_all_nodes(g,n)

XY_GRAPH_WIDTH(g) = maxx+1;

if(verbose)
	{
	message("graph is %d units high\n",XY_GRAPH_HEIGTH(g));
	message("graph is %d units width\n",XY_GRAPH_WIDTH(g));
	message("graph area is %d units\n",XY_GRAPH_WIDTH(g)*XY_GRAPH_HEIGTH(g));
	}
}


/*************************************************************************/
/***                      END OF FILE: WISMATHMOD.C                    ***/
/*************************************************************************/
