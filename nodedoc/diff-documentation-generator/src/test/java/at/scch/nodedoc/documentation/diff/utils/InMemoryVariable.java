package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UADataType;
import at.scch.nodedoc.nodeset.UAVariable;
import lombok.Getter;

import java.util.List;

@Getter
public class InMemoryVariable extends InMemoryInstance implements UAVariable {

    private final List<Argument> arguments;
    private final UADataType dataType;

    public InMemoryVariable(NodeId<?> nodeId, String browseName, InMemoryNodeSetUniverse universe, List<Argument> arguments, UADataType dataType) {
        super(nodeId, browseName, universe);
        this.arguments = arguments;
        this.dataType = dataType;
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UAVariable;
    }

    @Override
    public int getValueRank() {
        return 0;
    }

    @Override
    public int getAccessLevel() {
        return 0;
    }

    @Override
    public int getUserAccessLevel() {
        return 0;
    }

    @Override
    public double getMinimumSamplingInterval() {
        return 0;
    }

    @Override
    public boolean isHistorizing() {
        return false;
    }

    @Override
    public String getArrayDimensions() {
        return null;
    }
}
