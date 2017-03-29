#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_attribute_init_and_clear.h"
#include "lp_history.h"

/****************************************************************/
/*								*/
/*	modul	lp_history.c					*/
/*								*/
/****************************************************************/


/***********************************************************************
function:	new_history_ref

	Create new histoey recording n

Output:	Pointer to n
***********************************************************************/

history_ref 	new_history_ref(void)
{
	history_ref	new = (history_ref) mymalloc( sizeof( struct history_rec ) );
	
	new->pre	= new;
	new->suc	= new;
	new->element	= NULL;
	return(new);
}

/***********************************************************************
function:	add_to_history
Input:	history_ref liste, element

	Put element at the end of liste

Output:	Pointer to liste
***********************************************************************/

history_ref 	add_to_history(history_ref liste, history_ref element)
{

	if (liste == NULL)
		return( element );

	element->suc = liste;
	element->pre = liste->pre;
	liste->pre->suc = element;
	liste->pre = element;
	return( liste );
}

/**********************************************************************
function:	free_history
Input:	history_ref liste

	Free memory space of liste
**********************************************************************/

void	free_history(history_ref liste)
{
	history_ref	cur_liste = liste;
	history_ref	to_delete;

	if ( cur_liste != NULL)
		do
		{
			to_delete = cur_liste;
			cur_liste = cur_liste->suc;
			free( to_delete );
		}
		while( cur_liste != liste );
}
	
/*******************************************************************
function:	new_history_edge_ref

	Create a new history edge recording n

Output:	Pointer to n
*******************************************************************/

history_edge_ref 	new_history_edge_ref(void)
{
	history_edge_ref	new 
		= (history_edge_ref) mymalloc( sizeof( struct history_edge_rec ) );

	new->target	= NULL;
	new->source	= NULL;
	new->in_pre	= new;
	new->in_suc	= new;
	new->out_pre	= new;
	new->out_suc	= new;
	new->graphed_iso = NULL;
	init_attributes_of_history_edge_ref( new );
	return(new);
}


/*****************************************************************
function:	new_tree_node_ref

	Create new tree node recording n

Output:	Pointer to n
*****************************************************************/

tree_node_ref	 new_tree_node_ref(void)
{
	tree_node_ref	new = (tree_node_ref) mymalloc( sizeof( struct tree_node_rec ) );

	new->first_son			= NULL;
	new->prod_iso			= NULL;
	new->used_prod			= NULL;
	new->first_x 			= NULL;
	new->first_y			= NULL;
	new->graph_iso			= NULL;

	new->multi_edges 		= NULL;
	new->LP_costs			= NULL;
	new->LHS_costs			= 0;
	new->LP_COSTS			= 0;
	new->LP_set			= 0;

	new->possible_productions	= NULL;
	new->possible_nodes		= NULL;
	new->area_structures		= NULL;
	new->new_graph_node		= NULL;

	new->flag 		= 0;
	new->big		= 0;
	new->leaf		= 1;
	init_attributes_of_tree_node_ref( new );

	return(new);
}

/****************************************************************
function:	new_tree_edge_ref

	Create a new tree edge recording n

Output:	Pointer to n
****************************************************************/

tree_edge_ref 	new_tree_edge_ref(void)
{
	tree_edge_ref	new = (tree_edge_ref) mymalloc( sizeof( struct tree_edge_rec ) );

	new->type 		= RHS_EDGE;
	new->prod_iso		= NULL;
	new->out_edges		= NULL;
	new->in_edges		= NULL;
	new->tree_line		= NULL;
	new->target		= NULL;
	init_attributes_of_tree_edge_ref( new );

	return(new);
}

/****************************************************************
function:	new_tree_ref

	Create a new tree recording n

Output:	Pointer to n
****************************************************************/

tree_ref 	new_tree_ref(void)
{
	tree_ref	new = (tree_ref) mymalloc( sizeof( struct tree_rec ) );

	new->next_brother	= NULL;
	new->father		= NULL;
	new->tree_rec_type	= TREE_NODE;
	new->tree_rec.node 	= NULL;
	new->hierarchy_level	= 0;
	new->multi_edge 	= NULL;
	return(new);
}

/****************************************************************/
/*								*/
/*	update history after every production			*/
/*								*/
/****************************************************************/

