package at.scch.nodedoc.parser;

import at.scch.nodedoc.parser.rawModel.RawNodeSet;

import java.util.regex.Pattern;

public class BrowseNameValidator implements Validator {

    private static final String BROWSENAME_PATTERN = "(?:(?<index>[0-9]+):)?(?<name>.+)";

    public void validateOrThrow(RawNodeSet nodeSet) {
        var pattern = Pattern.compile(BROWSENAME_PATTERN);
        nodeSet.getNodes().forEach(node -> {;
            var browseName = node.getBrowseName();
            var matcher = pattern.matcher(browseName);
            if (!matcher.matches()) {
                throw new NodeSetBrowseNameValidationException("\"" + browseName + "\" is not a valid BrowseName");
            }
            var indexText = matcher.group("index");
            if (indexText != null) {
                var index = Integer.parseInt(indexText);
                if (index > nodeSet.getNamespaceUris().size()) {
                    throw new NodeSetBrowseNameValidationException("\"" + browseName + "\" is not a valid BrowseName, " + index + " is not a valid namespace index");
                }
            }
        });
    }
}
