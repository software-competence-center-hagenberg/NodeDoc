package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.uaStandard.NodeIdDefinition;

import java.util.List;

public class NodeIdParser {

    public NodeId<?> parseNodeId(String nodeId, List<String> namespaceUris) {
        var matcher = NodeIdDefinition.NODE_ID_REGEX.matcher(nodeId);
        if (!matcher.find()) {
            throw new NodeSetResolveException("Invalid NodeId detected " + nodeId);
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
                throw new RuntimeException("Unreachable because of previous regex check");
        }
    }

    public boolean isNodeId(String text) {
        return NodeIdDefinition.NODE_ID_REGEX.matcher(text).find();
    }
}
