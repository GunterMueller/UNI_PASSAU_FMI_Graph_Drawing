
/**************************************************************************/
/*                                                                        */
/*                              BUILD_GRID.C                              */
/*                                                                        */
/*         Embedding of the orthogonal represantation in the grid         */
/*                                                                        */
/*                                                                        */
/*                                                                        */
/*                                                                        */
/**************************************************************************/









# include "minimal_bends_layout2_export.h"


/********************************************************/
/*                                                      */
/* sets targetpoints of the face's elementshapes        */
/*                                                      */
/********************************************************/



void set_targetpoints(SFace face)
{
    SFaceElement element,
                 startelement;
    SShape shape;

	element = face->elements;
	startelement = element;
	/* goto start of elements' shapelists */
	do	/* for all elements */
	{
		while(element->shape->pred != NULL_SHAPE)
			element->shape = element->shape->pred;
		element = element->succ;
	}	/* do */
    while (element != startelement);
	/* get elementshapes' targetpoints */
	do	/* for all elements */
	{
		for (shape = element->shape; shape != NULL_SHAPE; shape = shape->succ)
		{
			if (shape->succ == NULL_SHAPE)
				shape->targetpoint = element->succ->shape->sourcepoint;
			else
				shape->targetpoint = shape->succ->sourcepoint;
		}	/* for shape */
		while(element->shape->pred != NULL_SHAPE)
			element->shape = element->shape->pred;
		element = element->succ;
	}	/* do */
	while (element != startelement);
}  /* set_targetpoints */







/*********************************************************/
/*                                                       */
/* sort gridlines: while not sorted gridline esists:     */
/*   choose next gridline (gridline with no minors)      */
/*   assign next number to it                            */
/*   and delete it from  all minorlists                  */
/*                                                       */
/*********************************************************/



/***********************************/
/*                                 */
/* deletes minline from minorlists */
/*                                 */
/***********************************/

void adapt_minors(SGridLineList minline, SGridLineList linelist)
{
	SGridLineList list,
                  nextlist,
                  nextline,
                  minorlist;

	/* goto end of minline */
    while (minline->succ != NULL)
    minline = minline->succ;
	/* traverse minline */
    while (minline != NULL)
	{
	    nextline = minline->pred;
	    /* goto start of linelist */
	    while (linelist->pred != NULL)
	    	   linelist = linelist->pred;
	    /* traverse linelist */
	    for (list = linelist; list != NULL; list = list->succ)
	    {   
            if(list->line->minors != NULL)
	        {
               /* traverse minorlist */
               while (list->line->minors->succ != NULL)
               list->line->minors = list->line->minors->succ;
               minorlist = list->line->minors;
               while (minorlist != NULL)
               {
                  nextlist = minorlist->pred;
                  if (minorlist->line == minline->line)
                  {
                     /* delete minline from minorlist */
                     if (minorlist->pred != NULL)
                     minorlist->pred->succ = minorlist->succ;
                     if (minorlist->succ != NULL)
                     minorlist->succ->pred = minorlist->pred;
                     if ((minorlist->pred == NULL) && (minorlist->succ == NULL))
                        list->line->minors = NULL;
                     minorlist = NULL;
                  } /* if is minline */
                  minorlist = nextlist;
               } /* while minorlist */
            }  /* if list->line->minors */
        } /* for linelist */
        free (minline);
        minline = nextline;
	} /* while minline */
}  /* adapt_minors */
		



/**********************************************/
/*                                            */
/* returns lines with lineno 0 and no minors, */
/* NULL if none exists                        */
/*                                            */
/**********************************************/


SGridLineList get_min_line (SGridLineList linelist)
{
	SGridLineList list,
		      minline;

	/* goto start of linelist */
	while (linelist->pred != NULL)
		linelist = linelist->pred;
	/* traverse linelist looking for minline */
	minline = NULL;
	for (list = linelist; list != NULL; list = list->succ)
	{
		if ((list->line->minors == NULL) && (list->line->lineno == 0))
			minline = create_line_list(list->line, minline);
	}  /* for list */
	return minline;
}  /* get_min_line */


/**************************************************/
/*                                                */
/* sorts the lines in linelist using their minors */
/* and assigns the linenos                        */
/*                                                */
/**************************************************/


void sort_gridlines(SGridLineList linelist)
{
	SGridLineList minline,
		      nextline;
        int done,    /* TRUE when all linenos are assigned */
	    number;

        done = FALSE;
	number = 0;
	while (linelist->pred !=NULL)
	    linelist = linelist->pred;
        nextline = linelist;
	while (nextline != NULL)
	{
		nextline->line->lineno = 0;
                nextline = nextline->succ;
	}   /* while nextline */
	while (!done)
	{
		/* get next line with no minors */
		minline = get_min_line(linelist);
		if (minline == NULL)   /* line withe lineno 0 exists */
		    done = TRUE;
		if (!done)
		{
		    /* assign lineno */
		    number++;
            for (nextline = minline; nextline != NULL;
                 nextline = nextline->pred)
		    {
                nextline->line->lineno = number;
		    }  /* for nextline */
		    /* delete minline from minorlists */
		    adapt_minors(minline, linelist);
		} /* if not done */
	}  /* while not done */
}   /* sort_gridlines */

/**************************************************************/
/*                                                            */
/* updates the majorlists, the minorlists and the tminorlists */
/*                                                            */
/**************************************************************/

