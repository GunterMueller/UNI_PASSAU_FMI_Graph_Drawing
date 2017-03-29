# This software is distributed under the Lesser General Public License
#
# dialog/overwrite.tcl
#
# This file implements a dialog which warns of overwritten files.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/overwrite.tcl,v $
# $Author: himsolt $
# $Revision: 1.3 $
# $Date: 1999/03/05 20:41:17 $
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
	show_overwrite_file_dialog
}



proc GT::show_overwrite_file_dialog { top filename } {

    set ok 1

    if { $filename != ""} {

	if ![file exists [file dirname $filename]] {

	    set msg "The path [file dirname $filename] does not exist."

	    set result [tk_dialog \
			    $top.overwritefile \
			    "Path does not exist" \
			    $msg \
			    warning  \
			    0 \
			    "OK" \
			   ]
	    return 0
	}

	if { $ok && [file exists $filename] } {

	    set msg "The file\n[file tail $filename]\nalready exists.\n
Really overwrite ?"

	    set result [tk_dialog \
			    $top.overwritefile \
			    "Overwrite File ?" \
			    $msg \
			    warning  \
			    1 \
			    "Overwrite" "Cancel" \
			   ]
	    set ok [expr $result == 0]
	}

    }

    return $ok
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
