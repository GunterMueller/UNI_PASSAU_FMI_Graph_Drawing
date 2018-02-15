// =============================================================================
//
//   DebugImage.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
abstract class ImageOperation {
    public abstract void perform(Point2D.Double origin, Graphics2D graphics);
}

/**
 * This package will soon be moved to a different location.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class LineOp extends ImageOperation {
    private double x1;
    private double y1;
    private double x2;
    private double y2;

    public LineOp(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void perform(Point2D.Double origin, Graphics2D graphics) {
        graphics.drawLine((int) (x1 - origin.getX()),
                (int) (y1 - origin.getY()), (int) (x2 - origin.getX()),
                (int) (y2 - origin.getY()));
    }
}

/**
 * This package will soon be moved to a different location.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class RectOp extends ImageOperation {
    enum Shape {
        RECTANGLE, FILLEDRECTANGLE, OVAL, FILLEDOVAL
    };

    private double x;
    private double y;
    private double width;
    private double height;
    private Shape shape;

    public RectOp(double x, double y, double width, double height, Shape shape) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.shape = shape;
    }

    @Override
    public void perform(Point2D.Double origin, Graphics2D graphics) {
        switch (shape) {
        case RECTANGLE:
            graphics.drawRect((int) (x - origin.getX()), (int) (y - origin
                    .getY()), (int) width, (int) height);
            break;
        case FILLEDRECTANGLE:
            graphics.fillRect((int) (x - origin.getX()), (int) (y - origin
                    .getY()), (int) width, (int) height);
            break;
        case OVAL:
            graphics.drawOval((int) (x - origin.getX()), (int) (y - origin
                    .getY()), (int) width, (int) height);
            break;
        case FILLEDOVAL:
            graphics.fillOval((int) (x - origin.getX()), (int) (y - origin
                    .getY()), (int) width, (int) height);
        }
    }
}

/**
 * This package will soon be moved to a different location.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class SetColorOp extends ImageOperation {
    private Color color;

    @Override
    public void perform(Point2D.Double origin, Graphics2D graphics) {
        graphics.setColor(color);
    }

    public SetColorOp(Color color) {
        this.color = color;
    }
}

/**
 * This package will soon be moved to a different location.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class SetFontOp extends ImageOperation {
    private Font font;

    @Override
    public void perform(Point2D.Double origin, Graphics2D graphics) {
        graphics.setFont(font);
    }

    public SetFontOp(Font font) {
        this.font = font;
    }
}

/**
 * This package will soon be moved to a different location.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class DrawTextOp extends ImageOperation {
    private String text;
    private double x;
    private double y;

    @Override
    public void perform(Point2D.Double origin, Graphics2D graphics) {
        graphics.drawString(text, (float) (x - origin.getX()),
                (float) (y - origin.getY()));
    }

    public DrawTextOp(String text, double x, double y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }
}

/**
 * This package will soon be moved to a different location.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DebugImage implements DebugElement {
    public enum Alignment {
        LEFT, CENTER, RIGHT
    };

    private static Font defaultFont;
    private Rectangle2D boundingRectangle;
    private LinkedList<ImageOperation> ops;
    private DebugSession session;
    private int grid;
    private int border;
    private FontRenderContext fontRenderContext;
    private Font currentFont;

    /*
     * @seeorg.graffiti.plugins.algorithms.reingoldtilford.debug.DebugElement#
     * writeToDocument(com.lowagie.text.Document)
     */
    public void writeToDocument(Document document) throws DocumentException {
        int width = (int) Math.ceil(boundingRectangle.getWidth() + 2 * border);
        int height = (int) Math
                .ceil(boundingRectangle.getHeight() + 2 * border);
        if (width == 0 || height == 0)
            return;
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, width, height);
        drawGrid(graphics, width, height);
        graphics.setColor(Color.BLACK);
        graphics.setFont(defaultFont);
        Point2D.Double origin = new Point2D.Double(boundingRectangle.getX()
                - border, boundingRectangle.getY() - border);
        for (ImageOperation op : ops) {
            op.perform(origin, graphics);
        }
        graphics.dispose();
        try {
            Image img = Image.getInstance(image, null);
            if (width > 500 || height > 700) {
                img.scaleToFit(500.0f, 700.0f);
            }
            document.add(img);
        } catch (IOException e) {
            throw new DocumentException();
        }
    }

    DebugImage(DebugSession session) {
        ops = new LinkedList<ImageOperation>();
        this.session = session;
        grid = 0;
        border = 30;
        Graphics2D graphics = (new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB)).createGraphics();
        fontRenderContext = graphics.getFontRenderContext();
        graphics.dispose();
        if (defaultFont == null) {
            defaultFont = new Font("Arial", Font.PLAIN, 10);
        }
        currentFont = defaultFont;
    }

    private void drawGrid(Graphics2D graphics, int width, int height) {
        if (grid == 0)
            return;
        Color prevColor = graphics.getColor();
        Stroke prevStroke = graphics.getStroke();
        Font prevFont = graphics.getFont();
        graphics.setColor(Color.GRAY);
        graphics.setStroke(new BasicStroke(0.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1.0f, new float[] { 1, 1 }, 0.0f));
        graphics.setFont(defaultFont);
        int x = (int) Math.ceil(boundingRectangle.getX() / grid) * grid;
        while (x <= boundingRectangle.getX() + boundingRectangle.getWidth()) {
            graphics.drawLine((int) (x - boundingRectangle.getX() + border),
                    border, (int) (x - boundingRectangle.getX() + border),
                    height - border);
            Rectangle2D rect = defaultFont.getStringBounds(x + "", graphics
                    .getFontRenderContext());
            graphics.drawString(x + "", (int) (x - boundingRectangle.getX()
                    + border - rect.getWidth() / 2.0f), (int) (border - 3.0f));
            x += grid;
        }
        int y = (int) Math.ceil(boundingRectangle.getY() / grid) * grid;
        while (y <= boundingRectangle.getY() + boundingRectangle.getHeight()) {
            graphics.drawLine(border,
                    (int) (y - boundingRectangle.getY() + border), width
                            - border,
                    (int) (y - boundingRectangle.getY() + border));
            Rectangle2D rect = defaultFont.getStringBounds(y + "", graphics
                    .getFontRenderContext());
            graphics.drawString(y + "",
                    (float) (border - rect.getWidth() - 3.0f), (float) (y
                            - boundingRectangle.getY() + border
                            + rect.getHeight() / 2.0f - 2.0f));
            y += grid;
        }
        graphics.setColor(prevColor);
        graphics.setStroke(prevStroke);
        graphics.setFont(prevFont);
    }

    public void setColor(Color color) {
        ops.addLast(new SetColorOp(color));
    }

    private void updateBoundingRectangle(double x, double y, double width,
            double height) {
        if (boundingRectangle == null) {
            boundingRectangle = new Rectangle2D.Double(x, y, width, height);
        } else {
            boundingRectangle = boundingRectangle
                    .createUnion(new Rectangle2D.Double(x, y, width, height));
        }
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        ops.addLast(new LineOp(x1, y1, x2, y2));
        updateBoundingRectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2
                - x1), Math.abs(y2 - y1));
    }

    public void drawRect(double x, double y, double width, double height) {
        ops.addLast(new RectOp(x, y, width, height, RectOp.Shape.RECTANGLE));
        updateBoundingRectangle(x, y, width, height);
    }

    public void fillRect(double x, double y, double width, double height) {
        ops.addLast(new RectOp(x, y, width, height,
                RectOp.Shape.FILLEDRECTANGLE));
        updateBoundingRectangle(x, y, width, height);
    }

    public void drawOval(double x, double y, double width, double height) {
        ops.addLast(new RectOp(x, y, width, height, RectOp.Shape.OVAL));
        updateBoundingRectangle(x, y, width, height);
    }

    public void fillOval(double x, double y, double width, double height) {
        ops.addLast(new RectOp(x, y, width, height, RectOp.Shape.FILLEDOVAL));
        updateBoundingRectangle(x, y, width, height);
    }

    public void drawText(String text, double x, double y, Alignment alignment) {
        if (text == null) {
            text = "null";
        }
        Rectangle2D rect = currentFont.getStringBounds(text, fontRenderContext);
        double rx = 0;
        switch (alignment) {
        case LEFT:
            rx = x;
            break;
        case CENTER:
            rx = x - rect.getWidth() / 2.0;
            break;
        case RIGHT:
            rx = x - rect.getWidth();
            break;
        }
        ops.addLast(new DrawTextOp(text, rx, y));
        updateBoundingRectangle(rx, y - rect.getHeight(), rect.getWidth(), rect
                .getHeight());
    }

    public void drawText(String text, double x, double y) {
        drawText(text, x, y, Alignment.LEFT);
    }

    public void setFont(Font font) {
        ops.addLast(new SetFontOp(font));
        currentFont = font;
    }

    public void close() {
        session.addDebugImage(this);
    }

    public void setGrid(int grid) {
        this.grid = grid;
    }

    public void setBorder(int border) {
        this.border = border;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
