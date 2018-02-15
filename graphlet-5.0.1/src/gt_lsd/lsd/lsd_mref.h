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
// Meta-reference-objects store the information needed
// to associate corresponding LEDA- and Sgraph-parts.
// Refer to class LEDA_Sgraph_association (ls_assoc.h)
// to get more information how and where meta-references
// are used.
//
///////////////////////////////////////////////////////////

#ifndef LSD_MREF_H
#define LSD_MREF_H

// forward-declaration
class LSD;

// LSD-standard includes
#include <lsd/lsdstd.h>

template <class STYPE, class TTYPE>
class LSD_Meta_Reference
{
	///////////////////////////////////////////////////////
	// methods
		   
  public:
	
	///////////////////////////////////////////////////////
	// constructors & destructors

	LSD_Meta_Reference(LSD *lsd_object, STYPE sobject, TTYPE tobject)
	{
		// sobject or tobject or lsd_object == 0?
		assert(lsd_object);
		assert(sobject);
		// MR: Sorry, funktioniert nicht wenn TTYPE = node ist
		assert(tobject != TTYPE());
	
		the_lsd = lsd_object;
		the_backref = sobject;
		the_ref = tobject;
	}

	virtual ~LSD_Meta_Reference();
	
	///////////////////////////////////////////////////////
	// service-methods

	TTYPE ref(void) { return the_ref; }
	STYPE backref(void) { return the_backref; }
	LSD* lsd(void) { return the_lsd; }
	
  private:

	///////////////////////////////////////////////////////
	// declare a assign- & a copy-operator as private methods
	// but dont define them - so it is impossible to missuse
	// default-operators created by the compiler.

	
	const LSD_Meta_Reference<STYPE, TTYPE>& operator= (
		const LSD_Meta_Reference<STYPE, TTYPE>& lsd_mref_object);
	
	LSD_Meta_Reference<STYPE, TTYPE>(
		const LSD_Meta_Reference<STYPE, TTYPE>& lsd_mref_object);

	///////////////////////////////////////////////////////
	// members
				
  private:

	TTYPE     the_ref;
	STYPE     the_backref;
	LSD*      the_lsd;
};


//
// Moved to header and deleted TRACE since g++ has problems with
// templates in .cc files.
//
// Michael Himsolt, 6/30/96
//

template <class STYPE, class TTYPE>
LSD_Meta_Reference<STYPE, TTYPE>::~LSD_Meta_Reference()
{
	// TRACE("DELETE MREF (" << --count_refs << ")");
}



class LSD_Meta_Edge_Reference : public LSD_Meta_Reference<Sedge, edge>
{
	///////////////////////////////////////////////////////
	// methods
		   
  public:
	
	///////////////////////////////////////////////////////
	// constructors & destructors
		   
	LSD_Meta_Edge_Reference(LSD* lsd_object, Sedge sobject,
		edge tobject);
	virtual ~LSD_Meta_Edge_Reference();

	///////////////////////////////////////////////////////
	// service-methods
		
	Edgeline edgeline(void) { return the_el; }
	void edgeline(Edgeline a_el);

	Snode real_source(void) { return the_real_source; }
	Snode real_target(void) { return the_real_target; }

  private:

	///////////////////////////////////////////////////////
	// declare a assign- & a copy-operator as private methods
	// but dont define them - so it is impossible to missuse
	// default-operators created by the compiler.
	
	const LSD_Meta_Edge_Reference& operator= (
		const LSD_Meta_Edge_Reference& lsd_mref_object);
	
	LSD_Meta_Edge_Reference(
		const LSD_Meta_Edge_Reference& lsd_mref_object);

	///////////////////////////////////////////////////////
	// members:
		
  private:

	// the edgeline of the edge
		   
	Edgeline the_el;

	// the real source and target of the edge.
	// these are importent, since the edgeline
    // induces a direction by the order of its
	// coordinates  

	Snode the_real_source;
	Snode the_real_target;
};

typedef LSD_Meta_Reference<Snode, node> NodeRef;
typedef LSD_Meta_Edge_Reference         EdgeRef;

#endif // LSD_MREF_H







