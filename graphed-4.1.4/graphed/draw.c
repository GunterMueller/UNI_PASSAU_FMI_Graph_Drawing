/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*				draw.c					*/
/*									*/
/************************************************************************/
/*									*/
/*	Diese Modul enthaelt die global verfuegbaren Prozeduren		*/
/*	zum Zeichnen und Markieren von Knoten und Kanten sowie		*/
/*	fuer das Gitter, das dem Graph (evtl.) unterlegt ist.		*/
/*									*/
/************************************************************************/

/* fuer rl_rectunion muss xview_other_rl_funcs definiert werden, sonst
   wird in xview/rectlist.h der Prototyp nicht uebernommen */
#define xview_other_rl_funcs

#include <xview/xview.h>
#include <xview/rectlist.h>

#include "misc.h"
#include "graph.h"
#include "draw.h"
#include "group.h"

/** ----------------- von fb auskommentiert   Anfang ----------------- **
#include <pixrect/pixrect_hs.h>
 ** ----------------- von fb auskommentiert    Ende ------------------ **/


#include "paint.h"

#include "graphed_subwindows.h"	/* modifies canvases	*/
#include "repaint.h"

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
extern Window xwin;
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/


/************************************************************************/
/*									*/
/*			GLOBALE FUNKTIONEN				*/
/*									*/
/************************************************************************/
/*									*/
/*				KNOTEN					*/
/*									*/
/*	void	draw_node  (node)					*/
/*	void	erase_node (node)					*/
/*									*/
/*	void	erase_and_delete_node (node)				*/
/*									*/
/*	void	draw_virtual_node  (x,y, sx,sy, image)			*/
/*	void	erase_virtual_node (x,y, sx,sy, image)			*/
/*									*/
/*	void	do_mark_node   (node)					*/
/*	void	do_unmark_node (node)					*/
/*									*/
/*======================================================================*/
/*									*/
/*				KANTEN					*/
/*									*/
/*	void	draw_edge             (edge)				*/
/*	void	erase_edge            (edge)				*/
/*									*/
/*	void	draw_edgelines        (buffer, el)			*/
/*	void	erase_edgelines       (buffer, el)			*/
/*	void	draw_single_edgeline  (buffer, el)			*/
/*	void	erase_single_edgeline (buffer, el)			*/
/*									*/
/*	void	draw_arrow            (edge)				*/
/*	void	erase_arrow           (edge)				*/
/*									*/
/*	void	draw_edgelabel        (edge)				*/
/*	void	erase_edgelabel       (edge)				*/
/*									*/
/*	void	draw_edge_sourcelist  (node)				*/
/*	void	draw_edge_targetlist  (node)				*/
/*	void	erase_edge_sourcelist (node)				*/
/*	void	erase_edge_targetlist (node)				*/
/*									*/
/*	void	erase_and_delete_edge (edge)				*/
/*									*/
/*	void	draw_virtual_line     (x1,y1, x2,y2)			*/
/*	void	erase_virtual_line    (x1,y1, x2,y2)			*/
/*									*/
/*	void	do_mark_edge   (edge)					*/
/*	void	do_unmark_edge (edge)					*/
/*									*/
/*	void	draw_edge_head  (edge)					*/
/*	void	draw_edge_tail  (edge)					*/
/*	void	erase_edge_head (edge)					*/
/*	void	erase_edge_tail (edge)					*/
/*									*/
/*	void	draw_edges_at_node  (node)				*/
/*	void	erase_edges_at_node (node)				*/
/*									*/
/*======================================================================*/
/*									*/
/*				GRUPPEN					*/
/*									*/
/*	void	draw_group  (group)					*/
/*	void	erase_group (group)					*/
/*									*/
/*	void	draw_virtual_group  (group, dx,dy)			*/
/*	void	erase_virtual_group (group, dx,dy)			*/
/*									*/
/*	void	erase_and_delete_group (group)				*/
/*									*/
/*	void	draw_virtual_group_box  (x1,y1, x2,y2)			*/
/*	void	erase_virtual_group_box (x1,y1, x2,y2)			*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	show_grid     (buffer, width)				*/
/*	int	get_gridwidth (wac_buffer)				*/
/*									*/
/************************************************************************/


