# (C) Universitaet Passau, Michael Himsolt 1986-1993
#########################################################################
#									#
#			    GRAPHED makeFILE				#
#									#
#########################################################################

#
# Sourcefiles
#
CSOURCES    = 	net_tool.c findface.c tools.c trans_to.c minos.c \
		bend_m.c re_tools.c retrans.c build_grid.c\
                change_GraphEd.c free_structs.c min_bend_sf.c
HSOURCES    =	newfuncs.h defines.h funcs.h struct.h \
		minimal_bends_layout2_export.h
MYFILES = $(HSOURCES) $(CSOURCES)

GRAPHEDDIR=../..
include ../makefile.template
include $(GRAPHEDDIR)/makefile.template



it :	$(MODULE).o

include makedepend
