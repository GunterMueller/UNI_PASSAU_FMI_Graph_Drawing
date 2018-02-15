/******************************************************************************/
/*											      	*/
/*				     FILE: lpr_lrp.c				   	      */
/*											      	*/
/*	Hier werden die Parameter des Layout-Restriktionen-Systems berechnet.	*/
/*    Es werden also die Struktureintr"age ta_td und pes_array in lpr_edge   	*/
/* 	und ts_array, tn, in_con_seq und out_con_seq in lpr_graph gesetzt.	*/
/*	Die Hauptfunktion dieses Abschnitts ist compute_layout_restriction_par	*/
/*	und steht am Ende des Files.								*/
/*											      	*/
/******************************************************************************/

#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lp_edgeline.h"

#include "lpr_nnode.h"
#include "lpr_eedge.h"
#include "lpr_ggraph.h"

#include "lpr_optimal_layout.h"
#include "lpr_lrp.h"



/******************************************************************************/
/*													*/
/*	Zun"achst einige Funktionen zum Arbeiten mit Objekten vom Typ		*/
/*					lpr_track_ass_des						*/
/*													*/
/******************************************************************************/

/******************************************************************************
Function	: create_lpr_track_ass_des	
Input		: void
Output	: lpr_Track_ass_des
Description : Erzeugt ein Objekt vom Typ lpr_track_ass_des und gibt einen Zeiger
		  darauf zur"uck. -1 in td und tn steht f"ur undefiniert.
******************************************************************************/		  
lpr_Track_ass_des create_lpr_track_ass_des(void)
{
	lpr_Track_ass_des new_ta_td;

	new_ta_td = (lpr_Track_ass_des) mymalloc(sizeof( struct lpr_track_ass_des));	/* Reserviere Speicher 	*/
	new_ta_td->td1		= (int)-1;								/* Setze Eintr"age	*/
	new_ta_td->td2		= (int)-1;
	new_ta_td->tn1		= (int)-1;
	new_ta_td->tn2		= (int)-1;
	new_ta_td->ta_type 	= (int)-1;
	new_ta_td->node		= NULL;
	new_ta_td->pre		= (lpr_Track_ass_des) new_ta_td;				/* Doppelt verkettet	*/
	new_ta_td->suc		= (lpr_Track_ass_des) new_ta_td;

	return new_ta_td;											/* und zur"uck		*/
}

/******************************************************************************
Function	: add_ta_td_to_ta_td_list	
Input		: lpr_Track_ass_des list, new
Output	: lpr_Track_ass_des
Description : H"angt ein Element new an die Liste list an. Zur"uckgegeben wird
		  der Anfang der Liste.
******************************************************************************/		  
lpr_Track_ass_des add_ta_td_to_ta_td_list(lpr_Track_ass_des list, lpr_Track_ass_des new)
{
	lpr_Track_ass_des last;

	if (list == NULL)			/* Falls Liste leer, dann ist new neue Liste 	*/
		return new;
	else
	{
		last      = list->pre;	/* Sonst stelle Verzeigerung her			*/
		last->suc = new;
		new->pre  = last;
		list->pre = new;
		new->suc  = list;
		return list;		/* und gib Listenanfang zur"uck.			*/
	}
}


/******************************************************************************
Function	: free_lpr_track_ass_des	
Input		: lpr_Track_ass_des list
Output	: void
Description : L"oscht eine Liste vom Typ lpr_Track_ass_des. list darf auch NULL
		  sein. Gibt aber nur den Speicher frei. Also NULL-setzen nicht ver-
		  gessen.
******************************************************************************/		  
void free_lpr_track_ass_des(lpr_Track_ass_des list)
{
	lpr_Track_ass_des to_delete;

	if (list != NULL)				/* Falls es was zu l"oschen gibt 	*/
	{
		list->pre->suc = NULL; 		/* Sichere Terminierung			*/
		while( list )			/* und durchlaufe die Liste		*/
		{
			to_delete = list;		/* L"osche ein Element nach dem an-	*/
			list = list->suc;		/* deren.					*/

			free( to_delete );
		}
	}
}










/******************************************************************************/
/*													*/
/*		Jetzt einige Funktionen zum Arbeiten mit Objekten vom Typ		*/
/*						lpr_track_sharing					*/
/*													*/
/******************************************************************************/

/******************************************************************************
Function	: create_lpr_track_sharing	
Input		: void
Output	: lpr_Track_sharing
Description : Erzeugt ein Objekt vom Typ lpr_track_sharing und gibt einen Zeiger
		  darauf zur"uck. -1 in track_number steht f"ur undefiniert.
******************************************************************************/		  
lpr_Track_sharing create_lpr_track_sharing(void)
{
	lpr_Track_sharing	new_ts = (lpr_Track_sharing) mymalloc(sizeof( struct lpr_track_sharing));	

	new_ts->edge1 		= NULL;
	new_ts->edge2 		= NULL;
	new_ts->track_number	= (int)-1;
	new_ts->pre		= (lpr_Track_sharing) new_ts;
	new_ts->suc		= (lpr_Track_sharing) new_ts;

	return new_ts;
}

/******************************************************************************
Function	: add_ts_to_ts_list	
Input		: lpr_Track_sharing list, new
Output	: lpr_Track_sharing
Description : H"angt ein Element new an die Liste list an. Zur"uckgegeben wird
		  der Anfang der Liste.
******************************************************************************/		  

lpr_Track_sharing add_ts_to_ts_list(lpr_Track_sharing list, lpr_Track_sharing new)
{
	lpr_Track_sharing last;

	if (list == NULL)			/* Falls Liste leer, dann ist new neue Liste 	*/
		return new;
	else					/* Sonst stelle Verzeigerung her			*/
	{
		last      = list->pre;
		last->suc = new;
		new->pre  = last;
		list->pre = new;
		new->suc  = list;
		return list;		/* und gib Listenanfang zur"uck.			*/
	}
}


/******************************************************************************
Function	: free_lpr_track_sharing	
Input		: lpr_Track_sharing list
Output	: void
Description : L"oscht eine Liste vom Typ lpr_Track_sharing. list darf auch NULL
		  sein. Gibt aber nur den Speicher frei. Also NULL-setzen nicht ver-
		  gessen.
******************************************************************************/
void free_lpr_track_sharing(lpr_Track_sharing list)
{
	lpr_Track_sharing	to_delete;

	if (list != NULL)			/* Falls es was zu l"oschen gibt 	*/
	{
		list->pre->suc = NULL; 	/* Sichere Terminierung			*/
		while( list )		/* und durchlaufe die Liste		*/
		{
			to_delete = list;	/* L"osche ein Element nach dem an-	*/
			list = list->suc;	/* deren.					*/

			free( to_delete );
		}
	}
}











/******************************************************************************/
/*													*/
/*				Jetzt einige Hilfs-Funktionen 				*/
/*													*/
/******************************************************************************/

