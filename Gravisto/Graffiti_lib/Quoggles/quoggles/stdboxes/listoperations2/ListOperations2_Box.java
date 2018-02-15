package quoggles.stdboxes.listoperations2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.NoHomogeneousTypeException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: two times: an object or a collection<p>
 * Output: an object or a collection resulting from applying an operation
 * (specified via a parameter) on both inputs.
 * If an input is <code>null</code>, it is converted to an empty list.
 */
public class ListOperations2_Box extends Box {

    private Collection[] cols1;
    
    private Collection[] cols2;
    
    private int ioNumber = 2;
    
    
    /**
     * Constructs the box.
     */
    public ListOperations2_Box() {
        parameters = new Parameter[]{
            new IntegerParameter(2,
                "ioNumber", "Number of inputs (equal to number of outputs"),
            new OptionParameter(
                new String[]{ new String(IBoxConstants.UNION), 
                    new String(IBoxConstants.INTERSECT),
                    new String(IBoxConstants.LISTMINUS) }, 
                        0, "returns", "Specifies what this box returns.") };
    }
    
    /**
     * Returns 2.
     * 
     * @see quoggles.boxes.IBox#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return ioNumber;
    }

    /**
     * @see quoggles.boxes.IBox#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] pars, boolean fromRep) {
        // added for compatability reasons; if snd parameter does not exist
        if (pars.length < 2) {
            ioNumber = 2;
            Parameter[] addPars = new Parameter[2];
            addPars[1] = pars[0];
            addPars[0] = new IntegerParameter(2,
                "ioNumber", "Number of inputs (equal to number of outputs");
            pars = addPars;
        } else {
            ioNumber = ((IntegerParameter)pars[0]).getInteger().intValue();
        }
        super.setParameters(pars, fromRep);

        // update everything that depends on the number of inputs
        reset();
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        int[] ios = new int[ioNumber];
        for (int i = 0; i < ioNumber; i++) {
            ios[i] = ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION;
        }
        return ios;
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        int[] ios = new int[ioNumber / 2];
        for (int i = 0; i < ioNumber / 2; i++) {
            ios[i] = ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION;
        }
        return ios;
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);

        if (inputs == null) return;
        
        cols1 = new Collection[ioNumber / 2];
        cols2 = new Collection[ioNumber / 2];

        int size = -1;
        for (int i = 0; i < ioNumber / 2; i++) {
            try {
                cols1[i] = (Collection)inputs[i];
                if (size == -1) {
                    size = cols1[i].size();
                } else if (cols1[i].size() != size) {
                    throw new InvalidInputException(getId() +
                        ": first " + ioNumber / 2 + " input collections must" +
                        " be of same size (" + cols1[i].size() + " != " + 
                        size + ")");
                }
            } catch (ClassCastException cce) {
                if (size == -1) {
                    cols1[i] = new ArrayList(1);
                    cols1[i].add(inputs[i]);
                    size = 1;
                } else if (size == 1) {
                    cols1[i] = new ArrayList(1);
                    cols1[i].add(inputs[i]);
                } else {
                    throw new InvalidInputException(getId() +
                        ": One input is not a collection. Putting it into a" +
                        " one element collection failed since not all other" +
                        " first " + ioNumber / 2 + " input collections have" +
                        " size 1");
                }
            }
        }
        size = -1;
        for (int i = 0; i < ioNumber / 2; i++) {
            try {
                cols2[i] = (Collection)inputs[i + ioNumber / 2];
                if (size == -1) {
                    size = cols2[i].size();
                } else if (cols2[i].size() != size) {
                    throw new InvalidInputException(getId() +
                        ": second " + ioNumber / 2 + " input collections must" +
                        " be of same size (" + cols2[i].size() + " != " + 
                        size + ")");
                }
            } catch (ClassCastException cce) {
                if (size == -1) {
                    cols2[i] = new ArrayList(1);
                    cols2[i].add(inputs[i + ioNumber / 2]);
                    size = 1;
                } else if (size == 1) {
                    cols2[i] = new ArrayList(1);
                    cols2[i].add(inputs[i + ioNumber / 2]);
                } else {
                    throw new InvalidInputException(getId() +
                        ": One input is not a collection. Putting it into a" +
                        " one element collection failed since not all other" +
                        " second " + ioNumber / 2 + " input collections have" +
                        " size 1");
                }
            }
        }

//        try {
//            if (inputs[0] == null) {
//                // convert null to empty list
//                col1 = new ArrayList(0);
//            } else {
//                col1 = (Collection)inputs[0];
//            }
//        } catch (ClassCastException cce) {
//            col1 = new ArrayList(1);
//            col1.add(inputs[0]);
//        }
//        try {
//            if (inputs[1] == null) {
//                // convert null to empty list
//                col2 = new ArrayList(0);
//            } else {
//                col2 = (Collection)inputs[1];
//            }
//        } catch (ClassCastException cce) {
//            col2 = new ArrayList(1);
//            col2.add(inputs[1]);
//        }
        
        checkSameTypes(cols1, cols2);
    }

    /**
     * Checks if the given collections have the same schema. Throws an
     * exception if it is not the case.
     * 
     * @param c1 first collection
     * @param c2 second collection
     * @throws InvalidInputException thrown if the collections cannot be 
     * unified, i.e. the types do not match (checked recursively)
     */
    private void checkSameTypes(Collection[] c1, Collection[] c2)
        throws InvalidInputException {
        
//        Iterator cit1 = c1.iterator();
//        Iterator cit2 = c2.iterator();
//        String errorMsg = "";
//        while (cit1.hasNext()) {
//            if (!cit2.hasNext()) {
//                errorMsg = 
//                    "first collection contains more elements than second";
//                break;
//            }
//            Object el1 = cit1.next();
//            Object el2 = cit2.next();
//            if (el1 instanceof Collection) {
//                if (el2 instanceof Collection) {
//                    checkSameTypes((Collection) el1, (Collection) el2);
//                } else {
//                    errorMsg = 
//                        "one element in first collection is a collection," +
//                        " but the corresponding element in the second" +
//                        " collection is not";
//                    break;
//                }
//            } else {
//                if (el2 instanceof Collection) {
//                    errorMsg = 
//                        "one element in first collection is a collection," +
//                        " but the corresponding element in the second" +
//                        " collection is not";
//                    break;
//                } else {
//                    // TODO better type equality checking needed
//                    if (el1.getClass().equals(el2.getClass())) {
//                        continue;
//                    }
//                    if (el1 instanceof Number && el2 instanceof Number) {
//                        continue;
//                    }
//                    if (el1 instanceof GraphElement && 
//                        el2 instanceof GraphElement) {
//                        continue;
//                    }
//                }
//            }
//        }
//        if (cit2.hasNext()) {
//            errorMsg = "second collection contains more elements than first";
//        }
        
        // element_i in c1 must correspond to element_i in c2
        if (c1.length != c2.length) {
            throw new InvalidInputException(getId() +
                " must get two equal sized sets of collections");
        }
        
        for (int i = 0; i < c1.length; i++) {
            Class type1  = null;
            try {
                type1 = getType(c1[i]);
                //System.out.println("type1: " + type1.getName());
            } catch (NoHomogeneousTypeException nhte) {
                throw new InvalidInputException(getId() +
                    ": Input collections not homogenous: " +
                        nhte.getMessage());
            }
            Class type2 = null;
            try {
                type2 = getType(c2[i]);
                //System.out.println("type2: " + type2.getName());
            } catch (NoHomogeneousTypeException nhte) {
                throw new InvalidInputException(getId() +
                    ": Input collections not homogenous: " +
                        nhte.getMessage());
            }

            if (type1 != null && type2!= null && !type1.equals(type2)) {
                throw new InvalidInputException(getId() +
                    ": Input collections not compatible: Types " +
                    type1.getName() + " and " + type2.getName() +
                    " (columns " + (i+1) + " and " + (i+1+c1.length) + ")");
            }
        }
        
//        Class type1  = null;
//        try {
//            type1 = getType(c1);
//            //System.out.println("type1: " + type1.getName());
//        } catch (NoHomogeneousTypeException nhte) {
//            throw new InvalidInputException(getId() +
//                ": Input collections not homogenous: " +
//                    nhte.getMessage());
//        }
//        Class type2 = null;
//        try {
//            type2 = getType(c2);
//            //System.out.println("type2: " + type2.getName());
//        } catch (NoHomogeneousTypeException nhte) {
//            throw new InvalidInputException(getId() +
//                ": Input collections not homogenous: " +
//                    nhte.getMessage());
//        }
//        
//        if (type1 != null && type2!= null && !type1.equals(type2)) {
//            throw new InvalidInputException(getId() +
//                ": Input collections not compatible: Types " +
//                type1.getName() + " and " + type2.getName());
//        }
    }
    
////    /**
////     * See <code>Class getType(Collection col)</code> which is called for every
////     * element in the given array.
////     * 
////     * @param cs
////     * @return
////     * @throws NoHomogeneousTypeException
////     */
////    private Class getType(Collection[] cs) throws NoHomogeneousTypeException {
////        Class type = null;
////        for (int i = 0; i < cs.length; i++) {
////            Class type2 = getType(cs[i]);
////            
////            if (type == null || type.equals(Object.class)) {
////                type = type2;
////            } else if (!type.equals(type2)) {
////                throw new NoHomogeneousTypeException("At least two" +
////                    " Collections are of incompatible types: " + 
////                    type.getName() + " and " + type2.getName());
////            }
////        }
////        return type;
////    }

