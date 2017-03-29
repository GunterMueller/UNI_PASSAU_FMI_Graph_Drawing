#include "minimal_bends_layout2_export.h"


/***************************************/
/*                                     */
/* allocates and initializes SGridLine */
/*                                     */
/***************************************/

SGridLine create_gr_line(SGridPoint pt, SGridLine min)
{
	SGridLine newgridline;
	newgridline = (SGridLine)calloc(1, sizeof(struct sgridline));
	newgridline->lineno = 0;
	newgridline->points = create_gridpoint_list(pt, NULL);
	newgridline->minors = create_line_list(min, NULL);
        newgridline->faceminors = NULL;
	newgridline->tminors = NULL;
	newgridline->majors = NULL;
	newgridline->clines = NULL;
	return newgridline;
} /* create_gr_line */


/**************************************************************/
/*                                                            */
/* stores newalloc in list if add is true; frees newalloc and */
/* deletes it if add is false; if newalloc is NULL and add is */
/* FALSE the entire list is freed!!!                          */
/*                                                            */
/**************************************************************/


void store_alloc(char *newalloc, int add)
                  /* NULL and add == FALSE to delete all!!! */
          /* TRUE if new pointer was allocated, FALSE to delete pointer */
{  
    static Sallocated First = NULL;
    static Sallocated Curr;
    Sallocated Temp,
               newtemp;

    if (add)
    {
        if(newalloc != NULL)
        {
            Temp = (Sallocated)calloc(1,sizeof(struct allocated));
            if(First == NULL)
            {
                First = Curr = Temp;
            }
            else
            {
                Curr->succ = Temp;
                Temp->pred = Curr;
                Curr = Temp;
            }
            Curr->alloc = newalloc;
            Curr->succ = NULL;
            return;
        } /* if newalloc != NULL */
    }  /* if add */
    else
    {
        if (newalloc != NULL)
        {
            /* delete newalloc */
            Temp = First;
            while (Temp != NULL)
            {
                newtemp = Temp->succ;
                if (Temp->alloc == newalloc)
                {
                    free(Temp->alloc);
                    if (First == Temp)
                        First = First->succ;
                    if (Curr == Temp)
                        Curr = Curr->pred;
                    if (Temp->pred != NULL)
                        Temp->pred->succ = Temp->succ;
                    if (Temp->succ != NULL)
                        Temp->succ->pred = Temp->pred;
                    free (Temp);
                } /* if Temp->alloc == newalloc */
            Temp = newtemp;
            }  /* while Temp != NULL */
        }  /* if newalloc != NULL */
        else
        {
            /* delete all!!! */
            while(First != NULL )
            {
            Temp=First;
            First=Temp->succ;
            free(Temp->alloc);
            free(Temp);
            }  /* while First != NULL */
        }  /* if newalloc == NULL */
    }   /* if not add */
}   /* store_alloc */
    



/******************************************/
/*                                        */
/* allocation and initilization of SShape */
/*                                        */
/******************************************/

SShape create_shape(int angle, int dir, SGridPoint spoint, SGridPoint tpoint, SShape pre, SShape suc, int iscopy)
           /* angleno */
          /* direction */
                   /* sourcepoint */
                   /* targetpoint */
            /* pred */
            /* suc */
            /* true if no new direction */
{
    SShape shape;

    shape = (SShape)calloc(1, sizeof(struct sshape));
    shape->angleno = angle;
    if (iscopy)
    {
	shape->direction = dir;
    }	/* if */
    else
    {
    	switch(angle)
    	{
		case RIGHT:
			shape->direction = (dir + 1) % 4;
			break;
		case FORWARD:
			shape->direction = dir;
			break;
		case LEFT:
			shape->direction = (dir + 3) % 4;
			break;
		case BACKWARD:
			shape->direction = (dir + 2) % 4;
			break;
    	}	/* switch angle */
    }	/* else */
    shape->sourcepoint = spoint;
    shape->targetpoint = tpoint;
    shape->pred = pre;
    shape->succ = suc;
    return(shape);
}	/* get_new_shape */



