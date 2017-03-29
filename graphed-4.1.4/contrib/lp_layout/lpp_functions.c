#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lpp_functions.h"

/********************************************************************************/
/*										*/
/*	Funktionen auf Datenstrukturen fuer Ableitung mit Parsing		*/
/*										*/
/********************************************************************************/

/********************************************************************************/
/*										*/
/*	lpp_Parsing_element							*/
/*										*/
/********************************************************************************/

/*********************************************************************************
function -->	new_parsing_element

	Allocate memory space for a new parsing_element pe

Output:	Pointer to pe
*********************************************************************************/

lpp_Parsing_element	new_parsing_element(void)
{
	lpp_Parsing_element	new = (lpp_Parsing_element)mymalloc(sizeof( struct lpp_parsing_element));

	new->label 		= NULL;
	new->source_edges	= NULL;
	new->target_edges	= NULL;
	new->nodes		= NULL;
	new->derivations	= NULL;
	new->hierarchy_level	= 1;
	new->has_big_nodelist	= FALSE;
	new->is_in_table	= FALSE;
	new->was_marked_by	= NULL;
	new->graph_iso		= NULL;
	new->tree_ref_iso	= NULL;

	return( new );
}

/*********************************************************************************
function -->	add_to_source_and_target_edges
Input:	lpp_Parsing_element source, target, char* label

	Erzeuge Kante zwischen source und target mit label label
*********************************************************************************/

void	add_to_source_and_target_edges(lpp_Parsing_element source, lpp_Parsing_element target, char *label)
{
	Edge_list	edge_s = new_edgelist( source, target, label );
	Edge_list	edge_t = new_edgelist( source, target, label );

	source->source_edges = add_to_edgelist( source->source_edges, edge_s );
	target->target_edges = add_to_edgelist( target->target_edges, edge_t );
}

/*********************************************************************************
function -->	free_parsing_element
Input:	lpp_Parsing_element	cur

	Free memory space of cur (Including nodes, incomming and outgoing edges)
*********************************************************************************/

void	free_parsing_element(lpp_Parsing_element cur)
{
	free_nodelist( cur->nodes );
	free_edgelist( cur->source_edges );
	free_edgelist( cur->target_edges );
	free( cur );
}

/*********************************************************************************
function:	is_terminal_label
Input	char* label

	Berchnet, ob 1. Zeichen gross oder klein- Buchstabe

Output:	TRUE iff. kleinbuchstabe
	FALSE sonst
*********************************************************************************/

int	is_terminal_label(char *label)
{
	if( (label != NULL ) && !isupper( label[0] ) )
	{
		return( TRUE );
	}
	else
	{
		return( FALSE );
	}
}

/*********************************************************************************
function -->	create_parsing_element_from_group
Input:	Set_of_parsing_elements	set

	Create copy of set set* and a parsing element pe, 
	append all elements of set at pe-> 

Output:	lpp_Parsing_element with 1st element of derivations having a pointer to set*
*********************************************************************************/

