/****************************************************************/
/*                                                              */
/*   trans_to.c                                                 */
/*   transformation of a graph to an equivalent network         */
/*                                                              */
/*                                                              */
/*                                                              */
/****************************************************************/

#include "minimal_bends_layout2_export.h"

/*************************/
/*                       */
/* counts node's sources */
/*                       */
/*************************/

int no_of_sources(Snode node)
{
    Sedge tempedge;
    int nos;
    nos = 0;
    for_sourcelist (node , tempedge)
    {
        nos++;
    }
    end_for_sourcelist(node, tempedge);
    return nos;
} /* no_of_sources */


/**************************/
/*                        */
/* counts Face's elements */
/*                        */
/**************************/


int no_of_elements(SFace Face)
{
    SFaceElement StartEl, TempEl;
    int nos;

    StartEl=TempEl=Face->elements;
    nos=0;

    do
    {
	nos++;
	TempEl=TempEl->succ;
    }
    while(TempEl!=StartEl);
 return nos;
}   /* no_of_elements */

/******************************************/
/*                                        */
/*     builds Face - Node EDGES           */
/*                                        */
/******************************************/
void process_edge_fv(Snetwork network, SFaceElement elements, Snetnode tnode)
{
    Snetnode snode;
    SFaceElement startelement;

    startelement = elements;
    while(elements!=NULL_ELEMENT)    
    {
        while (network->nodelist->pred != NULL)
              network->nodelist = network->nodelist->pred;
        snode=network->nodelist;
        while(snode!=NULL_NODE)
        {
            if((snode->type==WAS_NODE)&&
               (elements->snode==snode->real.node))
                insert_net_edge(network,
                                snode,
                                tnode,
                                INFINITE,
                                0);

            snode=snode->succ;
	    
        }
        elements=elements->succ;
        if (elements==startelement)
	    break;
    }
}  /* process_edge_fv */


/********************************************************/
/*                                                      */
/* inserts edges resulting of nodes belonging to a face */
/*                                                      */
/********************************************************/


void insert_fv_edges(Snetwork network, SFaceList sFaceList)
{
    Snetnode node;

    while (network->nodelist->pred != NULL)
              network->nodelist = network->nodelist->pred;
    node=network->nodelist;
    while (node!=NULL_NODE)
    {
        if(node->type==INNER_FACE)
            process_edge_fv(network, node->real.face->elements, node);
        else if(node->type==OUTER_FACE)
            process_edge_fv(network, sFaceList->OuterFace->elements, node);
        node=node->succ;
    }
}  /* insert_fv_edges */


/********************************************************/
/*                                                      */
/* returns true if nodes of edge and compedge are equal */
/*                                                      */
/********************************************************/


int same_edge(Sedge edge, Sedge compedge)
{
    if (((edge->tnode==compedge->snode)&&
         (edge->snode==compedge->tnode))||
        ((edge->snode==compedge->snode)&&
         (edge->tnode==compedge->tnode)))
        return TRUE;
    return FALSE;
}  /* same_edge */


/***************************************************************/
/*                                                             */
/* returns true if face containing faceelement contains bridge */
/*                                                             */
/***************************************************************/


int bridge_in_face(SFaceElement faceelement)
{
    SFaceElement compfaceel,start,startcomp;

    if(faceelement==NULL_ELEMENT)
	return FALSE;

    start=faceelement;
    while(faceelement->succ!=start)
    {
        compfaceel=faceelement->succ;
        startcomp=compfaceel;
        while(compfaceel->succ!=startcomp)
        {
            if(same_edge(faceelement->sedge,compfaceel->sedge))
                return TRUE;
            compfaceel=compfaceel->succ;
        }
        faceelement=faceelement->succ;
    }
    return FALSE;
}  /* bridge_in_face */


/***********************************************************/
/*                                                         */
/* returns true if two faces have at least one common edge */
/*                                                         */
/***********************************************************/

