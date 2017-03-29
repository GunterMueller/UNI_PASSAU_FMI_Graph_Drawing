

/******************************************************************/
/*                                                                */
/*                          MINOS.C                               */
/*     Finds the minimum cost flow in the constructed network     */
/*                                                                */
/*                                                                */
/******************************************************************/





#include "minimal_bends_layout2_export.h"



/*************************************************************/
/*                                                           */
/* Finds the shortest path from the network's general source */
/* to its general target using Dijkstra's algorithm          */
/*                                                           */
/*************************************************************/


Sedgelist  Dijkstra (Snetwork network, Sedgelist spath)
{
  Snetnode node,
           nnode,
           minnode; /* minimal distance node*/
  int      flag, 	/* UNVISITED_NODES if not visited node exists */
	   distance; 	/* to general source */
  Sedgelist  outedge,	/* edge having minnode as source */
			 newpath,	/* next edge of shortest path */
			 oldedge;
  Snetedge netedge;


  /* costs are adapted to positive costs for Dijkstra */
  while (network->edgelist->pred != NULL)
	network->edgelist = network->edgelist->pred;
  for (netedge = network->edgelist; netedge != NULL_EDGE;
	   netedge = netedge->succ)
  { 
           if (!(netedge->state & DELETED))
	         netedge->cost += netedge->snode->distance - netedge->tnode->distance;
	   /* stdeb */
	   if (netedge->cost < 0)
		netedge = netedge;
	   /* enddeb */
  }  /* for */

  /* initialize shortest path and general source's distance 0, all other distances infinite and all nodes not visited  */

  newpath = NULL_LIST;
  while (network->nodelist->pred != NULL)
	network->nodelist = network->nodelist->pred;
  for (node=network->nodelist; node != NULL_NODE; node = node->succ)
  {
   node->inedge = NULL_EDGE;
   node->state &= ~VISITED;       /* mark node as not visited */
   node->olddistance = node->distance;
   if (node == network->gsource)
   {
     node->distance = 0;
   }	/* if */
   else
   {
     node->distance = INFINITE;
   }	/* else */
  }   /* for */
   /* search node with minimal distance, mark it as visited, adjust distances of adjacent nodes and save next shortest path edge in  */
   /* minimal node's inedge until no unvisited node exists */

   flag = UNVISITED_NODES; 	
   while (flag == UNVISITED_NODES)	/* unvisited node exists */
   {
     distance = INFINITE; /*XXXX !!!! endless no minimum sonst wenn nur infinite uebrig */
     flag = NO_UNVISITED_NODES;
     while (network->nodelist->pred != NULL)
	network->nodelist = network->nodelist->pred;  
     for (nnode = network->nodelist; nnode != NULL_NODE; nnode = nnode->succ)
     {
       if ((!(nnode->state & VISITED)) && (!(nnode->state & DELETED)))  	/* nnode neither visited nor deleted */
       {
         flag = UNVISITED_NODES;
         if (nnode->distance <= distance)
         {
          minnode = nnode;
          distance = nnode->distance;
		 }	/* if smaller distance */
	   }	/* if neither visited nor deleted */
     }	/* for */
     minnode->state |= VISITED;    /* mark minnode as visited */
     if (minnode->outedgelist != NULL)
       while (minnode->outedgelist->pred != NULL)
          minnode->outedgelist = minnode->outedgelist->pred;
     for (outedge = minnode->outedgelist; outedge != NULL_LIST; outedge = outedge->succ)
     {
       if ( !(outedge->edge->tnode->state & VISITED) && 
	    (outedge->edge->cost + distance < outedge->edge->tnode->distance) &&
	    (outedge->edge->capacity > 0) && (!(outedge->edge->state & DELETED)))
		/* outedge's target not visited and new path is shorter */
       {         
         outedge->edge->tnode->distance = outedge->edge->cost + distance;
         outedge->edge->tnode->inedge = outedge->edge;
        }	/* if */
      }		/* for */
    }	/* while */

    /* build shortest path backwards from general target to general source via inedges and return it */

    if (network->gtarget->inedge != NULL)	/* shortest path exists */
    {
      spath=new_path(spath);
      spath->edge = network->gtarget->inedge;
      while (spath->edge->snode != network->gsource) 	
      {
		oldedge=spath;
        spath=insert_path(spath);
        spath->edge = oldedge->edge->snode->inedge;
      }		/* while */
    }	/* if */
    else
    {
	   free_path(spath);
	   spath = NULL_LIST;
    }	/* else */
  /* costs are readapted */
  while (network->edgelist->pred != NULL)
	network->edgelist = network->edgelist->pred;
 for (netedge = network->edgelist; netedge != NULL_EDGE;
	   netedge = netedge->succ)
  { 
	   netedge->cost = netedge->oldcost; 
  }  /* for */
  while (network->nodelist->pred != NULL)
	network->nodelist = network->nodelist->pred;
  for (node = network->nodelist; node != NULL;
	   node = node->succ)
  { 

	   node->distance += node->olddistance; 
  }  /* for */


    return(spath);
}	/* Dijkstra */





