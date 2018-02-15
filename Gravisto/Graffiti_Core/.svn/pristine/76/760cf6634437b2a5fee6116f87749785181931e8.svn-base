package org.graffiti.plugins.tools.demos;

import java.awt.Cursor;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;

import org.graffiti.plugin.tool.ToolEnvironment;
import org.graffiti.plugin.view.interactive.MouseButton;
import org.graffiti.plugins.modes.fast.ModifierHandling;
import org.graffiti.plugins.tools.commonactions.SetCursor;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.actions.PanView;
import org.graffiti.plugins.views.fast.actions.SetCompassPosition;
import org.graffiti.plugins.views.fast.actions.StartZoomRotation;
import org.graffiti.plugins.views.fast.actions.ZoomRotation;
import org.graffiti.plugins.views.fast.triggers.MouseDragTrigger;
import org.graffiti.plugins.views.fast.triggers.MousePressTrigger;
import org.graffiti.plugins.views.fast.triggers.MouseReleaseTrigger;
import org.graffiti.plugins.views.fast.triggers.MouseTrigger;

/**
 * Temporary class for demonstration purposes, which will be removed once there
 * is a real plugin employing the new tool system.
 * 
 * @author Andreas Glei&szlig;ner
 */
public class ExampleTool extends AbstractExampleTool {
    public ExampleTool() {
        super("abcde");
        setName("Sheep Tool");
        setDescription("Demo tool imitating the pan tool with Java code.");
        setIcon(new ImageIcon(ExampleTool.class.getResource("sheep.png")));
    }

    @Override
    protected void activated() {
    }

    @Override
    protected void deactivated() {
    }

    @Override
    protected void reseted(ToolEnvironment<FastView> env) {
        env.putIn(SetCursor.cursorSlot, Cursor.HAND_CURSOR).execute(
                SetCursor.class);
    }

    @Override
    protected boolean isDefaultMode() {
        return false;
    }

    @Override
    protected void gesturePerformed(ToolEnvironment<FastView> env) {
        if (env.putParam(MousePressTrigger.button, MouseButton.RIGHT).matches(
                "mouse.press")) {
            Point2D center = env.getOut(MouseTrigger.position);
            env.putIn(StartZoomRotation.center, center).putIn(
                    StartZoomRotation.rawCenter,
                    env.getOut(MousePressTrigger.rawPosition)).execute(
                    StartZoomRotation.class);
            env.putIn(SetCompassPosition.positionSlot, center).execute(
                    SetCompassPosition.class);
        } else if (env.putParam(MouseDragTrigger.button, MouseButton.LEFT)
                .matches("mouse.drag")) {
            env.putIn(PanView.deltaSlot, env.getOut(MouseDragTrigger.rawDelta))
                    .execute(PanView.class);
        } else if (env.putParam(MouseDragTrigger.button, MouseButton.RIGHT)
                .matches("mouse.drag")) {
            Point2D pos = env.getOut(MouseDragTrigger.rawPosition);
            env.putIn(
                    ZoomRotation.snapSlot,
                    env.putParam(MouseDragTrigger.button, MouseButton.RIGHT)
                            .putParam(MouseTrigger.alt,
                                    ModifierHandling.REQUIRE_UP).matches(
                                    "mouse.drag")).putIn(
                    ZoomRotation.rawPositionSlot, pos).execute(
                    ZoomRotation.class);
        } else if (env.putParam(MouseReleaseTrigger.button, MouseButton.RIGHT)
                .matches("mouse.release")) {
            reset();
            env.putIn(SetCursor.cursorSlot, Cursor.HAND_CURSOR).execute(
                    SetCursor.class);
        }
    }
}
