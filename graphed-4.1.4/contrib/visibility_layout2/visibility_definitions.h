/**********************************************************/
/*                                                        */
/* FILE: VISIBILITY_DEFINITIONS.H                         */
/*                                                        */
/* Beschreibung: Deklarationen, die fuer alle Algorithmen */
/*               benoetigt werden                         */
/*                                                        */
/**********************************************************/

#ifndef __VISIBILITY_DEFINITIONS_H__
#define __VISIBILITY_DEFINITIONS_H__

#include <sgraph_interface.h>

/******************** Typendefinitionen *******************/

typedef enum {        /* die verschiedenen Algorithmen */
          RT_weak,
          TT_weak,
          TT_epsilon,
          Kant_weak,
          Numm_weak
} Visibility_Type;

typedef struct s_t_selection_result {  /* Resultattyp der Funktion */
           Snode s;                    /* st_selection             */
           Snode t;
           Sedge dummy_edge;
} *S_T_Selection_Result;

typedef struct compression_node_info { /* Knotenattribute fuer Compression */
           int   level;
           int   xl;
           int   xr;
} *Compression_Node_Info;

typedef struct compression_edge_info { /* Kantenattribute fuer Compression */
           int   x;
           int   is_dummy;
} *Compression_Edge_Info;


/****************** externe Funktionen ********************/

extern bool w_visibility_error (Sgraph graph);
extern S_T_Selection_Result s_t_selection (Sgraph_proc_info info);
extern Sgraph save_nodeattrs (Sgraph graph);
extern void reset_nodeattrs (Sgraph graph, Sgraph save_graph);
extern void visibility_compression (Sgraph graph, int max, Visibility_Type type);



/******************* Makrodefinitionen ********************/

#define empty_attr (make_attr(ATTR_DATA,nil))
#define make_attr_snode(node) (make_attr(ATTR_DATA,(char*)(node)))
#define make_attr_sedge(edge) (make_attr(ATTR_DATA,(char*)(edge)))
#define make_attr_slist(list) (make_attr(ATTR_DATA,(char*)(list)))
#define make_attr_sgraph(graph) (make_attr(ATTR_DATA,(char*)(graph)))
#define attr_snode(x) (attr_data_of_type((x),Snode))
#define attr_sedge(x) (attr_data_of_type((x),Sedge))
#define attr_slist(x) (attr_data_of_type((x),Slist))
#define attr_sgraph(x) (attr_data_of_type((x),Sgraph))
#define add_snode_to_slist(list,node) (add_immediately_to_slist((list),make_attr(ATTR_DATA,(char*)(node))))
#define add_sedge_to_slist(list,edge) (add_immediately_to_slist((list),make_attr(ATTR_DATA,(char*)(edge))))
#define add_slist_to_slist(list1,list2) (add_immediately_to_slist((list1),make_attr(ATTR_DATA,(char*)(list2))))
#define subtract_first_element_from_slist(list) (subtract_immediately_from_slist((list),(list)))
#define subtract_last_element_from_slist(list) (subtract_immediately_from_slist((list),(list)->pre))
#define TARGET_NODE(edge,node) (iif((edge)->snode == (node),(edge)->tnode,(edge)->snode))



bool compression_used; /* Wurde Compression angewendet? */

/**********************************************************/
/*                                                        */
/*         END OF FILE: VISIBILITY_DEFINITIONS.H          */
/*                                                        */
/**********************************************************/

#endif
