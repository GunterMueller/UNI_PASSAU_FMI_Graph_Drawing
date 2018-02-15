package tests.graffiti.plugin.algorithm.animation;

import org.graffiti.plugin.algorithm.animation.AbstractAnimation;
import org.graffiti.plugin.algorithm.animation.Animation;

public class MockAnimation extends AbstractAnimation implements Animation {

    private static int noOfInstances = 0;

    private String name = "";

    private int noOfSteps = 0;

    private int nextStep = 0;

    private boolean isCleared = true;

    private boolean isReady = true;

    private boolean supportsClear = true;

    private boolean supportsPreviousStep = true;

    private boolean isFinished = false;

    public MockAnimation() {
        this(4);
    }

    public MockAnimation(int noOfSteps) {
        this(noOfSteps, true, true, true);
    }

    public MockAnimation(boolean isReady) {
        this(4, true, true, false);
    }

    public MockAnimation(int noOfSteps, boolean supportsClear,
            boolean supportsPreviousStep, boolean isReady) {
        noOfInstances++;
        this.name = noOfInstances + ". Mock";
        this.noOfSteps = noOfSteps;
        this.supportsClear = supportsClear;
        this.supportsPreviousStep = supportsPreviousStep;
        this.isReady = isReady;
    }

    public MockAnimation(String name, int noOfSteps) {
        this.name = name;
        this.noOfSteps = noOfSteps;
    }

    @Override
    public void clear() {
        checkIsReady();
        if (!supportsClear)
            throw new UnsupportedOperationException(name);
        isCleared = true;
    }

    @Override
    public boolean hasNextStep() {
        checkIsReady();
        return nextStep < noOfSteps;
    }

    @Override
    public boolean hasPreviousStep() {
        checkIsReady();
        if (!supportsPreviousStep)
            throw new UnsupportedOperationException(name);
        return nextStep > 0;
    }

    @Override
    public boolean isCleared() {
        checkIsReady();
        if (!supportsClear)
            throw new UnsupportedOperationException(name);
        return isCleared;
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public void nextStep() {
        checkIsReady();
        if (nextStep >= noOfSteps)
            throw new IllegalStateException(name);
        nextStep++;
        isCleared = false;
    }

    @Override
    public void previousStep() {
        checkIsReady();
        if (!supportsPreviousStep)
            throw new UnsupportedOperationException(name);
        if (nextStep <= 0)
            throw new IllegalStateException(name);
        nextStep--;
        if (nextStep == 0) {
            isCleared = true;
        }
    }

    @Override
    public boolean supportsClear() {
        checkIsReady();
        return supportsClear;
    }

    @Override
    public boolean supportsPreviousStep() {
        checkIsReady();
        return supportsPreviousStep;
    }

    private void checkIsReady() {
        if (!isReady)
            throw new IllegalStateException(name);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * @param b
     */
    public void setSupportsClear(boolean b) {
        this.supportsClear = b;
    }

    /**
     * @param b
     */
    public void setSupportsPreviousStep(boolean b) {
        this.supportsPreviousStep = b;
    }

    public boolean isFinished() {
        return isFinished;
    }

    /**
     * @param b
     */
    public void setFinished(boolean b) {
        isFinished = b;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
