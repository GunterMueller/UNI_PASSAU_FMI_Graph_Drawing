#===========================================================================
#
#   doc/Makefile.am
#
#===========================================================================
# $Id: Makefile.am,v 1.8 2003/03/17 12:47:15 raitner Exp $

SUBDIRS = userguide 

dist-hook: doc
	cp -R manual $(distdir)
	mkdir $(distdir)/icons
	cp -f www/icons/GTL.jpeg $(distdir)/icons
	cp -f www/style.css $(distdir)
	cp -f www/index.html $(distdir)
	cp -f www/register.html $(distdir)
	cp -f www/refer.html $(distdir)
	cp -f www/platforms.html $(distdir)
	cp -f www/lists.html $(distdir)
	cp -f www/history.html $(distdir)

.PHONY: doc

doc:
	doxygen doxygen_config
	cp www/icons/GTL.jpeg manual
	cd userguide; $(MAKE) doc

homepage: doc
	cp -Rf manual /home/http/htdocs/infosun/br/projects/GTL
	cp -f www/icons/GTL.jpeg /home/http/htdocs/infosun/br/projects/GTL/icons
	cp -f www/style.css /home/http/htdocs/infosun/br/projects/GTL
	cp -f www/index.html /home/http/htdocs/infosun/br/projects/GTL
	cp -f www/register.html /home/http/htdocs/infosun/br/projects/GTL
	cp -f www/refer.html /home/http/htdocs/infosun/br/projects/GTL
	cp -f www/platforms.html /home/http/htdocs/infosun/br/projects/GTL
	cp -f www/lists.html /home/http/htdocs/infosun/br/projects/GTL
	cp -f www/history.html /home/http/htdocs/infosun/br/projects/GTL
	cp -f www/download.html /home/http/htdocs/infosun/br/projects/GTL/download/index.html

homepagetest: doc
	cp -Rf manual $(HOME)/public_html/GTL
	cp -f www/icons/GTL.jpeg $(HOME)/public_html/GTL/icons
	cp -f www/style.css $(HOME)/public_html/GTL
	cp -f www/index.html $(HOME)/public_html/GTL
	cp -f www/register.html $(HOME)/public_html/GTL
	cp -f www/refer.html $(HOME)/public_html/GTL
	cp -f www/platforms.html $(HOME)/public_html/GTL
	cp -f www/lists.html $(HOME)/public_html/GTL
	cp -f www/history.html $(HOME)/public_html/GTL
	cp -f www/download.html $(HOME)/public_html/GTL/download/index.html


#---------------------------------------------------------------------------
#   end of file
#---------------------------------------------------------------------------