/***************************************************************************************
Function	: get_track_sharing	
Input		: lpr_Graph prod;
		  int	 	side, track_nr;
Output	: lpr_Track_sharing
Description : Holt aus dem track-sharing von prod mit Seite side den Eintrag im Track mit
		  Nummer track_nr
***************************************************************************************/
lpr_Track_sharing get_track_sharing(lpr_Graph prod, int side, int track_nr)
{
	lpr_Track_sharing cur_ts;

	FOR_LPR_TS(prod->ts_array[side], cur_ts)		/* Durchlaufe die Track-Sharing-Liste 	*/
		if (cur_ts->track_number == track_nr)	/* auf dieser Seite und suche nach 		*/
			return cur_ts;				/* Eintrack mit track_number == track_nr	*/
	END_FOR_LPR_TS(prod->ts_array[side], cur_ts);	/* Falls gefunden, zur"uckgeben.		*/
	return NULL;
}

/***************************************************************************************
Function	: get_track_ass_des	
Input		: lpr_Edge edge, lpr_Graph prod
Output	: lpr_Track_ass_des
Description : Sucht in der Track-Assignment-Description nach einem Eintrag mit
		  node->applied_production == prod und gibt diesen zur"uck.
***************************************************************************************/
lpr_Track_ass_des get_track_ass_des(lpr_Edge edge, lpr_Graph prod)
{
	lpr_Track_ass_des cur_track_ass_des;

	FOR_LPR_TRACK_ASS_DES( edge->ta_td, cur_track_ass_des)		/* Durchlaufe das ta_td dieser Kante 	*/
		if ( cur_track_ass_des->node->applied_production == prod )  /* und suche den richtigen Eintrag		*/
			return cur_track_ass_des;					/* Sobald gefunden, zur"uckgeben.		*/
	END_FOR_LPR_TRACK_ASS_DES( edge->ta_td, cur_track_ass_des);
	return NULL;
}


	
/*************************************************************************
Function	: get_optimal_edge_of_lpr_edge
Input		: lpr_Graph prod;
		  lpr_Edge  lpr_edge;
Output	: lpr_Iso_edge
Description : Zweck der Funktion ist es das Layout bzw einen lpr_Iso_edge-
		  Zeiger einer Kante zur"uckzugeben. Dazu wird die Nummer des
		  optimalen Layouts von prod berechnet und dann aus dem
		  array_of_iso_edge_pointers das entsprechende Objekt vom Typ
		  lpr_iso_edge zur"uckgegeben.
*************************************************************************/
lpr_Iso_edge get_optimal_edge_of_lpr_edge(lpr_Graph prod, lpr_Edge lpr_edge)
{
	int index;

	for(index = 0; index < prod->number_of_iso_prods; index++)			/* Suche die zur Einbettungsregel geh"orende 	*/
		if (prod->array_of_iso_prod_pointers[index] == prod->optimal_layout) break;	/* Graphed-Edge.				*/
	return lpr_edge->array_of_iso_edge_pointers[index];				/* und zur"uck.						*/
}


/*************************************************************************
Function	: get_optimal_node_of_lpr_node
Input		: lpr_Graph prod;
		  lpr_Edge  lpr_node;
Output	: Node
Description : Zweck der Funktion ist es das Layout  eines Knoten zur"uckzugeben.
		  Dazu wird die Nummer des optimalen Layouts von prod berechnet und
		  dann aus dem array_of_iso_node_pointers das entsprechende Objekt
		  vom Typ Node zur"uckgegeben.
*************************************************************************/
Node get_optimal_node_of_lpr_node(lpr_Graph prod, lpr_Node lpr_node)
{
	int index;

	for(index = 0; index < prod->number_of_iso_prods; index++)			/* Suche die zum lpr_node geh"orenden Node	*/
		if (prod->array_of_iso_prod_pointers[index] == prod->optimal_layout) break;							
	return lpr_node->array_of_iso_node_pointers[index];				/* und zur"uck.						*/
}

/***************************************************************************************
Function	: lower_neighbor_dir	
Input		: int dir
Output	: int 
Description : Gibt die lower_neighbor-Direction von dir zur"uck. Also left -> down, 
		  up->left, right->down, down->left 
***************************************************************************************/
int lower_neighbor_dir(int dir)
{
	if ((dir == L_dir) || (dir == R_dir)) return D_dir;
	else return L_dir;
}

/***************************************************************************************
Function	: higher_neighbor_dir	
Input		: int dir
Output	: int 
Description : Gibt die higher_neighbor-Direction von dir zur"uck. Also left -> up, 
		  up->right, right->up, down->right 
***************************************************************************************/
int higher_neighbor_dir(int dir)
{
	if ((dir == L_dir) || (dir == R_dir)) return U_dir;
	else return R_dir;
}












/******************************************************************************/
/*													*/
/*	Nun die Funktionen die im Zusammenhang mit dem Erstellen der Sequen-	*/
/*	tialisierungen von Einbettungsregeln ben"otigt werden.			*/
/*													*/
/******************************************************************************/

/***************************************************************************************
Function	: insert_edgelist_in_seq
Input		: lpr_Edgelist 	list, new;
		  int			dir;
		  lpr_Graph 	prod;
		  int 		typ;
Output	: lpr_Edgelist
Description : Dient zum Einf"ugen einer Einbettungsregel vom typ (0 = lpr_IN_CONN_REL,
		  1 = lpr_OUT_CONN_REL) in die Liste von sequentialisierten Einbettungs-
		  regeln. Mittels dir wird entschieden ob das Einf"ugekriterium die x-
		  oder y-Koordinate ist. prod wird ben"otigt um an das Layout der Kante
		  zu kommen und damit an die entsprechende Koordinate.
***************************************************************************************/
lpr_Edgelist insert_edgelist_in_seq(lpr_Edgelist list, lpr_Edgelist new, int dir, lpr_Graph prod, int typ)
{
	int 			first_pos, last_pos, new_pos, old_pos;
	lpr_Edgelist 	cur_edgelist;
	Edge			layout_edge, first_edge, last_edge, old_edge;


	if (list == NULL)											/* Bei leerer Liste wird wie immer new zur neuen Liste 	*/    
		return new;
	else
	{
		layout_edge = (get_optimal_edge_of_lpr_edge(prod,new->edge))->edge;	/* Berechne die Graphed-Edge und die relevante Koordinate 	*/
		if ((dir == L_dir) || (dir == R_dir))						/* der einzuf"ugenden Kanten. Die Koordinate h"angt vom Typ */
			if (typ == 0) 									/* der Einbettungsregel und ihrer Richtung im Layout ab.	*/
				new_pos = layout_edge->lp_edge.lp_line->y;
			else
				new_pos = layout_edge->lp_edge.lp_line->pre->y;
		else
			if (typ == 0)
				new_pos = layout_edge->lp_edge.lp_line->x;
			else
				new_pos = layout_edge->lp_edge.lp_line->pre->x;
				

		first_edge = (get_optimal_edge_of_lpr_edge(prod,list->edge))->edge;	/* Berechne die Graphed-Edge und die relevante Koordinate 	*/
		if ((dir == L_dir) || (dir == R_dir))						/* der ersten Kante in der Liste.					*/
			if (typ == 0)
				first_pos = first_edge->lp_edge.lp_line->y;
			else
				first_pos = first_edge->lp_edge.lp_line->pre->y;
		else
			if (typ == 0)
				first_pos = first_edge->lp_edge.lp_line->x;
			else
				first_pos = first_edge->lp_edge.lp_line->pre->x;	

		if (new_pos < first_pos)								/* "Uberpr"ufe, ob das neue Element, am Anfang der Liste	*/
		{												/* eingef"ugt werden muss.						*/
			new->suc 	= list;								/* Falls ja, dann f"uge ein, und gib new als neuen Anfang	*/
			new->pre 	= list->pre;							/* der Liste zur"uck.							*/
			list->pre->suc 	= new;
			list->pre 	= new;
			return new;
		}
		else
		{
			last_edge = (get_optimal_edge_of_lpr_edge(prod,list->pre->edge))->edge;	/* Berechne die Graphed-Edge und die relevante Koordinate*/
			if ((dir == L_dir) || (dir == R_dir))					/* der ersten Kante in der Liste.     			 	*/
				if (typ == 0)
					last_pos = last_edge->lp_edge.lp_line->y;
				else
					last_pos = last_edge->lp_edge.lp_line->pre->y;
			else
				if (typ == 0)
					last_pos = last_edge->lp_edge.lp_line->x;
				else
					last_pos = last_edge->lp_edge.lp_line->pre->x;		
			if (last_pos < new_pos)								/* "Uberpr"ufe, ob das neue Element, an das Ende der Liste	*/
			{											/* angeh"angt werden muss.						*/
				new->pre 	= list->pre;						/* Falls ja, dann h"ange an, und gib list als Anfang der 	*/
				new->suc	= list;							/* Liste zur"uck.								*/
				list->pre->suc 	= new;
				list->pre 	= new;
				return list;
			}
			else											/* In diesem Fall muss, das neue Element innerhalb der Lis-	*/
			{											/* te eingef"ugt werden.						*/
				FOR_LPR_EDGELIST(list, cur_edgelist)				/* Durchlaufe also die Liste und suche den richtigen Platz.	*/
					old_edge = (get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge))->edge;
					if ((dir == L_dir) || (dir == R_dir))
						if (typ == 0)
							old_pos = layout_edge->lp_edge.lp_line->y;
						else
							old_pos = layout_edge->lp_edge.lp_line->pre->y;						
					else
						if (typ == 0)
							old_pos = layout_edge->lp_edge.lp_line->x;
						else	
							old_pos = layout_edge->lp_edge.lp_line->pre->x;								
					if (old_pos > new_pos)						/* Falls gefunden, dann f"uge ein und gib list als Anfang	*/
					{									/* der Liste zur"uck.							*/
						new->suc = cur_edgelist;
						new->pre = cur_edgelist->pre;
						cur_edgelist->pre->suc = new;
						cur_edgelist->pre      = new;
						
						return list;
					}
				END_FOR_LPR_EDGELIST(list, cur_edgelist);
			}
		}
	}
	return NULL;
}

