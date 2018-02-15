# This software is distributed under the Lesser General Public License
#!/bin/sh
# \
exec wish "$0" ${1+"$@"}

#
## tkcon.tcl
## Enhanced Tk Console, part of the VerTcl system
##
## Originally based off Brent Welch's Tcl Shell Widget
## (from "Practical Programming in Tcl and Tk")
##
## Thanks to the following (among many) for early bug reports & code ideas:
## Steven Wahl <steven@indra.com>, Jan Nijtmans <nijtmans@nici.kun.nl>
## Crimmins <markcrim@umich.edu>, Wart <wart@ugcs.caltech.edu>
##
## Copyright 1995-1999 Jeffrey Hobbs
## Initiated: Thu Aug 17 15:36:47 PDT 1995
##
## jeff.hobbs@acm.org
##
## source standard_disclaimer.tcl
## source bourbon_ware.tcl
##

## FIX NOTES - ideas on the block:
## can tkConSplitCmd be used for debugging?
## can return/error be overridden for debugging?

if {$tcl_version>=8.0} {
    package require -exact Tk $tcl_version
} elseif {[catch {package require -exact Tk [expr {$tcl_version-3.4}]}]} {
    return -code error "TkCon requires at least Tcl7.6/Tk4.2"
}

catch {package require bogus-package-name}
foreach pkg [info loaded {}] {
    set file [lindex $pkg 0]
    set name [lindex $pkg 1]
    if {![catch {set version [package require $name]}]} {
	if {[string match {} [package ifneeded $name $version]]} {
	    package ifneeded $name $version [list load $file $name]
	}
    }
}
catch {unset pkg file name version}

set TKCON(WWW) [info exists embed_args]

