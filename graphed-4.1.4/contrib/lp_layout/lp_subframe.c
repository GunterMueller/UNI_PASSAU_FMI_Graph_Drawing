#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "user_header.h"

#include <X11/Xos.h>

#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/icon.h>
#include <xview/notify.h>

extern	Frame	base_frame;	/* GraphEd's baseframe	*/

#include "lpa_redraw.h"
#include "lpa_draw_new_tree.h"
#include "lpm_create_lgg.h"
#include "lpm_create_lgg_oriented.h"
#include "lpp_parse.h"
#include "lpr_top_down_cost_opt.h"

#include "lp_reduce.h"
#include "lp_open.h"
#include "lp_delete.h"
#include "lp_draw_tree.h"
#include "lp_draw.h"

#include <gragra_parser/lp_main.h>

#include "lp_test.h"
#include "lp_general_functions.h"
#include "lp_win_help.h"
#include "lp_subframe.h"



#define	FRAME_WIDTH		500
/****************************************************************************************/
/*											*/
/* In diesem Modul wird ein eigener Frame fuer Layout-Algorithmen erzeugt		*/
/*											*/
/****************************************************************************************/

/****************************************************************************************/
/* Fkt um Panels zur Algorithmusauswahl zu verstecken					*/
/****************************************************************************************/

void	hide_optimization_panels(void)
{
	xv_set( LP_WIN.text_302,	XV_SHOW,	FALSE, 	0);
	xv_set( LP_WIN.text_303,	XV_SHOW,	FALSE, 	0);
	xv_set( LP_WIN.text_304,	XV_SHOW,	FALSE, 	0);
	xv_set( LP_WIN.text_305,	XV_SHOW,	FALSE, 	0);
	xv_set( LP_WIN.text_306,	XV_SHOW,	FALSE, 	0);
	xv_set( LP_WIN.text_307,	XV_SHOW,	FALSE, 	0);
	xv_set( LP_WIN.text_308,	XV_SHOW,	FALSE, 	0);

	xv_set( LP_WIN.choice_302,	XV_SHOW,	FALSE,	0);
	xv_set( LP_WIN.choice_303,	XV_SHOW,	FALSE,	0);
	xv_set( LP_WIN.choice_304,	XV_SHOW,	FALSE,	0);
	xv_set( LP_WIN.choice_305,	XV_SHOW,	FALSE,	0);
	xv_set( LP_WIN.choice_306,	XV_SHOW,	FALSE,	0);

}

/****************************************************************************************/
/* Fkt um Panels zur Algorithmusauswahl sichtbar zu machen				*/
/****************************************************************************************/

void	unhide_optimization_panels(void)
{
	xv_set( LP_WIN.text_302,	XV_SHOW,	TRUE, 	0);
	xv_set( LP_WIN.text_303,	XV_SHOW,	TRUE, 	0);
	xv_set( LP_WIN.text_304,	XV_SHOW,	TRUE, 	0);
	xv_set( LP_WIN.text_305,	XV_SHOW,	TRUE, 	0);
	xv_set( LP_WIN.text_306,	XV_SHOW,	TRUE, 	0);
	xv_set( LP_WIN.text_307,	XV_SHOW,	TRUE, 	0);
	xv_set( LP_WIN.text_308,	XV_SHOW,	TRUE, 	0);

	xv_set( LP_WIN.choice_302,	XV_SHOW,	TRUE,	0);
	xv_set( LP_WIN.choice_303,	XV_SHOW,	TRUE,	0);
	xv_set( LP_WIN.choice_304,	XV_SHOW,	TRUE,	0);
	xv_set( LP_WIN.choice_305,	XV_SHOW,	TRUE,	0);
	xv_set( LP_WIN.choice_306,	XV_SHOW,	TRUE,	0);
}
/****************************************************************************************/
/*											*/
/* Grammar_panel muss je nach Algorithmus verschiedene Grammatik-Eigenschaften anzeigen	*/
/* Das macht diese Funktion								*/
/*											*/
/****************************************************************************************/

