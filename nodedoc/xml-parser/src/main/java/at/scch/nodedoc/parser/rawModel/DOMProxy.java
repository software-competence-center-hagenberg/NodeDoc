package at.scch.nodedoc.parser.rawModel;

import at.scch.nodedoc.parser.QueryUtils;
import at.scch.nodedoc.parser.XMLUtils;
import org.w3c.dom.Element;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class DOMProxy {

    private final Element element;

    public DOMProxy(Element element) {
        this.element = element;
    }

    protected Stream<Element> query(String... path) {
        return QueryUtils.queryPath(element, path);
    }

    protected String getAttribute(String attribute) {
        return element.getAttribute(attribute);
    }

    protected String getTextContent() {
        return element.getTextContent();
    }

    protected String getAttributeOrDefault(String attributeName, String defaultValue) {
        return getAttributeOrDefault(attributeName, x -> x, defaultValue);
    }

    protected <T> T getAttributeOrDefault(String attributeName, Function<String, T> converter, T defaultValue) {
        return element.hasAttribute(attributeName)
                ? converter.apply(element.getAttribute(attributeName))
                : defaultValue;
    }

    protected Element createElementBeforeWithSameNamespace(String tagName, List<String> elementNames) {
        return XMLUtils.createElementBeforeWithSameNamespace(element, tagName, elementNames);
    }
}