/****************************************************************
function: 	make_history_rec
input:	Edge cur_edge

	Create history_ref n with element cur_edge->tree_iso
	and append n as history to cur_edge	
****************************************************************/

void	make_history_rec(Edge cur_edge)
{
	history_ref	new 		= new_history_ref();

	new->element 			= cur_edge->lp_edge.tree_iso;
	cur_edge->lp_edge.history 	= new;
}

/****************************************************************
function:	create_history_rec_from_edge
Input:	Edge cur_edge

	Create a new history ref n with element cur_edge->tree_iso

Output:	n
****************************************************************/

history_ref	create_history_rec_from_edge(Edge cur_edge)
{
	history_ref	new	= new_history_ref();

	new->element		= cur_edge->lp_edge.tree_iso;
	return( new );
}

/****************************************************************
function: copy_history_of_edge
Input:	Edge edge

	Create a copy n of the history of edge

Output:	n
****************************************************************/

history_ref	copy_history_of_edge(Edge edge)
{
	history_ref	copy = NULL;
	history_ref	cur_history = edge->lp_edge.history;
	history_ref	new_history;

	if ( cur_history != NULL )
		do
		{
			new_history = new_history_ref();
			new_history->element = cur_history->element;
			copy = add_to_history(copy, new_history);
			cur_history = cur_history->suc;
		}
		while( cur_history != edge->lp_edge.history );
	return( copy );
}
	
/****************************************************************
function: print_personal_history_of_edge
input: Edge e

	Print the history of e on stdio	
****************************************************************/

void	print_personal_history_of_edge(Edge e)
{
	history_ref	cur_history_ref ;

	printf(" von %d %s mit %s in %d %s hat history:\n"
		,e->source->nr
		,e->source->label.text
		,e->label.text
		,e->target->nr
		,e->target->label.text);
	for_history_ref(e->lp_edge.history, cur_history_ref)
	{
		printf("	 %d %s mit %s in %d %s\n"
			,cur_history_ref->element->tree_rec.history_elem->prod_iso->source->nr
			,cur_history_ref->element->tree_rec.history_elem->prod_iso->source->label.text
			,cur_history_ref->element->tree_rec.history_elem->prod_iso->label.text
			,cur_history_ref->element->tree_rec.history_elem->prod_iso->target->nr
			,cur_history_ref->element->tree_rec.history_elem->prod_iso->target->label.text);
	}
	end_for_history_ref(e->lp_edge.history, cur_history_ref)
}

/****************************************************************
function: update_history_of_the embeddings
Input:	Edge replaced_edge, embedding_edge, new_edge

	Create the history of new_edge by making a copy of the 
	history of replaced_edge and puting it at the end of the`
	history of embedding edge
****************************************************************/

void	update_history_of_the_embedding(Edge replaced_edge, Edge embedding_edge, Edge new_edge)
{
	new_edge->lp_edge.history = add_to_history( copy_history_of_edge( replaced_edge ),
			                create_history_rec_from_edge( embedding_edge ) );
}

/****************************************************************
function: create_personal_history_of_edge
Input:	Group p

	Create for every edge of p a history	
****************************************************************/

void	create_personal_history_of_edge	(Group p)
{
	Group 		g;
	Edge		e;

	for_group (p, g )
	{
		for_edge_sourcelist( g->node, e )
		{
			make_history_rec(e);
		}
		end_for_edge_sourcelist( g->node, e );
	}
	end_for_group(p, g );
	
}

/*****************************************************************
function:	print_histories
Input:	Graph graph

	Print histories of all edges of graph
*****************************************************************/

void	print_histories	(Graph graph)
{
	Node 		n;
	Edge		e;

	printf("***********************************************\n");
	for_nodes (graph, n )
	{
		for_edge_sourcelist( n, e )
		{
			print_personal_history_of_edge( e );
		}
		end_for_edge_sourcelist( n, e );
	}
	end_for_nodes(graph, n );
	
}

/*****************************************************************
function:	add_to_history_edge_out_ref
Input:	history_edge_ref liste, cur

	Put cur at the end of liste for the out
*****************************************************************/

history_edge_ref	add_to_history_edge_out_ref(history_edge_ref liste, history_edge_ref cur)
{
	if ( liste == NULL ) return( cur );

	cur->out_pre = liste->out_pre;
	cur->out_suc = liste;
	liste->out_pre->out_suc = cur;
	liste->out_pre = cur;
	return( liste );
}