void	set_grammar_panel_corresponding_to_algorithm(int alg)
{
	switch( alg )
	{
		case	TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP:
				xv_set( LP_WIN.box_101,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][BNLC_GRAMMAR], 		0);
				xv_set( LP_WIN.box_102,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][DIFF_EMBEDDINGS], 		0);
				xv_set( LP_WIN.box_103,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][RECTANGULAR_EDGELINES], 	0);
				xv_set( LP_WIN.box_104,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][GRID_LAYOUT], 			0);
				xv_set( LP_WIN.box_105,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][UNIT_LAYOUT], 			0);
				xv_set( LP_WIN.box_106,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][INTERSECTING_LINE],	 	0);
				xv_set( LP_WIN.box_107,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][LINE_OUT_OF_PROD], 		0);
				xv_set( LP_WIN.box_108,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][DEGENERATED_EDGE], 		0);
				xv_set( LP_WIN.box_109,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][BORDER_GAP_FULLFILLED], 	0);
				xv_set( LP_WIN.box_110,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][NODE_DISTANCE],	 	0);
				xv_set( LP_WIN.box_111,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][EDGE_OVERLAPPING],	 	0);
				xv_set( LP_WIN.box_112,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][NON_TERM_UNIT],	 	0);
				xv_set( LP_WIN.box_113,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][ENCE_1_GRAMMAR],	 	0);
				xv_set( LP_WIN.box_114,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP][SIMPLE_EMBEDDINGS],	 	0);
				break;

		case 	TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B:
				xv_set( LP_WIN.box_101,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][BNLC_GRAMMAR], 		0);
				xv_set( LP_WIN.box_102,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][DIFF_EMBEDDINGS], 		0);
				xv_set( LP_WIN.box_103,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][RECTANGULAR_EDGELINES], 	0);
				xv_set( LP_WIN.box_104,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][GRID_LAYOUT], 		0);
				xv_set( LP_WIN.box_105,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][UNIT_LAYOUT], 		0);
				xv_set( LP_WIN.box_106,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][INTERSECTING_LINE],	 	0);
				xv_set( LP_WIN.box_107,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][LINE_OUT_OF_PROD], 		0);
				xv_set( LP_WIN.box_108,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][DEGENERATED_EDGE], 		0);
				xv_set( LP_WIN.box_109,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][BORDER_GAP_FULLFILLED], 	0);
				xv_set( LP_WIN.box_110,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][NODE_DISTANCE],	 	0);
				xv_set( LP_WIN.box_111,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][EDGE_OVERLAPPING],	 	0);
				xv_set( LP_WIN.box_112,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][NON_TERM_UNIT],	 	0);
				xv_set( LP_WIN.box_113,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][ENCE_1_GRAMMAR],	 	0);
				xv_set( LP_WIN.box_114,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B][SIMPLE_EMBEDDINGS],	 	0);
				break;

		case	TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP:
				xv_set( LP_WIN.box_101,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][BNLC_GRAMMAR], 			0);
				xv_set( LP_WIN.box_102,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][DIFF_EMBEDDINGS], 		0);
				xv_set( LP_WIN.box_103,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][RECTANGULAR_EDGELINES], 		0);
				xv_set( LP_WIN.box_104,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][GRID_LAYOUT], 			0);
				xv_set( LP_WIN.box_105,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][UNIT_LAYOUT], 			0);
				xv_set( LP_WIN.box_106,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][INTERSECTING_LINE],	 	0);
				xv_set( LP_WIN.box_107,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][LINE_OUT_OF_PROD], 		0);
				xv_set( LP_WIN.box_108,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][DEGENERATED_EDGE], 		0);
				xv_set( LP_WIN.box_109,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][BORDER_GAP_FULLFILLED], 		0);
				xv_set( LP_WIN.box_110,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][NODE_DISTANCE],		 	0);
				xv_set( LP_WIN.box_111,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][EDGE_OVERLAPPING],	 	0);
				xv_set( LP_WIN.box_112,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][NON_TERM_UNIT],	 		0);
				xv_set( LP_WIN.box_113,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][ENCE_1_GRAMMAR],	 		0);
				xv_set( LP_WIN.box_114,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][SIMPLE_EMBEDDINGS],	 	0);
				break;

		case	TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI:
				xv_set( LP_WIN.box_101,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][BNLC_GRAMMAR], 		0);
				xv_set( LP_WIN.box_102,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][DIFF_EMBEDDINGS], 		0);
				xv_set( LP_WIN.box_103,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][RECTANGULAR_EDGELINES], 	0);
				xv_set( LP_WIN.box_104,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][GRID_LAYOUT], 		0);
				xv_set( LP_WIN.box_105,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][UNIT_LAYOUT], 		0);
				xv_set( LP_WIN.box_106,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][INTERSECTING_LINE],	 	0);
				xv_set( LP_WIN.box_107,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][LINE_OUT_OF_PROD], 		0);
				xv_set( LP_WIN.box_108,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][DEGENERATED_EDGE], 		0);
				xv_set( LP_WIN.box_109,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][BORDER_GAP_FULLFILLED], 	0);
				xv_set( LP_WIN.box_110,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][NODE_DISTANCE],	 	0);
				xv_set( LP_WIN.box_111,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][EDGE_OVERLAPPING],	 	0);
				xv_set( LP_WIN.box_112,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][NON_TERM_UNIT],	 	0);
				xv_set( LP_WIN.box_113,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][ENCE_1_GRAMMAR],	 	0);
				xv_set( LP_WIN.box_114,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI][SIMPLE_EMBEDDINGS],	 	0);
				break;

		case	TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED:
				xv_set( LP_WIN.box_101,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][BNLC_GRAMMAR], 		0);
				xv_set( LP_WIN.box_102,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][DIFF_EMBEDDINGS], 		0);
				xv_set( LP_WIN.box_103,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][RECTANGULAR_EDGELINES], 	0);
				xv_set( LP_WIN.box_104,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][GRID_LAYOUT], 		0);
				xv_set( LP_WIN.box_105,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][UNIT_LAYOUT], 		0);
				xv_set( LP_WIN.box_106,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][INTERSECTING_LINE], 	0);
				xv_set( LP_WIN.box_107,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][LINE_OUT_OF_PROD],		0);
				xv_set( LP_WIN.box_108,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][DEGENERATED_EDGE],		0);
				xv_set( LP_WIN.box_109,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][BORDER_GAP_FULLFILLED],	0);
				xv_set( LP_WIN.box_110,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][NODE_DISTANCE],	 	0);
				xv_set( LP_WIN.box_111,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][EDGE_OVERLAPPING],	 	0);
				xv_set( LP_WIN.box_112,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][NON_TERM_UNIT],	 	0);
				xv_set( LP_WIN.box_113,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][ENCE_1_GRAMMAR],	 	0);
				xv_set( LP_WIN.box_114,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED][SIMPLE_EMBEDDINGS],	0);
				break;

		case	TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING:
				xv_set( LP_WIN.box_101,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][BNLC_GRAMMAR], 		0);
				xv_set( LP_WIN.box_102,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][DIFF_EMBEDDINGS], 		0);
				xv_set( LP_WIN.box_103,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][RECTANGULAR_EDGELINES], 	0);
				xv_set( LP_WIN.box_104,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][GRID_LAYOUT], 			0);
				xv_set( LP_WIN.box_105,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][UNIT_LAYOUT], 			0);
				xv_set( LP_WIN.box_106,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][INTERSECTING_LINE],	 	0);
				xv_set( LP_WIN.box_107,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][LINE_OUT_OF_PROD], 		0);
				xv_set( LP_WIN.box_108,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][DEGENERATED_EDGE], 		0);
				xv_set( LP_WIN.box_109,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][BORDER_GAP_FULLFILLED], 	0);
				xv_set( LP_WIN.box_110,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][NODE_DISTANCE],	 	0);
				xv_set( LP_WIN.box_111,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][EDGE_OVERLAPPING],	 	0);
				xv_set( LP_WIN.box_112,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][NON_TERM_UNIT],	 	0);
				xv_set( LP_WIN.box_113,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][ENCE_1_GRAMMAR],	 	0);
				xv_set( LP_WIN.box_114,	XV_SHOW,
					Algorithm_preconditions[TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][SIMPLE_EMBEDDINGS],	 	0);
				break;

		case	BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP:
				xv_set( LP_WIN.box_101,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][BNLC_GRAMMAR], 			0);
				xv_set( LP_WIN.box_102,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][DIFF_EMBEDDINGS], 		0);
				xv_set( LP_WIN.box_103,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][RECTANGULAR_EDGELINES], 		0);
				xv_set( LP_WIN.box_104,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][GRID_LAYOUT], 			0);
				xv_set( LP_WIN.box_105,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][UNIT_LAYOUT], 			0);
				xv_set( LP_WIN.box_106,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][INTERSECTING_LINE],	 	0);
				xv_set( LP_WIN.box_107,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][LINE_OUT_OF_PROD], 		0);
				xv_set( LP_WIN.box_108,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][DEGENERATED_EDGE], 		0);
				xv_set( LP_WIN.box_109,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][BORDER_GAP_FULLFILLED], 		0);
				xv_set( LP_WIN.box_110,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][NODE_DISTANCE],		 	0);
				xv_set( LP_WIN.box_111,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][EDGE_OVERLAPPING],	 	0);
				xv_set( LP_WIN.box_112,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][NON_TERM_UNIT],	 		0);
				xv_set( LP_WIN.box_113,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][ENCE_1_GRAMMAR],	 	0);
				xv_set( LP_WIN.box_114,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP][SIMPLE_EMBEDDINGS],	 	0);
				break;

		case	BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING:
				xv_set( LP_WIN.box_101,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][BNLC_GRAMMAR], 		0);
				xv_set( LP_WIN.box_102,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][DIFF_EMBEDDINGS], 		0);
				xv_set( LP_WIN.box_103,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][RECTANGULAR_EDGELINES], 	0);
				xv_set( LP_WIN.box_104,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][GRID_LAYOUT], 		0);
				xv_set( LP_WIN.box_105,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][UNIT_LAYOUT], 		0);
				xv_set( LP_WIN.box_106,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][INTERSECTING_LINE],	 	0);
				xv_set( LP_WIN.box_107,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][LINE_OUT_OF_PROD], 		0);
				xv_set( LP_WIN.box_108,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][DEGENERATED_EDGE], 		0);
				xv_set( LP_WIN.box_109,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][BORDER_GAP_FULLFILLED], 	0);
				xv_set( LP_WIN.box_110,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][NODE_DISTANCE],	 	0);
				xv_set( LP_WIN.box_111,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][EDGE_OVERLAPPING],	 	0);
				xv_set( LP_WIN.box_112,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][NON_TERM_UNIT],	 	0);
				xv_set( LP_WIN.box_113,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][ENCE_1_GRAMMAR],	 	0);
				xv_set( LP_WIN.box_114,	XV_SHOW,
					Algorithm_preconditions[BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING][SIMPLE_EMBEDDINGS],	 	0);
				break;

		case	BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP:
		case	BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B:
		case 	GRAPHED_STANDARD:
		default:	/*** GRAPHED_STANDARD ***/
				xv_set( LP_WIN.box_101,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][BNLC_GRAMMAR], 		0);
				xv_set( LP_WIN.box_102,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][DIFF_EMBEDDINGS], 		0);
				xv_set( LP_WIN.box_103,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][RECTANGULAR_EDGELINES], 	0);
				xv_set( LP_WIN.box_104,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][GRID_LAYOUT], 		0);
				xv_set( LP_WIN.box_105,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][UNIT_LAYOUT], 		0);
				xv_set( LP_WIN.box_106,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][INTERSECTING_LINE],	 	0);
				xv_set( LP_WIN.box_107,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][LINE_OUT_OF_PROD], 		0);
				xv_set( LP_WIN.box_108,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][DEGENERATED_EDGE], 		0);
				xv_set( LP_WIN.box_109,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][BORDER_GAP_FULLFILLED], 	0);
				xv_set( LP_WIN.box_110,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][NODE_DISTANCE],	 	0);
				xv_set( LP_WIN.box_111,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][EDGE_OVERLAPPING],	 	0);
				xv_set( LP_WIN.box_112,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][NON_TERM_UNIT],	 	0);
				xv_set( LP_WIN.box_113,	XV_SHOW, 	Algorithm_preconditions[GRAPHED_STANDARD][ENCE_1_GRAMMAR],	 	0);
				xv_set( LP_WIN.box_114,	XV_SHOW,	Algorithm_preconditions[GRAPHED_STANDARD][SIMPLE_EMBEDDINGS],	 	0);
				break;
	}
}

/****************************************************************************************/
/*											*/
/* Funktionen, die uebergebenen String als aktuellen Algorithmus ausgibt		*/
/* Ausserdem wird sichergestellt, dass die unteren Panel den aktuellen Algorithmus 	*/
/* wiedergeben										*/
/*											*/
/****************************************************************************************/

