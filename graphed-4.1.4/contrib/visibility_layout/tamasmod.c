/**************************************************************************/
/***                                                                    ***/
/*** Filename: TAMASSIAMOD.C                                            ***/
/***                                                                    ***/
/**************************************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "tarjanmod.h"
#include "listen.h"
#include "dualmod.h"


Global	void	tamassia_draw(Sgraph g, int visitype, int face, int verbose)
      	  
   	         
   	     
   	        
	/* compute x,y,dx,dy for nodes and edges,
	   set to graphed-structure */
{
Snode n,m,left_face,right_face;
Sedge e;
int min,max;
int x,width;

XY_GRAPH_HEIGTH(g) = XY_GRAPH_MAX_NR(g);
if(verbose)message("graph is %d units high\n",XY_GRAPH_HEIGTH(g));
width = (XY_GRAPH_WIDTH(g)*2)-1;
if(visitype == 0)
	{
	if((face == LARGESTFACE)&&(XY_GRAPH_DUMMY(g)))width = width - 2;
	if(verbose)
		{
		message("graph is %d units width\n",width);
		message("graph area is %d units\n",XY_GRAPH_MAX_NR(g)*width);
		}
	}

if(XY_GRAPH_DUMMY(g)) remove_edge(XY_GRAPH_DUMMY(g));
for_all_nodes (g,n) {
	XY_NODE_START(n)->y = n->nr;
	XY_NODE_END(n)->y = n->nr;
	max = 0;
	min = (XY_GRAPH_WIDTH(g)*2)+1;
	for_sourcelist(n,e) {
	    left_face =  XY_EDGE_LNODE(e);
	    right_face =  XY_EDGE_RNODE(e);
	    if(((e->snode->nr == 1)&&(e->tnode->nr == XY_GRAPH_MAX_NR(g)))
		||((e->tnode->nr == 1)&&(e->snode->nr == XY_GRAPH_MAX_NR(g))))
			x = -1;
	    else  	x = left_face->y + right_face->y;
	    if(XY_EDGE_TYPE(e) == DUMMY)continue;
            max = maximum(max,x);
            min = minimum(min,x);
	    m = (Snode)sedge_real_source(e);
	    if(n->nr == m->nr)
		{
		XY_EDGE_START(e)->y = n->nr;
		XY_EDGE_END(e)->y = e->tnode->nr;

		XY_EDGE_START(e)->x = x;
		XY_EDGE_END(e)->x   = x;

/*		message("Kante von (%d,%d) nach (%d,%d)\n",
			XY_EDGE_START(e)->x,XY_EDGE_START(e)->y,
			XY_EDGE_END(e)->x,XY_EDGE_END(e)->y);
		message("lb =  von  %s nach %s\n",
			e->snode->label,e->tnode->label);
		message("nr =  von  %d nach %d\n",e->snode->nr,e->tnode->nr);
                message("dual  von  %d nach %d\n",
			XY_EDGE_EDGE(e)->snode->nr,XY_EDGE_EDGE(e)->tnode->nr);
*/
		}
	} end_for_sourcelist(n,e)
	if(g->directed)
	    {
	    for_targetlist(n,e) {
	        left_face =  XY_EDGE_LNODE(e);
	        right_face =  XY_EDGE_RNODE(e);
		if(((e->snode->nr == 1)&&(e->tnode->nr == XY_GRAPH_MAX_NR(g)))
		||((e->tnode->nr == 1)&&(e->snode->nr == XY_GRAPH_MAX_NR(g))))
			x = -1;
		else  	x = left_face->y + right_face->y;
	    	if(XY_EDGE_TYPE(e) == DUMMY)continue;
            	max = maximum(max,x);
            	min = minimum(min,x);
	    } end_for_targetlist(n,e)
	    }

	if(min == max)	XY_NODE_START(n)->x = max -1;
	else 		XY_NODE_START(n)->x = min;
	XY_NODE_END(n)->x = max;

/*	message("Knoten %s von (%d,%d) nach (%d,%d)\n\n",n->label,
		XY_NODE_START(n)->x,XY_NODE_START(n)->y,
		XY_NODE_END(n)->x,XY_NODE_END(n)->y);*/
} end_for_all_nodes(g,n)
}



