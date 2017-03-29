# Microsoft Developer Studio Project File - Name="gt_base" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 5.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Static Library" 0x0104

CFG=gt_base - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "gt_base.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "gt_base.mak" CFG="gt_base - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "gt_base - Win32 Release" (based on "Win32 (x86) Static Library")
!MESSAGE "gt_base - Win32 Debug" (based on "Win32 (x86) Static Library")
!MESSAGE 

# Begin Project
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe

!IF  "$(CFG)" == "gt_base - Win32 Release"

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
# ADD CPP /nologo /W3 /GX /O2 /I "..\..\..\GTL\include" /I "..\..\src" /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /YX /FD /c
# SUBTRACT CPP /Fr
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LIB32=link.exe -lib
# ADD BASE LIB32 /nologo
# ADD LIB32 /nologo /out:"..\Release\gt_base.lib"

!ELSEIF  "$(CFG)" == "gt_base - Win32 Debug"

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
# ADD CPP /nologo /W3 /GX /Z7 /Od /I "..\..\..\GTL\include" /I "..\..\src" /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /YX /FD /c
# SUBTRACT CPP /Fr
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LIB32=link.exe -lib
# ADD BASE LIB32 /nologo
# ADD LIB32 /nologo /out:"..\Debug\gt_base.lib"

!ENDIF 

# Begin Target

# Name "gt_base - Win32 Release"
# Name "gt_base - Win32 Debug"
# Begin Source File

SOURCE=..\..\src\gt_base\Algorithm.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Algorithm.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Attribute_Base.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Attribute_Base.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Attribute_double.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Attribute_double.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Attribute_int.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Attribute_int.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Attribute_list.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Attribute_list.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Attribute_string.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Attribute_string.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Attributes.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Attributes.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Circle.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Circle.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Common_Attributes.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Common_Attributes.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Common_Graphics.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Common_Graphics.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\config.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Device.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Device.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Edge_Attributes.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Edge_Attributes.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\edge_NEI.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\edge_NEI.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Error.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Error.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\GML.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\GML.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\gml_parser.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\gml_parser.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Graph.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Graph.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Graph_Attributes.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Graph_Attributes.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Graph_handlers.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Graph_handlers.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Graphlet.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Graphlet.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\gt_base.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\gt_base.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\GTL_Shuttle.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\GTL_Shuttle.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Id.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Id.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Key.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Key.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Key_description.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Key_description.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Keymapper.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Keymapper.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Keys.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Keys.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Line.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Line.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\List_of_Attributes.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\List_of_Attributes.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\NEI.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\NEI.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Node_Attributes.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Node_Attributes.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\node_NEI.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\node_NEI.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Parser.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Parser.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Point.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Point.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Polyline.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Polyline.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Port.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Port.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Ports.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Ports.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Rectangle.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Rectangle.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Segment.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Segment.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Shuttle.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Shuttle.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Tagged_Attributes.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\Tagged_Attributes.h
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\UIObject.cpp
# End Source File
# Begin Source File

SOURCE=..\..\src\gt_base\UIObject.h
# End Source File
# End Target
# End Project
