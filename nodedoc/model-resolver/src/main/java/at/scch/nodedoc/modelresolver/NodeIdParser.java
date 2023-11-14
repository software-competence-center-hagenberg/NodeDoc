package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeId;

import java.util.List;
import java.util.regex.Pattern;

public class NodeIdParser {

    private static final Pattern NODE_ID_REGEX = Pattern.compile("(?:ns=(?<ns>\\d+);)?(?<type>[igsb])=(?<id>.+)");

    public NodeId<?> parseNodeId(String nodeId, List<String> namespaceUris) {
        var matcher = NODE_ID_REGEX.matcher(nodeId);
        if (!matcher.find()) {
            throw new RuntimeException("invalid node id");
        }

        var namespaceId = matcher.group("ns") != null
                ? Integer.parseInt(matcher.group("ns"))
                : 0;

        var namespaceUri = namespaceUris.get(namespaceId);

        var idType = matcher.group("type");
        var idValue = matcher.group("id");

        switch (idType) {
            case "i":
                return new NodeId.IntNodeId(namespaceUri, Integer.parseInt(idValue));
            case "g":
                return new NodeId.GuidNodeId(namespaceUri, idValue);
            case "s":
                return new NodeId.StringNodeId(namespaceUri, idValue);
            case "b":
                return new NodeId.OpaqueNodeId(namespaceUri, idValue);
            default:
                // TODO
                throw new RuntimeException("unreachable");
        }
    }

    public boolean isNodeId(String text) {
        return NODE_ID_REGEX.matcher(text).find();
    }
}
