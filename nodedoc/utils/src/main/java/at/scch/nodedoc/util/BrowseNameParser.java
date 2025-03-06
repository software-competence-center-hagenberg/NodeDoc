package at.scch.nodedoc.util;

import lombok.Data;

import java.util.Optional;
import java.util.regex.Pattern;

public class BrowseNameParser {

    @Data
    public static class RawBrowseName {
        private final Optional<Integer> namespaceIndex;
        private final String browseName;
    }

    private static final Pattern BROWSENAME_PATTERN = Pattern.compile("(?:(?<index>[0-9]+):)?(?<name>.+)");

    public static Optional<RawBrowseName> parseBrowseName(String browseName) {
        var matcher = BROWSENAME_PATTERN.matcher(browseName);
        if (matcher.matches()) {
            var indexText = matcher.group("index");
            if (indexText != null) {
                var index = Integer.parseInt(indexText);
                return Optional.of(new RawBrowseName(Optional.of(index), matcher.group("name")));
            } else {
                return Optional.of(new RawBrowseName(Optional.empty(), matcher.group("name")));
            }
        } else {
            return Optional.empty();
        }
    }
}
