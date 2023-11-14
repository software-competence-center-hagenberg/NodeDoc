package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.documentation.diff.DiffDisplayNode;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.diff.utils.InMemoryNodeSetUniverseBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeWithoutInstancesTest extends DiffTest {

    @Test
    @DisplayName("type added")
    void testTypeWithoutInstancesAdded() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().get(0);

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.ADDED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isNull();
            assertThat(heading.getText().getCompareValue()).isEqualTo("1:MachineType");
        });
    }

    @Test
    @DisplayName("type removed")
    void testTypeWithoutInstancesRemoved() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().get(0);

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.REMOVED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isEqualTo("1:MachineType");
            assertThat(heading.getText().getCompareValue()).isNull();
        });
    }

    @Test
    @DisplayName("type in both versions | BrowseName changed")
    void testTypeWithoutInstancesRenamed() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineType")
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:MachineTypeNew")
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().get(0);

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.CHANGED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isEqualTo("1:MachineType");
            assertThat(heading.getText().getCompareValue()).isEqualTo("1:MachineTypeNew");
        });
    }
}
