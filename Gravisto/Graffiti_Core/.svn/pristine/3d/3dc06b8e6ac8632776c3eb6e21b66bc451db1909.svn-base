package org.graffiti.plugins.algorithms.kandinsky;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.kandinsky.MCMFNode.Type;

/**
 * The interface to the Gravisto plugin mechanism. Constructs a Kandinsky model
 * which considers constraints if possible.
 */
public class SketchAlgorithm extends KandinskyAlgorithm {

    /** The List of angle constraints. */
    private LinkedList<AngleConstraint> angleList = new LinkedList<AngleConstraint>();

    /** The List of bend constraints. */
    private LinkedList<BendConstraint> bendList = new LinkedList<BendConstraint>();

    /** The list for the constraints, which should be added. */
    private LinkedList<String> constraints = new LinkedList<String>();

    /**
     * Algorithm computes the network flow.
     */
    protected ComputeFlow flow = new ComputeFlow();

    /** Checks if the format of the constraints is correct. */
    protected TestFormat test = new TestFormat();

    /**
     * Constraints are processed.
     */
    protected boolean isProcessed = false;

    /**
     * Creates a new <code>KandinskyAlgorithm</code>
     */
    public SketchAlgorithm() {
        parameters = new Parameter[0];
    }

    /**
     * Returns the name of the algorithm.
     * 
     * @return The name of the algorithm.
     */
    @Override
    public String getName() {
        return "Construct Sketch driven Network";
    }

    /**
     * Executes the planar test in GUI mode.
     */
    @Override
    public void execute() {
        startKandinskySketch();
        super.isChecked = false;
        super.computeFlow();
        super.transformOversaturatedDevices();
        super.removeObsoleteArcs();
        // super.printFlowArcs();
        ComputeOrthRepresentation orthRep = new ComputeOrthRepresentation(true,
                network, edgeRightFaceNode, edgeLeftFaceNode, bendList,
                edge_Start, edge_End, graph.getEdges());
        orthRep.computeFaceRep();
        orthRep.print();
        orthRep.check();
        CompactRepresentation compact = new CompactRepresentation(orthRep);
        compact.compact();
    }

    /**
     * Resets the algorithm.
     */
    @Override
    public void reset() {
        super.reset();
        planar.reset();
        angleList = new LinkedList<AngleConstraint>();
        bendList = new LinkedList<BendConstraint>();
        constraints = new LinkedList<String>();
        flow = new ComputeFlow();
        test = new TestFormat();
        java.lang.System.gc();
    }

