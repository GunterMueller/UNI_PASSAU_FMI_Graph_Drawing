# Microsoft Developer Studio Project File - Name="gt_tcl" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 5.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Static Library" 0x0104

CFG=gt_tcl - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "gt_tcl.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "gt_tcl.mak" CFG="gt_tcl - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "gt_tcl - Win32 Release" (based on "Win32 (x86) Static Library")
!MESSAGE "gt_tcl - Win32 Debug" (based on "Win32 (x86) Static Library")
!MESSAGE 

# Begin Project
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe

!IF  "$(CFG)" == "gt_tcl - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /YX /FD /c
# ADD CPP /nologo /W3 /GR- /GX /O2 /I "..\..\..\GTL\include" /I "..\..\src" /I "e:\Programs\Tcl\include" /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /YX /FD /c
# SUBTRACT CPP /Fr
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LIB32=link.exe -lib
# ADD BASE LIB32 /nologo
# ADD LIB32 /nologo /out:"..\Release\gt_tcl.lib"

!ELSEIF  "$(CFG)" == "gt_tcl - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /GX /Z7 /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /YX /FD /c
# ADD CPP /nologo /W3 /GR- /GX /Z7 /Od /I "..\..\..\GTL\include" /I "..\..\src" /I "e:\Programs\Tcl\include" /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /YX /FD /c
# SUBTRACT CPP /Fr
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LIB32=link.exe -lib
# ADD BASE LIB32 /nologo
# ADD LIB32 /nologo /out:"..\Debug\gt_tcl.lib"

!ENDIF 

# Begin Target

# Name "gt_tcl - Win32 Release"
# Name "gt_tcl - Win32 Debug"
# Begin Source File

SOURCE=..\..\src\gt_tcl\Graphscript.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Graphscript.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\gt_tcl.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\gt_tcl.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Algorithm.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Algorithm.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Command.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Command.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Edge.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Edge.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Graph.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Graph.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Graph_configure.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Graph_configure.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Graph_handlers.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Graph_handlers.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Info.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Info.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Node.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Node.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Rotate_Command.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Rotate_Command.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Scale_Command.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tcl_Scale_Command.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tk_Device.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tk_Device.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tk_UIEdge.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tk_UIEdge.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tk_UIGraph.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tk_UIGraph.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tk_UILabel.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tk_UILabel.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tk_UINode.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tk_UINode.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tk_UIObject.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_tcl\Tk_UIObject.h
# End Source File
# End Target
# End Project
