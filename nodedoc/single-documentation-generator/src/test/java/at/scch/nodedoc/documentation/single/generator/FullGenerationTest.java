package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.db.documents.NodeSetText;
import at.scch.nodedoc.documentation.DocumentationGenerator;
import at.scch.nodedoc.testkit.ClasspathModelRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FullGenerationTest extends GenerationTestBase {

    @Captor
    ArgumentCaptor<List<NodeSetText>> nodeSetTextsCaptor;

    @Test
    void testFullGeneration() throws IOException {
        var singleDisplayNodeSet = generateSingleDisplayNodeSet(ClasspathModelRepository.Models.EUROMAP83_1_01);

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
