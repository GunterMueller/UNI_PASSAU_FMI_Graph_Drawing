// =============================================================================
//
//   DebugSession.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.debug;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.DefaultListModel;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class DebugHeader implements DebugElement {
    private static Font font;
    private String text;

    public void writeToDocument(Document document) throws DocumentException {
        document.add(new Paragraph(text, font));
    }

    public DebugHeader(String text) {
        this.text = text;
        if (font == null) {
            font = new Font(Font.HELVETICA, 20, Font.UNDERLINE);
        }
    }
}

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DebugSession {
    private static Map<String, DebugSession> nameToSessionMap = new HashMap<String, DebugSession>();
    private static DefaultListModel sessionList = new DefaultListModel();

    private LinkedList<DebugElement> elements;
    private LinkedList<Integer> sectionNumbers;
    private String name;

    public static DebugSession create(String sessionName) {
        DebugSession session = new DebugSession(sessionName);
        nameToSessionMap.put(sessionName, session);
        sessionList.addElement(session);
        return session;
    }

    public static DebugSession get(String sessionName) {
        DebugSession session = nameToSessionMap.get(sessionName);
        if (session == null) {
            session = create(sessionName);
        }
        return session;
    }

    public static void remove(DebugSession session) {
        sessionList.removeElement(session);
        if (nameToSessionMap.get(session.getName()) == session) {
            nameToSessionMap.remove(session.getName());
        }
    }

    static DefaultListModel getSessionListModel() {
        return sessionList;
    }

    private DebugSession(String name) {
        this.name = name;
        elements = new LinkedList<DebugElement>();
        sectionNumbers = new LinkedList<Integer>();
        sectionNumbers.add(0);
        DebugWriter writer = createTextWriter(false);
        writer.println(name);
        writer.close();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name.equals("") ? "Session" : name;
    }

    public void writePdf(OutputStream stream) throws IOException {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, stream);
            document.open();
            for (DebugElement el : elements) {
                el.writeToDocument(document);
            }
        } catch (DocumentException e) {
            throw new IOException();
        }
        document.close();
    }

    public DebugImage createImageWriter() {
        return new DebugImage(this);
    }

    public DebugWriter createTextWriter(boolean isMonospaced) {
        return new DebugWriter(new ByteArrayOutputStream(), isMonospaced, this);
    }

    void addDebugText(DebugWriter text) {
        elements.addLast(text);
    }

    void addDebugImage(DebugImage image) {
        elements.addLast(image);
    }

    public void addLine(String line) {
        createTextWriter(true).append(line).close();
    }

    public String getSectionCode() {
        StringBuilder builder = new StringBuilder();
        Iterator<Integer> iter = sectionNumbers.iterator();
        builder.append(iter.next());
        while (iter.hasNext()) {
            builder.append(".");
            builder.append(iter.next());
        }
        return builder.toString();
    }

    public void addHeader(String header, int level) {
        if (level <= 0)
            throw new IllegalArgumentException();
        while (sectionNumbers.size() > level) {
            sectionNumbers.removeLast();
        }
        while (sectionNumbers.size() < level) {
            sectionNumbers.addLast(0);
        }
        sectionNumbers.addLast(sectionNumbers.removeLast() + 1);

        StringBuilder buf = new StringBuilder(getSectionCode());
        buf.append(" ");
        buf.append(header);
        elements.addLast(new DebugHeader(buf.toString()));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
