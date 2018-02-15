package org.graffiti.plugins.algorithms.kandinsky;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.graffiti.plugins.algorithms.kandinsky.NormArc.Status;

/**
 * This class computes a compacted representation of the orthogonal graph. First
 * it calculates the direction of the darts (status). Then it refines the graph
 * by removing 360� and 270� angles until each inner face is a rectangle. At
 * last, it computes the relative coordinates and initializes the drawing of the
 * result.
 * 
 * @author Sonja
 * @version $Revision$ $Date$
 */
public class CompactRepresentation {
    /** The network of NormArcs. */
    private NormNetwork network;

    /** Class which is used for normalizing the orthogonal representation. */
    private NormalizeRepresentation norm;

    /** Stores the representation of the faces. */
    private Hashtable<String, LinkedList<NormArc>> faces;

    /** List of Bars for calculating the x-coordinates. */
    private LinkedList<Bar> x = new LinkedList<Bar>();

    /** List of Bars for calculating the y-coordinates. */
    private LinkedList<Bar> y = new LinkedList<Bar>();

    /** True, if the edges of a face were completely run through. */
    private boolean done = false;

    /**
     * List with all of the NormArcs which have been replaced in order to get
     * rectangle shaped faces. Left side: new DummyArc, right side: old NormArc.
     */
    private Hashtable<NormArc, NormArc> replacement;

    /** List with the edges of a face, which describe the face. */
    private LinkedList<NormArc> edges;

    /** Complete list with the edges of a face. */
    private LinkedList<NormArc> list;

    /** Copy of the faces Hashtable. */
    private Hashtable<String, LinkedList<NormArc>> additionalFaces = new Hashtable<String, LinkedList<NormArc>>();

    /** List of dummy arcs which are needed for refining a face. */
    private LinkedList<NormArc> toDelete = new LinkedList<NormArc>();

    /**
     * Computes a compact representation of the orthogonal graph.
     * 
     * @param orthRep
     */
    CompactRepresentation(ComputeOrthRepresentation orthRep) {
        this.norm = new NormalizeRepresentation(orthRep);
        this.replacement = new Hashtable<NormArc, NormArc>();
    }

    /**
     * Calculate the compaction of the representation.
     */
    public void compact() {
        // normalisiere das Netzwerk
        norm.computeNormalizedRep();
        this.network = norm.getNetwork();
        this.faces = norm.getFaces();
        // berechne die Richtung (Status) der Pfeile
        getStatusOfNormArcs();
        System.out.println();
        System.out
                .println("Bestimmung der Form (Normalisierte Repr�sentation):");
        print();
        // Zerlegung / Erg�nzung der Fl�chen zu Rechtecken
        computeRefinement();
        System.out.println("Endergebnis nach der Zerlegung der Fl�chen:");
        // print();
        getCoordinates(); // berechne die relativen x-/y-Koordinaten
        printLong();
        drawing(); // zeichne das Ergebnis
    }

    /**
     * Calculates the status for each NormArc of the network.
     */
    /*
     * Berechnet, ob der ausgehende Pfeil nach links/rechts/oben/unten zeigt.
     * Prinzip Breitensuche bei der Berechnung der Richtung der Nachbarkante.
     */
    protected void getStatusOfNormArcs() {
        // Liste der bereits bearbeiteten Pfeile, f�r deren Nachfolger der
        // Status berechnet werden soll
        LinkedList<NormArc> queue = new LinkedList<NormArc>();
        // Initialisierung
        // Left upper/lower corner of the graph.
        NormArc init = faces.get("Face 0").getFirst();
        Status status;
        if (init.getDirection()) {
            status = Status.DOWN;
        } else {
            status = Status.UP;
        }
        init.setStatus(status);
        init.getFrom().addStatusArc(init);
        // entgegengestzte Kante hat entgegengesetzten Status
        init.setOppositeStatus(status);
        // beide in Liste einf�gen
        queue.add(init);
        queue.add(init.getOpposite());
        while (!queue.isEmpty()) {
            NormArc current = queue.getFirst();
            queue.removeFirst();
            NormArc next = current.getNext();
            boolean visitedOther = next.hasStatus();
            boolean visited = next.calculateStatus(current.getStatus(), current
                    .getAngle()); // berechnet den Status
            // wenn noch kein Status berechnet war --> Einf�gen in Liste
            if (!visited) {
                queue.add(next);
            }
            if (!visitedOther) {
                queue.add(next.getOpposite());
            }
        }
    }

    /**
     * Refines the faces.
     */
    @SuppressWarnings("unchecked")
    private void computeRefinement() {
        // zerlege die Innenfl�chen in Rechtecke
        // erg�nze die Au�enfl�che zu einem Rechteck
        // Kopie des Mappings von den Fl�chen und ihren Kantenlisten
        Hashtable<String, LinkedList<NormArc>> copyFaces = (Hashtable<String, LinkedList<NormArc>>) faces
                .clone();
        refineFace(copyFaces);
        // Bearbeitung der durch die Zerlegung entstandenen zus�tzlichen Fl�chen
        while (additionalFaces.size() != 0) {
            copyFaces = (Hashtable<String, LinkedList<NormArc>>) additionalFaces
                    .clone();
            additionalFaces = new Hashtable<String, LinkedList<NormArc>>();
            refineFace(copyFaces);
        }
    }

    /**
     * Calculates the refinement of the faces.
     * 
     * @param copyFaces
     *            Hashtable, which stores the name of a face with its edge list
     */
    private void refineFace(Hashtable<String, LinkedList<NormArc>> copyFaces) {
        // Iterator �ber alle Fl�chen
        Iterator<Entry<String, LinkedList<NormArc>>> it = copyFaces.entrySet()
                .iterator();
        while (it.hasNext()) {
            Map.Entry<String, LinkedList<NormArc>> entry = it.next();
            String face = entry.getKey(); // Name der aktuellen Fl�che
            list = entry.getValue(); // Kantenliste dieser Fl�che
            // Liste darf nicht mit 0�- oder 270�-Winkel beginnen,
            // sonst gibt es Probleme bei der Zuordnung der 0�-Winkel zu den
            // 270�-Winkeln: geh�rt 0�-Winkel zu vorhergehendem oder
            // nachfolgendem 270�-Winkel?
            int j = 0;
            loop: while ((list.getFirst().getAngle() == 0)
                    || (list.getFirst().getAngle() == 3)) {
                // die komplette Fl�che besteht nur aus 0�-/270�-Winkeln
                if (j == list.size()) {
                    break loop;
                }
                NormArc tmp = list.removeFirst();
                list.addLast(tmp);
                j++;
            }
            edges = new LinkedList<NormArc>();
            int count = 0;
            NormArc next = null;
            // Wurde list schon einmal komplett bearbeitet?
            done = false;
            // solange die Fl�che kein Rechteck ist
            while (!done) {
                next = list.get(count);
                sortEdges(next, count);
                count++;
                if (count == list.size()) {
                    count = 0;
                    done = true;
                }
            }
            // solange die Fl�che kein Rechteck ist
            while (!(isRectangle(edges))) {
                boolean changed = true;
                while (changed) {
                    changed = false;
                    changed |= remove4();
                    changed |= remove313or31113or3113or311();
                }
            }
            faces.put(face, edges);
        }
    }

