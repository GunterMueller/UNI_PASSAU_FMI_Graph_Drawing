package org.graffiti.plugins.views.fast;

import org.graffiti.plugin.view.interactive.ToolAction;
import org.graffiti.plugin.view.interactive.Trigger;
import org.graffiti.plugin.view.interactive.ViewFamily;
import org.graffiti.plugins.views.fast.actions.ActivateDummyHub;
import org.graffiti.plugins.views.fast.actions.AddBend;
import org.graffiti.plugins.views.fast.actions.AddEdgePoint;
import org.graffiti.plugins.views.fast.actions.AddLabelsToMenu;
import org.graffiti.plugins.views.fast.actions.AlignNodes;
import org.graffiti.plugins.views.fast.actions.ChangeSelectionRect;
import org.graffiti.plugins.views.fast.actions.CreateSnappedNode;
import org.graffiti.plugins.views.fast.actions.EditLabel;
import org.graffiti.plugins.views.fast.actions.FastViewAction;
import org.graffiti.plugins.views.fast.actions.FinishEdgeCreation;
import org.graffiti.plugins.views.fast.actions.FinishSelectionRect;
import org.graffiti.plugins.views.fast.actions.GetHubPosition;
import org.graffiti.plugins.views.fast.actions.MoveBend;
import org.graffiti.plugins.views.fast.actions.MoveNodes;
import org.graffiti.plugins.views.fast.actions.PanView;
import org.graffiti.plugins.views.fast.actions.RemoveBends;
import org.graffiti.plugins.views.fast.actions.ResizeNode;
import org.graffiti.plugins.views.fast.actions.RotateElements;
import org.graffiti.plugins.views.fast.actions.RotateView;
import org.graffiti.plugins.views.fast.actions.SetCompassPosition;
import org.graffiti.plugins.views.fast.actions.SetHoveredElement;
import org.graffiti.plugins.views.fast.actions.SetLastEdgePoint;
import org.graffiti.plugins.views.fast.actions.SetResizingCursor;
import org.graffiti.plugins.views.fast.actions.StartBendMoving;
import org.graffiti.plugins.views.fast.actions.StartEdgeCreation;
import org.graffiti.plugins.views.fast.actions.StartNodeResizing;
import org.graffiti.plugins.views.fast.actions.StartNodesMoving;
import org.graffiti.plugins.views.fast.actions.StartSelectionRect;
import org.graffiti.plugins.views.fast.actions.StartViewRotation;
import org.graffiti.plugins.views.fast.actions.StartZoom;
import org.graffiti.plugins.views.fast.actions.StartZoomRotation;
import org.graffiti.plugins.views.fast.actions.ZoomRotation;
import org.graffiti.plugins.views.fast.actions.ZoomView;
import org.graffiti.plugins.views.fast.triggers.KeyPressTrigger;
import org.graffiti.plugins.views.fast.triggers.KeyReleaseTrigger;
import org.graffiti.plugins.views.fast.triggers.KeyboardTrigger;
import org.graffiti.plugins.views.fast.triggers.MouseDragTrigger;
import org.graffiti.plugins.views.fast.triggers.MouseMoveNotOnNodeTrigger;
import org.graffiti.plugins.views.fast.triggers.MouseMoveOnEdgeTrigger;
import org.graffiti.plugins.views.fast.triggers.MouseMoveOnFreeTrigger;
import org.graffiti.plugins.views.fast.triggers.MouseMoveOnNodeBorderTrigger;
import org.graffiti.plugins.views.fast.triggers.MouseMoveOnNodeTrigger;
import org.graffiti.plugins.views.fast.triggers.MouseMoveTrigger;
import org.graffiti.plugins.views.fast.triggers.MousePressNotOnNodeTrigger;
import org.graffiti.plugins.views.fast.triggers.MousePressOnEdgeBendTrigger;
import org.graffiti.plugins.views.fast.triggers.MousePressOnEdgeTrigger;
import org.graffiti.plugins.views.fast.triggers.MousePressOnFreeTrigger;
import org.graffiti.plugins.views.fast.triggers.MousePressOnNodeBorderTrigger;
import org.graffiti.plugins.views.fast.triggers.MousePressOnNodeTrigger;
import org.graffiti.plugins.views.fast.triggers.MousePressTrigger;
import org.graffiti.plugins.views.fast.triggers.MouseReleaseOnNodeTrigger;
import org.graffiti.plugins.views.fast.triggers.MouseReleaseTrigger;
import org.graffiti.plugins.views.fast.triggers.MouseTrigger;
import org.graffiti.plugins.views.fast.triggers.PopupMenuTrigger;
import org.graffiti.plugins.views.fast.triggers.RootTrigger;
import org.graffiti.plugins.views.fast.triggers.ToolActivationTrigger;

