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
CSOURCES = notice.c menge.c arnborg.c nice_tree_dec.c my_misc.c		\
           heuristic.c mystd.c separatorheuristik.c cliquen.c control.c	\
	   mainwindow.c untere_schranke.c optionen.c decomp_window.c	\
           reduce_tree_dec.c valenzheuristic.c schnittstelle.c default.c\
           treewidth_main.c

HSOURCES = notice.h menge.h arnborg.h nice_tree_dec.h my_misc.h		\
           heuristic.h mystd.h separatorheuristik.h cliquen.h control.h	\
	   mainwindow.h  untere_schranke.h optionen.h decomp_window.h	\
           reduce_tree_dec.h valenzheuristic.h schnittstelle.h		\
           default.h move.h

MYFILES = $(HSOURCES) $(CSOURCES)

MODULE=treewidth
include ../makefile.template
include $(GRAPHEDDIR)/makefile.template

CFLAGS += 
include makedepend
