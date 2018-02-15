/* This software is distributed under the Lesser General Public License */

#include "radial_auxiliary.h"

#include <iostream>

#include <cmath>

#include <GTL/node_map.h>
#include <list>
#include <queue>
#include <stack>

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

// BFS
// has been written due to problems in usage of LEDA bfs

enum traversal_status { untouched, queued_or_stacked, finished } ;

list<node> my_BFS(graph * g, node start)
{

    node v,w ;
    list<node> ordered_nodes ;
    queue<node> under_process ;
    node_map<traversal_status> node_info(*g, untouched) ;


    node_info[start] = queued_or_stacked;
    under_process.push(start);

    node::adj_nodes_iterator it, end;

    while (!(under_process.empty()))
    {
	v = under_process.front();
	under_process.pop();
		
	end = v.adj_nodes_end();

	for (it = v.adj_nodes_begin(); it != end; ++it) {
	    w = *it;
	    if (node_info[w] == untouched){ 
		node_info[w] = queued_or_stacked; 
		under_process.push(w); 
	    }
	}

	node_info[v] = finished;
	ordered_nodes.push_back(v);
    }

    return ordered_nodes ;
}


/*              has been replaced by my_DFS

// preorder for unordered tree g 

list<node> free_preorder(graph * g, node start)
{
cout << "preorder entered" << endl;
	node current, next;
	stack<node> under_process;
	list<node> ordered_nodes;
	
	under_process.push(start);
cout << "start loop" << endl ;
	while (!(under_process.empty()))
		{
		current = under_process.top();
		ordered_nodes.append(current);
		next = g->target(g->first_adj_edge(current));  // any child
cout << "before if statement" << endl ;
		if (next) under_process.push(next);	

			else
			{
			while (!(under_process.empty()))
				{cout << "inner while" << endl ;
				current = under_process.pop();
				next = g->target(g->first_adj_edge(current));
				if (next) under_process.push(next);
				}
			}
		}
		
	return ordered_nodes;		
}


*/

//  non-recursive dfs

list<node> my_DFS(graph * g, node start)
{
    node v ;
    node nei;
    list<node> ordered_nodes ;
    stack<node> under_process ;		
    node_map<traversal_status> node_info(*g, untouched) ;
    node_map<bool> already_written(*g, false);
    bool found_untouched_neighbour ;
    node::adj_nodes_iterator it;
    node::adj_nodes_iterator end;

    node_info[start] = queued_or_stacked ;
    under_process.push(start);

    while (!(under_process.empty())) {
	//
	// search for untouched neighbour of v and put it on stack
	//

	v = under_process.top();
	found_untouched_neighbour = false;
		
	end = v.adj_nodes_end();

	for (it = v.adj_nodes_begin(); it != end && !found_untouched_neighbour; ++it)
	{
	    nei = *it;

	    if (node_info[nei] == untouched) {
		found_untouched_neighbour = true ;
		node_info[nei] = queued_or_stacked ;
		under_process.push(nei);
	    }
	}

	// if there wasn't any we are finished with node tos
		
	if (!found_untouched_neighbour) {
	    v = under_process.top();
	    under_process.pop();
	    node_info[v] = finished;
	}

	ordered_nodes.push_back(v);
    }

    return ordered_nodes;
}





// methods for class radial_polar_coordinate

// constructor

radial_polar_coordinate::radial_polar_coordinate()
{
    radius = 0;
    angle = 0;
}


// accessors
// most of them are inline

void radial_polar_coordinate::set_center(double x, double y)
{
    center_x = x; center_y = y;
};



// transformation
// from polar to cartesian coordinates

double radial_polar_coordinate::cartesian_x()
{
    return (center_x + radius * cos(angle));
};

double radial_polar_coordinate::cartesian_y()
{
    return (center_y + radius * sin(angle));
};




// methods for class radial_annulus_wedge

// constructor

radial_annulus_wedge::radial_annulus_wedge()
{
    from = 0;
    to = 0;
};




// some simple tree functions
// note that trees are directed from the root to the leaves

// appropriate assertions should be made ...

// sibling test

bool siblings(graph * g, node v, node w)
{
    node::in_edges_iterator v_it = v.in_edges_begin();
    node::in_edges_iterator w_it = w.in_edges_begin();
    node::in_edges_iterator v_end = v.in_edges_end();
    node::in_edges_iterator w_end = w.in_edges_end();
    
    if (v_it != v_end && w_it != w_end ) {
	return v.opposite (*v_it) == w.opposite (*w_it);
    } else {
	return false;
    }
}


// return parent node
// to make it a total function, define parent(root) = root

node father(graph * g, node v)
{
    node::in_edges_iterator v_it = v.in_edges_begin();

    if (v_it != v.in_edges_end()) {
	return v.opposite (*v_it);
    } else {
	return v;
    }
}




