package org.graffiti.plugins.algorithms.sugiyama.layout.cyclic;

import java.util.Collection;

interface TopoSortable {
    Collection<TopoSortable> getTopoSortSuccessors();
}
