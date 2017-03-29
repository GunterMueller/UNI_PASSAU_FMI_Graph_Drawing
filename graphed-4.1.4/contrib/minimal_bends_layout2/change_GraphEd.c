

#include "minimal_bends_layout2_export.h"

 Snode sedge_real_target();

/****************************************************************/
/*                                                              */
/*                      CHANGE_GRAPHED.C                        */
/*                                                              */
/*         changes the GraphEd graph after transformation       */
/*                                                              */
/*                                                              */
/*                                                              */
/****************************************************************/




/****************************************************************/
/*                                                              */
/* sets the GraphEd's nodes' coordinates and edges' edgelines   */
/*                                                              */
/****************************************************************/

void change_graph(SFaceList Facelist, Sgraph graph)
{
    SFace CurrFace;

    process_face_elements(Facelist->OuterFace);
    if (Facelist->InnerFaces != NULL)
       while (Facelist->InnerFaces->pred != NULL)
	   Facelist->InnerFaces = Facelist->InnerFaces->pred;
    CurrFace=Facelist->InnerFaces;
    while(CurrFace!=NULL_FACE)
    {
        process_face_elements(CurrFace);
        CurrFace=CurrFace->succ;
    }
} /* change_graph */



/****************************************************************/
/*                                                              */
/* sets for each faceelement the coordinates of the             */
/* correseponding edge and its nodes                            */
/*                                                              */
/****************************************************************/


void process_face_elements(SFace Face)
{
    SFaceElement  TempElement,
                  StartElement;
    Graphed_edge CurrGEdge;
    int endX, 
        endY,
        inverted;

    /* remember the starting point since the elements list is circular */
    StartElement=TempElement=Face->elements;
    do
    {  
        inverted = FALSE;
        /* coordinates of the node */
        while(TempElement->shape->pred != NULL)
          TempElement->shape = TempElement->shape->pred;
        TempElement->snode->x=
            (TempElement->shape->sourcepoint->xline->lineno * minimal_bends_settings2.grid) +
                                                              minimal_bends_settings2.grid;
        TempElement->snode->y=
            (TempElement->shape->sourcepoint->yline->lineno * minimal_bends_settings2.grid) +
                                                              minimal_bends_settings2.grid;
        /* test whether edgeline is already processed */
        if (!EDGE_ATTR(TempElement->sedge)->visited )
        {
            EDGE_ATTR(TempElement->sedge)->visited = ONCE_VISITED;
            CurrGEdge=graphed_edge(TempElement->sedge);
            if (sedge_real_target(TempElement->sedge) != TempElement->sedge->tnode)
            {      
                   inverted = TRUE;
                   invert_edge(TempElement);
            }
            /* find endX, endY */
            while(TempElement->shape->succ != NULL)
                TempElement->shape = TempElement->shape->succ;
            endX = (TempElement->shape->targetpoint->xline->lineno * 
                    minimal_bends_settings2.grid) + minimal_bends_settings2.grid;
            endY = (TempElement->shape->targetpoint->yline->lineno * 
                    minimal_bends_settings2.grid) + minimal_bends_settings2.grid;
           /* el=(Edgeline)edge_get(CurrGEdge,EDGE_LINE); */
          /*  free_edgeline(el); */
          /* el = NULL; */
            set_edgeline(CurrGEdge,
                         endX,
                         endY,
                         TempElement->shape);
        }
        if (inverted)
             invert_edge(TempElement);
        /* let's process the next element */
        TempElement=TempElement->succ;
    }
    while(TempElement!=StartElement);
}  /* process_face_elements */


/********************************************************/ 
/*                                                      */
/* swaps snode and tnode of element's sedge and inverts */
/* the order of its shapes                              */
/*                                                      */
/********************************************************/ 

void invert_edge(SFaceElement element)
{
   Snode swapnode;
   SShape shape, 
          swapshape;
   SGridPoint swappoint;

   swapnode = element->sedge->snode;
   element->sedge->snode = element->sedge->tnode;
   element->sedge->tnode = swapnode;

   while (element->shape->pred != NULL)
       element->shape = element->shape->pred;
   shape = element->shape;
   while (shape != NULL)
   {
       swapshape = shape->succ;
       shape->succ = shape->pred;
       shape->pred = swapshape;
       swappoint = shape->sourcepoint;
       shape->sourcepoint = shape->targetpoint;
       shape->targetpoint = swappoint; 
       shape = swapshape;
   }
}   /* invert_edge */


/****************************************************************/
/*                                                              */
/* sets the GraphEd's Edgelines                                 */
/*                                                              */
/****************************************************************/

void set_edgeline(Graphed_edge GEdge, int endX, int endY, SShape shape)
{
    int x,y;
    Edgeline line;

    line = NULL;
    line = new_edgeline(endX,endY);

    while(shape->succ != NULL)
       shape = shape->succ;
    while (shape!=NULL)
    {
        x=(shape->sourcepoint->xline->lineno * minimal_bends_settings2.grid) + 
           minimal_bends_settings2.grid;
        y=(shape->sourcepoint->yline->lineno * minimal_bends_settings2.grid) +
           minimal_bends_settings2.grid;
        line = add_to_edgeline(line,x,y);
        shape = shape->pred;
    }
    line = line->suc; /* goto start of edgeline */
    edge_set(GEdge, EDGE_LINE, line, 0);
}  /* set_edgeline */

