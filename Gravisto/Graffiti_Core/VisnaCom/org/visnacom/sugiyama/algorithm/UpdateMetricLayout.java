/*==============================================================================
*
*   UpdateMetricLayout.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: UpdateMetricLayout.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.algorithm;

import java.util.Iterator;
import java.util.List;

import org.visnacom.sugiyama.model.*;

/**
 * provides update of metric layout during expand and contract.
 */
public class UpdateMetricLayout {
    //~ Methods ================================================================

    /**
     * main method for contract
     *
     * @param s DOCUMENT ME!
     * @param v DOCUMENT ME!
     */
    public static void contract(SugiCompoundGraph s, SugiNode v) {
        updateVerticalLayout(s, v, false);
        MetricLayout.horizontalLayout(s, 4);
        assert compareWithStaticLayout(s);
    }

    /**
     * main method for expand
     *
     * @param action DOCUMENT ME!
     */
    public static void expand(SugiActionExpand action) {
        updateVerticalLayout(action.s, action.v, false);
        MetricLayout.horizontalLayout(action.s, 4);
        assert compareWithStaticLayout(action.s);
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     * @param w DOCUMENT ME!
     * @param v DOCUMENT ME!
     * @param displacement DOCUMENT ME!
     */
    private static void changeHeightAndShiftDown(SugiCompoundGraph s,
        SugiNode w, SugiNode v, int displacement) {
        if(w.getClev().isInitialSubstringOf(v.getClev())) {
            w.setHeight(w.getHeight() + displacement);
            for(Iterator it = s.getChildrenIterator(w); it.hasNext();) {
                SugiNode z = (SugiNode) it.next();
                changeHeightAndShiftDown(s, z, v, displacement);
            }
        } else if(w.getClev().compareTo(v.getClev()) > 0) {
            assert w.getClev().getLengthOfCommonPart(v.getClev()) == w.getClev()
                                                                      .getLength()
            - 1;

            //only for consistence reasons
            w.setLocalY(w.getLocalY() + displacement);

            shiftDown(s, w, displacement);
        }
    }

    /**
     * for debug only.  the updateschema results in the exact coordinates as a
     * new static metric layout.
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static boolean compareWithStaticLayout(SugiCompoundGraph s) {
        /* now compare result with complete static metric layout: */
        for(Iterator it = s.getAllNodesIterator(); it.hasNext();) {
            SugiNode sn = (SugiNode) it.next();
            sn.saveCoordinates();
        }

        MetricLayout.layout(s);
        for(Iterator it = s.getAllNodesIterator(); it.hasNext();) {
            SugiNode sn = (SugiNode) it.next();
            sn.checkCoordinates();
        }

        return true;
    }

    /**
     * used in updateVerticalLayout. see pseudocode.
     *
     * @param s DOCUMENT ME!
     * @param w DOCUMENT ME!
     * @param displacement DOCUMENT ME!
     */
    private static void shiftDown(SugiCompoundGraph s, SugiNode w,
        int displacement) {
        w.setAbsoluteY(w.getAbsoluteY() + displacement);
        for(Iterator it = s.getChildrenIterator(w); it.hasNext();) {
            SugiNode z = (SugiNode) it.next();
            shiftDown(s, z, displacement);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param s
     * @param v
     * @param recursive if true, the sublevels are recursivly new computed.
     *        whether this is necessary, is not obvious. the final
     *        implementation does not use the recursion.
     *
     * @return int the new height for the level of v.
     */
    private static int updateSublevelsOfV(SugiCompoundGraph s, SugiNode v,
        boolean recursive) {
        List W = s.getNodesAtLevel(v.getClev());

        //sets the height and localY of children and updates height of W
        //reuse modified localVLayout of static layout
        int height = MetricLayout.localVLayout(W, recursive);

        //sets the localY of W, because p_u's don't have it
        for(Iterator it = W.iterator(); it.hasNext();) {
            SugiNode w = (SugiNode) it.next();
            assert w.getLocalY() == v.getLocalY() || w.getLocalY() == 0;
            w.setLocalY(v.getLocalY());

            //sets in particular the absoluteY value of v and p_u's; and their children
            //the other nodes would have it already
            MetricLayout.absoluteVLayout(s, w,
                ((SugiNode) s.getParent(w)).getAbsoluteY());
        }

        return height;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s
     * @param v
     * @param recursive DOCUMENT ME!
     */
    private static void updateVerticalLayout(SugiCompoundGraph s, SugiNode v,
        boolean recursive) {
        int oldHeight = v.getHeight();
        int newHeight = updateSublevelsOfV(s, v, recursive);

        changeHeightAndShiftDown(s, s.getMetricRoot(), v, newHeight - oldHeight);
    }
}
