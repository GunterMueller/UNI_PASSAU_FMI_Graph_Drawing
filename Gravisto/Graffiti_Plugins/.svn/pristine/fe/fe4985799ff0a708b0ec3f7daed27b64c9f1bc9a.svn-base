// =============================================================================
//
//   CommandListFactoryFactory.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

import org.graffiti.plugins.views.fast.FontManager;
import org.graffiti.plugins.views.fast.ImageManager;
import org.graffiti.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.resource.CSSResource;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class CommandListFactory<L extends Label<L, LC>, LC extends LabelCommand> {
    private static final int DEFAULT_DOTS_PER_PIXEL = 1;

    private static final int DEFAULT_DOTS_PER_POINT = 1;

    private static final String HTML_STYLE = "position:absolute; width:auto; text-align:center";

    private static final String BODY_STYLE = "margin:0px";

    private static SharedContext sharedContext;

    private static UserAgentCallbackProxy userAgentCallback;

    private CommandFactory<L, LC> commandFactory;

    private XhtmlNamespaceHandler namespaceHandler;

    private NullUserInterface nullUserInterface;

    private FontManager<?> fontManager;

    private ImageManager<L, LC> imageManager;

    private L label;

    public static void createSharedContext() {
        userAgentCallback = new UserAgentCallbackProxy();
        System.setProperty("xr.util-logging.loggingEnabled", "false");
        PrintStream out = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                // Do nothing.
            }
        }));
        sharedContext = new SharedContext(userAgentCallback);
        System.setOut(out);
    }

    public CommandListFactory(CommandFactory<L, LC> commandFactory,
            FontManager<?> fontManager) {
        this.commandFactory = commandFactory;
        this.fontManager = fontManager;
        namespaceHandler = new XhtmlNamespaceHandler();
        nullUserInterface = new NullUserInterface();
    }

    public void setImageManager(ImageManager<L, LC> imageManager) {
        this.imageManager = imageManager;
    }

    public Pair<LinkedList<LC>, Point2D> createCommandList(String text,
            double width, String font, int fontSize, Color fontColor,
            Shape masterClip, L label) {
        if (text.length() == 0) {
            text = "\\";
        }
        if (text.charAt(0) == '\\') {
            StringBuffer buffer = new StringBuffer(
                    "<html><head></head><body><p>");
            int len = text.length();
            // Drop the first (escape) char.
            for (int i = 1; i < len; i++) {
                char ch = text.charAt(i);
                switch (ch) {
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                case '"':
                    buffer.append("&quot;");
                    break;
                case '\n':
                    buffer.append("<br />");
                    break;
                default:
                    buffer.append(ch);
                }
            }
            buffer.append("</p></body></html>");
            text = buffer.toString();
        } else {
            // HACK - Tidy.parseDOM returns an empty document if the last
            // character is '<' and subsequently getDocumentElement returns null
            // ... bug von jTidy?
            if (text.charAt(text.length() - 1) == '<') {
                text = text.substring(0, text.length() - 1).concat("&lt;");
            }
        }

        Tidy tidy = new Tidy();
        tidy.setXHTML(true);
        tidy.setLogicalEmphasis(true);
        tidy.setXmlOut(true);
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);
        Document document = tidy.parseDOM(new ByteArrayInputStream(text
                .getBytes()), null);
        Element htmlElement = document.getDocumentElement();
        htmlElement.setAttribute("style", HTML_STYLE);
        NodeList nodeList = htmlElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (element.getTagName().equals("body")) {
                if (!element.hasAttribute("style")) {
                    StringBuffer style = new StringBuffer(BODY_STYLE);
                    style.append("; font-family:'");
                    style.append(font);
                    style.append("'; font-size:");
                    style.append(fontSize);
                    style.append("pt");
                    style.append(String.format("; color:#%02x%02x%02x",
                            fontColor.getRed(), fontColor.getGreen(), fontColor
                                    .getBlue()));
                    element.setAttribute("style", style.toString());
                }
                break;
            }
        }
        userAgentCallback.setFactory(this);
        sharedContext.reset();
        sharedContext.setBaseURL(""); // TODO
        sharedContext.getCss().flushAllStyleSheets();
        sharedContext.setNamespaceHandler(namespaceHandler);
        sharedContext.getCss().setDocumentContext(sharedContext,
                namespaceHandler, document, nullUserInterface);
        sharedContext.setFontResolver(new LabelFontResolver(1.0, fontManager));
        sharedContext.setDPI(72 * DEFAULT_DOTS_PER_POINT);
        sharedContext.setDotsPerPixel(DEFAULT_DOTS_PER_PIXEL);
        sharedContext.setPrint(false);
        sharedContext.setInteractive(false);
        this.label = label;
        CommandListBuilder<L, LC> builder = new CommandListBuilder<L, LC>(
                commandFactory, imageManager, document, sharedContext, width,
                masterClip);
        this.label = null;
        return Pair.create(builder.getCommands(), builder.getSize());
    }

    public CSSResource getCSSResource(String uri) {
        System.out.println("getCSSResource(\"" + uri + "\")");
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public ImageResource getImageResource(String uri) {
        System.out.println("getImageResource(\"" + uri + "\")");
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public XMLResource getXMLResource(String uri) {
        System.out.println("getXMLResource(\"" + uri + "\")");
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public L getLabel() {
        return label;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
