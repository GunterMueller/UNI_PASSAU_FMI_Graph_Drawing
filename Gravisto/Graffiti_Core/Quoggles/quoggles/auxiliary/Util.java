package quoggles.auxiliary;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import javax.swing.JComponent;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
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
import org.graffiti.util.InstanceLoader;

import quoggles.QDialog;
import quoggles.algorithms.qbfs.QPreNodesAlgorithm;
import quoggles.auxiliary.attributes.CollAttribute;
import quoggles.auxiliary.attributes.ObjectAttribute;
import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.constants.QConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.InvalidParameterException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.icons.IBoxIcon;
import quoggles.parameters.OptionParameter;
import quoggles.querygraph.BoxAttribute;
import quoggles.representation.BoxRepresentation;
import quoggles.representation.IBoxRepresentation;
import quoggles.stdboxes.input.Input_Box;
import quoggles.stdboxes.output.BoolPredicateEnd_Box;
import quoggles.stdboxes.subquery.SubQuery_Box;

/**
 * A set of public static auxialiary methods.
 */
public class Util {

    public static final Object UNIQUE = new Unique();

    public static final double CLICK_TOLERANCE = 5.0d;
    

    public static int max(int a, int b, int c, int d) {
        return Math.max(Math.max(a, b), Math.max(c, d));
    }

    public static int min(int a, int b, int c, int d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }

    public static int max(int a, int b, int c) {
        return Math.max(Math.max(a, b), c);
    }

    public static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    /**
     * Returns the box that is saved via a <code>BoxAttribute</code> in the 
     * given <code>Node</code>.
     * 
     * @param n
     * @return
     */
    public static IBox getBox(Node n) {
        return ((BoxAttribute)n.getAttribute(IBoxConstants.BOX_ATTR_ID))
            .getIBox();
    }

    /**
     * Returns true iff the <code>MouseEvent</code> indicates that the control
     * or the shift key has been pressed while generating the event.
     *
     * @param me mouse event
     * 
     * @return true if user pressed the control or shift key while initiating
     * the mouse event
     */
    public static boolean isControlDown(MouseEvent me)
    {
        return me.isControlDown() || me.isShiftDown();
    }

    /**
     * Returns index of the input / putput (depending on the value of 
     * the second parameter) of the given edge.<p>
     * <code>false</code> means that the index is returned that indicates into
     * which input (number) the edge's output (target) leads.<p>
     * <code>true</code> means that the index is returned that indicates from
     * which output (number) the edge's input (source) comes from.
     * 
     * @param e
     * @param out
     * @return index saved as attribute of the edge
     */
    public static int getIOIndex(Edge e, boolean out) {
        if (out) {
            return e.getInteger(QConstants.OUTPUT_INDEX_ID);
        } else {
            return e.getInteger(QConstants.INPUT_INDEX_ID);
        }
    }
    
