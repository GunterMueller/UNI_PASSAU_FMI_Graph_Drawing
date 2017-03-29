/*===========================================================================*/
/*  
	 PROJEC		st_upward_draw

	 SYSTEM:       	GraphEd extension module
	 FILE:        	st_upward_draw_drawing.c
	 AUTHOR:       	Roland Stuempfl (diploma 1994)


	 Overview
	 ========
	 Source code for the drawing phase of the algorithm upward_draw
	 presented by Guiseppe Di Battista and Roberto Tamassia, published
	 in "Algorithms For Plane Representations of Acyclic Digraphs",
	 Theoretical Computer Science 61 (1988), pp. 175-198.
	 In the following this work will be referenced as [DiBatTam88].
*/
/*===========================================================================*/

#include <values.h>
#include <math.h>
#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/graphed.h>
#include <sgraph/slist.h>
#include <sgraph/algorithms.h>

#include "st_upward_draw_algorithm.h"
#include "st_upward_draw_export.h"

/*===========================================================================*/
/*
	bool	node_is_candidate (n)
	Snode 	n;

	The internal node n is a candidate if and only if it has at most
	five neighbors. By directly checking the number of neighbors, it
	is possible to work in constant time.
*/
/*===========================================================================*/

bool	node_is_candidate (Snode n)
{
	if (EMBEDLIST(n) == EMBEDLIST(n)->suc->suc)
		/* exactly 2 neihbors */
		return (true);
	if (EMBEDLIST(n) == EMBEDLIST(n)->suc->suc->suc)
		/* exactly 3 neihbors */
		return (true);
	if (EMBEDLIST(n) == EMBEDLIST(n)->suc->suc->suc->suc)
		/* exactly 4 neihbors */
		return (true);
	if (EMBEDLIST(n) == EMBEDLIST(n)->suc->suc->suc->suc->suc)
		/* exactly 5 neihbors */
		return (true);
	return (false);
}

/*===========================================================================*/
/*
	Angle	st_upward_draw_get_angle (x0, y0, x1, y1)
	double	x0,y0,x1,y1;

	The two points p0 = (x0, y0) and p1 = (x1, y1) are forming a vector.
	This vector together with the x-axis forms an angle. This angle will
	be returned. The angle is normalised to be in the standard intervall
	[0, 2 pi[.
*/
/*===========================================================================*/

Angle	st_upward_draw_get_angle (double x0, double y0, double x1, double y1)
{
	Angle alpha;

	if (x1 == x0) {
		if (y1 > y0) {
			return (M_PI_2);
		} else {
			return (M_PI_2 + M_PI);
		}
	} else if (y1 == y0) {
		if (x1 > x0) {
			return (0.0);
		} else {
			return (M_PI);
		}
	} else {
		alpha = atan((y1 - y0)/(x1 - x0));
		if ((x1 > x0) && (y1 > y0)) {
			return (alpha);
		} else if ((x1 < x0) && (y1 > y0)) {
			return (M_PI + alpha);
		} else if ((x1 < x0) && (y1 < y0)) {
			return (M_PI + alpha);
		} else  {
			return ((2.0 * M_PI) + alpha);
		}
	}
}

/*===========================================================================*/
/*
	Line	make_line (x0, y0, x1, y1)
	double	x0,y0,x1,y1;

	The two points p0 = (x0, y0) and p1 = (x1, y1) are forming exactly one
	straight line, properly a ray with origin p0 and an angle computed by
	the function st_upward_draw_get_angle.
*/
/*===========================================================================*/

Line	make_line (double x0, double y0, double x1, double y1)
{
	Line	  line;

	line = (Line)malloc(sizeof(struct line));
	line->x = x0;
	line->y = y0;
	line->angle = st_upward_draw_get_angle(x0,y0,x1,y1);

	return (line);
}

/*===========================================================================*/
/*
	Line	make_line_with_angle (x0, y0, angle)
	double	x0,y0;
	Angle	angle;

	This function is a variation of the above function make_line. If the
	angle is alraedy given, it is not necessary to compute it once more.
	In addition the angle will be normalised to be in the standard intervall
	]0, 2 pi[.
*/
/*===========================================================================*/

Line	make_line_with_angle (double x0, double y0, double angle)
{
	Line	  line;
	Angle  alpha;

	line = (Line)malloc(sizeof(struct line));
	line->x = x0;
	line->y = y0;

	alpha = angle;
	while (alpha < 0.0) alpha += (2.0 * M_PI);
	while (alpha > (2.0 * M_PI)) alpha -= (2.0 * M_PI);
	line->angle = alpha;

	return (line);
}

/*===========================================================================*/
/*
	void	free_line (line)
	Line	line;

	This procedure frees the allocated memory of a line.
*/
/*===========================================================================*/

void	free_line (Line line)
{
	free(line);
}

/*===========================================================================*/
/*
	Point	make_point(x, y)
	double	x, y;

	This function returns a point with the real coordinates (x, y).
*/
/*===========================================================================*/

Point	make_point(double x, double y)
{
	Point p;

	p = (Point)malloc(sizeof(struct point));
	p->x = x;
	p->y = y;

	return (p);
}

/*===========================================================================*/
/*
	void	free_point (p)
	Point	p;

	This procedure frees the allocated memory of a point.
*/
/*===========================================================================*/

void	free_point (Point p)
{
	free (p);
}

/*===========================================================================*/
/*
	bool	point_is_left_to_line (p, line)
	Point	p;
	Line	line;

	This tricky little function decides whether a point is part of the
	left half plane bounded by a straight line.
	If the angle of the straight line is one of the four main direction,
	it simply compares the coordinates of the point and the origin of the
	straight line. If not, it is a bit more complex. First, the crossing point
	of the given line with the y-axis is computed. Then the crossing point
	of the y-axis with a virtual line with origin p and the same angle as the
	given line is computed. These two crossing points (by the way only y-
	coordinates, x is always 0) are finally compared. Dependant upon the
	membership of the angle to one of the four quadrants, the relative
	position of the two crossing points decide whether p is in the left
	half plane or not.
*/
/*===========================================================================*/

