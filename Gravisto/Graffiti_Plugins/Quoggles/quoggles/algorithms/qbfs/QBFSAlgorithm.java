package quoggles.algorithms.qbfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.NodeParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.util.Queue;

/**
 * An implementation of the QBFSAlgorithm algorithm.
 *
 * @version $Revision: 491 $
 */
public class QBFSAlgorithm
    extends AbstractAlgorithm
{
    //~ Instance fields ========================================================

    /** DOCUMENT ME! */
    private Node sourceNode = null;
    
    private ArrayList bfsNodeList = null;

    //~ Methods ================================================================

    /**
     * @return
     */
    public ArrayList getBfsNodeList() {
        return bfsNodeList;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName()
    {
        return "Quoggles-BFS";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#
     *      setParameters(org.graffiti.plugin.algorithm.Parameter)
     */
    public void setParameters(Parameter[] params)
    {
        this.parameters = params;
        sourceNode = ((NodeParameter)params[0]).getNode();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getParameters()
     */
    public Parameter[] getParameters()
    {
        NodeParameter sourceNodeParam = new NodeParameter("Start node",
                "QBFSAlgorithm will start with the only selected node.");

        return new Parameter[] { sourceNodeParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    public void check()
        throws PreconditionException
    {
        if(sourceNode == null)
        {
            throw new PreconditionException(
                "QBFSAlgorithm needs exactly one source node.");
        }

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute(Graph)  The given
     *      graph must have at least one node.
     */
    public void execute()
    {
        Queue q = new Queue();
        bfsNodeList = new ArrayList();

        // nodeNumMap contains a mapping from node to an integer, the bfsnum
        Map nodeNumMap = new HashMap();

        q.addLast(sourceNode);
        nodeNumMap.put(sourceNode, new Integer(0));
        bfsNodeList.add(sourceNode);

        while(!q.isEmpty())
        {
            Node v = (Node) q.removeFirst();

            // mark all neighbours and add all unmarked neighbours
            // of v to the queue
            for(Iterator neighbours = v.getNeighborsIterator();
                neighbours.hasNext();)
            {
                Node neighbour = (Node) neighbours.next();

                if(!nodeNumMap.containsKey(neighbour))
                {
                    Integer bfsNum = new Integer(((Integer) nodeNumMap.get(v)).intValue() +
                            1);
                    nodeNumMap.put(neighbour, bfsNum);
                    bfsNodeList.ensureCapacity(bfsNum.intValue());
                    bfsNodeList.add(bfsNum.intValue(), neighbour);
                    q.addLast(neighbour);
                }
            }
        }

    }

}

//------------------------------------------------------------------------------
//   end of file
//------------------------------------------------------------------------------
