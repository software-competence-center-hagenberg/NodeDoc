package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.ModelMetaData;
import at.scch.nodedoc.nodeset.*;
import at.scch.nodedoc.testkit.ClasspathModelRepository;
import at.scch.nodedoc.uaStandard.Namespaces;
import at.scch.nodedoc.uaStandard.Nodes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ModelResolverUniverseTest {

    private static NodeSetUniverse universe;
    private static String plasticsRubberNamespaceUri;

    private static UANodeSet plasticsRubberNodeSet;

    private NodeId<?> createPlasticsRubberNodeId(int nodeId) {
        return new NodeId.IntNodeId(
                ClasspathModelRepository.Models.PLASTICS_RUBBER_1_02.getModelUri(),
                nodeId);
    }

    private NodeId<?> createUANodeId(int nodeId) {
        return new NodeId.IntNodeId(
                ClasspathModelRepository.Models.OPC_UA_1_04_3.getModelUri(),
                nodeId);
    }

    private <T extends UANode> T getNode(Collection<T> nodes, int nodeId) {
        return nodes.stream()
                .filter(node -> node.getNodeId().equals(createPlasticsRubberNodeId(nodeId)))
                .findAny().orElse(null);
    }

    private UANode getNodeFromUA(int nodeId) {
        return universe.getNodeSetByNamespaceUri(Namespaces.UA).getAllNodes().stream()
                .filter(node -> node.getNodeId().equals(createUANodeId(nodeId)))
                .findAny().orElse(null);
    }

    @BeforeAll
    public static void setup() {
        var modelRepository = new ClasspathModelRepository();
        var nodeIdParser = new NodeIdParser();
        var referenceResolver = new ReferenceResolver(nodeIdParser);
        var modelResolver = new ModelResolver(modelRepository, nodeIdParser, referenceResolver);
        var pinnedVersions = List.of(
                ClasspathModelRepository.Models.PLASTICS_RUBBER_1_02,
                ClasspathModelRepository.Models.OPC_UA_1_04_3,
                ClasspathModelRepository.Models.OPC_UA_DI_1_02
        );
        universe = modelResolver.loadNodeSetUniverse(
                pinnedVersions
        );
        plasticsRubberNamespaceUri = ClasspathModelRepository.Models.PLASTICS_RUBBER_1_02.getModelUri();
        plasticsRubberNodeSet = universe.getNodeSetByNamespaceUri(plasticsRubberNamespaceUri);
    }

    @Test
    public void checkNodeCount() {
        assertEquals(64, plasticsRubberNodeSet.getUAObjects().size());
        assertEquals(713, plasticsRubberNodeSet.getUAVariables().size());
        assertEquals(76, plasticsRubberNodeSet.getUAMethods().size());
        assertEquals(0, plasticsRubberNodeSet.getUAViews().size());
        assertEquals(68, plasticsRubberNodeSet.getUAObjectTypes().size());
        assertEquals(0, plasticsRubberNodeSet.getUAVariableTypes().size());
        assertEquals(27, plasticsRubberNodeSet.getUADataTypes().size());
        assertEquals(0, plasticsRubberNodeSet.getUAReferenceTypes().size());
        assertEquals(948, plasticsRubberNodeSet.getAllNodes().size());
    }

    @Test
    public void checkRequiredNodeSetIdentity() {
        var ua = universe.getNodeSetByNamespaceUri(Namespaces.UA);
        var di = universe.getNodeSetByNamespaceUri(Namespaces.UA_DI);
        var uaFromDi = di.getRequiredNodeSets().stream().filter(x -> x.getModelUri().equals(Namespaces.UA)).findAny().orElseThrow();
        assertSame(ua, uaFromDi);
    }

    @Test
    public void checkReferencedNodeIdentityOneNodeSetAway() {
        var nodeIdToCheck = new NodeId.IntNodeId(Namespaces.UA_DI, 15063);
        var nodeReferencedInOriginalNodeSet = universe
                .getNodeSetByNamespaceUri(plasticsRubberNamespaceUri)
                .getNodeById(new NodeId.IntNodeId(plasticsRubberNamespaceUri, 1058))
                .getBackwardReferencedNodes(Nodes.ReferenceTypes.HAS_SUBTYPE).stream()
                .filter(node -> node.getNodeId().equals(nodeIdToCheck))
                .findAny().orElseThrow();
        var di = universe.getNodeSetByNamespaceUri(Namespaces.UA_DI);
        var nodeReferencedInRequiredNodeSet = di.getNodeById(nodeIdToCheck);
        assertSame(nodeReferencedInOriginalNodeSet, nodeReferencedInRequiredNodeSet);
    }

    @Test
    public void checkRequiredNodeSets() {
        assertEquals(2, plasticsRubberNodeSet.getRequiredNodeSets().size());

        assertTrue(plasticsRubberNodeSet.getRequiredNodeSets().stream()
                .anyMatch(requiredNodeSet -> requiredNodeSet.getModelUri().equals(ClasspathModelRepository.Models.OPC_UA_1_04_3.getModelUri())));

        assertTrue(plasticsRubberNodeSet.getRequiredNodeSets().stream()
                .anyMatch(requiredNodeSet -> requiredNodeSet.getModelUri().equals(ClasspathModelRepository.Models.OPC_UA_DI_1_02.getModelUri())));
    }

    @Test
    public void checkNodeSetsInUniverse() {
        assertEquals(3, universe.getAllNodeSets().size());
        assertThat(universe.getAllNodeSets().stream()
                .map(nodeSet -> new ModelMetaData(nodeSet.getModelUri(), nodeSet.getVersion(), nodeSet.getPublicationDate()))
        ).containsExactlyInAnyOrder(
                ClasspathModelRepository.Models.PLASTICS_RUBBER_1_02,
                ClasspathModelRepository.Models.OPC_UA_1_04_3,
                ClasspathModelRepository.Models.OPC_UA_DI_1_02
        );
    }

    @Test
    public void checkNodeSetsByNamespaceIndex() {
        assertEquals(3, plasticsRubberNodeSet.getNodeSetsByNamespaceIndex().size());

        assertEquals(
                ClasspathModelRepository.Models.OPC_UA_1_04_3.getModelUri(),
                plasticsRubberNodeSet.getNodeSetsByNamespaceIndex().get(0).getModelUri()
        );
        assertEquals(
                ClasspathModelRepository.Models.PLASTICS_RUBBER_1_02.getModelUri(),
                plasticsRubberNodeSet.getNodeSetsByNamespaceIndex().get(1).getModelUri()
        );
        assertEquals(
                ClasspathModelRepository.Models.OPC_UA_DI_1_02.getModelUri(),
                plasticsRubberNodeSet.getNodeSetsByNamespaceIndex().get(2).getModelUri()
        );
    }

    @Test
    public void checkObject() {
        var object = getNode(plasticsRubberNodeSet.getUAObjects(), 5007);

        assertNotNull(object);
        assertEquals("1:ProductionDatasetLists", object.getBrowseName());
        assertEquals("ProductionDatasetLists", object.getDisplayName());
        assertEquals("Functions for exchanging information on the available production datasets on client and server", object.getDescription());
        assertEquals(NodeClass.UAObject, object.getNodeClass());
        assertEquals(1, object.getEventNotifier());
    }

    @Test
    public void checkObjectReferences() {
        var object = getNode(plasticsRubberNodeSet.getUAObjects(), 5033);
        var parentNode = object.getParent();
        assertEquals(createPlasticsRubberNodeId(1032), parentNode.getNodeId());
        assertEquals("1:JobsType", parentNode.getBrowseName());

        var forwardReferences = object.getForwardReferences();
        assertEquals(11, forwardReferences.size());

        assertTrue(forwardReferences.entries().stream()
                .filter(entry -> entry.getKey().getNodeId().equals(Nodes.ReferenceTypes.HAS_PROPERTY))
                .anyMatch(entry -> entry.getValue().getNodeId().equals(createPlasticsRubberNodeId(6298))));

        var backwardReferences = object.getBackwardReferences();
        assertEquals(1, backwardReferences.size());
    }

    @Test
    public void checkVariable() {
        var variable = getNode(plasticsRubberNodeSet.getUAVariables(), 6519);

        assertNotNull(variable);
        assertEquals("1:ActiveErrorDataType", variable.getBrowseName());
        assertEquals("ActiveErrorDataType", variable.getDisplayName());
        assertEquals(NodeClass.UAVariable, variable.getNodeClass());
        assertNull(variable.getDescription());
        assertEquals(createUANodeId(12), variable.getDataType().getNodeId());
        assertEquals(-1, variable.getValueRank());
        assertEquals("", variable.getArrayDimensions());
        assertEquals(1, variable.getAccessLevel());
        assertEquals(1, variable.getUserAccessLevel());
        assertEquals(0, variable.getMinimumSamplingInterval());
        assertFalse(variable.isHistorizing());

    }

    @Test
    public void checkVariableArgument() {
        var variable = getNode(plasticsRubberNodeSet.getUAVariables(), 6268);

        assertNotNull(variable);
        assertEquals(14, variable.getArguments().size());
        var jobNameArg = variable.getArguments().get(0);
        assertEquals("JobName", jobNameArg.getName());
        assertEquals(getNodeFromUA(12).getNodeId(), jobNameArg.getDataType().getNodeId());
        assertEquals(-1, jobNameArg.getValueRank());
        assertEquals("", jobNameArg.getArrayDimension());
        assertEquals("", jobNameArg.getDescription());
    }

    @Test
    public void checkMethod() {
        var method = getNode(plasticsRubberNodeSet.getUAMethods(), 7038);

        assertNotNull(method);
        assertEquals("1:SetWatchDogTime", method.getBrowseName());
        assertEquals("SetWatchDogTime", method.getDisplayName());
        assertEquals("Release of production for a given time", method.getDescription());
        assertEquals(NodeClass.UAMethod, method.getNodeClass());

        assertTrue(method.isExecutable());
        assertTrue(method.isUserExecutable());
        assertEquals(getNode(plasticsRubberNodeSet.getUAMethods(), 7029), method.getMethodDeclaration());
    }

    @Test
    public void checkMethodReferences() {
        var method = getNode(plasticsRubberNodeSet.getUAMethods(), 7038);

        var forwardReferences = method.getForwardReferences();
        assertEquals(2, forwardReferences.size());
        assertTrue(forwardReferences.entries().stream()
                .filter(entry -> entry.getKey().getNodeId().equals(Nodes.ReferenceTypes.HAS_PROPERTY))
                .anyMatch(entry -> entry.getValue().getNodeId().equals(createPlasticsRubberNodeId(6231))));

        var backwardReferences = method.getBackwardReferences();
        assertEquals(1, backwardReferences.size());
        assertTrue(backwardReferences.entries().stream()
                .filter(entry -> entry.getKey().getNodeId().equals(Nodes.ReferenceTypes.HAS_COMPONENT))
                .anyMatch(entry -> entry.getValue().getNodeId().equals(createPlasticsRubberNodeId(5030))));
    }

    @Test
    @Disabled
    public void checkView() {
        throw new RuntimeException("not implemented");
    }

    @Test
    public void checkObjectType() {
        var objectType = getNode(plasticsRubberNodeSet.getUAObjectTypes(), 1004);

        assertNotNull(objectType);
        assertEquals("1:MessageConditionType", objectType.getBrowseName());
        assertEquals("MessageConditionType", objectType.getDisplayName());
        assertEquals("Text messages (incl. error messages) of the control system currently shown on the screen of the machine", objectType.getDescription());
        assertEquals(NodeClass.UAObjectType, objectType.getNodeClass());
    }

    @Test
    public void checkObjectTypeReferences() {
        var objectType = getNode(plasticsRubberNodeSet.getUAObjectTypes(), 1004);

        var forwardReferences = objectType.getForwardReferences();
        assertEquals(3, forwardReferences.size());
        var backwardReferences = objectType.getBackwardReferences();
        assertEquals(2, backwardReferences.size());
    }

    @Test
    public void checkVariableType() {
        var variableType = (UAVariableType) getNodeFromUA(2164);

        assertEquals("SamplingIntervalDiagnosticsArrayType", variableType.getBrowseName());
        assertEquals(createUANodeId(856), variableType.getDataType().getNodeId());
        assertEquals(1, variableType.getValueRank());
        assertEquals("0", variableType.getArrayDimensions());
        assertEquals(NodeClass.UAVariableType, variableType.getNodeClass());
    }

    @Test
    public void checkVariableTypeReferences() {
        var variableType = (UAVariableType) getNodeFromUA(2164);

        var forwardReferences = variableType.getForwardReferences();
        assertEquals(1, forwardReferences.size());

        assertTrue(forwardReferences.entries().stream()
                .filter(ref -> ref.getKey().getNodeId().equals(Nodes.ReferenceTypes.HAS_COMPONENT))
                .anyMatch(ref -> ref.getValue().getNodeId().equals(createUANodeId(12779))));

        var backwardReferences = variableType.getBackwardReferences();
        assertEquals(3, backwardReferences.size());

        assertTrue(backwardReferences.entries().stream()
                .filter(ref -> ref.getKey().getNodeId().equals(Nodes.ReferenceTypes.HAS_SUBTYPE))
                .anyMatch(ref -> ref.getValue().getNodeId().equals(createUANodeId(63))));
    }

    @Test
    public void checkDataTypeEnum() {
        var dataType = getNode(plasticsRubberNodeSet.getUADataTypes(), 3013);

        assertEquals("1:MaintenanceStatusEnumeration", dataType.getBrowseName());
        assertEquals("MaintenanceStatusEnumeration", dataType.getDisplayName());
        assertEquals("Maintenance status of a machine/device/component", dataType.getDescription());
        assertEquals(NodeClass.UADataType, dataType.getNodeClass());

        assertEquals(3, dataType.getDefinition().size());

        var definitionField = dataType.getDefinition().stream()
                .filter(field -> field.getValue() == 1)
                .findAny().orElse(null);

        assertNotNull(definitionField);

        assertEquals("WARNING", definitionField.getName());
        assertEquals("Maintenance of the device/component is due in the near future", definitionField.getDescription());
    }

    @Test
    public void checkDataTypeStruct() {
        var dataType = getNode(plasticsRubberNodeSet.getUADataTypes(), 3028);

        assertEquals("1:ActiveErrorDataType", dataType.getBrowseName());
        assertEquals("ActiveErrorDataType", dataType.getDisplayName());
        assertEquals("Iinformation about an active error in a device", dataType.getDescription());
        assertEquals(NodeClass.UADataType, dataType.getNodeClass());

        assertEquals(3, dataType.getDefinition().size());

        var definitionField = dataType.getDefinition().stream()
                .filter(field -> field.getName().equals("Id"))
                .findAny().orElse(null);

        assertNotNull(definitionField);

        assertEquals("String", definitionField.getDataType().getBrowseName());
        assertEquals("Unique identifier defined by manufacturer", definitionField.getDescription());
    }

    @Test
    public void checkDataTypeReferences() {
        var dataType = getNode(plasticsRubberNodeSet.getUADataTypes(), 3028);

        var forwardReferences = dataType.getForwardReferences();
        assertEquals(2, forwardReferences.size());

        assertTrue(forwardReferences.entries().stream()
                .filter(ref -> ref.getKey().getNodeId().equals(Nodes.ReferenceTypes.HAS_ENCODING))
                .anyMatch(ref -> ref.getValue().getNodeId().equals(createPlasticsRubberNodeId(5048))));

        var backwardReferences = dataType.getBackwardReferences();
        assertEquals(1, backwardReferences.size());

        assertTrue(backwardReferences.entries().stream()
                .filter(ref -> ref.getKey().getNodeId().equals(Nodes.ReferenceTypes.HAS_SUBTYPE))
                .anyMatch(ref -> ref.getValue().getNodeId().equals(createUANodeId(22))));
    }

    @Test
    public void checkReferenceType() {
        var referenceType = (UAReferenceType) getNodeFromUA(46);
        assertEquals("HasProperty", referenceType.getBrowseName());

        var inverseNames = referenceType.getInverseNames();
        assertEquals(1, inverseNames.size());
        assertEquals("PropertyOf", inverseNames.get(0));
    }

    @Test
    public void checkReferenceTypeReferences() {
        var referenceType = (UAReferenceType) getNodeFromUA(46);

        var forwardReferences = referenceType.getForwardReferences();
        assertEquals(0, forwardReferences.size());

        var backwardReferences = referenceType.getBackwardReferences();
        assertEquals(1, backwardReferences.size());
        assertTrue(backwardReferences.entries().stream()
                .filter(ref -> ref.getKey().getNodeId().equals(Nodes.ReferenceTypes.HAS_SUBTYPE))
                .anyMatch(ref -> ref.getValue().getNodeId().equals(createUANodeId(44))));
    }
}