bool	point_is_left_to_line (Point p, Line line)
{
	double yp,yl,m;

	if (line->angle == M_PI_2)
		return (p->x < line->x);
	else if (line->angle == M_PI + M_PI_2)
		return (p->x > line->x);
	else	if (line->angle == 0.0)
		return (p->y > line->y);
	else if (line->angle == M_PI)
		return (p->y < line->y);
	else {
		m = tan(line->angle);
		yl =	line->y - m * line->x;
		yp =	p->y - m * p->x;
		if ((line->angle > 0.0) && (line->angle < M_PI_2))
			return (yp > yl);
		else if ((line->angle > M_PI_2) && (line->angle < M_PI))
			return (yp < yl);
		else if ((line->angle > M_PI) && (line->angle < M_PI + M_PI_2))
			return (yp < yl);
		else
			return (yp > yl);
	}
}

/*===========================================================================*/
/*
	Point	get_point_on_line (line, t)
	Line	line;
	double	t;

	This function computes the coordinates of a point of the line that has
	a distance t to the origin of the line.
	It is based on the parametrical form of a straight line:
		x = x0 + t * cos(alpha)
		y = y0 + t * sin(alpha)
	t is a real number, alpha is from the intervall [0, 2 pi[.
	Clearly, there are two point with distance t to the origin of the line.
	But with the angle (a vector together with the origin!) we are able to
	decide on which side the point is placed. If t > 0, the point is on the
	same side as the angle (vector) of line points.
*/
/*===========================================================================*/

Point	get_point_on_line (Line line, double t)
{
	Point p;

	p = make_point(line->x + t * cos(line->angle),
				line->y + t * sin(line->angle));

	return (p);
}

/*===========================================================================*/
/*
	Angle	diff_angle (l1, l2)
	Line	l1,l2;

	This function simply computes the (positiv) difference of the two
	angles formed by the lines l1 and l2.
*/
/*===========================================================================*/

Angle	diff_angle (Line l1, Line l2)
{
	Angle alpha;

	if (l1->angle > l2->angle) {
		alpha = (l1->angle - l2->angle);
	} else {
		alpha = (l2->angle - l1->angle);
	}
	return (alpha);
}

/*===========================================================================*/
/*
	bool	graph_has_three_vertices (g)
	Sgraph	g;

	This function checks whether the graph has exactly three vertices.
	If so, we have the base situation of the recursive calls of the
	drawing procedure upward_draw.
*/
/*===========================================================================*/

bool	graph_has_three_vertices (Sgraph g)
{
	if (g->nodes == g->nodes->suc->suc->suc)
		return (true);
	else
		return (false);
}

/*===========================================================================*/
/*
	void	update_coordinates (g)
	Sgraph	g;

	This procedure assigns the real coordinates of each node of g to their
	"back" nodes. But if the entire graph is finally reached, we have to
	perform a special procedure with it, because the back nodes of the
	entire graph are the original nodes of the given sgraph, and those have
	not the attribute X (lfx) and Y (lfy).
*/
/*===========================================================================*/

void	update_coordinates (Sgraph g)
{
	Snode n;

	if (INK(g) != 0) {
		for_all_nodes(g,n) {
			X(BACKNODE(n)) = X(n);
			Y(BACKNODE(n)) = Y(n);
		} end_for_all_nodes(g,n);
	}
}

/*===========================================================================*/
/*
	Snode	select_candidate (g)
	Sgraph 	g;

	This function selects a candidate out of the candidate list of g.
	Dependant upon the user defined settings, the node is selected which
	has the topological highest, lowest, or the middle rank of all nodes
	in the candidate list. Also, the user has the possibility to choose
	a random candidate. There are in [DiBatTam88] no statements about how
	to choose the candidate.
	Experience shows that selecting the node with highest rank yields
	a layout where many nodes are in the upper part of the drawing.
	Just as selecting the lowest node yields a layout with many nodes
	in the lower part of the drawing, while selecting a middle node yields
	a layout with nodes accumulated in the center of the drawing. With the
	later choice we obtain the perhaps best result.
*/
/*===========================================================================*/

Snode	select_candidate (Sgraph g)
{
	Snode	candidate,n;
	Slist	l;
	int	max_rank,min_rank;

	switch (st_settings.select_candidate_mode) {
	case SCM_HIGHEST_CANDIDATE:
		{
			max_rank = 0;
			for_slist(CANDLIST(g),l) {
				n = NODE(l);
				if (RANK(n) > max_rank) {
					max_rank = RANK(n);
					candidate = n;
				}
			} end_for_slist(CANDLIST(g),l);
		}
		break;
	case SCM_MIDDLE_CANDIDATE:
		{
			int	rank_sum = 0;
			int	nr = 0;
			int	mid_sum;
			int	min_dev = RANK(TARGET(g));
			int	dev;

			for_slist(CANDLIST(g),l) {
				rank_sum += RANK(NODE(l));
				nr++;
			} end_for_slist(CANDLIST(g),l);	
			mid_sum = rank_sum / nr;
			for_slist(CANDLIST(g),l) {
				n = NODE(l);
				dev = abs(mid_sum - RANK(n));
				if (dev < min_dev) {
					min_dev = dev;
					candidate = n;
				}	
			} end_for_slist(CANDLIST(g),l);	
		}
		break;
	case SCM_LOWEST_CANDIDATE:
		{
			min_rank = RANK(TARGET(g));
			for_slist(CANDLIST(g),l) {
				n = NODE(l);
				if (RANK(n) < min_rank) {
					min_rank = RANK(n);
					candidate = n;
				}	
			} end_for_slist(CANDLIST(g),l);			
		}
		break;
	case SCM_RANDOM_CANDIDATE:
		{
			int	i,r;
			Slist	l;

			r = rand() % size_of_slist(CANDLIST(g));
			l = CANDLIST(g);
			for (i = 0; i < r; i++) l = l->suc;
			candidate = NODE(l);
		}
		break;
	default:
		candidate = NODE(CANDLIST(g));
		break;
	}
	return (candidate);
}

