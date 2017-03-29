# (C) Universitaet Passau, Michael Himsolt 1986-1994
#########################################################################
#									#
#			    GRAPHED STANDARD MAKEFILE			#
#									#
#########################################################################


all:	 graphed2simple


GRAPHEDDIR=..
include $(GRAPHEDDIR)/makefile.template

#
# Sourcefiles
#

CSOURCES = graphed2simple.c
HSOURCES = 
MYFILES = $(HSOURCES) $(CSOURCES) simple2graphed simple2graphed.awk makefile converters.texi Converters.info vega2graphed graphed2vega graphedold2new
OFILES = graphed2simple.o
PROGRAM = graphed2simple

# now in $(GRAPHEDDIR)/makefile.template
#LINKLIBS = -lm -ll
# Solaris:
#LINKLIBS += -lw

$(PROGRAM):	$(OFILES)
		$(CC) $(LDFLAGS) $(OFILES) -o $(PROGRAM) $(SGRAPHDIR)/sgraph.o $(SGRAPHDIR)/sscanner.o $(SGRAPHDIR)/sparser.o $(LINKLIBS)


DISTRIBFILES = README $(CSOURCES) simple2graphed simple2graphed.awk makefile converters.texi vega2graphed graphed2vega

install.local::
	cp simple2graphed $(INSTALL_BINDIR)
	cp simple2graphed.awk $(INSTALL_BINDIR)
	cp graphed2simple $(INSTALL_BINDIR)
	cp vega2graphed $(INSTALL_BINDIR)
	cp graphed2vega $(INSTALL_BINDIR)
	cp graphedold2new $(INSTALL_BINDIR)

# DO NOT DELETE THIS LINE -- make depend depends on it.

graphed2simple.o: 
graphed2simple.o: ../sgraph/std.h
graphed2simple.o: ../sgraph/sgraph.h ../sgraph/std.h
graphed2simple.o: ../sgraph/slist.h ../sgraph/sgraph.h ../sgraph/slist.h
