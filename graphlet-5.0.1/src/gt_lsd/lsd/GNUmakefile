#
# Makefile
#
# This file is the main Makefile for the graphlet System.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/gt_lsd/lsd/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.1.1.1 $
# $Date: 1998/08/27 17:19:18 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1995, graphlet project
#

MODULE	= gt_lsd
GRAPHLET_BASE_DIR=../../..

SUBDIRS=

it all:	make_library

##############################
#
# my sources
#
##############################

#
#---------- LSD
#

MYCFILES = ge_dummy.cpp ls_assoc.cpp lsd.cpp lsd_mref.cpp trace.cpp
HFILES = algs_imp.h ge_dummy.h ls_assoc.h lsd.h lsd_mref.h lsdstd.h atrace.h

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