public final class FastViewFamily extends ViewFamily<FastView> {
    /**
     * Constructs a new {@code FastViewFamily}. Adds actions suitable to
     * {@link FastView}.
     * 
     * @see FastViewAction
     */
    protected FastViewFamily() {
        ChangeSelectionRect changeSelectionRect = new ChangeSelectionRect();
        FinishSelectionRect finishSelectionRect = new FinishSelectionRect();
        add(new StartSelectionRect(changeSelectionRect, finishSelectionRect));
        add(changeSelectionRect);
        add(finishSelectionRect);
        StartEdgeCreation startEdgeCreation = new StartEdgeCreation();
        add(startEdgeCreation);
        add(new SetLastEdgePoint(startEdgeCreation));
        add(new AddEdgePoint(startEdgeCreation));
        add(new FinishEdgeCreation(startEdgeCreation));
        add(new SetHoveredElement());
        add(new ActivateDummyHub());
        add(new GetHubPosition());
        add(new RotateElements());
        add(new PanView());
        add(new SetResizingCursor());
        ResizeNode resizeNode = new ResizeNode();
        add(new StartNodeResizing(resizeNode));
        add(resizeNode);
        MoveNodes moveNodes = new MoveNodes();
        add(new StartNodesMoving(moveNodes));
        add(moveNodes);
        MoveBend moveBend = new MoveBend();
        add(new StartBendMoving(moveBend));
        add(moveBend);
        add(new AddBend());
        add(new RemoveBends());
        add(new AlignNodes());
        add(new CreateSnappedNode());
        add(new AddLabelsToMenu());
        add(new EditLabel());
        RotateView rotateView = new RotateView();
        add(new StartViewRotation(rotateView));
        add(rotateView);
        ZoomView zoomView = new ZoomView();
        add(new StartZoom(zoomView));
        add(zoomView);
        add(new SetCompassPosition());
        ZoomRotation zoomRotation = new ZoomRotation();
        add(new StartZoomRotation(zoomRotation));
        add(zoomRotation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(ToolAction<FastView> action) {
        super.add(action);
    }

    /**
     * {@inheritDoc} This implementation returns {@link RootTrigger}
     */
    @Override
    protected Trigger createRootTrigger() {
        RootTrigger trigger = new RootTrigger();
        KeyboardTrigger keyboard = new KeyboardTrigger(trigger);
        /* KeyPressTrigger keyPress = */new KeyPressTrigger(keyboard);
        /* KeyReleaseTrigger keyRelease = */new KeyReleaseTrigger(keyboard);
        MouseTrigger mouse = new MouseTrigger(trigger);
        MousePressTrigger mousePress = new MousePressTrigger(mouse);
        MousePressOnNodeTrigger mousePressOnNode = new MousePressOnNodeTrigger(
                mousePress);
        new MousePressOnNodeBorderTrigger(mousePressOnNode);
        new MousePressNotOnNodeTrigger(mousePress);
        MousePressOnEdgeTrigger mousePressOnEdge = new MousePressOnEdgeTrigger(
                mousePress);
        new MousePressOnEdgeBendTrigger(mousePressOnEdge);
        new MousePressOnFreeTrigger(mousePress);
        MouseReleaseTrigger mouseRelease = new MouseReleaseTrigger(mouse);
        new MouseReleaseOnNodeTrigger(mouseRelease);
        /* MouseDragTrigger mouseDrag = */new MouseDragTrigger(mouse);
        MouseMoveTrigger mouseMove = new MouseMoveTrigger(mouse);
        MouseMoveOnNodeTrigger mouseMoveOnNode = new MouseMoveOnNodeTrigger(
                mouseMove);
        new MouseMoveOnNodeBorderTrigger(mouseMoveOnNode);
        new MouseMoveNotOnNodeTrigger(mouseMove);
        new MouseMoveOnEdgeTrigger(mouseMove);
        new MouseMoveOnFreeTrigger(mouseMove);
        new PopupMenuTrigger(trigger);
        new ToolActivationTrigger(trigger);
        return trigger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<FastView> getCommonSuperClass() {
        return FastView.class;
    }

    /**
     * {@inheritDoc} This implementation returns {@code "FastView"}.
     */
    @Override
    public String getName() {
        return "FastView";
    }
}