Global	void	tamassia_e_draw(Sgraph g, int face, int verbose)
      	  
   	     
   	        
	/* compute x,y,dx,dy for nodes and edges,
	   set to graphed-structure */
{
Snode		n,node;
Sgraph 		Dualgraph;
LIST		liste,hilfe;
int		width;
Dualgraph = XY_GRAPH_GRAPH(g);
width = (XY_GRAPH_WIDTH(g)*2)-1;
if((face == LARGESTFACE)&&(XY_GRAPH_DUMMY(g)))width = width - 1;
if(verbose)
	{
	message("graph is %d units width\n",width);
	message("graph area is %d units\n",XY_GRAPH_MAX_NR(g)*width);
	}
for_all_nodes(Dualgraph,n) {
	if(n->nr == DUAL_GRAPH_MAX_NR(Dualgraph))continue;
	liste = DUAL_NODE_R_PATH(n);
	for_slist(liste,hilfe) {
		node = attr_data_of_type(hilfe,Snode);
		if((node->nr == DUAL_NODE_LOW(n)->nr)||
		   (node->nr == DUAL_NODE_HIGH(n)->nr))continue;
		XY_NODE_START(node)->x = 2 * n->y;
	} end_for_slist(liste,hilfe)
	liste = DUAL_NODE_L_PATH(n);
	for_slist(liste,hilfe) {
		node = attr_data_of_type(hilfe,Snode);
		if((node->nr == DUAL_NODE_LOW(n)->nr)||
		   (node->nr == DUAL_NODE_HIGH(n)->nr))continue;
		XY_NODE_END(node)->x = 2 * n->y;
	} end_for_slist(liste,hilfe)
} end_for_all_nodes(Dualgraph,n);
}


Global	void	tamassia_s_draw(Sgraph g, int face, int verbose)
      	  
   	     
   	        
	/* compute x,y,dx,dy for nodes and edges,
	   set to graphed-structure */
{
Snode		n,node;
Sgraph 		Dualgraph;
LIST		liste,hilfe;
int		laenge_l,laenge_r,width;

Dualgraph = XY_GRAPH_GRAPH(g);
width = (XY_GRAPH_WIDTH(g)*2)-1;
if((face == LARGESTFACE)&&(XY_GRAPH_DUMMY(g)))width = width - 1;
if(verbose)
	{
	message("graph is %d units width\n",width);
	message("graph area is %d units\n",XY_GRAPH_MAX_NR(g)*width);
	}
for_all_nodes(Dualgraph,n) {
	if(n->nr == DUAL_GRAPH_MAX_NR(Dualgraph))continue;
	liste = DUAL_NODE_R_PATH(n);
	laenge_r = LIST_LENGTH(liste);
	for_slist(liste,hilfe) {
		node = attr_data_of_type(hilfe,Snode);
		if((node->nr == DUAL_NODE_LOW(n)->nr)||
		   (node->nr == DUAL_NODE_HIGH(n)->nr))continue;
		XY_NODE_START(node)->x = 2 * n->y;
	} end_for_slist(liste,hilfe)
	liste = DUAL_NODE_L_PATH(n);
	laenge_l = LIST_LENGTH(liste);
	for_slist(liste,hilfe) {
		node = attr_data_of_type(hilfe,Snode);
		if((node->nr == DUAL_NODE_LOW(n)->nr)||
		   (node->nr == DUAL_NODE_HIGH(n)->nr))continue;
		if((laenge_l != 2)&&(laenge_r != 2))
			XY_NODE_STRONG(node) = 1;
		XY_NODE_END(node)->x = 2 * n->y;
	} end_for_slist(liste,hilfe)
} end_for_all_nodes(Dualgraph,n);
}


Global	int	test_strong_st_numbering(Sgraph g)
{
Snode		n;
Sgraph 		Dualgraph;
int		laenge_l,laenge_r;
int		strong = 1;
Dualgraph = XY_GRAPH_GRAPH(g);
for_all_nodes(Dualgraph,n) {
	laenge_r = LIST_LENGTH(DUAL_NODE_R_PATH(n));
	laenge_l = LIST_LENGTH(DUAL_NODE_L_PATH(n));
	if((laenge_l != 2)&&(laenge_r != 2))strong = 0;
} end_for_all_nodes(Dualgraph,n);
return(strong);
}
                                                                         
/****************************************************************************/
/***                      END OF FILE: TAMASSIAMOD.C                      ***/
/****************************************************************************/
