/**************************************************************************/
/***                                                                    ***/
/*** Filename: INITMOD.C                                                ***/
/***                                                                    ***/
/**************************************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "tarjanmod.h"
#include "dualmod.h"
#include "listen.h"



/*******************************************************/
/*    ordinate1 berechnet die y-Koordinaten,           */
/*******************************************************/

Global  int 	ordinate(Sgraph graph)
{
Snode 	n,m;
Sedge 	e;
int   	max_y = 0;
LIST 	queue,liste,hilfe;
INIT_LIST(queue);
QUEUE_NODE(queue,graph->nodes);
while(! IS_EMPTY_LIST(queue))
	{
	POP_NODE(queue,n);
	max_y = maximum(max_y,n->y);
	liste = XY_NODE_EDGES(n);
	for_slist(liste,hilfe) {
		e = attr_data_of_type(hilfe,Sedge);
		m = OTHER_NODE(n,e);
		if((m->nr > n->nr)&&(n->y >= m->y))
			{
			m->y = n->y + 1;
			max_y = maximum(max_y,m->y);
			QUEUE_NODE(queue,m);
			}
	} end_for_slist(liste,hilfe)
	}
return(max_y+1);
}

Local 	int 	highest_node_number(Sgraph g)
{
Snode 	n;
int 	nr = 0;
for_all_nodes(g,n) {
	if(n->nr > nr) nr = n->nr;
} end_for_all_nodes(g,n);
return(nr);
}

/******************* Alloc - Routinen ******************/

Local char *alloc_graph_attrs(void)
{return (malloc(sizeof(struct graph_attr)));}
Local char *alloc_node_attrs(void)
{return (malloc(sizeof(struct node_attr)));}
Local char *alloc_edge_attrs(void)
{return (malloc(sizeof(struct edge_attr)));}
Local char *alloc_xy(void)
{return (malloc(sizeof(x_y)));}
Local char *alloc_dxy(void)
{return (malloc(sizeof(dx_y)));}

static		void init_edge_attrs(Edge_attr e)
{
e->left_face = NULL;
e->right_face = NULL;
e->dual_edge = NULL;
e->data = 0;
e->start = (X_Y)alloc_xy();
e->start->x = 0;
e->start->y = 0;
e->end = (X_Y)alloc_xy();
e->end->x = 0;
e->end->y = 0;
e->dstart = (DX_Y)alloc_dxy();
e->dstart->x = 0.0;
e->dstart->y = 0.0;
e->dend = (DX_Y)alloc_dxy();
e->dend->x = 0.0;
e->dend->y = 0.0;
e->width = 0.0;
e->type = REALLY;
e->source = NULL;
e->target = NULL;
}

/********** erzeugt Dummy-Kante ***********************/

Global 	Sedge 	createdummyedge(Snode snode, Snode tnode)
{               
Sedge 	new_edge;
new_edge = make_edge(snode,tnode,make_attr(ATTR_DATA,NULL));
attr_data(new_edge) = alloc_edge_attrs();
init_edge_attrs(XY_EDGE_ATTRS(new_edge));
XY_EDGE_TYPE(new_edge) = DUMMY;
if(!snode->graph->directed)
	{	
	attr_data(new_edge->tsuc) = alloc_edge_attrs();
	init_edge_attrs(XY_EDGE_ATTRS(new_edge->tsuc));
	XY_EDGE_TYPE(new_edge->tsuc) = DUMMY;
	}
return (new_edge);
}

static	void 	init_node_attrs(Node_attr n, Slist edgelist, int oldnr)
{
n->edges = edgelist;
n->data = 0;
n->start = (X_Y)alloc_xy();
n->start->x = 0;
n->start->y = 0;
n->end = (X_Y)alloc_xy();
n->end->x = 0;
n->end->y = 0;
n->dstart = (DX_Y)alloc_dxy();
n->dstart->x = 1.0;
n->dstart->y = 0.0;
n->dend = (DX_Y)alloc_dxy();
n->dend->x = 0.0;
n->dend->y = 0.0;
n->oldnr = oldnr;
n->width = 0.0;
n->quotient = 1.0;
n->up = 0;
n->down = 0;
n->strong = 0;
}

Global	void 	init_visinode_attrs(Snode n)
{
n->y = 0;
n->x = 0;
attr_data(n) = nil;
attr_data(n) = alloc_node_attrs();
init_node_attrs(XY_NODE_ATTRS(n),NULL,n->nr);
}

Global	void 	init_visiedge_attrs(Sedge e)
{
attr_data(e) = nil;
attr_data(e) = alloc_edge_attrs();
init_edge_attrs(XY_EDGE_ATTRS(e));
}

