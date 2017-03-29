#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lp_top_sort.h"

#include "lp_assign_edge.h"

/****************************************************************/
/*								*/
/*	modul:	lp_assign_edge.c				*/
/*								*/
/****************************************************************/

/****************************************************************/
/*								*/
/****************************************************************/

typedef enum
{
	IN_CONN,
	OUT_CONN
}	embedding_type;


/****************************************************************/
/*								*/
/*	define and initialize new coordinat 			*/
/*	(used in get_ord_side_number_of_imbedding)		*/
/*								*/
/****************************************************************/
typedef	struct coordinat
{
	Edge			edge;
	embedding_type		type;
	struct coordinat 	*next;
}  	
	*Coordinat;

/****************************************************************/

Coordinat 	new_coordinat(void)
{
	Coordinat 	new = (Coordinat ) mymalloc(sizeof( struct coordinat  ) );

	new->edge 	= NULL;
	new->next 	= NULL;	
	return( new );
}

/************************************************************************
function:	entry_point
input:	Coordinat cur_edge

output:	point of lp_line of edge, whitch is on border of production box
************************************************************************/

lp_Edgeline	entry_point(Coordinat cur_edge)
{
	if( cur_edge->type == IN_CONN ) return( cur_edge->edge->lp_edge.lp_line );
	if( cur_edge->type == OUT_CONN ) return( cur_edge->edge->lp_edge.lp_line->pre );
	return NULL; /* should not be reached */
}

/************************************************************************
function: add_coordinat
input:		
does:	create a sorted list , comparing coord. depends on side of cur_edge
************************************************************************/

Coordinat	add_coordinat(Coordinat list, Coordinat cur_edge)
{
	Coordinat	list_head = list;

	if (list == NULL)
		return( cur_edge );

	if (list->edge->lp_edge.side == 0)
	{
		if ( entry_point(list)->y < entry_point(cur_edge)->y ) /* append at top of list */
		{
			cur_edge->next = list;
			return( cur_edge );
		}
		while ( list->next != NULL )
		{
			if ( entry_point(list->next)->y < entry_point(cur_edge)->y ) /* append in middle of list */
			{
				cur_edge->next = list->next;
				list->next = cur_edge;
				return( list_head );
			}
			list = list->next;
		}
		list->next = cur_edge; /* append at end of list */
		return( list_head );
	}
		
	if (list->edge->lp_edge.side == 1)
	{
		if ( entry_point(list)->x > entry_point(cur_edge)->x ) /* append at top of list */
		{
			cur_edge->next = list;
			return( cur_edge );
		}
		while ( list->next != NULL )
		{
			if ( entry_point(list->next)->x > entry_point(cur_edge)->x ) /* append in middle of list */
			{
				cur_edge->next = list->next;
				list->next = cur_edge;
				return( list_head );
			}
			list = list->next;
		}
		list->next = cur_edge; /* append at end of list */
		return( list_head );
	}

	if (list->edge->lp_edge.side == 2)
	{
		if ( entry_point(list)->y > entry_point(cur_edge)->y ) /* append at top of list */
		{
			cur_edge->next = list;
			return( cur_edge );
		}
		while ( list->next != NULL )
		{
			if ( entry_point(list->next)->y > entry_point(cur_edge)->y ) /* append in middle of list */
			{
				cur_edge->next = list->next;
				list->next = cur_edge;
				return( list_head );
			}
			list = list->next;
		}
		list->next = cur_edge; /* append at end of list */
		return( list_head );
	}

	if (list->edge->lp_edge.side == 3)
	{
		if ( entry_point(list)->x < entry_point(cur_edge)->x ) /* append at top of list */
		{
			cur_edge->next = list;
			return( cur_edge );
		}
		while ( list->next != NULL )
		{
			if ( entry_point(list->next)->x < entry_point(cur_edge)->x ) /* append in middle of list */
			{
				cur_edge->next = list->next;
				list->next = cur_edge;
				return( list_head );
			}
			list = list->next;
		}
		list->next = cur_edge; /* append at end of list */
		return( list_head );
	}
	fprintf(stderr, "add_coordinat: undefined state\n");
	return NULL;
}


/************************************************************************
function: assign_ord
input:		
does:	 
************************************************************************/

int		assign_ord(Coordinat list)
{
	int 		a = 0;
	Coordinat	cur = list;

	while( cur != NULL )
	{	
		a++;
		cur->edge->lp_edge.lp_ord = a;
		cur = cur->next;
	}
	return(a);
}
 
/************************************************************************
function: free_coordinat
input:		
does:	 
************************************************************************/

void	free_coordinat(Coordinat list)
{
	Coordinat	to_delete = list;

	if ( list != NULL )
	{	
		while ( list->next != NULL )
		{
			list = list->next;
			free( to_delete );
			to_delete = list;
		}
		/* There was one element in list */
		free( to_delete );
	}
}
	
/************************************************************************
function: write_edge_side_a_ord
input:		
does:	 
************************************************************************/