    /**
     * Get (most general) type of the elements contained in the given 
     * collection.<p>
//     * Never returns <code>null</code>. An empty collection yields
//     * <code>Object.class</code>.
     * Returns <code>null</code> for an empty collection or a collection that
     * contains only null values.
     * 
     * @param col the collection to check
     * 
     * @return the (most general) type of the elements of the collection<p>
//     * <code>Object.class</code> for an empty collection
     * <code>null</code> if parameter is an empty collection or one containing
     * only <code>null</code> values.
     * 
     * @throws NoHomogeneousTypeException thrown if the no such type could be 
     * found
     */
    private Class getType(Collection col) throws NoHomogeneousTypeException {
        Class type = null;
        if (col.isEmpty()) {
            return null;
////            return Object.class;
        }

        Iterator it = col.iterator();
        while (it.hasNext()) {
            Object el = it.next();
            if (el == null) {
                // null always compatible
////                if (type == null) {
////                    // collection of null values: Object.class 
////                    type = Object.class;
////                }
                continue;
            }
            Class type2 = null;
            if (el instanceof Collection) {
                type2 = Collection.class;
            } else if (el instanceof Attributable) {
                type2 = Attributable.class;
            } else if (el instanceof GraphElement) {
                type2 = GraphElement.class;
            } else if (el instanceof Attribute) {
                type2 = Attribute.class;
            } else if (el instanceof Number) {
                type2 = Number.class;
            } else if (el instanceof String) {
                type2 = String.class;
            } else {
                type2 = el.getClass();
            }
            
            if (type == null || type.equals(Object.class)) {
                type = type2;
            } else if (!type.equals(type2)) {
                throw new NoHomogeneousTypeException("Collection contains" +
                    " elements of incompatible types: " + type.getName() +
                    " and " + type2.getName());
            }
        }

        return type;
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof ListOperations2_Rep)) {

            iBoxGRep = new ListOperations2_Rep(this);
        }
        return iBoxGRep;
    }

    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
            
        super.execute();
        
        if (areAllInputsNull()) {
            outputs = new Object[]{ null };
            return;
        }
        
        int colNr = getNumberOfInputs() / 2;

        // have a duplicate elimination box => dont use a set here
        // Collection col = new HashSet();
        List outputCol1 = new ArrayList(cols1[0].size());
        List outputCol2 = new ArrayList(cols1[0].size());
        List outputCol = new ArrayList(cols1[0].size());

        String paramValue = ((OptionParameter)parameters[1])
            .getValue().toString();
        