void update_maj_min_list (SGridLine minline, SGridLine majline)
{
	SGridLineList addline,
		      toline;
	/* update majorlists */
	minline->majors = create_line_list(majline, minline->majors);
	addline = majline->majors;
	if (addline != NULL)
		while (addline->pred != NULL)
			addline = addline->pred;
	while (addline != NULL)
	{
		minline->majors = create_line_list(addline->line, 
                                                   minline->majors);
		addline = addline->succ;
	}   /* while addline */
	toline = minline->minors;
	if (toline != NULL)
		while (toline->pred != NULL)
			toline = toline->pred;
	while (toline != NULL)
	{
		addline = majline->majors;
		if (addline != NULL)
			while (addline->pred != NULL)
				addline = addline->pred;
		while (addline != NULL)
		{
			toline->line->majors = create_line_list(addline->line, 
                	                                        toline->line->majors);
			addline = addline->succ;
		}   /* while addline */
		toline->line->majors = create_line_list(majline, toline->line->majors);
		toline = toline->succ;
	}   /* while toline */

	/* update minorlists */
	majline->minors = create_line_list(minline, majline->minors);
	majline->tminors = create_line_list(minline, majline->tminors);
	addline = minline->minors;
	if (addline != NULL)
		while (addline->pred != NULL)
			addline = addline->pred;
	while (addline != NULL)
	{
		majline->minors = create_line_list(addline->line, 
                                                   majline->minors);
		majline->tminors = create_line_list(addline->line, 
                                                    majline->tminors);
		addline = addline->succ;
	}   /* while addline */
	toline = majline->majors;
	if (toline != NULL)
		while (toline->pred != NULL)
			toline = toline->pred;
	while (toline != NULL)
	{
		addline = minline->minors;
		if (addline != NULL)
			while (addline->pred != NULL)
				addline = addline->pred;
		while (addline != NULL)
		{
			toline->line->minors = create_line_list(addline->line, 
                	                                        toline->line->minors);
			toline->line->tminors = create_line_list(addline->line, 
                	                                         toline->line->tminors);
			addline = addline->succ;
		}   /* while addline */
		toline->line->minors = create_line_list(minline, toline->line->minors);
		toline->line->tminors = create_line_list(minline, toline->line->tminors);
		toline = toline->succ;
	}   /* while toline */
}   /* update_maj_min_list */



/********************************************************/
/*                                                      */
/* collects all gridlines contained in face in one grid */
/*                                                      */
/********************************************************/

SGrid build_facegrid(SFace face, SGrid grid)
{
	SFaceElement element,
                     startelement;
	SShape shape;
	SGrid facegrid;

 	facegrid = create_grid(NULL, NULL);
	element = startelement = face->connection;
	do
	{
		shape = element->shape;
		while (shape->pred != NULL)
			shape = shape->pred;
		while (shape != NULL)
		{
		      facegrid->xlines = 		
				create_line_list(shape->sourcepoint->xline,
                                facegrid->xlines);
		      facegrid->ylines = 		
				create_line_list(shape->sourcepoint->yline,
                                facegrid->ylines);
		      shape = shape->succ;
		}   /* while shape */
		element = element->succ;
	}   /* do */
	while (element != startelement);
	return facegrid;
}   /* build_facegrid */



/**************************************************************/
/*                                                            */
/* updates minor- and majorlists according to sorted facegrid */
/*                                                            */
/**************************************************************/

void set_sort_minors(SGrid facegrid)
{
	SGridLineList linelist;

	linelist = facegrid->xlines;
	while (linelist->pred != NULL)
		linelist = linelist->pred;
	while (linelist->succ != NULL)
	{
		update_maj_min_list(linelist->line, linelist->succ->line);
		linelist = linelist->succ;
	}
	linelist = facegrid->ylines;
	while (linelist->pred != NULL)
		linelist = linelist->pred;
	while (linelist->succ != NULL)
	{
		update_maj_min_list(linelist->line, linelist->succ->line);
		linelist = linelist->succ;
	}
}   /* set_sort_minors */



/*******************************************************/
/*                                                     */
/* returns pointer to linelist element containing line */
/*                                                     */
/*******************************************************/


SGridLineList get_pos(SGridLineList linelist, SGridLine line)
{
	SGridLineList curlist;

	curlist = linelist;
	while (curlist->pred != NULL)
		curlist = curlist->pred;
	while (curlist != NULL)
	{
		if (curlist->line == line)
			return curlist;
		curlist = curlist->succ;
	}
	return NULL;
}   /* get_pos */



/**************************************************************/
/*                                                            */
/* tests depending on direction if xline is in yline's clines */
/* or vice versa and updates clines                           */
/*                                                            */
/**************************************************************/


