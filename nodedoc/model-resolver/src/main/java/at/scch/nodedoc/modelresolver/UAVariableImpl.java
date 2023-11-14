package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UADataType;
import at.scch.nodedoc.nodeset.UAVariable;
import at.scch.nodedoc.parser.rawModel.RawVariable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class UAVariableImpl extends UAInstanceImpl<RawVariable> implements UAVariable {

    public UAVariableImpl(RawVariable rawNode, NodeId<?> nodeId) {
        super(rawNode, nodeId);
        this.arguments = rawNode.getArguments().stream().map(ArgumentImpl::new).collect(Collectors.toList());
    }

    @Override
    public NodeClass getNodeClass() {
        return NodeClass.UAVariable;
    }

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private UADataType dataType;

    @Getter
    private final List<Argument> arguments;

    @Override
    public int getValueRank() {
        return rawNode.getValueRank();
    }

    @Override
    public int getAccessLevel() {
        return rawNode.getAccessLevel();
    }

    @Override
    public int getUserAccessLevel() {
        return rawNode.getUserAccessLevel();
    }

    @Override
    public double getMinimumSamplingInterval() {
        return rawNode.getMinimumSamplingInterval();
    }

    @Override
    public boolean isHistorizing() {
        return rawNode.isHistorizing();
    }

    @Override
    public String getArrayDimensions() {
        return rawNode.getArrayDimensions();
    }

    public static class ArgumentImpl implements Argument {

        @Getter
        private final RawVariable.Argument rawArgument;

        @Getter
        @Setter(AccessLevel.PACKAGE)
        private UADataType dataType;

        public ArgumentImpl(RawVariable.Argument rawArgument) {
            this.rawArgument = rawArgument;
        }

        @Override
        public String getName() {
            return rawArgument.getName();
        }

        @Override
        public int getValueRank() {
            return rawArgument.getValueRank();
        }

        @Override
        public String getArrayDimension() {
            return rawArgument.getArrayDimension();
        }

        @Override
        public String getDescription() {
            return rawArgument.getDescription();
        }

        @Override
        public void setDescription(String description) {
            rawArgument.setDescription(description);
        }
    }
}
