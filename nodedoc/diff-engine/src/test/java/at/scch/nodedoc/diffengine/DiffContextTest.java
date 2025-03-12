package at.scch.nodedoc.diffengine;

import at.scch.nodedoc.nodeset.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.*;

public class DiffContextTest {

    UADataType dataTypeA1;
    UADataType dataTypeA2;

    UADataType dataTypeB;

    UADataType dataTypeC1;
    UADataType dataTypeC2;

    UADataType dataTypeD1;
    UADataType dataTypeD2;

    UAVariable variableA1;
    UAVariable variableA2;

    UAVariable variableB1;
    UAVariable variableB2;

    UAReferenceType referenceType1;
    UAReferenceType referenceType2;

    NodeSetUniverse universe1;
    NodeSetUniverse universe2;

    DiffContext diffContext;

    NodeId.IntNodeId nodeId(int id) {
        return new NodeId.IntNodeId("http://mock", id);
    }

    <T extends UANode> T createMock(Class<T> clazz, int id, String browseName) {
        var result = mock(clazz);
        doReturn(nodeId(id)).when(result).getNodeId();
        when(result.getBrowseName()).thenReturn(browseName);
        return result;
    }

    DefinitionField createDefinitionFieldMock(String name, UADataType datatype) {
        var result = mock(DefinitionField.class);
        when(result.getName()).thenReturn(name);
        when(result.getDataType()).thenReturn(datatype);
        return result;
    }

    NodeSetUniverse.Reference createReference(UANode source, UANode target, UAReferenceType referenceType) {
        return new NodeSetUniverse.Reference(source, target, referenceType) {
            @Override
            public int hashCode() {
                return Objects.hash(
                        source.getNodeId(),
                        target.getNodeId(),
                        referenceType.getNodeId()
                );
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                NodeSetUniverse.Reference reference = (NodeSetUniverse.Reference) o;
                return Objects.equals(source.getNodeId(), reference.getSource().getNodeId())
                        && Objects.equals(target.getNodeId(), reference.getTarget().getNodeId())
                        && Objects.equals(referenceType.getNodeId(), reference.getReferenceType().getNodeId());
            }

        };
    }

    UAVariable.Argument createArgument(String name) {
        var result = mock(UAVariable.Argument.class);
        when(result.getName()).thenReturn(name);
        return result;
    }

    @BeforeEach
    void setup() {
        dataTypeA1 = createMock(UADataType.class, 1, "DataTypeA");
        dataTypeA2 = createMock(UADataType.class, 1, "DataTypeA");

        dataTypeB = createMock(UADataType.class, 2, "DataTypeB");

        dataTypeC1 = createMock(UADataType.class, 3, "OldDataTypeC");
        dataTypeC2 = createMock(UADataType.class, 3, "NewDataTypeC");

        dataTypeD1 = createMock(UADataType.class, 4, "DataTypeD");
        var definitionFields1 = List.of(
                createDefinitionFieldMock("DefFieldA", dataTypeA1),
                createDefinitionFieldMock("DefFieldB", dataTypeB),
                createDefinitionFieldMock("DefFieldC", dataTypeC1),
                createDefinitionFieldMock("DefFieldE", dataTypeA1)
        );
        when(dataTypeD1.getDefinition()).thenReturn(definitionFields1);
        dataTypeD2 = createMock(UADataType.class, 4, "DataTypeD");
        var definitionFields2 = List.of(
                createDefinitionFieldMock("DefFieldA", dataTypeA2),
                createDefinitionFieldMock("DefFieldC", dataTypeC2),
                createDefinitionFieldMock("DefFieldD", dataTypeA2),
                createDefinitionFieldMock("DefFieldE", dataTypeC2)
        );
        when(dataTypeD2.getDefinition()).thenReturn(definitionFields2);

        variableA1 = createMock(UAVariable.class, 5, "OldVariableA");
        when(variableA1.getDataType()).thenReturn(dataTypeD1);
        variableA2 = createMock(UAVariable.class, 5, "NewVariableA");
        when(variableA2.getDataType()).thenReturn(dataTypeD2);

        variableB1 = createMock(UAVariable.class, 6, "VariableB");
        when(variableB1.getDataType()).thenReturn(dataTypeA1);
        variableB2 = createMock(UAVariable.class, 6, "VariableB");
        when(variableB2.getDataType()).thenReturn(dataTypeC2);

        referenceType1 = createMock(UAReferenceType.class, 7, "Reference");
        referenceType2 = createMock(UAReferenceType.class, 7, "Reference");

        universe1 = mock(NodeSetUniverse.class);
        when(universe1.getAllNodes()).thenReturn(Set.of(
                dataTypeD1,
                variableA1,
                dataTypeA1,
                dataTypeB,
                dataTypeC1,
                variableB1,
                referenceType1
        ));
        when(universe1.getUADataTypes()).thenReturn(Set.of(
                dataTypeA1,
                dataTypeB,
                dataTypeC1,
                dataTypeD1
        ));
        var references1 = Set.of(
                createReference(dataTypeA1, dataTypeC1, referenceType1),
                createReference(dataTypeA1, dataTypeB, referenceType1)
        );
        when(universe1.getReferences()).thenReturn(references1);

        universe2 = mock(NodeSetUniverse.class);
        when(universe2.getAllNodes()).thenReturn(Set.of(
                dataTypeD2,
                variableA2,
                dataTypeA2,
                dataTypeC2,
                variableB2,
                referenceType2
        ));
        when(universe2.getUADataTypes()).thenReturn(Set.of(
                dataTypeA2,
                dataTypeC2,
                dataTypeD2
        ));
        var references2 = Set.of(
                createReference(dataTypeA2, dataTypeC2, referenceType2),
                createReference(dataTypeA2, variableA2, referenceType2)
        );
        when(universe2.getReferences()).thenReturn(references2);

        diffContext = new DiffContext(universe1, universe2);
    }

