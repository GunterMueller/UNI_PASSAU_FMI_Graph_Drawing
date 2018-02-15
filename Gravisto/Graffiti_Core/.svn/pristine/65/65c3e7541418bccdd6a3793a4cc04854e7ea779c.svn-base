// =============================================================================
//
//   DebugWriter.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.debug;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DebugWriter extends PrintWriter implements DebugElement {
    private DebugSession session;
    private ByteArrayOutputStream buffer;
    private LinkedList<String> text;
    private LinkedList<Color> colors;
    private Color color;
    private boolean isMonospaced;

    public void writeToDocument(Document document) throws DocumentException {
        flush2();
        Paragraph paragraph = new Paragraph();
        Iterator<String> textIter = text.iterator();
        Iterator<Color> colorIter = colors.iterator();
        while (textIter.hasNext()) {
            String str = textIter.next();
            Color col = colorIter.next();
            paragraph.add(new Phrase(str, FontFactory.getFont(
                    isMonospaced ? FontFactory.COURIER : FontFactory.TIMES, 12,
                    Font.NORMAL, col)));
        }
        document.add(paragraph);
    }

    public DebugWriter(ByteArrayOutputStream buffer, boolean isMonospaced,
            DebugSession session) {
        super(buffer);
        text = new LinkedList<String>();
        colors = new LinkedList<Color>();
        this.buffer = buffer;
        this.isMonospaced = isMonospaced;
        this.session = session;
        color = Color.BLACK;
    }

    private void flush2() {
        flush();
        text.addLast(buffer.toString());
        colors.addLast(color);
        buffer.reset();
    }

    public void setColor(Color color) {
        flush2();
        this.color = color;
    }

    @Override
    public void close() {
        session.addDebugText(this);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
