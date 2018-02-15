package org.graffiti.plugins.editcomponents.yagi;

import javax.swing.JComponent;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;

/**
 * This class provides an edit component for {@code CollectionAttribute}s. The
 * components for subattributes are laid out in rows. The behavior is customized
 * by passing a {@code CollectionEditWorker} to the constructor.
 * 
 * @author Andreas Glei&szlig;ner
 * @see CollectionAttribute
 * @see CollectionEditWorker
 */
public class CollectionEditComponent extends AbstractValueEditComponent
        implements SelfLabelingComponent {
    /**
     * Casts the specified {@code displayable} to {@code CollectionAttribute} or
     * throws an {@code IllegalArgumentException} if the cast is impossible.
     * 
     * @param displayable
     *            the {@code Displayable} to cast.
     * @return the specified {@code displayable} cast to {@code
     *         CollectionAttribute}.
     * @throws IllegalArgumentException
     *             if {@code displayable} is not a {@code CollectionAttribute}.
     */
    protected static CollectionAttribute convert(Displayable<?> displayable) {
        if (!(displayable instanceof CollectionAttribute))
            throw new IllegalArgumentException(
                    "Displayable must be HashMapAttribute, but is "
                            + displayable.getClass());
        return (CollectionAttribute) displayable;
    }

    /**
     * The {@code CollectionEditWorker}, which does the actual job.
     */
    private CollectionEditWorker worker;

    /**
     * Constructs a {@code CollectionEditComponent} for the specified attribute.
     * This uses a default {@link CollectionEditWorker}.
     * 
     * @param attribute
     *            the attribute to edit.
     */
    public CollectionEditComponent(CollectionAttribute attribute) {
        this(attribute, new CollectionEditWorker());
    }

    /**
     * Constructs a {@code CollectionEditComponent} for the specified
     * displayable, which must be a {@link CollectionAttribute}.
     * 
     * @param displayable
     *            the {@code CollectionAttribute} to edit.
     * @throws IllegalArgumentException
     *             if {@code displayable} cannot be cast to {@code
     *             CollectionAttribute}.
     */
    public CollectionEditComponent(Displayable<?> displayable) {
        this(convert(displayable));
    }

    /**
     * Constructs a {@code CollectionEditComponent} for the specified attribute
     * and with customized behavior defined by the specified {@code
     * CollectionEditWorker}.
     * 
     * @param attribute
     *            the attribute to edit.
     * @param worker
     *            the {@code CollectionEditWorker} to define customized
     *            behavior.
     */
    public CollectionEditComponent(CollectionAttribute attribute,
            CollectionEditWorker worker) {
        super(attribute);
        this.worker = worker;
        worker.setValueEditComponent(this);
        worker.build(attribute);
    }

    /**
     * Constructs a {@code CollectionEditComponent} for the specified attribute
     * and with customized behavior defined by the specified {@code
     * CollectionEditWorker}.
     * 
     * @param attributes
     *            the attributes to edit.
     * @param worker
     *            the {@code CollectionEditWorker} to define customized
     *            behavior.
     */
    public CollectionEditComponent(CollectionAttribute[] attributes,
            CollectionEditWorker worker) {
        super(attributes);
        this.worker = worker;
        worker.setValueEditComponent(this);
        worker.build(attributes);
    }

    @Override
    public void setDisplayables(Displayable<?>[] disps) {
        super.setDisplayables(disps);

        CollectionAttribute[] attributes = new CollectionAttribute[disps.length];
        for (int i = 0; i < disps.length; i++) {
            attributes[i] = convert(disps[i]);
        }

        this.worker.build(attributes);
    }

    @Override
    public void setDisplayable(Displayable<?> disp) {
        super.setDisplayable(disp);

        this.worker.build(convert(disp));
    }

    /**
     * {@inheritDoc}
     */
    public final JComponent getComponent() {
        return worker.getComponent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void setDispEditFieldValue() {
        // worker.setEditFieldValue(this.showEmpty);
        worker.setEditFieldValue(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void setDispValue() {
        worker.setValue();
    }

    /**
     * {@inheritDoc} This implementation delegates the decision to its {@code
     * CollectionEditWorker}.
     * 
     * @see CollectionEditWorker
     */
    public final boolean isSelfLabeling() {
        return worker.isSelfLabeling();
    }

    /**
     * Sets the current value of the <code>Displayable</code> in the
     * corresponding <code>JComponent</code> for the child whose path matches
     * <code>childPath</code>.
     * 
     * @param childPath
     *            the child attribute's path
     */
    public void setEditFieldValue(String childPath) {
        worker.setEditFieldValue(childPath);
    }
}