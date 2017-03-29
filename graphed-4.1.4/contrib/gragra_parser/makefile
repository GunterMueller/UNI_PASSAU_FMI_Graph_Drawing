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
CSOURCES = bitset.c lp_create_optimal_graph.c convert.c lp_datastruc.c	\
	debug.c lp_main.c glob_var.c lp_redraw.c			\
	gram1_cf.c main_sf.c gram2_cf.c parser.c gram_opt.c reduce.c	\
	graph_op.c trace_cf.c lab_int.c tracer.c lp_convert.c types.c	\
	lp_cost_struc.c w_memory.c lp_costs.c win_defs.c		\
	lp_create_layout_struc.c


HSOURCES = \
	bitset.h lp_datastruc.h convert.h lp_datastructures.h debug.h	\
	lp_main.h gragra_parser_export.h lp_redraw.h gram1_cf.h main_sf.h \
	gram2_cf.h misc.h gram_opt.h parser.h graph_op.h pm_defs.h	\
	lab_int.h reduce.h lp_convert.h trace_cf.h lp_cost_struc.h	\
	tracer.h lp_costs.h types.h lp_create_layout_struc.h w_memory.h	\
	lp_create_optimal_graph.h win_defs.h

MYFILES = $(HSOURCES) $(CSOURCES)

MODULE=gragra_parser
include ../makefile.template
include $(GRAPHEDDIR)/makefile.template

CFLAGS +=
CCINCLUDE +=

include makedepend