/***************************************************************************************
Function	: free_con_seq
Input		: lpr_Graph prod
Output	: void 
Description : L"oscht die gesamten Sequentialisierungen von Einbettungsregeln einer
		  Produktion, also sowohl lpr_IN_CONN_RELs wie lpr_OUT_CONN_RELs.
***************************************************************************************/
void free_con_seq(lpr_Graph prod)
{
	int index;

	if (prod != NULL)
	{
		for (index = 0; index < 4; index++)					/* Durchlaufe alle 4 Seiten der Produktion 	*/
		{
			free_lpr_edgelist(prod->in_con_seq[index]);		/* L"osche die Sequentialisierung der ein- 	*/
			prod->in_con_seq[index] = NULL;				/* laufenden Regeln dieser Seite und setze 	*/
			free_lpr_edgelist(prod->out_con_seq[index]);		/* Zeiger auf NULL. Ebenso f"ur die aus-		*/
			prod->out_con_seq[index] = NULL;				/* laufenden Regeln.					*/
		}
	}
}		

/***************************************************************************************
Function	: compute_conrel_seq	
Input		: lpr_Graph prod
Output	: void
Description : Richtet die Sequentialisierungen von ein- und auslaufenden Einbettungsre-
		  geln ein, indem es alle durchl"auft und in die entsprechende Liste einf"ugt.
***************************************************************************************/
void compute_conrel_seq(lpr_Graph prod)
{
	lpr_Edgelist 	cur_edgelist, new_edgelist;
	int 		dir, side;
	Edge		layout_edge;

	if (prod != NULL)
	{
		FOR_LPR_IN_CONN_REL(prod->IN_embeddings, cur_edgelist)					/* Durchlaufe die IN-Conn-rels		*/
			layout_edge = (get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge))->edge;	/* Hole entspr. Graphed-edge			*/
			dir  = first_dir( layout_edge );								/* Berechne Richtung				*/
			side = (dir + 2) % 4;										/* und Seite					*/
		
			new_edgelist 	= create_lpr_edgelist_with_edge(cur_edgelist->edge);		/* Erzeuge Kopie der IN-Conn-rel und f"uge*/
			prod->in_con_seq[side] = insert_edgelist_in_seq( prod->in_con_seq[side], new_edgelist, dir, prod,0);	/* sortiert ein	*/
		END_FOR_LPR_IN_CONN_REL(prod->IN_embeddings, cur_edgelist);

		FOR_LPR_OUT_CONN_REL(prod->OUT_embeddings, cur_edgelist)					/* Ebenso f"ur die lpr_OUT_CONN_RELs 	*/
			layout_edge = (get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge))->edge;	
			dir  = last_dir( layout_edge );						
			side = dir;								
		
			new_edgelist 	= create_lpr_edgelist_with_edge(cur_edgelist->edge);	
			prod->out_con_seq[side] = insert_edgelist_in_seq( prod->out_con_seq[side], new_edgelist, dir, prod,1);
		END_FOR_LPR_IN_CONN_REL(prod->OUT_embeddings, cur_edgelist);
	}		
}









/******************************************************************************/
/*													*/
/*	Es folgt eine der Hauptfunktionen aus diesem Abschnitt. Sie berechnet	*/
/*	die Sequentialisierung von Kanten.							*/
/*													*/
/******************************************************************************/