SGridLineList in_crossed(SGridLineList xline, SGridLineList yline, SShape shape)
{
	SGridLineList crossline, 
                      testline;

        switch (shape->direction)
	{
		case RIGHT:
			crossline = xline->succ;
			while (crossline->line != shape->targetpoint->xline)
			{
				testline = crossline->line->clines;
				if (testline != NULL)
		   			while (testline->pred != NULL)
						testline = testline->pred;
				while (testline != NULL)
				{
					if (testline->line == yline->line)
						return crossline;
					testline = testline->succ;
				}
				yline->line->clines = create_line_list(crossline->line, yline->line->clines);
				crossline = crossline->succ;
			}   /* while not targetpoint */
			break;
		case LEFT:
			crossline = xline->pred;
			while (crossline->line != shape->targetpoint->xline)
			{
				testline = crossline->line->clines;
				if (testline != NULL)
		   			while (testline->pred != NULL)
						testline = testline->pred;
				while (testline != NULL)
				{
					if (testline->line == yline->line)
						return crossline;
					testline = testline->succ;
				}
				yline->line->clines = create_line_list(crossline->line, yline->line->clines);
				crossline = crossline->pred;
			}   /* while not targetpoint */
			break;
		case UP:
			crossline = yline->succ;
			while (crossline->line != shape->targetpoint->yline)
			{
				testline = crossline->line->clines;
				if (testline != NULL)
		   			while (testline->pred != NULL)
						testline = testline->pred;
				while (testline != NULL)
				{
					if (testline->line == xline->line)
						return crossline;
					testline = testline->succ;
				}
				xline->line->clines = create_line_list(crossline->line, xline->line->clines);
				crossline = crossline->succ;
			}   /* while not targetpoint */
			break;
		case DOWN:
			crossline = yline->pred;
			while (crossline->line != shape->targetpoint->yline)
			{
				testline = crossline->line->clines;
				if (testline != NULL)
		   			while (testline->pred != NULL)
						testline = testline->pred;
				while (testline != NULL)
				{
					if (testline->line == xline->line)
						return crossline;
					testline = testline->succ;
				}
				xline->line->clines = create_line_list(crossline->line, xline->line->clines);
				crossline = crossline->pred;
			}   /* while not targetpoint */
			break;
	}   /* switch */			
	return NULL;
}   /* in_crossed */
		


	
/***************************/
/*                         */
/* builds faceminors       */
/*                         */
/***************************/

void build_faceminors(SGrid facegrid)
{
	SGridLineList curline,
		      gridline,
		      minorline;

	curline = facegrid->xlines;
	if (curline != NULL)
		while (curline->pred != NULL)
			curline = curline->pred;
	while (curline != NULL)
	{
		/* add minorlist line to faceminors if it is in facegrid */
		minorline = curline->line->tminors;
		if (minorline != NULL)
			while (minorline->pred != NULL)
				minorline = minorline->pred;
		while (minorline != NULL)
		{
			gridline = facegrid->xlines;
			if (gridline != NULL)
				while (gridline->pred != NULL)
					gridline = gridline->pred;
			while (gridline != NULL)
			{
				if (gridline->line == minorline->line)
			              curline->line->faceminors = 
                                            create_line_list(minorline->line, 
							     curline->line->faceminors);
				gridline = gridline->succ;
			}   /* while gridline */
			minorline = minorline->succ;
		}   /* while minorline */
		curline = curline->succ;
	}   /* while curline */
	curline = facegrid->ylines;
	if (curline != NULL)
		while (curline->pred != NULL)
			curline = curline->pred;
	while (curline != NULL)
	{
		/* add minorlist line to faceminors if it is in facegrid */
		minorline = curline->line->tminors;
		if (minorline != NULL)
			while (minorline->pred != NULL)
				minorline = minorline->pred;
		while (minorline != NULL)
		{
			gridline = facegrid->ylines;
			if (gridline != NULL)
				while (gridline->pred != NULL)
					gridline = gridline->pred;
			while (gridline != NULL)
			{
				if (gridline->line == minorline->line)
			              curline->line->faceminors = 
                                            create_line_list(minorline->line, 
							     curline->line->faceminors);
				gridline = gridline->succ;
			}   /* while gridline */
			minorline = minorline->succ;
		}   /* while minorline */
		curline = curline->succ;
	}   /* while curline */
}   /* build_faceminors */



/***************************/
/*                         */
/* builds face's faceshape */
/*                         */
/***************************/

SFaceShape get_faceshape(SGrid facegrid, SFace face)
{
	SFaceElement element,
                     startelement;
	SShape shape;
	SFaceShape faceshape, 
                   newfaceshape;

	faceshape = NULL;
	/* build faceshape */
	element = startelement = face->connection;
	do
	{
		shape = element->shape;
		while (shape->pred != NULL)
			shape = shape->pred;
		while (shape != NULL)
		{
			newfaceshape = create_faceshape(shape,
                            get_pos(facegrid->xlines, shape->sourcepoint->xline),
                            get_pos(facegrid->ylines, shape->sourcepoint->yline),
			    faceshape, NULL);
			faceshape = newfaceshape;
			shape = shape->succ;
		}   /* while shape */
		element = element->succ;
	}   /* do */
	while (element != startelement);
	/* goto start of faceshape */
	while (faceshape->pred != NULL)
		faceshape = faceshape->pred;
	return faceshape;
}   /* get_faceshape */
	
/***************************************/
/*                                     */
/* tests if lines are sorted correctly */
/*                                     */
/***************************************/

int test_sort(SGrid facegrid, SFaceShape faceshape)
{
	while (faceshape->pred != NULL)
		faceshape = faceshape->pred;
	while (faceshape != NULL)
	{
		if (in_crossed(faceshape->xPos, faceshape->yPos, faceshape->shape)
                          != NULL)	
		{	
			free_clines(facegrid);
			return FALSE;
		}   /* if in_crossed */
		faceshape = faceshape->succ;
	}   /* while faceshape */
	free_clines(facegrid);
	return TRUE;
}   /* test_sort */





