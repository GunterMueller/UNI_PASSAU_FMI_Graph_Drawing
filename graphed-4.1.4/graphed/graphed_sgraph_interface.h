/* (C) Universitaet Passau 1986-1994 */
#ifndef GRAPHED_SGRAPH_INTERFACE_HEADER
#define GRAPHED_SGRAPH_INTERDACE_HEADER
#include <sgraph/graphed_structures.h>

extern	Sgraph	copy_graphed_graph_to_sgraph	(Graph graph);
extern	void	free_sgraph			(Sgraph graph);
extern	void	menu_call_sgraph_proc		(Menu menu, Menu_item menu_item, void (*proc) ());
extern	void	call_sgraph_proc		(void (*proc) (), char *user_args);

extern	void	attatch_snode (Node node);
extern	void	attatch_sedge (Edge edge);
extern	void	attatch_sgraph (Graph graph);

extern	Graphed_graph	create_graphed_graph_from_sgraph_in_buffer (Sgraph sgraph, int buffer);
extern void delete_attatched_sgraph (Graph graph);
extern void delete_attatched_sedge (Edge edge);
extern void delete_attatched_snode (Node node);


#endif
