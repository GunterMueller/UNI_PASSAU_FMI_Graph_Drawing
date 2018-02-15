package org.graffiti.plugins.algorithms.hexagonalTrees;

public class PentaContour {
    private double leftX;

    private double leftY;

    private double rightX;

    private double rightY;

    private double leftAboveX;

    private double leftAboveY;

    private double rightAboveX;

    private double rightAboveY;

    private double leftBelowX;

    private double leftBelowY;

    private double rightBelowX;

    private double rightBelowY;

    public PentaContour(double lX, double lY, double rX, double rY, double laX,
            double laY, double lbX, double lbY, double raX, double raY,
            double rbX, double rbY) {
        this.leftX = lX;
        this.leftY = lY;
        this.rightX = rX;
        this.rightY = rY;

        this.leftAboveX = laX;
        this.leftAboveY = laY;
        this.rightAboveX = raX;
        this.rightAboveY = raY;

        this.leftBelowX = lbX;
        this.leftBelowY = lbY;
        this.rightBelowX = rbX;
        this.rightBelowY = rbY;
    }

    public double getLeftX() {
        return leftX;
    }

    public void setLeftX(double leftX) {
        this.leftX = leftX;
    }

    public double getLeftY() {
        return leftY;
    }

    public void setLeftY(double leftY) {
        this.leftY = leftY;
    }

    public double getRightX() {
        return rightX;
    }

    public void setRightX(double rightX) {
        this.rightX = rightX;
    }

    public double getRightY() {
        return rightY;
    }

    public void setRightY(double rightY) {
        this.rightY = rightY;
    }

    public double getLeftAboveX() {
        return leftAboveX;
    }

    public void setLeftAboveX(double leftAboveX) {
        this.leftAboveX = leftAboveX;
    }

    public double getLeftAboveY() {
        return leftAboveY;
    }

    public void setLeftAboveY(double leftAboveY) {
        this.leftAboveY = leftAboveY;
    }

    public double getRightAboveX() {
        return rightAboveX;
    }

    public void setRightAboveX(double rightAboveX) {
        this.rightAboveX = rightAboveX;
    }

    public double getRightAboveY() {
        return rightAboveY;
    }

    public void setRightAboveY(double rightAboveY) {
        this.rightAboveY = rightAboveY;
    }

    public double getLeftBelowX() {
        return leftBelowX;
    }

    public void setLeftBelowX(double leftBelowX) {
        this.leftBelowX = leftBelowX;
    }

    public double getLeftBelowY() {
        return leftBelowY;
    }

    public void setLeftBelowY(double leftBelowY) {
        this.leftBelowY = leftBelowY;
    }

    public double getRightBelowX() {
        return rightBelowX;
    }

    public void setRightBelowX(double rightBelowX) {
        this.rightBelowX = rightBelowX;
    }

    public double getRightBelowY() {
        return rightBelowY;
    }

    public void setRightBelowY(double rightBelowY) {
        this.rightBelowY = rightBelowY;
    }
}
