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
CSOURCES = max_clique.c clique.c kclique.c liste.c gfunc.c  
HSOURCES = maxclique.h clique.h kclique.h liste.h gfunc.h 
MYFILES = $(HSOURCES) $(CSOURCES)

include ../makefile.template
include $(GRAPHEDDIR)/makefile.template


it :	$(MODULE).o

include makedepend
