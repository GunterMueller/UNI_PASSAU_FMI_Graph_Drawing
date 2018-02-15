package org.graffiti.plugins.algorithms.hexagonalTrees;

import org.graffiti.graph.Node;
import org.graffiti.graphics.NodeGraphicAttribute;

public class Contour {
    double left;

    double top;

    double bottom;

    double right;

    double topRight;

    double bottomLeft;

    Node root;

    double rootX;

    double rootY;

    double rootZ;

    Node father;

    double fatherX;

    double fatherY;

    double fatherZ;

    static double UNIT;

    public Contour(Node root) {
        UNIT = TreeInHexa2.UNIT;
        left = 0;
        top = 0;
        bottom = 0;
        right = 0;
        topRight = 0;
        bottomLeft = 0;
        this.root = root;
        updateCoordinates();
    }

    public void changeRootToFather() {
        if (rootX == fatherX) {
            double dif = (rootY - fatherY) / UNIT;
            top -= dif;
            bottom += dif;
            topRight -= dif;
            bottomLeft += dif;
        } else if (rootY == fatherY) {
            double dif = (rootZ - fatherZ) / UNIT;
            left -= dif;
            right += dif;
            topRight += dif;
            bottomLeft -= dif;
        } else if (rootZ == fatherZ) {
            double dif = (rootX - fatherX) / UNIT;
            left -= dif;
            right += dif;
            top -= dif;
            bottom += dif;
        } else
            throw new RuntimeException("double");
        root = father;
        rootX = fatherX;
        rootY = fatherY;
        rootZ = fatherZ;
        if (root.getInDegree() > 0) {
            father = root.getAllInNeighbors().iterator().next();
            NodeGraphicAttribute ngaFather = (NodeGraphicAttribute) father
                    .getAttribute("graphics");
            fatherX = ngaFather.getCoordinate().getX();
            fatherY = ngaFather.getCoordinate().getY();
            fatherZ = fatherX - fatherY;
        } else {
            father = null;
        }
    }

    public void updateCoordinates() {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        rootX = ngaRoot.getCoordinate().getX();
        rootY = ngaRoot.getCoordinate().getY();
        rootZ = rootX - rootY;
        if (root.getInDegree() > 0) {
            father = root.getAllInNeighbors().iterator().next();
            NodeGraphicAttribute ngaFather = (NodeGraphicAttribute) father
                    .getAttribute("graphics");
            fatherX = ngaFather.getCoordinate().getX();
            fatherY = ngaFather.getCoordinate().getY();
            fatherZ = fatherX - fatherY;
        } else {
            father = null;
        }

    }

    public void addPoint(double x, double y) {
        double z = x - y;
        if (x < rootX - left * UNIT) {
            left = (rootX - x) / UNIT;
        }
        if (x > rootX + right * UNIT) {
            right = (x - rootX) / UNIT;
        }
        if (y < rootY - top * UNIT) {
            top = (rootY - y) / UNIT;
        }
        if (y > rootY + bottom * UNIT) {
            bottom = (y - rootY) / UNIT;
        }
        if (z < rootZ - bottomLeft * UNIT) {
            bottomLeft = (rootZ - z) / UNIT;
        }
        if (z > rootZ + topRight * UNIT) {
            topRight = (z - rootZ) / UNIT;
        }
    }

    public void addContour(Contour toAdd) {
        if (root != toAdd.root)
            throw new RuntimeException("falsche Wurzel");

        left = Math.max(left, toAdd.left);
        top = Math.max(top, toAdd.top);
        right = Math.max(right, toAdd.right);
        bottom = Math.max(bottom, toAdd.bottom);
        topRight = Math.max(topRight, toAdd.topRight);
        bottomLeft = Math.max(bottomLeft, toAdd.bottomLeft);

    }

    public boolean intersects(double x1, double y1, double x2, double y2) {
        double x = x1;
        double y = y1;
        if (isInside(x, y))
            return true;
        double dx = (x2 - x1) / UNIT;
        double dy = (y2 - y1) / UNIT;
        while (x != x2 || y != y2) {
            x += dx;
            y += dy;
            if (isInside(x, y))
                return true;
        }
        return false;
    }

    public boolean isInside(double x, double y) {
        if (x < rootX - left * UNIT)
            return false;
        if (x > rootX + right * UNIT)
            return false;
        if (y < rootY - top * UNIT)
            return false;
        if (y > rootY + bottom * UNIT)
            return false;
        if (x - y < rootX - rootY - bottomLeft * UNIT)
            return false;
        if (x - y > rootX - rootY + topRight * UNIT)
            return false;
        return true;
    }

