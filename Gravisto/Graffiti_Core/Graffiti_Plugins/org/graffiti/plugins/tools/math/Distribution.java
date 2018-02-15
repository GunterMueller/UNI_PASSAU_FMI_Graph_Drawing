package org.graffiti.plugins.tools.math;

import java.util.Random;

/**
 * Probability distribution over {@code T}.
 * 
 * @author Andreas Glei&szlig;ner
 */
public interface Distribution<T> {
    /**
     * Samples an element of {@code T} using the specified source of randomness.
     * 
     * @param random
     *            the source of randomness.
     * @return an element of {@code T} using the specified source of randomness.
     */
    T sample(Random random);
}
