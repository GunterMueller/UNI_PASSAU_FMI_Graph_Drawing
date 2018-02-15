#
# Makefile
#
# This file is the Makefile for graphlet/etc.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.2 $
# $Date: 1999/03/05 20:39:12 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet project
#


GRAPHLET_BASE_DIR=..
SUBDIRS=graphlet graphscript

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/common


all:	$(SUBDIRS)


.PHONY:	configure


clean.local::
	$(RM) lib*.so.* lib*.a


install.local::	
	$(INSTALL_MAKE_DIR) $(INSTALL_LIB_DIR)


##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; tab-width: 4 ***
# ;;; tab-stop-list: (4 8 12 16 20 24 28 32 36 40 44 48 52 56 60 64 68 72) ***
# ;;; End: ***
