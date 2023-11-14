package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.db.documents.TextId;
import at.scch.nodedoc.nodeset.UANode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Element;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class DocEntryEditorGenerator {

    private static String encodeTextIdBase64(TextId textId) {
        var objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String textIdAsJson;
        try {
            textIdAsJson = objectMapper.writeValueAsString(textId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return Base64.getEncoder().encodeToString(textIdAsJson.getBytes(StandardCharsets.UTF_8));
    }

    private Element getRewritableTextDiv(TextId textId, String placeholderText, String title, int headerLevel) {
        Element e = new Element("div");
        e.attr("class", "doc-entry-editor");
        e.attr("data-headinglevel", Integer.toString(headerLevel + 1));
        e.attr("data-textid", encodeTextIdBase64(textId));
        e.attr("data-placeholder", placeholderText);
        e.attr("data-title", title);
        return e;
    }

    /**
     * Adds a description div to the html-document
     *
     * @param node - node for which the div should be created
     * @return - div element
     */
    public Element getDivForNodeDescription(UANode node, int headerLevel) {
        return getRewritableTextDiv(
                TextId.forNodeDescription(node.getNodeId()),
                "No description specified...",
                "Description:",
                headerLevel);
    }

    public Element getDivForNodeDocumentation(UANode node, int headerLevel) {
        return getRewritableTextDiv(
                TextId.forNodeDocumentation(node.getNodeId()),
                "No documentation specified...",
                "Documentation:",
                headerLevel);
    }

    public Element getDivForIdentifier(TextId elementId, int headerLevel) {
        return getRewritableTextDiv(
                elementId,
                "No description specified...",
                null,
                headerLevel);
    }

    public Element getDivForFreetext(String headerId, int headerLevel) {
        return getRewritableTextDiv(
                TextId.forFreetext(headerId),
                "Type in your freetext...",
                null,
                headerLevel);
    }
}
