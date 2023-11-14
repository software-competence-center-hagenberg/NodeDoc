package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DiffDisplayInstance;
import at.scch.nodedoc.documentation.diff.DiffDisplayNode;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.diff.table.DiffTableCell;
import at.scch.nodedoc.documentation.diff.utils.InMemoryNodeSetUniverseBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class InstancesWithTypeDefinitionChangesTest extends DiffTest {

    static final int INSTANCE_ID = 2;

    interface Case {
        Consumer<InMemoryNodeSetUniverseBuilder> getBaseConfigurer();
        Consumer<InMemoryNodeSetUniverseBuilder> getCompareConfigurer();
    }

    static class Case1 implements Case {

        @Override
        public String toString() {
            return "1: Different TypeDefinition used";
        }

        @Override
        public Consumer<InMemoryNodeSetUniverseBuilder> getBaseConfigurer() {
            return (builder) -> {
                builder
                        .withObjectType(3, "1:DeviceStateType")
                        .withObjectType(4, "1:StateType")
                        .hasTypeDefinition(INSTANCE_ID, 3);
            };
        }

        @Override
        public Consumer<InMemoryNodeSetUniverseBuilder> getCompareConfigurer() {
            return (builder) -> {
                builder
                        .withObjectType(3, "1:DeviceStateType")
                        .withObjectType(4, "1:StateType")
                        .hasTypeDefinition(INSTANCE_ID, 4);
            };
        }
    }

    static class Case2 implements Case {

        @Override
        public String toString() {
            return "2: BrowseName of TypeDefinition changed";
        }

        @Override
        public Consumer<InMemoryNodeSetUniverseBuilder> getBaseConfigurer() {
            return (builder) -> {
                builder
                        .withObjectType(3, "1:DeviceStateType")
                        .hasTypeDefinition(INSTANCE_ID, 3);
            };
        }

        @Override
        public Consumer<InMemoryNodeSetUniverseBuilder> getCompareConfigurer() {
            return (builder) -> {
                builder
                        .withObjectType(3, "1:DevStateType")
                        .hasTypeDefinition(INSTANCE_ID, 3);
            };
        }
    }

    static class Case3 implements Case {

        @Override
        public String toString() {
            return "3: BrowseName of TypeDefinition changed and different TypeDefinition used";
        }

        @Override
        public Consumer<InMemoryNodeSetUniverseBuilder> getBaseConfigurer() {
            return (builder) -> {
                builder
                        .withObjectType(3, "1:DeviceStateType")
                        .withObjectType(4, "1:StateType")
                        .hasTypeDefinition(INSTANCE_ID, 3);
            };
        }

        @Override
        public Consumer<InMemoryNodeSetUniverseBuilder> getCompareConfigurer() {
            return (builder) -> {
                builder
                        .withObjectType(3, "1:DevStateType")
                        .withObjectType(4, "1:MachineStateType")
                        .hasTypeDefinition(INSTANCE_ID, 4);
            };
        }
    }

    static Stream<Arguments> testReferenceToInstanceOnlyInNewVersion() {
        return Stream.of(
                Arguments.of(new Case1(), "1:StateType"),
                Arguments.of(new Case2(), "1:DevStateType"),
                Arguments.of(new Case3(), "1:MachineStateType")
        );
    }

    @ParameterizedTest(name = "Case {0}")
    @MethodSource
    @DisplayName("2.5.1: type in both versions | referenced instance in both version | reference added")
    void testReferenceToInstanceOnlyInNewVersion(Case c, String expectedTypeDefinition) {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .useConfigurer(c.getBaseConfigurer())
                .withObjectType(1, "1:MachineType")
                .withObject(INSTANCE_ID, "1:DeviceState")
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .useConfigurer(c.getCompareConfigurer())
                .withObjectType(1, "1:MachineType")
                .withObject(INSTANCE_ID, "1:DeviceState")
                .hasComponent(1, INSTANCE_ID)
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

        assertThat(referenceRow.getCells().get(4).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
            assertThat(diffView.getBaseValue()).isNull();
            assertThat(diffView.getCompareValue()).isEqualTo(expectedTypeDefinition);
        });

        assertThat(objectType.getSubSections().get(0)).isInstanceOfSatisfying(DiffDisplayInstance.class, diffDisplayInstance -> {
            assertThat(diffDisplayInstance.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
            assertThat(diffDisplayInstance.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
                assertThat(heading.getText().getBaseValue()).isNull();
                assertThat(heading.getText().getCompareValue()).isEqualTo("1:DeviceState");
            });
        });
    }

    static Stream<Arguments> testReferenceToInstanceOnlyInOldVersion() {
        return Stream.of(
                Arguments.of(new Case1(), "1:DeviceStateType"),
                Arguments.of(new Case2(), "1:DeviceStateType"),
                Arguments.of(new Case3(), "1:DeviceStateType")
        );
    }

    @ParameterizedTest(name = "Case {0}")
    @MethodSource
    @DisplayName("2.5.2: type in both versions | referenced instance in both version | reference removed")
    void testReferenceToInstanceOnlyInOldVersion(Case c, String expectedTypeDefinition) {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .useConfigurer(c.getBaseConfigurer())
                .withObjectType(1, "1:MachineType")
                .withObject(INSTANCE_ID, "1:DeviceState")
                .hasComponent(1, INSTANCE_ID)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .useConfigurer(c.getCompareConfigurer())
                .withObjectType(1, "1:MachineType")
                .withObject(INSTANCE_ID, "1:DeviceState")
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

        assertThat(referenceRow.getCells().get(4).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
            assertThat(diffView.getBaseValue()).isEqualTo(expectedTypeDefinition);
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


    static Stream<Arguments> testReferenceToInstanceInBothVersions() {
        return Stream.of(
                Arguments.of(new Case1(), "1:DeviceStateType", "1:StateType"),
                Arguments.of(new Case2(), "1:DeviceStateType", "1:DevStateType"),
                Arguments.of(new Case3(), "1:DeviceStateType", "1:MachineStateType")
        );
    }

    @ParameterizedTest(name = "Case {0}")
    @MethodSource
    @DisplayName("2.5.3: type in both versions | referenced instance in both version | reference unchanged")
    void testReferenceToInstanceInBothVersions(Case c, String expectedBaseTypeDefinition, String expectedCompareTypeDefinition) {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .useConfigurer(c.getBaseConfigurer())
                .withObjectType(1, "1:MachineType")
                .withObject(INSTANCE_ID, "1:DeviceState")
                .hasComponent(1, INSTANCE_ID)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .useConfigurer(c.getCompareConfigurer())
                .withObjectType(1, "1:MachineType")
                .withObject(INSTANCE_ID, "1:DeviceState")
                .hasComponent(1, INSTANCE_ID)
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
        assertThat(((DiffTableCell<?>) referenceRow.getCells().get(0)).getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
        assertThat(referenceRow.getCells().get(2).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
            assertThat(diffView.getBaseValue()).isEqualTo("1:DeviceState");
            assertThat(diffView.getCompareValue()).isEqualTo("1:DeviceState");
        });

        assertThat(referenceRow.getCells().get(4).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
            assertThat(diffView.getBaseValue()).isEqualTo(expectedBaseTypeDefinition);
            assertThat(diffView.getCompareValue()).isEqualTo(expectedCompareTypeDefinition);
        });

        assertThat(objectType.getSubSections().get(0)).isInstanceOfSatisfying(DiffDisplayInstance.class, diffDisplayInstance -> {
            assertThat(diffDisplayInstance.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.CHANGED);
            assertThat(diffDisplayInstance.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
                assertThat(heading.getText().getBaseValue()).isEqualTo("1:DeviceState");
                assertThat(heading.getText().getCompareValue()).isEqualTo("1:DeviceState");
            });
        });
    }

    static Stream<Arguments> testDifferentReferenceToInstanceInBothVersions() {
        return Stream.of(
                Arguments.of(new Case1(), "1:StateType", "1:DeviceStateType"),
                Arguments.of(new Case2(), "1:DevStateType", "1:DeviceStateType"),
                Arguments.of(new Case3(), "1:MachineStateType", "1:DeviceStateType")
        );
    }

    @ParameterizedTest(name = "Case {0}")
    @MethodSource
    @DisplayName("2.5.4: type in both versions | referenced instance in both version | reference unchanged")
    void testDifferentReferenceToInstanceInBothVersions(Case c, String expectedTypeDefinitionInAdded, String expectedTypeDefinitionInRemoved) {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .useConfigurer(c.getBaseConfigurer())
                .withObjectType(1, "1:MachineType")
                .withObject(INSTANCE_ID, "1:DeviceState")
                .withReferenceType(300, "HasOrderedComponent")
                .withReference(1, INSTANCE_ID, 300)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .useConfigurer(c.getCompareConfigurer())
                .withObjectType(1, "1:MachineType")
                .withObject(INSTANCE_ID, "1:DeviceState")
                .withReferenceType(300, "HasOrderedComponent")
                .hasComponent(1, INSTANCE_ID)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().stream().filter(o -> o.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:MachineType")).findFirst().get();

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.CHANGED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isEqualTo("1:MachineType");
            assertThat(heading.getText().getCompareValue()).isEqualTo("1:MachineType");
        });

        assertThat(objectType.getDefinitionTable().getTypeReferencesTableSection().getRows()).hasSize(2);
        assertThat(objectType.getDefinitionTable().getTypeReferencesTableSection().getRows()).anySatisfy(referenceRow -> {
            assertThat(referenceRow.getCells().get(0)).isInstanceOfSatisfying(DiffTableCell.class, cell -> {
                assertThat(cell.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.ADDED);
                assertThat(cell.getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
                    assertThat(diffView.getBaseOrElseCompareValue()).isEqualTo("HasComponent");
                });
            });
            assertThat(referenceRow.getCells().get(2).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
                assertThat(diffView.getBaseValue()).isNull();
                assertThat(diffView.getCompareValue()).isEqualTo("1:DeviceState");
            });

            assertThat(referenceRow.getCells().get(4).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
                assertThat(diffView.getBaseValue()).isNull();
                assertThat(diffView.getCompareValue()).isEqualTo(expectedTypeDefinitionInAdded);
            });
        });

        assertThat(objectType.getDefinitionTable().getTypeReferencesTableSection().getRows()).anySatisfy(referenceRow -> {
            assertThat(referenceRow.getCells().get(0)).isInstanceOfSatisfying(DiffTableCell.class, cell -> {
                assertThat(cell.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.REMOVED);
                assertThat(cell.getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
                    assertThat(diffView.getBaseOrElseCompareValue()).isEqualTo("HasOrderedComponent");
                });
            });
            assertThat(referenceRow.getCells().get(2).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
                assertThat(diffView.getBaseValue()).isEqualTo("1:DeviceState");
                assertThat(diffView.getCompareValue()).isNull();
            });

            assertThat(referenceRow.getCells().get(4).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
                assertThat(diffView.getBaseValue()).isEqualTo(expectedTypeDefinitionInRemoved);
                assertThat(diffView.getCompareValue()).isNull();
            });
        });

        assertThat(objectType.getSubSections().get(0)).isInstanceOfSatisfying(DiffDisplayInstance.class, diffDisplayInstance -> {
            assertThat(diffDisplayInstance.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.CHANGED);
            assertThat(diffDisplayInstance.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
                assertThat(heading.getText().getBaseValue()).isEqualTo("1:DeviceState");
                assertThat(heading.getText().getCompareValue()).isEqualTo("1:DeviceState");
            });
        });
    }

    static Stream<Arguments> testTypeOnlyInNewVersion() {
        return Stream.of(
                Arguments.of(new Case1(), "1:StateType"),
                Arguments.of(new Case2(), "1:DevStateType"),
                Arguments.of(new Case3(), "1:MachineStateType")
        );
    }

    @ParameterizedTest(name = "Case {0}")
    @MethodSource
    @DisplayName("3.4.1: type in new version | referenced instance in both version | reference added")
    void testTypeOnlyInNewVersion(Case c, String expectedTypeDefinition) {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .useConfigurer(c.getBaseConfigurer())
                .withObject(INSTANCE_ID, "1:DeviceState")
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .useConfigurer(c.getCompareConfigurer())
                .withObjectType(1, "1:MachineType")
                .withObject(INSTANCE_ID, "1:DeviceState")
                .hasComponent(1, INSTANCE_ID)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);
        var objectType = diff.getContent().getObjectTypes().stream().filter(o -> o.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:MachineType")).findFirst().get();

        assertThat(objectType.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.ADDED);
        assertThat(objectType.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
            assertThat(heading.getText().getBaseValue()).isNull();
            assertThat(heading.getText().getCompareValue()).isEqualTo("1:MachineType");
        });

        assertThat(objectType.getDefinitionTable().getTypeReferencesTableSection().getRows()).hasSize(1);
        var referenceRow = objectType.getDefinitionTable().getTypeReferencesTableSection().getRows().get(0);
        assertThat(((DiffTableCell<?>) referenceRow.getCells().get(0)).getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
        assertThat(referenceRow.getCells().get(2).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
            assertThat(diffView.getBaseValue()).isNull();
            assertThat(diffView.getCompareValue()).isEqualTo("1:DeviceState");
        });

        assertThat(referenceRow.getCells().get(4).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
            assertThat(diffView.getBaseValue()).isNull();
            assertThat(diffView.getCompareValue()).isEqualTo(expectedTypeDefinition);
        });

        assertThat(objectType.getSubSections().get(0)).isInstanceOfSatisfying(DiffDisplayInstance.class, diffDisplayInstance -> {
            assertThat(diffDisplayInstance.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
            assertThat(diffDisplayInstance.getHeadingText()).isInstanceOfSatisfying(DiffDisplayNode.DiffDisplayHeadingText.class, heading -> {
                assertThat(heading.getText().getBaseValue()).isNull();
                assertThat(heading.getText().getCompareValue()).isEqualTo("1:DeviceState");
            });
        });
    }

    static Stream<Arguments> testTypeOnlyInOldVersion() {
        return Stream.of(
                Arguments.of(new Case1(), "1:DeviceStateType"),
                Arguments.of(new Case2(), "1:DeviceStateType"),
                Arguments.of(new Case3(), "1:DeviceStateType")
        );
    }

    @ParameterizedTest(name = "Case {0}")
    @MethodSource
    @DisplayName("4.4.1: type in old version | referenced instance in both version | reference added")
    void testTypeOnlyInOldVersion(Case c, String expectedTypeDefinition) {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .useConfigurer(c.getBaseConfigurer())
                .withObjectType(1, "1:MachineType")
                .withObject(INSTANCE_ID, "1:DeviceState")
                .hasComponent(1, INSTANCE_ID)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .useConfigurer(c.getCompareConfigurer())
                .withObject(INSTANCE_ID, "1:DeviceState")
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

        assertThat(referenceRow.getCells().get(4).getValue()).isInstanceOfSatisfying(DiffContext.DiffView.class, diffView -> {
            assertThat(diffView.getBaseValue()).isEqualTo(expectedTypeDefinition);
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
