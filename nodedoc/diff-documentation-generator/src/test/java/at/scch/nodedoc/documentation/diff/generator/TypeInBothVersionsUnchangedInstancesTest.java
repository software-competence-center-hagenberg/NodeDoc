package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DiffDisplayInstance;
import at.scch.nodedoc.documentation.diff.DiffDisplayNode;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.diff.table.DiffTableCell;
import at.scch.nodedoc.documentation.diff.utils.InMemoryNodeSetUniverseBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeInBothVersionsUnchangedInstancesTest extends DiffTest {

    @Test
    @DisplayName("2.1.1: type in both versions | referenced instance in new version | reference added")
    void testReferencedInstanceOnlyInNewVersion() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .hasComponent(1, 2)
                .hasTypeDefinition(2, 3)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().stream().filter(o -> o.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:MachineType")).findFirst().get();

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.CHANGED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isEqualTo("1:MachineType");
            assertThat(heading.getText().getCompareValue()).isEqualTo("1:MachineType");
        });

        assertThat(objectType.getDefinitionTable().getTypeReferencesTableSection().getRows()).hasSize(1);
        var referenceRow = objectType.getDefinitionTable().getTypeReferencesTableSection().getRows().get(0);
        assertThat(((DiffTableCell<?>) referenceRow.getCells().get(0)).getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.ADDED);
        assertThat(referenceRow.getCells().get(2).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
            assertThat(diffView.getBaseValue()).isNull();
            assertThat(diffView.getCompareValue()).isEqualTo("1:DeviceState");
        });

        assertThat(objectType.getSubSections().get(0)).isInstanceOfSatisfying(DiffDisplayInstance.class, diffDisplayInstance -> {
            assertThat(diffDisplayInstance.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.ADDED);
            assertThat(diffDisplayInstance.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
                assertThat(heading.getText().getBaseValue()).isNull();
                assertThat(heading.getText().getCompareValue()).isEqualTo("1:DeviceState");
            });
        });
    }

    @Test
    @DisplayName("2.2.1: type in both versions | referenced instance in old version | reference removed")
    void testReferencedInstanceOnlyInOldVersion() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .hasComponent(1, 2)
                .hasTypeDefinition(2, 3)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().stream().filter(o -> o.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:MachineType")).findFirst().get();

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.CHANGED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isEqualTo("1:MachineType");
            assertThat(heading.getText().getCompareValue()).isEqualTo("1:MachineType");
        });

        assertThat(objectType.getDefinitionTable().getTypeReferencesTableSection().getRows()).hasSize(1);
        var referenceRow = objectType.getDefinitionTable().getTypeReferencesTableSection().getRows().get(0);
        assertThat(((DiffTableCell<?>) referenceRow.getCells().get(0)).getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.REMOVED);
        assertThat(referenceRow.getCells().get(2).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
            assertThat(diffView.getBaseValue()).isEqualTo("1:DeviceState");
            assertThat(diffView.getCompareValue()).isNull();
        });

        assertThat(objectType.getSubSections().get(0)).isInstanceOfSatisfying(DiffDisplayInstance.class, diffDisplayInstance -> {
            assertThat(diffDisplayInstance.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.REMOVED);
            assertThat(diffDisplayInstance.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
                assertThat(heading.getText().getBaseValue()).isEqualTo("1:DeviceState");
                assertThat(heading.getText().getCompareValue()).isNull();
            });
        });
    }

    @Test
    @DisplayName("2.3.1: type in both versions | referenced instance in both version | reference added")
    void testReferenceToInstanceOnlyInNewVersion() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .hasTypeDefinition(2, 3)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .hasComponent(1, 2)
                .hasTypeDefinition(2, 3)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().stream().filter(o -> o.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:MachineType")).findFirst().get();

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.CHANGED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isEqualTo("1:MachineType");
            assertThat(heading.getText().getCompareValue()).isEqualTo("1:MachineType");
        });

        assertThat(objectType.getDefinitionTable().getTypeReferencesTableSection().getRows()).hasSize(1);
        var referenceRow = objectType.getDefinitionTable().getTypeReferencesTableSection().getRows().get(0);
        assertThat(((DiffTableCell<?>) referenceRow.getCells().get(0)).getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.ADDED);
        assertThat(referenceRow.getCells().get(2).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
            assertThat(diffView.getBaseValue()).isNull();
            assertThat(diffView.getCompareValue()).isEqualTo("1:DeviceState");
        });

        assertThat(objectType.getSubSections().get(0)).isInstanceOfSatisfying(DiffDisplayInstance.class, diffDisplayInstance -> {
            assertThat(diffDisplayInstance.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
            assertThat(diffDisplayInstance.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
                assertThat(heading.getText().getBaseValue()).isNull();
                assertThat(heading.getText().getCompareValue()).isEqualTo("1:DeviceState");
            });
        });
    }

    @Test
    @DisplayName("2.3.2: type in both versions | referenced instance in both version | reference removed")
    void testReferenceToInstanceOnlyInOldVersion() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .hasComponent(1, 2)
                .hasTypeDefinition(2, 3)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .hasTypeDefinition(2, 3)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().stream().filter(o -> o.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:MachineType")).findFirst().get();

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.CHANGED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isEqualTo("1:MachineType");
            assertThat(heading.getText().getCompareValue()).isEqualTo("1:MachineType");
        });

        assertThat(objectType.getDefinitionTable().getTypeReferencesTableSection().getRows()).hasSize(1);
        var referenceRow = objectType.getDefinitionTable().getTypeReferencesTableSection().getRows().get(0);
        assertThat(((DiffTableCell<?>) referenceRow.getCells().get(0)).getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.REMOVED);
        assertThat(referenceRow.getCells().get(2).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
            assertThat(diffView.getBaseValue()).isEqualTo("1:DeviceState");
            assertThat(diffView.getCompareValue()).isNull();
        });

        assertThat(objectType.getSubSections().get(0)).isInstanceOfSatisfying(DiffDisplayInstance.class, diffDisplayInstance -> {
            assertThat(diffDisplayInstance.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
            assertThat(diffDisplayInstance.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
                assertThat(heading.getText().getBaseValue()).isEqualTo("1:DeviceState");
                assertThat(heading.getText().getCompareValue()).isNull();
            });
        });
    }

    @Test
    @DisplayName("2.3.3: type in both versions | referenced instance in both version | reference unchanged")
    void testReferenceToInstanceInBothVersions() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .hasComponent(1, 2)
                .hasTypeDefinition(2, 3)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .hasComponent(1, 2)
                .hasTypeDefinition(2, 3)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().stream().filter(o -> o.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:MachineType")).findFirst().get();

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isEqualTo("1:MachineType");
            assertThat(heading.getText().getCompareValue()).isEqualTo("1:MachineType");
        });

        assertThat(objectType.getDefinitionTable().getTypeReferencesTableSection().getRows()).hasSize(1);
        var referenceRow = objectType.getDefinitionTable().getTypeReferencesTableSection().getRows().get(0);
        assertThat(referenceRow.getCells().get(2).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
            assertThat(diffView.getBaseValue()).isEqualTo("1:DeviceState");
            assertThat(diffView.getCompareValue()).isEqualTo("1:DeviceState");
        });

        assertThat(objectType.getSubSections().get(0)).isInstanceOfSatisfying(DiffDisplayInstance.class, diffDisplayInstance -> {
            assertThat(diffDisplayInstance.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
            assertThat(diffDisplayInstance.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
                assertThat(heading.getText().getBaseValue()).isEqualTo("1:DeviceState");
                assertThat(heading.getText().getCompareValue()).isEqualTo("1:DeviceState");
            });
        });
    }

    @Test
    @DisplayName("2.3.4: type in both versions | referenced instance in both version | different reference")
    void testDifferentReferenceToInstanceInBothVersions() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .withReferenceType(300, "HasOrderedComponent")
                .withReference(1, 2, 300)
                .hasTypeDefinition(2, 3)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .withReferenceType(300, "HasOrderedComponent")
                .hasComponent(1, 2)
                .hasTypeDefinition(2, 3)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().stream().filter(o -> o.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:MachineType")).findFirst().get();

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.CHANGED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isEqualTo("1:MachineType");
            assertThat(heading.getText().getCompareValue()).isEqualTo("1:MachineType");
        });

        var references = objectType.getDefinitionTable().getTypeReferencesTableSection().getRows();
        assertThat(references).hasSize(2);
        assertThat(references).anySatisfy(row -> {
            assertThat(((DiffTableCell<?>) row.getCells().get(0)).getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.ADDED);
            assertThat(row.getCells().get(0).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
                assertThat(((DiffContext.DiffView<String>)diffView).getBaseOrElseCompareValue()).isEqualTo("HasComponent");
            });
            assertThat(row.getCells().get(2).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
                assertThat(diffView.getBaseValue()).isNull();
                assertThat(diffView.getCompareValue()).isEqualTo("1:DeviceState");
            });
        });

        assertThat(references).anySatisfy(row -> {
            assertThat(((DiffTableCell<?>) row.getCells().get(0)).getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.REMOVED);
            assertThat(row.getCells().get(0).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
                assertThat(((DiffContext.DiffView<String>)diffView).getBaseOrElseCompareValue()).isEqualTo("HasOrderedComponent");
            });
            assertThat(row.getCells().get(2).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
                assertThat(diffView.getBaseValue()).isEqualTo("1:DeviceState");
                assertThat(diffView.getCompareValue()).isNull();
            });
        });

        assertThat(objectType.getSubSections().get(0)).isInstanceOfSatisfying(DiffDisplayInstance.class, diffDisplayInstance -> {
            assertThat(diffDisplayInstance.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
            assertThat(diffDisplayInstance.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
                assertThat(heading.getText().getBaseValue()).isEqualTo("1:DeviceState");
                assertThat(heading.getText().getCompareValue()).isEqualTo("1:DeviceState");
            });
        });
    }
}