/***************************************************************************************
Function	: compute_production_edge_seq	
Input		: lpr_Graph prod
Output	: void
Description : Entspricht der Funktion compute_production_edge_sequentialization aus der
		  Theorie. Ihre Aufgabe ist das Setzen des pes_array's in lpr_edge.
***************************************************************************************/
void compute_production_edge_seq(lpr_Graph prod)
{
	lpr_Nodelist 	cur_nodelist;
	lpr_Edgelist	cur_edgelist, new_edgelist, pes, cur_edgelist1, cur_edgelist2;
	int			segments, index, con_segments, side;
	lpr_Iso_edge	iso_edge, con_edge;
	lpr_Edge		track_edge;
	lpr_Track_ass_des track_ass;
	lpr_Graph		son_prod;
	lpr_Track_sharing ts;


	FOR_LPR_NODELIST( prod->nodes, cur_nodelist)											/* Berechne pes von Kanten mit 	*/
		FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist)							/* terminalen Knoten.			*/
			if ((cur_edgelist->edge->edge_type == lpr_RHS_EDGE) && (cur_edgelist->edge->source->applied_production == NULL) &&
			    (cur_edgelist->edge->target->applied_production == NULL))
			{
				segments = (get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge))->bends + 1;		/* Wieviele Segmente im optimalen 	*/
				cur_edgelist->edge->pes_array = (lpr_Edgelist *) mycalloc(segments, sizeof(lpr_Edgelist));/* Layout? Reserviere pes-Speicher	*/
				for (index = 0; index < segments; index++)								/* Trage f"ur jedes Segment der 	*/
				{															/* Kante die Kante selbst im		*/					
					new_edgelist = create_lpr_edgelist_with_edge(cur_edgelist->edge);				/* pes_array ein.				*/
					cur_edgelist->edge->pes_array[index] = new_edgelist;
				}
			}
		END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist);
	END_FOR_LPR_NODELIST( prod->nodes, cur_nodelist);


	FOR_LPR_IN_CONN_REL( prod->IN_embeddings, cur_edgelist )									/* Berechne pes f"ur Einbettungsre-	*/
		if (cur_edgelist->edge->target->applied_production == NULL)								/* auf deren Ergebnis nichts ange-	*/
		{																	/* wandt wurde.				*/
			segments = (get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge))->bends + 1;			/* Ebenso wie vorher.			*/
			cur_edgelist->edge->pes_array = (lpr_Edgelist *) mycalloc(segments, sizeof(lpr_Edgelist));
			for (index = 0; index < segments; index++)
				if (cur_edgelist->edge->EH != NULL)										/* pes ist hier eine beliebige Se-	*/
					cur_edgelist->edge->pes_array[index] = copy_lpr_edgelist(cur_edgelist->edge->EH->edges); /*quentialisierung der EH. Der */
				else															/* Einfachheit halber wird hier eine*/
					cur_edgelist->edge->pes_array[index] = NULL;							/* Kopie der EH in pes abgelegt.	*/
		}
	END_FOR_LPR_IN_CONN_REL( prod->IN_embeddings, cur_edgelist );
	FOR_LPR_OUT_CONN_REL( prod->OUT_embeddings, cur_edgelist )									/* Das gleiche Spiel mit den auslau-*/
		if (cur_edgelist->edge->source->applied_production == NULL)								/* fenden Einbettungsregeln.		*/						
		{																	
			segments = (get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge))->bends + 1;				
			cur_edgelist->edge->pes_array = (lpr_Edgelist *) mycalloc(segments, sizeof(lpr_Edgelist));
			for (index = 0; index < segments; index++)
				if (cur_edgelist->edge->EH != NULL)
					cur_edgelist->edge->pes_array[index] = copy_lpr_edgelist(cur_edgelist->edge->EH->edges);
				else
					cur_edgelist->edge->pes_array[index] = NULL;
		}
	END_FOR_LPR_OUT_CONN_REL( prod->OUT_embeddings, cur_edgelist );


	FOR_LPR_NODELIST( prod->nodes, cur_nodelist )											/* Nun zu Einbettungsregeln auf 	*/
		if ((son_prod = cur_nodelist->node->applied_production) != NULL)							/* deren Ergebnis wieder eine Regel */
		{																	/* angewandt wurde.			*/
			
			
			FOR_LPR_EDGELIST(cur_nodelist->node->target_edges, cur_edgelist)							/* Durchlaufe also alle einlaufenden*/
				iso_edge = get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge);					/* Kanten.					*/
				segments = iso_edge->bends + 1;
				cur_edgelist->edge->pes_array = (lpr_Edgelist *) mycalloc(segments, sizeof(lpr_Edgelist));/* Reserviere entspr. den Segmenten */
																			/* Speicher f"ur pes			*/
				for( side = 0; side < 4; side++)										/* und behandle alle Seiten.		*/
				{
					if (last_dir(iso_edge->edge) == (side+2)%4 )							/* L"auft diese Kante auf dieser 	*/
					{														/* Seite ein?				*/		
						cur_edgelist->edge->pes_array[segments - 1] = NULL;					/* Initialisiere die pes des letzten*/
						for (index = son_prod->tn[lower_neighbor_dir(side)] - 1; index >= 0; index--)	/* Segments	und durchlaufe die 	*/
						{													/* Tracks des lower_neighbor(side)  */
							ts = get_track_sharing(son_prod, lower_neighbor_dir(side), index + 1);	/* Hole das Track-Sharing des Tracks*/
							track_edge = ts->edge1;									/* und daraus die richtige Kante	*/
							if ((side == U_side) || (side == R_side))
								if (ts->edge2 != NULL) track_edge = ts->edge2;
							if (track_edge->last->edge_type == IN_CONN_REL)					/* Ist das eine einlaufende Kante? 	*/
							{
								track_ass = get_track_ass_des(track_edge, son_prod);			/* und stimmt ihre Richtung im ta?	*/
								if (track_ass->td1 == (side+2)%4)
								{	
									/* TH 23/11/93 */ /* ACHTUNG : Es muss auch noch der Kantenlabel verglichen werden */
									if ( !strcmp( track_edge->source_label, cur_edgelist->edge->source_label ) )
									{ 
									pes = cur_edgelist->edge->pes_array[segments - 1];		/* dann nimm sie in pes auf.		*/
									new_edgelist = create_lpr_edgelist_with_edge( track_edge );
									cur_edgelist->edge->pes_array[segments - 1]  = add_edgelist_to_lpr_edgelist( pes, new_edgelist);
									} 
								}
							}
						}
						FOR_LPR_EDGELIST( son_prod->in_con_seq[side], cur_edgelist1)			/* Betrachte jetzt die Regeln dieser*/
							FOR_LPR_EDGELIST( cur_edgelist1->edge->pes_array[0], cur_edgelist2)	/* Seite, und durchlaufe deren pes 	*/
								track_ass = get_track_ass_des(cur_edgelist2->edge, son_prod);	/* des ersten Segments.			*/
								if ( track_ass->ta_type == 0)							/* L"auft die Kante gerade durch?	*/
								{
									/* TH 23/11/93 */
									if ( !strcmp( cur_edgelist1->edge->source_label, cur_edgelist->edge->source_label ) )
									{ 
									pes = cur_edgelist->edge->pes_array[segments - 1];		/* dann nimm sie in pes auf.		*/
									new_edgelist = create_lpr_edgelist_with_edge( cur_edgelist2->edge );
									cur_edgelist->edge->pes_array[segments - 1] =  add_edgelist_to_lpr_edgelist(pes, new_edgelist);
									} 
								}
							END_FOR_LPR_EDGELIST( cur_edgelist1->edge->pes_array[0], cur_edgelist2);
						END_FOR_LPR_EDGELIST( son_prod->in_con_seq[side], cur_edgelist1)
						for (index = 0; index < son_prod->tn[higher_neighbor_dir(side)] ; index++)	/* Ebenso wie oben, doch jetzt f"ur	*/
						{													/* den higher_neighbor(side).		*/
							ts = get_track_sharing(son_prod, higher_neighbor_dir(side), index + 1);
							track_edge = ts->edge1;
							if ((side == U_side) || (side == R_side))
								if (ts->edge2 != NULL) track_edge = ts->edge2;
							if (track_edge->last->edge_type == IN_CONN_REL)
							{
								track_ass = get_track_ass_des(track_edge, son_prod);
								if (track_ass->td1 == (side+2)%4)
								{
									/* TH 23/11/93 */
									if ( !strcmp( track_edge->source_label, cur_edgelist->edge->source_label ) )
									{ 
									pes = cur_edgelist->edge->pes_array[segments - 1];
									new_edgelist = create_lpr_edgelist_with_edge( track_edge );
									cur_edgelist->edge->pes_array[segments - 1]  = add_edgelist_to_lpr_edgelist( pes, new_edgelist);
									} 
								}
							}
						}
					}
				}

				if (cur_edgelist->edge->EH != NULL)										/* F"ur die restlichen Segmente	*/
					for(index = 0; index < segments - 1; index++)							/* EH.					*/
						cur_edgelist->edge->pes_array[index] = copy_lpr_edgelist(cur_edgelist->edge->EH->edges);

			END_FOR_LPR_EDGELIST( cur_nodelist->node->target_edges, cur_edgelist);


			FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist)
				iso_edge = get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge);
				segments = iso_edge->bends + 1;
				cur_edgelist->edge->pes_array = (lpr_Edgelist *) mycalloc(segments, sizeof(lpr_Edgelist));

				for (side = 0; side < 4; side++)
				{
					if (first_dir(iso_edge->edge) == side)	
					{
						cur_edgelist->edge->pes_array[0] = NULL;
						for (index = son_prod->tn[lower_neighbor_dir(side)]-1; index >= 0; index--)
						{
							ts = get_track_sharing(son_prod, lower_neighbor_dir(side), index + 1);
							track_edge = ts->edge1;
							if ((side == U_side) || (side == R_side))
								if (ts->edge2 != NULL)
									track_edge = ts->edge2;
							if (track_edge->last->edge_type == OUT_CONN_REL)
							{
								track_ass = get_track_ass_des(track_edge, son_prod);
								if ( ((track_ass->td1 == side) && (track_ass->ta_type <= 2)) ||
								     ((track_ass->td2 == side) && (track_ass->ta_type >  2)))
								{
									/* TH 23/11/93 */
									if ( !strcmp( track_edge->target_label, cur_edgelist->edge->target_label ) )
									{ 
									pes = cur_edgelist->edge->pes_array[0];
									new_edgelist = create_lpr_edgelist_with_edge( track_edge );
									cur_edgelist->edge->pes_array[0]  = add_edgelist_to_lpr_edgelist( pes, new_edgelist);
									} 
								}
							}
						}
						FOR_LPR_EDGELIST( son_prod->out_con_seq[side], cur_edgelist1)
							con_edge = get_optimal_edge_of_lpr_edge(son_prod,cur_edgelist1->edge);
							con_segments = con_edge->bends + 1;
							FOR_LPR_EDGELIST( cur_edgelist1->edge->pes_array[con_segments - 1], cur_edgelist2)
								track_ass = get_track_ass_des(cur_edgelist2->edge, son_prod);
								if ( track_ass->ta_type == 0)
								{
									/* TH 23/11/93 */
									if ( !strcmp( cur_edgelist1->edge->target_label, cur_edgelist->edge->target_label ) )
									{
									pes = cur_edgelist->edge->pes_array[0];
									new_edgelist = create_lpr_edgelist_with_edge( cur_edgelist2->edge );
									cur_edgelist->edge->pes_array[0] =  add_edgelist_to_lpr_edgelist(pes, new_edgelist);
									}
								}
							END_FOR_LPR_EDGELIST( cur_edgelist1->edge->pes_array[con_segments - 1], cur_edgelist2);
						END_FOR_LPR_EDGELIST( son_prod->out_con_seq[side], cur_edgelist1)
						for (index = 0; index < son_prod->tn[higher_neighbor_dir(side)] ; index++)
						{
							ts = get_track_sharing(son_prod, higher_neighbor_dir(side), index + 1);
							track_edge = ts->edge1;
							if ((side == U_side) || (side == R_side))
								if (ts->edge2 != NULL)
									track_edge = ts->edge2;
							if (track_edge->last->edge_type == OUT_CONN_REL)
							{
								track_ass = get_track_ass_des(track_edge, son_prod);
								if ( ((track_ass->td1 == side) && (track_ass->ta_type <= 2)) ||
								     ((track_ass->td2 == side) && (track_ass->ta_type >  2)) )
								{
									/* TH 23/11/93 */
									if ( !strcmp( track_edge->target_label, cur_edgelist->edge->target_label ) )
									{ 
									pes = cur_edgelist->edge->pes_array[0];
									new_edgelist = create_lpr_edgelist_with_edge( track_edge );
									cur_edgelist->edge->pes_array[0]  = add_edgelist_to_lpr_edgelist( pes, new_edgelist);
									} 
								}
							}
						}
					}
				}

				if (cur_edgelist->edge->EH != NULL)
					for(index = 1; index < segments; index++)
						cur_edgelist->edge->pes_array[index] = copy_lpr_edgelist(cur_edgelist->edge->EH->edges);

			END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist);
		}
	END_FOR_LPR_NODELIST( prod->nodes, cur_nodelist );
}							