    @Test
    void getNodeByIdReturnsDiffView() {
        var diffObject = diffContext.getNodeById(nodeId(5), UAObject.class);
        assertThat(diffObject).isNotNull();
    }

    @ParameterizedTest
    @MethodSource
    void getNodeByIdReturnsDiffViewForNode(Class<? extends UANode> clazz, int id, String expectedBaseBrowseName, String expectedCompareBrowseName, DiffContext.ValueDiffType expectedValueDiffType) {
        var diffNode = diffContext.getNodeById(nodeId(id), clazz);
        var nodeId = diffNode.getKeyProperty(UANode::getNodeId);
        assertThat(nodeId).isEqualTo(nodeId(id));
        var browseName = diffNode.getProperty(UANode::getBrowseName);
        assertThat(browseName.getBaseValue()).isEqualTo(expectedBaseBrowseName);
        assertThat(browseName.getCompareValue()).isEqualTo(expectedCompareBrowseName);
        assertThat(browseName.getDiffType()).isEqualTo(expectedValueDiffType);
    }

    static Stream<Arguments> getNodeByIdReturnsDiffViewForNode() {
        return Stream.of(
                Arguments.arguments(UADataType.class, 1, "DataTypeA", "DataTypeA", DiffContext.ValueDiffType.UNCHANGED),
                Arguments.arguments(UADataType.class, 3, "OldDataTypeC", "NewDataTypeC", DiffContext.ValueDiffType.CHANGED),
                Arguments.arguments(UADataType.class, 2, "DataTypeB", null, DiffContext.ValueDiffType.UNCHANGED),
                Arguments.arguments(UAObject.class, 5, "OldVariableA", "NewVariableA", DiffContext.ValueDiffType.CHANGED),
                Arguments.arguments(UAObject.class, 6, "VariableB", "VariableB", DiffContext.ValueDiffType.UNCHANGED)
        );
    }

    @Test
    void navigateToDataTypeReturnsSameDataTypeDiffView() {
        var diffVariable = diffContext.getNodeById(nodeId(5), UAVariable.class);
        var diffRelation = diffVariable.navigate(UAVariable::getDataType);
        var dataTypeInBase = diffRelation.getDiffViewInBase();
        var dataTypeInCompare = diffRelation.getDiffViewInCompare();

        assertThat(dataTypeInBase).isSameAs(diffContext.getNodeById(nodeId(4), UADataType.class));
        assertThat(dataTypeInBase).isSameAs(dataTypeInCompare);
    }

