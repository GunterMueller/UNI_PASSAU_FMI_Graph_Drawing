/*
 * Created on 10.01.2006
 *
 * Comment on 03.07.2006 by Andreas Keilhauer: 
 * We should try and find out, whether this is actually useful in some way.
 * At the moment it is not used at all and it is uncertain, whether it would
 * work properly if turned into a sublass of AbstractAlgorithm. If you need
 * to generate a random binary tree, there is always:
 * org.graffiti.plugins.algorithms.generators.RandomTreeGraphGenerator
 */

package org.graffiti.plugins.algorithms.generators;

/**
 * 
 * @author Beiqi
 * 
 *         Create a random binary (k-nary) tree generator according to the
 *         Martin and Orr Algorithmus
 */
public class RandomBinaryTreeGenerator {

    public RandomBinaryTreeGenerator() {
    }

    /**
     * Generate Binary Tree
     * 
     * @param inversionTable
     * @param nodeNumber
     */
    public static void generateTree(int[] inversionTable, int nodeNumber) {
        generateTree(inversionTable, nodeNumber, 2);
    }

    /**
     * Generate z-nary tree
     * 
     * @param inversionTable
     * @param nodeNumber
     * @param arity
     */
    public static void generateTree(int[] inversionTable, int nodeNumber,
            int arity) {
        // set the value of the first element zero
        inversionTable[0] = 0;

        for (int j = 0; j < nodeNumber - 1; j++) {
            inversionTable[j + 1] = assign(inversionTable, nodeNumber, j, arity);
        }
    }

    protected static int assign(int[] inversionTable, int nodeNumber, int j,
            int arity) {
        double probability, quotient, sum, x;
        // binary tree: plus 1;
        // z-nary tree: plus (z-1);
        int k = inversionTable[j] + (arity - 1);

        probability = computeProbability(nodeNumber, j, k);
        sum = probability;
        x = Math.random();
        while ((1 - x) > sum) {
            // System.out.println("1-x: "+(1-x)+"\tsum: "+sum);
            quotient = computeQuotient(nodeNumber, j, k);
            probability = quotient * probability;
            sum = sum + probability;
            k = k - 1;
        }
        return k;
    }

    protected static double computeProbability(int nodeNumber, int j, int k) {
        return (double) ((k + 2) * (nodeNumber - j))
                / ((k + 1) * (2 * nodeNumber - 2 * j + k));
    }

    protected static double computeQuotient(int nodeNumber, int j, int k) {
        return (double) ((k + 1) * (nodeNumber - j + k + 1))
                / ((k + 2) * (2 * nodeNumber - 2 * j + k - 1));
    }

    protected static void printArray(int[] a) {
        String s = "[";
        for (int i = 0; i < a.length; i++) {
            s += a[i] + ", ";
        }
        s += "]";
        System.out.println(s);
    }
}