static	void 	init_graph_attrs(Graph_attr g)
{
g->dual_graph = NULL;
g->width = 0;
g->heigth = 0;
g->max_nr = 0;
g->dummy_edge = NULL;
}

Global	void 	init_visigraph_attrs(Sgraph g)
{
attr_data(g) = nil;
attr_data(g) = alloc_graph_attrs();
init_graph_attrs(XY_GRAPH_ATTRS(g));
XY_GRAPH_START(g) = g->nodes;
}

Local 	Sedge	find_st_edge(Sgraph g)
{
int	max = 0,min=100000;
Sedge	e,st = NULL;
Snode	n = g->nodes;
while(n->slist == NULL)n = n->suc;
st = n->slist;
for_all_nodes(g,n) {
        for_sourcelist(n,e) {
		/*printf("Kante von %d(lab:%s) nach %d(lab:%s) data:%d\n",e->snode->nr,e->snode->label,e->tnode->nr,e->tnode->label,XY_EDGE_DATA(e));*/	
		if(XY_EDGE_DATA(e) > max)
		/*if(XY_EDGE_DATA(e) < min)*/
			{
			max = XY_EDGE_DATA(e);
			min = XY_EDGE_DATA(e);
			/*printf("best st-edge mit gesamtknotengrad %d\n",XY_EDGE_DATA(e));*/
			st = e;
			continue;
			}
		if(XY_EDGE_DATA(e) == max)
			{
			/*printf("Gleichstand\n");*/
			if(XY_NODE_DATA(e->snode) < XY_NODE_DATA(st->snode))
				{
				max = XY_EDGE_DATA(e);
				min = XY_EDGE_DATA(e);
				/*printf("best st-edge mit gesamtknotengrad %d\n",XY_EDGE_DATA(e));*/
				st = e;
				}
			
			}
        }  end_for_sourcelist(n,e);
}  end_for_all_nodes(g,n);
return(st);
}

Local 	Sedge	find_best_st_edge(Sgraph g, int greedy)
{
int	area,min = 100000;
Sedge	firstedge,e,f,st = NULL;
Snode	n,m;
LIST	nodelist,edgelist,hilfe;
message("Searching best ST-edge");
nodelist = make_slist_of_sgraph(g);
INIT_LIST(edgelist);
for_slist(nodelist,hilfe) {
	n = attr_data_of_type(hilfe,Snode);
	/*printf("Knoten nr:%d lab:%s\n",n->nr,n->label);*/
	for_sourcelist(n,e) {
		QUEUE_EDGE(edgelist,e);
		/*printf("Kante von %d(lab:%s) nach %d(lab:%s) gequeued\n",e->snode->nr,e->snode->label,e->tnode->nr,e->tnode->label);*/
	} end_for_sourcelist(n,e)
} end_for_slist(nodelist,hilfe)
CLEAR_LIST(nodelist);
for_slist(edgelist,hilfe) {
	message(".");
	e = attr_data_of_type(hilfe,Sedge);
	/*printf("Kante von %d(lab:%s) nach %d(lab:%s)\n",e->snode->nr,e->snode->label,e->tnode->nr,e->tnode->label);*/
	g->nodes = e->snode;
	firstedge = g->nodes->slist; /*merken und spaeter restaurieren */
	g->nodes->slist = e;
	my_st_number(g);
	if(!my_test_st_number(g))
		{
		printf("graph is not st-numbered\n");
		exit(0);
		}
	XY_GRAPH_MAX_NR(g) = highest_node_number(g);
	XY_GRAPH_HEIGTH(g) = ordinate(g);
	switch(greedy)
	{
	case WISMATH: 
		area = XY_GRAPH_HEIGTH(g);
		break;
	case TAMAS: 
		make_dual_graph(g);
		area = XY_GRAPH_WIDTH(g);
		break;
	case TARJ: 
		make_dual_graph(g);
		area = XY_GRAPH_WIDTH(g) * XY_GRAPH_HEIGTH(g);
		break;
	default: message("error in init_mod :unknown algorithm\n");
		exit(1);
	}
	/*printf("Hoehe %d Breite: %d\n",XY_GRAPH_HEIGTH(g),XY_GRAPH_WIDTH(g));*/
	if(area < min)
		{
		min = area;
		/*printf("best st-edge mit flaechenbedarf %d\n",area);*/
		st = e;
		/*for_all_nodes(g,m) {
			printf("Node Nr:%d label:%s y:%d\n",m->nr,m->label,m->y);
		}  end_for_all_nodes(g,m);*/
		}
	/*else 	printf("st-edge mit flaechenbedarf %d =>ignored\n",area);*/

	for_all_nodes(g,m) { /* neuinitialisierung */
		m->nr = XY_NODE_OLDNR(m);
		m->y = 0;
		m->x = 0;
		init_node_attrs(XY_NODE_ATTRS(m),XY_NODE_EDGES(m),m->nr);
	       	for_sourcelist(m,f) {	
			init_edge_attrs(XY_EDGE_ATTRS(f));
		       }  end_for_sourcelist(m,f);
	}  end_for_all_nodes(g,m);
	if(XY_GRAPH_GRAPH(g))free_dual_graph(XY_GRAPH_GRAPH(g));
	XY_GRAPH_GRAPH(g) = NULL;
	g->nodes->slist = firstedge;
	g->nodes = XY_GRAPH_START(g);
} end_for_slist(edgelist,hilfe)
message("done\n");
CLEAR_LIST(edgelist);
/*printf("ST-Kante von %d(lab:%s) nach %d(lab:%s)\n",st->snode->nr,st->snode->label,st->tnode->nr,st->tnode->label);*/
return(st);
}

