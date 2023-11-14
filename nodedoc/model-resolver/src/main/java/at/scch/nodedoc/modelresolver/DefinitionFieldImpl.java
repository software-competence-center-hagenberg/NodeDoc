package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.DefinitionField;
import at.scch.nodedoc.nodeset.UADataType;
import at.scch.nodedoc.parser.rawModel.RawDefinitionField;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class DefinitionFieldImpl implements DefinitionField {

    @Getter(AccessLevel.PACKAGE)
    private final RawDefinitionField rawDefinitionField;

    public DefinitionFieldImpl(RawDefinitionField rawDefinitionField) {
        this.rawDefinitionField = rawDefinitionField;
    }

    @Override
    public String getDescription() {
        return rawDefinitionField.getDescription();
    }

    @Override
    public void setDescription(String description) {
        rawDefinitionField.setDescription(description);
    }

    @Override
    public String getName() {
        return rawDefinitionField.getName();
    }

    @Override
    public String getSymbolicName() {
        return rawDefinitionField.getSymbolicName();
    }

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private UADataType dataType;

    @Override
    public int getValueRank() {
        return rawDefinitionField.getValueRank();
    }

    @Override
    public int getValue() {
        return rawDefinitionField.getValue();
    }

    @Override
    public boolean isOptional() {
        return rawDefinitionField.isOptional();
    }
}
