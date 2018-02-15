package quoggles.stdboxes.getattributevalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.auxiliary.Util;
import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.constants.QConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: one or several <code>Attributable</code>s<p>
 * Output: one or several values of an attribute of the input(s). The attribute
 * is specified via a parameter. <code>null</code> values are passed on.
 * <code>null</code> if the input itself is <code>null</code><p>
 * 
 * Takes one or a collection of <code>Attributables</code> and returns the
 * value(s) of the attribute specified via the parameter.<p>
 * If an <code>Attributable</code> does not have the specified attribute, a 
 * <code>null</code> value is inserted in the output.<p>
 * If the resulting list contains only one element, this element is returned
 * (not a one-element list).
 */
public class GetAttributeValue_Box extends Box {

    /** A parameter of the box */
    private OptionParameter attrPath = Util.buildAttrPath();

    /** The input collection */
    private Collection inputCol;
    
    /** Only set if input is not a collection */
    private Object singleInput = null;
    

    /**
     * Constructs the box.
     */
    public GetAttributeValue_Box() {
        parameters = new Parameter[]{ attrPath };
    }
    
    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ 
            ITypeConstants.ATTRIBUTABLES + ITypeConstants.ATTRIBUTABLE};
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ 
            ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION };
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);

        singleInput = null;
        try {
            inputCol = (Collection)inputs[0];
        } catch (ClassCastException cce) {
            inputCol = null;
            if (inputs[0] == QConstants.EMPTY) {
                singleInput = QConstants.EMPTY;
            } else try {
                Attributable attbl = (Attributable)inputs[0];
                singleInput = attbl;
//                inputCol = new ArrayList(1);
//                inputCol.add(attbl);
            } catch (ClassCastException cce2) {
                throw new InvalidInputException(
                    getId() +
                    " needs one or a Collection of Attributables as input");
            }
        }
    }

    /**
     * @see quoggles.boxes.IGraphicalBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof GetAttributeValue_Rep)) {

            iBoxGRep = new GetAttributeValue_Rep(this);
        }
        return iBoxGRep;
    }

    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
// prev version: ignores non existing attributes etc.
//        List outputCol = new ArrayList(inputCol.size());
//        boolean atLeastOneFound = false;
//        
//        String searchAttrPath = ((OptionParameter)parameters[0])
//            .getValue().toString();
//        for (Iterator it = inputCol.iterator(); it.hasNext();) {
//            Attributable attbl = (Attributable)it.next();
//            try {
//                Attribute attr =
//                    attbl.getAttribute(searchAttrPath);
//                outputCol.add(attr.getValue());
//                atLeastOneFound = true;
//            } catch (AttributeNotFoundException anfe) {
//                // leave this Attributable
//            }
//        }
//        
//        if (outputCol.size() == 0) {
//            if (atLeastOneFound) {
//                outputs = new List[]{ outputCol };
//            } else {
//                outputs = new Object[]{ null };
//            }
//        } else if (outputCol.size() == 1) {
//            outputs = new Object[]{ outputCol.get(0) };
//        } else {
//            outputs = new List[]{ outputCol };
//        }

        String searchAttrPath = ((OptionParameter)parameters[0])
            .getValue().toString();

        if (singleInput != null) {
            if (singleInput == QConstants.EMPTY) {
                outputs = new Object[]{ quoggles.constants.QConstants.EMPTY };
            } else try {
                outputs = new Object[]{((Attributable)singleInput)
                    .getAttribute(searchAttrPath).getValue() };
            } catch (AttributeNotFoundException anfe) {
                outputs = new Object[]{ quoggles.constants.QConstants.EMPTY };
            }
            return;
        }

        if (inputCol == null) {
            outputs = new Object[]{ null };
            return;
        }
        
        // inserts EMPTY value if an attribute cannot be found
        List outputCol = new ArrayList(inputCol.size());
        
        for (Iterator it = inputCol.iterator(); it.hasNext();) {
            Object elem = null;
            Attributable attbl = null;
            try {
                elem = it.next();
                attbl = (Attributable)elem;
            } catch (ClassCastException cce) {
                throw new InvalidInputException(getId() + ": this box" +
                    " needs a collection of Attributables as input, not one" +
                    " containing elements of type " + 
                    elem.getClass().getName());
            }
            if (attbl == null) {
                //outputCol.add(null);
                throw new QueryExecutionException(getId() + " null value in" +
                    " input collection encountered. This is forbidden.");
            } else {
                try {
                    outputCol.add(
                        attbl.getAttribute(searchAttrPath).getValue());
                } catch (AttributeNotFoundException anfe) {
                    outputCol.add(quoggles.constants.QConstants.EMPTY);
                }
            }
        }
        
        outputs = new Collection[]{ outputCol };
    }
}