/*===========================================================================*/
/*
	Snode	get_highest_pred (v)
	Snode	v;

	This function returns the neighbor of v which has the highest rank of
	all v-neighbors.
*/
/*===========================================================================*/

Snode	get_highest_pred (Snode v)
{
	Snode 	u,w;
	Slist 	l;
	int   	max_rank;

	max_rank = 0;
	for_slist(EMBEDLIST(v),l) { 
		w = NODE(l); 
		if (RANK(w) > RANK(v)) continue; 
		if (RANK(w) > max_rank) { 
			u = w; 
			max_rank = RANK(w); 
		} 
	} end_for_slist(EMBEDLIST(v),l); 
 
	return (u); 
}

/*===========================================================================*/
/*
	Slist	test_on_cycle (u, v)
	Snode	u,v;

	This function decides whether the cycle c of the v-neighbors has a
	chord with endpoint u. If so, we have a triangle (u, v , w). Note
	that u and w are not neighbors in c. These three nodes are put
	together in a Slist named cycle that is returned to the caller.
	Moreover these three nodes are sorted according to their rank.
	If no such triangle exists, empty slist is returned.

	ATTENTION: In [DiBatTam88] the authors are suggesting a data structure
	named neighbor trees (which are balanced trees) to perform this
	function with a time complexity of O(log n). To avoid coding, testing,
	etc. of such a data structure, I decide to use the already given
	Slist structure for this purpose. Prof. Brandenburg said it is o.k.!
	The advantage of the Slist structure is the simple implementation.
	The disadvantage is the higher time complexity, because searching
	in Slist structures costs O(n) time instead of O(log n) with the tree
	variant. The total time complexity is therefore O(n*n) and not O(n log n)
	as in [DiBatTam88] discribed.
*/
/*===========================================================================*/

Slist	test_on_cycle (Snode u, Snode v)
{
	Slist  	l,cycle,el,list; 
	Snode	w; 
 
	list = empty_slist; 
	for_slist(EMBEDLIST(v),l) { 
		if ((u == NODE(l->pre)) || (u == NODE(l)) || (u == NODE(l->suc))) continue; 
		list = add_to_slist(list,MAKE_DATA_ATTR(NODE(l))); 
	} end_for_slist(EMBEDLIST(v),l); 
 
	cycle = empty_slist; 
	for_slist(list,l) { 
		w = NODE(l); 
		if (RANK(v) < RANK(w)) { /* Kante (v, w) => Suche nach (u, w) */ 
			for_slist(EMBEDLIST(w),el) { 
				if (NODE(el) == u) { 
					cycle = add_to_slist(cycle,MAKE_DATA_ATTR(u));
					cycle = add_to_slist(cycle,MAKE_DATA_ATTR(v)); 
					cycle = add_to_slist(cycle,MAKE_DATA_ATTR(w)); 
					break; 
				} 
			} end_for_slist(EMBEDLIST(w),el); 
		} else {             /* Kante (w, v) => Suche nach (w, u) */ 
			for_slist(EMBEDLIST(w),el) { 
				if (NODE(el) == u) { 
					cycle = add_to_slist(cycle,MAKE_DATA_ATTR(w)); 
					cycle = add_to_slist(cycle,MAKE_DATA_ATTR(u)); 
					cycle = add_to_slist(cycle,MAKE_DATA_ATTR(v)); 
					break;
				} 
			} end_for_slist(EMBEDLIST(w),el); 
		} 
		if (cycle != empty_slist) break;
	} end_for_slist(list,l); 

	free_slist(list); 
 
	return (cycle); 
}

/*===========================================================================*/
/*
	void	triangulate_cycle(cycle_list,u_elem)
	Slist	cycle_list,u_elem;

	This procedure triangulates the cycle cycle_list after
	removing the candidate.
*/
/*===========================================================================*/

void	triangulate_cycle(Slist cycle_list, Slist u_elem)
{
	int	size_of_cycle;
	Slist	l_elem,r_elem,el;
	Snode	u,n,l,r;

	size_of_cycle = size_of_slist(cycle_list);
	u = NODE(u_elem);
	l_elem = u_elem->pre;
	l = NODE(l_elem);
	r_elem = u_elem->suc;
	r = NODE(r_elem);

	if (size_of_cycle >= 4) {
		n = NODE(r_elem->suc);

		el = contains_slist_element(EMBEDLIST(u),MAKE_DATA_ATTR(l));
		el = add_to_slist(el,MAKE_DATA_ATTR(n));

		el = contains_slist_element(EMBEDLIST(n),MAKE_DATA_ATTR(r));
		el = add_to_slist(el,MAKE_DATA_ATTR(u));
	}  
	if (size_of_cycle == 5) {
		n = NODE(l_elem->pre);

		el = contains_slist_element(EMBEDLIST(u),MAKE_DATA_ATTR(l));
		el = add_to_slist(el,MAKE_DATA_ATTR(n));

		el = contains_slist_element(EMBEDLIST(n),MAKE_DATA_ATTR(l));
		el = el->suc;
		el = add_to_slist(el,MAKE_DATA_ATTR(u));
	} 
}

/*===========================================================================*/
/*
	Sgraph	contract_edge (g, u, v)
	Sgraph	g;
	Snode 	u,v;

	This function returns a new graph that is almost the same as g with the
	difference that the edge (u, v) is contracted into the node u.
*/
/*===========================================================================*/