    /**
     * An object evaluates to <code>false</code>, if one of the following holds
     * (tried in the given order; if the class pattern matches, but the rest 
     * of the condition fails, the initial object is considered to evaluate to 
     * <code>true</code>):
     * <ol>
     *  <li>it is an empty <code>Collection</code>
     *  <li>it is a non-empty <code>Collection</code> and one of its elements
     *      evaluates to false</li>
     *  <li>it is a <code>Boolean</code> and its value is false</li>
     *  <li>it is a <code>Number</code> and its value is 0d or NaN</li>
     *  <li>it is <code>null</code></li>
     *  <li>it is the special value <code>quoggles.constants.QConstants.EMPTY</code></li>
     *  <li>its value as a string (what a call to <code>toString()</code> 
     *      yields) is equal to "false" (ignoring case)</li>
     * </ol>
     * 
     * @param col the <code>Object</code> to check
     * @return a boolean evaluation of the given object
     */
    public static boolean interpretAsBoolean(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof Collection) {
            Collection col = (Collection)obj;
            if (col.isEmpty()) {
                return false;
            }
            for (Iterator it = col.iterator(); it.hasNext();) {
                if (!interpretAsBoolean(it.next())) {
                    return false;
                }                
            }
            return true;
        } else if (obj instanceof Boolean) {
            return ((Boolean)obj).booleanValue();
        } else if (obj instanceof Number) {
            double d = ((Number)obj).doubleValue();
            return d != 0d && d != Double.NaN;
        } else if (obj == quoggles.constants.QConstants.EMPTY) {
            return false;
        } else if (obj.toString().toLowerCase().equals("false")) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether <code>compObject</code> matches the value given by the
     * parameters.<p>
     * It uses conversions to the format specified via the parameters.<p>
     * A <code>null</code> value always leads to false except when string 
     * comparison is applied. In this case, the object is converted to the
     * string <code>"null"</code> prior to testing.
     * 
     * @param compObject the object to check
     * 
     * @return true if <code>compObject</code> matches the value given by the
     * parameters
     * 
     * @throws InvalidInputException
     * @throws InvalidParameterException
     */
    public static Boolean match(Object compObject, Parameter[] parameters) 
        throws InvalidInputException, InvalidParameterException {
            
        Object compValue = null;
        Object val = ((OptionParameter)parameters[0]).getValue();
        String attrString = ((StringParameter)parameters[2]).getString();

        if (compObject == null) {
            if (!val.equals(ITypeConstants.STRING_STR)) {
                return new Boolean(false);
            } else {
                compObject = "null";
            }
        }
        if (compObject == quoggles.constants.QConstants.EMPTY) {
            if (!val.equals(ITypeConstants.STRING_STR)) {
                return new Boolean(false);
            } else {
                compObject = "";
            }
        }

        Comparator compy = null;
        try {        
            if (val.equals(ITypeConstants.INTEGER_STR)) {
                compy = Comparators.getIntegerComparator();
                if (compObject instanceof Number) {
                    compObject = new Integer(((Number)compObject).intValue());
                } else {
                    compObject = 
                        new Long(Long.parseLong(compObject.toString()));
                }
                compValue = new Long(attrString);
                
            } else if (val.equals(ITypeConstants.FLOATING_STR)) {
                compValue = new Double(attrString);
                compObject = 
                    new Double(Double.parseDouble(compObject.toString()));
                compy = Comparators.getFloatingComparator();
                
            } else if (val.equals(ITypeConstants.STRING_STR)) {
                compValue = attrString;
                compObject = compObject.toString();
                compy = Comparators.getStringComparator();
                
            } else if (val.equals(ITypeConstants.BOOLEAN_STR)) {
                boolean boolValue = Util.interpretAsBoolean(attrString);
                switch (((OptionParameter)parameters[1]).getOptionNr()) {
                    case 0 : // EQUAL
                        return new Boolean(Util.interpretAsBoolean(compObject)
                            == boolValue);
                    case 1 : // NOTEQUAL
                        return new Boolean(Util.interpretAsBoolean(compObject)
                            != boolValue);
                    default :
                        throw new InvalidParameterException("This type of " +
                            "comparison not allowed for this type.");
                }

            } else {
                throw new RuntimeException(
                    "Wrong/unknown parameter value.");
            }
        } catch (NumberFormatException nfe) {
            throw new InvalidInputException(
                "Could not convert value or input to type given by the" +
                " parameter: (" + attrString + " and " + 
                compObject.getClass().getName() + " to type " + val + ")");
        }        
        
        boolean holds = false;
        try {
            switch (((OptionParameter)parameters[1]).getOptionNr()) {
                case 0 : // EQUAL
                    holds = compy.compare(compObject, compValue) == 0; break;
                case 1 : // NOTEQUAL
                    holds = compy.compare(compObject, compValue) != 0; break;
                case 2 : // LT
                    holds = compy.compare(compObject, compValue) < 0; break;
                case 3 : // GT
                    holds = compy.compare(compObject, compValue) > 0; break;
                case 4 : // LTE
                    holds = compy.compare(compObject, compValue) <= 0; break;
                case 5 : // GTE
                    holds = compy.compare(compObject, compValue) >= 0; break;
    
                default :
                    throw new RuntimeException(
                        "Wrong/unknown parameter value.");
            }
        } catch (ClassCastException cce) {
            throw new InvalidInputException(
                "Type of input and type parameter do not match.");
        }
        
        return new Boolean(holds);
    }