void	lp_set_cur_algorithm(int nr)
{
	char*	label;

	switch(nr)
	{
		case	TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP:
				xv_set( LP_WIN.choice_302, PANEL_VALUE, 2, 0);
				label = "Top Down Bends without Overlap";
				xv_set( LP_WIN.choice_303, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_304, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_305, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_306, PANEL_VALUE, 0, 0);
				LP_WIN.old_value_301 = 1;
				LP_WIN.old_value_302 = 2;
				LP_WIN.old_value_303 = 0;
				LP_WIN.old_value_304 = 0;
				LP_WIN.old_value_305 = 0;
				LP_WIN.old_value_306 = 0;
				set_grammar_panel_corresponding_to_algorithm( TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP );
				XV_awake( LP_WIN.alg_401 );
				XV_sleep( LP_WIN.alg_402 );
				XV_sleep( LP_WIN.alg_403 );
				XV_sleep( LP_WIN.alg_404 );
				xv_set( LP_WIN.choice_201, XV_SHOW, FALSE, 0);
				xv_set( LP_WIN.choice_202, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_204, XV_SHOW, FALSE, 0);
				xv_set( LP_WIN.text_207, XV_SHOW, TRUE, 0);
				break;

		case 	TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B:
				xv_set( LP_WIN.choice_302, PANEL_VALUE, 1, 0);
				label = "Top Down Bends without Overlap Boundary";
				xv_set( LP_WIN.choice_303, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_304, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_305, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_306, PANEL_VALUE, 0, 0);
				LP_WIN.old_value_301 = 1;
				LP_WIN.old_value_302 = 1;
				LP_WIN.old_value_303 = 0;
				LP_WIN.old_value_304 = 0;
				LP_WIN.old_value_305 = 0;
				LP_WIN.old_value_306 = 0;
				set_grammar_panel_corresponding_to_algorithm( TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B );
				XV_awake( LP_WIN.alg_401 );
				XV_sleep( LP_WIN.alg_402 );
				XV_sleep( LP_WIN.alg_403 );
				XV_sleep( LP_WIN.alg_404 );
				xv_set( LP_WIN.choice_201, XV_SHOW, FALSE, 0);
				xv_set( LP_WIN.choice_202, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_204, XV_SHOW, FALSE, 0);
				xv_set( LP_WIN.text_207, XV_SHOW, TRUE, 0);
				break;

		case	TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP:
				xv_set( LP_WIN.choice_301, PANEL_VALUE, 1, 0);
				xv_set( LP_WIN.choice_303, PANEL_VALUE, 1, 0);
				label = "Top Down Bends with Overlap";
				xv_set( LP_WIN.choice_302, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_304, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_305, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_306, PANEL_VALUE, 0, 0);
				LP_WIN.old_value_301 = 1;
				LP_WIN.old_value_302 = 0;
				LP_WIN.old_value_303 = 1;
				LP_WIN.old_value_304 = 0;
				LP_WIN.old_value_305 = 0;
				LP_WIN.old_value_306 = 0;
				set_grammar_panel_corresponding_to_algorithm( TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP );
				XV_awake( LP_WIN.alg_401 );
				XV_sleep( LP_WIN.alg_402 );
				XV_sleep( LP_WIN.alg_403 );
				XV_sleep( LP_WIN.alg_404 );
				xv_set( LP_WIN.choice_201, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.choice_202, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_204, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_207, XV_SHOW, TRUE, 0);
				break;

		case	TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI:
				xv_set( LP_WIN.choice_303, PANEL_VALUE, 2, 0);
				label = "Top Down Bends with Overlap Multi";
				xv_set( LP_WIN.choice_302, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_304, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_305, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_306, PANEL_VALUE, 0, 0);
				LP_WIN.old_value_301 = 1;
				LP_WIN.old_value_302 = 0;
				LP_WIN.old_value_303 = 2;
				LP_WIN.old_value_304 = 0;
				LP_WIN.old_value_305 = 0;
				LP_WIN.old_value_306 = 0;
				set_grammar_panel_corresponding_to_algorithm( TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI );
				XV_awake( LP_WIN.alg_401 );
				XV_sleep( LP_WIN.alg_402 );
				XV_sleep( LP_WIN.alg_403 );
				XV_sleep( LP_WIN.alg_404 );
				xv_set( LP_WIN.choice_201, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.choice_202, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_204, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_207, XV_SHOW, TRUE, 0);
				break;

		case	TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED:
				xv_set( LP_WIN.choice_303, PANEL_VALUE, 4, 0);
				label = "Top Down Bends with Overlap Multi Oriented";
				xv_set( LP_WIN.choice_302, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_304, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_305, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_306, PANEL_VALUE, 0, 0);
				LP_WIN.old_value_301 = 1;
				LP_WIN.old_value_302 = 0;
				LP_WIN.old_value_303 = 4;
				LP_WIN.old_value_304 = 0;
				LP_WIN.old_value_305 = 0;
				LP_WIN.old_value_306 = 0;
				set_grammar_panel_corresponding_to_algorithm( TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED );
				XV_awake( LP_WIN.alg_401 );
				XV_sleep( LP_WIN.alg_402 );
				XV_sleep( LP_WIN.alg_403 );
				XV_sleep( LP_WIN.alg_404 );
				xv_set( LP_WIN.choice_201, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.choice_202, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_204, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_207, XV_SHOW, TRUE, 0);
				break;

		case	TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING:
				xv_set( LP_WIN.choice_304, PANEL_VALUE, 1, 0);
				label = "Top Down Area Standard";
				xv_set( LP_WIN.choice_302, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_303, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_305, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_306, PANEL_VALUE, 0, 0);
				LP_WIN.old_value_301 = 1;
				LP_WIN.old_value_302 = 0;
				LP_WIN.old_value_303 = 0;
				LP_WIN.old_value_304 = 1;
				LP_WIN.old_value_305 = 0;
				LP_WIN.old_value_306 = 0;
				set_grammar_panel_corresponding_to_algorithm( TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING );
				XV_awake( LP_WIN.alg_401 );
				XV_awake( LP_WIN.alg_402 );
				XV_awake( LP_WIN.alg_403 );
				XV_awake( LP_WIN.alg_404 );
				xv_set( LP_WIN.choice_201, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.choice_202, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_204, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_207, XV_SHOW, TRUE, 0);
				break;

		case	BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP:
				xv_set( LP_WIN.choice_305, PANEL_VALUE, 1, 0);
				label = "Bottom Up Bends with Overlap Standard";
				xv_set( LP_WIN.choice_302, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_303, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_304, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_306, PANEL_VALUE, 0, 0);
				LP_WIN.old_value_301 = 1;
				LP_WIN.old_value_302 = 0;
				LP_WIN.old_value_303 = 0;
				LP_WIN.old_value_304 = 0;
				LP_WIN.old_value_305 = 1;
				LP_WIN.old_value_306 = 0;
				set_grammar_panel_corresponding_to_algorithm( BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP );
				XV_awake( LP_WIN.alg_401 );
				XV_sleep( LP_WIN.alg_402 );
				XV_sleep( LP_WIN.alg_403 );
				XV_sleep( LP_WIN.alg_404 );
				xv_set( LP_WIN.choice_201, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.choice_202, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_204, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_207, XV_SHOW, TRUE, 0);
				break;

		case	BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING:
				xv_set( LP_WIN.choice_306, PANEL_VALUE, 1, 0);
				label = "Bottom Up Area Standard";
				xv_set( LP_WIN.choice_302, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_303, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_304, PANEL_VALUE, 0, 0);
				xv_set( LP_WIN.choice_305, PANEL_VALUE, 0, 0);
				LP_WIN.old_value_301 = 1;
				LP_WIN.old_value_302 = 0;
				LP_WIN.old_value_303 = 0;
				LP_WIN.old_value_304 = 0;
				LP_WIN.old_value_305 = 0;
				LP_WIN.old_value_306 = 6;
				set_grammar_panel_corresponding_to_algorithm( BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING );
				XV_awake( LP_WIN.alg_401 );
				XV_sleep( LP_WIN.alg_402 );
				XV_sleep( LP_WIN.alg_403 );
				XV_sleep( LP_WIN.alg_404 );
				xv_set( LP_WIN.choice_201, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.choice_202, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_204, XV_SHOW, TRUE, 0);
				xv_set( LP_WIN.text_207, XV_SHOW, TRUE, 0);
				break;

		case	BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP:
		case	BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B:
		case 	GRAPHED_STANDARD:
		default:
				hide_optimization_panels();
				xv_set( LP_WIN.choice_301, PANEL_VALUE, 2, 0);
				label = "Graphed Standard";
				LP_WIN.old_value_301 = 1;
				LP_WIN.old_value_302 = 0;
				LP_WIN.old_value_303 = 0;
				LP_WIN.old_value_304 = 0;
				LP_WIN.old_value_305 = 0;
				LP_WIN.old_value_306 = 0;
				set_grammar_panel_corresponding_to_algorithm( GRAPHED_STANDARD );
				XV_sleep( LP_WIN.alg_401 );
				XV_sleep( LP_WIN.alg_402 );
				XV_sleep( LP_WIN.alg_403 );
				XV_sleep( LP_WIN.alg_404 );
				xv_set( LP_WIN.choice_201, XV_SHOW, FALSE, 0);
				xv_set( LP_WIN.choice_202, XV_SHOW, FALSE, 0);
				xv_set( LP_WIN.text_204, XV_SHOW, FALSE, 0);
				xv_set( LP_WIN.text_207, XV_SHOW, FALSE, 0);
				break;
	}

	LP_WIN.cur_algorithm	= nr;
	LP_WIN.algorithm_label	= label;

	xv_set( LP_WIN.text_203, PANEL_LABEL_STRING,	LP_WIN.algorithm_label, 0 );

}