Sgraph	contract_edge (Sgraph g, Snode u, Snode v)
{
	Sgraph	cg;
	Snode	old_node,new_node,other_old_node,other_new_node;
	Snode	new_u;
	Slist	l,cycle_node_list,u_elem;

	cg = make_new_graph();
	INK(cg) = INK(g) + 1;
	ALPHA(cg) = ALPHA(g);
	EXTERN_MIN(cg) = EXTERN_MIN(g);
	EXTERN_MAX(cg) = EXTERN_MAX(g);

	for_all_nodes(g,old_node) {
		if (old_node == v) continue;
		new_node = make_new_node(cg,old_node);
	} end_for_all_nodes(g,old_node);
	new_u = u->iso;

	for_all_nodes(g,old_node) {
		if (old_node == v) continue;
		new_node = old_node->iso;
		for_slist(EMBEDLIST(old_node),l) {
			other_old_node = NODE(l);
			if (other_old_node == v) continue;
			other_new_node = other_old_node->iso;
			EMBEDLIST(new_node) = add_to_slist(EMBEDLIST(new_node),MAKE_DATA_ATTR(other_new_node));
		} end_for_slist(EMBEDLIST(old_node),l);
	} end_for_all_nodes(g,old_node);
	
	cycle_node_list = empty_slist;
	for_slist(EMBEDLIST(v),l) {
		new_node = NODE(l)->iso;
		cycle_node_list = add_to_slist(cycle_node_list,MAKE_DATA_ATTR(new_node));
	} end_for_slist(EMBEDLIST(v),l);
	u_elem = contains_slist_element(cycle_node_list,MAKE_DATA_ATTR(new_u));

	triangulate_cycle(cycle_node_list,u_elem);
	free_slist(cycle_node_list);

	SOURCE(cg) = SOURCE(g)->iso;
	TARGET(cg) = TARGET(g)->iso;
	THIRD(cg)  = THIRD(g)->iso;
	X(SOURCE(cg)) = X(SOURCE(g));
	Y(SOURCE(cg)) = Y(SOURCE(g));
	X(TARGET(cg)) = X(TARGET(g));
	Y(TARGET(cg)) = Y(TARGET(g));
	X(THIRD(cg))  = X(THIRD(g));
	Y(THIRD(cg))  = Y(THIRD(g));

	for_all_nodes(cg,new_node) {
		if ((new_node != SOURCE(cg)) && (new_node != THIRD(cg) ) && 
		    (new_node != TARGET(cg)) && node_is_candidate(new_node)) { 
			CANDLIST(cg) = add_to_slist(CANDLIST(cg),MAKE_DATA_ATTR(new_node)); 
		}
	} end_for_all_nodes(cg,new_node);

	return (cg);
}

/*===========================================================================*/
/*
	void	reintroduce(cg)
	Sgraph	cg;

	This procedure is almost the same as the procedure update_coordinates.
	cg was created by contract_edge and has all vertices except the node v.
	This node v has to be placed (with the procedure place) into the polygon
	formed by the v-neighbors. This function updates the coordinates of all
	nodes of the father graph of cg and therefore also the coordinates of
	the v-neighbors.
*/
/*===========================================================================*/

void	reintroduce(Sgraph cg)
{
	Snode n;

	for_all_nodes(cg,n) {
		X(BACKNODE(n)) = X(n);
		Y(BACKNODE(n)) = Y(n);
	} end_for_all_nodes(cg,n);
}

/*===========================================================================*/
/*
	Point	get_crossing_point (l1, l2)
	Line	l1,l2;

	This function computes the crossing point of the two lines l1 and l2.
	If l1 and l2 are parallel, empty_point is returned. If not so,
	the crossing point is computed by using the standard form for
	straight lines y = y0 + m * (x- x0). Because the tangens is not defined
	for 90 resp. 270 degrees, there have to be considered different cases.
*/
/*===========================================================================*/

Point	get_crossing_point (Line l1, Line l2)
{
	Point p;
	double x,y;
	double m1,m2;

	if (l1->angle == l2->angle) return (empty_point);
	if (l1->angle == l2->angle + M_PI) return (empty_point);
	if (l2->angle == l1->angle + M_PI) return (empty_point);

	if (cos(l1->angle) == 0.0) {
		m2 = tan(l2->angle);
		x = l1->x;
		y = l2->y + m2 * (x - l2->x);
		p = make_point(x,y);
	} else if (cos(l2->angle) == 0.0) {
		m1 = tan(l1->angle);
		x = l2->x;
		y = l1->y + m1 * (x - l1->x);
		p = make_point(x,y);
	} else {
		m1 = tan(l1->angle);
		m2 = tan(l2->angle);
		x = (l1->y - l2->y - m1 * l1->x + m2 * l2->x) / (m2 - m1);
		y = l1->y + m1 * (x - l1->x);
		p = make_point(x,y);
	}

	return (p);
}

/*===========================================================================*/
/*
	bool	point_is_in_kernel (p, v)
	Point	p;
	Snode	v;

	This function decides whether the point p is in the kernel of the polygon
	formed by the v-neighbors. For every pair of neighbored nodes a line
	is created. By using the function point_is_left_to_line it is checked
	whether p is in the left half plane of this line. If p is in all left
	half planes of all such "neighbor lines", then this is equivalent to say
	p is in the kernel.
	This function is the second check function whether	the node v can be
	placed at the position p. The first is point_is_low_enough.
*/
/*===========================================================================*/

bool	point_is_in_kernel (Point p, Snode v)
{
	Slist l;
	Snode w1,w2;
	Line  line;
	bool  is_in_kernel;

	is_in_kernel = true;
	for_slist(EMBEDLIST(v),l) {
		w1 = NODE(l);
		w2 = NODE(l->suc);
		line = make_line(X(w1),Y(w1),X(w2),Y(w2));
		is_in_kernel = point_is_left_to_line(p,line);
		free_line(line);
		if (! is_in_kernel) break;
	} end_for_slist(EMBEDLIST(v),l);

	return (is_in_kernel);
}

