# This software is distributed under the Lesser General Public License
#
# dialog/options.tcl
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/options.tcl,v $
# $Author: himsolt $
# $Revision: 1.21 $
# $Date: 1999/03/05 20:41:15 $
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
	global_options_dialog
}


##########################################
#
# GT::options_dialog
#
##########################################


proc GT::global_options_dialog { editor } {

    global GT_options

    ##########################################
    #
    # Global Options
    #
    ##########################################

    lappend options { tab "General" }

    lappend options {
	group "Modes"
    } {
	menu "Default Mode" default_mode
	"Select the default mode for a new window."
	{ "Create" "Edit" "Text" }
    } {
	check "Edit Mode always on middle mouse button"
	edit_mode_on_extra_mouse_button
	"If this option is checked, then the middle mouse button (if available) provides access to edit mode functions."
    } {
	endgroup
    }

    lappend options {
	group "WWW"
    }

    if {$::tcl_platform(platform) == "unix"} {
	lappend options {
	    string "Browser:" www_browser
	    "Path to the WWW browser."
	}
    }

    lappend options {
	string "Graphlet Homepage" www_address
	"The WWW address of Graphlet's homepage. Change this if you have a
local copy."
    }

    lappend options {
	group "Tooltips"
    }

    lappend options {
	check "Show Tooltips" tooltips
	"Show MS Windows-ish tooltops."
    }

    lappend options {
	integer "Delay (ms)" tooltips_interval
	"Time (milliseconds) after which tooltips pops up."
	-scale 1
	-from 0
	-to 1000
	-res 100
    }

    lappend options {
	endgroup
    }

    lappend options {
	endtab
    }

    ##########################################
    #
    # GUI Options
    #
    ##########################################

    lappend options {
	newtab "User"
    }

    lappend options {
	group "General"
    } {
	check "Expert User" expert
	"Expert users get some additional features such as apply buttons for layout algorithm options. This option becomes effective after restart."
    } {
	check "Developer" developer
	"Show additional \"Developer\" menu. This option becomes effective after restart."
    } {
	endgroup
    }

    lappend options {
	group "Inspector"
    } {
	check "Dock into editor" inspector_dock_into_editor
	"Do not create new window for Inspector but use the editor window.\
          This option becomes effective after restart."
    } {
	endgroup
    }

    lappend options {
	group "Text Mode"
    } {
	check "Text Mode uses label editor" text_mode_uses_label_editor
	"In text mode, a click opens the label editor window."
    } {
	endgroup
    }

    lappend options {
	endtab
    }

    ##########################################
    #
    # GUI Options
    #
    ##########################################

    lappend options {
	newtab GUI
    }

    lappend options { newgroup "Marker" }

    lappend options {
	integer	"Width" marker_width
	"Size of the selection marker."
	-scale 1
	-from 1
	-to 5
	-res 1
    }

    lappend options {
	color "Color" marker_color
	"Color of the selection marker."
    }

    lappend options {
	color "Endnode Marker" temporary_endnode_indicator_color
	"Color of the marker which shows an the endnode when an edge is created."
    }

    lappend options endgroup

    lappend options { newgroup "Selection" }

    lappend options {
	radio "Selection Mode" select_in_rectangle
	"Select all enclosed or all overlapping objects."
	{
	    {"Enclosed" enclosed} {"Overlapping" overlapping}
	}
    }

    lappend options {
	radio "Rubberbox Selection" select_with_rubberbox
	"What to select with a rubberbox: only nodes, only edges or both."
	{
	    {"Nodes" nodes}
	    {"Edges" edges}
	    {"Both" all}
	}
    }

    lappend options {
	integer "Minumim Selection Rectangle" minimum_selection_rect_width
	"Rectangles which are smaller than this size are regarded as clicks."
	-scale 1
	-from 1
	-to 5
    }

    lappend options {
	integer "Overlap Gap" overlap_gap
	"Objects which are less than the given numbers of pixels away from the selection rectangle are included in the selection."
	-scale 1
	-from 1
	-to 5
    }

    lappend options {
	integer "Bend Overlap Gap" bend_overlap_gap
	"When selecting a bend, allow a gap of this number of pixels."
	-scale 1
	-from 1
	-to 5
    }

    lappend options {
	integer "Small Selection" small_selection_treshold
	"If the selection contains fewer nodes and edges than this, move nodes and edges in realtime. Otherwise, use a faster method which does not update the edges."
	-scale 1
	-from 0
	-to 30
    }

    lappend options {
	endgroup
    }

    lappend options {
	endtab
    }

    ##########################################
    #
    # GUI 2
    #
    ##########################################

    lappend options { newtab GUI2 }

    lappend options {
	check "Select after create" select_after_create
	"If this button is checked, a new node or edge is automatically selected."
    }

    lappend options {
	check "Audible Bell" bell
	"If this button is checked, ring the bell if an error occurs."
    }

    lappend options {
	check "Paste at Cursor Position" paste_at_cursor_position
	"If this option is checked, then paste operations go to the cursor position if possible. Otherwise, objects are pasted at their last position."
    }

    lappend options { newgroup "Adjust Size to Label" }

    lappend options {
	check "Adjust Node Size to Label" adjust_size_to_label
	"If this option is checked, then nodes automatically adapt "
    }

    lappend options {
	radio "Minimum Node Size" adjust_size_to_label_minimum
	"If this option is checked, node sizes cannot get smaller than the
default node size if the option \"Adjust size to label\" is active"
	{
	    {"Default Size" default}
	    {"Label Size" label}
	}
    }

    lappend options endgroup

    lappend options {
	group "Default Font"
    } {
	string "Default Font" system_default_font
	"System default font for nodes and edges."
    } {
	integer "Default Font Size" system_default_font_size
	"System default font size for nodes and edges."
    } {
	radio "Default Font Style" system_default_font_style
	"System default font style for nodes and edges."
	{
	    {"Roman" roman}
	    {"Bold" bold}
	    {"Italic" italic}
	    {"Overstrike" overstrike}
	    {"Underline" underline}
	}
    }

    lappend options {
	endtab
    }


    ##########################################
    #
    # Window Options
    #
    ##########################################

    lappend options { newtab "Window" }

    lappend options {
	newgroup "Drawing Area"
    }

    lappend options {
	float "Default Width:" canvas_maxx
	"Width of the drawing area. This option becomes effective when a new editor is opened."
    }
    lappend options {
	float "Default Height:" canvas_maxy
	"Height of the drawing area This option becomes effective when a new editor is opened."
    }

    lappend options {
 	float "Grid Width:" grid
	"Width of the grid in the current window. A value of 0 means no grid. This option becomes effective when a new editor is opened."
    }

    lappend options {
	endgroup
    }

    lappend options {
	newgroup "Window Size"
    }

    lappend options {
 	radio "Custom Window Size" custom_graphlet_window
	"Determines the size of a new window"
	{
	    { None default }
	    { "Use Size Below" explicit }
	    { Fullscreen fullscreen }
	}
    }

    lappend options {
	integer "Width:" graphlet_window_width
	"Width of a new editor window if \"Custom Window Size\" is set to \"Use Size Below\". This option becomes effective when a new editor is opened."
    }
    lappend options {
	integer "Height:" graphlet_window_height
	"Height of a new editor window if \"Custom Window Size\" is set to \"Use Size Below\". This option becomes effective when a new editor is opened."
    }
    lappend options {
	integer "Left:" graphlet_window_x
	"Upper left corner of a new editor window if \"Custom Window Size\" is set to \"Use Size Below\". This option becomes effective when a new editor is opened."
    }
    lappend options {
	integer "Top:" graphlet_window_y
	"Top corner of a new editor window if \"Custom Window Size\" is set to \"Use Size Below\". This option becomes effective when a new editor is opened."
    }

    lappend options {
	endgroup
    }

    lappend options {
	endtab
    }

    ##########################################
    #
    # Fileselector Options
    #
    ##########################################

    lappend options { newfolder "File" }

    lappend options { newgroup "File Format" }
    lappend options {
	radio "Version:" gml_version
	"GML Version. Choose version 2 for compact files, and version 1 for backward compatibility. Note that version 2 files may not work with older parsers."
	{
	    {"Version 1 (compatible)" 1}
	    {"Version 2 (compact)" 2}
	}
    }
    lappend options endgroup

#     lappend options { newgroup "Fileselector" }

#     lappend options {
# 	string	"Default Directory" fileselector_directory
# 	"Sets the default directory for opening and saving files. \".\" is the current directory."
#     }

#     lappend options {
# 	radio "Fileselector Placement" fileselector_center
# 	"Fileselector Placement"
# 	{
# 	    {"Defaul" no}
# 	    {"Center over Parent" parent}
# 	    {"Center over Screen" screen}
# 	}
#     }

#     lappend options {
# 	check "Use System Fileselector" use_native_fileselector
# 	"If this option is checked, then Graphlet uses the native file
# selector provided by Tk. Otherwise, Graphlet's replacement is used."
#     }

    lappend options {
	endgroup
    }

    lappend options {
	endtab
    }


    ##########################################
    #
    # Crazy Options
    #
    ##########################################

    global GT_options GT_default_options

#     set GT_default_options(Crash) 1
#     set GT_default_options(Speed) fast
#     set GT_default_options(deterministicBehaviour) 0
#     set GT_default_options(meaningOfLife) 42
#     set GT_default_options(food) Sandwich
#     set GT_default_options(drink) Tea

#     lappend options { newfolder
# 	"Crazy"
#     }

#     lappend options {
# 	integer	"The Meaning of Life" meaningOfLife
# 	"42"
#     }

#     lappend options { newgroup "Speed" }
#     lappend options {
# 	radio "Speed" Speed
# 	"Customizable speed. Not implemented on Windows or UNIX platforms. "
# 	{
# 	    { "Incredibly slow" very_slow }
# 	    { "Very slow" very_slow }
# 	    { Slow slow}
# 	    { Normal normal }
# 	    { Fast fast }
# 	    { "Very fast" very_fast }
# 	    { "Incredibly fast" very_fast }
# 	}
# 	-direction vertical
#     }
#     lappend options endgroup

#     lappend options { newgroup Stability }
#     lappend options {
# 	check "Occasional Crash" Crash
# 	"Behave like expected. Or not."
#     }
#     lappend options {
# 	check "Deterministic behaviour" deterministicBehaviour
# 	"If this option is checked, then Graphlet works not as you expect.
# Uncheck this options if you dont expect this."
#     }
#     lappend options endgroup


#     lappend options { newgroup "Favorites" }
#     lappend options {
# 	radio "Food:" food
# 	"What do you want to eat for lunch ?"
# 	{
# 	    {"Pizza" Pizza}
# 	    {"Sandwich" Sandwich}
# 	    {"Bagel" Bagel}
# 	}
#     }
#     lappend options {
# 	radio "Drink:" drink
# 	"What do you want to drink for lunch ?"
# 	{
# 	    {"Coffee" Coffee}
# 	    {"Tea" Tea}
# 	    {"Coke" Coke}
# 	}
#     }
#     lappend options endgroup

    GT::create_tabwindow $editor options "Graphlet Options" $options
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***



