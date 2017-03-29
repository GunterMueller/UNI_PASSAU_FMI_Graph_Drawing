# Microsoft Developer Studio Project File - Name="GTL" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 5.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Static Library" 0x0104

CFG=GTL - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "GTL.MAK".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "GTL.MAK" CFG="GTL - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "GTL - Win32 Release" (based on "Win32 (x86) Static Library")
!MESSAGE "GTL - Win32 Debug" (based on "Win32 (x86) Static Library")
!MESSAGE 

# Begin Project
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe

!IF  "$(CFG)" == "GTL - Win32 Release"

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
# ADD CPP /nologo /W3 /GX /O2 /I "..\..\..\GTL\include" /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /YX /FD /c
# SUBTRACT CPP /Fr
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LIB32=link.exe -lib
# ADD BASE LIB32 /nologo
# ADD LIB32 /nologo /out:"..\Release\GTL.lib"

!ELSEIF  "$(CFG)" == "GTL - Win32 Debug"

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
# ADD CPP /nologo /W3 /GX /Z7 /Od /I "..\..\..\GTL\include" /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /YX /FD /c
# SUBTRACT CPP /Fr
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LIB32=link.exe -lib
# ADD BASE LIB32 /nologo
# ADD LIB32 /nologo /out:"..\Debug\GTL.lib"

!ENDIF 

# Begin Target

# Name "GTL - Win32 Release"
# Name "GTL - Win32 Debug"
# Begin Source File

SOURCE=..\..\..\GTL\src\bfs.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\biconnectivity.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\debug.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\dfs.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\edge.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\embedding.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\gml_parser.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\gml_scanner.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\graph.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\node.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\planarity.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\pq_node.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\pq_tree.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\st_number.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\GTL\src\topsort.cpp
# End Source File
# End Target
# End Project
