# -----------------------------------------------------------------------
# Makefile for tree layout.       
#
# -----------------------------------------------------------------------
# ---------------------------------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/gt_tree_layout/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.4 $
# $Date: 1999/03/05 20:46:52 $
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

MODULE = gt_tree_layout

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

MYCFILES=  	tree_structure.cpp	\
			checks.cpp \
			layout_alg.cpp \
			tree_algorithm.cpp \
			leveling.cpp \
			permutation.cpp \
			shift.cpp \
			edgeanchor.cpp \
			father_place.cpp\
			node_edge.cpp \
			orth_rout.cpp \
			contour.cpp \
			transform.cpp \
			gt_tree_layout.cpp \
			tree_check.cpp

HFILES = 	tree_structure.h \
			checks.h \
			leveling.h \
			permutation.h \
			shift.h \
			edgeanchor.h \
			tree_algorithm.h \
			gt_tree_layout.h \
			tree_check.h

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