int face_face(SFaceElement face, SFaceElement compface)
{
    SFaceElement compstartface, /* BBB */
                 startface;
    startface=face;

    while(face!=NULL_ELEMENT)
    {
        /* compface=face->succ; */ /* ????? BBB */
        compstartface = compface;  /* BBB */
        while(compface!=NULL_ELEMENT)
        {
            if(same_edge(face->sedge,compface->sedge))
                return TRUE;
            compface=compface->succ;
            if(compface == compstartface)
		break;				/* BBB */ 
        }
        face=face->succ;
	if(face==startface)
	    break;
    }
    return FALSE;
}  /* face_face */


/***********************************************/
/*                                             */
/*     builds Face - Face EDGES                */
/*                                             */
/***********************************************/

void insert_ff_edges(Snetwork network, SFaceList sFaceList)
{
    Snetnode node;
    Snetnode compnode;

    while (network->nodelist->pred != NULL)
         network->nodelist = network->nodelist->pred;
    node = network->nodelist;
    while(node!=NULL_NODE)
    {
        if ((node->type==INNER_FACE)||
            (node->type==OUTER_FACE))
        {
            /* check for bridge */
            if (bridge_in_face((node->type==INNER_FACE)?
                               node->real.face->elements:
                               sFaceList->OuterFace->elements))
                insert_net_edge(network,node,node,INFINITE,1);

            /* compare through the rest of the list */
            compnode=node->succ;
            while(compnode!=NULL_NODE)
            {	
               if ((compnode->type==INNER_FACE)||
                   (compnode->type==OUTER_FACE))
               {
                     if(face_face((node->type==INNER_FACE)?
                                   node->real.face->elements:
                                   sFaceList->OuterFace->elements,
                                   (compnode->type==INNER_FACE)?
                                   compnode->real.face->elements:
                                   sFaceList->OuterFace->elements))
                    {
                        insert_net_edge(network,node,compnode,INFINITE,1);
                        insert_net_edge(network,compnode,node,INFINITE,1);
                    }  /* if face_face */
               }  /* if compnode is face */
               compnode=compnode->succ;
            }  /* while compnode */
        }  /* if node is face */
        node=node->succ;
    }   /* while node */
} /* insert_ff_edges */


/****************************************************************************/
/*                                                                          */
/*            Global Source - Face / Edge EDGES                             */
/* insert edges from source to either inner faces with less than 4 elements */
/* or nodes included in the network (wheight < 4)                           */
/*                                                                          */
/****************************************************************************/

void insert_sf_sv_edges(Snetwork network, SFaceList sFaceList)
{
    int length;
    Snetnode netnode;
 
    while (network->nodelist->pred != NULL)
         network->nodelist = network->nodelist->pred;
    netnode=network->nodelist;

    while(netnode != NULL)
    {
        if((netnode->type==INNER_FACE)&&
           ((length=no_of_elements(netnode->real.face)) < 4 ))
        {
            insert_net_edge(network,
                            network->gsource,
                            netnode,
                            4 - length,
                            0);
            network->flow += 4 - length;
        }
        else if(netnode->type==WAS_NODE)
        {
            insert_net_edge(network,
                            network->gsource,
                            netnode,
                            length = 4 - no_of_sources(netnode->real.node),
                            0);
            network->flow += length;
        }
	netnode=netnode->succ;
    }
} /* insert_sf_sv_edges */


/***************************************************************/
/*                                                             */
/*     builds Face / Edge - Global Target EDGES                */
/*                                                             */
/***************************************************************/

