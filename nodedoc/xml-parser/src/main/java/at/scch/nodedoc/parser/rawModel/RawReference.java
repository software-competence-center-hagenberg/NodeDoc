package at.scch.nodedoc.parser.rawModel;

import org.w3c.dom.Element;

public class RawReference extends DOMProxy {

    public RawReference(Element element) {
        super(element);
    }

    public String getReferenceType() {
        return getAttribute("ReferenceType");
    }

    public String getReferencedId() {
        return getTextContent();
    }

    public boolean isForward() {
        return getAttributeOrDefault("IsForward", Boolean::parseBoolean, true);
    }

}
