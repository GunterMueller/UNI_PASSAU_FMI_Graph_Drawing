
/**************************************************************************/
/*                                                                        */
/*                            FREE_STRUCTS                                */
/*                                                                        */
/*                 Fuctions for freeing data structures                   */
/*                                                                        */
/*                                                                        */
/*                                                                        */
/*                                                                        */
/**************************************************************************/






#include "minimal_bends_layout2_export.h"

/***********************/
/*                     */
/* frees SFaceElements */
/*                     */
/***********************/

void free_elements(SFaceElement elementlist)
{
    SFaceElement element,
                 lastelement,
                 nextelement;

   /* remember first element since elementlist is circular */
   lastelement = elementlist->pred;

   /* free elements */
   element = elementlist;
   while (element != lastelement)
   {
    /* snode and sedge are Sgraph structures and not freed */
    /* free shape */
    if (element->shape != NULL)
        free_shape(element->shape);
    nextelement = element->succ;
    free(element);
    element = nextelement;
   }
   /* free last element */
   if (lastelement->shape != NULL)
        free_shape(lastelement->shape);
   free(lastelement);
}  /* free_elements */


/************************/
/*                      */
/* frees SGridPointList */
/*                      */
/************************/

void free_gridpointlist (SGridPointList list)
{
    SGridPointList nextlist;

    /* goto end of list */
    while (list->succ != NULL)
         list = list->succ;
    nextlist = list;
    while (list != NULL)
    {
        nextlist = list->pred;
        free(list);   /* point is freed in free_grid_structures */
        list = nextlist;
    }  /* while list */
} /* free_gridpointlist */

/************************/
/*                      */
/* frees SGridLineLists */
/*                      */
/************************/

void free_sgridlinelists(SGridLineList linelist)
{
    SGridLineList line,
                  nextline;

    /* goto start of linelist */
    if(linelist != NULL)
        while (linelist->pred != NULL)
            linelist = linelist->pred;

    /* free gridlines */
    line = linelist;
    while(line != NULL)
    {
        nextline = line->succ;
        free_gridpointlist(line->line->points);
        /* minors, tminors, majors and clines are already freed */
        free(line->line);
        free(line);
        line = nextline;
    }
}   /* free_sgridlines */


/************************/
/*                      */
/* frees SFaceShape     */
/*                      */
/************************/

void free_faceshape(SFaceShape faceshape)
{
    SFaceShape curfaceshape,
               nextfaceshape;

    /* goto start of faceshape */
    if(faceshape != NULL)
        while (faceshape->pred != NULL)
            faceshape = faceshape->pred;

    /* free faceshape */
    curfaceshape = faceshape;
    while(curfaceshape != NULL)
    {
        nextfaceshape = curfaceshape->succ;
        free(curfaceshape);
        curfaceshape = nextfaceshape;
    }
}   /* free_faceshape */


/***********************************************/
/*                                             */
/* frees clines without freeing anythinge else */
/*                                             */
/***********************************************/

void free_clines(SGrid grid)
{
    SGridLineList clinelist,
		  nextline,
                  linelist;

    linelist = grid->xlines;
    /* goto start of linelist */
    if(linelist != NULL)
        while (linelist->pred != NULL)
            linelist = linelist->pred;
    while(linelist != NULL)
    {
	clinelist = linelist->line->clines;
    	/* goto start of clinelist */
    	if(clinelist != NULL)
        	while (clinelist->pred != NULL)
            		clinelist = clinelist->pred;
    	/* free clines */
    	while(clinelist != NULL)
    	{
        	nextline = clinelist->succ;
        	free(clinelist);
        	clinelist = nextline;
    	}
	linelist->line->clines = NULL;
        linelist = linelist->succ;
    }
    linelist = grid->ylines;
    /* goto start of linelist */
    if(linelist != NULL)
        while (linelist->pred != NULL)
            linelist = linelist->pred;
    while(linelist != NULL)
    {
	clinelist = linelist->line->clines;
    	/* goto start of clinelist */
    	if(clinelist != NULL)
        	while (clinelist->pred != NULL)
            		clinelist = clinelist->pred;
    	/* free clines */
    	while(clinelist != NULL)
    	{
        	nextline = clinelist->succ;
        	free(clinelist);
        	clinelist = nextline;
    	}
	linelist->line->clines = NULL;
        linelist = linelist->succ;
    }
}   /* free_clines */