    @Test
    void navigateToDataTypeReturnsDifferentDataTypeDiffView() {
        var diffVariable = diffContext.getNodeById(nodeId(6), UAVariable.class);
        var diffRelation = diffVariable.navigate(UAVariable::getDataType);
        var dataTypeInBase = diffRelation.getDiffViewInBase();
        var dataTypeInCompare = diffRelation.getDiffViewInCompare();

        assertThat(dataTypeInBase).isNotSameAs(dataTypeInCompare);
        assertThat(dataTypeInBase).isSameAs(diffContext.getNodeById(nodeId(1), UADataType.class));
        assertThat(dataTypeInCompare).isSameAs(diffContext.getNodeById(nodeId(3), UADataType.class));
    }

    @Test
    void getDefinitionFieldDiffSetForNode() {
        var diffNode = diffContext.getNodeById(nodeId(4), UADataType.class);
        var definitionFields = diffNode.getDiffSetWithValues((UADataType dataType) -> new HashSet<>(dataType.getDefinition()), DefinitionField::getName, String.class);

        assertThat(definitionFields).hasSize(5);
        assertThat(definitionFields)
                .extracting(
                        DiffContext.DiffCollectionEntry::getEntryDiffType,
                        field -> field.getValue().getProperty(DefinitionField::getName).getBaseOrElseCompareValue())
                .containsExactlyInAnyOrder(
                        tuple(DiffContext.EntryDiffType.UNCHANGED, "DefFieldA"),
                        tuple(DiffContext.EntryDiffType.REMOVED, "DefFieldB"),
                        tuple(DiffContext.EntryDiffType.UNCHANGED, "DefFieldC"),
                        tuple(DiffContext.EntryDiffType.ADDED, "DefFieldD"),
                        tuple(DiffContext.EntryDiffType.UNCHANGED, "DefFieldE")
                );
    }

    @Test
    void getReferenceDiffSet() {
        var diffRefs = diffContext.getUniverse()
                .getDiffSetWithValues(
                        NodeSetUniverse::getReferences,
                        Function.identity(),
                        NodeSetUniverse.Reference.class
                );

        assertThat(diffRefs).hasSize(3);
        assertThat(diffRefs)
                .extracting(
                        DiffContext.DiffCollectionEntry::getEntryDiffType,
                        entry -> entry.getValue().getProperty(NodeSetUniverse.Reference::getSource).getBaseOrElseCompareValue().getNodeId(),
                        entry -> entry.getValue().getProperty(NodeSetUniverse.Reference::getTarget).getBaseOrElseCompareValue().getNodeId()
                )
                .containsExactlyInAnyOrder(
                        tuple(DiffContext.EntryDiffType.UNCHANGED, dataTypeA1.getNodeId(), dataTypeC1.getNodeId()),
                        tuple(DiffContext.EntryDiffType.REMOVED, dataTypeA1.getNodeId(), dataTypeB.getNodeId()),
                        tuple(DiffContext.EntryDiffType.ADDED, dataTypeA2.getNodeId(), variableA2.getNodeId())
                );
    }

    @Test
    void getDiffSetWithNodesReturnsDiffSet() {
        var diffUniverse = diffContext.getUniverse();

        var diffSetWithNodes = diffUniverse.getDiffSetWithNodes(NodeSetUniverse::getUADataTypes);
        assertThat(diffSetWithNodes)
                .extracting(
                        DiffContext.DiffCollectionEntry::getEntryDiffType,
                        entry -> entry.getValue().getBaseValue(),
                        entry -> entry.getValue().getCompareValue()
                ).containsExactlyInAnyOrder(
                        tuple(DiffContext.EntryDiffType.UNCHANGED, dataTypeA1, dataTypeA2),
                        tuple(DiffContext.EntryDiffType.REMOVED, dataTypeB, null),
                        tuple(DiffContext.EntryDiffType.UNCHANGED, dataTypeC1, dataTypeC2),
                        tuple(DiffContext.EntryDiffType.UNCHANGED, dataTypeD1, dataTypeD2)
                );

    }