    /**
     * Initializes the network.
     */
    private void startKandinskySketch() {
        graph.getListenerManager().transactionStarted(this);
        super.calculateFaces();
        // addConstraint("[3; (1, 3); (3, 2); 90; 4]");
        // addConstraint("[3; (2, 3); (3, 4); 90; 4]");
        // addConstraint("[4; (4, 1); (1, 4); 90; 4]");
        // addConstraint("[2; (1, 2); (2, 4); 90; 4]");
        // addConstraint("[4; (0, 4); (4, 3); Face 2; 90; 4]");
        constructNetwork();
        // printNodes();
        super.printFaces();
        // addConstraint("[(4, 3); 0; 4]");

        // addConstraint("[(2, 0); 0; 4]");
        // addConstraint("[(2, 1); 0; 4]");
        // addConstraint("[(4, 1); 0; 4]");
        // addConstraint("[(3, 1); 0; 4]");
        // printArcs();
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * Reads the constraint input of the sketch.
     */
    private void getSketchConstraints() {
        String line; // Zeile, in der ein Constraint definiert wird
        BufferedReader f;
        printHowToDefineConstraints(); // druckt Anleitung f�r die Definition
        try {
            f = new BufferedReader(
                    new FileReader(
                            "c:/Dokumente und Einstellungen/Sonja/"
                                    + "workspace/Graffiti_Plugins/org/graffiti/plugins/"
                                    + "algorithms/kandinsky/Constraints.txt"));

            while ((line = f.readLine()) != null) {
                line = line.trim();
                checkConstraint(line);
            }
            if (!constraints.isEmpty()) {
                Iterator<String> it = constraints.iterator();
                while (it.hasNext()) {
                    line = it.next().trim();
                    checkConstraint(line);
                }
            }
            isProcessed = true;
            f.close();
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Datei");
        }
        System.out.println("");
    }

    /**
     * Prints the "How to define a constraint"-manual.
     */
    private void printHowToDefineConstraints() {
        System.out.println("WARNUNG: ");
        System.out.println("Der Algorithmus liefert nur korrekte Ergebnisse, "
                + "wenn ein zusammenh�ngender Graph eingegeben wird!!!");
        System.out.println();
        System.out.println("CONSTRAINTS aus der Datei Constraints.txt:");
        System.out.println("Geben Sie bitte nur einen Constraint pro Zeile "
                + "an.");
        System.out.println("Verwenden Sie dabei folgendes Format f�r Winkel: ");
        System.out
                .println("   [Knoten; Kante1; Kante2; Winkel; Fl�che; Kosten]");
        System.out.println("   Die Kanten werden dabei als Tupel aus zwei "
                + "Knoten angegeben, ");
        System.out
                .println("   die Angabe einer Fl�che ist optional (default: Au�enfl�che),");
        System.out.println("   der Winkel hat einen Wert aus {0, 90, 180, 270"
                + ", 360}.");
        System.out
                .println("       Beispiel 1: [2; (1, 2); (0, 2); Face 1; 90; 3]");
        System.out.println("       Beispiel 2: [2; (1, 2); (0, 2); 90; 3]");
        System.out.println("Verwenden Sie dabei folgendes Format f�r die "
                + "Knicke:");
        System.out.println("   [(Knoten1, Knoten2); Knickrichtungen; "
                + "Kosten f�rs Nicht-Erf�llen]");
        System.out.println("   Knoten1 ist der Anfangspunkt der Kante, "
                + "Knoten 2 der Endpunkt,");
        System.out.println("   ein Knick mit dem Wert 0 bewirkt den "
                + "Rechtsknick in der Kante, 1 den Linksknick.");
        System.out.println("   mehrere Knicke einer Kante werden als Bit-"
                + "String dargestellt.");
        System.out.println("Geben Sie dabei alle Knicke f�r eine Kante in 1 "
                + "einzigen Constraint an.");
        System.out.println("       Beispiel: [(0, 5); 010; 4]");
        System.out.println();
        System.out.println("Ihre Angaben:");
    }

    /**
     * Checks the format of a constraint.
     * 
     * @param line
     *            The constraint
     */
    private void checkConstraint(String line) {
        if (test.isFormatCorrect(line, network)) {
            if (test.getType() == Type.ANGLE) {
                angleList.add(test.getAngleConstraint());
            }
            if (test.getType() == Type.BEND) {
                bendList.add(test.getBendConstraint());
            }
        }
    }

    /**
     * Executes the construction of the network with the Sketch extensions.
     */
    @Override
    protected void constructNetwork() {
        super.constructNetwork(); // MCMFNode f�r MCMFNode und Fl�chen
        getSketchConstraints();
        constructSketchExtensions(); // Sketch-Constraints einf�gen
    }

    /**
     * Constructs the angle and bend constraints.
     */
    private void constructSketchExtensions() {
        // F�r alle AngleConstraint
        for (AngleConstraint a : angleList) {
            constructSketchAngles(a);
        }
        for (BendConstraint b : bendList) {
            constructSketchBends(b);
        }
    }

    /**
     * Constructs the angle constraints.
     */
    /*
     * Erzeugt: Hilfsknoten --> Winkelknoten, Winkelknoten <--> Knoten, Quelle
     * --> Winkelknoten bzw. Winkelknoten --> Senke, Winkelknoten --> Fl�che
     * 
     * Gel�scht: Hilfsknoten --> Knoten, Knoten --> Fl�che.
     */
    private void constructSketchAngles(AngleConstraint a) {
        // der Knoten an dem der Winkel ge�ndert werden soll
        GraphNode node = a.getAngleNode();
        // die Fl�che an der der Winkel ge�ndert werden soll
        FaceNode face = a.getFace();
        int angle = a.getAngle() - 1; // der gew�nschte Winkel
        int alpha = a.getCost(); // die Kosten f�rs Nichterf�llen
        // die Kante, welche von dem Constraint betroffen ist
        Edge edge = a.getEdge();
        // Erzeuge den AngleNode an einem Knoten f�r eine Fl�che
        AngleNode aNode = network.createAngleNode(node, face, edge);
        node.addAngleNode(face, aNode);

        // Entferne die Kante zwischen Knoten und Fl�che und ersetze sie
        // durch eine Kante vom Winkelknoten zur Fl�che
        MCMFArc arc1 = network.removeArc(network.searchArc(node, face, edge));
        network.removeArc(arc1.getRestArc());
        arc1 = network.createArc(aNode, face, 3, 0);
        MCMFArc arc2 = network.createArc(face, aNode, 0, 0);
        arc1.setRestArc(arc2);
        arc2.setRestArc(arc1);

        // Setze Supply bzw. Demand f�r den Winkelknoten
        if (angle > 0) {
            arc1 = network.createArc(network.getS(), aNode, angle, 0);
            arc2 = network.createArc(aNode, network.getS(), 0, 0);
            arc1.setRestArc(arc2);
            arc2.setRestArc(arc1);
        } else {
            if (angle < 0) {
                int cap = angle * (-1);
                arc1 = network.createArc(aNode, network.getT(), cap, 0);
                arc2 = network.createArc(network.getT(), aNode, 0, 0);
                arc1.setRestArc(arc2);
                arc2.setRestArc(arc1);
            }
        }

        // Help_Knoten_Fl�che
        HelpNode help = network.searchHelpNode(node, face, edge);
        // Entferne die Kante zwischen Hilfsknoten und Knoten
        arc1 = network.removeArc(network.searchArc(help, node));
        network.removeArc(arc1.getRestArc());
        // Kante zwischen Hilfsknoten und Winkelknoten mit Kapazit�t 1
        // und Kosten 0
        int cap = 1;
        arc1 = network.createArc(help, aNode, cap, 0);
        arc2 = network.createArc(aNode, help, 0, 0);
        arc1.setRestArc(arc2);
        arc2.setRestArc(arc1);

        // Kanten zwischen Winkelknoten und Knoten k mit Kapazit�t unendlich
        // und Kosten alpha
        cap = Integer.MAX_VALUE;
        arc1 = network.createArc(aNode, node, cap, alpha);
        arc2 = network.createArc(node, aNode, cap, alpha);
        arc1.setRestArc(arc2);
        arc2.setRestArc(arc1);
        // Supply f�r zugeh�rigen Knoten herabsetzen
        setCapAngleNode(node, angle);

    }

    /**
     * Constructs the bend constraints.
     */
    private void constructSketchBends(BendConstraint b) {
        GraphNode n1 = b.getArcNode1(); // Anfangspunkt der Kante
        GraphNode n2 = b.getArcNode2(); // Endpunkt der Kante
        Edge edge = b.getEdge(); // die zu knickende Kante
        // Liste der Knicke: TRUE --> Rechtsknick, FALSE --> Linksknick
        LinkedList<Boolean> bends = b.getBends();
        int costB = b.getCost(); // Kosten f�rs Nichterf�llen
        int count; // Z�hler f�r den i-ten Knick auf einer Kante
        int angle; // gew�nschte Knickrichtung: 0 --> rechts, 1 --> links
        // Erster und letzter Knick sind VertexBends, die anderen FaceBends
        if (bends.get(0)) // Rechtsknick
        {
            angle = 0;
            constructVertexBend(n1, n2, edge, angle, costB, true);
        } else
        // Linksknick
        {
            angle = 1;
            constructVertexBend(n1, n2, edge, angle, costB, true);
        }
        if (bends.size() > 2) // FaceBends vorhanden
        {
            for (count = 1; count < bends.size() - 1; count++) {
                if (bends.get(count)) // Rechtsknick
                {
                    angle = 0;
                } else {
                    angle = 1;
                }
                constructFaceBend(n1, n2, edge, angle, costB, (count - 1));
            }
        }
        if (bends.size() > 1) // letzter Knick ist VertexBend
        {
            if (bends.get(bends.size() - 1)) // Rechtsknick
            {
                angle = 0;
                constructVertexBend(n1, n2, edge, angle, costB, false);
            } else
            // Linksknick
            {
                angle = 1;
                constructVertexBend(n1, n2, edge, angle, costB, false);
            }
        }
    }

    /**
     * Constructs a vertex bend constraint.
     * 
     * @param n1
     *            the starting point of the edge.
     * @param n2
     *            the end point of the edge.
     * @param edge
     *            the edge where the bend is to be constructed.
     * @param angle
     *            the desired angle.
     * @param costB
     *            the costs for not fullfilling the constraint.
     * @param first
     *            if it is the first or the last vertex bend.
     */
    /*
     * VertexBends haben eine zus�tzliche Kante vom b' zu dem Hilfsknoten, zu
     * dessen Fl�che der Knick gehen soll, um kostenlos einen Vertexknick
     * aufgrund eines 0�-Winkels realisieren zu k�nnen.
     */
    private void constructVertexBend(GraphNode n1, GraphNode n2, Edge edge,
            int angle, int costB, boolean first) {
        String label_bend = "Bend_" + n1.getLabel() + "_" + n2.getLabel();
        String label_bend_0 = "Bend_'_" + n1.getLabel() + "_" + n2.getLabel();
        if (!first) {
            label_bend = label_bend + "_last";
            label_bend_0 = label_bend_0 + "_last";
        }
        // erzeuge b
        BendNode bend = network.createBendNode(label_bend, edge);
        // erzeuge b'
        BendNode bend_0 = network.createBendNode(label_bend_0, edge);
        createBend(edge, angle, costB, bend, bend_0);

        // Hilfsknoten, zu dem eine zus�tzliche Kante f�hrt
        HelpNode h_l = null;
        HelpNode h_r = null;
        if (first) {
            h_r = network.searchHelpNode(n1, edge);
            h_l = h_r.getOther(edge);
        } else {
            h_l = network.searchHelpNode(n2, edge);
            h_r = h_l.getOther(edge);
        }

        MCMFArc bendHelp = null; // Dritte Kante des Devices
        int cap = 1;
        if (angle == 0) {
            // Rechtsknick gew�nscht --> kosteng�nstige Kante zu H_f_r
            bendHelp = network.createArc(bend_0, h_l, cap, 0);
            MCMFArc arc1 = bendHelp;
            MCMFArc arc2 = network.createArc(h_l, bend_0, 0, 0);
            arc1.setRestArc(arc2);
            arc2.setRestArc(arc1);
        } else { // Linksknick gew�nscht --> kosteng�nstige Kante zu H_f_l
            bendHelp = network.createArc(bend_0, h_r, cap, 0);
            MCMFArc arc1 = bendHelp;
            MCMFArc arc2 = network.createArc(h_r, bend_0, 0, 0);
            arc1.setRestArc(arc2);
            arc2.setRestArc(arc1);
        }
        // Einf�gen der Kante BendNode_0 --> HelpNode als 3. Teil des Devices
        if (first) {
            for (Device d : n1.getDeviceList()) {
                if (d.getEdge() == bend_0.getEdge()) {
                    d.setThree(bendHelp);
                    break;
                }
            }
        } else {
            for (Device d : n2.getDeviceList()) {
                if (d.getEdge() == bend_0.getEdge()) {
                    d.setThree(bendHelp);
                    break;
                }
            }
        }
    }

    /**
     * Constructs a face bend constraint.
     * 
     * @param n1
     *            the starting point of the edge.
     * @param n2
     *            the end point of the edge.
     * @param edge
     *            the edge where the bend is to be constructed.
     * @param angle
     *            the desired angle.
     * @param costB
     *            the costs for not fullfilling the constraint.
     * @param count
     *            the number of the face bend.
     */
    private void constructFaceBend(GraphNode n1, GraphNode n2, Edge edge,
            int angle, int costB, int count) {
        String label_bend = "Bend_" + n1.getLabel() + "_" + n2.getLabel() + "_"
                + count;
        String label_bend_0 = "Bend_'_" + n1.getLabel() + "_" + n2.getLabel()
                + "_" + count;
        // erzeuge b
        BendNode bend = network.createBendNode(label_bend, edge);
        // erzeuge b'
        BendNode bend_0 = network.createBendNode(label_bend_0, edge);
        createBend(edge, angle, costB, bend, bend_0);
    }

    /**
     * Creates the two parts of a bend (b and b') and the corresponding arcs.
     * 
     * @param edge
     *            the edge where the bend is to be constructed.
     * @param angle
     *            the desired angle.
     * @param costB
     *            the costs for not fullfilling the constraint.
     * @param bend
     *            The BendNode b.
     * @param bend_0
     *            The BendNode b'.
     */
    private void createBend(Edge edge, int angle, int costB, BendNode bend,
            BendNode bend_0) {
        // b erh�lt Supply 2
        MCMFArc arc1 = network.createArc(network.getS(), bend, 2, 0);
        // Restnetzwerkkante
        MCMFArc arc2 = network.createArc(bend, network.getS(), 0, 0);
        arc1.setRestArc(arc2);
        arc2.setRestArc(arc1);
        // Kante zwischen b und b'mit Kapazit�t 1 und Kosten 0
        arc1 = network.createArc(bend, bend_0, 1, 0);
        arc2 = network.createArc(bend_0, bend, 0, 0); // Restnetzwerkkante
        arc1.setRestArc(arc2);
        arc2.setRestArc(arc1);

        // Heruntersetzen des Supplies der angrenzenden beiden Fl�chen
        FaceNode f_l = super.edgeLeftFaceNode.get(edge);
        FaceNode f_r = super.edgeRightFaceNode.get(edge);
        reduceSupply(f_l);
        reduceSupply(f_r);

        /*
         * TRUE bedeutet Rechtsknick, FALSE Linksknick. Rechtsknick: Kante von
         * Bend zur linken Fl�che f_links mit Kap 2 und Kosten 0, Kante von Bend
         * zur rechten Fl�che f_rechts mit Kap 1 und Kosten consCost, Kante von
         * Bend zum Hilfsknoten H_n1_fr mit Kap 1 und Kosten 2c. Linksknick:
         * Kante von Bend zur rechten Fl�che f_rechts mit Kap 2 und Kosten 0,
         * Kante von Bend zur linken Fl�che f_links mit Kap 1 und Kosten
         * consCost, Kante von Bend zum Hilfsknoten H_n1_fl mit Kap 1 und Kosten
         * 2c.
         */

        MCMFArc arc = network.searchFFArc(f_r, f_l, edge);
        // Ist dann null, wenn ein Knick f�r eine Kante mit Endknoten-Grad 1
        // definiert wird
        if (arc != null) {
            arc.setCost(1 + costB);
        }
        arc = network.searchFFArc(f_l, f_r, edge);
        if (arc != null) {
            arc.setCost(1 + costB);
        }
        int cap = 1;
        if (angle == 0) {
            // Rechtsknick gew�nscht --> kosteng�nstige Kante zu f_r
            arc1 = network.createArc(bend, f_l, 2, 0);
            arc2 = network.createArc(f_l, bend, 0, 0);
            arc1.setRestArc(arc2);
            arc2.setRestArc(arc1);
            arc1 = network.createArc(bend_0, f_r, cap, costB);
            arc2 = network.createArc(f_r, bend_0, 0, -costB);
            arc1.setRestArc(arc2);
            arc2.setRestArc(arc1);
        } else { // Linksknick gew�nscht --> kosteng�nstige Kante zu f_l
            arc1 = network.createArc(bend, f_r, 2, 0);
            arc2 = network.createArc(f_r, bend, 0, 0);
            arc1.setRestArc(arc2);
            arc2.setRestArc(arc1);
            arc1 = network.createArc(bend_0, f_l, cap, costB);
            arc2 = network.createArc(f_l, bend_0, 0, -costB);
            arc1.setRestArc(arc2);
            arc2.setRestArc(arc1);
        }
    }

    /**
     * Calculates the new capacity for a node. Changes the edge from the source
     * to the node.
     * 
     * @param node
     *            The GraphNode for which an AngleConstraint is given.
     * @param angle
     *            The target value of the AngleConstraint.
     */
    private void setCapAngleNode(GraphNode node, int angle) {
        MCMFArc edge = network.searchArc(network.getS(), node);
        if (edge != null) { // es existiert eine Kante von s nach n
            int cap = edge.getCap() - angle;
            if (cap > 0) {
                edge.setCap(cap); // �ndern des Supplies
            } else {
                // neue Kapazit�t ist null --> Kante l�schen
                if (cap == 0) {
                    MCMFArc arc1 = network.removeArc(edge);
                    network.removeArc(arc1.getRestArc());
                } else {
                    // neue Kapazit�t ist kleiner null --> Kante nach t
                    MCMFArc arc1 = network.removeArc(edge);
                    network.removeArc(arc1.getRestArc());
                    cap *= -1;
                    arc1 = network.createArc(node, network.getT(), cap, 0);
                    MCMFArc arc2 = network
                            .createArc(network.getT(), node, 0, 0);
                    arc1.setRestArc(arc2);
                    arc2.setRestArc(arc1);
                }
            }
        } else { // es existiert eine Kante von n nach t
            edge = network.searchArc(node, network.getT());
            if (edge != null) {
                int cap = edge.getCap() + angle;
                if (cap > 0) {
                    edge.setCap(cap);
                } else { // neue Kapazit�t ist null --> Kante l�schen
                    if (cap == 0) {
                        MCMFArc arc1 = network.removeArc(edge);
                        network.removeArc(arc1.getRestArc());
                    } else {
                        // neue Kapazit�t ist kleiner null --> Kante nach s
                        MCMFArc arc1 = network.removeArc(edge);
                        network.removeArc(arc1.getRestArc());
                        cap *= -1;
                        arc1 = network.createArc(network.getS(), node, cap, 0);
                        MCMFArc arc2 = network.createArc(node, network.getS(),
                                0, 0);
                        arc1.setRestArc(arc2);
                        arc2.setRestArc(arc1);
                    }
                }
            } else { // es existierte keine Kante
                int cap = angle;
                if (angle > 0) {
                    MCMFArc arc1 = network.createArc(node, network.getT(), cap,
                            0);
                    MCMFArc arc2 = network
                            .createArc(network.getT(), node, 0, 0);
                    arc1.setRestArc(arc2);
                    arc2.setRestArc(arc1);
                } else {
                    if (angle < 0) {
                        cap *= -1;
                        MCMFArc arc1 = network.createArc(network.getS(), node,
                                cap, 0);
                        MCMFArc arc2 = network.createArc(node, network.getS(),
                                0, 0);
                        arc1.setRestArc(arc2);
                        arc2.setRestArc(arc1);
                    }
                }
            }
        }
    }

    /**
     * Reduces the supply for a FaceNode by 1.
     * 
     * @param f
     *            the FaceNode whose supply is to be reduced.
     */
    private void reduceSupply(FaceNode f) {
        MCMFArc arc = network.searchArc(network.getS(), f);
        if (arc == null) // FaceNode hat keinen positiven Supply
        {
            arc = network.searchArc(f, network.getT());
            if (arc != null) // FaceNode hat Demand
            {
                arc.setCap(arc.getCap() + 1);
            } else {
                MCMFArc arc1 = network.createArc(f, network.getT(), 1, 0);
                MCMFArc arc2 = network.createArc(network.getT(), f, 0, 0);
                arc1.setRestArc(arc2);
                arc2.setRestArc(arc1);
            }
        } else
        // Supply des FaceNodes reduzieren
        {
            int x = arc.getCap() - 1;
            if (x == 0) {
                MCMFArc arc1 = network.removeArc(arc);
                network.removeArc(arc1.getRestArc());
            } else {
                arc.setCap(x);
            }
        }
    }

    /**
     * Adds a new constraint after checking the format.
     * 
     * @param line
     *            the definition of a constraint
     */
    protected void addConstraint(String line) {
        // Die definiertenConstraints wurden noch nicht �berpr�ft
        if (!isProcessed) {
            constraints.add(line);
        } else {
            // Extra-Pr�fung des neuen Constraints
            if (test.isFormatCorrect(line, network)) {
                if (test.getType() == Type.ANGLE) {
                    AngleConstraint aConstraint = test.getAngleConstraint();
                    if (!angleList.contains(aConstraint)) {
                        angleList.add(aConstraint);
                        constructSketchAngles(aConstraint);
                    }
                }
                if (test.getType() == Type.BEND) {
                    BendConstraint bConstraint = test.getBendConstraint();
                    if (!bendList.contains(bConstraint)) {
                        bendList.add(bConstraint);
                        constructSketchBends(bConstraint);
                    }
                }
            }
        }
        // System.out.println("");
    }
}
