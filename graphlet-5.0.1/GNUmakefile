#
# GNUMakefile
#
# This file is the main Makefile for the graphlet System.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.2 $
# $Date: 1999/03/05 20:39:02 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet project
#


GRAPHLET_BASE_DIR=.
SUBDIRS=bin src lib

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/common

all:	.linking $(SUBDIRS)

allnew:	configure modules clean depend all

configure:
	for i in `$(FIND) . -name GNUmakefile -print` ; do \
		depend=`dirname $$i`/GNUmakefile.depend; \
		if [ ! -f $$depend ]; then \
			$(TOUCH) $$depend ; \
		fi ; \
	done
	if [ ! -d include ]; then \
		$(LN) -s src include; \
	fi
	$(RM) $(GRAPHLET_SRC_GT_BASE_DIR)/config.h
	$(RM) $(GRAPHLET_SRC_GRAPHSCRIPT_DIR)/modules.h
	$(RM) $(GRAPHLET_SRC_GRAPHSCRIPT_DIR)/modules.cpp
	$(RM) $(GRAPHLET_GRAPHSCRIPT_DIR)/modules.tcl
	$(MAKE) -C $(SUB_CONFIG_DIR) $@
	$(MAKE) -C $(SUB_BIN_DIR) $@
	$(MAKE) -C $(SUB_GRAPHSCRIPT_DIR) $@


modules:	
	(cd $(SUB_CONFIG_DIR); $(MAKE) configure.modules)

static:
	$(MAKE) LINKING=static


#
# Determine wether linking has changed between the last build
#

.PHONY:	.linking
ifeq ($(LINKING),static)
.linking:	
	if test \! -f .static_linking ; then \
	    $(TOUCH) .linking ; \
	    $(TOUCH) .static_linking ; \
	    $(RM) -f .dynamic_linking ; \
	fi
else
.linking:	
	if test \! -f .dynamic_linking ; then \
	    $(TOUCH) .linking ; \
	    $(TOUCH) .dynamic_linking ; \
	    $(RM) -f .static_linking ; \
	fi
endif

CLEANFILES += .linking .static_linking .dynamic_linking



##########################################
#
# Installation
#
##########################################

.PHONY:	install
install:	
	if [ -r $(TCL_LIB_DIR)/lib$(TCL_LIB).a -a \
	     -r $(TK_LIB_DIR)/lib$(TK_LIB).a -a \
	     -r $(GTL_LIB_DIR)/libGTL.a \
	]; then \
	    $(MAKE) LINKING=static ; \
		$(MAKE) LINKING=static install.target ; \
	else \
		$(MAKE) install.target ; \
	fi



##########################################
#
# Source code distribution
#
##########################################


.PHONY:	distribution.source

distribution.source:	tag-with-version
	$(RM) -r $(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_VERSION_DIR)
	$(CVS) export \
		-d $(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_VERSION_DIR) \
		-r $(subst .,-,$(GRAPHLET_VERSION_DIR)) graphlet
	(cd	$(GRAPHLET_DISTRIBUTION_DIR); \
		$(TAR) cvf - ./$(GRAPHLET_VERSION_DIR) | \
		$(GZIP) > $(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_VERSION_DIR).tar.gz)
	(cd	$(GRAPHLET_DISTRIBUTION_DIR); \
		$(RM) -r ./$(GRAPHLET_VERSION_DIR))
#	$(CP) $(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_VERSION_DIR).tar.gz \
#		$(GRAPHLET_ARCHIVE_DIR)



##########################################
#
# Binary distribution
#
##########################################


.PHONY:	distribution.binary

install.distribution.binary:
	$(RM) -r $(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_BINARY_DIR)
	$(MAKE) install "INSTALL_DIR=$(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_BINARY_DIR)"

distribution.binary:	install.distribution.binary
	$(CP) README ACKNOWLEDGEMENTS PEOPLE INSTALL COPYRIGHT \
		$(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_BINARY_DIR)
	$(RM) $(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_BINARY_DIR).tar.gz
	(cd $(TCL_LIB_DIR); $(TAR) cvf - ./$(TCL_TCL_LIBRARY)) | \
		(cd $(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_BINARY_DIR)/$(SUB_LIB_DIR); \
		$(TAR) xvf -)
	(cd $(TK_LIB_DIR); $(TAR) cvf - ./$(TK_TCL_LIBRARY)) | \
		(cd $(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_BINARY_DIR)/$(SUB_LIB_DIR); \
		$(TAR) xvf - )
	$(INSTALL_FILE) $(GRAPHLET_ETC_DIR)/TclTk-license.terms $(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_BINARY_DIR)/$(SUB_LIB_DIR)/tcl$(TCL_MAJOR).$(TCL_MINOR)/license.terms
	$(INSTALL_FILE) $(GRAPHLET_ETC_DIR)/TclTk-license.terms $(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_BINARY_DIR)/$(SUB_LIB_DIR)/tk$(TK_MAJOR).$(TK_MINOR)/license.terms
	(cd	$(GRAPHLET_DISTRIBUTION_DIR); \
		$(TAR) cvf - ./$(GRAPHLET_BINARY_DIR) | \
		$(GZIP) > $(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_BINARY_DIR).tar.gz)
	(cd	$(GRAPHLET_DISTRIBUTION_DIR); \
		$(RM) -r ./$(GRAPHLET_BINARY_DIR))
	$(CP) $(GRAPHLET_DISTRIBUTION_DIR)/$(GRAPHLET_BINARY_DIR).tar.gz \
		$(GRAPHLET_ARCHIVE_DIR)



##########################################
#
# checkout, checkin
#
##########################################


.PHONY:	update commit commit-fast
update:
	$(CVS) update -d -P

commit:
	$(CVS) commit

commit-fast:
	$(CVS) commit -m "Commiting source code distribution"

tag-with-version:	commit-fast
	$(CVS) tag -F $(subst .,-,$(GRAPHLET_VERSION_DIR))



##########################################
#
# doc is a separate target sincs lynx is not available everywhere
#
##########################################


doc:	README ACKNOWLEDGEMENTS PEOPLE BUGREPORT COPYRIGHT

ACKNOWLEDGEMENTS:	$(GRAPHLET_HTML_DIR)/acknowledgements.html
	echo "[Created from $(subst $(GRAPHLET_HTML_DIR)/,,Graphlet/$<)]" > $@
	echo "" >> $@
	$(LYNX_DUMP) $< >> $@
COPYRIGHT:	$(GRAPHLET_HTML_DIR)/copyright.html
	echo "[Created from $(subst $(GRAPHLET_HTML_DIR)/,,Graphlet/$<)]" > $@
	echo "" >> $@
	$(LYNX_DUMP) $< >> $@
PEOPLE:	$(GRAPHLET_HTML_DIR)/people.html
	echo "[Created from $(subst $(GRAPHLET_HTML_DIR)/,,Graphlet/$<)]" > $@
	echo "" >> $@
	$(LYNX_DUMP) $< >> $@
README:	$(GRAPHLET_HTML_DIR)/index.html
	echo "[Created from $(subst $(GRAPHLET_HTML_DIR)/,,Graphlet/$<)]" > $@
	echo "" >> $@
	$(LYNX_DUMP) $< >> $@



##########################################
#
# newversion
#
##########################################


newversion:
	$(MAKE) -C $(GRAPHLET_CONFIG_DIR) $@
	$(MAKE) configure


##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; tab-width: 4 ***
# ;;; tab-stop-list: (4 8 12 16 20 24 28 32 36 40 44 48 52 56 60 64 68 72) ***
# ;;; End: ***
