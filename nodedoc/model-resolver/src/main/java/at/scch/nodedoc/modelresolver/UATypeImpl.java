package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAType;
import at.scch.nodedoc.parser.rawModel.RawType;
import at.scch.nodedoc.uaStandard.Nodes;


public abstract class UATypeImpl<Node extends RawType> extends UANodeImpl<Node> implements UAType {

    public UATypeImpl(Node rawNode, NodeId<?> nodeId) {
        super(rawNode, nodeId);
    }

    public boolean isAbstract() {
        return rawNode.isAbstract();
    }

    @Override
    public UAType getBaseType() {
        return getBackwardReferencedNodes(Nodes.ReferenceTypes.HAS_SUBTYPE).stream()
                .filter(node -> node instanceof UAType)
                .map(node -> (UAType) node)
                .findAny().orElse(null);
    }
}
