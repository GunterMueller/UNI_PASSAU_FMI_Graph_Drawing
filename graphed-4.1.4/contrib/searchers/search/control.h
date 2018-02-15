
/* do not include this file twice */
#ifndef _INCLUDE_CONTROL
#define _INCLUDE_CONTROL

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

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>


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

#define	COmethod()		COdataMethod( (Method)0, DATA_GET )
#define COsetMethod( NTH )	COdataMethod( NTH, DATA_SET )

#define COstrategy()		COdataStrategy( (Strategy)0, DATA_GET )
#define COsetStrategy( NTH )	COdataStrategy( NTH, DATA_SET )

#define COalgorithm()		COdataAlgorithm( 0, DATA_GET )
#define COsetAlgorithm( NTH )	COdataAlgorithm( NTH, DATA_SET )

#define COmanual()		COdataManual( FALSE, DATA_GET )
#define COsetManual( BOOL )	COdataManual( BOOL, DATA_SET )

#define COanimation()		COdataAnimation( FALSE, DATA_GET )
#define COanimate()		COdataAnimation( FALSE, DATA_GET )
#define COsetAnimation( BOOL )	COdataAnimation( BOOL, DATA_SET )


#define COinterval()		COdataInterval( 0, DATA_GET )
#define COsetInterval( TIME )	COdataInterval( TIME, DATA_SET )

#define COstatistics()		COdataStatistics( FALSE, DATA_GET )
#define COsetStatistics( BOOL )	COdataStatistics( BOOL, DATA_SET )
#define COtoggleStatistics()	COdataStatistics( FALSE, DATA_PROCESS )




#define COsearchers()		COreport( 0, COSEARCHERS, DATA_GET )
#define COsetSearchers( NUM )	COreport( NUM, COSEARCHERS, DATA_SET )
#define COincSearchers()	COreport( 0, COSEARCHERS, DATA_INC )
#define COdecSearchers()	COreport( 0, COSEARCHERS, DATA_DEC )


#define COmaxSearchers()	COreport( 0, COMAX_SEARCHERS, DATA_GET )
#define COsetMaxSearchers( NUM ) COreport( NUM, COMAX_SEARCHERS, DATA_SET )
#define COincMaxSearchers()	COreport( 0, COMAX_SEARCHERS, DATA_INC )
#define COdecMaxSearchers()	COreport( 0, COMAX_SEARCHERS, DATA_DEC )

#define COhiddenSearchers()		COreport( 0, COHIDDEN_SEARCHERS, DATA_GET )
#define COsetHiddenSearchers( NUM )	COreport( NUM, COHIDDEN_SEARCHERS, DATA_SET )
#define COincHiddenSearchers()	COreport( 0, COHIDDEN_SEARCHERS, DATA_INC )
#define COdecHiddenSearchers()	COreport( 0, COHIDDEN_SEARCHERS, DATA_DEC )


#define COhiddenMaxSearchers()	COreport( 0, COMAX_HIDDEN_SEARCHERS, DATA_GET )
#define COsetHiddenMaxSearchers( NUM ) COreport( NUM, COMAX_HIDDEN_SEARCHERS, DATA_SET )
#define COincHiddenMaxSearchers()	COreport( 0, COMAX_HIDDEN_SEARCHERS, DATA_INC )
#define COdecHiddenMaxSearchers()	COreport( 0, COMAX_HIDDEN_SEARCHERS, DATA_DEC )

#define COsteps()		COreport( 0, COSTEPS, DATA_GET )
#define COsetSteps( NUM )	COreport( NUM, COSTEPS, DATA_SET )
#define COincSteps()		COreport( 0, COSTEPS, DATA_INC )
#define COdecSteps()		COreport( 0, COSTEPS, DATA_DEC )

#define COmaxSteps()		COreport( 0, COMAX_STEPS, DATA_GET )
#define COsetMaxSteps( NUM )	COreport( NUM, COMAX_STEPS, DATA_SET )
#define COincMaxSteps()		COreport( 0, COMAX_STEPS, DATA_INC )
#define COdecMaxSteps()		COreport( 0, COMAX_STEPS, DATA_DEC )

