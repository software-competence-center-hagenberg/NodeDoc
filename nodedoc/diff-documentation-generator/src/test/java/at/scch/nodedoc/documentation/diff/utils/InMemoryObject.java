package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAObject;

public class InMemoryObject extends InMemoryInstance implements UAObject {

    public InMemoryObject(NodeId<?> nodeId, String browseName, InMemoryNodeSetUniverse universe) {
        super(nodeId, browseName, universe);
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UAObject;
    }

    @Override
    public int getEventNotifier() {
        throw new UnsupportedOperationException();
    }
}
