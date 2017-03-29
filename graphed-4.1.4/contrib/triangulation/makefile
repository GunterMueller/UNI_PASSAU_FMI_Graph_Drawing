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
CSOURCES    = menu_polygon.c \
              menu_planargraph.c \
              po_dynamic.c \
              po_minmaxdegree.c \
              po_sweep.c \
              po_bushy.c \
              po_maxmaxdegree.c \
              po_random.c \
              po_delaunay.c  \
              misc_utility.c \
              miscmath_utility.c \
              memory_allocation.c \
              polygon_utility.c \
              pl_sweep.c \
              pl_greedy.c \
              pl_delaunay.c \
              pl_random.c \
              pl_edgeflip.c \
              pl_maxmaxdegree.c \
              pl_minmaxangle.c \
              planargraph_utility.c \
              menu_makegraph.c \
              makegraph.c 

HSOURCES = globaldefinitions.h \
           makegraph.h \
           memory_allocation.h \
           menu_makegraph.h \
           menu_planargraph.h \
           menu_polygon.h \
           misc_utility.h \
           miscmath_utility.h \
           pl_delaunay.h \
           pl_edgeflip.h \
           pl_greedy.h \
           pl_maxmaxdegree.h \
           pl_minmaxangle.h \
           pl_random.h \
           pl_sweep.h \
           planargraph_utility.h \
           po_bushy.h \
           po_delaunay.h \
           po_dynamic.h \
           po_maxmaxdegree.h \
           po_minmaxdegree.h \
           po_random.h \
           po_sweep.h \
           polygon_utility.h \
           triangulation_export.h


MYFILES = $(HSOURCES) $(CSOURCES)


include ../makefile.template
include $(GRAPHEDDIR)/makefile.template



it :	$(MODULE).o

include makedepend
