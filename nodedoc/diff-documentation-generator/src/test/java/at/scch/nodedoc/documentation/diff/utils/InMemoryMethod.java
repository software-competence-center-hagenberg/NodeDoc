package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAMethod;

public class InMemoryMethod extends InMemoryInstance implements UAMethod {

    public InMemoryMethod(NodeId<?> nodeId, String browseName, InMemoryNodeSetUniverse universe) {
        super(nodeId, browseName, universe);
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UAMethod;
    }

    @Override
    public UAMethod getMethodDeclaration() {
        return null;
    }

    @Override
    public boolean isExecutable() {
        return false;
    }

    @Override
    public boolean isUserExecutable() {
        return false;
    }
}
