/* This software is distributed under the Lesser General Public License */
#ifndef GT_LSD_SPRINGEMBEDDER_RF_H
#define GT_LSD_SPRINGEMBEDDER_RF_H

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
    int Gt_lsd_springembedder_rf_Init (Tcl_Interp* interp);
}


//////////////////////////////////////////////////////////////////////
//
// Spring RF
//
//////////////////////////////////////////////////////////////////////


class GT_SpringRf : public GT_Algorithm {

protected:
    int the_weighted, the_max_iter, the_edgelength;
    double the_max_force, the_vibration;

public:
    GT_SpringRf (const string& name);
    virtual ~GT_SpringRf ();
    
    virtual int weighted () {
        return the_weighted;
    }
    virtual void weighted (int w) {
        the_weighted = w;
    }

    virtual int maximal_iterations () {
        return the_max_iter;
    }
    virtual void maximal_iterations (int m) {
        the_max_iter = m;
    }

    virtual int edgelength () {
      return the_edgelength;
    }
    virtual void edgelength (int l) {
      the_edgelength = l;
    }

    virtual double maximal_force () {
      return the_max_force;
    }
    virtual void maximal_force (double f) {
      the_max_force = f;
    }

    virtual double vibration () {
      return the_vibration;
    }
    virtual void vibration (double v) {
      the_vibration = v;
    }


    virtual int run (GT_Graph& g);
    virtual int check (GT_Graph& g, string& message);
};


class GT_Tcl_SpringRf : public GT_Tcl_Algorithm<GT_SpringRf>
{
public:
    GT_Tcl_SpringRf (const string& name);
    virtual ~GT_Tcl_SpringRf ();
    
    virtual int parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* g);
};

#endif