/******************************************************************************/
/*													*/
/*	Es folgt die zweite wichtige Funktion aus diesem Abschnitt.	Sie berech-	*/
/*	net die ta_td einer Kante und das ts_array und die tn einer Produktion.	*/
/*													*/
/******************************************************************************/

/***************************************************************************************
Function	: compute_track_ass_des_seq_nr
Input		: lpr_Node  node, lpr_Graph father
Output	: void
Description : Entspricht der Funktion compute_track_assignment_description_sequentiali-
		  zation_and_number aus der Theorie. 
***************************************************************************************/
void compute_track_ass_des_seq_nr(lpr_Node node, lpr_Graph father)
{
	lpr_Edgelist 	full_tracks[4], semi_tracks[4], lower_semi_tracks[4], higher_semi_tracks[4], 
				in_cons, out_cons, cons, cur_edgelist, cur_edgelist1, cur_edgelist2, cur_edgelist3,
				fulltrack_edge, semitrack_edge, higher_semitrack_edge, lower_semitrack_edge;
	lpr_Edge		con_c;
	int			side, d, d_strich, d_2strich, nr, zufall, edge_dir, segments;
	lpr_Track_ass_des new_ta_td, track_ass;
	lpr_Track_sharing new_ts;
	lpr_Graph 		prod = node->applied_production;

	for (side = 0; side < 4; side++)												/* Initialisiere die full_tracks	*/
		full_tracks[side] = NULL;												/* jeder Seite.				*/
	in_cons  = copy_lpr_edgelist( prod->IN_embeddings );									/* Da alle Regeln durchlaufen werden*/
	out_cons = copy_lpr_edgelist( prod->OUT_embeddings );									/* wird zur k"urzeren Impl. eine Ko-*/
	cons	 = add_edgelist_to_lpr_edgelist(in_cons, out_cons);								/* pie von lpr_IN- und OUT_CONN_RELs*/
																		/* erzeugt die zu einer Liste ver-	*/
	FOR_LPR_EDGELIST( cons, cur_edgelist1 )											/* kettet werden.	Durchlaufe die Li-*/
																		/* ste.					*/
		if (cur_edgelist1->edge->generated_edges != NULL)
		{
			FOR_LPR_EDGELIST( cur_edgelist1->edge->generated_edges->edges, cur_edgelist2 )		/* Durchlaufe alles in L(c')		*/
				con_c = cur_edgelist2->edge->pred->last;								/* Bestimme last(pred(e'))		*/

				if (cur_edgelist2->edge->EH != NULL)
				{
					FOR_LPR_EDGELIST(cur_edgelist2->edge->EH->edges, cur_edgelist3 )			/* und durchlaufe EH(e')		*/
						if (con_c->edge_type == lpr_IN_CONN_REL)						/* Bestimme die Richtung von c in 	*/
							d = last_dir(get_optimal_edge_of_lpr_edge(father,con_c)->edge);	/* Abh"angigkeit vom Typ der Kante.	*/
						if (con_c->edge_type == lpr_OUT_CONN_REL)
							d = first_dir(get_optimal_edge_of_lpr_edge(father,con_c)->edge);
						if (con_c->edge_type == lpr_RHS_EDGE)
						{
							if (con_c->target == node)
								d = last_dir(get_optimal_edge_of_lpr_edge(father,con_c)->edge);
							else
								d = first_dir(get_optimal_edge_of_lpr_edge(father,con_c)->edge);
						}
				
						if (cur_edgelist1->edge->edge_type == lpr_IN_CONN_REL)			/* Bestimme ebenso die c'-Richtung 	*/
							d_strich = first_dir(get_optimal_edge_of_lpr_edge(prod,cur_edgelist1->edge)->edge);
						else
							d_strich = last_dir(get_optimal_edge_of_lpr_edge(prod,cur_edgelist1->edge)->edge);
	
						new_ta_td = create_lpr_track_ass_des();						/* Erzeuge ein neues ta_td.		*/
						new_ta_td->node    = node;
	
						if (d == d_strich)									/* und setzte es in Abh"angigkeit 	*/
							new_ta_td->ta_type = 0;								/* von den Richtungen und Typen.	*/
						else
						{
							if (neighbor_dirs(d, d_strich))
							{
								if ( (con_c->edge_type == lpr_IN_CONN_REL) ||
							     	((con_c->edge_type == lpr_RHS_EDGE) && (con_c->target == node)) )
									if ((d+1) % 4 == d_strich)
										new_ta_td->ta_type = 1;
									else
										new_ta_td->ta_type = 2;
								else
									if ((d_strich+1) % 4 == d)	
										new_ta_td->ta_type = 1;
									else
										new_ta_td->ta_type = 2;
								new_ta_td->td1 = d;
							}
							else											/* in diesem Fall sind die Richtun-	*/									
							{											/* gen also entgegengesetzt.		*/
								if ( (con_c->edge_type == lpr_IN_CONN_REL) ||
								     ((con_c->edge_type == lpr_RHS_EDGE) && (con_c->target == node)) )
								{
									if ((zufall = rand()&1) == 0)
									{
										d_2strich = (d+1)%4;
										new_ta_td->ta_type = 3;
									}
									else
									{
										d_2strich = (d+3)%4;
										new_ta_td->ta_type = 4;
									}
									new_ta_td->td1 = d;
									new_ta_td->td2 = d_2strich;
									fulltrack_edge = create_lpr_edgelist_with_edge(cur_edgelist3->edge);
									side = (d_2strich + 2)%4;
									full_tracks[side] = add_edgelist_to_lpr_edgelist(full_tracks[side],fulltrack_edge);
								}
								else
								{
									if ((zufall = rand()&1) == 0)
									{
										d_2strich = (d+3)%4;
										new_ta_td->ta_type = 3;
									}
									else
									{
										d_2strich = (d+1)%4;
										new_ta_td->ta_type = 4;
									}	
									new_ta_td->td1 = d_2strich;
									new_ta_td->td2 = d;
									fulltrack_edge = create_lpr_edgelist_with_edge(cur_edgelist3->edge);
									side = d_2strich;
									full_tracks[side] = add_edgelist_to_lpr_edgelist(full_tracks[side],fulltrack_edge);
								}
							}
						}												/* H"ange die neue ta_td in die Liste ein	*/
						cur_edgelist3->edge->ta_td = add_ta_td_to_ta_td_list(cur_edgelist3->edge->ta_td, new_ta_td);
					END_FOR_LPR_EDGELIST(cur_edgelist2->edge->EH->edges, cur_edgelist3 );
				}
			END_FOR_LPR_EDGELIST( cur_edgelist1->edge->generated_edges->edges, cur_edgelist2 );
		}	
	END_FOR_LPR_EDGELIST( cons, cur_edgelist1 );

	for (side = 0; side < 4; side++)												/* Berechne jetzt die restl. Infos	*/
	{
		semi_tracks[side] 	 = NULL;											/* Initialisiere.				*/
		lower_semi_tracks[side]  = NULL;
		higher_semi_tracks[side] = NULL;

		FOR_LPR_EDGELIST(prod->in_con_seq[side], cur_edgelist)							/* Durchlaufe die einlaufenden Re-	*/
			FOR_LPR_EDGELIST(cur_edgelist->edge->pes_array[0], cur_edgelist1)					/* geln dieser Seite.			*/
				if (get_track_ass_des( cur_edgelist1->edge, prod )->ta_type > 0)				/* Falls der ta_type > 0 ist, ver-	*/
				{														/* mind. 1 Segment in einem Track.	*/
					semitrack_edge = create_lpr_edgelist_with_edge(cur_edgelist1->edge);		/* Hole jetzt die richtige Kante die*/
					semi_tracks[side] = add_edgelist_to_lpr_edgelist( semi_tracks[side], semitrack_edge);/* zu den semi_tracks geh"ort*/
					track_ass = get_track_ass_des(cur_edgelist1->edge, prod);
					if (track_ass->ta_type > 2)
						edge_dir = track_ass->td2;
					else
						edge_dir = track_ass->td1;

					if ((edge_dir == R_dir) || (edge_dir == U_dir))						/* und f"uge je nach Richtung in 	*/
					{													/* lower_ oder higher_semi_tracks 	*/
						lower_semitrack_edge = create_lpr_edgelist_with_edge(cur_edgelist1->edge);	/* ein.				*/
						lower_semi_tracks[side] = add_edgelist_to_lpr_edgelist( lower_semi_tracks[side], lower_semitrack_edge);
					}
					else
					{
						higher_semitrack_edge = create_lpr_edgelist_with_edge(cur_edgelist1->edge);
						higher_semi_tracks[side] = add_edgelist_to_lpr_edgelist( higher_semi_tracks[side], higher_semitrack_edge);
					}
				}
			END_FOR_LPR_EDGELIST(cur_edgelist->edge->pes_array[0], cur_edgelist1);
		END_FOR_LPR_EDGELIST(prod->in_con_seq[side], cur_edgelist);

		FOR_LPR_EDGELIST(prod->out_con_seq[side], cur_edgelist)							/* Wiederhole das ganze f"ur auslau-*/
			segments = get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge)->bends + 1;			/* fende Einbettungsregeln.		*/
			FOR_LPR_EDGELIST(cur_edgelist->edge->pes_array[segments - 1], cur_edgelist1)
				if (get_track_ass_des( cur_edgelist1->edge, prod )->ta_type > 0)
				{
					semitrack_edge = create_lpr_edgelist_with_edge(cur_edgelist1->edge);
					semi_tracks[side] = add_edgelist_to_lpr_edgelist( semi_tracks[side], semitrack_edge);
					edge_dir = get_track_ass_des(cur_edgelist1->edge, prod)->td1;
					if ((edge_dir == L_dir) || (edge_dir == D_dir))
					{
						lower_semitrack_edge = create_lpr_edgelist_with_edge(cur_edgelist1->edge);
						lower_semi_tracks[side] = add_edgelist_to_lpr_edgelist( lower_semi_tracks[side], lower_semitrack_edge);
					}
					else
					{
						higher_semitrack_edge = create_lpr_edgelist_with_edge(cur_edgelist1->edge);
						higher_semi_tracks[side] = add_edgelist_to_lpr_edgelist( higher_semi_tracks[side], higher_semitrack_edge);
					}
				}
			END_FOR_LPR_EDGELIST(cur_edgelist->edge->pes_array[segments - 1], cur_edgelist1);
		END_FOR_LPR_EDGELIST(prod->out_con_seq[side], cur_edgelist);

		nr = 0;															/* Mit diesen Listen kann nun die	*/
		while (semi_tracks[side] != NULL)											/* tn berechnet werden.			*/
		{
			semitrack_edge = create_lpr_edgelist_with_edge(semi_tracks[side]->edge);
			if ((higher_semi_tracks[side] != NULL) && (semitrack_edge->edge  == higher_semi_tracks[side]->edge )) /* Liegt in semitrack 	*/
			{															/* nur eine higher_semi_track-Kante?*/
				nr++;
				new_ts = create_lpr_track_sharing();								/* Erzeuge ein neues Track-Sharing	*/
				new_ts->edge1 = semitrack_edge->edge;								/* und speichere Kante darin ab.	*/
				new_ts->track_number = nr;								
				prod->ts_array[side] = add_ts_to_ts_list( prod->ts_array[side], new_ts);		/* H"ange es an.				*/
				track_ass = get_track_ass_des(semitrack_edge->edge, prod);					/* Setze jetzt die tn in ta_td	*/
				if (track_ass->ta_type <= 2) 
					track_ass->tn1 = nr;
				else
				{
					if (semitrack_edge->edge->last->edge_type == lpr_IN_CONN_REL)
						track_ass->tn2 = nr;
					else
						track_ass->tn1 = nr;
				}
				semi_tracks[side] 	 = remove_edgelist_from_lpr_edgelist(semi_tracks[side], semitrack_edge);/* und schmeiss die 	*/
				higher_semi_tracks[side] = remove_edgelist_from_lpr_edgelist(higher_semi_tracks[side], semitrack_edge); /* Kante aus der*/
			}															/* Liste					*/	
			else															/* sonst, ist diese Kante einer low	*/					
			{															/* erzeuge also entspr. Track-Shar. */			
				nr++;
				new_ts = create_lpr_track_sharing();
				new_ts->edge1 = semitrack_edge->edge;
				new_ts->track_number = nr;
				prod->ts_array[side] = add_ts_to_ts_list( prod->ts_array[side], new_ts);
				track_ass = get_track_ass_des(semitrack_edge->edge, prod);
				if (track_ass->ta_type <= 2) 
					track_ass->tn1 = nr;
				else
				{
					if (semitrack_edge->edge->last->edge_type == lpr_IN_CONN_REL)
						track_ass->tn2 = nr;
					else
						track_ass->tn1 = nr;
				}
				semi_tracks[side] 	= remove_edgelist_from_lpr_edgelist(semi_tracks[side], semitrack_edge);	 	
				lower_semi_tracks[side] = remove_edgelist_from_lpr_edgelist(lower_semi_tracks[side], semitrack_edge);
				if (higher_semi_tracks[side] != NULL)								/* und pr"ufe ob noch higher_semi-	*/
				{														/* tracks existieren.			*/
					semitrack_edge = higher_semi_tracks[side];
					new_ts->edge2 = semitrack_edge->edge;							/* falls ja erg"anze das neue ts	*/
					track_ass = get_track_ass_des(semitrack_edge->edge, prod);				/* und setze die tn			*/
					if (track_ass->ta_type <= 2) 
						track_ass->tn1 = nr;
					else
					{
						if (semitrack_edge->edge->last->edge_type == lpr_IN_CONN_REL)
							track_ass->tn2 = nr;
					else
							track_ass->tn1 = nr;
					}
					semi_tracks[side] = remove_edgelist_from_lpr_edgelist(semi_tracks[side], semitrack_edge);
					higher_semi_tracks[side] = remove_edgelist_from_lpr_edgelist(higher_semi_tracks[side], semitrack_edge);
				}
			}
		}

		FOR_LPR_EDGELIST(full_tracks[side], cur_edgelist)								/* Es fehlen nur noch die fulltracks*/
			nr++;
			new_ts = create_lpr_track_sharing();									/* Weise jeder einen eigenen Track 	*/
			new_ts->edge1 = cur_edgelist->edge;										/* zu.					*/
			new_ts->track_number = nr;
			prod->ts_array[side] = add_ts_to_ts_list( prod->ts_array[side], new_ts);
			track_ass = get_track_ass_des(cur_edgelist->edge, prod);
			if (cur_edgelist->edge->last->edge_type == lpr_IN_CONN_REL)						/* und setze tn in Abh"angigkeit vom*/
				track_ass->tn1 = nr;											/* Typ der Regel				*/
			else
				track_ass->tn2 = nr;
		END_FOR_LPR_EDGELIST(full_tracks[side], cur_edgelist);

		prod->tn[side] = nr;													/* Setze schliesslich die tn's der 	*/
	}																	/* Produktion.				*/
}