    /**
     * Calculate the absolute position from the relative position of the given
     * input / output (via index) of the given box representation.
     *
     * @param iBoxRep
     * @return
     */
    public static Point getAbsPos
        (IBoxRepresentation iBoxRep, boolean isInput, int index) {
        
        BoxRepresentation boxRep = iBoxRep.getRepresentation();
        Point2D relIO = null;
        if (isInput) {
            if (iBoxRep.getIBox().getNumberOfInputs() == 0) {
                return null;
            }
            relIO = iBoxRep.getRelInputsPos()[index];
            
        } else {
            if (iBoxRep.getIBox().getNumberOfOutputs() == 0) {
                return null;
            }
            relIO = iBoxRep.getRelOutputsPos()[index];
        }
        Point absIO = new Point
            ((int)(boxRep.getX() -1 + 
                boxRep.getWidth() * relIO.getX()),
            (int)(boxRep.getY() + 
                boxRep.getHeight() * relIO.getY()));

        return absIO;
    }

    /**
     * Checks whether or not a point is said to be located on a line. It uses
     * the field <code>CLICK_TOLERANCE</code> as a certain tolerance, i.e. it
     * really checks whether or not the point lies on a thicker line.
     *
     * @param line
     * @param x
     * @param y
     *
     * @return true if point lies close enough to the line
     */
    public static boolean lineContains(Line2D line, double x, double y)
    {
        double maxDist = CLICK_TOLERANCE;

        if(IBoxConstants.ONEONE_CONNECTOR_HEIGHT > maxDist)
        {
            maxDist = IBoxConstants.ONEONE_CONNECTOR_HEIGHT / 2d;
        }

        double dist = line.ptSegDist(x, y);

        return dist < maxDist;
    }

    /**
     * Builds an <code>OptionParameter</code> using paths of the default node 
     * and edge graphics attributes as options.
     * 
     * @return an <code>OptionParameter</code> holding paths of the default 
     * node and edge graphics attributes as options.
     */
    public static OptionParameter buildAttrPath() {
        Set attrPaths = new HashSet();
        
        // (1) node graphic attributes
        Attribute attr = new NodeGraphicAttribute();
        attrPaths = gatherPaths(attr, attrPaths);
        
        // (1) edge graphic attributes
        attr = new EdgeGraphicAttribute();
        attrPaths = gatherPaths(attr, attrPaths);
        
        String[] options = new String[attrPaths.size() + 1];
        attrPaths.toArray(options);
        options[options.length - 1] = "";
        Arrays.sort(options);
        options[0] = "label.label";
        
        return new OptionParameter(options, true,
            "attrPath", "The path to the attribute");
    }

    /**
     * Recursively adds attribute paths to the given set.
     * 
     * @param attr the attribute that is added / searched
     * @param set the set to which paths are added
     * @return a set of attribute paths
     */
    public static Set gatherPaths(Attribute attr, Set set) {
        String attrPathStr = attr.getPath();
        if (attr instanceof CollectionAttribute) {
            for (Iterator it = ((CollectionAttribute)attr).getCollection()
                .values().iterator(); it.hasNext(); ) {
                set = gatherPaths((Attribute)it.next(), set);
            }
        } else if (!"".equals(attrPathStr) && 
            !(attr instanceof CompositeAttribute)) {

            set.add(attrPathStr);
        }
        return set;
    }

    /**
     * Find a <code>BoolPredicateEnd_Box</code> that may be hidden in a
     * <code>SubQuery_Box</code>.
     * 
     * @param node the associated box must be a sink; find a predicate end box
     * (maybe hidden in nested sub query boxes)
     */
    public static Node findMaybeNestedPredicateEnd(Node node, boolean foundOne)
        throws QueryExecutionException {
        
        IBox box = Util.getBox(node);
        Node outputNode = null;
        if (box instanceof BoolPredicateEnd_Box) {
            if (foundOne) {
                throw new QueryExecutionException("The" +
                    " predicate query must not have more than one" +
                    " BoolPredicateEnd_Box as sink." +
                    " Use a BooleanOp_Box or SetOperation_Box" +
                    " to concatenate several sinks. (" + 
                    Util.getBox(outputNode).getId() + " and " +
                    Util.getBox(node).getId() + ")");
            }
            outputNode = node;
        
        } else if (box instanceof SubQuery_Box) {
            // look for predicate end box
            Graph subQueryBoxSubGraph = (Graph)
                ((ObjectParameter)box.getParameters()[1]).getValue();
            Iterator it = subQueryBoxSubGraph.getNodesIterator();
            while (it.hasNext()) {
                Node subNode = (Node)it.next();
                Node newOutputNode = 
                    findMaybeNestedPredicateEnd(subNode, foundOne);
                if (newOutputNode != null) {
                    outputNode = newOutputNode;
                    foundOne = true;
                }
            }
        }
        return outputNode;
    }

