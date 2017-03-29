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
CSOURCES = termgraph_agent.c termgraph_main.c termgraph_petrinet.c \
	   termgraph_tree.c termgraph_window.c
HSOURCES = termgraph_decl.h termgraph_main.h
MYFILES = $(HSOURCES) $(CSOURCES)

CFLAGS += 
include ../makefile.template
include $(GRAPHEDDIR)/makefile.template


it :	$(MODULE).o

include makedepend
