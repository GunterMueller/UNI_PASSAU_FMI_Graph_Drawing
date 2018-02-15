# This software is distributed under the Lesser General Public License
#
# fileselect.tcl
#
# This module implements the file select dialog
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/fileselect.tcl,v $
# $Author: himsolt $
# $Revision: 1.4 $
# $Date: 1999/03/05 20:40:05 $
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
    namespace export fileselector
}
namespace eval GT::FS {
    namespace export \
	init \
	run \
	done \
	setup_traces \
	create \
	resources \
	create_toplevel \
	create_statusBar \
	create_buttonPanel \
	create_dirPanel \
	create_filePanel \
	create_filesPanel \
	create_dirsPanel \
	setup_bindings \
	ok \
	valid_result \
	cancel \
	take \
	click \
	complete \
	listbox_search \
	init_history \
	append_history \
	show_history \
	select_history \
	set_dir \
	read_dir \
	get_path \
	simplify_path \
	is_root \
	init_patterns \
	set_pattern \
	append_pattern \
	extract_pattern \
	get_pattern \
	ferror \
	select \
	adjust_selection \
	get_underlined \
	update_info \
	wait_cursor \
	standard_cursor
}

#
# USAGE: GT::fileselector ?options?
#
# OPTIONS:
#   -directory <dirname>
#       Sets starting directory to <directory>. <directory> can be
#       either an absolute or a relative path name. It can contain the
#       "~"-character as well to access the users or someone else's home
#       directory.
#       By default the last directory is used.
#
#   -center <mode>
#       Specifies the center mode of the file selector. Possible Values
#       are "no" "yes" "parent" "screen", where "yes" is equivalent to
#       "parent". This is the default also.
#
#   -geometry <geom>
#       Should be clear. Defaults to "+100+100"
#
#   -historylength <length>
#       Sets the maximum length of file name history to <length>. By
#       default the last 10 file names are stored.
#
#   -mode <mode>
#       Sets the file selector mode to <mode>. Allowed values are "load"
#       and "save". Defaults to "load"
#
#   -parent <widget>
#       Sets the file selector's parent window to <widget>. By default
#       "." is used.
#
#   -patternhistlength <length>
#       Sets the maximum length of user pattern history to <length>. By
#       default the last 10 user patterns are stored.
#      
#   -title <title>
#       Sets the window title to <title>. The default value is either
#       "Open File" or "Save File", depending on the mode given with
#       -mode.
#
#   -types <types>
#       Sets the available search patterns to <types>. This has to be
#       a list consisting of entries like "All Files (*)", where the
#       pattern itself stands between "(" and ")". The rest of the
#       string ist description only. The default value is
#       {"All Files (*)"}.
#

#
# TODO:
#   - ../*
#   - ~xyz
#   - Windows
#

########################################################################
#
#   Main Procedure
#
########################################################################

proc GT::fileselector { args } {
    global GT_FS

    set File [eval GT::FS::init $args]

    if {[string length $File] != 0} {
	return $File
    } else {
	GT::FS::run 
	return [GT::FS::done]
    }
}