Global 	int 	init_node_and_edges(Sgraph g, int face, int greedy, int stedge)
      	  
   	     
   	       
   	       
	/* create structures , st-number and ordinate the graph */
{
Snode	n,node;
Sedge   e,f;
Slist   helpembed,liste,hilfe;

if((stedge > 2)||(stedge < 0))
	{
	message("Wrong stedge Option\n");
	exit(1);
	}
attr_data(g) = nil;
attr_data(g) = alloc_graph_attrs();
init_graph_attrs(XY_GRAPH_ATTRS(g));
XY_GRAPH_START(g) = g->nodes;

for_all_nodes(g,n) {
	n->y = 0;
	n->x = 0;
        helpembed = attr_data_of_type(n,Slist);
	attr_data(n) = nil;
	attr_data(n) = alloc_node_attrs();
	init_node_attrs(XY_NODE_ATTRS(n),helpembed,n->nr);
        for_sourcelist(n,e) {	
		attr_data(e) = alloc_edge_attrs();
		init_edge_attrs(XY_EDGE_ATTRS(e));
        }  end_for_sourcelist(n,e);
}  end_for_all_nodes(g,n);

/* setze Grade der Knoten und Kanten */
for_all_nodes(g,n) {
        for_sourcelist(n,e) {	
		XY_NODE_DATA(n)++;
        }  end_for_sourcelist(n,e);
	if(g->directed)
		{
	        for_targetlist(n,e) {	
			XY_NODE_DATA(n)++;
        	}  end_for_targetlist(n,e);
		}
}  end_for_all_nodes(g,n);
for_all_nodes(g,n) {
        for_sourcelist(n,e) {	
		XY_EDGE_DATA(e) = XY_NODE_DATA(e->snode) + XY_NODE_DATA(e->tnode);
        }  end_for_sourcelist(n,e);
}  end_for_all_nodes(g,n);

if(stedge == ANYEDGE)
	{
	while(g->nodes->slist == NULL)g->nodes = g->nodes->suc;
	}
if(stedge == CLEVER)
	{
	e = (Sedge)find_st_edge(g);
	g->nodes = e->snode;
	g->nodes->slist = e;
	}
for_all_nodes(g,n) {
	XY_NODE_DATA(n) = 0;
	for_sourcelist(n,e) {	
		XY_EDGE_DATA(e) = 0;
	}  end_for_sourcelist(n,e);
}  end_for_all_nodes(g,n);

/*for_all_nodes(g,n) {
	printf("Node Nr: %d label: %s \n",n->nr,n->label);
	print_edge_list(n);
}  end_for_all_nodes(g,n);

print_simple_graph(g);*/

if((greedy)&&(stedge == BEST))
	{  
	e = (Sedge)find_best_st_edge(g,greedy);
	/*printf("ST-Kante von %d(lab:%s) nach %d(lab:%s)\n",e->snode->nr,e->snode->label,e->tnode->nr,e->tnode->label);*/
	g->nodes = e->snode;
	g->nodes->slist = e;
	} 

my_st_number(g);
if(!my_test_st_number(g))
	{
	printf("graph is not st-numbered\n");
	exit(0);
	}
XY_GRAPH_MAX_NR(g) = highest_node_number(g);
XY_GRAPH_HEIGTH(g) = ordinate(g);

/*for_all_nodes(g,n) {
	printf("Node Nr:%d label:%s y:%d\n",n->nr,n->label,n->y);
	print_edge_list(n);
}  end_for_all_nodes(g,n);*/

if(face == LARGESTFACE)lface(g);

/*print_simple_graph(g);
for_all_nodes(g,n) {
	printf("Node Nr: %d label: %s \n",n->nr,n->label);
	print_edge_list(n);
}  end_for_all_nodes(g,n);*/


/* ------------- Initialisierungen die fuer Wismath gebraucht werden --*/
/* up = #Edges, die nach 'oben' fuehren                                */
/* XY_NODE_EDGES(n) steht bei der ersten Kante, die nach 'oben' fuehrt */

for_all_nodes(g,n) {
	liste = XY_NODE_EDGES(n);
	for_slist(liste,hilfe) {
		f = attr_data_of_type(hilfe,Sedge);
		if(XY_EDGE_TYPE(f) == DUMMY) continue;
		if(OTHER_NODE(n,f)->y > n->y)XY_NODE_UP(n)++; 
		else XY_NODE_DOWN(n)++;
} end_for_slist(liste,hilfe)
	f = attr_data_of_type(liste,Sedge);
	node = OTHER_NODE(n,f);
	if(n->nr == 1)
		{
		while(node->nr != XY_GRAPH_MAX_NR(g))
			{
			XY_NODE_EDGES(n) = XY_NODE_EDGES(n)->suc;
			liste = XY_NODE_EDGES(n);
			f = attr_data_of_type(liste,Sedge);
			node = OTHER_NODE(n,f);
			}
	} else 	{
		while(node->y > n->y)
			{
			XY_NODE_EDGES(n) = XY_NODE_EDGES(n)->suc;
			liste = XY_NODE_EDGES(n);
			f = attr_data_of_type(liste,Sedge);
			node = OTHER_NODE(n,f);
			}
		while((node->y < n->y)&&(n->nr != XY_GRAPH_MAX_NR(g)))
			{
			XY_NODE_EDGES(n) = XY_NODE_EDGES(n)->suc;
			liste = XY_NODE_EDGES(n);
			f = attr_data_of_type(liste,Sedge);
			node = OTHER_NODE(n,f);
			}
		}
}  end_for_all_nodes(g,n);
/*for_all_nodes(g,n) {
	printf("Node Nr: %d label: %s \n",n->nr,n->label);
	print_edge_list(n);
}  end_for_all_nodes(g,n);

printf("highest node-nr = %d\n",XY_GRAPH_MAX_NR(g));
print_my_dual_graph(g);*/

/*printf("function init_node_and_edges(g) finished\n");*/
return(XY_GRAPH_HEIGTH(g));
}


