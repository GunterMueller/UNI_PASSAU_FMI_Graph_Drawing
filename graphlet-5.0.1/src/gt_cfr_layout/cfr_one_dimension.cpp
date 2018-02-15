/* This software is distributed under the Lesser General Public License */
//
// cfr_one_dimension.cpp
//
// This is the implementation of the class FR_One_Dimensional_Constraint,
// which is used to build groups of nodes with the same coordinate
// in one dimension.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_cfr_layout/cfr_one_dimension.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:30 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#include <gt_base/Graphlet.h>
#include <gt_base/Graph.h>
#include <cmath>
#include <iterator>

#include "cfr_one_dimension.h"

//
// Constructor:
// FR_One_Dimensional_Constraint
//

FR_One_Dimensional_Constraint::FR_One_Dimensional_Constraint(
    const double coord)
{
    this->the_displacement = 0;
    this->the_group_count = 1;
    this->the_coord = coord;
    this->the_max_size = 0;
}


//
// add_member:
// Add a memeber to this h- or v-group.
//

void FR_One_Dimensional_Constraint::add_member(
    const double coord)
{
    double new_coord;
	
    new_coord = this->the_coord * this->the_group_count;
    new_coord += coord;
    this->the_group_count++;
    this->the_coord = new_coord / this->the_group_count;
}


//
// has_constraints:
// This just means geometrical constraints.
//

bool FR_One_Dimensional_Constraint::has_constraints()
{
    if (this->the_group_count > 1) {
	return true;
    }
	
    if ((this->the_upper_constraints.empty() == false) ||
	(this->the_lower_constraints.empty() == false)) {
	return true;
    } else {
	return false;
    }
}


//
// constraint_displace:
// Move nodes, but check if valid.
//

void FR_One_Dimensional_Constraint::constraint_displace(
    const double minimal_distance, const double min, const double max)
{
    double up = max, down = min;
	
    set<FR_One_Dimensional_Constraint*>::const_iterator it, end;

    double new_coord = this->the_coord + this->the_displacement;
	
    if(this->the_displacement > 0) {
	for(it = the_upper_constraints.begin(), end = the_upper_constraints.end();
	    it != end; ++it)
	{
	    if (((*it)->coord() - (*it)->max_size()) < up) {
		up = (*it)->coord() - (*it)->max_size();
	    }
	}
	if ((up - minimal_distance)  < (new_coord + this->max_size())) {
	    new_coord = up - minimal_distance - this->max_size();
	}
    } else {
	for(it = the_lower_constraints.begin(), end = the_lower_constraints.end();
	    it != end; ++it)
	{
	    if (((*it)->coord() + (*it)->max_size()) > down) {
		down = (*it)->coord() + (*it)->max_size();
	    }
	}
	if ((down + minimal_distance) > (new_coord - this->max_size())) {
	    new_coord = down + minimal_distance + this->max_size();
	}
    }
	
    this->the_coord = new_coord;
    this->the_displacement = 0;
}


//
// order_dependants:
// This is called from oder_nodes to produce a valid start-layout.
//

void FR_One_Dimensional_Constraint::order_dependants(
    double limit, const double minimal_distance)
{
    this->the_comp_num++;

    limit -= this->max_size();
	
    if ((this->coord() > limit)) {
	this->coord(limit);
    } else {
	limit = this->coord();
    }

    //
    // You might think the next >= should be a == 
    // BUT the '>' is needed for the top nodes.
    // 
	
    if (this->the_comp_num >= this->the_upper_constraints.size())
    {
	set<FR_One_Dimensional_Constraint*>::const_iterator it, end;
	for(it = lower_constraints().begin(), end = lower_constraints().end();
	    it != end; ++it)
	{
	    (*it)->order_dependants(
		limit - minimal_distance - this->max_size(),
		minimal_distance);
	}
    }
}


void FR_One_Dimensional_Constraint::add_upper_constraint(
    FR_One_Dimensional_Constraint* constraint)
{
    this->the_upper_constraints.insert(constraint);
    constraint->the_lower_constraints.insert(this);
}


void FR_One_Dimensional_Constraint::del_upper_constraint(
    FR_One_Dimensional_Constraint* constraint)
{
    this->the_upper_constraints.erase(constraint);
    constraint->the_lower_constraints.erase(this);
}


