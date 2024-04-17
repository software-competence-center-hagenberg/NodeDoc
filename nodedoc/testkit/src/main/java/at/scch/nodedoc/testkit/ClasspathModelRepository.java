package at.scch.nodedoc.testkit;

import at.scch.nodedoc.ModelMetaData;
import at.scch.nodedoc.ModelRepository;
import at.scch.nodedoc.parser.NodeSetXMLParser;
import at.scch.nodedoc.parser.rawModel.RawNodeSet;
import at.scch.nodedoc.uaStandard.Namespaces;
import org.javatuples.Pair;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Map;

public class ClasspathModelRepository implements ModelRepository {

    public static class Models {
        public static ModelMetaData DATA_TYPE_TEST = new ModelMetaData("http://test.com/datatype/", "1.0.0", OffsetDateTime.parse("2020-02-05T14:59:58Z"));
        public static ModelMetaData METHOD_TEST = new ModelMetaData("http://test.com/method/", "1.0.0", OffsetDateTime.parse("2020-02-05T14:59:58Z"));
        public static ModelMetaData VARIABLE_TYPE_TEST = new ModelMetaData("http://test.com/VariableType/", "1.0.0", OffsetDateTime.parse("2020-02-05T14:59:58Z"));

        public static ModelMetaData OPC_UA_1_03 = new ModelMetaData(Namespaces.UA, "1.03", OffsetDateTime.parse("2013-12-02T00:00:00Z"));
        public static ModelMetaData OPC_UA_1_04_3 = new ModelMetaData(Namespaces.UA, "1.04.3", OffsetDateTime.parse("2019-09-09T00:00:00Z"));
        public static ModelMetaData OPC_UA_1_04 = new ModelMetaData(Namespaces.UA, "1.04", OffsetDateTime.parse("2019-05-01T00:00:00Z"));
        public static ModelMetaData OPC_UA_DI_1_01 = new ModelMetaData(Namespaces.UA_DI, "1.01", OffsetDateTime.parse("2018-06-07T00:00:00Z"));
        public static ModelMetaData OPC_UA_DI_1_02 = new ModelMetaData(Namespaces.UA_DI, "1.02", OffsetDateTime.parse("2019-05-01T00:00:00Z"));
        public static ModelMetaData PLASTICS_RUBBER_1_02 = new ModelMetaData("http://opcfoundation.org/UA/PlasticsRubber/GeneralTypes/", "1.02", OffsetDateTime.parse("2020-06-01T00:00:00Z"));
        public static ModelMetaData EUROMAP83_1_01 = new ModelMetaData("http://www.euromap.org/euromap83/", "1.01", OffsetDateTime.parse("2019-01-28T08:00:00Z"));

        public static ModelMetaData A = new ModelMetaData("http://www.dependencies.org/A/", "1.00", OffsetDateTime.parse("2018-10-28T08:00:00Z"));
        public static ModelMetaData B = new ModelMetaData("http://www.dependencies.org/B/", "1.00", OffsetDateTime.parse("2018-10-28T08:00:00Z"));
        public static ModelMetaData B2 = new ModelMetaData("http://www.dependencies.org/B/", "1.01", OffsetDateTime.parse("2019-10-28T08:00:00Z"));
        public static ModelMetaData C = new ModelMetaData("http://www.dependencies.org/C/", "1.00", OffsetDateTime.parse("2018-10-28T08:00:00Z"));
        public static ModelMetaData C2 = new ModelMetaData("http://www.dependencies.org/C/", "1.01", OffsetDateTime.parse("2019-10-28T08:00:00Z"));
        public static ModelMetaData D = new ModelMetaData("http://www.dependencies.org/D/", "1.00", OffsetDateTime.parse("2018-10-28T08:00:00Z"));
        public static ModelMetaData E = new ModelMetaData("http://www.dependencies.org/E/", "1.00", OffsetDateTime.parse("2018-10-28T08:00:00Z"));

    }

    @Override
    public RawNodeSet loadNodeSet(ModelMetaData metaData) {
        var models = Map.ofEntries(
                Map.entry(Models.DATA_TYPE_TEST, "/at/scch/nodedoc/testkit/nodesets/DataTypeTestModel_1.0.0.xml"),
                Map.entry(Models.METHOD_TEST, "/at/scch/nodedoc/testkit/nodesets/MethodTestModel_1.0.0.xml"),
                Map.entry(Models.VARIABLE_TYPE_TEST, "/at/scch/nodedoc/testkit/nodesets/UAVariableTypeTestModel_1.0.0.xml"),
                Map.entry(Models.PLASTICS_RUBBER_1_02, "/at/scch/nodedoc/testkit/nodesets/Opc.Ua.PlasticsRubber.GeneralTypes.NodeSet2_1.02.xml"),
                Map.entry(Models.OPC_UA_1_04_3, "/at/scch/nodedoc/testkit/nodesets/Opc.Ua.NodeSet2_1.04.3.xml"),
                Map.entry(Models.OPC_UA_DI_1_02, "/at/scch/nodedoc/testkit/nodesets/Opc.Ua.Di.NodeSet2_1.02.xml"),
                Map.entry(Models.EUROMAP83_1_01, "/at/scch/nodedoc/testkit/nodesets/Opc_Ua.EUROMAP83.1_01.NodeSet2_1.01.xml"),
                Map.entry(Models.OPC_UA_1_04, "/at/scch/nodedoc/testkit/nodesets/Opc.Ua.NodeSet2_1.04.xml"),
                Map.entry(Models.OPC_UA_DI_1_01, "/at/scch/nodedoc/testkit/nodesets/Opc.Ua.Di.NodeSet2_1.01.xml"),
                Map.entry(Models.OPC_UA_1_03, "/at/scch/nodedoc/testkit/nodesets/Opc.Ua.NodeSet2_1.03.xml"),
                Map.entry(Models.A, "/at/scch/nodedoc/testkit/dependencies/A.xml"),
                Map.entry(Models.B, "/at/scch/nodedoc/testkit/dependencies/B.xml"),
                Map.entry(Models.B2, "/at/scch/nodedoc/testkit/dependencies/B2.xml"),
                Map.entry(Models.C, "/at/scch/nodedoc/testkit/dependencies/C.xml"),
                Map.entry(Models.C2, "/at/scch/nodedoc/testkit/dependencies/C2.xml"),
                Map.entry(Models.D, "/at/scch/nodedoc/testkit/dependencies/D.xml"),
                Map.entry(Models.E, "/at/scch/nodedoc/testkit/dependencies/E.xml")
        );

        try {
            var pathToModel = models.get(metaData);
            if (pathToModel == null) {
                pathToModel = models.entrySet().stream()
                        .filter(entry -> entry.getKey().getModelUri().equals(metaData.getModelUri()) && entry.getKey().getVersion().equals(metaData.getVersion()))
                        .map(Map.Entry::getValue)
                        .findAny().orElse(null);
            }
            if (pathToModel == null) {
                throw new RuntimeException("Model not found in classpath: " + metaData.getModelUri() + " (" + metaData.getVersion() + " / " + metaData.getPublicationDate() + ")");
            }
            return new NodeSetXMLParser().parseXML(ClasspathModelRepository.class.getResourceAsStream(pathToModel));
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ModelMetaData saveNodeSet(InputStream contents) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getFileForNodeSet(ModelMetaData metaData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean nodeSetWithModelUriExists(ModelMetaData modelMetaData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<Boolean, Collection<ModelMetaData>> deleteAllNodeSetsStartingAt(String relativePath) {
        throw new UnsupportedOperationException();
    }
}
