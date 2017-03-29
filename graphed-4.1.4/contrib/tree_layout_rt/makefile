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
CSOURCES = rt_alg.c rt_cor.c rt_err.c rt_new_layout.c rt_convert.c \
	   rt_main.c rt_verify.c rt_sf.c
HSOURCES = rt_def.h
MYFILES = $(HSOURCES) $(CSOURCES)

include ../makefile.template
include $(GRAPHEDDIR)/makefile.template


it :	$(MODULE).o

include makedepend
