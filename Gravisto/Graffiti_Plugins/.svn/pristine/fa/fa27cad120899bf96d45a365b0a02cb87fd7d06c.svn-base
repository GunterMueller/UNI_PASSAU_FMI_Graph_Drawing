package org.visnacom.sugiyama.algorithm;

import java.util.*;

import org.visnacom.model.ActionExpand;
import org.visnacom.model.Edge;
import org.visnacom.model.ActionExpand.Mapping;
import org.visnacom.sugiyama.model.*;
import org.visnacom.sugiyama.model.SugiActionExpand.SugiMapping;


/**
 * contains the operations for normalization during expand.
 */
public class ExpandNormalization {

    /**
     *
     * @param sugiAction DOCUMENT ME!
     */
    public static void expand(SugiActionExpand sugiAction) {
        SugiCompoundGraph s = sugiAction.s;
    
        /* 1st part */
        List changedInternalEdges = new LinkedList();
        for(Iterator it = sugiAction.internalEdges.iterator(); it.hasNext();) {
            SugiEdge e = (SugiEdge) it.next();
            changedInternalEdges.addAll(Normalization.normalizeEdge(s, e));
        }
    
        sugiAction.internalEdges = changedInternalEdges;
    
        /* 2nd part */
    
        //foreach old edge (v,u) or (u,v)...
        for(Iterator it = sugiAction.origAction.getMappinsIterator();
            it.hasNext();) {
            ActionExpand.Mapping m = (Mapping) it.next();
            List edgePath = s.getCorrespondingEdges(m.oldEdge);
            SugiEdge oldEdge;
            SugiNode v = sugiAction.v;
            SugiNode paV = (SugiNode) s.getParent(v);
            SugiNode u;
            boolean isFromVtoU;
    
            //determine matching edge segment -> oldEdge
            SugiEdge upperSegment = (SugiEdge) edgePath.get(0);
            SugiEdge lowerSegment = (SugiEdge) edgePath.get(edgePath.size() - 1);
            if(upperSegment.getSource() == v) {
                isFromVtoU = true;
                u = (SugiNode) upperSegment.getTarget();
                oldEdge = upperSegment;
            } else {
                assert lowerSegment.getTarget() == v;
                isFromVtoU = false;
                u = (SugiNode) lowerSegment.getSource();
                oldEdge = lowerSegment;
            }
    
            // insert dummy node
            DummyNode p_u = s.newDummyLeaf(paV, v.getClev().getTail(),DummyNode.LOCAL_OR_EXTERNAL);
    
            //add new edge (p_u, u), respectivly (u, p_u)
            SugiEdge p_uToUOrUToP_u;
            if(isFromVtoU) {
                p_uToUOrUToP_u = (SugiEdge) s.newEdge(p_u, u);
            } else {
                p_uToUOrUToP_u = (SugiEdge) s.newEdge(u, p_u);
            }
    
            if(m.oldEdgeDeleted) {
                s.deleteEdge(oldEdge);
            }
    
            /*
             * newEdge does not yet belong to the edge path I keep the old path
             * consistent until the splitting is done
             */
    
            //edgepath is kept accessible via "m.oldEdge"
            SugiMapping sm = sugiAction.addMapping(p_u, u, oldEdge,
                    p_uToUOrUToP_u, isFromVtoU, m);
    
            //2nd problem (v',c) or (c,v')
            assert !m.newEdges.isEmpty();
            for(Iterator it2 = m.newEdges.iterator(); it2.hasNext();) {
                Edge e = (Edge) it2.next();
                SugiNode vDash;
                if(e.getSource() == m.oldEdge.getSource()) {
                    vDash = s.getCorrespondingNode(e.getTarget());
                } else {
                    assert e.getTarget() == m.oldEdge.getTarget();
                    vDash = s.getCorrespondingNode(e.getSource());
                }
    
                assert s.getParent(vDash) == v;
    
                DummyNode c = s.ensureDummyChild(p_u,
                        vDash.getClev().getTail() + (isFromVtoU ? 1 : -1));
    
                if(isFromVtoU) {
                    SugiEdge vDashToC = (SugiEdge) s.newEdge(vDash, c);
                    sm.addEdge(vDashToC);
                    s.establishEdgeMapping(e, vDashToC);
                } else {
                    SugiEdge CtovDash = (SugiEdge) s.newEdge(c, vDash);
                    sm.addEdge(CtovDash);
                    s.establishEdgeMapping(e, CtovDash);
                }
            }
        }
    
        assert s.checkEdgeMappings(0);
    }

}
