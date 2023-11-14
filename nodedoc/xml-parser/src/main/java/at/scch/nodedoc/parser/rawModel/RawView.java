package at.scch.nodedoc.parser.rawModel;

import org.w3c.dom.Element;

public class RawView extends RawInstance {

    public RawView(Element element) {
        super(element);
    }

    public boolean isContainsNoLoops() {
        return getAttributeOrDefault("ContainsNoLoops", Boolean::parseBoolean, false);
    }

    public int getEventNotifier() {
        return getAttributeOrDefault("EventNotifier", Integer::parseInt, 0);
    }

}
