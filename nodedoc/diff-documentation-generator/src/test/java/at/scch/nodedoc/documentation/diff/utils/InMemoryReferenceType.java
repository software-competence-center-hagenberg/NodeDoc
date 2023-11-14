package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAReferenceType;

import java.util.List;

public class InMemoryReferenceType extends InMemoryType implements UAReferenceType {

    public InMemoryReferenceType(NodeId<?> nodeId, String browseName, InMemoryNodeSetUniverse universe) {
        super(nodeId, browseName, universe);
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UAReferenceType;
    }

    @Override
    public boolean getSymmetric() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getInverseNames() {
        throw new UnsupportedOperationException();
    }
}
