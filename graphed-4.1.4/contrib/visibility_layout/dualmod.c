/**************************************************************************/
/***                                                                    ***/
/*** Filename: DUALMOD.C                                                ***/
/***                                                                    ***/
/**************************************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "listen.h"
#include "tarjanmod.h"
#include "dualmod.h"


/******************************************************************/
/*								  */
/* Folgende Routinen dienen zur richtigen Fortbewegung im Graphen */
/*								  */
/******************************************************************/

/*
Local 	Sedge 	spre(Snode n, Sedge e)
{
LIST 	liste,hilfe;
Sedge 	f;
liste = XY_NODE_EDGES(n);
for_slist(liste,hilfe) {
	f = attr_data_of_type(hilfe,Sedge);
	if(f == e) return((Sedge)attr_data_of_type(hilfe->pre,Sedge));
} end_for_slist(liste,hilfe)
printf("Fehler bei spre\n");
exit(1);
}
*/

Local 	Sedge 	ssuc(Snode n, Sedge e)
{
LIST 	liste,hilfe;
Sedge 	f;
liste = XY_NODE_EDGES(n);
for_slist(liste,hilfe) {
	f = attr_data_of_type(hilfe,Sedge);
	if(f == e) return((Sedge)attr_data_of_type(hilfe->suc,Sedge));
} end_for_slist(liste,hilfe)
printf("Fehler bei ssuc\n");
exit(1);
}

Local 	Sedge 	otheredge(Snode n, Sedge e)
{
LIST 	liste,hilfe;
Sedge 	f;
/*if(!n->graph->directed) return(e->tsuc);*/
liste = XY_NODE_EDGES(n);
for_slist(liste,hilfe) {
	f = attr_data_of_type(hilfe,Sedge);
	if(OTHER_NODE(n,e) == OTHER_NODE(n,f)) 
		return(f);
} end_for_slist(liste,hilfe)
printf("Fehler bei otheredge\n");
exit(1);
}

Local	int	dual_edge_exists(Sedge e)
{
Snode 	n;
Sedge	f;
int	found = 0;
n = XY_EDGE_LNODE(e);
for_sourcelist(n,f) {
	if(f->tnode->nr == XY_EDGE_RNODE(e)->nr) 
		{
		found = 1;
		XY_EDGE_EDGE(e) = f;
		if(!e->snode->graph->directed)XY_EDGE_EDGE(e->tsuc) = f;
		break;
		}
} end_for_sourcelist(n,f)
if(found == 1)
	{
	/*printf("Dualedge already exists\n");*/
	return(1);
	}
n = XY_EDGE_RNODE(e);
for_sourcelist(n,f) {
	if(f->tnode->nr == XY_EDGE_LNODE(e)->nr) 
		{
		found = 1;
		XY_EDGE_EDGE(e) = f;
		if(!e->snode->graph->directed)XY_EDGE_EDGE(e->tsuc) = f;
		break;
		}
} end_for_sourcelist(n,f)
if(found == 1)
	{
	/*printf("Dualedge already exists\n");*/
	return(1);
	}
else    return(0);
}

/*
Local	void	remove_dual_edge(Sedge e)
{
Snode 	n;
Sedge	f,edge;
int	found = 1;
while(found == 1)
	{
	n = XY_EDGE_LNODE(e);
	found = 0;
	for_sourcelist(n,f) {
		if(f->tnode->nr == XY_EDGE_RNODE(e)->nr) 
			{
			found = 1;
			edge = f;
			break;
			}
	} end_for_sourcelist(n,f)
	if(found == 1)
		{
		remove_edge(edge);
		printf("Wrong Dualedge is now deleted\n");
		}
	}
found = 1;
while(found == 1)
	{
	n = XY_EDGE_RNODE(e);
	found = 0;
	for_sourcelist(n,f) {
		if(f->tnode->nr == XY_EDGE_LNODE(e)->nr) 
			{
			found = 1;
			edge = f;
			break;
			}
	} end_for_sourcelist(n,f)
	if(found == 1)
		{
		remove_edge(edge);
		printf("Wrong Dualedge is now deleted\n");
		}
	}
}
*/

