package quoggles.boxes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.auxiliary.RunQuery;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.BoxNotExecutedException;
import quoggles.exceptions.InputNotSetException;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.IBoxRepresentation;


/**
 * Superclass providing all methods of <code>IBox</code> interface. If a box
 * subclasses this class, overriding methods should call the super versions
 * as first action. This specially applies to methods 
 * <code>execute(), reset(), reset(int), setinputs(Object[])</code>.<p>
 * Some basic type checking is done. It acts as an identity operator.
 */
public class Box implements IBox {
    
    protected Parameter[] parameters;
    
    private boolean inputSet = false;
    
    protected boolean boxExecuted = false;
    
    protected Object[] outputs;
    
    protected Object[] inputs;

    protected IBoxRepresentation iBoxGRep;
    
    private Node node;
    
    private boolean[] inputNumbersSet;
    
    private int boxNr;
    
    private boolean ignoreBox = false;
    
    
    public Box() { }
    
    public Box(Node n) {
        node = n;
    }
    
    
    /**
     * Set the number of the box. Used for identification purposes.<p>
     * 
     * @param boxNumber
     */
    public final void setBoxNumber(int boxNumber) {
        boxNr = boxNumber;
//        if (!isGraphicalRepNull()) {
//            getGraphicalRepresentation().updateGraphicalRep();
//        }
    }
    
    /**
     * Sets the parameters and - if needed - sets them in the 
     * graphical representation.
     * Subclasses need not override this method. Just assign your parameters to
     * the protected field <code>parameters</code>.
     * 
     * @see quoggles.IBox#setParameters(org.graffiti.plugin.algorithm.Parameter)
     */
    public void setParameters(Parameter[] pars, boolean fromRep) {
        parameters = pars;
        if (!fromRep && iBoxGRep != null)  {
            iBoxGRep.setParameters(pars, true);
        }
    }

    /**
     * Returns empty parameter array meaning the box does not need any
     * parameters if the field <code>parameters</code> is still 
     * <code>null</code>, otherwise returns this field.
     * 
     * @see quoggles.boxes.IBox#getParameters()
     */
    public final Parameter[] getParameters() {
        if (parameters == null) {
            return new Parameter[]{ };
        } else  {
            return parameters;
        }
    }

    /**
     * Should be overridden and called first by subclasses. 
     * Throws <code>InputNotSetException</code> if the <code>inputSet</code> 
     * flag indicates the no input has been set for this box.
     * May throw other <code>QueryExecutionException</code>s.
     * 
     * The implementation of this abstract sets the output to be equal to the
     * input.
     * 
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        if (!inputSet) {
            throw new InputNotSetException(getId() + ": Input not set.");
        }
        
        boxExecuted = true;
        outputs = inputs;
    }

    /**
     * Used by <code>getId()</code> to retrieve the class name of the box.
     * 
     * @return a string that is used as class name of the box
     */
    protected String getClassName() {
        return getClass().getName();
    }
    