    /**
     * Adds null values to the end of the given collection until the size of 
     * the collection is at least as large as the second parameter.
     * 
     * @param col the collection the size of which should be ensured
     * @param minSize the minimum size the collection must have
     * 
     * @return the given (and maybe manipulated) collection for convenience
     */
    public static Collection ensureSize(Collection col, int minSize) {
        while (col.size() <= minSize) {
            col.add(null);
        }
        return col;
    }

    /**
     * Set output from the given node/box, as input to its successors.
     * 
     * @param node
     * @param box
     * @throws QueryExecutionException
     */
    public static void pushOutputs(Node node, IBox box) 
        throws QueryExecutionException {
          
        pushOutputs(node, box, -1);
    }
        
  /**
   * Set output from the given node/box, as input to its successors.
   * Does not try to get the output with the index specified via the third
   * parameter.
   * 
   * @param node
   * @param box
   * @throws QueryExecutionException
   */
  public static void pushOutputs(Node node, IBox box, int notIndex) 
      throws QueryExecutionException {
        
      // push output(s) from this box to all successors in query graph
      for (Iterator edgeIt = node.getDirectedOutEdgesIterator();
          edgeIt.hasNext();) {
                            
          // from where do we come ...
          Edge edge = (Edge)edgeIt.next();
          int outIndex = Util.getIOIndex(edge, true);
            
          if (outIndex == notIndex) {
              continue;
          }
            
          // and where do we go?
          Node targetNode = edge.getTarget();
          IBox targetBox = Util.getBox(targetNode);
          int inIndex = Util.getIOIndex(edge, false);
                            
          //targetBox@inIndex needs output from box@outIndex as input
          targetBox.setInputAt(box.getOutputAt(outIndex), inIndex);
            
//@deprecated system does not contain clones any more
//          if (box instanceof ClonesOutputBox) {
//              // this box creates copies itself; tell subsequent boxes
//              targetBox.setContainsCopies(inIndex);
//          } else {
//              boolean[] contCop = box.getContainsCopies();
//              boolean allCopies = true;
//              if (contCop.length == 0) {
//                  allCopies = false;
//              } else {
//                  for (int i = 0; i < contCop.length; i++) {
//                      if (!contCop[i]) {
//                          allCopies = false;
//                          break;
//                      }
//                  }
//              }
//                
//              if (allCopies) {
//                  // box got copies; tell subsequent boxes that they receive 
//                  // copies as well
//                  targetBox.setContainsCopies(inIndex);
//              }
//          }
      }
  }

//  /**
//   * The given object is set as input to the successors of the given node.
//   * 
//   * @param node
//   * @param o
//   * @throws QueryExecutionException
//   */
//  private void pushOutputs(Node node, Object o) 
//      throws QueryExecutionException {
//        
//      // push output(s) from this box to all successors in query graph
//      for (Iterator edgeIt = node.getDirectedOutEdgesIterator();
//          edgeIt.hasNext();) {
//                            
//          Edge edge = (Edge)edgeIt.next();
//          IBox targetBox = getBox(edge.getTarget());
//          int inIndex = Util.getIOIndex(edge, false);
//                            
//          //targetBox@inIndex needs output
//          targetBox.setInputAt(o, inIndex);
//
//// @deprecated the system does not contain clones any more
////            IBox box = getBox(node);
////            if (box instanceof ClonesOutputBox) {
////                // this box creates copies itself; tell subsequent boxes
////                targetBox.setContainsCopies(inIndex);
////            } else {
////                boolean[] contCop = box.getContainsCopies();
////                boolean allCopies = true;
////                if (contCop.length == 0) {
////                    allCopies = false;
////                } else {
////                    for (int i = 0; i < contCop.length; i++) {
////                        if (!contCop[i]) {
////                            allCopies = false;
////                            break;
////                        }
////                    }
////                }
////                
////                if (allCopies) {
////                    // box got copies; tell subsequent boxes that they receive 
////                    // copies as well
////                    targetBox.setContainsCopies(inIndex);
////                }
////            }
//      }
//  }

