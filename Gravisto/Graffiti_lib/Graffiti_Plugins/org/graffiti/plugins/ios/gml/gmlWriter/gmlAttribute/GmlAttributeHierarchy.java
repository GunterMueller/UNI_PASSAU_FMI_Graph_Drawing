// =============================================================================
//
//   GmlAttributeHierarchy.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlAttributeHierarchy.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlWriter.gmlAttribute;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Class <code>GmlAttributeHierarchy</code> is used as an intermediate
 * representation of the hierarchy to be saved in GML style.
 * 
 * @author ruediger
 */
public class GmlAttributeHierarchy {
    /** The mapping from hierarchy to values. */
    private Map<String, Object> hierarchy;

    /** The level of indentation. */
    private int offset;

    /**
     * Constructs a new <code>GmlAttributeHierarchy</code> for a given level of
     * indentation.
     * 
     * @param offset
     *            the level of indentation to be used.
     */
    public GmlAttributeHierarchy(int offset) {
        this.offset = offset;
        this.hierarchy = new LinkedHashMap<String, Object>();
    }

    /**
     * Adds a an attribute represented as a String to the hierarchy according to
     * its path.
     * 
     * @param gmlPath
     *            the path in GML representation.
     * @param value
     *            the value of the Attribute.
     */
    public void add(String gmlPath, GmlAttributeValue value) {
        assert gmlPath != null;
        assert value != null;

        String[] ids = gmlPath.split("\\.");

        if (ids.length == 1) {
            // this.hierarchy.put(gmlPath, value);
            LinkedList<GmlAttributeValue> l = new LinkedList<GmlAttributeValue>();
            l.add(value);
            this.hierarchy.put(gmlPath, l);
        } else {
            Map<String, Object> m = this.hierarchy;

            for (int i = 0; i < (ids.length - 1); ++i) {
                if (ids[i].equals("")) {
                } else {
                    assert !ids[i].equals("") : "empty id of path " + gmlPath
                            + ".";

                    Map<String, Object> newMap = new LinkedHashMap<String, Object>();

                    if (!m.containsKey(ids[i])) {
                        m.put(ids[i], newMap);
                        m = newMap;
                    } else {
                        try {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> unsafe = (Map<String, Object>) m
                                    .get(ids[i]);

                            m = unsafe;
                        } catch (ClassCastException cce) {
                        }
                    }
                }
            }

            // m.put(ids[ids.length - 1], value);
            String key = ids[ids.length - 1];
            assert !key.equals("");

            if (m.containsKey(key)) {
                @SuppressWarnings("unchecked")
                LinkedList<GmlAttributeValue> l = (LinkedList<GmlAttributeValue>) m
                        .get(key);
                l.addLast(value);
                m.put(key, l);
            } else {
                LinkedList<GmlAttributeValue> l = new LinkedList<GmlAttributeValue>();
                l.addLast(value);
                m.put(key, l);
            }
        }
    }

    /**
     * Adds missing levels for the specified path to the hierarchy.
     * 
     * @param path
     *            the path for which to establish the hierarchy if necessary.
     */
    public void addLevel(String path) {
        if (path.equals("") || path.equals("."))
            return;

        Map<String, Object> m = this.hierarchy;
        String[] ids = path.split("\\.");

        for (int i = 0; i < ids.length; ++i) {
            assert !ids[i].equals("");

            if (m.containsKey(ids[i])) {
            } else {
                Map<String, Object> newmap = new LinkedHashMap<String, Object>();
                m.put(ids[i], newmap);
                m = newmap;
            }
        }
    }

    /**
     * Prints the attribute hierarchy to a given output stream.
     * 
     * @param os
     *            the output stream to which to write the hierarchy.
     * @param indent
     *            the offset to be used initially.
     * 
     * @throws IOException
     *             if something fails while writing the hierarchy to the output
     *             stream.
     */
    public void printGML(OutputStream os, int indent) throws IOException {
        recPrintGML(os, indent, this.hierarchy);
    }

    /**
     * Returns <code>true</code> if the specified <code>Map</code> only contains
     * nonempty lists as values, <code>false</code> otherwise.
     * 
     * @param atts
     *            the <code>Map</code> to scan for nonempty lists.
     * 
     * @return <code>true</code> if the specified <code>Map</code> only contains
     *         nonempty lists as values, <code>false</code> otherwise.
     */
    private boolean isList(Map<String, Object> atts) {
        boolean list = true;

        for (Iterator<String> itr = atts.keySet().iterator(); itr.hasNext();) {
            String id = itr.next();
            Object o = atts.get(id);

            if (o instanceof Map<?, ?>) {
                list = false;
            } else {
                LinkedList<?> l = (LinkedList<?>) o;
                list = list && (l.size() > 0);
            }
        }

        return list;
    }

    /**
     * Writes whitespace of the specified length to the output stream
     * 
     * @param os
     *            the output stream to which to write the whitespace.
     * @param indent
     *            the number of spaces to be writen.
     * 
     * @throws IOException
     *             if an error occurrs accessing the output stream.
     */
    private void indent(OutputStream os, int indent) throws IOException {
        StringBuffer s = new StringBuffer("");

        for (int i = 0; i < indent; ++i) {
            s.append(" ");
        }

        os.write(s.toString().getBytes());
    }

    /**
     * Recursive auxiliary method for printin the attribute hierarchy to a given
     * output stream.
     * 
     * @param os
     *            the output stream to which to write the hierarchy.
     * @param indent
     *            the offset to be used initially.
     * @param atts
     *            the map containing keys and values to be printed.
     * 
     * @throws IOException
     *             if something fails while writing the hierarchy to the output
     *             stream.
     */
    private void recPrintGML(OutputStream os, int indent,
            Map<String, Object> atts) throws IOException {
        assert os != null;
        assert offset >= 0;
        assert atts != null;

        for (String id : atts.keySet()) {
            assert !id.equals("");

            Object o = atts.get(id);
            assert o != null;

            if (o instanceof Map<?, ?>) {
                // recursively print the hierarchy
                @SuppressWarnings("unchecked")
                Map<String, Object> m = (Map<String, Object>) o;

                if (m != null) {
                    if (isList(m)) {
                        do {
                            indent(os, indent);
                            os.write((id + " ").getBytes());
                            os.write("[\n".getBytes());
                            recPrintGML(os, indent + offset, m);
                            indent(os, indent);
                            os.write("]\n".getBytes());
                        } while (isList(m));
                    } else {
                        indent(os, indent);
                        os.write((id + " ").getBytes());
                        os.write("[\n".getBytes());
                        recPrintGML(os, indent + offset, m);
                        indent(os, indent);
                        os.write("]\n".getBytes());
                    }
                }
            } else {

                try {
                    // GmlAttributeValue v = (GmlAttributeValue) o;
                    @SuppressWarnings("unchecked")
                    LinkedList<GmlAttributeValue> l = (LinkedList<GmlAttributeValue>) o;
                    if (!l.isEmpty()) {
                        GmlAttributeValue v = l.getFirst();
                        l.remove(v);
                        indent(os, indent);
                        os.write((id + " ").getBytes());
                        os.write((v.getString() + "\n").getBytes());
                    }
                } catch (ClassCastException cce) {
                    System.out.println("type of Object: "
                            + o.getClass().getName());
                    throw cce;
                }

            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
