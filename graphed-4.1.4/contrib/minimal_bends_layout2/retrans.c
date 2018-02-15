


/************************************************************************/
/*                                                                      */
/*                              RETRANS.C                               */
/*                                                                      */
/*              Retransformation of the network to a graph              */
/*                                                                      */
/*                                                                      */
/*                                                                      */
/************************************************************************/







# include "minimal_bends_layout2_export.h"









/*****************************************/
/*                                       */
/* gets the left face of an edge:        */
/* face2 if face1 is equal to right face */
/* face 1 otherwise.                     */
/* NULL_FACE is converted to outerface.  */
/*                                       */
/*****************************************/

SFace get_left_face(Sedge edge, SFace rightface, SFace outerface)
{
    SFace leftface;

    if( rightface == outerface)
    {
        rightface = NULL_FACE;
    }
    if (EDGE_ATTR(edge)->Face1 == rightface)
    {
        leftface = EDGE_ATTR(edge)->Face2;
    }
    else
    {
        leftface = EDGE_ATTR(edge)->Face1;
    }
    if (leftface == NULL_FACE)
    {
        leftface = outerface;
    }
    return(leftface);
}    /*  get_left_face */


/***********************************************/
/*                                             */
/* determines the number of bends of an edge:  */
/* flow rightface->leftface bends to the right */
/* flow leftface->rightface bends to the left  */
/* saved in faceelement->shape                 */
/* The flows are then set 0                    */
/*                                             */
/***********************************************/

SShape get_bends(SFaceElement faceelement, SFace rightface, SFace leftface, int direction)
{
    int count;
    Sedgelist rlflowedge, /* network edge from right to left face node */
              lrflowedge; /*                   left to right           */
    SGridPoint newpoint;
    SShape elementshape,
           newshape;

    /* flow right face to left face: bends to the right */

    elementshape = faceelement->shape;
    while (rightface->netnode->outedgelist->pred != NULL)
	rightface->netnode->outedgelist = rightface->netnode->outedgelist->pred;
    for (rlflowedge = rightface->netnode->outedgelist;
         (rlflowedge->edge->tnode != leftface->netnode)||(rlflowedge->edge->father != NULL);
         rlflowedge = rlflowedge->succ);
    for (count = rlflowedge->edge->flow; count != 0; count--)
    {
        newpoint = create_gridpoint(NULL_G_LINE, NULL_G_LINE);
        for (elementshape = faceelement->shape;
             elementshape->succ != NULL_SHAPE;
             elementshape = elementshape->succ);
        newshape = create_shape(RIGHT, direction, newpoint, NULL,
                                 elementshape, NULL_SHAPE, FALSE);
        direction = newshape->direction; /* right bend */
        elementshape->succ = newshape;
        elementshape = newshape;
    }   /* for rlflow */
    rlflowedge->edge->flow = 0;
    
    /* flow left face to right face: bends to the left */
    while (leftface->netnode->outedgelist->pred != NULL)
	leftface->netnode->outedgelist = leftface->netnode->outedgelist->pred;
    for (lrflowedge = leftface->netnode->outedgelist;
         (lrflowedge->edge->tnode != rightface->netnode)||(lrflowedge->edge->father != NULL);
         lrflowedge = lrflowedge->succ);
    for (count = lrflowedge->edge->flow; count != 0; count--)
    {
        newpoint = create_gridpoint(NULL_G_LINE, NULL_G_LINE);
        for (elementshape = faceelement->shape;
             elementshape->succ != NULL_SHAPE;
             elementshape = elementshape->succ);
        newshape = create_shape(LEFT, direction, newpoint, NULL, elementshape,
				 NULL_SHAPE, FALSE);
        direction = newshape->direction; /* left bend */
        elementshape->succ = newshape;
        elementshape = newshape;
    }   /* for rlflow */
    lrflowedge->edge->flow = 0;
    return elementshape;
}    /* get_bends */




/********************************************************/
/*                                                      */
/* calculates angle to predecessing edge using the      */
/* corresponding network flow and builds first shape	*/
/* element                                              */
/*                                                      */
/********************************************************/

