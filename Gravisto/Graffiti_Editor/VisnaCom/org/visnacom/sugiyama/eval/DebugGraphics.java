/*==============================================================================
*
*   DebugGraphics.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: DebugGraphics.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.eval;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * is only used for debug. all calls are delegated to another Graphics2D object
 */
public class DebugGraphics extends Graphics2D {
    //~ Instance fields ========================================================

    private Graphics2D delegate;

    //~ Constructors ===========================================================

    /**
     * Creates a new DebugGraphics object.
     */
    public DebugGraphics() {
        super();
    }

    /**
     * Creates a new DebugGraphics object.
     *
     * @param g2 DOCUMENT ME!
     */
    public DebugGraphics(Graphics2D g2) {
        delegate = g2;
    }

    //~ Methods ================================================================

    /**
     * @see java.awt.Graphics2D#setBackground(java.awt.Color)
     */
    public void setBackground(Color arg0) {
        delegate.setBackground(arg0);
    }

    /**
     * @see java.awt.Graphics2D#getBackground()
     */
    public Color getBackground() {
        return delegate.getBackground();
    }

    /**
     * @see java.awt.Graphics#setClip(int, int, int, int)
     */
    public void setClip(int arg0, int arg1, int arg2, int arg3) {
        delegate.setClip(arg0, arg1, arg2, arg3);
    }

    /**
     * @see java.awt.Graphics#setClip(java.awt.Shape)
     */
    public void setClip(Shape arg0) {
        delegate.setClip(arg0);
    }

    /**
     * @see java.awt.Graphics#getClip()
     */
    public Shape getClip() {
        return delegate.getClip();
    }

    /**
     * @see java.awt.Graphics#getClipBounds()
     */
    public Rectangle getClipBounds() {
        return delegate.getClipBounds();
    }

    /**
     * @see java.awt.Graphics#setColor(java.awt.Color)
     */
    public void setColor(Color arg0) {
        delegate.setColor(arg0);
    }

    /**
     * @see java.awt.Graphics#getColor()
     */
    public Color getColor() {
        return delegate.getColor();
    }

    /**
     * @see java.awt.Graphics2D#setComposite(java.awt.Composite)
     */
    public void setComposite(Composite arg0) {
        delegate.setComposite(arg0);
    }

    /**
     * @see java.awt.Graphics2D#getComposite()
     */
    public Composite getComposite() {
        return delegate.getComposite();
    }

    /**
     * @see java.awt.Graphics2D#getDeviceConfiguration()
     */
    public GraphicsConfiguration getDeviceConfiguration() {
        return delegate.getDeviceConfiguration();
    }

    /**
     * @see java.awt.Graphics#setFont(java.awt.Font)
     */
    public void setFont(Font arg0) {
        delegate.setFont(arg0);
    }

    /**
     * @see java.awt.Graphics#getFont()
     */
    public Font getFont() {
        return delegate.getFont();
    }

    /**
     * @see java.awt.Graphics#getFontMetrics(java.awt.Font)
     */
    public FontMetrics getFontMetrics(Font arg0) {
        return delegate.getFontMetrics(arg0);
    }

    /**
     * @see java.awt.Graphics2D#getFontRenderContext()
     */
    public FontRenderContext getFontRenderContext() {
        return delegate.getFontRenderContext();
    }

    /**
     * @see java.awt.Graphics2D#setPaint(java.awt.Paint)
     */
    public void setPaint(Paint arg0) {
        delegate.setPaint(arg0);
    }

    /**
     * @see java.awt.Graphics2D#getPaint()
     */
    public Paint getPaint() {
        return delegate.getPaint();
    }

    /**
     * @see java.awt.Graphics#setPaintMode()
     */
    public void setPaintMode() {
        delegate.setPaintMode();
    }

    /**
     * @see java.awt.Graphics2D#setRenderingHint(java.awt.RenderingHints.Key,
     *      java.lang.Object)
     */
    public void setRenderingHint(Key arg0, Object arg1) {
        delegate.setRenderingHint(arg0, arg1);
    }

    /**
     * @see java.awt.Graphics2D#getRenderingHint(java.awt.RenderingHints.Key)
     */
    public Object getRenderingHint(Key arg0) {
        return delegate.getRenderingHint(arg0);
    }

    /**
     * @see java.awt.Graphics2D#setRenderingHints(java.util.Map)
     */
    public void setRenderingHints(Map arg0) {
        delegate.setRenderingHints(arg0);
    }

    /**
     * @see java.awt.Graphics2D#getRenderingHints()
     */
    public RenderingHints getRenderingHints() {
        return delegate.getRenderingHints();
    }

    /**
     * @see java.awt.Graphics2D#setStroke(java.awt.Stroke)
     */
    public void setStroke(Stroke arg0) {
        delegate.setStroke(arg0);
    }

    /**
     * @see java.awt.Graphics2D#getStroke()
     */
    public Stroke getStroke() {
        return delegate.getStroke();
    }

