package at.scch.nodedoc.parser;

import org.w3c.dom.Element;

import java.util.List;

public class XMLUtils {
    public static Element createElementBeforeWithSameNamespace(Element element, String tagName, List<String> elementNames) {
        var prefix = element.getPrefix() != null ? element.getPrefix() + ":" : "";
        var newElement = element.getOwnerDocument().createElementNS(element.getNamespaceURI(), prefix + tagName);
        var successor = elementNames.stream()
                .flatMap(childElementName -> QueryUtils.queryPath(element, childElementName).findFirst().stream())
                .findFirst()
                .orElse(null);
        element.insertBefore(newElement, successor);
        return newElement;
    }
}