/************************************************************************/
/*									*/
/*			LOKALE FUNKTIONEN				*/
/*									*/
/************************************************************************/


static	Rect	compute_edge_head_rect (Edge edge);
static	Rect	compute_edge_tail_rect (Edge edge);


/************************************************************************/
/*									*/
/*				ALLGEMEINES				*/
/*									*/
/************************************************************************/
/*									*/
/*			PROZEDURNAMEN					*/
/*									*/
/*	draw_...		Zeichenprozeduren			*/
/*	erase_...							*/
/*									*/
/*									*/
/*	draw_virtual_...	Zeichnen einen "virtuellen" Knoten bzw.	*/
/*	erase_virtual_...	eine "virtuelle" Kante. Hier wird	*/
/*				direkt gezeichnet, und zwar mit		*/
/*				PIX_XOR.				*/
/*									*/
/*	draw_node		Zeichnen eines "normalen" Objekts. Hier	*/
/*	draw_edge		wird nur das umbeschriebene Rechteck in	*/
/*	usw.			die global_repaint_list bzw.		*/
/*				global_erase_rectlist eingetragen; das	*/
/*				Neuzeichnen erfolgt erst durch Aufruf	*/
/*				von force_repainting (siehe auch	*/
/*				unten).					*/
/*									*/
/*	do_mark_...		Uebernehmen die Zeichenarbeit (und nur	*/
/*				diese !) beim Markieren. Auch hier	*/
/*				wird direkt gezeichnet, wieder mit	*/
/*				PIX_XOR. Daher vorsichtig einsetzen !	*/
/*									*/
/*	erase_and_delete_...	Zeichnerisches UND physikalisches	*/
/*				Loeschen eines Knoten / einer Kante.	*/
/*				Ruft set_graph_has_changed auf.		*/
/*									*/
/*======================================================================*/
/*									*/
/*	BEHANDLUNG VON GLOBAL_REPAINT_RECTLIST, GLOBAL_ERASE_RECTLIST	*/
/*									*/
/*	Beim "normalen" Zeichnen und Loeschen wird das Objekt auf der	*/
/*	Zeichenflaeche nicht direkt gezeichnet bzw. geloescht, sondern	*/
/*	zunaechst nur das umbeschriebene Rechteck in			*/
/*	global_repaint_rectlist bzw. global_erase_rectlist eingetragen.	*/
/*	In der Prozedur force_repainting werden diese Listen dann	*/
/*	ausgewertet und die angegebenen Flaechen neu gezeichnet.	*/
/*	Ist painting_enabled == FALSE, so wird das Eintragen (und damit	*/
/*	auch das Neuzeichnen) unterlassen (z.B. beim Laden von		*/
/*	Graphen).							*/
/*									*/
/*	Operation	Eintrag in					*/
/*									*/
/*	Zeichnen	global_repaint_rectlist				*/
/*	Loeschen	global_erase_rectlist				*/
/*									*/
/*									*/
/*	Zum Erzeugen, Loeschen oder Aendern ist daher folgendermassen	*/
/*	vorzugehen :							*/
/*									*/
/*	Erzeugen	    Loeschen		Aendern			*/
/*									*/
/*	Erzeugen	    erase_...		erase_...		*/
/*	.		    .			.			*/
/*	.		    .			.			*/
/*	.		    .			.			*/
/*	draw_...	    Loeschen		Aendern...		*/
/*	.		    .			.			*/
/*	.		    .			.			*/
/*	.		    .			.			*/
/*	force_repainting    force_repainting	draw_...		*/
/*						.			*/
/*						.			*/
/*						.			*/
/*									*/
/*						force_repainting	*/
/*									*/
/*	force_repainting steht i.a. immer am Ende einer Notify- oder	*/
/*	Eventprozedur, muss also nicht explizit aufgerufen werden.	*/
/*									*/
/************************************************************************/
/*									*/
/*	HINWEISE ZU DEN PROZEDUREN DRAW_VIRTUAL_..., ERASE_VIRTUAL_...	*/
/*									*/
/*======================================================================*/
/*									*/
/*	Virtuelle Objekte werden mit PIX_XOR direkt gezeichnet, dh.	*/
/*	NICHT ueber global_repaint_rectlist / global_erase_rectlist.	*/
/*	Deshalb sollten waehrend der Praesenz von "virtuellen" Objekten	*/
/*	keine "normalen" Objekte gezeichnet weren, da sonst evtl. Teile	*/
/*	von "virtuellen" Objekten geloescht, aber nicht mehr neu	*/
/*	gezeichnet werden.						*/
/*									*/
/************************************************************************/