/*****************************************************************
function:	add_to_history_edge_in_ref
Input:	history_edge_ref liste, cur

	Put cur at the end of liste for the in
*****************************************************************/

history_edge_ref	add_to_history_edge_in_ref(history_edge_ref liste, history_edge_ref cur)
{
	if ( liste == NULL ) return( cur );

	cur->in_pre = liste->out_pre;
	cur->in_suc = liste;
	liste->in_pre->in_suc = cur;
	liste->in_pre = cur;
	return( liste );
}


int	is_target_of_out_edges_of_node(tree_ref target, tree_ref node)
{
	history_edge_ref	first_edge = node->tree_rec.history_elem->out_edges;
	history_edge_ref	cur_edge;

	if ( first_edge != NULL )
	{
		cur_edge = first_edge;
		do
		{
			if ( cur_edge->target == target )
				return( TRUE );
			cur_edge = cur_edge->out_suc;
		}
		while ( cur_edge != first_edge );
	}
	return( FALSE );
}
	

/****************************************************************
function: 	create_history_of_edge
input:	tree_ref upper, lower

	makes an update of upper->out_edges and lower->in_edges
	with l as target
****************************************************************/

void	create_history_of_edge(tree_ref upper, tree_ref lower)
{
	history_edge_ref	new;

	if (!is_target_of_out_edges_of_node(lower, upper) )
	{
		new = new_history_edge_ref();

		new->target = lower;
		new->source = upper;

		upper->tree_rec.history_elem->out_edges = 
		add_to_history_edge_out_ref(upper->tree_rec.history_elem->out_edges, new ); 
		lower->tree_rec.history_elem->in_edges =  
		add_to_history_edge_in_ref(lower->tree_rec.history_elem->in_edges, new ); 
	}
}
	

/****************************************************************
function: append
Input:	tree_ref father, new_son, old_son

	iff. father = old_son 	: make new_son to first_son of father
	else			: make new_son to next_brother of old_son

output: new_son
****************************************************************/

tree_ref append(tree_ref father, tree_ref new_son, tree_ref old_son)
{
	new_son->father = father;
	if (father == old_son)    /* in this case new_son is first_son  */
	{
		father->tree_rec.node->first_son = new_son;
		new_son->father = father;
	}
	else 
	/*  now father has less one son  */
		old_son->next_brother = new_son;
	return(new_son);
}	

/************************************************************************
function: 	edge_is_out_embedding
Input:	Edge e

Output:	FALSE iff. edge is no out_embedding
	TRUE otherwise
************************************************************************/

int	edge_is_out_embedding(Edge edge, Graph p)
{
	Node	left_hand_side_node = p->gra.gra.nce1.left_side->node;

	if(inside(edge->target->x, edge->target->y, left_hand_side_node) )
		return( FALSE );
	return( TRUE );
}

/****************************************************************
function: make_node_father_to_production
input:	Node node_father, Graph prod

	Create derivation_net_elements d of production and make 
	node_father->tree_ref to father of d
****************************************************************/

