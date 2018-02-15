extern bool error_empty_sgraph;
extern bool error_empty_node;
extern bool error_no_edges;
extern bool error_one_node;
extern bool error_no_inner_face;
extern bool error_crossings;
extern bool error_overlap;
extern bool remark;

extern	void	call_layout_info (Sgraph_proc_info info);
extern	void	call_output_long_statistics (Sgraph_proc_info info);

typedef struct layout_info_edge_list {
  Sedge edge;
  int is_source;
  double angle;
  struct layout_info_edge_list *pre,*suc;
  } *Edge_List;

typedef struct angles_list {
  double angle;
  struct angles_list *suc;
  } *Angles_List;

typedef struct myedgeattrs {
  double length;
  bool undir_loop;
  Angles_List angles_list;  
  } *MyEdgeAttrs;

typedef struct mynodeattrs {
  Edge_List edge_list;
  } *MyNodeAttrs;

typedef	struct {
  double shortest, average, longest, variance, ratio;
}
  NodeDistances;

typedef	struct {
  double shortest, average, longest, variance, ratio;
}
  EdgeLengths;

typedef struct {
  double min,average,max, variance, ratio;
} 
  Angles_Value;

typedef struct {
  double min,average,max, variance, ratio;
} 
  Faces_Value;

typedef struct {
  double used, width, height;
} 
  Area_used;

extern	int		countNodes(Sgraph sgraph);
extern	int		countEdges(Sgraph sgraph);
extern	Area_used	AreaOfDrawing(Sgraph sgraph);
extern	int		nr_of_crossings(Sgraph sgraph);
extern  void   	 	compute_my_attrs(Sgraph sgraph);
extern  NodeDistances 	ComputeNodeDistances(Sgraph sgraph);
extern  EdgeLengths  	ComputeEdgeLengths(Sgraph sgraph);
extern  Angles_Value 	ComputeAngleInfo (Sgraph dualgraph);
extern  Faces_Value   	ComputeFaceInfo (Sgraph dualgraph);
extern	int		count_nr_of_bends (Sgraph sgraph);

typedef	struct	{
  int 	        number_of_nodes;
  int	        number_of_edges;
  int	        size;

  /* double	area_used; */
  Area_used	area;

  double	density;
  NodeDistances node_distances;

  int		number_of_bends;
  int		number_of_crossings;
  EdgeLengths   edge_lengths;
  Angles_Value  angles;
  Faces_Value   faces;
}
  Layout_info;

extern	Layout_info	layout_info(Sgraph sgraph);

extern Layout_info linfo;

extern Sgraph compute_inner_faces_and_angles(Sgraph sgraph);

typedef struct cycle_list {
  Sedge edge;
  bool is_source;
  struct cycle_list *pre,*suc;
  } *Cycle_List;

typedef struct mydualnodeattrs {
  double face;
  Snode start_node;
  Sedge start_edge;
  Cycle_List cycle_list;
  Angles_List angles_list;
  } *MyDualNodeAttrs;

typedef struct dot_list {
  double x,y;
  struct dot_list *pre,*suc;
  } *Dot_List;

extern void show_info_subframe(void);
extern int showing_info_subframe(void);

extern GraphEd_Menu_Proc menu_info_show_subframe;

typedef struct {
  int dummy;
} Info_settings;

extern Info_settings info_settings;
extern Info_settings init_info_settings(void);
extern void save_info_settings(void);


extern void remove_all_my_attrs(Sgraph sgraph);
extern void remove_mynodeattrs(Sgraph sgraph);
extern void remove_myedgeattrs(Sgraph sgraph);
extern void remove_mydualnodeattrs(Sgraph dualgraph); 
