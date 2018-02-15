/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef	STORE_HEADER
#define	STORE_HEADER

#include <graph.h>

typedef	enum	{
	STORE_LAST_GRAPH,
	STORE_ALL_GRAPHS,
	
	NUMBER_OF_STORE_WHATS	/* Dummy	*/
}
	Store_what;

#define STORE_ONE_GRAPH STORE_LAST_GRAPH

typedef struct {

	Store_what what;
	
	union {
		Graph	graph;		/* Der Rest kommt spaeter	*/
	}
		which;
}
	Store_info;

extern	int	store_graph  (Graph graph, char *filename);
extern	int	store_graphs (char *filename);
extern	int	save_state   (void);
extern  int	store        (Store_info info, char *filename);
 
#endif
