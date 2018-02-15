#
# Makefile
#
# This file is the main Makefile for the graphlet System.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/gt_lsd_gem/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.1.1.1 $
# $Date: 1998/08/27 17:19:20 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#


MODULE	= gt_lsd_gem
GRAPHLET_BASE_DIR=../..
SUBDIRS=

all it:	make_library

MYCFILES =	\
	adtgraph.cpp \
	embedder.cpp \
	gem_main.cpp \
	geometry.cpp \
	global.cpp	\
	gsearch.cpp \
	gt_lsd_gem.cpp \
	interface.cpp \
	lsd_gem_panel.cpp \
	quality.cpp

HFILES = \
	adtgraph.h \
	embedder.h \
	gem_main.h \
	gem_export.h \
	geometry.h \
	global.h \
	gsearch.h \
	gt_lsd_gem.h \
	interface.h \
	quality.h

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
