package at.scch.nodedoc.documentation.displaymodel.table;

import at.scch.nodedoc.documentation.displaymodel.Renderable;

public class VariableTypeDefinitionTable extends TypeDefinitionTable<VariableTypeAttributesTableSection<?>> {

    public VariableTypeDefinitionTable(Renderable typeName, VariableTypeAttributesTableSection<?> variableTypeAttributesTableSection, TypeSubTypeTableSection subTypeTableSection, TypeReferencesTableSection typeReferencesTableSection) {
        super(typeName, variableTypeAttributesTableSection, subTypeTableSection, typeReferencesTableSection);
    }
}