    @Test
    void getDiffListWithValuesReturnsDiffListWithAddedValues() {
        when(variableB1.getDataType()).thenReturn(dataTypeA1);
        var arguments1 = List.of(
                createArgument("A"),
                createArgument("B"),
                createArgument("C")
        );
        when(variableB1.getArguments()).thenReturn(arguments1);
        var arguments2 = List.of(
                createArgument("A"),
                createArgument("B"),
                createArgument("D"),
                createArgument("E")
        );
        when(variableB2.getArguments()).thenReturn(arguments2);
        var diffVariable = diffContext.getNodeById(nodeId(6), UAVariable.class);
        var diffList = diffVariable.getDiffListWithValues(UAVariable::getArguments, UAVariable.Argument::getName, String.class);
        assertThat(diffList)
                .extracting(
                        DiffContext.DiffCollectionEntry::getEntryDiffType,
                        entry -> entry.getValue().getKeyProperty(UAVariable.Argument::getName)
                ).containsExactly(
                        tuple(DiffContext.EntryDiffType.UNCHANGED, "A"),
                        tuple(DiffContext.EntryDiffType.UNCHANGED, "B"),
                        tuple(DiffContext.EntryDiffType.REMOVED, "C"),
                        tuple(DiffContext.EntryDiffType.ADDED, "D"),
                        tuple(DiffContext.EntryDiffType.ADDED, "E")
                );
    }

    @Test
    void getDiffListWithValuesReturnsDiffListWithChangedValues() {
        when(variableB1.getDataType()).thenReturn(dataTypeA1);
        var arguments1 = List.of(
                createArgument("A"),
                createArgument("B"),
                createArgument("D")
        );
        when(variableB1.getArguments()).thenReturn(arguments1);
        var arguments2 = List.of(
                createArgument("A"),
                createArgument("C"),
                createArgument("D")
        );
        when(variableB2.getArguments()).thenReturn(arguments2);
        var diffVariable = diffContext.getNodeById(nodeId(6), UAVariable.class);
        var diffList = diffVariable.getDiffListWithValues(UAVariable::getArguments, UAVariable.Argument::getName, String.class);
        assertThat(diffList)
                .extracting(
                        DiffContext.DiffCollectionEntry::getEntryDiffType,
                        entry -> entry.getValue().getKeyProperty(UAVariable.Argument::getName)
                ).containsExactly(
                        tuple(DiffContext.EntryDiffType.UNCHANGED, "A"),
                        tuple(DiffContext.EntryDiffType.REMOVED, "B"),
                        tuple(DiffContext.EntryDiffType.ADDED, "C"),
                        tuple(DiffContext.EntryDiffType.UNCHANGED, "D")
                );
    }

    @Test
    void getDiffListWithValuesReturnsDiffListWithRemovedValues() {
        when(variableB1.getDataType()).thenReturn(dataTypeA1);
        var arguments1 = List.of(
                createArgument("A"),
                createArgument("B"),
                createArgument("D"),
                createArgument("E")
        );
        when(variableB1.getArguments()).thenReturn(arguments1);
        var arguments2 = List.of(
                createArgument("A"),
                createArgument("B"),
                createArgument("C")
        );
        when(variableB2.getArguments()).thenReturn(arguments2);
        var diffVariable = diffContext.getNodeById(nodeId(6), UAVariable.class);
        var diffList = diffVariable.getDiffListWithValues(UAVariable::getArguments, UAVariable.Argument::getName, String.class);
        assertThat(diffList)
                .extracting(
                        DiffContext.DiffCollectionEntry::getEntryDiffType,
                        entry -> entry.getValue().getKeyProperty(UAVariable.Argument::getName)
                ).containsExactly(
                        tuple(DiffContext.EntryDiffType.UNCHANGED, "A"),
                        tuple(DiffContext.EntryDiffType.UNCHANGED, "B"),
                        tuple(DiffContext.EntryDiffType.REMOVED, "D"),
                        tuple(DiffContext.EntryDiffType.ADDED, "C"),
                        tuple(DiffContext.EntryDiffType.REMOVED, "E")
                );
    }
}
