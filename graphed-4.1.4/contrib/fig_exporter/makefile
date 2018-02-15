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
CSOURCES = fig_exporter_.c
HSOURCES = fig_exporter.h fig_exporter_export.h
MYFILES = $(HSOURCES) $(CSOURCES)

MODULE=fig_exporter
include ../makefile.template
include $(GRAPHEDDIR)/makefile.template

CFLAGS += 
include makedepend