#define COnodes()		COreport( 0, CONODES, DATA_GET )
#define COsetNodes( NUM )	COreport( NUM, CONODES, DATA_SET )
#define COincNodes()		COreport( 0, CONODES, DATA_INC )
#define COdecNodes()		COreport( 0, CONODES, DATA_DEC )

#define COnodesUnset()		COreport( 0, CONODES_UNSET, DATA_GET )
#define COsetNodesUnset( NUM )	COreport( NUM, CONODES_UNSET, DATA_SET )
#define COincNodesUnset()	COreport( 0, CONODES_UNSET, DATA_INC )
#define COdecNodesUnset()	COreport( 0, CONODES_UNSET, DATA_DEC )

#define COnodesSet()		COreport( 0, CONODES_SET, DATA_GET )
#define COsetNodesSet( NUM )	COreport( NUM, CONODES_SET, DATA_SET )
#define COincNodesSet()		COreport( 0, CONODES_SET, DATA_INC )
#define COdecNodesSet()		COreport( 0, CONODES_SET, DATA_DEC )

#define COnodesNotSet()		COreport( 0, CONODES_NOT_SET, DATA_GET )
#define COsetNodesNotSet( NUM )	COreport( NUM, CONODES_NOT_SET, DATA_SET )
#define COincNodesNotSet()	COreport( 0, CONODES_NOT_SET, DATA_INC )
#define COdecNodesNotSet()	COreport( 0, CONODES_NOT_SET, DATA_DEC )

#define COedges()		COreport( 0, COEDGES, DATA_GET )
#define COsetEdges( NUM )	COreport( NUM, COEDGES, DATA_SET )
#define COincEdges()		COreport( 0, COEDGES, DATA_INC )
#define COdecEdges()		COreport( 0, COEDGES, DATA_DEC )

#define COedgesClear()		COreport( 0, COEDGES_CLEAR, DATA_GET )
#define COsetEdgesClear( NUM )	COreport( NUM, COEDGES_CLEAR, DATA_SET )
#define COincEdgesClear()	COreport( 0, COEDGES_CLEAR, DATA_INC )
#define COdecEdgesClear()	COreport( 0, COEDGES_CLEAR, DATA_DEC )

#define COedgesNotClear()	COreport( 0, COEDGES_NOT_CLEAR, DATA_GET )
#define COsetEdgesNotClear( NUM ) COreport( NUM, COEDGES_NOT_CLEAR, DATA_SET )
#define COincEdgesNotClear()	COreport( 0, COEDGES_NOT_CLEAR, DATA_INC )
#define COdecEdgesNotClear()	COreport( 0, COEDGES_NOT_CLEAR, DATA_DEC )

#define COreset()		COreport( 0, (ReportData)NULL, DATA_RESET ) 



#define COfastPlay()		COdataFastPlay( 0, DATA_GET )
#define COsetFastPlay()		COdataFastPlay( TRUE, DATA_SET )

#define COplay()		COdataPlay( 0, DATA_GET )
#define COsetPlay()		COdataPlay( TRUE, DATA_SET )
#define COstop()		(!COdataPlay( 0, DATA_GET )
#define COsetStop()		COdataPlay( FALSE, DATA_SET )

#define COrecorded()		COdataRecorded( 0, DATA_GET )
#define COsetRecorded( BOOL )	COdataRecorded( BOOL, DATA_SET )

#define CObackstep()		COdataBackstep( 0, DATA_GET )
#define COsetBackstep( BOOL )	COdataBackstep( BOOL, DATA_SET )


#define COsaveGraphinfo( INFO )	COdataGraphinfo( INFO, DATA_SET )
#define COgetBuffer()		COdataGraphinfo( 0, DATA_GET )
#define COrepaint()		COdataGraphinfo( 0, DATA_PROCESS )

#define COsetSetNodeType(TYPE )	COdataSetNodeType( TYPE, DATA_SET )
#define COgetSetNodeType( )	COdataSetNodeType( 0, DATA_GET )
#define COsetUnsetNodeType( TYPE )	COdataUnsetNodeType( TYPE, DATA_SET )
#define COgetUnsetNodeType( )	COdataUnsetNodeType( 0, DATA_GET )
#define COsetMoveNodeType( TYPE )	COdataMoveNodeType( TYPE, DATA_SET )
#define COgetMoveNodeType( )	COdataMoveNodeType( 0, DATA_GET )




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

