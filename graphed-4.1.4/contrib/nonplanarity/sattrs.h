/****************************************************************************\
 *                                                                          *
 *  sattrs.h                                                                *
 *  --------                                                                *
 *                                                                          *
 *  author:  a.j. winter (11027)  03/93.                                    *
 *                                                                          *
\****************************************************************************/



#define UNDEFINED   (-1)

#define UNVISITED   (-1)
#define CURRENT     (1)
#define COMPLETED   (2)
#define EMBEDDED    (3)

#define OLD         (0)
#define NEW         (-1)

#define TREE_EDGE   (0)
#define CYCLE_EDGE  (1)

#define ARTICULATION_POINT (1)
#define CENTER		   (2)

/* return codes for graphs that cannot be processed due to *
 * requirements that are needed for graphs */
#define NONPLANAR	(0)  /* is_planar = FALSE */
#define PLANAR		(1)  /* is_planar = TRUE */

#define PROPER_GRAPH	 (0)
/* in case that the given graph is improper *
 * one of the following (negative) return codes is returned *
 * that cannot conflict with the returncode of the function *
 * is_planar (TRUE (1) / FALSE (0) *
 * else the returned code of is_planar is returned *
 * i.e. that the property PROPER_GRAPH itself is never returned *
 * and may therefore conflict with the returncode of is_planar */
#define TOO_MANY_EDGES  (-1)
#define	LOOP		(-2)
#define	MULTI_EDGE	(-3)


#define	NO_LAYER	(-1)

#define LAYER1		(0)
#define LAYER2		(1)


/****************************************************************************/
/* set some default coordinates for embedding graphs                        */
/****************************************************************************/

#define CENTER_X        2000
#define CENTER_Y        2000
#define MIN_DISTANCE      50  /* depending on nodesize */
#define MIN_X            100
#define MIN_Y            100
#define MAX_X           3900
#define MAX_Y           3900

#define	FACTOR		5


#define MAX_RANDOM_NODES	50



#define LABEL_LENGTH	3
#define NEW_ATTRS(t)    (char *) malloc(sizeof(t))
#define NEW_LABEL       (char *) malloc(LABEL_LENGTH)
#define FREE_ATTRS(t)   free(t)


typedef struct _nodeattrs
{
    short	visited;
    int		dfnumber;
    int		compnumber;
    int		dfsucc_count;
    short	bcc_property;
    int		stnumber;
    Snode	stsucc;
    short	marker;
    short	temp_flag;
    int		degree;
    int		Lvalue;
    Snode	Lnode;
    Snode	toLnode;
    Slist	listElem;
}   *NodeAttrs;


#define node_visited(nn)	attr_data_of_type(nn,NodeAttrs)->visited
#define node_dfnumber(nn)	attr_data_of_type(nn,NodeAttrs)->dfnumber
#define node_compnumber(nn)	attr_data_of_type(nn,NodeAttrs)->compnumber
#define node_dfsucc_count(nn)  	attr_data_of_type(nn,NodeAttrs)->dfsucc_count
#define node_bcc_property(nn)   attr_data_of_type(nn,NodeAttrs)->bcc_property
#define node_stnumber(nn)	attr_data_of_type(nn,NodeAttrs)->stnumber
#define node_stsucc(nn)		attr_data_of_type(nn,NodeAttrs)->stsucc
#define node_marker(nn)		attr_data_of_type(nn,NodeAttrs)->marker
#define node_temp_flag(nn)	attr_data_of_type(nn,NodeAttrs)->temp_flag
#define node_degree(nn)		attr_data_of_type(nn,NodeAttrs)->degree
#define node_Lvalue(nn)		attr_data_of_type(nn,NodeAttrs)->Lvalue
#define node_Lnode(nn)		attr_data_of_type(nn,NodeAttrs)->Lnode
#define node_toLnode(nn)	attr_data_of_type(nn,NodeAttrs)->toLnode
#define node_listElem(nn)	attr_data_of_type(nn,NodeAttrs)->listElem


#define node_flags(nn)      attr_flags(nn)
/* for compatibility */
#define attr_marker(nn)      attr_flags(nn)


typedef struct _edgeattrs
{
    Sedge   quer;   /* e->tsuc */
    short   marker;
    short   visited;
    short   type; /* TREE-edge, CYCLE-edge */
    bool    in_mpg; /* TRUE, FALSE */
    short   layer;    /* layer-number */
    Sedge   iso;
/*  Sedge   original;  * iso alone is not enough to remember iso-edges */
}   *EdgeAttrs;