/****************************************************************************************/
/*											*/
/* Zwischengeschaltete Funktionen, die vom Subframe aus aufgerufen werden		*/
/*											*/
/****************************************************************************************/

void	lp_choice_301(Panel_item item, int value, Event *event)
{

	/*** Falls altes item nochmal gedrueckt setze alten Wert wieder ***/
	if( value == 0 ){ value = LP_WIN.old_value_301; }
	else		{ /*** Jetzt wirds schwierig. Es sind zwei items ausgewaehlt. Suche also den richtigen ***/
			  if( value > 2 ){ value = value - LP_WIN.old_value_301; }
			}
		
	switch( value )
	{
		case 1:	lp_set_cur_algorithm( TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP );
			graph_state.lp_graph_state.LGG_Algorithm = TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP;
			unhide_optimization_panels();
			LP_WIN.old_value_301 = 1;
			break;
		case 2:	lp_set_cur_algorithm( GRAPHED_STANDARD );
			graph_state.lp_graph_state.LGG_Algorithm = GRAPHED_STANDARD;
			LP_WIN.old_value_301 = 2;
			break;
	}
}

void	lp_choice_302(Panel_item item, int value, Event *event)
{
	/*** Falls altes item nochmal gedrueckt setze alten Wert wieder ***/
	if( value == 0 ){ value = LP_WIN.old_value_302; }
	else		{ /*** Jetzt wirds schwierig. Es sind zwei items ausgewaehlt. Suche also den richtigen ***/
			  if( value > 2 ){ value = value - LP_WIN.old_value_302; }
			}

	switch( value )
	{
		case 1:	lp_set_cur_algorithm( TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B );
			graph_state.lp_graph_state.LGG_Algorithm = TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B;
			break;
		case 2:	lp_set_cur_algorithm( TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP );
			graph_state.lp_graph_state.LGG_Algorithm = TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP;
			break;
	}
}

void	lp_choice_303(Panel_item item, int value, Event *event)
{
	/*** Falls altes item nochmal gedrueckt setze alten Wert wieder ***/
	if( value == 0 ){ value = LP_WIN.old_value_303; }
	else		{ /*** Jetzt wirds schwierig. Es sind zwei items ausgewaehlt. Suche also den richtigen ***/
			  if( value > 2 ){ value = value - LP_WIN.old_value_303; }
			}

	switch( value )
	{
		case 1:	lp_set_cur_algorithm( TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP );
			graph_state.lp_graph_state.LGG_Algorithm = TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP;
			break;
		case 2:	lp_set_cur_algorithm( TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI );
			graph_state.lp_graph_state.LGG_Algorithm = TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI;
			break;
		case 4:	lp_set_cur_algorithm( TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED );
			graph_state.lp_graph_state.LGG_Algorithm = TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED;
			break;
	}
}

void	lp_choice_304(Panel_item item, int value, Event *event)
{
	if( value == 0 )
	{
		value = 1;
	}

	switch( value )
	{
		case 1:	lp_set_cur_algorithm( TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING );
			graph_state.lp_graph_state.LGG_Algorithm = TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING;
			break;
	}
}

void	lp_choice_305(Panel_item item, int value, Event *event)
{
	if( value == 0 )
	{
		value = 1;
	}

	switch( value )
	{
		case 1:	lp_set_cur_algorithm( BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP );
			graph_state.lp_graph_state.LGG_Algorithm = BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP;
			break;
	}
}

void	lp_choice_306(Panel_item item, int value, Event *event)
{
	if( value == 0 )
	{
		value = 1;
	}

	switch( value )
	{
		case 1:	lp_set_cur_algorithm( BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING );
			graph_state.lp_graph_state.LGG_Algorithm = BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING;
			break;
	}
}

/****************************************************************************************/
/*											*/
/* Die Funktion, die eintraegt, was mit abgeleiteten Knoten passiert			*/
/*											*/
/****************************************************************************************/

void	lp_derivated_node(Panel_item item, int value, Event *event)
{
	if( value == 0 )
	{
		LP_WIN.what_to_do_with_derivated_node = TRUE;
	}
	else
	{
		LP_WIN.what_to_do_with_derivated_node = FALSE;
	}

}

/****************************************************************************************/
/*											*/
/* Die Funktion, die eintraegt, ob Knoten minimiert werden				*/
/*											*/
/****************************************************************************************/

void	lp_minimize_nodes(Panel_item item, int value, Event *event)
{
	if( value == 0 )
	{
		LP_WIN.min_nodes = TRUE;
	}
	else
	{
		LP_WIN.min_nodes = FALSE;
	}
}


/****************************************************************************************/
/*											*/
/* Die Funktion, die ueberprueft, welche Produktion die geforderte Eigenschaft nicht 	*/
/* erfuellt.( Immer nur die erste )							*/
/*											*/
/****************************************************************************************/

void	lp_test_productions_on(int attribute)
{
	int	buffer, no_error;
	Graph	graph;

	for (buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++) 
	{
		for_all_graphs (buffer, graph)
		{
			if( graph->is_production )
			{
				/****** Now we can test ******/
				switch( attribute )
				{
					case	(int)BNLC_GRAMMAR:
						no_error = additional_bnlc_test( graph );
						if( !no_error ) goto lp_out;
						break;
					case	(int)DIFF_EMBEDDINGS:
						no_error = all_embeddings_are_different( graph );
						if( !no_error ) goto lp_out;
						break;
					case	(int)RECTANGULAR_EDGELINES:
						no_error = rectangular_edgelines( graph );
						if( !no_error ) goto lp_out;
						break;
					case	(int)GRID_LAYOUT:
						no_error = grid_layout_production( graph, 16 );
						if( !no_error ) goto lp_out;
						break;
					case	(int)UNIT_LAYOUT:
						no_error = unit_layout_production( graph, 32 );
						if( !no_error ) goto lp_out;
						break;
					case	(int)INTERSECTING_LINE:
						no_error = schnitt_edge_node( graph );
						if( !no_error ) goto lp_out;
						break;
					case	(int)LINE_OUT_OF_PROD:
						no_error = edgelines_in_production( graph );
						if( !no_error ) goto lp_out;
						break;
					case	(int)DEGENERATED_EDGE:
						no_error = degenerated_edges( graph );
						if( no_error ) goto lp_out;
						break;
					case	(int)BORDER_GAP_FULLFILLED:
						no_error = lp_test_on_frame( graph );
						if( !no_error ) goto lp_out;
						break;
					case	(int)NODE_DISTANCE:
						no_error = lp_test_nodes_non_touching( graph );
						if( !no_error ) goto lp_out;
						break;
					case	(int)EDGE_OVERLAPPING:
						no_error = lp_test_non_overlapping_edges( graph );
						if( !no_error ) goto lp_out;
						break;
					case	(int)NON_TERM_UNIT:
						no_error = non_term_unit( graph, 32 );
						if( !no_error ) goto lp_out;
						break;
					case	(int)ENCE_1_GRAMMAR:
						no_error = is_ENCE_1_GRAMMAR( graph );
						if( !no_error ) goto lp_out;
						break;
					case	(int)SIMPLE_EMBEDDINGS:
						no_error = !number_of_in_embedding_edges_is_incorrect( graph );
						if( !no_error ) goto lp_out;
						break;
				}
			}
		}
		end_for_all_graphs (buffer, graph);
	}
lp_out:		dispatch_user_action (UNSELECT);
		dispatch_user_action (SELECT, compile_production_error_list);
		bell();

}

