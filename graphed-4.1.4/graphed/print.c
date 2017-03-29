/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt, Marc Felsberg		*/
/************************************************************************/
/*									*/
/*			    print.c					*/
/*									*/
/************************************************************************/

#define _OTHER_RECT_FUNCTIONS

#include <xview/xview.h>
#include "misc.h"
#include "graph.h"
#include "type.h"
#include "print.h"
#include "repaint.h"
#include "ps.h"
#include "graphed_svi.h"

void   get_buffer_visible_rect (int buffer, Rect *rect);


Print_settings	print_settings;

Print_settings	init_print_settings (void)
{
	Print_settings	settings;  

	settings.device = OUTPUT_PS;
	settings.area   = AREA_FULL;
	settings.color  = PRINT_NOCOLOR;

	settings.frame.width  = 0.0;
	settings.frame.height = 0.0;
	settings.frame.top    = 0.0;
	settings.frame.left   = 0.0;

	settings.ps.fit           = TRUE;
	settings.ps.frame_visible = TRUE;
	settings.ps.orientation   = PS_PORTRAIT;

	settings.ps.scaling = (double)0.2;
	settings.ps.margin_left = (double)25;
	settings.ps.margin_right = (double)25;
	settings.ps.margin_top = (double)25;
	settings.ps.margin_bottom = (double)25;

	return settings;
}


Rect		compute_print_rect (int buffer, Print_area area)
{
	Rect	visible_rect, print_rect;
	
	switch (area) {
	    case AREA_VISIBLE :
		get_buffer_visible_rect (buffer, &visible_rect);
		print_rect = compute_rect_around_graphs (buffer);
		rect_intersection (&print_rect, &visible_rect, &print_rect);
		break;
	    case AREA_FULL :
		print_rect = compute_rect_around_graphs (buffer);
		break;
	}

	return print_rect;
}


void		print_buffer (char *filename, int buffer, Print_settings settings)
{
	Rect	rect;

	rect = compute_print_rect (buffer, settings.area);
	switch (print_settings.device) {
	    case OUTPUT_PS :
		if (settings.ps.fit == FALSE) {
			settings.frame.left   = rect_left(&rect);
			settings.frame.top    = rect_top(&rect);
			settings.frame.width  = rect_width(&rect);
			settings.frame.height = rect_height(&rect);
		}
		write_postscript_file (filename, buffer, rect, settings);
		break;
	    case OUTPUT_XBITMAP_FILE :
		write_xbitmap_file (filename, buffer, rect, settings);
		break;
	}
}


void		write_postscript_file (char *filename, int buffer, Rect print_rect, Print_settings settings)
{
	PsPage*         pspage;
	Graph		graph;
	Node		node;
	Edge		edge;
	Nodetype	nodetype;
	Edgetype	edgetype;
	Server_image	svi;
	int		i;
	
	pspage = ps_page_create(filename,
		PS_SCALE_TO_FIT,
			TRUE,
			(double)print_rect.r_left,
			(double)print_rect.r_top,
			(double)print_rect.r_width,
			(double)print_rect.r_height,
	        PS_PAGE_SCALING_XY,
			settings.ps.scaling,
			settings.ps.scaling,
	        PS_FRAME_VISIBLE,
			settings.ps.frame_visible,
	        PS_PAGE_ORIENTATION,
			settings.ps.orientation,
	        PS_FRAME_MARGIN_LEFT,
			settings.ps.margin_left,
	        PS_FRAME_MARGIN_RIGHT,
			settings.ps.margin_right,
	        PS_FRAME_MARGIN_TOP,
			settings.ps.margin_top,
	        PS_FRAME_MARGIN_BOTTOM,
			settings.ps.margin_bottom,
	NULL);
	
	if (settings.ps.fit == FALSE) {
		ps_page_set (pspage,
/*
			PS_FRAME_ORIGIN_X, settings.frame.left,
			PS_FRAME_ORIGIN_Y, settings.frame.top,
*/
			PS_FRAME_WIDTH,    settings.frame.width,
			PS_FRAME_HEIGHT,   settings.frame.height,
			PS_SCALE_TO_FIT,
				TRUE,
				(double)print_rect.r_left,
				(double)print_rect.r_top,
				(double)print_rect.r_width,
				(double)print_rect.r_height,
			NULL);
	}

	edgetype = get_edgetype(0);
	ps_page_set(pspage,
		PS_LINESTYLE_DEF,
			edgetype->filename, pat2str(edgetype->pattern),
		PS_DEFAULT_LINESTYLE,
			edgetype->filename,
		NULL);

	i=0;
	while((edgetype = get_edgetype(++i)) != (Edgetype)NULL) {
		ps_page_set(pspage,
			PS_LINESTYLE_DEF,
				edgetype->filename,
				pat2str(edgetype->pattern),
			NULL);
	};

	i=0;
	while((nodetype = get_nodetype(++i)) !=(Nodetype)NULL)
		if(!nodetype->is_system) {
/** ----------------- von fb auskommentiert   Anfang ----------------- **
			svi = pr_to_svi(nodetype->pr);
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
			svi = pm_to_svi(nodetype->pm);
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

			ps_page_set(pspage,
				PS_IMAGE_DEF,
					nodetype->filename,
					(int)xv_get(svi, XV_WIDTH),
					(int)xv_get(svi, XV_HEIGHT),
#ifdef OLD_SVI2STR
					(int)xv_get(svi, SERVER_IMAGE_DEPTH),
					svi2str(svi),
#else
					1, /* just b/w */
					pm2str(nodetype->pm, (int)xv_get(svi, XV_WIDTH), (int)xv_get(svi, XV_HEIGHT)),
#endif
				NULL);
		};
	ps_write_page_header(pspage);
	
	for_all_graphs(buffer, graph) if(rect_intersectsrect(&(graph->box), &print_rect)) {
		for_nodes(graph, node) {
			ps_paint_node(pspage, node, &print_rect);
		} end_for_nodes(graph, node);
	} end_for_all_graphs(buffer, graph);

	for_all_graphs(buffer, graph) if(rect_intersectsrect(&(graph->box), &print_rect)) {
		for_nodes(graph, node) {
			for_edge_sourcelist(node, edge) {
				ps_paint_edge(pspage, edge, &print_rect);
			} end_for_edge_sourcelist(node, edge);
		} end_for_nodes(graph, node);
	} end_for_all_graphs(buffer, graph);

	ps_page_close(pspage);
}


void		write_xbitmap_file (char *filename, int buffer, Rect rect, Print_settings settings)
{
/** ----------------- von fb auskommentiert   Anfang ----------------- **/
#if FALSE
	Pixrect *pixrect;
	FILE    *file;
			
	file = fopen (filename, "w");
	if (file != (FILE *)NULL) {
		pixrect = paint_graph_in_rect (
			&rect,
			buffer,
			iif (settings.color == PRINT_COLOR, TRUE, FALSE));
		if (pixrect != (Pixrect *)NULL) {
			pr_dump (pixrect, file, NULL, RT_STANDARD, 0);
		}
	}
	fclose (file);
#endif
/** ----------------- von fb auskommentiert    Ende ------------------ **/


}
