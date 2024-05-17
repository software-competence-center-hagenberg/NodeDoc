package at.scch.nodedoc.documentation.displaymodel.graph;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Getter
public class GraphDisplayData {

    @JsonProperty("nodes")
    private final Set<GraphNode> nodes;

    @JsonProperty("references")
    private final Set<GraphReference> references;

    @JsonProperty("nodeColors")
    private final Set<GraphNodeColor> nodeColors;

    @JsonProperty("referenceColors")
    private final Set<GraphReferenceColor> referenceColors;

    @JsonProperty("root")
    private final NodeId<?> root;

    public GraphDisplayData(Set<GraphNode> nodes, Set<GraphReference> references, Set<GraphNodeColor> nodeColors, Set<GraphReferenceColor> referenceColors, NodeId<?> root) {
        this.nodes = nodes;
        this.references = references;
        this.nodeColors = nodeColors;
        this.referenceColors = referenceColors;
        this.root = root;
    }

    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class GraphNode {
        @JsonProperty("nodeClass")
        private final String nodeClass;
        @JsonProperty("browseName")
        private final String browseName;
        @JsonProperty("tooltipText")
        private final String tooltipText;
        @JsonProperty("chapterAnchor")
        private final String chapterAnchor;
        @JsonProperty("color")
        private final String color;
        @JsonProperty("nodeId")
        @EqualsAndHashCode.Include
        private final NodeId<?> nodeId;

        public GraphNode(String nodeClass, String browseName, String tooltipText, String chapterAnchor, String color, NodeId<?> nodeId) {
            this.nodeClass = nodeClass;
            this.browseName = browseName;
            this.tooltipText = tooltipText;
            this.chapterAnchor = chapterAnchor;
            this.color = color;
            this.nodeId = nodeId;
        }
    }

    @Getter
    public static class GraphVariableNode extends GraphNode {

        @JsonProperty("dataType")
        private final NodeId<?> dataType;

        public GraphVariableNode(String nodeClass, String browseName, String tooltipText, String chapterAnchor, String color, NodeId<?> nodeId, NodeId<?> dataType) {
            super(nodeClass, browseName, tooltipText, chapterAnchor, color, nodeId);
            this.dataType = dataType;
        }
    }

    @Getter
    public static class NodeId<T> {
        @JsonProperty("namespace")
        private final String namespaceUri;
        @JsonProperty("type")
        private final String type;
        @JsonProperty("value")
        private final T id;

        public NodeId(String namespaceUri, String type, T id) {
            this.namespaceUri = namespaceUri;
            this.type = type;
            this.id = id;
        }

        public static <T> NodeId<T> from(at.scch.nodedoc.nodeset.NodeId<T> nodeId) {
            if (nodeId instanceof at.scch.nodedoc.nodeset.NodeId.IntNodeId) {
                return new GraphDisplayData.NodeId<>(nodeId.getNamespaceUri(), "i", nodeId.getId());
            } else if (nodeId instanceof at.scch.nodedoc.nodeset.NodeId.StringNodeId) {
                return new GraphDisplayData.NodeId<>(nodeId.getNamespaceUri(), "s", nodeId.getId());
            } else if (nodeId instanceof at.scch.nodedoc.nodeset.NodeId.OpaqueNodeId) {
                return new GraphDisplayData.NodeId<>(nodeId.getNamespaceUri(), "o", nodeId.getId());
            } else if (nodeId instanceof at.scch.nodedoc.nodeset.NodeId.GuidNodeId) {
                return new GraphDisplayData.NodeId<>(nodeId.getNamespaceUri(), "g", nodeId.getId());
            } else {
                throw new RuntimeException("Unsupported NodeId class " + nodeId.getClass());
            }
        }
    }

    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class GraphReference {
        @JsonProperty("referenceType")
        @EqualsAndHashCode.Include
        private final NodeId<?> referenceType;

        @JsonProperty("source")
        @EqualsAndHashCode.Include
        private final NodeId<?> source;

        @JsonProperty("target")
        @EqualsAndHashCode.Include
        private final NodeId<?> target;

        @JsonProperty("color")
        private final String color;

        public GraphReference(NodeId<?> referenceType, NodeId<?> source, NodeId<?> target, String color) {
            this.referenceType = referenceType;
            this.source = source;
            this.target = target;
            this.color = color;
        }
    }

    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class GraphNodeColor {
        @JsonProperty("name")
        @EqualsAndHashCode.Include
        private final String name;
        @JsonProperty("backgroundColor")
        private final String backgroundColor;
        @JsonProperty("shadowColor")
        private final String shadowColor;

        public GraphNodeColor(String name, String backgroundColor, String shadowColor) {
            this.name = name;
            this.backgroundColor = backgroundColor;
            this.shadowColor = shadowColor;
        }
    }

    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class GraphReferenceColor {
        @JsonProperty("name")
        @EqualsAndHashCode.Include
        private final String name;
        @JsonProperty("color")
        private final String color;

        public GraphReferenceColor(String name, String color) {
            this.name = name;
            this.color = color;
        }
    }
}