/****************************************************************************************/
/*											*/
/* Die Funktionen, die dafuer sorgen, dass an Grammatikeigenschaften nix geaendert wird	*/
/*											*/
/****************************************************************************************/

void	lp_reset_grammar_101(void)
{
	/*** This is how we have to react to find out, which production works unpropper (<- was immer das heisst) ***/
	if(  !grammar_preconditions[BNLC_GRAMMAR] )
	{
		lp_test_productions_on( BNLC_GRAMMAR );
	}
	xv_set( LP_WIN.box_101, PANEL_VALUE, grammar_preconditions[BNLC_GRAMMAR], 0);
}

void	lp_reset_grammar_102(void)
{
	/*** This is how we have to react to find out, which production works unpropper (<- was immer das heisst) ***/
	if( !grammar_preconditions[DIFF_EMBEDDINGS] )
	{
		lp_test_productions_on( DIFF_EMBEDDINGS );
	}
	xv_set( LP_WIN.box_102, PANEL_VALUE, grammar_preconditions[DIFF_EMBEDDINGS], 0);
}

void	lp_reset_grammar_103(void)
{
	/*** This is how we have to react to find out, which production works unpropper (<- was immer das heisst) ***/
	if( !grammar_preconditions[RECTANGULAR_EDGELINES] )
	{
		lp_test_productions_on( RECTANGULAR_EDGELINES );
	}
	xv_set( LP_WIN.box_103, PANEL_VALUE, grammar_preconditions[RECTANGULAR_EDGELINES], 0);
}

void	lp_reset_grammar_104(void)
{
	/*** This is how we have to react to find out, which production works unpropper (<- was immer das heisst) ***/
	if( !grammar_preconditions[GRID_LAYOUT] )
	{
		lp_test_productions_on( GRID_LAYOUT );
	}
	xv_set( LP_WIN.box_104, PANEL_VALUE, grammar_preconditions[GRID_LAYOUT], 0);
}

void	lp_reset_grammar_105(void)
{
	/*** This is how we have to react to find out, which production works unpropper (<- was immer das heisst) ***/
	if( !grammar_preconditions[UNIT_LAYOUT] )
	{
		lp_test_productions_on( UNIT_LAYOUT );
	}
	xv_set( LP_WIN.box_105, PANEL_VALUE, grammar_preconditions[UNIT_LAYOUT], 0);
}

void	lp_reset_grammar_106(void)
{
	/*** This is how we have to react to find out, which production works unpropper (<- was immer das heisst) ***/
	if( !grammar_preconditions[INTERSECTING_LINE] )
	{
		lp_test_productions_on( INTERSECTING_LINE );
	}
	xv_set( LP_WIN.box_106, PANEL_VALUE, grammar_preconditions[INTERSECTING_LINE], 0);
}

void	lp_reset_grammar_107(void)
{
	/*** This is how we have to react to find out, which production works unpropper (<- was immer das heisst) ***/
	if( !grammar_preconditions[LINE_OUT_OF_PROD] )
	{
		lp_test_productions_on( LINE_OUT_OF_PROD );
	}
	xv_set( LP_WIN.box_107, PANEL_VALUE, grammar_preconditions[LINE_OUT_OF_PROD], 0);
}

void	lp_reset_grammar_108(void)
{
	/*** This is how we have to react to find out, which production works unpropper (<- was immer das heisst) ***/
	if( !grammar_preconditions[DEGENERATED_EDGE] )
	{
		lp_test_productions_on( DEGENERATED_EDGE );
	}
	xv_set( LP_WIN.box_108, PANEL_VALUE, grammar_preconditions[DEGENERATED_EDGE], 0);
}

void	lp_reset_grammar_109(void)
{
	/*** This is how we have to react to find out, which production works (<- was immer das heisst) ***/
	if( !grammar_preconditions[BORDER_GAP_FULLFILLED] )
	{
		lp_test_productions_on( BORDER_GAP_FULLFILLED );
	}
	xv_set( LP_WIN.box_109, PANEL_VALUE, grammar_preconditions[BORDER_GAP_FULLFILLED], 0);
}

void	lp_reset_grammar_110(void)
{
	/*** This is how we have to react to find out, which production works (<- was immer das heisst) ***/
	if( !grammar_preconditions[NODE_DISTANCE] )
	{
		lp_test_productions_on( NODE_DISTANCE );
	}
	xv_set( LP_WIN.box_110, PANEL_VALUE, grammar_preconditions[NODE_DISTANCE], 0);
}

void	lp_reset_grammar_111(void)
{
	/*** This is how we have to react to find out, which production works (<- was immer das heisst) ***/
	if( !grammar_preconditions[EDGE_OVERLAPPING] )
	{
		lp_test_productions_on( EDGE_OVERLAPPING );
	}
	xv_set( LP_WIN.box_111, PANEL_VALUE, grammar_preconditions[EDGE_OVERLAPPING], 0);
}

void	lp_reset_grammar_112(void)
{
	/*** This is how we have to react to find out, which production works (<- was immer das heisst) ***/
	if( !grammar_preconditions[NON_TERM_UNIT] )
	{
		lp_test_productions_on( NON_TERM_UNIT );
	}
	xv_set( LP_WIN.box_112, PANEL_VALUE, grammar_preconditions[NON_TERM_UNIT], 0);
}

void	lp_reset_grammar_113(void)
{
	/*** This is how we have to react to find out, which production works (<- was immer das heisst) ***/
	if( !grammar_preconditions[ENCE_1_GRAMMAR] )
	{
		lp_test_productions_on( ENCE_1_GRAMMAR );
	}
	xv_set( LP_WIN.box_113, PANEL_VALUE, grammar_preconditions[ENCE_1_GRAMMAR], 0);
}

void	lp_reset_grammar_114(void)
{
	/*** This is how we have to react to find out, which production works (<- was immer das heisst) ***/
	if( !grammar_preconditions[SIMPLE_EMBEDDINGS] )
	{
		lp_test_productions_on( SIMPLE_EMBEDDINGS );
	}
	xv_set( LP_WIN.box_114, PANEL_VALUE, grammar_preconditions[SIMPLE_EMBEDDINGS], 0);
}

/****************************************************************************************/
/*											*/
/* Die Funktionen, die nach compile_production die Panels neu zuruecksetzen		*/
/*											*/
/****************************************************************************************/


void	lp_set_grammar_101(void)
{
	xv_set( LP_WIN.box_101, PANEL_VALUE, grammar_preconditions[BNLC_GRAMMAR], 0);
}

void	lp_set_grammar_102(void)
{
	xv_set( LP_WIN.box_102, PANEL_VALUE, grammar_preconditions[DIFF_EMBEDDINGS], 0);
}

void	lp_set_grammar_103(void)
{
	xv_set( LP_WIN.box_103, PANEL_VALUE, grammar_preconditions[RECTANGULAR_EDGELINES], 0);
}

void	lp_set_grammar_104(void)
{
	xv_set( LP_WIN.box_104, PANEL_VALUE, grammar_preconditions[GRID_LAYOUT], 0);
}

void	lp_set_grammar_105(void)
{
	xv_set( LP_WIN.box_105, PANEL_VALUE, grammar_preconditions[UNIT_LAYOUT], 0);
}

void	lp_set_grammar_106(void)
{
	xv_set( LP_WIN.box_106, PANEL_VALUE, grammar_preconditions[INTERSECTING_LINE], 0);
}

void	lp_set_grammar_107(void)
{
	xv_set( LP_WIN.box_107, PANEL_VALUE, grammar_preconditions[LINE_OUT_OF_PROD], 0);
}

void	lp_set_grammar_108(void)
{
	xv_set( LP_WIN.box_108, PANEL_VALUE, grammar_preconditions[DEGENERATED_EDGE], 0);
}

void	lp_set_grammar_109(void)
{
	xv_set( LP_WIN.box_109, PANEL_VALUE, grammar_preconditions[BORDER_GAP_FULLFILLED], 0);
}

void	lp_set_grammar_110(void)
{
	xv_set( LP_WIN.box_110, PANEL_VALUE, grammar_preconditions[NODE_DISTANCE], 0);
}

