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
CSOURCES = testmod.c woods_sf.c dfsmod.c bisecmod.c getpmod.c stnummod.c woodsmod.c woodscall.c
HSOURCES = testmod.h dfsmod.h bisecmod.h getpmod.h stnummod.h woodsmod.h woodscall.h woods_planar_layout_export.h


MYFILES = $(HSOURCES) $(CSOURCES)


include ../makefile.template
include $(GRAPHEDDIR)/makefile.template



it :	$(MODULE).o

include makedepend
