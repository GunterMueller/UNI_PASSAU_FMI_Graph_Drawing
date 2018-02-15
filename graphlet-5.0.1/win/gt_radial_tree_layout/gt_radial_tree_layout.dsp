# Microsoft Developer Studio Project File - Name="gt_radial_tree_layout" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 5.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Static Library" 0x0104

CFG=gt_radial_tree_layout - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "gt_radial_tree_layout.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "gt_radial_tree_layout.mak"\
 CFG="gt_radial_tree_layout - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "gt_radial_tree_layout - Win32 Release" (based on\
 "Win32 (x86) Static Library")
!MESSAGE "gt_radial_tree_layout - Win32 Debug" (based on\
 "Win32 (x86) Static Library")
!MESSAGE 

# Begin Project
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe

!IF  "$(CFG)" == "gt_radial_tree_layout - Win32 Release"

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
# ADD LIB32 /nologo /out:"..\Release\gt_radial_tree_layout.lib"

!ELSEIF  "$(CFG)" == "gt_radial_tree_layout - Win32 Debug"

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
# ADD LIB32 /nologo /out:"..\Debug\gt_radial_tree_layout.lib"

!ENDIF 

# Begin Target

# Name "gt_radial_tree_layout - Win32 Release"
# Name "gt_radial_tree_layout - Win32 Debug"
# Begin Source File

SOURCE=..\..\src\gt_radial_tree_layout\gt_radial_tree_layout.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_radial_tree_layout\gt_radial_tree_layout.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_radial_tree_layout\radial_auxiliary.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_radial_tree_layout\radial_auxiliary.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_radial_tree_layout\radial_tree_layout.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_radial_tree_layout\radial_tree_layout.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_radial_tree_layout\radial_tree_layout_algorithm.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_radial_tree_layout\radial_tree_layout_algorithm.h
# End Source File
# End Target
# End Project
