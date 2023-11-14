package at.scch.nodedoc.db.documents;

import at.scch.nodedoc.nodeset.DefinitionField;
import at.scch.nodedoc.nodeset.UAVariable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TextId {
    public static class Type {
        public static final String DOCUMENTATION = "documentation";
        public static final String DESCRIPTION = "description";
        public static final String ARGUMENT = "argument";
        public static final String FIELD = "field";
        public static final String FREETEXT = "freetext";
    }

    private String type;
    private String nodeId;
    private String fieldName;
    private String argumentName;
    private String headerId;

    private TextId(String type, String nodeId, String fieldName, String argumentName, String headerId) {
        this.type = type;
        this.nodeId = nodeId;
        this.fieldName = fieldName;
        this.argumentName = argumentName;
        this.headerId = headerId;
    }

    public static TextId forNodeDescription(at.scch.nodedoc.nodeset.NodeId<?> nodeId) {
        return new TextId(Type.DESCRIPTION, nodeId.toExpandedNodeIdString(), null, null, null);
    }

    public static TextId forNodeDocumentation(at.scch.nodedoc.nodeset.NodeId<?> nodeId) {
        return new TextId(Type.DOCUMENTATION, nodeId.toExpandedNodeIdString(), null, null, null);
    }

    public static TextId forField(at.scch.nodedoc.nodeset.NodeId<?> dataTypeNodeId, DefinitionField field) {
        return new TextId(Type.FIELD, dataTypeNodeId.toExpandedNodeIdString(), field.getName(), null, null);
    }

    public static TextId forArgument(at.scch.nodedoc.nodeset.NodeId<?> methodNodeId, UAVariable.Argument argument) {
        return new TextId(Type.ARGUMENT, methodNodeId.toExpandedNodeIdString(), null, argument.getName(), null);
    }

    public static TextId forFreetext(String headerId) {
        return new TextId(Type.FREETEXT, null, null, null, headerId);
    }
}