    /**
     * Removes all of the 360� angles of the outer face.
     * 
     * @return true iff a 360� angle could be removed.
     */
    private boolean remove4() {
        boolean changed = false;
        for (int i = 0; i < edges.size(); i++) {
            LinkedList<NormArc> newFace = new LinkedList<NormArc>();
            int size = edges.size();
            if (edges.get(i).getAngle() == 4) {
                // durchsucht die Liste vorw�rts nach 360�-Winkeln
                NormArc count_4 = edges.get((i + 4) % size);
                NormArc count_3 = edges.get((i + 3) % size);
                NormArc count_2 = edges.get((i + 2) % size);
                NormArc count_1 = edges.get((i + 1) % size);
                NormArc count_0 = edges.get(i);
                if ((count_1.getAngle() == 1) && (count_2.getAngle() == 1)) {
                    // 41113 oder 41114-Winkelfolge
                    if ((count_3.getAngle() == 1)
                            && ((count_4.getAngle() == 3) || (count_4
                                    .getAngle() == 4))) {
                        // System.out.println("Aufruf von remove41113or41114 f�r
                        // "
                        // + count_4 + ", " + count_3 + ", " + count_2 + ", "
                        // + count_1 + ", " + count_0 + ":");
                        remove41113or41114(i, newFace, count_4, count_3,
                                count_2, count_1, count_0);
                        String face = "Face "
                                + (faces.size() + additionalFaces.size());
                        additionalFaces.put(face, newFace);
                        // String n = "[" + face + ": ";
                        // for (NormArc x: newFace)
                        // {
                        // n += ", ";
                        // n += x;
                        // }
                        // n += "]";
                        // System.out.println(n);
                    } else if (((count_3.getAngle() == 3) || (count_3
                            .getAngle() == 4))) {
                        // 4113 oder 4114-Winkelfolge
                        // System.out.println("Aufruf von remove4113or4114 f�r "
                        // + count_3 + ", " + count_2 + ", " + count_1 + ", "
                        // + count_0 + ":");
                        remove4113or4114(i, newFace, size, count_3, count_2,
                                count_1, count_0);
                        String face = "Face "
                                + (faces.size() + additionalFaces.size());
                        additionalFaces.put(face, newFace);
                        // String n = "[" + face + ": ";
                        // for (NormArc x: newFace)
                        // {
                        // n += ", ";
                        // n += x;
                        // }
                        // n += "]";
                        // System.out.println(n);
                    } else {
                        // 411-Winkelfolge
                        // System.out.println("Aufruf von remove411 f�r " + ", "
                        // + count_2 + ", " + count_1 + ", " + count_0 + ":");
                        remove411(i, newFace, count_3, count_2, count_1,
                                count_0, size);
                        String face = "Face "
                                + (faces.size() + additionalFaces.size());
                        additionalFaces.put(face, newFace);
                        // String n = "[" + face + ": ";
                        // for (NormArc x: newFace)
                        // {
                        // n += ", ";
                        // n += x;
                        // }
                        // n += "]";
                        // System.out.println(n);
                    }
                    changed = true;
                } else {
                    // durchsucht die Liste r�ckw�rts nach 360�-Winkeln
                    count_4 = edges.get((i + size - 4) % size);
                    count_3 = edges.get((i + size - 3) % size);
                    count_2 = edges.get((i + size - 2) % size);
                    count_1 = edges.get((i + size - 1) % size);
                    count_0 = edges.get(i);
                    // 41114 oder 31114-Winkelfolge
                    if ((count_1.getAngle() == 1) && (count_2.getAngle() == 1)) {
                        if ((count_3.getAngle() == 1)
                                && ((count_4.getAngle() == 3) || (count_4
                                        .getAngle() == 4))) {
                            // System.out.println("Aufruf von remove31114 f�r "
                            // + count_4 + ", " + count_3 + ", " + count_2
                            // + ", " + count_1 + ", " + count_0 + ":");
                            remove31114(i, newFace, count_4, count_3, count_2,
                                    count_1, count_0);
                            String face = "Face "
                                    + (faces.size() + additionalFaces.size());
                            additionalFaces.put(face, newFace);
                            // String n = "[" + face + ": ";
                            // for (NormArc x: newFace)
                            // {
                            // n += ", ";
                            // n += x;
                            // }
                            // n += "]";
                            // System.out.println(n);
                        } else if ((count_3.getAngle() == 3)
                                || (count_3.getAngle() == 4)) {
                            // 4114 oder 3114-Winkelfolge
                            // System.out.println("Aufruf von remove3114 f�r "
                            // + count_3 + ", " + count_2 + ", " + count_1
                            // + ", " + count_0 + ":");
                            remove3114(i, newFace, size, count_3, count_2,
                                    count_1, count_0);
                            String face = "Face "
                                    + (faces.size() + additionalFaces.size());
                            additionalFaces.put(face, newFace);
                            // String n = "[" + face + ": ";
                            // for (NormArc x: newFace)
                            // {
                            // n += ", ";
                            // n += x;
                            // }
                            // n += "]";
                            // System.out.println(n);
                        } else {
                            // 114-Winkelfolge
                            // System.out.println("Aufruf von remove114 f�r "
                            // + count_2 + ", " + count_1 + ", " + count_0
                            // + ":");
                            remove114(i, newFace, size, count_2, count_1,
                                    count_0);
                            String face = "Face "
                                    + (faces.size() + additionalFaces.size());
                            additionalFaces.put(face, newFace);
                            // String n = "[" + face + ": ";
                            // for (NormArc x: newFace)
                            // {
                            // n += ", ";
                            // n += x;
                            // }
                            // n += "]";
                            // System.out.println(n);
                        }
                        changed = true;
                    }
                }

            }
        }
        return changed;
    }