/*===========================================================================*/
/*
	bool	point_is_low_enough (p, v)
	Point	p;
	Snode	v;

	This function decides whether the point p is low enough with respect to
	all successors of v. If it is so, the position p is a possible candidate
	for the placement of the node v.
	This function is the first check function whether the node v can be
	placed at the position p. The second is point_is_in_kernel. Only if
	these two function returns true, the position p is checked whether
	the slopes of the incident edges of v are o.k.!
*/
/*===========================================================================*/

bool	point_is_low_enough (Point p, Snode v)
{
	Slist l; 
	Snode w; 
	bool  is_low_enough; 
 
	is_low_enough = true;
	for_slist(EMBEDLIST(v),l) {
		w = NODE(l);
		if (RANK(w) < RANK(v)) continue;
		is_low_enough = (p->y < Y(w));
		if (! is_low_enough) break;
	} end_for_slist(EMBEDLIST(v),l);

	return(is_low_enough);
}

/*===========================================================================*/
/*
	double	next_distance(t)
	double	t;

	This little function returns the next value that is the distance
	between u and the next point for trying to place v on.
*/
/*===========================================================================*/

double	next_distance(double t)
{
	return (t * (double)st_settings.t_div/100.0);
}

/*===========================================================================*/
/*
	Line	get_vline (g,u,v)
	Sgraph	g;
	Snode	u,v;

	This very important function computes the line where v must
	be placed on.
*/
/*===========================================================================*/

Line	get_vline (Sgraph g, Snode u, Snode v)
{
	Slist	l;
	Snode	lnode,rnode;
	Angle	la,ra,mina,maxa,vmin,vmax;
	Line	vline;
	struct sector {
		Angle min;
		Angle max; }	ls,rs,mins,maxs;

	for_slist(EMBEDLIST(v),l) {
		if (NODE(l) == u) {
			lnode = NODE(l->pre);
			rnode = NODE(l->suc);
			break;
		}
	} end_for_slist(EMBEDLIST(v),l);

	la = st_upward_draw_get_angle(X(lnode),Y(lnode),X(u),Y(u));
	ra = st_upward_draw_get_angle(X(u),Y(u),X(rnode),Y(rnode));
	mina = EXTERN_MIN(g) - ALPHA(g);
	if (mina < 0.0) mina = 0.0;
	maxa = M_PI +(EXTERN_MAX(g) + ALPHA(g));
	if (maxa > 2.0 * M_PI) maxa = 0.0;

	if (la > M_PI) la -= 2.0 * M_PI;
	if (ra > M_PI) ra -= 2.0 * M_PI;
	if (mina > M_PI) mina -= 2.0 * M_PI;
	if (maxa > M_PI) maxa -= 2.0 * M_PI;

	ls.min = la;
	ls.max = la + M_PI;
	rs.min = ra;
	rs.max = ra + M_PI;
	mins.min = mina;
	mins.max = mina + M_PI;
	maxs.min = maxa;
	maxs.max = maxa + M_PI;

	vmin = ls.min;
	if (vmin < rs.min) vmin = rs.min;
	if (vmin < mins.min) vmin = mins.min;
	if (vmin < maxs.min) vmin = maxs.min;
	vmax = ls.max;
	if (rs.max < vmax) vmax = rs.max;
	if (mins.max < vmax) vmax = mins.max;
	if (maxs.max < vmax) vmax = maxs.max;

	vline = make_line_with_angle(X(u),Y(u),(vmin + vmax)/2);

	return (vline);
}

/*===========================================================================*/
/*
Local	void	place (g, u, v)
	Sgraph	g;
	Snode	u,v;

	This procedure looks for a legal placement of the node v in the polygon
	formed by the v-neighbors. All nodes of the original graph (except the
	nodes of the external face) must pass this procedure (as node v).
	Therefore this	procedure is perhaps the most important of this algorithm.
	A legal placement for v must satisfy the following conditions:
	1. Every vertex of the v-polygon must be properly visible from v
		<=> v is in the kernel of that v-polygon.
	2. The slope of every edge incident upon v must be in the intervall
		[EXTERN_MIN(g) - ALPHA(g), EXTERN_MAX(g) + ALPHA(g)]
	3. And last but not least: The result should be an upward_drawing
		(checked by the function point_is_low_enough).
	To satisfy these conditions a lot of geometrical knowledge is used.

	(One problem of this algorithm besides others is that according placing
	strategy the nodes` placement is dependant upon the slopes of the mates
	(details see [DiBatTam88]). If we have a very large graph with a lot
	of nodes, there is the possibility that some nodes of a polygon have
	(almost) the same coordinates, although we are calculating with reel 
	numbers in double format.
	If we have such a situation, we cannot find a legal position
	for v, because there is no proper kernel of the polygon. We overcome
	this situation by simply placing v on its highest predecessor. This
	is almost no quality change for the worse, because before placing v we have
	already some nodes with the same coordinates and the drawings are 
	already very bad.)

	First the left and the right wedge are created, just as two lines that
	describe the slopes of the external face. v must placed so that its
	position is in the left halve plane of all that lines. This yields
	two angles v_min and v_max. A straight line through u and v must have
	an angle that is greater than v_min and less than v_max. The angle
	(v_min + v_max) / 2 does it. Therefore there is a straight line named
	v_line on which the node v must be placed. Now the distance of v to u
	remains unknown. The first distance that is checked is given by the
	user defined paramater max_len (in % of the user defined parameter st_len).
	From this, a point p on v_line with distance t to u is computed and
	checked whether it satisfies the above conditions. If it does not
	satisfy these conditions, a new point on v_line must be checked. This
	point is now closer to u. The difference between the former point and
	the new point is given by the function next_distance. The user has the
	abitily to control this function by the parameter t_div. t_div simply
	describes in % how much the former distance is shortened. If t_div is
	50, then the distance will always be halved and so on. If t_div is close
	to 100, then the entire algorithm needs more absolut time, but the
	placement of v is as much as high as it is possible. If t_div is close
	to 0, then the node v will be placed not far away from u. Sometimes
	controlling the shortening of the distance has no influence on the later
	layout, but sometimes there are enormous differences between layouts
	with different t_div. It depends upon the original graph.
	But nevertheless 50 is a good choice for t_div.

	ATTENTION: There is again a difference between this implementation and
	the original algorithm in [DiBatTam88] concerning the tolerance angle.
	There, the tolerance angle ALPHA(g) is always halved with every recursive
	call of upward_draw. This is right if we are only considering the
	theoretical side. But for practical purpose, this tolerance angle
	always becomes smaller and smaller and finally it is very close to 0.
	This implies that the node position for v becomes closer and closer to
	u, and this implies that we yield a layout in which almost all nodes
	are accumulated near by each other. The result is obviously very bad.
	Therefore I decide to do it without this consecutive halvening of the
	tolerance angle. I use always the user defined parameter ALPHA(g).
	I could use a global variable for that, but I decide to use a graph
	attribut ALPHA(g), so that future changes of this implementation could
	easily be made.
*/
/*===========================================================================*/

