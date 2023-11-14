package at.scch.nodedoc.documentation.displaymodel.table;

import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.documentation.template.ConcatTemplate;
import at.scch.nodedoc.documentation.template.StringTemplate;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class TypeDefinitionTable<AttributesSection extends TypeAttributesTableSection<?>> extends DisplayTable {

    private final AttributesSection typeAttributesTableSection;
    private final TypeSubTypeTableSection subTypeTableSection;
    private final TypeReferencesTableSection typeReferencesTableSection;

    public TypeDefinitionTable(Renderable typeName, AttributesSection typeAttributesTableSection, TypeSubTypeTableSection subTypeTableSection, TypeReferencesTableSection typeReferencesTableSection) {
        super(content -> ConcatTemplate.template(typeName, x -> StringTemplate.template(" Definition")));
        this.typeAttributesTableSection = typeAttributesTableSection;
        this.subTypeTableSection = subTypeTableSection;
        this.typeReferencesTableSection = typeReferencesTableSection;
    }

    @Override
    public List<Section> getSections() {
        return List.of(
                typeAttributesTableSection,
                subTypeTableSection,
                typeReferencesTableSection
        );
    }
}
