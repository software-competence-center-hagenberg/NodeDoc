package at.scch.nodedoc.parser.rawModel;

import lombok.Getter;
import org.w3c.dom.Element;

import java.util.List;
import java.util.stream.Collectors;

public abstract class RawNode extends DOMProxy {

    public RawNode(Element element) {
        super(element);

        this.references = query("References", "Reference")
                .map(RawReference::new)
                .collect(Collectors.toList());
    }

    public String getDisplayName() {
        return query("DisplayName")
                .map(Element::getTextContent)
                .findAny().orElse(null);
    }

    public String getDescription() {
        return query("Description")
                .map(Element::getTextContent)
                .findAny().orElse(null);
    }

    public void setDescription(String description) {
        var element = query("Description")
                .findAny()
                .orElseGet(() -> createElementBeforeWithSameNamespace("Description",
                        List.of("Category", "Documentation", "References", "RolePermissions", "Extensions")));
        element.setTextContent(description);
    }

    public String getCategory() {
        return query("Category")
                .map(Element::getTextContent)
                .findAny().orElse(null);
    }

    public String getDocumentation() {
        return query("Documentation")
                .map(Element::getTextContent)
                .findAny().orElse(null);
    }

    public void setDocumentation(String documentation) {
        var element = query("Documentation")
                .findAny()
                .orElseGet(() -> createElementBeforeWithSameNamespace("Documentation",
                        List.of("References", "RolePermissions", "Extensions")));
        element.setTextContent(documentation);
    }

    @Getter
    private final List<RawReference> references;

    // TODO: RolePermissions?

    // TODO: Extensions?

    public String getNodeId() {
        return getAttribute("NodeId");
    }

    public String getBrowseName() {
        return getAttribute("BrowseName");
    }

    public int getWriteMask() {
        return getAttributeOrDefault("WriteMask", Integer::parseInt, 0);
    }

    public int getUserWriteMask() {
        return getAttributeOrDefault("UserWriteMask", Integer::parseInt, 0);
    }

    // TODO: AccessRestrictions?

    // TODO: HasNoPermissions?

    public String getSymbolicName() {
        return getAttributeOrDefault("SymbolicName", null);
    }

    // TODO: ReleaseStatus?

}
