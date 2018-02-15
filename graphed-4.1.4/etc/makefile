GRAPHEDDIR = ..
include $(GRAPHEDDIR)/makefile.template

all:	FORCE

distribution.local ::	FORCE
			cp graphed.wrapper shorten_dependencies $(DISTRIBDIR);

MYFILES = graphed.wrapper

install.local::	FORCE
		if [ ! -d $(INSTALL_ETCDIR) ]; \
		then \
			mkdir -p $(INSTALL_ETCDIR) ; \
		fi
		cp -r $(MYFILES) $(INSTALL_ETCDIR)