//            outputCol.addAll(col1);
//            outputCol.addAll(col2);
        // initialize iterators for each column
        Iterator[] its1 = new Iterator[colNr];
        for (int i = 0; i < colNr; i++) {
            its1[i] = cols1[i].iterator();
        }
        Iterator[] its2 = new Iterator[colNr];
        for (int i = 0; i < colNr; i++) {
            its2[i] = cols2[i].iterator();
        }
    
        // iterate through columns creating rows (lists)
        while (its1[0].hasNext()) {
            List list = new ArrayList(colNr);
            for (int i = 0; i < colNr; i++) {
                list.add(its1[i].next());
            }
            outputCol1.add(list);
        }
        while (its2[0].hasNext()) {
            List list = new ArrayList(colNr);
            for (int i = 0; i < colNr; i++) {
                list.add(its2[i].next());
            }
            outputCol2.add(list);
        }
        


        if (IBoxConstants.UNION.equals(paramValue)) {
            outputCol.addAll(outputCol1);
            outputCol.addAll(outputCol2);
        
        } else if (IBoxConstants.INTERSECT.equals(paramValue)) {
            if (outputCol1.size() < outputCol2.size()) {
                for (Iterator it = outputCol1.iterator(); it.hasNext();) {
                    Object elem = it.next();
                    if (outputCol2.remove(elem)) {
                        outputCol.add(elem);
                    }
                }
            } else {
                for (Iterator it = outputCol2.iterator(); it.hasNext();) {
                    Object elem = it.next();
                    if (outputCol1.remove(elem)) {
                        outputCol.add(elem);
                    }
                }
            }
////            if (outputCol1.size() < outputCol2.size()) {
////                outputCol.addAll(outputCol1);
////                outputCol.retainAll(outputCol2);
////            } else {
////                outputCol.addAll(outputCol2);
////                outputCol.retainAll(outputCol1);
////            }

//            Collection smallerCol = null;
//            Collection largerCol = null;
//            if (col1.size() <= col2.size()) {
//                smallerCol = col1;
//                largerCol = col2;
//            } else {
//                smallerCol = col2;
//                largerCol = col1;
//            }
//            for (Iterator iter = smallerCol.iterator(); iter.hasNext();) {
//                Object o1 = iter.next();
//                if (largerCol.contains(o1)) {
//                    outputCol.add(o1);
//                }
//            }
        
        } else {
            // SETMINUS
            outputCol.addAll(outputCol1);
            for (Iterator it = outputCol2.iterator(); it.hasNext();) {
                outputCol.remove(it.next());
            }
////            outputCol.addAll(outputCol1);
////            outputCol.removeAll(outputCol2);
//            for (Iterator iter = col1.iterator(); iter.hasNext();) {
//                Object o1 = iter.next();
//                if (!col2.contains(o1)) {
//                    outputCol.add(o1);
//                }
//            }
        }
        
        // unpack
        Collection[] outputCols = new Collection[colNr];
        for (int i = 0; i < colNr; i++) {
            outputCols[i] = new ArrayList(outputCol.size());
        }
        
        for (Iterator it = outputCol.iterator(); it.hasNext();) {
            List list = (List)it.next();
            for (int i = 0; i < colNr; i++) {
                outputCols[i].add(list.get(i));
            }
        }
        
        outputs = outputCols;

//        doSetOutput(removeNullValues(outputCol));
    }

    /**
     * @see quoggles.boxes.IBox#reset()
     */
    public void reset() {
        super.reset();

        cols1 = null;
        cols2 = null;
    }

    /**
     * @see quoggles.boxes.IBox#reset(int)
     */
    public void reset(int index) {
        super.reset(index);

        int ioNum = getNumberOfInputs();
        
        if (ioNum == 2) {
            if (index == 0) {
                cols1 = null;
            } else {
                cols2 = null;
            }
        } else {
            if (index < ioNum / 2) {
                if (cols1 != null) {
                    cols1[index] = null;
                }
            } else {
                if (cols2 != null) {
                    cols2[index - ioNum / 2] = null;
                }
            }
        }
    }

}