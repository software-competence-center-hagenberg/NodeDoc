package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UANodeSet;
import at.scch.nodedoc.nodeset.UAObjectType;
import at.scch.nodedoc.testkit.ClasspathModelRepository;
import org.apache.maven.surefire.shared.lang3.NotImplementedException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class NodeSetUniverseImplTest {

    private static NodeSetUniverseImpl universe;
    private static String plasticsRubberNamespaceUri;

    private static UANodeSet plasticsRubberNodeSet;

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
        universe = (NodeSetUniverseImpl) modelResolver.loadNodeSetUniverse(
                pinnedVersions
        );
    }


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

    @Test
    void getNodeById() {
        var activeJobValuesType = universe.getNodeById(createPlasticsRubberNodeId(1021));
        assertEquals("1:ActiveJobValuesType", activeJobValuesType.getBrowseName());
        assertThat(activeJobValuesType).isInstanceOf(UAObjectType.class);

        var baseObjectType = universe.getNodeById(createUANodeId(58));
        assertEquals("BaseObjectType", baseObjectType.getBrowseName());
        assertThat(baseObjectType).isInstanceOf(UAObjectType.class);
    }

    @Test
    void getUATypes() {
        var uaTypes = universe.getUATypes();
        assertEquals(651, uaTypes.size());
    }

    @Test
    void getUAObjectTypes() {
        var uaObjectTypes = universe.getUAObjectTypes();
        assertEquals(294, uaObjectTypes.size());
    }

    @Test
    void getUADataTypes() {
        var uaDataTypes = universe.getUADataTypes();
        assertEquals(251, uaDataTypes.size());
    }

    @Test
    void getUAVariableTypes() {
        var uaVariableTypes = universe.getUAVariableTypes();
        assertEquals(58, uaVariableTypes.size());
    }

    @Test
    void getUAReferenceTypes() {
        var uaReferenceTypes = universe.getUAReferenceTypes();
        assertEquals(48, uaReferenceTypes.size());
    }

    @Test
    void getUAObjects() {
        var uaObjects = universe.getUAObjects();
        assertEquals(745, uaObjects.size());
    }

    @Test
    void getUAVariables() {
        var uaVariables = universe.getUAVariables();
        assertEquals(3216, uaVariables.size());
    }

    @Test
    void getUAMethods() {
        var uaMethods = universe.getUAMethods();
        assertEquals(373, uaMethods.size());
    }

    @Test
    @Disabled
    void getUAViews() {
        throw new NotImplementedException("");
    }
}