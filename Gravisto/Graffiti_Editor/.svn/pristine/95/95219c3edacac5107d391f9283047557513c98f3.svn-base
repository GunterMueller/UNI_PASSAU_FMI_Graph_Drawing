package org.graffiti.plugins.tools.grid.trees;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugin.view.interactive.ToolAction;
import org.graffiti.plugins.algorithms.hexagonalTrees.MoveSubtreesOut;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.session.EditorSession;

/**
 * @author Marco Matzeder
 * @version $Revision$ $Date$
 */
@ActionId("contractIncomingEdge")
public class ContractIncomingEdgeAction extends ToolAction<FastView> {
    @InSlot
    public static final Slot<GraphElement> elementSlot = Slot.create("element",
            GraphElement.class);
    
    private ContractIncomingEdge contractIncomingEdge;
    
    public ContractIncomingEdgeAction(ContractIncomingEdge contractIncomingEdge) {
        super("Foo", "Bar");
        this.contractIncomingEdge = contractIncomingEdge;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        
        
        System.out.println("ölaskgjdf");
        
        System.out.println("asdf");
        
        
        
        GraffitiSingleton.runAlgorithm(contractIncomingEdge);
    }

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