    /**
     * Generates a unique id.
     * 
     * @see quoggles.boxes.IBox#getID()
     */
    public final String getId() {
        String className = getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);
        int _box = className.indexOf("_Box");
        if (_box > 0) {
            className = className.substring(0, _box);
        }
        if (boxNr < 10) {
            return className + "_0" + boxNr;
        } else {
            return className + "_" + boxNr;
        }
    }

    /**
     * @see quoggles.boxes.IBox#hasBeenExecuted()
     */
    public final boolean hasBeenExecuted() {
        return boxExecuted;
    }

    /**
     * If the subclass has any parameters, it may override this method. It sets
     * the values of the parameters that are displayed first time the user sees
     * this box.
     * The best way however is to specify correct default values when creating
     * the parameters in the first place. Thus, this implementation will set
     * the correct values: It uses the information it gets from  a call to
     * <code>getParameters</code>.
     * 
     * @see quoggles.boxes.IBox#setDefaultParameters()
     */
    public void setDefaultParameters() {
        setParameters(getParameters(), false);
    }

    /**
     * This implementation returns a new instance of 
     * <code>DefaultBoxRepresentation</code>.
     * If your subclass has any parameters, you will probably have to design
     * your own representation and override this method for example as follows:
     * <code>
     *  if (iBoxGRep == null || !(iBoxGRep instanceof MYOWNBOX_Rep)) {
     *      iBoxGRep = new MYOWNBOX_Rep(this);
     *  }
     *  return iBoxGRep;
     * </code>
     * 
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null)  {
            iBoxGRep = new DefaultBoxRepresentation(this);
        }
        return iBoxGRep;
    }

    /**
     * @see quoggles.boxes.IBox#isGraphicalRepNull()
     */
    public boolean isGraphicalRepNull() {
        return iBoxGRep == null;
    }

    /**
     * This implementation returns ONE element, the most general type.
     * Override this method to specify more restricted types for the input(s)
     * of the box.
     * The size of the returned array must be consistent with the return value
     * of <code>getNumberOfInputs</code>.
     * 
     * @see quoggles.boxes.IBox#getOtherInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ ITypeConstants.GENERAL };
    }

    /**
     * Returns the number of inputs this box expects.
     * This implementation returns the length of the input types array.
     * 
     * @see quoggles.boxes.IBox#hasSeveralInputs()
     */
    public int getNumberOfInputs() {
        return getInputTypes().length;
    }

    /**
     * This method calls <code>setInputs</code> with the inputs set so far.
     * 
     * @see quoggles.boxes.IBox#setInputAt(java.lang.Object, int)
     */
    public final void setInputAt(Object input, int index)
        throws InvalidInputException {

        if (index < 0 || index > getNumberOfInputs()-1) {
            throw new IllegalArgumentException("Index " + index +
                "out of range.");
        }
        if (inputs == null) {
            inputs = new Object[getNumberOfInputs()];
        }
        inputs[index] = input;
        
        // assume we have not yet set all of the inputs
        inputSet = false;
        
        if (inputNumbersSet == null) {
            inputNumbersSet = new boolean[getNumberOfInputs()];
            for (int i = 0; i < inputNumbersSet.length; i++) {
                inputNumbersSet[i] = false;
            }
        }
        inputNumbersSet[index] = true;
        
        // check if all inputs have been set
        if (!inputSet) {
            inputSet = true;
            for (int i = 0; i < inputNumbersSet.length; i++) {
                if (!inputNumbersSet[i]) {
                    inputSet = false;
                    break;
                }
            }
        }

        if (inputSet) {
            setInputs(inputs);
        }

        boxExecuted = false;
    }

    /**
     * Returns true iff all inputs are null.
     * 
     * @return true iff all inputs are null.
     */
    protected final boolean areAllInputsNull() {
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i] != null) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns <code>true</code> iff all inputs have been set.
     * @return
     */
    public final boolean isInputSet() {
        return inputSet;
    }
    
    /**
     * @see quoggles.boxes.IBox#isInputSetAt(int)
     */
    public final boolean isInputSetAt(int index) {
        if (inputNumbersSet == null) {
            return false;
        }
        if (inputSet) {
            return true;
        }
        return inputNumbersSet[index];
    }

    /**
     * Checks if the number of inputs is correct and sets the inputs.
     * Subclasses probably will want to override this method to avoid many 
     * casts necessary when working with the input array.
     * They should however first call this method (via 
     * <code>super.setInputs(inputs)</code>) so that consistency checks are
     * done and some flags are correctly set.
     * 
     * @see quoggles.boxes.IBox#setOtherInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        if (inputs.length != getNumberOfInputs()) {
            throw new InvalidInputException("Invalid number of inputs to " +
                getId() + ", have " + inputs.length + ", need " +
                getNumberOfInputs());
        }
        inputSet = true;
        boxExecuted = false;
        this.inputs = inputs;
    }

    /**
     * Returns the number of outputs this box provides.
     * This implementation returns the length of the output types array.
     * 
     * @see quoggles.boxes.IBox#hasSeveralOutputs()
     */
    public int getNumberOfOutputs() {
        return getOutputTypes().length;
    }

    /**
     * Override this method to specify more restricted types for the output(s)
     * of the box.
     * The size of the returned array must be consistent with the return value
     * of <code>getNumberOfOutputs</code>.
     *
     * This implementation returns the same as a call to 
     * <code>getInputTypes</code> yields.
     * 
     * @see quoggles.boxes.IBox#getOtherOutputTypes()
     */
    public int[] getOutputTypes() {
        return getInputTypes();
    }

    /**
     * Returns the <code>i</code>th output.
     * @see quoggles.boxes.IBox#getOutputAt(int)
     */
    public Object getOutputAt(int index) throws BoxNotExecutedException {
        if (index < 0 || index > getNumberOfOutputs()-1) {
            throw new IllegalArgumentException("Index " + index +
                " out of range.");
        }
        return getOutputs()[index];
    }

    /**
     * Returns <code>outputs</code>. Throws exception if the box's
     * <code>execute</code> method has not been called.
     * No need to override this method. Just work on the <code>outputs</code>
     * field.
     * 
     * @see quoggles.boxes.IBox#getOtherOutputs()
     */
    public Object[] getOutputs() throws BoxNotExecutedException {
        if (!boxExecuted) {
            throw new BoxNotExecutedException(getId() + 
                ": Box not executed.");
        }
        return outputs;
    }