/********************************************************************/
/********************** die FREE-Prozedur ***************************/
/********************************************************************/

Local	void	free_xy_graph(Sgraph g)
{
Snode 	n;
Sedge 	e;
free(attr_data(g));
for_all_nodes(g,n)
	{
	n->nr = XY_NODE_OLDNR(n);
	if(XY_NODE_EDGES(n))CLEAR_LIST(XY_NODE_EDGES(n));
	free(XY_NODE_START(n));
	free(XY_NODE_END(n));
	free(XY_NODE_DSTART(n));
	free(XY_NODE_DEND(n));
	free(attr_data(n));
	for_sourcelist(n,e)
		{
		free(XY_EDGE_START(e));
		free(XY_EDGE_END(e));
		free(XY_EDGE_DSTART(e));
		free(XY_EDGE_DEND(e));
		free(attr_data(e));
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)
}

Global	void	free_xy_cutnode(Snode n)
{
free(XY_NODE_START(n));
free(XY_NODE_END(n));
free(XY_NODE_DSTART(n));
free(XY_NODE_DEND(n));
}

Global	void	free_node_and_edges(Sgraph g)
{
if(XY_GRAPH_GRAPH(g))
	free_dual_graph(XY_GRAPH_GRAPH(g));
g->nodes = XY_GRAPH_START(g);
free_xy_graph(g);
}



Global 	void 	polyline(Sgraph g, int mode, int x_gridsize, int y_gridsize, int node_height)
{
Snode 		n;
Sedge 		e;
int		xnew,ynew,max,dy,yold,yoffset,xedge;
Edgeline	eline;
/*if(mode == BETTER)
	{
	message("Creating better polyline Layout\n");
}  else { 
	message("Creating good polyline Layout\n");
	}*/
for_all_nodes(g,n)  {
	node_set(graphed_node(n),
	NODE_SIZE,node_height,node_height,
	NODE_NLP,NODELABEL_MIDDLE,0);
	if(mode == BETTER)
	   {
	   max = 0;
	   for_sourcelist(n,e)  {
		if(n->nr == ((Snode)sedge_real_source(e))->nr)
			{
			eline = (Edgeline)edge_get(graphed_edge(e),EDGE_LINE);
			yold = (int)node_get(graphed_node(e->snode),NODE_Y);
			ynew = edgeline_y(eline->suc);
			xnew = edgeline_x(eline->suc);
			dy = e->tnode->y - e->snode->y;
			if (dy < 0) dy = -dy;
			if(dy > max)
				{
				max = dy;
				n->x = xnew;
				n->y = yold;
				node_set(graphed_node(n),
				NODE_POSITION,xnew,yold,0);
				}
			}
		if(!g->directed)
	  	   {
		   if(n->nr == ((Snode)sedge_real_target(e))->nr)
			{
			eline = (Edgeline)edge_get(graphed_edge(e),EDGE_LINE);
			yold = (int)node_get(graphed_node(e->snode),NODE_Y);
			ynew = edgeline_y(eline->suc);
			xnew = edgeline_x(eline->suc);
			dy = e->snode->y - e->tnode->y;
			if (dy < 0) dy = -dy;
			if(dy > max)
				{
				max = dy;
				n->x = xnew;
				n->y = yold;
				node_set(graphed_node(n),
				NODE_POSITION,xnew,yold,0);
				}
			}
		    }
	} end_for_sourcelist(n,e)
	/*if(g->directed)
	  {*/
	  for_targetlist(n,e)  {
		if(n->nr == ((Snode)sedge_real_target(e))->nr)
			{
			eline = (Edgeline)edge_get(graphed_edge(e),EDGE_LINE);
			yold = (int)node_get(graphed_node(e->tnode),NODE_Y);
			ynew = edgeline_y(eline->suc);
			xnew = edgeline_x(eline->suc);
			dy = e->snode->y - e->tnode->y;
			if (dy < 0) dy = -dy;
			if(dy > max)
				{
				max = dy;
				n->x = xnew;
				n->y = yold;
				node_set(graphed_node(n),
				NODE_POSITION,xnew,yold,0);
				}
			}
	  } end_for_targetlist(n,e)
	  /*}*/
	}  
} end_for_all_nodes(g,n)
for_all_nodes(g,n)  {
    for_sourcelist(n,e)  {
	if(n->nr == ((Snode)sedge_real_source(e))->nr)
		{
		eline = (Edgeline)edge_get(graphed_edge(e),EDGE_LINE);
		xedge = edgeline_x(eline->suc);
		free_edgeline(eline);
		yoffset = (2*((y_gridsize/2) + (node_height/2)));
		dy = e->tnode->y - n->y;
		if (dy < 0) 
			{
			dy = -dy;	
			yoffset = -yoffset;
			}
		dy = (dy+5)/(2*((y_gridsize/2) + (node_height/2)));
		if(dy == 0)
			{
			printf("Error\n");
			exit(0);
			}
		xnew = (int)node_get(graphed_node(e->snode),NODE_X);
		ynew = (int)node_get(graphed_node(e->snode),NODE_Y);
		eline = new_edgeline(xnew,ynew);
		if(dy == 2)
			{
			ynew = ynew + yoffset;
			/*message(" to %d %d\n",xnew,ynew);*/
			eline = add_to_edgeline(eline,xedge,ynew);
			}
		if(dy > 2)
			{
			ynew = ynew + yoffset;
			/*message(" to %d %d\n",xnew,ynew);*/
			eline = add_to_edgeline(eline,xedge,ynew);

			ynew = (int)node_get(graphed_node(e->tnode),NODE_Y);
			ynew = ynew - yoffset;
			/*message(" to %d %d\n",xnew,ynew);*/
			eline = add_to_edgeline(eline,xedge,ynew);
			}
		xnew = (int)node_get(graphed_node(e->tnode),NODE_X);
		ynew = (int)node_get(graphed_node(e->tnode),NODE_Y);
		/*message(" to %d %d\n",xnew,ynew);*/
		eline = add_to_edgeline(eline,xnew,ynew);
		eline = eline->suc;
		edge_set(graphed_edge(e),EDGE_LINE,eline,0);
		}
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)

for_all_nodes(g,n)  {
	node_set(graphed_node(n),
	NODE_NEI,TO_BORDER_OF_BOUNDING_BOX,0);
} end_for_all_nodes(g,n)
}

/****************************************************************************/
/***                      END OF FILE: INITMOD.C                          ***/
/****************************************************************************/