/**********************************************/
/*                                            */
/* permutes through all possible combinations */
/*                                            */
/**********************************************/


int list_permute(int listNo, int action, SGridLineList linelist)
{
   static int numItems[2];
   static SGridLineList * linearr[2];
   static int *linecount[2];
   static int *isUsed[2];
   static int *predCnt[2];
   static int permPos[2];
   
   
   SGridLineList actLine;
   SGridLineList lastLine;
   int cnt,nth_cnt,listcnt;
      
   switch (action)
   {
      case PERM_INIT:
         actLine = linelist;   
         if (actLine == NULL)
            {
            numItems[listNo]=0;
            break;
            }
         while(actLine->succ != NULL)
            actLine = actLine->succ;
         numItems[listNo]=1;
         while(actLine->pred !=NULL)   
            {
            actLine = actLine->pred;
            numItems[listNo]++;
            }
         linearr[listNo]=(SGridLineList * )calloc(numItems[listNo],sizeof(SGridLineList)); 
         linecount[listNo]=(int *)calloc(numItems[listNo],sizeof(int));
         isUsed[listNo]=(int *)calloc(numItems[listNo],sizeof(int));
         predCnt[listNo]=(int *)calloc(numItems[listNo],sizeof(int));
         for(cnt = 0;cnt < numItems[listNo] && actLine!=NULL; cnt++, actLine = actLine->succ)
            {
            linearr[listNo][cnt]=actLine;
	    actLine->line->lineno = cnt;
            }
      case PERM_RESET:
	 
	 /* set number of preceding lines */
	 for(cnt=0;cnt<numItems[listNo];cnt++)
	    {
	    predCnt[listNo][cnt]=0;
	    isUsed[listNo][cnt]=FALSE;
	    }
	 for(cnt=0;cnt<numItems[listNo];cnt++)
	    {
	    if((actLine=linearr[listNo][cnt]->line->faceminors)!=NULL) 
	        while(actLine->pred!=NULL)
		    actLine=actLine->pred;
	    while(actLine != NULL)
		{
		predCnt[listNo][actLine->line->lineno]++;
		actLine=actLine->succ;
		}
	    }
            
	 linecount[listNo][0]=-1;
	 
         /*tminors*/ 
         permPos[listNo]=-1;
         break;
      
      case PERM_DELETE:
         free(linearr[listNo]);
         free(linecount[listNo]);   
         free(isUsed[listNo]);
	 free(predCnt[listNo]);
         break;
         
      case PERM_NEXT:     
                           
 	 if(linecount[listNo][0]==-1)
	 {
	 /* set initial permutation */
	 lastLine=NULL;    
         for(listcnt = 0;listcnt < numItems[listNo]; listcnt++)
            {
	    linecount[listNo][listcnt]=0;
	    for(cnt = 0;cnt<numItems[listNo];cnt++)
		{ 
	    	if((predCnt[listNo][cnt]==0)&&
		    (isUsed[listNo][cnt]==FALSE))
		    {
		    linecount[listNo][listcnt]++;
		    actLine=linearr[listNo][cnt];
		    }
		}
	    isUsed[listNo][actLine->line->lineno]=TRUE;
	    actLine->succ=lastLine;
	    lastLine=actLine;
	    /* lower number of predecessor in successorlist by one */
	    if((actLine=actLine->line->faceminors)!=NULL) 
	        while(actLine->pred!=NULL)
		    actLine=actLine->pred;
	    while(actLine != NULL)
		{
		predCnt[listNo][actLine->line->lineno]--;
		actLine=actLine->succ;
		}

            }   
	 }
	 else
	 {
	 /*next permutation*/
	 /* update linecounts to next permutation */
	 cnt = numItems[listNo]-1;
	 actLine=linearr[listNo][0];
	 while(actLine->pred!=NULL)
		actLine=actLine->pred;
	 while((--linecount[listNo][cnt])==0)
	    {
	    /*update number of predecessors in successorlist */
	    isUsed[listNo][actLine->line->lineno]=FALSE;
	    lastLine=actLine->line->faceminors; 
	    if(lastLine!=NULL)
	    	{	
	    	while(lastLine->pred!=NULL)
			lastLine=lastLine->pred;
	    	}	
	    while(lastLine != NULL)
		{
		predCnt[listNo][lastLine->line->lineno]++;
		lastLine=lastLine->succ;
		}
	    actLine=actLine->succ;
	    cnt--;
	    if(cnt < 0)
		return FALSE;
	    }
	isUsed[listNo][actLine->line->lineno]=FALSE;
	lastLine=actLine->line->faceminors; 
	if(lastLine!=NULL)
	    {	
	    while(lastLine->pred!=NULL)
			lastLine=lastLine->pred;
	    }	
	while(lastLine != NULL)
	    {
	    predCnt[listNo][lastLine->line->lineno]++;
	    lastLine=lastLine->succ;
	    }

	 lastLine=actLine;
	 nth_cnt=0;
	 listcnt=cnt;
	 for(cnt = 0;cnt<numItems[listNo];cnt++)
		{ 
	    	if((predCnt[listNo][cnt]==0)&&
		    (isUsed[listNo][cnt]==FALSE))
		    {
		    nth_cnt++;	
		    actLine=linearr[listNo][cnt];
		    if(nth_cnt==linecount[listNo][listcnt])
			break;
		    }
		}
	 isUsed[listNo][actLine->line->lineno]=TRUE;
	 if(lastLine->succ != NULL)
	 	lastLine->succ->pred = actLine;
  	 actLine->succ=lastLine->succ;
	 lastLine=actLine;
	    /* lower number of predecessor in successorlist by one */
	    if((actLine=actLine->line->faceminors)!=NULL) 
	        while(actLine->pred!=NULL)
		    actLine=actLine->pred;
	    while(actLine != NULL)
		{
		predCnt[listNo][actLine->line->lineno]--;
		actLine=actLine->succ;
		}
	listcnt++;

	 for(/*listcnt*/;listcnt < numItems[listNo]; listcnt++)
            {
	    linecount[listNo][listcnt]=0;
	    for(cnt = 0;cnt<numItems[listNo];cnt++)
		{ 
	    	if((predCnt[listNo][cnt]==0)&&
		    (isUsed[listNo][cnt]==FALSE))
		    {
		    linecount[listNo][listcnt]++;
		    actLine=linearr[listNo][cnt];
		    }
		}
	    isUsed[listNo][actLine->line->lineno]=TRUE;
	    actLine->succ=lastLine;
	    lastLine=actLine;
	    /* lower number of predecessor in successorlist by one */
	    if((actLine=actLine->line->faceminors)!=NULL) 
	        while(actLine->pred!=NULL)
		    actLine=actLine->pred;
	    while(actLine != NULL)
		{
		predCnt[listNo][actLine->line->lineno]--;
		actLine=actLine->succ;
		}

            }   
	 }
         lastLine->pred = NULL;
         while(lastLine->succ!=NULL) 
            {
            lastLine->succ->pred=lastLine;
            lastLine=lastLine->succ; 
            }

	 return TRUE;  
         break;
   }
   return FALSE; /* shoud not be reached ? */
}    /* list_permute */




