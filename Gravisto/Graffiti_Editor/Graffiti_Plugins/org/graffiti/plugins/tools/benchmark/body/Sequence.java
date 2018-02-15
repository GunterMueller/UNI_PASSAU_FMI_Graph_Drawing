// =============================================================================
//
//   Sequence.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.body;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.Data;
import org.graffiti.plugins.tools.benchmark.Seedable;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Sequence implements Seedable, BodyElement {
    private Long fixedSeed;
    private long actualSeed;
    private List<BodyElement> elements;

    public Sequence() {
        elements = new LinkedList<BodyElement>();
    }

    public void addElement(BodyElement element) {
        elements.add(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFixedSeed(long fixedSeed) {
        this.fixedSeed = fixedSeed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNext(BodyElement nextElement) throws BenchmarkException {
        if (elements.isEmpty())
            throw new BenchmarkException("error.emptySequence");
        Iterator<BodyElement> iter = elements.iterator();
        BodyElement prevElement = iter.next();
        while (iter.hasNext()) {
            BodyElement element = iter.next();
            prevElement.setNext(element);
            prevElement = element;
        }
        prevElement.setNext(nextElement);
    }

    @Override
    public void updateSeed(long seed) {
        actualSeed = fixedSeed == null ? seed : fixedSeed;
        Random random = new Random(actualSeed);
        for (BodyElement element : elements) {
            element.updateSeed(random.nextLong());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Data data, Assignment assignment)
            throws BenchmarkException {
        if (elements.isEmpty())
            throw new BenchmarkException("error.emptySequence");
        elements.iterator().next().execute(data, assignment);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
