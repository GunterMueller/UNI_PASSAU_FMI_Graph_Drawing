/* main functions */
extern char *minimal_bends_proc (Sgraph_proc_info info);

/* tool functions */
SFace		insert_Face (SFace Face);
SFaceElement    insert_FaceElement(SFaceElement elements, Snode node, Sedge edge);
SFaceList       new_Facelist(void);
void 	        reset_edge_visited(Sgraph sGraph);
void            set_gridpoint_null(Sgraph sGraph);


/* functions used while creating facelist */
float get_angle(Snode base, Spoint sfrom, Spoint sto);
Snode top_left (Sgraph sGraph);
Sedge next_edge (Snode node, Spoint sfrom);
SFaceElement find_outer_face (Sgraph sGraph);
SFaceElement find_face(Sgraph sGraph, Sedge sStartEdge);
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
void free_path(Sedgelist spath);


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
SGridPointList create_gridpoint_list(SGridPoint point, SGridPointList pre);
SGridPoint create_gridpoint(SGridLine xline, SGridLine yline);
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
SGrid grid_embed(SFaceList facelist);
void assign_gridlines(SFace face, SGrid grid);