void	make_node_father_to_production(Node node_father, Graph prod)
{
	Edge		cur_edge;
	Group		cur_group, g;
	tree_ref	new_place_to_append ;
	tree_ref	object_to_append ; 
	tree_node_ref	node_to_append ;
	tree_edge_ref   edge_to_append ;
	int		i, number_of_in_embeddings;
	tree_ref	wurzel;
	tree_node_ref	wurzel_knoten;
	tree_ref	node_to_be_father;
	int		new_level;

	number_of_in_embeddings  = size_of_embedding (prod->gra.gra.nce1.embed_in);

	cur_group = prod->gra.gra.nce1.right_side;

	/* there is no derivation net, so this is a single node from which we will create a hierarchical_graph */
	if (node_father->graph->lp_graph.derivation_net == NULL )
	{
		wurzel 						= new_tree_ref();
		wurzel_knoten					= new_tree_node_ref();
		wurzel_knoten->prod_iso				= node_father;
		wurzel->tree_rec_type				= TREE_NODE;	
		wurzel->tree_rec.node				= wurzel_knoten;
		wurzel->tree_rec.node->prod_iso  		= prod->gra.gra.nce1.left_side->node;
		wurzel->tree_rec.node->used_prod 		= prod;
		node_father->lp_node.tree_iso			= wurzel;
		node_father->graph->lp_graph.derivation_net 	= wurzel;
		node_to_be_father	 			= wurzel;

	/*	node_father->graph->lp_graph.creation_time	= (int)ticks(); */
	}
	else 
	{
		node_to_be_father				= node_father->lp_node.tree_iso;
		node_to_be_father->tree_rec.node->used_prod 	= prod;
	}
	new_place_to_append 				= node_to_be_father;

	node_to_be_father->tree_rec.node->leaf 		= 0;
	node_to_be_father->tree_rec.node->graph_iso	= NULL;
	new_level					= node_to_be_father->hierarchy_level + 1;

	/* append all nodes of right side of production to the derivation net */
	for_group(cur_group, g)	 
	{
		object_to_append 			= new_tree_ref();
   		node_to_append 				= new_tree_node_ref();

		node_to_append->prod_iso 		= g->node;
		node_to_append->graph_iso 		= g->node->iso;
		object_to_append->tree_rec.node 	= node_to_append;
		object_to_append->tree_rec_type 	= TREE_NODE;
		object_to_append->hierarchy_level 	= new_level;
		g->node->lp_node.tree_iso 		= object_to_append;
		g->node->iso->lp_node.tree_iso 		= object_to_append;

		new_place_to_append 
			= append(node_to_be_father, object_to_append, new_place_to_append);
	}
	end_for_group(cur_group, g);

	/* append all edges of the right side of the production to the derivation net */
	/* ***remember: this routine also appends out_embedding_edges***	      */

	for_group(cur_group, g)
	{
		for_edge_sourcelist(g->node, cur_edge)
		{
			object_to_append 			= new_tree_ref();
   			edge_to_append 				= new_tree_edge_ref();

			edge_to_append->prod_iso 		= cur_edge;
			if(edge_is_out_embedding(cur_edge, prod) )
			{
				edge_to_append->type		= OUT_CONN_REL;
			}
			else	
			{
				edge_to_append->type 		= RHS_EDGE;
			}
			edge_to_append->target			= cur_edge->target->lp_node.tree_iso;

			object_to_append->tree_rec.history_elem	= edge_to_append;
			object_to_append->tree_rec_type 	= HISTORY_ELEM;
			object_to_append->hierarchy_level	= new_level;

			cur_edge->lp_edge.tree_iso 			= object_to_append;
			if( !(cur_edge->lp_edge.tree_iso->tree_rec.history_elem->type == OUT_CONN_REL) )
			{
				cur_edge->lp_edge.iso->lp_edge.tree_iso 	= object_to_append;
			}

			new_place_to_append 			= append(node_to_be_father, object_to_append, new_place_to_append);
		}
		end_for_edge_sourcelist(g->node,cur_edge);
	}
	end_for_group(cur_group, g);

	/* append all _in_embedding of the production to the derivation net */
	for( i=0; i<number_of_in_embeddings; i++) 
	{
		cur_group = prod->gra.gra.nce1.embed_in[i].embed;
	      	for_group (cur_group, g) 
		{
			for_edge_sourcelist( g->node, cur_edge )
			{
				object_to_append 			= new_tree_ref();
   				edge_to_append 				= new_tree_edge_ref();

				edge_to_append->prod_iso 		= cur_edge;
				edge_to_append->type 			= IN_CONN_REL;
				edge_to_append->target 			= cur_edge->target->lp_node.tree_iso;

				object_to_append->tree_rec.history_elem = edge_to_append;
				object_to_append->tree_rec_type 	= HISTORY_ELEM;
				cur_edge->lp_edge.tree_iso 			= object_to_append;
				object_to_append->hierarchy_level 	= new_level;

				new_place_to_append 			= append(node_to_be_father, object_to_append, new_place_to_append);
			}
			end_for_edge_sourcelist( g->node, cur_edge );
	      	}
		end_for_group (cur_group, g);
	}
}


void	lp_increment_derivation_tree(Node node, Graph prod, Group copy_of_right_side)
{
	if ( node->graph->lp_graph.hierarchical_graph == TRUE )

	{
		make_node_father_to_production(node, prod );
		create_personal_history_of_edge( copy_of_right_side );
	}
}
