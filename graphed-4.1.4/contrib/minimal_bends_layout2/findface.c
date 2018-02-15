
/****************************************************************/
/*                                                              */
/*   findface.c                                                 */
/*   Main include for function global types and definitions     */
/*                                                              */
/*                                                              */
/*                                                              */
/****************************************************************/

#include "minimal_bends_layout2_export.h"

#define MAX_ANGLE 4.0


/****************************************************************/
/*                                                              */
/* Computes a pseudo angle between three nodes                  */
/* Value ranges between 0 and 4                                 */
/*                                                              */
/****************************************************************/

float get_angle(Snode base, Spoint sfrom, Spoint sto)
{
    float angle1, angle2, angle;
    float xDiff, yDiff, tangente;

    /* normalize the coordinates */
    xDiff = (float)(sfrom->x - base->x );
    yDiff = (float)(base->y - sfrom->y );
    tangente=sqrt((xDiff * xDiff)+(yDiff * yDiff));  /* BBB  pow is nich */
    xDiff/=tangente;
    yDiff/=tangente;

    /* compute the first pseudo angle */
    if((xDiff>0.0)&&(yDiff>0.0))
    {
        /* 1. quadrant, range >0 <1 */
        angle1=yDiff;
    }
    else if (xDiff<=0.0)
    {
        /* 2. and 3. quadrant, range >=1 <=3 */
        angle1=2-yDiff;
    }
    else
    {
        /* 4. quadrant */
        angle1=3+xDiff;
    }

    /* normalize the coordinates */
    xDiff =(float)sto->x - (float)base->x  ;
    yDiff =(float)base->y - (float)sto->y  ;
    tangente=sqrt((xDiff * xDiff)+(yDiff * yDiff));  /* BBB */
    xDiff/=tangente;
    yDiff/=tangente;

    /* compute the first pseudo angle */
    if((xDiff>0.0)&&(yDiff>0.0))
    {
        /* 1. quadrant, range >0 <1 */
        angle2=yDiff;
    }
    else if (xDiff<=0.0)
    {
        /* 2. and 3. quadrant, range >=1 <=3 */
        angle2=2-yDiff;
    }
    else
    {
        /* 4. quadrant */
        angle2=3+xDiff;
    }

    angle=angle2-angle1;
    if(angle<=0.0)
        angle+=4 ;
 
    return angle;
}  /* get_angle */


/****************************************************************/
/*                                                              */
/* Find the outer face of the graph                             */
/*                                                              */
/****************************************************************/

/* The algorithm works as follows:
    a) it looks for the most left node of the graph
    b) it introduces a virtual node which is more left
    c) it looks for the edge, which has the smallest angle
    d) it adds the corresponding node to the face
    e) it traverses looking for the edge with the smallest
       angle regarding the last two edges until it comes to
       the starting node
                                                                */

/****************************************************************/
/*                                                              */
/*    find the top left node of the graph                       */
/*    where left ist more important than top                    */
/*                                                              */
/****************************************************************/

Snode top_left (Sgraph sGraph)
{
    Snode TempMin;
    Snode TempNode;


    /* initialize TempMin with first node */
    TempMin = sGraph->nodes;

    /* traverse through all nodes */
    for_all_nodes ( sGraph, TempNode )
    {
        if ( ( TempNode->x < TempMin->x ) ||
             ( (TempNode->x == TempMin->x ) &&
               (TempNode->y <  TempMin->y ) ) )
            TempMin = TempNode;
    }
    end_for_all_nodes ( sGraph, TempNode );
    return TempMin;
}  /* top_left */



/****************************************************************/
/*                                                              */
/*    get next edge in the face                                 */
/*    next edge is the edge with the smallest angle             */
/*    regarding the prior edge                                  */
/*                                                              */
/****************************************************************/

