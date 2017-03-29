/**************************************************************************/
/***                                                                    ***/
/*** Filename: OTTENMOD.C                                               ***/
/***                                                                    ***/
/**************************************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "tarjanmod.h"
#include "listen.h"
#include "dualmod.h"


Global	void	otten_draw(Sgraph g, int face, int verbose)
      	  
   	     
   	        
	/* compute x,y,dx,dy for nodes and edges,
	   set to graphed-structure */
{
Sgraph 	dual;
Snode 	n,m;
Sedge 	e;
Slist 	liste,hilfe;
int 	min,max;
int 	x,i;

dual = XY_GRAPH_GRAPH(g);
XY_GRAPH_WIDTH(g) = DUAL_GRAPH_MAX_NR(dual);
XY_GRAPH_HEIGTH(g) = XY_GRAPH_MAX_NR(g);
if((face == LARGESTFACE)&&(XY_GRAPH_DUMMY(g)))XY_GRAPH_WIDTH(g)--;

if(verbose)
	{
	message("graph is %d units high\n",XY_GRAPH_HEIGTH(g));
	message("graph is %d units width\n",XY_GRAPH_WIDTH(g));
	message("graph area is %d units\n",XY_GRAPH_HEIGTH(g)*XY_GRAPH_WIDTH(g));
	}
for_all_nodes (g,n) {
	XY_NODE_START(n)->y = n->nr;
	XY_NODE_END(n)->y = n->nr;
	for_sourcelist(n,e) {
	    if(((e->snode->nr ==1)&&(e->tnode->nr) == XY_GRAPH_MAX_NR(g))||
		((e->tnode->nr ==1)&&(e->snode->nr) == XY_GRAPH_MAX_NR(g)))
		{		
                XY_EDGE_START(e)->x = DUAL_GRAPH_MAX_NR(dual); 
                XY_EDGE_END(e)->x = DUAL_GRAPH_MAX_NR(dual);
		} 
	    if(XY_EDGE_TYPE(e) == DUMMY)continue;
	    m = (Snode)sedge_real_source(e);
	    if(n->nr == m->nr)
		{
		XY_EDGE_START(e)->y = n->nr;
		XY_EDGE_END(e)->y = e->tnode->nr;
		}
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)
for(i = DUAL_GRAPH_MAX_NR(dual);i>0;i--)
   {
   for_all_nodes(dual,n) {
	if(n->nr == i)
	    {
	    liste = DUAL_NODE_BOUNDEDGE(n);
	    for_slist(liste,hilfe) {
		 e = attr_data_of_type(hilfe,Sedge);
		 if(XY_EDGE_START(e)->x == 0)
			{
                    	XY_EDGE_START(e)->x = i-1; 
                	XY_EDGE_END(e)->x = i-1;
			if(!g->directed)
				{
                    		XY_EDGE_START(e->tsuc)->x = i-1; 
                		XY_EDGE_END(e->tsuc)->x = i-1;
				}
			}
	    } end_for_slist(liste,hilfe)
	    }
   } end_for_all_nodes(dual,n)
   }
if(XY_GRAPH_DUMMY(g)) remove_edge(XY_GRAPH_DUMMY(g));
for_all_nodes (g,n) {
	max = 0;
	min = DUAL_GRAPH_MAX_NR(dual);
	for_sourcelist(n,e) {
	    x = XY_EDGE_START(e)->x;
            max = maximum(max,x);
            min = minimum(min,x);
/*		message("Kante von (%d,%d) nach (%d,%d)\n",
			XY_EDGE_START(e)->x,XY_EDGE_START(e)->y,
			XY_EDGE_END(e)->x,XY_EDGE_END(e)->y);
		message("lb =  von  %s nach %s\n",
			e->snode->label,e->tnode->label);
		message("nr =  von  %d nach %d\n",e->snode->nr,e->tnode->nr);
                message("dual  von  %d nach %d\n",
			XY_EDGE_EDGE(e)->snode->nr,XY_EDGE_EDGE(e)->tnode->nr);
*/
	} end_for_sourcelist(n,e)
	    for_targetlist(n,e) {
	    	x = XY_EDGE_START(e)->x;
            	max = maximum(max,x);
            	min = minimum(min,x);
	    } end_for_targetlist(n,e)

	XY_NODE_START(n)->x = min;
	XY_NODE_END(n)->x = max;

/*	message("Knoten %s von (%d,%d) nach (%d,%d)\n\n",n->label,
		XY_NODE_START(n)->x,XY_NODE_START(n)->y,
		XY_NODE_END(n)->x,XY_NODE_END(n)->y);*/
} end_for_all_nodes(g,n)
}




/*                                                                            */
/* ************************************************************************** */
/* **                      END OF FILE: OTTENMOD.C                      ** */
/* ************************************************************************** */
