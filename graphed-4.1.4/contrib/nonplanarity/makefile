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
CSOURCES = nonplanarity_calls.c        \
         nonplanarity_subframe.c       \
         nonplanarity_settings.c       \
         maxplanarsubgraph.c           \
         pqplanarity.c                 \
         stnumber.c                    \
         pqhandle.c                    \
         reduce.c                      \
         templates.c                   \
         thickness.c                   \
         crossingnumber.c              \
	 random.c                      \
         sattrs.c                      \
         setattrsandlabels.c           \
         sdebug.c


HSOURCES = crossingnumber.h		\
	pqplanarity.h			\
	sdebug.h			\
	maxplanarsubgraph.h		\
	pqtree.h			\
	setattrsandlabels.h		\
	nonplanarity_export.h		\
	random.h			\
	stnumber.h			\
	nonplanarity_settings.h		\
	reduce.h			\
	templates.h			\
	pqhandle.h			\
	sattrs.h			\
	thickness.h

MYFILES = $(HSOURCES) $(CSOURCES)

MODULE=nonplanarity
include ../makefile.template
include $(GRAPHEDDIR)/makefile.template

CFLAGS += -DNODEBUG -DSUN_VERSION
include makedepend