/***********************************************************/
/*														   */
/* updates network after shortest path has been found:	   */
/* reduces shortest path's edges capacities by flowchange, */
/* inserts backward edges along shortest path and		   */
/* calculates new costs of all edges					   */
/*														   */
/***********************************************************/


void ChangeNetwork (Snetwork network, Sedgelist pedges, int flowchange)

                  
                  	 /* shortest path */
               		 /* flow augmentation */
{
  int exBackedge;		/* NO_BACK_EDGE if no backward edge exists, */
						/* HELP_BACK_EDGE if help backward edge exists, */
						/* NET_BACK_EDGE if only backward edge of the */
						/* original network exists */
  Sedgelist  curedge,
			 outedge,
			 helpedge;	 /* helpedge corresponding to curedge */
  Snetedge	newedge,
			secondedge; 
  Snetnode
			newnode;

 
  while (pedges->pred != NULL)
	pedges = pedges->pred;
  while (pedges != NULL_LIST)
  {
   curedge = pedges;
   if (curedge->edge->capacity != INFINITE)
		 curedge->edge->capacity -= flowchange;
   if (curedge->edge->capacity == 0)
   {
	curedge->edge->state |= DELETED;
   }	/* if */
   exBackedge = NO_BACK_EDGE; 
   if (curedge->edge->tnode->outedgelist != NULL_LIST)
   {
	 /* check outedgelist of curedge  for backward edges */
	 if (curedge->edge->tnode->outedgelist != NULL)
            while (curedge->edge->tnode->outedgelist->pred != NULL)
		curedge->edge->tnode->outedgelist = curedge->edge->tnode->outedgelist->pred;
	 for (outedge = curedge->edge->tnode->outedgelist;
		  (outedge != NULL_LIST) && (exBackedge != HELP_BACK_EDGE) &&
		  (exBackedge != NET_BACK_EDGE);
          outedge = outedge->succ)
     {
		if (outedge->edge->father == curedge->edge)
		{
		   /* use outedge as new help backward edge */
           if (outedge->edge->capacity != INFINITE)
			   outedge->edge->capacity += flowchange;
/* lets think lets sink lets stink !!!! */
		   outedge->edge->state &= ~DELETED;
		   if (outedge->edge->tnode->type == HELPNODE)
		   /* helpnode construction exists */
		   {
			 /* get second help edge */
			 if (outedge->edge->tnode->outedgelist != NULL)
                            while (outedge->edge->tnode->outedgelist->pred != NULL)
				outedge->edge->tnode->outedgelist = outedge->edge->tnode->outedgelist->pred;
			 for (helpedge = outedge->edge->tnode->outedgelist;
				  helpedge != NULL_LIST; helpedge = helpedge->succ)
			 {
				if (helpedge->edge->father == curedge->edge)
				{
				   if (helpedge->edge->capacity != INFINITE)
				   /* capacity augmentation, mark not deleted */
                       helpedge->edge->capacity += flowchange;
				   helpedge->edge->state &= ~DELETED;
			   }  /* if second helpedge */
			}	 /* for get second help edge */
		   }	/* if helpnode */
		  exBackedge = HELP_BACK_EDGE;
	   }  /* if help edge */
	   else
	   {
		  if ((outedge->edge->tnode == curedge->edge->snode) &&
			  (exBackedge == NO_BACK_EDGE))
		  /* backward edge in original network and no help edges yet */
		  {
			 exBackedge = NET_BACK_EDGE;
		  }    /* if net back edge */
	   }  /* else  help edge */
     }   /* for outedgelist of curedge */
   }	/* if outedgelist exists */
   switch (exBackedge)
   {
	case NO_BACK_EDGE:
			/* no backward edge yet: insert help edge*/
            newedge = insert_net_edge(network, curedge->edge->tnode, curedge->edge->snode, flowchange, curedge->edge->cost * (-1));
            newedge->flow = 0;
            newedge->father = curedge->edge;
            break;
	case HELP_BACK_EDGE: break;
					 /* helpedge exists, was adapted above */
	case NET_BACK_EDGE:
			/* backedge in original network, no helpedge yet: */
			/* build bypass by inserting helpnode and two helpedges */
            newnode=InsertNetNode(&(network->nodelist), HELPNODE);
            newnode->distance = curedge->edge->snode->distance;  /* costs in edge from tnode to newnode */
			newedge = insert_net_edge(network, curedge->edge->tnode,
						  newnode,flowchange,
					          curedge->edge->cost * (- 1));
			newedge->flow = 0;
            newedge->father = curedge->edge;
			secondedge = insert_net_edge(network, newnode,
				                     curedge->edge->snode, flowchange,
						     0); /* costs already in newedge */
            secondedge->flow = 0;
            secondedge->father = curedge->edge;
            break;
   }  /* switch */
   pedges=pedges->succ;
  }   /* while */ 
}   /* Change Network */



