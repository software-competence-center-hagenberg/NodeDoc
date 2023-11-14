package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.db.documents.NodeSetText;
import at.scch.nodedoc.db.repository.NodeDescriptionRepository;
import at.scch.nodedoc.documentation.DocumentationGenerator;
import at.scch.nodedoc.documentation.single.graph.SingleGraphDisplayDataBuilder;
import at.scch.nodedoc.modelresolver.DependencyResolver;
import at.scch.nodedoc.modelresolver.ModelResolver;
import at.scch.nodedoc.modelresolver.NodeIdParser;
import at.scch.nodedoc.modelresolver.ReferenceResolver;
import at.scch.nodedoc.testkit.ClasspathModelRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FullGenerationTest {

    ModelResolver modelResolver;
    DependencyResolver dependencyResolver;

    SingleDisplayNodeSetGenerator singleDisplayNodeSetGenerator;

    @Mock
    NodeDescriptionRepository nodeDescriptionRepository;
    DocumentationGenerator documentationGenerator;

    @BeforeEach
    void setup() {
        var modelRepository = new ClasspathModelRepository();
        var nodeIdParser = new NodeIdParser();
        var referenceResolver = new ReferenceResolver(nodeIdParser);
        modelResolver = new ModelResolver(modelRepository, nodeIdParser, referenceResolver);
        dependencyResolver = new DependencyResolver(modelRepository);

        when(nodeDescriptionRepository.getAllNodeSetTextsForNodeSet(any(), any(), any()))
                .thenReturn(List.of());

        var docEntryGenerator = new DocEntryEditorGenerator();
        var singleDisplayReferencesTableSectionGenerator = new SingleDisplayReferencesTableSectionGenerator();
        var singleDisplayDataTypeTableSectionGenerator = new SingleDisplayDataTypeTableSectionGenerator(docEntryGenerator);
        var singleDisplayMethodGenerator = new SingleDisplayMethodGenerator(docEntryGenerator);
        var singleDisplayInstanceGenerator = new SingleDisplayInstanceGenerator(singleDisplayMethodGenerator, docEntryGenerator);
        var singleDisplayTypeGenerator = new SingleDisplayTypeGenerator(singleDisplayReferencesTableSectionGenerator, singleDisplayDataTypeTableSectionGenerator, singleDisplayInstanceGenerator, docEntryGenerator);
        var singleGraphDisplayDataBuilder = new SingleGraphDisplayDataBuilder();
        singleDisplayNodeSetGenerator = new SingleDisplayNodeSetGenerator(singleDisplayTypeGenerator, singleGraphDisplayDataBuilder, docEntryGenerator, nodeDescriptionRepository);

        documentationGenerator = new DocumentationGenerator();
    }

    @Captor
    ArgumentCaptor<List<NodeSetText>> nodeSetTextsCaptor;

    @Test
    void testFullGeneration() throws IOException {
        var nodeSetMetaData = ClasspathModelRepository.Models.EUROMAP83_1_01;
        var dependencies = dependencyResolver.collectDependencies(nodeSetMetaData);
        var universe = modelResolver.loadNodeSetUniverse(
                Stream.concat(Stream.of(nodeSetMetaData), dependencies.stream())
                        .collect(Collectors.toSet()));
        var singleDisplayNodeSet = singleDisplayNodeSetGenerator.generateSingleDocumentation(universe, nodeSetMetaData.getModelUri());

        var expectedHtml = readFileFromResources("/euromap-1_01-expected.html");

        var outputStream = new ByteArrayOutputStream();
        documentationGenerator.generateDocumentation(singleDisplayNodeSet, new DocumentationGenerator.Config("http://ignored-couchdb-uri"), outputStream, FullGenerationTest.class.getResourceAsStream("/emptyTemplate.html"));
        var resultHtml = outputStream.toString();

        var expectedHtmlDoc = Jsoup.parse(expectedHtml);
        var actualHtmlDoc = Jsoup.parse(resultHtml);

        assertThat(actualHtmlDoc.select("h1, h2, h3"))
                .usingElementComparator(Comparator.comparing(Element::outerHtml))
                .containsExactlyElementsOf(expectedHtmlDoc.select("h1, h2, h3"));

        assertThat(actualHtmlDoc.select("table"))
                .usingElementComparator(Comparator.comparing(Element::outerHtml))
                .containsExactlyElementsOf(expectedHtmlDoc.select("table"));

        assertThat(actualHtmlDoc.select(".doc-entry-editor"))
                .usingElementComparator(Comparator.comparing(Element::outerHtml))
                .containsExactlyElementsOf(expectedHtmlDoc.select(".doc-entry-editor"));

        verify(nodeDescriptionRepository).save(nodeSetTextsCaptor.capture());
        assertThat(nodeSetTextsCaptor.getValue()).hasSize(880);
    }

    private static String readFileFromResources(String path) {
        return new BufferedReader(
                new InputStreamReader(FullGenerationTest.class.getResourceAsStream(path)))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
