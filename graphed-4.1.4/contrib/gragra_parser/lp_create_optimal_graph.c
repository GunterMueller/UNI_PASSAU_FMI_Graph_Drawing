#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>

#include <X11/Xos.h>
#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/icon.h>
#include <xview/notify.h>

#include "misc.h"
#include "types.h"
#include "main_sf.h"

#include "lp_datastruc.h"
#include "lp_cost_struc.h"

#include "lp_create_optimal_graph.h"


/************************************************************************************************/
/*												*/
/*	Die Layout-Kosten wurden ausgerechnet. Der Graph muss jetzt neu gezeichnet werden.	*/
/*	Die Funktion in diesem File geht durch die Datenstruktur und legt fest, welche Produk-	*/
/*	tion auf welchen Knoten angewendet werden muss						*/
/*	Ausserdem steht hier die Funktion fuer einen Ableitungsschritt				*/
/*												*/
/************************************************************************************************/
/****************************************************************************************/
/*			attr_type nur dummy um Knoten zu erzeugen			*/
/****************************************************************************************/
#define	EMPTY_ATTR \
	make_attr( ATTR_DATA, NULL )



int	MY_UNDEF = -1;

/***********************************************************************
function:	my_strcmp_wa
Input:	char	*string1, string2

	ACHTUNG: Komischer Name, da sonst Namenskonflikt

Output:	TRUE iff. strcmp(string1, string2) = TRUE or string1 = string2 = NULL
	FALSE otherwise
***********************************************************************/

int	my_strcmp_wa(char *string1, char *string2)
{
	if( (string1 != NULL) && (string2 != NULL) &&
	    (strcmp(string1, string2) == 0) )
		return( TRUE );
	if( (string1 == NULL) && (string2 == NULL) )
		return( TRUE );
	return( FALSE );
}

/*****************************************************************************************
function:	find_corresponding_pe
Input:	LP_parsing_element_list	pe_list, Snode snode

	liefert pe zurueck, das snode in unserer Datenstruktur entspricht

Output:	Gefundenes pe
*****************************************************************************************/

LP_Parsing_element	find_corresponding_pe(LP_Parsing_element_list pe_list, Snode snode)
{
	LP_Parsing_element_list	cur_pe_list;
	Parsing_element		lams_pe;

	FOR_LP_PARSING_ELEMENTS( pe_list, cur_pe_list )
	{
		lams_pe = cur_pe_list->lams_prod_iso;
		while( lams_pe )
		{
			if( lams_pe->prod_iso == snode )
			{
				return( cur_pe_list->pe );
			}
			lams_pe = lams_pe->next_iso_node;
		}
	}
	END_FOR_LP_PARSING_ELEMENTS( pe_list, cur_pe_list );

	printf("FEHLER!!!!\n\n\n");
	return NULL;		
}

/*****************************************************************************************
function:	my_apply_production_on_pe
Input:	LP_Parsing_element father, Sprod production, LP_dependency_graph x_dependency,
	y_dependency, LP_Parsing_element_list sons, Sgraph sgraph

	Fuehrt auf der Sgraph - Datenstruktur einen Ableitungsschritt aus
	Muss:	Graph_iso von LP_Parsing_elementen aus eintragen
		Von Produktionsknoten aus temporaere Zeiger auf die Entsprechung im
		neu erzeugten Graphen machen 
*****************************************************************************************/

