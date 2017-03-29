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
// (see headerfile)
//
///////////////////////////////////////////////////////////

// LSD-standard includes
#include "lsdstd.h"
#include <gt_base/NEI.h>

int wac_buffer=0;

////////////////////////////////////////////////////////////////////////////
//

void	call_sgraph_proc (void (*proc) (), char* user_args)
{
    ENTRY;
//     cout << "call_sgraph_proc() is obsolete " << endl
// 	 << "Use LSD::callSgraph() instead!" << endl;
    assert(proc);
    assert(user_args);
    assert( 1 == 2 );
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
//

int bell (void)
{
    ENTRY;
    TRACE("... when the postman rings twice ...");
    TRACE("... function bell() was called ...");
    return 0;
    LEAVE;
}
	
////////////////////////////////////////////////////////////////////////////
//
/*
void fatal_error (char* format, ...)
{
    ENTRY;
    cerr << "ERROR : ";
	
    char	buffer[1000];
    va_list ap;
	
    va_start (ap, format);
	
    vsprintf (buffer, format, ap);
    cerr << buffer << endl;
	
    va_end (format);
    abort();
    LEAVE;
}
*/
////////////////////////////////////////////////////////////////////////////
//
/*
void error (char* format, ...)
{
    ENTRY;
    cerr << "ERROR : ";
	
    char	buffer[1000];
    va_list ap;
	
    va_start (ap, format);
	
    vsprintf (buffer, format, ap);
    cerr << buffer << endl;
	
    va_end (format);
    LEAVE;
}
*/

////////////////////////////////////////////////////////////////////////////
//

void warning (char* format, ...)
{
    ENTRY;
    cerr << "Warning : ";
	
    char	buffer[1000];
    va_list ap;
	
    va_start (ap, format);
	
    vsprintf (buffer, format, ap);
    cerr << buffer << endl;
	
    va_end (format);
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
//

void message (char* format, ...)
{
    ENTRY;
    cerr << "Message : ";
	
    char	buffer[1000];
    va_list ap;
	
    va_start (ap, format);
	
    vsprintf (buffer, format, ap);
    cerr << buffer << endl;
	
    va_end (format);
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
//

char* dispatch_user_action (User_action action, ...)
{
    ENTRY;
    // it's just a dummy   
    TRACE("Function \'dispatch_user_action()\' "
	<< "is a (dummy function)" << endl
	<< "requested action: "
	<< int(action));
	
    return 0;
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// WA CH Muesste ueberfluessig sein ???

void force_repainting (void)
{	
    ENTRY;
    // it's just a dummy   
    TRACE("Function \'force_repainting()\' does nothing "
	<< "(dummy-function)");
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// WA CH Was machen wir nun statt dessen? Automatisch einen Knoten
//       erzeugen mag ich nicht!

Graphed_node create_graphed_node_from_snode (Snode snode)
{
    ENTRY;
    // Annotation:
    // creation of LEDA-counterparts from Snodes generated
    // by a running Sgraph-algorithm is automaticly done
    // by LSD via the Sgraphs make_node_proc function.
    // (refer also to class LSD)
    
    
    NodeRef* meta_ref = (NodeRef*) snode->graphed;
    node* tmp = new node (meta_ref->ref());
    // get the LEDA-counterpart via the meta-reference
    return (char*) tmp;
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// WA CH Was machen wir nun statt dessen? Automatisch einen Kante
//       erzeugen mag ich nicht!

Graphed_edge create_graphed_edge_from_sedge (Sedge sedge)
{
    ENTRY;
    // Annotation:
    // creation of LEDA-counterparts from Sedges generated
    // by a running Sgraph-algorithm is automaticly done
    // by LSD via the Sgraphs make_edge_proc function.
    // (refer also to class LSD)

    EdgeRef* meta_ref = (EdgeRef*) sedge->graphed;
    
    // get the LEDA-counterpart via the meta-reference
    edge* tmp = new edge (meta_ref->ref());
    return (char*) tmp;
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
//

Graphed_group create_graphed_group_from_slist (Slist slist)
{
    ENTRY;
	
    list<node>* nodelist = new list<node>;
	
    Slist sitem;
    Snode snode;
	
    for_slist (slist, sitem)
	{
	    snode = (Snode) attr_data(sitem);

	    NodeRef* node_ref = (NodeRef*) snode->graphed;

	    // node_ref == 0?
	    assert(node_ref);

	    LSD* act_lsd = node_ref->lsd();

	    // act_lsd == 0?
	    assert(act_lsd);
		
	    node lnode = act_lsd->get_associated_lnode(snode);

	    // lnode == 0?
	    // assert(lnode);
		
	    nodelist->push_back(lnode);
		
	} end_for_slist (slist, sitem);

	LEAVE;
	
	return (char*) nodelist;
}

////////////////////////////////////////////////////////////////////////////
//

void free_group (Graphed_group group)
{
    ENTRY;
    list<node>*  nodelist = (list<node>*) group;

    nodelist->erase(nodelist->begin(), nodelist->end());
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
//

void group_set (Graphed_group /*group*/, ...)
{
    ENTRY;
    // it's just a dummy   
    TRACE("Function \'group_set()\' does nothing "
	<< "(dummy-function)");
    // assert(group=group);
    LEAVE;
	
}

/////////////////////////////////////////////
// allgemeine GraphEd-Funktionen
/////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
//

int test_graph_is_drawn_planar (Graph /*graph*/)
{
    ENTRY;
    // it's just a dummy   
    TRACE("CAUTION!!! function \'test_graph_is_drawn_planar()\' "
	<< "always returns FALSE (dummy-function)");
    // assert(graph=graph);
    LEAVE;
    return FALSE;
}

////////////////////////////////////////////////////////////////////////////
//

int get_gridwidth(int buffer)
{
    ENTRY;
    // prevent "unused variable XXX"-message:
    int tmp_buff = buffer;
    buffer = tmp_buff;
	
    LEAVE;
    return LSD::gridwidth();
}

////////////////////////////////////////////////////////////////////////////
//

int get_current_node_width(void)
{
    ENTRY;
    LEAVE;
    return LSD::current_node_width();
}

////////////////////////////////////////////////////////////////////////////
//

int get_current_node_height(void)
{
    ENTRY;
    LEAVE;
    return LSD::current_node_height();
}


/////////////////////////////////////////////
// Funktionen zur Ermittlung von Knoten-Attributen
/////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96 

int node_height(Node gnode)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();

    LEAVE;
    return (int(act_lsd->gt_graph()->gt(lnode).graphics()->h()));
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

int node_width(Node gnode)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();

    LEAVE;
    return (int(act_lsd->gt_graph()->gt(lnode).graphics()->w()));
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

int node_x(Node gnode)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();

    LEAVE;
    return (int(act_lsd->gt_graph()->gt(lnode).graphics()->x()));
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

int node_y(Node gnode)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();

    LEAVE;
    return (int(act_lsd->gt_graph()->gt(lnode).graphics()->y()));
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

Node_edge_interface node_edge_interface(Node gnode)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();
    GT_Node_NEI *node_nei = act_lsd->gt_graph()->gt(lnode).node_nei();
    GT_Key      anchor;

    // anchor = node_nei->get_EA_default_function ();
    anchor = node_nei->default_function ();

    // Only the same as in graphed if the default-function is a connectfunction
    // we don't have a one-to-one correspondence!!

    if (anchor == GT_Keys::EA_connect_corner_shortest) {
	return TO_CORNER_OF_BOUNDING_BOX;
    }
    if (anchor == GT_Keys::EA_connect_middle_shortest) {
	return TO_BORDER_OF_BOUNDING_BOX;
    }

    //     LEAVE;
    // We don't have any correspondence
    return NO_NODE_EDGE_INTERFACE;
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

Nodelabel_placement node_label_placement(Node gnode)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();
    GT_Key      anchor;

    anchor = act_lsd->gt_graph()->gt(lnode).label_anchor();
	
    // we don't have a one-to-one correspondence!!
    if( anchor == GT_Keys::anchor_nw ) { return(NODELABEL_UPPERLEFT); };
    if( anchor == GT_Keys::anchor_ne ) { return(NODELABEL_UPPERRIGHT); };
    if( anchor == GT_Keys::anchor_sw ) { return(NODELABEL_LOWERLEFT); };
    if( anchor == GT_Keys::anchor_se ) { return(NODELABEL_LOWERRIGHT); };
	
    LEAVE;
    return (NODELABEL_MIDDLE);
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

char* node_label_text(Node gnode)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();

    LEAVE;
    return ( const_cast<char*>(act_lsd->gt_graph()->gt(lnode).label().c_str()) );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

int node_font_index(Node /*gnode*/)
{
    ENTRY;
    LEAVE;
    return( 0 );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96
// WA CH

int node_type_index(Node /*gnode*/)
{
    ENTRY;
    return (0);
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

int node_label_visible(Node /*gnode*/)
{
    ENTRY;
    // up to now a label is in graphlet always visible
    return (TRUE);
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

int node_color(Node /*gnode*/)
{
    ENTRY;
    // sgraph works only with one color: Black
    return ( 1 );
}


/////////////////////////////////////////////
// Funktionen zum Setzen von Knoten-Attributen
/////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

extern void set_node_width(Node gnode, int width)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();

    act_lsd->gt_graph()->gt(lnode).graphics()->w( width );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

extern void set_node_height(Node gnode, int height)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();

    act_lsd->gt_graph()->gt(lnode).graphics()->h( height );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

extern void set_node_x(Node gnode, int x)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();

    act_lsd->gt_graph()->gt(lnode).graphics()->x( x );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

extern void set_node_y(Node gnode, int y)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();

    act_lsd->gt_graph()->gt(lnode).graphics()->y( y );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

extern void set_node_edge_interface(Node gnode, Node_edge_interface nei)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();	
    GT_Key      anchor;
    GT_Node_NEI *node_nei = act_lsd->gt_graph()->gt(lnode).node_nei();

    // we don't have a one-to-one correspondence!!

    switch (nei) {
	case TO_CORNER_OF_BOUNDING_BOX:
	    node_nei->set_EA_default_function (
		GT_Keys::EA_connect_corner_shortest);
	    break;
	case TO_BORDER_OF_BOUNDING_BOX:
	    node_nei->set_EA_default_function (
		GT_Keys::EA_connect_middle_shortest);	    
	    break;
	default:
	    // Don't change
	    break;
    }
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

extern void set_node_label_placement(Node gnode, Nodelabel_placement nlp)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();
    GT_Key      anchor;

    // we don't have a one-to-one correspondence!!
    switch (nlp) {
	case NODELABEL_UPPERLEFT:
	    anchor = GT_Keys::anchor_nw;
	    break;
	case NODELABEL_UPPERRIGHT:
	    anchor = GT_Keys::anchor_ne;
	    break;
	case NODELABEL_LOWERLEFT:
	    anchor = GT_Keys::anchor_sw;
	    break;
	case NODELABEL_LOWERRIGHT:
	    anchor = GT_Keys::anchor_se;
	    break;
	default:
	    anchor = GT_Keys::anchor_center;
	    break;
    }
    act_lsd->gt_graph()->gt(lnode).label_anchor(anchor);
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

extern void set_node_label_text(Node gnode, char* text)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();

    act_lsd->gt_graph()->gt(lnode).label( string(text) );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

extern void set_node_font_index(Node /*gnode*/, int /*index*/)
{
    ENTRY;
    // cout << "Sorry, but set_node_font_index is not supported any more" << endl;
    assert( 0 == 1 );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

extern void set_node_type_index(Node /*gnode*/, int /*index*/)
{
    ENTRY;
    // cout << "Sorry, but set_node_type_index is not supported any more" << endl;
    assert( 0 == 1 );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

extern void set_node_label_visible(Node /*gnode*/, int /*visible*/)
{
    ENTRY;
    // in graphlet a label is always visible
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

extern void set_node_color(Node gnode, int /* color */)
{
    ENTRY;
    NodeRef*    lnode_meta_ref    = (NodeRef*) gnode;
    LSD*        act_lsd           = lnode_meta_ref->lsd();
    node        lnode             = lnode_meta_ref->ref();

    // we support only black
    act_lsd->gt_graph()->gt(lnode).graphics()->outline (GT_Keys::black);
    LEAVE;
}


/////////////////////////////////////////////
// Funktionen zur Ermittlung von Kanten-Attributen
/////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

Edgeline get_edgeline(Edge gedge)
{
    ENTRY;
    EdgeRef* edge_ref =	(EdgeRef*) gedge;

    return edge_ref->edgeline();
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

int edge_arrow_length(Edge gedge)
{
    ENTRY;
    EdgeRef*   ledge_meta_ref = (EdgeRef*) gedge;
//     LSD*       act_lsd = ledge_meta_ref->lsd();
//     edge       ledge = ledge_meta_ref->ref();
    GT_Key     shape;
    int        length;

    // MH Jan 10 1999: unused
    // shape = act_lsd->gt_graph()->gt(ledge).graphics()->arrowshape();

    // WA CH Michael fragen wie das elegant geht
    length = 8;
    // Arrowshape: <width> <length> <length touching part> as a string

    return( length );
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96
// This is different in tcl/tk: You have only width and length

int edge_arrow_angle(Edge gedge)
{
    ENTRY;
    EdgeRef*   ledge_meta_ref = (EdgeRef*) gedge;
//     LSD*       act_lsd = ledge_meta_ref->lsd();
//     edge       ledge = ledge_meta_ref->ref();
    GT_Key     shape;
    int        angle;

    // MH Jan 10 1999: unused
    // shape = act_lsd->gt_graph()->gt(ledge).graphics()->arrowshape();

    // WA CH Michael fragen wie das elegant geht
    angle = 8;
    // Arrowshape: <width> <length> <length touching part> as a string

    return( angle );
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

char* edge_label_text(Edge gedge)
{
    EdgeRef*   ledge_meta_ref = (EdgeRef*) gedge;
    LSD*       act_lsd = ledge_meta_ref->lsd();
    edge       ledge = ledge_meta_ref->ref();

    return( const_cast<char*>(act_lsd->gt_graph()->gt(ledge).label().c_str()) );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

int edge_font_index(Edge /*gedge*/)
{
    // cout << "Function edge_font_index is not supported any more" << endl;
    assert( 1 == 2 );
    return( 1 );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

int edge_type_index(Edge gedge)
{
    EdgeRef*   ledge_meta_ref = (EdgeRef*) gedge;
    LSD*       act_lsd = ledge_meta_ref->lsd();
    edge       ledge = ledge_meta_ref->ref();
    GT_Key     stipple;
    int        index;

    stipple = act_lsd->gt_graph()->gt(ledge).graphics()->stipple();

    // WA CH Michael fragen wie das elegant geht
    // WA CH stipple scheint zu passen 
    index = 8;
    // stipple ist der Key einer Bitmap

    return( index );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

int edge_label_visible(Edge /*gedge*/)
{
    // Up to now, in graphlet is everything visible
    return( TRUE );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

int edge_color(Edge /* gedge */)
{
    // Sgraph is only supported with the black color
    return( 1 );
}


/////////////////////////////////////////////
// Funktionen zum Setzen von Kanten-Attributen
/////////////////////////////////////////////

void set_edge_line(Edge gedge, Edgeline edgeline)
{
    ENTRY;

    // gedge == 0?
    assert(gedge);
	
    EdgeRef* ledge_meta_ref =
	(EdgeRef*) gedge;

    ledge_meta_ref->edgeline(edgeline);
	
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

void set_edge_arrow_length(Edge gedge, int length)
{
    EdgeRef*   ledge_meta_ref = (EdgeRef*) gedge;
//     LSD*       act_lsd = ledge_meta_ref->lsd();
//     edge       ledge = ledge_meta_ref->ref();
    GT_Key     shape;

    // MH Jan 10 1999: unused

    // shape = act_lsd->gt_graph()->gt(ledge).graphics()->arrowshape();

    // length = 10;
    // WA CH Michael fragen wie das elegant geht
    // Arrowshape: <width> <length> <length touching part> as a string

    // act_lsd->gt_graph()->gt(ledge).graphics()->arrowshape( shape );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

void set_edge_arrow_angle(Edge gedge, int angle)
{
    EdgeRef*   ledge_meta_ref = (EdgeRef*) gedge;
//     LSD*       act_lsd = ledge_meta_ref->lsd();
//     edge       ledge = ledge_meta_ref->ref();
    GT_Key     shape;

    // MH Jan 10 1999: unused
    // shape = act_lsd->gt_graph()->gt(ledge).graphics()->arrowshape();

    angle = 10;
    // WA CH Michael fragen wie das elegant geht
    // Arrowshape: <width> <length> <length touching part> as a string

    // act_lsd->gt_graph()->gt(ledge).graphics()->arrowshape( shape );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

void set_edge_label_text(Edge gedge, char* text)
{
    EdgeRef*   ledge_meta_ref = (EdgeRef*) gedge;
    LSD*       act_lsd = ledge_meta_ref->lsd();
    edge       ledge = ledge_meta_ref->ref();

    act_lsd->gt_graph()->gt(ledge).label( text );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

void set_edge_font_index(Edge /*gedge*/, int /*font_index*/)
{
    // cout << "Function set_edge_font_index is not supported" << endl;
    // Stop
    // assert( 0==1 );
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

void set_edge_type_index(Edge gedge, int edgetype_index)
{
    EdgeRef*   ledge_meta_ref = (EdgeRef*) gedge;
    LSD*       act_lsd = ledge_meta_ref->lsd();
    edge       ledge = ledge_meta_ref->ref();
    GT_Key     stipple;

    stipple = act_lsd->gt_graph()->gt(ledge).graphics()->stipple();

    // WA CH Michael fragen wie das elegant geht
    // WA CH stipple scheint zu passen 
    edgetype_index = 8;
    // stipple ist der Key einer Bitmap
    act_lsd->gt_graph()->gt(ledge).graphics()->stipple(stipple);
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

void set_edge_label_visible(Edge /*gedge*/, int /*visible*/)
{
    // Dummy, because everything is visible
}

////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

void set_edge_color(Edge gedge, int /*color*/)
{
    EdgeRef*   ledge_meta_ref = (EdgeRef*) gedge;
    LSD*       act_lsd = ledge_meta_ref->lsd();
    edge       ledge = ledge_meta_ref->ref();

    // We support only black !!
    act_lsd->gt_graph()->gt(ledge).graphics()->outline (GT_Keys::black);
}


////////////////////////////////////////////////////////////////////////////
// WA 28.5.96

char* node_get (Node gnode, Set_attribute attr)
{
    switch (attr) {
		
	case NODE_X :
	    return (char*) node_x(gnode);
	    break;
			
	case NODE_Y :
	    return (char*) node_y(gnode);
	    break;
			
	case NODE_WIDTH :
	    return (char*) node_width(gnode);
	    break;
			
	case NODE_HEIGHT :
	    return (char*) node_height(gnode);
	    break;
			
	case NODE_NEI :
	    return (char*) node_edge_interface(gnode);
	    break;
			
	case NODE_NLP :
	    return (char*) node_label_placement(gnode);
	    break;
			
	case NODE_LABEL :
	    return (char*) node_label_text(gnode);
	    break;
			
	case NODE_FONT :
	    return (char*) node_font_index(gnode);
	    break;
			
	case NODE_TYPE :
	    return (char*) node_type_index(gnode);
	    break;
			
	case NODE_LABEL_VISIBILITY :
	    return (char*) node_label_visible(gnode);
	    break;
			
	case NODE_COLOR :
#ifdef GRAPHED2
	    return (char*) node_color(gnode);
#else
	    return 0;
#endif
	    break; 
	default:
	    return 0;
	    break;
    }
}


char	*edge_get (Edge gedge, Set_attribute attr)
{
    switch (attr) {
		
	case EDGE_LINE :
	    return (char*) get_edgeline(gedge);
	    break;
			
	case EDGE_ARROW_LENGTH :
	    return (char*) edge_arrow_length(gedge);
	    break;
			
	case EDGE_LABEL :
	    return edge_label_text(gedge);
	    break;
			
	case EDGE_FONT :
	    return (char*) edge_font_index(gedge);
	    break;
			
	case EDGE_TYPE :
	    return (char*) edge_type_index(gedge);
	    break;
			
	case EDGE_LABEL_VISIBILITY :
	    return (char*) edge_label_visible(gedge);
	    break;
			
	case EDGE_COLOR :
#ifdef GRAPHED2
	    return (char*) edge_color(gedge);
#else
	    return 0;
#endif
	    break; 
	default:
	    return 0;
	    break;
			
    }
}

void	graph_set (Graph /*graph*/, ...)
{
    cerr << "graph_set() was called but is not yet implemented!" << endl;
}


//**********************************************************************
//									
//		       KANTE POSITIONIEREN				
//									
//		Ab jetzt wird wieder mitgezeichnet !			
//									
//**********************************************************************
//									
//	void	edge_set (edge, ...)					
//									
//	edge_set (edge,							
//	          [ONLY_SET],		  Nur setzen, keine adjust_...	
//	          [EDGE_LINE,   line]					
//	          [EDGE_INSERT, el, x,y]  Nach el (x,y) einfuegen	
//	          [EDGE_DELETE, el]       el aus edge->line loeschen	
//	          [MOVE, el, dx,dy]					
//	          [EDGE_TYPE, index]					
//	          [EDGE_FONT, index]					
//	          [EDGE_LABEL, text]					
//	          [EDGE_LABEL_VISIBILITY, visible]			
//	          [EDGE_ARROW_LENGTH, length]				
//	          [EDGE_ARROW_ANGLE, angle]				
//	          [RESTORE_IT]		    Nur nach ONLY_SET		
//									
//	ONLY_SET dient zum schnellen setzen von Parametern; vor dem	
//	Neuzeichnen muss unbedingt RESTORE_IT aufgerufen werden.	
//	(ONLY_SET setzt nur den Wert ein und loescht/zeichnet nicht und	
//	fuehrt keine adjust_...	Prozeduren durch).			
//	EDGE_DELETE kann edge->line loeschen !				
//									
//======================================================================
//									
//	void	move_edge (edge, el, x,y)				
//									
//	Bewegt den Punkt el in edge->line auf Position (x,y).		
//									
//======================================================================
//									
//	void	extend_edge (edge, el, x,y)				
//									
//	Erweitert edge->line um einen Punkt (x,y) NACH el.		
//	ACHTUNG : el != edge->line->pre (= letzter Punkt).		
//									
//======================================================================
//									
//	void	comprime_edge (edge, el)				
//									
//	Entfernt el aus edge->line.					
//									
//**********************************************************************


void edge_set (Edge edge, Set_attribute attr, ...)
{
    va_list		args;
	
    int		adjust_elp   = FALSE;
    int 	adjust_graph = FALSE;
    int 	adjust_arrow = FALSE;
    int 	adjust_box   = FALSE;
    int 	adjust_head_and_tail = FALSE;
    int 	adjust_label_text_to_draw = FALSE;
	
    int		redraw_edge   = FALSE;
    int 	redraw_label  = FALSE;
	
    int		set_line         = FALSE;	// Flags what to set	
    int 		set_type         = FALSE;
    int 		set_label        = FALSE;
    int 		set_font         = FALSE;
    int 		set_visibility   = FALSE;
    int 		set_arrow_length = FALSE;
    int 		set_arrow_angle  = FALSE;
    int 		set_color       = FALSE;
	
    int		only_set     = FALSE;
    int		restore_edge = FALSE;
	
    Edgeline	el = 0;
    int		    x = 0;
    int 		y = 0;
    int 		dx = 0;
    int 		dy = 0;
    int		    edgetype_index = 0;
    int 		font_index = 0;
    int 		visible = 0;
    int 		length = 0;
    int 		color = 0;
    double		angle = 0;
    char*		text = 0;
	
	
    va_start (args, attr);
	
#ifdef LP_LAYOUT
    if( edge->source->graph->lp_graph.derivation_net ) {
	edge->source->graph->lp_graph.changed = TRUE;
    }
#endif
	
    if (attr == ONLY_SET) {
	only_set = TRUE;
	attr = va_arg (args, Set_attribute);
    }
	
    // Set the flags etc.	
	
    while (attr != SET_ATTRIBUTE_END) {
		
	switch (attr) {
			
	    case EDGE_LINE :
		el = va_arg (args, Edgeline);
		redraw_edge          = TRUE;
		adjust_elp           = TRUE;
		adjust_head_and_tail = TRUE;
		adjust_box           = TRUE;
		adjust_graph         = TRUE;
		set_line             = TRUE;
		break;
				
	    case EDGE_INSERT :
		el = va_arg (args, Edgeline);
		x = va_arg (args, int);
		y = va_arg (args, int);
		el = add_to_edgeline (el, x,y);
		// geaendert: 
		el = get_edgeline(edge);
		redraw_edge          = TRUE;
		adjust_elp           = TRUE;
		adjust_head_and_tail = TRUE;
		adjust_box           = TRUE;
		adjust_graph         = TRUE;
		set_line             = TRUE;
		break;
				
	    case EDGE_DELETE :
		Edgeline helpel;
		el = va_arg (args, Edgeline);
		// geaendert: 
		if (el == get_edgeline(edge))
		{
		    el = remove_from_edgeline (el);
		    (helpel = get_edgeline(edge)) = el->suc;
		}
		else
		{
		    el = remove_from_edgeline (el);
		}
		el = get_edgeline(edge);
				
		redraw_edge          = TRUE;
		adjust_elp           = TRUE;
		adjust_head_and_tail = TRUE;
		adjust_box           = TRUE;
		adjust_graph         = TRUE;
		set_line             = TRUE;
		break;
				
	    case MOVE :
		el = va_arg (args, Edgeline);
		dx  = va_arg (args, int);
		dy  = va_arg (args, int);
		set_edgeline_xy (el, el->x + dx, el->y + dy);
		redraw_edge          = TRUE;
		adjust_elp           = TRUE;
		adjust_head_and_tail = TRUE;
		adjust_box           = TRUE;
		adjust_graph         = TRUE;
		break;
				
	    case EDGE_TYPE :
		edgetype_index = va_arg (args, int);
		redraw_edge    = TRUE;
		set_type       = TRUE;
		break;
				
	    case EDGE_LABEL :
		text = va_arg (args,char*);
		if (text != 0 && strcmp(text, ""))
		{
		    redraw_label = TRUE;
		}
		else
		{
		    // The label size is 0,	and therefore nothing	
		    // gets done - the markings would not be	
		    // redrawn in force_repainting. To avoid this,	
		    // redraw the whole edge. MH 18/6/89		
		    redraw_edge       = TRUE;
		}
		adjust_label_text_to_draw = TRUE;
		adjust_elp                = TRUE;
		adjust_box                = TRUE;
		adjust_graph              = TRUE;
		set_label                 = TRUE;
		break;
				
	    case EDGE_FONT :
		font_index = va_arg (args, int);
		redraw_label              = TRUE;
		adjust_label_text_to_draw = TRUE;
		adjust_elp                = TRUE;
		adjust_box                = TRUE;
		adjust_graph              = TRUE;
		set_font                  = TRUE;
		break;
				
	    case EDGE_LABEL_VISIBILITY :
		visible        = va_arg (args, int);
		redraw_label   = TRUE;
		set_visibility = TRUE;
		break;
				
	    case EDGE_ARROW_LENGTH :
		length            = va_arg (args, int);
		redraw_edge       = TRUE;
		adjust_arrow      = TRUE;
		adjust_box        = TRUE;
		adjust_graph      = TRUE;
		set_arrow_length  = TRUE;
		break;
				
	    case EDGE_ARROW_ANGLE :
		angle            = va_arg (args, double);
		redraw_edge      = TRUE;
		adjust_arrow     = TRUE;
		adjust_box       = TRUE;
		adjust_graph     = TRUE;
		set_arrow_angle  = TRUE;
		break;
				
	    case EDGE_COLOR :
		color            = va_arg (args, int);
		redraw_edge      = TRUE;
		set_color        = TRUE;
		break;
				
	    case RESTORE_IT :
		redraw_edge               = TRUE;
		adjust_head_and_tail      = TRUE;
		adjust_label_text_to_draw = TRUE;
		adjust_elp                = TRUE;
		adjust_graph              = TRUE;
		adjust_box                = TRUE;
		restore_edge              = TRUE;
		break;
				
	    default :
		break;
	}
		
	attr = va_arg (args, Set_attribute);
    }
	
	
    // Let's do It		
	
    if (set_line) {
	set_edge_line(edge, el);
    }
	
    if (set_type) {	
	set_edge_type_index(edge, edgetype_index);
    }
	
    if (set_label) {
	set_edge_label_text(edge, text);
    }
	
    if (set_font) {	
	set_edge_font_index(edge, font_index);
    }
	
    if (set_visibility) {
	set_edge_label_visible(edge, visible);
    }
	
    if (set_arrow_length) {
	set_edge_arrow_length(edge, length);
    }
	
    if (set_arrow_angle) {
	set_edge_arrow_angle(edge, (int) angle);
    }
    if (set_color) {
	set_edge_color(edge, color);
    }
	
    va_end (args);
}


//**********************************************************************
//									
//			KNOTENATTRIBUTE VERWALTEN			
//									
//		    Ab jetzt wird wieder gezeichnet !			
//									
//**********************************************************************
//									
//	void	node_set (node, ...)					
//									
//	Setzen von Knotenattributen. Syntax ( [...] = optional ) :	
//									
//	node_set (node, [ONLY_SET,]					
//		  [NODE_POSITION,         x,y,]				
//		  [MOVE,                  dx,dy,]			
//		  [NODE_SIZE,             width, height,]		
//		  [NODE_TYPE,             nodetype_index,]		
//		  [NODE_NEI,              nei,]				
//		  [NODE_NLP,              nlp,]				
//		  [NODE_LABEL,            text,]			
//		  [NODE_FONT,             font_index,]			
//		  [NODE_LABEL_VISIBILITY, visibility,]			
//		  [NODE_COLOR,            color,]			
//		  [RESTORE_IT,]						
//		  0);							
//									
//	ONLY_SET dient zum schnellen setzen von Parametern; vor dem	
//	Neuzeichnen muss unbedingt RESTORE_IT aufgerufen werden.	
//	(ONLY_SET setzt nur den Wert ein und loescht/zeichnet nicht und	
//	fuehrt keine adjust_...	Prozeduren durch).			
//									
//======================================================================
//									
//	void	set_node_marked (node, marked)				
//									
//======================================================================
//									
//	void	set_node_to_be_marked (node)				
//									
//	Knoten wird beim naechsten Neuzeichnen (force_repainting)	
//	markiert.							
//									
//**********************************************************************

void node_set (Node gnode, Set_attribute attr, ...)
{
    va_list		args;
	
    int		adjust_nlp   = FALSE;	// Flags whether to adjust	
    int		adjust_edges = FALSE;	// attributes etc; a set would	
    int		adjust_graph = FALSE;	// be the right thing here ...	
    int		adjust_label_text_to_draw = FALSE;	// but in C ?	
    int		redraw_node               = FALSE;
    int		redraw_only_label         = FALSE;
    int		redraw_edges              = FALSE;
	
    int		set_position   = FALSE;	// Flags what to set		
    int		move           = FALSE;
    int		set_size       = FALSE;
    int		set_type       = FALSE;
    int		set_nei        = FALSE;
    int		set_label      = FALSE;
    int		set_font       = FALSE;
    int		set_visibility = FALSE;
    int		set_nlp        = FALSE;
    int		set_color      = FALSE;
	
    int		only_set     = FALSE;
    int		restore_node = FALSE;
	
    int		x = 0;
    int		y = 0;
    int		dx = 0;
    int		dy = 0;
    int		width = 0;
    int		height = 0;
	
    int		nodetype_index = 0;
    int		font_index = 0;
    int		visible = 0;
    int		nei = 0;
    int		nlp = 0;
    int		color = 0;
	
    char*	text = 0;
	
    va_start (args, attr);
	
	
#ifdef LP_LAYOUT
    if( gnode->graph->lp_graph.derivation_net ) {
	gnode->graph->lp_graph.changed = TRUE;
    }
#endif
	
    if (attr == ONLY_SET) {
	only_set = TRUE;
	attr = va_arg (args, Set_attribute);
    }
	
    // Set the flags etc.	
	
    while (attr != SET_ATTRIBUTE_END) {
	switch (attr) {
			
	    case NODE_POSITION :
		x = va_arg (args, int);
		y = va_arg (args, int);
				
		redraw_node  = TRUE;
		redraw_edges = TRUE;
		adjust_nlp   = TRUE;
		adjust_edges = TRUE;
		adjust_graph = TRUE;
		set_position = TRUE;
		break;
				
	    case MOVE :
		dx = va_arg (args, int);
		dy = va_arg (args, int);
		redraw_node  = TRUE;
		redraw_edges = TRUE;
		adjust_nlp   = TRUE;
		adjust_edges = TRUE;
		adjust_graph = TRUE;
		move = TRUE;
		break;
				
	    case NODE_SIZE :
		width = va_arg (args, int);
		height = va_arg (args, int);
		if (width < 1)  width  = 1;
		if (height < 1) height = 1;
		redraw_node  = TRUE;
		redraw_edges = TRUE;
		adjust_nlp   = TRUE;
		adjust_edges = TRUE;
		adjust_graph = TRUE;
		adjust_label_text_to_draw = TRUE;
		set_size     = TRUE;
		break;
				
	    case NODE_TYPE :
		nodetype_index = va_arg (args, int);
		redraw_node  = TRUE;
		redraw_edges = TRUE;
		adjust_label_text_to_draw = TRUE;
		adjust_nlp                = TRUE;
		adjust_edges              = TRUE;
		// Adjusting the graph seems to be unnecessary	
		set_type                  = TRUE;
		break;
				
	    case NODE_NEI :
		nei = va_arg (args, int);
		redraw_edges = TRUE;
		adjust_edges = TRUE;
		// Adjusting the graph seems to be unnecessary	
		set_nei      = TRUE;
		break;
				
	    case NODE_LABEL :
		text = va_arg (args,char*);
		if (text != 0 && strcmp(text, ""))
		    redraw_only_label = TRUE;
		else
		    // The label size is 0,	and therefore nothing	
		    // gets done - the markings would not be	
		    // redrawn in force_repainting. To avoid this,	
		    // redraw the whole node. MH 18/6/89		
		    redraw_node       = TRUE;
		adjust_label_text_to_draw = TRUE;
		adjust_nlp                = TRUE;
		set_label                 = TRUE;
		break;
				
	    case NODE_FONT :
		font_index = va_arg (args, int);
		redraw_node               = TRUE;
		adjust_label_text_to_draw = TRUE;
		adjust_nlp                = TRUE;
		set_font                  = TRUE;
		break;
				
	    case NODE_LABEL_VISIBILITY :
		visible        = va_arg (args, int);
		redraw_node    = TRUE;
		set_visibility = TRUE;
		break;
				
	    case NODE_NLP :
		nlp         = va_arg (args, int);
		redraw_node = TRUE;
		adjust_nlp  = TRUE;
		set_nlp     = TRUE;
		break;
				
	    case NODE_COLOR :
		color       = va_arg (args, int);
		set_color   = TRUE;
		redraw_node = TRUE;
		break;
				
	    case RESTORE_IT :
		redraw_node               = TRUE;
		redraw_edges              = TRUE;
		restore_node              = TRUE;
		adjust_nlp                = TRUE;
		adjust_edges              = TRUE;
		adjust_graph              = TRUE;
		adjust_label_text_to_draw = TRUE;
		break;
				
	    default :
		break;
	}
		
	attr = va_arg (args, Set_attribute);
    }
	
    // Let's do It		
	
    if (set_position || move) {
		
	if (set_position) {
	    dx = x - node_x (gnode);
	    dy = y - node_y (gnode);
	}
	else // move 
	{
	    x = dx + node_x (gnode);
	    y = dy + node_y (gnode);
	}
		
	set_node_x(gnode, x);
	set_node_y(gnode, y);
		
	if (node_edge_interface(gnode) == NO_NODE_EDGE_INTERFACE)
	{
	    NodeRef* lnode_meta_ref = (NodeRef*) gnode;
	    LSD* act_lsd = lnode_meta_ref->lsd();
	    node lnode = lnode_meta_ref->ref();

	    edge ledge;
	    Edge temp_gedge;
			
	    forall_out_edges (ledge, lnode) {
				
		temp_gedge = (Edge)
		    (act_lsd->get_associated_sedge(ledge))->graphed;

		assert(temp_gedge);
				
		set_edgeline_xy (get_edgeline(temp_gedge),
		    get_edgeline(temp_gedge)->x + dx,
		    get_edgeline(temp_gedge)->y + dy);
	    }
			
	    forall_in_edges (ledge, lnode) {
				
		temp_gedge = (Edge)
		    (act_lsd->get_associated_sedge(ledge))->graphed;
				
		assert(temp_gedge);
				
		set_edgeline_xy (get_edgeline(temp_gedge)->pre,
		    get_edgeline(temp_gedge)->pre->x + dx,
		    get_edgeline(temp_gedge)->pre->y + dy);
	    }
	}
    }
	
    if (set_size) {
	set_node_width(gnode, width);
	set_node_height(gnode, height);
    }
	
    if (set_type) {	
	set_node_type_index(gnode, nodetype_index);
    }
	
    if (set_nei) {	
	set_node_edge_interface(gnode, (Node_edge_interface) nei);
    }
	
    if (set_label) {
	set_node_label_text(gnode, text);
    }
	
    if (set_font) {	
	if (font_index != -1) {
	    set_node_font_index(gnode, font_index);
	}
    }
	
    if (set_visibility) {
	set_node_label_visible(gnode, visible);
    }
	
    if (set_nlp) {
	set_node_label_placement(gnode, (Nodelabel_placement) nlp);
    }
	
    if (set_color) {
	set_node_color(gnode, color);
    }
	
    va_end (args);
}


///////////////////////////////////////////////////////
// sedge_real_target() / sedge_real_source()

Snode sedge_real_source (Sedge sedge)
{
    // sedge == 0?
    assert(sedge);

    EdgeRef* ledge_meta_ref =
	(EdgeRef*) sedge->graphed;

	// sedge->graphed == 0?
    assert(ledge_meta_ref);

    return ledge_meta_ref->real_source();
}


Snode sedge_real_target (Sedge sedge)
{
    // sedge == 0?
    assert(sedge);

    EdgeRef* ledge_meta_ref =
	(EdgeRef*) sedge->graphed;

	// sedge->graphed == 0?
    assert(ledge_meta_ref);

    return  ledge_meta_ref->real_target();
}

///////////////////////////////////////////////////////
// Edgeline-Functions

#define mymalloc_edgeline() (Edgeline)mymalloc(sizeof(struct edgeline))
#define myfree(s)           free(s)

Edgeline new_edgeline (int x, int y)
{
    Edgeline	new_el;

    new_el      = (Edgeline) malloc (sizeof(struct edgeline));
    /* new_el->x   = (coord)x;
       new_el->y   = (coord)y;*/
    new_el->x   = x;
    new_el->y   = y;
    new_el->suc = new_el;
    new_el->pre = new_el;
    rect_construct (&(new_el->box), 0, 0, 0, 0);

    return new_el;
}


Edgeline add_to_edgeline (Edgeline el_tail, int x, int y)
{
    Edgeline	new_el;
	
    new_el = new_edgeline(x,y);
	
    if (el_tail != (Edgeline)NULL) {
	new_el->suc      = el_tail->suc;
	new_el->pre      = el_tail;
	new_el->pre->suc = new_el;
	new_el->suc->pre = new_el;
    }
	
    set_edgeline_xy (new_el, x,y);
	
    return new_el;
}


Edgeline remove_from_edgeline (Edgeline el)
{
    if (el == (Edgeline)NULL)
    {
	return (Edgeline)NULL;
    }
    else
    {
	if (el->suc == el)
	{
#if __SUNPRO_CC == 0x401
	    myfree ((char*) el);
#else
	    myfree (el);
#endif
	    return (Edgeline)NULL;
	}
	else
	{
	    Edgeline	el_pre = el->pre;
			
	    el->pre->suc = el->suc;
	    el->suc->pre = el->pre;
	    set_edgeline_xy (el->suc, el->suc->x, el->suc->y);
#if __SUNPRO_CC == 0x401
	    myfree ((char*) el);
#else
	    myfree (el);
#endif
		
	    return el_pre;
	}
    }
}


void set_edgeline_xy (Edgeline el, int x, int y)
{
    int	x0;
    int	y0;
    int	x2;
    int	y2;
	
    edgeline_x (el) = x;
    edgeline_y (el) = y;
	
    // Justiere die box bei el->pre, el neu				
    // BUGS : die (virtuelle) Rueckkante vom Ende zum Anfang wird	
    // ebenfalls gesetzt. Besteht die Kante nur aus einem Punkt (?)	
    // so macht die Prozedur Mist (aber dann ist sowieso etwas	
    // nicht in Ordnung).						
	
    x0 = edgeline_x (el->pre);
    y0 = edgeline_y (el->pre);
    x2 = edgeline_x (el->suc);
    y2 = edgeline_y (el->suc);
	
    rect_construct (&(el->pre->box),
	iif (x0<x, x0, x), iif (y0<y, y0 ,y),     
	abs (x0-x) + 1,    abs(y0-y) + 1
		    );
    rect_construct (&(el->box),
	iif (x2<x, x2, x), iif (y2<y, y2, y),     
	abs (x2-x) + 1,    abs (y2-y) + 1
		    );

}


void free_edgeline (Edgeline el_head)
{
    register Edgeline	el = el_head;
    register Edgeline	el_next = el;

    if (el != (Edgeline)NULL) {
		do {
			el = el_next;
			el_next = el->suc;
#if __SUNPRO_CC == 0x401
			myfree((char*) el);
#else
			myfree(el);
#endif
		} while (el_next != el_head);
	}
}


Edgeline copy_edgeline (Edgeline el_head)
{
    Edgeline	el;
    Edgeline	new_el;
    Edgeline	new_el_head;
		
    new_el_head = new_el = (Edgeline)NULL;
    for_edgeline (el_head, el) {
	if (new_el_head == (Edgeline)NULL)
	{
	    new_el = new_el_head =
		new_edgeline (edgeline_x(el), edgeline_y(el));
	}
	else
	{
	    new_el = add_to_edgeline (new_el,
		edgeline_x(el), edgeline_y(el));
	}
    } end_for_edgeline (el_head, el);
	
    return new_el_head;
}