lpp_Parsing_element	create_parsing_element_from_group(Set_of_parsing_elements set, char *label)
{
	Set_of_parsing_elements		cur_set;
	Set_of_parsing_elements		new_set;
	Nodelist			cur_node;
	int				node_nr = 0;
	Edge_list			cur_edge;
	Set_of_parsing_elements		result			= NULL;
	lpp_Parsing_element			pe 			= new_parsing_element();
	Nodelist			node_result		= NULL;
	Derivation			new_der			= new_derivation();

	pe->label = strsave(label);

	/******* erzeuge Liste aller Knoten, die durch das neue lpp_Parsing_element abgedeckt werden 			*******/
	for_set_of_parsing_elements( set, cur_set )
	{
		node_nr++;

		new_set	= new_set_of_parsing_elements( cur_set->pe );
		new_set->production_iso = cur_set->production_iso;

		result	= add_to_set_of_parsing_elements( result, new_set );

		for_nodelist( cur_set->pe->nodes, cur_node )
		{
			node_result = add_to_nodelist( node_result, new_nodelist(cur_node->node, cur_node->edge) );
		}
		end_for_nodelist( cur_set->pe->nodes, cur_node );

	}
	end_for_set_of_parsing_elements( set, cur_set );


	/******* erzeuge alle Kanten, die von dem neuen lpp_Parsing_element ausgehen (Auch Update bei den alten) 		*******/
	for_set_of_parsing_elements( set, cur_set )
	{
		lpp_for_edgelist( cur_set->pe->source_edges, cur_edge )
		{
			if( !is_in_nodeset(cur_edge->target, node_result) && edge_does_not_already_exist(cur_edge, pe, cur_edge->target) &&
			    (is_terminal_label(pe->label) || is_terminal_label(cur_edge->target->label)) )
			{
				add_to_source_and_target_edges( pe, cur_edge->target, cur_edge->label );
			}
		}
		end_lpp_for_edgelist( cur_set->pe->source_edges, cur_edge );

		lpp_for_edgelist( cur_set->pe->target_edges, cur_edge )
		{
			if( !is_in_nodeset(cur_edge->source, node_result) && edge_does_not_already_exist(cur_edge, cur_edge->source, pe) &&
			    (is_terminal_label(pe->label) || is_terminal_label(cur_edge->source->label)) )
			{
				add_to_source_and_target_edges( cur_edge->source, pe, cur_edge->label );
			}
		}
		end_lpp_for_edgelist( cur_set->pe->target_edges, cur_edge );

	}
	end_for_set_of_parsing_elements( set, cur_set );

	/****** erzeuge beim lpp_Parsing_element, das als Ergebnis zurueckgeschickt wird alle Zeiger ******/
	new_der->derivation_nodes	= result;
	new_der->pe			= pe;
	pe->derivations			= add_to_derivations( pe->derivations, new_der );
	pe->nodes			= node_result;
	pe->label 			= label;

	if( node_nr > 3 )
	{
		pe->has_big_nodelist	= TRUE;
	}
	else
	{
		pe->has_big_nodelist	= FALSE;
	}


	return( pe );
}

/********************************************************************************/
/*										*/
/*	Nodelist								*/
/*										*/
/********************************************************************************/

/*********************************************************************************
function:	is_in_nodeset
Input:	lpp_Parsing_element element, Nodelist list

Output:	TRUE iff. elment repraesentiert einen Knoten, der sich in list befindet
*********************************************************************************/
int	is_in_nodeset(lpp_Parsing_element element, Nodelist list)
{
	Nodelist	cur_node, cur_list_node;

	for_nodelist( element->nodes, cur_node )
	{
		for_nodelist( list, cur_list_node )
		{
			if( cur_node->node == cur_list_node->node )
				return( TRUE );
		}
		end_for_nodelist( list, cur_list_node );
	}
	end_for_nodelist( element->nodes, cur_node );

	return( FALSE );
}

/*********************************************************************************
function -->	new_nodelist
Input:	Node node

	Allocate memory space for a new nodelist nl and set nl->node = node

Output:	Pointer to nl
*********************************************************************************/

Nodelist	new_nodelist(Node node, Edge edge)
{
	Nodelist	new = (Nodelist)mymalloc( sizeof(struct nodelist));

	new->node	= node;
	new->edge	= edge;
	new->next	= NULL;

	return( new );
}

/*********************************************************************************
function -->	add_to_nodelist
Input:	Nodelist list, cur

	Append cur at the beginning of list

Output:	cur
*********************************************************************************/

Nodelist	add_to_nodelist(Nodelist list, Nodelist cur)
{
	cur->next = list;

	return( cur );
}

/*********************************************************************************
function -->	free_nodelist
Input:	Nodelist list

	Free memory space of list
*********************************************************************************/

void	free_nodelist(Nodelist list)
{
	Nodelist	cur;

	while( list )
	{
		cur = list;
		list = list->next;
		free( cur );
	}
}

/********************************************************************************/
/*										*/
/*	set_of_parsing_elements							*/
/*										*/
/********************************************************************************/