Local void	place (Sgraph g, Snode u, Snode v)	/* Changed to local MH 23/9/94 */
       		  
     		    
{
	Snode 	w,w1;
	Line	 w_to_u,w_to_p,v_line;
	double t,ddx,ddy;
	Slist l,l1;
	Point p;
	bool	slopes_ok;
	bool	some_nodes_have_same_coordinates;

	some_nodes_have_same_coordinates = false;
	for_slist(EMBEDLIST(v),l) {
		w = NODE(l);
		for_slist(EMBEDLIST(v),l1) {
			w1 = NODE(l1);
			if (w == w1) continue;
			if (X(w) >= X(w1)) ddx = X(w) - X(w1); else ddx = X(w1) - X(w);
			if (Y(w) >= Y(w1)) ddy = Y(w) - Y(w1); else ddy = Y(w1) - Y(w);
			if ((ddx <= EPS) && (ddy <= EPS)) {
				some_nodes_have_same_coordinates = true;
			}
		} end_for_slist(EMBEDLIST(v),l1);
	} end_for_slist(EMBEDLIST(v),l);

	if (some_nodes_have_same_coordinates) {

		X(v) = X(u);
		Y(v) = Y(u);

	} else {

		v_line = get_vline (g,u,v);
 
		t = (double)st_settings.max_len / 100.0 * 2.0 * BASE_LENGTH; 

		slopes_ok = false; 
		while (! slopes_ok) { 
			p = get_point_on_line(v_line,t); 
			if (! point_is_low_enough(p,v)) { 
				free_point(p); 
				t = next_distance(t);
				continue; 
			} 
			if ((! point_is_in_kernel(p,v))) { 
				free_point(p); 
				t = next_distance(t); 
				continue; 
			} 
			slopes_ok = true; 
			for_slist(EMBEDLIST(v),l) { 
				w = NODE(l); 
				if (slopes_ok && (w != u)) { 
					w_to_u = make_line(X(w),Y(w),X(u),Y(u)); 
					w_to_p = make_line(X(w),Y(w),p->x,p->y); 
					slopes_ok = (diff_angle(w_to_u,w_to_p) <= (ALPHA(g))/2.0);
					free_line(w_to_u); 
					free_line(w_to_p); 
				}
			} end_for_slist(EMBEDLIST(v),l);
			if (slopes_ok) {
				X(v) = p->x;
				Y(v) = p->y;
				free_line(v_line);
				slopes_ok = true;
			} else {
				t = next_distance(t);
			}
			free_point(p);
		}
	}
}

/*===========================================================================*/
/*
	void	scan_nodes(g, cycle)
	Sgraph  g;
	Slist	cycle;

	This procedure marks the nodes of g whether they are extern, intern,
	or on the cycle. The parameter cycle is the Slist returned from the
	function test_on_cycle. This cycle separates the entire graph in
	an extern and an intern part. Each part is independantly upward drawn.
	The nodes are created with the state INTERN, and not all three external
	nodes SOURCE(g), THIRD(g), and TARGET(g) can be part of the cycle.
	Therefore there must	exist at least one such node that is not on the cycle.
	Note the loop that marks the EXTERN nodes. It works because add_to_slist
	inserts a new element as predecessor of the first element.
*/
/*===========================================================================*/

void	scan_nodes(Sgraph g, Slist cycle)
{
	Slist l,external_list,el;

	STATE(NODE(cycle)) 	    = ON_THE_CYCLE;
	STATE(NODE(cycle->suc)) = ON_THE_CYCLE;
	STATE(NODE(cycle->pre)) = ON_THE_CYCLE;

	if (STATE(SOURCE(g)) != ON_THE_CYCLE) {
		STATE(SOURCE(g)) = EXTERN;
		external_list = new_slist(MAKE_DATA_ATTR(SOURCE(g)));
	} else if (STATE(THIRD(g)) != ON_THE_CYCLE) {
		STATE(THIRD(g)) = EXTERN;
		external_list = new_slist(MAKE_DATA_ATTR(THIRD(g)));
	} else if (STATE(TARGET(g)) != ON_THE_CYCLE) {
		STATE(TARGET(g)) = EXTERN;
		external_list = new_slist(MAKE_DATA_ATTR(TARGET(g)));
	}

	el = external_list;
	do {
		for_slist(EMBEDLIST(NODE(el)),l) {
			if (STATE(NODE(l)) == INTERN) {
				STATE(NODE(l)) = EXTERN;
				external_list = add_to_slist(external_list,MAKE_DATA_ATTR(NODE(l)));
			}
		} end_for_slist(EMBEDLIST(NODE(el)),l);
		el = el->suc;
	} while (el != external_list);

	free_slist(external_list);
}

/*===========================================================================*/
/*
	Sgraph	get_external_graph (g)
	Sgraph 	g;

	This function creates the extern part of g with respect to the cycle.
	The nodes are marked by the procedure scan_edges. Therefore this
	function has only to create but not to compute the extern part.
*/
/*===========================================================================*/