    public double maxShiftToFather() {
        if (fatherX == rootX && fatherY < rootY) {
            double maxYShift = (rootY - top * UNIT - fatherY) / UNIT - 1;
            double maxZShift = (-rootZ - topRight * UNIT + fatherZ) / UNIT - 1;
            return Math.min(maxYShift, maxZShift);
        }
        if (fatherX == rootX && fatherY > rootY) {
            double maxYShift = (-rootY - bottom * UNIT + fatherY) / UNIT - 1;
            double maxZShift = (rootZ - bottomLeft * UNIT - fatherZ) / UNIT - 1;
            return Math.min(maxYShift, maxZShift);
        }
        if (fatherY == rootY && fatherX < rootX) {
            double maxXShift = (rootX - left * UNIT - fatherX) / UNIT - 1;
            double maxZShift = (rootZ - bottomLeft * UNIT - fatherZ) / UNIT - 1;
            return Math.min(maxXShift, maxZShift);
        }
        if (fatherY == rootY && fatherX > rootX) {
            double maxXShift = (-rootX - right * UNIT + fatherX) / UNIT - 1;
            double maxZShift = (-rootZ - topRight * UNIT + fatherZ) / UNIT - 1;
            return Math.min(maxXShift, maxZShift);
        }
        if (fatherZ == rootZ && fatherX < rootX) {
            double maxXShift = (rootX - left * UNIT - fatherX) / UNIT - 1;
            double maxYShift = (rootY - top * UNIT - fatherY) / UNIT - 1;
            return Math.min(maxXShift, maxYShift);
        }
        if (fatherZ == rootZ && fatherX > rootX) {
            double maxXShift = (-rootX - right * UNIT + fatherX) / UNIT - 1;
            double maxYShift = (-rootY - bottom * UNIT + fatherY) / UNIT - 1;
            return Math.min(maxXShift, maxYShift);
        }
        throw new RuntimeException("double");
    }

    public void moveRoot(double shift) {
        if (fatherX == rootX && fatherY < rootY) {
            rootY -= shift * UNIT;
        } else if (fatherX == rootX && fatherY > rootY) {
            rootY += shift * UNIT;
        } else if (fatherY == rootY && fatherX < rootX) {
            rootX -= shift * UNIT;
        } else if (fatherY == rootY && fatherX > rootX) {
            rootX += shift * UNIT;
        } else if (fatherZ == rootZ && fatherX < rootX) {
            rootX -= shift * UNIT;
            rootY -= shift * UNIT;
        } else if (fatherZ == rootZ && fatherX > rootX) {
            rootX += shift * UNIT;
            rootY += shift * UNIT;
        } else
            throw new RuntimeException("komischer shift");
    }

    public static double moveToEdge(Contour c, Node n1, Node n2) {
        double shift = 0;
        double oldRootX = c.rootX;
        double oldRootY = c.rootY;

        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) n1
                .getAttribute("graphics");
        double x1 = ngaRoot.getCoordinate().getX();
        double y1 = ngaRoot.getCoordinate().getY();

        ngaRoot = (NodeGraphicAttribute) n2.getAttribute("graphics");
        double x2 = ngaRoot.getCoordinate().getX();
        double y2 = ngaRoot.getCoordinate().getY();

        while (!c.intersects(x1, y1, x2, y2)) {
            shift++;
            c.moveRoot(1);
        }
        c.rootX = oldRootX;
        c.rootY = oldRootY;
        shift--;

        return shift;
    }

    public static double move(Contour c1, Contour c2) {
        if (c1 == null || c2 == null)
            return Double.MAX_VALUE;
        double shift = 0;
        double c1rootX = c1.rootX;
        double c1rootY = c1.rootY;
        double c2rootX = c2.rootX;
        double c2rootY = c2.rootY;
        while (Contour.disjoint(c1, c2)) {
            shift++;
            c1.moveRoot(1);
            c2.moveRoot(1);
        }
        c1.rootX = c1rootX;
        c1.rootY = c1rootY;
        c2.rootX = c2rootX;
        c2.rootY = c2rootY;
        shift--;

        return shift;
    }

    public double move() {
        double shift = 0;
        double oldRootX = rootX;
        double oldRootY = rootY;
        while (!isInside(fatherX, fatherY)) {
            shift++;
            moveRoot(1);
        }
        rootX = oldRootX;
        rootY = oldRootY;
        shift--;

        return shift;
    }

    private static boolean disjoint(Contour c1, Contour c2) {
        double c1left = c1.rootX - c1.left * UNIT;
        double c1right = c1.rootX + c1.right * UNIT;
        double c1top = c1.rootY - c1.top * UNIT;
        double c1bottom = c1.rootY + c1.bottom * UNIT;

        double c2left = c2.rootX - c2.left * UNIT;
        double c2right = c2.rootX + c2.right * UNIT;
        double c2top = c2.rootY - c2.top * UNIT;
        double c2bottom = c2.rootY + c2.bottom * UNIT;

        if (c1left > c2right)
            return true;
        if (c1right < c2left)
            return true;
        if (c1top > c2bottom)
            return true;
        if (c1bottom < c2top)
            return true;

        double left = Math.min(c1.rootX - c1.left * UNIT, c2.rootX - c2.left
                * UNIT);
        double top = Math.min(c1.rootY - c1.top * UNIT, c2.rootY - c2.top
                * UNIT);
        double right = Math.max(c1.rootX + c1.right * UNIT, c2.rootX + c2.right
                * UNIT);
        double bottom = Math.max(c1.rootY + c1.bottom * UNIT, c2.rootY
                + c2.bottom * UNIT);
        for (double x = left; x <= right; x += UNIT) {
            for (double y = top; y <= bottom; y += UNIT) {
                if (c1.isInside(x, y) && c2.isInside(x, y))
                    return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "";
        s += "left: " + left + "\n";
        s += "right: " + right + "\n";
        s += "top: " + top + "\n";
        s += "bottom: " + bottom + "\n";
        s += "bottomleft: " + bottomLeft + "\n";
        s += "topright: " + topRight + "\n";
        if (father != null) {
            s += "dist: " + maxShiftToFather() + "\n";
        }
        return s;
    }
}
