#
# GNUmakefile
#
# Makefile for gt_xdag
#
# ---------------------------------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/gt_xdag/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.1.1.1 $
# $Date: 1998/08/27 17:19:30 $
# $Locker:  $
# $State: Exp $
#
# ----------------------------------------------------------------------
#
# (C) University of Passau 1995-1997, Graphlet Project
#

#
# the target "it" is the first target in the makefile. It will be
# executed if you just type "make".
#


#
# Name of the module
#

MODULE = gt_xdag

#
# Start of the world
#

GRAPHLET_BASE_DIR=../..

#
# Subdirectories
#

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/modules

#
# Default target
#

all it:	make_library

#
# My files
#

MYCFILES= \
	region.cpp \
	dag_node.cpp \
	ordergraph.cpp \
	level.cpp \
	scanline.cpp \
	dag.cpp \
	xdag_algorithm.cpp \
	gt_xdag.cpp

CFILES = $(MYCFILES)

HFILES = \
	dag.h \
	dag_node.h \
	debug.h \
	gt_xdag.h \
	level.h \
	ordergraph.h \
	region.h \
	scanline.h \
	working.h \
	xdag_algorithm.h


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
# ;;; End: ***
