/******************************************************************/
/*                                                                */
/* FILE: NUMMENMAA_VISIBILITY.H                                   */
/*                                                                */
/******************************************************************/


extern void Nummenmaa_w_visibility (Sgraph graph, Snode s, Snode t, Sedge dummy_edge);
extern int number_of_edges (Sgraph graph);
extern Slist visibility_layout2_triangulate (Sgraph graph);
extern Slist canonical_ordering (Sgraph graph, Snode u, Snode v, Snode w);
