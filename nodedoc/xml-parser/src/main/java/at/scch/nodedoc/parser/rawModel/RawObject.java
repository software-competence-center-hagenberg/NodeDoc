package at.scch.nodedoc.parser.rawModel;

import org.w3c.dom.Element;

public class RawObject extends RawInstance {

    public RawObject(Element element) {
        super(element);
    }

    public int getEventNotifier() {
        return getAttributeOrDefault("EventNotifier", Integer::parseInt, 0);
    }

}
