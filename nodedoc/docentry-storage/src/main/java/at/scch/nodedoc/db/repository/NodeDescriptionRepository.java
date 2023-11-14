package at.scch.nodedoc.db.repository;

import at.scch.nodedoc.db.documents.NodeSetText;
import at.scch.nodedoc.db.documents.TextId;
import at.scch.nodedoc.nodeset.*;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.query.QueryBuilder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.cloudant.client.api.query.Expression.eq;
import static com.cloudant.client.api.query.Expression.exists;
import static com.cloudant.client.api.query.Operation.and;

public class NodeDescriptionRepository {

    private final Database db;

    public NodeDescriptionRepository(Database db) {
        this.db = db;
    }

    public void save(NodeSetText nodeSetText) {
        this.db.save(nodeSetText);
    }

    public void save(List<NodeSetText> nodeSetTexts) {
        this.db.bulk(nodeSetTexts);
    }

    public List<NodeSetText> getUserDescriptionsForNodeSet(String namespaceUri, String version, OffsetDateTime publicationDate) {
        String query = new QueryBuilder(and(
                eq("namespaceUri", namespaceUri),
                eq("version", version),
                exists("userText", true)))
                .limit(Integer.MAX_VALUE)
                .build();

        List<NodeSetText> nodes = db.query(query, NodeSetText.class).getDocs();
        return nodes.stream()
                .filter(nodeDescription -> OffsetDateTime.parse(nodeDescription.getPublicationDate()).equals(publicationDate))
                .collect(Collectors.toList());
    }

    public List<NodeSetText> getAllNodeSetTextsForNodeSet(String namespaceUri, String version, OffsetDateTime publicationDate) {
        String query = new QueryBuilder(and(
                eq("namespaceUri", namespaceUri),
                eq("version", version)))
                .limit(Integer.MAX_VALUE)
                .build();

        List<NodeSetText> nodes = db.query(query, NodeSetText.class).getDocs();
        return nodes.stream()
                .filter(nodeDescription -> OffsetDateTime.parse(nodeDescription.getPublicationDate()).equals(publicationDate))
                .collect(Collectors.toList());
    }

    private Predicate<NodeSetText> matchNodeIdAndIdentifier(UANode node, Function<NodeId<?>, TextId> identifierFunction) {
        return nodeDescription -> nodeDescription.getTextId().equals(identifierFunction.apply(node.getNodeId()));
    }
    public Predicate<NodeSetText> matchNodeForDescription(UANode node) {
        return matchNodeIdAndIdentifier(node, TextId::forNodeDescription);
    }

    public Predicate<NodeSetText> matchNodeForDocumentation(UANode node) {
        return matchNodeIdAndIdentifier(node, TextId::forNodeDocumentation);
    }

    public Predicate<NodeSetText> matchForField(UADataType dataType, DefinitionField field) {
        return nodeDescription -> nodeDescription.getTextId().equals(TextId.forField(dataType.getNodeId(), field));
    }

    public Predicate<NodeSetText> matchForArgument(UAMethod method, UAVariable.Argument argument) {
        return nodeDescription -> nodeDescription.getTextId().equals(TextId.forArgument(method.getNodeId(), argument));
    }

}