/******************************************************/
/*                                                    */
/* counts xlines and ylines and returns their product */
/*                                                    */
/******************************************************/

int get_linesno(SGrid facegrid)
{
	int xno, yno;
	SGridLineList linelist;

	xno = yno = 0;
	linelist = facegrid->xlines;
	while (linelist->pred != NULL)
		linelist = linelist->pred;
	while (linelist != NULL)
	{
		xno++;
		linelist = linelist->succ;
	}
	linelist = facegrid->ylines;
	while (linelist->pred != NULL)
		linelist = linelist->pred;
	while (linelist != NULL)
	{
		yno++;
		linelist = linelist->succ;
	}
        return (xno * yno);
}   /* get_linesno */

/*******************************************/
/*                                         */
/* adds minors until grid passes all tests */
/*                                         */
/*******************************************/

void add_gridline_minors(SFaceList facelist, SGrid grid)
{
    SFace face,
          curface;
    int done,
        min;
    SFaceShape faceshape;

   /* traverse all faces */
   while (facelist->InnerFaces->pred != NULL)
       facelist->InnerFaces = facelist->InnerFaces->pred;
   for (curface = facelist->InnerFaces; curface != NULL; curface = curface->succ)
   {	
	curface->facegrid = build_facegrid(curface, grid);
	curface->linesno = get_linesno(curface->facegrid);
   }
   min = 0;
   while (min == 0)
   {
   	min = INFINITE;
   	for (face = facelist->InnerFaces; face != NULL; face = face->succ)
   	{	
		if (face->linesno < min)
		{
			curface = face;
			min = face->linesno;
        	}   /* if */
    	}   /* for face */
        if (min < INFINITE)
	{
		done = FALSE;	
		faceshape = get_faceshape(curface->facegrid, curface);
        	build_faceminors(curface->facegrid);
		list_permute(XLIST,PERM_INIT,curface->facegrid->xlines);
		list_permute(YLIST,PERM_INIT,curface->facegrid->ylines);
		while (list_permute(XLIST,PERM_NEXT,curface->facegrid->xlines))
		{
			while(list_permute(YLIST,PERM_NEXT,curface->facegrid->ylines))
	    		{
				done = test_sort(curface->facegrid, faceshape);
				if(done)
		    			break;
	    		}
	    		if(done)
				break;
            		list_permute(YLIST,PERM_RESET,curface->facegrid->ylines);
		}   /* while not done */
		list_permute(XLIST,PERM_DELETE,curface->facegrid->xlines);
		list_permute(YLIST,PERM_DELETE,curface->facegrid->ylines);
	
   		set_sort_minors(curface->facegrid);
		free_faceminors(curface->facegrid);
        	free_facegrid(curface->facegrid);
		curface->facegrid = NULL;
        	curface->linesno = INFINITE;
                free_faceshape(faceshape);
		min = 0;
	}   /* if min */
   } /* while min */
	done = FALSE;
	curface = facelist->OuterFace;	
	curface->facegrid = build_facegrid(curface, grid);
	faceshape = get_faceshape(curface->facegrid, curface);
        build_faceminors(curface->facegrid);
	list_permute(XLIST,PERM_INIT,curface->facegrid->xlines);
	list_permute(YLIST,PERM_INIT,curface->facegrid->ylines);
	while (list_permute(XLIST,PERM_NEXT,curface->facegrid->xlines))
	{
		while(list_permute(YLIST,PERM_NEXT,curface->facegrid->ylines))
	    	{
			done = test_sort(curface->facegrid, faceshape);
			if(done)
		    		break;
	    	}
	    	if(done)
			break;
            	list_permute(YLIST,PERM_RESET,curface->facegrid->ylines);
	}   /* while not done */
	list_permute(XLIST,PERM_DELETE,curface->facegrid->xlines);
	list_permute(YLIST,PERM_DELETE,curface->facegrid->ylines);
	
   	set_sort_minors(curface->facegrid);
	free_faceminors(curface->facegrid);
        free_facegrid(curface->facegrid);
	curface->facegrid = NULL;
	free_faceshape(faceshape);

}  /* add_gridline_minors */