// strip off all but the firstmost occurrence

list<node> delete_multiple_nodes(list<node> original)
{
    list<node> result;
	
    list<node>::iterator o_it;
    list<node>::iterator o_end = original.end();

    for (o_it = original.begin(); o_it != o_end; ++o_it) {
	if (find (result.begin(), result.end(), *o_it) == result.end()) {
	    result.push_back (*o_it);
	}
    }

    return result;	
}






// auxiliary node for algorithm
// note that in dfs there can be no uncle between son and father !

node auxiliary_node(graph * g,node root, node v, list<node> order)
{
    bool found = false;
    node result, w;
    list<node>::iterator it, end;
    end = order.end(); 

    // w iterates from v to root, increasing level
    for (w = v; (w != root) && (!found) ; w = father(g, w))
    {
	// search for most recently processed sibling of w
	    
	it = find(order.begin(), end, w);
	--it;
	--it;

	for (; it != end && (!found); --it) {
	    
	    // 	    for (counter = order.rank(w) - 2;	// points 1 before w
	    // 		 (counter > 0) && (!found);
	    // 		 counter--)
	    // 	    {

	    if (siblings(g, w, *it))
		// got it !!
	    {  
		found = true;
		result = *it;
	    }
	}		
    }


    if (found) {return result;}
    else {return root;};

}





// maximum

double max(double x, double y)
{
    if (x > y)
	return x;
    else
	return y;
}



// random integer

int randint(int N)
{
    int r = rand();
    if (r < 0) r = -r;
    return 1 + r%N ;
}






// graph-theoretic center of tree
// based on the well-known "leaf-stripper"

// as the center shall become the new root
// we don't care about edge directions
// and insert all reverse edges
// however it's important to clean them up before exiting !!

// we are not allowed to destroy graph *g
// nor can we simply make a copy of it
// as this would confuse node-IDs

// so we use a counter to keep track of 
// the number of remaining nodes

// at each stage "untouched" nodes are those
// that would have survived the destructive variant of the algorithm

// bug report
// bug caused premature change of status from untouched to queued
// and consequently could lead to wrong choice of center
// bug2 caused premature decrement of nodes_left counter
// and consequently could lead to premature termination of iteration
// bug and bug2 have been fixed
// bug3 could cause illegal multiple queueing of newly discovered leaves
// into aux_queue, and consequently premature termination of iteration
// bug3 has been fixed

node center_of_tree(graph * g)
{
    node center,v,w;
    int nodes_left = g->number_of_nodes();
    node_map<traversal_status> node_info(*g, untouched);
    queue<node> stripped_leaves;
    queue<node> auxiliary_queue;
    int untouched_neighbours;
    node_map<int> in_aux_queue(*g, 0);

    // insert reverse edges

    list<edge> auxiliary_edges = g->insert_reverse_edges();

    // current version can't handle 2-node-centers
    // so it arbitrarily "reduces" to 1 node
    // anyway, a 2-node-graph is not too interesting

    if (nodes_left < 3)	// nothing to do ?
    {
	// delete previously inserted reverse edges
	while (!auxiliary_edges.empty()) {
	    g->del_edge(auxiliary_edges.front());
	    auxiliary_edges.pop_front();
	}

	return g->choose_node();
    }


    // prepare iteration by putting all leaves into queue

    forall_nodes(v, *g) {
	if (v.degree() == 2) { 
	    // v is a leave, because graph is strongly connected
	    stripped_leaves.push(v);
	    node_info[v] = queued_or_stacked;
	}
    }

    // iteration
    // queue leaves until tree has shrunken to center
    
    node::adj_nodes_iterator it, it1, end, end1;
				
    while (nodes_left > 2) {
	// empty main queue and put new leaves in auxiliary queue

	while (!stripped_leaves.empty()) {
	    v = stripped_leaves.front();
	    stripped_leaves.pop();

	    // search for newly "discovered" leaves
	    // e.g. untouched nodes adjacent to v
	    // which have only 1 untouched neighbour

	    end = v.adj_nodes_end();
	    for (it = v.adj_nodes_begin (); it != end; ++it) {
		w = *it;
		untouched_neighbours = 0;

		if (node_info[w] == untouched) {
		    end1 = w.adj_nodes_end();

		    for (it1 = w.adj_nodes_begin (); it1 != end1; ++it1) {
			if (node_info[*it1] == untouched)
			    untouched_neighbours++;
		    }
		}

		if (untouched_neighbours == 1 && !in_aux_queue[w]) {
		    //
		    // w is a leave
		    //

		    in_aux_queue[w] = 1;	// bug3 correction
		    auxiliary_queue.push(w);	
		    // this was a bug !!	node_info[w] = queued_or_stacked;
		    // this was bug2 !!		nodes_left--;
		}
	    }

	    nodes_left--;			// bug2 correction
	    node_info[v] = finished;
	}

	// copy auxiliary queue into main queue
	// before starting next iteration

	while (!auxiliary_queue.empty())
	{
	    node_info[auxiliary_queue.front()] = queued_or_stacked;
	    // change state here to avoid bug !!
	    stripped_leaves.push(auxiliary_queue.front());
	    auxiliary_queue.pop();
	}
    }

    // if 1 untouched node is left, it's the center
    // if 2 untouched nodes are left,
    // current version sets center to one having maximum degree
    // if more than 2 untouched nodes are left, something strange has happened

    int center_degree = 0;

    forall_nodes(v, *g) {
	if (node_info[v] == untouched) {
	    if (v.outdeg() > center_degree) {
		center_degree = v.outdeg();
		center = v;
	    }
	}
    }

    // actually, after correction of bug3 
    // a 2-node-center will be queued in stripped_leaves
    // so we have to add

    if (center_degree == 0) {

	while(!(stripped_leaves.empty())) {
	    v = stripped_leaves.front();
	    stripped_leaves.pop();

	    //		cout << "found center in aux queue" << endl;
	    if (v.outdeg() > center_degree)
	    {
		center_degree = v.outdeg();
		center = v;
	    }
	}
    }

    // IMPORTANT
    // remove previously inserted reverse edges

    while (!auxiliary_edges.empty()) {
	g->del_edge(auxiliary_edges.front());
	auxiliary_edges.pop_front();
    }

    return center;
}






