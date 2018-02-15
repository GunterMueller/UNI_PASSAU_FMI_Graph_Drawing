package quoggles.changeboxes.algorithm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.EdgeParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.NodeParameter;
import org.graffiti.plugin.parameter.ObjectParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.selection.Selection;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.InvalidParameterException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: 
 * Output: 
 */
public class Algorithm_Box extends Box {
    
    private Parameter[] params;

    private int[] inputTypes = new int[]{ ITypeConstants.GENERAL };
    
    private int[] outputTypes = new int[]{ ITypeConstants.GENERAL };

    private Map algNameClassMap = new HashMap();
    
    /**
     *  Indicator if a dummy in- and output is needed
     */
    private int needDummy = 1;
    
    
    /**
     * Constructs the box.
     */
    public Algorithm_Box() {
        super();

//        ObjectParameter algParam = new ObjectParameter(
//            "Algorithm", "The algorithm associated with this box.");
        Collection algs = null;
        try {
            algs = GraffitiSingleton.getInstance().getMainFrame()
                .getAlgorithmManager().getAlgorithms();
        } catch (NullPointerException npe) {
            System.err.println(
                "Could not load list of algorithms; no mainframe");
            parameters = new Parameter[]{ new OptionParameter
                (new Object[]{ "no algorithms" }, 0, false, "", "") };
            return;
        }
        for (Iterator it = algs.iterator(); it.hasNext();) {
            Algorithm alg = (Algorithm)it.next();
            algNameClassMap.put(alg.getName(), alg);
        }
        
        OptionParameter algParam = new OptionParameter(
            algNameClassMap.keySet().toArray(), 
            0, false, "Algorithm", "The algorithm associated with this box.");
        parameters = new Parameter[]{ algParam };
    }


    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();

        Algorithm algorithm = null;
        try {
            algorithm = 
                (Algorithm)algNameClassMap.get(
                    ((OptionParameter)parameters[0]).getValue().toString());
        } catch (ClassCastException cce) {
            throw new InvalidParameterException(getId() + ": wrong type" +
                " of parameter: " + 
                parameters[0].getValue().getClass().getName());
        }

        if (algorithm == null) {
            throw new InvalidParameterException(getId() + ": wrong" +
                " parameter. Type: " + 
                parameters[0].getValue().getClass().getName());
        }
        
        algorithm.reset();
        
        try {
            algorithm.attach(GraffitiSingleton.getInstance().getMainFrame()
                .getActiveEditorSession().getGraph());
        } catch (NullPointerException npe) {
//            throw new QueryExecutionException(getId() + 
//                " could not get current graph.");
            // TODO check what to do
            System.err.println(getId() + 
                " could not get current graph.");
        }
        
        Parameter[] pars = null;
        int i = 0;
        try {
            algorithm.setParameters(params);
            // the following may or may not be redundant
            pars = algorithm.getParameters();
            for (; i < pars.length; i++) {
                pars[i].setValue(params[i].getValue());
            }
        } catch (IndexOutOfBoundsException ioobe) {
            throw new InvalidInputException(getId() +
                ": could not set all parameters of the algorithm; need " +
                pars.length + " but have only " + params.length);
        } catch (ClassCastException cce) {
            if (pars != null) {
                throw new InvalidInputException(getId() +
                    ": a parameter has incompatable type: need " +
                    pars[i].getClass().getName() + " but have " +
                    params[i].getClass().getName());
            } else {
                throw new InvalidInputException(getId() +
                    ": a parameter has incompatable type: " + cce);
            }
        } catch (Exception e) {
            throw new QueryExecutionException(getId() + e);
        }

        try {
            algorithm.check();
        } catch (PreconditionException pe) {
            throw new QueryExecutionException(getId() + " could not execute" +
                " algorithm: " + pe);
        }
        algorithm.execute();
        
        if (algorithm instanceof CalculatingAlgorithm) {
            AlgorithmResult aRes = 
                ((CalculatingAlgorithm)algorithm).getResult();
////            outputs = new Object[inputs.length + 1];
////            for (int i = 0; i < inputs.length - 1; i++) {
////                outputs[i] = inputs[i];
////            }
////            outputs[outputs.length - 1] = aRes.getResult().values();
            outputs = new Object[1];
            outputs[0] = aRes.getResult().values();
        }        
    }

    protected Algorithm getAlgorithm() {
        return (Algorithm)algNameClassMap.get(
            ((OptionParameter)parameters[0]).getValue().toString());
    }

