#
# Makefile
#
# This file is the main Makefile for the graphlet System.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.3 $
# $Date: 1999/03/05 20:42:38 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet project
#

it:	all

GRAPHLET_BASE_DIR = ..

SUBDIRS= $(BASE_MODULES) $(EXTENSION_MODULES) graphscript

#
# Inter module dependencies
#

# gt_icse_layout reuses parts of gt_cfr_layout

gt_icse_layout::	gt_cfr_layout

# All LSD modules depend on LSD

gt_lsd_gem::			gt_lsd
gt_lsd_springembedder_kamada::	gt_lsd
gt_lsd_springembedder_rf::	gt_lsd
gt_lsd_tunkelang::		gt_lsd
gt_lsd_sugiyama::		gt_lsd
gt_lsd_tree_layout_walker::	gt_lsd

gt_lsd_tree_layout_walker::	gt_tree_layout
gt_radial_tree_layout::		gt_tree_layout
# gt_tcl is based on gt_base
gt_tcl::			gt_base


# The following subdirectories compile without Tcl/Tk
# SUBDIRS=gt_base

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/common


all:	$(SUBDIRS)




##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; End: ***
