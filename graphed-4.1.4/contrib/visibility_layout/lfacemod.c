/******************************************************/
/*					  	      */
/*                    LFACEMOD.C                      */
/*					     	      */
/*  (sucht das groesste Face und legt eine Dummy      */
/*  ST-Kante mitten durch			      */
/******************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "listen.h"
#include "tarjanmod.h"

Local 	LIST 	Spre(Snode n, Sedge e);
Global  int     ordinate(Sgraph graph);
Global  Sedge   createdummyedge(Snode snode, Snode tnode);

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


Local 	LIST 	Spre(Snode n, Sedge e)
{
LIST 	liste,hilfe;
Sedge 	f;
liste = XY_NODE_EDGES(n);
for_slist(liste,hilfe) {
	f = attr_data_of_type(hilfe,Sedge);
	if(f == e) return((LIST)hilfe->pre);
} end_for_slist(liste,hilfe)
printf("Fehler bei Spre\n");
exit(1);
}


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

/*
Local 	LIST 	Ssuc(Snode n, Sedge e)
{
LIST liste,hilfe;
Sedge f;
liste = XY_NODE_EDGES(n);
for_slist(liste,hilfe) {
	f = attr_data_of_type(hilfe,Sedge);
	if(f == e) return((LIST)hilfe->suc);
} end_for_slist(liste,hilfe)
printf("Fehler bei Ssuc\n");
exit(1);
}
*/

Local 	LIST 	edge_list(Snode n, Sedge e)
{
LIST 	liste,hilfe;
Sedge 	f;
liste = XY_NODE_EDGES(n);
for_slist(liste,hilfe) {
	f = attr_data_of_type(hilfe,Sedge);
	if(OTHER_NODE(n,e) == OTHER_NODE(n,f)) 
		return(hilfe);
} end_for_slist(liste,hilfe)
printf("Fehler bei edge_list\n");
exit(1);
}

Local 	Sedge 	OTHEREDGE(Snode n, Sedge e)
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

Local	Sedge	edge_exists(Snode sou, Snode tar)
{
Snode 	n;
Sedge	f;
Sedge	found = NULL;
n = sou;
for_sourcelist(n,f) {
	if(f->tnode->nr == tar->nr) 
		{
		found = f;
		break;
		}
} end_for_sourcelist(n,f)
if(found)
	{
	/*printf("edge from %d to %d already exists\n",sou->nr,tar->nr);*/
	return(f);
	}
n = tar;
for_sourcelist(n,f) {
	if(f->tnode->nr == sou->nr) 
		{
		found = f;
		break;
		}
} end_for_sourcelist(n,f)
if(found)
	{
	/*printf("edge from %d to %d already exists\n",tar->nr,sou->nr);*/
	return(f);
	}
else    return(NULL);
}

Local	int	find_best_Maxnode(Snode Maxnode, Sedge Maxedge, int Maxcount)
{
Snode 	dummynode;
Sedge	dummy;
int	max = 0,i,count = 0;
dummynode = Maxnode;
dummy = Maxedge;

for(i = 0;i< Maxcount;i++) 
	{	
	if(XY_NODE_DATA(dummynode) > max)
		{
		max = XY_NODE_DATA(dummynode);
		Maxnode = dummynode;
		Maxedge = dummy;
		/*printf("Neuer Maxnode nr:%d label:%s\n",Maxnode->nr,Maxnode->label);*/
		count= i;
		}
	dummynode = OTHER_NODE(dummynode,dummy);
	dummy = OTHEREDGE(dummynode,dummy);
	dummy = ssuc(dummynode,dummy);
	}
return(count);
}

/***********************************************************/
/*							   */
/* Mit der folgenden Prozedur werden Dummy-Kanten in       */
/*      interne Struktur des Graphen eingebaut             */
/*							   */
/***********************************************************/

