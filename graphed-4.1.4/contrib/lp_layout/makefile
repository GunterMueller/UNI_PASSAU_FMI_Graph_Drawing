# (C) Universitaet Passau, Michael Himsolt 1986-1991
#########################################################################
#									#
#			    GRAPHED MAKEFILE				#
#									#
#########################################################################


#
# Directories
#
GRAPHEDDIR = ../..

#
# Sourcefiles
#
CSOURCES = lp_general_functions.c lp_win_help.c lp_subframe.c \
	   lp_lgg_to_graphed.c lp_test.c lp_edgeline.c lp_top_sort.c \
	   lp_history.c lp_assign_edge.c lp_1_pass.c lp_2_pass.c \
	   lp_3_pass.c lp_4_pass.c lp_5_pass.c lp_6_pass.c lp_7_pass.c \
	   lp_tree_top_sort.c lp_attribute_init_and_clear.c lp_draw.c \
	   lp_reduce.c lp_open.c lp_free_tree.c lp_delete.c lp_routing.c \
	   lp_adjust.c lp_draw_tree.c lp_make_changes_in_productions.c \
	   lpm_remove_tree.c  lpm_iso_test.c lpm_history.c lpm_create_lgg.c  \
	   lpm_multi_functions.c lpm_2_pass.c lpm_3_pass.c lpm_4_pass.c \
	   lpmo_4_pass.c lpmo_3_pass.c lpm_create_lgg_oriented.c \
	   lpp_functions.c lpp_graph_functions.c lpp_iso.c lpp_parse.c \
	   lpp_attributes.c lpp_create_attribute_structures.c lpp_tree.c \
	   lpp_clear_table.c lpp_1_pass.c lpp_2_pass.c lpp_table_to_tree.c \
	   lpp_1_pass_layout.c lpp_redraw.c lpr_optimize_nodes.c \
	   lpr_eedge.c lpr_nnode.c lpr_ggraph.c lpr_apply_production.c \
	   lpr_hhierarchie.c lpr_optimal_layout.c lpr_lrp.c lpr_plr_system.c \
	   lpr_glr_system.c lpr_seq_plrs.c lpr_compute_pos_ass.c \
	   lpr_compute_layout.c lpr_top_down_cost_opt.c lpa_draw_new_tree.c \
	   lpa_create_struc.c  lpa_optimization_structures.c lpa_redraw.c \
	   lpa_create_area_struc.c lpa_create_sizes.c lpr_comp_properties.c

HSOURCES =  lp_general_functions.h lp_win_help.h lp_subframe.h lp_delete.h \
	   lp_lgg_to_graphed.h lp_top_sort.h lp_history.h lp_assign_edge.h \
	   lp_1_pass.h lp_2_pass.h lp_3_pass.h lp_4_pass.h lp_5_pass.h \
	   lp_6_pass.h lp_7_pass.h lp_tree_top_sort.h lp_free_tree.h lp_test.h \
	   lp_attribute_init_and_clear.h lp_draw.h lp_reduce.h lp_open.h \
	   lp_routing.h lp_adjust.h lp_draw_tree.h lpm_4_pass.h \
	   lp_make_changes_in_productions.h lpmo_4_pass.h lpmo_3_pass.h \
	   lpm_remove_tree.h lpm_iso_test.h lpm_history.h lpm_create_lgg.h \
	   lpm_multi_functions.h lpm_2_pass.h lpm_3_pass.h \
	   lpm_create_lgg_oriented.h lpp_functions.h lpp_graph_functions.h \
	   lpp_iso.h lpp_parse.h lpp_attributes.h lpp_1_pass.h lpp_2_pass.h \
	   lpp_create_attribute_structures.h lpp_clear_table.h lpp_tree.h \
	   lpp_table_to_tree.h lpp_1_pass_layout.h lpp_redraw.h lpr_eedge.h \
	   lpr_nnode.h lpr_ggraph.h lpr_apply_production.h lpr_hhierarchie.h \
	   lpr_optimal_layout.h lpr_lrp.h lpr_plr_system.h lpr_glr_system.h \
	   lpr_seq_plrs.h lpr_compute_pos_ass.h lpr_compute_layout.h \
	   lpr_top_down_cost_opt.h lpr_optimize_nodes.h  lpa_create_struc.h\
	   lpa_redraw.h  lpa_optimization_structures.h lpa_create_area_struc.h \
	   lpa_create_sizes.h lpa_draw_new_tree.h lpr_comp_properties.h \
	   lp_graph.h lpa_graph.h lpr_graph.h lp_edgeline.h lpr_plrs.h

MYFILES = $(HSOURCES) $(CSOURCES)

MODULE=lp_layout
include ../makefile.template
include $(GRAPHEDDIR)/makefile.template

CFLAGS += -DLP_LAYOUT
include makedepend
