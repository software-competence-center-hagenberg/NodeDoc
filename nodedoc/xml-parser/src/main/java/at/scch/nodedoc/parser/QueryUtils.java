package at.scch.nodedoc.parser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.stream.Stream;

public class QueryUtils {

    public static Stream<Element> elementStreamFromNodeList(NodeList nodeList) {
        return Stream.iterate(0, i -> i < nodeList.getLength(), i -> i + 1)
                .map(nodeList::item)
                .filter(node -> node instanceof Element)
                .map(node -> (Element) node);
    }

    public static Stream<Element> queryPath(Element element, String... path) {
        return queryPath(element, path, 0);
    }

    private static Stream<Element> queryPath(Element element, String[] path, int currentIndex) {
        if (currentIndex == path.length) {
            return Stream.of(element);
        } else {
            return elementStreamFromNodeList(element.getChildNodes())
                    .filter(child -> child.getLocalName().equals(path[currentIndex]))
                    .flatMap(child -> queryPath(child, path, currentIndex + 1));
        }
    }
}