/* void	write_edge_side_a_ord( p )
Graph	p;
{
	Edge		e;
	Group		cur_group,g;
	int		i, number_of_in_embeddings, number_of_out_embeddings;

	number_of_in_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_in);
	number_of_out_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_out);
	for( i=0; i<number_of_in_embeddings; i++) 
	{
		cur_group = p->gra.gra.nce1.embed_in[i].embed;
	      	for_group (cur_group, g) 
		{
			for_edge_sourcelist( g->node, e )
			{
				printf("%s with %s  in %s %d. edge, side %d",e->source->label.text,
				e->label.text,e->target->label.text,
				e->lp_edge.lp_ord, e->lp_edge.side);
				printf("\n");
			}
			end_for_edge_sourcelist( g->node, e );
	      	}
		end_for_group (cur_group, g);
	}
	for( i=0; i<number_of_out_embeddings; i++) 
	{
		cur_group = p->gra.gra.nce1.embed_out[i].embed;
	      	for_group (cur_group, g) 
		{
			for_edge_sourcelist( g->node, e )
			{
				printf("%s with %s  in %s %d. edge, side %d",e->source->label.text,
				e->label.text,e->target->label.text,
				e->lp_edge.lp_ord,e->lp_edge.side);
				printf("\n");
			}
			end_for_edge_sourcelist( g->node, e );
	      	}
		end_for_group (cur_group, g);
	}
} */

/************************************************************************
function: get_edge_side
input:	Edge e, left_hand_side of Production, max_ord m	
does:	 
************************************************************************/

void	assign_edge_side(Coordinat akt_coord, Node left_hand_side)
{
	Edge	e = akt_coord->edge;
	int	x1 = left_hand_side->x - left_hand_side->box.r_width / 2;
	int	x2 = left_hand_side->x + left_hand_side->box.r_width / 2;
	int	y1 = left_hand_side->y - left_hand_side->box.r_height / 2;
	int	y2 = left_hand_side->y + left_hand_side->box.r_height / 2;

	if (entry_point(akt_coord)->x == x1 )
	{
		e->lp_edge.side = 0;
	}
	if (entry_point(akt_coord)->y == y2 )
	{
		e->lp_edge.side = 1;
	}
	if (entry_point(akt_coord)->x == x2 )
	{
		e->lp_edge.side = 2;
	}
	if (entry_point(akt_coord)->y == y1 )
	{
		e->lp_edge.side = 3;
	}
}
/************************************************************************
function: get_ord_number_of_imbedding
input:		
does:	 
************************************************************************/

void	get_ord_side_number_of_imbedding(Graph p)
{
	Group		cur_group, g;
	int		i, number_of_in_embeddings, number_of_out_embeddings;
	Edge		e;
	Node		left_hand_side_node = p->gra.gra.nce1.left_side->node;
	Coordinat 		left = NULL;
	Coordinat 		upper = NULL;
	Coordinat 		right = NULL;
	Coordinat 		lower = NULL;
	Coordinat 		akt_coordinat;
	number_of_in_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_in);
	number_of_out_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_out);

	for( i=0; i<number_of_in_embeddings; i++) 
	{
		cur_group = p->gra.gra.nce1.embed_in[i].embed;
	      	for_group (cur_group, g) 
		{
  			for_edge_sourcelist( g->node, e)
			{
				akt_coordinat = new_coordinat();
				akt_coordinat->edge = e;
				akt_coordinat->type = IN_CONN;
				assign_edge_side(akt_coordinat, left_hand_side_node);
				if (e->lp_edge.side == 0) 
					left = add_coordinat(left, akt_coordinat);
				if (e->lp_edge.side == 1) 
					upper = add_coordinat(upper, akt_coordinat);
				if (e->lp_edge.side == 2) 
					right = add_coordinat(right, akt_coordinat);
				if (e->lp_edge.side == 3) 
					lower = add_coordinat(lower, akt_coordinat);
			}
			end_for_edge_sourcelist( g->node, e);	
  	 	}
		end_for_group (cur_group, g);
	}
	for( i=0; i<number_of_out_embeddings; i++) 
	{
		cur_group = p->gra.gra.nce1.embed_out[i].embed;
	      	for_group (cur_group, g) 
		{
  			for_edge_targetlist( g->node, e)
			{
				akt_coordinat  = new_coordinat();
				akt_coordinat->edge = e;
				akt_coordinat->type = OUT_CONN;
				assign_edge_side(akt_coordinat, left_hand_side_node);
				if (e->lp_edge.side == 0) 
					left = add_coordinat(left, akt_coordinat);
				if (e->lp_edge.side == 1) 
					upper = add_coordinat(upper, akt_coordinat);
				if (e->lp_edge.side == 2) 
					right = add_coordinat(right, akt_coordinat);
				if (e->lp_edge.side == 3) 
					lower = add_coordinat(lower, akt_coordinat);
			}
			end_for_edge_targetlist( g->node, e);	
  	 	}
		end_for_group (cur_group, g);
	} 

	p->gra.gra.nce1.lp_nce1_gragra.max_ord[L_side] = assign_ord( left );
	p->gra.gra.nce1.lp_nce1_gragra.max_ord[U_side] = assign_ord( upper );
	p->gra.gra.nce1.lp_nce1_gragra.max_ord[R_side] = assign_ord( right );
	p->gra.gra.nce1.lp_nce1_gragra.max_ord[D_side] = assign_ord( lower );

	free_coordinat ( left );
	free_coordinat ( upper );
	free_coordinat ( right );
	free_coordinat ( lower );
}
