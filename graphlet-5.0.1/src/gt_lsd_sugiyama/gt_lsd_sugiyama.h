/* This software is distributed under the Lesser General Public License */
#ifndef GT_LSD_SUGIYAMA_H
#define GT_LSD_SUGIYAMA_H

//
// lsd_main.h
//

// Walter Bachl: 25.6.96
// Basic definitions to call sgraph-algorithms
// Implementation: main.cc


//
// Initialization procedure, Tcl/Tk naming conventions
//

extern "C" {
    int Gt_lsd_sugiyama_Init (Tcl_Interp* interp);
}


//////////////////////////////////////////////////////////////////////
//
// DAG Layout
//
//////////////////////////////////////////////////////////////////////


class GT_Sugiyama : public GT_Algorithm
{
protected:
    int the_vert_dist, the_horiz_dist, the_it1, the_it2,
	the_arrange, the_res_cycles, the_reduce_cross;


public:
    // Constructor
    GT_Sugiyama (const string& name);
    virtual ~GT_Sugiyama ();
    
    int vertical_distance () {
	return the_vert_dist;
    }
    int horizontal_distance () {
	return the_horiz_dist;
    }
    int arrange () {
	return the_arrange;
    }
    int resolve_cycles () {
	return the_res_cycles;
    }
    int reduce_crossings () {
	return the_reduce_cross;
    }

    void vertical_distance (int v) {
	the_vert_dist = v;
    }
    void horizontal_distance (int v) {
	 the_horiz_dist = v;
    }
    void arrange (int v) {
	the_arrange = v;
    }
    void resolve_cycles (int v) {
	the_res_cycles = v;
    }
    void reduce_crossings (int v) {
	the_reduce_cross = v;
    }


    virtual int run (GT_Graph& g);
    virtual int check (GT_Graph& g, string& message);
};



class GT_Tcl_Sugiyama : public GT_Tcl_Algorithm<GT_Sugiyama>
{   
public:
    GT_Tcl_Sugiyama (const string& name);
    virtual ~GT_Tcl_Sugiyama();
    
    virtual int parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* g);
};

#endif
