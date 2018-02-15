#
# Makefile for gt_radial_tree_layout
#
# ---------------------------------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/gt_radial_tree_layout/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.3 $
# $Date: 1999/03/05 20:45:48 $
# $Locker:  $
# $State: Exp $
# ----------------------------------------------------------------------
#
# (C) University of Passau 1995-1999, Graphlet Project
#

#
# the target "it" is the first target in the makefile. It will be
# executed if you just type "make".
#


#
# Name of the module
#

MODULE = gt_radial_tree_layout

#
# Start of the world
#

GRAPHLET_BASE_DIR=../..

#
# Default target
#

all it:	make_library


#
# My files
#

MYCFILES= \
	gt_radial_tree_layout.cpp \
	radial_tree_layout.cpp \
	radial_tree_layout_algorithm.cpp \
	radial_auxiliary.cpp

CFILES = $(MYCFILES)

HFILES = \
	gt_radial_tree_layout.h \
	radial_tree_layout.h \
	radial_auxiliary.h \
	radial_tree_layout_algorithm.h

#
# include common definitions
#

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/common

ifdef GT_SUPPORT_SHARED_LIBRARIES
    LDFLAGS += $(LD_PATH_OPTION)$(INSTALL_LIB_DIR) -lgt_tree_layout
endif

#
# include dependencies
#

include GNUmakefile.depend

#
# Installation instruction
#

install.local::	install.library install.headers


##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; tab-width: 4 ***
# ;;; tab-stop-list: (4 8 12 16 20 24 28 32 36 40 44 48 52 56 60 64 68 72) ***
# ;;; End: ***
