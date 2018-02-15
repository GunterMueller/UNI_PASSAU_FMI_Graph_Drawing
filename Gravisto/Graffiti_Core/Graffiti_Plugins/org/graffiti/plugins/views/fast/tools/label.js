if (matches("mouse.press.node")
    || matches("mouse.press.edge"))
{
    ins["element"] = out["element"];
    action("editLabel");
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
