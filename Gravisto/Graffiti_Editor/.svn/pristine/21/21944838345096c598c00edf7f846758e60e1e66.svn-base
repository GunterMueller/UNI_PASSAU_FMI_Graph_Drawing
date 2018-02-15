if (state == 0)
{
    if (matches("mouse.press.nonode"))
    {
        ins["position"] = out["position"];
        action("createSnappedNode");
        selection.set(out["node"]);
    }
    else if (matches("mouse.press.node"))
    {
        ins["node"] = out["element"];
        action("startEdgeCreation");
        state = 1;
    }
    else if (matches("mouse.move.node")
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
    else if (matches("mouse.press.node", ["button", "RIGHT"])
             || matches("mouse.press", ["button", "RIGHT"]))
    {
        ins["position"] = out["position"];
        ins["menu"] = new Menu();
        action("showPopupMenu");
    }
}
else if (state == 1) // During edge creation
{
    if (matches("mouse.move.node")
        || matches("mouse.move"))
    {
        var pos = out["position"];
        if (out["element"] == null)
        {
            ins["element"] = null;
            action("setHoveredElement");
            ins["hover"] = true;
        }
        else
        {
            ins["element"] = out["element"];
            action("setHoveredElement");
            ins["hover"] = false;
        }
        ins["position"] = pos;
        action("setLastEdgePoint");
    }
    else if (matches("mouse.press.nonode", ["shift", "REQUIRE_UP"]))
    {
        ins["position"] = out["position"];
        action("addEdgePoint");
    }
    else if (matches("mouse.press.nonode", ["shift", "REQUIRE_DOWN"]))
    {
        ins["position"] = out["position"];
        action("createSnappedNode");
        selection.add(out["node"]);
        ins["node"] = out["node"];
        action("finishEdgeCreation");
        reset();
    }
    else if (matches("mouse.press.node"))
    {
        ins["node"] = out["element"];
        action("finishEdgeCreation");
        reset();
    }
}
