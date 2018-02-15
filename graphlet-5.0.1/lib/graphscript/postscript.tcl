# This software is distributed under the Lesser General Public License
#
# This file implements several utilities for PostScript output.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/postscript.tcl,v $
# $Author: himsolt $
# $Revision: 1.4 $
# $Date: 1999/03/05 20:40:18 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet project
#

package require Graphlet
package provide Graphscript [gt_version]


namespace eval GT {
    namespace export \
	save_as_postscript \
	PS_select_size
}



proc GT::save_as_postscript  { editor filename } {

    global GT GT_options
    set canvas $GT($editor,canvas)
    set graph $GT($editor,graph)

    ##########################################
    #
    # Remove the selection
    #
    ##########################################

    global GT_selection
    set saved_selection $GT_selection($editor,selected,node)
    GT::select $editor remove selection

    ##########################################
    #
    # Preprocess standard options
    #
    ##########################################

    set options {}

    if { $GT_options(postscript_colormode) != {} } {
	lappend options -colormode $GT_options(postscript_colormode)
    }

    if { $GT_options(postscript_anchor) != {} } {
	lappend options -pageanchor $GT_options(postscript_anchor)
    }

    if { $GT_options(postscript_rotate) != {} } {
	lappend options -rotate $GT_options(postscript_rotate)
    }


    ##########################################
    #
    # PostScript Frame
    # inner = around the graph, inside cropmarks
    # outer = around the graph, outside cropmarks
    #
    ##########################################

    foreach i { inner outer } {
	foreach j { left right top bottom } {
	    set option postscript_${i}_frame${j}
	    if { $GT_options($option) != {} } {
		set frame($i,$j) [winfo fpixels $canvas $GT_options($option)]
	    } else {
		set frame($i,$j) 0
	    }	    
	}
    }

    #
    # Temporaryly move the graph
    #

    if { $GT_options(postscript_crop_marks) ||
	 $GT_options(postscript_signature) } {

	set move_x [expr $frame(outer,left) + $frame(inner,left) + 42]
	set move_y [expr $frame(outer,top) + $frame(inner,top) + 42]

	$canvas move all $move_x $move_y
    }


    ##########################################
    #
    # Determine the area to be printed
    #
    ##########################################

    switch $GT_options(postscript_area) {

	all {
 	    set bbox [$GT($editor,canvas) bbox all]
	    set area(x) [lindex $bbox 0]
	    set area(y) [lindex $bbox 1]
	    set area(width) [expr [lindex $bbox 2] - [lindex $bbox 0]]
	    set area(height) [expr [lindex $bbox 3] - [lindex $bbox 1]]
	}

	visible {
 	    set bbox [GT::visible_drawing_area $editor]
	    set area(x) [lindex $bbox 0]
	    set area(y) [lindex $bbox 1]
	    set area(width) [expr [lindex $bbox 2] - [lindex $bbox 0]]
	    set area(height) [expr [lindex $bbox 3] - [lindex $bbox 1]]
	}

	selected {
	    foreach object $saved_selection {
		$canvas addtag GT::print_selected withtag $object
	    }
	    set bbox [$GT($editor,canvas) bbox GT::print_selected]
	    $canvas dtag GT::print_selected withtag
	    set area(x) [lindex $bbox 0]
	    set area(y) [lindex $bbox 1]
	    set area(width) [expr [lindex $bbox 2] - [lindex $bbox 0]]
	    set area(height) [expr [lindex $bbox 3] - [lindex $bbox 1]]
	}

	custom {

	    foreach i { x y width height } {
		if { $GT_options(postscript_$i) != {} } {
		    set area($i) \
			[winfo fpixels $canvas $GT_options(postscript_$i)]
		} else {
		    set area($i) 0
		}
	    }

	}
    }

    #
    # Expand area by *inner* frame size
    #

    set area(x) [expr $area(x) - $frame(inner,left)]
    set area(y) [expr $area(y) - $frame(inner,top)]
    foreach j { left right } {
	set area(width) [expr $area(width) + $frame(inner,$j)]
    }
    foreach j { top bottom } {
	set area(height) [expr $area(height) + $frame(inner,$j)]
    }

    #
    # Compute area aspect
    #

    if { $area(width) != 0 && $area(height) != 0 } {
	set aspect [expr double($area(width)) / double($area(height))]
    }


    ##########################################
    #
    # PostScript Page Size & Location
    #
    ##########################################

    set page(select) $GT_options(postscript_select)
    GT::PS_select_size GT_options page

    if { $page(width) != {} && $page(height) != {} } {
	set pageaspect [expr double($page(width)) / double($page(height))]
    } else {
	set pageaspect 1
    }

    
    #
    # If page width and height are specified, subtract frame width
    #

    if { $page(width) != {} } {
	foreach i { inner outer} {
	    foreach j { left right } {
		set page(width) [expr $page(width) - $frame($i,$j)]
	    }
	}
    }

    if { $page(height) != {} } {
	foreach i { inner outer} {
	    foreach j { top bottom } {
		set page(height) [expr $page(height) - $frame($i,$j)]
	    }
	}
    }

    ##########################################
    #
    # Printing starts here
    #
    ##########################################

    #
    # Create a frame around the graph (temporary)
    #

    if { $GT_options(postscript_area) == "all" } {

	set x0 [expr $area(x)]
	set y0 [expr $area(y)]
	set x1 [expr $area(x) + $area(width)]
	set y1 [expr $area(y) + $area(height)]

	if $GT_options(postscript_crop_marks) {
	    $canvas create rectangle $x0 $y0 $x1 $y1 \
		-tags GT::postscript_crop_marks
	}
	
	if $GT_options(postscript_signature)  {

	    if { [info exists GT($editor,filename)] &&
		 $GT($editor,filename) != {} } {
		set text "$GT($editor,filename) by Graphlet $GT(version)"
	    } else {
		set text "Graphlet $GT(version)"
	    }

	    $canvas create text $x1 $y1 \
		-anchor ne \
		-text $text \
		-tags GT::postscipt_signature
	}

	#
	# Recompute the area now. Note that we dont need to
	# incorporate the inner frame this time.
	#

	set bbox [$GT($editor,canvas) bbox all]
	set area(x) [lindex $bbox 0]
	set area(y) [lindex $bbox 1]
	set area(width) [expr [lindex $bbox 2] - [lindex $bbox 0]]
	set area(height) [expr [lindex $bbox 3] - [lindex $bbox 1]]
    }


    # Append print area to options

    foreach i { x y width height } {
	if { $area($i) != 0 } {
	    lappend options -$i $area($i) 
	}
    }

    if { $page(width) != {} && $page(width) != {} } {
# 	parray page
# 	parray area
# 	puts [expr $page(width)/$area(width)]
# 	puts [expr $page(height)/$area(height)]
	if { $page(width)/$area(width) >= $page(height)/$area(height) } {
	    if {$GT_options(postscript_rotate)} {
		lappend options -pageheight $page(height)
	    } else {
		lappend options -pageheight $page(height)
	    }
	} else {
	    if {$GT_options(postscript_rotate)} {
		lappend options -pagewidth $page(width)
	    } else {
		lappend options -pagewidth $page(width)
	    }
	}
    } elseif { $page(height) != {} && $page(width) == {} } {
	lappend options -pageheight $page(height)
    } elseif { $page(height) == {} && $page(width) != {} } {
	lappend options -pagewidth $page(width)
    } else {
	# Just relax
    }

    #
    # print it, really
    #

    update

    set saved_cursor [$editor cget -cursor]
    $editor configure -cursor watch
    eval $GT($editor,canvas) postscript -file [list $filename] $options
    # puts "eval $GT($editor,canvas) postscript -file $filename $options"
    $editor configure -cursor $saved_cursor

    #
    # Remove the temporary frame
    #

    if $GT_options(postscript_crop_marks) {
	$canvas delete GT::postscript_crop_marks
    }
    
    if $GT_options(postscript_signature) {
	$canvas delete GT::postscipt_signature
    }

    #
    # If the graph was moved before, move it back
    #

    if [info exists move_x] {
	$canvas move all -$move_x -$move_y
    }


    ##########################################
    #
    # Some hacks to round it up ...
    #
    ##########################################

    #
    # Temporary bugfix (?) in UNIX to make the file readable
    #

    global tcl_platform tk_version tk_patchLevel
    if { $tcl_platform(platform) == "unix" && $tk_patchLevel == "4.2" } { 

	exec "chmod" "u+rw" "$filename"
    }

    #
    # Temporary bugfix (?) in UNIX to insert "%!PS-Adobe-3.0 EPSF-3.0"
    # into the PostScript file header (Version 4.2 unpatched only)
    #
    
    global tk_patchLevel
    if { $tk_patchLevel == "4.2" } {

	set tmp_filename "$filename.[pid]"
	
	file rename -force $filename $tmp_filename
	set new_file [open $filename w]
	puts $new_file {%!PS-Adobe-3.0 EPSF-3.0}
	set old_file [open $tmp_filename r]
	while { [gets $old_file line] >= 0 } {
	    puts $new_file $line
	}
	close $new_file
	close $old_file
	file delete -force $tmp_filename
    }
}