/*********************************************************************************
function -->	new_set_of_parsing_elements
Input:	lpp_Parsing_element target

	Allocate memory space for a new parsing_element pe and set 
	pe->pe = target;

Output: pointer to pe
*********************************************************************************/

Set_of_parsing_elements	new_set_of_parsing_elements(lpp_Parsing_element target)
{
	Set_of_parsing_elements	new = (Set_of_parsing_elements)mymalloc( sizeof( struct set_of_parsing_elements));

	new->pe	= target;
	new->next			= NULL;

	return( new );
}

/*********************************************************************************
function -->	add_to_set_of_parsing_elements
Input:	Set_of_parsing_elements list, cur

	append cur at the beginning of list

Output:	cur
*********************************************************************************/

Set_of_parsing_elements	add_to_set_of_parsing_elements(Set_of_parsing_elements list, Set_of_parsing_elements cur)
{
	cur->next = list;

	return( cur );
}

/*********************************************************************************
function -->	delete_from_set_of_parsing_elements
Input:	Set_of_lpp_Parsing_elements set, element

	delete element from set

Output:	Pointer to first element of changed set
*********************************************************************************/

Set_of_parsing_elements	delete_from_set_of_parsing_elements(Set_of_parsing_elements set, Set_of_parsing_elements element)
{
	Set_of_parsing_elements	cur_elem;

	if( set->pe == element->pe )
	{
		cur_elem = set->next;
		free( set );
		return( cur_elem );
	}

	for_set_of_parsing_elements( set, cur_elem )
	{
		if( cur_elem->next->pe == element->pe )
		{
			cur_elem->next = cur_elem->next->next;
			free( element );
			return( set );
		}
	}
	end_for_set_of_parsing_elements( set, cur_elem );
	return (set);
}

/*********************************************************************************
function -->	union_to_set_of_parsing_elements
Input:	Set_of_parsing_elements group_1, group_2

	Add group_2 at the end of group_1

Output:	group_1
*********************************************************************************/

Set_of_parsing_elements	union_to_set_of_parsing_elements(Set_of_parsing_elements group_1, Set_of_parsing_elements group_2)
{
	Set_of_parsing_elements	cur = group_1;

	if( group_1 == NULL )
		return( group_2 );

	while( cur->next )
		cur = cur->next;

	cur->next = group_2;

	return( group_1 );
}

/*********************************************************************************
*********************************************************************************/

int	find_corresponding_node(Nodelist node, lpp_Parsing_element pars_elem)
{
	Nodelist	cur_node;

	for_nodelist( pars_elem->nodes, cur_node )
	{
		if( cur_node->node == node->node )
			return( TRUE );
	}
	end_for_nodelist( pars_elem->nodes, cur_node );

	return( FALSE );
}

/*********************************************************************************
*********************************************************************************/

int	matching_nodelists(lpp_Parsing_element elem1, lpp_Parsing_element elem2)
{
	Nodelist	cur_node;
	int		n1_length = 0;
	int		n2_length = 0;

	for_nodelist( elem1->nodes, cur_node )
	{
		if( !find_corresponding_node(cur_node, elem2) )
			return( FALSE );
		n1_length ++;
	}
	end_for_nodelist( elem1->nodes, cur_node );

	for_nodelist( elem2->nodes, cur_node )
	{
		n2_length++;
	}
	end_for_nodelist( elem2->nodes, cur_node );

	if( n1_length != n2_length )
		return( FALSE );
	return( TRUE );
}

/*********************************************************************************
*********************************************************************************/

lpp_Parsing_element	corresponding_element(Set_of_parsing_elements group, Set_of_parsing_elements elem)
{
	while( group )
	{
		if( my_strcmp(group->pe->label, elem->pe->label) )
		{
			if( matching_nodelists(group->pe, elem->pe) )
				return( group->pe );
		}
		group = group->next;
	}

	return( NULL );
}

/*********************************************************************************
function	change_sourcepointer_of_edge
Input:	parsing_element target, old_source, new_source

	Lasse
*********************************************************************************/

