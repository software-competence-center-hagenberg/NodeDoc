package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAInstance;
import at.scch.nodedoc.nodeset.UANode;

public abstract class InMemoryInstance extends InMemoryNode implements UAInstance {

    public InMemoryInstance(NodeId<?> nodeId, String browseName, InMemoryNodeSetUniverse universe) {
        super(nodeId, browseName, universe);
    }

    @Override
    public UANode getParent() {
        throw new UnsupportedOperationException();
    }
}
