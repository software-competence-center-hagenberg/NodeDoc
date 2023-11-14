package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAObjectType;
import at.scch.nodedoc.parser.rawModel.RawObjectType;

public class UAObjectTypeImpl extends UATypeImpl<RawObjectType> implements UAObjectType {

    public UAObjectTypeImpl(RawObjectType rawNode, NodeId<?> nodeId) {
        super(rawNode, nodeId);
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UAObjectType;
    }
}
