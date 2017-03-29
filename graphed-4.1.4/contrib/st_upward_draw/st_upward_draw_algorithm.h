/*===========================================================================*/
/*  
	 PROJECT	st_upward_draw

	 SYSTEM:       	GraphEd extension module
	 FILE:        	st_upward_draw_algorithm.h
	 AUTHOR:       	Roland Stuempfl (diploma 1994)


	 Overview
	 ========
	 Source code of the header of the graphed
	 extension module st_upward_draw, based on the algorithm upward_draw
	 presented by Guiseppe Di Battista and Roberto Tamassia, published
	 in "Algorithms For Plane Representations of Acyclic Digraphs",
	 Theoretical Computer Science 61 (1988), pp. 175-198.
	 In the following this work will be referenced as [DiBatTam88].
*/
/*===========================================================================*/

#define PRENODEATTR(node)	(attr_data_of_type((node),Prenodeattrs))
#define NODEATTR(node)		(attr_data_of_type((node),Nodeattrs))
#define PREEDGEATTR(edge)	(attr_data_of_type((edge),Preedgeattrs))
#define EDGEATTR(edge)		(attr_data_of_type((edge),Edgeattrs))
#define PREGRAPHATTR(graph)	(attr_data_of_type((graph),Pregraphattrs))
#define GRAPHATTR(graph)	(attr_data_of_type((graph),Graphattrs))


#define DUMMY_NODES_LIST(graph) (PREGRAPHATTR(graph)->dummy_nodes_list)
#define DUMMY_EDGES_LIST(graph) (PREGRAPHATTR(graph)->dummy_edges_list)
#define PREEMBEDLIST(node)	(PRENODEATTR(node)->embed_list)
#define X(node)			(NODEATTR(node)->lfx)
#define Y(node)			(NODEATTR(node)->lfy)
#define EMBEDLIST(node)		(NODEATTR(node)->embed_list)
#define BACKNODE(node)		(NODEATTR(node)->back_node)
#define RANK(node)		(NODEATTR(node)->rank)
#define STATE(node)		(NODEATTR(node)->state)
#define SOURCE(graph)		(GRAPHATTR(graph)->source)
#define TARGET(graph)		(GRAPHATTR(graph)->target)
#define THIRD(graph)		(GRAPHATTR(graph)->third)
#define EXTERN_MIN(graph)	(GRAPHATTR(graph)->min_theta)
#define EXTERN_MAX(graph)	(GRAPHATTR(graph)->max_theta)
#define ALPHA(graph)		(GRAPHATTR(graph)->alpha)
#define INK(graph)		(GRAPHATTR(graph)->ink)
#define CANDLIST(graph)		(GRAPHATTR(graph)->candidate_list)
#define EDGEISO(edge)		(EDGEATTR(edge)->iso)

#define MAKE_DATA_ATTR(ptr)	(make_attr(ATTR_DATA,(ptr)))

#define EDGE(l)			(attr_data_of_type((l),Sedge))
#define NODE(l)			(attr_data_of_type((l),Snode))

#define OTHERNODE(edge,node) 	((node) == (edge)->snode ? (edge)->tnode : (edge)->snode)

#define Angle			double
#define empty_point		((Point)NULL)

#define EXTERN			1
#define INTERN			2
#define ON_THE_CYCLE		3

#define	BASE_LENGTH		1000000.0
#define EPS			0.0001

typedef struct prenodeattrs {
	bool	visited;	/* sorting flag */
	bool	sorted_in;	/* sorting flag */
	int	rank;		/* topological rank of the node*/
	Slist	embed_list;	/* output of the already implemented planar embedding algorithm embed */
}
	*Prenodeattrs;

typedef struct nodeattrs {
	Snode	back_node; 	/* the former instance of this node */
	int	rank;		/* topological rank */
	Slist	embed_list;	/* planar embedding lists */
	int	state;		/* state: EXTERN, INTERN, ON_THE_CYCLE */
	double	lfx;		/* x-coordinate in double format */
	double	lfy;		/* y-coordinate in double format */
}
	*Nodeattrs;

typedef struct preedgeattrs {
	Sedge	iso;		/* auxialiary data field */
}
	*Preedgeattrs;

typedef struct edgeattrs {
	Sedge	iso;		/* auxialiary data field */
}
	*Edgeattrs;

typedef struct pregraphattrs {
	Slist	dummy_edges_list; /* list of the dummy edges */
	Slist dummy_nodes_list;   /* list of the dummy nodes */
}
	*Pregraphattrs;

typedef struct graphattrs {
	Slist	candidate_list;	/* candidate list */
	Snode	source;		/* source (=> external face) */
	Snode target;       	/* target (=> external face) */
	Snode	third;		/* third node of the external face */
	Angle	min_theta;	/* minimal slope of edges of the external face */
	Angle	max_theta;	/* maximal slope of edges of the external face */
	Angle	alpha;		/* tolerance angle */
	int	ink;		/* instance number, recursion depth */
}
	*Graphattrs;

typedef struct point {		/* data structure of a point with real coordinates */
	double x;
	double y;
}
	*Point;

typedef struct	line {		/* data structure of a straight line */
	double	x;		/* x-coordinate of the origin */
	double	y;		/* y-coordinate of the origin */
	Angle	angle;		/* angle of the vector with respect to the x-axis */
}
	*Line;


Snode source, target;		/* source resp. target of the original graph */
Slist ts_list;			/* auxiliary sorting list */

/* for_... and end_for_... macros to traverse an Slist in opposite direction */
#define	for_slist_reverse(list, l) \
	{ if ((list) != (Slist)NULL) { (l) = (list)->pre; do {
#define	end_for_slist_reverse(list, l) \
	} while (((l) = (l)->pre) != list->pre); } }

extern 	Sgraph	make_new_graph(void);
extern 	Snode	make_new_node(Sgraph g, Snode old_node);
extern	void	remove_new_graph(Sgraph g);
extern	void	remove_new_node(Snode n);

extern bool   node_is_candidate (Snode n);

extern Angle  st_upward_draw_get_angle (double x0, double y0, double x1, double y1);