Local  	int 	y_and_stnumber(Sgraph Dualgraph)
{
Snode 	n,m;
Sedge 	e;
int   	max_y = 0,nr = 1,i;
LIST 	queue;
INIT_LIST(queue);
QUEUE_NODE(queue,Dualgraph->nodes);
while(! IS_EMPTY_LIST(queue))
	{
	POP_NODE(queue,n);
	for_sourcelist(n,e) {
		m = OTHER_NODE(n,e);
		m->y = n->y + 1;
		max_y = m->y;
		QUEUE_NODE(queue,m);
	} end_for_sourcelist(n,e)
	}
/*printf("Dualnode oldnr.: %d newnr.: %d\n",Dualgraph->nodes->nr,1);*/
Dualgraph->nodes->nr = nr++;
for(i = 1;i < max_y + 1;i++)
	{
	for_all_nodes(Dualgraph,n) {
		if(n->y == i)
			{
			/*printf("Dualnode oldnr.: %d newnr.: %d\n",n->nr,nr);*/
			n->nr = nr++;
			}
	} end_for_all_nodes(Dualgraph,n);
	}
DUAL_GRAPH_MAX_NR(Dualgraph) = nr - 1;
DUAL_GRAPH_HEIGTH(Dualgraph) = max_y + 1;
return(max_y+1);
}

Local	void	create_dual_graph(Sgraph g)
{
Dual_graph_attr  	helpgraph;
Sgraph 			Dualgraph;
helpgraph = (Dual_graph_attr)malloc(sizeof(struct dual_graph_attr));
Dualgraph = make_graph(make_attr(ATTR_DATA,helpgraph));
Dualgraph->directed = 1;
set_graphlabel(Dualgraph,"Dual-Graph");
XY_GRAPH_GRAPH(g) = Dualgraph;
DUAL_GRAPH_GRAPH(Dualgraph) = g;
DUAL_GRAPH_MAX_NR(Dualgraph) = 0;
DUAL_GRAPH_HEIGTH(Dualgraph) = 0;
}

/*****                  der duale Graph wurde erzeugt                    ***/

Local 	char *alloc_dual_node_attrs(void)
{return (malloc(sizeof(struct dual_node_attr)));}

Local	void init_dual_node_attrs(Dual_node_attr n)
{
n->node_boundary = NULL;
n->edge_boundary = NULL;
n->high = NULL;
n->low = NULL;
n->left_path = NULL;
n->right_path = NULL;
n->father = NULL;
n->data = 0;
n->type = 0;
}

Local 	char *alloc_dual_edge_attrs(void)
{return (malloc(sizeof(struct dual_edge_attr)));}

Local	void init_dual_edge_attrs(Dual_edge_attr e)
{
e->source = NULL;
e->target = NULL;
}

