package quoggles.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides string representations for (combinations of) types declared
 * in <code>ITypeConstants</code>.
 * 
 * @see quoggles.constants.ITypeConstants
 */
public class TypeStringConstants {

    private static Map map = null;
    

    private static void fillMap() {
        map = new HashMap();
        map.put(new Integer(ITypeConstants.EDGE), "edge");
        map.put(new Integer(ITypeConstants.EDGES), "edges");
//        map.put(new Integer(ITypeConstants.GRAPH), "graph");
        map.put(new Integer(ITypeConstants.GRAPH_ELEMENT), "gr. element");
        map.put(new Integer(ITypeConstants.NODE), "node");
        map.put(new Integer(ITypeConstants.NODES), "nodes");
        map.put(new Integer(ITypeConstants.GRAPH_ELEMENTS), "gr. elements");
        map.put(new Integer(ITypeConstants.ONEOBJECT), "1 object");
        map.put(new Integer(ITypeConstants.COLLECTION), "collection");
        map.put(new Integer(ITypeConstants.EDGE + 
            ITypeConstants.EDGES), "edge(s)");
        map.put(new Integer(ITypeConstants.NODE + 
            ITypeConstants.NODES), "node(s)");
        map.put(new Integer(ITypeConstants.GRAPH_ELEMENT + 
            ITypeConstants.GRAPH_ELEMENTS), "gr. element(s)");
        map.put(new Integer(ITypeConstants.ATTRIBUTABLE), 
                "attributable");
        map.put(new Integer(ITypeConstants.ATTRIBUTABLES), 
                "attributables");
        map.put(new Integer(ITypeConstants.ATTRIBUTABLES +
            ITypeConstants.ATTRIBUTABLE), 
                "attributable(s)");
        map.put(new Integer(ITypeConstants.ONEOBJECT +
            ITypeConstants.COLLECTION), 
                "1 or more");
//        map.put(new Integer(ITypeConstants.GRAPH_ELEMENT + 
//            ITypeConstants.GRAPH_ELEMENTS + ITypeConstants.SELECTION), 
//                "gr. elem(s) / sel");
//        map.put(new Integer(ITypeConstants.GRAPH + 
//            ITypeConstants.SELECTION), "graph / selection");
        map.put(new Integer(ITypeConstants.NUMBER), ITypeConstants.NUMBER_STR);
        map.put(new Integer(ITypeConstants.BOOLEAN), ITypeConstants.BOOLEAN_STR);
        map.put(new Integer(ITypeConstants.GENERAL), "no restriction");
        
        map.put(new Integer(ITypeConstants.COLOF_NODESET), "Coll(Set(node))");
        map.put(new Integer(ITypeConstants.COLOF_EDGESET), "Coll(Set(edge))");

        map.put(new Integer(ITypeConstants.COLOF_NUMBER), "Coll(number)");
        map.put(new Integer(ITypeConstants.COLOF_COLS), "Coll(Coll)");

        map.put(new Integer(ITypeConstants.PARAMETER), "parameter");
    }

    /**
     * Get a map that contains a mapping between integer values and a string
     * corresponding to the type the integer represents.
     * 
     * @return mapping integer - type string
     */
    public static final Map getMap() {
        if (map == null) {
            fillMap();
        }
        return map;
    }
}
