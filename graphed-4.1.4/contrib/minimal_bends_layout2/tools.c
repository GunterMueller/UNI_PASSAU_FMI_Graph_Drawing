/****************************************************************/
/*                                                              */
/*   tools.c                                                    */
/*   general toos                                               */
/*                                                              */
/*                                                              */
/*                                                              */
/****************************************************************/

#include "minimal_bends_layout2_export.h"






/****************************************************************/
/*                                                              */
/* set all node attribute->gridpoint NULL                       */
/*                                                              */
/****************************************************************/

void set_gridpoint_null(Sgraph sGraph)
{
    Snode node;

    for_all_nodes(sGraph,node)
    {
        NODE_ATTR(node)->gridpoint=NULL;
    }
    end_for_all_nodes(sGraph,node);
} /* set_gridpoint_null */





/****************************************************************/
/*                                                              */
/* set all edges unvisited                                      */
/*                                                              */
/****************************************************************/


void reset_edge_visited(Sgraph sGraph)
{
    Sedge edge;

    for_all_edges(sGraph,edge)
    {
        EDGE_ATTR(edge)->visited=0;
    }
    end_for_all_edges(sGraph,edge);
} /* reset_edge_visited */



/***********************/
/*                     */
/* allocates SFaceList */
/*                     */
/***********************/


SFaceList new_Facelist(void)
{
    SFaceList newList;
    
    newList=(SFaceList)malloc(sizeof(struct sFacelist));
    
    newList->OuterFace=NULL;
    newList->InnerFaces=NULL;

    return newList;
} /* new_Facelist */


/****************************************************************/
/*                                                              */
/* Insert an element at the top of the facelist                 */
/* a facelist is organized as a ring                            */
/*                                                              */
/****************************************************************/

SFaceElement insert_FaceElement(SFaceElement elements, Snode node, Sedge edge)
{
    SFaceElement element;

    element=(SFaceElement)malloc(sizeof(struct sFaceElement));
    element->shape=NULL;
    element->succ=NULL;
    element->pred=NULL;
    element->snode=node;
    element->sedge=edge;
    if (elements!=NULL_ELEMENT)
    {
        element->succ=elements;
        element->pred=elements->pred;
        elements->pred->succ=element;
        elements->pred=element;
    }
    else
    {
        element->succ=element;
        element->pred=element;
    }
    return element;
}  /* insert_FaceElement */


/*********************************************/
/*                                           */
/* allocates face and inserts it in facelist */
/*                                           */
/*********************************************/

SFace insert_Face(SFace Face)
{
    SFace NewFace;
 
    NewFace=(SFace)malloc(sizeof(struct sFace));
    
    /* initialize with NULL */
    NewFace->elements=NULL;
    NewFace->connection=NULL;
    NewFace->blocklist=NULL;
    NewFace->netnode=NULL;
    NewFace->facegrid=NULL;
    NewFace->linesno =0;
    NewFace->pred=NULL;
    NewFace->succ=NULL;    
   
    if(Face==NULL_FACE)
    {
        return NewFace;
    }
    else
    {
        Face->pred=NewFace;
        NewFace->succ=Face;
        return NewFace;
    }
} /* insert_Face */
 


/****************************************************************/
/*                                                              */
/* Inserting and deleting the attribute-structure for nodes     */
/* and edges                                                    */
/*                                                              */
/****************************************************************/

void insert_attributes(Sgraph sGraph)
{
    Snode       node;
    Sedge       edge;
    SEdgeAttr   EdgeAttr;
    SNodeAttr   NodeAttr;

    for_all_nodes(sGraph,node)
    {
        NodeAttr=(SNodeAttr)malloc(sizeof(struct sNodeAttr));
        NodeAttr->netnode = NULL;
        NodeAttr->attrs=node->attrs;
        node->attrs=make_attr(ATTR_DATA,(char *)NodeAttr);
    }
    end_for_all_nodes(sGraph,node);

    for_all_edges(sGraph,edge)
    {
      /*if((EdgeAttr=(SEdgeAttr)attr_data(edge))==NULL)*/
      if(get_unique_edge_handle(edge)==edge) 	
      {
            EdgeAttr=(SEdgeAttr)malloc(sizeof(struct sNodeAttr));
       	    EdgeAttr->Face1 = NULL;
            EdgeAttr->Face2 = NULL;
            EdgeAttr->attrs = edge->attrs;
            EdgeAttr->visited = 0;
            set_edgeattrs(edge, make_attr(ATTR_DATA,(char *)EdgeAttr));
      }
    }
    end_for_all_edges(sGraph,edge);
}  /* insert_attributes */


/*********************************************/
/*                                           */
/* frees Snode and Sedge attributes          */
/*                                           */
/*********************************************/


void free_attributes(Sgraph sGraph)
{
    Snode       node;
    Sedge       edge;

    for_all_nodes(sGraph,node)
    {
        free(attr_data(node));
        node->attrs=make_attr(ATTR_DATA,NULL);
    }
    end_for_all_nodes(sGraph,node);

    for_all_edges(sGraph,edge)
    {
        if (attr_data(edge) != NULL) {
	   free(attr_data(edge));
	}
        edge->attrs=make_attr(ATTR_DATA,NULL);
    }
    end_for_all_edges(sGraph,edge);
}  /* free_attributes */



/*********************************************/
/*                                           */
/* tools for the handling of the nonrel list */
/*                                           */
/*********************************************/

void free_NonRelatedList (SNonRelatedList nlist)
{
    SNonRelatedList tmplist;
    
   while (nlist != NULL)
	{
	tmplist = nlist->succ;
	free (nlist);
	nlist = tmplist;
	}
   }

SNonRelatedList
prepend_NonRelatedList (SNonRelatedList nlist, SGridLine line1, SGridLine line2)
{
    SNonRelatedList newElement;

    newElement =  (struct snonrelatedlist *)malloc(sizeof(struct snonrelatedlist));
    newElement->line1=line1;
    newElement->line2=line2;
    newElement->relation=_NONE;
    newElement->succ=nlist;
    return newElement;
    }