void	change_sourcepointer_of_edge(lpp_Parsing_element target, lpp_Parsing_element old_source, lpp_Parsing_element new_source)
{
	Edge_list	cur_edge;

	lpp_for_edgelist( target->target_edges, cur_edge )
	{
		if( cur_edge->source == old_source )
		{
			cur_edge->source = new_source;
		}
	}
	end_lpp_for_edgelist( target->target_edges, cur_edge );
}

/*********************************************************************************
function	change_targetpointer_of_edge
Input:	parsing_element source, old_target, new_target

	Lasse
*********************************************************************************/

void	change_targetpointer_of_edge(lpp_Parsing_element target, lpp_Parsing_element old_source, lpp_Parsing_element new_source)
{
	Edge_list	cur_edge;

	lpp_for_edgelist( target->source_edges, cur_edge )
	{
		if( cur_edge->target == old_source )
		{
			cur_edge->target = new_source;
		}
	}
	end_lpp_for_edgelist( target->source_edges, cur_edge );
}

/*********************************************************************************
*********************************************************************************/

void	free_edge_in_target(Edge_list edge)
{
	Edge_list	pre,
			cur_edge;
	lpp_Parsing_element	target	= edge->target;

	if( (target->target_edges->source == edge->source)	&&
	    my_strcmp(target->target_edges->label, edge->label) )
	{
		pre = target->target_edges;
		target->target_edges = target->target_edges->next;

		free( pre );
	}
	else
	{
		pre = target->target_edges;
		cur_edge = pre->next;

		while( cur_edge )
		{
			if( (cur_edge->source == edge->source)	&&
			    my_strcmp(cur_edge->label, edge->label) )
			{
				pre->next = cur_edge->next;
				free( cur_edge );
				cur_edge = pre->next;
			}
			else
			{
				pre = cur_edge;
				cur_edge = cur_edge->next;
			}
		}
	}
}

/*********************************************************************************
*********************************************************************************/

void	free_edge_in_source(Edge_list edge)
{
	Edge_list	pre,
			cur_edge;
	lpp_Parsing_element	source	= edge->source;

	if( (source->source_edges->target == edge->target)	&&
	    my_strcmp(source->source_edges->label, edge->label) )
	{
		pre = source->source_edges;
		source->source_edges = source->source_edges->next;

		free( pre );
	}
	else
	{
		pre = source->source_edges;
		cur_edge = pre->next;

		while( cur_edge )
		{
			if( (cur_edge->target == edge->target)	&&
			    my_strcmp(cur_edge->label, edge->label) )
			{
				pre->next = cur_edge->next;
				free( cur_edge );
				cur_edge = pre->next;
			}
			else
			{
				pre = cur_edge;
				cur_edge = cur_edge->next;
			}
		}
	}
}


/*********************************************************************************
*********************************************************************************/

int	source_edge_does_not_exist(Edge_list edge, lpp_Parsing_element pe)
{
	Edge_list	cur_edge;

	lpp_for_edgelist( pe->source_edges, cur_edge )
	{
		if( (cur_edge->target == edge->target)	&&
		    my_strcmp(cur_edge->label, edge->label) )
		{
			return( FALSE );
		}
	}
	end_lpp_for_edgelist( pe->source_edges, cur_edge );

	return( TRUE );
}
/*********************************************************************************
*********************************************************************************/

int	target_edge_does_not_exist(Edge_list edge, lpp_Parsing_element pe)
{
	Edge_list	cur_edge;

	lpp_for_edgelist( pe->target_edges, cur_edge )
	{
		if( (cur_edge->source == edge->source)	&&
		    my_strcmp(cur_edge->label, edge->label) )
		{
			return( FALSE );
		}
	}
	end_lpp_for_edgelist( pe->target_edges, cur_edge );

	return( TRUE );
}

/*********************************************************************************
function	union_to_tree
Input:	Set_of_parsing_elements	group1, group2

	Append group1 to group2. Wenn in group2 ein Parsing-element existiert, das 
	den gleichen label hat wie ein Element aus group1 UND die sich die gleichen 
	Knoten in den Knotenlisten befinden, dann fuege das Element aus group2 unter
	dem entsprechenden aus group1 ein

Output:	Pointer to group1
*********************************************************************************/
	
