/* Here are the globally needed variables and structures !!!!!!!!!!!!!!!! */
#include <sgraph/std.h>
#include <sgraph/sgraph.h>

#define graphed_xsize            800
#define graphed_ysize            800

#define virtual_x_size           500
#define virtual_y_size           500

/* ---------------------------------------------------------------------- */

/* For the adjacency-list */
struct nachfolger                   
{
 int nummer;
 struct nachfolger *next;
};


/* For the edgelist      */
struct kante
{
 int von;
 int nach;
 struct kante *next;
};


/* ---------------------------------------------------------------------- */


extern struct kante *edgelist;

extern Snode             *iso_node; 

extern struct kante      ***edgelist_matrix; 

extern struct nachfolger **real_adjacency_list;            
extern struct nachfolger **actual_adjacency_list;

extern int **adjacency_matrix;               
extern int **which_edges_have_been_counted;
extern int **adjacency_matrix_for_saved_graph;

extern int *real_x_coord;
extern int *real_y_coord;
extern int *zoomed_x_coord;
extern int *zoomed_y_coord;

extern int *is_visited;
extern int *number_of_node_with_rank;
extern int *rank_of_node;
extern int *node_is_already_placed;
extern int *height_value;
extern int *degree;
extern int *degree_to_placed_nodes;

/* ---------------------------------------------------------------------- */

extern int nodes; 
extern int edges;                   
extern int iterations;
extern int make_edge_count_unambigious;
extern int cut_number;
extern int placed_nodes;
extern int count_cuts;
extern int cuts_at_end;
extern int scan_corners;
extern int animation;
extern int randomize;
extern int stepwise_image;
extern int drawing_area;
extern int number_of_image_to_save;
extern int end_fine_tuning;
extern int recursion_depth;
extern int width_of_cell;
extern int quit_the_algorithm;
extern int animation_not_possible;
extern int no_memory;
extern int not_all_images_saved;
extern int minimum_edge_length;
extern char image_directory[100];
/* Note: initial value is 0: ST 260 C111 I2(2), p. 213 */

/* ---------------------------------------------------------------------- */

extern float node_node_weight, crossing_weight, node_edge_weight;
extern int l;

/* ---------------------------------------------------------------------- */

  