Sgraph	get_external_graph (Sgraph g)
{
	Sgraph g_ext;
	Snode  old_node,new_node,other_old_node,other_new_node;
	Slist  l;

	g_ext = make_new_graph();
	INK(g_ext) = INK(g) + 1;
	ALPHA(g_ext) = ALPHA(g);
	EXTERN_MIN(g_ext) = EXTERN_MIN(g);
	EXTERN_MAX(g_ext) = EXTERN_MAX(g);

	for_all_nodes(g,old_node) {
		if (STATE(old_node) == INTERN) continue;
		new_node = make_new_node(g_ext,old_node);
	} end_for_all_nodes(g,old_node);

	for_all_nodes(g,old_node) {
		if (STATE(old_node) == INTERN) continue;
		new_node = old_node->iso;
		for_slist(EMBEDLIST(old_node),l) {
			other_old_node = NODE(l);
			if (STATE(other_old_node) == INTERN) continue;
			other_new_node = other_old_node->iso;
			EMBEDLIST(new_node) = add_to_slist(EMBEDLIST(new_node),MAKE_DATA_ATTR(other_new_node));
		} end_for_slist(EMBEDLIST(old_node),l);
	} end_for_all_nodes(g,old_node);

	SOURCE(g_ext) = SOURCE(g)->iso;
	TARGET(g_ext) = TARGET(g)->iso;
	THIRD(g_ext)  = THIRD(g)->iso;
	X(SOURCE(g_ext)) = X(SOURCE(g));
	Y(SOURCE(g_ext)) = Y(SOURCE(g));
	X(TARGET(g_ext)) = X(TARGET(g));
	Y(TARGET(g_ext)) = Y(TARGET(g));
	X(THIRD(g_ext))  = X(THIRD(g));
	Y(THIRD(g_ext))  = Y(THIRD(g));

	for_all_nodes(g_ext,new_node) {
		if ((new_node != SOURCE(g_ext)) && (new_node != THIRD(g_ext) ) &&
			 (new_node != TARGET(g_ext)) && node_is_candidate(new_node)) {
			CANDLIST(g_ext) = add_to_slist(CANDLIST(g_ext),MAKE_DATA_ATTR(new_node));
		}
	} end_for_all_nodes(g_ext,new_node);

	return (g_ext);
}

/*===========================================================================*/
/*
	Sgraph	get_internal_graph(g, cycle)
	Sgraph	g;
	Slist	cycle;

	This function creates the intern part of g with respect to the cycle.
	The nodes are marked by the procedure scan_edges. Therefore this
	function has only to create but not to compute the intern part.
*/
/*===========================================================================*/

Sgraph	get_internal_graph(Sgraph g, Slist cycle)
{
	Sgraph g_int;
	Snode  old_node,new_node,other_old_node,other_new_node;
	Slist  l;

	g_int = make_new_graph();
	INK(g_int) = INK(g) + 1;
	ALPHA(g_int) = ALPHA(g);

	for_all_nodes(g,old_node) {
		if (STATE(old_node) == EXTERN) continue;
		new_node = make_new_node(g_int,old_node);
	} end_for_all_nodes(g,old_node);

	for_all_nodes(g,old_node) {
		if (STATE(old_node) == EXTERN) continue;
		new_node = old_node->iso;
		for_slist(EMBEDLIST(old_node),l) {
			other_old_node = NODE(l);
			if (STATE(other_old_node) == EXTERN) continue;
			other_new_node = other_old_node->iso;
			EMBEDLIST(new_node) = add_to_slist(EMBEDLIST(new_node),MAKE_DATA_ATTR(other_new_node));
		} end_for_slist(EMBEDLIST(old_node),l);
	} end_for_all_nodes(g,old_node);

	SOURCE(g_int) = NODE(cycle)->iso;
	THIRD(g_int)  = NODE(cycle->suc)->iso;
	TARGET(g_int) = NODE(cycle->pre)->iso;

	for_all_nodes(g_int,new_node) {
		if ((new_node != SOURCE(g_int)) && (new_node != THIRD(g_int) ) &&
			 (new_node != TARGET(g_int)) && node_is_candidate(new_node)) {
			CANDLIST(g_int) = add_to_slist(CANDLIST(g_int),MAKE_DATA_ATTR(new_node));
		}
	} end_for_all_nodes(g_int,new_node);

	return (g_int);
}

/*===========================================================================*/
/*
	void		update_external_face(g_int)
	Sgraph	g_int;

	After layouting the extern part of the graph, the coordinates of the
	cycle nodes are fixed. These cycle nodes form the external face of
	the intern part of the graph. Therefore the coordinates of the counterparts
	of these	cycle nodes in the intern part must be fixed with the just
	computed coordinates. Also the quantities of the external face of the
	intern part must be updated.
*/
/*===========================================================================*/

void	update_external_face(Sgraph g_int)
{
	Angle st_angle,sv_angle,vt_angle;

	X(SOURCE(g_int)) = X(BACKNODE(SOURCE(g_int)));
	Y(SOURCE(g_int)) = Y(BACKNODE(SOURCE(g_int)));
	X(THIRD(g_int))  = X(BACKNODE(THIRD(g_int)));
	Y(THIRD(g_int))  = Y(BACKNODE(THIRD(g_int)));
	X(TARGET(g_int)) = X(BACKNODE(TARGET(g_int)));
	Y(TARGET(g_int)) = Y(BACKNODE(TARGET(g_int)));

	st_angle = st_upward_draw_get_angle(X(SOURCE(g_int)), Y(SOURCE(g_int)), X(TARGET(g_int)), Y(TARGET(g_int)));
	sv_angle = st_upward_draw_get_angle(X(SOURCE(g_int)), Y(SOURCE(g_int)), X(THIRD(g_int)) , Y(THIRD(g_int)));
	vt_angle = st_upward_draw_get_angle(X(THIRD(g_int)) , Y(THIRD(g_int)),  X(TARGET(g_int)), Y(TARGET(g_int)));

	EXTERN_MIN(g_int) = st_angle;
	if (sv_angle < EXTERN_MIN(g_int)) EXTERN_MIN(g_int) = sv_angle;
	if (vt_angle < EXTERN_MIN(g_int)) EXTERN_MIN(g_int) = vt_angle;
	EXTERN_MAX(g_int) = st_angle;
	if (EXTERN_MAX(g_int) < sv_angle) EXTERN_MAX(g_int) = sv_angle;
	if (EXTERN_MAX(g_int) < vt_angle) EXTERN_MAX(g_int) = vt_angle;
}


