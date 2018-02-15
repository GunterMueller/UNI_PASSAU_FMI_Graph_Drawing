// =============================================================================
//
//   Faces.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Faces.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.planarity.faces;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.planarity.TestedComponent;

/**
 * Represents the faces of a connected component. Faces which were created due
 * to multiple edges or self loops are not represented.
 * 
 * @see Face
 */
public class Faces {
    /** Set of faces. */
    private Set<Face> faces;

    /**
     * Reference to connected component whose faces are represented by this
     * object.
     */
    private TestedComponent component;

    /** Mapping between nodes and darts. */
    private HashMap<Pair<Node>, Dart> mapping;

    /**
     * Calculate all faces for the given component.
     * 
     * @param component
     */
    public Faces(TestedComponent component) {
        this.component = component;

        faces = new LinkedHashSet<Face>();

        if (component.getNodes().size() <= 1)
            return;

        if (!component.isPlanar())
            return;

        // initialize all internal darts
        mapping = new LinkedHashMap<Pair<Node>, Dart>();

        for (Node n : component.getNodes()) {
            for (Edge e : n.getEdges()) {
                if ((e.getSource() != n) || (e.getSource() == e.getTarget())) {
                    continue;
                }

                Node source = e.getSource();
                Node target = e.getTarget();
                Dart st = new Dart(e, source);
                Dart ts = new Dart(e, target);
                st.setReverse(ts);
                ts.setReverse(st);
                mapping.put(getKey(source, target), st);
                mapping.put(getKey(target, source), ts);
            }
        }

        // iterate through all nodes and create our internal representation
        // of the graph
        for (Node n : component.getNodes()) {
            Vertex in = new Vertex(n);

            for (Node a : component.getAdjacencyList(n)) {
                in.add(mapping.get(getKey(a, n)));
            }
        }

        for (Dart e : mapping.values()) {
            if (e.getFace() == null) {
                faces.add(calculateFace(e));
            }
        }

        faces = Collections.unmodifiableSet(faces);
    }

    // internal methods to compute the faces.

    /**
     * Calculate face starting at the given dart.
     * 
     * @param start
     *            Starting dart of face.
     * @return Face starting at the given dart.
     */
    private Face calculateFace(Dart start) {
        Face face = new Face();
        Dart current = start;

        do {
            face.addDart(current);
            current.setFace(face);
            current = current.nextDart();
        } while (current != start);

        return face;
    }

    /**
     * Create a new pair used as key for the mapping.
     * 
     * @param source
     *            Source node.
     * @param target
     *            Target node.
     * @return Pair containing source and target.
     */
    private final Pair<Node> getKey(Node source, Node target) {
        return new Pair<Node>(source, target);
    }

    /**
     * Get all faces for the connected component.
     * 
     * @return List of darts.
     */
    public Set<Face> getFaces() {
        return faces;
    }

    /**
     * Get dart from source to target.
     * 
     * @param source
     *            Source node of dart.
     * @param target
     *            Target node of dart.
     * @return Dart between source and target. If there is no such dart,
     *         <code>null</code> is returned.
     */
    public Dart getDart(Node source, Node target) {
        return mapping.get(getKey(source, target));
    }

    /**
     * Get connected component for the faces.
     * 
     * @return Connected component for the faces.
     */
    public TestedComponent getTestedComponent() {
        return component;
    }

    @Override
    public String toString() {
        if (getFaces().isEmpty())
            return "";
        String result = "Faces:\n";
        for (Face f : getFaces()) {
            result += "    [";
            for (Iterator<Node> i = f.getNodes().iterator(); i.hasNext();) {
                result += component.toString(i.next());
                if (i.hasNext()) {
                    result += ",";
                }
            }
            result += "]\n";
        }
        return result;
    }
}
