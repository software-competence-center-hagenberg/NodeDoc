package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAReferenceType;
import at.scch.nodedoc.parser.rawModel.RawReferenceType;

import java.util.List;

public class UAReferenceTypeImpl extends UATypeImpl<RawReferenceType> implements UAReferenceType {

    public UAReferenceTypeImpl(RawReferenceType rawNode, NodeId<?> nodeId) {
        super(rawNode, nodeId);
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UAReferenceType;
    }

    @Override
    public boolean getSymmetric() {
        return rawNode.isSymmetric();
    }

    @Override
    public List<String> getInverseNames() {
        return rawNode.getInverseNames();
    }
}
