/* This software is distributed under the Lesser General Public License */
//
// cfr_one_dimension.h
//
// Definitions of the class FR_One_Dimensional_Constraint.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_cfr_layout/cfr_one_dimension.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:32 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#ifndef CFR_ONE_DIMENSION_H
#define CFR_ONE_DIMENSION_H

#include <set>

#define ERR_CYCLE -1
#define ERR_LOOP -2
#define UNMARKED -3

//
// This defintion of compare looks roundabout.
// But it compiles clean with LEDA-3.3
// _AND_ LEDA-3.4. If in doubt do not change it.
//
// MG, 17.6.: not needed with GTL
//
// class FR_One_Dimensional_Constraint;

// inline int compare(FR_One_Dimensional_Constraint* const &x,
//     FR_One_Dimensional_Constraint* const &y)
// {
//     return *(long*)&x - *(long*)&y;
// }

class FR_One_Dimensional_Constraint
{
private:
    set<FR_One_Dimensional_Constraint*> the_upper_constraints; 
    set<FR_One_Dimensional_Constraint*> the_lower_constraints; 
    
    GT_VARIABLE (int, group_count);
    GT_VARIABLE (double, coord); 
    GT_VARIABLE (double, displacement); 
    GT_VARIABLE (double, max_size);
    
    GT_VARIABLE (unsigned, comp_num);
    GT_VARIABLE (int, dfs_num);
	
    set<FR_One_Dimensional_Constraint*> the_all_upper_constraints; 
    
public:
    FR_One_Dimensional_Constraint(const double coord);
    virtual ~FR_One_Dimensional_Constraint() {}
	
    void add_lower_constraint(FR_One_Dimensional_Constraint* constraint);
    void add_member(const double coord);
    inline void add_displacement(const double displacement)
    { this->the_displacement += displacement; }
    void add_upper_constraint(FR_One_Dimensional_Constraint* constraint);
    void calculate_constraint_displacement(const double optimal_distance,
	const double minimal_distance);
    void check_dfs(int& check_dfs_num,
	int& check_comp_num, bool& cycle, bool& loop);
    void constraint_displace(const double minimal_distance,
	const double min, const double max);
    void del_lower_constraint(FR_One_Dimensional_Constraint* constraint);
    void del_upper_constraint(FR_One_Dimensional_Constraint* constraint);
    void final_cut();
    inline const set<FR_One_Dimensional_Constraint*>& lower_constraints() 
    { return the_lower_constraints; } 
    bool has_constraints();
    void FR_One_Dimensional_Constraint::inflate_dependants(
	set<FR_One_Dimensional_Constraint*> global_upper);
    void join_members(const set<node>& new_member);
    void order_dependants(double limit, const double minimal_distance);
    void reduce_dependants(
	const set<FR_One_Dimensional_Constraint*>& global_upper);
    bool self_loop();
    inline const set<FR_One_Dimensional_Constraint*>& upper_constraints() 
    { return the_upper_constraints; } 
    inline void unconstraint_displace() {
	this->the_coord += this->the_displacement;
	this->the_displacement = 0;
    }
};

#endif



