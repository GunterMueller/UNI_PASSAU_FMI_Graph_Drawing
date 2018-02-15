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

///////////////////////////////////////////////////////
// define & initialize static mebers

int LSD::the_gridwidth = 32;
int LSD::the_current_node_width = 32;
int LSD::the_current_node_height = 32;

////////////////////////////////////////////////////////////////////////////
// constructors & destructors
// WA: 23.5.96 changed all structures from GA_Graph to GT_graph

LSD::LSD()
{
    ENTRY;
    the_gt_graph_is_mine = FALSE;
    update_gt_counterpart = TRUE;
	
    the_sgraph = empty_sgraph;
    the_proc_info = default_sgraph_proc_info();
    the_selected = SGRAPH_SELECTED_NONE;
    the_new_selected = SGRAPH_SELECTED_NONE;
    LEAVE;
}

LSD::~LSD()
{
    ENTRY;
    free_selections();
    clear();
    LEAVE;
}


///////////////////////////////////////////////////////
// service-methods

////////////////////////////////////////////////////////////////////////////
// prepare_call()
// test of some preconditions for a algorithm-call
// WA: 23.5.96 GA_Graph -> GT_Graph
bool LSD::prepare_call()
{
    ENTRY;
	
    if (the_gt_graph == NULL)   {
	the_sgraph = empty_sgraph;
	//cout << "Called Sgraph-algorithm on an empty graph" << endl;
	LEAVE;
	return FALSE;
    }
	
    // set the_proc_info to default settings
    the_proc_info = default_sgraph_proc_info();

    // set the static members
    set_statics();
	
    LEAVE;
    return TRUE;
}

bool LSD::prepare_call(void (*proc) (Sgraph_proc_info proc_info))
{
    ENTRY;
	
    if (the_gt_graph == NULL)   {
	the_sgraph = empty_sgraph;
	//cout << "Called Sgraph-algorithm on an empty graph" << endl;
	LEAVE;
	return FALSE;
    }
	
    // set the_proc_info to default settings
    the_proc_info = default_sgraph_proc_info();

    // set the static members
    set_statics();

    if (proc == NULL)   {
	//WA CH das muss noch weg
	//cout << "Called without algorithm." << endl;
	LEAVE;
	return FALSE;
    }
	
    LEAVE;
    return TRUE;
}

