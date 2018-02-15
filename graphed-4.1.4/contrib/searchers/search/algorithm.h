
/* do not include this file twice */
#ifndef _INCLUDE_ALGORITHM
#define _INCLUDE_ALGORITHM

/*******************************************************************************
*									       *
*									       *
*			SEARCH STRATEGIES ON GRAPHS			       *
*									       *
*									       *
*	Copyright (C) 1992/93 Andreas Schweikardt			       *
********************************************************************************




	File	:	%M%

	Date	:	%E%	(%U%)

	Version	:	%I%

	Author	:	Schweikardt, Andreas



Portability

	Language		:	C
	Operating System	:	Sun-OS (UNIX)
	User Inteface (graphic)	:	
	Other			:	GraphEd & Sgraph


********************************************************************************


Layer   : 	

Modul   :	


********************************************************************************


Description of %M% :



*******************************************************************************/


/******************************************************************************
*									      *
*			standard includes				      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			gui includes    	 			      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			GraphEd & Sgraph includes			      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			local includes		 			      *
*									      *
*******************************************************************************/

#include <searchers/search/search.h>

/******************************************************************************
*									      *
*			defines 		 			      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			macros  		 			      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			structs			 			      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			types			 			      *
*									      *
*******************************************************************************/


typedef struct {
	int	mark;
	int	separator_mark;
	int	temp_mark;
	Snode	isocopy,
		isoorg;
} SeparatorNode;




/******************************************************************************
*									      *
*			global variables				      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			functions					      *
*									      *
*******************************************************************************/

extern void	OptimumInitAndTest(Sgraph graph, Method method);
extern void	OptimumSearch(Sgraph graph, Method method);
extern void	OptimumFree(Sgraph graph, Method method);

extern void	GridInitAndTest(Sgraph graph, Method method);
extern void	GridSearch(Sgraph graph, Method method);

extern void	TreeInitAndTest(Sgraph graph, Method method);
extern void	TreeSearch(Sgraph graph, Method method);
extern void	TreeFree(Sgraph graph, Method method);

extern void	CographInitAndTest(Sgraph graph, Method method);
extern void	CographSearch(Sgraph graph, Method method);
extern void	CographFree(Sgraph graph, Method method);

extern void	CompleteInitAndTest(Sgraph graph, Method method);
extern void	CompleteSearch(Sgraph graph, Method method);

extern void	KnmInitAndTest(Sgraph graph, Method method);
extern void	KnmSearch(Sgraph graph, Method method);

extern void	SmallestDegreeSearch(Sgraph graph, Method method);

extern void	BruteForceSearch(Sgraph graph, Method method);

extern void	AppTreeInitAndTest(Sgraph graph, Method method);
extern void	AppTreeSearch(Sgraph graph, Method method);
extern void	AppTreeFree(Sgraph graph, Method method);

extern void	App2TreeInitAndTest(Sgraph graph, Method method);
extern void	App2TreeSearch(Sgraph graph, Method method);

extern void	AppGridInitAndTest(Sgraph graph, Method method);
extern void	AppGridSearch(Sgraph graph, Method method);


extern void	MinComponentLogSepSearch(Sgraph graph, Method method);
extern void	MovingSepSearch(Sgraph graph, Method method);
extern Slist	Separator(Sgraph graph);

extern void	CliqueSearch(Sgraph graph, Method method);

extern void	SubgraphSearch(Sgraph graph, Method method);

extern void	MinorSearch(Sgraph graph, Method method);

extern void	NodesAndEdgesSearch(Sgraph graph, Method method);



#endif /* do not include file twice */
/******************************************************************************
*		       [EOF] end of header-file %M% 
******************************************************************************/
