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
CSOURCES = \
	st_upward_draw_auxiliary.c \
	st_upward_draw_main.c \
	st_upward_draw_drawing.c \
	st_upward_draw_postprocessing.c \
	st_upward_draw_preprocessing.c \
	st_upward_draw_sf.c

HSOURCES = \
	st_upward_draw_algorithm.h \
	st_upward_draw_export.h

MYFILES = $(HSOURCES) $(CSOURCES)

MODULE=st_upward_draw
include ../makefile.template
include $(GRAPHEDDIR)/makefile.template

CFLAGS += 
include makedepend