Local 	void 	make_dummy_right(Snode n, Sedge e, int nr, Sedge dummy)
{
LIST 	liste;
Sedge 	f;
Snode 	fsnode,dummynode;
int 	i;
INIT_LIST(liste);
f = e;
fsnode = n;
for(i = 0;i<nr;i++) 
	{
	fsnode = OTHER_NODE(fsnode,f);
	f = OTHEREDGE(fsnode,f);
	f = ssuc(fsnode,f);
	}
dummynode = OTHER_NODE(fsnode,f);
if(dummynode != n)
	{
	liste = XY_NODE_EDGES(n);
	XY_NODE_EDGES(n) = (LIST)edge_list(n,e);
	PUSH_EDGE(XY_NODE_EDGES(n),dummy);
	XY_NODE_EDGES(n) = liste;
	liste = XY_NODE_EDGES(fsnode);
	XY_NODE_EDGES(fsnode) = (LIST)edge_list(fsnode,f);
	if(!n->graph->directed)dummy = dummy->tsuc;
	PUSH_EDGE(XY_NODE_EDGES(fsnode),dummy);
	XY_NODE_EDGES(fsnode) = liste;
	/*printf("Dummyedge sou nr:%d label:%s tou nr:%d label:%s\n",n->nr,n->label,fsnode->nr,fsnode->label);*/
	}
}

/************************************************************/
/*							    */
/* biconnect wandert alle Flaechen im Graphen aus, sucht    */
/* sich die Groesste und legt sie als 'outer face' um den   */
/* Graphen. Mit einigen Zeilen Ergaenzung ueberprueft       */
/* biconnect den Graphen auf zweifachen Zusammenhang  oder  */
/* kann diese durch Einfuegen von Kanten herstellen.        */
/*       						    */
/************************************************************/

