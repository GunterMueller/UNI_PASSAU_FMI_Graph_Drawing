#
# Makefile
#
# This file is the main Makefile for the graphlet System.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/gt_lsd/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.1.1.1 $
# $Date: 1998/08/27 17:19:17 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1995, graphlet project
#

MODULE	= gt_lsd
GRAPHLET_BASE_DIR=../..

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/modules

it all:	make_library

##############################
#
# my sources
#
##############################

MYCFILES = \
	gt_lsd.cpp \
	lsd/ge_dummy.cpp \
	lsd/ls_assoc.cpp \
	lsd/lsd.cpp \
	lsd/lsd_mref.cpp \
	lsd/trace.cpp \
	sgraph/attrs.cpp \
	sgraph/sedge.cpp \
	sgraph/sgraph_.cpp \
	sgraph/sprod.cpp \
	sgraph/graphed.cpp \
	sgraph/sembed.cpp \
	sgraph/slist.cpp \
	sgraph/std.cpp \
	sgraph/salpha.cpp \
	sgraph/sgragra.cpp \
	sgraph/snode.cpp \
	sgraph/utils_move_rotate.cpp


HFILES = \
	lsd/algs_imp.h \
	lsd/ge_dummy.h \
	lsd/ls_assoc.h \
	lsd/lsd.h \
	lsd/lsd_mref.h \
	lsd/lsdstd.h \
	sgraph/algorithms.h \
	sgraph/random.h \
	sgraph/sgraph_interface.h \
	sgraph/dispatch_commands.h \
	sgraph/sgragra.h \
	sgraph/slist.h \
	sgraph/graphed.h \
	sgraph/sgragra_interface.h \
	sgraph/std.h \
	sgraph/graphed_structures.h \
	sgraph/sgraph.h \
	sgraph/utils_move_rotate.h

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



##############################
# libs and flags
##############################


#
# Installation instruction
#

install.local::	install.library

#
# include dependencies
#

include GNUmakefile.depend


##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; tab-width: 4 ***
# ;;; tab-stop-list: (4 8 12 16 20 24 28 32 36 40 44 48 52 56 60 64 68 72) ***
# ;;; End: ***
