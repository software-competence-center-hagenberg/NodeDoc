package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.UADataType;
import at.scch.nodedoc.nodeset.UAVariable;
import lombok.Getter;

@Getter
public class InMemoryArgument implements UAVariable.Argument {

    private final UADataType dataType;
    private final String name;

    public InMemoryArgument(UADataType dataType, String name) {
        this.dataType = dataType;
        this.name = name;
    }

    @Override
    public int getValueRank() {
        return 0;
    }

    @Override
    public String getArrayDimension() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setDescription(String description) {

    }
}
