package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAView;
import at.scch.nodedoc.parser.rawModel.RawView;

public class UAViewImpl extends UAInstanceImpl<RawView> implements UAView {

    public UAViewImpl(RawView rawNode, NodeId<?> nodeId) {
        super(rawNode, nodeId);
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UAView;
    }

    @Override
    public boolean isContainsNoLoops() {
        return rawNode.isContainsNoLoops();
    }

    @Override
    public int getEventNotifier() {
        return rawNode.getEventNotifier();
    }
}
