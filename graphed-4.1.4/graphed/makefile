# (C) Universitaet Passau, Michael Himsolt 1986-1993
#########################################################################
#									#
#			    GRAPHED makeFILE				#
#									#
#########################################################################

##############################
# Make standard target : the graphed binary
##############################

it :	graphed

##############################
# Make all (graphed + livrary)
##############################

all :		graphed lib


##############################
# Sourcefiles
##############################

GRAPHEDDIR =..

EXPORTABLE_CSOURCES = \
about.c \
adjust.c \
base_frame.c \
buffer.c \
canvas.c \
config.c \
create_mode.c \
derivation.c \
derivation_sequence.c \
dispatch.c \
drag.c \
draw.c \
edge_defaults_sf.c \
edge_sf.c \
edit_mode.c \
eedge.c \
error.c \
extensions_config.c \
file_attributes.c \
file_sf.c \
find.c \
font.c \
font_sf.c \
ggraph.c \
gragra.c \
gragra_sf.c \
graph_sf.c \
graphed_mpr.c \
graphed_pin_sf.c \
graphed_sgragra_interface.c \
graphed_sgraph_interface.c \
graphed_svi.c \
gridder.c \
group.c \
group_sf.c \
load.c \
main.c \
menu.c \
menubar.c \
misc.c \
nnode.c \
node_defaults_sf.c \
node_sf.c \
nodetypes/black.c \
nodetypes/box.c \
nodetypes/circle.c \
nodetypes/diamond.c \
nodetypes/double_box.c \
nodetypes/double_circle.c \
nodetypes/double_diamond.c \
nodetypes/black_box.c \
nodetypes/black_circle.c \
nodetypes/black_diamond.c \
nodetypes/nodetypes.c \
nodetypes/pixmap.c \
nodetypes/white.c \
paint.c \
print.c \
print_sf.c \
ps.c \
repaint.c \
simple_fs.c \
state.c \
store.c \
template.c \
textsw.c \
toolbar.c \
type.c \
type_sf.c \
user.c \
user_event_functions.c \
util.c


CSOURCES = $(EXPORTABLE_CSOURCES) $(SF_SOURCES) user_commands.c

HSOURCES = \
adjust.h \
config.h \
derivation.h \
dispatch_commands.h \
draw.h \
error.h \
extensions_config.h \
file_attributes_functions.h \
file_attributes_types.h \
find.h \
font.h \
gragra.h \
graph.h \
graphed_mpr.h \
graphed_pin_sf.h \
graphed_sgragra_interface.h \
graphed_sgraph_interface.h \
graphed_subwindows.h \
graphed_svi.h \
gridder.h \
group.h \
install.h \
load.h \
menu.h \
misc.h \
nodetypes/nodetypes.h \
paint.h \
print.h \
ps.h \
repaint.h \
simple_fs.h \
state.h \
store.h \
type.h \
user.h \
user_commands.h \
user_event_functions.h \
user_header.h \
util.h

YSOURCES = parser.y
LSOURCES = scanner.l

MYFILES = $(HSOURCES) $(CSOURCES) $(YSOURCES) $(LSOURCES)

##############################
# Objectfiles
##############################

include makefile.extensions

EXPORTABLE_OBJECTFILES = $(EXPORTABLE_CSOURCES:%.c=%.o) $(OBJECTFILES_EXTENSIONS) $(SGRAPHDIR)/sgraph.o scanner.o parser.o

OBJECTFILES = $(EXPORTABLE_OBJECTFILES) user_commands.o


##############################
# Compiler Switches
##############################
# nun in $(GRAPHEDIR)/makfile.template

#LINKLIBS        = -L/usr/openwin/lib -lxview -lolgx -lX11 -lm -ll
#STATIC_LINKLIBS = -Bstatic -L/usr/openwin/lib -lxview -lolgx -lX11 \
#                  -Bdynamic -lm -ll
# For Solaris 2.*
#LINKLIBS += -lsunmath -lsocket -lnsl -lw


##############################
# Include default values
##############################

include $(GRAPHEDDIR)/makefile.template