/***************************/
/*                         */
/* builds major lists      */
/*                         */
/***************************/

void build_majorlists(SGridLineList linelist)
{
	SGridLineList curline,
		  minorline;

	curline = linelist;
	if (curline != NULL)
		while (curline->pred != NULL)
			curline = curline->pred;
	while (curline != NULL)
	{
		/* add line to the majorlists of all lines in its minorlist */
		minorline = curline->line->minors;
		if (minorline != NULL)
			while (minorline->pred != NULL)
				minorline = minorline->pred;
		while (minorline != NULL)
		{
			minorline->line->majors = create_line_list(curline->line, 
							     minorline->line->majors);
			minorline = minorline->succ;
		}   /* while minorline */
		curline = curline->succ;
	}   /* while curline */
}   /* build_majorlists */







/*********************************************************************/
/*                                                                   */
/* build the gridlines' minorlists:                                  */
/* First all gridlines connected by a shape are                      */
/* added to the minorlists, then the transitive minorlists are built */
/* and finally the minorlists are set equal to their transitive      */
/* counterparts.                                                     */
/*                                                                   */
/*********************************************************************/




/********************************************************/
/*                                                      */
/* deletes minline from minorlists and fills tminorlist */
/*                                                      */
/********************************************************/

void fill_tminors(SGridLineList minline, SGridLineList linelist)
{
	SGridLineList list,
                  nextlist,
                  nextline,
                  minorlist,
	          addline;

    /* goto end of minline */
    while (minline->succ != NULL)
    minline = minline->succ;
    /* traverse minline */
    while (minline != NULL)
	{
            minline->line->lineno = 1;
	    nextline = minline->pred;
	    /* goto start of linelist */
	    while (linelist->pred != NULL)
	    	   linelist = linelist->pred;
	    /* traverse linelist */
	    for (list = linelist; list != NULL; list = list->succ)
	    {   
            if(list->line->minors != NULL)
	        {
               /* traverse minorlist */
               while (list->line->minors->succ != NULL)
               list->line->minors = list->line->minors->succ;
               minorlist = list->line->minors;
               while (minorlist != NULL)
               {
                  nextlist = minorlist->pred;
                  if (minorlist->line == minline->line)
                  {
		     /* add minline and minline's tminors to tminorlist */
		     list->line->tminors = create_line_list(minline->line, 
                                                            list->line->tminors);
		     addline = minline->line->tminors;
		     if (addline != NULL)
			while (addline->succ != NULL)
				addline = addline->succ;
		     while (addline != NULL)
		     {
			  list->line->tminors = create_line_list(addline->line,
								 list->line->tminors);
		          addline = addline->pred;
		     }   /* while addline */
                     /* delete minline from minorlist */
                     if (minorlist->pred != NULL)
                     minorlist->pred->succ = minorlist->succ;
                     if (minorlist->succ != NULL)
                     minorlist->succ->pred = minorlist->pred;
                     if ((minorlist->pred == NULL) && (minorlist->succ == NULL))
                        list->line->minors = NULL;
                     minorlist = NULL;
                  } /* if is minline */
                  minorlist = nextlist;
               } /* while minorlist */
            }  /* if list->line->minors */
        } /* for linelist */
        free (minline);
        minline = nextline;
	} /* while minline */
}  /* fill_tminors */
		

/***************************/
/*                         */
/* builds transminor lists */
/*                         */
/***************************/

void build_tlist(SGridLineList linelist)
{
	SGridLineList minline,
		      curline;
        int done,    /* TRUE when all linenos are assigned */
	    number;

        done = FALSE;
	number = 0;
	while (!done)
	{
		/* get next line with no minors */
		minline = get_min_line(linelist);
		if (minline == NULL)  
		    done = TRUE;
		if (!done)
		{		
		    /* delete minline from minorlists and fill tminors */
		    fill_tminors(minline, linelist);
		} /* if not done */
	}  /* while not done */
        /* minorlist is tminlist */
	curline = linelist;
        while (curline->pred != NULL)
		curline = curline->pred;
	while (curline != NULL)
	{
		curline->line->minors = curline->line->tminors;
		curline->line->lineno = 0;
		curline = curline->succ;
	}
}   /* build_tlist */






/*********************************************************************/
/*                                                                   */
/* decides which of the lines connected by shape is minor and adds   */
/* it to the minorlist                                               */
/*                                                                   */
/*********************************************************************/


