# (C) Universitaet Passau, Michael Himsolt 1986-1993

it:	all

SUBDIRS = sgraph contrib graphed lib etc doc converters
GRAPHEDDIR = .
$(SUBDIRS) :	FORCE
		cd $@; $(MAKE) $(TARGET)

distribution ::	FORCE
		rm -rf $(DISTRIBDIR)

install ::	FORCE
		if [ ! -d $(INSTALLBASE) ] ; \
		then \
			mkdir $(INSTALLBASE) ; \
		fi
		if [ ! -d $(INSTALLBASE)/bin ] ; \
		then \
			mkdir $(INSTALLBASE)/bin ; \
		fi
		if [ ! -d $(INSTALLBASE)/lib ] ; \
		then \
			mkdir $(INSTALLBASE)/lib ; \
		fi
		if [ ! -d $(INSTALLBASE)/include ] ; \
		then \
			mkdir $(INSTALLBASE)/include ; \
		fi

dist:		newversion
		touch graphed/config.c
		$(MAKE) all
		$(MAKE) distribution
		$(MAKE) distribution.binary

include makefile.template

DISTRIBDIR = /home/br/graphed/graphed-$(VERSION)
DISTRIB_TAR = $(DISTRIBDIR).tar
DISTRIB_BINTAR = $(DISTRIBDIR)-binary.tar
DISTRIB_SMALL_BINTAR = $(DISTRIBDIR)-small-binary.tar

dependency ::	
		$(MAKE) $(SUBDIRS) TARGET=dependency
all	:	
		$(MAKE) $(SUBDIRS) TARGET=all

#$(SOURCES) :	
#		cd $@; $(MAKE) $(TARGET)

distribution.local::	FORCE
		cp -r images $(DISTRIBDIR)
		cp makefile.version $(DISTRIBDIR)

distribution ::	FORCE
		/bin/rm -f $(DISTRIB_TAR)
		/bin/rm -f $(DISTRIB_TAR).Z
		/bin/rm -f $(DISTRIB_TAR).gz
		(cd `dirname $(DISTRIBDIR)` ; tar cf $(DISTRIB_TAR) ./`basename $(DISTRIBDIR)`)
		compress -c $(DISTRIB_TAR) > $(DISTRIB_TAR).Z
		gzip --stdout --best $(DISTRIB_TAR) > $(DISTRIB_TAR).gz
		/bin/rm -f $(DISTRIB_TAR)
		

distribution.binary ::	FORCE
		/bin/rm -rf $(TMPINSTALLBASE)
		$(MAKE) all
		$(MAKE) tmpinstall
		mv $(TMPINSTALLBASE)/bin/graphed $(TMPINSTALLBASE)/bin/graphed.binary
		cp $(TMPINSTALLBASE)/etc/graphed.wrapper $(TMPINSTALLBASE)/bin/graphed
		/bin/rm -f $(DISTRIB_BINTAR)
		/bin/rm -f $(DISTRIB_BINTAR).Z
		/bin/rm -f $(DISTRIB_BINTAR).gz
		(cd `dirname $(TMPINSTALLBASE)`; tar cf $(DISTRIB_BINTAR) ./`basename $(TMPINSTALLBASE)`)
		compress -c $(DISTRIB_BINTAR) > $(DISTRIB_BINTAR).Z
		gzip --stdout --best $(DISTRIB_BINTAR) > $(DISTRIB_BINTAR).gz
		/bin/rm -f $(DISTRIB_SMALL_BINTAR)
		/bin/rm -f $(DISTRIB_SMALL_BINTAR).Z
		/bin/rm -f $(DISTRIB_SMALL_BINTAR).gz
		/bin/rm -f $(TMPINSTALLBASE)/lib/$(INSTALLEDNAME)/$(NEWLIBGRAPHED)
		(cd `dirname $(TMPINSTALLBASE)`; tar cf $(DISTRIB_SMALL_BINTAR) ./`basename $(TMPINSTALLBASE)`)
		compress -c $(DISTRIB_SMALL_BINTAR) > $(DISTRIB_SMALL_BINTAR).Z
		gzip --stdout --best $(DISTRIB_SMALL_BINTAR) > $(DISTRIB_SMALL_BINTAR).gz
		/bin/rm -f $(DISTRIB_SMALL_BINTAR)
		/bin/rm -rf $(TMPINSTALLBASE)

newversion :	FORCE
		MINI_VERSION=`expr $(MINI_VERSION) + 1`
		echo "MAJOR_VERSION = $(MAJOR_VERSION)" > $(MAKEFILE_VERSION)
		echo "MINOR_VERSION = $(MINOR_VERSION)" >> $(MAKEFILE_VERSION)
		echo "MINI_VERSION = `expr $(MINI_VERSION) + 1`" >> $(MAKEFILE_VERSION)
		echo "EXTRA_VERSION = $(EXTRA_VERSION)" >> $(MAKEFILE_VERSION)
		touch graphed/config.c

checks :	FORCE
		echo "looking for /public includes ..."
		find . -name '*.[ch]' -exec grep -l '/public' {} \;
		echo "looking for /usr includes ..."
		find . -name '*.[ch]' -exec grep -l '/usr' {} \;
