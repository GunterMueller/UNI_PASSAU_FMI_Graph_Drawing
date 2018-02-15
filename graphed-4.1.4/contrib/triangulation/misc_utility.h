/***************************************************************/
/*                                                             */
/*  filename:  misc_utilily.h                                  */
/*  filetype:  Header-File                                     */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/


extern void remove_all_triangulation_edges(Sgraph inputgraph);
extern int number_of_nodes_in_the_inputgraph(Sgraph inputgraph);
extern Sedge make_a_prescribed_edge(Snode a, Snode b);
extern Sedge make_a_triangulation_edge(Snode a, Snode b);
extern Snode *create_an_array_for_the_nodes(Sgraph inputgraph, int *n);
extern int degree_of_node(Snode node);
extern int get_konvex_quadrilateral(Sedge ab, Sedge *ac, Sedge *ad, Sedge *bd, Sedge *bc, Snode *c, Snode *d);

/***************************************************************/
