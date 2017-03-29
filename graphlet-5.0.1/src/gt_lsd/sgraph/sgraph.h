/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */
/* Sgraph Source, 1988-1994 by Michael Himsolt */

#ifndef SGRAPH_HEADER
#define SGRAPH_HEADER

#ifndef SGRAPH_STANDALONE
#define GRAPHED_POINTERS
#define SGRAGRA_POINTERS
#endif

#include "std.h"

/************************************************************************/
/*									*/
/*		        Save / Restore Attributes			*/
/*									*/
/************************************************************************/

typedef	struct	saved_sgraph_attrs	{
    Attributes	attrs;
    void		(*make_node_proc)(struct snode *the_node);
    void		(*make_edge_proc)(struct sedge *the_edge);
    void		(*remove_node_proc)(struct snode *the_node);
    void		(*remove_edge_proc)(struct sedge *the_edge);
    void		(*remove_graph_proc)(struct sgraph *the_graph);
    char		*key;
}
*Saved_sgraph_attrs;

#define	empty_saved_sgraph_attrs (Saved_sgraph_attrs)NULL


typedef	struct	saved_snode_attrs	{
    Attributes	attrs;
    char		*key;
}
*Saved_snode_attrs;

#define	empty_saved_snode_attrs (Saved_snode_attrs)NULL


typedef	struct	saved_sedge_attrs	{
    Attributes	attrs;
    char		*key;
}
*Saved_sedge_attrs;

#define	empty_saved_sedge_attrs (Saved_sedge_attrs)NULL



/************************************************************************/
/*									*/
/*				SGRAPH					*/
/*									*/
/************************************************************************/


typedef struct	sgraph {
    struct	snode	*nodes;
    char		*label;
    int		directed;
#ifdef GRAPHED_POINTERS
    char		*graphed;
#endif

    void		(*make_node_proc)(struct snode *the_node);
    void		(*make_edge_proc)(struct sedge *the_edge);
    void		(*remove_node_proc)(struct snode *the_node);
    void		(*remove_edge_proc)(struct sedge *the_edge);
    void		(*remove_graph_proc)(struct sgraph *the_graph);

    struct sgraph	*iso;

    Attributes	attrs;
    struct slist	*saved_attrs;	/* List of saved attributes        */
    char		*attrs_key;	/* key of current saved attributes */
}
*Sgraph;

#define	first_node_in_graph(g)	((g)->nodes)
#define	last_node_in_graph(g)	((g)->nodes->pre)
#ifndef GRAPHED
#define	empty_graph		((Sgraph)NULL)
#endif
#define	empty_sgraph	((Sgraph)NULL)

Sgraph	make_graph     (Attributes attrs);
extern	Sgraph	copy_sgraph    (Sgraph sgraph);
void	remove_graph   (Sgraph graph);
extern	void	set_graphlabel (Sgraph graph, char *text);
extern	void	print_graph    (FILE *file, Sgraph g,
				void* (print_graph_attributes) (FILE *file, Sgraph g),
				void* (print_node_attributes) (FILE *file, struct snode *n),
				void* (print_edge_attributes) (FILE *file, struct sedge *e));
extern	Sgraph	load_graph     (void); /* graphed2simple needs this */



/************************************************************************/
/*									*/
/*				SNODE					*/
/*									*/
/************************************************************************/


typedef	struct	snode {
    struct	snode	*pre,   *suc;
    struct	sedge	*slist, *tlist;
    struct	sgraph	*graph;
    char		*label;
    int		nr;
    int		x,y;
    struct	snode	*iso;

#ifdef GRAPHED_POINTERS
    char		*graphed;
#endif
#ifdef SGRAGRA_POINTERS
    char		*embedding;
#endif
    struct	snode	*filter;

    Attributes	attrs;
    struct slist	*saved_attrs;	/* List of saved attributes        */
    char		*attrs_key;	/* key of current saved attributes */
}
*Snode;

#ifndef GRAPHED
#define	empty_node	((Snode)NULL)
#endif
#define	empty_snode	((Snode)NULL)

#define snode_x(n) ((n)->x)
#define snode_y(n) ((n)->y)

#define	for_all_nodes(graph, node) \
{ if (((node) = (graph)->nodes) != (Snode)NULL) do {
#define	end_for_all_nodes(graph, node) \
} while (((node) = (node)->suc) != (graph)->nodes); }

extern	Snode	make_node             (Sgraph graph, Attributes attrs);
extern	Snode	make_node_with_number (Sgraph graph, Attributes attrs, int nr);
extern	Snode	copy_snode            (Snode snode);
extern	void	remove_node           (Snode node);
extern	void	set_nodelabel         (Snode node, char *text);
extern	void	set_nodefilter        (Snode node, Snode filter);
extern	Snode	get_nodefilter        (Snode node, Snode filter);
extern	void	set_node_xy           (Snode node, int x, int y);



/************************************************************************/
/*									*/
/*				SEDGE					*/
/*									*/
/************************************************************************/


typedef	struct	sedge {
    struct	sedge	*spre,  *ssuc,
										*tpre,  *tsuc;
    struct	snode	*snode, *tnode;
    char		*label;
#ifdef GRAPHED_POINTERS
    char		*graphed;
#endif
    struct	sedge	*filter;

    Attributes	attrs;
    struct slist 	*saved_attrs;	/* List of saved attributes        */
    char		*attrs_key;	/* key of current saved attributes */
}
*Sedge;


#ifndef GRAPHED
#define	empty_edge	((Sedge)NULL)
#endif
#define empty_sedge	((Sedge)NULL)

#include "slist.h"

extern	Sedge	make_edge     (Snode snode, Snode tnode, Attributes attrs);
extern	Sedge	copy_sedge     (Sedge sedge);
extern	void	remove_edge   (Sedge edge);
extern	void	set_edgelabel (Sedge edge, char *text);
extern	void	set_edgefilter (Sedge edge, Sedge filter);
extern	Sedge	get_edgefilter (Sedge edge, Sedge filter);

extern	void	set_nodeattrs (Snode node, Attributes attrs);
extern	void	set_edgeattrs (Sedge edge, Attributes attrs);
extern	void	set_graphattrs (Sgraph graph, Attributes attrs);

extern void save_sgraph_attrs(Sgraph graph, char *key);
extern void restore_sgraph_attrs(Sgraph graph, char *key,
				 void (*make_node_proc)(Snode node), void (*make_edge_proc)(Sedge edge), int remove);

extern	void	set_graph_directed (Sgraph graph, int directed);


#define	for_sourcelist(node, edge)		\
{ if (((edge) = (node)->slist) != (Sedge)NULL) do {
#define	end_for_sourcelist(node, edge)	\
} while (((edge) = (edge)->ssuc) != (node)->slist); }
#define	for_targetlist(node, edge)		\
{ if (((edge) = (node)->tlist) != (Sedge)NULL) do {
#define	end_for_targetlist(node, edge)	\
} while (((edge) = (edge)->tsuc) != (node)->tlist); }

#define for_edgelist(node,edge) for_sourcelist(node,edge)
#define end_for_edgelist(node,edge) end_for_sourcelist(node,edge)

#define unique_edge(e) ((e) < (e)->tsuc)

extern	Sedge	get_unique_edge_handle(Sedge edge);

#endif
