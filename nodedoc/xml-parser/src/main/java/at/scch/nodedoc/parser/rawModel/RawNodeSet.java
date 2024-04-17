package at.scch.nodedoc.parser.rawModel;

import at.scch.nodedoc.parser.QueryUtils;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RawNodeSet extends DOMProxy {

    private final Document document;

    public RawNodeSet(Element element) {
        super(element);
        this.document = element.getOwnerDocument();

        this.namespaceUris = query("NamespaceUris", "Uri")
                .map(Element::getTextContent)
                .collect(Collectors.toList());

        this.nodes = QueryUtils.elementStreamFromNodeList(element.getChildNodes())
                .map(e -> {
                    switch (e.getLocalName()) {
                        case "UAObject":
                            return new RawObject(e);
                        case "UAVariable":
                            return new RawVariable(e);
                        case "UAMethod":
                            return new RawMethod(e);
                        case "UAView":
                            return new RawView(e);
                        case "UAObjectType":
                            return new RawObjectType(e);
                        case "UAVariableType":
                            return new RawVariableType(e);
                        case "UADataType":
                            return new RawDataType(e);
                        case "UAReferenceType":
                            return new RawReferenceType(e);
                        default:
                            return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        this.models = query("Models", "Model")
                .map(RawModel::new)
                .collect(Collectors.toList());
    }

    public Document getXMLDocument() {
        return document;
    }

    @Getter
    private final List<String> namespaceUris;

    // TODO: ServerUris?

    @Getter
    private List<RawModel> models;

    public List<RawAlias> getAliases() {
        return query("Aliases", "Alias")
                .map(RawAlias::new)
                .collect(Collectors.toList());
    }

    // TODO: Extensions?

    @Getter
    private final List<RawNode> nodes;

    public OffsetDateTime getLastModified() {
        return getAttributeOrDefault("LastModified", OffsetDateTime::parse, null);
    }

}