// return node with indegree = 0

node source_of_graph(graph * g)
{

    node v;
	
    forall_nodes(v, *g)
	if (v.indeg() == 0)
	    return v;


    // didn't find a source ...

    return g->choose_node();

}




// child of root whose subtree contains v

node root_of_subtree(graph * g, node root, node v)
{
    if (v == root) return root;	// redundant, as father(root) = root

    while (father(g, v) != root)
	v = father(g, v);

    return v;
}




// returns the path of nodes from v to root 

list<node> path_to_root(graph * g, node root, node v)
{

    list<node> path;

    while (v != root)
    {
	path.push_back(v);
	v = father(g,v);
    }

    path.push_back(root);

    return path;
}

// returns level of node v in tree

int level_in_tree(graph * g, node root, node v)
{
    int height = 0;

    while (v != root) {
	++height;
	v = father(g,v);
    }
    
    return height;
}



// return true if v does NOT occur after pos in nlist

// bool does_not_occur_after(node v, int pos, list<node> nlist)
// {
// 	int counter;
// 	bool found = false;

// 	for (counter = pos + 1; !found && counter < nlist.size(); counter++)
		
// 		found = (v == 
// 			nlist.contents(nlist.get_item(counter)));

// 	return !found;
// }





// an object of class node image stores geometric information about a node
// which may be temporarily alterated 


// constructor

node_image::node_image()
{
    heigth = 0; width = 0;
    circle = false; rectangle = false; oval = false;
}



// when confronted with non-standard node types
// the layout algorithm may temporarily replace them with
// their circumcircles

// method has been expanded 22/08/97
// old version failed for nodes with unknown shape
// new version computes upper bound of circumcircle_radius

double node_image::compute_circumcircle_radius()
{
    if (circle)
	return (width/2);
	
    else if (rectangle)
	return (0.5 * sqrt(heigth*heigth + width*width));

    else if (oval)
	return 0.5 * max(width, heigth);
    //		return (0.5 * sqrt(heigth*heigth + width*width));

    else
	//		return 0;	// old version
	return (0.5 * sqrt(heigth*heigth + width*width));  // new
}







// compute QuadStatus of a rectangle in a cartesian system

// ATTENTION !!
// computer y coordinates grow downwards
// so QuadStatus doesn't correspond to the orientation you see on your monitor


QuadStatus quadstatus_of_rectangle(
    double centerX,
    double centerY,
    double width,
    double height,
    double cosystemcenterX,
    double cosystemcenterY)
{
    // delimit border lines of rectangle

    double leftborder = centerX - width/2;
    double rightborder = centerX + width/2;
    double upperborder = centerY + height/2;
    double lowerborder = centerY - height/2;

    // there are 8 possibilities

	// there was a devious bug in the composite subcases NE etc.
	// operators < and > have been replaced by >= and <=

    if (leftborder >= cosystemcenterX && lowerborder >= cosystemcenterY)
	return NE;
    else if (rightborder <= cosystemcenterX && lowerborder >= cosystemcenterY)		return NW;
    else if (rightborder <= cosystemcenterX && upperborder <= cosystemcenterY)		return SW;
    else if (leftborder >= cosystemcenterX && upperborder <= cosystemcenterY)		return SE;

	
    else if (leftborder > cosystemcenterX && 
	lowerborder < cosystemcenterY && upperborder > cosystemcenterY)
	return E;
    else if (lowerborder > cosystemcenterY &&
	leftborder < cosystemcenterX && rightborder > cosystemcenterX)
	return N;
    else if (rightborder < cosystemcenterX &&
	lowerborder < cosystemcenterY && upperborder > cosystemcenterY)
	return W;
    else if (upperborder < cosystemcenterY &&
	leftborder < cosystemcenterX && rightborder > cosystemcenterX)
	return S;


    else	 return ILLEGAL_QuadStatus; 


};