    /**
     * @see java.awt.Graphics2D#setTransform(java.awt.geom.AffineTransform)
     */
    public void setTransform(AffineTransform arg0) {
        delegate.setTransform(arg0);
    }

    /**
     * @see java.awt.Graphics2D#getTransform()
     */
    public AffineTransform getTransform() {
        return delegate.getTransform();
    }

    /**
     * @see java.awt.Graphics#setXORMode(java.awt.Color)
     */
    public void setXORMode(Color arg0) {
        delegate.setXORMode(arg0);
    }

    /**
     * @see java.awt.Graphics2D#addRenderingHints(java.util.Map)
     */
    public void addRenderingHints(Map arg0) {
        delegate.addRenderingHints(arg0);
    }

    /**
     * @see java.awt.Graphics#clearRect(int, int, int, int)
     */
    public void clearRect(int arg0, int arg1, int arg2, int arg3) {
        delegate.clearRect(arg0, arg1, arg2, arg3);
    }

    /**
     * @see java.awt.Graphics2D#clip(java.awt.Shape)
     */
    public void clip(Shape arg0) {
        delegate.clip(arg0);
    }

    /**
     * @see java.awt.Graphics#clipRect(int, int, int, int)
     */
    public void clipRect(int arg0, int arg1, int arg2, int arg3) {
        delegate.clipRect(arg0, arg1, arg2, arg3);
    }

