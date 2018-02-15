/**************************************************************************/
/***                                                                    ***/
/*** Filename: TARJANMOD.C                                              ***/
/***                                                                    ***/
/**************************************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "tarjanmod.h"


#define	SET	1
#define NODE	2
#define EDGE	3


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


Global 	int 	x_transform(Sgraph g, int x, int dx, int x_gridsize, int mode)
{
Local	int x_grid,x_start;
if(mode == SET)
	{
	x_grid = x_gridsize/2;
	x_start  = (int)node_get(graphed_node(g->nodes),NODE_X);
	/*message("x_start = %d\n",x_start);
	x_start = x_start - ((g->nodes->x + 1)*2*x_grid) - (x_grid*dx);
	message("x_grid = %d\n",x_grid);
	message("x_start = %d\n",x_start);*/
	return(-1);
	}
else	{
	if(mode == NODE)
		{
		return(x_start + (((x + 1)*2*x_grid)+(x_grid*dx)));
		}
	else	{
		if(mode == EDGE)
			{
			return(x_start + ((x + 1)*2*x_grid));
			}
		else	{
			message("ERROR in x-transform\n");
			}
		}
	}
	return(-1); /* should not be reached */
}

Global int y_transform(Sgraph g, int y, int dy, int maxordinate, int y_gridsize, int node_height, int mode)
{
Local	int b = 0;
Local	int y_grid,y_node,y_start;
if(mode == SET)
	{
	y_grid = y_gridsize/2;
	y_node = node_height/2;
	b = 2*(y_grid + y_node)*(maxordinate + 2);
	y_start  = (int)node_get(graphed_node(g->nodes),NODE_Y);
	/*message("y_start = %d\n",y_start);
	y_start = y_start - b + ( (g->nodes->y*2*(y_grid + y_node)) + y_node);
	message("y_grid = %d  : b = %d : y_node = %d\n",y_grid,b,y_node);
	message("y_start = %d\n",y_start);*/
	return(-1);
	}
else	{
	if(mode == NODE)
		{
		return(y_start + b - ( (y*2*(y_grid + y_node)) + y_node));
		}
	else	{
		if(mode == EDGE)
			{
			if(dy == 0)return(y_start + b - ((y*2*(y_grid + y_node))+(2*y_node)));
			else return(y_start + b - ((y+dy)*2*(y_grid + y_node)));
			}
		else	{
			message("ERROR in y-transform\n");
			}
		}
	}
	return(-1); /* should not be reached */
}


Global void tarjan_set(Sgraph g, int x_gridsize, int y_gridsize, int node_height, int verbose)
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
	if(dx < 1) dx = 1;
	gesamtknotenbreite = gesamtknotenbreite + dx;
	maxknotenbreite = maximum(maxknotenbreite,dx);
	node_width = dx*2*(x_gridsize/2);
	if(XY_NODE_STRONG(n) == 1)
		{
		node_width = node_width + x_gridsize;
		xnew = xnew + (x_gridsize/2);
		}
	if(!(node_width % 2))node_width++;
	n->x = xnew;
	n->y = ynew;
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
	message("Average node width : %f\n",(double)gesamtknotenbreite/XY_GRAPH_MAX_NR(g));
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
				{
				ynew = y_transform(g,XY_EDGE_START(e)->y,0,0,
				0,0,EDGE);
				/*ynew = ynew - 2;*/
				}
			else	{
				ynew = y_transform(g,XY_EDGE_END(e)->y,-dy,0,
				0,0,EDGE);
				/*ynew = ynew + 2;*/
				}
			/*message(" %d %d",xnew,ynew);*/
			/*eline = add_to_edgeline(eline,xnew,ynew);*/
			eline = new_edgeline(xnew,ynew);
			xnew = x_transform(g,XY_EDGE_START(e)->x,0,0,EDGE);
			if(dy > 0)
				{
				ynew = y_transform(g,XY_EDGE_START(e)->y,dy,0,
				0,0,EDGE);
				/*ynew = ynew + 2;*/
			} else	{
				ynew = y_transform(g,XY_EDGE_END(e)->y,0,0,
				0,0,EDGE);
				/*ynew = ynew - 2;*/
				}
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


Global	void	tarjan_draw(Sgraph g, int face, int mode, int verbose)
      	  
   	     
   	     
   	        
	/* compute x,y,dx,dy for nodes and edges */
{
Snode n,m;
Sedge e;
int min,max;
int x;

if((face == LARGESTFACE)&&(XY_GRAPH_DUMMY(g)))XY_GRAPH_WIDTH(g)--;
if(verbose)
	{
	message("graph is %d units high\n",XY_GRAPH_HEIGTH(g));
	message("graph is %d units width\n",XY_GRAPH_WIDTH(g));
	message("graph area is %d units\n",XY_GRAPH_WIDTH(g)*XY_GRAPH_HEIGTH(g));
	}
if(XY_GRAPH_DUMMY(g)) remove_edge(XY_GRAPH_DUMMY(g));
for_all_nodes (g,n) {
	max = 0;
	min = XY_GRAPH_WIDTH(g);
	for_sourcelist(n,e) {
	    if(mode == TARJAN)
	         m =  XY_EDGE_LNODE(e);
	    else m =  XY_EDGE_RNODE(e);
	    x =  m->y;
            max = maximum(max,x);
            min = minimum(min,x);
	    m = (Snode)sedge_real_source(e);
	    if(n->nr == m->nr)
		{
		XY_EDGE_START(e)->y = n->y;
		XY_EDGE_END(e)->y = e->tnode->y;

		XY_EDGE_START(e)->x = x;
		XY_EDGE_END(e)->x   = x;

/*		message("Kante von (%d,%d) nach (%d,%d)\n",
			XY_EDGE_START(e)->x,XY_EDGE_START(e)->y,
			XY_EDGE_END(e)->x,XY_EDGE_END(e)->y);
		message("lb =  von  %s nach %s\n",
			e->snode->label,e->tnode->label);
		message("nr =  von  %d nach %d\n",e->snode->nr,e->tnode->nr);
                message("dual  von  %d nach %d\n",
			XY_EDGE_EDGE(e)->snode->nr,XY_EDGE_EDGE(e)->tnode->nr);*/
		}
	} end_for_sourcelist(n,e)
	if(g->directed)
	    {
	    for_targetlist(n,e) {
	       if(mode == TARJAN)
	         	m =  XY_EDGE_LNODE(e);
	        else m =  XY_EDGE_RNODE(e);
	        x =  m->y;
            	max = maximum(max,x);
            	min = minimum(min,x);
	    } end_for_targetlist(n,e)
	    }
	XY_NODE_START(n)->x = min;
	XY_NODE_START(n)->y = n->y;
	XY_NODE_END(n)->x = max;
	XY_NODE_END(n)->y = n->y;
	n->x = min;

/*	message("Knoten %s von (%d,%d) nach (%d,%d)\n\n",n->label,
		XY_NODE_START(n)->x,XY_NODE_START(n)->y,
		XY_NODE_END(n)->x,XY_NODE_END(n)->y);*/
} end_for_all_nodes(g,n)
}





/*                                                                            */
/* ************************************************************************** */
/* **                      END OF FILE: TARJANMOD.C                        ** */
/* ************************************************************************** */