Sedge next_edge (Snode node, Spoint sfrom)
{
    Sedge TempEdge,
          MinEdge,
          TEdge;
    float MinAngle, TempAngle;
    Edgeline el;
    struct spoint to;

    /* initialize variables */
    MinAngle = MAX_ANGLE+1.0;
    MinEdge = NULL;

    for_sourcelist ( node, TempEdge )
    {

        TEdge = TempEdge;
        /* get the next position of the edgeline */
        el=(Edgeline)edge_get(graphed_edge(TempEdge), EDGE_LINE); 
	
        if(unique_edge(TempEdge))
        {
            to.x=edgeline_x(el->suc);
            to.y=edgeline_y(el->suc);
        }
        else
        {
            to.x=edgeline_x(el->pre->pre);
            to.y=edgeline_y(el->pre->pre);
        }
        /* get the angle of the incoming nodes */
        TempAngle=get_angle(node, sfrom, &to);
        if ( TempAngle < MinAngle )
        {
            MinAngle=TempAngle;
            /* remember edge */
            MinEdge=TempEdge;
        }
    }
    end_for_sourcelist ( node, TempEdge );
    return MinEdge;
}  /* next_edge */


/****************************************************************/
/*                                                              */
/*    find the outer face                                       */
/*                                                              */
/****************************************************************/
#undef nodef
#ifdef nodef

SFaceElement find_outer_face(Sgraph sGraph)
{   
    struct spoint VirtPoint;
    Sedge sStartEdge;
    Snode sStartNode;

    sStartNode=top_left(sGraph);
    /* introduce a virtual point left from the leftmost point */
    /* to retrieve the first edge of the outer face */
    VirtPoint.x=sStartNode->x-1;
    VirtPoint.y=sStartNode->y;
    /* find first edge of the outer face */
    sStartEdge=next_edge(sStartNode, &VirtPoint);

    /* now process just as a normal face */
    return find_face( sGraph, sStartEdge);
}  /* find_outer_face */

#endif

/*************************************************************/
/*                                                           */
/* finds face containing sStartEdge                          */
/*                                                           */
/*************************************************************/

struct find_face_ret
    {
    int numElements;
    SFaceElement sElements;
    };

struct find_face_ret find_face(Sgraph sGraph, Sedge sStartEdge)
{  
    Sedge sCurrEdge,
          sTempEdge,
          SearchEdge;
    Snode sStartNode,
          sTempNode;
    SFaceElement FElement;
    Slist edgeList;
    int    NumElements;

    struct find_face_ret Ret;
    
    /* tja irgendwie brauchen wir noch ein fehlerkonzept */
    /*if ((sGraph==NULL) || (sStartEdge==NULL) )
        return NULL;*/

    FElement = NULL_ELEMENT;
    sStartNode=sStartEdge->snode;
    sCurrEdge = sStartEdge;
   
    NumElements = 0;
    do
    { 
        NumElements ++;
        /* mark edge as visited */
        edge_visited(sCurrEdge);
      
        sTempNode=sCurrEdge->tnode;
 
        FElement=insert_FaceElement(FElement, sTempNode,sCurrEdge);
 
        /* !!!!!! pay attention for isolated edges !!! */
        /*!!! something needs to be changed there !!!*/
        /* infinity is not specially excluded */
        /* auszugliedern in new edge */

        edgeList=NODE_ATTR_SLIST(sCurrEdge->tnode);
        sTempEdge= ATTRS_EDGE(edgeList);
        while(get_unique_edge_handle(sCurrEdge)!=get_unique_edge_handle(sTempEdge))
            sTempEdge=ATTRS_EDGE(edgeList=edgeList->pre);
        sTempEdge=ATTRS_EDGE(edgeList=edgeList->pre);
        if(sTempEdge->snode!=sCurrEdge->tnode)
        {
            for_all_edges(sGraph,SearchEdge)
            {
                 if((SearchEdge->snode==sTempEdge->tnode)&&
                    (SearchEdge->tnode==sTempEdge->snode))
                 {
                       sCurrEdge=SearchEdge;
                       break;
                 }  /* if edge found */
            }
            end_for_all_edges(sGraph,SearchEdge);
        }  /* if snode != tnode */
        else
        {
            sCurrEdge=sTempEdge;
        }  /* else */
    } /* do */
    while ( sCurrEdge != sStartEdge );
    Ret.numElements = NumElements;
    Ret.sElements = FElement;
    return Ret;
} /* find_face */


