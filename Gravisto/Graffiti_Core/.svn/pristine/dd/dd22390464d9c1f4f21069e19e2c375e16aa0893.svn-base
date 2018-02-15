package org.graffiti.plugins.tools.demos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import org.graffiti.plugin.gui.AbstractGraffitiComponent;
import org.graffiti.plugin.tool.Tool;
import org.graffiti.plugin.tool.ToolFilter;
import org.graffiti.plugin.tool.ToolRegistry;
import org.graffiti.util.VoidCallback;

/**
 * Temporary class for demonstration purposes, which will be removed once there
 * is a real plugin employing the new tool system.
 * 
 * @author Andreas Glei&szlig;ner
 */
public class ToggleModeTool extends AbstractGraffitiComponent {
    /**
     * 
     */
    private static final long serialVersionUID = -2572697902273019754L;

    public ToggleModeTool() {
        super("defaultToolbar");
        final Package pack = ToggleModeTool.class.getPackage();
        final ToolFilter filter = new ToolFilter() {
            public boolean isVisible(Tool<?> tool) {
                return tool.getClass().getPackage().equals(pack);
            }
        };

        final JToggleButton button = new JToggleButton("Toggle Mode Demo");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ToolRegistry.get().executeChanges(
                        new VoidCallback<ToolRegistry>()

                        {
                            public void call(ToolRegistry registry) {
                                if (button.isSelected()) {
                                    registry.addDefaultModeVeto(button);
                                    registry.addToolFilter(filter);
                                } else {
                                    registry.removeDefaultModeVeto(button);
                                    registry.removeToolFilter(filter);
                                }
                            }
                        });
            }
        });
        add(button);
    }
}
