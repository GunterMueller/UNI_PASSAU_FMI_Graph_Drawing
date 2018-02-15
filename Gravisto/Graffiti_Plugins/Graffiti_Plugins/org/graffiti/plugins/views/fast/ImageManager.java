// =============================================================================
//
//   ImageManager.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.imageio.ImageIO;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graphics.RenderedImageAttribute;
import org.graffiti.plugins.views.fast.label.Label;
import org.graffiti.plugins.views.fast.label.LabelCommand;
import org.graffiti.plugins.views.fast.label.LabelManager;
import org.graffiti.util.Pair;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class ImageManager<L extends Label<L, LC>, LC extends LabelCommand> {
    private Map<Attributable, LinkedList<Pair<String, FastImage<L, LC>>>> images;
    private FastImage<L, LC> errorImage;
    private LabelManager<L, LC> labelManager;

    public ImageManager(LabelManager<L, LC> labelManager) {
        this.labelManager = labelManager;
        images = new HashMap<Attributable, LinkedList<Pair<String, FastImage<L, LC>>>>();
        BufferedImage image = new BufferedImage(10, 10,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setBackground(Color.GRAY);
        g.fillRect(0, 0, 10, 10);
        g.setStroke(new BasicStroke(3.0f));
        g.setColor(Color.RED);
        g.drawLine(0, 0, 10, 10);
        g.drawLine(10, 0, 0, 10);
        g.dispose();
        errorImage = createImage(image);
    }

    public FastImage<L, LC> getImage(Attributable attributable, String path,
            L label, boolean fromAttributeSystem) {
        LinkedList<Pair<String, FastImage<L, LC>>> list = images
                .get(attributable);
        if (list == null) {
            list = new LinkedList<Pair<String, FastImage<L, LC>>>();
            images.put(attributable, list);
        }
        FastImage<L, LC> image = null;
        for (Pair<String, FastImage<L, LC>> pair : list) {
            if (pair.getFirst().equals(path)) {
                image = pair.getSecond();
                break;
            }
        }
        if (image == null) {
            try {
                if (fromAttributeSystem) {
                    Attribute attribute = attributable.getAttribute(path);
                    if (!(attribute instanceof RenderedImageAttribute))
                        return errorImage;
                    image = createImage(((RenderedImageAttribute) attribute)
                            .getImage());
                    list.addLast(Pair.create(path, image));
                } else {
                    return createImage(ImageIO.read(new URL(path).openStream()));
                }
            } catch (AttributeNotFoundException e) {
                return errorImage;
            } catch (MalformedURLException e) {
                return errorImage;
            } catch (IOException e) {
                return errorImage;
            }
        }
        image.addDependentLabel(label);
        return image;
    }

    // path == null <=> drop all
    public void dropImage(Attributable attributable, String path) {
        LinkedList<Pair<String, FastImage<L, LC>>> list = images
                .get(attributable);
        if (list != null) {
            dropImage(list, path);
            if (list.isEmpty()) {
                images.remove(attributable);
            }
        }
    }

    private void dropImage(LinkedList<Pair<String, FastImage<L, LC>>> list,
            String path) {
        Iterator<Pair<String, FastImage<L, LC>>> iter = list.iterator();
        while (iter.hasNext()) {
            Pair<String, FastImage<L, LC>> pair = iter.next();
            if (path == null || pair.getFirst().equals(path)) {
                pair.getSecond().dispose(labelManager);
                iter.remove();
                if (path != null)
                    return;
            }
        }
    }

    public void reset() {
        for (Map.Entry<Attributable, LinkedList<Pair<String, FastImage<L, LC>>>> entry : images
                .entrySet()) {
            dropImage(entry.getValue(), null);
        }
        images.clear();
    }

    public FastImage<L, LC> getErrorImage() {
        return errorImage;
    }

    protected abstract FastImage<L, LC> createImage(BufferedImage image);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
