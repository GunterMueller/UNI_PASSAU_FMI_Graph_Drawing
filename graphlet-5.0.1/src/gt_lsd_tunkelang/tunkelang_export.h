/* This software is distributed under the Lesser General Public License */
extern void call_tunkelang(Sgraph_proc_info info, int edgelength, int quality,
	int rec_depth, int randomize, int cut_value, int scan_corners);
extern void tunkelang(Sgraph_proc_info info);
extern void shake(Sgraph_proc_info info);

extern GraphEd_Menu_Proc tunkelang_layout;
extern GraphEd_Menu_Proc shake_graph;

