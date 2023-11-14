package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.DefinitionField;
import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UADataType;
import at.scch.nodedoc.parser.rawModel.RawDataType;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

public class UADataTypeImpl extends UATypeImpl<RawDataType> implements UADataType {

    public UADataTypeImpl(RawDataType rawNode, NodeId<?> nodeId) {
        super(rawNode, nodeId);

        var definitionFields = rawNode.getDefinition();

        this.definition = definitionFields.stream()
                .map(DefinitionFieldImpl::new)
                .collect(Collectors.toSet());
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UADataType;
    }

    @Getter
    private final Set<DefinitionField> definition;

}
