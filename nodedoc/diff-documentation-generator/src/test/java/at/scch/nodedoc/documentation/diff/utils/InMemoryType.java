package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAType;

public abstract class InMemoryType extends InMemoryNode implements UAType {

    public InMemoryType(NodeId<?> nodeId, String browseName, InMemoryNodeSetUniverse universe) {
        super(nodeId, browseName, universe);
    }

    @Override
    public boolean isAbstract() {
        return true;
    }

    @Override
    public UAType getBaseType() {
        throw new UnsupportedOperationException();
    }
}