    /**
     * Refines a 90�-90�-360� angle.
     * 
     * @param i
     *            the actual position in the list of edges.
     * @param newFace
     *            the calculated new face.
     * @param size
     *            the size of the edge list.
     * @param count_2
     *            the edge at position i + 2, which has a 90�-angle.
     * @param count_1
     *            the edge at position i + 1, which has a 90�-angle.
     * @param count_0
     *            the edge at position i, which has a 360�-angle.
     */
    /*
     * Das Ohr count_0, count_1 und ein Teil von count_2 wird abgeschnitten.
     */
    private void remove114(int i, LinkedList<NormArc> newFace, int size,
            NormArc count_2, NormArc count_1, NormArc count_0) {
        // Erzeuge Dummy-Knoten auf der anderen Seite auf count_2
        NormNode dummy = network.createDummyNode("D_out(" + count_2.getFrom()
                + ", " + count_2.getTo() + ")");
        count_2.getFrom().removeStatusArc(count_2);
        count_2.getTo().removeStatusArc(count_2.getOpposite());
        // Teile Kante two durch Dummyknoten
        NormArc front1 = network.createDummyArc(count_2.getFrom(), dummy, true,
                1, count_2.getStatus());
        NormArc b = network.createDummyArc(dummy, count_2.getFrom(), false, 1,
                count_2.getOpposite().getStatus());
        b.setOpposite(front1);
        front1.setOpposite(b);
        replacement.put(front1, count_2);
        replacement.put(b, count_2.getOpposite());
        // Erzeuge Dummy-Kante zum Dummy-Knoten auf der
        // anderen Seite
        NormArc a = network.createDummyArc(count_0.getTo(), dummy, false, 1,
                count_1.getOpposite().getStatus());
        newFace.add(count_0);
        // Kante in anderer Richtung
        NormArc front2 = network.createDummyArc(dummy, count_0.getTo(), true,
                3, count_1.getStatus());
        newFace.add(a);
        a.setOpposite(front2);
        front2.setOpposite(a);
        // Erzeuge Kante von Dummy-Knoten zu next.to und
        // umgekehrt
        NormArc c = network.createDummyArc(dummy, count_2.getTo(), true, 1,
                count_2.getStatus());
        b = network.createDummyArc(count_2.getTo(), dummy, false, 1, count_2
                .getOpposite().getStatus());
        b.setOpposite(c);
        c.setOpposite(b);
        replacement.put(c, count_2);
        replacement.put(b, count_2.getOpposite());
        newFace.add(c);
        newFace.add(count_1);
        count_0.setAngle(1);
        edges.add((i + size - 2) % size, front1);
        edges.add(i, front2);
        edges.remove(count_0);
        edges.remove(count_1);
        edges.remove(count_2);
        // System.out.println("Statt: " + count_2 + ", " + count_1 + ", "
        // + count_0 + " nun: " + front1 + ", " + front2 + ", nicht " + c
        // + ", " + b);
        toDelete.add(front2);
        toDelete.add(front2.getOpposite());
    }

    /**
     * Refines a 360�-90�-90� angle.
     * 
     * @param i
     *            the actual position in the list of edges.
     * @param newFace
     *            the calculated new face.
     * @param count_3
     *            the edge at position i + 3, which has a 90�-angle.
     * @param count_2
     *            the edge at position i + 2, which has a 90�-angle.
     * @param count_1
     *            the edge at position i + 1, which has a 90�-angle.
     * @param count_0
     *            the edge at position i, which has a 360�-angle.
     * @param size
     *            the size of the edge list
     */
    /*
     * Das Ohr count_1, count_2 und ein Teil von count_3 wird abgeschnitten.
     */
    private void remove411(int i, LinkedList<NormArc> newFace, NormArc count_3,
            NormArc count_2, NormArc count_1, NormArc count_0, int size) {
        // Erzeuge Dummy-Knoten auf der anderen Seite, auf count_3
        NormNode dummy = network.createDummyNode("D_out(" + count_3.getFrom()
                + ", " + count_3.getTo() + ")");
        count_3.getFrom().removeStatusArc(count_3);
        count_3.getTo().removeStatusArc(count_3.getOpposite());
        // Teile Kante three durch Dummyknoten
        NormArc c = network.createDummyArc(count_3.getFrom(), dummy, true, 1,
                count_3.getStatus());
        newFace.add(c);
        NormArc front1 = network.createDummyArc(dummy, count_3.getFrom(),
                false, 3, count_3.getOpposite().getStatus());
        front1.setOpposite(c);
        c.setOpposite(front1);
        replacement.put(c, count_3);
        replacement.put(front1, count_3.getOpposite());
        // Erzeuge Dummy-Kante zum Dummy-Knoten auf der anderen Seite
        NormArc front2 = network.createDummyArc(count_0.getTo(), dummy, true,
                1, count_2.getStatus());
        // Kante in anderer Richtung
        NormArc d = network.createDummyArc(dummy, count_0.getTo(), false, 1,
                count_2.getOpposite().getStatus());
        newFace.add(d);
        newFace.add(count_1);
        newFace.add(count_2);
        d.setOpposite(front2);
        front2.setOpposite(d);
        // Erzeuge Kante von Dummy-Knoten zu next.to und
        // umgekehrt
        NormArc front3 = network.createDummyArc(dummy, count_3.getTo(), true,
                count_3.getAngle(), count_3.getStatus());
        NormArc b = network.createDummyArc(count_3.getTo(), dummy, false, 1,
                count_3.getOpposite().getStatus());
        b.setOpposite(front3);
        front3.setOpposite(b);
        replacement.put(front3, count_3);
        replacement.put(b, count_3.getOpposite());
        count_0.setAngle(3);
        edges.add((i + 1) % size, front2);
        edges.add((i + 2) % size, front3);
        edges.remove(count_3);
        edges.remove(count_2);
        edges.remove(count_1);
        // System.out.println("Statt: " + count_3 + ", " + count_2 + ", "
        // + count_1 + " nun: " + count_0 + ", "
        // + front2 + ", " + front3 + ", nicht " + front1 + ", " + b);
        toDelete.add(front2);
        toDelete.add(d);
    }