proc GT::PS_select_size { var_options var_page } {

    upvar $var_options options
    upvar $var_page page

    switch -exact $page(select) {
	"US Letter" {
	    set page(x) {}
	    set page(y) {}
	    set page(width) 8.5i
	    set page(height) 11i
	}
	A3 {
	    set page(x) {}
	    set page(y) {}
	    set page(width) 842
	    set page(height) 1191
	}
	A4 {
	    set page(x) {}
	    set page(y) {}
	    set page(width) 595
	    set page(height) 842
	}
	A5 {
	    set page(x) {}
	    set page(y) {}
	    set page(width) 420
	    set page(height) 595
	}
	A6 {
	    set page(x) {}
	    set page(y) {}
	    set page(width) 297
	    set page(height) 420
	}
	Custom {
	    foreach i { x y width height } {
		set option postscript_page$i
		if { $options($option) != {} } {
		    set page($i) [winfo fpixels $canvas $options($option)]
		} else {
		    set page($i) {}
		}
	    }
	}
	None {
	    set page(x) {}
	    set page(y) {}
	    set page(width) {}
	    set page(height) {}
	}
	default {
	    set page(x) {}
	    set page(y) {}
	    set page(width) {}
	    set page(height) {}
	}
    }

    if { $options(postscript_rotate) != {} && $options(postscript_rotate)} {

	switch -regexp -- $page(select) {
	    "US Letter|A3|A4|A5|A6" {
		set x $page(x)
		set y $page(y)
		set width $page(width)
		set height $page(height)		
		set page(x) $y
		set page(y) $x
		set page(width) $height
		set page(height) $width
	    }
	    default {
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