SShape get_first_shape(SFaceElement curelement, int direction, SFace curface, int isOuterFace)
{	 
     Snetnode nodenetnode,  /* network node corresponding to */
                            /* curelement's edge's sourcenode */
              facenetnode;  /* network node corresponding to curface */
	 Sedgelist flownetedge; /* network edge from nodenetnode to facenetnode */
     int angle;
     SGridPoint newpoint;
         SShape newshape;

	 newpoint = NULL;
         if ((isOuterFace) && 
             (EDGE_ATTR(curelement->sedge)->Face1 == EDGE_ATTR(curelement->sedge)->Face2) &&
             (no_of_sources(curelement->snode) == 2))
         {
             if ( NODE_ATTR(curelement->snode)->netnode != NULL_NODE)
         /* deg(snode) < 4 -> angle derived from network flow */
             {
               nodenetnode = NODE_ATTR(curelement->snode)->netnode;
               facenetnode = curface->netnode;
               while (nodenetnode->outedgelist->pred != NULL)
			nodenetnode->outedgelist = nodenetnode->outedgelist->pred;
               for(flownetedge = nodenetnode->outedgelist;
                   flownetedge->edge->tnode != facenetnode;
                   flownetedge = flownetedge->succ);
		angle = FORWARD;
	     }
         }
         else 
         {     
 	 /* get angle to predecessing edge -> first shape element */
             if ( NODE_ATTR(curelement->snode)->netnode != NULL_NODE)
         /* deg(snode) < 4 -> angle derived from network flow */
             {
               nodenetnode = NODE_ATTR(curelement->snode)->netnode;
               facenetnode = curface->netnode;
               while (nodenetnode->outedgelist->pred != NULL)
			nodenetnode->outedgelist = nodenetnode->outedgelist->pred;
               for(flownetedge = nodenetnode->outedgelist;
                   flownetedge->edge->tnode != facenetnode;
                   flownetedge = flownetedge->succ);
               angle = (flownetedge->edge->flow) % 4;
              }
              else
              /* deg(snode) == 4 -> angle = 90 deg */
              {
                    angle = RIGHT;
              }
         } /* no outer face bridge */
	 /* build shape element */

         if ( NODE_ATTR(curelement->snode)->gridpoint != NULL )
	 /* visited -> get corresponding gridpoint */
         {
             newpoint = NODE_ATTR(curelement->snode)->gridpoint;
         }  /* if gridpoint */
	 else
	 {
          	newpoint = create_gridpoint(NULL_G_LINE, NULL_G_LINE);
             	NODE_ATTR(curelement->snode)->gridpoint = newpoint;
	 }   /* if no gridpoint */	
	 if (curelement == curface->connection)
            newshape = create_shape(angle, direction, newpoint, NULL,
                                  	 NULL_SHAPE, NULL_SHAPE, TRUE);
	 else
            newshape = create_shape(angle, direction, newpoint, NULL,
                                  	 NULL_SHAPE, NULL_SHAPE, FALSE);
     if ( NODE_ATTR(curelement->snode)->netnode != NULL_NODE)
         	flownetedge->edge->flow = 0;
	 curelement->shape=newshape;
	 return(newshape);
}	/* get_first_shape */




/************************************************************************/
/*                                                                      */
/* gets shape of visited edge by inverting shape of corresponding edge  */
/*                                                                      */
/************************************************************************/

SFaceElement adapt_shape(SFaceElement curelement, SFace leftface, int direction)
{	
    SFaceElement leftfaceelem, /* element of leftface corresponding to */
                               /* curelement */
                 firstelem;
    SShape  newshape,
            leftshape;

	firstelem = leftface->elements;
	leftfaceelem = leftface->elements;
 	if (EDGE_ATTR(curelement->sedge)->visited)
        { 
	   do	/* for all leftfaceelements */
           {
                if (get_unique_edge_handle(leftfaceelem->sedge) == 
                    get_unique_edge_handle(curelement->sedge))
                {  
                    if((leftfaceelem->shape->succ != NULL_SHAPE) ||
                        (leftfaceelem->shape->pred != NULL_SHAPE))
                          /* bends exist */
                    {
                       while(leftfaceelem->shape->succ != NULL_SHAPE)
                          leftfaceelem->shape = leftfaceelem->shape->succ;
                       for (leftshape = leftfaceelem->shape;
                            leftshape->pred != NULL_SHAPE;
                            leftshape = leftshape->pred)
                       {
                          newshape = create_shape((leftshape->angleno + 2) % 4,
                                            direction, leftshape->sourcepoint,
                                            NULL,curelement->shape, NULL_SHAPE,
                                            FALSE);
	
                          curelement->shape->succ = newshape;
                          curelement->shape = newshape;
                          direction = newshape->direction;
                       }  /* for shape */
                    }	/* if bends exist */
                    while (leftfaceelem->shape->pred != NULL_SHAPE)
                        leftfaceelem->shape = leftfaceelem->shape->pred;
                    curelement->shape->targetpoint =
                                leftfaceelem->shape->sourcepoint;
                } /* if same edge */
                leftfaceelem = leftfaceelem->succ;
           } /* do */
           while(leftfaceelem != firstelem);
        } /* if visited */
	 return(curelement);
}  /* adapt_shape */



/************************************************/
/*                                              */
/* finds the ortogonal representation of        */
/* the graph: the edge's angle to its           */
/* predecessing edge in the face is             */
/* derived from the flow from the netnode's 	*/
/* equivalent of its source node to the         */
/* netnode's equivalent of the face, the        */
/* bends of the edges are derived from the      */
/* flow inbetween the left and right faces' 	*/
/* netnode equivalents.                         */
/* The ortogonal representation is saved in 	*/
/* the faceelment's shapes and later placed     */
/* on the gridlines.                            */
/*                                              */
/************************************************/