void	lp_set_grammar_111(void)
{
	xv_set( LP_WIN.box_111, PANEL_VALUE, grammar_preconditions[EDGE_OVERLAPPING], 0);
}

void	lp_set_grammar_112(void)
{
	xv_set( LP_WIN.box_112, PANEL_VALUE, grammar_preconditions[NON_TERM_UNIT], 0);
}

void	lp_set_grammar_113(void)
{
	xv_set( LP_WIN.box_113, PANEL_VALUE, grammar_preconditions[ENCE_1_GRAMMAR], 0);
}

void	lp_set_grammar_114(void)
{
	xv_set( LP_WIN.box_114, PANEL_VALUE, grammar_preconditions[SIMPLE_EMBEDDINGS], 0);
}

void	lp_reset_all_grammar_panels(void)
{
	int	buffer, index;
	Graph	graph;

	/*** First we have to update our Grammar Attributes ***/


	for ( index = 0; index < PRECONDITION_COUNTER; index++)	/*** Set all attributes to TRUE ***/
	{
		grammar_preconditions[index] = 1;
	}


	for (buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++) 
	{
		for_all_graphs (buffer, graph)
		{
			if( graph->is_production )
			{
				for ( index = 0; index < PRECONDITION_COUNTER; index++)	/*** Set all attributes to the real value ***/
				{
					grammar_preconditions[index] = grammar_preconditions[index] && graph->lp_graph.properties_array[index];
				}
			}
		}
		end_for_all_graphs (buffer, graph);
	}

	if( LP_WIN.frame_is_created )
	{
		lp_set_grammar_101();
		lp_set_grammar_102();
		lp_set_grammar_103();
		lp_set_grammar_104();
		lp_set_grammar_105();
		lp_set_grammar_106();
		lp_set_grammar_107();
		lp_set_grammar_108();
		lp_set_grammar_109();
		lp_set_grammar_110();
		lp_set_grammar_111();
		lp_set_grammar_112();
		lp_set_grammar_113();
		lp_set_grammar_114();
	}
}

/****************************************************************************************/
/*											*/
/* Die Funktion, die einen ausgewaehlten Algorithmus ohne weitere Ableitung ausfuehrt	*/
/*											*/
/****************************************************************************************/

void 	lp_execute_algorithm(void)
{
	int             value, result;
	Graph           graph;
	Group		group_of_graph;

	value = graph_state.lp_graph_state.LGG_Algorithm;
	graph = compute_graph();

	if (graph)
	{
		if (graph->lp_graph.dependency_visible &&
		    ((value == GRAPHED_STANDARD) || (value == TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B)))
		{
			remove_dependency(graph->lp_graph.derivation_net);
		} 
		else
		{
			if (LP_WIN.what_to_do_with_derivated_node == SHOW &&
			    !((value == GRAPHED_STANDARD) || (value == TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B)))
			{
				graph->lp_graph.dependency_visible = TRUE;
			}
		}
		if ( test_grammar_changed( graph->lp_graph.creation_time ) )
		{
			/* graph->lp_graph.hierarchical_graph = FALSE; */
		}
		else
		{	    	

		if( graph->lp_graph.changed 						&&
		  	 (value != BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP) 	&&
		 	 (value != BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B) 	&&
		  	 (value != BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP) 		&&
		  	 (value != BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING) 	)
		{
			result = notice_prompt(base_frame, NULL,	/* fisprompt */
				       NOTICE_MESSAGE_STRINGS, "WARNING!\nGraph has been changed.\nCompute anyway?", NULL,
				       NOTICE_BUTTON_YES, "Yes",
				       NOTICE_BUTTON_NO, "No",
				       NULL);

			if (!(result == NOTICE_YES))
			{
				goto aus;
			}
		}

		if( !graph->lp_graph.hierarchical_graph 				&&
		    (value != BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP) 	&&
	            (value != BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B) 	&&
		    (value != BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP) 		&&
		    (value != BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING) 	)
		{
			MsgBox("This is no hierarchical graph. The only possible algorithm \nwould be graphed standard which you can't call from here.",
			     	 CMD_OK);
		}
		else
		{
			if (graph->lp_graph.reduced &&
			  ((value != TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING) &&
			   (value != BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP) &&
			   (value != BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B) &&
			   (value != BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP) &&
			   (value != BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING)))
			{
				MsgBox("This is a reduced graph. The only possible algorithms are: \ntop down area optimization or \na bottom up algorithm.",
			     	  CMD_OK);
			}
			else
			{

				switch (value)
				{
				case GRAPHED_STANDARD:
					MsgBox("Actually not possible for graphed standard", CMD_OK);
					break;

				case TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B:
					if (!grammar_fits_algorithm_conditions(TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B))
					{
						MsgBox("Can't execute Algorithm, because Grammar has not \n the conditions for this algorithm", CMD_OK);
					}
					else
					{
						if (graph->lp_graph.LRS_graph)
						{
							top_down_cost_optimization(graph->lp_graph.LRS_graph, NULL);
							/*** ACHTUNG: Funktioniert nur weil momentan 2. Parameter nicht benutzt wird. Wenn sich das ***/
							/*** Aendert, dann sollte man sich was ueberlegen ***/
							group_set (group_of_graph = make_group_of_graph (graph), RESTORE_IT, 0);
							free_group (group_of_graph);
							graph->lp_graph.changed = FALSE;
						}
						else 
						{
							MsgBox("Impossible. This is no hierarchical graph.", CMD_OK);
						}
					}
					break;

				case TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP:
					/****** first test grammar attributes ******/
					if (!grammar_fits_algorithm_conditions(TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP))
					{
						MsgBox("Can't execute Algorithm, because Grammar has not \n the attributes for this algorithm", CMD_OK);
					}
					else
					{
						if (!graph->lp_graph.derivation_net)
						{
							MsgBox("This function needs a graph \nwith derivation_net", CMD_OK);
						}
						else
						{
							compute_attributes(graph);
							/* restore_graph(graph); */
							graph->lp_graph.changed = FALSE;
						}
					}
					break;

				case TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI:
					if (!grammar_fits_algorithm_conditions(TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI)) {
						MsgBox("Can't execute Algorithm, because Grammar has not \n the attributes for this algorithm", CMD_OK);
					} else {
						if (!graph->lp_graph.derivation_net) {
							MsgBox("This function needs a graph \nwith derivation_net", CMD_OK);
						} else {
							create_multi_lgg_layout(graph);
							graph->lp_graph.changed = FALSE;
						}
					}
					break;

				case TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED:
					if (!grammar_fits_algorithm_conditions(TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED)) {
						MsgBox("Can't execute Algorithm, because Grammar has not \n the attributes for this algorithm", CMD_OK);
					} else {
						if (!graph->lp_graph.derivation_net) {
							MsgBox("This function needs a graph \nwith derivation_net", CMD_OK);
						} else {
							create_oriented_multi_lgg_layout(graph);
							graph->lp_graph.changed = FALSE;
						}
					}
					break;

				case TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING:
					if (!grammar_fits_algorithm_conditions(TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING))
					{
						MsgBox("Can't execute Algorithm, because Grammar has not \n the attributes for this algorithm", CMD_OK);
					}
					else 
					{
						if (!graph->lp_graph.derivation_net)
						{
							MsgBox("This function needs a graph \nwith derivation_net", CMD_OK);
						} 
						else
						{
							lp_create_optimal_graph_layout_for_derivation_tree_with_given_graph(graph);
							/* restore_graph(graph); */
							graph->lp_graph.changed = FALSE;
						}
					}
					break;

				case BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP:
					MsgBox("Actually not possible for graphed standard", CMD_OK);
					break;

				case BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B:
					MsgBox("Actually not possible for graphed standard", CMD_OK);
					break;

				case BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP:
					if (!grammar_fits_algorithm_conditions(BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP))
					{
						MsgBox("Can't execute Algorithm, because Grammar has not \n the attributes for this algorithm", CMD_OK);
					} 
					else
					{
						derivation_by_parsing();
					}
					break;

				case BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING:
					create_parser_with_layout_extensions((Menu)0, (Menu_item)0);
 					break;
				}

			}
		}
		}
aus:		dispatch_user_action(UNSELECT);
	}
}

/****************************************************************************************/
/*											*/
/* Die Funktion, die LP_WIN zuruecksetzt, falls frame verschwindet			*/
/*											*/
/****************************************************************************************/

