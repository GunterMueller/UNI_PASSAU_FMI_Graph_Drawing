INCLUDES = -I$(top_srcdir)/include 
check_PROGRAMS  = bellman_ford_test graph_test
bellman_ford_test_SOURCES = bellman_ford_test.cpp 
bellman_ford_test_LDADD = $(top_builddir)/src/libGTL.la
graph_test_SOURCES = graph_test.cpp
graph_test_LDADD = $(top_builddir)/src/libGTL.la

runtests : bellman_ford_test graph_test
	./bellman_ford_test
	./graph_test