Set_of_parsing_elements		union_to_tree(Set_of_parsing_elements group1, Set_of_parsing_elements group2)
{
	Set_of_parsing_elements	cur_set_elem;
	lpp_Parsing_element		cur_pe_elem;
	Edge_list		cur;

	/****** Durchlaufe die 2. Gruppe 										******/
	while( group2 )
	{
		/****** Trenne das 1. Element der 2. Gruppe von der Liste ab						******/
		cur_set_elem 		= group2;
		group2 			= group2->next;
		cur_set_elem->next 	= NULL;

		/****** Suche in der 1. Gruppe, ob schon ein lpp_Parsing_element mit gleicher Knotenliste existiert		******/
		cur_pe_elem 		= corresponding_element( group1, cur_set_elem );

		/****** Existiert so ein lpp_Parsing_element, dann haenge das Element aus group2 als alternative Produktion	******/
		/****** bei dem lpp_Parsing_element dazu									******/
		if( cur_pe_elem )
		{
			cur_pe_elem->derivations = add_to_derivations( cur_pe_elem->derivations, cur_set_elem->pe->derivations );

			/****** Kanten umbiegen, da sie sonst auf das alte Element zeigen, das geloescht wird		******/
			/****** falls diese Kante schon existiert, dann vergessen					******/

			lpp_for_edgelist( cur_set_elem->pe->source_edges, cur )
			{
				if( source_edge_does_not_exist( cur, cur_pe_elem ) )
				{
					change_sourcepointer_of_edge( cur->target, cur_set_elem->pe, cur_pe_elem );
				}
				else	/***forget the edge***/
				{
					free_edge_in_target( cur );
				}
			}
			end_lpp_for_edgelist( cur_set_elem->pe->source_edges, cur );
			lpp_for_edgelist( cur_set_elem->pe->target_edges, cur )
			{
				if( target_edge_does_not_exist( cur, cur_pe_elem ) )
				{
					change_targetpointer_of_edge( cur->source, cur_set_elem->pe, cur_pe_elem );
				}
				else	/***forget the edge***/
				{
					free_edge_in_source( cur );
				}
			}
			end_lpp_for_edgelist( cur_set_elem->pe->target_edges, cur );

			/****** Mache Speicherplatz von lpp_Parsing_element und Set_of_parsing_elements wieder frei		******/
			free( cur_set_elem );
		}

		/****** Existiert kein passendes lpp_Parsing_element in group1, dann haenge das Element aus group2 zur Liste******/
		/****** in group1											******/
		else
		{
			group1 = union_to_set_of_parsing_elements( group1, cur_set_elem );
		}
	}

	return( group1 );
}
		
/*********************************************************************************
function	set_of_parsing_elements_consists_one_with_level
Input:	Set_of_parsing_elements group, int hierarchy_level 

Output:	TRUE iff. hierarchy_level of one lpp_Parsing_element = hierarchy_level
	FALSE otherwise
*********************************************************************************/

int	set_of_parsing_elements_consists_one_with_level(Set_of_parsing_elements group, int hierarchy_level)
{
	Set_of_parsing_elements	cur;

	for_set_of_parsing_elements( group, cur )
	{
		if( cur->pe->hierarchy_level == hierarchy_level )
			return( TRUE );
	}
	end_for_set_of_parsing_elements( group, cur );

	return( FALSE );
}

/*********************************************************************************
function	is_in_set
Input:	Set_of_parsing_elements group, lpp_Parsing_element elem

Output:	TRUE iff. elem is in group
	FALSE otherwise
*********************************************************************************/

int	is_in_set(lpp_Parsing_element elem, Set_of_parsing_elements group)
{
	Set_of_parsing_elements	cur_elem;

	for_set_of_parsing_elements( group, cur_elem )
	{
		if( cur_elem->pe == elem )
			return( TRUE );
	}
	end_for_set_of_parsing_elements( group, cur_elem );

	return( FALSE );
}

