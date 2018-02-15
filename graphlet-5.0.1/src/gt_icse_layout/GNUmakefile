#
# Makefile for icse_layout
#
# ---------------------------------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/gt_icse_layout/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.2 $
# $Date: 1999/03/05 20:45:34 $
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

MODULE = gt_icse_layout

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
	gt_icse_layout.cpp \
	icse_layout.cpp \
	icse_algorithm.cpp

HFILES = \
	gt_icse_layout.h \
	icse_layout.h \
	icse_algorithm.h

CFILES = $(MYCFILES)


#
# include common definitions
#

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/common


ifdef GT_SUPPORT_SHARED_LIBRARIES
    LDFLAGS += $(LD_PATH_OPTION)$(INSTALL_LIB_DIR) -lgt_cfr_layout
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