    /**
     * Refines a 360�/270�-90�-90�-360� angle.
     * 
     * @param i
     *            the actual position in the list of edges.
     * @param newFace
     *            the calculated new face.
     * @param size
     *            the size of the edge list.
     * @param count_3
     *            the edge at position i - 3, which has a 360�/270�-angle.
     * @param count_2
     *            the edge at position i - 2, which has a 90�-angle.
     * @param count_1
     *            the edge at position i - 1, which has a 90�-angle.
     * @param count_0
     *            the edge at position i, which has a 360�-angle.
     */
    /*
     * Das Ohr count_0, count_1 und count_2 wird abgeschnitten, indem ein Deckel
     * (a) daraufgesetzt wird.
     */
    private void remove3114(int i, LinkedList<NormArc> newFace, int size,
            NormArc count_3, NormArc count_2, NormArc count_1, NormArc count_0) {
        // Deckel
        NormArc a = network.createDummyArc(count_3.getTo(), count_0.getTo(),
                true, 3, count_1.getStatus());
        NormArc b = network.createDummyArc(count_0.getTo(), count_3.getTo(),
                false, 1, count_1.getOpposite().getStatus());
        count_0.setAngle(1);
        count_3.setAngle(count_3.getAngle() - 1);
        if (count_3.getAngle() == 2) {
            edges.remove(count_3);
        }
        edges.add((i + size - 2) % size, a);
        newFace.add(b);
        newFace.add(count_2);
        newFace.add(count_1);
        newFace.add(count_0);
        edges.remove(count_0);
        edges.remove(count_1);
        edges.remove(count_2);
        // System.out.println("Statt: evtl. " + count_3 + ", " + count_2 + ", "
        // + count_1 + ", " + count_0 + " nun: " + a);
        b.setOpposite(a);
        a.setOpposite(b);
        toDelete.add(a);
        toDelete.add(b);
    }

    /**
     * Refines a 360�/270�-90�-90�-90�-360� angle.
     * 
     * @param i
     *            the actual position in the list of edges.
     * @param newFace
     *            the calculated new face.
     * @param count_4
     *            the edge at position i - 4, which has a 360�/270�-angle.
     * @param count_3
     *            the edge at position i - 3, which has a 90�-angle.
     * @param count_2
     *            the edge at position i - 2, which has a 90�-angle.
     * @param count_1
     *            the edge at position i - 1, which has a 90�-angle.
     * @param count_0
     *            the edge at position i, which has a 360�-angle.
     */
    /*
     * Das Ohr count_0, count_1, count_2 und count_3 wird abgeschnitten, indem
     * ein Deckel daraufgesetzt wird.
     */
    private void remove31114(int i, LinkedList<NormArc> newFace,
            NormArc count_4, NormArc count_3, NormArc count_2, NormArc count_1,
            NormArc count_0) {
        // Deckel
        NormArc a = network.createDummyArc(count_4.getTo(), count_0.getTo(),
                true, 2, count_0.getOpposite().getStatus());
        // entgegengesetzte Richtung
        NormArc b = network.createDummyArc(count_0.getTo(), count_4.getTo(),
                false, 1, count_0.getStatus());
        count_0.setAngle(2);
        count_4.setAngle(count_4.getAngle() - 1);
        newFace.add(b);
        newFace.add(count_3);
        newFace.add(count_2);
        newFace.add(count_1);
        if (a.getAngle() != 2) {
            edges.add(i, a);
        }
        edges.remove(count_0);
        edges.remove(count_1);
        edges.remove(count_2);
        edges.remove(count_3);
        if (count_4.getAngle() == 2) // spielt keine Rolle f�r die Form
        {
            edges.remove(count_4);
        }
        // System.out.println("Statt: evtl. " + count_4 + ", " + count_3 + ", "
        // + count_2 + ", " + count_1 + ", " + count_0 + " nun : evtl. " + a);
        b.setOpposite(a);
        a.setOpposite(b);
        toDelete.add(a);
        toDelete.add(b);
    }

    /**
     * Refines a 360�-90�-90�-360�/270� angle.
     * 
     * @param i
     *            the actual position in the list of edges.
     * @param newFace
     *            the calculated new face.
     * @param size
     *            the size of the list of edges.
     * @param count_3
     *            the edge at position i + 3, which has a 360�/270�-angle.
     * @param count_2
     *            the edge at position i + 2, which has a 90�-angle.
     * @param count_1
     *            the edge at position i + 1, which has a 90�-angle.
     * @param count_0
     *            the edge at position i, which has a 360�-angle.
     */
    /*
     * Das Ohr count_1, count_2 und count_3 wird abgeschnitten, indem ein Deckel
     * daraufgesetzt wird.
     */
    private void remove4113or4114(int i, LinkedList<NormArc> newFace, int size,
            NormArc count_3, NormArc count_2, NormArc count_1, NormArc count_0) {
        NormArc a = network.createDummyArc(count_3.getTo(), count_0.getTo(),
                false, 1, count_2.getOpposite().getStatus());
        // Deckel
        NormArc b = network.createDummyArc(count_0.getTo(), count_3.getTo(),
                true, (count_3.getAngle() - 1), count_2.getStatus());
        count_0.setAngle(3);
        count_3.setAngle(1);
        newFace.add(a);
        newFace.add(count_1);
        newFace.add(count_2);
        newFace.add(count_3);
        edges.remove(count_1);
        edges.remove(count_2);
        edges.remove(count_3);
        if (b.getAngle() == 3) {
            edges.add((i + 1) % size, b); // jetzt Winkel 3
        }
        // System.out.println("Statt: " + count_3 + ", " + count_2 + ", "
        // + count_1 + ", " + count_0 + " nun: evtl. " + b);
        b.setOpposite(a);
        a.setOpposite(b);
        toDelete.add(a);
        toDelete.add(b);
    }

