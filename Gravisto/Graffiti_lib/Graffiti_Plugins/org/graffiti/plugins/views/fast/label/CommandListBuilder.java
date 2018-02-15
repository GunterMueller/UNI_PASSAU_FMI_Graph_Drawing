// =============================================================================
//
//   LabelCommandFactory.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.graffiti.plugins.views.fast.FastFont;
import org.graffiti.plugins.views.fast.ImageManager;
import org.w3c.dom.Document;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.FontContext;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.layout.BoxBuilder;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.render.AbstractOutputDevice;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.ViewportBox;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
final class CommandListBuilder<L extends Label<L, LC>, LC extends LabelCommand>
        extends AbstractOutputDevice implements OutputDevice {
    private static final int DEFAULT_HEIGHT = 10000;
    private LinkedList<LC> commands;
    private Point2D size;
    private BufferedImage dummyImage;
    private Graphics2D dummyGraphics;
    private FontRenderContext fontRenderContext;
    private CommandFactory<L, LC> commandFactory;
    private BlockBox root;
    private FontContext fontContext;
    private LabelReplacedElementFactory<L, LC> replacedElementFactory;
    private TextListener<L, LC> textRenderer;
    private Shape masterClip;

    protected CommandListBuilder(CommandFactory<L, LC> commandFactory,
            ImageManager<L, LC> imageManager, Document document,
            SharedContext sharedContext, double width, Shape masterClip) {
        this.commandFactory = commandFactory;
        this.masterClip = masterClip;
        commands = new LinkedList<LC>();

        dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        dummyGraphics = dummyImage.createGraphics();
        dummyGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        if (masterClip != null) {
            dummyGraphics.setClip(masterClip);
            commands.addLast(commandFactory.createSetClip(CloneUtil
                    .clone(masterClip)));
        }
        fontRenderContext = dummyGraphics.getFontRenderContext();
        // Fill list.
        replacedElementFactory = new LabelReplacedElementFactory<L, LC>(
                imageManager);
        sharedContext.setReplacedElementFactory(replacedElementFactory);
        textRenderer = new TextListener<L, LC>(commandFactory, commands,
                dummyGraphics, fontRenderContext);
        sharedContext.setTextRenderer(textRenderer);

        Rectangle rectangle = new Rectangle(0, 0, (int) width, DEFAULT_HEIGHT);
        sharedContext.set_TempCanvas(rectangle);
        LayoutContext layoutContext = sharedContext.newLayoutContextInstance();
        fontContext = new FontContext() {
        };
        layoutContext.setFontContext(fontContext);
        sharedContext.getTextRenderer().setup(layoutContext.getFontContext());
        root = BoxBuilder.createRootBox(layoutContext, document);
        root.setContainingBlock(new ViewportBox(rectangle));
        root.layout(layoutContext);
        size = new Point2D.Double(root.getWidth(), root.getHeight());
        RenderingContext renderingContext = sharedContext
                .newRenderingContextInstance();
        renderingContext.setFontContext(fontContext);
        renderingContext.setOutputDevice(this);
        sharedContext.getTextRenderer().setup(fontContext);
        root.getLayer().paint(renderingContext, 0, 0);
        dummyGraphics.dispose();
    }

    public LinkedList<LC> getCommands() {
        return commands;
    }

    public Point2D getSize() {
        return size;
    }

    public void clip(Shape clip) {
        dummyGraphics.clip(clip);
        commands.addLast(commandFactory.createSetClip(CloneUtil
                .clone(dummyGraphics.getClip())));
    }

    public void drawBorderLine(Rectangle bounds, int side, int lineWidth,
            boolean solid) {
        commands.addLast(commandFactory.createDrawBorderLine((Rectangle) bounds
                .clone(), side, lineWidth, solid));
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        commands.addLast(commandFactory.createDrawLine(x1, y1, x2, y2));
    }

    public void drawImage(FSImage arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void drawOval(int x, int y, int width, int height) {
        commands.addLast(commandFactory.createDrawOval(x, y, width, height));
    }

    public void drawRect(int x, int y, int width, int height) {
        commands.addLast(commandFactory.createDrawRect(x, y, width, height));
    }

    public void fill(Shape shape) {
        commands.addLast(commandFactory.createFill(CloneUtil.clone(shape)));
    }

    public void fillOval(int x, int y, int width, int height) {
        commands.addLast(commandFactory.createFillOval(x, y, width, height));
    }

    public void fillRect(int x, int y, int width, int height) {
        commands.addLast(commandFactory.createFillRect(x, y, width, height));
    }

    public Shape getClip() {
        return dummyGraphics.getClip();
    }

    public Object getRenderingHint(Key key) {
        return dummyGraphics.getRenderingHint(key);
    }

    public Stroke getStroke() {
        return dummyGraphics.getStroke();
    }

    public void paintReplacedElement(RenderingContext context, BlockBox box) {
        ReplacedElement replacedElement = box.getReplacedElement();
        if (replacedElement instanceof ReplacedImageElement<?, ?>) {
            ReplacedImageElement<?, ?> rie = (ReplacedImageElement<?, ?>) replacedElement;
            Point location = rie.getLocation();
            commands.add(commandFactory.createDrawImage(rie.getImage(),
                    location.x, location.y));
        }
    }

    public void setClip(Shape clip) {
        dummyGraphics.setClip(masterClip);
        clip(clip);
    }

    public void setColor(Color color) {
        commands.addLast(commandFactory.createSetColor(color));
    }

    public void setFont(FSFont font) {
        commands.addLast(commandFactory.createSetFont((FastFont) font));
    }

    public void setRenderingHint(Key key, Object value) {
        dummyGraphics.setRenderingHint(key, value);
        commands.addLast(commandFactory.createSetRenderingHint(key, value));
    }

    public void setStroke(Stroke stroke) {
        dummyGraphics.setStroke(stroke);
        commands.addLast(commandFactory.createSetStroke(stroke));
    }

    public void translate(double tx, double ty) {
        commands.addLast(commandFactory.createTranslate(tx, ty));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
