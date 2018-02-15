# This software is distributed under the Lesser General Public License
#
# utility.tcl
#
# This module implements several utilities for the editor.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/browse_url.tcl,v $
# $Author: himsolt $
# $Revision: 1.7 $
# $Date: 1999/03/05 20:41:00 $
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
	browse_url
}

##########################################
#
# GT::browse_url
#
##########################################



proc GT::browse_url { url args } {

    global GT_options
    global tcl_platform

    if { $tcl_platform(platform) != "unix" } {

	package require registry
	set cmd [registry get \
		     HKEY_CLASSES_ROOT\\htmlfile\\shell\\open\\command {}]
	regsub -all \\\\ $cmd \\\\\\\\ cmd
	eval exec $cmd $url

    } else {

	switch -regexp -- [lindex [file split $GT_options(www_browser)] end] {
	    
	    netscape*|comunicator* {
		if { $args != {} } {
		    set opencmd "openURL($url,$args)"
		} else {
		    set opencmd "openURL($url)"
		}
		exec $GT_options(www_browser) $opencmd &
	    }

	    default {
		exec xterm -e $GT_options(www_browser) $url &
	    }
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
