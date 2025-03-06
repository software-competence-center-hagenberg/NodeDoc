package at.scch.nodedoc.nodeset;

import lombok.Getter;

import java.util.List;
import java.util.Optional;

public class StructuredBrowseName {

    @Getter
    private final Optional<Integer> originalNamespaceIndex;

    @Getter
    private final String namespaceUri;

    @Getter
    private final String browseName;

    public StructuredBrowseName(Optional<Integer> originalNamespaceIndex, String namespaceUri, String browseName) {
        this.originalNamespaceIndex = originalNamespaceIndex;
        this.namespaceUri = namespaceUri;
        this.browseName = browseName;
    }

    public int computeNamespaceIndex(List<String> namespaceIndices) {
        if (originalNamespaceIndex.isEmpty() || originalNamespaceIndex.get() == 0) {
            return 0;
        } else {
            return namespaceIndices.indexOf(namespaceUri) + 1;
        }
    }
}
