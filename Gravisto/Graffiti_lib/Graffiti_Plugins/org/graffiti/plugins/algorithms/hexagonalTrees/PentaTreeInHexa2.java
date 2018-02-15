// =============================================================================
//
//   PentaTreeGrid.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.hexagonalTrees;

import java.util.Collection;
import java.util.LinkedList;

import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;

/**
 * @author Tom
 * @version $Revision$ $Date$
 */
public class PentaTreeInHexa2 extends TreeInHexa2 {

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        this.root = GraphChecker.checkTree(this.graph, 5);
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        if (root == null)
            throw new RuntimeException("Must call method \"check\" before "
                    + " calling \"execute\".");

        this.graph.getListenerManager().transactionStarted(this);
        removeBends(root);

        depth = calculateTreeDepth(root, 1);
        placeRoot();
        this.drawTree(root, 2, 1);
        System.out.println(compact(root));
        this.graph.getListenerManager().transactionFinished(this);
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "PentaTreeInHexa2";
    }

    private Contour compact(Node root) {
        Contour contour = new Contour(root);
        double shift = Double.MAX_VALUE;
        Collection<Contour> contours = new LinkedList<Contour>();
        for (Node node : root.getOutNeighbors()) {
            Contour c = compact(node);
            contours.add(c);
            shift = Math.min(shift, c.move());
            // shift = Math.min(shift, c.maxShiftToFather());
        }
        for (Contour c1 : contours) {
            for (Contour c2 : contours) {
                if (c1 != c2) {
                    shift = Math.min(shift, Contour.move(c1, c2));
                }
            }
        }
        if (contour.father != null) {
            for (Contour c : contours) {
                shift = Math.min(shift, Contour.moveToEdge(c, root,
                        contour.father));
            }
        }
        for (Node node : root.getOutNeighbors()) {
            moveSubtree(node, shift);
        }
        for (Contour c : contours) {
            c.updateCoordinates();
            c.changeRootToFather();
            contour.addContour(c);
        }

        return contour;
    }

    private void moveSubtree(Node root, double shift) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();
        double rootZ = rootX - rootY;
        Node father = root.getAllInNeighbors().iterator().next();
        NodeGraphicAttribute ngaFather = (NodeGraphicAttribute) father
                .getAttribute("graphics");
        double fatherX = ngaFather.getCoordinate().getX();
        double fatherY = ngaFather.getCoordinate().getY();
        double fatherZ = fatherX - fatherY;
        if (fatherX == rootX && fatherY < rootY) {
            moveSubtree(root, 0, -shift);
        } else if (fatherX == rootX && fatherY > rootY) {
            moveSubtree(root, 0, shift);
        } else if (fatherY == rootY && fatherX < rootX) {
            moveSubtree(root, -shift, 0);
        } else if (fatherY == rootY && fatherX > rootX) {
            moveSubtree(root, shift, 0);
        } else if (fatherZ == rootZ && fatherX < rootX) {
            moveSubtree(root, -shift, -shift);
        } else if (fatherZ == rootZ && fatherX > rootX) {
            moveSubtree(root, shift, shift);
        } else
            throw new RuntimeException("komischer shift");
    }

    public void moveSubtree(Node root, double dx, double dy) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        CoordinateAttribute ca = ngaRoot.getCoordinate();
        ca.setX(ca.getX() + dx * UNIT);
        ca.setY(ca.getY() + dy * UNIT);
        for (Node node : root.getAllOutNeighbors()) {
            moveSubtree(node, dx, dy);
        }
    }

    private void drawTree(Node placed, int level, int grandfather) {
        Node[] sons = new Node[0];
        sons = placed.getOutNeighbors().toArray(sons);
        if (sons.length == 1) {
            placeAtPosition(sons[0], Math.pow(3, (depth - level)), placed,
                    grandfather);
            drawTree(sons[0], level + 1, grandfather);
        } else if (sons.length == 2) {
            placeAtPosition(sons[0], Math.pow(3, (depth - level)), placed,
                    (grandfather + 5) % 6);
            drawTree(sons[0], level + 1, (grandfather + 5) % 6);
            placeAtPosition(sons[1], Math.pow(3, (depth - level)), placed,
                    (grandfather + 1) % 6);
            drawTree(sons[1], level + 1, (grandfather + 1) % 6);
        } else if (sons.length == 3) {
            placeAtPosition(sons[0], Math.pow(3, (depth - level)), placed,
                    (grandfather + 5) % 6);
            drawTree(sons[0], level + 1, (grandfather + 5) % 6);
            placeAtPosition(sons[1], Math.pow(3, (depth - level)), placed,
                    grandfather % 6);
            drawTree(sons[1], level + 1, grandfather % 6);
            placeAtPosition(sons[2], Math.pow(3, (depth - level)), placed,
                    (grandfather + 1) % 6);
            drawTree(sons[2], level + 1, (grandfather + 1) % 6);
        } else if (sons.length == 4) {
            placeAtPosition(sons[0], Math.pow(3, (depth - level)), placed,
                    (grandfather + 4) % 6);
            drawTree(sons[0], level + 1, (grandfather + 4) % 6);
            placeAtPosition(sons[1], Math.pow(3, (depth - level)), placed,
                    (grandfather + 5) % 6);
            drawTree(sons[1], level + 1, (grandfather + 5) % 6);
            placeAtPosition(sons[2], Math.pow(3, (depth - level)), placed,
                    (grandfather + 1) % 6);
            drawTree(sons[2], level + 1, (grandfather + 1) % 6);
            placeAtPosition(sons[3], Math.pow(3, (depth - level)), placed,
                    (grandfather + 2) % 6);
            drawTree(sons[3], level + 1, (grandfather + 2) % 6);
        } else if (sons.length == 5) {
            placeAtPosition(sons[0], Math.pow(3, (depth - level)), placed,
                    (grandfather + 4) % 6);
            drawTree(sons[0], level + 1, (grandfather + 4) % 6);
            placeAtPosition(sons[1], Math.pow(3, (depth - level)), placed,
                    (grandfather + 5) % 6);
            drawTree(sons[1], level + 1, (grandfather + 5) % 6);
            placeAtPosition(sons[2], Math.pow(3, (depth - level)), placed,
                    grandfather % 6);
            drawTree(sons[2], level + 1, grandfather % 6);
            placeAtPosition(sons[3], Math.pow(3, (depth - level)), placed,
                    (grandfather + 1) % 6);
            drawTree(sons[3], level + 1, (grandfather + 1) % 6);
            placeAtPosition(sons[4], Math.pow(3, (depth - level)), placed,
                    (grandfather + 2) % 6);
            drawTree(sons[4], level + 1, (grandfather + 2) % 6);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