    /**
     * Used by the <code>MouseListener</code> methods. Find the 
     * <code>IBoxIcon</code> or <code>BoxRepresentation</code> at the point
     * from the given <code>MouseEvent</code>. If no such component can be
     * found, returns the source saved in the <code>MouseEvent</code>.
     * 
     * @param e mouse event
     * 
     * @return component that has been hit
     */
   public static JComponent getCorrectSource(MouseEvent e) {
      Component comp = (Component) e.getSource();
      if (comp instanceof IBoxIcon) {
         return (JComponent) comp;
      }

      // // TODO fixme why is SharedOwnerFrame invisible?
      // Component parent = comp.getParent();
      // while (parent != null) {
      // parent.setVisible(true);
      // parent = parent.getParent();
      // }

      comp = comp.getComponentAt(e.getPoint());
      while (comp != null && comp instanceof Container) {
         Component tempcomp = comp.getComponentAt(e.getX() - comp.getX(), e.getY() - comp.getY());
         if (tempcomp == null || comp == tempcomp) {
            break;
         } else {
            comp = tempcomp;
         }
      }
      // if (comp == null)
      // return (JComponent)comp;

      // comp = (JComponent)
      // ((JComponent)comp).findComponentAt(e.getPoint());
      if (comp == e.getSource()) {
         return (JComponent) comp;
      }
      if (comp == null) {
         comp = (Component) e.getSource();
         return (JComponent) comp;
      }
      while (!(comp instanceof BoxRepresentation || comp instanceof QDialog)) {
         comp = comp.getParent();
      }
      if (comp instanceof QDialog) {
         return (JComponent) e.getSource();
      } else {
         return (JComponent) comp;
      }
   }

    /**
     * Get position of a free IO connection next to the given position.
     * The boolean parameter specifies whether to look for inputs or outputs.
     * 
     * @param graph the graph whose node contain the boxes to be searched
     * @param mousePos
     * @param lookForFreeOutput true if the box we work for has a free input 
     * i.e. only outputs from other boxes need to be searched
     * 
     * @return position of a free IO connection next to the given position
     */
    public static Point2D getNextFreeConnection(Graph graph,
        Point2D mousePos, boolean lookForFreeOutput, Component dComp) {

        double smallestDist = Double.POSITIVE_INFINITY;
        Point nextCon = null;
        
        Collection boxReps = new LinkedList();
        for (Iterator it = graph.getNodesIterator(); it.hasNext();) {
            Node node = (Node)it.next();
            IBox box = Util.getBox(node);
            boxReps.add(box.getGraphicalRepresentation().getRepresentation());
        }
        
        for (Iterator it = boxReps.iterator(); it.hasNext();) {
            BoxRepresentation bRep = (BoxRepresentation)it.next();
            if (bRep == dComp) {
                // ignore self
                continue;
            }
            IBoxRepresentation boxRep = bRep.getIBoxRepresentation();
                
            IBox iBox = boxRep.getIBox();
            Node boxNode = iBox.getNode();
                        
            if (!lookForFreeOutput) {
                if (boxNode.getInDegree() < iBox.getNumberOfInputs()) {
                    Point[] absInputs = getAbsoluteInputPos(boxRep);
                    for (int i = 0; i < absInputs.length; i++) {
                        double dist = absInputs[i].distanceSq(mousePos);
                        if (dist < smallestDist) {
                            smallestDist = dist;
                            nextCon = absInputs[i];
                        } 
                    }
                }
            } else {
                if (boxNode.getOutDegree() < iBox.getNumberOfOutputs()) {
                    Point[] absOutputs = getAbsoluteOutputPos(boxRep);
                    for (int i = 0; i < absOutputs.length; i++) {
                        if (boxRep instanceof Input_Box.InputBoxRepresentation) {
                            absOutputs[i].setLocation(0, absOutputs[i].y);
                        }
                        double dist = absOutputs[i].distanceSq(mousePos);
                        if (dist < smallestDist) {
                            smallestDist = dist;
                            nextCon = absOutputs[i];
                        } 
                    }
                }
            }
        }
        
        if (nextCon == null) {
            return mousePos;
        } else {
            return nextCon;
        }
    }
    
    /**
     * Calculate the absolute positions from the relative positions given by 
     * the box representation.
     *
     * @param iBoxRep
     * 
     * @return absolute positions of inputs
     */
    public static Point[] getAbsoluteInputPos(IBoxRepresentation iBoxRep) {
        BoxRepresentation boxRep = iBoxRep.getRepresentation();
        if (iBoxRep.getIBox().getNumberOfInputs() == 0) {
            return new Point[0];
        }
        
        Point2D[] relInputs = iBoxRep.getRelInputsPos();
        Point[] absInputs = new Point[relInputs.length];
        for (int i = 0; i < relInputs.length; i++) {
            Point2D pt = relInputs[i];
            absInputs[i] = new Point
                ((int)(boxRep.getX() -1 + 
                    boxRep.getWidth()*pt.getX()),
                (int)(boxRep.getY() + 
                    boxRep.getHeight()*pt.getY()));
        }
        return absInputs;
    }
    
