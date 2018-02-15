package org.graffiti.plugins.algorithms.labeling;

import java.util.Iterator;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;

/**
 * An implementation of a simple algorithm plugin example which generates a
 * horizontal node chain with a user defined number of nodes.
 * 
 * @author scholz
 */
public class LabelingAlgorithm extends AbstractAlgorithm {
    // private StringParameter labelParam;

    public LabelingAlgorithm() {
        // labelParam =
        // new StringParameter(
        // "label0",
        // "label",
        // "every edge of the graph gets this label");
    }

    public String getName() {
        return "Labeling Algorithm";
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        // return new Parameter[] { labelParam };
        return new Parameter[] {};
    }

    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        // if (labelParam.getString().compareTo("") == 0)
        // {
        // errors.add("No empty string allowed.");
        // }

        // The graph is inherited from AbstractAlgorithm.
        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    // private void printNodeLabels(Node n)
    // {
    // View activeView = GraffitiSingleton.getInstance().getMainFrame()
    // .getActiveSession().getActiveView();
    // if (!(activeView instanceof FastView))
    // {
    // throw new RuntimeException("This algorithm works only for FastView.");
    // }
    // System.out.println(n); // the node
    // NodeGraphicAttribute nga = (NodeGraphicAttribute)n
    // .getAttribute(GraphicAttributeConstants.GRAPHICS);
    // double nodeX = nga.getCoordinate().getX();
    // double nodeY = nga.getCoordinate().getY();
    // double nodeWidth = nga.getDimension().getWidth();
    // double nodeHeight = nga.getDimension().getHeight();
    // LabelManager lm = ((FastView)activeView).getGraphicsEngine()
    // .getLabelManager();
    // for (Attribute a: n.getAttributes().getCollection().values())
    // {
    // if (a instanceof NodeLabelAttribute)
    // {
    // NodeLabelAttribute nla = (NodeLabelAttribute)a;
    // Label l = lm.acquireLabel(n, nla);
    // System.out.println(nla.getId()); // the name of the label
    // System.out.println(nla.getLabel()); // the displayed text
    // //System.out.println(l..getWidth()); // width of the enclosing
    // // rectangle
    // //System.out.println(l.getHeight()); // height of the enclosing
    // // rectangle
    // double dx = nla.getPosition().getRelativeXOffset();
    // double dy = nla.getPosition().getRelativeYOffset();
    // System.out.println(nodeX + (nodeWidth / 2) * dx); // x-coordinate of the
    // center of the label
    // System.out.println(nodeY + (nodeHeight / 2) * dy); // y-coordinate ...
    // }
    // }
    // }

    // graphics.setColor(Color.BLACK);
    // graphics.setStroke(new BasicStroke());
    // graphics.drawRect(0, 0, (int)width, (int)height);

    // private void moveNodeLabel(Node n)
    // {
    // View activeView = GraffitiSingleton.getInstance().getMainFrame()
    // .getActiveSession().getActiveView();
    // if (!(activeView instanceof FastView))
    // {
    // throw new RuntimeException("This algorithm works only for FastView.");
    // }
    //        
    // // Move labels
    // for (Attribute attr: n.getAttributes().getCollection().values())
    // {
    // if (attr instanceof NodeLabelAttribute)
    // {
    // NodeLabelAttribute labelAttr = (NodeLabelAttribute)attr;
    //                
    // // // tedious preparations
    // // HashMapAttribute collectionAttr =
    // // new HashMapAttribute("Ich brauche einen Namen sonst bin ich traurig");
    // // DoubleAttribute horzPosition =
    // // new DoubleAttribute(GraphicAttributeConstants.ABSOLUTE_X_OFFSET, 1.0);
    // // collectionAttr.add(horzPosition, false);
    // // DoubleAttribute vertPosition =
    // // new DoubleAttribute(GraphicAttributeConstants.ABSOLUTE_Y_OFFSET, 1.0);
    // // collectionAttr.add(vertPosition, false);
    // // DoubleAttribute vertOffset =
    // // new DoubleAttribute(GraphicAttributeConstants.LOCALALIGN, 0.0);
    // // collectionAttr.add(vertOffset, false);
    // //
    // // // perform position change
    // // labelAttr.setPosition(collectionAttr);
    //                
    // // tell about your capital efforts
    // System.out.println("Moved label: " + labelAttr.getLabel());
    // }
    // }
    // }

    private void putNodeLabelsAbove(Node node) {
        // View activeView = GraffitiSingleton.getInstance().getMainFrame()
        // .getActiveSession().getActiveView();

        // Move labels
        for (Attribute attr : node.getAttributes().getCollection().values()) {
            if (attr instanceof NodeLabelAttribute) {
                // NodeLabelAttribute labelAttr = (NodeLabelAttribute)attr;

                // perform position change
                // labelAttr.setPosition(new PositionAttribute())
            }
        }
    }

    public void execute() {
        Iterator<Node> nodesIt = graph.getNodesIterator();
        Node node;
        while (nodesIt.hasNext()) {
            node = nodesIt.next();
            putNodeLabelsAbove(node);
        }
    }

    @Override
    public void reset() {
        graph = null;
        // labelParam.setValue("label0");
    }

    /**
     * This function evaluates several layout criteria for the given node.
     * <p>
     * Each of these criteria is evaluated and weighted independently.
     * 
     * @returns the cumulated layout badness for the given node
     */
    public double calculateLayoutBadness(Node node) {
        // TODO: Criterion: recognizability of affiliation (distance to
        // corresponding node)

        // TODO: Criterion: label overlaps

        // TODO: Criterion: legibility (text size)

        return 0;
    }

}
