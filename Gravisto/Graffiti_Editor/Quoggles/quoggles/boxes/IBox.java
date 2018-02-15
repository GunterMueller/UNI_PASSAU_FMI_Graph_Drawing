package quoggles.boxes;

import java.util.List;
import java.util.Stack;

import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.auxiliary.RunQuery;
import quoggles.exceptions.BoxNotExecutedException;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.IBoxRepresentation;

/**
 * Interface for boxes. Provides methods to set the input(s), execute the code
 * of the box and get its output(s).
 */
public interface IBox {

    /**
     * Set the number of the box. Used for identification purposes.
     * 
     * @param boxNumber a number identifying the box
     */
    public void setBoxNumber(int boxNumber);

    /**
     * Implement this method to specify the types for the input(s)
     * of the box.
     * The size of the returned array must be consistent with the return value
     * of <code>getNumberOfInputs</code>.
     * 
     * @return an <code>int</code> array whose size gives the number of inputs.
     * The entries specify the type of the inputs. This uses constants defined
     * in <code>quoggles.constants.ITypeConstants</code>.
     * 
     * @see quoggles.constants.ITypeConstants
     */
    public int[] getInputTypes();
    
    /**
     * Returns the number of inputs this box expects.
     * 
     * @return the number of inputs this box expects
     */
    public int getNumberOfInputs();
    
    /**
     * Checks if the number of inputs is correct and sets the inputs.
     * Also must ensure that a call to <code>execute</code> will no longer
     * throw an <code>InputNotSetException</code>.
     * 
     * @param inputs the inputs of the box
     */
    public void setInputs(Object[] inputs) throws InvalidInputException;
    
    /**
     * Sets the input at the specified index.
     * 
     * @param input the input to be set at the given index
     * @param index the index of the input to set
     */
    public void setInputAt(Object input, int index) 
        throws InvalidInputException;
    
    /**
     * Returns <code>true</code> iff all inputs have been set.
     * 
     * @return  <code>true</code> iff all inputs have been set.
     */
    public boolean isInputSet();
    
    /**
     * Returns true iff the input at the given index has been set.
     * 
     * @param index between zero and <code>getNumberOfInputs()</code>-1
     * 
     * @return true if the input has been set at the given index
     */
    public boolean isInputSetAt(int index);

    /**
     * Executes the box, i.e. calculates the output from the input.
     * 
     * Should be overridden and called first by subclasses. 
     * @throws <code>InputNotSetException</code> if the <code>inputSet</code> 
     * flag indicates the no input has been set for this box.
     * @throws maybe other <code>QueryExecutionException</code>s.
     */
    public void execute() throws QueryExecutionException;
    
    /**
     * Returns <code>true</code> iff the box's <code>execute()</code> method 
     * has been called since the creation of the box or the last call to 
     * <code>reset</code>.
     * 
     * @return true if the box has been executed and its outputs are ready
     */
    public boolean hasBeenExecuted();
    
    /**
     * Returns the number of outputs this box provides.
     * 
     * @return the number of outputs this box provides
     */
    public int getNumberOfOutputs();

    /**
     * Implement this method to specify restricted types for the output(s)
     * of the box.
     * The size of the returned array must be consistent with the return value
     * of <code>getNumberOfOutputs</code>.
     * 
     * @return an <code>int</code> array whose size gives the number of 
     * outputs.
     * The entries specify the type of the outputs. This uses constants defined
     * in <code>quoggles.constants.ITypeConstants</code>.
     * 
     * @see quoggles.constants.ITypeConstants
     */
    public int[] getOutputTypes();

    /**
     * Returns the output array of the box. Throws exception if the box's
     * <code>execute</code> method has not been called.
     * 
     * @return the outputs of the box
     */
    public Object[] getOutputs() throws BoxNotExecutedException;

    /**
     * Returns the <code>i</code>th output.
     * 
     * @return the <code>i</code>th output of the box
     */
    public Object getOutputAt(int index) throws BoxNotExecutedException;

    /**
     * Important only if the subclass has any parameters. It sets
     * the values of the parameters that are displayed first time the user sees
     * this box.
     */
    public void setDefaultParameters();
    
