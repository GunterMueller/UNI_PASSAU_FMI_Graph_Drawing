/****************************************************************************\
 *                                                                          *
 *  nonplanarity_export.h                                                   *
 *  ---------------------                                                   *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
\****************************************************************************/


/*
#define SUN_VERSION
*/

extern	GraphEd_Menu_Proc MenuPQPlanarityTest;

extern	GraphEd_Menu_Proc MenuMaxPlanarExecuteJayakumar;
extern	GraphEd_Menu_Proc MenuMaxPlanarExecuteGreedy;
extern	GraphEd_Menu_Proc MenuMaxPlanarExecuteRandomizedGreedy;
extern	GraphEd_Menu_Proc MenuMaxPlanarExecuteRandomizedGraphTest;
extern	GraphEd_Menu_Proc MenuMaxPlanarSettings;

extern	GraphEd_Menu_Proc MenuThicknessExecute;
extern	GraphEd_Menu_Proc MenuThicknessSettings;

extern	GraphEd_Menu_Proc MenuCrossingNumberExecuteConvexDraw;
extern	GraphEd_Menu_Proc MenuCrossingNumberExecuteChrobakPayne;
extern	GraphEd_Menu_Proc MenuCrossingNumberExecuteChrobakPayneAsslia;
extern	GraphEd_Menu_Proc MenuCrossingNumberExecuteSpringRF;
extern	GraphEd_Menu_Proc MenuCrossingNumberExecuteSpringKamada;
extern	GraphEd_Menu_Proc MenuCrossingNumberExecuteNaiveEmbedding;
extern	GraphEd_Menu_Proc MenuCrossingNumberExecuteCompleteEmbedding;
extern	GraphEd_Menu_Proc MenuCrossingNumberExecuteBipartiteEmbedding;
extern	GraphEd_Menu_Proc MenuCrossingNumberCountCrossings;
extern	GraphEd_Menu_Proc MenuCrossingNumberSettings;


extern	GraphEd_Menu_Proc MenuBiConnComp;
extern	GraphEd_Menu_Proc MenuResetGraph;



extern	void	CallPQPlanarityTest (Sgraph_proc_info info);

extern	void	CallMaxPlanarJayakumar (Sgraph_proc_info info);
extern	void	CallMaxPlanarGreedy (Sgraph_proc_info info);
extern	void	CallMaxPlanarRandomizedGreedy (Sgraph_proc_info info);
extern	void	CallMaxPlanarRandomizedGraphTest (Sgraph_proc_info info);

extern	void	CallThickness (Sgraph_proc_info info);

extern	void	CallCrossingNumber (Sgraph_proc_info info);
extern	void	CallCrossingNumberNaiveEmbedding (Sgraph_proc_info info);
extern	void	CallCrossingNumberCompleteEmbedding (Sgraph_proc_info info);
extern	void	CallCrossingNumberBipartiteEmbedding (Sgraph_proc_info info);

extern	void	CallCrossingNumberPrepare (Sgraph_proc_info info);
extern	void	CallCrossingNumberBipartitePrepare (Sgraph_proc_info info);
extern	void	CallEmbedRemainingEdges(Sgraph_proc_info info);
extern	void	CallCountCrossingsInEmbedding(Sgraph_proc_info info);


extern	void	CallBiConnComp (Sgraph_proc_info info);
extern	void	CallResetGraph (Sgraph_proc_info info);


#include "nonplanarity/nonplanarity_settings.h"