/***************************************************************************************
Function	: free_track_info
Input		: lpr_Graph prod
Output	: void
Description : Gibt die ganze berechnete Information wieder frei, um im n"achsten Ablei-
		  tungsschritt wieder neu anfangen zu k"onnen.
***************************************************************************************/
void free_track_info(lpr_Graph prod)
{
	int 		segments, index;
	lpr_Nodelist	cur_nodelist;
	lpr_Edgelist	cur_edgelist, cur_edgelist1;

	free_lpr_track_sharing(prod->ts_array[0]);							/* L"osche alle ts-Listen von prod	*/
	free_lpr_track_sharing(prod->ts_array[1]);
	free_lpr_track_sharing(prod->ts_array[2]);
	free_lpr_track_sharing(prod->ts_array[3]);
	prod->ts_array[0] = prod->ts_array[1] = prod->ts_array[2] = prod->ts_array[3] = NULL;
	prod->tn[0] = prod->tn[1] = prod->tn[2] = prod->tn[3] = 0;					/* Setze die tn's zur"uck		*/

	FOR_LPR_EDGELIST( prod->IN_embeddings, cur_edgelist)						/* L"osche die ta_td's			*/
		if (cur_edgelist->edge->pes_array != NULL)
		{
			FOR_LPR_EDGELIST(cur_edgelist->edge->pes_array[0], cur_edgelist1)
				free_lpr_track_ass_des(cur_edgelist1->edge->ta_td);
				cur_edgelist1->edge->ta_td = NULL;
			END_FOR_LPR_EDGELIST(cur_edgelist->edge->pes_array[0], cur_edgelist1);
		}
	END_FOR_LPR_EDGELIST( prod->IN_embeddings, cur_edgelist);

	FOR_LPR_EDGELIST( prod->OUT_embeddings, cur_edgelist)
		segments = get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge)->bends + 1;
		if (cur_edgelist->edge->pes_array != NULL)
		{
			FOR_LPR_EDGELIST(cur_edgelist->edge->pes_array[segments-1], cur_edgelist1)
				free_lpr_track_ass_des(cur_edgelist1->edge->ta_td);
				cur_edgelist1->edge->ta_td = NULL;
			END_FOR_LPR_EDGELIST(cur_edgelist->edge->pes_array[segments-1], cur_edgelist1);
			
		}
	END_FOR_LPR_EDGELIST( prod->OUT_embeddings, cur_edgelist);

	FOR_LPR_NODELIST(prod->nodes, cur_nodelist)							/* L"osche die pes der RHS_EDGES	*/
		FOR_LPR_EDGELIST(cur_nodelist->node->source_edges, cur_edgelist)
			segments = get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge)->bends + 1;
			if (cur_edgelist->edge->pes_array != NULL)
			{
				for (index = 0; index < segments; index++)
					free_lpr_edgelist(cur_edgelist->edge->pes_array[index]);
				free(cur_edgelist->edge->pes_array);
				cur_edgelist->edge->pes_array = NULL;
			}	
		END_FOR_LPR_EDGELIST(cur_nodelist->node->source_edges, cur_edgelist);
	END_FOR_LPR_NODELIST(prod->nodes, cur_nodelist);

	FOR_LPR_EDGELIST(prod->IN_embeddings, cur_edgelist)						/* und die der Einbettungsregeln	*/
		segments = get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge)->bends + 1;
		if (cur_edgelist->edge->pes_array != NULL)
		{
			for (index = 0; index < segments; index++)
				free_lpr_edgelist(cur_edgelist->edge->pes_array[index]);
			free(cur_edgelist->edge->pes_array);
			cur_edgelist->edge->pes_array = NULL;
		}	
	END_FOR_LPR_EDGELIST(prod->IN_embeddings, cur_edgelist);

	FOR_LPR_EDGELIST(prod->OUT_embeddings, cur_edgelist)
		segments = get_optimal_edge_of_lpr_edge(prod,cur_edgelist->edge)->bends + 1;
		if (cur_edgelist->edge->pes_array != NULL)
		{
			for (index = 0; index < segments; index++)
				free_lpr_edgelist(cur_edgelist->edge->pes_array[index]);
			free(cur_edgelist->edge->pes_array);
			cur_edgelist->edge->pes_array = NULL;	
		}
	END_FOR_LPR_EDGELIST(prod->OUT_embeddings, cur_edgelist);
}

