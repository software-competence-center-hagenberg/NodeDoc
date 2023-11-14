package at.scch.nodedoc.parser.rawModel;

import org.w3c.dom.Element;

public class RawAlias extends DOMProxy {

    public RawAlias(Element element) {
        super(element);
    }

    public String getAlias() {
        return getAttribute("Alias");
    }

    public String getNodeId() {
        return getTextContent();
    }
}
