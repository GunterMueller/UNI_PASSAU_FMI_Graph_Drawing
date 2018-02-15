#
# Makefile for cfr_layout
#
# ---------------------------------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/gt_cfr_layout/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.2 $
# $Date: 1999/03/05 20:45:18 $
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

MODULE = gt_cfr_layout

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
	cfr_algorithm.cpp \
	cfr_layout.cpp \
	cfr_io.cpp \
	cfr_one_dimension.cpp \
	gt_cfr_layout.cpp

HFILES = \
	cfr_algorithm.h \
	cfr_layout.h \
	cfr_io.h \
	cfr_one_dimension.h \
	gt_cfr_layout.h


CFILES = $(MYCFILES)


#
# include common definitions
#

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/common


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
