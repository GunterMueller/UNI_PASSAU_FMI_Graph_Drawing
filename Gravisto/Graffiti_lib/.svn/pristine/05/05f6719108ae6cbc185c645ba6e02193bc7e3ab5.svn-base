@echo off
java -classpath c:\Data\Uni\HiWiJob\ps-lib\lib\jlex_java_cup.jar JLex.Main gml.lex

copy gml.lex.java Yylex.java

del gml.lex.java

java -classpath c:\Data\Uni\HiWiJob\ps-lib\lib\jlex_java_cup.jar -ea java_cup.Main gml.cup

javac -classpath ..\..\..\build\classes -source 1.4 -d ..\..\..\build\classes GMLReaderPlugin.java

javac -classpath c:\Data\Uni\HiWiJob\ps-lib\lib\jlex_java_cup.jar;C:\Progs\Programming\eclipse\workspace\Graffiti\build\classes -source 1.4 -d ..\..\..\build\classes sym.java parser.java Yylex.java