//    /**
//     * @see quoggles.boxes.IBox#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
//     */
//    public void setParameters(Parameter[] pars, boolean fromRep) {
//        super.setParameters(pars, fromRep);
//
//        try {
//            algorithm = 
//                (Algorithm)((ObjectParameter)parameters[0]).getValue();
//        } catch (ClassCastException cce) {
//            algorithm = null;
//        }
//    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof Algorithm_Rep)) {

            iBoxGRep = new Algorithm_Rep(this);
        }
        return iBoxGRep;
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return inputTypes;
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return outputTypes;
    }
    
    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
        params = new Parameter[inputs.length];
        try {
            for (int i = needDummy; i < inputs.length; i++) {
                if ((Parameter)inputs[i] instanceof SelectionParameter) {
                    // clone
                    try {
                        SelectionParameter inPar = 
                            (SelectionParameter)inputs[i];
                        SelectionParameter selP = new SelectionParameter(
                            (Selection)inPar.getSelection().clone(), 
                            inPar.getName(), inPar.getDescription());
                        params[i - needDummy] = selP;
                    } catch (CloneNotSupportedException cnse) {
                        params[i - needDummy] = (Parameter)inputs[i];
                    }
                } else {
                    params[i - needDummy] = (Parameter)inputs[i];
                }
            }
        } catch (ClassCastException cce) {
            throw new InvalidInputException(getId() +
                " needs object of type Parameter as input.");
        }
    }

    /**
     * @see quoggles.boxes.IBox#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] pars, boolean fromRep) {
try {
        Algorithm algorithm = null;
        try {
            algorithm = 
                (Algorithm)algNameClassMap.get(
                    ((OptionParameter)pars[0]).getValue().toString());
        } catch (ClassCastException cce) {
            // TODO check what to do
            System.err.println(getId() + ": Wrong type of parameter.");
        }
        if (algorithm == null) {
            return;
        }
        Parameter[] algPars = algorithm.getParameters();
        if (algPars == null) {
            algPars = new Parameter[0];
        }
        needDummy = 1;
        for (int i = 0; i < algPars.length; i++) {
            if (algPars[i] instanceof SelectionParameter) {
                needDummy = 0;
                break;
            }
        }
        
        inputTypes = new int[algPars.length + needDummy];
        for (int i = needDummy; i < inputTypes.length; i++) {
            inputTypes[i] = convertTypeToInt(algPars[i - needDummy]);
        }
        if (needDummy > 0) {
            inputTypes[0] = ITypeConstants.GENERAL;
        }
        
        // outputTypes: just use parameters
        if (algorithm instanceof CalculatingAlgorithm) {
            // TODO solve problem: can't get result before executing
            // just add result list
////            if (needDummy > 0) {
////                outputTypes[0] = ITypeConstants.GENERAL;
////            }
////            outputTypes = new int[algPars.length + needDummy + 1];
////            for (int i = needDummy; i < outputTypes.length - 1; i++) {
////                outputTypes[i] = convertTypeToInt(algPars[i]);
////            }
////            outputTypes[outputTypes.length - 1] = ITypeConstants.COLLECTION;
            outputTypes = new int[1];
            outputTypes[0] = ITypeConstants.COLLECTION;
        } else {
            outputTypes = new int[1];
            outputTypes[0] = ITypeConstants.GENERAL;
        }

        super.setParameters(pars, fromRep);

        // update everything that depends on the number of inputs
        reset();
} catch (Exception e) { System.err.println("error: " + e); } 
    }

    private int convertTypeToInt(Parameter par) {
        if (par instanceof IntegerParameter) {
            return ITypeConstants.INTEGER_PAR;
        } else if (par instanceof DoubleParameter) {
            return ITypeConstants.DOUBLE_PAR;
        } else if (par instanceof EdgeParameter) {
            return ITypeConstants.EDGE_PAR;
        } else if (par instanceof NodeParameter) {
            return ITypeConstants.NODE_PAR;
        } else if (par instanceof BooleanParameter) {
            return ITypeConstants.BOOLEAN_PAR;
        } else if (par instanceof ObjectParameter) {
            return ITypeConstants.OBJECT_PAR;
        } else if (par instanceof SelectionParameter) {
            return ITypeConstants.SELECTION_PAR;
        } else if (par instanceof StringParameter) {
            return ITypeConstants.STRING_PAR;
        } else {
            return -1;
        }
    }

}
