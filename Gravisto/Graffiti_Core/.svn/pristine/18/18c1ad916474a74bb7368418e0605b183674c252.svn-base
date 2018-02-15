// =============================================================================
//
//   CsvOutput.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class CsvOutput extends TabulatingOutput {
    private List<String> lines;
    private String separator;
    private String nullString;

    public CsvOutput() {
        separator = "\t";
        nullString = "NA";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beginTable(List<String> headers) {
        lines = new LinkedList<String>();
        add(headers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addRow(List<String> values) {
        add(values);
    }

    private void add(List<String> values) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = values.iterator();
        if (iter.hasNext()) {
            builder.append(iter.next());
        }
        while (iter.hasNext()) {
            builder.append(separator);
            String v = iter.next();
            if (v == null) {
                builder.append(nullString);
            } else {
                builder.append(v);
            }
        }
        lines.add(builder.toString());
        try {
            OutputStream out = getOut(true);
            PrintWriter writer = new PrintWriter(out);
            writer.println(builder.toString());
            writer.flush();
            out.flush();
            out.close();
        } catch (IOException e) {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void endTable() throws IOException {
        OutputStream out = getOut();
        PrintWriter writer = new PrintWriter(out);
        for (String line : lines) {
            writer.println(line);
        }
        writer.flush();
        out.flush();
        lines = null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
