package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UADataType;
import at.scch.nodedoc.nodeset.UAVariableType;
import at.scch.nodedoc.parser.rawModel.RawVariableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class UAVariableTypeImpl extends UATypeImpl<RawVariableType> implements UAVariableType {

    public UAVariableTypeImpl(RawVariableType rawNode, NodeId<?> nodeId) {
        super(rawNode, nodeId);
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UAVariableType;
    }

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private UADataType dataType;

    @Override
    public int getValueRank() {
        return rawNode.getValueRank();
    }

    @Override
    public String getArrayDimensions() {
        return rawNode.getArrayDimensions();
    }
}
