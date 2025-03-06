package at.scch.nodedoc.parser;

import at.scch.nodedoc.parser.rawModel.RawNodeSet;
import at.scch.nodedoc.util.BrowseNameParser;

public class BrowseNameValidator implements Validator {

    public void validateOrThrow(RawNodeSet nodeSet) {
        nodeSet.getNodes().forEach(node -> {;
            var browseName = node.getBrowseName();
            var rawBrowseName = BrowseNameParser.parseBrowseName(browseName);
            if (rawBrowseName.isEmpty()) {
                throw new NodeSetBrowseNameValidationException("\"" + browseName + "\" is not a valid BrowseName");
            }
            var index = rawBrowseName.get().getNamespaceIndex();
            if (index.isPresent()) {
                if (index.get() > nodeSet.getNamespaceUris().size()) {
                    throw new NodeSetBrowseNameValidationException("\"" + browseName + "\" is not a valid BrowseName, " + index + " is not a valid namespace index");
                }
            }
        });
    }
}
