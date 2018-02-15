#
# makefile for the gml file reader
#
# $Id: Makefile,v 1.4 2002/08/05 19:11:39 schnoy Exp $
#

TOPDIR     = ../../../../..
BUILDDIR   = $(TOPDIR)/../build/classes
CP         = -classpath $$CLASSPATH:$(TOPDIR):$(BUILDDIR)
SOURCE     = -source 1.4
JAVACFLAGS = $(CP) $(SOURCE)
JAVAFLAGS  = $(CP) -ea

JAVAC      = javac $(JAVACFLAGS) -nowarn -O -d $(BUILDDIR)
JAVA       = java $(JAVAFLAGS)
JAR        = jar
RM         = /bin/rm -f
MV         = /bin/mv
ZIP        = tar czv

FORMAT     = gml
PARSER     = org.graffiti.io.parser
PLUGIN     = $(FORMAT)reader

JLEX       = $(JAVA) JLex.Main
JAVA_CUP   = $(JAVA) java_cup.Main

TEST       = $(JAVA) -Djava.util.logging.config.file=Logging.properties \
		$(PARSER)


all: lexer parser reader
	$(JAVAC) sym.java parser.java Yylex.java

lexer: $(FORMAT).lex
	$(JLEX) $(FORMAT).lex
	$(MV) $(FORMAT).lex.java Yylex.java

parser: $(FORMAT).cup
	$(JAVA_CUP) $(FORMAT).cup

reader: lexer parser
	$(JAVAC) GMLReaderPlugin.java

plugin: all
	$(JAR) cvf $(PLUGIN).jar *.class plugin.xml \
		java_cup/*.class \
		java_cup/runtime/*.class

dist: clean
	$(ZIP) -f gmlparser-`date +"%Y-%m-%d"`.tgz *.* */*.*

clean:
	$(RM) *.class *~ gml.lex.java parser.java sym.java Yylex.java run.log*

love:
	@echo 'not war!'
