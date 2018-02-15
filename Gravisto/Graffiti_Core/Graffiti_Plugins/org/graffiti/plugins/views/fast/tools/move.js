if (matches("popupmenu"))
{
    if (out["id"] == "addbend")
    {
        ins["position"] = out["position"];
        ins["edges"] = out["edges"];
        action("addBend");
    }
    else if (out["id"] == "removebends")
    {
        ins["edges"] = out["edges"];
        action("removeBends");
    }
    else if (out["id"] == "align")
    {
        ins["anchor"] = out["anchor"];
        ins["nodes"] = out["nodes"];
        ins["align"] = out["align"];
        action("alignNodes");
    }
}
if (state == 0)
{
    if (matches("mouse.press.free"))
    {
        ins["position"] = out["position"];
        action("startSelectionRect");
        action("startAddRectSelection");
        state = 1;
    }
    else if (matches("mouse.press.edge.bend"))
    {
        var elem = out["element"];
        if (!selection.contains(elem))
        {
            selection.set(elem);
        }
        ins["bend"] = out["bend"];
        ins["edge"] = elem;
        ins["position"] = out["position"];
        action("startBendMoving");
        state = 4;
    }
    else if (matches("mouse.press.node", ["ctrl", "REQUIRE_UP"])
             || matches("mouse.press.edge", ["ctrl", "REQUIRE_UP"]))
    {
        var elem = out["element"];
        if (!selection.contains(elem))
        {
            selection.set(elem);
        }
        ins["nodes"] = selection.getNodes();
        ins["position"] = out["position"];
        action("startNodesMoving");
        state = 2;
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
        reset();
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
    else if (matches("mouse.press.node", ["button", "RIGHT"])
             || matches("mouse.press", ["button", "RIGHT"]))
    {
        var m = new Menu();
        var element = out["element"];
        var position = out["position"];
        if (selection.getEdges().size() > 0)
        {
            if (element == null)
            {
                m[0].label = "Add Bend";
                m[0].id = "addbend";
                m[0].out["edges"] = selection.getEdges();
                m[0].out["position"] = position;
            }
            m[1].label = "Remove Bends";
            m[1].id = "removebends";
            m[1].out["edges"] = selection.getEdges();
        }
        if (selection.getNodes().size() > 0 && element != null)
        {
            m[2].label = "Align";
            m[2][0].out["align"] = "li"; m[2][0].label = "Left";
            m[2][1].out["align"] = "ci"; m[2][1].label = "Center horizontally";
            m[2][2].out["align"] = "hi"; m[2][2].label = "Right";
            m[2][3].out["align"] = "il"; m[2][3].label = "Top";
            m[2][4].out["align"] = "ic"; m[2][4].label = "Center vertically";
            m[2][5].out["align"] = "cc"; m[2][5].label = "Center";
            m[2][6].out["align"] = "ih"; m[2][6].label = "Bottom";
            for (i = 0; i < 7; i++)
            {
                m[2][i].id = "align";
                m[2][i].out["anchor"] = element;
                m[2][i].out["nodes"] = selection.getNodes();
            } 
        }
        if (selection.size() > 0)
        {
            ins["elements"] = selection;
            ins["menu"] = m;
            action("addLabelsToMenu");
        }
        ins["position"] = position;
        ins["menu"] = m;
        action("showPopupMenu");
    }
}
else if (state == 1) // Selection rectangle
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
        reset();
    }
    else if (matches("mouse.release", ["ctrl", "REQUIRE_DOWN"]))
    {
        ins["position"] = out["position"];
        action("finishSelectionRect");
        ins["rectangle"] = out["rectangle"];
        action("addRectSelection");
        reset();
    }
}
else if (state == 2)
{
    if (matches("mouse.drag"))
    {
        ins["position"] = out["position"];
        action("moveNodes");
        state = 3;
    }
    else if (matches("mouse.release.node"))
    {
        selection.set(out["element"]);
        reset();
    }
    else if (matches("mouse.release"))
    {
        reset();
    }
}
else if (state == 3)
{
    if (matches("mouse.drag"))
    {
        ins["position"] = out["position"];
        action("moveNodes");
    }
    else if (matches("mouse.release"))
    {
        reset();
    }
}
else if (state == 4)
{
    if (matches("mouse.drag"))
    {
        ins["position"] = out["position"];
        action("moveBend");
    }
    else if (matches("mouse.release"))
    {
         reset();
    }
}