void FR_One_Dimensional_Constraint::add_lower_constraint(
    FR_One_Dimensional_Constraint* constraint)
{
    this->the_lower_constraints.insert(constraint);
    constraint->the_upper_constraints.insert(this);
}


void FR_One_Dimensional_Constraint::del_lower_constraint(
    FR_One_Dimensional_Constraint* constraint)
{
    this->the_lower_constraints.erase(constraint);
    constraint->the_upper_constraints.erase(this);
}


//
// calculate_constraint_displacement:
// Constraint forces are disabled by default.
//

void FR_One_Dimensional_Constraint::calculate_constraint_displacement(
    const double optimal_distance, const double minimal_distance)
{
    set<FR_One_Dimensional_Constraint*>::const_iterator it, end;

    for(it = the_upper_constraints.begin(), end = the_upper_constraints.end();
	it != end; ++it)
    {
	//
	// distance is always > 0, since this is a upper constraint 
	//

	double distance = (*it)->coord() -
	    this->the_coord - minimal_distance + 1;

	double displacement = optimal_distance / distance;
	(*it)->add_displacement(displacement);
	this->add_displacement(-displacement);
    }
}


//
// final_cut:
// This must not be called during a iteration which
// relies on the graph-structure.
//

void FR_One_Dimensional_Constraint::final_cut()
{
    list<FR_One_Dimensional_Constraint*> intersect;
    list<FR_One_Dimensional_Constraint*>::const_iterator it, end;
    
    set_intersection(the_upper_constraints.begin(),
		     the_upper_constraints.end(),
		     the_all_upper_constraints.begin(),
		     the_all_upper_constraints.end(),
		     back_inserter(intersect));

    for(it = intersect.begin(), end = intersect.end();
	it != end; ++it)
    {
	this->del_upper_constraint(*it);
    }
}


//
// reduce_dependants:
// This get called from reduce_constraints to recursivly
// search for redundant constraints.
//

void FR_One_Dimensional_Constraint::reduce_dependants(
    const set<FR_One_Dimensional_Constraint*>& global_upper)
{
#ifdef __GTL_MSVCC
    set<FR_One_Dimensional_Constraint*>::iterator it  = global_upper.begin();
    set<FR_One_Dimensional_Constraint*>::iterator end = global_upper.end();
    for (; it != end; ++it)
    {
	the_all_upper_constraints.insert(*it);
    }
#else
    the_all_upper_constraints.insert(global_upper.begin(), global_upper.end());
#endif;
    this->the_comp_num++;

    if (this->the_comp_num == this->the_upper_constraints.size()) {

	set<FR_One_Dimensional_Constraint*> new_upper;
	
	set_union(the_all_upper_constraints.begin(),
		  the_all_upper_constraints.end(),
		  the_upper_constraints.begin(),
		  the_upper_constraints.end(),
		  inserter(new_upper, new_upper.begin()));

	set<FR_One_Dimensional_Constraint*>::const_iterator it, end;
	for(it = the_lower_constraints.begin(), end = the_lower_constraints.end();
	    it != end; ++it)
	{
	    (*it)->reduce_dependants(new_upper);
	}
    }
}


bool FR_One_Dimensional_Constraint::self_loop()
{
    return (the_upper_constraints.find(this) != the_upper_constraints.end());
}


//
// check_dfs:
// This get called from check, to check for loops and cycles.
//

void FR_One_Dimensional_Constraint::check_dfs(
    int& check_dfs_num, int& check_comp_num, bool& cycle, bool& loop)
{
    set<FR_One_Dimensional_Constraint*>::const_iterator it, end;

    for(it = the_lower_constraints.begin(), end = the_lower_constraints.end();
	it != end; ++it)
    {
	if(*it == this) {
	    this->group_count(ERR_LOOP);
	    loop = true;
	}
			
	if ((*it)->dfs_num() == UNMARKED) {
	    (*it)->dfs_num(++check_dfs_num);
	    (*it)->check_dfs(check_dfs_num, check_comp_num, cycle, loop);
	    (*it)->comp_num(++check_comp_num);
	} else {
	    if ((this->dfs_num() > (*it)->dfs_num()) &&
		(this->comp_num() >= (*it)->comp_num())) {
		if((*it)->group_count() != ERR_LOOP) {
		    (*it)->group_count(ERR_CYCLE);
		    cycle = true;
		}
	    }
	}
    }
}