Local	void	create_dual_nodes(Sgraph g)
{
Snode 	n,cursornode,node;
Sedge 	cursor;
Sgraph 	Dualgraph;
LIST	liste,hilfe,pfad;
int 	face = 1,unten,oben;
Dualgraph = XY_GRAPH_GRAPH(g);
for_all_nodes(g,n) {
	liste = XY_NODE_EDGES(n);
	for_slist(liste,hilfe) {
		cursor = attr_data_of_type(hilfe,Sedge);
		cursornode = OTHER_NODE(n,cursor);
		if((XY_EDGE_DATA(cursor) == 0)&&
		   (cursornode->nr > n->nr))
			{
 	  		node = make_node(Dualgraph,make_attr(ATTR_DATA,(char *)alloc_dual_node_attrs()));
			/*printf("DualNode Nr. %d erzeugt\n",node->nr);*/
			node->y = 0;
			node->x = 0;
			/*node.attrs = make_attr(ATTR_DATA,(char *)alloc_dual_node_attrs());
			attrs_data(node) = (char *)alloc_dual_node_attrs();*/
			init_dual_node_attrs(DUAL_NODE_ATTRS(node));
                        XY_EDGE_DATA(cursor) = face;
                        XY_EDGE_LNODE(cursor) = node;
			if(!g->directed)
				{
                        	XY_EDGE_DATA(cursor->tsuc) = face;
                        	XY_EDGE_LNODE(cursor->tsuc) = node;
				}
			/*printf("%d. face : Node(%s)",node->nr,n->label);*/
			QUEUE_NODE(DUAL_NODE_BOUND(node),n);
			QUEUE_EDGE(DUAL_NODE_BOUNDEDGE(node),cursor);
			/*printf(" Edge von (%s) nach (%s)",cursor->snode->label,cursor->tnode->label);*/
			while(cursornode != n)				
				{ 
				/*printf(" Node(%s)",cursornode->label);*/
				QUEUE_NODE(DUAL_NODE_BOUND(node),cursornode);
				cursor = (Sedge)otheredge(cursornode,cursor);
				cursor = (Sedge)ssuc(cursornode,cursor);
				QUEUE_EDGE(DUAL_NODE_BOUNDEDGE(node),cursor);
				/*printf(" Edge von (%s) nach (%s)",cursor->snode->label,cursor->tnode->label);*/
	                        if( cursornode->nr < OTHER_NODE(cursornode,cursor)->nr)
					{
				        XY_EDGE_DATA(cursor) = face;
 	                            	XY_EDGE_LNODE(cursor) = node;
					if(!g->directed)
						{
                        			XY_EDGE_DATA(cursor->tsuc) = face;
                        			XY_EDGE_LNODE(cursor->tsuc) = node;
						}
					}
 	                        else 	{
					XY_EDGE_RNODE(cursor) = node;
					if(!g->directed)
						{
                        			XY_EDGE_RNODE(cursor->tsuc) = node;
						}
					}
				cursornode = OTHER_NODE(cursornode,cursor);
				}
			/*printf("\n");*/
			face++;
			}
	} end_for_slist(liste,hilfe)
} end_for_all_nodes(g,n);
DUAL_GRAPH_MAX_NR(Dualgraph) = face-1;
/*printf("%d Dual Nodes generated\n",face-1);*/


/* in jeden Face soll nun der obere und untere Knoten gesucht werden */
for_all_nodes(Dualgraph,n) {
	unten = 0;
	oben = 100000;

	liste = DUAL_NODE_BOUND(n);
	for_slist(liste,hilfe) {
		node = attr_data_of_type(hilfe,Snode);
		if(node->nr > unten) 
			{
			DUAL_NODE_HIGH(n) = node;
			unten = node->nr;
			}
		if(node->nr < oben) 
			{
			DUAL_NODE_LOW(n) = node;
			oben = node->nr;
			}
	} end_for_slist(liste,hilfe)
	/*printf("High : %d   Low : %d\n",DUAL_NODE_HIGH(n)->nr,DUAL_NODE_LOW(n)->nr);*/
/* in jeden Face wird die boundary liste auf den unteren Knoten justiert */
	liste = DUAL_NODE_BOUND(n);
	node = attr_data_of_type(liste,Snode);
	while(node->nr != DUAL_NODE_LOW(n)->nr)
		{
		liste = DUAL_NODE_BOUND(n)->suc;
		node = attr_data_of_type(liste,Snode);
		DUAL_NODE_BOUND(n) = liste;
		}	
/* in jeden Face werden jetzt der linke und rechte Pfad gesetzt */
	liste = DUAL_NODE_BOUND(n);
	pfad = DUAL_NODE_R_PATH(n);
	for_slist(liste,hilfe) {
		node = attr_data_of_type(hilfe,Snode);
		if(node->nr == DUAL_NODE_HIGH(n)->nr)
			{
			QUEUE_NODE(pfad,node);
			DUAL_NODE_R_PATH(n) = pfad;
			pfad = DUAL_NODE_L_PATH(n);
			}
		QUEUE_NODE(pfad,node);
	} end_for_slist(liste,hilfe)
	node = attr_data_of_type(liste,Snode);
	QUEUE_NODE(pfad,node);
	DUAL_NODE_L_PATH(n) = REVERSE_NODE_LIST(pfad);
} end_for_all_nodes(Dualgraph,n);
}

Local 	void	create_dual_edges(Sgraph g)
{
LIST	liste,hilfe;
Snode 	n,esnode,etnode;
Sedge 	e,dualedge;
for_all_nodes(g,n)
	{
	liste = XY_NODE_EDGES(n);
	for_slist(liste,hilfe) {
		e = attr_data_of_type(hilfe,Sedge);
		esnode = n;
		etnode = OTHER_NODE(n,e);
		XY_EDGE_DATA(e) = 0;
		if(etnode->nr > esnode->nr)
			{
			/*printf("1:edge from %d to %d\n",esnode->nr,
				etnode->nr);*/
			if((esnode->nr != 1)||
			   (etnode->nr != XY_GRAPH_MAX_NR(g)))
            		    {
			    if(!dual_edge_exists(e))
				{
	    			dualedge = make_edge(XY_EDGE_LNODE(e),XY_EDGE_RNODE(e),make_attr(ATTR_DATA,(char *)alloc_dual_edge_attrs()));
				/*attrs_data(dualedge) = alloc_dual_edge_attrs();*/
				init_dual_edge_attrs(DUAL_EDGE_ATTRS(dualedge));
	    			XY_EDGE_EDGE(e) = dualedge;
				DUAL_EDGE_EDGE(dualedge) = e;
				if(!g->directed)XY_EDGE_EDGE(e->tsuc) = dualedge;

				/*printf("1:making edge from %d to %d\n",
					dualedge->snode->nr,dualedge->tnode->nr);*/
	    			}
			     }
			}
	} end_for_slist(liste,hilfe)
} end_for_all_nodes(g,n);
}



