/* This software is distributed under the Lesser General Public License */
/* 
   if the last parameter is n, then the minimum edge length will be n; 
   if n=0 then zoom such that the graph will exactly fit the window 
   (-> global params graphed_x_size and graphed_y_size)
*/
extern void  zoom_the_graph(int drawing_area_xsize, int drawing_area_ysize, int x_offset, int y_offset, int minimum_edge_length);

extern float compute_cost(int node_number, int *cuts);

extern void  insert_edge_into_hash_table(int i, int j, int cell_width);

extern void  update_hash_table_for_edge(int node1, int node2, int node1_x_old, int node1_y_old, int node1_x_new, int node1_y_new, int cell_width);

extern void  update_structures(int node_number);

extern void  free_my_own_structures(void);


/* 
  input:         a sgraph 
  output:        0 if there were memory-problems
                 1 if there were no such problems
  side-effects:  builds the internally needed initial data-structures
                 and calls the initialization-procedure
*/
extern int   build_internal_data_structure(Sgraph sgraph);
