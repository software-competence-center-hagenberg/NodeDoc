package at.scch.nodedoc.db.documents;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor // For JSON support
public class NodeSetText {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String _id;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String _rev;
    private String namespaceUri;
    private String version;
    private String publicationDate;
    private TextId textId;
    private String xmlText;
    private String userText;
    private List<HistoryEntry> userTextHistory;
}
