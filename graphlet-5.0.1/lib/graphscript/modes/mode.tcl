# This software is distributed under the Lesser General Public License
#
# mode.tcl
#
# The description of filename goes HERE.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/modes/mode.tcl,v $
# $Author: himsolt $
# $Revision: 1.9 $
# $Date: 1999/06/08 13:53:05 $
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
	event_handler \
	leave_mode \
	remove_bindings \
	install_bindings \
	translate_bindings \
	switch_to_mode \
	ignore_event_handler
}


##########################################
#
# GT::switch_to_mode editor mode
#
# Mode switching
#
##########################################


proc GT_event_handler { event tag id type W A x y X Y K } {

    global GT GT_event GT_modes

    set GT_event(id) $id
    set GT_event(type) $type

    set GT_event(W) $W
    set GT_event(A) $A
    set GT_event(K) $K

    set GT_event(editor) [winfo toplevel $W]
    set GT_event(graph) $GT($GT_event(editor),graph)
    
    set GT_event(x) $x
    set GT_event(y) $y
    set GT_event(X) $X
    set GT_event(Y) $Y
    set GT_event(canvasx) [$W canvasx $x]
    set GT_event(canvasy) [$W canvasy $y]

    set state $GT_modes($GT_event(editor),current)
    set code 0
    if [info exists GT_modes($GT_event(editor),$state,$event,$tag)] {
	focus $GT($GT_event(editor),canvas)
	foreach handler $GT_modes($GT_event(editor),$state,$event,$tag) {
	    set code [catch {eval $handler} returncode]
	    switch $code {
		1 {		    
		    global errorInfo
		    error $returncode $errorInfo
		}
		3 {
		    break
		}
		4 {
		    continue
		}
	    }
	}
    }

    if [info exists returncode] {
	return -code $code $returncode 
    } else {
	GT::message .graphlet_0 "return -code $code"
	return -code $code
    }
}



proc GT::leave_mode { editor mode } {

    global GT_modes

    if [info exists GT_modes($mode,leave)] {
	set leave [$GT_modes($mode,leave) $editor $mode]
    } else {
	set leave {}
    }

    return $leave
}


proc GT::remove_bindings { editor mode } {

    global GT GT_modes

    if [info exists GT_modes($editor,$mode,installed_bindings)] {
	foreach binding $GT_modes($editor,$mode,installed_bindings) {
	    set state [lindex $binding 0]
	    set event [lindex $binding 1]
	    set tag [lindex $binding 2]
	    set GT_modes($editor,$state,$event,$tag) {}
	}
	set GT_modes($editor,$mode,installed_bindings) {}
    }
}


proc GT::install_bindings { editor mode { translate {} } } {

    global GT GT_modes
    set canvas $GT($editor,canvas)

    #
    # Install Bindings
    #

    set state $mode
    foreach binding $GT_modes($mode,bindings) {
	
	set tags [lindex $binding 0]
	set event [lindex $binding 1]
	set handler [lindex $binding 2]

	# Expand tags if neccessary

	switch $tags {
	    NODE {
		set tags { node:node node:label node:marker:selected }
	    }
	    EDGE {
		set tags { edge:edge edge:label edge:marker:selected }
	    }
	}

	# Install bindings

	foreach tag $tags {

	    if ![info exists GT_modes($editor,$state,$event,$tag)] {
		set cmd "GT_event_handler $event $tag %# %T %W %A %x %y %X %Y %K"
		switch $tag {
		    BACKGROUND {
			bind $canvas $event $cmd
		    }
		    default {
			$canvas bind $tag $event $cmd
		    }
		}
	    }

	    lappend GT_modes($editor,$state,$event,$tag) $handler
	    lappend GT_modes($editor,$mode,installed_bindings) \
		    [list $state $event $tag]
	}
    }

}


proc GT::translate_bindings { bindings { translate {} } } {

    #
    # Init translations
    #

    foreach translation { 1 2 3 } {
	set translations($translation) $translation
    }
    foreach translation $translate {
	set from [lindex $translation 0]
	set to [lindex $translation 1]
	set translations($from) $to
    }

    #
    # Install Bindings
    #

    set translated {}

    foreach binding $bindings {
	
	set tag [lindex $binding 0]
	set event [lindex $binding 1]
	set handler [lindex $binding 2]

	foreach translation [array names translations] {
	    regsub %$translation $event $translations($translation) event
	}

	lappend translated [list $tag $event $handler]
    }

    return $translated
}


proc GT::switch_to_mode { editor mode } {

    global GT GT_modes

    if {[lsearch $GT_modes(modes) $mode] == -1} {
	foreach m [array names GT_modes *,name] {
	    if {$GT_modes($m) == $mode} {
		set mode [lindex [split $m ,] 0]
	    }
	}
    }
    #
    # Leave the current mode (is allowed)
    #

    if {
	[info exists GT_modes($editor,last)] &&
	$GT_modes($editor,last) != {}
    } {
	set may_leave_last_mode [GT::leave_mode $editor $GT_modes($editor,last)]
    } else {
	set may_leave_last_mode {}
    }

    if { $may_leave_last_mode == {} } {

	#
	# Remove old bindings
	#

	if [info exists GT_modes($editor,last)] {
	    GT::remove_bindings $editor $GT_modes($editor,last)
	}

	#
	# Install new bindings
	#

	GT::install_bindings $editor $mode

	#
	# Update status & sidebar
	#
    
	if [info exists GT_modes($editor,current)] {
	    set button \
		$editor.modebar.[string tolower $GT_modes($editor,current)]
	    $button configure \
		-background [lindex [$button configure -background] 3] \
		-relief flat
	}

	set GT_modes($editor,current) $mode
	set GT_modes($editor,last) $mode

	if [info exists GT_modes($mode,init)] {
	    $GT_modes($mode,init) $editor $mode
	}

	$editor.modebar.[string tolower $GT_modes($editor,current)] configure \
	    -background white \
	    -relief sunken

    } else {
	set GT_modes($editor,current) $GT_modes($editor,last)
	GT::message $editor $may_leave_last_mode error
    }

    return $may_leave_last_mode
}



##########################################
#
# "Ignore event" handler
#
##########################################

proc GT::ignore_event_handler { } {
    return -code break;
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
