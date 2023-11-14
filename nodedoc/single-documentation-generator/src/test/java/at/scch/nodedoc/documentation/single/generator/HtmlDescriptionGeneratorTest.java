package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.db.documents.TextId;
import at.scch.nodedoc.nodeset.DefinitionField;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UANode;
import at.scch.nodedoc.nodeset.UAVariable;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HtmlDescriptionGeneratorTest {
    private DocEntryEditorGenerator generator;

    private static final String NAMESPACE_URI = "http://testuri.com";

    private static final NodeId<?> NODE_ID = new NodeId.IntNodeId(NAMESPACE_URI, 2);

    private static final String DESCRIPTION_IDENTIFIER_BASE64 = "eyJ0eXBlIjoiZGVzY3JpcHRpb24iLCJub2RlSWQiOiJuc3U9aHR0cDovL3Rlc3R1cmkuY29tO2k9MiJ9";
    private static final String DOCUMENTATION_IDENTIFIER_BASE64 = "eyJ0eXBlIjoiZG9jdW1lbnRhdGlvbiIsIm5vZGVJZCI6Im5zdT1odHRwOi8vdGVzdHVyaS5jb207aT0yIn0=";

    @BeforeEach
    void setUp() {
        generator = new DocEntryEditorGenerator();
    }

    @Test
    void testDescriptionDiv() {
        var node = mock(UANode.class);
        when(node.getNodeId()).thenAnswer((invocation) -> NODE_ID);
        Element divElement = generator.getDivForNodeDescription(node, 1);
        String expectedHtml = "<div class=\"doc-entry-editor\" data-headinglevel=\"2\" data-textid=\"" + DESCRIPTION_IDENTIFIER_BASE64 + "\" data-placeholder=\"No description specified...\" data-title=\"Description:\"></div>";
        assertEquals(expectedHtml, divElement.toString());
    }

    @Test
    void testDocumentationDiv() {
        var node = mock(UANode.class);
        when(node.getNodeId()).thenAnswer((invocation) -> NODE_ID);
        Element divElement = generator.getDivForNodeDocumentation(node, 1);
        String expectedHtml = "<div class=\"doc-entry-editor\" data-headinglevel=\"2\" data-textid=\"" + DOCUMENTATION_IDENTIFIER_BASE64 + "\" data-placeholder=\"No documentation specified...\" data-title=\"Documentation:\"></div>";
        assertEquals(expectedHtml, divElement.toString());
    }

    @ParameterizedTest
    @MethodSource
    void getDivForIdentifierAsString(TextId identifier, String identifierBase64) {
        Element divElement = generator.getDivForIdentifier(identifier, 1);
        String expectedHtml = "<div class=\"doc-entry-editor\" data-headinglevel=\"2\" data-textid=\"" + identifierBase64 + "\" data-placeholder=\"No description specified...\" data-title></div>";
        assertEquals(expectedHtml, divElement.toString());
    }

    static Stream<Arguments> getDivForIdentifierAsString() {
        var argument = mock(UAVariable.Argument.class);
        when(argument.getName()).thenReturn("TestArgument");

        var field = mock(DefinitionField.class);
        when(argument.getName()).thenReturn("TestField");

        return Stream.of(
                arguments(TextId.forNodeDescription(NODE_ID), "eyJ0eXBlIjoiZGVzY3JpcHRpb24iLCJub2RlSWQiOiJuc3U9aHR0cDovL3Rlc3R1cmkuY29tO2k9MiJ9"),
                arguments(TextId.forNodeDocumentation(NODE_ID), "eyJ0eXBlIjoiZG9jdW1lbnRhdGlvbiIsIm5vZGVJZCI6Im5zdT1odHRwOi8vdGVzdHVyaS5jb207aT0yIn0="),
                arguments(TextId.forField(NODE_ID, field), "eyJ0eXBlIjoiZmllbGQiLCJub2RlSWQiOiJuc3U9aHR0cDovL3Rlc3R1cmkuY29tO2k9MiJ9"),
                arguments(TextId.forArgument(NODE_ID, argument), "eyJ0eXBlIjoiYXJndW1lbnQiLCJub2RlSWQiOiJuc3U9aHR0cDovL3Rlc3R1cmkuY29tO2k9MiIsImFyZ3VtZW50TmFtZSI6IlRlc3RGaWVsZCJ9"),
                arguments(TextId.forFreetext("Test_Heading"), "eyJ0eXBlIjoiZnJlZXRleHQiLCJoZWFkZXJJZCI6IlRlc3RfSGVhZGluZyJ9")
        );
    }

    @Test
    void getDivForFreetext() {
        Element divElement = generator.getDivForFreetext("Test_Heading", 1);
        String identifier = "eyJ0eXBlIjoiZnJlZXRleHQiLCJoZWFkZXJJZCI6IlRlc3RfSGVhZGluZyJ9";
        String expectedHtml = "<div class=\"doc-entry-editor\" data-headinglevel=\"2\" data-textid=\"" + identifier + "\" data-placeholder=\"Type in your freetext...\" data-title></div>";
        assertEquals(expectedHtml, divElement.toString());
    }
}