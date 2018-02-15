/* This software is distributed under the Lesser General Public License */
#include <sgraph/std.h>
#include <sgraph/sgraph.h>

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

struct kante *edgelist;

Snode             *iso_node; 

struct kante      ***edgelist_matrix; 

struct nachfolger **real_adjacency_list;            
struct nachfolger **actual_adjacency_list;

int **adjacency_matrix;               
int **which_edges_have_been_counted;
int **adjacency_matrix_for_saved_graph;

int *real_x_coord;
int *real_y_coord;
int *zoomed_x_coord;
int *zoomed_y_coord;

int *is_visited;
int *number_of_node_with_rank;
int *rank_of_node;
int *node_is_already_placed;
int *height_value;
int *degree;
int *degree_to_placed_nodes;

/* ---------------------------------------------------------------------- */

int nodes;  
int edges;                  
int iterations;
int make_edge_count_unambigious;
int cut_number;
int placed_nodes;
int count_cuts;
int cuts_at_end;
int scan_corners;
int animation;
int randomize;
int stepwise_image;
int drawing_area;
int number_of_image_to_save;
int end_fine_tuning;
int recursion_depth;
int width_of_cell;
int quit_the_algorithm;
int animation_not_possible;
int no_memory;
int not_all_images_saved;
int minimum_edge_length;
char image_directory[100];
/* ---------------------------------------------------------------------- */

float node_node_weight, crossing_weight, node_edge_weight;
int l;

/* ---------------------------------------------------------------------- */

  
