package at.scch.nodedoc.nodeset;

import java.util.Set;

public interface UADataType extends UAType {

    Set<DefinitionField> getDefinition();
}