void	lp_done_proc(void)
{
	LP_WIN.frame_is_created = FALSE;
	xv_destroy( LP_WIN.mainframe );
}
	
	
/****************************************************************************************/
/*											*/
/* Die Funktion, die den Subframe am Bildschirm ausgibt					*/
/*											*/
/****************************************************************************************/
static	unsigned short	lgg_icon[] = {
#			include "lp_lgg2.icon"
		};


void	lp_create_baseframe(Menu menu, Menu_item menu_item)
{ 
	Server_image	image1;
	Icon		icon;


	if( !LP_WIN.frame_is_created ) 
	{
		LP_WIN.frame_is_created = TRUE;

		LP_WIN.old_value_301 = 1;
		LP_WIN.old_value_302 = 0;
		LP_WIN.old_value_303 = 1;
		LP_WIN.old_value_304 = 0;
		LP_WIN.old_value_305 = 0;
		LP_WIN.old_value_306 = 0;



		image1 = (Server_image) xv_create(	XV_NULL,			SERVER_IMAGE,
							XV_WIDTH,		64,
							XV_HEIGHT,		64,
							SERVER_IMAGE_BITS,	lgg_icon,
							0 );

		icon = (Icon) xv_create(		base_frame,		ICON,
							ICON_IMAGE,		image1,
							ICON_TRANSPARENT,	TRUE,
							0 );

		LP_WIN.mainframe = (Frame) xv_create(	
						base_frame,			
						FRAME,

						FRAME_LABEL,			"Layout Algorithms for bend and area optimization.      V 0.2b",
						FRAME_SHOW_LABEL,		TRUE,
						XV_SHOW,			FALSE,
						FRAME_NO_CONFIRM,		TRUE,
						FRAME_SHOW_RESIZE_CORNER, 	FALSE,
						FRAME_ICON, 			icon,
						FRAME_DONE_PROC,		lp_done_proc,
						0);

/***********************************************************************************************************************************/
		LP_WIN.grammar_attributes = (Panel) xv_create(
						LP_WIN.mainframe,
						PANEL,

						XV_X,			0,
						XV_WIDTH,		(int)FRAME_WIDTH,
						WIN_ROW_GAP,		4,
						0);

		LP_WIN.text_101	 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	TRUE,
						XV_X,			xv_col(LP_WIN.grammar_attributes, 1),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 0),
						PANEL_LABEL_STRING,	"Condition to run the current algorithm:",
						0);

		LP_WIN.box_101 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"Boundary",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 0),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 1),
						PANEL_VALUE,		grammar_preconditions[BNLC_GRAMMAR],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_101,
						0);

		LP_WIN.box_104 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"Grid Layout",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 0),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 2),
						PANEL_VALUE,		grammar_preconditions[GRID_LAYOUT],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_104,
						0);

		LP_WIN.box_105 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"Unit Layout",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 0),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 3),
						PANEL_VALUE,		grammar_preconditions[UNIT_LAYOUT],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_105,
						0);

		LP_WIN.box_102 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"Unique Embeddings",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 17),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 1),
						PANEL_VALUE,		grammar_preconditions[DIFF_EMBEDDINGS],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_102,
						0);

		LP_WIN.box_103 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"Rectangular Edges",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 40),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 1),
						PANEL_VALUE,		grammar_preconditions[RECTANGULAR_EDGELINES],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_103,
						0);

		LP_WIN.box_106 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"No Crossing Edges",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 17),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 3),
						PANEL_VALUE,		grammar_preconditions[INTERSECTING_LINE],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_106,
						0);

		LP_WIN.box_107 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"Edges Inside Prod",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 17),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 2),
						PANEL_VALUE,		grammar_preconditions[LINE_OUT_OF_PROD],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_107,
						0);

		LP_WIN.box_108 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"No Degenerated Edges",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 40),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 2),
						PANEL_VALUE,		grammar_preconditions[DEGENERATED_EDGE],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_108,
						0);

		LP_WIN.box_109 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"Gap to Frontier",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 40),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 3),
						PANEL_VALUE,		grammar_preconditions[BORDER_GAP_FULLFILLED],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_109,
						0);

		LP_WIN.box_110 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"No Node Intersection",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 17),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 4),
						PANEL_VALUE,		grammar_preconditions[NODE_DISTANCE],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_110,
						0);

		LP_WIN.box_111 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"No Edge Overlap",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 40),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 4),
						PANEL_VALUE,		grammar_preconditions[EDGE_OVERLAPPING],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_111,
						0);

		LP_WIN.box_112 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"Unit Nonterm",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 0),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 4),
						PANEL_VALUE,		grammar_preconditions[NON_TERM_UNIT],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_112,
						0);


		LP_WIN.box_113 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"ENCE_1",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 0),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 5),
						PANEL_VALUE,		grammar_preconditions[ENCE_1_GRAMMAR],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_113,
						0);

		LP_WIN.box_114 = xv_create(
						LP_WIN.grammar_attributes,
						PANEL_CHECK_BOX,

						PANEL_LABEL_STRING,	"Simple Embeddings",
						XV_X,			xv_col(LP_WIN.grammar_attributes, 17),
						XV_Y,			xv_row(LP_WIN.grammar_attributes, 5),
						PANEL_VALUE,		grammar_preconditions[SIMPLE_EMBEDDINGS],
						PANEL_NOTIFY_PROC,	lp_reset_grammar_114,
						0);

		window_fit_height	( LP_WIN.grammar_attributes 	);



/***********************************************************************************************************************************/
		LP_WIN.cur_settings = (Panel) xv_create(
						LP_WIN.mainframe,
						PANEL,

						WIN_BELOW,		LP_WIN.grammar_attributes,
						XV_X,			0,
						XV_WIDTH,		FRAME_WIDTH,
						WIN_ROW_GAP,		4,
						0);

		LP_WIN.text_201	 = xv_create(
						LP_WIN.cur_settings,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	TRUE,
						XV_X,			xv_col(LP_WIN.cur_settings, 1),
						XV_Y,			xv_row(LP_WIN.cur_settings, 0),
						PANEL_LABEL_STRING,	"Current Selections: ",
						0);

		LP_WIN.text_202	 = xv_create(
						LP_WIN.cur_settings,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	FALSE,
						XV_X,			xv_col(LP_WIN.cur_settings, 1),
						XV_Y,			xv_row(LP_WIN.cur_settings, 1),
						PANEL_LABEL_STRING,	"Current Algorithm:",
						0);

		LP_WIN.text_203	 = xv_create(
						LP_WIN.cur_settings,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	FALSE,
						XV_X,			xv_col(LP_WIN.cur_settings, 20),
						XV_Y,			xv_row(LP_WIN.cur_settings, 1),
						PANEL_LABEL_STRING,	LP_WIN.algorithm_label,
						0);

		LP_WIN.text_204	 = xv_create(
						LP_WIN.cur_settings,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	FALSE,
						XV_X,			xv_col(LP_WIN.cur_settings, 1),
						XV_Y,			xv_row(LP_WIN.cur_settings, 2),
						PANEL_LABEL_STRING,	"Show Hierarchy:",
						0);

		LP_WIN.choice_201 = xv_create(
						LP_WIN.cur_settings,
						PANEL_CHOICE,

						XV_X,			xv_col(LP_WIN.cur_settings, 20),
						XV_Y,			xv_row(LP_WIN.cur_settings, 2),
						PANEL_LABEL_STRING,	"",
						PANEL_CHOICE_STRINGS,	"      Yes      ", "       No      ", NULL,
						PANEL_NOTIFY_PROC,	lp_derivated_node,
						PANEL_VALUE,		1,
						0);

		LP_WIN.text_207 = xv_create(
						LP_WIN.cur_settings,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	FALSE,
						XV_X,			xv_col(LP_WIN.cur_settings, 1),
						XV_Y,			xv_row(LP_WIN.cur_settings, 3),
						PANEL_LABEL_STRING,	"Minimize Node Sizes",
						0);

		LP_WIN.choice_202 = xv_create(
						LP_WIN.cur_settings,
						PANEL_CHOICE,

						XV_X,			xv_col(LP_WIN.cur_settings, 20),
						XV_Y,			xv_row(LP_WIN.cur_settings, 3),
						PANEL_LABEL_STRING,	"",
						PANEL_CHOICE_STRINGS,	"      Yes      ", "       No      ", NULL,
						PANEL_NOTIFY_PROC,	lp_minimize_nodes,
						PANEL_VALUE,		1,
						0);




		window_fit_height	( LP_WIN.cur_settings 		);
		XV_win_position_down	( LP_WIN.cur_settings, 1 	);


