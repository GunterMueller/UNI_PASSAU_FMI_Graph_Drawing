// =============================================================================
//
// .java
//
//   Copyright (c) 2004 Graffiti Team, Uni Passau
//
// =============================================================================
// $Id: DummySupportView.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * DummySupportView.java
 * 
 * 
 * Created: Sat Jun 19 14:01:56 2004
 * 
 * @author <a href="mailto:">Wolfgang Pausch</a>
 * @version 1.0
 */

public interface DummySupportView {
    public void addViewForNode(Node node);

    public void removeViewForNode(Node node);

    public void addViewForEdge(Edge edge);

    public void removeViewForEdge(Edge edge);
}