/*********************************************************************************
function	edge_exists_from
Input:	Set_of_parsing_elements node, group

Output:	TRUE iff. node is connected to one node of group
	FALSE otherwise
*********************************************************************************/

int	edge_exists_from(Set_of_parsing_elements node, Set_of_parsing_elements group)
{
	Edge_list	cur;

	if( group == NULL )
		return( TRUE );

	lpp_for_edgelist( node->pe->source_edges, cur )
	{
		if( is_in_set( cur->target, group ))
			return( TRUE );
	}
	end_lpp_for_edgelist( node->pe->source_edges, cur );

	lpp_for_edgelist( node->pe->target_edges, cur )
	{
		if( is_in_set( cur->source, group ))
			return( TRUE );
	}
	end_lpp_for_edgelist( node->pe->target_edges, cur );

	return( FALSE );
}

/*********************************************************************************
function	disjunkt
Input:	Set_of_parsing_elements	node, group

Output:	TRUE iff. no element of group includes node
*********************************************************************************/

int	disjunkt(lpp_Parsing_element node, Set_of_parsing_elements group)
{
	Set_of_parsing_elements	cur;
	Nodelist		cur_node_node, cur_group_node;

	for_set_of_parsing_elements( group, cur )
	{
		for_nodelist( cur->pe->nodes, cur_group_node )
		{
			for_nodelist( node->nodes, cur_node_node )
			{
				if( cur_node_node->node == cur_group_node->node )
				return( FALSE );
			}
			end_for_nodelist( node->nodes, cur_node_node );
		}
		end_for_nodelist( cur->pe->nodes, cur_group_node );
	}
	end_for_set_of_parsing_elements( group, cur );

	return( TRUE );
}

/*********************************************************************************
function -->	free_set_of_parsing_elements
Input:	Set_of_parsing_elements list

	Free memory space of list
*********************************************************************************/

void	free_set_of_parsing_elements(Set_of_parsing_elements list)
{
	Set_of_parsing_elements	cur;

	while( list )
	{
		cur = list;
		list = list->next;
		free( cur );
	}
}

/********************************************************************************/
/*										*/
/*	Edge_list								*/
/*										*/
/********************************************************************************/

/*********************************************************************************
function -->	new_edgelist
Input:	lpp_Parsing_element	source, target, char* label, Edge iso_edge

	Create a new edge_list el with source source and target target

Output:	Pointer to el
*********************************************************************************/

Edge_list	new_edgelist(lpp_Parsing_element source, lpp_Parsing_element target, char *label)
{
	Edge_list	new = (Edge_list)mymalloc( sizeof(struct edge_list));

	new->source 		= source;
	new->target		= target;
	new->label		= label;
	new->next		= NULL;

	return( new );
}

/*********************************************************************************
function -->	add_to_edgelist
Input:	Edge_list	list, cur

	Add cur at the beginning of list

Output:	cur
*********************************************************************************/

Edge_list	add_to_edgelist(Edge_list list, Edge_list cur)
{
	cur->next = list;

	return( cur );
}

/*********************************************************************************
function	edge_does_not_already_exist
*********************************************************************************/

int	edge_does_not_already_exist(Edge_list edge, lpp_Parsing_element source, lpp_Parsing_element target)
{
	Edge_list	cur_edge;

	lpp_for_edgelist(  source->source_edges,cur_edge )
	{
		if( (target == cur_edge->target) && my_strcmp(edge->label, cur_edge->label) )
			return( FALSE );
	}
	end_lpp_for_edgelist(  source->source_edges,cur_edge );

	return( TRUE );
}

/*********************************************************************************
function -->	free_edgelist
Input:	Edge_list list

	Free memory space of list
*********************************************************************************/

void	free_edgelist(Edge_list list)
{
	Edge_list	to_delete;

	while( list )
	{
		to_delete = list;
		list = list->next;
		free( to_delete );
	}
}

/********************************************************************************/
/*										*/
/*	int_list								*/
/*										*/
/********************************************************************************/

