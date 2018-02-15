package quoggles;

import org.graffiti.plugin.algorithm.AbstractAlgorithm;

/**
 * The algorithm class that starts the QUOGGLES system. 
 */
public class QAlgorithm extends AbstractAlgorithm
{
    //~ Static fields/initializers =============================================

    /** The main QUOGGLES class */
    private QMain qMain = null;
    
    //~ Methods ================================================================

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName()
    {
        return "QUOGGLES";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute()
    {
        if(qMain == null)
        {
            qMain = new QMain();
            qMain.setGraph(graph);
        } else {
            qMain.setGraph(graph);
        }
        qMain.showQDialog(false);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    public void reset()
    {
        graph = null;
    }
}

//------------------------------------------------------------------------------
//   end of file
//------------------------------------------------------------------------------
