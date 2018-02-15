#
# Makefile
#
# This file is the Makefile for graphlet/bin.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/bin/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.2 $
# $Date: 1999/03/05 20:39:04 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet project
#


GRAPHLET_BASE_DIR=..
SUBDIRS=

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/common

all:	
	$(CHMOD) a+x gmlpp gmlview graphlet graphed-to-graphlet

.PHONY:	configure
configure:	
	$(RM) $(GRAPHSCRIPT) graphlet gmlview $(GRAPHSCRIPT)
	$(LN) -s $(GRAPHLET_SRC_GRAPHSCRIPT_DIR)/$(GRAPHSCRIPT) $(GRAPHSCRIPT)
	$(LN) -s $(GRAPHLET_SRC_GRAPHSCRIPT_DIR)/graphlet graphlet
	$(LN) -s $(GRAPHLET_SRC_GRAPHSCRIPT_DIR)/gmlview gmlview

#CLEANFILES += $(GRAPHSCRIPT) graphlet gmlview

install.local::
	$(INSTALL_MAKE_DIR) $(INSTALL_DIR)/bin
	$(INSTALL_FILE) gmlpp  gmlview graphlet $(INSTALL_DIR)/bin

##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; tab-width: 4 ***
# ;;; tab-stop-list: (4 8 12 16 20 24 28 32 36 40 44 48 52 56 60 64 68 72) ***
# ;;; End: ***