/***********************************************
	Noch eine Funktion f"ur Testausgaben.	
***********************************************/
void testprint_lrp(lpr_Node node)
{
	lpr_Graph 		prod = node->applied_production;
	lpr_Nodelist		cur_nodelist;
	lpr_Edgelist		cur_edgelist, cur_edgelist1;
	lpr_Track_ass_des	cur_ta_td;
	int			index;

	if (prod != NULL)
	{
 	 	printf("\r\n\r\n **************** Produktion %p ***************", prod);
		for (index = 0; index < 4; index++)
		{
			printf("\r\nTrack-Nr[%d] = %d", index, prod->tn[index]);
		}							
		FOR_LPR_NODELIST(prod->nodes, cur_nodelist)
			FOR_LPR_EDGELIST(cur_nodelist->node->source_edges, cur_edgelist)
				printf("\r\nKante: %s -> %s", cur_edgelist->edge->source_label, cur_edgelist->edge->target_label);
				FOR_LPR_TRACK_ASS_DES(cur_edgelist->edge->ta_td, cur_ta_td)
					printf("\r\n Produktion: %p -- td1: %d, tn1: %d -- td2: %d tn2: %d -- ta_type: %d", cur_ta_td->node->applied_production,
															    cur_ta_td->td1, cur_ta_td->tn1,
														            cur_ta_td->td2, cur_ta_td->tn2, 
															    cur_ta_td->ta_type);
				END_FOR_LPR_TRACK_ASS_DES(cur_edgelist->edge->ta_td, cur_ta_td);

			END_FOR_LPR_EDGELIST(cur_nodelist->node->source_edges, cur_edgelist);

		END_FOR_LPR_NODELIST(prod->nodes, cur_nodelist);
		FOR_LPR_EDGELIST( prod->IN_embeddings, cur_edgelist)
			if (cur_edgelist->edge->pes_array != NULL)
			{
				FOR_LPR_EDGELIST(cur_edgelist->edge->pes_array[0], cur_edgelist1)
					printf("\r\nKante: %s -> %s", cur_edgelist1->edge->source_label, cur_edgelist1->edge->target_label);
					FOR_LPR_TRACK_ASS_DES(cur_edgelist1->edge->ta_td, cur_ta_td)
					printf("\r\n Produktion: %p -- td1: %d, tn1: %d -- td2: %d tn2: %d -- ta_type: %d", cur_ta_td->node->applied_production,
															    cur_ta_td->td1, cur_ta_td->tn1,
												 			    cur_ta_td->td2, cur_ta_td->tn2,
 															    cur_ta_td->ta_type);
					END_FOR_LPR_TRACK_ASS_DES(cur_edgelist1->edge->ta_td, cur_ta_td);
				END_FOR_LPR_EDGELIST(cur_edgelist->edge->pes_array[0], cur_edgelist1);
			}
		END_FOR_LPR_EDGELIST( prod->IN_embeddings, cur_edgelist);

	
	
		FOR_LPR_NODELIST(prod->nodes, cur_nodelist)
			testprint_lrp ( cur_nodelist->node );
		END_FOR_LPR_NODELIST(prod->nodes, cur_nodelist);
	}
}

