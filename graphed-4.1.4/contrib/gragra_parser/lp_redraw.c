#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>

#include "misc.h"
#include "types.h"
#include "parser.h"
#include "win_defs.h"

#include "lp_datastruc.h"
#include "lp_convert.h"
#include "lp_create_layout_struc.h"
#include "lp_costs.h"
#include "lp_create_optimal_graph.h"

#include "lp_redraw.h"


/****************************************************************************************/
/*											*/
/*	Hauptfile fuer die Platzoptimale Neuzeichnung eines bereits geparsten Graphen	*/
/*											*/
/****************************************************************************************/


void	test_output(LP_Parsing_element pe)
{

	LP_Derivation_list	cur_der_list;
	LP_Parsing_element_list	cur_pe_list;

	FOR_LP_DERIVATIONS( pe->derivations, cur_der_list )
	{
		FOR_LP_PARSING_ELEMENTS( cur_der_list->derivation->parsing_elements, cur_pe_list )
		{
			printf("pelist:	%p    ->suc: %p\n", cur_pe_list, cur_pe_list->suc );
		}
		END_FOR_LP_PARSING_ELEMENTS( cur_der_list->derivation->parsing_elements, cur_pe_list );
		printf( "\nnext derivation\n");
	}
	END_FOR_LP_DERIVATIONS( pe->derivations, cur_der_list );

}

/*****************************************************************************************
function:	lp_create_optimal_graph_layout
Input:	----

	Erzeugt die Platzoptimale Neuzeichnung eines geparsten Graphen; 
	dabei : NEUBERECHNUNG aller Attribute
*****************************************************************************************/

void	lp_create_optimal_graph_layout(void)
{
	LP_Parsing_element	pars_table;


	/****** Falls alte Datenstrukturen vorhanden, loesche Sie			******/
	if( PRS_info.pars_table )
	{
		free_lp_parsing_element_with_lower_part( PRS_info.pars_table );
		PRS_info.pars_table = NULL;
		free_copy_iso_in_lams_table( PRS_info.start_elements );
	}

	pars_table = convert_parser_data_structures_to_applying_structures( PRS_info.start_elements );

	create_layout_datastructures		( pars_table );
	optimal_sizes_for_tree			( pars_table );
	copy_dependency_graph_to_lower_array	( pars_table );
	lp_convert_table_to_tree		( pars_table );

	PRS_info.pars_table = pars_table;
}