    /**
     * Refines a 360�-90�-90�-90�-360�/270� angle.
     * 
     * @param i
     *            the actual position in the list of edges.
     * @param newFace
     *            the calculated new face.
     * @param count_4
     *            the edge at position i + 4, which has a 360�/270�-angle.
     * @param count_3
     *            the edge at position i + 3, which has a 90�-angle.
     * @param count_2
     *            the edge at position i + 2, which has a 90�-angle.
     * @param count_1
     *            the edge at position i + 1, which has a 90�-angle.
     * @param count_0
     *            the edge at position i, which has a 360�-angle.
     */
    /*
     * Das Ohr count_1, count_2 und count_3 wird abgeschnitten, indem ein Deckel
     * daraufgesetzt wird.
     */
    private void remove41113or41114(int i, LinkedList<NormArc> newFace,
            NormArc count_4, NormArc count_3, NormArc count_2, NormArc count_1,
            NormArc count_0) {
        NormArc a = network.createDummyArc(count_4.getTo(), count_0.getTo(),
                false, 1, count_4.getStatus());
        // Deckel
        NormArc b = network.createDummyArc(count_0.getTo(), count_4.getTo(),
                true, ((count_4.getAngle() + 2) % 4), count_4.getOpposite()
                        .getStatus());
        count_1.setAngle(1);
        count_4.setAngle(2);
        count_0.setAngle(count_0.getAngle() - 1);
        // newFace.add(a); // nicht einf�gen, da Winkel 2
        newFace.add(count_1);
        newFace.add(count_2);
        newFace.add(count_3);
        newFace.add(a);
        if (b.getAngle() == 1) {
            edges.add(((i + 1) % edges.size()), b); // jetzt Winkel 3
        }
        edges.remove(count_1);
        edges.remove(count_2);
        edges.remove(count_3);
        edges.remove(count_4);
        // System.out.println("Statt: " + count_4 + ", " + count_3 + ", "
        // + count_2 + ", " + count_1 + " nun: evtl. " + b + ", nicht " + a);
        b.setOpposite(a);
        a.setOpposite(b);
        toDelete.add(a);
        toDelete.add(b);
    }

