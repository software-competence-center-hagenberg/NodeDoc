package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.documentation.diff.DiffDisplayMethod;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.diff.utils.InMemoryNodeSetUniverseBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodDiffingTest extends DiffTest {

    Consumer<DiffDisplayMethod.DiffDisplayArgument> checkAddedArgument(String name) {
        return arg -> {
            assertThat(arg.getDiffType()).isEqualTo(DisplayDifferenceType.ADDED);
            assertThat(arg.getArgument().getCompareOrElseBaseValue().getName()).isEqualTo(name);
        };
    }

    Consumer<DiffDisplayMethod.DiffDisplayArgument> checkRemovedArgument(String name) {
        return arg -> {
            assertThat(arg.getDiffType()).isEqualTo(DisplayDifferenceType.REMOVED);
            assertThat(arg.getArgument().getBaseOrElseCompareValue().getName()).isEqualTo(name);
        };
    }

    Consumer<DiffDisplayMethod.DiffDisplayArgument> checkUnchangedArgument(String name) {
        return arg -> {
            assertThat(arg.getDiffType()).isEqualTo(DisplayDifferenceType.UNCHANGED);
            assertThat(arg.getArgument().getBaseOrElseCompareValue().getName()).isEqualTo(name);
        };
    }

    @Test
    void testNoArgumentChanges() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:ObjectType")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:Method")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(4, List.of("A", "B", "C"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(4, 5)
                .hasProperty(3, 4)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:ObjectType")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:Method")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(4, List.of("A", "B", "C"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(4, 5)
                .hasProperty(3, 4)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);

        var diffMethod = (DiffDisplayMethod) diff.getContent().getObjectTypes().get(0).getSubSections().get(0);

        assertThat(diffMethod.getArguments()).satisfiesExactly(
                checkUnchangedArgument("A"),
                checkUnchangedArgument("B"),
                checkUnchangedArgument("C")
        );
    }

    @Test
    void testNoArgumentChangesWithDifferentVariables() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:ObjectType")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:Method")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(4, List.of("A", "B", "C"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(4, 5)
                .hasProperty(3, 4)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:ObjectType")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:Method")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(40, List.of("A", "B", "C"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(40, 5)
                .hasProperty(3, 40)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);

        var diffMethod = (DiffDisplayMethod) diff.getContent().getObjectTypes().get(0).getSubSections().get(0);

        assertThat(diffMethod.getArguments()).satisfiesExactly(
                checkUnchangedArgument("A"),
                checkUnchangedArgument("B"),
                checkUnchangedArgument("C")
        );
    }

    @Test
    void testAddedArgumentAtTheStart() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:ObjectType")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:Method")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(4, List.of("A", "B"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(4, 5)
                .hasProperty(3, 4)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:ObjectType")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:Method")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(4, List.of("C", "A", "B"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(4, 5)
                .hasProperty(3, 4)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);

        var diffMethod = (DiffDisplayMethod) diff.getContent().getObjectTypes().get(0).getSubSections().get(0);

        assertThat(diffMethod.getArguments()).satisfiesExactly(
                checkRemovedArgument("A"),
                checkAddedArgument("C"),
                checkRemovedArgument("B"),
                checkAddedArgument("A"),
                checkAddedArgument("B")
        );
    }

    @Test
    void testAddedArgumentAtTheEnd() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:ObjectType")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:Method")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(4, List.of("A", "B"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(4, 5)
                .hasProperty(3, 4)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:ObjectType")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:Method")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(4, List.of("A", "B", "C"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(4, 5)
                .hasProperty(3, 4)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);

        var diffMethod = (DiffDisplayMethod) diff.getContent().getObjectTypes().get(0).getSubSections().get(0);

        assertThat(diffMethod.getArguments()).satisfiesExactly(
                checkUnchangedArgument("A"),
                checkUnchangedArgument("B"),
                checkAddedArgument("C")
        );
    }

    @Test
    void testArgumentsChanged() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:ObjectType")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:Method")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(4, List.of("A", "B"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(4, 5)
                .hasProperty(3, 4)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:ObjectType")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:Method")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(4, List.of("C", "A", "D", "B", "E"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(4, 5)
                .hasProperty(3, 4)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);

        var diffMethod = (DiffDisplayMethod) diff.getContent().getObjectTypes().get(0).getSubSections().get(0);

        assertThat(diffMethod.getArguments()).satisfiesExactly(
                checkRemovedArgument("A"),
                checkAddedArgument("C"),
                checkRemovedArgument("B"),
                checkAddedArgument("A"),
                checkAddedArgument("D"),
                checkAddedArgument("B"),
                checkAddedArgument("E")
        );
    }

    @Test
    void testArgumentsChangedWithDifferentVariables() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:ObjectType")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:Method")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(4, List.of("A", "B"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(4, 5)
                .hasProperty(3, 4)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:ObjectType")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:Method")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(40, List.of("C", "A", "D", "B", "E"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(40, 5)
                .hasProperty(3, 40)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);

        var diffMethod = (DiffDisplayMethod) diff.getContent().getObjectTypes().get(0).getSubSections().get(0);

        assertThat(diffMethod.getArguments()).satisfiesExactly(
                checkRemovedArgument("A"),
                checkAddedArgument("C"),
                checkRemovedArgument("B"),
                checkAddedArgument("A"),
                checkAddedArgument("D"),
                checkAddedArgument("B"),
                checkAddedArgument("E")
        );
    }

    @Test
    void testSameMethodInDifferentTypes() {
        var baseUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(1, "1:TypeA")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:OldMethodName")
                .hasComponent(1, 3)
                .withInputArgumentsVariable(4, List.of("A", "B"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(4, 5)
                .hasProperty(3, 4)
                .build();

        var compareUniverse = InMemoryNodeSetUniverseBuilder.universe(MAIN_NAMESPACE_URI)
                .withObjectType(10, "1:TypeB")
                .withDataType(2, "SomeDataType")
                .withMethod(3, "1:NewMethodName")
                .hasComponent(10, 3)
                .withInputArgumentsVariable(4, List.of("C", "D"), 2, 2)
                .withVariableType(5, "VariableType")
                .hasTypeDefinition(4, 5)
                .hasProperty(3, 4)
                .build();

        var diff = generateDiff(baseUniverse, compareUniverse);

        var methodInA = (DiffDisplayMethod) diff.getContent().getObjectTypes()
                .stream().filter(t -> t.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:TypeA"))
                .findFirst().get().getSubSections().get(0);

        var methodInB = (DiffDisplayMethod) diff.getContent().getObjectTypes()
                .stream().filter(t -> t.getHeadingTextValue().getBaseOrElseCompareValue().equals("1:TypeB"))
                .findFirst().get().getSubSections().get(0);

        assertThat(methodInA.getHeadingTextValue().getBaseValue()).isEqualTo("1:OldMethodName");
        assertThat(methodInA.getHeadingTextValue().getCompareValue()).isNull();
        assertThat(methodInA.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);

        assertThat(methodInA.getArguments()).satisfiesExactly(
                checkUnchangedArgument("A"),
                checkUnchangedArgument("B")
        );

        assertThat(methodInB.getHeadingTextValue().getBaseValue()).isNull();
        assertThat(methodInB.getHeadingTextValue().getCompareValue()).isEqualTo("1:NewMethodName");
        assertThat(methodInB.getDisplayDifferenceType()).isEqualTo(DisplayDifferenceType.UNCHANGED);

        assertThat(methodInB.getArguments()).satisfiesExactly(
                checkUnchangedArgument("C"),
                checkUnchangedArgument("D")
        );

    }
}
