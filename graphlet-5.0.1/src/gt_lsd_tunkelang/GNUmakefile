#
# Makefile
#
# This file is the main Makefile for the graphlet System.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/gt_lsd_tunkelang/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.1.1.1 $
# $Date: 1998/08/27 17:19:23 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1995, graphlet project
#

MODULE	= gt_lsd_tunkelang
GRAPHLET_BASE_DIR=../..

SUBDIRS=

it all:	make_library

MYCFILES =	\
	allocation_procs.cpp \
	bfs.cpp \
	create_animation_files.cpp \
	glob_var_for_algo.cpp	\
	gt_lsd_tunkelang.cpp \
	help_for_algo.cpp \
	help_general.cpp \
	menu_functions.cpp \
	random_coords.cpp \
	sgraph_pre_and_post_processing.cpp \
	tunkelang_main.cpp

HFILES = \
	allocation_procs.h \
	bfs.h \
	create_animation_files.h \
	glob_var_for_algo.h \
	gt_lsd_tunkelang.h \
	help_for_algo.h \
	help_general.h \
	random_coords.h \
	sgraph_pre_and_post_processing.h \
	tunkelang_main.h

CFILES = $(MYCFILES)
MYFILES = $(HFILES) $(CFILES)


#
# INCLUDES must start with -I$(GRAPHLET_SRC_GT_LSD_DIR) so that xview
# points ot the gaked headers.
#

INCLUDES += -I$(GRAPHLET_SRC_GT_LSD_DIR)

#
# CFLAGS adds an -D_LSD
#

CCFLAGS += -D_LSD


#
# include common definitions
#

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/common

ifdef GT_SUPPORT_SHARED_LIBRARIES
    LDFLAGS += $(LD_PATH_OPTION)$(INSTALL_LIB_DIR) -lgt_lsd
endif


#
# include dependencies
#

include GNUmakefile.depend

#
# Installation instruction
#

install.local::	install.library


##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; tab-width: 4 ***
# ;;; tab-stop-list: (4 8 12 16 20 24 28 32 36 40 44 48 52 56 60 64 68 72) ***
# ;;; End: ***