/****************************************************************/
/*                                                              */
/*     Create a list of all faces                               */
/*     main function for creating a facelist                    */
/*                                                              */
/****************************************************************/

SFaceList find_faces(Sgraph sGraph)
{                                     
    SFaceList sGraphFaces;
    Sedge     sTempEdge;
    SFace     sInnerFace;
    SFaceElement     sCurrFaceElements;
    SFaceElement     sMaxFaceElements;
    int       maxNumElements;
    int       currNumElements;
    struct find_face_ret find_face_ret;

    /* Check for correct input */
    
    /* Allocate the facelist struct */
    sGraphFaces = new_Facelist();   
    
    /*sGraphFaces->OuterFace=insert_Face(NULL);*/
    /* first find the outer face */
    /*sGraphFaces->OuterFace->elements=find_outer_face(sGraph);*/
    

    maxNumElements = 0;
    sMaxFaceElements = NULL;
    /* now process the faces */
    for_all_edges(sGraph, sTempEdge)
    {
        if(!edge_visited_double(sTempEdge))
        {
	    find_face_ret = find_face(sGraph, sTempEdge);
	    sCurrFaceElements=find_face_ret.sElements;
	    currNumElements = find_face_ret.numElements;
	    if(currNumElements > maxNumElements)
	    {
		if(sMaxFaceElements != NULL)
		{
		    sGraphFaces->InnerFaces=sInnerFace=insert_Face(sGraphFaces->InnerFaces);
		    sInnerFace->elements = sMaxFaceElements;
		    member_edges(sInnerFace);

	    	}
		sMaxFaceElements = sCurrFaceElements;
		maxNumElements = currNumElements;
	    }
	    else
	    {
		sGraphFaces->InnerFaces=sInnerFace=insert_Face(sGraphFaces->InnerFaces);
		sInnerFace->elements = sCurrFaceElements;
		member_edges(sInnerFace);

	    }
	    
	   /*
            sGraphFaces->InnerFaces=sCurrFace=insert_Face(sGraphFaces->InnerFaces);
            sCurrFace->elements=find_face(sGraph, sTempEdge);
            member_edges(sCurrFace);*/

        }
    }  	
    end_for_all_edges(sGraph,sTempEdge); 
    sGraphFaces->OuterFace=insert_Face(NULL);
    sGraphFaces->OuterFace->elements = sMaxFaceElements; 
     member_edges(sGraphFaces->OuterFace);   
    return sGraphFaces;               
} /* find_faces */
    
/****************************************************************/
/*                                                              */
/*    Set a pointer from the edge to the corresponding face     */
/*                                                              */
/****************************************************************/

void member_edges(SFace face)
{
    SFaceElement StartElement;
    SFaceElement Element;

    StartElement=Element=face->elements;
 
    do
    {
        if(EDGE_ATTR(Element->sedge)->Face1==NULL)
            EDGE_ATTR(Element->sedge)->Face1=face;
        else
            EDGE_ATTR(Element->sedge)->Face2=face;
        Element=Element->succ;
    }
    while(Element!=StartElement);
}  /* member_edges */

/****************************************************************/
/*                                                              */
/* mark edges as visited                                        */
/*                                                              */
/****************************************************************/


/* to be changed sometimes: ONCE... means snode address is lower
			    TWICE.. means tnode address is lower */

void edge_visited(Sedge edge)
{
    int test;  /* was soll das ??? */
    EDGE_ATTR(edge)->visited= EDGE_ATTR(edge)->visited |
                  ((edge->snode<edge->tnode)?
                  ONCE_VISITED:TWICE_VISITED);
    test=EDGE_ATTR(edge)->visited;
} /* edge_visited */


/****************************************************************/
/*                                                              */
/* test if edge is proceeded                                    */
/*                                                              */
/****************************************************************/

int edge_visited_double(Sedge edge)
{
    int invitro;  /* und was soll das ??? du reagenzglas */
    invitro =(EDGE_ATTR(edge)->visited==TWICE_VISITED);
    return EDGE_ATTR(edge)->visited & ((edge->snode<edge->tnode)?
				      ONCE_VISITED:TWICE_VISITED);
}  /* edge_visited_double */
