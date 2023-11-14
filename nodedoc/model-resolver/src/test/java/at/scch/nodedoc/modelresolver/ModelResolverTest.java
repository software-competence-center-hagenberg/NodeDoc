package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.modelresolver.ModelResolver;
import at.scch.nodedoc.modelresolver.NodeIdParser;
import at.scch.nodedoc.modelresolver.ReferenceResolver;
import at.scch.nodedoc.nodeset.*;
import at.scch.nodedoc.testkit.ClasspathModelRepository;
import at.scch.nodedoc.uaStandard.Nodes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ModelResolverTest {

    private static UANodeSet nodeSet;

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
        return nodeSet.getRequiredNodeSets().stream()
                .filter(model -> model.getModelUri().equals(ClasspathModelRepository.Models.OPC_UA_1_04_3.getModelUri()))
                .flatMap(model -> model.getAllNodes().stream())
                .filter(node -> node.getNodeId().equals(createUANodeId(nodeId)))
                .findAny().orElse(null);
    }

    @BeforeAll
    public static void setup() {
        var start = System.currentTimeMillis();
        var modelRepository = new ClasspathModelRepository();
        var nodeIdParser = new NodeIdParser();
        var referenceResolver = new ReferenceResolver(nodeIdParser);
        var modelResolver = new ModelResolver(modelRepository, nodeIdParser, referenceResolver);
        nodeSet = modelResolver.resolveModel(
                ClasspathModelRepository.Models.PLASTICS_RUBBER_1_02.getModelUri(),
                ClasspathModelRepository.Models.PLASTICS_RUBBER_1_02.getVersion(),
                ClasspathModelRepository.Models.PLASTICS_RUBBER_1_02.getPublicationDate()
        );
        var end = System.currentTimeMillis();
        System.out.println((end - start) + "ms");
    }

    @Test
    public void checkNodeCount() {
        assertEquals(64, nodeSet.getUAObjects().size());
        assertEquals(713, nodeSet.getUAVariables().size());
        assertEquals(76, nodeSet.getUAMethods().size());
        assertEquals(0, nodeSet.getUAViews().size());
        assertEquals(68, nodeSet.getUAObjectTypes().size());
        assertEquals(0, nodeSet.getUAVariableTypes().size());
        assertEquals(27, nodeSet.getUADataTypes().size());
        assertEquals(0, nodeSet.getUAReferenceTypes().size());
        assertEquals(948, nodeSet.getAllNodes().size());
    }

    @Test
    public void checkRequiredNodeSets() {
        assertEquals(2, nodeSet.getRequiredNodeSets().size());

        assertTrue(nodeSet.getRequiredNodeSets().stream()
                .anyMatch(nodeSet -> nodeSet.getModelUri().equals(ClasspathModelRepository.Models.OPC_UA_1_04_3.getModelUri())));

        assertTrue(nodeSet.getRequiredNodeSets().stream()
                .anyMatch(nodeSet -> nodeSet.getModelUri().equals(ClasspathModelRepository.Models.OPC_UA_DI_1_02.getModelUri())));
    }

    @Test
    public void checkNodeSetsByNamespaceIndex() {
        assertEquals(3, nodeSet.getNodeSetsByNamespaceIndex().size());

        assertEquals(
                ClasspathModelRepository.Models.OPC_UA_1_04_3.getModelUri(),
                nodeSet.getNodeSetsByNamespaceIndex().get(0).getModelUri()
        );
        assertEquals(
                ClasspathModelRepository.Models.PLASTICS_RUBBER_1_02.getModelUri(),
                nodeSet.getNodeSetsByNamespaceIndex().get(1).getModelUri()
        );
        assertEquals(
                ClasspathModelRepository.Models.OPC_UA_DI_1_02.getModelUri(),
                nodeSet.getNodeSetsByNamespaceIndex().get(2).getModelUri()
        );
    }

    @Test
    public void checkObject() {
        var object = getNode(nodeSet.getUAObjects(), 5007);

        assertNotNull(object);
        assertEquals("1:ProductionDatasetLists", object.getBrowseName());
        assertEquals("ProductionDatasetLists", object.getDisplayName());
        assertEquals("Functions for exchanging information on the available production datasets on client and server", object.getDescription());
        assertEquals(NodeClass.UAObject, object.getNodeClass());
        assertEquals(1, object.getEventNotifier());
    }

    @Test
    public void checkObjectReferences() {
        var object = getNode(nodeSet.getUAObjects(), 5033);
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
        var variable = getNode(nodeSet.getUAVariables(), 6519);

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
        var variable = getNode(nodeSet.getUAVariables(), 6268);

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
        var method = getNode(nodeSet.getUAMethods(), 7038);

        assertNotNull(method);
        assertEquals("1:SetWatchDogTime", method.getBrowseName());
        assertEquals("SetWatchDogTime", method.getDisplayName());
        assertEquals("Release of production for a given time", method.getDescription());
        assertEquals(NodeClass.UAMethod, method.getNodeClass());

        assertTrue(method.isExecutable());
        assertTrue(method.isUserExecutable());
        assertEquals(getNode(nodeSet.getUAMethods(), 7029), method.getMethodDeclaration());
    }

    @Test
    public void checkMethodReferences() {
        var method = getNode(nodeSet.getUAMethods(), 7038);

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
        var objectType = getNode(nodeSet.getUAObjectTypes(), 1004);

        assertNotNull(objectType);
        assertEquals("1:MessageConditionType", objectType.getBrowseName());
        assertEquals("MessageConditionType", objectType.getDisplayName());
        assertEquals("Text messages (incl. error messages) of the control system currently shown on the screen of the machine", objectType.getDescription());
        assertEquals(NodeClass.UAObjectType, objectType.getNodeClass());
    }

    @Test
    public void checkObjectTypeReferences() {
        var objectType = getNode(nodeSet.getUAObjectTypes(), 1004);

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
        var dataType = getNode(nodeSet.getUADataTypes(), 3013);

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
        var dataType = getNode(nodeSet.getUADataTypes(), 3028);

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
        var dataType = getNode(nodeSet.getUADataTypes(), 3028);

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
