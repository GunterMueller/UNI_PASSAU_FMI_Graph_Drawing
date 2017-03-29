# (C) Universitaet Passau, Michael Himsolt 1986-1991
#########################################################################
#									#
#			    GRAPHED MAKEFILE				#
#									#
#########################################################################


#
# Directories
#
GRAPHEDDIR = ../..

#
# Sourcefiles
#
CSOURCES = cros.c cycle.c hier.c recycle.c sugi.c pos.c sugi_sf.c undirected.c
HSOURCES = sugiyama_export.h recycle.h
MYFILES = $(HSOURCES) $(CSOURCES)

MODULE=sugiyama
include ../makefile.template
include $(GRAPHEDDIR)/makefile.template

CFLAGS += 
include makedepend