void insert_ft_vt_edges(Snetwork network, SFaceList sFaceList)
{
    int length;
    Snetnode netnode;

    while (network->nodelist->pred != NULL)
         network->nodelist = network->nodelist->pred;
    netnode=network->nodelist;

    while(netnode != NULL)
    {
        if(netnode->type==INNER_FACE)
        {      
           length = no_of_elements(netnode->real.face);  /* BBB */
           if (length > 4)
           {
             insert_net_edge(network,
                             netnode,
                             network->gtarget,
                             length - 4,
                             0);
           } /* if length > 4 */
        }  /* if nodetype is inner face */
        else if(netnode->type==OUTER_FACE)
        {
            insert_net_edge(network,
                            netnode,
                            network->gtarget,
                            /*!!!! achtung*/
                            no_of_elements(sFaceList->OuterFace) + 4,
                            0);
        } /* if nodetype is outer face */
        netnode=netnode->succ;
    }  /* while netnode */
}  /* insert_ft_vt_edges */


/*******************************************************/
/*                                                     */
/* inverts direction and order of the faces' elements: */
/* the faces must be on the right hand side of their   */
/* elements                                            */
/*                                                     */
/*******************************************************/



SFaceList invert_face_elements(SFaceList sFaceList)
{
    SFaceElement element,
		 startelement,
                 nextelement;
    SFace face;
    Snode Tempnode;
 
    face = sFaceList->OuterFace;
    element = startelement = face->elements;
    do
    {
        nextelement = element->succ;
        Tempnode = element->tnode;
        element->tnode = element->snode;
	element->snode = Tempnode;
       /* element->succ = element->pred;
        element->pred = nextelement; */
        element = nextelement;
    }   /* do */
    while(element != startelement);
    while (sFaceList->InnerFaces->pred != NULL)
          sFaceList->InnerFaces = sFaceList->InnerFaces->pred;
    for(face = sFaceList->InnerFaces; face != NULL; face = face->succ)
    {    
        	element = startelement = face->elements;
    		do
    		{
			nextelement = element->succ;
        		Tempnode = element->tnode;
        		element->tnode = element->snode;
			element->snode = Tempnode;
			/*element->succ = element->pred;
        		element->pred = nextelement;*/
			element = nextelement;
    		}    /* do */
    		while(element != startelement);
    }   /* for inner faces */
    return sFaceList;
}    /* invert_face_elements */



/***********************************************/
/*                                             */
/* main function to transform graph to network */
/*                                             */
/***********************************************/


Snetwork TransformToNetwork(Sgraph sGraph, SFaceList sFaceList)
{
    Snetwork network;
    Snode    TempNode;
    SFace    TempFace;
    Snetnode netnode;

    /* invert elements: faces to the right */
   /* invert_face_elements(sFaceList); */

    /* Allocate the space for network */
    network = new_network(); 

    /* insert global source */
    network->gsource=InsertNetNode(&network->nodelist,G_SOURCE);

    /* insert global target */
    network->gtarget=InsertNetNode(&network->nodelist,G_TARGET);

    /* insert nodes into nodelist */
    for_all_nodes(sGraph,TempNode)
    {
        /* only nodes with weihgt of edges < 4 */
        if (no_of_sources(TempNode)<4)
        {
            netnode=InsertNetNode(&network->nodelist, WAS_NODE);
            NODE_ATTR(TempNode)->netnode=netnode;
            netnode->real.node=TempNode;
        }
    }
    end_for_all_nodes(sGraph,TempNode);

    /* insert faces into nodelist */
    while (sFaceList->InnerFaces->pred != NULL)
          sFaceList->InnerFaces = sFaceList->InnerFaces->pred;
    TempFace=sFaceList->InnerFaces;
    while(TempFace!=NULL_FACE)
    {
        netnode=InsertNetNode(&network->nodelist,INNER_FACE);
        TempFace->netnode=netnode;
        netnode->real.face=TempFace;
        TempFace=TempFace->succ;
    }
    netnode=InsertNetNode(&network->nodelist, OUTER_FACE);
    sFaceList->OuterFace->netnode=netnode;
    netnode->real.face=sFaceList->OuterFace;

    insert_fv_edges(network,sFaceList);
    insert_ff_edges(network,sFaceList);
    insert_sf_sv_edges(network,sFaceList);
    insert_ft_vt_edges(network,sFaceList);


    return network;
}  /* TransformToNetwork */

