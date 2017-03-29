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
CSOURCES = ConvexDrawMain.c DrawConvex.c PossibleConvexDraw.c \
           ConvexTest.c FindSepPairs.c Utilities.c \
           FindSplitComp.c ConvexDraw_sf.c
HSOURCES = ConvexTest.h FindSplitComp.h Utilities.h \
           DrawConvex.h NodeEdgeAttrs.h embed.h \
           FindSepPairs.h PossibleConvexDraw.h paths.h
MYFILES = $(HSOURCES) $(CSOURCES)

include ../makefile.template
include $(GRAPHEDDIR)/makefile.template



it :	$(MODULE).o

include makedepend