/***********************************************/
/*                                             */
/* frees majors without freeing anythinge else */
/*                                             */
/***********************************************/

void free_majors(SGrid grid)
{
    SGridLineList majorlist,
		  nextline,
                  linelist;

    linelist = grid->xlines;
    /* goto start of linelist */
    if(linelist != NULL)
        while (linelist->pred != NULL)
            linelist = linelist->pred;
    while(linelist != NULL)
    {
	majorlist = linelist->line->majors;
    	/* goto start of clinelist */
    	if(majorlist != NULL)
        	while (majorlist->pred != NULL)
            		majorlist = majorlist->pred;
    	/* free majors */
    	while(majorlist != NULL)
    	{
        	nextline = majorlist->succ;
        	free(majorlist);
        	majorlist = nextline;
    	}
        linelist = linelist->succ;
    }
    linelist = grid->ylines;
    /* goto start of linelist */
    if(linelist != NULL)
        while (linelist->pred != NULL)
            linelist = linelist->pred;
    while(linelist != NULL)
    {
	majorlist = linelist->line->majors;
    	/* goto start of clinelist */
    	if(majorlist != NULL)
        	while (majorlist->pred != NULL)
            		majorlist = majorlist->pred;
    	/* free majors */
    	while(majorlist != NULL)
    	{
        	nextline = majorlist->succ;
        	free(majorlist);
        	majorlist = nextline;
    	}
        linelist = linelist->succ;
    }
}   /* free_majors */

/*************************************************/
/*						 */
/*   frees faceminors of a facegrid              */
/*						 */
/*************************************************/

void free_faceminors(SGrid grid)
{
    SGridLineList faceminorlist,
		  nextline,
                  linelist;

    linelist = grid->xlines;
    /* goto start of linelist */
    if(linelist != NULL)
        while (linelist->pred != NULL)
            linelist = linelist->pred;
    while(linelist != NULL)
    {
	faceminorlist = linelist->line->faceminors;
	linelist->line->faceminors=NULL;
    	/* goto start of clinelist */
    	if(faceminorlist != NULL)
        	while (faceminorlist->pred != NULL)
            		faceminorlist = faceminorlist->pred;
    	/* free faceminors */
    	while(faceminorlist != NULL)
    	{
        	nextline = faceminorlist->succ;
        	free(faceminorlist);
        	faceminorlist = nextline;
    	}
        linelist = linelist->succ;
    }
    linelist = grid->ylines;
    /* goto start of linelist */
    if(linelist != NULL)
        while (linelist->pred != NULL)
            linelist = linelist->pred;
    while(linelist != NULL)
    {
	faceminorlist = linelist->line->faceminors;
  	linelist->line->faceminors=NULL;
  	/* goto start of clinelist */
    	if(faceminorlist != NULL)
        	while (faceminorlist->pred != NULL)
            		faceminorlist = faceminorlist->pred;
    	/* free faceminors */
    	while(faceminorlist != NULL)
    	{
        	nextline = faceminorlist->succ;
        	free(faceminorlist);
        	faceminorlist = nextline;
    	}
        linelist = linelist->succ;
    }
}   /* free_faceminors */


/********************************************/
/*                                          */
/* frees facegrid without freeing gridlines */
/*                                          */
/********************************************/

void free_facegrid(SGrid facegrid)
{
    SGridLineList nextline,
                  linelist;

    linelist = facegrid->xlines;
    /* goto start of linelist */
    if(linelist != NULL)
        while (linelist->pred != NULL)
            linelist = linelist->pred;
    /* free linelist */
    while(linelist != NULL)
    {
        nextline = linelist->succ;
        free(linelist);
        linelist = nextline;
    }
    linelist = facegrid->ylines;
    /* goto start of linelist */
    if(linelist != NULL)
        while (linelist->pred != NULL)
            linelist = linelist->pred;
    /* free linelist */
    while(linelist != NULL)
    {
        nextline = linelist->succ;
        free(linelist);
        linelist = nextline;
    }
    free(facegrid);
}   /* free_facegrid */




/*******************/
/*                 */
/* frees Snetedges */
/*                 */
/*******************/

