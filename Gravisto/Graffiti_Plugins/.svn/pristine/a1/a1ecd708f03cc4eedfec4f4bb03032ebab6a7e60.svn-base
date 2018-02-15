// =============================================================================
//
//   PrimitiveAttributeFactoryFactoryManager.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util.attributes;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.ByteAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.FloatAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.LongAttribute;
import org.graffiti.attributes.ShortAttribute;
import org.graffiti.attributes.StringAttribute;

/**
 * Factory class to create default appropriate {@code Attribute}s to hold values
 * of the primitive types that are represented by the {@code Class} objects
 * passed to {@link #createAttribute(String, Class)} or
 * {@link #createAttribute(String, Class, org.graffiti.util.Callback, org.graffiti.util.VoidCallback)}
 * . E.g., a new {@link BooleanAttribute} is created if {@code Boolean.class} or
 * {@code boolean.class} is passed. One may optionally specify callbacks to be
 * called by the setter methods of the created {@link Attribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class PrimitiveAttributeFactoryFactoryManager extends
        AttributeFactoryFactoryManager {
    /**
     * {@code AttributeFactoryFactory} to create the {@code AttributeFactory} to
     * create {@code BooleanAttribute}s.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    public static class BooleanAttributeFactoryFactory implements
            AttributeFactoryFactory {
        /**
         * {@inheritDoc}
         */
        public AttributeFactory createAttributeFactory() {
            return new AttributeFactory() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected Attribute createAttribute(String id) {
                    return new BooleanAttribute(id) {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        protected void doSetValue(Object o)
                                throws IllegalArgumentException {
                            if (preCallback(o)) {
                                super.doSetValue(o);
                            }
                            postCallback(o);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void setBoolean(boolean value) {
                            if (preCallback(value)) {
                                super.setBoolean(value);
                            }
                            postCallback(value);
                        }
                    };
                }
            };
        }
    }

    /**
     * {@code AttributeFactoryFactory} to create the {@code AttributeFactory} to
     * create {@code ByteAttribute}s.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    public static class ByteAttributeFactoryFactory implements
            AttributeFactoryFactory {
        /**
         * {@inheritDoc}
         */
        public AttributeFactory createAttributeFactory() {
            return new AttributeFactory() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected Attribute createAttribute(String id) {
                    return new ByteAttribute(id) {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        protected void doSetValue(Object o)
                                throws IllegalArgumentException {
                            if (preCallback(o)) {
                                super.doSetValue(o);
                            }
                            postCallback(o);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void setByte(byte value) {
                            if (preCallback(value)) {
                                super.setByte(value);
                            }
                            postCallback(value);
                        }
                    };
                }
            };
        }
    }

    /**
     * {@code AttributeFactoryFactory} to create the {@code AttributeFactory} to
     * create {@code DoubleAttribute}s.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    public static class DoubleAttributeFactoryFactory implements
            AttributeFactoryFactory {
        /**
         * {@inheritDoc}
         */
        public AttributeFactory createAttributeFactory() {
            return new AttributeFactory() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected Attribute createAttribute(String id) {
                    return new DoubleAttribute(id) {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        protected void doSetValue(Object o)
                                throws IllegalArgumentException {
                            if (preCallback(o)) {
                                super.doSetValue(o);
                            }
                            postCallback(o);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void setDouble(double value) {
                            if (preCallback(value)) {
                                super.setDouble(value);
                            }
                            postCallback(value);
                        }
                    };
                }
            };
        }
    }

    /**
     * {@code AttributeFactoryFactory} to create the {@code AttributeFactory} to
     * create {@code FloatAttribute}s.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    public static class FloatAttributeFactoryFactory implements
            AttributeFactoryFactory {
        /**
         * {@inheritDoc}
         */
        public AttributeFactory createAttributeFactory() {
            return new AttributeFactory() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected Attribute createAttribute(String id) {
                    return new FloatAttribute(id) {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        protected void doSetValue(Object o)
                                throws IllegalArgumentException {
                            if (preCallback(o)) {
                                super.doSetValue(o);
                            }
                            postCallback(o);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void setFloat(float value) {
                            if (preCallback(value)) {
                                super.setFloat(value);
                            }
                            postCallback(value);
                        }
                    };
                }
            };
        }
    }

    /**
     * {@code AttributeFactoryFactory} to create the {@code AttributeFactory} to
     * create {@code IntegerAttribute}s.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    public static class IntegerAttributeFactoryFactory implements
            AttributeFactoryFactory {
        /**
         * {@inheritDoc}
         */
        public AttributeFactory createAttributeFactory() {
            return new AttributeFactory() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected Attribute createAttribute(String id) {
                    return new IntegerAttribute(id) {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        protected void doSetValue(Object o)
                                throws IllegalArgumentException {
                            if (preCallback(o)) {
                                super.doSetValue(o);
                            }
                            postCallback(o);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void setInteger(int value) {
                            if (preCallback(value)) {
                                super.setInteger(value);
                            }
                            postCallback(value);
                        }
                    };
                }
            };
        }
    }

    /**
     * {@code AttributeFactoryFactory} to create the {@code AttributeFactory} to
     * create {@code LongAttribute}s.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    public static class LongAttributeFactoryFactory implements
            AttributeFactoryFactory {
        /**
         * {@inheritDoc}
         */
        public AttributeFactory createAttributeFactory() {
            return new AttributeFactory() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected Attribute createAttribute(String id) {
                    return new LongAttribute(id) {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        protected void doSetValue(Object o)
                                throws IllegalArgumentException {
                            if (preCallback(o)) {
                                super.doSetValue(o);
                            }
                            postCallback(o);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void setLong(long value) {
                            if (preCallback(value)) {
                                super.setLong(value);
                            }
                            postCallback(value);
                        }
                    };
                }
            };
        }
    }

    /**
     * {@code AttributeFactoryFactory} to create the {@code AttributeFactory} to
     * create {@code ShortAttribute}s.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    public static class ShortAttributeFactoryFactory implements
            AttributeFactoryFactory {
        /**
         * {@inheritDoc}
         */
        public AttributeFactory createAttributeFactory() {
            return new AttributeFactory() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected Attribute createAttribute(String id) {
                    return new ShortAttribute(id) {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        protected void doSetValue(Object o)
                                throws IllegalArgumentException {
                            if (preCallback(o)) {
                                super.doSetValue(o);
                            }
                            postCallback(o);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void setShort(short value) {
                            if (preCallback(value)) {
                                super.setShort(value);
                            }
                            postCallback(value);
                        }
                    };
                }
            };
        }
    }

    /**
     * {@code AttributeFactoryFactory} to create the {@code AttributeFactory} to
     * create {@code StringAttribute}s.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    public static class StringAttributeFactoryFactory implements
            AttributeFactoryFactory {
        /**
         * {@inheritDoc}
         */
        public AttributeFactory createAttributeFactory() {
            return new AttributeFactory() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected Attribute createAttribute(String id) {
                    return new StringAttribute(id) {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        protected void doSetValue(Object o)
                                throws IllegalArgumentException {
                            if (preCallback(o)) {
                                super.doSetValue(o);
                            }
                            postCallback(o);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void setString(String value) {
                            if (preCallback(value)) {
                                super.setString(value);
                            }
                            postCallback(value);
                        }
                    };
                }
            };
        }
    }

    /**
     * Constructs a {@code PrimitiveAttributeFactoryFactoryManager}.
     */
    public PrimitiveAttributeFactoryFactoryManager() {
        add(new BooleanAttributeFactoryFactory(), Boolean.class, boolean.class);
        add(new ByteAttributeFactoryFactory(), Byte.class, byte.class);
        add(new DoubleAttributeFactoryFactory(), Double.class, double.class);
        add(new FloatAttributeFactoryFactory(), Float.class, float.class);
        add(new IntegerAttributeFactoryFactory(), Integer.class, int.class);
        add(new LongAttributeFactoryFactory(), Long.class, long.class);
        add(new ShortAttributeFactoryFactory(), Short.class, short.class);
        add(new StringAttributeFactoryFactory(), String.class);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
