package quoggles.auxiliary;

import quoggles.boxes.IBox;

/**
 * Triple of a <code>Box</code>, a <code>boolean</code> and an <code>int</code>.
 *
 */
public class BoxBooleanIndex {
    
    private IBox box;
    
    private boolean isInput;
    
    private int index;
    
    
    /**
     * Constructor.
     * 
     * @param iBox
     * @param isInput
     * @param index
     */
    public BoxBooleanIndex(IBox iBox, boolean isInput, int index) {
        box = iBox;
        this.isInput = isInput;
        this.index = index;
    }
    
    
    /**
     * @return the index saved in this class
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the box saved in this class 
     */
    public IBox getIBox() {
        return box;
    }

    /**
     * @param box
     */
    public void setIBox(IBox iBox) {
        box = iBox;
    }

    /**
     * @return true if this is an input
     */
    public boolean isInput() {
        return isInput;
    }

    /**
     * @param isInput
     */
    public void setInput(boolean isInput) {
        this.isInput = isInput;
    }

}

