package at.scch.nodedoc.documentation.displaymodel.table;

import at.scch.nodedoc.documentation.displaymodel.Renderable;

public class GenericTypeDefinitionTable extends TypeDefinitionTable<TypeAttributesTableSection<?>> {
    public GenericTypeDefinitionTable(Renderable typeName, TypeAttributesTableSection<?> typeAttributesTableSection, TypeSubTypeTableSection subTypeTableSection, TypeReferencesTableSection typeReferencesTableSection) {
        super(typeName, typeAttributesTableSection, subTypeTableSection, typeReferencesTableSection);
    }
}
