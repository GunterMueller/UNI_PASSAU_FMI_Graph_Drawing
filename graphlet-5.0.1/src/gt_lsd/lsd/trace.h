/* This software is distributed under the Lesser General Public License */
// 
// L      S      D
// Leda & Sgraph Do it
//
// an interface to run
// Sgraph-alorithms on LEDA-graph-structures

// Author: Dirk Heider
// email: heider@fmi.uni-passau.de

///////////////////////////////////////////////////////////
// MODULE DESCRIPTION
//
// Class & macros to support tracing & debugging.
// DEEP supports indentation of nested functioncalls.
//
///////////////////////////////////////////////////////////

// two simple macros for debug purpose:
// ENTRY displays "-> <functionname>"
// LEAVE displays "<functionname> -|"
// deep indents the output.

class DEEP
{
  public:
	
	friend ostream& operator<< (ostream& out, DEEP& deep_object);

	void inc(void) { ++deepness; }
	void dec(void) { --deepness; }

  private:

	int deepness;
};

extern DEEP deep;
extern bool my_trace;
extern bool my_fct_trace;


#define ENTRY { if (my_fct_trace) {				\
	cout << deep << "-> "						\
		 << "dummy"                             \
		 << endl;								\
	deep.inc(); } }

#define LEAVE { if (my_fct_trace) {				\
	deep.dec();									\
	cout << deep								\
		 << "dummy"                             \
		 << " -|" << endl; } }

#define TRACE(message) { if (my_trace) {		\
	cout << deep << "TRACE: "					\
		 << message << endl; } }