/* Das folgende Makro haengt ein Rechteck in die Liste			*/
/* global_repaint_rectlists[n]						*/

static	void	add_to_global_repaint_rectlists (Rect *r, int n)
{
	rl_rectunion (r,
		&global_repaint_rectlists[n],
		&global_repaint_rectlists[n]);
}
#ifdef OUTDATED_CODE
#define	DRAW(r,n)  rl_rectunion (&(r),		\
		 &global_repaint_rectlists[n],	\
		 &global_repaint_rectlists[n]);
#endif
#define DRAW(r,n ) add_to_global_repaint_rectlists (&r, n);

/* Nochmal dasselbe fuer global_erase_rectlist[n]			*/

#define	ERASE(r,n) rl_rectunion (&(r),		\
		   &global_erase_rectlists[n],	\
		   &global_erase_rectlists[n]);


/************************************************************************/
/*									*/
/*				KNOTEN					*/
/*									*/
/************************************************************************/
/*									*/
/*	void	draw_node  (node)					*/
/*	void	erase_node (node)					*/
/*									*/
/*	Zeichne / Loesche Knoten, inklusive Label.			*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	draw_nodelabel  (node)					*/
/*	void	erase_nodelabel (node)					*/
/*									*/
/*	Zeichne Knotenlabel.						*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	erase_and_delete_node (node)				*/
/*									*/
/*	Loescht node sowohl zeichnerisch UND physikalisch.		*/
/*									*/
/*======================================================================*/
/*									*/
/*	draw_virtual_node  (x,y, sx,sy, image)				*/
/*	erase_virtual_node (x,y, sx,sy, image)				*/
/*									*/
/*	Zeichnet / loescht einen "virtuellen" Knoten.			*/
/*	Virtuelle Knoten haben (natuerlich) keine Label.		*/
/*									*/
/*======================================================================*/
/*									*/
/*	do_mark_node   (node)						*/
/*	do_unmark_node (node)						*/
/*									*/
/*	Zeichnerischer Anteil am Markieren von Knoten.			*/
/*	Knoten werden mit Quadraten vom Typ marker_square		*/
/*	(->paint_marker_square in paint.c) an den Ecken von		*/
/*	node->box markiert. Allerdings liegen diese Quadrate innerhalb	*/
/*	von node->box.							*/
/*									*/
/************************************************************************/



void	draw_node (Node node)
{
	DRAW (node->full_box, node->graph->buffer);
}


void	erase_node (Node node)
{
	ERASE (node->full_box, node->graph->buffer);
}



void	draw_nodelabel (Node node)
{
	DRAW (node->label.box, node->graph->buffer);
}


void	erase_nodelabel (Node node)
{
	ERASE (node->label.box, node->graph->buffer);
}



void	erase_and_delete_node (Node node)
{
	Graph	nodes_graph = node->graph;
	
	if (is_marked(node)) unmark_node (node);
	
	erase_node  (node);
	while (node->sourcelist != empty_edge)
		erase_and_delete_edge (node->sourcelist);
	while (node->targetlist != empty_edge)
		erase_and_delete_edge (node->targetlist);
	delete_node (node);
	
	set_graph_has_changed (nodes_graph);
}