Global 	Sgraph 	lface(Sgraph g)
{
Snode 	n,cursornode,Maxnode,dummynode;
Sedge 	e,dummy,cursor,Maxedge;
int 	Maxcount = 1,face = 1,count,i=1;
LIST 	liste,hilfe;
message("Large Face Option activated\n");
/*printf("Searching largest face ...\n");*/
for_all_nodes(g,n)
	{
	liste = XY_NODE_EDGES(n);
	for_slist(liste,hilfe) {
		cursor = attr_data_of_type(hilfe,Sedge);
		cursornode = OTHER_NODE(n,cursor);
		if((XY_EDGE_DATA(cursor) == 0)&&
		   (cursornode->nr > n->nr))
			{
			count = 1;
			XY_EDGE_DATA(cursor) = face;
			/*if(!g->directed) XY_EDGE_DATA(cursor->tsuc) = face;*/
			/*printf("%d. face : Node(%s)",i++,n->label);*/
			while(cursornode != n)
				{ 
				/*printf(" Node(%s)",cursornode->label);*/
				cursor = (Sedge)OTHEREDGE(cursornode,cursor);
				cursor = (Sedge)ssuc(cursornode,cursor);
	                        if( cursornode->nr < OTHER_NODE(cursornode,cursor)->nr)
					{
				        XY_EDGE_DATA(cursor) = face;
					/*if(!g->directed)XY_EDGE_DATA(cursor->tsuc) = face;*/
					}
 	                        /*else 	{
					if(!g->directed)XY_EDGE_DATA(cursor->tsuc) = face;
					}*/
				cursornode = OTHER_NODE(cursornode,cursor);
				count++;
				}
			/*printf("\n");*/
			if(count > Maxcount)
				{
				Maxcount = count;
				Maxedge = (Sedge)ssuc(cursornode,(Sedge)OTHEREDGE(cursornode,cursor));
				Maxnode = n;
				}
			face++;
			}
	} end_for_slist(liste,hilfe)
} end_for_all_nodes(g,n);
for_all_nodes(g,n)
	{
	liste = XY_NODE_EDGES(n);
	for_slist(liste,hilfe) {
		e = attr_data_of_type(hilfe,Sedge);
		XY_EDGE_DATA(e) = 0;
	} end_for_slist(liste,hilfe)
} end_for_all_nodes(g,n);
/*printf("Largest face with %d nodes\n",Maxcount);*/
if(Maxcount > 3)
	{
	message("Largest face with %d nodes\n",Maxcount);
	/*printf("Maxnode nr:%d label:%s\n",Maxnode->nr,Maxnode->label);
	printf("Maxedge sou nr:%d label:%s tou nr:%d label:%s\n",Maxedge->snode->nr,Maxedge->snode->label,Maxedge->tnode->nr,Maxedge->tnode->label);*/
	
	dummynode = Maxnode;
	dummy = Maxedge;

	for(i = 0;i< Maxcount;i++) 
		{
		XY_NODE_DATA(dummynode) = 0;
		liste = XY_NODE_EDGES(dummynode);
		for_slist(liste,hilfe) {
			e = attr_data_of_type(hilfe,Sedge);
			XY_NODE_DATA(dummynode)++;
		} end_for_slist(liste,hilfe)
		
		dummynode = OTHER_NODE(dummynode,dummy);
		dummy = OTHEREDGE(dummynode,dummy);
		dummy = ssuc(dummynode,dummy);
		}
		count = find_best_Maxnode(Maxnode,Maxedge,Maxcount);
		for(i = 0;i< count;i++)
			{
			Maxnode = OTHER_NODE(Maxnode,Maxedge);
			Maxedge = OTHEREDGE(Maxnode,Maxedge);
			Maxedge = ssuc(Maxnode,Maxedge);
			}
	/*printf("Maxnode nr:%d label:%s\n",Maxnode->nr,Maxnode->label);
	printf("Maxedge sou nr:%d label:%s tou nr:%d label:%s\n",Maxedge->snode->nr,Maxedge->snode->label,Maxedge->tnode->nr,Maxedge->tnode->label);*/

		XY_NODE_DATA(Maxnode) = 0;
		dummynode = Maxnode;
		dummy = Maxedge;

		for(i = 0;i< (Maxcount+1)/2;i++)
			{
			dummynode = OTHER_NODE(dummynode,dummy);
			dummy = OTHEREDGE(dummynode,dummy);
			dummy = ssuc(dummynode,dummy);
			}

	g->nodes = Maxnode;
	if((e = edge_exists(Maxnode,dummynode)))
		{
		/*printf("Old ST-edge sou nr:%d label:%s tou nr:%d label:%s\n",e->snode->nr,e->snode->label,e->tnode->nr,e->tnode->label);*/
		g->nodes = e->snode;
		g->nodes->slist = e;
		my_st_number(g);
		/*printf("neu stnummeriert\n");*/
		}
	else  	{
	      	e = (Sedge)createdummyedge(Maxnode,dummynode);
		XY_GRAPH_DUMMY(g) = e;
		/*printf("New ST-edge sou nr:%d label:%s tou nr:%d label:%s\n",e->snode->nr,e->snode->label,e->tnode->nr,e->tnode->label);*/
		g->nodes->slist = e;
		my_st_number(g);
		/*printf("neu stnummeriert\n");
		printf("Maxnode nr:%d label:%s\n",Maxnode->nr,Maxnode->label);
		printf("Maxedge sou nr:%d label:%s tou nr:%d label:%s\n",Maxedge->snode->nr,Maxedge->snode->label,Maxedge->tnode->nr,Maxedge->tnode->label);*/
		make_dummy_right(Maxnode,Maxedge,((Maxcount+1)/2),e);
		XY_NODE_EDGES(Maxnode) = (LIST)(Spre(Maxnode,Maxedge));
		}
	for_all_nodes(g,n)
		{
		n->y = 0;
		XY_NODE_DATA(n) = 0;
	} end_for_all_nodes(g,n)
	XY_GRAPH_HEIGTH(g) = ordinate(g);
	}
/*printf("Dummy edge inserted\n");*/
return(g);
}
