extern void compute_layout_restriction_par(lpr_Node node, lpr_Graph father);
extern lpr_Iso_edge get_optimal_edge_of_lpr_edge(lpr_Graph prod, lpr_Edge lpr_edge);
extern Node get_optimal_node_of_lpr_node(lpr_Graph prod, lpr_Node lpr_node);
extern lpr_Track_sharing get_track_sharing(lpr_Graph prod, int side, int track_nr);
extern lpr_Track_ass_des get_track_ass_des(lpr_Edge edge, lpr_Graph prod);
extern void testprint_lrp(lpr_Node node);