void		draw_virtual_node (int x, int y, int sx, int sy, Nodetypeimage image)
{
	if (image != (Nodetypeimage)NULL) {
		paint_virtual_node (x,y, sx,sy, image, PIX_XOR | PIX_COLOR(current_nodecolor));
	} else {
		paint_rectangle (
			x-sx/2,y-sy/2, x+sx/2-1, y+sy/2-1,
			PIX_XOR | PIX_COLOR(current_nodecolor));
	}
}


void		erase_virtual_node (int x, int y, int sx, int sy, Nodetypeimage image)
{
	if (image != (Nodetypeimage)NULL) {
		paint_virtual_node (x,y, sx,sy, image, PIX_XOR | PIX_COLOR(current_nodecolor));
	} else {
		paint_rectangle (
			x-sx/2,y-sy/2, x+sx/2-1, y+sy/2-1,
			PIX_XOR | PIX_COLOR(current_nodecolor));
	}
}



void	do_mark_node (Node node)
{
	int    x1 = node_left(node) + MARKER_SQUARE_SIZE/2,
	       y1 = node_top(node) + MARKER_SQUARE_SIZE/2,
	       x2 = node_left(node) + node_width(node) - MARKER_SQUARE_SIZE/2,
	       y2 = node_top(node) + node_height(node) - MARKER_SQUARE_SIZE/2;

#if 0
	Xv_Window	pw;
	
	pw = canvases [node->graph->buffer].pixwin;
#endif
/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
	xwin = (Window)xv_get(canvases[node->graph->buffer].pixwin,XV_XID); /** auf welchem Fenster wird gearde gearbeitet? **/
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

	switch (node->marked) {
	    case MARKED_WITH_SQUARES :
		paint_marker_square (x1,y1); 
		paint_marker_square (x1,y2); 
		paint_marker_square (x2,y1); 
		paint_marker_square (x2,y2); 
		break;
	    case MARKED_AT_BOUNDARY :
		if (node_width(node) < MARKER_SQUARE_SIZE) {
			x1 = node_left(node) + node_width(node)/4;
			x2 = node_left(node) + node_width(node) - node_width(node)/4;
		}
		if (node_height(node) < MARKER_SQUARE_SIZE) {
			y1 = node_top(node) + node_height(node)/4;
			y2 = node_top(node) + node_height(node) - node_height(node)/4;
		}
		paint_marker_rectangle (x1,y1, x2,y2);
		break;
            default: break;
	}
}


void	do_unmark_node (Node node)
{
	do_mark_node (node);
}



/************************************************************************/
/*									*/
/*			KANTEN ZEICHNEN					*/
/*									*/
/************************************************************************/
/*									*/
/*	void	draw_edge  (edge)					*/
/*	void	erase_edge (edge)					*/
/*									*/
/*	Zeichne / Loesche gesamte Kante (edgeline + label + arrow).	*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	draw_edgelines  (buffer, el)				*/
/*	void	erase_edgelines (buffer, el)				*/
/*									*/
/*	Zeichne / loesche den Kantenzug ab el.				*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	draw_single_edgeline  (buffer, el)			*/
/*	void	erase_single_edgeline (buffer, el)			*/
/*									*/
/*	Zeichne / loesche das Kantenstueck el --- el->suc.		*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	draw_arrow  (el)					*/
/*	void	erase_arrow (el)					*/
/*									*/
/*	Zeichne / loesche arrow.					*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	draw_edgelabel  (edge)					*/
/*	void	erase_edgelabel (edge)					*/
/*									*/
/*	Zeichne / loesche Kantenlabel.					*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	draw_edge_sourcelist  (node)				*/
/*	void	draw_edge_targetlist  (node)				*/
/*	void	erase_edge_sourcelist (node)				*/
/*	void	erase_edge_targetlist (node)				*/
/*									*/
/*	Zeichne / loesche Kantenliste.					*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	erase_and_delete_edge (edge)				*/
/*									*/
/*	Loescht edge zeichnerisch UND physikalisch.			*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	draw_virtual_line  (x1,y1, x2,y2)			*/
/*	void	erase_virtual_line (x1,y1, x2,y2)			*/
/*									*/
/*	Zeichnet / loescht eine "virtuelle" Kante.			*/
/*	Virtuelle Kanten haben weder Pfeil noch Label !			*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	do_mark_edge   (edge)					*/
/*	void	do_unmark_edge (edge)					*/
/*									*/
/*	Zeichnerischer Anteil am Markieren von Kanten.			*/
/*	Eine Kante wird mit einem Quadrat vom Typ marker_square		*/
/*	(->paint_marker_square in paint.c) an den Eckpunkten und	*/
/*	einem Rechteck (->paint_marker_rect in paint.c) an den		*/
/*	Mittelpunkten jedes Kantenstueckes markiert.			*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	draw_edge_head  (edge)					*/
/*	void	draw_edge_tail  (edge)					*/
/*	void	erase_edge_head (edge)					*/
/*	void	erase_edge_tail (edge)					*/
/*									*/
/*	Zeichnet / loescht Kopf- bzw. Schwanzstueck einer Kante.	*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	draw_edges_at_node  (node)				*/
/*	void	erase_edges_at_node (node)				*/
/*									*/
/*	Zeichnet / loescht die Kopfstuecke aller Kanten in		*/
/*	node->sourcelist und die Schwanzstuecke in node->targetlist.	*/
/*									*/
/************************************************************************/



