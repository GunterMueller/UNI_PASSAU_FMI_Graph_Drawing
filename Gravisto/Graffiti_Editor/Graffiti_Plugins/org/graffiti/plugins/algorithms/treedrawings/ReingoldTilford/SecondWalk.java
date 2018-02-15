/*
 * Created on 05.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford;

/**
 * @author Beiqi
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class SecondWalk {

    public void secondWalk(RTNode node, double shiftSum) {

        node.setX(node.getX() + shiftSum);

        String str = "";
        for (int i = 0; i < node.getNumberOfChildren(); i++) {
            RTNode n = (RTNode) node.getChildren().get(i);
            ConturElement c = n.getRightContur().getContur().getFirst();
            secondWalk(n, shiftSum + c.getShift());

            str += "\nx: " + n.getX() + "\tc.getShift: " + c.getShift();
        }

        // System.out.println(str);
    }

}