    /**
     * Removes all of the 270�-180�-(180�)-270� angles. Removes all of the
     * 270�-180�-180� angles.
     */
    private boolean remove313or31113or3113or311() {
        boolean changed = false;
        for (int i = 0; i < edges.size(); i++) {
            LinkedList<NormArc> newFace = new LinkedList<NormArc>();
            int size = edges.size();
            NormArc count_2 = edges.get((i + 2) % size);
            NormArc count_1 = edges.get((i + 1) % size);
            NormArc count_0 = edges.get(i);
            if (((count_0.getAngle() == 3) || (count_0.getAngle() == 4))
                    && (count_1.getAngle() == 1)
                    && ((count_2.getAngle() == 3) || (count_2.getAngle() == 4))) {
                // 313 oder 414
                // Die Treppenstufe count_0, count_1 und count_2 wird zu einem
                // Rechteck erg�nzt --> Dummyknoten und 2 Kanten

                // System.out.println("Aufruf von remove313 f�r " + count_2 + ",
                // "
                // + count_1 + ", " + count_0 + ":");
                // Erzeuge Dummy-Knoten auf der anderen Seite, um diese
                // "Treppenstufe" zu f�llen --> Erg�nzung zum Rechteck
                NormNode dummy = network.createDummyNode("D_out("
                        + count_0.getFrom() + ", " + count_2.getTo() + ")");
                // Erzeugen von zwei neuen Kanten (front1, front2) und deren
                // entgegengesetzten Richtungen (a, b)
                NormArc front1 = network.createDummyArc(count_0.getTo(), dummy,
                        true, 3, count_2.getStatus());
                NormArc b = network.createDummyArc(dummy, count_0.getTo(),
                        false, 1, count_2.getOpposite().getStatus());
                b.setOpposite(front1);
                front1.setOpposite(b);
                NormArc front2 = network.createDummyArc(dummy, count_2.getTo(),
                        true, (count_2.getAngle() - 1), count_1.getStatus());
                NormArc a = network.createDummyArc(count_2.getTo(), dummy,
                        false, 1, count_1.getOpposite().getStatus());
                a.setOpposite(front2);
                front2.setOpposite(a);
                count_2.setAngle(1);
                newFace.add(a);
                newFace.add(b);
                newFace.add(count_1);
                newFace.add(count_2);
                edges.add(((i + 1) % size), front1);
                if (front2.getAngle() != 2) {
                    edges.add(((i + 3) % size), front2);
                }
                count_0.setAngle(count_0.getAngle() - 1);
                if (count_0.getAngle() == 2) {
                    edges.remove(count_0);
                }
                edges.remove(count_1);
                edges.remove(count_2);
                // System.out.println("Statt: " + count_2 + ", " + count_1 + ",
                // "
                // + count_0 + " nun: " + front1 + ", evtl. " + front2);
                toDelete.add(front1);
                toDelete.add(front2);
                toDelete.add(a);
                toDelete.add(b);
                changed = true;
                String face = "Face " + (faces.size() + additionalFaces.size());
                additionalFaces.put(face, newFace);
                // String n = "[" + face + ": ";
                // for (NormArc x: newFace)
                // {
                // n += ", ";
                // n += x;
                // }
                // n += "]";
                // System.out.println(n);
            } else {
                // 3113 oder 3114
                // Das Ohr count_0, count_1, count_2 und count_3 wird
                // abgeschnitten, indem ein Deckel daraufgesetzt wird.

                NormArc count_3 = edges.get((i + 3) % size);
                if ((count_0.getAngle() == 3)
                        && (count_1.getAngle() == 1)
                        && (count_2.getAngle() == 1)
                        && ((count_3.getAngle() == 3) || (count_3.getAngle() == 4))) {
                    // System.out.println("Aufruf von remove3113 f�r " + count_3
                    // + ", " + count_2 + ", " + count_1 + ", " + count_0
                    // + ":");
                    // Erzeuge eine Kante, die als "Deckel" das Ohr abschneidet
                    NormArc front = network.createDummyArc(count_0.getTo(),
                            count_3.getTo(), true, (count_3.getAngle() - 1),
                            count_2.getStatus());
                    // andere Richtung
                    NormArc b = network.createDummyArc(count_3.getTo(), count_0
                            .getTo(), false, 1, count_2.getOpposite()
                            .getStatus());
                    b.setOpposite(front);
                    front.setOpposite(b);
                    count_3.setAngle(1);
                    count_0.setAngle(2);
                    newFace.add(b);
                    newFace.add(count_1);
                    newFace.add(count_2);
                    newFace.add(count_3);
                    if (front.getAngle() != 2) {
                        edges.add(((i + 1) % size), front);
                    }
                    edges.remove(count_0);
                    edges.remove(count_1);
                    edges.remove(count_2);
                    edges.remove(count_3);
                    // System.out.println("Statt: " + count_3 + ", " + count_2
                    // + ", " + count_1 + ", " + count_0 + " nun: evtl. "
                    // + front);
                    toDelete.add(front);
                    toDelete.add(b);
                    changed = true;
                    String face = "Face "
                            + (faces.size() + additionalFaces.size());
                    additionalFaces.put(face, newFace);
                    // String n = "[" + face + ": ";
                    // for (NormArc x: newFace)
                    // {
                    // n += ", ";
                    // n += x;
                    // }
                    // n += "]";
                    // System.out.println(n);
                } else {
                    // 31113 oder 31114
                    // Das Ohr count_0, count_1, count_2, count_3 und count_4
                    // wird abgeschnitten, indem ein Deckel daraufgesetzt wird.

                    NormArc count_4 = edges.get((i + 4) % size);
                    if ((count_0.getAngle() == 3)
                            && (count_1.getAngle() == 1)
                            && (count_2.getAngle() == 1)
                            && (count_3.getAngle() == 1)
                            && ((count_4.getAngle() == 3) || (count_4
                                    .getAngle() == 4))) {
                        // System.out.println("Aufruf von remove31113 f�r "
                        // + count_4 + ", " + count_3 + ", " + count_2 + ", "
                        // + count_1 + ", " + count_0 + ":");
                        int angle = 1;
                        if (count_4.getAngle() == 4) {
                            angle = 2;
                        }
                        // Erzeuge eine Kante, die als "Deckel" das Ohr
                        // abschneidet
                        NormArc front = network.createDummyArc(count_0.getTo(),
                                count_4.getTo(), true, angle, count_2
                                        .getStatus());
                        // entgegengesetzte Richtung
                        NormArc b = network.createDummyArc(count_4.getTo(),
                                count_0.getTo(), false, 1, count_2
                                        .getOpposite().getStatus());
                        b.setOpposite(front);
                        front.setOpposite(b);
                        count_4.setAngle(2);
                        count_0.setAngle(2);
                        newFace.add(b);
                        newFace.add(count_1);
                        newFace.add(count_2);
                        newFace.add(count_3);
                        if (front.getAngle() != 2) {
                            edges.add(((i + 1) % size), front);
                        }
                        edges.remove(count_0);
                        edges.remove(count_1);
                        edges.remove(count_2);
                        edges.remove(count_3);
                        edges.remove(count_4);
                        // System.out.println("Statt: " + count_4 + ", " +
                        // count_3
                        // + ", " + count_2 + ", " + count_1 + ", " + count_0
                        // + " nun: evtl. " + front);
                        toDelete.add(front);
                        toDelete.add(b);
                        changed = true;
                        String face = "Face "
                                + (faces.size() + additionalFaces.size());
                        additionalFaces.put(face, newFace);
                        // String n = "[" + face + ": ";
                        // for (NormArc x: newFace)
                        // {
                        // n += ", ";
                        // n += x;
                        // }
                        // n += "]";
                        // System.out.println(n);
                    } else
                    // 311
                    // Das Ohr count_1, count_2 und ein Teil von count_3 wird
                    // abgeschnitten.

                    {
                        if ((count_0.getAngle() == 3)
                                && (count_1.getAngle() == 1)
                                && (count_2.getAngle() == 1)) {
                            // System.out.println("Aufruf von remove311 f�r "
                            // + count_2 + ", " + count_1 + ", " + count_0
                            // + ":");
                            // Erzeuge Dummy-Knoten auf der anderen Seite, auf
                            // count_3
                            NormNode dummy = network.createDummyNode("D_out("
                                    + count_3.getFrom() + ", "
                                    + count_3.getTo() + ")");
                            // Ersetze die alte Kante, auf der der Dummy-Knoten
                            // jetzt sitzt
                            // durch zwei neue Kante (front1, a) und deren
                            // entgegengesetzten Richtungen (b, d)
                            NormArc front1 = network.createDummyArc(count_0
                                    .getTo(), dummy, true, 1, count_2
                                    .getStatus());
                            NormArc b = network.createDummyArc(dummy, count_0
                                    .getTo(), false, 1, count_2.getOpposite()
                                    .getStatus());
                            b.setOpposite(front1);
                            front1.setOpposite(b);
                            // Entfernen der durch den Dummy-Knoten zu
                            // unterteilenden Kante count_3
                            count_3.getTo().removeStatusArc(
                                    count_3.getOpposite());
                            count_3.getFrom().removeStatusArc(count_3);
                            NormArc a = network.createDummyArc(dummy, count_3
                                    .getFrom(), false, count_3.getOpposite()
                                    .getAngle(), count_3.getOpposite()
                                    .getStatus());
                            NormArc d = network.createDummyArc(count_3
                                    .getFrom(), dummy, true, 1, count_3
                                    .getStatus());
                            d.setOpposite(a);
                            a.setOpposite(d);
                            // Erzeuge Kante, die das Ohr abschneidet--> Deckel
                            NormArc front3 = network.createDummyArc(dummy,
                                    count_3.getTo(), true, count_3.getAngle(),
                                    count_3.getStatus());
                            NormArc c = network.createDummyArc(count_3.getTo(),
                                    dummy, false, 2, count_3.getOpposite()
                                            .getStatus());
                            c.setOpposite(front3);
                            front3.setOpposite(c);
                            replacement.put(d, count_3);
                            replacement.put(front3, count_3);
                            replacement.put(c, count_3.getOpposite());
                            replacement.put(a, count_3.getOpposite());
                            count_0.setAngle(2);
                            newFace.add(d);
                            newFace.add(b);
                            newFace.add(count_1);
                            newFace.add(count_2);
                            edges.add(i, front1);
                            edges.add(((i + 1) % size), front3);
                            edges.remove(count_0);
                            edges.remove(count_1);
                            edges.remove(count_2);
                            edges.remove(count_3);
                            // System.out.println("Statt: " + count_3 + ", "
                            // + count_2 + ", " + count_1 + ", " + count_0
                            // + " nun: " + front1 + ", " + front3 + ", nicht "
                            // + a + ", " + c);
                            toDelete.add(front1);
                            toDelete.add(b);
                            changed = true;
                            String face = "Face "
                                    + (faces.size() + additionalFaces.size());
                            additionalFaces.put(face, newFace);
                            // String n = "[" + face + ": ";
                            // for (NormArc x: newFace)
                            // {
                            // n += ", ";
                            // n += x;
                            // }
                            // n += "]";
                            // System.out.println(n);
                        }
                    }
                }
            }
        }
        return changed;
    }

