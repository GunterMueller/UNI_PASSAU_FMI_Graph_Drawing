/* This software is distributed under the Lesser General Public License */
#ifndef GT_LSD_GEM_H
#define GT_LSD_GEM_H

//
// lsd_lsd_gem.h
//

// Walter Bachl: 25.6.96
// Basic definitions to call sgraph-algorithms
// Implementation: main.cc

//
// Initialization procedure, Tcl/Tk naming conventions
//

extern "C" {
    int Gt_lsd_gem_Init (Tcl_Interp* interp);
}


//////////////////////////////////////////////////////////////////////
//
// Gem
//
//////////////////////////////////////////////////////////////////////

class GT_Gem : public GT_Algorithm {

protected:
    double the_insert_max_temp, the_insert_start_temp,
	the_insert_final_temp, the_insert_max_iter,
	the_insert_gravity, the_insert_oscilation,
	the_insert_rotation, the_insert_shake;
    int	the_insert_skip;
    double the_arrange_max_temp, the_arrange_start_temp,
	the_arrange_final_temp, the_arrange_max_iter,
	the_arrange_gravity, the_arrange_oscilation,
	the_arrange_rotation, the_arrange_shake;
    int	the_arrange_skip;
    double the_optimize_max_temp, the_optimize_start_temp,
	the_optimize_final_temp, the_optimize_max_iter,
	the_optimize_gravity, the_optimize_oscilation,
	the_optimize_rotation, the_optimize_shake;
    int	the_optimize_skip,
	the_rand, the_quality, the_default_edgelength;

public:
    GT_Gem (const string& name);
    virtual ~GT_Gem ();
    
    // Insertion
    double insertion_maximal_temperature () { 
	return the_insert_max_temp; 
    }
    double insertion_start_temperature () {
	return the_insert_start_temp;
    }
    double insertion_final_temperature () {
	return the_insert_final_temp;
    }
    double insertion_maximal_iterations () {
	return the_insert_max_iter;
    }
    double insertion_gravity () {
	return the_insert_gravity;
    }
    double insertion_oscilation () {
	return the_insert_oscilation;
    }
    double insertion_rotation () {
	return the_insert_rotation;
    }
    double insertion_shake () {
	return the_insert_shake;
    }
    int skip_insertion () {
	return the_insert_skip;
    }
    void insertion_maximal_temperature (double v) { 
	the_insert_max_temp = v; 
    }
    void insertion_start_temperature (double v) {
	the_insert_start_temp = v;
    }
    void insertion_final_temperature (double v) {
	the_insert_final_temp = v;
    }
    void insertion_maximal_iterations (double v) {
	the_insert_max_iter = v;
    }
    void insertion_gravity (double v) {
	the_insert_gravity = v;
    }
    void insertion_oscilation (double v) {
	the_insert_oscilation = v;
    }
    void insertion_rotation (double v) {
	the_insert_rotation = v;
    }
    void insertion_shake (double v) {
	the_insert_shake = v;
    }
    void skip_insertion (int v) {
	the_insert_skip = v;
    }

     // arrange
    double arrange_maximal_temperature () { 
	return the_arrange_max_temp; 
    }
    double arrange_start_temperature () {
	return the_arrange_start_temp;
    }
    double arrange_final_temperature () {
	return the_arrange_final_temp;
    }
    double arrange_maximal_iterations () {
	return the_arrange_max_iter;
    }
    double arrange_gravity () {
	return the_arrange_gravity;
    }
    double arrange_oscilation () {
	return the_arrange_oscilation;
    }
    double arrange_rotation () {
	return the_arrange_rotation;
    }
    double arrange_shake () {
	return the_arrange_shake;
    }
    int skip_arrange () {
	return the_arrange_skip;
    }
    void arrange_maximal_temperature (double v) { 
	the_arrange_max_temp = v; 
    }
    void arrange_start_temperature (double v) {
	the_arrange_start_temp = v;
    }
    void arrange_final_temperature (double v) {
	the_arrange_final_temp = v;
    }
    void arrange_maximal_iterations (double v) {
	the_arrange_max_iter = v;
    }
    void arrange_gravity (double v) {
	the_arrange_gravity = v;
    }
    void arrange_oscilation (double v) {
	the_arrange_oscilation = v;
    }
    void arrange_rotation (double v) {
	the_arrange_rotation = v;
    }
    void arrange_shake (double v) {
	the_arrange_shake = v;
    }
    void skip_arrange (int v) {
	the_arrange_skip = v;
    }

     // optimize
    double optimize_maximal_temperature () { 
	return the_optimize_max_temp; 
    }
    double optimize_start_temperature () {
	return the_optimize_start_temp;
    }
    double optimize_final_temperature () {
	return the_optimize_final_temp;
    }
    double optimize_maximal_iterations () {
	return the_optimize_max_iter;
    }
    double optimize_gravity () {
	return the_optimize_gravity;
    }
    double optimize_oscilation () {
	return the_optimize_oscilation;
    }
    double optimize_rotation () {
	return the_optimize_rotation;
    }
    double optimize_shake () {
	return the_optimize_shake;
    }
    int skip_optimize () {
	return the_optimize_skip;
    }
    void optimize_maximal_temperature (double v) { 
	the_optimize_max_temp = v; 
    }
    void optimize_start_temperature (double v) {
	the_optimize_start_temp = v;
    }
    void optimize_final_temperature (double v) {
	the_optimize_final_temp = v;
    }
    void optimize_maximal_iterations (double v) {
	the_optimize_max_iter = v;
    }
    void optimize_gravity (double v) {
	the_optimize_gravity = v;
    }
    void optimize_oscilation (double v) {
	the_optimize_oscilation = v;
    }
    void optimize_rotation (double v) {
	the_optimize_rotation = v;
    }
    void optimize_shake (double v) {
	the_optimize_shake = v;
    }
    void skip_optimize (int v) {
	the_optimize_skip = v;
    }


    void the_random (int v) {
	the_rand = v;
    }
    void quality (int v) {
	the_quality = v;
    }
    void edgelength (int v) {
	the_default_edgelength = v;
    }

    int the_random () {
	return the_rand;
    }
    int quality () {
	return the_quality;
    }
    int edgelength () {
	return the_default_edgelength;
    }



    virtual int run (GT_Graph& g);
    virtual int check (GT_Graph& g, string& message);
};



class GT_Tcl_Gem : public GT_Tcl_Algorithm<GT_Gem>
{
    
public:    
    GT_Tcl_Gem (const string& name);
    virtual ~GT_Tcl_Gem ();
    
    virtual int parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* g);
};

#endif
