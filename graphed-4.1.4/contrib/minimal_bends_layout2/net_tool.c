/****************************************************************/
/*                                                              */
/*   net_tool.c                                                 */
/*   tools for maintaining the network                          */
/*                                                              */
/*                                                              */
/*                                                              */
/****************************************************************/

#include "minimal_bends_layout2_export.h"

/**********************/
/*                    */
/* allocates Snetwork */
/*                    */
/**********************/


Snetwork new_network(void)
{
    Snetwork network;

    network=(Snetwork)malloc(sizeof(struct snetwork));
    network->gsource=NULL;
    network->gtarget=NULL;
    network->nodelist=NULL;
    network->edgelist=NULL;
    network->flow=0;
    return network;
}  /* new_network */


/***************************************/
/*                                     */
/* allocates Snetnode and inserts it   */
/* in nodelist                         */
/*                                     */
/***************************************/

Snetnode InsertNetNode(Snetnode *pnetnode, enum netnode type)
{
    Snetnode netnode;

    netnode= (Snetnode) malloc ( sizeof(struct snetnode) );
    netnode->distance = 0;
    netnode->olddistance = 0;
    netnode->state = 0;
    netnode->inedge = NULL;
    netnode->outedgelist = NULL;
    netnode->pred = NULL;
    netnode->succ = NULL;
    netnode->type = type;
    if(*pnetnode==(Snetnode)NULL)
    {
        *pnetnode=netnode;
    }
    else
    {
        while ((*pnetnode)->pred != NULL)
            *pnetnode = (*pnetnode)->pred;
        netnode->succ=*pnetnode;
        (*pnetnode)->pred=netnode;
        *pnetnode=netnode;
    }
    return netnode;
}  /* InsertNetNode */

/***************************************/
/*                                     */
/* allocates and inserts an edge into  */
/* the network and connects            */
/* the corresponding nodes             */
/*                                     */
/***************************************/

Snetedge insert_net_edge(Snetwork network, Snetnode snode, Snetnode tnode, int capacity, int cost)
{
    Snetedge  edge;
    Sedgelist tlist;

    edge = (Snetedge)malloc(sizeof(struct snetedge));
    edge->flow = 0;
    edge->state = 0;
    edge->father = NULL;
    edge->pred = NULL;
    edge->succ = NULL;
    if ( network->edgelist==NULL )
    {
        /* list was empty */
        network->edgelist=edge;
    }
    else
    {
        /* insert edge at top of list */
        while (network->edgelist->pred != NULL)
           network->edgelist = network->edgelist->pred;
        network->edgelist->pred = edge;
        edge->succ = network->edgelist;
        network->edgelist = edge;
    }
    /* fill structure */
    edge->snode=snode;
    edge->tnode=tnode;
    edge->capacity=capacity;
    edge->cost=cost;
    edge->oldcost = cost;
    /* now connect the sourcenode */
    tlist = (Sedgelist)calloc(1,sizeof(struct sedgelist));
    tlist->pred = NULL;
    tlist->succ = NULL;
    tlist->edge = edge;
    if (snode->outedgelist == (struct sedgelist *)NULL)
    {
           snode->outedgelist=tlist;
    }
    else
    {
        while(snode->outedgelist->pred != NULL)
		snode->outedgelist = snode->outedgelist->pred;
        tlist->succ=snode->outedgelist;
        snode->outedgelist->pred=tlist;
        snode->outedgelist=tlist;
    }
		return edge;
}  /* insert_net_edge */


/***********************************************/
/*											   */
/* allocates Sedgelist and frees spath		   */
/*											   */
/***********************************************/


Sedgelist new_path(Sedgelist spath)
{
    Sedgelist path;

	if (spath!=NULL_LIST)
	free_path(spath);
	
    path=(Sedgelist)malloc(sizeof( struct sedgelist));
    path->edge=NULL;
    path->pred=NULL;
    path->succ=NULL;

    return (path);
}  /* new_path */



/**********************************************/
/*											  */
/* allocates Sedgelist and iserts it in spath */
/*											  */
/**********************************************/

Sedgelist insert_path(Sedgelist spath)
{
    Sedgelist path;

    path=(Sedgelist)malloc(sizeof( struct sedgelist));
    while (spath->pred != NULL)
          spath = spath->pred;
    path->succ=spath;
	path->pred=NULL;
    path->edge=NULL;
    spath->pred=path;
    
    return (path);
} /* insert_path */


/****************************************/
/*										*/
/* frees path including preds and succs */
/*										*/	 
/****************************************/


void free_path(Sedgelist path)
{
    Sedgelist next;

    if (path != NULL_LIST)
    {
        while (path->pred!=NULL)
		path=path->pred;
	    while(path !=NULL)
		{
		   next = path->succ;
		   free(path);
		   path=next;
		}
     }
}  /* free_path */
	
	
    

    
