if (activated())
{
    action("activateDummyHub");
}
if (state == 0)
{
    if (matches("mouse.press.free"))
    {
        ins["position"] = out["position"];
        action("startSelectionRect");
        action("startAddRectSelection");
        state = 2;
    }
    else if (matches("mouse.press.node", ["ctrl", "REQUIRE_UP"])
             || matches("mouse.press.edge", ["ctrl", "REQUIRE_UP"]))
    {
        var elem = out["element"];
        if (!selection.contains(elem))
        {
            selection.set(elem);
        }
        state = 1;
    }
    else if (matches("mouse.press.node", ["ctrl", "REQUIRE_DOWN"])
             || matches("mouse.press.edge", ["ctrl", "REQUIRE_DOWN"]))
    {
        var elem = out["element"];
        if (!selection.contains(elem))
        {
            selection.add(elem);
        }
        else
        {
            selection.remove(elem);
        }
    }
    else if (matches("mouse.move.node")
             || matches("mouse.move.edge")
             || matches("mouse.move.nonode"))
    {
        if (out["element"] == null)
        {
            ins["element"] = null;
        }
        else
        {
            ins["element"] = out["element"];
        }
        action("setHoveredElement");
    }
}
else if (state == 1)
{
    if (matches("mouse.drag"))
    {
        to = out["position"];
        delta = out["delta"];
        from = new Vector(0, 0);
        from.x = to.x - delta.x;
        from.y = to.y - delta.y;
        action("getHubPosition");
        ins["origin"] = out["position"];
        ins["to"] = to;
        ins["from"] = from;
        ins["elements"] = selection;
        action("rotateElements");
    }
    else if (matches("mouse.release"))
    {
        state = 0;
    }
}
else if (state == 2) // Selection rectangle
{
    if (matches("mouse.drag", ["ctrl", "REQUIRE_UP"]))
    {
        ins["position"] = out["position"];
        action("changeSelectionRect");
        ins["rectangle"] = out["rectangle"];
        action("setRectSelection");
    }
    else if (matches("mouse.drag", ["ctrl", "REQUIRE_DOWN"]))
    {
        ins["position"] = out["position"];
        action("changeSelectionRect");
        ins["rectangle"] = out["rectangle"];
        action("addRectSelection");
    }
    else if (matches("mouse.release", ["ctrl", "REQUIRE_UP"]))
    {
        ins["position"] = out["position"];
        action("finishSelectionRect");
        ins["rectangle"] = out["rectangle"];
        action("setRectSelection");
        state = 0;
    }
    else if (matches("mouse.release", ["ctrl", "REQUIRE_DOWN"]))
    {
        ins["position"] = out["position"];
        action("finishSelectionRect");
        ins["rectangle"] = out["rectangle"];
        action("addRectSelection");
        state = 0;
    }
}