proc GT::FS::init { args } {
    global GT_FS GT_options env tcl_platform

    if [info exists GT_FS(lastDir)] {
	if ![file exists $GT_FS(lastDir)] {
	    set GT_FS(lastDir) "."
	}
    } else {
	set GT_FS(lastDir) "."
    }

    # check the arguments

    GT::DU::parse_args [list \
			  {center "parent"} \
			  "directory $GT_FS(lastDir)" \
			  {geometry "+100+100"} \
			  {historylength 10} \
			  {mode "load"} \
			  {parent "."} \
			  {patternhistlength 10} \
			  {title ""} \
			  {types {"All Files (*)"}} \
			 ]

    if {$mode != "load"} {
	set mode "save"
    }

    if {$title == ""} {
	if {$mode == "load"} {
	    set title "Open File"
	} else {
	    set title "Save File"
	}
    }

    global tk_version
    if	{ $GT_options(use_native_fileselector) && $tk_version != "4.1" } {
	foreach type $types {
	    regexp {([^\(]*) \((.*)\)(.*)} $type dummy Name Pattern
	    lappend Patterns [list $Name $Pattern]
	}
	if {$directory == "."} {
	    set directory [pwd]
	}
	if {$mode == "load"} {
	    set File [tk_getOpenFile \
			-filetypes $Patterns \
			-initialdir $directory \
			-parent $parent \
			-title $title]
	} else {
	    set File [tk_getSaveFile \
			-filetypes $Patterns \
			-initialdir $directory \
			-parent $parent \
			-title $title]
	}
	set GT_FS(lastDir) [file dirname $File]
	return $File
    }

    # init histories

    GT::FS::init_patterns $patternhistlength
    GT::FS::init_history $historylength

    # init global variables

    set GT_FS(mode) $mode
    set GT_FS(types) \
	[concat $types $GT_FS(userPatterns)]
    set GT_FS(home) $env(HOME)
    if {$tcl_platform(platform) == "windows"} {
	regsub -all {\\} $GT_FS(home) "/" GT_FS(home)
    }

    # Create the dialog window

    set Dlg [GT::FS::create $parent]

    # set up bindings and window properties

    GT::FS::setup_bindings 
    GT::FS::setup_traces

    # window manager options

    wm title $Dlg $title
    wm minsize $Dlg 400 176
    wm transient $Dlg $parent
    wm geometry $Dlg $geometry

    # show window offscreen to calculate its width/height

    wm geometry $Dlg +[winfo screenwidth $Dlg]+[winfo screenheight $Dlg]
    tkwait visibility $Dlg

    # center dialog

    GT::DU::center $Dlg $center $geometry

    # set up dialog

    catch {grab set $Dlg} ;# sometimes returns errors - don't know why
    tkwait visibility $GT_FS(fileEntry)
    focus $GT_FS(fileEntry)

    # adjust the given directory path and read directory

    tkwait visibility $GT_FS(dirList)
    if {$directory != ""  && \
	    [string index $directory 0] != "/" && \
	    [string index $directory 0] != "~"} {
	set directory "[pwd]/$directory"
    }
    GT::FS::set_dir $directory

    return ""
}

proc GT::FS::run { } {
    global GT_FS

    set GT_FS(done) false
    tkwait variable GT_FS(done)
}


proc GT::FS::done { } {
    global GT_FS

    if ![info exists GT_FS(dialog)] {
	return ""
    }

    # delete dialog
    grab release $GT_FS(dialog)
    destroy $GT_FS(dialog)

    # delete all global variables except history and userPatterns

    set Result $GT_FS(result)
    set Variables {history userPatterns}

    foreach var $Variables {
	if [info exists GT_FS($var)] {
	    set $var $GT_FS($var)
	}
    }

    unset GT_FS

    foreach var $Variables {
	if [info exists $var] {
	    set GT_FS($var) [set $var]
	}
    }

    set GT_FS(lastDir) [file dirname $Result]

    return $Result
}


proc GT::FS::setup_traces { } {
    global GT_FS tcl_platform

    trace variable GT_FS(file) w "GT::FS::update_info"
    trace variable GT_FS(pattern) w "GT::FS::read_dir"

    if {$tcl_platform(platform) == "unix"} {
	trace variable GT_FS(showHidden) w "GT::FS::read_dir"
    }
}

########################################################################
#
#   Dialog Creation
#
########################################################################

#
# Structure of the file selector:
#
# +-----------------------------------+-------------+
# | filePanel (3)                     | buttonPanel |
# +-----------------------------------+ (2)         |
# | dirPanel (4)                      |             |
# +-----------------+-----------------+             |
# | filesPanel (5)  | dirsPanel (6)   |             |
# |                 |                 |             |
# |                 |                 |             |
# |                 |                 |             |
# |                 |                 |             |
# |                 |                 |             |
# |                 |                 |             |
# |                 |                 |             |
# |                 |                 |             |
# |                 |                 |             |
# +-----------------+-----------------+-------------+
# | statusBar (1)                                   |
# +-------------------------------------------------+


proc GT::FS::create { Top } {
    global GT_FS

    set Dlg [GT::FS::create_toplevel $Top.fileselect]

    GT::FS::resources

    GT::FS::create_statusBar $Dlg.statusBar
    GT::FS::create_buttonPanel $Dlg.buttonPanel
    GT::FS::create_dirPanel $Dlg.dirPanel
    GT::FS::create_filePanel $Dlg.filePanel
    GT::FS::create_filesPanel $Dlg.filesPanel
    GT::FS::create_dirsPanel $Dlg.dirsPanel

    return $Dlg
}

proc GT::FS::resources { } {
    global tcl_platform

    # panels

    option add *FileSelect*statusBar.relief        raised
    option add *FileSelect*statusBar.borderWidth   1    
    option add *FileSelect*buttonPanel.relief      raised
    option add *FileSelect*buttonPanel.borderWidth 1    
    option add *FileSelect*dirPanel.relief         raised
    option add *FileSelect*dirPanel.borderWidth    1    
    option add *FileSelect*filePanel.relief        raised
    option add *FileSelect*filePanel.borderWidth   1    
    option add *FileSelect*filesPanel.relief       raised
    option add *FileSelect*filesPanel.borderWidth  1    
    option add *FileSelect*dirsPanel.relief        raised
    option add *FileSelect*dirsPanel.borderWidth   1    
    option add *FileSelect*default.relief          sunken
    option add *FileSelect*default.borderWidth     2

    # labels

    option add *FileSelect*dirLabel.text           "Directory:"
    option add *FileSelect*fileLabel.text          "File:"
    option add *FileSelect*fileLabel.underline     0
    option add *FileSelect*filesLabel.text         "Files:"
    option add *FileSelect*filesLabel.anchor       w
    option add *FileSelect*filesLabel.underline    1
    option add *FileSelect*dirsLabel.text          "Directories:"
    option add *FileSelect*dirsLabel.anchor        w
    option add *FileSelect*dirsLabel.underline     0

    # buttons

    option add *FileSelect*ok.text                 "Ok"
    option add *FileSelect*ok.underline            0
    option add *FileSelect*cancel.text             "Cancel"
    option add *FileSelect*cancel.underline        0

    if {$tcl_platform(platform) == "unix"} {
	option add *FileSelect*showHidden.text     "Show\nhidden\nfiles"
	option add *FileSelect*showHidden.anchor   w
	option add *FileSelect*showHidden.justify  left
	option add *FileSelect*showHidden.underline 5
	option add *FileSelect*showHidden.variable GT_FS(showHidden)
    }

    # the rest

    option add *FileSelect*dirEntry.relief         flat
    option add *FileSelect*dirEntry.cursor         {}
    option add *FileSelect*info.relief             flat
    option add *FileSelect*info.cursor             {}

    # new

    option add *FileSelect*info.state              disabled
    option add *FileSelect*info.textVariable       GT_FS(info)
    option add *FileSelect*ok.command              GT::FS::ok
    option add *FileSelect*cancel.command          GT::FS::cancel
    option add *FileSelect*dirEntry.state          disabled
    option add *FileSelect*dirEntry.textVariable   GT_FS(dir)
    option add *FileSelect*fileEntry.textVariable  GT_FS(file)
    option add *FileSelect*history.image           [GT::get_image down_arrow]
    option add *FileSelect*history.relief          raised
    option add *FileSelect*history.borderWidth     2
    option add *FileSelect*filesScroll.orient      vertical
    option add *FileSelect*dirsScroll.orient       vertical
}


proc GT::FS::create_toplevel { Top } {
    global GT_FS
    
    return [set GT_FS(dialog) [toplevel $Top -class FileSelect]]
}

proc GT::FS::create_statusBar { Frame } {
    pack [frame $Frame] -side bottom -fill x
    pack [entry $Frame.info] -fill x
}


proc GT::FS::create_buttonPanel { Frame } {
    global GT_FS tcl_platform

    set GT_FS(ok) $Frame.ok
    set GT_FS(cancel) $Frame.cancel

    pack [frame $Frame] -side right -fill y
    pack [frame $Frame.default] -side top -pady 7 -padx 7 -fill x
    pack [button $Frame.ok] -in $Frame.default -fill x
    pack [button $Frame.cancel] -side top -padx 5 -fill x

    if {$tcl_platform(platform) == "unix"} {
	set GT_FS(hiddenCheck) $Frame.showHidden
	pack [checkbutton $Frame.showHidden] -side bottom
    }
}


proc GT::FS::create_dirPanel { Frame } {
    global GT_FS

    set GT_FS(dirEntry) $Frame.dirEntry

    pack [frame $Frame] -side top -fill x
    pack [label $Frame.dirLabel] -side left
    pack [entry $Frame.dirEntry] -side right -fill x -expand true
}


proc GT::FS::create_filePanel { Frame } {
    global GT_FS

    set GT_FS(fileLabel) $Frame.fileLabel
    set GT_FS(fileEntry) $Frame.fileEntry

    pack [frame $Frame] -side top -fill x
    pack [label $Frame.fileLabel] -side left
    pack [entry $Frame.fileEntry] -side left -fill x -expand true

    if [info exists GT_FS(history)] {

	# create history button

	set HistButton $Frame.history
	
	pack [menubutton $HistButton -menu $HistButton.menu] \
	    -side right -padx 2
	menu $HistButton.menu \
	    -postcommand "GT::FS::show_history $HistButton.menu"
    }
}


proc GT::FS::create_filesPanel { Frame } {
    global GT_FS

    set GT_FS(filesLabel) $Frame.filesLabel
    set GT_FS(fileList) $Frame.filesList

    pack [frame $Frame] -side left -fill both -expand true
    pack [label $Frame.filesLabel] -side top -fill x
    pack [listbox $Frame.filesList \
	      -yscrollcommand "$Frame.filesScroll set"] \
	-side left -fill both -expand true
    pack [scrollbar $Frame.filesScroll \
	      -command "$GT_FS(fileList) yview" \
	      -takefocus 0] \
	-side right -fill y
}


proc GT::FS::create_dirsPanel { Frame } {
    global GT_FS

    set GT_FS(dirsLabel) $Frame.dirsLabel
    set GT_FS(dirList) $Frame.dirsList 
    set GT_FS(typeSelect) $Frame.typeSelect

    pack [frame $Frame] -side right -fill both -expand true
    pack [label $Frame.dirsLabel] -side top -fill x

    pack [frame $Frame.box] -expand true -fill both
    pack [listbox $Frame.dirsList \
	      -yscrollcommand "$Frame.dirsScroll set"] \
	-side left -fill both -expand true -in $Frame.box
    pack [scrollbar $Frame.dirsScroll \
	      -command "$GT_FS(dirList) yview" \
	      -takefocus 0] \
	-side right -fill y -in $Frame.box

    # need to use 'eval' because of the structure of $Types
    set GT_FS(typeMenu) [eval tk_optionMenu \
				     $Frame.typeSelect \
 				     GT_FS(pattern) \
				     $GT_FS(types)]
    $Frame.typeSelect configure -takefocus 1

    pack $Frame.typeSelect -side bottom -fill x
}

########################################################################
#
#   Bindings
#
########################################################################

proc GT::FS::setup_bindings { } {
    global GT_FS tcl_platform

    set Dlg $GT_FS(dialog)

    # file entry

    bind $GT_FS(fileEntry) <space> {
	GT::FS::complete
	break
    }
    bind $GT_FS(fileEntry) <Return> {
	GT::FS::ok
    }

    # list boxes

    foreach ListBox "$GT_FS(fileList) $GT_FS(dirList)" {
	bindtags $ListBox [concat [bindtags $ListBox] GT::FS::ListBoxes]
    }
    
    bind GT::FS::ListBoxes <Up> {
	GT::FS::take %W
    }
    bind GT::FS::ListBoxes <Down> {
	GT::FS::take %W
    }
    bind GT::FS::ListBoxes <Return> {
	GT::FS::take %W
	GT::FS::ok
    }
    bind GT::FS::ListBoxes <Button-1> {
	GT::FS::click %W %x %y
    }
    bind GT::FS::ListBoxes <Double-Button-1> {
	GT::FS::click %W %x %y
	GT::FS::ok
    }
    bind GT::FS::ListBoxes <ButtonRelease-1> {
	GT::FS::adjust_selection %W
    }
    bind GT::FS::ListBoxes <B1-Motion> {
	GT::FS::adjust_selection %W	
	GT::FS::click %W %x %y
    }
    bind GT::FS::ListBoxes <Any-Key> {
	GT::FS::listbox_search %W %A
    }

    # type selector

    bind $GT_FS(typeSelect) <Up> [bind Menubutton <space>]
    bind $GT_FS(typeSelect) <Down> [bind Menubutton <space>]

    # ok button

    bind $GT_FS(ok) <Down> {
	focus $GT_FS(cancel)
    }
    bind $GT_FS(ok) <Up> {
	focus $GT_FS(cancel)
    }

    # cancel button

    bind $GT_FS(cancel) <Down> {
	focus $GT_FS(ok)
    }
    bind $GT_FS(cancel) <Up> {
	focus $GT_FS(ok)
    }

    # shortcuts

    foreach but [list $GT_FS(ok) $GT_FS(cancel)] {
	bind $Dlg <Alt-[GT::FS::get_underlined $but]> "$but invoke"
    }
    bind $Dlg <Alt-[GT::FS::get_underlined $GT_FS(fileLabel)]> {
	focus $GT_FS(fileEntry)
    }
    bind $Dlg <Alt-[GT::FS::get_underlined $GT_FS(filesLabel)]> {
	focus $GT_FS(fileList)
    }
    bind $Dlg <Alt-[GT::FS::get_underlined $GT_FS(dirsLabel)]> {
	focus $GT_FS(dirList)
    }
    if {$tcl_platform(platform) == "unix"} {
	bind $Dlg <Alt-[GT::FS::get_underlined $GT_FS(hiddenCheck)]> {
	    focus $GT_FS(hiddenCheck)
	}
    }

    bind $Dlg <Escape> {
	GT::FS::cancel
    }
}

########################################################################
#
#   Event binding procedures
#
########################################################################

proc GT::FS::ok { } {    
    global GT_FS

    set Path [GT::FS::get_path]

    # change directory if path is a directory

    if {[file isdirectory $Path] || $Path == ""} {

	# save name of previous directory for later

	set PrevDir $GT_FS(dir)

	# change directory

	GT::FS::set_dir $Path
	set GT_FS(file) ""

	# if the user is changing to the parent directory using the
	# directory list box, then select the previous directory;
	# select the first entry otherwise

	if {[focus] == $GT_FS(dirList)} {
	    if {[string length $GT_FS(dir)] < \
		    [string length $PrevDir]} {

		set SubDir [file tail $PrevDir]

		set Index [lsearch -exact \
		            [$GT_FS(dirList) get 0 end] $SubDir]
		
		if {$Index == -1} {
		    set Index 0
		}

		GT::FS::select $GT_FS(dirList) $Index

	    } else {
		GT::FS::select $GT_FS(dirList) 0
	    }
	}
	return
    }

    # if path name includes wild characters, such as "*" and "?" read
    # the given directory (if it exists) and set the search pattern
    # according

    set Tail [file tail $Path]
    set Dir [file dirname $Path]
    if {[regexp {\*|\?|\[.*\]|\{.*\}} $Tail]&&[file isdirectory $Dir]} {
	GT::FS::set_dir $Dir
	GT::FS::set_pattern $Tail
	set GT_FS(file) $Tail
	return
    }

    # if $Path is a file then end dialog and return file name 
    
    if [GT::FS::valid_result $Path] {
	GT::FS::append_history $Path
	set GT_FS(result) $Path
	set GT_FS(done) true
    }
}


proc GT::FS::valid_result { Path } {
    global GT_FS

    if [file isfile $Path] {
	if {$GT_FS(mode) == "save"} {

	    # Check writing permissions

	    if ![file writable $Path] {
		GT::FS::ferror "No writing permissions for file \"$Path\"!"
		return 0
	  
	    # Ask, wether the user wants to overwrite an existing file

	    } elseif {[tk_dialog .confirm "Confirmation" \
			   "The file \"$Path\" already exists. \
                            Do you want to overwrite it?" \
			   question 1 "Yes" "No"] == 1} {
		return 0
	    } else {
		return 1
	    }
	} else {

	    # check reading permissions

	    if ![file readable $Path] {
		GT::FS::ferror "No reading permissions for file \"$Path\"!"
		return 0
	    } else {
		return 1
	    }
	}

    } elseif ![file exists $Path] {

	if {$GT_FS(mode) == "save"} {
	    if ![file writable [file dir $Path]] {
		GT::FS::ferror "No writing permissions for directory \
                             \"[file dir $Path]\"!"
		return 0
	    } else {
		return 1
	    }
	} else {
	    GT::FS::ferror "File \"$Path\"does not exist!"
	    return 0
	}

    } else {

	GT::FS::ferror "\"$Path\" is not a file!"
	return 0
    }
}


proc GT::FS::cancel { } {
    global GT_FS

    set GT_FS(result) ""
    set GT_FS(done) true
}


proc GT::FS::take { ListBox } {
    global GT_FS

    set GT_FS(file) [$ListBox get active]
}


proc GT::FS::click { ListBox x y } {
    global GT_FS

    $ListBox activate @$x,$y
    set GT_FS(file) [$ListBox get active]
    focus $ListBox
}


proc GT::FS::complete { } {
    global GT_FS

    # look for matching files
    
    set Files [glob -nocomplain -- /[GT::FS::get_path]*]

    # find the longest common prefix

    if {[llength [split $Files]] <= 1} {
	set Result $Files
    } else {
	set Result [lindex $Files 0]
	set matched 0
	while {!$matched} {
	    set matched 1
	    foreach File $Files {
		if ![string match $Result* $File] {
		    set Result [string range $Result 0 \
				    [expr [string length $Result] -2]]
		    set matched 0
		    break
		}
	    }
	}
    }

    # complete file name and reread directory
    if {"$Result" != ""} {
	if {[file isdirectory $Result]&&[llength [glob $Result*]]==1} {
	    GT::FS::set_dir $Result
	    set GT_FS(file) ""
	} else {
	    GT::FS::set_dir [file dirname $Result]

	    set Tail [file tail $Result]
	    if {$Tail != "" && [llength [split $Files]] == 1 && \
		    [file isdirectory $GT_FS(dir)$Tail]} {
		set GT_FS(file) $Tail/
	    } else {
		set GT_FS(file) $Tail  
	    }
	}
    }

    # reposition cursor

    $GT_FS(fileEntry) icursor end

}


proc GT::FS::listbox_search { Listbox Char } {
    set Size [$Listbox size]
    if {$Char != ""} {	
	if {[string index [$Listbox get active] 0] == $Char} {
	    set i [expr [$Listbox index active]+1]
	    if {$i >= $Size || \
		    [string index [$Listbox get $i] 0] != $Char} {
		unset i
	    }
	}
	if ![info exists i] {
	    set i -1
	    set temp ""
	    while {$i < $Size && [string index $temp 0] != $Char } {
		incr i
		set temp [$Listbox get $i]
	    }
	}
	if {$i < $Size} {
	    GT::FS::select $Listbox $i
	}
    }
}


########################################################################
#
#   History management
#
########################################################################

proc GT::FS::init_history { Length } {
    global GT_FS

    set GT_FS(historyLength) $Length
}

proc GT::FS::append_history { Path } {
    global GT_FS

    # if history list doesn't exist then create it

    if ![info exists GT_FS(history)] {
	set GT_FS(history) {}
    }
    
    # check wether $Path was already added before.

    set Index [lsearch -exact $GT_FS(history) $Path]
    if {$Index != -1} {
	set GT_FS(history) \
	    [lreplace $GT_FS(history) $Index $Index]
    }

    # insert the new Path

    set GT_FS(history) [linsert $GT_FS(history) 0 $Path]

    # if history ist too long, then cut off old entries

    if {[llength $GT_FS(history)] > \
	    $GT_FS(historyLength)} {
	set GT_FS(history) [lreplace $GT_FS(history) \
				      $GT_FS(historyLength) end]
    }
}


proc GT::FS::show_history { Menu } {
    global GT_FS

    $Menu config -tearoff false 
    $Menu delete 0 last

    foreach file $GT_FS(history) {
	$Menu add command -label $file \
	    -command "GT::FS::select_history $file"
    }
}


proc GT::FS::select_history { File } {
    global GT_FS

    GT::FS::set_dir [file dirname $File]
    set GT_FS(file) [file tail $File]
    $GT_FS(fileEntry) selection range 0 end
    $GT_FS(fileEntry) icursor end

}

########################################################################
#
#   Directory management
#
########################################################################

proc GT::FS::set_dir { Dir } {
    global GT_FS

    # add "/" if neccessary

    regsub {//$} [GT::FS::simplify_path $Dir]/ {/} Dir 

    # reread directory if neccessary

    if {[string compare $GT_FS(dir) $Dir] != 0} {
	set GT_FS(dir) $Dir
	GT::FS::read_dir
    }
}


proc GT::FS::read_dir { args } {
    global GT_FS tcl_platform
 
    # set up variables

    set Dir $GT_FS(dir)

    set fileList $GT_FS(fileList) 
    set dirList $GT_FS(dirList) 

    $fileList delete 0 end
    $dirList delete 0 end

    # error messages

    if ![file isdirectory $Dir] {
	set GT_FS() "Bad Directory"
	return 0
    }

    if {![file readable $Dir] || ![file executable $Dir]} {
	set GT_FS(info) "No reading permissions"
	$dirList insert 0 ".."
	return 0
    }

    # show clock cursor (big directories need much time to load)

    GT::FS::wait_cursor
    set GT_FS(info) "Reading Directory"
    update idletasks

    # read files / pattern matching

    set Files [glob -nocomplain -- "$Dir/[GT::FS::get_pattern]" ]
    if {$tcl_platform(platform) == "unix" && $GT_FS(showHidden)} {
	set Files [concat $Files \
		       [glob -nocomplain "$Dir/.[GT::FS::get_pattern]" ]]
    } 

    foreach f [lsort $Files] {
	if [file isfile $f] {
	    $fileList insert end [file tail $f]
	}
    }

    # read directories; done separately because of pattern matching

    set Dirs [glob -nocomplain -- "$Dir/*"]
    if {$tcl_platform(platform) == "unix" && $GT_FS(showHidden)} {
	set Dirs [concat $Dirs [glob -nocomplain -- "$Dir/.*"]]
    } else {
	lappend Dirs ".."
    }

    foreach d [lsort $Dirs] {
	set Tail [file tail $d]
	if {[file isdirectory $d]} {
	    if {$Tail != "." && ($Tail != ".." || ![GT::FS::is_root $Dir])} {
		$dirList insert end $Tail
	    }
	} 
    }

    # finished

    GT::FS::standard_cursor
    set GT_FS(info) ""

    return 1
}

########################################################################
#
#   Path management
#
########################################################################

proc GT::FS::get_path { } {
    global GT_FS

    set Result $GT_FS(file)
    switch [file pathtype $Result] {
	relative {
	    set Result "$GT_FS(dir)$Result"
	}
	volumerelative {
	    if {[string index $Result 0] == "/"} {
		set Result "[string index $GT_FS(dir) 0]:$Result"
	    } else {
		puts "----- ERROR: not implemented -----"
	    }
	}
	default {
	}
    } 

    return [GT::FS::simplify_path $Result]
}


proc GT::FS::simplify_path { Path } {
    global GT_FS tcl_platform

    # under unix "" represents the root directory (here :-)

    if {$tcl_platform(platform) == "unix" && $Path == ""} {
	return ""
    }

    # do tilde substitution

    set Path [file split $Path]
    set Prefix [lindex $Path 0]
    set Path [lreplace $Path 0 0]

    if {$Prefix == "~"} then {
	set Prefix $GT_FS(home)
    } elseif {[string index $Prefix 0] == "~"} {
	if ![catch {file dirname $Prefix}] {
	    set Prefix \
		[file dirname $Prefix]/[string range $Prefix 1 end]
	}
    }
    set Path [eval file join $Prefix $Path]

    # windows: split off volume letter

    if {$tcl_platform(platform) == "windows"} {
	set Volume "[string range $Path 0 1]"
	set Path "[string range $Path 2 end]"
    }

    # replace multiple slashes at beginning of Path

    while {[regsub {^//} $Path {/} Path]} {}

    # check for "/xyz/../" and replace it with "/"
    # also includes "/abc/def/../../" -> "/" etc.

    while {[regsub {/[^/]+/\.\./} $Path {/} Path]} {}

    # check for "/./" and replace it with "/". I use "while" 
    # instead of "-all", because this way it also works for "/././" 

    while {[regsub {/\./} $Path {/} Path]} {}
    regsub {/\.$} $Path {} Path

    # check for trailing "/.." (this is not covered by the above
    # because of the missing slash at the end)

    regsub {/[^/]+/\.\.$} $Path {} Path

    # check for "/../" at the beginning of the Path

    regsub {^/\.\./} $Path {/} Path

    if {$Path == "/.."} {
	set Path ""
    }

    # windows: re-add volume letter

    if {$tcl_platform(platform) == "windows"} {
	if {$Path == ""} {
	    set Path "/"
	}
	set Path "$Volume$Path"
    }

    return $Path
}


proc GT::FS::is_root { Path } {
    global tcl_platform

    if {$tcl_platform(platform) == "windows"} {
	return [expr [string length $Path] == 3]
    } else {
	return [expr [string compare "$Path" "/"] == 0]
    }
}

########################################################################
#
#   Pattern management
#
########################################################################

proc GT::FS::init_patterns { Length } {
    global GT_FS

    set GT_FS(patternHistLength) $Length

    if {![info exists GT_FS(userPatterns)]} {
	set GT_FS(userPatterns) {}
    }

    set cutOff [expr [llength GT_FS(userPatterns)] - $Length]
    if {$cutOff > 0} then {
	set GT_FS(userPatterns) \
	    [lrange $GT_FS(userPatterns) cutOff end]
    }
}

proc GT::FS::set_pattern { Pattern } {
    global GT_FS

    # look if $Pattern has already been used 
    #   yes -> select corresponding entry

    foreach Type \
	   [concat $GT_FS(types) $GT_FS(userPatterns)] {
	if {[GT::FS::extract_pattern $Type] == $Pattern} {
	    set GT_FS(pattern) $Type
	    return
	}
    }

    # otherwise: add Pattern to list

    set GT_FS(pattern) $Pattern    
    GT::FS::append_pattern $Pattern
}


proc GT::FS::append_pattern { Pattern } {
    global GT_FS

    lappend GT_FS(userPatterns) $Pattern


    # append $Pattern to type menu

    set Menu $GT_FS(typeMenu)
    $Menu add command -label $Pattern -command \
	"set GT_FS(pattern) $Pattern"

    # check wether pattern history gets too long and shorten it

    set Length [llength $GT_FS(userPatterns)]
    if {$Length > $GT_FS(patternHistLength)} {

	# delete oldest entry from type menu...

	$Menu delete [llength $GT_FS(types)]
	
	# ... and from the history list

	set GT_FS(userPatterns) \
	    [lrange $GT_FS(userPatterns) 1 end]
    }
}


proc GT::FS::extract_pattern { String } {
    if [regexp {\(.+\)} $String Pattern] {
	set String [string trimleft \
			 [string trimright $Pattern ")"] "("]
    }
    return $String
}


proc GT::FS::get_pattern { } {
    global GT_FS

    return [GT::FS::extract_pattern $GT_FS(pattern)]
}

########################################################################
#
#   Utility procedures
#
########################################################################

proc GT::FS::ferror { Message } {
    global GT_FS

    tk_dialog .error "Error" $Message error 0 "Ok"
    grab set $GT_FS(dialog)
}


proc GT::FS::select { Listbox Index } {
    $Listbox selection clear 0 end
    $Listbox activate $Index
    $Listbox selection set active
    $Listbox see active
}


proc GT::FS::adjust_selection { ListBox } {
    set CurSel [$ListBox curselection]
    if {$CurSel != ""} {
	$ListBox activate [$ListBox curselection]
    }
}


proc GT::FS::get_underlined { Widget } {
    set Index [$Widget cget -underline]
    if {$Index == -1} {
	set Index 0
    }
    return [string tolower [string index [$Widget cget -text] $Index]]
}


proc GT::FS::update_info { args } {
    global GT_FS

    set Path [GT::FS::get_path]
    if {$Path == ""} {
	set Path "/"
    }

    if {([catch "file dirname $Path"] != 0) && \
	                              ([string index $Path 0] == "~")} {
	set GT_FS(info) "User does not exist"
    } elseif ![file readable $GT_FS(dir)] {
	set GT_FS(info) "No reading permissions"
    } elseif {![file exists $Path] || $GT_FS(file) == ""} {
	set GT_FS(info) ""
    } else {

	if [file isdirectory $Path] {
	    set perm "d"
	} else {
	    set perm "-"
	}
	if [file readable $Path] {
	    set perm "${perm}r"
	} else {
	    set perm "${perm}-"
	}
	if [file writable $Path] {
	    set perm "${perm}w"
	} else {
	    set perm "${perm}-"
	}
	if [file executable $Path] {
	    set perm "${perm}x"
	} else {
	    set perm "${perm}-"
	}
	set size [file size $Path]
	set time [clock format [file mtime $Path] -format "%D %H:%M"]

	if {$Path == "/" || $Path == "//"} {
	    set name "/"
	} else {
	    set name [file tail $Path]
	}
	
	set GT_FS(info) [format "%s %10u    %s    %s" \
				     $perm $size $time $name]
    }
}


proc GT::FS::wait_cursor { } {
    global GT_FS

    # show clock cursor

    $GT_FS(dialog) config -cursor watch
    $GT_FS(fileEntry) config -cursor watch
    $GT_FS(dirEntry) config -cursor watch
    update idletasks
}


proc GT::FS::standard_cursor { } {
    global GT_FS

    # show arrow cursor

    $GT_FS(dialog) config -cursor {}
    $GT_FS(dirEntry) config -cursor {}
    $GT_FS(fileEntry) config -cursor xterm
    update idletasks
}


########################################################################
#
#   END OF FILE
#
########################################################################
