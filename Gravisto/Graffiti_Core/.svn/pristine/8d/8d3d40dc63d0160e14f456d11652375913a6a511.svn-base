package org.graffiti.plugins.algorithms.labeling;

import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

public class DirectionTest {

    public static void main(String[] args) {
        GeometricalVector v;
        Direction dir;

        v = new GeometricalVector(1, 1);
        dir = new Direction(v);
        System.out.println("Direction(" + v + ") -> " + dir);
        v = new GeometricalVector(-1, 1);
        dir = new Direction(v);
        System.out.println("Direction(" + v + ") -> " + dir);
        v = new GeometricalVector(-1, -1);
        dir = new Direction(v);
        System.out.println("Direction(" + v + ") -> " + dir);
        v = new GeometricalVector(1, -1);
        dir = new Direction(v);
        System.out.println("Direction(" + v + ") -> " + dir);

        double coneAngle;
        Direction d1;
        Direction d2;

        d1 = new Direction(0);
        d2 = new Direction(0);
        coneAngle = Direction.coneAngle(d1, d2);
        System.out.println("Direction.coneAngle(" + d1 + ", " + d2 + ") -> "
                + coneAngle);

        d1 = new Direction(0);
        d2 = new Direction(Math.PI);
        coneAngle = Direction.coneAngle(d1, d2);
        System.out.println("Direction.coneAngle(" + d1 + ", " + d2 + ") -> "
                + coneAngle);

        d1 = new Direction(Math.PI);
        d2 = new Direction(1.125 * Math.PI);
        coneAngle = Direction.coneAngle(d1, d2);
        System.out.println("Direction.coneAngle(" + d1 + ", " + d2 + ") -> "
                + coneAngle);

        d1 = new Direction(1.75 * Math.PI);
        d2 = new Direction(0.25 * Math.PI);
        coneAngle = Direction.coneAngle(d1, d2);
        System.out.println("Direction.coneAngle(" + d1 + ", " + d2 + ") -> "
                + coneAngle);

        d1 = new Direction(Math.PI);
        d2 = new Direction(0);
        coneAngle = Direction.coneAngle(d1, d2);
        System.out.println("Direction.coneAngle(" + d1 + ", " + d2 + ") -> "
                + coneAngle);

        d1 = new Direction(1.125 * Math.PI);
        d2 = new Direction(Math.PI);
        coneAngle = Direction.coneAngle(d1, d2);
        System.out.println("Direction.coneAngle(" + d1 + ", " + d2 + ") -> "
                + coneAngle);

        d1 = new Direction(0.25 * Math.PI);
        d2 = new Direction(1.75 * Math.PI);
        coneAngle = Direction.coneAngle(d1, d2);
        System.out.println("Direction.coneAngle(" + d1 + ", " + d2 + ") -> "
                + coneAngle);

        double distance;

        d1 = new Direction(0);
        d2 = new Direction(0);
        distance = Direction.clockwiseDistance(d1, d2);
        System.out.println("Direction.clockwiseDistance(" + d1 + ", " + d2
                + ") -> " + distance);

        d1 = new Direction(0);
        d2 = new Direction(Math.PI);
        distance = Direction.clockwiseDistance(d1, d2);
        System.out.println("Direction.clockwiseDistance(" + d1 + ", " + d2
                + ") -> " + distance);

        d1 = new Direction(Math.PI);
        d2 = new Direction(1.125 * Math.PI);
        distance = Direction.clockwiseDistance(d1, d2);
        System.out.println("Direction.clockwiseDistance(" + d1 + ", " + d2
                + ") -> " + distance);

        d1 = new Direction(1.75 * Math.PI);
        d2 = new Direction(0.25 * Math.PI);
        distance = Direction.clockwiseDistance(d1, d2);
        System.out.println("Direction.clockwiseDistance(" + d1 + ", " + d2
                + ") -> " + distance);

        d1 = new Direction(Math.PI);
        d2 = new Direction(0);
        distance = Direction.clockwiseDistance(d1, d2);
        System.out.println("Direction.clockwiseDistance(" + d1 + ", " + d2
                + ") -> " + distance);

        d1 = new Direction(1.125 * Math.PI);
        d2 = new Direction(Math.PI);
        distance = Direction.clockwiseDistance(d1, d2);
        System.out.println("Direction.clockwiseDistance(" + d1 + ", " + d2
                + ") -> " + distance);

        d1 = new Direction(0.25 * Math.PI);
        d2 = new Direction(1.75 * Math.PI);
        distance = Direction.clockwiseDistance(d1, d2);
        System.out.println("Direction.clockwiseDistance(" + d1 + ", " + d2
                + ") -> " + distance);

    }

}
