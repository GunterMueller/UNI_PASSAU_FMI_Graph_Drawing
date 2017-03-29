
#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <sys/syscall.h>
#include <ctype.h>

void set_all_properties_of_grammar(void)
{
	int index;

	for ( index = 0; index < PRECONDITION_COUNTER; index++ )		/* Zun"achst alles setzen	*/
		graph_state.lp_graph_state.gragra_properties[index] = 1;	
}

void compute_grammar_properties(Graph graph)
{
	int index;

	for ( index = 0; index < PRECONDITION_COUNTER; index++ )
		graph_state.lp_graph_state.gragra_properties[index]&=graph->lp_graph.properties_array[index];
}