SFace find_ort_rep(Snetwork network, SFaceList facelist)
{
 SFace newfacelist, /* contains the outer face and all faces */
                    /* having already been left faces */
       rightface,
       curface,   /* current face */
       leftface,
       newleftface,
       lastface;    /* of newfacelist */
 SFaceElement curelement,   /* current element */
              leftfaceelem; /* element of the left face corresponding to */
                            /* current element */
 int direction,
     inFaceList;
 SShape curshape;	/* current shape */
 

 /* build orthogonal representation: */
 /* start with outer face and dirction right */

 newfacelist = facelist->OuterFace;
 newfacelist->succ = NULL_FACE;
 leftface = NULL_FACE;

 /* while new elements of newfacelists exist */

 for (curface = newfacelist; curface != NULL_FACE;
      curface = curface->succ)
    {
       rightface = curface;
       if (curface == facelist->OuterFace)
	   /* start with any faceelement */
       {
         curelement = curface->elements;
         direction = RIGHT;
       }
       else
	/* start with first visited element */
       {
         for (curelement = curface->elements;
              !EDGE_ATTR(curelement->sedge)->visited;
              curelement = curelement->succ);
       }
       newleftface = get_left_face(curelement->sedge, rightface,
                                     facelist->OuterFace);
       curface->connection = curelement;
       if (curface != facelist->OuterFace)
       /* get direction from connection */
        {	 
  		for (leftfaceelem = newleftface->elements;
		     get_unique_edge_handle(leftfaceelem->sedge ) != get_unique_edge_handle(curelement->sedge );
		     leftfaceelem = leftfaceelem->succ);
		for (;leftfaceelem->shape->succ != NULL_SHAPE;
		      leftfaceelem->shape = leftfaceelem->shape->succ);
      		direction = (leftfaceelem->shape->direction + 2) % 4;
	} /* if not outer face */
       do /* for all elements of the face */
       {
         newleftface = get_left_face(curelement->sedge, rightface,
                                     facelist->OuterFace);
         curshape = get_first_shape(curelement, direction, curface, (curface == facelist->OuterFace));
         direction = curshape->direction;

         /* if new left face exists: if edge not visited then add       */
         /* new left face to newfacelist and get bend from network flow */

         if (newleftface != leftface && (!EDGE_ATTR(curelement->sedge)->visited))
         {
           leftface = newleftface;
           /* test if leftface already in newfacelist */
           inFaceList = FALSE;
           for (lastface = newfacelist; lastface->pred != NULL_FACE;
                lastface = lastface->pred);
           for(lastface = newfacelist; lastface != NULL_FACE;
               lastface = lastface->succ)
           if (lastface == leftface)
               inFaceList = TRUE;
           for(lastface = newfacelist; lastface->succ != NULL_FACE;
               lastface = lastface->succ);
           if (!inFaceList)
           {
              leftface->pred = lastface;
              lastface->succ = leftface;
              leftface->succ = NULL_FACE;
           }
           curshape = get_bends(curelement, curface, leftface, direction);
           direction = curshape->direction;
         }  /* if new left face */
        /* else: if edge visited adapt shape of corresponding left face shape */
         else
         {
            if(EDGE_ATTR(get_unique_edge_handle(curelement->sedge))->visited)
            {
                 leftface = newleftface;
                 curelement = adapt_shape(curelement, leftface, direction);
                 direction = curelement->shape->direction;
            } /* if edge visited */
         } /* else */
         EDGE_ATTR(get_unique_edge_handle(curelement->sedge))->visited = TRUE;
         curelement = curelement->succ;
       }  /* do while */
       while(curelement != curface->connection);
    } /* for newfacelist */
    return newfacelist;
} /* find_ort_rep */





/********************************************************/
/*                                                      */
/* start of the retransformation of the network:        */
/* The faces' shapes are calculated using corresponding */
/* network flows and thus a orthogonal representation	*/
/* of the graph is established.                         */
/*                                                      */
/********************************************************/

void build_ort_rep (Snetwork Network, SFaceList FaceList)
{

	SFace face;

        /*stdeb */
        Snetedge edge;
        /* enddeb */
	FaceList->InnerFaces = find_ort_rep(Network, FaceList);
	while (FaceList->InnerFaces->pred != NULL)
	   FaceList->InnerFaces = FaceList->InnerFaces->pred;
        face = FaceList->InnerFaces;
        while (face != FaceList->OuterFace)
	   face = face->succ;
	if (face->pred != NULL)
	   face->pred->succ = face->succ;
        if (face->succ != NULL)
           face->succ->pred = face->pred;
        if (FaceList->InnerFaces == FaceList->OuterFace)
           FaceList->InnerFaces = FaceList->InnerFaces->succ;
        FaceList->OuterFace->pred = FaceList->OuterFace->succ = NULL;
        /* stdeb */
        while (Network->edgelist->pred != NULL)
	    Network->edgelist = Network->edgelist->pred;
        for (edge = Network->edgelist; edge != NULL; edge = edge->succ)
            if ((edge->flow < 0) && (edge->father == NULL))
		face = NULL;
        /* enddeb */
}	/* build_ort_rep */ 









     