## tkConInit - inits tkCon
#
# Calls:	tkConInitUI
# Outputs:	errors found in tkCon resource file
##
;proc tkConInit {} {
    global auto_path tcl_platform env tcl_pkgPath \
	    TKCON argc argv tcl_interactive errorInfo

    if {![info exists argv]} {
	set argv {}
	set argc 0
    }

    set tcl_interactive 1

    if {[info exists TKCON(name)]} {
	set title $TKCON(name)
    } else {
	tkConMainInit
	set title Main
    }

    # get bg color from the main toplevel
    array set TKCON {
	color,bg	{}
	color,blink	\#FFFF00
	color,cursor	\#000000
	color,disabled	\#4D4D4D
	color,proc	\#008800
	color,var	\#FFC0D0
	color,prompt	\#8F4433
	color,stdin	\#000000
	color,stdout	\#0000FF
	color,stderr	\#FF0000

	autoload	{}
	blinktime	500
	blinkrange	1
	buffer		512
	calcmode	0
	cols		80
	debugPrompt	{(level \#$level) debug [history nextid] > }
	dead		{}
	expandorder	{Pathname Variable Procname}
	font		{}
	history		48
	hoterrors	1
	library		{}
	lightbrace	1
	lightcmd	1
	maineval	{}
	maxmenu		15
	nontcl		0
	rows		20
	scrollypos	right
	showmenu	1
	showmultiple	1
	slaveeval	{}
	slaveexit	close
	subhistory	1

	exec		slave
	app		{}
	appname		{}
	apptype		slave
	namesp		::
	cmd		{}
	cmdbuf		{}
	cmdsave		{}
	event		1
	deadapp		0
	deadsock	0
	debugging	0
	gc-delay	60000
	histid		0
	find		{}
	find,case	0
	find,reg	0
	errorInfo	{}
	slavealias	{ edit more less tkcon }
	slaveprocs	{
	    alias clear dir dump echo idebug lremove
	    tkcon_puts tclindex observe observe_var unalias which what
	}
	version		1.6
	release		{31 March 1999}
	docs		"http://www.purl.org/net/hobbs/tcl/script/tkcon/\nhttp://www.hobbs.wservice.com/tcl/script/tkcon/"
	email		{jeff.hobbs@acm.org}
	root		.
    }
    ## NOTES FOR STAYING IN PRIMARY INTERPRETER:
    ## If you set TKCON(exec) to {}, then instead of a multiple interpreter
    ## model, you get TkCon operating in the main interp by default.
    ## This can be useful when attaching to programs that like to operate
    ## in the main interpter (for example, based on special wish'es).
    ## You can set this from the command line with -exec ""
    ## A side effect is that all tkcon command line args will be used
    ## by the first console only.
    #set TKCON(exec) {}

    if {$TKCON(WWW)} {
	lappend TKCON(slavealias) history
	set TKCON(prompt1) {[history nextid] % }
    } else {
	lappend TKCON(slaveprocs) tcl_unknown unknown
	set TKCON(prompt1) {([file tail [pwd]]) [history nextid] % }
    }

    ## If there appear to be children of '.', then make sure we use
    ## a disassociated toplevel.
    if {[llength [winfo children .]]} {
	set TKCON(root) .tkcon
    }

    ## Do platform specific configuration here
    ### Use tkcon.cfg filename for resource filename on non-unix systems
    ### Determine what directory the resource file should be in
    ### Windows could possibly use env(WINDIR)
    switch $tcl_platform(platform) {
	macintosh	{
	    set envHome PREF_FOLDER
	    cd [file dirname [info script]]
	    set TKCON(rcfile) tkcon.cfg
	}
	windows		{
	    set envHome HOME
	    set TKCON(rcfile) tkcon.cfg
	}
	unix		{
	    set envHome HOME
	    set TKCON(rcfile) .tkconrc
	}
    }
    if {[info exists env($envHome)]} {
	set TKCON(rcfile) [file join $env($envHome) $TKCON(rcfile)]
    }

    ## Handle command line arguments before sourcing resource file to
    ## find if resource file is being specified (let other args pass).
    if {[set i [lsearch -exact $argv -rcfile]] != -1} {
	set TKCON(rcfile) [lindex $argv [incr i]]
    }

    if {!$TKCON(WWW) && [file exists $TKCON(rcfile)]} {
	set code [catch [list uplevel \#0 source $TKCON(rcfile)] err]
    }

    if {[info exists env(TK_CON_LIBRARY)]} {
	uplevel \#0 lappend auto_path $env(TK_CON_LIBRARY)
    } else {
	uplevel \#0 lappend auto_path $TKCON(library)
    }

    if {![info exists tcl_pkgPath]} {
	set dir [file join [file dirname [info nameofexec]] lib]
	if {[llength [info commands @scope]]} {
	    set dir [file join $dir itcl]
	}
	catch {source [file join $dir pkgIndex.tcl]}
    }
    catch {tclPkgUnknown dummy-name dummy-version}

    ## Handle rest of command line arguments after sourcing resource file
    ## and slave is created, but before initializing UI or setting packages.
    set slaveargs {}
    set slavefiles {}
    set truth {^(1|yes|true|on)$}
    for {set i 0} {$i < $argc} {incr i} {
	set arg [lindex $argv $i]
	if {[string match {-*} $arg]} {
	    set val [lindex $argv [incr i]]
	    ## Handle arg based options
	    switch -glob -- $arg {
		-- - -argv	{
		    set argv [concat -- [lrange $argv $i end]]
		    set argc [llength $argv]
		    break
		}
		-color,*	{ set TKCON([string range $arg 1 end]) $val }
		-exec	{ set TKCON(exec) $val }
		-main - -e - -eval	{ append TKCON(maineval) \n$val\n }
		-package - -load	{ lappend TKCON(autoload) $val }
		-slave	{ append TKCON(slaveeval) \n$val\n }
		-nontcl	{ set TKCON(nontcl) [regexp -nocase $truth $val] }
		-root	{ set TKCON(root) $val }
		-font	{ set TKCON(font) $val }
		-rcfile	{}
		default	{ lappend slaveargs $arg; incr i -1 }
	    }
	} elseif {[file isfile $arg]} {
	    lappend slavefiles $arg
	} else {
	    lappend slaveargs $arg
	}
    }

    ## Create slave executable
    if {[string compare {} $TKCON(exec)]} {
	uplevel \#0 tkConInitSlave $TKCON(exec) $slaveargs
    } else {
	set argc [llength $slaveargs]
	set argv $slaveargs
	uplevel \#0 $slaveargs
    }
    history keep $TKCON(history)

    ## Attach to the slave, tkConEvalAttached will then be effective
    tkConAttach $TKCON(appname) $TKCON(apptype)
    tkConInitUI $title

    ## swap puts and gets with the tkcon versions to make sure all
    ## input and output is handled by tkcon
    if {![catch {rename puts tkcon_tcl_puts}]} {
	interp alias {} puts {} tkcon_puts
    }
    #if {![catch {rename gets tkcon_tcl_gets}]} {
	#interp alias {} gets {} tkcon_gets
    #}

    ## Autoload specified packages in slave
    set pkgs [tkConEvalSlave package names]
    foreach pkg $TKCON(autoload) {
	puts -nonewline "autoloading package \"$pkg\" ... "
	if {[lsearch -exact $pkgs $pkg]>-1} {
	    if {[catch {tkConEvalSlave package require [list $pkg]} pkgerr]} {
		puts stderr "error:\n$pkgerr"
		append TKCON(errorInfo) $errorInfo\n
	    } else { puts "OK" }
	} else {
	    puts stderr "error: package does not exist"
	}
    }

    ## Evaluate maineval in slave
    if {[string compare {} $TKCON(maineval)] && \
	    [catch {uplevel \#0 $TKCON(maineval)} merr]} {
	puts stderr "error in eval:\n$merr"
	append TKCON(errorInfo) $errorInfo\n
    }

    ## Source extra command line argument files into slave executable
    foreach fn $slavefiles {
	puts -nonewline "slave sourcing \"$fn\" ... "
	if {[catch {tkConEvalSlave source [list $fn]} fnerr]} {
	    puts stderr "error:\n$fnerr"
	    append TKCON(errorInfo) $errorInfo\n
	} else { puts "OK" }
    }

    ## Evaluate slaveeval in slave
    if {[string compare {} $TKCON(slaveeval)] && \
	    [catch {interp eval $TKCON(exec) $TKCON(slaveeval)} serr]} {
	puts stderr "error in slave eval:\n$serr"
	append TKCON(errorInfo) $errorInfo\n
    }
    ## Output any error/output that may have been returned from rcfile
    if {[info exists code] && $code && [string compare {} $err]} {
	puts stderr "error in $TKCON(rcfile):\n$err"
	append TKCON(errorInfo) $errorInfo
    }
    if {[string compare {} $TKCON(exec)]} {
	tkConStateCheckpoint [concat $TKCON(name) $TKCON(exec)] slave
    }
    tkConStateCheckpoint $TKCON(name) slave
}

## tkConInitSlave - inits the slave by placing key procs and aliases in it
## It's arg[cv] are based on passed in options, while argv0 is the same as
## the master.  tcl_interactive is the same as the master as well.
# ARGS:	slave	- name of slave to init.  If it does not exist, it is created.
#	args	- args to pass to a slave as argv/argc
##
;proc tkConInitSlave {slave args} {
    global TKCON argv0 tcl_interactive tcl_library env
    if {[string match {} $slave]} {
	return -code error "Don't init the master interpreter, goofball"
    }
    if {![interp exists $slave]} { interp create $slave }
    if {[interp eval $slave info command source] == ""} {
	$slave alias source tkConSafeSource $slave
	$slave alias load tkConSafeLoad $slave
	$slave alias open tkConSafeOpen $slave
	$slave alias file file
	interp eval $slave [dump var -nocomplain tcl_library env]
	interp eval $slave { catch {source [file join $tcl_library init.tcl]} }
	interp eval $slave { catch unknown }
    }
    $slave alias exit exit
    interp eval $slave {
	catch {rename puts tkcon_tcl_puts}
	#catch {rename gets tkcon_tcl_gets}
	catch {package require bogus-package-name}
    }
    foreach cmd $TKCON(slaveprocs) { $slave eval [dump proc $cmd] }
    foreach cmd $TKCON(slavealias) { $slave alias $cmd $cmd }
    interp alias $slave ls $slave dir -full
    interp alias $slave puts $slave tkcon_puts
    #interp alias $slave gets $slave tkcon_gets
    if {[info exists argv0]} {interp eval $slave [list set argv0 $argv0]}
    interp eval $slave set tcl_interactive $tcl_interactive \; \
	    set argc [llength $args] \; \
	    set argv  [list $args] \; history keep $TKCON(history) \; {
	if {![llength [info command bgerror]]} {
	    ;proc bgerror err {
		global errorInfo
		set body [info body bgerror]
		rename bgerror {}
		if {[auto_load bgerror]} { return [bgerror $err] }
		;proc bgerror err $body
		tkcon bgerror $err $errorInfo
	    }
	}
    }

    foreach pkg [lremove [package names] Tcl] {
	foreach v [package versions $pkg] {
	    interp eval $slave [list package ifneeded $pkg $v \
		    [package ifneeded $pkg $v]]
	}
    }
}

## tkConInitInterp - inits an interpreter by placing key
## procs and aliases in it.
# ARGS: name	- interp name
#	type	- interp type (slave|interp)
##
;proc tkConInitInterp {name type} {
    global TKCON
    ## Don't allow messing up a local master interpreter
    if {[string match namespace $type] || ([string match slave $type] && \
	    [regexp {^([Mm]ain|Slave[0-9]+)$} $name])} return
    set old [tkConAttach]
    if {$TKCON(A:version) >= 8.0} { set oldname $TKCON(namesp) }
    catch {
	tkConAttach $name $type
	tkConEvalAttached {
	    catch {rename puts tkcon_tcl_puts}
	    #catch {rename gets tkcon_tcl_gets}
	}
	foreach cmd $TKCON(slaveprocs) { tkConEvalAttached [dump proc $cmd] }
	switch -exact $type {
	    slave {
		foreach cmd $TKCON(slavealias) {
		    tkConMain interp alias $name $cmd $TKCON(name) $cmd
		}
	    }
	    interp {
		set thistkcon [tk appname]
		foreach cmd $TKCON(slavealias) {
		    tkConEvalAttached "proc $cmd args { send [list $thistkcon] $cmd \$args }"
		}
	    }
	}
	## Catch in case it's a 7.4 (no 'interp alias') interp
	tkConEvalAttached {
	    catch {interp alias {} ls {} dir -full}
	    if {[catch {interp alias {} puts {} tkcon_puts}]} {
		catch {rename tkcon_puts puts}
	    }
	    #if {[catch {interp alias {} gets {} tkcon_gets}]} {
		#catch {rename tkcon_gets gets}
	    #}
	}
	return
    } {err}
    eval tkConAttach $old
    if {$TKCON(A:version) >= 8.0} { tkConAttachNamespace $oldname }
    if {[string compare {} $err]} { return -code error $err }
}

## tkConInitUI - inits UI portion (console) of tkCon
## Creates all elements of the console window and sets up the text tags
# ARGS:	root	- widget pathname of the tkCon console root
#	title	- title for the console root and main (.) windows
# Calls:	tkConInitMenus, tkConPrompt
##
;proc tkConInitUI {title} {
    global TKCON

    set root $TKCON(root)
    if {[string match . $root]} { set w {} } else { set w [toplevel $root] }
    catch {wm withdraw $root}
    set TKCON(base) $w

    ## Text Console
    set TKCON(console) [set con $w.text]
    text $con -wrap char -yscrollcommand [list $w.sy set] \
	    -foreground $TKCON(color,stdin) \
	    -insertbackground $TKCON(color,cursor)
    if {[string compare {} $TKCON(color,bg)]} {
	$con configure -background $TKCON(color,bg)
    }
    set TKCON(color,bg) [$con cget -background]
    if {[string compare {} $TKCON(font)]} {
	## Set user-requested font, if any
	$con configure -font $TKCON(font)
    } elseif {[info tclversion] >= 8.0} {
	## otherwise make sure the font is monospace
	set font [$con cget -font]
	if {![font metrics $font -fixed]} {
	    font create tkconfixed -family Courier -size -12
	    $con configure -font tkconfixed
	}
    } else {
	$con configure -font {*Courier*12*}
    }
    set TKCON(font) [$con cget -font]
    if {!$TKCON(WWW)} {
	$con configure -setgrid 1 -width $TKCON(cols) -height $TKCON(rows)
    }
    bindtags $con [list $con PreCon TkConsole PostCon $root all]
    if {[info tclversion] >= 8.0} {
	## Menus
	## catch against use in plugin
	if {[catch {menu $w.mbar} TKCON(menubar)]} {
	    set TKCON(menubar) [frame $w.mbar -relief raised -bd 1]
	}
    } else {
	set TKCON(menubar) [frame $w.mbar -relief raised -bd 1]
    }
    ## Scrollbar
    set TKCON(scrolly) [scrollbar $w.sy -takefocus 0 -bd 1 \
	    -command [list $con yview]]

    tkConInitMenus $TKCON(menubar) $title
    tkConBindings

    if {$TKCON(showmenu)} {
	if {[info tclversion] >= 8.0} {
	    $root configure -menu $TKCON(menubar)
	} else {
	    pack $TKCON(menubar) -fill x
	}
    }
    pack $w.sy -side $TKCON(scrollypos) -fill y
    pack $con -fill both -expand 1

    tkConPrompt "$title console display active\n"

    foreach col {prompt stdout stderr stdin proc} {
	$con tag configure $col -foreground $TKCON(color,$col)
    }
    $con tag configure var -background $TKCON(color,var)
    $con tag configure blink -background $TKCON(color,blink)
    $con tag configure find -background $TKCON(color,blink)

    if {![catch {wm title $root "TkCon $TKCON(version) $title"}]} {
	bind $con <Configure> {
	    scan [wm geometry [winfo toplevel %W]] "%%dx%%d" \
		    TKCON(cols) TKCON(rows)
	}
    }
    catch {wm deiconify $root}
    focus -force $TKCON(console)
    if {$TKCON(gc-delay)} {
	after $TKCON(gc-delay) tkConGarbageCollect
    }
}

## tkConGarbageCollect - do various cleanup ops periodically to our setup
##
;proc tkConGarbageCollect {} {
    global TKCON
    set w $TKCON(console)
    ## Remove error tags that no longer span anything
    ## Make sure the tag pattern matches the unique tag prefix
    foreach tag [$w tag names] {
	if {[string match _tag* $tag] && ![llength [$w tag ranges $tag]]} {
	    $w tag delete $tag
	}
    }
    if {$TKCON(gc-delay)} {
	after $TKCON(gc-delay) tkConGarbageCollect
    }
}

## tkConEval - evaluates commands input into console window
## This is the first stage of the evaluating commands in the console.
## They need to be broken up into consituent commands (by tkConCmdSep) in
## case a multiple commands were pasted in, then each is eval'ed (by
## tkConEvalCmd) in turn.  Any uncompleted command will not be eval'ed.
# ARGS:	w	- console text widget
# Calls:	tkConCmdGet, tkConCmdSep, tkConEvalCmd
## 
;proc tkConEval {w} {
    set incomplete [tkConCmdSep [tkConCmdGet $w] cmds last]
    $w mark set insert end-1c
    $w insert end \n
    if {[llength $cmds]} {
	foreach c $cmds {tkConEvalCmd $w $c}
	$w insert insert $last {}
    } elseif {!$incomplete} {
	tkConEvalCmd $w $last
    }
    $w see insert
}

## tkConEvalCmd - evaluates a single command, adding it to history
# ARGS:	w	- console text widget
# 	cmd	- the command to evaluate
# Calls:	tkConPrompt
# Outputs:	result of command to stdout (or stderr if error occured)
# Returns:	next event number
## 
;proc tkConEvalCmd {w cmd} {
    global TKCON
    $w mark set output end
    if {[string compare {} $cmd]} {
	set code 0
	if {$TKCON(subhistory)} {
	    set ev [tkConEvalSlave history nextid]
	    incr ev -1
	    if {[string match !! $cmd]} {
		set code [catch {tkConEvalSlave history event $ev} cmd]
		if {!$code} {$w insert output $cmd\n stdin}
	    } elseif {[regexp {^!(.+)$} $cmd dummy event]} {
		## Check last event because history event is broken
		set code [catch {tkConEvalSlave history event $ev} cmd]
		if {!$code && ![string match ${event}* $cmd]} {
		    set code [catch {tkConEvalSlave history event $event} cmd]
		}
		if {!$code} {$w insert output $cmd\n stdin}
	    } elseif {[regexp {^\^([^^]*)\^([^^]*)\^?$} $cmd dummy old new]} {
		set code [catch {tkConEvalSlave history event $ev} cmd]
		if {!$code} {
		    regsub -all -- $old $cmd $new cmd
		    $w insert output $cmd\n stdin
		}
	    } elseif {$TKCON(calcmode) && ![catch {expr $cmd} err]} {
		tkConEvalSlave history add $cmd
		set cmd $err
		set code -1
	    }
	}
	if {$code} {
	    $w insert output $cmd\n stderr
	} else {
	    ## We are about to evaluate the command, so move the limit
	    ## mark to ensure that further <Return>s don't cause double
	    ## evaluation of this command - for cases like the command
	    ## has a vwait or something in it
	    $w mark set limit end
	    if {$TKCON(nontcl) && [string match interp $TKCON(apptype)]} {
		set code [catch "tkConEvalSend $cmd" res]
		if {$code == 1} {
		    set TKCON(errorInfo) "Non-Tcl errorInfo not available"
		}
	    } else {
		set code [catch {tkConEvalAttached $cmd} res]
		if {$code == 1} {
		    if {[catch {tkConEvalAttached set errorInfo} err]} {
			set TKCON(errorInfo) "Error getting errorInfo:\n$err"
		    } else {
			set TKCON(errorInfo) $err
		    }
		}
	    }
	    tkConEvalSlave history add $cmd
	    if {$code} {
		if {$TKCON(hoterrors)} {
		    set tag [tkConUniqueTag $w]
		    $w insert output $res [list stderr $tag] \n stderr
		    $w tag bind $tag <Enter> \
			    [list $w tag configure $tag -under 1]
		    $w tag bind $tag <Leave> \
			    [list $w tag configure $tag -under 0]
		    $w tag bind $tag <ButtonRelease-1> \
			    "if {!\$tkPriv(mouseMoved)} \
			    {[list edit -attach [tkConAttach] -type error -- $TKCON(errorInfo)]}"
		} else {
		    $w insert output $res\n stderr
		}
	    } elseif {[string compare {} $res]} {
		$w insert output $res\n stdout
	    }
	}
    }
    tkConPrompt
    set TKCON(event) [tkConEvalSlave history nextid]
}

## tkConEvalSlave - evaluates the args in the associated slave
## args should be passed to this procedure like they would be at
## the command line (not like to 'eval').
# ARGS:	args	- the command and args to evaluate
##
;proc tkConEvalSlave args {
    global TKCON
    interp eval $TKCON(exec) $args
}

## tkConEvalOther - evaluate a command in a foreign interp or slave
## without attaching to it.  No check for existence is made.
# ARGS:	app	- interp/slave name
#	type	- (slave|interp)
##
;proc tkConEvalOther { app type args } {
    if {[string compare slave $type]==0} {
	return [tkConSlave $app $args]
    } else {
	return [uplevel 1 send [list $app] $args]
    }
}

## tkConEvalSend - sends the args to the attached interpreter
## Varies from 'send' by determining whether attachment is dead
## when an error is received
# ARGS:	args	- the args to send across
# Returns:	the result of the command
##
;proc tkConEvalSend args {
    global TKCON
    if {$TKCON(deadapp)} {
	if {[lsearch -exact [winfo interps] $TKCON(app)]<0} {
	    return
	} else {
	    set TKCON(appname) [string range $TKCON(appname) 5 end]
	    set TKCON(deadapp) 0
	    tkConPrompt "\n\"$TKCON(app)\" alive\n" \
		    [tkConCmdGet $TKCON(console)]
	}
    }
    set code [catch {uplevel 1 send [list $TKCON(app)] $args} result]
    if {$code && [lsearch -exact [winfo interps] $TKCON(app)]<0} {
	## Interpreter disappeared
	if {[string compare leave $TKCON(dead)] && \
		([string match ignore $TKCON(dead)] || \
		[tk_dialog $TKCON(base).dead "Dead Attachment" \
		"\"$TKCON(app)\" appears to have died.\
		\nReturn to primary slave interpreter?" questhead 0 OK No])} {
	    set TKCON(appname) "DEAD:$TKCON(appname)"
	    set TKCON(deadapp) 1
	} else {
	    set err "Attached Tk interpreter \"$TKCON(app)\" died."
	    tkConAttach {}
	    set TKCON(deadapp) 0
	    tkConEvalSlave set errorInfo $err
	}
	tkConPrompt \n [tkConCmdGet $TKCON(console)]
    }
    return -code $code $result
}

## tkConEvalNamespace - evaluates the args in a particular namespace
## This is an override for tkConEvalAttached for when the user wants
## to attach to a particular namespace of the attached interp
# ARGS:	attached	
#	namespace	the namespace to evaluate in
#	args		the args to evaluate
# RETURNS:	the result of the command
##
;proc tkConEvalNamespace { attached namespace args } {
    global TKCON
    if {[llength $args]} {
	if {$TKCON(A:itcl2)} {
	    uplevel \#0 $attached namespace [list $namespace $args]
	} else {
	    uplevel \#0 $attached namespace eval [list $namespace $args]
	}
    }
}


## tkConNamespaces - return all the namespaces descendent from $ns
##
#
##
;proc tkConNamespaces { {ns ::} } {
    global TKCON
    if {$TKCON(A:itcl2)} {
	return [tkConNamespacesItcl $ns]
    } else {
	return [tkConNamespacesTcl8 $ns]
    }
}

;proc tkConNamespacesTcl8 { ns {l {}} } {
    if {[string compare {} $ns]} { lappend l $ns }
    foreach i [tkConEvalAttached [list namespace children $ns]] {
	set l [tkConNamespacesTcl8 $i $l]
    }
    return $l
}

;proc tkConNamespacesItcl { ns {l {}} } {
    if {[string compare {} $ns]} { lappend l $ns }
    set names [tkConEvalAttached [list info namespace children $ns]]
    foreach i $names { set l [tkConNamespacesItcl $i $l] }
    return $l
}

## tkConCmdGet - gets the current command from the console widget
# ARGS:	w	- console text widget
# Returns:	text which compromises current command line
## 
;proc tkConCmdGet w {
    if {![llength [$w tag nextrange prompt limit end]]} {
	$w tag add stdin limit end-1c
	return [$w get limit end-1c]
    }
}

## tkConCmdSep - separates multiple commands into a list and remainder
# ARGS:	cmd	- (possible) multiple command to separate
# 	list	- varname for the list of commands that were separated.
#	last	- varname of any remainder (like an incomplete final command).
#		If there is only one command, it's placed in this var.
# Returns:	constituent command info in varnames specified by list & rmd.
## 
;proc tkConCmdSep {cmd list last} {
    upvar 1 $list cmds $last inc
    set inc {}
    set cmds {}
    foreach c [split [string trimleft $cmd] \n] {
	if {[string compare $inc {}]} {
	    append inc \n$c
	} else {
	    append inc [string trimleft $c]
	}
	if {[info complete $inc] && ![regexp {[^\\]\\$} $inc]} {
	    if {[regexp "^\[^#\]" $inc]} {lappend cmds $inc}
	    set inc {}
	}
    }
    set i [string compare $inc {}]
    if {!$i && [string compare $cmds {}] && ![string match *\n $cmd]} {
	set inc [lindex $cmds end]
	set cmds [lreplace $cmds end end]
    }
    return $i
}

## tkConCmdSplit - splits multiple commands into a list
# ARGS:	cmd	- (possible) multiple command to separate
# Returns:	constituent commands in a list
## 
;proc tkConCmdSplit {cmd} {
    set inc {}
    set cmds {}
    foreach cmd [split [string trimleft $cmd] \n] {
	if {[string compare {} $inc]} {
	    append inc \n$cmd
	} else {
	    append inc [string trimleft $cmd]
	}
	if {[info complete $inc] && ![regexp {[^\\]\\$} $inc]} {
	    #set inc [string trimright $inc]
	    if {[regexp "^\[^#\]" $inc]} {lappend cmds $inc}
	    set inc {}
	}
    }
    if {[regexp "^\[^#\]" $inc]} {lappend cmds $inc}
    return $cmds
}

## tkConUniqueTag - creates a uniquely named tag, reusing names
## Called by tkConEvalCmd
# ARGS:	w	- text widget
# Outputs:	tag name guaranteed unique in the widget
## 
;proc tkConUniqueTag {w} {
    set tags [$w tag names]
    set idx 0
    while {[lsearch -exact $tags _tag[incr idx]] != -1} {}
    return _tag$idx
}

## tkConConstrainBuffer - This limits the amount of data in the text widget
## Called by tkConPrompt and in tkcon proc buffer/console switch cases
# ARGS:	w	- console text widget
#	size	- # of lines to constrain to
# Outputs:	may delete data in console widget
## 
;proc tkConConstrainBuffer {w size} {
    if {[$w index end] > $size} {
	$w delete 1.0 [expr {int([$w index end])-$size}].0
    }
}

## tkConPrompt - displays the prompt in the console widget
# ARGS:	w	- console text widget
# Outputs:	prompt (specified in TKCON(prompt1)) to console
## 
;proc tkConPrompt {{pre {}} {post {}} {prompt {}}} {
    global TKCON
    set w $TKCON(console)
    if {[string compare {} $pre]} { $w insert end $pre stdout }
    set i [$w index end-1c]
    if {[string compare {} $TKCON(appname)]} {
	$w insert end ">$TKCON(appname)< " prompt
    }
    if {[string compare :: $TKCON(namesp)]} {
	$w insert end "<$TKCON(namesp)> " prompt
    }
    if {[string compare {} $prompt]} {
	$w insert end $prompt prompt
    } else {
	$w insert end [tkConEvalSlave subst $TKCON(prompt1)] prompt
    }
    $w mark set output $i
    $w mark set insert end
    $w mark set limit insert
    $w mark gravity limit left
    if {[string compare {} $post]} { $w insert end $post stdin }
    tkConConstrainBuffer $w $TKCON(buffer)
    $w see end
}

## tkConAbout - gives about info for tkCon
## 
;proc tkConAbout {} {
    global TKCON
    set w $TKCON(base).about
    if {[winfo exists $w]} {
	wm deiconify $w
    } else {
	global tk_patchLevel tcl_patchLevel tcl_platform
	toplevel $w
	wm title $w "About TkCon v$TKCON(version)"
	button $w.b -text Dismiss -command [list wm withdraw $w]
	text $w.text -height 9 -bd 1 -width 60 \
		-foreground $TKCON(color,stdin) \
		-background $TKCON(color,bg) \
		-font $TKCON(font)
	pack $w.b -fill x -side bottom
	pack $w.text -fill both -side left -expand 1
	$w.text tag config center -justify center
	if {[string compare unix $tcl_platform(platform)] \
		|| [info tclversion] >= 8} {
	    $w.text tag config title -justify center -font {Courier -18 bold}
	} else {
	    $w.text tag config title -justify center -font *Courier*Bold*18*
	}
	$w.text insert 1.0 "About TkCon v$TKCON(version)" title \
		"\n\nCopyright 1995-1999 Jeffrey Hobbs, $TKCON(email)\
		\nRelease Date: v$TKCON(version), $TKCON(release)\
		\nDocumentation available at:\n$TKCON(docs)\
		\nUsing: Tcl v$tcl_patchLevel / Tk v$tk_patchLevel" center
	$w.text config -state disabled
    }
}

## tkConInitMenus - inits the menubar and popup for the console
# ARGS:	w	- console text widget
## 
;proc tkConInitMenus {w title} {
    global TKCON tcl_platform

    if {[catch {menu $w.pop -tearoff 0}]} {
	label $w.label -text "Menus not available in plugin mode"
	pack $w.label
	return
    }
    menu $w.context -tearoff 0 -disabledforeground $TKCON(color,disabled)
    set TKCON(context) $w.context
    set TKCON(popup) $w.pop

    if {[info tclversion] >= 8.0} {
 	proc tkConMenuButton {w m l} {
 	    $w add cascade -label $m -underline 0 -menu $w.$l
	    return $w.$l
 	}
 	set x {}
    } else {
 	proc tkConMenuButton {w m l} {
 	    pack [menubutton $w.$l -text $m -underline 0 \
		    -padx 6p -pady 6p -menu $w.$l.m] -side left
	    return $w.$l.m
 	}
 	set x .m
    }
    foreach m [list File Console Edit Interp Prefs History Help] {
 	set l [string tolower $m]
 	tkConMenuButton $w $m $l
 	$w.pop add cascade -label $m -underline 0 -menu $w.pop.$l
    }
    if {[info tclversion] < 8.0} {
	pack $w.help -side right
    }

    ## File Menu
    ##
    foreach m [list [menu $w.file$x -disabledforeground $TKCON(color,disabled)] \
	    [menu $w.pop.file -disabledforeground $TKCON(color,disabled)]] {
	$m add command -label "Load File" -underline 0 -command tkConLoad
	$m add cascade -label "Save ..."  -underline 0 -menu $m.save
	$m add separator
	$m add command -label "Quit" -underline 0 -accel Ctrl-q -command exit

	## Save Menu
	##
	set s $m.save
	menu $s -disabledforeground $TKCON(color,disabled) -tearoff 0
	$s add command -label "All"	-und 0 -command {tkConSave {} all}
	$s add command -label "History"	-und 0 -command {tkConSave {} history}
	$s add command -label "Stdin"	-und 3 -command {tkConSave {} stdin}
	$s add command -label "Stdout"	-und 3 -command {tkConSave {} stdout}
	$s add command -label "Stderr"	-und 3 -command {tkConSave {} stderr}
    }

    ## Console Menu
    ##
    foreach m [list [menu $w.console$x -disabledfore $TKCON(color,disabled)] \
	    [menu $w.pop.console -disabledfore $TKCON(color,disabled)]] {
	$m add command -label "$title Console"	-state disabled
	$m add command -label "New Console"	-und 0 -accel Ctrl-N \
		-command tkConNew
	$m add command -label "Close Console"	-und 0 -accel Ctrl-w \
		-command tkConDestroy
	$m add command -label "Clear Console"	-und 1 -accel Ctrl-l \
		-command { clear; tkConPrompt }
	if {[string match unix $tcl_platform(platform)]} {
	    $m add separator
	    $m add command -label "Make Xauth Secure" -und 5 \
		    -command tkConXauthSecure
	}
	$m add separator
	$m add cascade -label "Attach To ..."	-und 0 -menu $m.attach

	## Attach Console Menu
	##
	set sub [menu $m.attach -disabledforeground $TKCON(color,disabled)]
	$sub add cascade -label "Interpreter"   -und 0 -menu $sub.apps
	$sub add cascade -label "Namespace" -und 1 -menu $sub.name

	## Attach Console Menu
	##
	menu $sub.apps -disabledforeground $TKCON(color,disabled) \
		-postcommand [list tkConAttachMenu $sub.apps]

	## Attach Namespace Menu
	##
	menu $sub.name -disabledforeground $TKCON(color,disabled) -tearoff 0 \
		-postcommand [list tkConNamespaceMenu $sub.name]
    }

    ## Edit Menu
    ##
    set text $TKCON(console)
    foreach m [list [menu $w.edit$x] [menu $w.pop.edit]] {
	$m add command -label "Cut"   -underline 2 -accel Ctrl-x \
		-command [list tkConCut $text]
	$m add command -label "Copy"  -underline 0 -accel Ctrl-c \
		-command [list tkConCopy $text]
	$m add command -label "Paste" -underline 0 -accel Ctrl-v \
		 -command [list tkConPaste $text]
	$m add separator
	$m add command -label "Find"  -underline 0 -accel Ctrl-F \
		-command [list tkConFindBox $text]
    }

    ## Interp Menu
    ##
    foreach m [list $w.interp$x $w.pop.interp] {
	menu $m -disabledforeground $TKCON(color,disabled) \
		-postcommand [list tkConInterpMenu $m]
    }

    ## Prefs Menu
    ##
    foreach m [list [menu $w.prefs$x] [menu $w.pop.prefs]] {
	$m add check -label "Brace Highlighting" \
		-underline 0 -variable TKCON(lightbrace)
	$m add check -label "Command Highlighting" \
		-underline 0 -variable TKCON(lightcmd)
	$m add check -label "History Substitution" \
		-underline 0 -variable TKCON(subhistory)
	$m add check -label "Hot Errors" \
		-underline 0 -variable TKCON(hoterrors)
	$m add check -label "Non-Tcl Attachments" \
		-underline 0 -variable TKCON(nontcl)
	$m add check -label "Calculator Mode" \
		-underline 1 -variable TKCON(calcmode)
	$m add check -label "Show Multiple Matches" \
		-underline 0 -variable TKCON(showmultiple)
	$m add check -label "Show Menubar" \
		-underline 5 -variable TKCON(showmenu) \
		-command "if {\$TKCON(showmenu)} { \
		pack $w -fill x -before $TKCON(console) \
		-before $TKCON(scrolly) \
	    } else { pack forget $w }"
	$m add cascade -label "Scrollbar" -underline 2 -menu $m.scroll

	## Scrollbar Menu
	##
	set m [menu $m.scroll -tearoff 0]
	$m add radio -label "Left" -variable TKCON(scrollypos) -value left \
		-command { pack config $TKCON(scrolly) -side left }
	$m add radio -label "Right" -variable TKCON(scrollypos) -value right \
		-command { pack config $TKCON(scrolly) -side right }
    }

    ## History Menu
    ##
    foreach m [list $w.history$x $w.pop.history] {
	menu $m -disabledforeground $TKCON(color,disabled) \
		-postcommand [list tkConHistoryMenu $m]
    }

    ## Help Menu
    ##
    foreach m [list [menu $w.help$x] [menu $w.pop.help]] {
	$m add command -label "About " -und 0 -accel Ctrl-A -command tkConAbout
    }
}

## tkConHistoryMenu - dynamically build the menu for attached interpreters
##
# ARGS:	m	- menu widget
##
;proc tkConHistoryMenu m {
    global TKCON

    if {![winfo exists $m]} return
    set id [tkConEvalSlave history nextid]
    if {$TKCON(histid)==$id} return
    set TKCON(histid) $id
    $m delete 0 end
    while {$id && ($id>$TKCON(histid)-10) && \
	    ![catch {tkConEvalSlave history event [incr id -1]} tmp]} {
	set lbl [lindex [split $tmp "\n"] 0]
	if {[string len $lbl]>32} { set lbl [string range $tmp 0 28]... }
	$m add command -label "$id: $lbl" -command "
	$TKCON(console) delete limit end
	$TKCON(console) insert limit [list $tmp]
	$TKCON(console) see end
	tkConEval $TKCON(console)"
    }
}

## tkConInterpMenu - dynamically build the menu for attached interpreters
##
# ARGS:	w	- menu widget
##
;proc tkConInterpMenu w {
    global TKCON

    if {![winfo exists $w]} return
    $w delete 0 end
    foreach {app type} [tkConAttach] break
    $w add command -label "[string toupper $type]: $app" -state disabled
    if {($TKCON(nontcl) && [string match interp $type]) || $TKCON(deadapp)} {
	$w add separator
	$w add command -state disabled -label "Communication disabled to"
	$w add command -state disabled -label "dead or non-Tcl interps"
	return
    }

    ## Show Last Error
    ##
    $w add separator
    $w add command -label "Show Last Error" \
	    -command [list tkcon error $app $type]

    ## Packages Cascaded Menu
    ##
    if {$TKCON(A:version) > 7.4} {
	$w add separator
	$w add cascade -label Packages -underline 0 -menu $w.pkg
	set m $w.pkg
	if {![winfo exists $m]} {
	    menu $m -tearoff no -disabledforeground $TKCON(color,disabled) \
		    -postcommand [list tkConPkgMenu $m $app $type]
	}
    }

    ## State Checkpoint/Revert
    ##
    $w add separator
    $w add command -label "Checkpoint State" \
	    -command [list tkConStateCheckpoint $app $type]
    $w add command -label "Revert State" \
	    -command [list tkConStateRevert $app $type]
    $w add command -label "View State Change" \
	    -command [list tkConStateCompare $app $type]

    ## Init Interp
    ##
    $w add separator
    $w add command -label "Send TkCon Commands" \
	    -command [list tkConInitInterp $app $type]
}

## tkConPkgMenu - fill in  in the applications sub-menu
## with a list of all the applications that currently exist.
##
;proc tkConPkgMenu {m app type} {
    global TKCON

    set lopt [expr {([info tclversion] >= 8.0)?"-dictionary":"-ascii"}]

    # just in case stuff has been added to the auto_path
    # we have to make sure that the errorInfo doesn't get screwed up
    tkConEvalAttached {
	set __tkcon_error $errorInfo
	catch {package require bogus-package-name}
	set errorInfo ${__tkcon_error}
	unset __tkcon_error
    }
    $m delete 0 end
    foreach pkg [tkConEvalAttached [list info loaded {}]] {
	set loaded([lindex $pkg 1]) [package provide $pkg]
    }
    foreach pkg [lremove [tkConEvalAttached {package names}] Tcl] {
	set version [tkConEvalAttached [list package provide $pkg]]
	if {[string compare {} $version]} {
	    set loaded($pkg) $version
	} elseif {![info exists loaded($pkg)]} {
	    set loadable($pkg) [list package require $pkg]
	}
    }
    foreach pkg [tkConEvalAttached {info loaded}] {
	set pkg [lindex $pkg 1]
	if {![info exists loaded($pkg)] && ![info exists loadable($pkg)]} {
	    set loadable($pkg) [list load {} $pkg]
	}
    }
    foreach pkg [lsort $lopt [array names loadable]] {
	foreach v [tkConEvalAttached [list package version $pkg]] {
	    $m add command -label "Load $pkg ($v)" -command \
		    "tkConEvalOther [list $app] $type $loadable($pkg) $v"
	}
    }
    if {[info exists loaded] && [info exists loadable]} {
	$m add separator
    }
    foreach pkg [lsort $lopt [array names loaded]] {
	$m add command -label "${pkg}$loaded($pkg) Loaded" -state disabled
    }
}

## tkConAttachMenu - fill in  in the applications sub-menu
## with a list of all the applications that currently exist.
##
;proc tkConAttachMenu m {
    global TKCON

    array set interps [set tmp [tkConInterps]]
    foreach {i j} $tmp { set tknames($j) {} }

    $m delete 0 end
    set cmd {tkConPrompt \n [tkConCmdGet $TKCON(console)]}
    $m add radio -label {None (use local slave) } -variable TKCON(app) \
	    -value [concat $TKCON(name) $TKCON(exec)] -accel Ctrl-1 \
	    -command "tkConAttach {}; $cmd"
    $m add separator
    $m add command -label "Foreign Tk Interpreters" -state disabled
    foreach i [lsort [lremove [winfo interps] [array names tknames]]] {
	$m add radio -label $i -variable TKCON(app) -value $i \
		-command "tkConAttach [list $i] interp; $cmd"
    }
    $m add separator

    $m add command -label "TkCon Interpreters" -state disabled
    foreach i [lsort [array names interps]] {
	if {[string match {} $interps($i)]} { set interps($i) "no Tk" }
	if {[regexp {^Slave[0-9]+} $i]} {
	    set opts [list -label "$i ($interps($i))" -variable TKCON(app) \
		    -value $i -command "tkConAttach [list $i] slave; $cmd"]
	    if {[string match $TKCON(name) $i]} {
		append opts " -accel Ctrl-2"
	    }
	    eval $m add radio $opts
	} else {
	    set name [concat Main $i]
	    if {[string match Main $name]} {
		$m add radio -label "$name ($interps($i))" \
			-variable TKCON(app) -value Main -accel Ctrl-3 \
			-command "tkConAttach [list $name] slave; $cmd"
	    } else {
		$m add radio -label "$name ($interps($i))" \
			-variable TKCON(app) -value $i \
			-command "tkConAttach [list $name] slave; $cmd"
	    }
	}
    }
}

## Namepaces Cascaded Menu
##
;proc tkConNamespaceMenu m {
    global TKCON

    $m delete 0 end
    if {!$TKCON(A:namespace) || ($TKCON(deadapp) || \
	    ($TKCON(nontcl) && [string match interp $TKCON(apptype)]))} {
	$m add command -label "No Namespaces" -state disabled
	return
    }

    ## Same command as for tkConAttachMenu items
    set cmd {tkConPrompt \n [tkConCmdGet $TKCON(console)]}

    set names [lsort [tkConNamespaces ::]]
    if {[llength $names] > $TKCON(maxmenu)} {
	$m add command -label "Attached to $TKCON(namesp)" -state disabled
	$m add command -label "List Namespaces" \
		-command [list tkConNamespacesList $names]
    } else {
	foreach i $names {
	    if {[string match :: $i]} {
		$m add radio -label "Main" -variable TKCON(namesp) -value $i \
			-command "tkConAttachNamespace [list $i]; $cmd"
	    } else {
		$m add radio -label $i -variable TKCON(namesp) -value $i \
			-command "tkConAttachNamespace [list $i]; $cmd"
	    }
	}
    }
}

## Namepaces List 
##
;proc tkConNamespacesList {names} {
    global TKCON
    
    set f $TKCON(base).tkConNamespaces
    catch {destroy $f}
    toplevel $f
    listbox $f.names -width 30 -height 15 -selectmode single \
	    -yscrollcommand [list $f.scrollv set] \
	    -xscrollcommand [list $f.scrollh set]
    scrollbar $f.scrollv -command [list $f.names yview]
    scrollbar $f.scrollh -command [list $f.names xview] -orient horizontal
    frame $f.buttons
    button $f.cancel -text "Cancel" -command [list destroy $f]

    grid $f.names $f.scrollv -sticky nesw
    grid $f.scrollh -sticky ew
    grid $f.buttons -sticky nesw
    grid $f.cancel -in $f.buttons -pady 6

    grid columnconfigure $f 0 -weight 1
    grid rowconfigure $f  0 -weight 1
    #fill the listbox
    foreach i $names {
	if {[string match :: $i]} {
	    $f.names insert 0 Main
	} else {
	    $f.names insert end $i
	}
    }
    #Bindings
    bind $f.names <Double-1> {
	## Catch in case the namespace disappeared on us
	catch { tkConAttachNamespace [%W get [%W nearest %y]] }
	tkConPrompt "\n" [tkConCmdGet $TKCON(console)]
	destroy [winfo toplevel %W]
    }
}

# tkConXauthSecure --
#
#   This removes all the names in the xhost list, and secures
#   the display for Tk send commands.  Of course, this prevents
#   what might have been otherwise allowable X connections
#
# Arguments:
#   none
# Results:
#   Returns nothing
#
proc tkConXauthSecure {} {
    global tcl_platform
    if {[string compare unix $tcl_platform(platform)]} {
	# This makes no sense outside of Unix
	return
    }
    set hosts [exec xhost]
    # the first line is info only
    foreach host [lrange [split $hosts \n] 1 end] {
	exec xhost -$host
    }
    exec xhost -
    tk_messageBox -title "Xhost secured" -message "Xhost secured" -icon info
}

## tkConFindBox - creates minimal dialog interface to tkConFind
# ARGS:	w	- text widget
#	str	- optional seed string for TKCON(find)
##
;proc tkConFindBox {w {str {}}} {
    global TKCON

    set base $TKCON(base).find
    if {![winfo exists $base]} {
	toplevel $base
	wm withdraw $base
	wm title $base "TkCon Find"

	pack [frame $base.f] -fill x -expand 1
	label $base.f.l -text "Find:"
	entry $base.f.e -textvar TKCON(find)
	pack [frame $base.opt] -fill x
	checkbutton $base.opt.c -text "Case Sensitive" \
		-variable TKCON(find,case)
	checkbutton $base.opt.r -text "Use Regexp" -variable TKCON(find,reg)
	pack $base.f.l -side left
	pack $base.f.e $base.opt.c $base.opt.r -side left -fill both -expand 1
	pack [frame $base.sep -bd 2 -relief sunken -height 4] -fill x
	pack [frame $base.btn] -fill both
	button $base.btn.fnd -text "Find" -width 6
	button $base.btn.clr -text "Clear" -width 6
	button $base.btn.dis -text "Dismiss" -width 6
	eval pack [winfo children $base.btn] -padx 4 -pady 2 \
		-side left -fill both

	focus $base.f.e

	bind $base.f.e <Return> [list $base.btn.fnd invoke]
	bind $base.f.e <Escape> [list $base.btn.dis invoke]
    }
    $base.btn.fnd config -command "tkConFind [list $w] \$TKCON(find) \
	    -case \$TKCON(find,case) -reg \$TKCON(find,reg)"
    $base.btn.clr config -command "
    [list $w] tag remove find 1.0 end
    set TKCON(find) {}
    "
    $base.btn.dis config -command "
    [list $w] tag remove find 1.0 end
    wm withdraw [list $base]
    "
    if {[string compare {} $str]} {
	set TKCON(find) $str
	$base.btn.fnd invoke
    }

    if {[string compare normal [wm state $base]]} {
	wm deiconify $base
    } else { raise $base }
    $base.f.e select range 0 end
}

## tkConFind - searches in text widget $w for $str and highlights it
## If $str is empty, it just deletes any highlighting
# ARGS: w	- text widget
#	str	- string to search for
#	-case	TCL_BOOLEAN	whether to be case sensitive	DEFAULT: 0
#	-regexp	TCL_BOOLEAN	whether to use $str as pattern	DEFAULT: 0
##
;proc tkConFind {w str args} {
    $w tag remove find 1.0 end
    set truth {^(1|yes|true|on)$}
    set opts  {}
    foreach {key val} $args {
	switch -glob -- $key {
	    -c* { if {[regexp -nocase $truth $val]} { set case 1 } }
	    -r* { if {[regexp -nocase $truth $val]} { lappend opts -regexp } }
	    default { return -code error "Unknown option $key" }
	}
    }
    if {![info exists case]} { lappend opts -nocase }
    if {[string match {} $str]} return
    $w mark set findmark 1.0
    while {[string compare {} [set ix [eval $w search $opts -count numc -- \
	    [list $str] findmark end]]]} {
	$w tag add find $ix ${ix}+${numc}c
	$w mark set findmark ${ix}+1c
    }
    global TKCON
    $w tag configure find -background $TKCON(color,blink)
    catch {$w see find.first}
    return [expr {[llength [$w tag ranges find]]/2}]
}

## tkConAttach - called to attach tkCon to an interpreter
# ARGS:	name	- application name to which tkCon sends commands
#		  This is either a slave interperter name or tk appname.
#	type	- (slave|interp) type of interpreter we're attaching to
#		  slave means it's a TkCon interpreter
#		  interp means we'll need to 'send' to it.
# Results:	tkConEvalAttached is recreated to evaluate in the
#		appropriate interpreter
##
;proc tkConAttach {{name <NONE>} {type slave}} {
    global TKCON
    if {[string match <NONE> $name]} {
	if {[string match {} $TKCON(appname)]} {
	    return [list [concat $TKCON(name) $TKCON(exec)] $TKCON(apptype)]
	} else {
	    return [list $TKCON(appname) $TKCON(apptype)]
	}
    }
    set path [concat $TKCON(name) $TKCON(exec)]

    if {[string match namespace $type]} {
	return [uplevel tkConAttachNamespace $name]
    } elseif {[string compare {} $name]} {
	array set interps [tkConInterps]
	if {[string match {[Mm]ain} [lindex $name 0]]} {
	    set name [lrange $name 1 end]
	}
	if {[string match $path $name]} {
	    set name {}
	    set app $path
	    set type slave
	} elseif {[info exists interps($name)]} {
	    if {[string match {} $name]} { set name Main; set app Main }
	    set type slave
	} elseif {[interp exists $name]} {
	    set name [concat $TKCON(name) $name]
	    set type slave
	} elseif {[interp exists [concat $TKCON(exec) $name]]} {
	    set name [concat $path $name]
	    set type slave
	} elseif {[lsearch -exact [winfo interps] $name] > -1} {
	    if {[tkConEvalSlave info exists tk_library] \
		    && [string match $name [tkConEvalSlave tk appname]]} {
		set name {}
		set app $path
		set type slave
	    } elseif {[set i [lsearch -exact \
		    [tkConMain set TKCON(interps)] $name]] != -1} {
		set name [lindex [tkConMain set TKCON(slaves)] $i]
		if {[string match {[Mm]ain} $name]} { set app Main }
		set type slave
	    } else {
		set type interp
	    }
	} else {
	    return -code error "No known interpreter \"$name\""
	}
    } else {
	set app $path
    }
    if {![info exists app]} { set app $name }
    array set TKCON [list app $app appname $name apptype $type deadapp 0]

    ## tkConEvalAttached - evaluates the args in the attached interp
    ## args should be passed to this procedure as if they were being
    ## passed to the 'eval' procedure.  This procedure is dynamic to
    ## ensure evaluation occurs in the right interp.
    # ARGS:	args	- the command and args to evaluate
    ##
    switch $type {
	slave {
	    if {[string match {} $name]} {
		interp alias {} tkConEvalAttached {} tkConEvalSlave uplevel \#0
	    } elseif {[string match Main $TKCON(app)]} {
		interp alias {} tkConEvalAttached {} tkConMain
	    } elseif {[string match $TKCON(name) $TKCON(app)]} {
		interp alias {} tkConEvalAttached {} uplevel \#0
	    } else {
		interp alias {} tkConEvalAttached {} tkConSlave $TKCON(app)
	    }
	}
	interp {
	    if {$TKCON(nontcl)} {
		interp alias {} tkConEvalAttached {} tkConEvalSlave
		array set TKCON {A:version 0 A:namespace 0 A:itcl2 0 namesp ::}
	    } else {
		interp alias {} tkConEvalAttached {} tkConEvalSend
	    }
	}
	default {
	    return -code error "[lindex [info level 0] 0] did not specify\
		    a valid type: must be slave or interp"
	}
    }
    if {[string match slave $type] || \
	    (!$TKCON(nontcl) && [string match interp $type])} {
	set TKCON(A:version)   [tkConEvalAttached {info tclversion}]
	set TKCON(A:namespace) [llength \
		[tkConEvalAttached {info commands namespace}]]
	# Itcl3.0 for Tcl8.0 should have Tcl8 namespace semantics
	# and not effect the patchlevel
	set TKCON(A:itcl2) [string match *i* \
		[tkConEvalAttached {info patchlevel}]]
	set TKCON(namesp) ::
    }
    return
}

## tkConAttachNamespace - called to attach tkCon to a namespace
# ARGS:	name	- namespace name in which tkCon should eval commands
# Results:	tkConEvalAttached will be modified
##
;proc tkConAttachNamespace { name } {
    global TKCON
    if {($TKCON(nontcl) && [string match interp $TKCON(apptype)]) \
	    || $TKCON(deadapp)} {
	return -code error "can't attach to namespace in bad environment"
    }
    if {[string match Main $name]} {set name ::}
    if {[string compare {} $name] && \
	    [lsearch [tkConNamespaces ::] $name] == -1} {
	return -code error "No known namespace \"$name\""
    }
    if {[regexp {^(|::)$} $name]} {
	## If name=={} || ::, we want the primary namespace
	set alias [interp alias {} tkConEvalAttached]
	if {[string match tkConEvalNamespace* $alias]} {
	    eval [list interp alias {} tkConEvalAttached {}] [lindex $alias 1]
	}
	set name ::
    } else {
	interp alias {} tkConEvalAttached {} tkConEvalNamespace \
		[interp alias {} tkConEvalAttached] [list $name]
    }
    set TKCON(namesp) $name
}

## tkConLoad - sources a file into the console
## The file is actually sourced in the currently attached's interp
# ARGS:	fn	- (optional) filename to source in
# Returns:	selected filename ({} if nothing was selected)
## 
;proc tkConLoad { {fn ""} } {
    global TKCON
    set types {
	{{Tcl Files}	{.tcl .tk}}
	{{Text Files}	{.txt}}
	{{All Files}	*}
    }
    if {
	[string match {} $fn] &&
	([catch {tk_getOpenFile -filetypes $types \
	    -title "Source File"} fn] || [string match {} $fn])
    } { return }
    tkConEvalAttached [list source $fn]
}

## tkConSave - saves the console or other widget buffer to a file
## This does not eval in a slave because it's not necessary
# ARGS:	w	- console text widget
# 	fn	- (optional) filename to save to
## 
;proc tkConSave { {fn ""} {type ""} {widget ""} {mode w} } {
    global TKCON
    if {![regexp -nocase {^(all|history|stdin|stdout|stderr|widget)$} $type]} {
	array set s { 0 All 1 History 2 Stdin 3 Stdout 4 Stderr 5 Cancel }
	## Allow user to specify what kind of stuff to save
	set type [tk_dialog $TKCON(base).savetype "Save Type" \
		"What part of the text do you want to save?" \
		questhead 0 $s(0) $s(1) $s(2) $s(3) $s(4) $s(5)]
	if {$type == 5 || $type == -1} return
	set type $s($type)
    }
    if {[string match {} $fn]} {
	set types {
	    {{Tcl Files}	{.tcl .tk}}
	    {{Text Files}	{.txt}}
	    {{All Files}	*}
	}
	if {[catch {tk_getSaveFile -defaultextension .tcl -filetypes $types \
		-title "Save $type"} fn] || [string match {} $fn]} return
    }
    set type [string tolower $type]
    switch $type {
	stdin -	stdout - stderr {
	    set data {}
	    foreach {first last} [$TKCON(console) tag ranges $type] {
		lappend data [$TKCON(console) get $first $last]
	    }
	    set data [join $data \n]
	}
	history		{ set data [tkcon history] }
	all - default	{ set data [$TKCON(console) get 1.0 end-1c] }
	widget		{
	    set data [$widget get 1.0 end-1c]
	}
    }
    if {[catch {open $fn $mode} fid]} {
	return -code error "Save Error: Unable to open '$fn' for writing\n$fid"
    }
    puts $fid $data
    close $fid
}

## tkConMainInit
## This is only called for the main interpreter to include certain procs
## that we don't want to include (or rather, just alias) in slave interps.
##
;proc tkConMainInit {} {
    global TKCON

    if {![info exists TKCON(slaves)]} {
	array set TKCON [list slave 0 slaves Main name {} \
		interps [list [tk appname]]]
    }
    interp alias {} tkConMain {} tkConInterpEval Main
    interp alias {} tkConSlave {} tkConInterpEval

    ;proc tkConGetSlaveNum {} {
	global TKCON
	set i -1
	while {[interp exists Slave[incr i]]} {
	    # oh my god, an empty loop!
	}
	return $i
    }

    ## tkConNew - create new console window
    ## Creates a slave interpreter and sources in this script.
    ## All other interpreters also get a command to eval function in the
    ## new interpreter.
    ## 
    ;proc tkConNew {} {
	global argv0 argc argv TKCON
	set tmp [interp create Slave[tkConGetSlaveNum]]
	lappend TKCON(slaves) $tmp
	load {} Tk $tmp
	lappend TKCON(interps) [$tmp eval [list tk appname \
		"[tk appname] $tmp"]]
	if {[info exist argv0]} {$tmp eval [list set argv0 $argv0]}
	$tmp eval set argc $argc \; set argv [list $argv] \; \
		set TKCON(name) $tmp \; set TKCON(SCRIPT) [list $TKCON(SCRIPT)]
	$tmp alias exit			tkConExit $tmp
	$tmp alias tkConDestroy		tkConDestroy $tmp
	$tmp alias tkConNew		tkConNew
	$tmp alias tkConMain		tkConInterpEval Main
	$tmp alias tkConSlave		tkConInterpEval
	$tmp alias tkConInterps		tkConInterps
	$tmp alias tkConStateCheckpoint	tkConStateCheckpoint
	$tmp alias tkConStateCleanup	tkConStateCleanup
	$tmp alias tkConStateCompare	tkConStateCompare
	$tmp alias tkConStateRevert	tkConStateRevert
	$tmp eval {if [catch {source -rsrc tkcon}] {source $TKCON(SCRIPT)}}
	return $tmp
    }

    ## tkConExit - full exit OR destroy slave console
    ## This proc should only be called in the main interpreter from a slave.
    ## The master determines whether we do a full exit or just kill the slave.
    ## 
    ;proc tkConExit {slave args} {
	global TKCON
	## Slave interpreter exit request
	if {[string match exit $TKCON(slaveexit)]} {
	    ## Only exit if it specifically is stated to do so
	    uplevel 1 exit $args
	}
	## Otherwise we will delete the slave interp and associated data
	set name [tkConInterpEval $slave]
	set TKCON(interps) [lremove $TKCON(interps) [list $name]]
	set TKCON(slaves)  [lremove $TKCON(slaves) [list $slave]]
	interp delete $slave
	tkConStateCleanup $slave
	return
    }

    ## tkConDestroy - destroy console window
    ## This proc should only be called by the main interpreter.  If it is
    ## called from there, it will ask before exiting TkCon.  All others
    ## (slaves) will just have their slave interpreter deleted, closing them.
    ## 
    ;proc tkConDestroy {{slave {}}} {
	global TKCON
	if {[string match {} $slave]} {
	    ## Main interpreter close request
	    if {[tk_dialog $TKCON(base).destroyme {Quit TkCon?} \
		    {Closing the Main console will quit TkCon} \
		    warning 0 "Don't Quit" "Quit TkCon"]} exit
	} else {
	    ## Slave interpreter close request
	    set name [tkConInterpEval $slave]
	    set TKCON(interps) [lremove $TKCON(interps) [list $name]]
	    set TKCON(slaves)  [lremove $TKCON(slaves) [list $slave]]
	    interp delete $slave
	}
	tkConStateCleanup $slave
	return
    }

    ## tkConInterpEval - passes evaluation to another named interpreter
    ## If the interpreter is named, but no args are given, it returns the
    ## [tk appname] of that interps master (not the associated eval slave).
    ##
    ;proc tkConInterpEval {{slave {}} args} {
	if {[string match {} $slave]} {
	    global TKCON
	    return $TKCON(slaves)
	} elseif {[string match {[Mm]ain} $slave]} {
	    set slave {}
	}
	if {[llength $args]} {
	    return [interp eval $slave uplevel \#0 $args]
	} else {
	    return [interp eval $slave tk appname]
	}
    }

    ;proc tkConInterps {{ls {}} {interp {}}} {
	if {[string match {} $interp]} { lappend ls {} [tk appname] }
	foreach i [interp slaves $interp] {
	    if {[string compare {} $interp]} { set i "$interp $i" }
	    if {[string compare {} [interp eval $i package provide Tk]]} {
		lappend ls $i [interp eval $i tk appname]
	    } else {
		lappend ls $i {}
	    }
	    set ls [tkConInterps $ls $i]
	}
	return $ls
    }

    ##
    ## The following state checkpoint/revert procedures are very sketchy
    ## and prone to problems.  They do not track modifications to currently
    ## existing procedures/variables, and they can really screw things up
    ## if you load in libraries (especially Tk) between checkpoint and
    ## revert.  Only with this knowledge in mind should you use these.
    ##

    ## tkConStateCheckpoint - checkpoints the current state of the system
    ## This allows you to return to this state with tkConStateRevert
    # ARGS:
    ##
    ;proc tkConStateCheckpoint {app type} {
	global TKCON
	if {[info exists TKCON($type,$app,cmd)] &&
	[tk_dialog $TKCON(base).warning "Overwrite Previous State?" \
		"Are you sure you want to lose previously checkpointed\
		state of $type \"$app\"?" questhead 1 "Do It" "Cancel"]} return
	set TKCON($type,$app,cmd) [tkConEvalOther $app $type info commands *]
	set TKCON($type,$app,var) [tkConEvalOther $app $type info vars *]
	return
    }

    ## tkConStateCompare - compare two states and output difference
    # ARGS:
    ##
    ;proc tkConStateCompare {app type {verbose 0}} {
	global TKCON
	if {![info exists TKCON($type,$app,cmd)]} {
	    return -code error "No previously checkpointed state for $type \"$app\""
	}
	set w $TKCON(base).compare
	if {[winfo exists $w]} {
	    $w.text config -state normal
	    $w.text delete 1.0 end
	} else {
	    toplevel $w
	    frame $w.btn
	    scrollbar $w.sy -takefocus 0 -bd 1 -command [list $w.text yview]
	    text $w.text -yscrollcommand [list $w.sy set] -height 12 \
		    -foreground $TKCON(color,stdin) \
		    -background $TKCON(color,bg) \
		    -insertbackground $TKCON(color,cursor) \
		    -font $TKCON(font)
	    pack $w.btn -side bottom -fill x
	    pack $w.sy -side right -fill y
	    pack $w.text -fill both -expand 1
	    button $w.btn.close -text "Dismiss" -width 11 \
		    -command [list destroy $w]
	    button $w.btn.check  -text "Recheckpoint" -width 11
	    button $w.btn.revert -text "Revert" -width 11
	    button $w.btn.expand -text "Verbose" -width 11
	    button $w.btn.update -text "Update" -width 11
	    pack $w.btn.check $w.btn.revert $w.btn.expand $w.btn.update \
		    $w.btn.close -side left -fill x -padx 4 -pady 2 -expand 1
	    $w.text tag config red -foreground red
	}
	wm title $w "Compare State: $type [list $app]"

	$w.btn.check config -command "tkConStateCheckpoint [list $app] $type; \
		tkConStateCompare [list $app] $type $verbose"
	$w.btn.revert config -command "tkConStateRevert [list $app] $type; \
		tkConStateCompare [list $app] $type $verbose"
	$w.btn.update config -command [info level 0]
	if {$verbose} {
	    $w.btn.expand config -text Brief \
		    -command [list tkConStateCompare $app $type 0]
	} else {
	    $w.btn.expand config -text Verbose \
		    -command [list tkConStateCompare $app $type 1]
	}
	## Don't allow verbose mode unless 'dump' exists in $app
	## We're assuming this is TkCon's dump command
	set hasdump [llength [tkConEvalOther $app $type info commands dump]]
	if {$hasdump} {
	    $w.btn.expand config -state normal
	} else {
	    $w.btn.expand config -state disabled
	}

	set cmds [lremove [tkConEvalOther $app $type info commands *] \
		$TKCON($type,$app,cmd)]
	set vars [lremove [tkConEvalOther $app $type info vars *] \
		$TKCON($type,$app,var)]

	if {$hasdump && $verbose} {
	    set cmds [tkConEvalOther $app $type eval dump c -nocomplain $cmds]
	    set vars [tkConEvalOther $app $type eval dump v -nocomplain $vars]
	}
	$w.text insert 1.0 "NEW COMMANDS IN \"$app\":\n" red \
		$cmds {} "\n\nNEW VARIABLES IN \"$app\":\n" red $vars {}

	raise $w
	$w.text config -state disabled
    }

    ## tkConStateRevert - reverts interpreter to previous state
    # ARGS:
    ##
    ;proc tkConStateRevert {app type} {
	global TKCON
	if {![info exists TKCON($type,$app,cmd)]} {
	    return -code error \
		    "No previously checkpointed state for $type \"$app\""
	}
	if {![tk_dialog $TKCON(base).warning "Revert State?" \
		"Are you sure you want to revert the state in $type \"$app\"?"\
		questhead 1 "Do It" "Cancel"]} {
	    foreach i [lremove [tkConEvalOther $app $type info commands *] \
		    $TKCON($type,$app,cmd)] {
		catch {tkConEvalOther $app $type rename $i {}}
	    }
	    foreach i [lremove [tkConEvalOther $app $type info vars *] \
		    $TKCON($type,$app,var)] {
		catch {tkConEvalOther $app $type unset $i}
	    }
	}
    }

    ## tkConStateCleanup - cleans up state information in master array
    #
    ##
    ;proc tkConStateCleanup {args} {
	global TKCON
	if {![llength $args]} {
	    foreach state [array names TKCON slave,*] {
		if {![interp exists [string range $state 6 end]]} {
		    unset TKCON($state)
		}
	    }
	} else {
	    set app  [lindex $args 0]
	    set type [lindex $args 1]
	    if {[regexp {^(|slave)$} $type]} {
		foreach state [array names TKCON "slave,$app\[, \]*"] {
		    if {![interp exists [string range $state 6 end]]} {
			unset TKCON($state)
		    }
		}
	    } else {
		catch {unset TKCON($type,$app)}
	    }
	}
    }
}

## tkConEvent - get history event, search if string != {}
## look forward (next) if $int>0, otherwise look back (prev)
# ARGS:	W	- console widget
##
;proc tkConEvent {int {str {}}} {
    if {!$int} return

    global TKCON
    set w $TKCON(console)

    set nextid [tkConEvalSlave history nextid]
    if {[string compare {} $str]} {
	## String is not empty, do an event search
	set event $TKCON(event)
	if {$int < 0 && $event == $nextid} { set TKCON(cmdbuf) $str }
	set len [string len $TKCON(cmdbuf)]
	incr len -1
	if {$int > 0} {
	    ## Search history forward
	    while {$event < $nextid} {
		if {[incr event] == $nextid} {
		    $w delete limit end
		    $w insert limit $TKCON(cmdbuf)
		    break
		} elseif {
		    ![catch {tkConEvalSlave history event $event} res] &&
		    ![string compare $TKCON(cmdbuf) [string range $res 0 $len]]
		} {
		    $w delete limit end
		    $w insert limit $res
		    break
		}
	    }
	    set TKCON(event) $event
	} else {
	    ## Search history reverse
	    while {![catch {tkConEvalSlave \
		    history event [incr event -1]} res]} {
		if {![string compare $TKCON(cmdbuf) \
			[string range $res 0 $len]]} {
		    $w delete limit end
		    $w insert limit $res
		    set TKCON(event) $event
		    break
		}
	    }
	} 
    } else {
	## String is empty, just get next/prev event
	if {$int > 0} {
	    ## Goto next command in history
	    if {$TKCON(event) < $nextid} {
		$w delete limit end
		if {[incr TKCON(event)] == $nextid} {
		    $w insert limit $TKCON(cmdbuf)
		} else {
		    $w insert limit [tkConEvalSlave \
			    history event $TKCON(event)]
		}
	    }
	} else {
	    ## Goto previous command in history
	    if {$TKCON(event) == $nextid} {
		set TKCON(cmdbuf) [tkConCmdGet $w]
	    }
	    if {[catch {tkConEvalSlave \
		    history event [incr TKCON(event) -1]} res]} {
		incr TKCON(event)
	    } else {
		$w delete limit end
		$w insert limit $res
	    }
	}
    }
    $w mark set insert end
    $w see end
}

## tkConErrorHighlight - magic error highlighting
## beware: voodoo included
# ARGS:
##
;proc tkConErrorHighlight w {
    global TKCON
    ## do voodoo here
    set app [tkConAttach]
    # we have to pull the text out, because text regexps are screwed on \n's.
    set info [$w get 1.0 end-1c]
    # Check for specific line error in a proc
    set exp(proc) "\"(\[^\"\]+)\"\n\[\t \]+\\\(procedure \"(\[^\"\]+)\""
    # Check for too few args to a proc
    set exp(param) "parameter \"(\[^\"\]+)\" to \"(\[^\"\]+)\""
    set start 1.0
    while {
	[regexp -indices -- $exp(proc) $info junk what cmd] ||
	[regexp -indices -- $exp(param) $info junk what cmd]
    } {
	foreach {w0 w1} $what {c0 c1} $cmd {break}
	set what [string range $info $w0 $w1]
	set cmd  [string range $info $c0 $c1]
	if {$TKCON(A:namespace) && [string match *::* $cmd]} {
	    set res [uplevel 1 tkConEvalOther $app namespace eval \
		    [list [namespace qualifiers $cmd] \
		    [list info procs [namespace tail $cmd]]]]
	} else {
	    set res [uplevel 1 tkConEvalOther $app info procs [list $cmd]]
	}
	if {[llength $res]==1} {
	    set tag [tkConUniqueTag $w]
	    $w tag add $tag $start+${c0}c $start+1c+${c1}c
	    $w tag configure $tag -foreground $TKCON(color,stdout)
	    $w tag bind $tag <Enter> [list $w tag configure $tag -under 1]
	    $w tag bind $tag <Leave> [list $w tag configure $tag -under 0]
	    $w tag bind $tag <ButtonRelease-1> "if {!\$tkPriv(mouseMoved)} \
		    {[list edit -attach $app -type proc -find $what -- $cmd]}"
	}
	set info [string range $info $c1 end]
	set start [$w index $start+${c1}c]
    }
    ## Next stage, check for procs that start a line
    set start 1.0
    set exp(cmd) "^\"\[^\" \t\n\]+"
    while {
	[string compare {} [set ix \
		[$w search -regexp -count numc -- $exp(cmd) $start end]]]
    } {
	set start [$w index $ix+${numc}c]
	# +1c to avoid the first quote
	set cmd [$w get $ix+1c $start]
	if {$TKCON(A:namespace) && [string match *::* $cmd]} {
	    set res [uplevel 1 tkConEvalOther $app namespace eval \
		    [list [namespace qualifiers $cmd] \
		    [list info procs [namespace tail $cmd]]]]
	} else {
	    set res [uplevel 1 tkConEvalOther $app info procs [list $cmd]]
	}
	if {[llength $res]==1} {
	    set tag [tkConUniqueTag $w]
	    $w tag add $tag $ix+1c $start
	    $w tag configure $tag -foreground $TKCON(color,proc)
	    $w tag bind $tag <Enter> [list $w tag configure $tag -under 1]
	    $w tag bind $tag <Leave> [list $w tag configure $tag -under 0]
	    $w tag bind $tag <ButtonRelease-1> "if {!\$tkPriv(mouseMoved)} \
		    {[list edit -attach $app -type proc -- $cmd]}"
	}
    }
}

## tkcon - command that allows control over the console
# ARGS:	totally variable, see internal comments
## 
proc tkcon {cmd args} {
    global TKCON errorInfo
    switch -glob -- $cmd {
	buf* {
	    ## 'buffer' Sets/Query the buffer size
	    if {[llength $args]} {
		if {[regexp {^[1-9][0-9]*$} $args]} {
		    set TKCON(buffer) $args
		    tkConConstrainBuffer $TKCON(console) $TKCON(buffer)
		} else {
		    return -code error "buffer must be a valid integer"
		}
	    }
	    return $TKCON(buffer)
	}
	bg* {
	    ## 'bgerror' Brings up an error dialog
	    set errorInfo [lindex $args 1]
	    bgerror [lindex $args 0]
	}
	cl* {
	    ## 'close' Closes the console
	    tkConDestroy
	}
	cons* {
	    ## 'console' - passes the args to the text widget of the console.
	    uplevel 1 $TKCON(console) $args
	    tkConConstrainBuffer $TKCON(console) $TKCON(buffer)
	}
	congets {
	    ## 'congets' a replacement for [gets stdin varname]
	    ## This forces a complete command to be input though
	    set old [bind TkConsole <<TkCon_Eval>>]
	    bind TkConsole <<TkCon_Eval>> { set TKCON(wait) 0 }
	    set w $TKCON(console)
	    vwait TKCON(wait)
	    set line [tkConCmdGet $w]
	    $w insert end \n
	    while {![info complete $line] || [regexp {[^\\]\\$} $line]} {
		vwait TKCON(wait)
		set line [tkConCmdGet $w]
		$w insert end \n
		$w see insert
	    }
	    bind TkConsole <<TkCon_Eval>> $old
	    if {![llength $args]} {
		return $line
	    } else {
		upvar [lindex $args 0] data
		set data $line
		return [string length $line]
	    }
	}
	get*	{
	    ## 'gets' - a replacement for [gets stdin]
	    ## This pops up a text widget to be used for stdin (local grabbed)
	    if {[llength $args]} {
		return -code error "wrong # args: should be \"tkcon gets\""
	    }
	    set t $TKCON(base).gets
	    if {![winfo exists $t]} {
		toplevel $t
		wm withdraw $t
		wm title $t "TkCon gets stdin request"
		label $t.gets -text "\"gets stdin\" request:"
		text $t.data -width 32 -height 5 -wrap none \
			-xscrollcommand [list $t.sx set] \
			-yscrollcommand [list $t.sy set]
		scrollbar $t.sx -orient h -takefocus 0 -highlightthick 0 \
			-command [list $t.data xview]
		scrollbar $t.sy -orient v -takefocus 0 -highlightthick 0 \
			-command [list $t.data yview]
		button $t.ok -text "OK" -command {set TKCON(grab) 1}
		bind $t.ok <Return> { %W invoke }
		grid $t.gets -		-sticky ew
		grid $t.data $t.sy	-sticky news
		grid $t.sx		-sticky ew
		grid $t.ok   -		-sticky ew
		grid columnconfig $t 0 -weight 1
		grid rowconfig    $t 1 -weight 1
		wm transient $t $TKCON(root)
		wm geometry $t +[expr {([winfo screenwidth $t]-[winfo \
			reqwidth $t]) / 2}]+[expr {([winfo \
			screenheight $t]-[winfo reqheight $t]) / 2}]
	    }
	    $t.data delete 1.0 end
	    wm deiconify $t
	    raise $t
	    grab $t
	    focus $t.data
	    vwait TKCON(grab)
	    grab release $t
	    wm withdraw $t
	    return [$t.data get 1.0 end-1c]
	}
	err* {
	    ## Outputs stack caused by last error.
	    ## error handling with pizazz (but with pizza would be nice too)
	    if {[llength $args]==2} {
		set app  [lindex $args 0]
		set type [lindex $args 1]
		if {[catch {tkConEvalOther $app $type set errorInfo} info]} {
		    set info "error getting info from $type $app:\n$info"
		}
	    } else {
		set info $TKCON(errorInfo)
	    }
	    if {[string match {} $info]} { set info "errorInfo empty" }
	    ## If args is empty, the -attach switch just ignores it
	    edit -attach $args -type error -- $info
	}
	fi* {
	    ## 'find' string
	    tkConFind $TKCON(console) $args
	}
	fo* {
	    ## 'font' ?fontname? - gets/sets the font of the console
	    if {[llength $args]} {
		$TKCON(console) config -font $args
		set TKCON(font) [$TKCON(console) cget -font]
	    }
	    return $TKCON(font)
	}
	hid* - with* {
	    ## 'hide' 'withdraw' - hides the console.
	    wm withdraw $TKCON(root)
	}
	his* {
	    ## 'history'
	    set sub {\2}
	    if {[string match -n* $args]} { append sub "\n"}
	    set h [tkConEvalSlave history]
	    regsub -all "( *\[0-9\]+  |\t)(\[^\n\]*\n?)" $h $sub h
	    return $h
	}
	ico* {
	    ## 'iconify' - iconifies the console with 'iconify'.
	    wm iconify $TKCON(root)
	}
	mas* - eval {
	    ## 'master' - evals contents in master interpreter
	    uplevel \#0 $args
	}
	set {
	    ## 'set' - set (or get, or unset) simple vars (not whole arrays)
	    ## from the master console interpreter
	    ## possible formats:
	    ##    tkcon set <var>
	    ##    tkcon set <var> <value>
	    ##    tkcon set <var> <interp> <var1> <var2> w
	    ##    tkcon set <var> <interp> <var1> <var2> u
	    ##    tkcon set <var> <interp> <var1> <var2> r
	    if {[llength $args]==5} {
		## This is for use w/ 'tkcon upvar' and only works with slaves
		foreach {var i var1 var2 op} $args break
		if {[string compare {} $var2]} { append var1 "($var2)" }
		switch $op {
		    u { uplevel \#0 [list unset $var] }
		    w {
			return [uplevel \#0 [list set $var \
				[interp eval $i [list set $var1]]]]
		    }
		    r {
			return [interp eval $i [list set $var1 \
				[uplevel \#0 [list set $var]]]]
		    }
		}
	    }
	    return [uplevel \#0 set $args]
	}
	append {
	    ## Modify a var in the master environment using append
	    return [uplevel \#0 append $args]
	}
	lappend {
	    ## Modify a var in the master environment using lappend
	    return [uplevel \#0 lappend $args]
	}
	sh* - dei* {
	    ## 'show|deiconify' - deiconifies the console.
	    wm deiconify $TKCON(root)
	    raise $TKCON(root)
	}
	ti* {
	    ## 'title' ?title? - gets/sets the console's title
	    if {[llength $args]==1} {
		return [wm title $TKCON(root) [lindex $args 0]]
	    } else {
		return [wm title $TKCON(root)]
	    }
	}
	upv* {
	    ## 'upvar' masterVar slaveVar
	    ## link slave variable slaveVar to the master variable masterVar
	    ## only works masters<->slave
	    set masterVar [lindex $args 0]
	    set slaveVar  [lindex $args 1]
	    if {[info exists $masterVar]} {
		interp eval $TKCON(exec) [list set $slaveVar [set $masterVar]]
	    } else {
		catch {interp eval $TKCON(exec) [list unset $slaveVar]}
	    }
	    interp eval $TKCON(exec) [list trace variable $slaveVar rwu \
		    [list tkcon set $masterVar $TKCON(exec)]]
	    return
	}
	v* {
	    return $TKCON(version)
	}
	default {
	    ## tries to determine if the command exists, otherwise throws error
	    set new tkCon[string toupper \
		    [string index $cmd 0]][string range $cmd 1 end]
	    if {[llength [info command $new]]} {
		uplevel \#0 $new $args
	    } else {
		return -code error "bad option \"$cmd\": must be\
			[join [lsort [list attach close console destroy \
			font hide iconify load main master new save show \
			slave deiconify version title bgerror]] {, }]"
	    }
	}
    }
}

##
## Some procedures to make up for lack of built-in shell commands
##

## tkcon_puts -
## This allows me to capture all stdout/stderr to the console window
## This will be renamed to 'puts' at the appropriate time during init
##
# ARGS:	same as usual	
# Outputs:	the string with a color-coded text tag
## 
;proc tkcon_puts args {
    set len [llength $args]
    if {$len==1} {
	eval tkcon console insert output $args stdout {\n} stdout
	tkcon console see output
    } elseif {$len==2 && \
	    [regexp {^(stdout|stderr|-nonewline)} [lindex $args 0] junk tmp]} {
	if {[string compare $tmp -nonewline]} {
	    eval tkcon console insert output \
		    [lreplace $args 0 0] $tmp {\n} $tmp
	} else {
	    eval tkcon console insert output [lreplace $args 0 0] stdout
	}
	tkcon console see output
    } elseif {$len==3 && \
	    [regexp {^(stdout|stderr)$} [lreplace $args 2 2] junk tmp]} {
	if {[string compare [lreplace $args 1 2] -nonewline]} {
	    eval tkcon console insert output [lrange $args 1 1] $tmp
	} else {
	    eval tkcon console insert output [lreplace $args 0 1] $tmp
	}
	tkcon console see output
    } else {
	global errorCode errorInfo
	if {[catch "tkcon_tcl_puts $args" msg]} {
	    regsub tkcon_tcl_puts $msg puts msg
	    regsub -all tkcon_tcl_puts $errorInfo puts errorInfo
	    return -code error $msg
	}
	return $msg
    }
    ## WARNING: This update should behave well because it uses idletasks,
    ## however, if there are weird looping problems with events, or
    ## hanging in waits, try commenting this out.
    if {$len} {update idletasks}
}

## tkcon_gets -
## This allows me to capture all stdin input without needing to stdin
## This will be renamed to 'gets' at the appropriate time during init
##
# ARGS:		same as gets	
# Outputs:	same as gets
##
;proc tkcon_gets args {
    set len [llength $args]
    if {$len != 1 && $len != 2} {
	return -code error \
		"wrong # args: should be \"gets channelId ?varName?\""
    }
    if {[string compare stdin [lindex $args 0]]} {
	return [uplevel 1 tkcon_tcl_gets $args]
    }
    set data [tkcon gets]
    if {$len == 2} {
	upvar 1 [lindex $args 1] var
	set var $data
	return [string length $data]
    }
    return $data
}

## edit - opens a file/proc/var for reading/editing
## 
# Arguments:
#   type	proc/file/var
#   what	the actual name of the item
# Returns:	nothing
## 
;proc edit {args} {
    global TKCON

    array set opts {-find {} -type {} -attach {}}
    while {[string match -* [lindex $args 0]]} {
	switch -glob -- [lindex $args 0] {
	    -f*	{ set opts(-find) [lindex $args 1] }
	    -a*	{ set opts(-attach) [lindex $args 1] }
	    -t*	{ set opts(-type) [lindex $args 1] }
	    --	{ set args [lreplace $args 0 0]; break }
	    default {return -code error "unknown option \"[lindex $args 0]\""}
	}
	set args [lreplace $args 0 1]
    }
    # determine who we are dealing with
    if {[llength $opts(-attach)]} {
	foreach {app type} $opts(-attach) {break}
    } else {
	foreach {app type} [tkcon attach] {break}
    }

    set word [lindex $args 0]
    if {[string match {} $opts(-type)]} {
	if {[llength [tkConEvalOther $app $type info commands [list $word]]]} {
	    set opts(-type) "proc"
	} elseif {[llength [tkConEvalOther $app $type info vars [list $word]]]} {
	    set opts(-type) "var"
	} elseif {[tkConEvalOther $app $type file isfile [list $word]]} {
	    set opts(-type) "file"
	}
    }
    if {[string compare $opts(-type) {}]} {
	# Create unique edit window toplevel
	set w $TKCON(base).__edit
	set i 0
	while {[winfo exists $w[incr i]]} {}
	append w $i
	toplevel $w
	wm withdraw $w
	if {[string length $word] > 12} {
	    wm title $w "TkCon Edit: [string range $word 0 9]..."
	} else {
	    wm title $w "TkCon Edit: $word"
	}

	text $w.text -wrap none \
		-xscrollcommand [list $w.sx set] \
		-yscrollcommand [list $w.sy set] \
		-foreground $TKCON(color,stdin) \
		-background $TKCON(color,bg) \
		-insertbackground $TKCON(color,cursor) \
		-font $TKCON(font)
	scrollbar $w.sx -orient h -takefocus 0 -bd 1 \
		-command [list $w.text xview]
	scrollbar $w.sy -orient v -takefocus 0 -bd 1 \
		-command [list $w.text yview]

	if {[info tclversion] >= 8.0} {
	    set menu [menu $w.mbar]
	    $w configure -menu $menu
	} else {
	    set menu [frame $w.mbar -relief raised -bd 1]
	    grid $menu - - -sticky news
	}

	## File Menu
	##
	set m [menu [tkConMenuButton $menu File file]]
	$m add command -label "Save As..."  -underline 0 \
		-command [list tkConSave {} widget $w.text]
	$m add command -label "Append To..."  -underline 0 \
		-command [list tkConSave {} widget $w.text a+]
	$m add separator
	$m add command -label "Dismiss" -underline 0 -accel "Ctrl-w" \
		-command [list destroy $w]
	bind $w <Control-w>		[list destroy $w]
	bind $w <$TKCON(meta)-w>	[list destroy $w]

	## Edit Menu
	##
	set text $w.text
	set m [menu [tkConMenuButton $menu Edit edit]]
	$m add command -label "Cut"   -under 2 -command [list tkConCut $text]
	$m add command -label "Copy"  -under 0 -command [list tkConCopy $text]
	$m add command -label "Paste" -under 0 -command [list tkConPaste $text]
	$m add separator
	$m add command -label "Find" -under 0 \
		-command [list tkConFindBox $text]

	## Send To Menu
	##
	set m [menu [tkConMenuButton $menu "Send To..." send]]
	$m add command -label "Send To $app" -underline 0 \
		-command "tkConEvalOther [list $app] $type \
		eval \[$w.text get 1.0 end-1c\]"
	set other [tkcon attach]
	if {[string compare $other [list $app $type]]} {
	    $m add command -label "Send To [lindex $other 0]" \
		    -command "tkConEvalOther $other \
		    eval \[$w.text get 1.0 end-1c\]"
	}

	grid $w.text - $w.sy -sticky news
	grid $w.sx - -sticky ew
	grid columnconfigure $w 0 -weight 1
	grid columnconfigure $w 1 -weight 1
	grid rowconfigure $w 0 -weight 1
    } else {
	return -code error "unrecognized type '$word'"
    }
    switch -glob -- $opts(-type) {
	proc*	{
	    $w.text insert 1.0 [tkConEvalOther $app $type dump proc [list $word]]
	}
	var*	{
	    $w.text insert 1.0 [tkConEvalOther $app $type dump var [list $word]]
	}
	file	{
	    $w.text insert 1.0 [tkConEvalOther $app $type eval \
		    [subst -nocommands {set __tkcon(fid) [open $word r]
	    set __tkcon(data) [read \$__tkcon(fid)]
	    close \$__tkcon(fid)
	    after 2000 unset __tkcon
	    return \$__tkcon(data)}]]
	}
	error*	{
	    $w.text insert 1.0 [join $args \n]
	    tkConErrorHighlight $w.text
	}
	default	{
	    $w.text insert 1.0 [join $args \n]
	}
    }
    wm deiconify $w
    focus $w.text
    if {[string compare $opts(-find) {}]} {
	tkConFind $w.text $opts(-find) -case 1
    }
}
interp alias {} more {} edit
interp alias {} less {} edit

## echo
## Relaxes the one string restriction of 'puts'
# ARGS:	any number of strings to output to stdout
##
proc echo args { puts [concat $args] }

## clear - clears the buffer of the console (not the history though)
## This is executed in the parent interpreter
## 
proc clear {{pcnt 100}} {
    if {![regexp {^[0-9]*$} $pcnt] || $pcnt < 1 || $pcnt > 100} {
	return -code error \
		"invalid percentage to clear: must be 1-100 (100 default)"
    } elseif {$pcnt == 100} {
	tkcon console delete 1.0 end
    } else {
	set tmp [expr {$pcnt/100.0*[tkcon console index end]}]
	tkcon console delete 1.0 "$tmp linestart"
    }
}

## alias - akin to the csh alias command
## If called with no args, then it dumps out all current aliases
## If called with one arg, returns the alias of that arg (or {} if none)
# ARGS:	newcmd	- (optional) command to bind alias to
# 	args	- command and args being aliased
## 
proc alias {{newcmd {}} args} {
    if {[string match {} $newcmd]} {
	set res {}
	foreach a [interp aliases] {
	    lappend res [list $a -> [interp alias {} $a]]
	}
	return [join $res \n]
    } elseif {![llength $args]} {
	interp alias {} $newcmd
    } else {
	eval interp alias [list {} $newcmd {}] $args
    }
}

## unalias - unaliases an alias'ed command
# ARGS:	cmd	- command to unbind as an alias
## 
proc unalias {cmd} {
    interp alias {} $cmd {}
}

## dump - outputs variables/procedure/widget info in source'able form.
## Accepts glob style pattern matching for the names
# ARGS:	type	- type of thing to dump: must be variable, procedure, widget
# OPTS: -nocomplain
#		don't complain if no vars match something
#	-filter pattern
#		specifies a glob filter pattern to be used by the variable
#		method as an array filter pattern (it filters down for
#		nested elements) and in the widget method as a config
#		option filter pattern
#	--	forcibly ends options recognition
# Returns:	the values of the requested items in a 'source'able form
## 
proc dump {type args} {
    set whine 1
    set code  ok
    if {![llength $args]} {
	## If no args, assume they gave us something to dump and
	## we'll try anything
	set args $type
	set type any
    }
    while {[string match -* [lindex $args 0]]} {
	switch -glob -- [lindex $args 0] {
	    -n* { set whine 0; set args [lreplace $args 0 0] }
	    -f* { set fltr [lindex $args 1]; set args [lreplace $args 0 1] }
	    --  { set args [lreplace $args 0 0]; break }
	    default {return -code error "unknown option \"[lindex $args 0]\""}
	}
    }
    if {$whine && ![llength $args]} {
	return -code error "wrong \# args: [lindex [info level 0] 0] type\
		?-nocomplain? ?-filter pattern? ?--? pattern ?pattern ...?"
    }
    set res {}
    switch -glob -- $type {
	c* {
	    # command
	    # outputs commands by figuring out, as well as possible, what it is
	    # this does not attempt to auto-load anything
	    foreach arg $args {
		if {[llength [set cmds [info commands $arg]]]} {
		    foreach cmd [lsort $cmds] {
			if {[lsearch -exact [interp aliases] $cmd] > -1} {
			    append res "\#\# ALIAS:   $cmd =>\
				    [interp alias {} $cmd]\n"
			} elseif {
			    [llength [info procs $cmd]] ||
			    ([string match *::* $cmd] &&
			    ([info tclversion] >= 8) &&
			    [llength [namespace eval [namespace qual $cmd]
				    info procs [namespace tail $cmd]]])
			} {
			    if {[catch {dump p -- $cmd} msg] && $whine} {
				set code error
			    }
			    append res $msg\n
			} else {
			    append res "\#\# COMMAND: $cmd\n"
			}
		    }
		} elseif {$whine} {
		    append res "\#\# No known command $arg\n"
		    set code error
		}
	    }
	}
	v* {
	    # variable
	    # outputs variables value(s), whether array or simple.
	    if {![info exists fltr]} { set fltr * }
	    foreach arg $args {
		if {![llength [set vars [uplevel 1 info vars [list $arg]]]]} {
		    if {[uplevel info exists $arg]} {
			set vars $arg
		    } elseif {$whine} {
			append res "\#\# No known variable $arg\n"
			set code error
			continue
		    } else { continue }
		}
		foreach var [lsort $vars] {
		    if {[info tclversion] >= 8} {
			set var [uplevel [list namespace which -variable $var]]
		    }
		    upvar $var v
		    if {[array exists v] || [catch {string length $v}]} {
			set nst {}
			append res "array set [list $var] \{\n"
			if {[array size v]} {
			    foreach i [lsort [array names v $fltr]] {
				upvar 0 v\($i\) __a
				if {[array exists __a]} {
				    append nst "\#\# NESTED ARRAY ELEM: $i\n"
				    append nst "upvar 0 [list $var\($i\)] __a;\
					    [dump v -filter $fltr __a]\n"
				} else {
				    append res "    [list $i]\t[list $v($i)]\n"
				}
			    }
			} else {
			    ## empty array
			    append res "    empty array\n"
			    append nst "unset [list $var](empty)\n"
			}
			append res "\}\n$nst"
		    } else {
			append res [list set $var $v]\n
		    }
		}
	    }
	}
	p* {
	    # procedure
	    foreach arg $args {
		if {
		    ![llength [set procs [info proc $arg]]] &&
		    ([string match *::* $arg] && ([info tclversion] >= 8) &&
		    [llength [set ps [namespace eval \
			    [namespace qualifier $arg] \
			    info procs [namespace tail $arg]]]])
		} {
		    set procs {}
		    set namesp [namespace qualifier $arg]
		    foreach p $ps {
			lappend procs ${namesp}::$p
		    }
		}
		if {[llength $procs]} {
		    foreach p [lsort $procs] {
			set as {}
			foreach a [info args $p] {
			    if {[info default $p $a tmp]} {
				lappend as [list $a $tmp]
			    } else {
				lappend as $a
			    }
			}
			append res [list proc $p $as [info body $p]]\n
		    }
		} elseif {$whine} {
		    append res "\#\# No known proc $arg\n"
		    set code error
		}
	    }
	}
	w* {
	    # widget
	    ## The user should have Tk loaded
	    if {![llength [info command winfo]]} {
		return -code error "winfo not present, cannot dump widgets"
	    }
	    if {![info exists fltr]} { set fltr .* }
	    foreach arg $args {
		if {[llength [set ws [info command $arg]]]} {
		    foreach w [lsort $ws] {
			if {[winfo exists $w]} {
			    if {[catch {$w configure} cfg]} {
				append res "\#\# Widget $w\
					does not support configure method"
				set code error
			    } else {
				append res "\#\# [winfo class $w]\
					$w\n$w configure"
				foreach c $cfg {
				    if {[llength $c] != 5} continue
				    ## Check to see that the option does
				    ## not match the default, then check
				    ## the item against the user filter
				    if {[string compare [lindex $c 3] \
					    [lindex $c 4]] && \
					    [regexp -nocase -- $fltr $c]} {
					append res " \\\n\t[list [lindex $c 0]\
						[lindex $c 4]]"
				    }
				}
				append res \n
			    }
			}
		    }
		} elseif {$whine} {
		    append res "\#\# No known widget $arg\n"
		    set code error
		}
	    }
	}
	a* {
	    ## see if we recognize it, other complain
	    if {[regexp {(var|com|proc|widget)} \
		    [set types [uplevel 1 what $args]]]} {
		foreach type $types {
		    append res "[uplevel 1 dump $type $args]\n"
		}
	    } else {
		set res "dump was unable to resolve type for \"$args\""
		set code error
	    }
	}
	default {
	    return -code error "bad [lindex [info level 0] 0] option\
		    \"$type\": must be variable, command, procedure,\
		    or widget"
	}
    }
    return -code $code [string trimright $res \n]
}

## idebug - interactive debugger
# ARGS:	opt
#
##
proc idebug {opt args} {
    global IDEBUG

    if {![info exists IDEBUG(on)]} {
	array set IDEBUG { on 0 id * debugging 0 }
    }
    set level [expr {[info level]-1}]
    switch -glob -- $opt {
	on	{
	    if {[llength $args]} { set IDEBUG(id) $args }
	    return [set IDEBUG(on) 1]
	}
	off	{ return [set IDEBUG(on) 0] }
	id  {
	    if {![llength $args]} {
		return $IDEBUG(id)
	    } else { return [set IDEBUG(id) $args] }
	}
	break {
	    if {!$IDEBUG(on) || $IDEBUG(debugging) || \
		    ([llength $args] && \
		    ![string match $IDEBUG(id) $args]) || [info level]<1} {
		return
	    }
	    set IDEBUG(debugging) 1
	    puts stderr "idebug at level \#$level: [lindex [info level -1] 0]"
	    set tkcon [llength [info command tkcon]]
	    if {$tkcon} {
		tkcon show
		tkcon master eval set TKCON(prompt2) \$TKCON(prompt1)
		tkcon master eval set TKCON(prompt1) \$TKCON(debugPrompt)
		set slave [tkcon set TKCON(exec)]
		set event [tkcon set TKCON(event)]
		tkcon set TKCON(exec) [tkcon master interp create debugger]
		tkcon set TKCON(event) 1
	    }
	    set max $level
	    while 1 {
		set err {}
		if {$tkcon} {
		    tkcon evalSlave set level $level
		    tkcon prompt
		    set line [tkcon congets]
		    tkcon console mark set output end
		} else {
		    puts -nonewline stderr "(level \#$level) debug > "
		    gets stdin line
		    while {![info complete $line]} {
			puts -nonewline "> "
			append line "\n[gets stdin]"
		    }
		}
		if {[string match {} $line]} continue
		set key [lindex $line 0]
		if {![regexp {^([#-]?[0-9]+)} [lreplace $line 0 0] lvl]} {
		    set lvl \#$level
		}
		set res {}; set c 0
		switch -- $key {
		    + {
			## Allow for jumping multiple levels
			if {$level < $max} {
			    idebug trace [incr level] $level 0 VERBOSE
			}
		    }
		    - {
			## Allow for jumping multiple levels
			if {$level > 1} {
			    idebug trace [incr level -1] $level 0 VERBOSE
			}
		    }
		    . { set c [catch {idebug trace $level $level 0 VERBOSE} res] }
		    v { set c [catch {idebug show vars $lvl } res] }
		    V { set c [catch {idebug show vars $lvl VERBOSE} res] }
		    l { set c [catch {idebug show locals $lvl } res] }
		    L { set c [catch {idebug show locals $lvl VERBOSE} res] }
		    g { set c [catch {idebug show globals $lvl } res] }
		    G { set c [catch {idebug show globals $lvl VERBOSE} res] }
		    t { set c [catch {idebug trace 1 $max $level } res] }
		    T { set c [catch {idebug trace 1 $max $level VERBOSE} res]}
		    b { set c [catch {idebug body $lvl} res] }
		    o { set res [set IDEBUG(on) [expr {!$IDEBUG(on)}]] }
		    h - ?	{
			puts stderr "    +		Move down in call stack
    -		Move up in call stack
    .		Show current proc name and params

    v		Show names of variables currently in scope
    V		Show names of variables currently in scope with values
    l		Show names of local (transient) variables
    L		Show names of local (transient) variables with values
    g		Show names of declared global variables
    G		Show names of declared global variables with values
    t		Show a stack trace
    T		Show a verbose stack trace

    b		Show body of current proc
    o		Toggle on/off any further debugging
    c,q		Continue regular execution (Quit debugger)
    h,?		Print this help
    default	Evaluate line at current level (\#$level)"
		    }
		    c - q break
		    default { set c [catch {uplevel \#$level $line} res] }
		}
		if {$tkcon} {
		    tkcon set TKCON(event) \
			    [tkcon evalSlave eval history add [list $line]\
			    \; history nextid]
		}
		if {$c} {
		    puts stderr $res
		} elseif {[string compare {} $res]} {
		    puts $res
		}
	    }
	    set IDEBUG(debugging) 0
	    if {$tkcon} {
		tkcon master interp delete debugger
		tkcon master eval set TKCON(prompt1) \$TKCON(prompt2)
		tkcon set TKCON(exec) $slave
		tkcon set TKCON(event) $event
		tkcon prompt
	    }
	}
	bo* {
	    if {[regexp {^([#-]?[0-9]+)} $args level]} {
		return [uplevel $level {dump c -no [lindex [info level 0] 0]}]
	    }
	}
	t* {
	    if {[llength $args]<2} return
	    set min [set max [set lvl $level]]
	    set exp {^#?([0-9]+)? ?#?([0-9]+) ?#?([0-9]+)? ?(VERBOSE)?}
	    if {![regexp $exp $args junk min max lvl verbose]} return
	    for {set i $max} {
		$i>=$min && ![catch {uplevel \#$i info level 0} info]
	    } {incr i -1} {
		if {$i==$lvl} {
		    puts -nonewline stderr "* \#$i:\t"
		} else {
		    puts -nonewline stderr "  \#$i:\t"
		}
		set name [lindex $info 0]
		if {[string compare VERBOSE $verbose] || \
			![llength [info procs $name]]} {
		    puts $info
		} else {
		    puts "proc $name {[info args $name]} { ... }"
		    set idx 0
		    foreach arg [info args $name] {
			if {[string match args $arg]} {
			    puts "\t$arg = [lrange $info [incr idx] end]"
			    break
			} else {
			    puts "\t$arg = [lindex $info [incr idx]]"
			}
		    }
		}
	    }
	}
	s* {
	    #var, local, global
	    set level \#$level
	    if {![regexp {^([vgl][^ ]*) ?([#-]?[0-9]+)? ?(VERBOSE)?} \
		    $args junk type level verbose]} return
	    switch -glob -- $type {
		v* { set vars [uplevel $level {lsort [info vars]}] }
		l* { set vars [uplevel $level {lsort [info locals]}] }
		g* { set vars [lremove [uplevel $level {info vars}] \
			[uplevel $level {info locals}]] }
	    }
	    if {[string match VERBOSE $verbose]} {
		return [uplevel $level dump var -nocomplain $vars]
	    } else {
		return $vars
	    }
	}
	e* - pu* {
	    if {[llength $opt]==1 && [catch {lindex [info level -1] 0} id]} {
		set id [lindex [info level 0] 0]
	    } else {
		set id [lindex $opt 1]
	    }
	    if {$IDEBUG(on) && [string match $IDEBUG(id) $id]} {
		if {[string match e* $opt]} {
		    puts [concat $args]
		} else { eval puts $args }
	    }
	}
	default {
	    return -code error "bad [lindex [info level 0] 0] option \"$opt\",\
		    must be: [join [lsort [list on off id break print body\
		    trace show puts echo]] {, }]"
	}
    }
}

## observe - like trace, but not
# ARGS:	opt	- option
#	name	- name of variable or command
##
proc observe {opt name args} {
    global tcl_observe
    switch -glob -- $opt {
	co* {
	    if {[regexp {^(catch|lreplace|set|puts|for|incr|info|uplevel)$} \
		    $name]} {
		return -code error "cannot observe \"$name\":\
			infinite eval loop will occur"
	    }
	    set old ${name}@
	    while {[llength [info command $old]]} { append old @ }
	    rename $name $old
	    set max 4
	    regexp {^[0-9]+} $args max
	    ## idebug trace could be used here
	    ;proc $name args "
	    for {set i \[info level\]; set max \[expr \[info level\]-$max\]} {
		\$i>=\$max && !\[catch {uplevel \#\$i info level 0} info\]
	    } {incr i -1} {
		puts -nonewline stderr \"  \#\$i:\t\"
		puts \$info
	    }
	    uplevel \[lreplace \[info level 0\] 0 0 $old\]
	    "
	    set tcl_observe($name) $old
	}
	cd* {
	    if {[info exists tcl_observe($name)] && [catch {
		rename $name {}
		rename $tcl_observe($name) $name
		unset tcl_observe($name)
	    } err]} { return -code error $err }
	}
	ci* {
	    ## What a useless method...
	    if {[info exists tcl_observe($name)]} {
		set i $tcl_observe($name)
		set res "\"$name\" observes true command \"$i\""
		while {[info exists tcl_observe($i)]} {
		    append res "\n\"$name\" observes true command \"$i\""
		    set i $tcl_observe($name)
		}
		return $res
	    }
	}
	va* - vd* {
	    set type [lindex $args 0]
	    set args [lrange $args 1 end]
	    if {![regexp {^[rwu]} $type type]} {
		return -code error "bad [lindex [info level 0] 0] $opt type\
			\"$type\", must be: read, write or unset"
	    }
	    if {![llength $args]} { set args observe_var }
	    uplevel [list trace $opt $name $type $args]
	}
	vi* {
	    uplevel [list trace vinfo $name]
	}
	default {
	    return -code error "bad [lindex [info level 0] 0] option\
		    \"[lindex $args 0]\", must be: [join [lsort \
		    [list command cdelete cinfo variable vdelete vinfo]] {, }]"
	}
    }
}

## observe_var - auxilary function for observing vars, called by trace
## via observe
# ARGS:	name	- variable name
#	el	- array element name, if any
#	op	- operation type (rwu)
##
;proc observe_var {name el op} {
    if {[string match u $op]} {
	if {[string compare {} $el]} {
	    puts "unset \"${name}($el)\""
	} else {
	    puts "unset \"$name\""
	}
    } else {
	upvar $name $name
	if {[info exists ${name}($el)]} {
	    puts [dump v ${name}($el)]
	} else {
	    puts [dump v $name]
	}
    }
}

## which - tells you where a command is found
# ARGS:	cmd	- command name
# Returns:	where command is found (internal / external / unknown)
## 
proc which cmd {
    ## This tries to auto-load a command if not recognized
    set types [what $cmd 1]
    if {[llength $types]} {
	set out {}
	
	foreach type $types {
	    switch -- $type {
		alias		{ set res "$cmd: aliased to [alias $cmd]" }
		procedure	{ set res "$cmd: procedure" }
		command		{ set res "$cmd: internal command" }
		executable	{ lappend out [auto_execok $cmd] }
		variable	{ lappend out "$cmd: variable" }
	    }
	    if {[info exists res]} {
		global auto_index
		if {[info exists auto_index($cmd)]} {
		    ## This tells you where the command MIGHT have come from -
		    ## not true if the command was redefined interactively or
		    ## existed before it had to be auto_loaded.  This is just
		    ## provided as a hint at where it MAY have come from
		    append res " ($auto_index($cmd))"
		}
		lappend out $res
		unset res
	    }
	}
	return [join $out \n]
    } else {
	return -code error "$cmd: command not found"
    }
}

## what - tells you what a string is recognized as
# ARGS:	str	- string to id
# Returns:	id types of command as list
## 
proc what {str {autoload 0}} {
    set types {}
    if {[llength [info commands $str]] || ($autoload && \
	    [auto_load $str] && [llength [info commands $str]])} {
	if {[lsearch -exact [interp aliases] $str] > -1} {
	    lappend types "alias"
	} elseif {
	    [llength [info procs $str]] ||
	    ([string match *::* $str] && ([info tclversion] >= 8) &&
	    [llength [namespace eval [namespace qualifier $str] \
		    info procs [namespace tail $str]]])
	} {
	    lappend types "procedure"
	} else {
	    lappend types "command"
	}
    }
    if {[llength [uplevel 1 info vars $str]]} {
	lappend types "variable"
    }
    if {[file isdirectory $str]} {
	lappend types "directory"
    }
    if {[file isfile $str]} {
	lappend types "file"
    }
    if {[llength [info commands winfo]] && [winfo exists $str]} {
	lappend types "widget"
    }
    if {[string compare {} [auto_execok $str]]} {
	lappend types "executable"
    }
    return $types
}

## dir - directory list
# ARGS:	args	- names/glob patterns of directories to list
# OPTS:	-all	- list hidden files as well (Unix dot files)
#	-long	- list in full format "permissions size date filename"
#	-full	- displays / after directories and link paths for links
# Returns:	a directory listing
## 
proc dir {args} {
    array set s {
	all 0 full 0 long 0
	0 --- 1 --x 2 -w- 3 -wx 4 r-- 5 r-x 6 rw- 7 rwx
    }
    while {[string match \-* [lindex $args 0]]} {
	set str [lindex $args 0]
	set args [lreplace $args 0 0]
	switch -glob -- $str {
	    -a* {set s(all) 1} -f* {set s(full) 1}
	    -l* {set s(long) 1} -- break
	    default {
		return -code error "unknown option \"$str\",\
			should be one of: -all, -full, -long"
	    }
	}
    }
    set sep [string trim [file join . .] .]
    if {![llength $args]} { set args . }
    foreach arg $args {
	if {[file isdir $arg]} {
	    set arg [string trimr $arg $sep]$sep
	    if {$s(all)} {
		lappend out [list $arg [lsort [glob -nocomplain -- $arg.* $arg*]]]
	    } else {
		lappend out [list $arg [lsort [glob -nocomplain -- $arg*]]]
	    }
	} else {
	    lappend out [list [file dirname $arg]$sep \
		    [lsort [glob -nocomplain -- $arg]]]
	}
    }
    if {$s(long)} {
	set old [clock scan {1 year ago}]
	set fmt "%s%9d %s %s\n"
	foreach o $out {
	    set d [lindex $o 0]
	    append res $d:\n
	    foreach f [lindex $o 1] {
		file lstat $f st
		set f [file tail $f]
		if {$s(full)} {
		    switch -glob $st(type) {
			d* { append f $sep }
			l* { append f "@ -> [file readlink $d$sep$f]" }
			default { if {[file exec $d$sep$f]} { append f * } }
		    }
		}
		if {[string match file $st(type)]} {
		    set mode -
		} else {
		    set mode [string index $st(type) 0]
		}
		foreach j [split [format %o [expr {$st(mode)&0777}]] {}] {
		    append mode $s($j)
		}
		if {$st(mtime)>$old} {
		    set cfmt {%b %d %H:%M}
		} else {
		    set cfmt {%b %d  %Y}
		}
		append res [format $fmt $mode $st(size) \
			[clock format $st(mtime) -format $cfmt] $f]
	    }
	    append res \n
	}
    } else {
	foreach o $out {
	    set d [lindex $o 0]
	    append res "$d:\n"
	    set i 0
	    foreach f [lindex $o 1] {
		if {[string len [file tail $f]] > $i} {
		    set i [string len [file tail $f]]
		}
	    }
	    set i [expr {$i+2+$s(full)}]
	    ## This gets the number of cols in the TkCon console widget
	    set j [expr {[tkcon master set TKCON(cols)]/$i}]
	    set k 0
	    foreach f [lindex $o 1] {
		set f [file tail $f]
		if {$s(full)} {
		    switch -glob [file type $d$sep$f] {
			d* { append f $sep }
			l* { append f @ }
			default { if {[file exec $d$sep$f]} { append f * } }
		    }
		}
		append res [format "%-${i}s" $f]
		if {[incr k]%$j == 0} {set res [string trimr $res]\n}
	    }
	    append res \n\n
	}
    }
    return [string trimr $res]
}
interp alias {} ls {} dir -full

## tclindex - creates the tclIndex file
# OPTS:	-ext	- extensions to auto index (defaults to *.tcl)
#	-pkg	- whether to create a pkgIndex.tcl file
#	-idx	- whether to create a tclIndex file
# ARGS:	args	- directories to auto index (defaults to pwd)
# Outputs:	tclIndex/pkgIndex.tcl file to each directory
##
proc tclindex args {
    set truth {^(1|yes|true|on)$}; set pkg 0; set idx 1;
    while {[regexp -- {^-[^ ]+} $args opt] && [llength $args]} {
	switch -glob -- $opt {
	    --  { set args [lreplace $args 0 0]; break }
	    -e* { set ext [lindex $args 1] }
	    -p* { set pkg [regexp -nocase $truth [lindex $args 1]] }
	    -i* { set idx [regexp -nocase $truth [lindex $args 1]] }
	    default {
		return -code error "bad option \"$opt\": must be one of\
			[join [lsort [list -- -extension -package -index]] {, }]"
	    }
	    set args [lreplace $args 0 1]
	}
    }
    if {![info exists ext]} {
	set ext {*.tcl}
	if {$pkg} { lappend ext *[info sharedlibextension] }
    }
    if {![llength $args]} {
	if {$idx} { eval auto_mkindex [list [pwd]] $ext }
	if {$pkg} { eval pkg_mkIndex [list [pwd]] $ext }
    } else {
	foreach dir $args {
	    if {[file isdir $dir]} {
		if {$idx} { eval auto_mkindex [list [pwd]] $ext }
		if {$pkg} { eval pkg_mkIndex [list [pwd]] $ext }
	    }
	}
    }
}

## lremove - remove items from a list
# OPTS:
#   -all	remove all instances of each item
#   -glob	remove all instances matching glob pattern
#   -regexp	remove all instances matching regexp pattern
# ARGS:	l	a list to remove items from
#	args	items to remove (these are 'join'ed together)
##
proc lremove {args} {
    array set opts {-all 0 pattern -exact}
    while {[string match -* [lindex $args 0]]} {
	switch -glob -- [lindex $args 0] {
	    -a*	{ set opts(-all) 1 }
	    -g*	{ set opts(pattern) -glob }
	    -r*	{ set opts(pattern) -regexp }
	    --	{ set args [lreplace $args 0 0]; break }
	    default {return -code error "unknown option \"[lindex $args 0]\""}
	}
	set args [lreplace $args 0 0]
    }
    set l [lindex $args 0]
    foreach i [join [lreplace $args 0 0]] {
	if {[set ix [lsearch $opts(pattern) $l $i]] == -1} continue
	set l [lreplace $l $ix $ix]
	if {$opts(-all)} {
	    while {[set ix [lsearch $opts(pattern) $l $i]] != -1} {
		set l [lreplace $l $ix $ix]
	    }
	}
    }
    return $l
}

if {!$TKCON(WWW)} {;

## Unknown changed to get output into tkCon window
# unknown:
# Invoked automatically whenever an unknown command is encountered.
# Works through a list of "unknown handlers" that have been registered
# to deal with unknown commands.  Extensions can integrate their own
# handlers into the 'unknown' facility via 'unknown_handler'.
#
# If a handler exists that recognizes the command, then it will
# take care of the command action and return a valid result or a
# Tcl error.  Otherwise, it should return "-code continue" (=2)
# and responsibility for the command is passed to the next handler.
#
# Arguments:
# args -	A list whose elements are the words of the original
#		command, including the command name.

proc unknown args {
    global unknown_handler_order unknown_handlers errorInfo errorCode

    #
    # Be careful to save error info now, and restore it later
    # for each handler.  Some handlers generate their own errors
    # and disrupt handling.
    #
    set savedErrorCode $errorCode
    set savedErrorInfo $errorInfo

    if {![info exists unknown_handler_order] || \
	    ![info exists unknown_handlers]} {
	set unknown_handlers(tcl) tcl_unknown
	set unknown_handler_order tcl
    }

    foreach handler $unknown_handler_order {
        set status [catch {uplevel $unknown_handlers($handler) $args} result]

        if {$status == 1} {
            #
            # Strip the last five lines off the error stack (they're
            # from the "uplevel" command).
            #
            set new [split $errorInfo \n]
            set new [join [lrange $new 0 [expr {[llength $new]-6}]] \n]
            return -code $status -errorcode $errorCode \
                -errorinfo $new $result

        } elseif {$status != 4} {
            return -code $status $result
        }

        set errorCode $savedErrorCode
        set errorInfo $savedErrorInfo
    }

    set name [lindex $args 0]
    return -code error "invalid command name \"$name\""
}

# tcl_unknown:
# Invoked when a Tcl command is invoked that doesn't exist in the
# interpreter:
#
#	1. See if the autoload facility can locate the command in a
#	   Tcl script file.  If so, load it and execute it.
#	2. If the command was invoked interactively at top-level:
#	    (a) see if the command exists as an executable UNIX program.
#		If so, "exec" the command.
#	    (b) see if the command requests csh-like history substitution
#		in one of the common forms !!, !<number>, or ^old^new.  If
#		so, emulate csh's history substitution.
#	    (c) see if the command is a unique abbreviation for another
#		command.  If so, invoke the command.
#
# Arguments:
# args -	A list whose elements are the words of the original
#		command, including the command name.

proc tcl_unknown args {
    global auto_noexec auto_noload env unknown_pending tcl_interactive tkCon
    global errorCode errorInfo

    # If the command word has the form "namespace inscope ns cmd"
    # then concatenate its arguments onto the end and evaluate it.

    set cmd [lindex $args 0]
    if {[regexp "^namespace\[ \t\n\]+inscope" $cmd] && [llength $cmd] == 4} {
        set arglist [lrange $args 1 end]
	set ret [catch {uplevel $cmd $arglist} result]
        if {$ret == 0} {
            return $result
        } else {
	    return -code $ret -errorcode $errorCode $result
        }
    }

    # Save the values of errorCode and errorInfo variables, since they
    # may get modified if caught errors occur below.  The variables will
    # be restored just before re-executing the missing command.

    set savedErrorCode $errorCode
    set savedErrorInfo $errorInfo
    set name [lindex $args 0]
    if {![info exists auto_noload]} {
	#
	# Make sure we're not trying to load the same proc twice.
	#
	if {[info exists unknown_pending($name)]} {
	    return -code error "self-referential recursion in \"unknown\" for command \"$name\""
	}
	set unknown_pending($name) pending
	if {[llength [info args auto_load]]==1} {
	    set ret [catch {auto_load $name} msg]
	} else {
	    set ret [catch {auto_load $name [uplevel 1 {namespace current}]} msg]
	}
	unset unknown_pending($name)
	if {$ret} {
	    return -code $ret -errorcode $errorCode \
		    "error while autoloading \"$name\": $msg"
	}
	if {![array size unknown_pending]} { unset unknown_pending }
	if {$msg} {
	    set errorCode $savedErrorCode
	    set errorInfo $savedErrorInfo
	    set code [catch {uplevel 1 $args} msg]
	    if {$code ==  1} {
		#
		# Strip the last five lines off the error stack (they're
		# from the "uplevel" command).
		#

		set new [split $errorInfo \n]
		set new [join [lrange $new 0 [expr {[llength $new]-6}]] \n]
		return -code error -errorcode $errorCode \
			-errorinfo $new $msg
	    } else {
		return -code $code $msg
	    }
	}
    }
    if {[info level] == 1 && [string match {} [info script]] \
	    && [info exists tcl_interactive] && $tcl_interactive} {
	if {![info exists auto_noexec]} {
	    set new [auto_execok $name]
	    if {$new != ""} {
		set errorCode $savedErrorCode
		set errorInfo $savedErrorInfo
		return [uplevel exec $new [lrange $args 1 end]]
		#return [uplevel exec >&@stdout <@stdin $new [lrange $args 1 end]]
	    }
	}
	set errorCode $savedErrorCode
	set errorInfo $savedErrorInfo
	##
	## History substitution moved into tkConEvalCmd
	##
	if {[string compare $name "::"] == 0} {
	    set name ""
	}
	if {$ret != 0} {
	    return -code $ret -errorcode $errorCode \
		"error in unknown while checking if \"$name\" is a unique command abbreviation: $msg"
	}
	set cmds [info commands $name*]
	if {[llength $cmds] == 1} {
	    return [uplevel [lreplace $args 0 0 $cmds]]
	}
	if {[llength $cmds]} {
	    if {$name == ""} {
		return -code error "empty command name \"\""
	    } else {
		return -code error \
			"ambiguous command name \"$name\": [lsort $cmds]"
	    }
	}
	## We've got nothing so far
	## Check and see if Tk wasn't loaded, but it appears to be a Tk cmd
	if {![uplevel \#0 info exists tk_version]} {
	    lappend tkcmds bell bind bindtags button \
		    canvas checkbutton clipboard destroy \
		    entry event focus font frame grab grid image \
		    label listbox lower menu menubutton message \
		    option pack place radiobutton raise \
		    scale scrollbar selection send \
		    text tk tkwait toplevel winfo wm
	    if {[lsearch -exact $tkcmds $name] >= 0 && \
		    [tkcon master tk_messageBox -icon question -parent . \
		    -title "Load Tk?" -type retrycancel -default retry \
		    -message "This appears to be a Tk command, but Tk\
		    has not yet been loaded.  Shall I retry the command\
		    with loading Tk first?"] == "retry"} {
		return [uplevel "[list load {} Tk]; $args"]
	    }
	}
    }
    return -code continue
}

} ; # end exclusionary code for WWW

;proc tkConBindings {} {
    global TKCON tcl_platform tk_version

    #-----------------------------------------------------------------------
    # Elements of tkPriv that are used in this file:
    #
    # char -		Character position on the line;  kept in order
    #			to allow moving up or down past short lines while
    #			still remembering the desired position.
    # mouseMoved -	Non-zero means the mouse has moved a significant
    #			amount since the button went down (so, for example,
    #			start dragging out a selection).
    # prevPos -		Used when moving up or down lines via the keyboard.
    #			Keeps track of the previous insert position, so
    #			we can distinguish a series of ups and downs, all
    #			in a row, from a new up or down.
    # selectMode -	The style of selection currently underway:
    #			char, word, or line.
    # x, y -		Last known mouse coordinates for scanning
    #			and auto-scanning.
    #-----------------------------------------------------------------------

    switch -glob $tcl_platform(platform) {
	win*	{ set TKCON(meta) Alt }
	mac*	{ set TKCON(meta) Command }
	default	{ set TKCON(meta) Meta }
    }

    ## Get all Text bindings into TkConsole
    foreach ev [bind Text] { bind TkConsole $ev [bind Text $ev] }	
    ## We really didn't want the newline insertion
    bind TkConsole <Control-Key-o> {}

    ## Now make all our virtual event bindings
    foreach {ev key} [subst -nocommand -noback {
	<<TkCon_Exit>>		<Control-q>
	<<TkCon_New>>		<Control-N>
	<<TkCon_Close>>		<Control-w>
	<<TkCon_About>>		<Control-A>
	<<TkCon_Help>>		<Control-H>
	<<TkCon_Find>>		<Control-F>
	<<TkCon_Slave>>		<Control-Key-1>
	<<TkCon_Master>>	<Control-Key-2>
	<<TkCon_Main>>		<Control-Key-3>
	<<TkCon_Expand>>	<Key-Tab>
	<<TkCon_ExpandFile>>	<Key-Escape>
	<<TkCon_ExpandProc>>	<Control-P>
	<<TkCon_ExpandVar>>	<Control-V>
	<<TkCon_Tab>>		<Control-i>
	<<TkCon_Tab>>		<$TKCON(meta)-i>
	<<TkCon_Newline>>	<Control-o>
	<<TkCon_Newline>>	<$TKCON(meta)-o>
	<<TkCon_Newline>>	<Control-Key-Return>
	<<TkCon_Newline>>	<Control-Key-KP_Enter>
	<<TkCon_Eval>>		<Return>
	<<TkCon_Eval>>		<KP_Enter>
	<<TkCon_Clear>>		<Control-l>
	<<TkCon_Previous>>	<Up>
	<<TkCon_PreviousImmediate>>	<Control-p>
	<<TkCon_PreviousSearch>>	<Control-r>
	<<TkCon_Next>>		<Down>
	<<TkCon_NextImmediate>>	<Control-n>
	<<TkCon_NextSearch>>	<Control-s>
	<<TkCon_Transpose>>	<Control-t>
	<<TkCon_ClearLine>>	<Control-u>
	<<TkCon_SaveCommand>>	<Control-z>
	<<TkCon_Popup>>		<Button-3>
    }] {
	event add $ev $key
	## Make sure the specific key won't be defined
	bind TkConsole $key {}
    }

    ## Make the ROOT bindings
    bind $TKCON(root) <<TkCon_Exit>>	exit
    bind $TKCON(root) <<TkCon_New>>	{ tkConNew }
    bind $TKCON(root) <<TkCon_Close>>	{ tkConDestroy }
    bind $TKCON(root) <<TkCon_About>>	{ tkConAbout }
    bind $TKCON(root) <<TkCon_Help>>	{ tkConHelp }
    bind $TKCON(root) <<TkCon_Find>>	{ tkConFindBox $TKCON(console) }
    bind $TKCON(root) <<TkCon_Slave>>	{
	tkConAttach {}
	tkConPrompt "\n" [tkConCmdGet $TKCON(console)]
    }
    bind $TKCON(root) <<TkCon_Master>>	{
	if {[string compare {} $TKCON(name)]} {
	    tkConAttach $TKCON(name)
	} else {
	    tkConAttach Main
	}
	tkConPrompt "\n" [tkConCmdGet $TKCON(console)]
    }
    bind $TKCON(root) <<TkCon_Main>>	{
	tkConAttach Main
	tkConPrompt "\n" [tkConCmdGet $TKCON(console)]
    }
    bind $TKCON(root) <<TkCon_Popup>> {
	tkConPopupMenu %X %Y
    }

    ## Menu items need null PostCon bindings to avoid the TagProc
    ##
    foreach ev [bind $TKCON(root)] {
	bind PostCon $ev {
	    # empty
	}
    }


    # tkConClipboardKeysyms --
    # This procedure is invoked to identify the keys that correspond to
    # the copy, cut, and paste functions for the clipboard.
    #
    # Arguments:
    # copy -	Name of the key (keysym name plus modifiers, if any,
    #		such as "Meta-y") used for the copy operation.
    # cut -		Name of the key used for the cut operation.
    # paste -	Name of the key used for the paste operation.

    ;proc tkConClipboardKeysyms {copy cut paste} {
	bind TkConsole <$copy>	{tkConCopy %W}
	bind TkConsole <$cut>	{tkConCut %W}
	bind TkConsole <$paste>	{tkConPaste %W}
    }

    ;proc tkConCut w {
	if {[string match $w [selection own -displayof $w]]} {
	    clipboard clear -displayof $w
	    catch {
		clipboard append -displayof $w [selection get -displayof $w]
		if {[$w compare sel.first >= limit]} {
		    $w delete sel.first sel.last
		}
	    }
	}
    }
    ;proc tkConCopy w {
	if {[string match $w [selection own -displayof $w]]} {
	    clipboard clear -displayof $w
	    catch {
		clipboard append -displayof $w [selection get -displayof $w]
	    }
	}
    }
    ## Try and get the default selection, then try and get the selection
    ## type TEXT, then try and get the clipboard if nothing else is available
    ## Why?  Because the Kanji patch screws up the selection types.
    ;proc tkConPaste w {
	if {
	    ![catch {selection get -displayof $w} tmp] ||
	    ![catch {selection get -displayof $w -type TEXT} tmp] ||
	    ![catch {selection get -displayof $w -selection CLIPBOARD} tmp] ||
	    ![catch {selection get -displayof $w -selection CLIPBOARD \
		    -type STRING} tmp]
	} {
	    if {[$w compare insert < limit]} { $w mark set insert end }
	    $w insert insert $tmp
	    $w see insert
	    if {[string match *\n* $tmp]} { tkConEval $w }
	}
    }

    ## Redefine for TkConsole what we need
    ##
    event delete <<Paste>> <Control-V>
    tkConClipboardKeysyms <Copy> <Cut> <Paste>

    bind TkConsole <Insert> {
	catch { tkConInsert %W [selection get -displayof %W] }
    }

    bind TkConsole <Triple-1> {+
	catch {
	    eval %W tag remove sel [%W tag nextrange prompt sel.first sel.last]
	    eval %W tag remove sel sel.last-1c
	    %W mark set insert sel.first
	}
    }

    ## binding editor needed
    ## binding <events> for .tkconrc

    bind TkConsole <<TkCon_ExpandFile>> {
	if {[%W compare insert > limit]} {tkConExpand %W path}
	break
    }
    bind TkConsole <<TkCon_ExpandProc>> {
	if {[%W compare insert > limit]} {tkConExpand %W proc}
    }
    bind TkConsole <<TkCon_ExpandVar>> {
	if {[%W compare insert > limit]} {tkConExpand %W var}
    }
    bind TkConsole <<TkCon_Expand>> {
	if {[%W compare insert > limit]} {tkConExpand %W}
    }
    bind TkConsole <<TkCon_Tab>> {
	if {[%W compare insert >= limit]} {
	    tkConInsert %W \t
	}
    }
    bind TkConsole <<TkCon_Newline>> {
	if {[%W compare insert >= limit]} {
	    tkConInsert %W \n
	}
    }
    bind TkConsole <<TkCon_Eval>> {
	tkConEval %W
    }
    bind TkConsole <Delete> {
	if {[llength [%W tag nextrange sel 1.0 end]] \
		&& [%W compare sel.first >= limit]} {
	    %W delete sel.first sel.last
	} elseif {[%W compare insert >= limit]} {
	    %W delete insert
	    %W see insert
	}
    }
    bind TkConsole <BackSpace> {
	if {[llength [%W tag nextrange sel 1.0 end]] \
		&& [%W compare sel.first >= limit]} {
	    %W delete sel.first sel.last
	} elseif {[%W compare insert != 1.0] && [%W compare insert > limit]} {
	    %W delete insert-1c
	    %W see insert
	}
    }
    bind TkConsole <Control-h> [bind TkConsole <BackSpace>]

    bind TkConsole <KeyPress> {
	tkConInsert %W %A
    }

    bind TkConsole <Control-a> {
	if {[%W compare {limit linestart} == {insert linestart}]} {
	    tkTextSetCursor %W limit
	} else {
	    tkTextSetCursor %W {insert linestart}
	}
    }
    bind TkConsole <Key-Home> [bind TkConsole <Control-a>]
    bind TkConsole <Control-d> {
	if {[%W compare insert < limit]} break
	%W delete insert
    }
    bind TkConsole <Control-k> {
	if {[%W compare insert < limit]} break
	if {[%W compare insert == {insert lineend}]} {
	    %W delete insert
	} else {
	    %W delete insert {insert lineend}
	}
    }
    bind TkConsole <<TkCon_Clear>> {
	## Clear console buffer, without losing current command line input
	set TKCON(tmp) [tkConCmdGet %W]
	clear
	tkConPrompt {} $TKCON(tmp)
    }
    bind TkConsole <<TkCon_Previous>> {
	if {[%W compare {insert linestart} != {limit linestart}]} {
	    tkTextSetCursor %W [tkTextUpDownLine %W -1]
	} else {
	    tkConEvent -1
	}
    }
    bind TkConsole <<TkCon_Next>> {
	if {[%W compare {insert linestart} != {end-1c linestart}]} {
	    tkTextSetCursor %W [tkTextUpDownLine %W 1]
	} else {
	    tkConEvent 1
	}
    }
    bind TkConsole <<TkCon_NextImmediate>>  { tkConEvent 1 }
    bind TkConsole <<TkCon_PreviousImmediate>> { tkConEvent -1 }
    bind TkConsole <<TkCon_PreviousSearch>> { tkConEvent -1 [tkConCmdGet %W] }
    bind TkConsole <<TkCon_NextSearch>>	    { tkConEvent 1 [tkConCmdGet %W] }
    bind TkConsole <<TkCon_Transpose>>	{
	## Transpose current and previous chars
	if {[%W compare insert > "limit+1c"]} { tkTextTranspose %W }
    }
    bind TkConsole <<TkCon_ClearLine>> {
	## Clear command line (Unix shell staple)
	%W delete limit end
    }
    bind TkConsole <<TkCon_SaveCommand>> {
	## Save command buffer (swaps with current command)
	set TKCON(tmp) $TKCON(cmdsave)
	set TKCON(cmdsave) [tkConCmdGet %W]
	if {[string match {} $TKCON(cmdsave)]} {
	    set TKCON(cmdsave) $TKCON(tmp)
	} else {
	    %W delete limit end-1c
	}
	tkConInsert %W $TKCON(tmp)
	%W see end
    }
    catch {bind TkConsole <Key-Page_Up>   { tkTextScrollPages %W -1 }}
    catch {bind TkConsole <Key-Prior>     { tkTextScrollPages %W -1 }}
    catch {bind TkConsole <Key-Page_Down> { tkTextScrollPages %W 1 }}
    catch {bind TkConsole <Key-Next>      { tkTextScrollPages %W 1 }}
    bind TkConsole <$TKCON(meta)-d> {
	if {[%W compare insert >= limit]} {
	    %W delete insert {insert wordend}
	}
    }
    bind TkConsole <$TKCON(meta)-BackSpace> {
	if {[%W compare {insert -1c wordstart} >= limit]} {
	    %W delete {insert -1c wordstart} insert
	}
    }
    bind TkConsole <$TKCON(meta)-Delete> {
	if {[%W compare insert >= limit]} {
	    %W delete insert {insert wordend}
	}
    }
    bind TkConsole <ButtonRelease-2> {
	if {
	    (!$tkPriv(mouseMoved) || $tk_strictMotif) &&
	    (![catch {selection get -displayof %W} TKCON(tmp)] ||
	    ![catch {selection get -displayof %W -type TEXT} TKCON(tmp)] ||
	    ![catch {selection get -displayof %W \
		    -selection CLIPBOARD} TKCON(tmp)])
	} {
	    if {[%W compare @%x,%y < limit]} {
		%W insert end $TKCON(tmp)
	    } else {
		%W insert @%x,%y $TKCON(tmp)
	    }
	    if {[string match *\n* $TKCON(tmp)]} {tkConEval %W}
	}
    }

    ##
    ## End TkConsole bindings
    ##

    ##
    ## Bindings for doing special things based on certain keys
    ##
    bind PostCon <Key-parenright> {
	if {$TKCON(lightbrace) && $TKCON(blinktime)>99 && \
		[string compare \\ [%W get insert-2c]]} {
	    tkConMatchPair %W \( \) limit
	}
    }
    bind PostCon <Key-bracketright> {
	if {$TKCON(lightbrace) && $TKCON(blinktime)>99 && \
		[string compare \\ [%W get insert-2c]]} {
	    tkConMatchPair %W \[ \] limit
	}
    }
    bind PostCon <Key-braceright> {
	if {$TKCON(lightbrace) && $TKCON(blinktime)>99 && \
		[string compare \\ [%W get insert-2c]]} {
	    tkConMatchPair %W \{ \} limit
	}
    }
    bind PostCon <Key-quotedbl> {
	if {$TKCON(lightbrace) && $TKCON(blinktime)>99 && \
		[string compare \\ [%W get insert-2c]]} {
	    tkConMatchQuote %W limit
	}
    }

    bind PostCon <KeyPress> {
	if {$TKCON(lightcmd) && [string compare {} %A]} { tkConTagProc %W }
    }
}

##
# tkConPopupMenu - what to do when the popup menu is requested
##
;proc tkConPopupMenu {X Y} {
    global TKCON
    set w $TKCON(console)
    if {[info tclversion] < 8.0 || \
	    [string compare $w [winfo containing $X $Y]]} {
	tk_popup $TKCON(popup) $X $Y
	return
    }
    set x [expr {$X-[winfo rootx $w]}]
    set y [expr {$Y-[winfo rooty $w]}]
    if {[llength [set tags [$w tag names @$x,$y]]]} {
	if {[lsearch -exact $tags "proc"] >= 0} {
	    lappend type "proc"
	    foreach {first last} [$w tag prevrange proc @$x,$y] {
		set word [$w get $first $last]; break
	    }
	}
	if {[lsearch -exact $tags "var"] >= 0} {
	    lappend type "var"
	    foreach {first last} [$w tag prevrange var @$x,$y] {
		set word [$w get $first $last]; break
	    }
	}
    }
    if {![info exists type]} {
	set exp "(^|\[^\\\\\]\[ \t\n\r\])"
	set exp2 "\[\[\\\\\\?\\*\]"
	set i [$w search -backwards -regexp $exp @$x,$y "@$x,$y linestart"]
	if {[string compare {} $i]} {
	    if {![string match *.0 $i]} {append i +2c}
	    if {[string compare {} \
		    [set j [$w search -regexp $exp $i "$i lineend"]]]} {
		append j +1c
	    } else {
		set j "$i lineend"
	    }
	    regsub -all $exp2 [$w get $i $j] {\\\0} word
	    set word [string trim $word {\"$[]{}',?#*}]
	    if {[llength [tkConEvalAttached info commands [list $word]]]} {
		lappend type "proc"
	    }
	    if {[llength [tkConEvalAttached info vars [list $word]]]} {
		lappend type "var"
	    }
	    if {[tkConEvalAttached file isfile [list $word]]} {
		lappend type "file"
	    }
	}
    }
    if {![info exists type] || ![info exists word]} {
	tk_popup $TKCON(popup) $X $Y
	return
    }
    $TKCON(context) delete 0 end
    $TKCON(context) add command -label "$word" -state disabled
    $TKCON(context) add separator
    set app [tkConAttach]
    if {[lsearch $type proc] != -1} {
	$TKCON(context) add command -label "View Procedure" \
		-command [list edit -attach $app -type proc -- $word]
    }
    if {[lsearch $type var] != -1} {
	$TKCON(context) add command -label "View Variable" \
		-command [list edit -attach $app -type var -- $word]
    }
    if {[lsearch $type file] != -1} {
	$TKCON(context) add command -label "View File" \
		-command [list edit -attach $app -type file -- $word]
    }
    tk_popup $TKCON(context) $X $Y
}

## tkConTagProc - tags a procedure in the console if it's recognized
## This procedure is not perfect.  However, making it perfect wastes
## too much CPU time...
##
;proc tkConTagProc w {
    set exp "\[^\\\\\]\[\[ \t\n\r\;{}\"\$\]"
    set i [$w search -backwards -regexp $exp insert-1c limit-1c]
    if {[string compare {} $i]} {append i +2c} else {set i limit}
    regsub -all "\[\[\\\\\\?\\*\]" [$w get $i "insert-1c wordend"] {\\\0} c
    if {[llength [tkConEvalAttached [list info commands $c]]]} {
	$w tag add proc $i "insert-1c wordend"
    } else {
	$w tag remove proc $i "insert-1c wordend"
    }
    if {[llength [tkConEvalAttached [list info vars $c]]]} {
	$w tag add var $i "insert-1c wordend"
    } else {
	$w tag remove var $i "insert-1c wordend"
    }
}

## tkConMatchPair - blinks a matching pair of characters
## c2 is assumed to be at the text index 'insert'.
## This proc is really loopy and took me an hour to figure out given
## all possible combinations with escaping except for escaped \'s.
## It doesn't take into account possible commenting... Oh well.  If
## anyone has something better, I'd like to see/use it.  This is really
## only efficient for small contexts.
# ARGS:	w	- console text widget
# 	c1	- first char of pair
# 	c2	- second char of pair
# Calls:	tkConBlink
## 
;proc tkConMatchPair {w c1 c2 {lim 1.0}} {
    if {[string compare {} [set ix [$w search -back $c1 insert $lim]]]} {
	while {
	    [string match {\\} [$w get $ix-1c]] &&
	    [string compare {} [set ix [$w search -back $c1 $ix-1c $lim]]]
	} {}
	set i1 insert-1c
	while {[string compare {} $ix]} {
	    set i0 $ix
	    set j 0
	    while {[string compare {} [set i0 [$w search $c2 $i0 $i1]]]} {
		append i0 +1c
		if {[string match {\\} [$w get $i0-2c]]} continue
		incr j
	    }
	    if {!$j} break
	    set i1 $ix
	    while {$j && [string compare {} \
		    [set ix [$w search -back $c1 $ix $lim]]]} {
		if {[string match {\\} [$w get $ix-1c]]} continue
		incr j -1
	    }
	}
	if {[string match {} $ix]} { set ix [$w index $lim] }
    } else { set ix [$w index $lim] }
    global TKCON
    if {$TKCON(blinkrange)} {
	tkConBlink $w $ix [$w index insert]
    } else {
	tkConBlink $w $ix $ix+1c [$w index insert-1c] [$w index insert]
    }
}

## tkConMatchQuote - blinks between matching quotes.
## Blinks just the quote if it's unmatched, otherwise blinks quoted string
## The quote to match is assumed to be at the text index 'insert'.
# ARGS:	w	- console text widget
# Calls:	tkConBlink
## 
;proc tkConMatchQuote {w {lim 1.0}} {
    set i insert-1c
    set j 0
    while {[string compare [set i [$w search -back \" $i $lim]] {}]} {
	if {[string match {\\} [$w get $i-1c]]} continue
	if {!$j} {set i0 $i}
	incr j
    }
    if {$j&1} {
	global TKCON
	if {$TKCON(blinkrange)} {
	    tkConBlink $w $i0 [$w index insert]
	} else {
	    tkConBlink $w $i0 $i0+1c [$w index insert-1c] [$w index insert]
	}
    } else {
	tkConBlink $w [$w index insert-1c] [$w index insert]
    }
}

## tkConBlink - blinks between n index pairs for a specified duration.
# ARGS:	w	- console text widget
# 	i1	- start index to blink region
# 	i2	- end index of blink region
# 	dur	- duration in usecs to blink for
# Outputs:	blinks selected characters in $w
## 
;proc tkConBlink {w args} {
    global TKCON
    eval $w tag add blink $args
    after $TKCON(blinktime) eval $w tag remove blink $args
    return
}


## tkConInsert
## Insert a string into a text console at the point of the insertion cursor.
## If there is a selection in the text, and it covers the point of the
## insertion cursor, then delete the selection before inserting.
# ARGS:	w	- text window in which to insert the string
# 	s	- string to insert (usually just a single char)
# Outputs:	$s to text widget
## 
;proc tkConInsert {w s} {
    if {[string match {} $s] || [string match disabled [$w cget -state]]} {
	return
    }
    if {[$w comp insert < limit]} {
	$w mark set insert end
    }
    if {[llength [$w tag ranges sel]] && \
	    [$w comp sel.first <= insert] && [$w comp sel.last >= insert]} {
	$w delete sel.first sel.last
    }
    $w insert insert $s
    $w see insert
}

## tkConExpand - 
# ARGS:	w	- text widget in which to expand str
# 	type	- type of expansion (path / proc / variable)
# Calls:	tkConExpand(Pathname|Procname|Variable)
# Outputs:	The string to match is expanded to the longest possible match.
#		If TKCON(showmultiple) is non-zero and the user longest match
#		equaled the string to expand, then all possible matches are
#		output to stdout.  Triggers bell if no matches are found.
# Returns:	number of matches found
## 
;proc tkConExpand {w {type ""}} {
    global TKCON
    set exp "\[^\\\\\]\[\[ \t\n\r\{\"\\\$\]"
    set tmp [$w search -backwards -regexp $exp insert-1c limit-1c]
    if {[string compare {} $tmp]} {append tmp +2c} else {set tmp limit}
    if {[$w compare $tmp >= insert]} return
    set str [$w get $tmp insert]
    switch -glob $type {
	pa* { set res [tkConExpandPathname $str] }
	pr* { set res [tkConExpandProcname $str] }
	v*  { set res [tkConExpandVariable $str] }
	default {
	    set res {}
	    foreach t $TKCON(expandorder) {
		if {![catch {tkConExpand$t $str} res] && \
			[string compare {} $res]} break
	    }
	}
    }
    set len [llength $res]
    if {$len} {
	$w delete $tmp insert
	$w insert $tmp [lindex $res 0]
	if {$len > 1} {
	    if {$TKCON(showmultiple) && \
		    ![string compare [lindex $res 0] $str]} {
		puts stdout [lsort [lreplace $res 0 0]]
	    }
	}
    } else { bell }
    return [incr len -1]
}

## tkConExpandPathname - expand a file pathname based on $str
## This is based on UNIX file name conventions
# ARGS:	str	- partial file pathname to expand
# Calls:	tkConExpandBestMatch
# Returns:	list containing longest unique match followed by all the
#		possible further matches
## 
;proc tkConExpandPathname str {
    set pwd [tkConEvalAttached pwd]
    if {[catch {tkConEvalAttached [list cd [file dirname $str]]} err]} {
	return -code error $err
    }
    set dir [file tail $str]
    ## Check to see if it was known to be a directory and keep the trailing
    ## slash if so (file tail cuts it off)
    if {[string match */ $str]} { append dir / }
    if {[catch {lsort [tkConEvalAttached [list glob $dir*]]} m]} {
	set match {}
    } else {
	if {[llength $m] > 1} {
	    global tcl_platform
	    if {
		[string match windows $tcl_platform(platform)] &&
		!([string match *NT* $tcl_platform(os)] && \
			[info tclversion]>8.0)
	    } {
		## Windows is screwy because it's case insensitive
		## NT for 8.1+ is case sensitive though...
		set tmp [tkConExpandBestMatch [string tolower $m] \
			[string tolower $dir]]
		## Don't change case if we haven't changed the word
		if {[string length $dir]==[string length $tmp]} {
		    set tmp $dir
		}
	    } else {
		set tmp [tkConExpandBestMatch $m $dir]
	    }
	    if {[string match ?*/* $str]} {
		set tmp [file dirname $str]/$tmp
	    } elseif {[string match /* $str]} {
		set tmp /$tmp
	    }
	    regsub -all { } $tmp {\\ } tmp
	    set match [linsert $m 0 $tmp]
	} else {
	    ## This may look goofy, but it handles spaces in path names
	    eval append match $m
	    if {[file isdir $match]} {append match /}
	    if {[string match ?*/* $str]} {
		set match [file dirname $str]/$match
	    } elseif {[string match /* $str]} {
		set match /$match
	    }
	    regsub -all { } $match {\\ } match
	    ## Why is this one needed and the ones below aren't!!
	    set match [list $match]
	}
    }
    tkConEvalAttached [list cd $pwd]
    return $match
}

## tkConExpandProcname - expand a tcl proc name based on $str
# ARGS:	str	- partial proc name to expand
# Calls:	tkConExpandBestMatch
# Returns:	list containing longest unique match followed by all the
#		possible further matches
##
;proc tkConExpandProcname str {
    global TKCON
    set match [tkConEvalAttached [list info commands $str*]]
    if {[llength $match] == 0 && $TKCON(A:namespace)} {
	if {$TKCON(A:itcl2)} {
	    ## They are [incr Tcl] namespaces
	    set ns [tkConEvalAttached [list info namespace all $str*]]
	    if {[llength $ns]==1} {
		foreach p [tkConEvalAttached \
			[list namespace $ns { ::info procs }]] {
		    lappend match ${ns}::$p
		}
	    } else {
		set match $ns
	    }
	} else {
	    ## They are Tk8 namespaces
	    set ns [tkConEvalAttached namespace children \
		    {[namespace current]} [list $str]*]
	    if {[llength $ns]==1} {
		set match [tkConEvalAttached [list info commands ${ns}::*]]
	    } else {
		set match $ns
	    }
	}
    }
    if {[llength $match] > 1} {
	regsub -all { } [tkConExpandBestMatch $match $str] {\\ } str
	set match [linsert $match 0 $str]
    } else {
	regsub -all { } $match {\\ } match
    }
    return $match
}

## tkConExpandVariable - expand a tcl variable name based on $str
# ARGS:	str	- partial tcl var name to expand
# Calls:	tkConExpandBestMatch
# Returns:	list containing longest unique match followed by all the
#		possible further matches
## 
;proc tkConExpandVariable str {
    if {[regexp {([^\(]*)\((.*)} $str junk ary str]} {
	## Looks like they're trying to expand an array.
	set match [tkConEvalAttached [list array names $ary $str*]]
	if {[llength $match] > 1} {
	    set vars $ary\([tkConExpandBestMatch $match $str]
	    foreach var $match {lappend vars $ary\($var\)}
	    return $vars
	} else {set match $ary\($match\)}
	## Space transformation avoided for array names.
    } else {
	set match [tkConEvalAttached [list info vars $str*]]
	if {[llength $match] > 1} {
	    regsub -all { } [tkConExpandBestMatch $match $str] {\\ } str
	    set match [linsert $match 0 $str]
	} else {
	    regsub -all { } $match {\\ } match
	}
    }
    return $match
}

## tkConExpandBestMatch2 - finds the best unique match in a list of names
## Improves upon the speed of the below proc only when $l is small
## or $e is {}.  $e is extra for compatibility with proc below.
# ARGS:	l	- list to find best unique match in
# Returns:	longest unique match in the list
## 
;proc tkConExpandBestMatch2 {l {e {}}} {
    set s [lindex $l 0]
    if {[llength $l]>1} {
	set i [expr {[string length $s]-1}]
	foreach l $l {
	    while {$i>=0 && [string first $s $l]} {
		set s [string range $s 0 [incr i -1]]
	    }
	}
    }
    return $s
}

## tkConExpandBestMatch - finds the best unique match in a list of names
## The extra $e in this argument allows us to limit the innermost loop a
## little further.  This improves speed as $l becomes large or $e becomes long.
# ARGS:	l	- list to find best unique match in
# 	e	- currently best known unique match
# Returns:	longest unique match in the list
## 
;proc tkConExpandBestMatch {l {e {}}} {
    set ec [lindex $l 0]
    if {[llength $l]>1} {
	set e  [string length $e]; incr e -1
	set ei [string length $ec]; incr ei -1
	foreach l $l {
	    while {$ei>=$e && [string first $ec $l]} {
		set ec [string range $ec 0 [incr ei -1]]
	    }
	}
    }
    return $ec
}

# Here is a group of functions that is only used when Tkcon is
# executed in a safe interpreter. It provides safe versions of
# missing functions. For example:
#
# - "tk appname" returns "tkcon.tcl" but cannot be set
# - "toplevel" is equivalent to 'frame', only it is automatically
#   packed.
# - The 'source', 'load', 'open', 'file' and 'exit' functions are
#   mapped to corresponding functions in the parent interpreter.
#
# Further on, Tk cannot be really loaded. Still the safe 'load'
# provedes a speciall case. The Tk can be divided into 4 groups,
# that each has a safe handling procedure.
#
# - "tkConSafeItem" handles commands like 'button', 'canvas' ......
#   Each of these functions has the window name as first argument.
# - "tkConSafeManage" handles commands like 'pack', 'place', 'grid',
#   'winfo', which can have multiple window names as arguments.
# - "tkConSafeWindow" handles all windows, such as '.'. For every
#   window created, a new alias is formed which also is handled by
#   this function.
# - Other (e.g. bind, bindtag, image), which need their own function.
#
## These functions courtesy Jan Nijtmans (nijtmans@nici.kun.nl)
##
if {[string compare [info command tk] tk]} {
    ;proc tk {option args} {
	if {![string match app* $option]} {
	    error "wrong option \"$option\": should be appname"
	}
	return "tkcon.tcl"
    }
}

if {[string compare [info command toplevel] toplevel]} {
    ;proc toplevel {name args} {
	eval frame $name $args
	pack $name
    }
}

;proc tkConSafeSource {i f} {
    set fd [open $f r]
    set r [read $fd]
    close $fd
    if {[catch {interp eval $i $r} msg]} {
	error $msg
    }
}

;proc tkConSafeOpen {i f {m r}} {
    set fd [open $f $m]
    interp transfer {} $fd $i
    return $fd
}

;proc tkConSafeLoad {i f p} {
    global tk_version tk_patchLevel tk_library auto_path
    if {[string compare $p Tk]} {
	load $f $p $i
    } else {
	foreach command {button canvas checkbutton entry frame label
	listbox message radiobutton scale scrollbar text toplevel} {
	    $i alias $command tkConSafeItem $i $command
	}
	$i alias image tkConSafeImage $i
	foreach command {pack place grid destroy winfo} {
	    $i alias $command tkConSafeManage $i $command
	}
	if {[llength [info command event]]} {
	    $i alias event tkConSafeManage $i $command
	}
	frame .${i}_dot -width 300 -height 300 -relief raised
	pack .${i}_dot -side left
	$i alias tk tk
	$i alias bind tkConSafeBind $i
	$i alias bindtags tkConSafeBindtags $i
	$i alias . tkConSafeWindow $i {}
	foreach var {tk_version tk_patchLevel tk_library auto_path} {
	    $i eval set $var [list [set $var]]
	}
	$i eval {
	    package provide Tk $tk_version
	    if {[lsearch -exact $auto_path $tk_library] < 0} {
		lappend auto_path $tk_library
	    }
	}
	return ""
    }
}

;proc tkConSafeSubst {i a} {
    set arg1 ""
    foreach {arg value} $a {
	if {![string compare $arg -textvariable] ||
	![string compare $arg -variable]} {
	    set newvalue "[list $i] $value"
	    global $newvalue
	    if {[interp eval $i info exists $value]} {
		set $newvalue [interp eval $i set $value]
	    } else {
		catch {unset $newvalue}
	    }
	    $i eval trace variable $value rwu \{[list tkcon set $newvalue $i]\}
	    set value $newvalue
	} elseif {![string compare $arg -command]} {
	    set value [list $i eval $value]
	}
	lappend arg1 $arg $value
    }
    return $arg1
}

;proc tkConSafeItem {i command w args} {
    set args [tkConSafeSubst $i $args]
    set code [catch "$command [list .${i}_dot$w] $args" msg]
    $i alias $w tkConSafeWindow $i $w
    regsub -all .${i}_dot $msg {} msg
    return -code $code $msg
}

;proc tkConSafeManage {i command args} {
    set args1 ""
    foreach arg $args {
	if {[string match . $arg]} {
	    set arg .${i}_dot
	} elseif {[string match .* $arg]} {
	    set arg ".${i}_dot$arg"
	}
	lappend args1 $arg
    }
    set code [catch "$command $args1" msg]
    regsub -all .${i}_dot $msg {} msg
    return -code $code $msg
}

#
# FIX: this function doesn't work yet if the binding starts with '+'.
#
;proc tkConSafeBind {i w args} {
    if {[string match . $w]} {
	set w .${i}_dot
    } elseif {[string match .* $w]} {
	set w ".${i}_dot$w"
    }
    if {[llength $args] > 1} {
	set args [list [lindex $args 0] \
		"[list $i] eval [list [lindex $args 1]]"]
    }
    set code [catch "bind $w $args" msg]
    if {[llength $args] <2 && $code == 0} {
	set msg [lindex $msg 3]
    }
    return -code $code $msg
}

;proc tkConSafeImage {i option args} {
    set code [catch "image $option $args" msg]
    if {[string match cr* $option]} {
	$i alias $msg $msg
    }
    return -code $code $msg
}

;proc tkConSafeBindtags {i w {tags {}}} {
    if {[string match . $w]} {
	set w .${i}_dot
    } elseif {[string match .* $w]} {
	set w ".${i}_dot$w"
    }
    set newtags {}
    foreach tag $tags {
	if {[string match . $tag]} {
	    lappend newtags .${i}_dot
	} elseif {[string match .* $tag]} {
	    lappend newtags ".${i}_dot$tag"
	} else {
	    lappend newtags $tag
	}
    }
    if {[string match $tags {}]} {
	set code [catch {bindtags $w} msg]
	regsub -all \\.${i}_dot $msg {} msg
    } else {
	set code [catch {bindtags $w $newtags} msg]
    }
    return -code $code $msg
}

;proc tkConSafeWindow {i w option args} {
    if {[string match conf* $option] && [llength $args] > 1} {
	set args [tkConSafeSubst $i $args]
    } elseif {[string match itemco* $option] && [llength $args] > 2} {
	set args "[list [lindex $args 0]] [tkConSafeSubst $i [lrange $args 1 end]]"
    } elseif {[string match cr* $option]} {
	if {[llength $args]%2} {
	    set args "[list [lindex $args 0]] [tkConSafeSubst $i [lrange $args 1 end]]"
	} else {
	    set args [tkConSafeSubst $i $args]
	}
    } elseif {[string match bi* $option] && [llength $args] > 2} {
	set args [list [lindex $args 0] [lindex $args 1] "[list $i] eval [list [lindex $args 2]]"]
    }
    set code [catch ".${i}_dot$w $option $args" msg]
    if {$code} {
	regsub -all .${i}_dot $msg {} msg
    } elseif {[string match conf* $option] || [string match itemco* $option]} {
	if {[llength $args] == 1} {
	    switch -- $args {
		-textvariable - -variable {
		    set msg "[lrange $msg 0 3] [list [lrange [lindex $msg 4] 1 end]]"
		}
		-command - updatecommand {
		    set msg "[lrange $msg 0 3] [list [lindex [lindex $msg 4] 2]]"
		}
	    }
	} elseif {[llength $args] == 0} {
	    set args1 ""
	    foreach el $msg {
		switch -- [lindex $el 0] {
		    -textvariable - -variable {
			set el "[lrange $el 0 3] [list [lrange [lindex $el 4] 1 end]]"
		    }
		    -command - updatecommand {
			set el "[lrange $el 0 3] [list [lindex [lindex $el 4] 2]]"
		    }
		}
		lappend args1 $el
	    }
	    set msg $args1
	}
    } elseif {[string match cg* $option] || [string match itemcg* $option]} {
	switch -- $args {
	    -textvariable - -variable {
		set msg [lrange $msg 1 end]
	    }
	    -command - updatecommand {
		set msg [lindex $msg 2]
	    }
	}
    } elseif {[string match bi* $option]} {
	if {[llength $args] == 2 && $code == 0} {
	    set msg [lindex $msg 2]
	}
    }
    return -code $code $msg
}

## tkConResource - re'source's this script into current console
## Meant primarily for my development of this program.  It follows
## links until the ultimate source is found.
## 
set TKCON(SCRIPT) [info script]
if {!$TKCON(WWW) && [string compare $TKCON(SCRIPT) {}]} {
    while {[string match link [file type $TKCON(SCRIPT)]]} {
	set link [file readlink $TKCON(SCRIPT)]
	if {[string match relative [file pathtype $link]]} {
	    set TKCON(SCRIPT) [file join [file dirname $TKCON(SCRIPT)] $link]
	} else {
	    set TKCON(SCRIPT) $link
	}
    }
    catch {unset link}
    if {[string match relative [file pathtype $TKCON(SCRIPT)]]} {
	set TKCON(SCRIPT) [file join [pwd] $TKCON(SCRIPT)]
    }
}

;proc tkConResource {} {
    global TKCON
    uplevel \#0 {if [catch {source -rsrc tkcon}] {source $TKCON(SCRIPT)}}
    tkConBindings
    tkConInitSlave $TKCON(exec)
}

## Initialize only if we haven't yet
##
if {[catch {winfo exists $TKCON(root)}]} tkConInit
