#
# GNUmakefile
#
# This file is the default makefile four building a GraphScript interpeter.
#
# First, a definition of "Outside" and "Inside". "Outside
# Graphlet" means one of the following:
#
# - You are using the binary distribution and link with the
#   precompiled libraries
# - You link with the libraries of an installed Graphlet
#
# You are "inside graphlet" if you use this makefile from a CVS
# distribution.
#
# Use this makefile as follows ("gmake" is GNU make)
#
# - "gmake depend" creates dependency information in GNUmakefile.depend
# - "gmake" creates a Graphscript interpreter and a startup scripts.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/graphscript/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.2 $
# $Date: 1999/03/05 20:42:40 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet project
#


#
# GT_EXTERNAL
#
# Uncomment the following line if this GNUmakefile is used
# outside Graphlet. 
#

# GT_EXTERNAL=1


#
# Name of the module (and also name of the executable program)
#

MODULE=graphscript

#
# Start of the world
#
# Note: If you are using GT_EXTERNAL, you might want to modify
# the second line.
#

ifdef GT_EXTERNAL
GRAPHLET_BASE_DIR=/usr/local/graphlet/graphlet-3.0.0-beta-CC
else
GRAPHLET_BASE_DIR=../..
endif


#
# Enter GraphScript startup scripts here.
#
# If you use GT_EXTERNAL, you should modify the first line.
#

ifdef GT_EXTERNAL
GRAPHSCRIPT_STARTUP_SCRIPTS = graphlet
else
GRAPHSCRIPT_STARTUP_SCRIPTS = gmlview graphlet
endif


#
# The target "it" is the first target in the makefile. It will be
# executed if you just type "make".
#

all it:	$(MODULE) $(GRAPHSCRIPT_STARTUP_SCRIPTS)



##########################################
#
# Input files
#
##########################################

#
# Add C++ Files in MYCFILES,
#

MYCFILES = graphscript.cpp 

#
# CFILES = MYCFILES + generated files (e.g. if lex,yacc is used)
#

CFILES = $(MYCFILES)

#
# Header Files
#

HFILES = graphscript.h modules.h


##########################################
#
# include common definitions
#
##########################################

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/common

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/application

include ./GNUmakefile.depend

$(OFILES):	modules.h modules.cpp $(GRAPHLET_BASE_DIR)/.linking

#
# Installation instructions
#

install.local::
	$(INSTALL_MAKE_DIR) $(INSTALL_LIB_DIR)
	$(INSTALL_MAKE_DIR) $(INSTALL_INCLUDE_DIR)/$(MODULE)
	$(INSTALL_FILE) modules.h modules.cpp $(INSTALL_INCLUDE_DIR)/$(MODULE)


###########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; tab-width: 4 ***
# ;;; tab-stop-list: (4 8 12 16 20 24 28 32 36 40 44 48 52 56 60 64 68 72) ***
# ;;; End: ***