void assign_shape_minors(SShape shape)
{
    switch (shape->direction)
    {
       case RIGHT:
        shape->targetpoint->xline->minors =
            		create_line_list (shape->sourcepoint->xline,
                                      shape->targetpoint->xline->minors);
        break;
    case LEFT:
        shape->sourcepoint->xline->minors =
            create_line_list (shape->targetpoint->xline,
                                           shape->sourcepoint->xline->minors);
        break;
    case UP:
        shape->targetpoint->yline->minors =
            		create_line_list (shape->sourcepoint->yline,
                                      shape->targetpoint->yline->minors);
        break;
    case DOWN:
        shape->sourcepoint->yline->minors =
            		create_line_list (shape->targetpoint->yline,
                                      shape->sourcepoint->yline->minors);
        break;
     }  /* switch direction */
}   /* assign_shape_minors */




/******************************************************/
/*                                                    */
/* fills the minor lists of the gridlines:            */
/* line1 is minor to line2 if it is more left or down */
/*                                                    */
/******************************************************/

void get_gridline_minors(SFaceList facelist, SGrid grid)
{
    SFace curface;
    SFaceElement element,
                 startelement;
    SShape shape;

   /* traverse all faces */
   curface = facelist->OuterFace;
   element = startelement = curface->connection;
   do
   {
	while (element->shape->pred != NULL)
             element->shape = element->shape->pred;
	for (shape = element->shape; shape != NULL; shape = shape->succ)
	{
		/* assign minors */
                assign_shape_minors(shape);
        }  /* for shape */
        element = element->succ;
   } /* do */
   while (element != startelement);
   while (facelist->InnerFaces->pred != NULL)
       facelist->InnerFaces = facelist->InnerFaces->pred;
   for (curface = facelist->InnerFaces; curface != NULL; curface = curface->succ)
   {
        /* traverse all shapes */
	   element = startelement = curface->connection;
	   do
	   {
		while (element->shape->pred != NULL)
		    element->shape = element->shape->pred;
		for (shape = element->shape; shape != NULL; shape = shape->succ)
		{
		    /* assign minors */
                   assign_shape_minors(shape);
		 }  /* for shape */
		 element = element->succ;
	     } /* do */
	     while (element != startelement);
   } /* for facelist */
   facelist->OuterFace->pred = NULL;
   facelist->OuterFace->succ = NULL;
   /* build transitive minor lists */
   build_tlist(grid->xlines);
   build_tlist(grid->ylines);
   build_majorlists(grid->xlines);
   build_majorlists(grid->ylines);
   add_gridline_minors(facelist, grid);
}  /* get_gridline_minors */





/****************************************************************/
/*                                                              */
/* assign gridlines to the gridpoints: points which are         */
/* connected by a shape are - depending on shape->direction -   */
/* placed on the same x- or y-line                              */
/*                                                              */
/****************************************************************/






/***********************************/
/*                                 */
/* creates newline, assigns points */
/* from both lines and sets the    */
/* points' linepointers on newline */
/*                                 */
/***********************************/

SGridLine get_merge_line(SGridLine line1, SGridLine line2, SGridPoint point)
{
          SGridLine newline;
          SGridPointList pointlist,
                         nextpointlist;
          int isX;    /* TRUE if line1,2 are xlines */

          /* create newline */
             newline = create_gr_line(point, NULL);
          /* build pointlist */
          while (line2->points->succ != NULL)
             line2->points = line2->points->succ;
          for (pointlist = line2->points; pointlist != NULL;
               pointlist = pointlist->pred)
          {
                newline->points = create_gridpoint_list(pointlist->point,
                                                        newline->points);
          } /* for pointlist */
          while (line1->points->succ != NULL)
             line1->points = line1->points->succ;
          for (pointlist = line1->points; pointlist != NULL;
               pointlist = pointlist->pred)
          {
                newline->points = create_gridpoint_list(pointlist->point,
                                                        newline->points);
          } /* for pointlist */
         /* free line1->points, line2->points */
	 pointlist = line1->points;
	 while (pointlist != NULL)
	 {
		nextpointlist = pointlist->pred;
		free(pointlist);
		pointlist = nextpointlist;
	 }
	 pointlist = line2->points;
	 while (pointlist != NULL)
	 {
		nextpointlist = pointlist->pred;
		free(pointlist);
		pointlist = nextpointlist;
	 }
     /* set linepointers */
     pointlist = newline->points;
     if (point->xline == line2)
        isX = TRUE;
     else
     isX = FALSE;
     while (pointlist != NULL)
     {
         if (isX)
             pointlist->point->xline = newline;
         else
             pointlist->point->yline = newline;
         pointlist = pointlist->pred;
     }  /* while pointlist */
     return newline;
}  /* get_merge_line */



/************************/
/*                      */
/* merges two gridlines */
/*                      */
/************************/