void	my_apply_production_on_pe(LP_Parsing_element father, Sprod production, LP_dependency_graph x_dependency, LP_dependency_graph y_dependency, LP_Parsing_element_list sons, Sgraph sgraph)
{
	Snode			cur_snode,
				sgraph_node,
				new_snode;
	Sedge			cur_sedge,
				new_sedge;
	Sembed			cur_embed;
	Graphed_node		new_graphed_node,
				corresponding_node;
	Graphed_edge		new_graphed_edge;
	LP_dependency_graph	cur_dependency;
	LP_Parsing_element	pe_iso;
	Attributes		cur_attr;	/****** Um komfortabel umzukopieren muss der Produktionsknoten seine Entsprechung	******/
						/****** im erzeugten Graph kennen. Dazu dient cur_attr 					******/
	int			x_origin, y_origin,	/****** linke obere Ecke, zu der relativ eingesetzt werden muss			******/
				node_x, node_width,
				node_y, node_height;

	/* Hole linken oberen Eckpunkt, zu dem relativ eingesetzt werden muss */
	x_origin = (int)node_get( graphed_node(father->graph_iso), NODE_X ) - (int)node_get( graphed_node(father->graph_iso), NODE_WIDTH ) / 2;
	y_origin = (int)node_get( graphed_node(father->graph_iso), NODE_Y ) - (int)node_get( graphed_node(father->graph_iso), NODE_HEIGHT ) / 2;


	cur_dependency = x_dependency;
	while( cur_dependency )
	{
		if( (cur_dependency->side == RIGHT) && (cur_dependency->node_in_prod) )
		{
			pe_iso = find_corresponding_pe( sons, cur_dependency->node_in_prod );

			new_snode	= make_node( sgraph, EMPTY_ATTR );
			cur_attr	= make_attr( ATTR_DATA, (char*)new_snode );
			set_nodeattrs( cur_dependency->node_in_prod, cur_attr );

			new_graphed_node	= create_graphed_node_from_snode( new_snode );

			node_width	= cur_dependency->new_coord - cur_dependency->first_border_part->new_coord;
			node_x		= cur_dependency->new_coord - (node_width / 2) + x_origin;

			set_nodelabel( new_snode, mem_copy_string_wa(pe_iso->label) );
			node_set( new_graphed_node, ONLY_SET,	NODE_POSITION,	node_x, MY_UNDEF, 
								NODE_SIZE,	node_width, MY_UNDEF,
								NODE_LABEL,	mem_copy_string_wa(pe_iso->label), 0 );
								/****** MY_UNDEF nur dummy, weil Hoehe und neues y erst in y_dependency ******/ 

			pe_iso->graph_iso = new_snode;
		}
		cur_dependency = cur_dependency->next;
	}

	cur_dependency = y_dependency;
	while( cur_dependency )
	{
		if( (cur_dependency->side == DOWN) && (cur_dependency->node_in_prod) )
		{
			corresponding_node = graphed_node( (Snode)attr_data(cur_dependency->node_in_prod) );

			node_height	= cur_dependency->new_coord - cur_dependency->first_border_part->new_coord;
			node_y		= cur_dependency->new_coord - (node_height / 2) + y_origin;

			node_set( corresponding_node, ONLY_SET,	NODE_POSITION,	(int)node_get(corresponding_node, NODE_X), node_y, 
								NODE_SIZE,	(int)node_get(corresponding_node, NODE_WIDTH), node_height, 0 );
		}
		cur_dependency = cur_dependency->next;
	}

	/****** Zeichne alle RHS_Kanten ******/
	for_all_nodes( production->right, cur_snode )
	{
		for_sourcelist( cur_snode, cur_sedge )
		{
			new_sedge		= make_edge( (Snode)attr_data(cur_snode), (Snode)attr_data(cur_sedge->tnode), EMPTY_ATTR );
			new_graphed_edge	= create_graphed_edge_from_sedge( new_sedge );

			edge_set( new_graphed_edge, ONLY_SET, EDGE_LABEL, mem_copy_string_wa(cur_sedge->label), 0 );
			set_edgelabel( new_sedge, mem_copy_string_wa(cur_sedge->label) );
		}
		end_for_sourcelist( cur_snode, cur_sedge );
	}
	end_for_all_nodes( production->right, cur_snode );


	/****** Die Embedding_edges muessen noch eingefuegt werden ******/
	for_all_nodes( production->right, cur_snode )
	{
		for_all_snode_sembeds( cur_snode, cur_embed )
		{
			if( cur_embed->olddir == S_in )		/****** Behandlung fuer IN_Embeddings ******/
			{
				for_targetlist( father->graph_iso, cur_sedge )
				{
					if( my_strcmp_wa( (char*)node_get(graphed_node(cur_sedge->snode), NODE_LABEL), cur_embed->node_embed) &&
					    my_strcmp_wa( cur_sedge->label, cur_embed->oldedge) )
					{
						new_sedge 		= make_edge( cur_sedge->snode, (Snode)attr_data(cur_snode), make_attr(ATTR_DATA, NULL) );
						new_graphed_edge	= create_graphed_edge_from_sedge( new_sedge );

						edge_set( new_graphed_edge, ONLY_SET, EDGE_LABEL, mem_copy_string_wa(cur_sedge->label), 0 );
						set_edgelabel( new_sedge, mem_copy_string_wa(cur_sedge->label) );
					}
				}
				end_for_targetlist( father->graph_iso, cur_sedge );
			}


			if( cur_embed->olddir == S_out )	/****** Behandlung fuer OUT_Embeddings ******/
			{
				for_sourcelist( father->graph_iso, cur_sedge )
				{
					if( my_strcmp_wa( (char*)node_get(graphed_node(cur_sedge->tnode), NODE_LABEL), cur_embed->node_embed) &&
					    my_strcmp_wa( cur_sedge->label, cur_embed->oldedge) )
					{
						new_sedge = make_edge( (Snode)attr_data(cur_snode), cur_sedge->tnode, make_attr(ATTR_DATA, NULL) );
						new_graphed_edge = create_graphed_edge_from_sedge( new_sedge );

						edge_set( new_graphed_edge, ONLY_SET, EDGE_LABEL, mem_copy_string_wa(cur_sedge->label), 0 );
						set_edgelabel( new_sedge, mem_copy_string_wa(cur_sedge->label) );
					}
				}
				end_for_sourcelist( father->graph_iso, cur_sedge );
			}
		}
		end_for_all_snode_sembeds( cur_snode, cur_embed );
	}
	end_for_all_nodes( production->right, cur_snode );

	/* hier werden vom erzeugten Graphen Zeiger auf meine Datenstrukturen gelegt, um spaeter, falls gefordert */
	/* problemlos die Groessen der Knoten in den Produktionen einsetzen zu koennen */

	cur_dependency = x_dependency;
	while( cur_dependency )
	{
		if( (cur_dependency->side == RIGHT) && (cur_dependency->node_in_prod) )
		{
			pe_iso		= find_corresponding_pe( sons, cur_dependency->node_in_prod );
			sgraph_node	= (Snode)attr_data( cur_dependency->node_in_prod );

			cur_attr	= make_attr( ATTR_DATA, (char*)pe_iso );
			set_nodeattrs( sgraph_node, cur_attr );

		}
		cur_dependency = cur_dependency->next;
	}

	switch( WIN.what_to_do_with_derivated_node )
	{
		case 0:	node_set( graphed_node(father->graph_iso), ONLY_SET, 	NODE_NLP,	NODELABEL_UPPERLEFT,
										NODE_TYPE,	find_nodetype("#box"), 0 );
			break;

		case 1: node_set( graphed_node(father->graph_iso), ONLY_SET,	NODE_LABEL, 	NULL, 
										NODE_TYPE,	find_nodetype("#box"), 0 );
			break;

		case 2: delete_node( graphed_node(father->graph_iso) );
			remove_node( father->graph_iso );
			father->graph_iso = NULL;
	}
	if( WIN.what_to_do_with_derivated_node != 2 )
	{
		cur_attr = make_attr( ATTR_DATA, NULL );
		set_nodeattrs( father->graph_iso, cur_attr );
	}
}