typedef enum {
	COSEARCHERS,
	COMAX_SEARCHERS,
	COHIDDEN_SEARCHERS,
	COMAX_HIDDEN_SEARCHERS,
	COSTEPS,
	COMAX_STEPS,
	CONODES,
	CONODES_SET,
	CONODES_NOT_SET,
	CONODES_UNSET,
	COEDGES,
	COEDGES_CLEAR,
	COEDGES_NOT_CLEAR,

	COMAX_REPORT

} ReportData;


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


/* file: data.c */
extern bool             COdataRecorded(int data, DataAdmin what);
extern Method		COdataMethod(Method data, DataAdmin what);
extern Strategy		COdataStrategy(Strategy data, DataAdmin what);
extern unsigned int	COdataAlgorithm(unsigned int data, DataAdmin what);
extern bool		COdataManual(int data, DataAdmin what);
extern bool		COdataAnimation(int data, DataAdmin wha);
extern unsigned int	COdataInterval(unsigned int data, DataAdmin what);
extern bool		COdataStatistics(int data, DataAdmin what);

extern unsigned int	COreport(unsigned int data, ReportData which_data, DataAdmin what);
extern int             	COdataGraphinfo(Sgraph_proc_info data, DataAdmin what);
extern int		COdataSetNodeType(int data, DataAdmin what);
extern int		COdataUnsetNodeType(int data, DataAdmin what);
extern bool		COdataBackstep(int data, DataAdmin what);
extern bool		COdataFastPlay(int data, DataAdmin what);
extern bool		COdataPlay(int data, DataAdmin what);
extern Sgraph		COgetGraph(void);


extern bool 		COrecEndOfTape(void);
extern bool		COrecStartOfTape(void);
extern void        	COwait(unsigned int *seconds, unsigned int *microseconds);
extern int              COdataMoveNodeType(int data, DataAdmin what);


/* file: control.c */
extern void	COnotifyInfo(char *string);
extern void	COnotifyInterval(unsigned int value);
extern void	COnotifyAnimation(int value);
extern void	COnotifyMethod(Method value);
extern void	COnotifyStrategy(Strategy value);
extern void	COnotifyManual(int value);
extern void	COnotifyAlgorithm(unsigned int value);
extern void	COnotifySearch(void);
extern void	COnotifyClear(void);
extern void	COnotifyBackstep(void);
extern void	COnotifyRewind(void);
extern void	COnotifyRewindStep(void);
extern void	COnotifyPlay(void);
extern void	COnotifyStop(void);
extern void	COnotifyForwardStep(void);
extern void	COnotifyForward(void);
extern void	COnotifyStep(void);
extern void	COnotifyReset(void);
extern void	COnotifyRecorded(void);
extern void 	COnotifyLastFewSteps(void);




extern void	COsearch(Sgraph_proc_info graphinfo);

extern void	COtestSearchability(Sgraph graph);
extern void	COfreeSearchStructure(Sgraph graph);
extern void	COinitSearchStructure(Sgraph graph, int origina);
extern void	COinitGraphAttributes(Sgraph graph);
extern void	COstartManualControl(Sgraph_proc_info graphinfo);
extern void	COstopManualControl(Sgraph_proc_info graphinfo);

extern void	COmanualSetOn(Snode node);
extern void	COmanualMove(Sedge edge);


extern void	COinitProgram(void);

extern Sgraph	MakeCopyOfGraph(Sgraph graph);



/* file: if.c */
extern char	*COnextAlgorithm(void);
extern bool	COalgorithmAnimation(int nr);
extern VoidFunction	COalgorithmInitAndTest(int nr);
extern VoidFunction	COalgorithmSearch(int nr);
extern VoidFunction	COalgorithmFree(int nr);
extern bool	add_to_search_algorithms(char *name, VoidFunction proc_init_and_test, VoidFunction proc_search, VoidFunction proc_free, Strategy strategy, int animation, int es, int ns, int ms);



#endif /* do not inclune file twice */
/******************************************************************************
*		       [EOF] end of header-file %M% 
******************************************************************************/
