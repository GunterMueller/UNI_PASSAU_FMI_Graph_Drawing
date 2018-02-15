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
CSOURCES = \
	allocation_procs.c \
	bfs.c \
	create_animation_files.c \
	glob_var_for_algo.c \
	help_for_algo.c \
	help_general.c \
	menu_functions.c \
	random_coords.c \
	sgraph_pre_and_post_processing.c \
	tunkelang_main.c

HSOURCES = \
	allocation_procs.h \
	bfs.h \
	create_animation_files.h \
	glob_var_for_algo.h \
	help_for_algo.h \
	help_general.h \
	menu_functions.h \
	random_coords.h \
	sgraph_pre_and_post_processing.h \
	tunkelang_main.h

MYFILES = $(HSOURCES) $(CSOURCES)

include ../makefile.template
include $(GRAPHEDDIR)/makefile.template

CFLAGS += 
include makedepend
