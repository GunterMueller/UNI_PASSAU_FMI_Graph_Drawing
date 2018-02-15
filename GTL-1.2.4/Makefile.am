#===========================================================================
#
#   Makefile.am
#
#===========================================================================
# $Id: Makefile.am,v 1.17 2003/03/17 12:48:16 raitner Exp $

#---------------------------------------------------------------------------
#   General Settings
#---------------------------------------------------------------------------

SUBDIRS = doc src include tests

AUTOMAKE_OPTIONS = dist-zip dist-bzip2 dist-zip

EXTRA_DIST =				\
	GTL.spec			\
	README.Irix			\
	README.win			\
	src/GTL.dsw			\
	src/GTL_dynamic.dsp		\
	src/GTL_static.dsp		\
	src/GTL.sln			\
	src/GTL_dynamic.vcproj		\
	src/GTL_static.vcproj

#---------------------------------------------------------------------------
#   Goal for building documentation
#---------------------------------------------------------------------------

.PHONY: doc runtests

runtests:
	cd tests; $(MAKE) runtests

doc:
	cd doc; $(MAKE) doc

#---------------------------------------------------------------------------
#   end of file
#---------------------------------------------------------------------------
