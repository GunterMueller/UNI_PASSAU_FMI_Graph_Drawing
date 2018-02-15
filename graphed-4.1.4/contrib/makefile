# (C) Universitaet Passau, Michael Himsolt 1986-1993
#########################################################################
#									#
#			    GRAPHED makeFILE				#
#									#
#########################################################################

all :	contribs

GRAPHEDDIR = ..
#include makefile.template
include $(GRAPHEDDIR)/makefile.template
include extensions
#
# Directories
#
SUBDIRS = $(EXTENSIONS)

contribs :	$(SUBDIRS)
$(SUBDIRS) :	FORCE
		cd $@; $(MAKE) $(TARGET)

		
dependency ::
		$(MAKE) $(SUBDIRS) TARGET=dependency
clean ::
		$(SUBDIRS) TARGET=clean

FORCE :		
