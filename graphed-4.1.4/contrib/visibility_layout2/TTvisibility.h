/*************************************************************/
/*                                                           */
/* FILE: TT_VISIBILITY.H                                     */
/*                                                           */
/* Beschreibung: enthaelt fuer die Algorithmen von Tam/Tol   */
/*               und Ros_Tar benoetigte Deklarationen        */
/*                                                           */
/*************************************************************/

#include "visibility_definitions.h"

typedef struct visibility_node_info { /* Knotenattribute */
            int    level; /* Laenge des laengsten Weges */
            int    xl; /* x-Koordinate des linken Endpunkts */
            int    xr; /* x-Koordinate des rechten Endpunkts */
            bool   is_dummy; /* Dummy-Knoten? */
} *Visibility_Node_Info;

typedef struct visibility_edge_info { /* Kantenattribute */
            Snode  left_face; /* linkes Face im dualen Graphen */
            Snode  right_face; /* rechtes Face im dualen Graphen */
            int    x; /* x-Koordinate des Segments */
            bool   is_dummy; /* Dummy-Kante? */
} *Visibility_Edge_Info;

typedef struct visibility_face_info { /* Faceattribute */
            Slist  right_boundary; /* Kanten am rechten Rand */
            Slist  left_boundary; /* Kanten am linken Rand */
            Snode  lowpoint; /* Knoten mit kleinster Nummer */
            Snode  highpoint; /* Knoten mit groesster Nummer */
} *Visibility_Face_Info;

typedef struct duality_edge_info { /* Kantenattribute, die von der  */
            Snode  left_face;      /* Funktion construct_dual_graph */
            Snode  right_face;     /* zurueckgegeben werden         */
} *Duality_Edge_Info;


extern bool epsilon_visibility_error (Sgraph graph);
extern Sgraph TT_w_visibility_biconnected (Sgraph graph, Snode s, Snode t, Snode dummy_node, Slist dummy_edges, int contains_dummies, Visibility_Type type);
extern void TT_w_visibility (Sgraph graph, Snode s, Snode t, Sedge dummy_edge, Visibility_Type type);
extern void TT_epsilon_visibility (Sgraph graph, Snode s, Snode t, Sedge dummy_edge);
extern Slist construct_biconnected_graph (Sgraph graph);
extern Snode construct_epsilon_visibility_biconnected_graph (Sgraph graph);
extern Sgraph construct_block_cutpoint_tree (Sgraph graph, Snode s);
extern void remove_block_cutpoint_tree (Sgraph block_cutpoint_tree);
extern void st_numbering (Sgraph graph, Snode s, Snode t);
extern Sgraph construct_dual_graph (Sgraph graph, Snode s, Snode t);
extern void compute_length_of_longest_path (Sgraph graph, Snode s);
extern void construct_w_visibility_representation (Sgraph graph, Snode s, Snode t, Snode dummy_node, Slist dummy_edges, int contains_dummies, Visibility_Type type);
extern void construct_epsilon_visibility_representation (Sgraph graph, Sgraph dual_graph);
extern void compute_size_of_TT_drawing (Sgraph graph);
extern void free_visibility_node_info (Sgraph graph);
extern void free_visibility_edge_info (Sgraph graph);
extern void free_visibility_face_info (Sgraph dual_graph);
extern Snode highest_node_in_graph (Sgraph graph);
extern Snode highest_node_in_nodelist (Slist nodelist);
extern Snode compute_source (Sgraph graph);
extern Slist compute_sinks (Sgraph graph);



/*************************************************************/
/*                                                           */
/*                END OF FILE: TT_VISIBILITY.H               */
/*                                                           */
/*************************************************************/
