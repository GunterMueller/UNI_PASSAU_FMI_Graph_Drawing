/* This software is distributed under the Lesser General Public License */
#ifndef GT_LSD_TUNKELANG_H
#define GT_LSD_TUNKELANG_H

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
    int Gt_lsd_tunkelang_Init (Tcl_Interp* interp);
}


//////////////////////////////////////////////////////////////////////
//
// Tunkelang Layout
//
//////////////////////////////////////////////////////////////////////


class GT_Tunkelang : public GT_Algorithm {

protected:
    int the_edgelength, the_cut_value, the_scan_corners;  // Parameters set in Dialog
    int the_quality, the_rec_depth, the_randomize;

public:
    GT_Tunkelang (const string& name);
    virtual ~GT_Tunkelang ();

    int edgelength () {
	return the_edgelength;
    }
    int crossings () {
	return the_cut_value;
    }
    int scan_corners () {
	return the_scan_corners;
    }
    int quality () {
	return the_quality;
    }
    int recursion_depth () {
	return the_rec_depth;
    }
    int randomize () {
	return the_randomize;
    }

    void edgelength (int v) {
	the_edgelength = v;
    }
    void crossings (int v) {
	the_cut_value = v;
    }
    void scan_corners (int v) {
	the_scan_corners = v;
    }
    void quality (int v) {
	the_quality = v;
    }
    void recursion_depth (int v) {
	the_rec_depth = v;
    }
    void randomize (int v) {
	the_randomize = v;
    }

    virtual int run (GT_Graph& g);
    virtual int check (GT_Graph& g, string& message);
};


class GT_Tcl_Tunkelang : public GT_Tcl_Algorithm<GT_Tunkelang>
{

public:
    GT_Tcl_Tunkelang (const string name);
    virtual ~GT_Tcl_Tunkelang ();
    
    virtual int parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* g);
};

#endif