/*===========================================================================*/
/*
	void	upward_draw (g)
	Sgraph	g;

	This is the base procedure of the drawing phase and controls the
	recursive calls. The original text of [DiBatTam88] is inserted as
	comments, just as a little explanation how the original statements
	are implemented here.
*/
/*===========================================================================*/

/*
	procedure UPWARD_DRAW (G,L,D,a)
	{ constructs an upward drawing of the triangular st-graph G }
	{ G is represented by the adjacency lists A(v) and the neighbor trees T(v) }
	{ L points to the list of internal vertices of G with degree at most five }
	{ D ist the assigned external face; a is the tolerance angle }

	HERE:
	The procedure has only one parameter g. All quantities of G, L, D, and a
	are included in the structure of g. G is g itself, L is the graph attribut
	CANDLIST(g), D is given by the assigend coordinates of the external face
	nodes, and a is ALPHA(g). Moreover all adjacency lists A(v) are given as
	the node attributes EMBEDLIST(n) und the neighbor trees are not used in
	this implementation (details see test_on_cycle).
*/

void	upward_draw (Sgraph g)
{
	Snode 	u,v;
	Slist 	cycle;
	Sgraph 	cg,g_ext,g_int;


	/*
		if G has three vertices
	*/

	if (graph_has_three_vertices(g)) { /* then */

		/*
			draw G as D

			HERE: The coordinates of g are given back to the former instance of g.
			g only consists of the external face nodes and the coordinates of
			these nodes are already given by the initialisation.
		*/
		update_coordinates(g);

	} else { /* else begin */

		/*
			SELECT a vertex v from L;

			HERE: The user has the ability to control this selecting
			(details see select_candidate).
		*/
		v = select_candidate(g);


		/*
			SCAN A(v) to find the predecessor u of v with highest number;

			HERE: The function get_highest_pred performs this search.
		*/

		u = get_highest_pred(v);

		/*
			TEST if the cycle c of the neighbors of v has a chord with endpoint
			u by searching in the neighbor trees T(u)

			HERE: There are no neighbor trees (details see test_on_cycle)!
		*/

		cycle = test_on_cycle(u,v);

		/*
			if such a chord (u, w) exists

			HERE: If such a chord does not exist, test_on_cycle has returned
			empty_slist.
		*/

		if (cycle != empty_slist) { /* then begin { Case 1 }*/

			/*
				let G1 and G2 be the subgraphs of G external and internal to
				the cycle l = (u, v, w) respectively
				DECOMPOSE the adjacency list structure of G into the structures
				for G1 and G2 (the vertices and edges of l are duplicated)
				SPLIT the list L into the lists L1 and L2, as follows:
					repeat
						VISITSTEP (G1,L1)
						VISITSTEP (G2,L2)
					until visit of G1 or G2 is completed
					MOVE the remaining elements of L to the list Li of the graph
					Gi whose visit has not been completed
					REMOVE, possibly, u, v, and w from L1;

				HERE: All the above statements are done by the procedure scan_nodes
				and the functions get_external_graph and get_internal_graph.
			*/

			scan_nodes(g,cycle);

			g_ext = get_external_graph(g);


			g_int = get_internal_graph(g,cycle);


			/*
				UPWARD_DRAW (G1, L1, D, a/2)

				HERE: The external graph will be upward drawn and the coordinates
				of its nodes will be given to their back_nodes. The tolerance
				angle is not halved (details see place).
			*/
			upward_draw(g_ext);
			update_coordinates(g_ext);

			/*
				let C be the drawing of cycle l in G1
				UPWARD_DRAW (G2, L2, C, a/2)

				HERE: The coordinates of the external face of the internal graph
				are assigned according to their position computed by upward drawing
				the external graph. Then it will be upward drawn.
				The tolerance angle is not halved (details see place).
			*/

			update_external_face(g_int);
			upward_draw(g_int);
			update_coordinates(g_int);

			/*
				MERGE the adjacency lists and candidate lists of G1 and G2

				HERE: Only the coordinates are given back to the former instance.
				After that: Allocated memory of temporary datas will be freed.
			*/

			update_coordinates(g);
			remove_new_graph(g_ext);
			remove_new_graph(g_int);
			free_slist(cycle);

			/* end { then } */

		} else { /* else begin { Case 2 } */

			/*
				CONTRACT edge (u, v), i.e.
					remove vertex v and its incidents edges
					add new edges incident upon u to triangulate cycle c
					modify accordingly the neighbor trees of the vertices of c
					modify accordingly the list L

				HERE: contract_edge creates a new graph with the desired
				conditions. The neighbor trees are not used.
			*/

			cg = contract_edge(g,u,v);

			/*
				UPWARD_DRAW (G, L, D, a/2)

				HERE: The smaller graph will be upward drawn.
			*/

			upward_draw(cg);


			/*
				REINTRODUCE edge (u, v) and restore the data structures of G

				HERE: reintroduce simply updates the coordinates. Restoring is
				not necessary.
			*/

			reintroduce (cg);

			/*
				PLACE in suitable position vertex v

				HERE: v will be placed as required (details see place).
				After that: The coordinates are given back and allocated memory
				of temporary datas is freed.
			*/
			place(g,u,v);
			update_coordinates(g);
			remove_new_graph(cg);
		}
	}
}