// polar ray QuadStatus

QuadStatus quadstatus_of_ray(double angle)
{
    if (angle < 0)				// should not happen
	return ILLEGAL_QuadStatus;


  START_QUADSTATUS_OF_RAY:

    if (angle == 0)			return E;

    else if (angle < M_PI/2)	return NE;

    else if (angle == M_PI/2)	return N;

    else if (angle < M_PI)		return NW;

    else if (angle == M_PI)		return W;

    else if (angle < 1.5*M_PI)	return SW;

    else if (angle == 1.5*M_PI)	return S;

    else if (angle < 2*M_PI)	return SE;

    else { angle -= 2*M_PI ; goto START_QUADSTATUS_OF_RAY; };
}




// oriented QuadStatus of point to rectangle

QuadStatus oriented_quadstatus_to_rectangle(
    double pointx, double pointy,
    double recenterx, double recentery,
    double w, double h)			// width & height

{

    double upper = recentery + h/2;
    double lower = recentery - h/2;
    double left = recenterx - w/2;
    double right = recenterx + w/2;

    if (pointy > upper && pointx > left)	return N;

    if (pointy < upper && pointx > right)	return E;

    if (pointy < lower && pointx < right)	return S;

    if (pointy > lower && pointx < left)	return W;


    if (pointy > upper && pointx == left)	return NW;

    if (pointy == upper && pointx > right)	return NE;

    if (pointy < lower && pointx == right)	return SE;

    if (pointy == lower && pointx < left)	return SW;

    else return ILLEGAL_QuadStatus;		// rectangle includes point

}





// angle between 2 vectors in orthogonal system

double orthogonal_angle(double vector1x, double vector1y,
    double vector2x, double vector2y)

{

    return acos(
	(vector1x * vector2x + vector1y * vector2y) /
	( sqrt(vector1x*vector1x + vector1y*vector1y)
	    *sqrt(vector2x*vector2x + vector2y*vector2y)));

}






// according to Eades' original algorithm

// current implementation is not efficient

int width_of_tree(graph * g, node root, node v)
{
    int result = 0;
    node testnode;
    list<node> testlist;

    forall_nodes(testnode, *g) {
	testlist = path_to_root(g,root,testnode);
	
	if (testnode.outdeg() == 0		// is a leave
	    && find(testlist.begin(), testlist.end(), v) != testlist.end())		// in subtree of v    
	    result++;			// increment result
    }

    return result;
}






// variation w2 from my thesis

double width2_of_tree(graph * g, node root, node v)
{
    double result = 0;		// was a bug !!
    node testnode;
    list<node> testlist;

    forall_nodes(testnode, *g) {
	testlist = path_to_root(g,root,testnode);
	
	if (testnode.outdeg() == 0
	    && find(testlist.begin(), testlist.end(), v) != testlist.end())
	    result += 1.0/(level_in_tree(g,v,testnode) + 1);
    }

    return result;
}



/* this has been a silly idea 

// variation w3 from my thesis
// again, compute width of subtree under v

double width3_of_tree(graph * g, node root, node v)
{
	double result = 0;		
	node testnode,testnode2;
	list<node> testlist;
	int number_of_nodes_in_subtree = 0;

	forall_nodes(testnode, *g)
		{
		testlist = path_to_root(g,root,testnode);

		if (testlist.search(v))	// in subtree of v
			{
			number_of_nodes_in_subtree++;
			forall_adj_nodes(testnode2,testnode)
				result++;	// count children
			if (testnode != v)
				result++;	// root has no parent
			result++;		// avoid zero
			}		
		}
	
	

	return result / number_of_nodes_in_subtree;
}

*/





int eades_h(graph * g, node root, node v)
{
    int result = 0;
    node testnode;
    list<node> testlist;

    forall_nodes(testnode, *g) {
	testlist = path_to_root(g,root,testnode);
	
	if (find(testlist.begin(), testlist.end(), v) != testlist.end())		// in subtree of v
	    result = (int) max(result, level_in_tree (g,v,testnode));
    }

    return result;
}