/***********************************************************************************************************************************/
		LP_WIN.algorithm_choice = (Panel) xv_create(
						LP_WIN.mainframe,
						PANEL,

						WIN_BELOW,		LP_WIN.cur_settings,
						XV_X,			0,
						XV_WIDTH,		FRAME_WIDTH,
						WIN_ROW_GAP,		4,
						0);

		LP_WIN.text_301	 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	TRUE,
						XV_X,			xv_col(LP_WIN.algorithm_choice, 1),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 0),
						PANEL_LABEL_STRING,	"Optimization:",
						0);

		LP_WIN.choice_301 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_CHOICE,

						PANEL_CHOOSE_ONE,	FALSE,
						XV_X,			xv_col(LP_WIN.algorithm_choice, 20),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 0),
						PANEL_LABEL_STRING,	"",
						PANEL_CHOICE_STRINGS,	"      Yes      ", "       No      ", NULL,
						PANEL_NOTIFY_PROC,	lp_choice_301,
						PANEL_VALUE,		1,
						0);

		LP_WIN.text_302	 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	TRUE,
						XV_X,			xv_col(LP_WIN.algorithm_choice, 1),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 1),
						PANEL_LABEL_STRING,	"Top Down",
						0);

		LP_WIN.text_303	 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	FALSE,
						XV_X,			xv_col(LP_WIN.algorithm_choice, 1),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 2),
						PANEL_LABEL_STRING,	"Bends  without Overlap",
						0);
		
		LP_WIN.choice_302 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_TOGGLE,

						XV_X,			xv_col(LP_WIN.algorithm_choice, 20),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 2),
						PANEL_LABEL_STRING,	"",
						PANEL_CHOICE_STRINGS,	"     Boundary     ", NULL,
						PANEL_NOTIFY_PROC,	lp_choice_302,
						PANEL_VALUE,		0,
						0);

		LP_WIN.text_304	 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	FALSE,
						XV_X,			xv_col(LP_WIN.algorithm_choice, 1),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 3),
						PANEL_LABEL_STRING,	"           with Overlap",
						0);
		
		LP_WIN.choice_303 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_TOGGLE,

						XV_X,			xv_col(LP_WIN.algorithm_choice, 20),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 3),
						PANEL_LABEL_STRING,	"",
						PANEL_CHOICE_STRINGS,	"     Standard     ", "      Multi     ", "Multi Oriented" , NULL,
						PANEL_NOTIFY_PROC,	lp_choice_303,
						PANEL_VALUE,		1,
						0);


		LP_WIN.text_305	 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	False,
						XV_X,			xv_col(LP_WIN.algorithm_choice, 1),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 4),
						PANEL_LABEL_STRING,	"Area",
						0);
		
		LP_WIN.choice_304 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_TOGGLE,

						XV_X,			xv_col(LP_WIN.algorithm_choice, 20),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 4),
						PANEL_LABEL_STRING,	"",
						PANEL_CHOICE_STRINGS,	"     Standard     " , NULL,
						PANEL_NOTIFY_PROC,	lp_choice_304,
						PANEL_VALUE,		0,
						0);

		LP_WIN.text_306	 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	TRUE,
						XV_X,			xv_col(LP_WIN.algorithm_choice, 1),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 5),
						PANEL_LABEL_STRING,	"Bottom Up",
						0);

		LP_WIN.text_307	 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	FALSE,
						XV_X,			xv_col(LP_WIN.algorithm_choice, 1),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 6),
						PANEL_LABEL_STRING,	"Bends",
						0);

		LP_WIN.choice_305 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_TOGGLE,

						XV_X,			xv_col(LP_WIN.algorithm_choice, 20),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 6),
						PANEL_LABEL_STRING,	"",
						PANEL_CHOICE_STRINGS,	"     Standard     " , NULL,
						PANEL_NOTIFY_PROC,	lp_choice_305,
						PANEL_VALUE,		0,
						0);

		LP_WIN.text_308	 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_MESSAGE,

						PANEL_LABEL_BOLD,	FALSE,
						XV_X,			xv_col(LP_WIN.algorithm_choice, 1),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 7),
						PANEL_LABEL_STRING,	"Area",
						0);

		LP_WIN.choice_306 = xv_create(
						LP_WIN.algorithm_choice,
						PANEL_TOGGLE,

						XV_X,			xv_col(LP_WIN.algorithm_choice, 20),
						XV_Y,			xv_row(LP_WIN.algorithm_choice, 7),
						PANEL_LABEL_STRING,	"",
						PANEL_CHOICE_STRINGS,	"     Standard     " , NULL,
						PANEL_NOTIFY_PROC,	lp_choice_306,
						PANEL_VALUE,		0,
						0);


		window_fit_height	( LP_WIN.algorithm_choice 	);
		XV_win_position_down	( LP_WIN.algorithm_choice, 1 	);


/***********************************************************************************************************************************/
		LP_WIN.immediate_execute = (Panel) xv_create(
						LP_WIN.mainframe,
						PANEL,

						WIN_BELOW,		LP_WIN.algorithm_choice,
						XV_X,			0,
						XV_WIDTH,		FRAME_WIDTH,
						WIN_ROW_GAP,		4,
						0);

		LP_WIN.alg_401	= xv_create(
						LP_WIN.immediate_execute,
						PANEL_BUTTON,

						XV_X,			xv_col(LP_WIN.immediate_execute, 1),
						XV_Y,			xv_row(LP_WIN.immediate_execute, 0),
						PANEL_LABEL_STRING,	"     execute    ",
						PANEL_NOTIFY_PROC,	lp_execute_algorithm,
						0);

		LP_WIN.alg_402	= xv_create(
						LP_WIN.immediate_execute,
						PANEL_BUTTON,

						XV_X,			xv_col(LP_WIN.immediate_execute, 33),
						XV_Y,			xv_row(LP_WIN.immediate_execute, 0),
						PANEL_LABEL_STRING,	"    reduce   ",
						PANEL_NOTIFY_PROC,	reduce_tree,
						0);

		LP_WIN.alg_403	= xv_create(
						LP_WIN.immediate_execute,
						PANEL_BUTTON,

						XV_X,			xv_col(LP_WIN.immediate_execute, 17),
						XV_Y,			xv_row(LP_WIN.immediate_execute, 0),
						PANEL_LABEL_STRING,	"      delete     ",
						PANEL_NOTIFY_PROC,	delete_tree,
						0);

		LP_WIN.alg_404	= xv_create(
						LP_WIN.immediate_execute,
						PANEL_BUTTON,

						XV_X,			xv_col(LP_WIN.immediate_execute, 33),
						XV_Y,			xv_row(LP_WIN.immediate_execute, 1),
						PANEL_LABEL_STRING,	"    open     ",
						PANEL_NOTIFY_PROC,	open_tree,
						0);

		LP_WIN.alg_405	= xv_create(
						LP_WIN.immediate_execute,
						PANEL_BUTTON,

						XV_X,			xv_col(LP_WIN.immediate_execute, 48),
						XV_Y,			xv_row(LP_WIN.immediate_execute, 0),
						PANEL_LABEL_STRING,	"    scale down  ",
						PANEL_NOTIFY_PROC,	lp_scale_down_proc,
						0);
		LP_WIN.alg_406	= xv_create(
						LP_WIN.immediate_execute,
						PANEL_BUTTON,

						XV_X,			xv_col(LP_WIN.immediate_execute, 48),
						XV_Y,			xv_row(LP_WIN.immediate_execute, 1),
						PANEL_LABEL_STRING,	"     scale up     ",
						PANEL_NOTIFY_PROC,	lp_scale_up_proc,
						0);


		window_fit_height	( LP_WIN.immediate_execute 	);
		XV_win_position_down	( LP_WIN.immediate_execute, 1 	);

	}


	/*** Wollte nur nicht alles nochmal aendern. ***/
	lp_set_cur_algorithm( GRAPHED_STANDARD );
	graph_state.lp_graph_state.LGG_Algorithm = GRAPHED_STANDARD;
	LP_WIN.old_value_301 = 2;

	window_fit_height	( LP_WIN.mainframe 			);
	window_fit_width	( LP_WIN.mainframe 			);
	MY_WIN_position_relative( LP_WIN.mainframe, base_frame, 641, 1	);
	xv_set			( LP_WIN.mainframe, XV_SHOW, TRUE, 0	);

}
