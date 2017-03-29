#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lp_top_sort.h"

/****************************************************************/
/*								*/
/*	modul:	lp_top_sort.c					*/
/*								*/
/****************************************************************/

/************************************************************************
function:	new_top_sort_ref

	Create new top sort recording n

Output:	Pointer to n
************************************************************************/

top_sort_ref	new_top_sort_ref(void)
{
	top_sort_ref	new = (top_sort_ref) mymalloc( sizeof( struct top_sort_rec ) );

	new->next_x 	= NULL;
	new->next_y 	= NULL;
	new->first_x 	= new;
	new->first_y 	= new;
	new->iso	= NULL;
	new->type 	= LP_NODE;
	new->ref.node	= NULL;
	return( new );
}

/************************************************************************
function: 	write_production_sorted
Input:	production p

	write p->gra.gra.nce1.first_x and p->gra.gra.nce1.first_y
	on stdio
************************************************************************/

void	write_production_sorted(Graph p)
{
	top_sort_ref	liste = p->gra.gra.nce1.lp_nce1_gragra.first_x;

	printf("\n");
	printf("\n");
	while(liste != NULL)
	{
		if (liste->type == LP_NODE)
			printf(" %d *x*",liste->ref.node->x);
		else	printf(" %d *x*",liste->ref.lp_edgeline->x);
		liste = liste->next_x;
	}
	printf("\n");
	liste = p->gra.gra.nce1.lp_nce1_gragra.first_x;
	while(liste != NULL)
	{
/*		printf(" %d *fx*",liste->first_x); <- kein integer*/
		printf(" %d *fx*",liste->first_x->ref.node->x);
		liste = liste->next_x;
	}
	printf("\n");
	printf("\n");

	liste = p->gra.gra.nce1.lp_nce1_gragra.first_y;
	while(liste != NULL)
	{
		if (liste->type == LP_NODE)
			printf(" %d *y*",liste->ref.node->y);
		else	printf(" %d *y*",liste->ref.lp_edgeline->y);
		liste = liste->next_y;
	}
	printf("\n");
	liste = p->gra.gra.nce1.lp_nce1_gragra.first_y;
	while(liste != NULL)
	{
/*			printf(" %d *fy*",liste->first_y); <-kein integer */
			printf(" %d *fy*",liste->first_y->ref.node->y);
		liste = liste->next_y;
	}
	printf("\n");
}

/************************************************************************
function: get_int
input:	top_sort_ref elem, int a

output: iff a = 1 x-coordinat of elem->ref
	else y-coordinat of elem->ref
************************************************************************/

int 	get_int(top_sort_ref elem, int a)
{
	if (a == 1)
	{
		if (elem->type == LP_EDGELINE) 
			return(elem->ref.lp_edgeline->x);
		return(elem->ref.node->x);
	}
	if (elem->type == LP_EDGELINE) 
			return(elem->ref.lp_edgeline->y);
		return(elem->ref.node->y);
}

/************************************************************************
function: between
input:	top_sort_ref liste, elem, int a 

	if a = 1 comparing x-Coordinat , else y-coordinat

output:	TRUE iff. liste->ref <= e < l->next_x->ref
	FALSE otherwise

Garantie by calling function: It's only used when l->next != NULL
************************************************************************/

int	between(top_sort_ref liste, top_sort_ref elem, int a)
{
	if (a == 1)
	{
		if ( (get_int(liste, a) <= get_int(elem, a) ) && 
		     (get_int(liste->next_x, a) > get_int(elem, a) ) )
			return( TRUE );
		return( FALSE );
	}
	else
	{
		if ( (get_int(liste, a) <= get_int(elem, a) ) && 
		     (get_int(liste->next_y, a) > get_int(elem, a) ) )
			return( TRUE );
		return( FALSE );
	}

}
	
/************************************************************************
function: 	insert_tsr
input:	pointer on element ptr to insert, 
	top_sort_ref x_liste, y_liste, top_sort_rec_type kind

does:	insert ptr in x_liste and y_liste
************************************************************************/