void	draw_edge (Edge edge)
{
	DRAW (edge->box, edge->source->graph->buffer);
}


void	erase_edge (Edge edge)
{
	ERASE (edge->box, edge->source->graph->buffer);
}


void		draw_edgelines (int buffer, Edgeline el)
{	
	Edgeline	e = el;
	
	if (e != (Edgeline)NULL) do {
		draw_single_edgeline (buffer, e);
		e = e->suc;
	} while (e->suc != el);
}


void		erase_edgelines (int buffer, Edgeline el)
{
	Edgeline	e = el;
		
	if (e != (Edgeline)NULL) do {
		erase_single_edgeline (buffer, e);
		e = e->suc;
	} while (e->suc != el);
}


void		draw_single_edgeline (int buffer, Edgeline el)
{
	DRAW (el->box, buffer);
}


void		erase_single_edgeline (int buffer, Edgeline el)
{
	DRAW (el->box, buffer); 
}



void	draw_arrow (Edge edge)
{
	DRAW (edge->arrow.box, edge->source->graph->buffer);
}


void	erase_arrow (Edge edge)
{
	ERASE (edge->arrow.box, edge->source->graph->buffer);
}



void	draw_edgelabel (Edge edge)
{
	DRAW (edge->label.box, edge->source->graph->buffer);
}


void	erase_edgelabel (Edge edge)
{
	ERASE (edge->label.box, edge->source->graph->buffer);
}



void	draw_edge_sourcelist (Node node)
{
	Edge	edge;
	
	for_edge_sourcelist (node, edge) {
		draw_edge (edge);
	} end_for_edge_sourcelist (node, edge);
}


void	draw_edge_targetlist (Node node)
{
	Edge	edge;
	
	for_edge_targetlist (node, edge) {
		draw_edge (edge);
	} end_for_edge_targetlist (node, edge);
}


void	erase_edge_sourcelist (Node node)
{
	Edge	edge;
	
	for_edge_sourcelist (node, edge) {
		erase_edge (edge);
	} end_for_edge_sourcelist (node, edge);
}


void	erase_edge_targetlist (Node node)
{
	Edge	edge;
	
	for_edge_targetlist (node, edge) {
		erase_edge (edge);
	} end_for_edge_targetlist (node, edge);
}




void	erase_and_delete_edge (Edge edge)
{
	Graph	edges_graph = edge->source->graph;
	
	if (is_marked(edge)) unmark_edge (edge);
	erase_edge  (edge);
	delete_edge (edge);
	
	set_graph_has_changed (edges_graph);
}



