# This software is distributed under the Lesser General Public License
#
# utility.tcl
#
# This module implements several utilities for the editor.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/image.tcl,v $
# $Author: himsolt $
# $Revision: 1.6 $
# $Date: 1999/03/05 20:40:10 $
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
	get_image
}


##########################################
#
# GT::image name filename
#
# Utility to load an image. The global variable GT_images holds
# the set of all loaded images.
#
##########################################


proc GT::get_image {name {imagename ""}} {

    global GT GT_images GT_options

    if [info exists GT_images($name)] {

	return $GT_images($name)

    } elseif {[lsearch [image names] $name] >= 0} {

	return $name

    } else {

	set try [list \
		     $name \
		     [file join $GT(image_dir) $name] \
		     [file join $GT(bitmap_dir) $name] \
		     [file join $GT(image_dir) $name.ppm] \
		     [file join $GT(image_dir) $name.gif] \
		     [file join $GT(bitmap_dir) $name.xbm] \
		    ]
	foreach file $try {
	    if [file exists $file] {
		set imagename $file
		break
	    }
	}

	if { $imagename == "" } {
	    return ""
	}

	set extension [string tolower [file extension $imagename]]
	if { $extension == ".gif" || $extension == ".ppm" } {
	    image create photo $name -file $imagename
	    set GT_images($name) $name
	    return $GT_images($name)
	} else {
	    image create bitmap $name -file $imagename
	    set GT_images($name) $name
	    return $GT_images($name)
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
