/* main functions */
extern void minimal_bends_proc (Sgraph_proc_info info);

/* menu functions */
void	save_minimal_bends_settings2 ();
struct minimal_bends_settings2 init_minimal_bends_settings2(void);
void	show_minimal_bends_subframe (void);
int	showing_minimal_bends_subframe(void);
void	 	 check_and_call_minimal_bends_layout2 (Sgraph_proc_info info);
GraphEd_Menu_Proc	menu_minimal_bends_layout2;
GraphEd_Menu_Proc	menu_minimal_bends_layout2_settings;
int	test_minimal_bends_layout2 (Sgraph_proc_info info);


/* tool functions */
SFace		insert_Face (SFace Face);
SFaceElement    insert_FaceElement(SFaceElement elements, Snode node, Sedge edge);
SFaceList       new_Facelist(void);
void 	        reset_edge_visited(Sgraph sGraph);
void            set_gridpoint_null(Sgraph sGraph);
void            free_NonRelatedList (SNonRelatedList nlist);
SNonRelatedList prepend_NonRelatedList (SNonRelatedList nlist, SGridLine line1, SGridLine line2);



/* functions used while creating facelist */
float get_angle(Snode base, Spoint sfrom, Spoint sto);
Snode top_left (Sgraph sGraph);
Sedge next_edge (Snode node, Spoint sfrom);
SFaceElement find_outer_face (Sgraph sGraph);
struct find_face_ret find_face(Sgraph sGraph, Sedge sStartEdge);
void  member_edges(SFace face);
void  edge_visited(Sedge edge);
int   edge_visited_double(Sedge edge);
SFaceList find_faces(Sgraph sGraph);

/* tools for creating the network */
Snetnode InsertNetNode(Snetnode *pnetnode, enum netnode type);
Snetedge insert_net_edge(Snetwork network, Snetnode snode, Snetnode tnode, int capacity, int cost);
Snetwork new_network(void);
Sedgelist new_path(Sedgelist spath);
Sedgelist insert_path(Sedgelist spath);
void free_path(Sedgelist path);


/* transforming the network back to a graph */



/* min-cost-flow-algorithm */
Sedgelist  Dijkstra (Snetwork network, Sedgelist spath);
void ChangeNetwork (Snetwork network, Sedgelist pedges, int flowchange);
int  GetFlowPlus (Sedgelist spath, int flow, int flowval);
void GetMinCostFlow(Snetwork network);
int MinCostFlow (Snetwork network);

/* transform the graph to a network */
SFaceList invert_face_element(SFaceList sFaceList);
int    no_of_sources(Snode node);
void process_edge_fv(Snetwork network, SFaceElement elements, Snetnode tnode);
void insert_fv_edges(Snetwork network, SFaceList sFaceList);
int same_edge(Sedge edge, Sedge compedge);
int bridge_in_face(SFaceElement faceelement);
int face_face(SFaceElement face, SFaceElement compface);
void insert_ff_edges(Snetwork network, SFaceList sFaceList);
void insert_sf_sv_edges(Snetwork network, SFaceList sFaceList);
void insert_ft_vt_edges(Snetwork network, SFaceList sFaceList);
Snetwork TransformToNetwork(Sgraph sGraph, SFaceList sFaceList);

/* retransformation tools */
SShape create_shape(int angle, int dir, SGridPoint spoint, SGridPoint tpoint, SShape pre, SShape suc, int iscopy);
/*SBlock create_block(side1, side2, side3, side4, connection, blocklist);*/
SGridPointList create_gridpoint_list(SGridPoint point, SGridPointList pre);
SGridLineList create_line_list(SGridLine line, SGridLineList pre);
SFaceShape create_faceshape(SShape shape, SGridLineList xPos, SGridLineList yPos, SFaceShape pre, SFaceShape suc);
SGrid create_grid(SGridLineList xlines, SGridLineList ylines);
SGridPoint create_gridpoint(SGridLine xline, SGridLine yline);
SGridLine create_gr_line(SGridPoint pt, SGridLine min);
void store_alloc(char *newalloc, int add);
void free_shape(SShape shape);

/* build orthogonal representation */
SFace get_left_face(Sedge edge, SFace rightface, SFace outerface);
SShape get_bends(SFaceElement faceelement, SFace rightface, SFace leftface, int direction);
SFace find_ort_rep(Snetwork network, SFaceList facelist);
SShape get_first_shape(SFaceElement curelement, int direction, SFace curface, int isOuterFace);
SFaceElement adapt_shape(SFaceElement curelement, SFace leftface, int direction);
void build_ort_rep (Snetwork Network, SFaceList FaceList);

/* grid embedding */
SFaceShape get_faceshape(SGrid facegrid, SFace face);
void set_targetpoints(SFace face);
void adapt_minors(SGridLineList minline, SGridLineList linelist);
SGridLineList get_min_line (SGridLineList linelist);
void sort_gridlines(SGridLineList linelist);
void build_majorlists(SGridLineList linelist);
void fill_tminors(SGridLineList minline, SGridLineList linelist);
void build_tlist(SGridLineList linelist);
void assign_shape_minors(SShape shape);
void get_gridline_minors(SFaceList facelist, SGrid grid);
SGridLine get_merge_line(SGridLine line1, SGridLine line2, SGridPoint point);
SGridLine merge_lines(SGridLine line1, SGridLine line2, SGridPoint point, SGridLineList linelist);
SShape assign_lines_right_left(SShape shape, SGrid grid);
SShape assign_lines_up_down(SShape shape, SGrid grid);
void update_maj_min_list (SGridLine minline, SGridLine majline); 
SGrid build_facegrid(SFace face, SGrid grid);
SGridLineList get_pos(SGridLineList linelist, SGridLine line);
SGridLineList in_crossed(SGridLineList xline, SGridLineList yline, SShape shape);
int get_linesno(SGrid facegrid);
int test_sort(SGrid facegrid, SFaceShape faceshape);
void set_sort_minors(SGrid facegrid);
void add_gridline_minors(SFaceList facelist, SGrid grid);
void assign_gridlines(SFace face, SGrid grid);
SGrid grid_embed(SFaceList facelist);



/* change GraphEd */
void change_graph(SFaceList Facelist, Sgraph graph);
void process_face_elements(SFace Face);
void set_edgeline(Graphed_edge GEdge, int endX, int endY, SShape shape);
void invert_edge(SFaceElement element);

/* functions used for freeing data structure */
void free_gridpointlist (SGridPointList list);
void free_data_structures(Snetwork network, SFaceList facelist, SGrid sGrid);
void free_facelist_structures(SFaceList facelist);
void free_grid_structures(SGrid sGrid);
void free_network_structures(Snetwork network);
void free_snetnodes(Snetnode nodelist);
void free_sedgelist(Sedgelist edgelist);
void free_snetedges(Snetedge netedgelist);
void free_sgridlinelists(SGridLineList linelist);
void free_elements(SFaceElement elementlist);
void free_clines(SGrid grid);
void free_facegrid(SGrid facegrid);
void free_majors(SGrid grid);
void free_faceminors(SGrid grid);
void free_faceshape(SFaceShape faceshape);

void insert_attributes(Sgraph sGraph);
void free_attributes(Sgraph sGraph);
