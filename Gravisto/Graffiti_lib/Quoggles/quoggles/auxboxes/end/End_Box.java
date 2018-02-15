package quoggles.auxboxes.end;

import quoggles.boxes.Box;

/**
 * Input: anything<p>
 * Output: none
 */
public class End_Box extends Box {

    /**
     * Returns empty array of length 0.
     * 
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[0];
    }

}
