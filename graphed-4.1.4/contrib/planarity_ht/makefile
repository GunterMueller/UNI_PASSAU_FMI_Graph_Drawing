# (C) Universitaet Passau, Michael Himsolt 1986-1993
#########################################################################
#									#
#			    GRAPHED makeFILE				#
#									#
#########################################################################


#
# Directories
#

#
# Sourcefiles
#
CSOURCES = HTembed.c HTplanarity.c HTmain.c dualopt.c
HSOURCES = planarity_ht_export.h
MYFILES = $(HSOURCES) $(CSOURCES)

GRAPHEDDIR = ../..

include ../makefile.template
include $(GRAPHEDDIR)/makefile.template


it :	$(MODULE).o

include makedepend
