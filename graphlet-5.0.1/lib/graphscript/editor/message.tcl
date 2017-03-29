# This software is distributed under the Lesser General Public License
#
# message.tcl
#
# The description of filename goes HERE.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/editor/message.tcl,v $
# $Author: himsolt $
# $Revision: 1.9 $
# $Date: 1999/03/05 20:41:37 $
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
	create_message_label \
	reset_message
}

# GT::message not exported to avoid autoloading problems with
# Tk's message. Since this file is loaded at startup, this is a
# hack but not a problem -- MH, Oct 2 98

proc GT::create_message_label { editor } {

    global GT GT_status

    frame $editor.message

    #
    # Number of nodes & edges
    #

    set graph $GT($editor,graph)

    label $editor.message.nodes \
	-textvariable GT_status($graph,nodes) \
	-width 4 \
	-foreground blue3
    pack  $editor.message.nodes \
	-side left
    GT::tooltips $editor.message.nodes \
	"Number of nodes in the graph"

    label $editor.message.edges \
	-textvariable GT_status($graph,edges) \
	-width 4 \
	-foreground blue3
    pack  $editor.message.edges \
	-side left \
	-padx 2
    GT::tooltips $editor.message.edges \
	"Number of edges in the graph"

    #
    # Message Label
    #

    if [info exists GT($editor,default_message)] {
	set GT($editor,message) $GT($editor,default_message)
    } else {
	set GT($editor,message) $GT(default_message)
    }

    label $editor.message.label \
	-textvariable GT($editor,message)
    pack $editor.message.label \
	-fill x

    GT::tooltips $editor.message.label \
	"Graphlet displays messages in this area."

    #
    # individual look for windows & unix
    #

    global tcl_platform
    foreach child [winfo children $editor.message] {
	if { $tcl_platform(platform) == "windows" } {
	    
	    $child configure \
		-relief groove \
		-bd 1
	    
	} else {
	    
	    $child configure \
		-relief ridge \
		-bd 1
	}
    }
    
    return $editor.message
}



proc GT::reset_message editor {

    global GT GT_tooltips

    if [winfo exists $editor] {

	if [info exists GT($editor,default_message)] {
	    set GT($editor,message) $GT($editor,default_message)
	} else {
	    set GT($editor,message) $GT(default_message)
	}
	$editor.message.label configure -foreground "#000000"
	set GT_tooltips($editor.message.label) \
		"Graphlet displays messages in this area."
    }

}



proc GT::message { editor message {level message} {goaway 10} } {

    global GT GT_options GT_tooltips

    set GT($editor,message) $message

    switch $level {

	error {
	    # red
	    $editor.message.label configure -foreground "#FF0000"
	    if $GT_options(bell) {
		bell
	    }
	    append GT_tooltips($editor.message.label) \
		"\nThis message is red because it signals an error."
	}

	warning {
	    # light red
	    $editor.message.label configure -foreground "#FF8080"
	    if $GT_options(bell) {
		bell
	    }
	    append GT_tooltips($editor.message.label) \
		"\nThis message is light red because it signals a warning."
	}

	message {
	    # black
	    $editor.message.label configure -foreground "#000000"
	}

	default {
	}
    }

    if { $goaway != "never" && $goaway != 0 } {

	if [info exists GT($editor,afterid)] {
	    after cancel $GT($editor,afterid)
	    unset GT($editor,afterid)
	}

	set id [after [expr 1000*$goaway] "GT::reset_message $editor"]
	set GT($editor,afterid) $id
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
