#
# Makefile
#
# This file is the makefile for the demo parser in the graphlet
# project.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/GNUmakefile,v $
# $Author: himsolt $
# $Revision: 1.21 $
# $Date: 1999/03/05 20:39:51 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet project
#


#
# Name of the module
#
MODULE = graphscript

#
# Start of the world
#

GRAPHLET_BASE_DIR=../..

#
# The target "it" is the first target in the makefile. It will be
# executed if you just type "make".
#

.PHONY:	all it
all it:	pkgIndex.tcl

##########################################
#
# My files
#
##########################################

TCLFILES=					\
	./action.tcl				\
	./algorithms/complete_binary_tree.tcl	\
	./algorithms/fibonacci_tree.tcl		\
	./algorithms/guai.tcl			\
	./algorithms/layout_cse.tcl		\
	./algorithms/layout_fr.tcl		\
	./algorithms/layout_gem.tcl		\
	./algorithms/layout_hispring.tcl	\
	./algorithms/layout_icse.tcl		\
	./algorithms/layout_kk.tcl		\
	./algorithms/layout_radial_tree.tcl	\
	./algorithms/layout_random.tcl		\
	./algorithms/layout_sugiyama.tcl	\
	./algorithms/layout_tree.tcl		\
	./algorithms/layout_tree_walker.tcl	\
	./algorithms/layout_tunkelang.tcl	\
	./algorithms/layout_xdag.tcl		\
	./algorithms/leda_algorithms.tcl	\
	./animation.tcl				\
	./bgerror.tcl				\
	./clipboard.tcl				\
	./colorsel.tcl				\
	./contrib/tkcon.tcl			\
	./debug.tcl				\
	./dialog/about.tcl			\
	./dialog/browse_url.tcl			\
	./dialog/close.tcl			\
	./dialog/configuration.tcl		\
	./dialog/display_file.tcl		\
	./dialog/display_messages.tcl		\
	./dialog/edit_label.tcl			\
	./dialog/exit.tcl			\
	./dialog/greeting.tcl			\
	./dialog/mail_bugreport.tcl		\
	./dialog/options.tcl			\
	./dialog/overwrite.tcl			\
	./dialog/postscript.tcl			\
	./dialog/scale.tcl			\
	./dialog/search_and_replace.tcl		\
	./dialog/simple_dialog.tcl		\
	./dialog/utility.tcl			\
	./dlg_util.tcl				\
	./easy.tcl				\
	./editor/canvas.tcl			\
	./editor/frame.tcl			\
	./editor/graphicbar.tcl			\
	./editor/graphlet.tcl			\
	./editor/menubar.tcl			\
	./editor/message.tcl			\
	./editor/modebar.tcl			\
	./editor/toolbar.tcl			\
	./fileselect.tcl			\
	./graph.tcl				\
	./hooks.tcl				\
	./image.tcl				\
	./init.tcl				\
	./init/action.tcl			\
	./init/graphlet.tcl			\
	./init/modes.tcl			\
	./init/options.tcl			\
	./init/postscript.tcl			\
	./inspector/bends_ctl.tcl		\
	./inspector/edge_anchor_ctl.tcl		\
	./inspector/font_ctl.tcl		\
	./inspector/generic_ctl.tcl		\
	./inspector/init.tcl			\
	./inspector/justify_ctl.tcl		\
	./inspector/label_anchor_ctl.tcl	\
	./inspector/linestyle_ctl.tcl		\
	./inspector/main.tcl			\
	./inspector/ports_ctl.tcl		\
	./inspector/shape_ctl.tcl		\
	./inspector/stipple_ctl.tcl		\
	./inspector/utils.tcl			\
	./menu/arrange.tcl			\
	./menu/canvas_popup_menu.tcl		\
	./menu/developer.tcl			\
	./menu/edge.tcl				\
	./menu/edit.tcl				\
	./menu/file.tcl				\
	./menu/generate.tcl			\
	./menu/graph.tcl			\
	./menu/help.tcl				\
	./menu/label.tcl			\
	./menu/layout.tcl			\
	./menu/node.tcl				\
	./menu/options.tcl			\
	./menu/select.tcl			\
	./menu/tool.tcl				\
	./menu/view.tcl				\
	./mini_inspector.tcl			\
	./modes/create_mode.tcl			\
	./modes/edit_mode.tcl			\
	./modes/global.tcl			\
	./modes/mode.tcl			\
	./modes/rotate_mode.tcl			\
	./modes/scale_mode.tcl			\
	./modes/text_mode.tcl			\
	./modes/view_mode.tcl			\
	./modes/zoom_mode.tcl			\
	./options.tcl				\
	./patches.tcl				\
	./postscript.tcl			\
	./sample.tcl				\
	./scale.tcl				\
	./selection.tcl				\
	./undo.tcl				\
	./utility.tcl				\
	./widgets/colorbutton.tcl		\
	./widgets/combobox.tcl			\
	./widgets/flatbutton.tcl		\
	./widgets/numentry.tcl			\
	./widgets/tabwindow.tcl			\
	./widgets/tooltips.tcl			\
	./widgets/widget.tcl