/******************************************************************************/
/*													*/
/*	Schliesslich die Hauptfunktion aus diesem Abschnitt, die den Bottom-Up	*/
/*	Durchlauf regelt und die Berechnung anst"osst.					*/
/*													*/
/******************************************************************************/

/******************************************************************************
Function	: compute_layout_restriction_par
Input		: lpr_Node node, lpr_Graph father
Output	: void
Description : Entspricht der Funktion compute_layout_restriction_parameters aus
		  der Theorie.
******************************************************************************/
void compute_layout_restriction_par(lpr_Node node, lpr_Graph father)
{
	lpr_Graph 	prod = node->applied_production;
	lpr_Nodelist	cur_nodelist;

	free_track_info(prod);					/* L"osche noch vorhandene Infos	*/
	free_con_seq(prod);					/* und Sequentialisierungen		*/
	compute_conrel_seq(prod);				/* Berechne Sequentialisierungen neu*/

	FOR_LPR_NODELIST(prod->nodes, cur_nodelist)	/* Bottom-Up-Durchlauf			*/
		if (cur_nodelist->node->applied_production != NULL)
			compute_layout_restriction_par(cur_nodelist->node, prod);
	END_FOR_LPR_NODELIST(prod->nodes, cur_nodelist);
	if (prod != NULL)
	{
		compute_production_edge_seq(prod); 		/* Berechne pes				*/
		compute_track_ass_des_seq_nr( node , father); /* Berechne ta_td, tn, ts		*/
	}
}
	
	