//    /**
//     * @see quoggles.boxes.IBox#setContainsCopies(int)
//     */
//    public void setContainsCopies(int inputNr) {
//        copyInputs[inputNr] = true;
//    }


//    /**
//     * @see quoggles.boxes.IBox#getContainsCopies()
//     */
//    public boolean[] getContainsCopies() {
//        return copyInputs;
//    }


    /**
     * Should be called by all classes that override this method.
     * 
     * @see quoggles.boxes.IBox#reset()
     */
    public void reset() {
        inputs = null;
        outputs = null;
        inputSet = false;
        boxExecuted = false;
        if (inputNumbersSet != null) {
            inputNumbersSet = new boolean[getNumberOfInputs()];
            for (int i = 0; i < inputNumbersSet.length; i++) {
                inputNumbersSet[i] = false;
            }
        }
        setIgnoreBox(false);
    }
    
    /**
     * Remove all <code>null</code> values from the given collection.<p>
     * If the given collection supports the <code>remove</code> operation, it
     * is changed directly. Otherwise, all non-<code>null</code> elements are
     * inserted into a new list. In both cases, the new list is returned.
     * 
     * @param col the collection to remove <code>null</code> values from
     * 
     * @return a collection holding the same elements as the given collection
     * without any <code>null</code> values. Needs not be the same object as
     * the given collection.
     */
    public final Collection removeNullValues(Collection col) {
        try {
            while (col.remove(null)) {}
            return col;
        } catch (UnsupportedOperationException usoe) {
            ArrayList al = new ArrayList(col.size());
            for (Iterator it = col.iterator(); it.hasNext();) {
                Object o = it.next();
                if (o != null) {
                    al.add(o);
                }
            }
            return al;
        }
    }    

    /**
     * Should be called by all classes that override this method.
     * 
     * @see quoggles.boxes.IBox#reset(int)
     */
    public void reset(int index) {
        try {
            inputs[index] = null;
        } catch (Exception e) {
            // ignore
        }
        if (getNumberOfInputs() == 1 && index == 0) {
            reset();
        } else {
            outputs = null;
            inputSet = false;
            boxExecuted = false;
            if (inputNumbersSet != null) {
                if (inputNumbersSet.length != getNumberOfInputs()) {
                    inputNumbersSet = new boolean[getNumberOfInputs()];
                    for (int i = 0; i < inputNumbersSet.length; i++) {
                        inputNumbersSet[i] = false;
                    }
                } else {
                    inputNumbersSet[index] = false;
                }
            }
            setIgnoreBox(false);
        }
    }

    /**
     * Returns <code>false</code>.
     * 
     * @see quoggles.boxes.IBox#neeedsQueryRunner()
     */
    public boolean needsQueryRunner() {
        return false;
    }

    /**
     * Empty implementation.
     * 
     * @see quoggles.boxes.IBox#setQueryRunner(quoggles.auxiliary.RunQuery)
     */
    public void setQueryRunner(RunQuery qr) { }

    /**
     * Empty implementation.
     * 
     * @see quoggles.boxes.IBox#setCurrentResult(java.util.List)
     */
    public void setCurrentResult(List res) { }

    /**
     * Empty implementation.
     * 
     * @see quoggles.boxes.IBox#setCurrentNodesTodo(java.util.ArrayList)
     */
    public void setCurrentNodesTodo(Stack nodesTodo) { }

    /**
     * @see quoggles.boxes.IBox#setIgnoreBox(boolean)
     */
    public void setIgnoreBox(boolean ignore) {
        ignoreBox = ignore;
    }

    /**
     * @see quoggles.boxes.IBox#ignoreBox()
     */
    public boolean ignoreBox() {
        return ignoreBox;
    }

    /**
     * @see quoggles.boxes.IBox#setNode(org.graffiti.graph.Node)
     */
    public void setNode(Node n) {
        node = n;
    }

    /**
     * @see quoggles.boxes.IBox#getNode()
     */
    public Node getNode() {
        return node;
    }

}