////////////////////////////////////////////////////////////////////////////
// evaluate_call()
// evaluate the result of a algorithm-call
// WA: CH das ist nur zum debuggen. also noch weghauen
void LSD::evaluate_call(void)
{
    ENTRY;
    // Check if anything has changed
    TRACE("no_changes: " << the_proc_info.no_changes);
    TRACE("no_structure_changes: " << the_proc_info.no_structure_changes);
    TRACE("save_selection: " << the_proc_info.save_selection);
    TRACE("recompute: " << the_proc_info.recompute);
    TRACE("repaint: " << the_proc_info.repaint);
    TRACE("recenter: " << the_proc_info.recenter);
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// callGT()
// run a algorithm on a GT-Graph - no Sgraph-counterpart created or modified.
// WA: 23.5.96 GA_Graph -> GT_Graph

void LSD::callGT(void (*proc) (Sgraph_proc_info proc_info))
{
    ENTRY;
    if (prepare_call(proc))   {
	// delete existing Sgraph- and association-structures
	clear();
	// build a dummy Sgraph
	make_dummy_sgraph();
	// set the member sgraph to the_sgraph
	the_proc_info.sgraph = the_sgraph;
	
	TRACE("proc()");
	// call the algorithm
	proc(&the_proc_info);
	TRACE("proc() o.k.");

	// delete existing Sgraph- and association-structures
	clear();
    }
    // show some results of the algorithm for debug purposes
    evaluate_call();
    LEAVE;
}


///////////////////////////////////////////////////////
// callSgraph()
// run a Sgraph-algorithm on a GA-Graph
// WA: 23.5.96 GA_Graph -> GT_Graph
void LSD::init_call()
{
    if (prepare_call())   {
	// delete existing Sgraph- and association-structures
	clear();
	// build the Sgraph-counterpart
	copy_gtgraph_to_sgraph();
	// copy the LEDA-selection to a Sgraph-selection
	set_sgraph_selection();
	// set the member sgraph to the_sgraph
	the_proc_info.sgraph = the_sgraph;
	
	// activate auto-deletion for LIGA-counterparts
	update_gt_counterpart = TRUE;			
    }
    else {
	// cout << "init call failed" << endl;
    }
}

void LSD::clean_up()
{
    // deactivate auto-deletion for LIGA-counterparts
    update_gt_counterpart = FALSE;

	// if something changed, copy the changes back to the GA-graph.
	// WA CH We copy always!
    if (the_sgraph != empty_sgraph)  {
	copy_sgraph_to_gtgraph();
    }
    else {
	// cout << "The graph is empty" << endl;
    }
		
    // copy the new Sgraph-selection to a LEDA-selection
    get_sgraph_new_selection();
    // delete existing Sgraph- and association-structures
    clear();
}	
	
void LSD::callSgraph(void (*proc) (Sgraph_proc_info proc_info))
{
    ENTRY;

    if (prepare_call(proc))   {
	// delete existing Sgraph- and association-structures
	clear();
	// build the Sgraph-counterpart
	copy_gtgraph_to_sgraph();
	// copy the LEDA-selection to a Sgraph-selection
	set_sgraph_selection();
	// set the member sgraph to the_sgraph
	the_proc_info.sgraph = the_sgraph;
	
	// activate auto-deletion for LIGA-counterparts
	update_gt_counterpart = TRUE;
			
	TRACE("proc()");
	// call the algorithm
	proc(&the_proc_info);
	TRACE("proc() o.k.");

	// deactivate auto-deletion for LIGA-counterparts
	update_gt_counterpart = FALSE;

	// if something changed, copy the changes back to the GA-graph.
	// WA CH We copy always!
	if (the_sgraph != empty_sgraph)  {
	    copy_sgraph_to_gtgraph();
	}
	else
		
	// copy the new Sgraph-selection to a LEDA-selection
	get_sgraph_new_selection();
	// delete existing Sgraph- and association-structures
	clear();
    }
    else
    // show some results of the algorithm for debug purposes
    evaluate_call();
    LEAVE;
}

///////////////////////////////////////////////////////
// clear the LSD-object: delete the Sgraph and
// the association between LEDA and Sgraph.

void LSD::clear(void)
{
    ENTRY;
    the_proc_info = default_sgraph_proc_info();	
    update_gt_counterpart = FALSE;

    if (the_sgraph != empty_sgraph)   {
	remove_graph(the_sgraph);
	the_sgraph = empty_sgraph;
    }
	
    update_gt_counterpart = TRUE;
    // refresh the association-maps for  better performance
    clear_association_maps();	
    LEAVE;
}

///////////////////////////////////////////////////////
// fill the "sgraph_proc_info"-structure with
// default values.

struct sgraph_proc_info	LSD::default_sgraph_proc_info (void)
{
    ENTRY;
    struct sgraph_proc_info	     sgraph_proc_info;

    sgraph_proc_info.buffer    = 0;
    sgraph_proc_info.sgraph    = empty_sgraph;
    sgraph_proc_info.selected  = SGRAPH_SELECTED_NONE;
	
    sgraph_proc_info.no_changes           = FALSE;
    sgraph_proc_info.no_structure_changes = FALSE;
    sgraph_proc_info.save_selection       = FALSE;
    sgraph_proc_info.recompute            = FALSE;
    sgraph_proc_info.repaint              = FALSE;
    sgraph_proc_info.recenter             = FALSE;
	
    sgraph_proc_info.new_buffer           = 0;
    sgraph_proc_info.new_sgraph           = empty_sgraph;
    sgraph_proc_info.new_selected         = SGRAPH_SELECTED_NONE;
	
    LEAVE;
    return sgraph_proc_info;
}

///////////////////////////////////////////////////////
// set the selection within "the_proc_info"

void LSD::set_selection(LEDA_selected selected,
    LEDA_selection selection)
{
    ENTRY;
    the_selected = selected;
    the_selection = selection;
    LEAVE;
}
	
///////////////////////////////////////////////////////
// get the new selection from "the_proc_info"

void LSD::get_new_selection(LEDA_selected& selected,
    LEDA_selection& selection)
{
    ENTRY;
    selected = the_new_selected;
    selection = the_new_selection;
    LEAVE;
}
	
///////////////////////////////////////////////////////
// copy "the_selection" to the selection in Sgraphs
// "the_proc_info"

void LSD::set_sgraph_selection(void)
{
    ENTRY;
    if (the_proc_info.selected == SGRAPH_SELECTED_GROUP)
    {
	// list-attributes must not be freed since
	// they were pointers to nodes which are
	// managed elsewhere.
	free_slist(the_proc_info.selection.group);
    }
		
    the_proc_info.selected = the_selected;

    Snode snode;
    Sedge sedge;
    list<node>* nodelist;
    node lnode;
    list<node>::iterator it;
    list<node>::iterator end;
    Slist slist;
	
    switch (the_selected)
    {
	case SGRAPH_SELECTED_SNODE:
	    snode = get_associated_snode(the_selection.lnode);

	    assert(snode);
			
	    the_proc_info.selection.snode = snode;
	    break;
			
	case SGRAPH_SELECTED_SEDGE:
	    sedge = get_associated_sedge(the_selection.ledge);
			
	    assert(sedge);
			
	    the_proc_info.selection.sedge = sedge;
	    break;
			
	case SGRAPH_SELECTED_GROUP:
	    nodelist = the_selection.lgroup;
	    slist = empty_slist;

	    end = nodelist->end ();
	    
	    for (it = nodelist->begin(); it != end; ++it) {
		lnode = *it;
				
		snode = get_associated_snode(lnode);

		assert(snode);

		slist = add_to_slist( slist, make_attr(ATTR_DATA, snode) );
	    }
			
	    the_proc_info.selection.group = slist;
	    break;
			
	case SGRAPH_SELECTED_SAME: 
	case SGRAPH_SELECTED_NOTHING: 
	case SGRAPH_SELECTED_NONE: 
	    // do nothing, the actual graph (Sgraph)
	    // is selected
	    break;
				
	default:
	    assert(0); // invalid enum-value
	    break;
    }
    LEAVE;
}

///////////////////////////////////////////////////////
// get "the_new_selection" from the new selection in
// Sgraphs "the_proc_info"

void LSD::get_sgraph_new_selection(void)
{
    ENTRY;
    if (the_new_selected == SGRAPH_SELECTED_GROUP)
    {
	// delete old group, since it was created here...
	delete the_new_selection.lgroup;
	the_new_selection.lgroup = 0;
    }
	
    the_new_selected = the_proc_info.new_selected;

    if ( the_proc_info.new_selected == SGRAPH_SELECTED_SAME )
    {
	return;
    }
	
    node lnode;
    edge ledge;
    list<node>* nodelist;
    Snode snode;
    Slist slist;
    Slist sitem;
	
    switch (the_new_selected)
    {
	case SGRAPH_SELECTED_SNODE:
	    lnode = get_associated_lnode(the_proc_info.new_selection.snode);

 	    assert(lnode != node ());
			
	    the_new_selection.lnode = lnode;
	    break;
			
	case SGRAPH_SELECTED_SEDGE:
	    ledge = get_associated_ledge(the_proc_info.new_selection.sedge);
			
	    assert(ledge != edge ());
			
	    the_new_selection.ledge = ledge;
	    break;
			
	case SGRAPH_SELECTED_GROUP:
	    nodelist = new list<node>;
	    slist = the_proc_info.new_selection.group;
			
	    for_slist (slist, sitem) {

		snode = (Snode) attr_data(sitem);
		lnode = get_associated_lnode(snode);

 		assert(lnode != node());
				
		nodelist->push_back(lnode);
									
	    } end_for_slist (slist, sitem);
			
	    the_new_selection.lgroup = nodelist;
	    break;
			
	case SGRAPH_SELECTED_SAME:
	case SGRAPH_SELECTED_NOTHING:
	case SGRAPH_SELECTED_NONE:
	    // do nothing, the actual graph (Sgraph)
	    // is selected
	    break;
				
	default:
	    assert(0); // invalid enum-value
	    break;
    }
    LEAVE;
}

///////////////////////////////////////////////////////
// free the seletion-groups

void LSD::free_selections(void)
{
    ENTRY;
    if (the_proc_info.selected == SGRAPH_SELECTED_GROUP)
    {
	// list-attributes must not be freed since
	// they were pointers to nodes which are
	// managed elsewhere.
	free_slist(the_proc_info.selection.group);
    }
    the_proc_info.selected = SGRAPH_SELECTED_NONE;
	
    if (the_new_selected == SGRAPH_SELECTED_GROUP)
    {
	// delete old group, since it was created by LSD...
	delete the_new_selection.lgroup;
	the_new_selection.lgroup = 0;
    }
    the_new_selected = SGRAPH_SELECTED_NONE;
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// set the static members
// WA: 23.5.96 GA_Graph -> GT_Graph

void LSD::set_statics(void)
{
    ENTRY;
    // Get gridwidt, nodewidth and nodeheight from the graph
    // We don't have this variables in GT_Graph. Set the default value
    the_gridwidth           = 32;
    the_current_node_width  = 32;
    the_current_node_height = 32;
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// Copy sgraph (back) to a GT_graph.
// WA: 23.596

void LSD::copy_sgraph_to_gtgraph (void)
{
    ENTRY;
    // Leda-structures
    graph*       ledagraph;
    node         ledanode;
    edge         ledaedge;
    // Sgraph-structures
    Snode        snode;
    Sedge        sedge;
	
    ledagraph = the_gt_graph->attached();
  
    if ( the_sgraph->directed && ledagraph->is_undirected())   {
	ledagraph->make_directed();
    }
    if ( !(the_sgraph->directed) && ledagraph->is_directed())	{
	ledagraph->make_undirected();
    }
	
    forall_nodes(ledanode, *ledagraph)
	{
	    // get corresponding LEDA-node
	    snode = get_associated_snode(ledanode);
	    assert(snode);
		
	    the_gt_graph->gt(ledanode).graphics()->x(snode->x);
	    the_gt_graph->gt(ledanode).graphics()->y(snode->y);
	    the_gt_graph->gt(ledanode).label(string(snode->label));
	    forall_out_edges(ledaedge, ledanode)
		{
		    // get corresponding LEDA-edge
		    sedge = get_associated_sedge(ledaedge);
		    assert(sedge);
			
		    edgeline_2_GT(sedge, ledaedge);
		    the_gt_graph->gt(ledaedge).label(string(sedge->label));
		}
	}
    LEAVE;
}


////////////////////////////////////////////////////////////////////////////
// construct a dummy Sgraph-counterpart
// This is needed to run algorithms, which make NO
// use of Sgraph (but of GA- and LEDA) with the same
// function-type.

void  LSD::make_dummy_sgraph(void)
{
    ENTRY;
    // the Sgraph must be empty first
    assert(the_sgraph == empty_sgraph);
		
    the_sgraph = make_graph(EMPTY_ATTR);

    // the only purpose of creating a Sgraph-dummy is
    // to set the Sgraphs graphed pointer to the LSD-instance. 
    the_sgraph->graphed = (char*) this;
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// Create a sgraph corresponding to the GT_Graph
// WA: 22.5.96
// WA: CH update_ga_counterpart?
//        GA_2_edgeline muss noch geaendert werden

void LSD::copy_gtgraph_to_sgraph (void)
{
    ENTRY;
    string       label;
    int          number;
    // LEDA structures
    graph*       ledagraph;
    node         ledanode;
    edge         ledaedge;
    // sgraph structures
    Snode        snode, sourcenode, targetnode;
    Sedge        sedge;
	
    update_gt_counterpart = FALSE;
    // Get structures
    ledagraph = the_gt_graph->attached();

    // Clean up and create a new and empty graph
    if (the_sgraph != empty_sgraph) {
	remove_graph(the_sgraph);
	the_sgraph = empty_sgraph;
    }
	
    the_sgraph = make_graph(EMPTY_ATTR);
	
    // Set sgraph-information 
    //directed
    the_sgraph->directed = ledagraph->is_directed(); 
    //graphlabel
    label = the_gt_graph->gt().label();
    the_sgraph->label = strsave (const_cast<char*>(label.c_str()));
	
    // Set the Sgraphs graphed pointer to the LSD-instance,
    // which possesses the Sgraph. This is importent for the
    // functions 
    //    lsd_make_node_proc, lsd_make_edge_proc, lsd_remove_graph_proc
    //    lsd_remove_node_proc, lsd_remove_edge_proc
    // since the graphed-pointers of their argument nodes &
    // edges does not reference to a LSD_Meta_Reference-object.
    // To create these meta-references, we have to know to which LSD-
    // object the nodes & edges belong.
    the_sgraph->graphed = (char*) this;

    // This has to be inserted because of an error in the leda class 'map'
    // reset_associations (*ledagraph);

    forall_nodes(ledanode, *ledagraph) {
	// make Sgraph-node with same number as the corresponding GA-node
	number = the_gt_graph->gt(ledanode).id();
	// MH, removed assertin because this is over-cautious
	// assert(number);
	snode = make_node_with_number(the_sgraph, EMPTY_ATTR, number);
	// save association of corresponding Sgraph- & LEDA-nodes
	set_associated_nodes(ledanode, snode);
		
	// get the node-position
	snode->x = int(the_gt_graph->gt(ledanode).graphics()->x());
	snode->y = int(the_gt_graph->gt(ledanode).graphics()->y());
		
	// get the node-label
	snode->label = strsave (const_cast<char*>(the_gt_graph->gt(ledanode).label().c_str()));
    }

    // Maybe we have hidden edges. We don't want to have them in the sgraph
    // Because of ledas implementation of hidden edges we have to copy by
    // going through all nodes and then through adj edges
    // forall_edges (ledaedge, *ledagraph) {
    
    edge_map<bool> represented (*ledagraph, false);
    forall_nodes (ledanode, *ledagraph) {
	forall_adj_edges (ledaedge, ledanode) {
	    if (represented[ledaedge] == false) {
		represented[ledaedge] = true;
		sourcenode = get_associated_snode (ledaedge.source());
		targetnode = get_associated_snode (ledaedge.target());
		assert (sourcenode);
		assert (targetnode);
			
		sedge = make_edge (sourcenode, targetnode, EMPTY_ATTR);
		set_associated_edges(ledaedge, sedge);
		GT_2_edgeline(ledaedge, sedge);
		// get the edge-label
		sedge->label =
		    strsave (const_cast<char*>(the_gt_graph->gt(ledaedge).label().c_str()));
	    }
	}
    }
		
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// Copy the GT-edgeline to a sgraph-edgeline
// WA: 22.5.96

void LSD::GT_2_edgeline(edge ledaedge, Sedge sedge)
{
    ENTRY;
    list<GT_Point>   gt_edgelist;
    GT_Point         coordinate;
    Edgeline         el;
    bool             first = TRUE;
	
    gt_edgelist = the_gt_graph->gt(ledaedge).graphics()->line();

    // maybe the edgeline is empty
    if (NULL == &gt_edgelist)  {
	// nothing has to be done
	LEAVE;
	return;
    }

    list<GT_Point>::iterator it;
    list<GT_Point>::iterator end = gt_edgelist.end();

    //go through list and copy the elements 

    for (it = gt_edgelist.begin (); it != end; ++it) {
	coordinate = *it;

	if( first ) {
	    // first we have to create a new edgeline
	    el = new_edgeline (int(coordinate.x()), int(coordinate.y()));
	    first = FALSE;
	} else {
	    // append the new point
	    el = add_to_edgeline (el, int(coordinate.x()), int(coordinate.y()));
	}
    }
	
    // WA CH Was macht diese Zeile ???
    ((EdgeRef*) sedge->graphed)->edgeline(el);
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// Copy the sgraph-edgeline to a GT-edgeline
// WA: 22.5.96
// WA CH: siehe Kommentar in alter Funktion (edgeline_2_GA)

void LSD::edgeline_2_GT(Sedge sedge, edge ledaedge)
{
    ENTRY;
    list<GT_Point>  gt_line;
    EdgeRef         *ledge_meta_ref = (EdgeRef*) sedge->graphed;
    Edgeline        edgeline = ledge_meta_ref->edgeline();
    Edgeline        el;

    // delete the old GT_edgeline is not necessary!!!

    // copy the edgeline
    for_edgeline(edgeline, el) {
	gt_line.push_back( GT_Point(edgeline_x(el), edgeline_y(el)) );
    } end_for_edgeline(edgeline, el);

    // set the GT_edgeline
    the_gt_graph->gt(ledaedge).graphics()->line(gt_line);
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// get a new number for a GA-nodenumbering

int LSD::get_new_node_number(void)
{
    ENTRY;

    // Leda-structures
    graph*  lgraph;
    node    lnode;

    int     act_max = 0;

    lgraph = the_gt_graph->attached();
	
    forall_nodes(lnode, *lgraph)	{
	act_max = maximum(act_max, the_gt_graph->gt(lnode).id());
    }

    LEAVE;
    return ++act_max;	
}

////////////////////////////////////////////////////////////////////////////
// after copying the LIGA-graph to an Sgraph-structure, all structural
// changes made on sgraph can be reflected by setting the following routines
// as the constructors rsp. destructors in Sgraph's attribute fields
// "make_node_proc", make_edge_proc",
// "remove_graph_proc", "remove_node_proc" and "remove_edge_proc".
// If parts of the Sgraph-structure have to be deleted without destroying
// the (changed) associated parts in the LIGA-Graph, update_ga_counterpart
// has to be set to FALSE. If deletions should be reflected,
// update_ga_counterpart has to be set to TRUE.
//
// WA: 23.5.96 Changed from GA_Graph-structures to GT_Graph-structures
// WA CH: Michael fragen, ob ich die ID so setzen darf

void  LSD::make_node_proc(Snode snode)
{
    ENTRY;

    graph*  ledagraph = the_gt_graph->attached();
    node    ledanode = ledagraph->new_node();
    int     new_number;
	
    // by our convention, the Snodes number has to be the same
    // as the number stored in the GA of the LEDA node.
    // (notice: the number in the nodes GA, NOT the LEDA-index!)
    // Therefore, we first have to insert a number to the nodes
    // GA, that is (maximum_of_existing_numbers)+1.
	
    // get number and set it in both nodes
    new_number = get_new_node_number();
    the_gt_graph->gt(ledanode).id( new_number );
    snode->nr = new_number;

    // set the label
    the_gt_graph->gt(ledanode).label( string(snode->label) );
	
    // save the association between the Sgraph- and the LEDA-node
    set_associated_nodes(ledanode, snode);
	
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// Create LEDA-edge from a sedge

void  LSD::make_edge_proc(Sedge sedge)
{
    ENTRY;
    node source_node = get_associated_lnode(sedge->snode);
    node target_node = get_associated_lnode(sedge->tnode);
    // create the corresponding LEDA-edge
    graph* ledagraph = the_gt_graph->attached();  
    edge ledaedge    = ledagraph->new_edge(source_node, target_node);
	
    // We have to assign a "real" source- and target-node
    // to the edge (even if its undirected) to establish
    // a direction implicit given by an edgeline.

    // WA CH vielleicht source- und target-id setzen

    the_gt_graph->gt(ledaedge).label( string(sedge->label) );

    // save the association between Sgraph- and LEDA-edge
    set_associated_edges(ledaedge, sedge);
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
//

void  LSD::remove_graph_proc(Sgraph sgraph)
{
    ENTRY;

    free (sgraph->label);
    sgraph->label = 0;
	
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
//

void  LSD::remove_edge_proc(Sedge sedge)
{
    ENTRY;
    graph*     lgraph;
    edge       ledge = get_associated_ledge(sedge);

    assert(ledge != edge());

    free (sedge->label);
    sedge->label = 0;
    deassociate_edges(sedge);
	
    if (update_gt_counterpart)	{
	lgraph = the_gt_graph->attached();
	lgraph->del_edge(ledge);
    }	
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
//

void  LSD::remove_node_proc(Snode snode)
{
    ENTRY;
    graph*    lgraph;
    node      lnode = get_associated_lnode(snode);
    assert(lnode != node());

    free (snode->label);
    snode->label = 0;	
    deassociate_nodes(snode);
	
    if (update_gt_counterpart)	{	
	lgraph = the_gt_graph->attached();
	lgraph->del_node(lnode);
    }
    
    LEAVE;
}

////////////////////////////////////////////////////////////////////////////
// convert a simple LEDA-graph to a GT-graph
// CAUTION:
// the original LEDA-graph can now be modified via the GT-graph!

GT_Graph* LSD::make_GT_graph(graph *leda_graph)
{
    ENTRY;

    GT_Graph* gt_graph;
	
    gt_graph = new GT_Graph;
    gt_graph->attach (leda_graph);
	
    LEAVE;
    return (gt_graph);
}

////////////////////////////////////////////////////////////////////////////
// reduce a GT-graph to a LEDA-Graph
// CAUTION:
// the information hold in the graphs generic
// attributes (GAs) is destroyed!

graph* LSD::make_LEDA_graph(GT_Graph *gt_graph)
{
    ENTRY;

    graph* leda_graph;
	
    if (gt_graph == 0)	{
	leda_graph = 0;
    }	else	{
	leda_graph = gt_graph->attached();
	gt_graph->attach (NULL);
	delete gt_graph ;
	gt_graph = 0;
    }	
    LEAVE;
    return leda_graph;
}

////////////////////////////////////////////////////////////////////////////
// Output of the GA-graph "the_ga_graph" in
// GraphEd 4.* compatible file format

ostream& operator<< (ostream& out, LSD& lsd_object)
{
    ENTRY;
    if (lsd_object.the_gt_graph != 0)	{
	out << *(lsd_object.the_gt_graph);
    }	else	{
	//cout << "The GT-graph of the LSD-instance requested "
	//    << "for output is empty" << endl;
	exit(0);
    }	
    LEAVE;
    return out;
}


////////////////////////////////////////////////////////////////////////////
// The following functions serve as aliases for the
// LSD-functions
//    make_node_proc(), remove_node_proc(),
//    make_edge_proc(), remove_edge_proc() and
//    remove_graph_proc().
// These functions cannot be called directly via Sgraph, because
// Sgraph does not know, to which LSD-instance the associated
// Sgraph-graph belongs. So, the alias-functions get the right
// LSD-instance via the "missused" graphed-pointer and call the
// LSD-functions.

void  lsd_make_node_proc(Snode snode)
{
    ENTRY;
    LSD* act_lsd = (LSD*) snode->graph->graphed;

    assert(act_lsd);
    act_lsd->make_node_proc(snode);
    LEAVE;
}

void  lsd_remove_node_proc(Snode snode)
{
    ENTRY;
    LSD* act_lsd = (LSD*) snode->graph->graphed;
    assert(act_lsd);

    act_lsd->remove_node_proc(snode);
    LEAVE;
}

void  lsd_make_edge_proc(Sedge sedge)
{
    ENTRY;
    LSD* act_lsd = (LSD*) sedge->snode->graph->graphed;
    assert(act_lsd);

    act_lsd->make_edge_proc(sedge);
    LEAVE;
}

void  lsd_remove_edge_proc(Sedge sedge)
{
    ENTRY;
    LSD* act_lsd = (LSD*) sedge->snode->graph->graphed;
    assert(act_lsd);

    act_lsd->remove_edge_proc(sedge);
    LEAVE;
}

void  lsd_remove_graph_proc(Sgraph sgraph)
{
    ENTRY;
    LSD* act_lsd = (LSD*) sgraph->graphed;
    assert(act_lsd);

    act_lsd->remove_graph_proc(sgraph);
    LEAVE;
}







