package at.scch.nodedoc.parser;

import at.scch.nodedoc.parser.rawModel.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NodeSetXMLParserTest {

    private static RawNodeSet nodeSet;

    private static <T extends RawNode> T getNodeById(String id) {
        return (T) nodeSet.getNodes().stream()
                .filter(node -> node.getNodeId().equals(id))
                .findAny().orElse(null);
    }

    @BeforeAll
    public static void parseXML() throws IOException, SAXException {
        var nodeSetXMLValidator = new SimpleNodeIdValidator();
        var modelValidator = new ModelValidator();
        var parser = new NodeSetXMLParser(nodeSetXMLValidator, modelValidator);
        nodeSet = parser.parseAndValidateXML(NodeSetXMLParserTest.class.getResourceAsStream("/nodesets/Euromap_test_1_00.xml"));
    }

    @Test
    public void checkNumberOfNodes() {
        assertEquals(1, nodeSet.getNodes().stream().filter(node -> node instanceof RawObject).count());
        assertEquals(9, nodeSet.getNodes().stream().filter(node -> node instanceof RawVariable).count());
        assertEquals(3, nodeSet.getNodes().stream().filter(node -> node instanceof RawMethod).count());
        assertEquals(0, nodeSet.getNodes().stream().filter(node -> node instanceof RawView).count());
        assertEquals(4, nodeSet.getNodes().stream().filter(node -> node instanceof RawObjectType).count());
        assertEquals(1, nodeSet.getNodes().stream().filter(node -> node instanceof RawVariableType).count());
        assertEquals(1, nodeSet.getNodes().stream().filter(node -> node instanceof RawDataType).count());
        assertEquals(1, nodeSet.getNodes().stream().filter(node -> node instanceof RawReferenceType).count());
        assertEquals(20, nodeSet.getNodes().size());
    }

    @Test
    public void checkHeader() {
        assertEquals(OffsetDateTime.parse("2019-01-28T08:00:00Z"), nodeSet.getLastModified());
    }

    @Test
    public void checkNamespaceUris() {
        assertEquals(2, nodeSet.getNamespaceUris().size());
        assertEquals("http://www.difftest.org/euromap83/", nodeSet.getNamespaceUris().get(0));
        assertEquals("http://opcfoundation.org/UA/DI/", nodeSet.getNamespaceUris().get(1));
    }

    @Test
    public void checkModels() {
        assertEquals(1, nodeSet.getModels().size());

        var model = nodeSet.getModels().get(0);
        assertEquals("http://www.difftest.org/euromap83/", model.getModelUri());
        assertEquals(OffsetDateTime.parse("2019-01-28T08:00:00Z"), model.getPublicationDate());
        assertEquals("1.00", model.getVersion());

        assertEquals(2, model.getRequiredModels().size());

        var requiredModel1 = model.getRequiredModels().get(0);
        assertEquals("http://opcfoundation.org/UA/", requiredModel1.getModelUri());
        assertEquals(OffsetDateTime.parse("2018-05-15T00:00:00Z"), requiredModel1.getPublicationDate());
        assertEquals("1.04", requiredModel1.getVersion());

        var requiredModel2 = model.getRequiredModels().get(1);
        assertEquals("http://opcfoundation.org/UA/DI/", requiredModel2.getModelUri());
        assertEquals(OffsetDateTime.parse("2013-12-02T00:00:00Z"), requiredModel2.getPublicationDate());
        assertEquals("1.01", requiredModel2.getVersion());
    }

    @Test
    public void checkAliases() {
        assertEquals(46, nodeSet.getAliases().size());
        assertEquals("i=40", nodeSet.getAliases().stream()
                .filter(alias -> alias.getAlias().equals("HasTypeDefinition"))
                .findAny().get().getNodeId());
        assertEquals("ns=1;i=3012", nodeSet.getAliases().stream()
                .filter(alias -> alias.getAlias().equals("SequenceChangeEnumeration"))
                .findAny().get().getNodeId());
    }

    @Test
    public void checkObject() {
        var object = NodeSetXMLParserTest.<RawObject>getNodeById("ns=1;i=5016");
        Assertions.assertEquals("User__Nr_", object.getSymbolicName());
        Assertions.assertEquals("ns=1;i=1048", object.getParentNodeId());
        Assertions.assertEquals("1:User_<Nr>", object.getBrowseName());
        Assertions.assertEquals("User_<Nr>", object.getDisplayName());

        Assertions.assertEquals(2, object.getReferences().size());

        var reference1 = object.getReferences().get(0);
        Assertions.assertEquals("HasTypeDefinition", reference1.getReferenceType());
        Assertions.assertTrue(reference1.isForward());
        Assertions.assertEquals("ns=1;i=1045", reference1.getReferencedId());

        var reference2 = object.getReferences().get(1);
        Assertions.assertEquals("HasModellingRule", reference2.getReferenceType());
        Assertions.assertTrue(reference2.isForward());
        Assertions.assertEquals("i=11508", reference2.getReferencedId());
    }

    @Test
    public void checkVariable() {
        var variable = NodeSetXMLParserTest.<RawVariable>getNodeById("ns=1;i=6264");
        assertEquals("EnumValueType", variable.getDataType());
        Assertions.assertEquals("ns=1;i=3017", variable.getParentNodeId());
        assertEquals(1, variable.getValueRank());
        assertEquals("12", variable.getArrayDimensions());
        Assertions.assertEquals("EnumValues", variable.getBrowseName());
        Assertions.assertEquals("EnumValues", variable.getDisplayName());

        Assertions.assertEquals(3, variable.getReferences().size());

        var reference1 = variable.getReferences().get(0);
        Assertions.assertEquals("HasProperty", reference1.getReferenceType());
        Assertions.assertFalse(reference1.isForward());
        Assertions.assertEquals("ns=1;i=3017", reference1.getReferencedId());

        var reference2 = variable.getReferences().get(1);
        Assertions.assertEquals("HasModellingRule", reference2.getReferenceType());
        Assertions.assertTrue(reference2.isForward());
        Assertions.assertEquals("i=78", reference2.getReferencedId());
    }

    @Test
    public void checkVariableArgument() {
        var variable = NodeSetXMLParserTest.<RawVariable>getNodeById("ns=1;i=6268");

        assertEquals(14, variable.getArguments().size());
        var jobNameArg = variable.getArguments().get(0);
        assertEquals("JobName", jobNameArg.getName());
        assertEquals("i=12", jobNameArg.getDataType());
        assertEquals(-1, jobNameArg.getValueRank());
        assertEquals("", jobNameArg.getArrayDimension());
        assertEquals("", jobNameArg.getDescription());

    }

    @Test
    public void checkMethod() {
        var method = NodeSetXMLParserTest.<RawMethod>getNodeById("ns=1;i=7003");
        assertEquals("ns=1;i=1021", method.getParentNodeId());
        assertEquals("1:InterruptJob", method.getBrowseName());
        assertEquals("InterruptJob", method.getDisplayName());
        assertEquals("With this Method the client (e.g. MES) requests the machine to change the JobStatus to JOB_INTERRUPTED_7", method.getDescription());

        assertEquals(2, method.getReferences().size());

        var reference1 = method.getReferences().get(0);
        assertEquals("HasComponent", reference1.getReferenceType());
        assertFalse(reference1.isForward());
        assertEquals("ns=1;i=1021", reference1.getReferencedId());

        var reference2 = method.getReferences().get(1);
        assertEquals("HasModellingRule", reference2.getReferenceType());
        assertTrue(reference2.isForward());
        assertEquals("i=78", reference2.getReferencedId());
    }

    @Test
    @Disabled
    public void checkView() {
        throw new RuntimeException("not implemented");
    }

    @Test
    public void checkObjectType() {
        var objectType = NodeSetXMLParserTest.<RawObjectType>getNodeById("ns=1;i=1052");
        assertEquals("1:HelpOffNormalAlarmType", objectType.getBrowseName());
        assertEquals("HelpOffNormalAlarmType", objectType.getDisplayName());
        assertEquals("OffNormalAlarmType with additional help text", objectType.getDescription());

        assertEquals(2, objectType.getReferences().size());

        var reference1 = objectType.getReferences().get(0);
        assertEquals("HasSubtype", reference1.getReferenceType());
        assertFalse(reference1.isForward());
        assertEquals("i=10637", reference1.getReferencedId());

        var reference2 = objectType.getReferences().get(1);
        assertEquals("HasProperty", reference2.getReferenceType());
        assertTrue(reference2.isForward());
        assertEquals("ns=1;i=6309", reference2.getReferencedId());
    }

    @Test
    public void checkVariableType() {
        var variableType = NodeSetXMLParserTest.<RawVariableType>getNodeById("ns=1;i=2365");
        assertEquals("DataItemType", variableType.getBrowseName());
        assertEquals("DataItemType", variableType.getDisplayName());
        assertEquals(-2, variableType.getValueRank());
        assertEquals("A variable that contains live automation data.", variableType.getDescription());

        assertEquals(2, variableType.getReferences().size());

        var reference1 = variableType.getReferences().get(0);
        assertEquals("HasProperty", reference1.getReferenceType());
        assertTrue(reference1.isForward());
        assertEquals("ns=1;i=2366", reference1.getReferencedId());

        var reference2 = variableType.getReferences().get(1);
        assertEquals("HasProperty", reference2.getReferenceType());
        assertTrue(reference2.isForward());
        assertEquals("ns=1;i=2367", reference2.getReferencedId());
    }

    @Test
    public void checkDataType() {
        var dataType = NodeSetXMLParserTest.<RawDataType>getNodeById("ns=1;i=3017");
        assertEquals("1:JobStatusEnumeration", dataType.getBrowseName());
        assertEquals("JobStatusEnumeration", dataType.getDisplayName());
        assertEquals("Current status of the job", dataType.getDescription());

        assertEquals(2, dataType.getReferences().size());

        var reference1 = dataType.getReferences().get(0);
        assertEquals("HasProperty", reference1.getReferenceType());
        assertTrue(reference1.isForward());
        assertEquals("ns=1;i=6264", reference1.getReferencedId());

        var reference2 = dataType.getReferences().get(1);
        assertEquals("HasSubtype", reference2.getReferenceType());
        assertFalse(reference2.isForward());
        assertEquals("i=29", reference2.getReferencedId());

        assertEquals(12, dataType.getDefinition().size());

        var definition = dataType.getDefinition().get(5);
        assertEquals("START_UP_ACTIVE", definition.getName());
        assertEquals(5, definition.getValue());
        assertEquals("The operator is setting the machine in the start-up phase", definition.getDescription());
    }

    @Test
    public void checkReferenceType() {
        var referenceType = NodeSetXMLParserTest.<RawReferenceType>getNodeById("ns=1;i=6030");
        assertEquals("1:ConnectsTo", referenceType.getBrowseName());
        assertTrue(referenceType.isSymmetric());
        assertEquals("ConnectsTo", referenceType.getDisplayName());
        assertEquals("Used to indicate that source and target Node have a topological connection.", referenceType.getDescription());
        assertEquals("https://reference.opcfoundation.org/v104/DI/docs/6.5", referenceType.getDocumentation());

        assertEquals(1, referenceType.getReferences().size());

        var reference = referenceType.getReferences().get(0);
        assertEquals("HasSubtype", reference.getReferenceType());
        assertFalse(reference.isForward());
        assertEquals("i=33", reference.getReferencedId());
    }

}