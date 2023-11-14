package at.scch.nodedoc.nodeset;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public abstract class NodeId<T> {

    private final String namespaceUri;
    private final T id;

    public NodeId(String namespaceUri, T id) {
        this.namespaceUri = namespaceUri;
        this.id = id;
    }

    public static class IntNodeId extends NodeId<Integer> {

        public IntNodeId(String namespaceUri, Integer value) {
            super(namespaceUri, value);
        }

        @Override
        public String toExpandedNodeIdString() {
            var sb = new StringBuilder();
            sb.append("nsu=");
            sb.append(getNamespaceUri());
            sb.append(";i=");
            sb.append(getId());
            return sb.toString();
        }
    }

    public static class StringNodeId extends NodeId<String> {

        public StringNodeId(String namespaceUri, String value) {
            super(namespaceUri, value);
        }

        @Override
        public String toExpandedNodeIdString() {
            var sb = new StringBuilder();
            sb.append("nsu=");
            sb.append(getNamespaceUri());
            sb.append(";s=");
            sb.append(getId());
            return sb.toString();
        }
    }

    public static class GuidNodeId extends NodeId<String> {

        public GuidNodeId(String namespaceUri, String value) {
            super(namespaceUri, value);
        }

        @Override
        public String toExpandedNodeIdString() {
            var sb = new StringBuilder();
            sb.append("nsu=");
            sb.append(getNamespaceUri());
            sb.append(";g=");
            sb.append(getId());
            return sb.toString();
        }
    }

    public static class OpaqueNodeId extends NodeId<String> {

        public OpaqueNodeId(String namespaceUri, String value) {
            super(namespaceUri, value);
        }

        @Override
        public String toExpandedNodeIdString() {
            var sb = new StringBuilder();
            sb.append("nsu=");
            sb.append(getNamespaceUri());
            sb.append(";b=");
            sb.append(getId());
            return sb.toString();
        }
    }

    public abstract String toExpandedNodeIdString();
}
