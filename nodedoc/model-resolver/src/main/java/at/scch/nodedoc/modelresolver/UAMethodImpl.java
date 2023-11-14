package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAMethod;
import at.scch.nodedoc.parser.rawModel.RawMethod;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class UAMethodImpl extends UAInstanceImpl<RawMethod> implements UAMethod {

    public UAMethodImpl(RawMethod rawNode, NodeId<?> nodeId) {
        super(rawNode, nodeId);
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UAMethod;
    }

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private UAMethod methodDeclaration;

    @Override
    public boolean isExecutable() {
        return rawNode.isExecutable();
    }

    @Override
    public boolean isUserExecutable() {
        return rawNode.isUserExecutable();
    }
}
