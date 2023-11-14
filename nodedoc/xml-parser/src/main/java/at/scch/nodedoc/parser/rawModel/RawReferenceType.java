package at.scch.nodedoc.parser.rawModel;

import org.w3c.dom.Element;

import java.util.List;
import java.util.stream.Collectors;

public class RawReferenceType extends RawType {

    public RawReferenceType(Element element) {
        super(element);
    }

    public List<String> getInverseNames() {
        return query("InverseName")
                .map(Element::getTextContent)
                .collect(Collectors.toList());
    }

    public boolean isSymmetric() {
        return getAttributeOrDefault("Symmetric", Boolean::parseBoolean, false);
    }
}
