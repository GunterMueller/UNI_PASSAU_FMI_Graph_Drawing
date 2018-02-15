package org.graffiti.plugins.algorithms.circulardrawing;

import java.util.ArrayList;
import java.util.List;

import org.graffiti.graph.GraphElement;

/**
 * @author demirci Created on Aug 2, 2005
 */
public class QuickSort {

    /**
     * Comment for <code>geArray</code>
     * 
     * @see org.graffiti.graph.GraphElement
     */
    GraphElement[] geArray;

    private List listToSort;

    private int maxIndex;

    /**
     * Konstruktur.
     * 
     * @param listToSort
     *            .
     */
    public QuickSort(List listToSort) {
        this.listToSort = listToSort;
        geArray = new GraphElement[listToSort.size()];
        for (int i = 0; i < listToSort.size(); i++) {
            geArray[i] = (GraphElement) listToSort.get(i);
        }
        maxIndex = geArray.length;
    }

    /**
     * @return a sorted list.
     */
    private List geArrayList() {
        quickSort(0, maxIndex - 1);
        List sortedgeArrayList = new ArrayList();
        for (int i = 0; i < geArray.length; i++) {
            GraphElement ge = geArray[i];
            System.out.print(" ");
            sortedgeArrayList.add(ge);
        }
        return sortedgeArrayList;
    }

    /**
     * @param a
     * @param b
     */
    private void swap(int a, int b) {
        GraphElement tmp = geArray[a];
        geArray[a] = geArray[b];
        geArray[b] = tmp;
    }

    /**
     * @param left
     * @param right
     */
    private void quickSort(int left, int right) {
        // Laufindex, der vom rechten Ende nach links laeuft
        int toLeft = right;
        // Laufindex, der vom linken Ende nach rechts laeuft
        int toRight = left;
        if (toRight < toLeft) {
            // Pivotelement bestimmen
            int pivot = (geArray[(toRight + toLeft) / 2])
                    .getInteger("graphics.sortId");
            while (toRight <= toLeft) {
                // Links erstes Element suchen, das
                // groesser oder gleich dem Pivotelement ist
                while ((toRight < right)
                        && ((geArray[toRight]).getInteger("graphics.sortId") < pivot)) {
                    toRight++;
                }
                // Rechts erstes Element suchen, das
                // kleiner oder gleich dem Pivotelement ist
                while ((toLeft > left)
                        && ((geArray[toLeft]).getInteger("graphics.sortId") > pivot)) {
                    toLeft--;
                }
                // Wenn nicht aneinander vorbei gelaufen, Inhalte vertauschen
                if (toRight <= toLeft) {
                    swap(toRight, toLeft);
                    toRight++;
                    toLeft--;
                }
            } // end while

            // Linken Teil sortieren
            if (toLeft > left) {
                quickSort(left, toLeft);
            }

            // Rechten Teil sortieren
            if (toRight < right) {
                quickSort(toRight, right);
            }
        }
    }

    /**
     * @return a sorted list.
     */
    public List getSortedList() {
        listToSort = geArrayList();
        return listToSort;
    }
}
