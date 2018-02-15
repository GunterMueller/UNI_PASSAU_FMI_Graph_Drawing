package quoggles.auxboxes.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: one object or a collection of objects<p>
 * Output: the input object or, if the input is a collection, sorted
 * according to the natural ordering of the elements.<p>
 * If an element does not implement <code>Comparable</code>, the
 * <code>toString()</code> method is used.<p>
 * <code>null</code> if the input has been <code>null</code>.<p>
 */
public class Sort_Box extends Box {
    
    private Collection inputCol;
    
    /** Only set if input is not a collection */
    private Object singleInput = null;
    
    
    public Sort_Box() {
        OptionParameter adParam = new OptionParameter(IBoxConstants.ASC_DESC,
            0, false, "asc/desc", "Sort in ascending or descending order?");
            
        parameters = new Parameter[]{ adParam };
    }

    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        if (singleInput != null) {
            outputs = new Object[]{ singleInput };
            return;
        }        
        
        if (inputCol == null) {
            outputs = new Object[]{ null };
            return;
        }
        
        if (inputCol.isEmpty()) {
            outputs = new Collection[]{ new ArrayList(0) };
            return;
        }
        
        outputs = new List[]{ sort(inputCol) };
    }
    
    /**
     * Sorts the given collection.
     * 
     * @param in
     * @return
     */
    private List sort(Collection in) {
////        // put in wrapper so that all objects are comparable
////        ArrayList al = new ArrayList(in.size());
////        for (Iterator iter = in.iterator(); iter.hasNext();) {
////            Object elem = new ComparableWrapper(iter.next());
////            al.add(elem);
////        }

        // new implementation: compare string representation if one of the 
        // elements does not implement Comparable or any two elements do not
        // have the same type (class)
        
        boolean asString = false;
        Class c = null;
        for (Iterator iter = in.iterator(); iter.hasNext();) {
            Object elem = iter.next();
            if (!(elem instanceof Comparable)) {
                asString = true;
                break;
            }
            if (c == null) {
                c = elem.getClass();
            } else if (c != elem.getClass()) {
                asString = true;
                break;
            }
        }
        
        ArrayList al = new ArrayList(in.size());
        if (asString) {
            for (Iterator it = in.iterator(); it.hasNext();) {
                al.add(it.next().toString());
            }
        } else {
            al.addAll(in);
        }
        
        Collections.sort(al);
        
        ArrayList al2 = al;

////        // unwrap
////        ArrayList al2 = new ArrayList(in.size());
////        for (Iterator iter = al.iterator(); iter.hasNext();) {
////            al2.add(((ComparableWrapper)iter.next()).getValue());
////        }

        if (((OptionParameter)parameters[0]).getOptionNr() == 1) {
            Collections.reverse(al2);
        }
        
        return al2;
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
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
            singleInput = inputs[0];
        }
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || !(iBoxGRep instanceof Sort_Rep)) {
            iBoxGRep = new Sort_Rep(this);
        }
        return iBoxGRep;
    }
    
    
    /**
     * This class encapsulates an arbitrary object. It implements interface
     * <code>Comparable</code>. If the wrapped object does not itself implement
     * <code>Comparable</code>, the <code>compareTo</code> method uses the 
     * string representation of the object.
     */
    class ComparableWrapper implements Comparable {
        
        private Object val;
        
        
        /**
         * Constructor taking the wrapped object as parameter.
         * 
         * @param value the object that is wrapped in this class.
         */
        public ComparableWrapper(Object value) {
            val = value;
        }
        
        /**
         * Returns the value this class wraps.
         * 
         * @return the value this class wraps.
         */
        public Object getValue() {
            return val;
        }

        /**
         * Uses the string representation of the wrapped object if this does
         * not implement interface <code>Comparable</code>. 
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o) {
            if (val instanceof Comparable) {
                try {
                    return ((Comparable)o).compareTo(val);
                } catch (ClassCastException cce) {
                    return o.toString().compareTo(val.toString());
                }
            } else {
                try {
                    return ((Comparable)o).compareTo(val.toString());
                } catch (ClassCastException cce) {
                    return o.toString().compareTo(val.toString());
                }
            }
        }
    }
}