void free_snetedges(Snetedge netedgelist)
{
    Snetedge edge,
             nextedge;

    /* goto start of netedgelist */
    while(netedgelist->pred != NULL)
        netedgelist = netedgelist->pred;

   /* free netedgelist */
   edge = netedgelist;
   while (edge != NULL)
   {
    /* snode and tnode are freed in free_snetnodes */
    /* father is part of netedgelist */
    nextedge = edge->succ;
    free(edge);
    edge = nextedge;
   }
}  /* free_snetedges */




/*******************/
/*                 */
/* frees Sedgelist */
/*                 */
/*******************/

void free_sedgelist(Sedgelist edgelist)
{
    Sedgelist listelement,
              nextelement;

    /* goto start of edgelist */
    while(edgelist->pred != NULL)
        edgelist = edgelist->pred;

   /* free edgelist */
   listelement = edgelist;
   while (listelement != NULL)
   {
    /* edge is freed in free_snetedges */
    nextelement = listelement->succ;
    free(listelement);
    listelement = nextelement;
   }
}  /* free_sedgelist */


/*******************/
/*                 */
/* frees Snetnodes */
/*                 */
/*******************/

void free_snetnodes(Snetnode nodelist)
{
    Snetnode node,
             nextnode;

    /* goto start of nodelist */
    while (nodelist->pred != NULL)
        nodelist = nodelist->pred;

   /* free nodes */
   node = nodelist;
   while(node != NULL)
   {
    /* real is freed in free_facelist_structures or also in nodelist */
    /* inedge is freed in free_snetedges */
    if(node->outedgelist != NULL)
        free_sedgelist(node->outedgelist);
    nextnode = node->succ;
    free(node);
    node = nextnode;
   }
}   /* free_snetnodes */



/******************/
/*                */
/* frees Snetwork */
/*                */
/******************/

void free_network_structures(Snetwork network)
{
    /* free nodelist */
    if(network->nodelist != NULL)
        free_snetnodes(network->nodelist);
    /* free edgelist */
    if (network->edgelist != NULL)
        free_snetedges(network->edgelist);
    /* free network */
    free(network);
}   /* free_network_structures */



/****************************************************************************/
/*                                                                          */
/* frees SGrid, SGridLineLists, SGridLines, SGridPointLists and SGridPoints */
/*                                                                          */
/****************************************************************************/

void free_grid_structures(SGrid sGrid)
{
    /* free majors and clines */
    free_majors(sGrid);
    free_clines(sGrid);
    /* gridlines are freed via the gridlinelists */
    free_sgridlinelists(sGrid->xlines);
    free_sgridlinelists(sGrid->ylines);

    /* free grid */
    free(sGrid);
    /* free SGridPoints */
    store_alloc(NULL, FALSE);
}   /* free_grid_structures */


/*******************/
/*                 */
/* frees SFaceList */
/*                 */
/*******************/


void free_facelist_structures(SFaceList facelist)
{
    SFace face,
          nextface;

    /* goto start of inner faces list */
    if (facelist->InnerFaces != NULL)
        while(facelist->InnerFaces->pred != NULL)
            facelist->InnerFaces = facelist->InnerFaces->pred;

    /* free faces: start with outer face */
    face = facelist->OuterFace;
    if (face != NULL)
    {
        /* free face elements: connection is in element list */
        if(face->elements != NULL)
            free_elements(face->elements);
        /* netnode is freed in free_network_structures */
        /* free face */
        free(face);
    }
    /* free inner faces */
    face = facelist->InnerFaces;
    while (face != NULL)
    {
        /* free face elements: connection is in element list */
        if(face->elements != NULL)
            free_elements(face->elements);
        /* netnode is freed in free_network_structures */
        /* free face */
        nextface = face->succ;
        free(face);
        face = nextface;
    }

    /* free facelist */
    free(facelist);
}   /* free_facelist_structures */




/**************/
/*            */
/*  frees all */
/*            */
/**************/


void free_data_structures(Snetwork network, SFaceList facelist, SGrid sGrid)
{
    if (network != NULL)
        free_network_structures(network);
    if (facelist != NULL)
    {
        free_grid_structures(sGrid);
        free_facelist_structures(facelist);
    }
}   /* free_data_structure */