void	insert_tsr(reference ptr, top_sort_ref *x_liste, top_sort_ref *y_liste, top_sort_rec_type kind)
{
	top_sort_ref	new_elem = new_top_sort_ref();
	top_sort_ref	cur_ptr	 = *x_liste;

	new_elem->type	= kind;
	new_elem->ref	= ptr;
		
	if ( *x_liste == NULL )
	{
		*x_liste = new_elem;
	}
	else
	{
		
		if ( get_int(new_elem, 1) < get_int(cur_ptr, 1) )
		{
			*x_liste = new_elem;
			new_elem->next_x = cur_ptr;
		}
		else
		{
			while( (cur_ptr->next_x != NULL) && (!between(cur_ptr, new_elem,1)) ) 
			{
				cur_ptr = cur_ptr->next_x;
			}
			
			new_elem->next_x = cur_ptr->next_x;
			cur_ptr->next_x = new_elem;

			if (get_int(cur_ptr,1) == get_int(new_elem, 1) )
				new_elem->first_x = cur_ptr->first_x;
			else 	new_elem->first_x = new_elem;
		}	
	}

	cur_ptr = *y_liste;

	if ( *y_liste == NULL )
	{
		*y_liste = new_elem;
	}
	else
	{
		
		if ( get_int(new_elem, 0) < get_int(cur_ptr, 0) )
		{
			*y_liste = new_elem;
			new_elem->next_y = cur_ptr;
		}
		else
		{
			while( (cur_ptr->next_y != NULL) && (!between(cur_ptr, new_elem,0)) ) 
			{
				cur_ptr = cur_ptr->next_y;
			}
				
			new_elem->next_y = cur_ptr->next_y;
			cur_ptr->next_y = new_elem;

			if (get_int(cur_ptr,0) == get_int(new_elem, 0) )
				new_elem->first_y = cur_ptr->first_y;
			else 	new_elem->first_y = new_elem;
		}
	}
}

/************************************************************************
function: 	topological_sorting
input:	graph p

	create p->gra.gra.nce1.first_x ,p->gra.gra.nce1.first_y 
************************************************************************/

void	topological_sorting(Graph p)
{
	Edge			e;
	int			i, number_of_in_embeddings;
	Group			g, cur_group;
	reference		ptr;
	lp_Edgeline		l;
	top_sort_ref		x_liste = NULL;
	top_sort_ref		y_liste = NULL;

	number_of_in_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_in);
	cur_group = p->gra.gra.nce1.right_side;

	/* executed for right_side and out_embeddings */
	for_group(cur_group, g)
	{
		ptr.node = g->node;
		insert_tsr(ptr,&x_liste,&y_liste,LP_NODE);
		for_edge_sourcelist(g->node, e)
		{
			for_lp_edgeline(e->lp_edge.lp_line,l)
			{
				ptr.lp_edgeline = l;
				insert_tsr(ptr,&x_liste,&y_liste,LP_EDGELINE);
			}
			end_for_lp_edgeline(e->lp_edge.lp_line,l);
		}
		end_for_edge_sourcelist(g->node, e);
	}
	end_for_group(cur_group, g);
	
	for( i=0; i<number_of_in_embeddings; i++) 
	{
		cur_group = p->gra.gra.nce1.embed_in[i].embed;
	      	for_group (cur_group, g) 
		{
			for_edge_sourcelist(g->node, e)
			{
				for_lp_edgeline(e->lp_edge.lp_line,l)
				{
					ptr.lp_edgeline = l;
					insert_tsr(ptr,&x_liste,&y_liste,LP_EDGELINE);
				}
				end_for_lp_edgeline(e->lp_edge.lp_line,l);
			}
			end_for_edge_sourcelist(g->node, e);
		}
		end_for_group(cur_group, g)
	}

	p->gra.gra.nce1.lp_nce1_gragra.first_x = x_liste;
	p->gra.gra.nce1.lp_nce1_gragra.first_y = y_liste;
}
