#
# Makefile
#
# This file is the Makefile for graphlet/doc.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/doc/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.2 $
# $Date: 1999/03/05 20:39:10 $
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

MANUAL = \
	Graphscript Cpp GML Packages
TEXFILES = \
	Cpp \
	CodingStandards \
	CodingStandards-C++ \
	CodingStandards-General \
	Config \
	GML \
	GML-Introduction \
	GML-Definition \
	GML-Keys \
	GML-ISO \
	Packages

it all:	dvi ps

.PHONY:	dvi ps
dvi:	$(foreach file,$(MANUAL),$(file).dvi)	
ps:	$(foreach file,$(MANUAL),$(file).ps)


#
# Configure
#

.PHONY:	configure
configure:	

#
# Create DVI from LaTeX sources. There are two options: dvi and dvi2.
#

.SUFFIXES:	.tex .dvi .ps

LATEX_RERUN_MESSAGE="Rerun to get cross-references right."

.tex.dvi:	
ifdef LATEX
		$(LATEX) $<
		while grep $(LATEX_RERUN_MESSAGE) $(basename $<).log ; do \
			$(LATEX) $< ; \
		done
else
		echo "Cannot make DVI file because LaTeX is not configured"
endif

.dvi.ps:	
ifdef DVIPS
		$(DVIPS) $(DVIPSFLAGS) $<
else
		echo "Cannot make PostScript file because dvips is not configured"
endif

#
# LaTeX clean
#

clean.local::	
	$(RM) $(foreach file,$(TEXFILES),$(file).log $(file).dvi $(file).aux $(file).toc $(file).lof $(file).lot $(file).idx $(file).ind $(file).ilg $(file).glo $(file).bbl $(file).bib $(file).blg $(file).ps)

##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: makefile ***
# ;;; End: ***
