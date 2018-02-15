# (C) Universitaet Passau, Michael Himsolt 1986-1993
#########################################################################
#									#
#			    GRAPHED makeFILE				#
#									#
#########################################################################


#
# Directories
#
GRAPHEDDIR = ../..
#
# Sourcefiles
#

CSOURCES =	visibility_sf.c \
		selection.c \
		visibility_call.c \
		error_handling.c \
		TT_free_memory.c \
		block_cutpoint_tree.c \
		biconnected_graph.c \
		TT_epsilon.c \
		TTvisibility.c \
		st_numbering.c \
		length.c \
		dual_graph.c \
		TT_weak.c \
		triangulation.c \
		canonical_ordering.c \
		canonical_4_ordering.c \
		Nummenmaa_visibility.c \
		4_block_tree.c \
		Kant_visibility.c \
		compression.c \
		tree_visibility.c

HSOURCES =	$(MODULE)_export.h \
		Kant_visibility.h \
		tree_visibility.h \
		Nummenmaa_visibility.h \
		visibility_definitions.h \
		TTvisibility.h

MYFILES = $(HSOURCES) $(CSOURCES)


include ../makefile.template
include $(GRAPHEDDIR)/makefile.template



it :	$(MODULE).o

include makedepend
