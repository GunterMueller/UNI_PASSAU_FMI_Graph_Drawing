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
CSOURCES = alg/app2tree.c alg/movesep.c alg/appgrid.c			\
	alg/optimum.c gui/notify.c alg/apptree.c alg/separator.c	\
	gui/timer.c alg/bf.c alg/subgraph.c gui/window.c alg/clique.c	\
	alg/tree.c alg/cograph.c move/animate.c				\
	alg/complete.c ctrl/control.c move/move.c alg/graphsize.c	\
	ctrl/ctrlsearch.c move/record.c alg/grid.c ctrl/data.c		\
	move/simplify.c alg/knm.c ctrl/if.c move/smove.c alg/logsep.c	\
	ctrl/manctrl.c move/state.c alg/mindeg.c gui/display.c		\
	move/utilities.c alg/minor.c gui/error.c

HSOURCES = search/algorithm.h search/move.h	\
	search/control.h search/search.h	\
	search/err.h search/window.h		\
	search/error.h


MYFILES = $(HSOURCES) $(CSOURCES) \
	search/ffwd.icon search/play.icon search/sog.icon	\
	search/frwd.icon search/reset.icon search/stop.icon	\
	search/fwd.icon search/rwd.icon


MODULE=searchers
include ../makefile.template
include $(GRAPHEDDIR)/makefile.template

CCINCLUDE += -I$(CONTRIBDIR)/$(MODULE)



include makedepend
