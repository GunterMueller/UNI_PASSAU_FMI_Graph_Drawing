#
# Makefile
#
# This file is the main Makefile for the graphlet System.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/gt_lsd_tree_layout_walker/GNUmakefile,v $
# $Author: bachl $
# $Revision: 1.2 $
# $Date: 1999/02/12 12:12:33 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1995, graphlet project
#

MODULE	= gt_lsd_tree_layout_walker
GRAPHLET_BASE_DIR=../..

SUBDIRS=

it all:	make_library

MYCFILES = \
	gt_lsd_tree_layout_walker.cpp \
	lsd_tree_sf.cpp \
	tree_layout.cpp \
	walker_checks.cpp
HFILES = \
	gt_lsd_tree_layout_walker.h \
	tree_layout_walker_export.h \
	walker_checks.h

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
    LDFLAGS += $(LD_PATH_OPTION)$(INSTALL_LIB_DIR) -lgt_tree_layout
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