    /**
     * Calculate the absolute positions from the relative positions given by 
     * the box representation.
     *
     * @param iBoxRep
     * 
     * @return absolute positions of outputs
     */
    public static Point[] getAbsoluteOutputPos(IBoxRepresentation iBoxRep) {
        BoxRepresentation boxRep = iBoxRep.getRepresentation();
        if (iBoxRep.getIBox().getNumberOfOutputs() == 0) {
            return new Point[0];
        }
        
        Point2D[] relOutputs = iBoxRep.getRelOutputsPos();
        Point[] absOutputs = new Point[relOutputs.length];
        for (int i = 0; i < relOutputs.length; i++) {
            Point2D pt = relOutputs[i];
            absOutputs[i] = new Point
                ((int)(boxRep.getX() -1 + 
                    boxRep.getWidth()*pt.getX()),
                (int)(boxRep.getY() + 
                    boxRep.getHeight()*pt.getY()));
        }
        return absOutputs;
    }

    /**
     * See the QPreNodesAlgorithm.
     * Fills second parameter with all nodes from which a path exists to the
     * given end node.
     * Returns all source nodes (indegree == 0) from that collection.
     * 
     * @param endNode
     * 
     * @return all source nodes (indegree == 0) from which a path exists to the
     * given end node
     * 
     * @see quoggles.algorithms.qbfs.QPreNodesAlgorithm
     */
    public static Collection getSourcesFrom
        (Node endNode, Collection preNodes) {

        QPreNodesAlgorithm pnAlg = new QPreNodesAlgorithm();
        pnAlg.attach(endNode.getGraph());
        pnAlg.setParameters(new Parameter[]{ 
            new NodeParameter(endNode, "", "") });
        try {
            pnAlg.check();
        } catch (PreconditionException e) {
            // should not happen
            throw new RuntimeException(e);
        }
        pnAlg.execute();
        
        for (Iterator it = pnAlg.getPreNodesList().iterator(); it.hasNext();) {
            Node node = (Node)it.next();
            preNodes.add(node);
        }
        return pnAlg.getPreSourceNodes();
    }

    /**
     * Calls <code>reset(int)</code> for all boxes where there exists a path 
     * from one of the nodes in the collection  <code>startNodes</code>.
     * That does not include the nodes contained in <code>startNodes</code>.
     * It only resets those inputs that can be reached, not the whole box.
     * 
     * @param qGraph
     * @param startNodes nodes that should not be reset but where the search 
     * starts
     */
    public static void resetAllBoxesFrom(Graph qGraph, Collection startNodes) {
        Stack copyNodes = new Stack();
        Stack indices = new Stack();
        for (Iterator it = startNodes.iterator(); it.hasNext();) {
            Node startNode = (Node)it.next();
            copyNodes.push(startNode);
            indices.push(new Integer(-1));
        }
        
        while (!copyNodes.isEmpty()) {
            Node node = (Node)copyNodes.pop();
            int resetAt = ((Integer)indices.pop()).intValue();
            if (!startNodes.contains(node)) {
                if (resetAt == -1) {
                    // do not reset nodes contained in startNodes
                    // does not happen
                    throw new RuntimeException("tried to reset wrong box");
                } else {
                    IBox box = Util.getBox(node);
                    box.reset(resetAt);
                }
            }
            Iterator it = node.getDirectedOutEdgesIterator();
            while (it.hasNext()) {
                Edge edge = (Edge)it.next();
                int index = Util.getIOIndex(edge, false);
                Node targetNode = edge.getTarget();
                if (!copyNodes.contains(targetNode)) {
                    copyNodes.push(targetNode);
                    indices.push(new Integer(index));
                }
            }
        }
    }

    /**
     * Calls <code>reset()</code> for all boxes in the given graph.
     * 
     * @param qGraph
     */
    public static void resetAllBoxes(Graph qGraph) {
        for (Iterator it = qGraph.getNodesIterator(); it.hasNext();) {
            Node node = (Node)it.next();
            IBox box = Util.getBox(node);
            box.reset();
        }
    }
        
