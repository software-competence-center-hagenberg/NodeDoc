package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAObject;
import at.scch.nodedoc.parser.rawModel.RawObject;

public class UAObjectImpl extends UAInstanceImpl<RawObject> implements UAObject {

    public UAObjectImpl(RawObject rawNode, NodeId<?> nodeId) {
        super(rawNode, nodeId);
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UAObject;
    }

    @Override
    public int getEventNotifier() {
        return rawNode.getEventNotifier();
    }
}
