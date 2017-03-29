# This software is distributed under the Lesser General Public License
#
# graphicbar.tcl
#
# This module implements the graphicbar of the editor.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/editor/graphicbar.tcl,v $
# $Author: himsolt $
# $Revision: 1.2 $
# $Date: 1999/03/05 20:41:31 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet Project
#

package require Graphlet
package provide Graphscript [gt_version]

namespace eval GT {
    namespace export \
	create_graphicbar \
	init_status
}


#
# Create Graphicbar
#

proc GT::create_graphicbar { editor } {

    global GT GT_status GT_modes GT_menu

    set graphicbar [frame $editor.graphicbar -relief groove -borderwidth 2]
    
    set solid [image create bitmap -data {
#define black16x16_width 16
#define black16x16_height 16
static unsigned char black16x16_bits[] = {
   0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
   0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
   0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff};
}]

    foreach color {green red blue} {
	button $graphicbar.$color \
	    -foreground $color \
	    -image $solid
	pack $graphicbar.$color \
	    -side top
    }

    return $graphicbar
}


##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
