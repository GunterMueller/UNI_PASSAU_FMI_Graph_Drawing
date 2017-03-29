#
# Makefile
#
# This file is the main Makefile for the graphlet System.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/gt_lsd_sugiyama/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.1.1.1 $
# $Date: 1998/08/27 17:19:22 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1995, graphlet project
#

MODULE	= gt_lsd_sugiyama
GRAPHLET_BASE_DIR=../..

SUBDIRS=

it all:	make_library

MYCFILES = \
	cros.cpp \
	cycle.cpp \
	gt_lsd_sugiyama.cpp \
	hier.cpp \
	lsd_sugi_sf.cpp \
	pos.cpp \
	recycle.cpp \
	sugi.cpp \
	undirected.cpp

HFILES = \
	gt_lsd_sugiyama.h \
	recycle.h \
	sugiyama_export.h

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
