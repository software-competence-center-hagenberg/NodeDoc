package at.scch.nodedoc.db.documents;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeSetText {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String _id;
    private String namespaceUri;
    private String version;
    private String publicationDate;
    private TextId textId;
    private String xmlText;
    private String userText;

}
