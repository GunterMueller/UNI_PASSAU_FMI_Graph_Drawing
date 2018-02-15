package org.graffiti.plugins.editcomponents.yagi.grid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.graffiti.core.Bundle;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.graphics.grid.GridClassAttribute;
import org.graffiti.graphics.grid.GridParametersAttribute;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugins.editcomponents.yagi.CollectionEditWorker;
import org.graffiti.plugins.editcomponents.yagi.SelfLabelingComponent;
import org.graffiti.plugins.editcomponents.yagi.ValueEditComponentFactory;
import org.graffiti.plugins.grids.GridRegistry;
import org.graffiti.plugins.grids.GridRegistryListener;
import org.graffiti.util.Pair;

/**
 * Component for editing grids.
 * 
 * @author Kathrin Hanauer
 * @author Andreas Glei&szlig;ner
 */
public class GridEditComponent extends JPanel implements GridRegistryListener,
        SingletonComponent<GridAttribute> {
    /**
     * 
     */
    private static final long serialVersionUID = 3922950908700142436L;

    /**
     * Resource bundle used to obtain user readable labels.
     */
    private static Bundle resourceBundle;

    /**
     * Value editing component, which provides a component for editing grids.
     */
    public static class GridEditComponentAdapter extends
            SingletonAdapter<GridAttribute> implements SelfLabelingComponent {
        /**
         * Constructs a {@code GridEditComponentAdapter}.
         * 
         * @param displayable
         *            the displayable to edit, which must be a
         *            {@link GridAttribute}.
         * @throws IllegalArgumentException
         *             if {@code displayable} is not a {@code GridAttribute}.
         */
        public GridEditComponentAdapter(Displayable<?> displayable) {
            super(displayable);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected GridEditComponent createJComponent() {
            return new GridEditComponent(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Class<GridAttribute> getAttributeClass() {
            return GridAttribute.class;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isSelfLabeling() {
            return true;
        }
    }

    /**
     * Returns a resource bundle used to obtain user readable labels.
     * 
     * @return a resource bundle used to obtain user readable labels.
     */
    public static Bundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Collection of {@link GridClassComboAdapter}s representing grid types.
     */
    private HashMap<GridClassComboAdapter, GridClassComboAdapter> grids;

    /**
     * The currently edited grid attribute.
     */
    private GridAttribute gridAttribute;

    /**
     * Component to select the grid type.
     */
    private JComboBox classCombo;

    /**
     * Utility class for automatic subcomponent management.
     */
    private CollectionEditWorker worker;

    /**
     * Constructs a {@code GridEditComponent}.
     */
    public GridEditComponent(ValueEditComponent vec) {
        if (resourceBundle == null) {
            resourceBundle = Bundle.getBundle(GridEditComponent.class);
        }
        classCombo = new JComboBox();
        classCombo.setRenderer(new GridClassComboRenderer());
        classCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (gridAttribute == null)
                    return;
                Grid previousGrid = gridAttribute.getGrid();
                Class<? extends Grid> previousGridClass = previousGrid
                        .getClass();
                GridClassComboAdapter previousGcca = acquireGridClassComboAdapter(previousGridClass);
                previousGcca.setHistoryGrid(previousGrid);
                GridClassComboAdapter nextGcca = (GridClassComboAdapter) classCombo
                        .getSelectedItem();
                Grid historyGrid = nextGcca.getHistoryGrid();
                if (historyGrid != null) {
                    gridAttribute.setGrid(historyGrid);
                } else {
                    gridAttribute.setClass(nextGcca.getGridClass());
                }
                revalidate(); // TODO
            }
        });

        grids = new HashMap<GridClassComboAdapter, GridClassComboAdapter>();
        GridRegistry registry = GridRegistry.get();
        for (Class<? extends Grid> gridClass : registry.getGrids()) {
            acquireGridClassComboAdapter(gridClass);
        }
        registry.addListener(this);

        final Pair<String, String> selfLabel = denominateSelf();
        worker = new CollectionEditWorker(this,
                new ValueEditComponentFactory() {
                    @Override
                    public ValueEditComponent createComponent(
                            Displayable<?> displayable) {
                        if (displayable instanceof GridClassAttribute)
                            return wrap(displayable, classCombo);
                        else if (displayable instanceof GridParametersAttribute)
                            return new GridParametersEditComponent(
                                    gridAttribute.getParametersAttribute(),
                                    acquireGridClassComboAdapter(gridAttribute
                                            .getGrid().getClass()));
                        return super.createComponent(displayable);
                    }
                }) {
            @Override
            protected Pair<String, String> denominateSelf() {
                return selfLabel;
            }

            @Override
            protected Bundle getResourceBundle() {
                return resourceBundle;
            }
        };
        worker.setValueEditComponent(vec);
    }

    /**
     * {@inheritDoc}
     */
    public void gridAdded(Class<? extends Grid> gridClass) {
        if (grids.containsKey(new GridClassComboAdapter(gridClass, true)))
            return;
        GridClassComboAdapter gcca = new GridClassComboAdapter(gridClass, false);
        grids.put(gcca, gcca);
        classCombo.addItem(gcca);
    }

    /**
     * {@inheritDoc}
     */
    public void gridRemoved(Class<? extends Grid> gridClass) {
        GridClassComboAdapter key = new GridClassComboAdapter(gridClass, true);
        if (((GridClassComboAdapter) classCombo.getSelectedItem()).equals(key)) {
            // TODO:
        }
        classCombo.removeItem(key);
        grids.remove(key);
    }

    /**
     * Returns a {@code GridClassComboAdapter} for the grid type represented by
     * the specified {@code Class}-object. If {@code grids} map does not contain
     * such an adapter yet, it is created and added to the map.
     * 
     * @param gridClass
     *            the {@code Class}-object representing the grid type for which
     *            the adapter is to be returned.
     * @return a {@code GridClassComboAdapter} for the grid type represented by
     *         the specified {@code Class}-object.
     */
    private GridClassComboAdapter acquireGridClassComboAdapter(
            Class<? extends Grid> gridClass) {
        GridClassComboAdapter gcca = grids.get(new GridClassComboAdapter(
                gridClass, true));
        if (gcca == null) {
            gcca = new GridClassComboAdapter(gridClass, false);
            grids.put(gcca, gcca);
            classCombo.addItem(gcca);
        }
        return gcca;
    }

    /**
     * Assigns the component the specified attribute and rebuilds.
     * 
     * @param gridAttribute
     *            the attribute to assign.
     */
    public void setAttribute(GridAttribute gridAttribute) {
        this.gridAttribute = gridAttribute;
        worker.build(gridAttribute);
    }

    /**
     * Makes all value edit subcomponents to set the current value of the
     * {@code Displayable} in the corresponding {@code JComponent}.
     * 
     * @see Displayable
     * @see JComponent
     * @see ValueEditComponent#setEditFieldValue()
     * @see CollectionEditWorker#setEditFieldValue(boolean)
     */
    public void setEditFieldValue() {
        selectCurrentGrid();
        worker.setEditFieldValue(false);
    }

    /**
     * Makes all value edit subcomponents to set the value of the {@code
     * Displayable} specified in the {@code JComponent}, if the value is in fact
     * different.
     * 
     * @see Displayable
     * @see JComponent
     * @see ValueEditComponent#setValue()
     * @see CollectionEditWorker#setValue()
     */
    public void setValue() {
        worker.setValue();
    }

    /**
     * Makes the combobox to select the type of the grid currently set in the
     * grid attribute.
     */
    private void selectCurrentGrid() {
        Class<? extends Grid> gridClass = gridAttribute.getGrid().getClass();
        GridClassComboAdapter gcca = acquireGridClassComboAdapter(gridClass);
        if (gcca.equals(classCombo.getSelectedItem()))
            return;
        classCombo.setSelectedItem(gcca);
    }

    /**
     * Returns reader usable name and description of this component.
     * 
     * @return a pair consisting of reader usable name and description of this
     *         component or {@code null} if the denomination is not available.
     */
    private Pair<String, String> denominateSelf() {
        String name = resourceBundle
                .getString(GraphicAttributeConstants.GRID_PATH + ".name");

        if (name == null)
            return null;

        Pair<String, String> selfLabel = Pair.create(name,
                resourceBundle.getString(GraphicAttributeConstants.GRID_PATH
                        + ".description"));

        return selfLabel;
    }
}