##########################################
#
# include common definitions
#
##########################################

include $(GRAPHLET_BASE_DIR)/lib/graphlet/config/common



##########################################
#
# make index
#
##########################################

index:		pkgIndex.tcl

pkgIndex.tcl:	$(TCLFILES) $(GRAPHLET_BASE_DIR)/.linking $(GRAPHLET_CONFIG_DIR)/version
	echo "package provide Graphscript $(MAJORVERSION).$(MINORVERSION).$(MINIVERSION)" > version.tcl
	for m in $(BASE_MODULES) $(EXTENSION_MODULES) ; do \
	    if test -f ../lib$$m.so -a \! -h lib$$m.so ; then \
	        $(LN) -s ../lib$$m.so . ; \
	    fi \
	done
	GRAPHLET_DIR=`pwd`/../.. \
	LD_LIBRARY_PATH=.:$$LD_LIBRARY_PATH \
	$(GRAPHLET_SRC_GRAPHSCRIPT_DIR)/$(GRAPHSCRIPT) -tcl_only make_index

#	for m in $(BASE_MODULES) $(EXTENSION_MODULES) ; do \
#	    if test -h lib$$m.so ; then \
#	        $(RM) lib$$m.so . ; \
#	    fi \
#	done

.PHONY:	configure
configure:
	echo "set GT(graphscript_package_version) $(MAJORVERSION).$(MINORVERSION).$(MINIVERSION)" > version.tcl

##########################################
#
# make tcl2c
#
##########################################

tcl2c:		$(TCLFILES) graphlet-startup.tcl
	tcl2c $(TCLFILES) graphlet-startup.tcl > x.cpp

##########################################
#
# Installation instructions
#
##########################################

install.local:: index
	$(INSTALL_MAKE_DIR) $(INSTALL_GRAPHSCRIPT_DIR)
	(cd $(INSTALL_GRAPHSCRIPT_DIR); \
	    for m in $(BASE_MODULES) $(EXTENSION_MODULES) ; do \
	        if test -f ../lib$$m.so -a \! -h lib$$m.so ; then \
	            $(LN) -s ../lib$$m.so . ; \
	        fi \
	    done \
	)
	$(TAR) cvf - $(TCLFILES) pkgIndex.tcl | \
		(cd $(INSTALL_GRAPHSCRIPT_DIR); $(TAR) xvf -)
	$(INSTALL_MAKE_DIR) $(INSTALL_IMAGES_DIR)
	(cd images; $(TAR) cf - *.gif *.GIF inspector/*/*.gif) | \
		(cd $(INSTALL_IMAGES_DIR); $(TAR) xvf -)
	$(INSTALL_MAKE_DIR) $(INSTALL_BITMAPS_DIR)
	for i in bitmaps/*.xbm ; do \
		$(INSTALL_FILE) $$i $(INSTALL_BITMAPS_DIR) ; \
	done
	$(INSTALL_FILE) graphscript.header $(INSTALL_GRAPHSCRIPT_DIR) ; \