void	draw_virtual_line (int x1, int y1, int x2, int y2)
{
/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
	xwin = (Window)xv_get(working_area_canvas_pixwin,XV_XID); /** auf welchem Fenster wird gearbeitet? **/
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/
	paint_line (x1,y1, x2,y2, PIX_XOR | PIX_COLOR (current_edgecolor));
}



void	erase_virtual_line (int x1, int y1, int x2, int y2)
{
	paint_line (x1,y1, x2,y2, PIX_XOR | PIX_COLOR (current_edgecolor));
}



void	do_mark_edge (Edge edge)
{
	register Edgeline	el, el_head;
	int			label_is_empty;
#if 0
	Xv_Window		pw;
	
	pw = canvases [edge->source->graph->buffer].pixwin;
#endif

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
	xwin = (Window)xv_get(canvases[edge->source->graph->buffer].pixwin,XV_XID); /** auf welchem Fenster wird gearbeitet? **/
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

	el = el_head = edge->line;
	label_is_empty = edge->label.text == NULL || !strcmp(edge->label.text,"");
	if (el != (Edgeline)NULL) {
		paint_marker_square (el->x, el->y);
		if (label_is_empty || el != edge->label.after_el) {
			paint_marker_rect (
				(el->x + el->suc->x) / 2,
				(el->y + el->suc->y) / 2);
		}
		el = el->suc;
		if (el != el_head->pre) do {
			paint_marker_square (el->x, el->y);
			if (label_is_empty || el != edge->label.after_el) {
				paint_marker_rect (
					(el->x + el->suc->x) / 2,
					(el->y + el->suc->y) / 2);
			}
			el = el->suc;
		} while (el != el_head->pre);
		paint_marker_square (el->x, el->y);
	}

}


void	do_unmark_edge (Edge edge)
{
	do_mark_edge (edge);
}



void	draw_edge_head (Edge edge)
{
	Rect	r;
	
	if (is_marked(edge)) do_mark_edge (edge);
	r = compute_edge_head_rect (edge);
	DRAW (r, edge->source->graph->buffer);
}


void	draw_edge_tail (Edge edge)
{
	Rect	r;
	
	if (is_marked(edge)) do_mark_edge (edge);
	r = compute_edge_tail_rect (edge);
	DRAW (r, edge->source->graph->buffer);
}



void	erase_edge_head (Edge edge)
{
	Rect	r;
	
	if (is_marked(edge)) do_unmark_edge (edge);
	r = compute_edge_head_rect (edge);
	ERASE (r, edge->source->graph->buffer);
}


void	erase_edge_tail (Edge edge)
{
	Rect	r;
	
	if (is_marked(edge)) do_unmark_edge (edge);
	r = compute_edge_tail_rect (edge);
	ERASE(r, edge->source->graph->buffer);
}



void	draw_edges_at_node (Node node)
{
	Edge	edge;
	Rect	r, edge_head_rect, edge_tail_rect;
	
	r = rect_null;
	
	for_edge_sourcelist (node, edge) {
		edge_head_rect = compute_edge_head_rect (edge);
		r = rect_bounding (&r, &edge_head_rect);
	} end_for_edge_sourcelist (node, edge);
	for_edge_targetlist (node, edge) {
		edge_tail_rect = compute_edge_tail_rect (edge);
		r = rect_bounding (&r, &edge_tail_rect);
	} end_for_edge_targetlist (node, edge);
	
	DRAW (r, node->graph->buffer);
}


void	erase_edges_at_node (Node node)
{
	Edge	edge;
	Rect	r, edge_head_rect, edge_tail_rect;
	
	r = rect_null;
	
	for_edge_sourcelist (node, edge) {
		edge_head_rect = compute_edge_head_rect (edge);
		r = rect_bounding (&r, &edge_head_rect);
	} end_for_edge_sourcelist (node, edge);
	for_edge_targetlist (node, edge) {
		edge_tail_rect = compute_edge_tail_rect (edge);
		r = rect_bounding (&r, &edge_tail_rect);
	} end_for_edge_targetlist (node, edge);

	ERASE (r, node->graph->buffer);
}
/************************************************************************/
/*									*/
/*			GROUP ZEICHNEN					*/
/*									*/
/************************************************************************/
/*									*/
/*	void	draw_group  (group)					*/
/*	void	erase_group (group)					*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	erase_and_delete_group (group)				*/
/*									*/
/*	Frees group after deleting and erasing its nodes.		*/
/*									*/
/*======================================================================*/	
/*	void	draw_virtual_group  (group, dx,dy)			*/
/*	void	erase_virtual_group (group, dx,dy)			*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	draw_virtual_group_box  (x1,y1, x2,y2)			*/
/*	void	erase_virtual_group_box (x1,y1, x2,y2)			*/
/*									*/
/************************************************************************/


