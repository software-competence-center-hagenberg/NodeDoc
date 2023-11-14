package at.scch.nodedoc.parser.rawModel;

import lombok.Getter;
import org.w3c.dom.Element;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RawModel extends DOMProxy {

    public RawModel(Element element) {
        super(element);

        this.requiredModels = query("RequiredModel")
                .map(RawModel::new)
                .collect(Collectors.toList());
    }

    // TODO: RolePermissions?

    @Getter
    public final List<RawModel> requiredModels;

    public String getModelUri() {
        return getAttribute("ModelUri");
    }

    public String getVersion() {
        return getAttributeOrDefault("Version", null);
    }

    public OffsetDateTime getPublicationDate() {
        return getAttributeOrDefault("PublicationDate", OffsetDateTime::parse, null);
    }

    // TODO: AccessRestrictions?

}
