package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UADataType;
import at.scch.nodedoc.nodeset.UAVariableType;

public class InMemoryVariableType extends InMemoryType implements UAVariableType {

    public InMemoryVariableType(NodeId<?> nodeId, String browseName, InMemoryNodeSetUniverse universe) {
        super(nodeId, browseName, universe);
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UAVariableType;
    }

    @Override
    public UADataType getDataType() {
        return null;
    }

    @Override
    public String getArrayDimensions() {
        return null;
    }

    @Override
    public int getValueRank() {
        return 0;
    }
}