/*****************************************************************************************
function:	set_graph_nodesizes_in_nodes
Input:	Sgraph sgraph

	Durchlaeuft alle Knoten und setzt, falls Zeiger auf Pars_table gesetzt ist,
	die Knotengroesse vom Ausgangsgraphen ein

*****************************************************************************************/

void	set_graph_nodesizes_in_nodes(Sgraph sgraph)
{
	Snode			cur_node;
	LP_Parsing_element	iso_pe;

	for_all_nodes( sgraph, cur_node )
	{
		iso_pe = (LP_Parsing_element)attr_data( cur_node );
		if( iso_pe && (iso_pe->width != 0) )
		{
			node_set( graphed_node(cur_node), ONLY_SET, NODE_SIZE, iso_pe->width, iso_pe->height, 0 );
		}
	}
	end_for_all_nodes( sgraph, cur_node );
}

/*****************************************************************************************
function:	convert_lower_part_of_table_to_tree
Input:	LP_sizes_array sizes, int which_sizes

	Zeichnet aktuelle lower_derivation und ruft sich selbst fuer alle abgeleiteten
	Soehne eine Stufe weiter unten auf
*****************************************************************************************/

void	convert_lower_part_of_table_to_tree(LP_sizes_array sizes, int which_sizes, Sgraph sgraph)
{
	int				i;
	LP_lower_derivation		lower_der;
	LP_array_of_lower_productions	low_array;
	LP_sizes_ref			cur_sizes_ref;

	lower_der = sizes[which_sizes].used_derivation;

	if( lower_der )
	{

		low_array = lower_der->productions;
		for( i = 0; i < lower_der->number_of_productions; i++ )
		{
			my_apply_production_on_pe( low_array[i].father_node,
						   low_array[i].production, 
						   low_array[i].x_dependency,
						   low_array[i].y_dependency,
						   low_array[i].derivation->parsing_elements, 
						   sgraph );
		}

		cur_sizes_ref = sizes[which_sizes].used_productions;
		for( i = 0; i < sizes[which_sizes].nr_of_prods_below; i++ )
		{
			if( cur_sizes_ref[i].which_sizes_array )
			{
				convert_lower_part_of_table_to_tree( cur_sizes_ref[i].which_sizes_array, cur_sizes_ref[i].what_entry_in_array, sgraph );
			}
		}

	}
}

