/* This software is distributed under the Lesser General Public License */
#ifndef GT_LSD_SPRINGEMBEDDER_KAMADA_H
#define GT_LSD_SPRINGEMBEDDER_KAMADA_H

//
// 
//

// Walter Bachl: 25.6.96
// Basic definitions to call sgraph-algorithms
// Implementation: main.cc


//
// Initialization procedure, Tcl/Tk naming conventions
//

extern "C" {
    int Gt_lsd_springembedder_kamada_Init (Tcl_Interp* interp);
}


//////////////////////////////////////////////////////////////////////
//
// Springembedder Kamada
//
//////////////////////////////////////////////////////////////////////


class GT_SpringKamada : public GT_Algorithm {

  protected:
    int the_edgelength;

  public:
    GT_SpringKamada (const string& name);
    virtual ~GT_SpringKamada ();
    
    virtual int edgelength () {
      return the_edgelength;
    }

    virtual void edgelength (int l) {
      the_edgelength = l;
    }

    virtual int run (GT_Graph& g);
    virtual int check (GT_Graph& g, string& message);
};


class GT_Tcl_SpringKamada : public GT_Tcl_Algorithm<GT_SpringKamada>
{
  public:
    GT_Tcl_SpringKamada (const string& name);
    virtual ~GT_Tcl_SpringKamada ();

    virtual int parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* g);
};


#endif
