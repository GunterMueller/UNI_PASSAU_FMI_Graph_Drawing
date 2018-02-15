package org.graffiti.plugins.algorithms.sugiyama.util;

import java.util.Iterator;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.constraints.SugiyamaConstraint;
import org.graffiti.plugins.algorithms.sugiyama.constraints.VerticalConstraintWithTwoNodes;

public class ConstraintsUtil {

    /**
     * This method iterates through all available <tt>SugiyamaConstraint</tt>s
     * stored in <tt>SugiyamaData</tt>. The method check() is executed on each
     * available <tt>SugiyamaConstraint</tt> and the error-messages collected
     * and displayed to the user with a call to Graffiti's mainFrame.
     */
    public static void checkConstraints(SugiyamaData data) {
        if (data.getConstraintPolicy() != SugiyamaConstants.CONSTRAINTS_HANDLE)
            return;

        Iterator<SugiyamaConstraint> citer = data.getConstraints().iterator();
        SugiyamaConstraint constraint;

        boolean haveMandatoryMessages = false;
        boolean haveNonMandatoryMessages = false;
        String mandatoryMessages = "";
        String nonMandatoryMessages = "";
        String message = null;

        while (citer.hasNext()) {
            constraint = citer.next();
            message = constraint.check();
            if (message != null) {
                if (constraint.isMandatory()) {
                    haveMandatoryMessages = true;
                    mandatoryMessages += "- " + message + "\n";
                    message = null;
                } else {
                    haveNonMandatoryMessages = true;
                    nonMandatoryMessages += "- " + message + "\n";
                    message = null;
                }
            }
        }

        message = "";
        if (haveMandatoryMessages || haveNonMandatoryMessages) {
            if (haveMandatoryMessages) {
                message += "The following mandatory constraints are violated:\n\n";
                message += mandatoryMessages;
            }
            if (haveNonMandatoryMessages) {
                if (haveMandatoryMessages) {
                    message += "\n";
                }

                message += "The following non-mandatory constraints are violated:\n\n";
                message += nonMandatoryMessages;
            }
            GraffitiSingleton.getInstance().getMainFrame().showMessageDialog(
                    message);
        }

    }

    /**
     * Checks all constraints of type
     * <code>VerticalConstraintWithTwoNodes</code>. The method check() is
     * executed and the error-messages collected and displayed to the user with
     * a call to Graffiti's mainFrame.
     */
    public static void checkVerticalConstraints(SugiyamaData data) {
        Iterator<SugiyamaConstraint> citer = data.getConstraints().iterator();
        SugiyamaConstraint constraint;

        boolean haveMandatoryMessages = false;
        boolean haveNonMandatoryMessages = false;
        String mandatoryMessages = "";
        String nonMandatoryMessages = "";
        String message = null;

        while (citer.hasNext()) {
            constraint = citer.next();
            if (constraint instanceof VerticalConstraintWithTwoNodes) {
                message = constraint.check();
                // System.out.println("VerticalConstraint.checked!!! -> "
                // + (message == null));

                if (message != null) {
                    if (constraint.isMandatory()) {
                        haveMandatoryMessages = true;
                        mandatoryMessages += "- " + message + "\n";
                        message = null;
                    } else {
                        haveNonMandatoryMessages = true;
                        nonMandatoryMessages += "- " + message + "\n";
                        message = null;
                    }
                }
            }
        }

        message = "";
        if (haveMandatoryMessages || haveNonMandatoryMessages) {
            if (haveMandatoryMessages) {
                message += "The following mandatory constraints are violated:\n\n";
                message += mandatoryMessages;
            }
            if (haveNonMandatoryMessages) {
                if (haveMandatoryMessages) {
                    message += "\n";
                }

                message += "The following non-mandatory constraints are violated:\n\n";
                message += nonMandatoryMessages;
            }
            GraffitiSingleton.getInstance().getMainFrame().showMessageDialog(
                    message);
        }
    }

    public static void removeDeletedEdgesConstraints(Graph graph) {
        Iterator<Node> nodeIterator = graph.getNodesIterator();
        while (nodeIterator.hasNext()) {
            try {
                Node node = nodeIterator.next();
                CollectionAttribute c = (CollectionAttribute) node
                        .getAttribute(SugiyamaConstants.PATH_CONSTRAINTS);
                Map<String, Attribute> constraints = c.getCollection();
                for (String name : constraints.keySet()) {
                    if (name.startsWith("sugiyamaConstraint_deletedEdge_")) {
                        node.removeAttribute(SugiyamaConstants.PATH_CONSTRAINTS
                                + Attribute.SEPARATOR + name);
                    }
                }
            } catch (AttributeNotFoundException anfe) {
                // Doesn't matter. Don't do anything.
            }
        }

    }

}
