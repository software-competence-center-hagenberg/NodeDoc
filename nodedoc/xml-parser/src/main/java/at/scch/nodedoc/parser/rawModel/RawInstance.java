package at.scch.nodedoc.parser.rawModel;

import org.w3c.dom.Element;

public abstract class RawInstance extends RawNode {

    public RawInstance(Element element) {
        super(element);
    }

    public String getParentNodeId() {
        return getAttributeOrDefault("ParentNodeId", null);
    }

}