Local	void	justify_dual_graph(Sgraph g)
{
Snode 	n,node,lnode,rnode;
Sedge 	e,dualedge;
LIST 	liste,hilfe;
int	found = 0;
Sgraph 	Dualgraph;
Dualgraph = XY_GRAPH_GRAPH(g);
for_all_nodes(g,n)
	{
	if(n->nr == 1)
		{
		found = 1;
		/*printf("s-node found\n");*/
		liste = XY_NODE_EDGES(n);
		for_slist(liste,hilfe) {
			e = attr_data_of_type(hilfe,Sedge);
			node = OTHER_NODE(n,e);
			/*printf("found edge from %d to %d\n",
				n->nr,node->nr);*/
			if(node->nr == XY_GRAPH_MAX_NR(g))
				{
				/*printf("t-node found\n");*/
				found = 2;
				/*printf("found st-edge from %d to %d\n",
					n->nr,node->nr);*/
				lnode = XY_EDGE_LNODE(e);
				rnode = XY_EDGE_RNODE(e);
				/*printf("wanted edge from %d to %d\n",rnode->nr,lnode->nr);*/
				Dualgraph->nodes = rnode;
				XY_GRAPH_GRAPH(g) = Dualgraph;


		    		dualedge = make_edge(rnode,lnode,make_attr(ATTR_DATA,(char *)alloc_dual_edge_attrs()));
				/*attrs_data(dualedge) = alloc_dual_edge_attrs();*/
				init_dual_edge_attrs(DUAL_EDGE_ATTRS(dualedge));
		    		XY_EDGE_EDGE(e) = dualedge;
				DUAL_EDGE_EDGE(dualedge) = e;
				if(!g->directed)XY_EDGE_EDGE(e->tsuc) = dualedge;

				/*printf("1:making st-edge from %d to %d\n",
					dualedge->snode->nr,
					dualedge->tnode->nr);*/
				rnode->slist = dualedge;
				break;
				}
		} end_for_slist(liste,hilfe)
		break;
		}
} end_for_all_nodes(g,n);
if(found == 0)
	{
	printf("s-node in Graph not found\n");
	exit(1);
	}
if(found == 1)
	{
	printf("t-node in Graph not found\n");
	exit(1);
	}
}

Global	int	make_dual_graph(Sgraph g)
       	  
	/* compute level(g), faces(g),create dual nodes and edges,
	   compute level(dual_g);*/
{
create_dual_graph(g);

/*****                  der duale Graph wurde erzeugt                   ***/

create_dual_nodes(g);

/*printf("Die dualen Knoten wurden erzeugt\n");
print_my_dual_graph(XY_GRAPH_GRAPH(g));*/

/*****                  die dualen Knoten wurden erzeugt                ***/

justify_dual_graph(g);

/*printf("Die duale ST-Kante wurde erzeugt\n");
print_my_graph(g);
print_my_dual_graph(XY_GRAPH_GRAPH(g));*/
/*****                  die duale st-Kante wurde erzeugt                ***/

create_dual_edges(g);
/*
printf("Die dualen Kanten wurden erzeugt\n");
print_my_graph(g);
print_my_dual_graph(XY_GRAPH_GRAPH(g));*/

/*****                  die dualen Kanten wurden erzeugt                ***/


/*node_nr_to_label(XY_GRAPH_GRAPH(g));*/

XY_GRAPH_WIDTH(g) = y_and_stnumber(XY_GRAPH_GRAPH(g));

/*printf("Die dualen Abszissen wurden erzeugt\n");
print_my_dual_graph(XY_GRAPH_GRAPH(g));
print_my_graph(g);*/

if(!my_test_st_number(XY_GRAPH_GRAPH(g)))
	{
	printf("Dualgraph is not st-numbered\n");
	exit(0);
	}


/*printf("function make_dual_graph(g) finished\n");*/
return(XY_GRAPH_WIDTH(g));
}



/********************************************************************/
/********************** die FREE-Prozedur ***************************/
/********************************************************************/

Global	void	free_dual_graph(Sgraph g)
{
Snode n;
Sedge e;
free(attr_data(g));
for_all_nodes(g,n) {
	CLEAR_LIST(DUAL_NODE_BOUND(n));
	CLEAR_LIST(DUAL_NODE_R_PATH(n));
	CLEAR_LIST(DUAL_NODE_L_PATH(n));
	free(attr_data(n));
	for_sourcelist(n,e) {
		free(attr_data(e));
	} end_for_sourcelist(n,e)
} end_for_all_nodes(g,n)	
remove_graph(g);
}

/*********************************************************************/
/***                      END OF FILE: DUALMOD.C                   ***/
/*********************************************************************/