SGridLine merge_lines(SGridLine line1, SGridLine line2, SGridPoint point, SGridLineList linelist)
{
   SGridLine newline;
   SGridLineList list,
		 nextlist;
   int replaced;

   if(line1 != line2)
   {
      if(line2 == NULL)
      /* no line assigned yet -> assign line1 and complete pointlist */
      {
         newline = line1;
         newline->points = create_gridpoint_list(point, newline->points);
      }  /* if line2 == NULL */
      else
      /* line1 is next to line2: get newline and free line1 and line2 */
      {
         newline = get_merge_line(line1, line2, point);
         while (linelist->succ != NULL)
         linelist = linelist->succ;
         list = linelist;
         replaced = FALSE;
         while (list != NULL)
         {
                nextlist = list->pred;
                if ((list->line == line1) || (list->line == line2))
                {
                   if (replaced)
                   {
                      if (list->pred != NULL)
                         list->pred->succ = list->succ;
                      if (list->succ != NULL)
                         list->succ->pred = list->pred;
                      free (list);
                   }   /* if replaced */
                   else
                   {
                      list->line = newline;
                      replaced = TRUE;
                   }   /* if not replaced */
                }   /* if line1 or line2 */
                list = nextlist;
         }   /* while list */
         free(line1);
         free(line2);
      }  /* if line2 != NULL */
      return newline;  
   }  /* if lines not equal */
   return line1;
}  /* merge_lines */






/*********************************************/
/*                                           */
/* assigns xlines and ylines to targetpoints */
/* of shapes with direction right or left    */
/*                                           */
/*********************************************/

SShape assign_lines_right_left(SShape shape, SGrid grid)
{
   /* xline: if none exists: insert targetpoint xline */
   if (shape->targetpoint->xline == NULL)
   {
      shape->targetpoint->xline = create_gr_line(shape->targetpoint, NULL);
      grid->xlines = create_line_list(shape->targetpoint->xline, grid->xlines);
   }

   /* yline: merge targetpoint yline with sourcepoint yline */
   shape->targetpoint->yline = merge_lines(shape->sourcepoint->yline, 
                                             shape->targetpoint->yline,
                                              shape->targetpoint, grid->ylines);
   shape->sourcepoint->yline = shape->targetpoint->yline;
   return shape;
}  /* assign_lines_right_left */



/*********************************************/
/*                                           */
/* assigns xlines and ylines to targetpoints */
/* of shapes with direction up or down       */
/*                                           */
/*********************************************/

SShape assign_lines_up_down(SShape shape, SGrid grid)
{
   /* yline: if none exists: insert targetpoint yline after sourcepoint yline */
   if (shape->targetpoint->yline == NULL)
   {
      shape->targetpoint->yline = create_gr_line(shape->targetpoint, NULL);
      grid->ylines = create_line_list(shape->targetpoint->yline, grid->ylines);
   } /* if no yline */
   /* xline: merge targetpoint xline with sourcepoint yline */
   shape->targetpoint->xline = merge_lines(shape->sourcepoint->xline, 
                                           shape->targetpoint->xline,
                                           shape->targetpoint, grid->xlines);
   shape->sourcepoint->xline = shape->targetpoint->xline;
   return shape;
}  /* assign_lines_up_down */






/*******************************************/
/*                                         */
/* assigns xlines and ylines to gridpoints */
/*                                         */
/*******************************************/

void assign_gridlines(SFace face, SGrid grid)
{
   SFaceElement element,
                startelement;
   SShape shape;
  /* traversation of face's elements */
  element = startelement = face->connection;
  do
  {
     /* goto start of shapelist */
     while (element->shape->pred != NULL_SHAPE)
           element->shape = element->shape->pred;
    /* traverse shapelist and assign gridlines */
    for (shape = element->shape; shape != NULL_SHAPE; shape = shape->succ)
    {
        switch(shape->direction)
        {
            case RIGHT: assign_lines_right_left(shape, grid);
                        break;
            case LEFT:  assign_lines_right_left(shape, grid);
                        break;
            case UP:    assign_lines_up_down(shape, grid);
                        break;
            case DOWN:  assign_lines_up_down(shape, grid);
                        break;
        }  /* switch direction */
    }  /* for shapes */
    element = element->succ;
  }  /* do */
  while (element != startelement);
} /* assign_gridlines */



/********************************************************/
/*                                                      */
/* builds the grid on which the gridpoints are placed:  */
/* First, the gridlines are assigned to the gridpoints, */
/* connected points are placed on the same line.        */
/* Then for each gridline all lines which have to be    */
/* left of or under it are collected in its minorlist.  */
/* Finally, the gridlines are sorted and their numbers  */
/* are assigned.                                        */
/*                                                      */
/********************************************************/

SGrid grid_embed(SFaceList facelist)
{
   SFace face,
         curface;
   SGrid grid;

   grid = create_grid(NULL,NULL);
   face = facelist->OuterFace;
   while (face->connection->shape->pred != NULL_SHAPE)
         face->connection->shape = face->connection->shape->pred;
   /* first face: create gridlines */
   face->connection->shape->sourcepoint->xline = 
                 create_gr_line(face->connection->shape->sourcepoint, NULL);
   face->connection->shape->sourcepoint->yline = 
                 create_gr_line(face->connection->shape->sourcepoint, NULL);
   grid->xlines = create_line_list(face->connection->shape->sourcepoint->xline, NULL);
   grid->ylines = create_line_list(face->connection->shape->sourcepoint->yline, NULL);
   set_targetpoints(face);
   assign_gridlines(face, grid);
   face = facelist->InnerFaces;
   while (face->pred != NULL)
       face = face->pred;
   for (curface = face; curface != NULL; curface = curface->succ)
   {
      set_targetpoints(curface);
      assign_gridlines(curface, grid);
   }  /* for faces */
   get_gridline_minors(facelist, grid);
   sort_gridlines(grid->xlines);
   sort_gridlines(grid->ylines);
   return grid;
}
