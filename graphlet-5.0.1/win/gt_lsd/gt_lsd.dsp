# Microsoft Developer Studio Project File - Name="gt_lsd" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 5.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Static Library" 0x0104

CFG=gt_lsd - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "gt_lsd.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "gt_lsd.mak" CFG="gt_lsd - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "gt_lsd - Win32 Release" (based on "Win32 (x86) Static Library")
!MESSAGE "gt_lsd - Win32 Debug" (based on "Win32 (x86) Static Library")
!MESSAGE 

# Begin Project
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe

!IF  "$(CFG)" == "gt_lsd - Win32 Release"

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
# ADD CPP /nologo /W3 /GR- /GX /O2 /I "..\..\..\GTL\include" /I "..\..\src" /I "e:\Programs\Tcl\include" /I "..\..\src\gt_lsd" /D "NDEBUG" /D "WIN32" /D "_WINDOWS" /D "_LSD" /YX /FD /c
# SUBTRACT CPP /Fr
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LIB32=link.exe -lib
# ADD BASE LIB32 /nologo
# ADD LIB32 /nologo /out:"..\Release\gt_lsd.lib"

!ELSEIF  "$(CFG)" == "gt_lsd - Win32 Debug"

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
# ADD CPP /nologo /W3 /GR- /GX /Z7 /Od /I "..\..\..\GTL\include" /I "..\..\src" /I "e:\Programs\Tcl\include" /I "..\..\src\gt_lsd" /D "_DEBUG" /D "WIN32" /D "_WINDOWS" /D "_LSD" /YX /FD /c
# SUBTRACT CPP /Fr
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LIB32=link.exe -lib
# ADD BASE LIB32 /nologo
# ADD LIB32 /nologo /out:"..\Debug\gt_lsd.lib"

!ENDIF 

# Begin Target

# Name "gt_lsd - Win32 Release"
# Name "gt_lsd - Win32 Debug"
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\algorithms.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\algs_imp.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\attrs.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\dispatch_commands.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\ge_dummy.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\ge_dummy.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\graphed.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\graphed.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\graphed_structures.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\gt_lsd.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\gt_lsd.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\leda_sle.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\ls_assoc.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\ls_assoc.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\lsd.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\lsd.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\lsd_mref.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\lsd_mref.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\lsdstd.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\random.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\salpha.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\sedge.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\sembed.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\sgragra.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\sgragra.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\sgragra_interface.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\sgraph.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\sgraph_.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\sgraph_interface.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\slist.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\slist.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\snode.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\sprod.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\std.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\std.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\trace.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\lsd\trace.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\utils_move_rotate.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_lsd\sgraph\utils_move_rotate.h
# End Source File
# End Target
# End Project