    /**
     * Adds the name (id) of the given box as a label to the given node.
     * 
     * @param iBox
     * @param node
     */
    public static void addLabel(IBox iBox, Node node) {
        NodeLabelAttribute nla = new NodeLabelAttribute("label", iBox.getId());
        nla.setAlignment(GraphicAttributeConstants.ABOVE);
        try {
            node.addAttribute(nla, "");
        } catch (AttributeExistsException aee) {
            // nothing to worry about
        }
    }
    
    /**
     * Converts an array of objects to an array of parameters.
     * 
     * @param in
     * 
     * @throws Exception
     * 
     * @return <code>Parameter[]</code> version of the given object array
     */
    public static Parameter[] convertToParameters(Object[] in) 
        throws Exception {
            
        Parameter[] params = new Parameter[in.length];
        
        for (int i = 0; i < in.length; i++) {
            Object obj = in[i];
            params[i] = convertToParameter(obj, null);
        }
        
        return params;
    }
    
    /**
     * Converts the given object to a parameter. If the given string is not 
     * <code>null</code>, it should contain a (fully-quantified) class name of 
     * the parameter to which the given object should be converted.<p>
     * If the string is <code>null</code>, the best fitting parameter type
     * is automatically deduced. 
     * 
     * @param obj
     * @param parType
     * 
     * @return A <code>Parameter</code> holding the given object as value
     * 
     * @throws Exception if the object cannot be converted
     */
    public static Parameter convertToParameter(Object obj, String parType)
        throws Exception {

        if (parType == null) {
            Parameter param = null;
            // must find out best type
            if (obj instanceof Parameter) {
                param = (Parameter)obj;

            } else if (obj instanceof Integer) {
                param = new IntegerParameter((Integer)obj, "", "");

            } else if (obj instanceof Boolean) {
                param = new BooleanParameter((Boolean)obj, "", "");

            } else if (obj instanceof Double) {
                param = 
                    new DoubleParameter(((Double)obj).doubleValue(), "", "");

            } else if (obj instanceof Edge) {
                param = new EdgeParameter((Edge)obj, "", "");

            } else if (obj instanceof Node) {
                param = new NodeParameter((Node)obj, "", "");

            } else if (obj instanceof Selection) {
                param = new SelectionParameter((Selection)obj, "", "");

            } else if (obj instanceof String) {
                param = new EdgeParameter((Edge)obj, "", "");

            } else if (obj instanceof Collection) {
                Selection tempSel = new Selection();
                try {
                    tempSel.addAll((Collection)obj);
                    param = new SelectionParameter(tempSel, "", "");
                } catch (ClassCastException cce) {
                    // not a collection of graphelements; cannot be converted
                    // to a SelectionParameter
                    param = new ObjectParameter(obj, "", "");
                }

            } else {
                param = new ObjectParameter(obj, "", "");
            }
            return param;
        }
        
        // know desired param type
        
        if (IBoxConstants.INTEGER_PAR.equals(parType)) {
            if (obj instanceof Number) {
                return new IntegerParameter(((Number)obj).intValue(), "", "");
            } else {
                try {
                    return new IntegerParameter
                        (Integer.parseInt(obj.toString()), "", "");
                } catch (NumberFormatException nfe) {
                    throw new Exception("cannot convert " +
                        obj.getClass().getName());
                }
            }
        } else if (IBoxConstants.DOUBLE_PAR.equals(parType)) {
            if (obj instanceof Number) {
                return new DoubleParameter(((Number)obj).doubleValue(), "", "");
            } else {
                try {
                    return new DoubleParameter
                        (Double.parseDouble(obj.toString()), "", "");
                } catch (NumberFormatException nfe) {
                    throw new Exception("cannot convert " +
                        obj.getClass().getName());
                }
            }
        } else if (IBoxConstants.BOOLEAN_PAR.equals(parType)) {
            if (!(obj instanceof Boolean)) {
                return new BooleanParameter
                    (Boolean.valueOf(obj.toString()), "", "");
            }
        } else if (IBoxConstants.EDGE_PAR.equals(parType)) {
            if (!(obj instanceof Edge)) {
                throw new Exception("cannot convert " +
                    obj.getClass().getName());
            } else {
                return new EdgeParameter((Edge)obj, "", "");
            }
        } else if (IBoxConstants.NODE_PAR.equals(parType)) {
            if (!(obj instanceof Node)) {
                throw new Exception("cannot convert " +
                    obj.getClass().getName());
            } else {
                return new NodeParameter((Node)obj, "", "");
            }
        } else if (IBoxConstants.STRING_PAR.equals(parType)) {
            if (!(obj instanceof String)) {
                return new StringParameter(obj.toString(), "", "");
            } else {
                return new StringParameter((String)obj, "", "");
            }
        } else if (IBoxConstants.SELECTION_PAR.equals(parType)) {
            Selection tempSel = new Selection();
            if (obj instanceof GraphElement) {
                tempSel.add((GraphElement)obj);
                return new SelectionParameter(tempSel, "", "");
            } else if (obj instanceof Collection) {
                try {
                    tempSel.addAll((Collection)obj);
                    return new SelectionParameter(tempSel, "", "");
                } catch (ClassCastException cce) {
                    // not a collection of graphelements; cannot be converted
                    // to a SelectionParameter
                    throw new Exception("can only convert collections containing" +
                        "  only graph elements");
                }
            }
        }
        
        return (Parameter)InstanceLoader.createInstance
            (Class.forName(parType), new Object[]{ obj, "", "" });
    }
    