/**************************************************************/
/*															  */
/* calculates flow change by calculating the missing flow and */
/* then the minimum of this flow and the edge capacities	  */
/* along shortest path and the missing flow 				  */
/*															  */
/**************************************************************/



int GetFlowPlus (Sedgelist spath, int flow, int flowval)

                 	/* shortest path */
         		/* existing flow */
            		/* flow to be reached */

{
  int mincap;
  Sedgelist  pedge;

  mincap = flowval - flow;
  if (spath != NULL)
    while (spath->pred != NULL)
	spath = spath->pred;
  for (pedge = spath; pedge != NULL_LIST; pedge = pedge->succ)
  { 
    if (pedge->edge->capacity < mincap)
    {
      mincap = pedge->edge->capacity;
    }   /* if */
  }  /* for */ 
  return (mincap);
 }  /* GetFlowPlus */




/*************************************************************/
/*															 */
/* calculates from edge flows and flows of the corresponding */
/* help back edges the flow of the original network 		 */
/* and marks help edges and help nodes as deleted			 */
/*															 */
/*************************************************************/


void GetMinCostFlow(Snetwork network)
{
  Snetedge	cedge,
			fatheredge; /* original network edge corresponding to cedge */
  Sedgelist  helpedge;	/* second helpedge corresponding to cedge*/

  /* mark all edges as not deleted */

  while(network->edgelist->pred != NULL)
    network->edgelist = network->edgelist->pred;
  for (cedge = network->edgelist; cedge != NULL_EDGE; cedge = cedge->succ)
  {
    cedge->state &= ~DELETED;
  }	/* for */

  /* look for helpedge construction, adapt flow of original edge */
  /* and delete helpedge construction */
  while(network->edgelist->pred != NULL)
    network->edgelist = network->edgelist->pred;

  for (cedge = network->edgelist; cedge != NULL_EDGE; cedge = cedge->succ)
  {
    if ( !(cedge->state & DELETED) && (cedge->father != NULL_EDGE) && 
         (cedge->snode->type != HELPNODE))
       /* not deleted help edge */
    {
      fatheredge = cedge->father;
	  while (fatheredge->father != NULL_EDGE)
	  /* fatheredge is another helpedge */
      {
/*stdeb*/
       if((cedge->father != NULL)&&
	  (cedge->flow > cedge->father->flow))
		cedge->flow=cedge->flow;
/*enddeb*/


        cedge->flow *= -1;
        fatheredge = fatheredge->father;
      }	  /* while */
      fatheredge->flow -= cedge->flow;
      if (cedge->tnode->type == HELPNODE)  		/* bypass via helpnode */
      {
		if (cedge->tnode->outedgelist != NULL)
		   while (cedge->tnode->outedgelist->pred != NULL)
			cedge->tnode->outedgelist = cedge->tnode->outedgelist->pred;
		for (helpedge = cedge->tnode->outedgelist;
			 helpedge != NULL_LIST; helpedge = helpedge->succ)
        {
		  if (helpedge->edge->father == cedge->father)
			/* helpedge is second edge of the bypass */
          {
			helpedge->edge->state |= DELETED;
          }   /* if second help edge */
        }    /* for second help edge */
		  cedge->tnode->state |= DELETED;
	  }   /* if bypass */
	  cedge->state |= DELETED;
    }   /* if help edge */
  }    /* for look for help edges */
}     /* GetMinCostFlow */




/***********************************************/
/*											   */
/* calculated network flow with minimal costs, */
/* returns 1 if solution exists, 0 otherwise   */
/*											   */
/***********************************************/



int MinCostFlow (Snetwork network)
{
 int sol,			 /* 1 if solution exists */
	 val,			 /* current flow value */
	 flowplus;		 /* current flow augmentation */
 Sedgelist	path,	 /* shortest path from network source to network target */
			pathedge;
 sol = 1;
 val = 0;
 flowplus = 0;
path = NULL_LIST;

while (sol && (val < network->flow))
  {
   if (val != 0)				 /* start with original network */
    { 
	 /* build help network according to shortest path */
     ChangeNetwork(network, path, flowplus);
    }    /* if */
   path = Dijkstra(network, path);			/* build shortest path */
   if (path == NULL_LIST)                      /* no solution exists */
    {
     sol = 0;
    }   /* if */
   else 
    {
     flowplus = GetFlowPlus (path, val, network->flow);     
     val += flowplus;
	 /* flow augmentation along shortest path */
     if (path != NULL)
       while (path->pred != NULL)
	path = path->pred;
     for (pathedge = path; pathedge != NULL_LIST; pathedge = pathedge->succ)
	  {
       pathedge->edge->flow += flowplus;
/*stdeb*/
       if((pathedge->edge->father != NULL)&&
	  (pathedge->edge->flow > pathedge->edge->father->flow))
		flowplus=flowplus;
/*enddeb*/
      }   /* for */
    }   /* else */
  }   /* while */
  free_path(path);

 if (sol) 
  /* calculate minimum cost flow of original network */
  { 
   GetMinCostFlow(network);
  }  /* if */

 return (sol);
} /* MinCostFlow */


