# (C) Universitaet Passau, Michael Himsolt 1986-1993
#########################################################################
#									#
#			    GRAPHED makeFILE				#
#									#
#########################################################################


#
# Directories
#
GRAPHEDDIR = ../..

#
# Sourcefiles
#
CSOURCES = move_rotate.c external_program_caller.c tests.c planarify.c
HSOURCES = utils_export.h external_program_caller.h
MYFILES = $(HSOURCES) $(CSOURCES)

include ../makefile.template
include $(GRAPHEDDIR)/makefile.template



it :	$(MODULE).o

include makedepend

install ::	
		cp external_program_caller.h  $(SGRAPH_INSTALL_INCLUDEDIR)/$(MODULE)