    /**
     * Converts the given <code>Parameter</code> to an object.
     * 
     * @param param
     * 
     * @return the object associated with the given parameter
     */
    public static Object convertFromParameter(Parameter param) {
        return param.getValue();
        
//        if (param instanceof IntegerParameter) {
//            return ((IntegerParameter)param).getInteger();
//
//        } else if (param instanceof BooleanParameter) {
//            return ((BooleanParameter)param).getBoolean();
//
//        } else if (param instanceof DoubleParameter) {
//            return ((DoubleParameter)param).getDouble();
//
//        } else if (param instanceof EdgeParameter) {
//            return ((EdgeParameter)param).getEdge();
//
//        } else if (param instanceof NodeParameter) {
//            return ((NodeParameter)param).getNode();
//
//        } else if (param instanceof SelectionParameter) {
//            return ((SelectionParameter)param).getSelection().getElements();
//
//        } else if (param instanceof StringParameter) {
//            return ((StringParameter)param).getString();
//
//        } else if (param instanceof ObjectParameter) {
//            return ((ObjectParameter)param).getValue();
//
//        } else {
//            throw new Exception("unknown parameter");
//        }
    }
    
    static final class Unique {
        /**
         * Always returns false.
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            return false;
        }
    }

    /**
     * According to the type specified by first (0) parameter, create a new 
     * instance of an attribute. Uses <code>value</code> as its initial value.
     *  
     * @param id id of the new attribute
     * @param value value of the new attribute
     * @param attbl <code>Attributable</code>; used to further distinguish type
     * of attribute (e.g. labels: node label / edge label)
     * 
     * @return a new attribute
     * 
     * @throws InvalidInputException if type and value do not correspond
     */
    public static Attribute getFreshAttribute
        (String id, Object value, Attributable attbl, String type) 
        throws InvalidInputException {

        Attribute attr = null;
        
        try {
            if (ITypeConstants.LABEL_STR.equals(type)) {
                if (attbl instanceof Node) {
                    attr = new NodeLabelAttribute(id, value.toString());
                } else if (attbl instanceof Edge) {
                    attr = new EdgeLabelAttribute(id, value.toString());
                } else {
                    attr = new LabelAttribute(id, value.toString());
                }
            } else if (ITypeConstants.STRING_STR.equals(type)) {
                attr = new StringAttribute(id, value.toString());
            } else if (ITypeConstants.INTEGER_STR.equals(type)) {
                attr = new IntegerAttribute(id, new Integer(value.toString()));
            } else if (ITypeConstants.DOUBLE_STR.equals(type)) {
                attr = new DoubleAttribute(id, new Double(value.toString()));
            } else if (ITypeConstants.BOOLEAN_STR.equals(type)) {
                attr = new BooleanAttribute(id, new Boolean(value.toString()));
            } else if (ITypeConstants.OBJECT_STR.equals(type)) {
                attr = new ObjectAttribute(id, value.toString());
            } else if (ITypeConstants.COLLECTION_STR.equals(type)) {
                attr = new CollAttribute(id, (Collection)value);
            }
        } catch (NumberFormatException nfe) {
            throw new InvalidInputException( 
                "Could not create attribute of" +
                " wanted type for that input: " + nfe.getLocalizedMessage());
        }
        
        return attr;
    }
    
}
