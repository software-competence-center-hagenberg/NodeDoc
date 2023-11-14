package at.scch.nodedoc.documentation.displaymodel.table;

import at.scch.nodedoc.documentation.displaymodel.Renderable;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class DataTypeDefinitionTable extends TypeDefinitionTable<TypeAttributesTableSection<?>> {

    private final DataTypeTableSection dataTypeTableSection;

    public DataTypeDefinitionTable(Renderable typeName, TypeAttributesTableSection<?> typeAttributesTableSection, TypeSubTypeTableSection subTypeTableSection, TypeReferencesTableSection typeReferencesTableSection, DataTypeTableSection dataTypeTableSection) {
        super(typeName, typeAttributesTableSection, subTypeTableSection, typeReferencesTableSection);
        this.dataTypeTableSection = dataTypeTableSection;
    }

    @Override
    public List<Section> getSections() {
        return Stream.concat(
                super.getSections().stream(),
                Stream.of(dataTypeTableSection)
        ).collect(Collectors.toList());
    }
}