    /**
     * Sets the parameters of this box. This method should (the 
     * implementation of <code>Box</code> does it) call 
     * <code>setParameters</code> on the representation of this box. To avoid
     * updating several times, this is not done when the given flag 
     * <code>fromRep</code> is true since then the call has originated from
     * the representation.
     * 
     * @param pars the parameters to be set
     * @param fromRep true if called from the box's representation
     */
    public void setParameters(Parameter[] pars, boolean fromRep);
    
    /**
     * Returns the array of currently set parameters.
     * 
     * @return the parameters of the box
     */
    public Parameter[] getParameters();
    
    /**
     * Returns a unique id.
     * 
     * @return a <code>String</code> identifying the box
     */
    public String getId();

    /**
     * If your subclass has any parameters, you will probably have to design
     * your own representation and implement this method for example as follows:
     * <code>
     *  if (iBoxGRep == null || !(iBoxGRep instanceof MYOWNBOX_Rep)) {
     *      iBoxGRep = new MYOWNBOX_Rep(this);
     *  }
     *  return iBoxGRep;
     * </code>
     * 
     * @return the graphical representation of the box. Should be the same
     * object each time the method is called.
     */
    public IBoxRepresentation getGraphicalRepresentation();
    
//    /**
//     * Returns <code>true</code> iff the graphical representation has not yet 
//     * been built.
//     * 
//     * @return <code>true</code> iff the graphical representation has not yet 
//     * been built.
//     */
//    public boolean isGraphicalRepNull();
//    
//    /**
//     * Specify that the input with the given number contains copies instead of
//     * original graph elements.
//     * 
//     * @param inputNr
//     * @deprecated no clones in the system any more
//     */
//    public void setContainsCopies(int inputNr);

//    /**
//     * Returns boolean array that specifies which inputs contains copies 
//     * instead of original graph elements.
//     * 
//     * @param inputNr
//     * @deprecated no clones in the system any more
//     */
//    public boolean[] getContainsCopies();
    
    /**
     * Reset the box: Inputs will not be set, ...
     */
    public void reset();
    
    /**
     * States that the input at the given index is no longer valid.
     * 
     * @param index the index of the input that should be marked as invalid
     */
    public void reset(int index);
    
    /**
     * Specifies whether or not a box should be ignored by any query processing
     * (possibly because it has already been executed as part of a sub query).
     * 
     * @param ignore <code>true</code> iff the box should not be executed
     */
    public void setIgnoreBox(boolean ignore);
    
    /**
     * Gets the value previously set by <code>setIgnoreBox(boolean)</code> or
     * returns <code>false</code> if not yet set.
     * 
     * @return <code>true</code> if the box should be ignored
     */
    public boolean ignoreBox();
    
    /**
     * Should return <code>true</code> if the box needs the queryRunner field
     * to be set.
     * 
     * @return true if an object implementing the <code>RunQuery</code>
     * interface should be passed to this box
     */
    public boolean needsQueryRunner();
    
    /**
     * Set the object that has a <code>runQuery</code> method 
     * to be called for the execution of the predicate sub query.
     * Only used if <code>needsQueryRunner()</code> returns true.
     *  
     * @param qr an object that has a <code>runQuery(...)</code> method
     * 
     * @see needsQueryRunner()
     */
    public void setQueryRunner(RunQuery qr);
    
    /**
     * Sets the list (representing the result table) that the last call to
     * <code>runQuery</code> returned. Only boxes that run sub queries need
     * this information. They use that as the result to pass on to the sub
     * query executor.
     * 
     * @param res the last result of a call to <code>runQuery(...)</code>
     */
    public void setCurrentResult(List res);

    /**
     * Sets the stack of nodes that still have to be processed via a call to
     * <code>runQuery</code>. Only boxes that run sub queries need
     * this information. They pass it on to the sub query executor.<p>
     * Used to prevent multiple execution of boxes that can be reached from
     * a high level and is executed within a lower level (sub query).
     * 
     * @param nodesTodo the stack of nodes that still need to be processed
     */
    public void setCurrentNodesTodo(Stack nodesTodo);
    
    /**
     * Sets the node that holds this box.
     * 
     * @param node  the node that holds this box.
     */
    public void setNode(Node node);

    /**
     * Returns the node that holds this box.
     * 
     * @return the node that holds this box
     */
    public Node getNode();

}