    /**
     * Sorts the edges depending of their angles and handles 0�-angles and their
     * bends.
     * 
     * @param next
     *            the next edge, which might be added to the shape list.
     * @param count
     *            the position of the current edge in the list.
     */
    /*
     * Hier werden die Kanten aussortiert, die die Form der Fl�che nicht �ndern.
     * Die hier erzeugte Liste enth�lt also nur Kanten, welche die Form
     * beschreiben: Kanten mit Winkel 180� tragen nichts zur Form bei und werden
     * nicht ber�cksichtigt. Bei 270�-Winkel mu� �berpr�ft werden, ob sie zu
     * einem 0�-Winkel geh�ren. In diesem Fall m�ssen Abstandskanten eingef�gt
     * werden, damit sp�ter keine �berlappung von einem Knoten und einem Knick
     * berechnet wird. Andernfalls werden sie einfach in die Liste eingef�gt.
     * 90�- und 360�-Winkel m�ssen nicht gesondert behandelt werden, sondern
     * werden nur in die Liste aufgenommen.
     */
    private void sortEdges(NormArc next, int count) {
        // System.out.println(next.getLabel() + ": " + next.getAngle());
        if (next.getAngle() == 1) {
            edges.add(next);
        } else {
            // Suche, ob vorherige Kante ein 0�-Winkel ist
            // --> z�hlt zusammen als 90�-Winkel
            if ((next.getAngle() == 3))
            // 0�-Winkel mit 270�-Winkel entspricht 90�
            {
                int size = list.size();
                // gibt es vorher einen korrespondierenden 0�-Winkel?
                // wenn Knoten mit 270�-Winkel kein Dummy ist --> ist nicht
                // zugeh�riger VertexBend
                if ((list.get((count - 1 + size) % size).getAngle() == 0)
                        && next.getTo().isDummy()) {
                    // Erzeugen von Abstandskanten, damit keine �berlappungen
                    // entstehen zwischen Knoten und 270�-Knick wegen 0�-Winkel
                    // System.out.println(" 3: Fall 1");
                    NormArc dummy1 = network.createDummyArc(next.getTo(), (list
                            .get((count - 1 + size) % size)).getFrom(), true,
                            1, (list.get((count - 1 + size) % size))
                                    .getOpposite().getStatus());
                    NormArc dummy2 = network.createDummyArc((list
                            .get((count - 1 + size) % size)).getFrom(), next
                            .getTo(), false, 1, (list.get((count - 1 + size)
                            % size)).getStatus());
                    // System.out.println("Abstandskanten: " + dummy1 + " und "
                    // + dummy2);
                    toDelete.add(dummy1);
                    toDelete.add(dummy2);
                    next.setAngle(2);
                    list.get((count - 1 + size) % size).setAngle(1);

                } else {
                    // gibt es danach einen korrespondierenden 0�-Winkel?
                    // wenn Knoten mit 270�-Winkel kein Dummy ist --> ist nicht
                    // zugeh�riger VertexBend
                    if ((list.get((count + 1) % list.size()).getAngle() == 0)
                            && next.getTo().isDummy()) {
                        // Erzeugen von Abstandskanten, damit keine
                        // �berlappungen
                        // entstehen zwischen Knoten und 270�-Knick wegen
                        // 0�-Winkel
                        // System.out.println(" 3: Fall 2");
                        NormArc dummy1 = network.createDummyArc(next.getTo(),
                                (list.get((count + 2) % size)).getTo(), true,
                                1, (list.get((count + 2) % size)).getStatus());
                        NormArc dummy2 = network.createDummyArc((list
                                .get((count + 2) % size)).getTo(),
                                next.getTo(), false, 1, (list.get((count + 2)
                                        % size)).getOpposite().getStatus());
                        // System.out.println("Abstandskanten: " + dummy1
                        // + " und " + dummy2);
                        toDelete.add(dummy1);
                        toDelete.add(dummy2);
                        next.setAngle(1);
                        edges.add(next);
                        list.get((count + 1) % size).setAngle(2);
                    } else {
                        // einzelner 270�-Winkel
                        edges.add(next);
                        // System.out.println(" 3: Fall 3");
                    }
                }
            } else {
                if ((next.getAngle() == 4)) {
                    edges.add(next);
                } else {
                    // Vorherige Kante hat einen 270�-Winkel?
                    // --> z�hlt zusammen als 90�-Winkel
                    if ((next.getAngle() == 0)) {
                        int size = list.size();
                        // wenn Knoten mit 270�-Winkel kein Dummy ist --> ist
                        // nicht zugeh�riger VertexBend
                        if ((list.get((count - 1 + size) % size).getAngle() == 3)
                                && list.get((count - 1 + size) % size)
                                        .isDummy()) {
                            // Erzeugen von Abstandskanten, damit keine
                            // �berlappungen
                            // entstehen zwischen Knoten und 270�-Knick wegen
                            // 0�-Winkel
                            // System.out.println(" 0: Fall 1");
                            NormArc dummy1 = network.createDummyArc(next
                                    .getFrom(), (list.get((count + 1) % size))
                                    .getTo(), true, 1, (list.get((count + 2)
                                    % size)).getStatus());
                            NormArc dummy2 = network.createDummyArc((list
                                    .get((count + 1) % size)).getTo(), next
                                    .getFrom(), false, 1, (list.get((count + 2)
                                    % size)).getOpposite().getStatus());
                            // System.out.println("Abstandskanten: " + dummy1
                            // + " und " + dummy2);
                            toDelete.add(dummy1);
                            toDelete.add(dummy2);
                            list.get((count - 1 + size) % size).setAngle(1);
                            next.setAngle(2);
                        } else {
                            // wenn Knoten mit 270�-Winkel kein Dummy ist -->
                            // ist nicht zugeh�riger VertexBend
                            if ((list.get((count + 1) % size).getAngle() == 3 && (list
                                    .get((count + 1) % size)).getTo().isDummy())) {
                                // Erzeugen von Abstandskanten, damit keine
                                // �berlappungen
                                // entstehen zwischen Knoten und 270�-Knick
                                // wegen 0�-Winkel
                                // System.out.println(" 0: Fall 2");
                                NormArc dummy1 = network.createDummyArc(next
                                        .getFrom(), (list.get((count + 1)
                                        % size)).getTo(), true, 1, next
                                        .getStatus());
                                NormArc dummy2 = network.createDummyArc((list
                                        .get((count + 1) % size)).getTo(), next
                                        .getFrom(), false, 1, next
                                        .getOpposite().getStatus());
                                // System.out.println("Abstandskanten: " +
                                // dummy1
                                // + " und " + dummy2);
                                toDelete.add(dummy1);
                                toDelete.add(dummy2);
                                list.get((count + 1) % size).setAngle(2);
                                next.setAngle(1);
                                edges.add(next);
                            } else {
                                System.out.println(" Fehlerhafter Fall bei "
                                        + next);
                                System.exit(-1);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns true, if the face is refined to a rectangle.
     * 
     * @param edges
     *            the list of angles of the face.
     * @return if the face is a rectangle.
     */
    private boolean isRectangle(LinkedList<NormArc> edges) {
        // Innenfl�che ist Rechteck: besteht aus vier 90�-Winkeln
        if ((edges.size() == 4) && (edges.get(0).getAngle() == 1)
                && (edges.get(1).getAngle() == 1)
                && (edges.get(2).getAngle() == 1)
                && (edges.get(3).getAngle() == 1))
            return true;
        else {
            // Au�enfl�che besteht aus vier 270�-Winkel
            if ((edges.size() == 4) && (edges.get(0).getAngle() == 3)
                    && (edges.get(1).getAngle() == 3)
                    && (edges.get(2).getAngle() == 3)
                    && (edges.get(3).getAngle() == 3))
                return true;
            else
                return false;
        }
    }

    /**
     * Calculates the coordinates of the NormNodes.
     */
    /*
     * Geh�rt ein NormNode noch zu keinem Bar, wird er zum ersten Repr�sentanten
     * eines neuen Bars gemacht, welcher seinen Namen bekommt. F�r ein x-Bar
     * werden nun alle Knoten, die durch aufw�rts und abw�rts gerichtete Pfeile
     * verbunden sind, in den Bar eingef�gt. Bei dem y-Bar analog mit links und
     * rechtsgerichteten Pfeilen. So geh�rt jeder Knoten zu einem x- und einem
     * y-Bar. Der Bar, von dem aus kein Pfeil nach links geht, bekommt die
     * x-Koordinate 0 zugewiesen. Anschlie�end werden alle nach rechts gehenden
     * Pfeile gel�scht. Der x-Bar, der nun keine Kanten nach links mehr hat,
     * erh�lt die n�chsth�here Koordinate. Analog f�r y: Suche nach einem y-Bar
     * ohne aufw�rts gerichtete Pfeile.
     */
    private void getCoordinates() {
        // Zuweisen zu x-Bars
        for (NormNode n : network.getNormNodes()) {
            if (n.getXBar() == null) {
                Bar bar = new Bar(n.getLabel(), true);
                bar.addNode(n);
                x.add(bar);
            }
        }
        int coordinate = 0;
        // Hinzuf�gen der Knoten links und rechts zu diesem Bar
        for (Bar bar : x) {
            bar.addLeftArcs();
            bar.addRightArcs();
        }
        // Suche nach Bar ohne Pfeile nach links, da dieser am weitesten links
        while (!x.isEmpty()) {
            LinkedList<Bar> next = new LinkedList<Bar>();
            boolean found = false;
            for (Bar bar : x) {
                if (bar.getLeftBars().size() == 0) {
                    next.add(bar);
                    found = true;
                }
            }
            if (!found) {
                System.out
                        .println("Keine X-Bar ohne Aufw�rtskanten gefunden f�r Koordinate "
                                + coordinate);
                System.exit(-1);
            }
            // Weise die x-Koordinate zu und entferne Bar mit allen Kanten
            for (Bar bar : next) {
                bar.setCoordinates(coordinate);
                bar.removeArcs();
                x.remove(bar);
            }
            coordinate++;
        }
        // Zuweisen zu y-Bars
        for (NormNode n : network.getNormNodes()) {
            if (n.getYBar() == null) {
                Bar bar = new Bar(n.getLabel(), false);
                bar.addNode(n);
                y.add(bar);
            }
        }
        coordinate = 0;
        // Hinzuf�gen der Knoten unten und oben zu diesem Bar
        for (Bar bar : y) {
            bar.addUpArcs();
            bar.addDownArcs();
        }
        // Suche nach Bar ohne Pfeile nach unten, da dieser am weitesten rechts
        while (!y.isEmpty()) {
            LinkedList<Bar> next = new LinkedList<Bar>();
            boolean found = false;
            for (Bar bar : y) {
                if (bar.getUpBars().size() == 0) {
                    next.add(bar);
                    found = true;
                }
            }
            if (!found) {
                System.out
                        .println("Keine Y-Bar ohne Aufw�rtskanten gefunden f�r Koordinate "
                                + coordinate);
                System.exit(-1);
            }
            // Weise die y-Koordinate zu und entferne Bar mit allen Kanten
            for (Bar bar : next) {
                bar.setCoordinates(coordinate);
                bar.removeArcs();
                y.remove(bar);
            }
            coordinate++;
        }

        // Entferne die Dummykanten, da f�r sie keine richtige Kante als
        // Ersatz da ist.
        for (NormArc arc : toDelete) {
            network.removeNormArc(arc);
        }
    }

    /**
     * Drawing the graph dependent on the new coordinates. Method invokes
     * <code>Drawing</code>.
     */
    protected void drawing() {
        new Drawing(network, replacement);
    }

    /**
     * Prints the normalized orthogonal representation of the graph.
     */
    public void print() {
        for (int i = 0; i < faces.size(); i++) {
            String label = "Face " + i;
            printFace(label);
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Prints the normalized orthogonal representation of a face.
     * 
     * @param label
     *            the name of the face
     */
    public void printFace(String label) {
        System.out.print(label + ": ");
        for (NormArc arc : faces.get(label)) {
            System.out.print("[" + arc.getLabel() + ", " + arc.printStatus()
                    + ", " + arc.getAngle() + "]; ");
        }
    }

    /**
     * Prints the normalized orthogonal representation of the graph with the
     * coordinates.
     */
    public void printLong() {
        for (int i = 0; i < faces.size(); i++) {
            String label = "Face " + i;
            System.out.print(label + ": ");
            for (NormArc arc : faces.get(label)) {
                System.out.print("[" + arc.getLabel() + ", "
                        + arc.printStatus() + ", <" + arc.getFrom().getX()
                        + ", " + arc.getFrom().getY() + ">, <"
                        + arc.getTo().getX() + ", " + arc.getTo().getY()
                        + ">]; ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
