# This software is distributed under the Lesser General Public License
#
# dialog/exit.tcl
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/exit.tcl,v $
# $Author: himsolt $
# $Revision: 1.5 $
# $Date: 1999/03/05 20:41:10 $
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
	show_exitdialog
}

proc GT::show_exitdialog {{editor {}}} {

    set result [tk_dialog \
		    .exitdialog \
		    "Exit Graphlet ?" \
		    "Do you want to save the changes you made to the graph ?" \
		    warning  \
		    0 \
		    "Yes" "No" "Cancel" \
		   ]

    switch $result {
	0 {
	    if {[GT::action $editor file_save] != ""} {
		return yes
	    } else {
		return cancel
	    }
	}
	1 {
	    return no
	}
	2 {
	    return cancel
	}
    }
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
