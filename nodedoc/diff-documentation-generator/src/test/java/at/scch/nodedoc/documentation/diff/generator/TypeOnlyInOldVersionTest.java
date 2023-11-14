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

public class TypeOnlyInOldVersionTest extends DiffTest {

    @Test
    @DisplayName("4.1.1: type in old version | referenced instance in old version | reference removed")
    void testReferencedInstanceOnlyInNewVersion() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .hasComponent(1, 2)
                .hasTypeDefinition(2, 3)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().stream().filter(o -> o.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:MachineType")).findFirst().get();

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.REMOVED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isEqualTo("1:MachineType");
            assertThat(heading.getText().getCompareValue()).isNull();
        });

        assertThat(objectType.getDefinitionTable().getTypeReferencesTableSection().getRows()).hasSize(1);
        var referenceRow = objectType.getDefinitionTable().getTypeReferencesTableSection().getRows().get(0);
        assertThat(((DiffTableCell<?>) referenceRow.getCells().get(0)).getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
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
    @DisplayName("4.2.1: type in old version | referenced instance in both version | reference removed")
    void testReferencedInstanceInBothVersions() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .hasComponent(1, 2)
                .hasTypeDefinition(2, 3)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .hasTypeDefinition(2, 3)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().stream().filter(o -> o.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:MachineType")).findFirst().get();

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.REMOVED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isEqualTo("1:MachineType");
            assertThat(heading.getText().getCompareValue()).isNull();
        });

        assertThat(objectType.getDefinitionTable().getTypeReferencesTableSection().getRows()).hasSize(1);
        var referenceRow = objectType.getDefinitionTable().getTypeReferencesTableSection().getRows().get(0);
        assertThat(((DiffTableCell<?>) referenceRow.getCells().get(0)).getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
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
    @DisplayName("4.3.1: type in old version | referenced instance in both version (changed) | reference removed")
    void testReferencedInstanceInBothVersionsAndChanged() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .withObject(2, "1:DeviceState")
                .withObjectType(3, "1:DeviceStateType")
                .hasComponent(1, 2)
                .hasTypeDefinition(2, 3)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObject(2, "1:DevState")
                .withObjectType(3, "1:DeviceStateType")
                .hasTypeDefinition(2, 3)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().stream().filter(o -> o.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:MachineType")).findFirst().get();

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.REMOVED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isEqualTo("1:MachineType");
            assertThat(heading.getText().getCompareValue()).isNull();
        });

        assertThat(objectType.getDefinitionTable().getTypeReferencesTableSection().getRows()).hasSize(1);
        var referenceRow = objectType.getDefinitionTable().getTypeReferencesTableSection().getRows().get(0);
        assertThat(((DiffTableCell<?>) referenceRow.getCells().get(0)).getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
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
}
