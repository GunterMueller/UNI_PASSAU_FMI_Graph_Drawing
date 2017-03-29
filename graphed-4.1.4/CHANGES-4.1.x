GraphEd 4.1 - 4.1.x changes :
=============================

4.1.3 - 4.1.4:

- Bugfix in fileselectors directory-reading
- Bugfix in some paint-procedures
- Install convertes/simple2graphed to INSTALL_BINDIR

4.1.2 - 4.1.3:

- Reviewed include-files and ANSI-prototyping
- Bugfix in Pixmap-node Postscript export
- Introduced "outlining" of Fonts in Postscript export

4.1.1 - 4.1.2:

- Introduced gcc-option -pedantic -Wall
  resulting in many minor code changes and bug-fixes


4.1 - 4.1.1:

- Bug fixed in loading undirected graphs:
  graphed/ggraph.c, set_graph_directedness, line 291, inserted missing
  parameter for set_graph_directed:
  set_graph_directed (graph->sgraph_graph, directed);
- minor menu-name changes.
