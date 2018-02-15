# (C) Universitaet Passau, Michael Himsolt 1986-1993

GRAPHEDDIR=..

include ../makefile.template

all :	

MYFILES = dot.graphed dot.graphed_fileselectors \
          graphed.header.ps graphed.tail.ps \
          uni-passau.big.logo uni-passau.logo \
          fonts graphs help types

install.local::	FORCE
		if [ ! -d $(INSTALL_COMMON_LIBDIR) ]; \
		then \
			mkdir -p $(INSTALL_COMMON_LIBDIR) ; \
		fi
		cp -r $(MYFILES) $(INSTALL_COMMON_LIBDIR)
		rm -f $(INSTALL_COMMON_LIBDIR)/.graphed
		cp $(INSTALL_COMMON_LIBDIR)/dot.graphed $(INSTALL_COMMON_LIBDIR)/.graphed
		rm -f $(INSTALL_COMMON_LIBDIR)/.graphed_fileselectors
		cp $(INSTALL_COMMON_LIBDIR)/dot.graphed_fileselectors $(INSTALL_COMMON_LIBDIR)/.graphed_fileselectors
