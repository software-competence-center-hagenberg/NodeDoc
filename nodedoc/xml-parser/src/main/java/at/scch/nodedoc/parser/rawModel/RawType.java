package at.scch.nodedoc.parser.rawModel;

import org.w3c.dom.Element;

public abstract class RawType extends RawNode {

    public RawType(Element element) {
        super(element);
    }

    public boolean isAbstract() {
        return getAttributeOrDefault("IsAbstract", Boolean::parseBoolean, false);
    }
}
