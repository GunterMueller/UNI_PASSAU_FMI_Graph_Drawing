# -----------------------------------------------------------------------
# Makefile for gt_tcl.
#
# -----------------------------------------------------------------------
# ---------------------------------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/gt_tcl/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.3 $
# $Date: 1999/03/05 20:45:50 $
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

MODULE = gt_tcl

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
	Graphscript.cpp \
	Tcl.cpp \
	Tcl_Algorithm.cpp \
	Tcl_Command.cpp \
	Tcl_Edge.cpp \
	Tcl_Graph.cpp \
	Tcl_Graph_configure.cpp \
	Tcl_Graph_handlers.cpp \
	Tcl_Info.cpp \
	Tcl_Node.cpp \
	Tcl_Rotate_Command.cpp \
	Tcl_Scale_Command.cpp \
	Tk_Device.cpp \
	Tk_UIEdge.cpp \
	Tk_UIGraph.cpp \
	Tk_UILabel.cpp \
	Tk_UINode.cpp \
	Tk_UIObject.cpp \
	gt_tcl.cpp

CFILES = $(MYCFILES)
HFILES = $(CFILES:%.cpp=%.h)


#
# include common definitions
#

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/common

ifdef GT_SUPPORT_SHARED_LIBRARIES
    LDFLAGS += -lgt_base
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