/*********************************************************************************
function	new_intlist
Input:	int nr

	Create a new int_list il with integer nr

Output:	Pointer to il
*********************************************************************************/

Int_list	new_intlist(int nr)
{
	Int_list	new = (Int_list)mymalloc( sizeof( struct int_list));

	new->integer	= nr;
	new->next 	= NULL;

	return( new );
}

/*********************************************************************************
function:	is_in_intlist
Input:	Int_list list, int nr

	Ueberprueft ob nr in list enthalten ist

Output:	TRUE iff. nr ist in list enthalten
	FALSE sonst
*********************************************************************************/

int	is_in_intlist(Int_list list, int nr)
{
	Int_list	cur_elem;

	for_int_list( list, cur_elem )
	{
		if( cur_elem->integer == nr )
		{
			return( TRUE );
		}
	}
	end_for_int_list( list, cur_elem );

	return( FALSE );
}

/*********************************************************************************
function	add_to_intlist
Input:	Int_list list, int nr

	Wenn nr noch nicht enthalten ist in List, dann erzeuge neues Element und
	haenge es vorne an Liste

Output:	il
*********************************************************************************/

Int_list	add_to_intlist(Int_list list, int nr)
{
	Int_list	cur_elem;

	for_int_list( list, cur_elem )
	{
		if( cur_elem->integer == nr )
		{
			return( list );
		}
	}
	end_for_int_list( list, cur_elem );

	cur_elem 	= new_intlist( nr );
	cur_elem->next	= list;
	
	return( cur_elem );
}

/*********************************************************************************
function	free_intlist
Input:	Int_list list

	Free memory space of list
*********************************************************************************/

void	free_intlist(Int_list list)
{
	Int_list	to_delete;

	while( list )
	{
		to_delete = list;
		list = list->next;
		free( to_delete );
	}
}

/********************************************************************************/
/*										*/
/*	set_of_nodelist								*/
/*										*/
/********************************************************************************/

/*********************************************************************************
function	new_set_of_nodelist
*********************************************************************************/

Set_of_nodelist	new_set_of_nodelist(Nodelist list)
{
	Set_of_nodelist	new = (Set_of_nodelist)mymalloc( sizeof(struct set_of_nodelist) );

	new->list		= list;
	new->is_in_embedding	= TRUE;
	new->next		= NULL;

	return( new );
}

/*********************************************************************************
function	add_to_set_of_nodelist
*********************************************************************************/

Set_of_nodelist	add_to_set_of_nodelist(Set_of_nodelist list, Set_of_nodelist element)
{
	element->next = list;

	return( element );
}

/*********************************************************************************
function	free_set_of_nodelist
*********************************************************************************/

void	free_set_of_nodelist(Set_of_nodelist list)
{
	Set_of_nodelist	del;

	while( list )
	{
		del = list;
		list = list->next;
		free( del );
	}
}


/********************************************************************************/
/*										*/
/*	Derivation								*/
/*										*/
/********************************************************************************/

/*********************************************************************************
function: -->	new_derivation

	Allocate memory space for a struct derivation n

Output:	Pointer to n
*********************************************************************************/

Derivation	new_derivation(void)
{
	Derivation	new = (Derivation)mymalloc( sizeof(struct derivation) );

	new->derivation_nodes		= NULL;
	new->used_prod			= NULL;
	new->next			= NULL;
	new->attributes_table_down	= NULL;
	new->attributes_table_up	= NULL;
	new->is_in_table		= FALSE;

	return( new );
}

/*********************************************************************************
function: -->	add_to_derivations
Input:	Derivation list, new

	Put new at the beginning of list

Output:	new
*********************************************************************************/

Derivation	add_to_derivations(Derivation list, Derivation new)
{
	new->next = list;

	return( new );
}

/*********************************************************************************
function: -->	free_derivations
Input:	Derivation list

	free memory space of list

Output:	new
*********************************************************************************/

void	free_derivation(Derivation list)
{
	Derivation	to_delete;

	while( list )
	{
		to_delete 	= list;
		list 		= list->next;
		free( to_delete );
	}
}