#
# The environmenr variable GRAPHED_PRIVATE_DBX is (hopefully)
# local to my account in Passau
#
DBX=$(GRAPHED_PRIVATE_DBX)
#
# Compiler Switches
#
CFLAGS += $(DBX)
# CFLAGS += $(DBX)
#
# Add support for the LP_LAYOUT module
#
CFLAGS += -DLP_LAYOUT
#
# Linker flags
#
#LDFLAGS += $(DBX)
#
# Optional :Flags neededfor profiling
#
# CFLAGS  += -pg
# LDFLAGS += -pg -Bstatic

##############################
# How to $(MAKE) the programm
###############################

#
# Default : dynamically linked version
#
graphed :	$(OBJECTFILES)
		$(CC) $(LDFLAGS) $(OBJECTFILES) $(LINKLIBS) -o graphed
#		purify $(CC) $(LDFLAGS) $(OBJECTFILES) $(LINKLIBS) -o graphed.purify
# Optional : Profiling
#		$(CC) $(LDFLAGS) $(OBJECTFILES) $(LINKLIBS) -o graphed /usr/lib/debug/malloc.o

#
# Optional : statically linked version
#
graphed.static:	$(OBJECTFILES)
		$(CC) $(LDFLAGS) $(OBJECTFILES) $(STATIC_LINKLIBS) -o graphed.static

#
# Create the library (actually a combined object file)
#
lib :		graphed.o
graphed.o :	$(EXPORTABLE_OBJECTFILES)
		ld -r $(LDFLAGS) $(EXPORTABLE_OBJECTFILES) -o graphed.o
# Obsolete: -X no longer supported in Solaris 2.*
#		ld -r -X $(EXPORTABLE_OBJECTFILES) -o graphed.o

#
# Include here a file with the dependencies
#
include	makedepend

# some additional dependencies
scanner.c :	scanner.l	# cc -M cannot recognize lex and yacc ...
parser.c  :	parser.y
y.tab.h   :	parser.c
scanner.o :	parser.o


#
# install
#
install.local::	graphed lib install.local.makedirs install.local.graphed install.local.lib
		if [ ! -d $(SGRAPH_INSTALL_INCLUDEDIR)/graphed ]; \
		then \
			mkdir $(SGRAPH_INSTALL_INCLUDEDIR)/graphed; \
		fi
		cp graphed_pin_sf.h simple_fs.h gridder.h $(SGRAPH_INSTALL_INCLUDEDIR)/graphed

install.local.makedirs:	FORCE
		if [ ! -d $(INSTALL_BINDIR) ]; \
		then \
			mkdir $(INSTALL_BINDIR); \
		fi
		if [ ! -d $(INSTALL_LIBDIR) ]; \
		then \
			mkdir $(INSTALL_LIBDIR); \
		fi

install.local.graphed:	graphed
		cp ./graphed $(INSTALL_BINDIR)/$(INSTALLEDNAME)
		strip $(INSTALL_BINDIR)/$(INSTALLEDNAME)

install.local.lib:	lib
		if [ -f $(NEWLIBGRAPHED) ];                        \
		then                                               \
	                cp $(NEWLIBGRAPHED) $(INSTALL_LIBDIR);     \
		fi

distribution.local ::	FORCE
		for file in makefile.extensions extensions.c extensions.h existing_extensions.h ; \
		do \
			if [ -f $$file ]; \
			then \
				cp $$file $(DISTRIBDIR); \
			fi \
		done
#
# Goodies
#
include $(CONTRIBDIR)/extensions

extensions :	$(CONTRIBDIR)/extensions
extensions.c:	$(CONTRIBDIR)/extensions
		echo "OBJECTFILES_EXTENSIONS =" > makefile.extensions ; \
		echo "" > extensions.c ; \
		echo "" > extensions.h ; \
		echo "" > existing_extensions.h ; \
		for i in $(EXTENSIONS) ; do \
			echo "OBJECTFILES_EXTENSIONS += " $(CONTRIBDIR)/$$i/$$i.o >> makefile.extensions ; \
			echo "#include \""$(CONTRIBDIR)/$$i/$${i}_export.h\" >> extensions.h ; \
			echo "#include \""$(CONTRIBDIR)/$$i/$${i}_init.c\" >> extensions.c ; \
			echo "#define EXTENSION_"$$i >> existing_extensions.h ; \
		done
