# This software is distributed under the Lesser General Public License
#
# mini_inspector.tcl
#
# This module implements a menu to change attribute settings
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/mini_inspector.tcl,v $
# $Author: himsolt $
# $Revision: 1.3 $
# $Date: 1999/03/05 20:40:13 $
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
	create_*_menu
}


proc GT::create_*_menu { editor menu } {

    proc ::mini_inspector { editor args } {
	
	global GT GT_selection
	set graph $GT($editor,graph)
	
	GT::excursion $editor {
	    foreach node $GT_selection($editor,selected,node) {
		eval $graph configure $node $args
	    }
	    foreach edge $GT_selection($editor,selected,edge) {
		eval $graph configure $edge $args
	    }
	    $graph draw [concat \
			     $GT_selection($editor,selected,node) \
			     $GT_selection($editor,selected,edge)]
	}
    }


    #
    # Global Attributes
    #

    set values(label_anchor)  {
	c  { -label "Center" }
	n  { -label "North" }
	nw { -label "North West" }
	w  { -label "West" }
	sw { -label "South West" }
	s  { -label "South" }
	se { -label "South East" }
	e  { -label "East" }
	ne { -label "North East" }
    }

    #
    # Graphics Attributes
    #

    set values(anchor) $values(label_anchor)

    set values(arrow) {
	none  { -label "none" }
	first { -label "first" }
	last  { -label "last" }
	both  { -label "both" }
    }

    set values(background)  {
	red    { -label "Red" -foreground red }
	green  { -label "Green" -foreground green }
	blue   { -label "Blue" -foreground blue }
	black  { -label "Black" -foreground black }
	white  { -label "White" -foreground white }
	gray5  { -label "Gray5" -foreground gray5 }
	gray10 { -label "Gray10" -foreground gray10 }
	gray25 { -label "Gray25" -foreground gray25 }
	gray50 { -label "Gray50" -foreground gray50 }
	gray75 { -label "Gray75" -foreground gray75 }
    }

    set values(bitmap) {
	error     { -bitmap error }
	gray25    { -bitmap gray25 }
	gray50    { -bitmap gray50 }
	hourglass { -bitmap hourglass }
	info      { -bitmap info }
	questhead { -bitmap questhead }
	question  { -bitmap question }
	warning   { -bitmap warning }
    }

    set values(capstyle) {
	butt       { -label "Butt" }
	projecting { -label "Projecting" }
	round      { -label "Round" }
    }

    set values(extent) {
	0   { -label 0 }
	45  { -label 45 }
	90  { -label 90 }
	135 { -label 135 }
	180 { -label 180 }
	225 { -label 225 }
	270 { -label 270 }
	315 { -label 315 }
    }

    set values(fill) $values(background)
    set values(foreground) $values(background)

    set values(image) {}
    foreach i [image names] {
	lappend values(image) $i [list -label $i]
    }


    set values(joinstyle) {
	round { -label "Round" }
	miter { -label "Miter" }
	bevel { -label "Bevel" }
    }

    set values(justify) {
	left   { -label "Left" }
	center { -label "Center" }
	right  { -label "Right" }
    }

    set values(outline) $values(fill)

    set values(smooth) {
	0 { -label "Off" }
	1 { -label "On" }
    }

    set values(start) $values(extent)

    set values(stipple) $values(bitmap)

    set values(style) {
	pieslice { -label "Pie Slice" }
	chord    { -label "Chord" }
	arc      { -label "Arc" }
    }

    set values(type)  {
	arc       { -label "Arc" }
	bitmap    { -label "Bitmap" }
	image     { -label "Image" }
	polygon   { -label "Polygon" }
	line      { -label "Line" }
	oval      { -label "Oval" }
	rectangle { -label "Rectangle" }
    }

    set values(width) {
	0  { -label 0 }
	1  { -label 1 }
	2  { -label 2 }
	3  { -label 3 }
	4  { -label 4 }
	5  { -label 5 }
	6  { -label 6 }
	7  { -label 7 }
	8  { -label 8 }
	9  { -label 9 }
	10 { -label 10 }
    }

    set values(visible) {
	1 { -label On }
	0 { -label Off }
    }

    #
    # Menu addition procs
    #

    $menu configure \
	-tearoff 0

    foreach {attr name} {
	label_anchor "Label Anchor"
	visible    "Visible"
    } {
	set submenu [string tolower $attr]
	$menu add cascade \
	    -label $name \
	    -menu $menu.$submenu
	menu $menu.$submenu \
	    -tearoff 0
	foreach {value description} $values($attr) {
	    eval $menu.$submenu add command \
		{ -command "mini_inspector $editor -$attr $value"} \
		$description
	}
    }

    $menu add separator

    foreach {attr name} {
	anchor     "Anchor"
	arrow      "Arrow"
	background "Background"
	bitmap     "Bitmap"
	capstyle   "Capstyle"
	extent     "Extent"
	fill       "Fill"
	foreground "Foreground"
	image      "Image"
	joinstyle  "Joinstyle"
	outline    "Outline"
	smooth     "Smooth"
	start      "Start"
	style      "Style"
	type       "Type"
	width      "Width"
    } {
	set submenu [string tolower $attr]
	$menu add cascade \
	    -label $name \
	    -menu $menu.graphics_$submenu
	menu $menu.graphics_$submenu \
	    -tearoff 0
	foreach {value description} $values($attr) {	
	    eval $menu.graphics_$submenu add command \
		{ -command "mini_inspector $editor graphics -$attr $value"} \
		$description
	}
    }

    $menu add separator

    foreach {attr name} {
	anchor  "Anchor"
	fill    "Fill"
	justify "Justify"
	outline "Outline"
	stipple "Stipple"
	type    "Type"
    } {
	set submenu [string tolower $attr]
	$menu add cascade \
	    -label $name \
	    -menu $menu.label_graphics_$submenu
	menu $menu.label_graphics_$submenu \
	    -tearoff 0
	foreach {value description} $values($attr) {	
	    eval $menu.label_graphics_$submenu add command \
		{ -command "mini_inspector $editor label_graphics -$attr $value"} \
		$description
	}
    }
}

