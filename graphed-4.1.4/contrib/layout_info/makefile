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
CSOURCES = layout_inf.c crossings_info.c edge_info.c misc_info.c dual.c info_sf.c remove.c
#CSOURCES = crossings_info.c edge_info.c misc_info.c dual.c info_sf.c remove.c
HSOURCES = layout_info_export.h
MYFILES = $(HSOURCES) $(CSOURCES)

include ../makefile.template
include $(GRAPHEDDIR)/makefile.template
CFLAGS += $(DBX)

include makedepend
