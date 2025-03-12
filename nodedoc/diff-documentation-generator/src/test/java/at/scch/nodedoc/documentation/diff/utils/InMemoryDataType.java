package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.DefinitionField;
import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UADataType;

import java.util.List;

public class InMemoryDataType extends InMemoryType implements UADataType {

    public InMemoryDataType(NodeId<?> nodeId, String browseName, InMemoryNodeSetUniverse universe) {
        super(nodeId, browseName, universe);
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UADataType;
    }

    @Override
    public List<DefinitionField> getDefinition() {
        return List.of();
    }
}