/********************************************/
/*                                          */
/* allocates and initializes SGridPoint     */
/*                                          */
/********************************************/

SGridPoint create_gridpoint(SGridLine xline, SGridLine yline)
{
	SGridPoint newpoint;

	newpoint = (SGridPoint)calloc(1,sizeof(struct sgridpoint));
	newpoint->xline = xline;
	newpoint->yline = yline;

    store_alloc((char *)newpoint, TRUE);
	return(newpoint);
}  /* create_gridpoint */


/***************************************/
/*                                     */
/* allocates and initializes SGrid     */
/*                                     */
/***************************************/

SGrid create_grid(SGridLineList xlines, SGridLineList ylines)
{
	SGrid newgrid;

	newgrid = (SGrid)calloc(1,sizeof(struct sgrid));
	newgrid->xlines = xlines;
	newgrid->ylines = ylines;

	return(newgrid);
} /* create_grid */


/********************************************/
/*                                          */
/* allocates and initializes SGridPointList */
/*                                          */
/********************************************/

SGridPointList create_gridpoint_list(SGridPoint point, SGridPointList pre)
{
    SGridPointList newlist,
                   testlist;

    /* test if point already in list  */
    for (testlist = pre; testlist != NULL; testlist = testlist->pred)
	if (testlist->point == point)
             return pre;
    newlist = NULL;
    newlist = (SGridPointList)calloc(1, sizeof(struct sgridpointlist));
    newlist->point = point;
    newlist->pred = pre;
    if (pre != NULL)
    {
        newlist->succ = pre->succ;
        if (newlist->succ != NULL)
        	newlist->succ->pred = newlist;
        pre->succ = newlist;
    }
    else
       newlist->succ = NULL;
    return(newlist);
}   /* create_gridpoint_list */


/********************************************/
/*                                          */
/* allocates and initializes SGridLineList  */
/*                                          */
/********************************************/

SGridLineList create_line_list(SGridLine line, SGridLineList pre)
{
    SGridLineList newlist;
   
    if (line == NULL)
       return pre;
    /* test if line already in list  */
    for (newlist = pre; newlist != NULL; newlist = newlist->pred)
	if (newlist->line == line)
             return pre;
    newlist = (SGridLineList)calloc(1, sizeof(struct sgridlinelist));
    newlist->line = line;
    newlist->pred = pre;
    if (pre != NULL)
    {
        newlist->succ = pre->succ;
        if (newlist->succ != NULL)
        	newlist->succ->pred = newlist;
        pre->succ = newlist;
    }
    else
       newlist->succ = NULL;
    return(newlist);
}  /* create_line_list */


/********************************************/
/*                                          */
/* allocates and initializes SFaceShape     */
/*                                          */
/********************************************/

SFaceShape create_faceshape(SShape shape, SGridLineList xPos, SGridLineList yPos, SFaceShape pre, SFaceShape suc)
{
    SFaceShape newfaceshape;
   
    newfaceshape = (SFaceShape)calloc(1, sizeof(struct sfaceshape));
    newfaceshape->shape = shape;
    newfaceshape->xPos = xPos;
    newfaceshape->yPos = yPos;
    newfaceshape->pred = pre;
    newfaceshape->succ = suc;
    if (pre != NULL)
	pre->succ = newfaceshape;
    if (suc != NULL)
	suc->pred = newfaceshape;
    return(newfaceshape);
}  /* create_faceshape */






/****************************************/
/*                                      */
/* frees a shape which may be circular  */
/*                                      */
/****************************************/



void free_shape(SShape shape)
{
	SShape firstshape;
	SShape succshape;

	firstshape=shape;

	while((shape->pred != NULL_SHAPE) && (shape->pred != firstshape))
	{
		shape = shape->pred;
	}
        succshape = shape;
	if(shape->pred!=NULL_SHAPE)
            succshape->pred->succ=NULL_SHAPE;
	succshape=shape;
	while(succshape != NULL_SHAPE) 
	{
		succshape = shape->succ;
		free(shape);
		shape=succshape;
		
	}
}	/* free shape */ 