void	draw_group (Group group)
{
	Rect	rect_around_group;
	
	rect_around_group = compute_rect_around_group (group);
	DRAW (rect_around_group, group->node->graph->buffer);
}


void	erase_group (Group group)
{
	Rect	rect_around_group;
	
	rect_around_group = compute_rect_around_group (group);
	ERASE (rect_around_group, group->node->graph->buffer);
}


void	erase_and_delete_group (Group group)
{
	Group	g;
	Graph	last_graph = empty_graph;
	
	erase_group (group);
	
	for_group (group, g) {
		if (g->node->graph != last_graph)
			set_graph_has_changed (last_graph = g->node->graph);
		delete_node (g->node);
	} end_for_group (group, g);
	free_group (group);
}


void	draw_virtual_group (Group group, int dx, int dy)
{
	Group	g;
	
	for_group (group, g) {
		draw_virtual_node (
			node_x(g->node) + dx, node_y(g->node) + dy,
			node_width (g->node), node_height(g->node),
			(Nodetypeimage)NULL);
	} end_for_group (group, g);
}


void	erase_virtual_group (Group group, int dx, int dy)
{
	Group	g;
	
	for_group (group, g) {
		erase_virtual_node (
			node_x(g->node) + dx, node_y(g->node) + dy,
			node_width (g->node), node_height(g->node),
			(Nodetypeimage)NULL);
	} end_for_group (group, g);
}


void	draw_virtual_group_box (int x1, int y1, int x2, int y2)
{
	paint_rectangle (x1,y1, x2,y2, PIX_XOR | PIX_COLOR (current_nodecolor));
}


void	erase_virtual_group_box (int x1, int y1, int x2, int y2)
{
	paint_rectangle (x1,y1, x2,y2, PIX_XOR | PIX_COLOR (current_nodecolor));
}
/************************************************************************/
/*									*/
/*		GITTERUNTERLAGE FUER DEN GRAPHEN			*/
/*									*/
/************************************************************************/
/*									*/
/*	void	show_grid (buffer, width)				*/
/*									*/
/*	width == 0  <==>  kein Gitter angezeigt				*/
/*	width != 0  <==>  Gitter angezeigt				*/
/*									*/
/*	ACHTUNG : Nur Werte 8,16,32,64,128  koennen im Menue angezeigt	*/
/*	werden !							*/
/*									*/
/************************************************************************/


void	show_grid (int buffer, int width)
{
	canvases[buffer].gridwidth = width;
/*	install_grid_in_menu (width);	*/
	redraw_all ();
}


int	get_gridwidth (int buffer)
{
	return canvases[buffer].gridwidth;
}
/************************************************************************/
/*									*/
/*			    HILFSFUNKTIONEN				*/
/*									*/
/************************************************************************/



static	Rect	compute_edge_head_rect (Edge edge)
{
	Rect	r;
	
	r = rect_bounding (&edge->line->box, &(edge->label.box));
	if (is_single_edgeline(edge->line))
		r = rect_bounding (&r, &(edge->arrow.box));
	
	return	r;
}



static	Rect	compute_edge_tail_rect (Edge edge)
{
	Rect	r;
	
	r = rect_bounding (&edge->line->pre->pre->box, &(edge->arrow.box));
	if (is_single_edgeline(edge->line))
		r = rect_bounding (&r, &(edge->label.box));
	
	return	r;
}
