// =============================================================================
//
//   StartAddRectSelection.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.commonactions;

import java.util.HashSet;
import java.util.Set;

import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.selection.Selection;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("startAddRectSelection")
public class StartAddRectSelection extends CommonAction {
    private Set<GraphElement> initialElements;

    public StartAddRectSelection() {
        initialElements = new HashSet<GraphElement>();
    }

    protected Set<GraphElement> getInitialElements() {
        return initialElements;
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            InteractiveView<?> view, EditorSession session) {
        Selection selection = session.getSelectionModel().getActiveSelection();
        initialElements.clear();
        initialElements.addAll(selection.getElements());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
