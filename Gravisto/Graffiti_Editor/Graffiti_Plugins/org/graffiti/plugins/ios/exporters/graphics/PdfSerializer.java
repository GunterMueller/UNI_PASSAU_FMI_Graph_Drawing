//==============================================================================
//
//   EpsSerializer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//==============================================================================
// $Id: PdfSerializer.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.exporters.graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.OutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Saves the graph as a pdf graphic.
 * 
 * @version $Revision: 5766 $
 */
public class PdfSerializer extends GraphicsSerializer {

    /*
     * @see org.graffiti.plugin.io.Serializer#getExtensions()
     */
    public String[] getExtensions() {
        return new String[] { ".pdf" };
    }

    /*
     * @see GraphicsSerializer#getGraphicsContainer(OutputStream, Rectangle)
     */
    @Override
    protected GraphicsContainer getGraphicsContainer(OutputStream stream,
            Rectangle rect) throws IOException {
        try {
            return new PdfGraphicsContainer(stream, rect);
        } catch (DocumentException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     * Implementation of GraphicsContainer using a PdfGraphics2D.
     */
    public class PdfGraphicsContainer implements GraphicsContainer {
        /** Graphics implementation used for pdf graphics. */
        Graphics2D pdfGraphics;

        /** Whole pdf document. */
        Document document;

        /**
         * Creates a new PdfGraphicsContainer object.
         * 
         * @param stream
         *            OutputStream to use for the file.
         * @param rect
         *            Rectangular describing the area.
         * 
         * @throws IOException
         *             if an error occurs accessing the stream.
         * @throws DocumentException
         */
        public PdfGraphicsContainer(OutputStream stream, Rectangle rect)
                throws IOException, DocumentException {
            document = new Document(new com.lowagie.text.Rectangle((float) rect
                    .getWidth(), (float) rect.getHeight()));
            PdfWriter writer = PdfWriter.getInstance(document, stream);
            document.open();

            DefaultFontMapper mapper = new DefaultFontMapper();
            // TODO: Add to a properties file
            // mapper.insertDirectory("C:\\Windows\\Fonts");
            FontFactory.registerDirectories();

            PdfContentByte cb = writer.getDirectContent();
            pdfGraphics = cb.createGraphics((float) rect.getWidth(),
                    (float) rect.getHeight(), mapper);

            pdfGraphics.translate(-rect.getMinX(), -rect.getMinY());
        }

        /*
         * @see GraphicsContainer#getGraphics()
         */
        public Graphics getGraphics() {
            return pdfGraphics;
        }

        /*
         * @see GraphicsContainer#saveGraphics()
         */
        public void saveGraphics() throws IOException {
            pdfGraphics.dispose();
            document.close();
        }
    }

    public String getName() {
        return "PDF Exporter";
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
