package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAInstance;
import at.scch.nodedoc.nodeset.UANode;
import at.scch.nodedoc.parser.rawModel.RawInstance;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class UAInstanceImpl<Node extends RawInstance> extends UANodeImpl<Node> implements UAInstance {

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private UANode parent;

    public UAInstanceImpl(Node rawNode, NodeId<?> nodeId) {
        super(rawNode, nodeId);
    }
}
