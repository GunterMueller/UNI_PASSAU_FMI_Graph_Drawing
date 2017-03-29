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
CSOURCES = chrobak_payne_algorithm.c make_output.c polygons.c take_input.c \
           compression.c proc_main.c triangulation.c numbering.c regular.c
HSOURCES = decl.h
MYFILES = $(HSOURCES) $(CSOURCES)


include ../makefile.template
include $(GRAPHEDDIR)/makefile.template


it :	$(MODULE).o

include makedepend