#define edge_visited(ee)     attr_data_of_type(ee,EdgeAttrs)->visited
#define edge_marker(ee)      attr_data_of_type(ee,EdgeAttrs)->marker
#define edge_quer(ee)        attr_data_of_type(ee,EdgeAttrs)->quer
#define edge_type(ee)        attr_data_of_type(ee,EdgeAttrs)->type
#define edge_in_mpg(ee)      attr_data_of_type(ee,EdgeAttrs)->in_mpg
#define edge_layer(ee)       attr_data_of_type(ee,EdgeAttrs)->layer
#define edge_iso(ee)	     attr_data_of_type(ee, EdgeAttrs)->iso
/* #define edge_original(ee)    attr_data_of_type(ee, EdgeAttrs)->original */


#define empty_attrs	    make_attr(ATTR_DATA, NULL)
#define slist_elem(elem)    make_attr(ATTR_DATA,(char *) (elem))
#define slist_flag(elem)    make_attr(ATTR_FLAGS,(elem))
#define push(l,elem)        (add_immediately_to_slist(l,slist_elem(elem)))->pre
#define push_flag(l,elem)   (add_immediately_to_slist(l,slist_flag(elem)))->pre
#define enqueue(l,elem)     add_immediately_to_slist(l,slist_elem(elem))
#define isempty(l)          (l == empty_slist)
#define empty_stack         empty_slist
#define top_node(l)         attr_data_of_type(l,Snode)
#define top_edge(l)         attr_data_of_type(l,Sedge)
#define top_graph(l)        attr_data_of_type(l,Sgraph)
#define top_flag(l)         attr_flags(l)
#define pop(l)              subtract_immediately_from_slist(l,l)
#define rest(l)             subtract_immediately_from_slist(l,l)

#define add_first(l,elem)    (add_immediately_to_slist(l,slist_elem(elem)))->pre
#define add_last(l,elem)     add_immediately_to_slist(l,slist_elem(elem))

#define make_slist_elem(elem)    (add_immediately_to_slist(empty_slist,slist_elem(elem)))->pre
#define remove_slist_elem(l, elem) (subtract_immediately_from_slist(l, elem))

#define sedge_in_slist(l)    attr_data_of_type(l,Sedge)
#define snode_in_slist(l)    attr_data_of_type(l,Snode)
#define sgraph_in_slist(l)   attr_data_of_type(l,Sgraph)



#define	min(x,y)	(((x) < (y)) ? (x) : (y))
#define	max(x,y)	(((x) > (y)) ? (x) : (y))


#if defined SUN_VERSION
extern  int     CheckAssumedGraphProperties(Sgraph g, int *nodeCount, int *edgeCount);
extern  Sgraph  copy_graph_with_attrs(Sgraph g);
extern  Sgraph  copy_graph_with_flags(Sgraph g);
extern  Sgraph  copy_graph_without_attrs(Sgraph g);
extern  void    create_and_init_all_attributes(Sgraph g);
extern  void    create_and_init_edge_attributes(Sedge edge);
extern  void    create_and_init_node_attributes(Snode node);
extern  void    clear_all_attributes(Sgraph graph);
extern  void    clear_node_attributes(Snode node);
extern  void    clear_edge_attributes(Sedge edge);
extern  void    re_init_all_attributes(Sgraph g);
extern  Slist   concat_slists(Slist l, Slist m);
extern	void	count_all_nodes_and_edges_in_graph(Sgraph g, int *nc, int *ec);
extern	int	count_edges_in_graph(Sgraph g);
extern  void    clear_all_labels(Sgraph graph);
#else
extern  int     CheckAssumedGraphProperties(Sgraph, int *, int *);
extern  Sgraph  copy_graph_with_attrs(Sgraph);
extern  Sgraph  copy_graph_with_flags();
extern  Sgraph  copy_graph_without_attrs(Sgraph);
extern  void    create_and_init_all_attributes(Sgraph);
extern  void    create_and_init_edge_attributes(Sedge);
extern  void    create_and_init_node_attributes(Snode);
extern  void    clear_all_attributes(Sgraph);
extern  void    clear_node_attributes(Snode);
extern  void    clear_edge_attributes(Sedge);
extern  void    re_init_all_attributes(Sgraph);
extern  Slist   concat_slists(Slist, Slist);
extern	void	count_all_nodes_and_edges_in_graph(Sgraph, int *, int *);
extern	int	count_edges_in_graph(Sgraph);
extern  void    clear_all_labels(Sgraph);
#endif

