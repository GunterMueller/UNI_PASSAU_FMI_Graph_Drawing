# (C) Universitaet Passau, Michael Himsolt 1986-1993
#########################################################################
#									#
#			Sgraph Makefile					#
#									#
#########################################################################

GRAPHEDDIR = ..


CSOURCES = sgraph_.c snode.c sedge.c std.c slist.c graphed.c \
	   sgragra.c sprod.c sembed.c salpha.c attrs.c
HSOURCES = algorithms.h	graphed_structures.h sgraph_interface.h \
	   sgragra.h slist.h \
	   dispatch_commands.h sgragra_interface.h std.h \
	   graphed.h sgraph.h random.h

MYFILES = $(HSOURCES) $(CSOURCES) sscanner.l sparser.y

#OBJECTFILES = $(CSOURCES:.c=.o)

include ../contrib/makefile.template
include ../makefile.template

all :		$(MODULE) sparser.o sscanner.o

#
# How to install Sgraph (local)
#

install ::	FORCE
		if [ ! -d $(SGRAPH_INSTALL_INCLUDEDIR) ]; \
		then \
			mkdir $(SGRAPH_INSTALL_INCLUDEDIR); \
		fi
		cp $(HSOURCES) $(SGRAPH_INSTALL_INCLUDEDIR)


include makedepend