/*****************************************************************************************
function:	lp_convert_table_to_graph
Input:	LP_Parsing_element head

	Legt fuer Root des Ableitungstable die Ableitung fest.
	Ruft die rekursive Funktion fuer den Rest des Baums auf
*****************************************************************************************/

void	lp_convert_table_to_tree(LP_Parsing_element head)
{
	int				i, j,
					cur_optimal_size,
					cur_size,
					opt_upper_array_nr,
					opt_sizes_array_nr;
	LP_upper_production		cur_upper,
					optimal_upper;
	LP_array_of_productions		opt_upper_array,
					cur_upper_array;
	LP_sizes_array			cur_sizes,
					opt_sizes;
	Snode				start_node,
					cur_node;
	Sedge				cur_edge;
	Graphed_node			new_graphed_node;
	Graphed_graph			new_graphed_graph;
	Sgraph				sgraph;

	cur_optimal_size = MY_UNDEF;

	/****** Suche nach einer Ableitung, die eine optimale Groesse des Graphen erlaubt ******/

		FOR_LP_UPPER_PRODUCTION( head->layout_structures, cur_upper )
		{
			cur_upper_array = cur_upper->production_layouts;
			for( i = 0; i < cur_upper->number_of_prod_layouts; i++ )
			{
				cur_sizes = cur_upper_array[i].SIZES;
				for( j = 0; j < cur_upper_array[i].length_of_sizes; j++ )
				{
					if( ((cur_size = cur_sizes[j].w * cur_sizes[j].h) < cur_optimal_size) ||
					    (cur_optimal_size == MY_UNDEF) )
					{
						cur_optimal_size	= cur_size;
						opt_upper_array_nr	= i;
						opt_sizes_array_nr	= j;
						optimal_upper		= cur_upper;
						opt_upper_array		= cur_upper_array;
						opt_sizes		= cur_sizes;
					}
				}
			}
		}
		END_FOR_LP_UPPER_PRODUCTION( head->layout_structures, cur_upper );

	/****** Erzeuge Startknoten und wende erste Produktion darauf an ******/

		sgraph			= make_graph	( EMPTY_ATTR );
		sgraph->directed	= TRUE;
		new_graphed_graph	= create_graphed_graph_from_sgraph( sgraph );

		start_node		= make_node	( sgraph, EMPTY_ATTR );
		new_graphed_node	= create_graphed_node_from_snode( start_node );

		set_nodelabel( start_node, mem_copy_string_wa(head->label) );
		node_set( new_graphed_node, ONLY_SET, 
						NODE_POSITION, (opt_sizes[opt_sizes_array_nr].w /2) + 16, (opt_sizes[opt_sizes_array_nr].h / 2) + 16, 
						NODE_SIZE, opt_sizes[opt_sizes_array_nr].w, opt_sizes[opt_sizes_array_nr].h,
						NODE_LABEL, mem_copy_string_wa(head->label), 0 );

		head->graph_iso	= start_node;

		my_apply_production_on_pe( head, 
					   opt_upper_array[opt_upper_array_nr].prod_iso, 
					   opt_sizes[opt_sizes_array_nr].x_dependency,
					   opt_sizes[opt_sizes_array_nr].y_dependency,
					   optimal_upper->derivation->parsing_elements,
					   sgraph					);

	/******	Rufe die rekursive Funktion auf, die die Zeichnung fuer LP_lower_derivation durchfuehrt ******/
		if( opt_sizes )
		{
			convert_lower_part_of_table_to_tree( opt_sizes, opt_sizes_array_nr, sgraph );
		}


	/****** Jetzt muessen noch, falls Knotengroessen vom Graph verwendet werden, alles reduziert werden ******/
		if( WIN.create_with_graph_nodesizes )
		{
			set_graph_nodesizes_in_nodes( sgraph );
		}

	/****** Mache RESTORE auf neu erzeugtem Graphen ******/
		for_all_nodes( sgraph, cur_node )
		{
			for_sourcelist( cur_node, cur_edge )
			{
				edge_set( graphed_edge(cur_edge), RESTORE_IT, 0 );
			}
			end_for_sourcelist( cur_node, cur_edge );

			node_set( graphed_node(cur_node), RESTORE_IT, 0 );
		}
		end_for_all_nodes( sgraph, cur_node );

}

