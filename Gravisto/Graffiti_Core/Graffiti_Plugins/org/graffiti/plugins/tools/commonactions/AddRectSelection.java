// =============================================================================
//
//   AddRectSelection.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.commonactions;

import java.awt.geom.Rectangle2D;
import java.util.Set;

import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionModel;
import org.graffiti.session.EditorSession;
import org.graffiti.util.Callback;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("addRectSelection")
public class AddRectSelection extends CommonAction {
    @InSlot
    public static final Slot<Rectangle2D> rectangleSlot = Slot.create(
            "rectangle", Rectangle2D.class);

    private Set<GraphElement> initialElements;

    public AddRectSelection(StartAddRectSelection startAddRectSelectionAction) {
        initialElements = startAddRectSelectionAction.getInitialElements();
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            InteractiveView<?> view, final EditorSession session) {
        Rectangle2D rect = in.get(rectangleSlot);
        view.getGraphElementFinder().deferredTellIntersectingElements(rect,
                new Callback<Object, Set<GraphElement>>() {

                    public Object call(Set<GraphElement> set) {
                        SelectionModel model = session.getSelectionModel();
                        Selection selection = model.getActiveSelection();
                        selection.clear();
                        selection.addAll(initialElements);
                        selection.addAll(set);
                        model.selectionChanged();
                        return null;
                    }
                });
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