    /**
     * @see java.awt.Graphics#copyArea(int, int, int, int, int, int)
     */
    public void copyArea(int arg0, int arg1, int arg2, int arg3, int arg4,
        int arg5) {
        delegate.copyArea(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * @see java.awt.Graphics#create()
     */
    public Graphics create() {
        return delegate.create();
    }

    /**
     * @see java.awt.Graphics#dispose()
     */
    public void dispose() {
        delegate.dispose();
    }

    /**
     * @see java.awt.Graphics2D#draw(java.awt.Shape)
     */
    public void draw(Shape arg0) {
        delegate.draw(arg0);
    }

    /**
     * @see java.awt.Graphics#drawArc(int, int, int, int, int, int)
     */
    public void drawArc(int arg0, int arg1, int arg2, int arg3, int arg4,
        int arg5) {
        delegate.drawArc(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * @see java.awt.Graphics2D#drawGlyphVector(java.awt.font.GlyphVector,
     *      float, float)
     */
    public void drawGlyphVector(GlyphVector arg0, float arg1, float arg2) {
        delegate.drawGlyphVector(arg0, arg1, arg2);
    }

    /**
     * @see java.awt.Graphics2D#drawImage(java.awt.image.BufferedImage,
     *      java.awt.image.BufferedImageOp, int, int)
     */
    public void drawImage(BufferedImage arg0, BufferedImageOp arg1, int arg2,
        int arg3) {
        delegate.drawImage(arg0, arg1, arg2, arg3);
    }

    /**
     * @see java.awt.Graphics2D#drawImage(java.awt.Image,
     *      java.awt.geom.AffineTransform, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image arg0, AffineTransform arg1,
        ImageObserver arg2) {
        return delegate.drawImage(arg0, arg1, arg2);
    }

    /**
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int,
     *      java.awt.Color, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image arg0, int arg1, int arg2, Color arg3,
        ImageObserver arg4) {
        return delegate.drawImage(arg0, arg1, arg2, arg3, arg4);
    }

    /**
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int,
     *      java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image arg0, int arg1, int arg2, ImageObserver arg3) {
        return delegate.drawImage(arg0, arg1, arg2, arg3);
    }

    /**
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int,
     *      java.awt.Color, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
        int arg4, Color arg5, ImageObserver arg6) {
        return delegate.drawImage(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    /**
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int,
     *      java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
        int arg4, ImageObserver arg5) {
        return delegate.drawImage(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int,
     *      int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
        int arg4, int arg5, int arg6, int arg7, int arg8, Color arg9,
        ImageObserver arg10) {
        return delegate.drawImage(arg0, arg1, arg2, arg3, arg4, arg5, arg6,
            arg7, arg8, arg9, arg10);
    }

    /**
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int,
     *      int, int, int, int, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
        int arg4, int arg5, int arg6, int arg7, int arg8, ImageObserver arg9) {
        return delegate.drawImage(arg0, arg1, arg2, arg3, arg4, arg5, arg6,
            arg7, arg8, arg9);
    }

    /**
     * @see java.awt.Graphics#drawLine(int, int, int, int)
     */
    public void drawLine(int arg0, int arg1, int arg2, int arg3) {
        System.out.println("drawline " + arg0 + "," + arg1 + "," + arg2 + ","
            + arg3);
        delegate.drawLine(arg0, arg1, arg2, arg3);
    }

    /**
     * @see java.awt.Graphics#drawOval(int, int, int, int)
     */
    public void drawOval(int arg0, int arg1, int arg2, int arg3) {
        delegate.drawOval(arg0, arg1, arg2, arg3);
    }

    /**
     * @see java.awt.Graphics#drawPolygon(int[], int[], int)
     */
    public void drawPolygon(int[] arg0, int[] arg1, int arg2) {
        delegate.drawPolygon(arg0, arg1, arg2);
    }

    /**
     * @see java.awt.Graphics#drawPolyline(int[], int[], int)
     */
    public void drawPolyline(int[] arg0, int[] arg1, int arg2) {
        delegate.drawPolyline(arg0, arg1, arg2);
    }

    /**
     * @see java.awt.Graphics2D#drawRenderableImage(java.awt.image.renderable.RenderableImage,
     *      java.awt.geom.AffineTransform)
     */
    public void drawRenderableImage(RenderableImage arg0, AffineTransform arg1) {
        delegate.drawRenderableImage(arg0, arg1);
    }

    /**
     * @see java.awt.Graphics2D#drawRenderedImage(java.awt.image.RenderedImage,
     *      java.awt.geom.AffineTransform)
     */
    public void drawRenderedImage(RenderedImage arg0, AffineTransform arg1) {
        delegate.drawRenderedImage(arg0, arg1);
    }

    /**
     * @see java.awt.Graphics#drawRoundRect(int, int, int, int, int, int)
     */
    public void drawRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4,
        int arg5) {
        delegate.drawRoundRect(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * @see java.awt.Graphics2D#drawString(java.text.AttributedCharacterIterator,
     *      float, float)
     */
    public void drawString(AttributedCharacterIterator arg0, float arg1,
        float arg2) {
        delegate.drawString(arg0, arg1, arg2);
    }

    /**
     * @see java.awt.Graphics#drawString(java.text.AttributedCharacterIterator,
     *      int, int)
     */
    public void drawString(AttributedCharacterIterator arg0, int arg1, int arg2) {
        delegate.drawString(arg0, arg1, arg2);
    }

    /**
     * @see java.awt.Graphics2D#drawString(java.lang.String, float, float)
     */
    public void drawString(String arg0, float arg1, float arg2) {
        delegate.drawString(arg0, arg1, arg2);
    }

    /**
     * @see java.awt.Graphics#drawString(java.lang.String, int, int)
     */
    public void drawString(String arg0, int arg1, int arg2) {
        delegate.drawString(arg0, arg1, arg2);
    }

    /**
     * @see java.awt.Graphics2D#fill(java.awt.Shape)
     */
    public void fill(Shape arg0) {
        delegate.fill(arg0);
    }

    /**
     * @see java.awt.Graphics#fillArc(int, int, int, int, int, int)
     */
    public void fillArc(int arg0, int arg1, int arg2, int arg3, int arg4,
        int arg5) {
        delegate.fillArc(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * @see java.awt.Graphics#fillOval(int, int, int, int)
     */
    public void fillOval(int arg0, int arg1, int arg2, int arg3) {
        delegate.fillOval(arg0, arg1, arg2, arg3);
    }

    /**
     * @see java.awt.Graphics#fillPolygon(int[], int[], int)
     */
    public void fillPolygon(int[] arg0, int[] arg1, int arg2) {
        delegate.fillPolygon(arg0, arg1, arg2);
    }

    /**
     * @see java.awt.Graphics#fillRect(int, int, int, int)
     */
    public void fillRect(int arg0, int arg1, int arg2, int arg3) {
        delegate.fillRect(arg0, arg1, arg2, arg3);
    }

    /**
     * @see java.awt.Graphics#fillRoundRect(int, int, int, int, int, int)
     */
    public void fillRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4,
        int arg5) {
        delegate.fillRoundRect(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * @see java.awt.Graphics2D#hit(java.awt.Rectangle, java.awt.Shape,
     *      boolean)
     */
    public boolean hit(Rectangle arg0, Shape arg1, boolean arg2) {
        return delegate.hit(arg0, arg1, arg2);
    }

    /**
     * @see java.awt.Graphics2D#rotate(double, double, double)
     */
    public void rotate(double arg0, double arg1, double arg2) {
        delegate.rotate(arg0, arg1, arg2);
    }

    /**
     * @see java.awt.Graphics2D#rotate(double)
     */
    public void rotate(double arg0) {
        delegate.rotate(arg0);
    }

    /**
     * @see java.awt.Graphics2D#scale(double, double)
     */
    public void scale(double arg0, double arg1) {
        delegate.scale(arg0, arg1);
    }

    /**
     * @see java.awt.Graphics2D#shear(double, double)
     */
    public void shear(double arg0, double arg1) {
        delegate.shear(arg0, arg1);
    }

    /**
     * @see java.awt.Graphics2D#transform(java.awt.geom.AffineTransform)
     */
    public void transform(AffineTransform arg0) {
        delegate.transform(arg0);
    }

    /**
     * @see java.awt.Graphics2D#translate(double, double)
     */
    public void translate(double arg0, double arg1) {
        delegate.translate(arg0, arg1);
    }

    /**
     * @see java.awt.Graphics#translate(int, int)
     */
    public void translate(int arg0, int arg1) {
        delegate.translate(arg0, arg1);
    }
}
