/*****************************************************************/
/*                                                               */
/* FILE: KANT_VISIBILITY.H                                       */
/*                                                               */
/* Beschreibung: fuer Kant-w-visibility benoetigte Deklarationen */
/*                                                               */
/*****************************************************************/



typedef struct triangle { /* separierendes Dreieck */
           Sedge edge1;
           Sedge edge2;
           Sedge edge3;
           Snode u;
           Snode v;
           Snode w;
} *Triangle;

typedef struct tree_edge_info { /* Kantenattribut fuer den 4-block tree */
           Snode    source;
           Triangle triangle_in_source;
           Triangle triangle_in_target;
} *Tree_Edge_Info;

typedef struct tree_result { /* Resultattyp fuer die Funktion */
           Sgraph tree;      /* construct_4_block_tree        */
           Snode  root;
} *Tree_Result;


#define TREE_EDGE_INFO(edge) (attr_data_of_type((edge),Tree_Edge_Info))
#define TREE_EDGE_SOURCE(edge) (TREE_EDGE_INFO(edge)->source)
#define TRIANGLE_IN_SOURCE(edge) (TREE_EDGE_INFO(edge)->triangle_in_source)
#define TRIANGLE_IN_TARGET(edge) (TREE_EDGE_INFO(edge)->triangle_in_target)
#define TRIANGLE(x) (attr_data_of_type((x),Triangle))
#define make_attr_triangle(triangle) (make_attr(ATTR_DATA,(char*)(triangle)))
#define add_triangle_to_slist(list,triangle) (add_immediately_to_slist((list),make_attr_triangle(triangle)))


extern void        Kant_w_visibility (Sgraph graph, Snode s, Snode t, Sedge dummy_edge);
extern int         number_of_edges (Sgraph graph);
extern Slist       visibility_layout2_triangulate (Sgraph graph);
extern Tree_Result construct_4_block_tree (Sgraph graph, Sedge st_edge);
extern void        remove_4_block_tree (Sgraph tree, Sgraph graph);
extern bool        equal_edge (Sedge edge1, Sedge edge2);
extern void        canonical_4_ordering (Sgraph graph, Snode u, Snode v, Snode w);



/*****************************************************************/
/*                                                               */
/*                 END OF FILE: KANT_VISIBILITY.H                */
/*                                                               */
/*****************************************************************/
