package at.scch.nodedoc.db.repository;

import at.scch.nodedoc.db.documents.NodeSetText;
import at.scch.nodedoc.db.documents.TextId;
import at.scch.nodedoc.nodeset.*;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.query.QueryBuilder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Stream<CouchDbUtils.Page<NodeSetText>> getAllNodeSetTexts(int pageSize) {
        return CouchDbUtils.fetchAllDocsPaginated(db, pageSize, NodeSetText.class);
    }

    @Getter
    @Setter
    private static class DocForDelete {
        private String _id;
        private String _rev;
        private boolean _deleted;

        public DocForDelete(String _id, String _rev, boolean _deleted) {
            this._id = _id;
            this._rev = _rev;
            this._deleted = _deleted;
        }
    }

    public void deleteAllNodeSetTextsForNamespaceUri(String namespaceUri) {
        String query = new QueryBuilder(
                eq("namespaceUri", namespaceUri)
        ).limit(Integer.MAX_VALUE).build();
        var docs = db.query(query, DocForDelete.class).getDocs();
        docs.forEach(x -> x._deleted = true);
        db.bulk(docs);
    }

    public long deleteAllNodeSetTexts(int pageSize) {
        long[] docsDeleted = {0}; // array wrapper for modification inside of lambda
        Stream.generate(() -> CouchDbUtils.fetchAllDocIdsAndRevsPaginated(db, pageSize).findFirst())
                .takeWhile(Optional::isPresent)
                .map(Optional::get)
                .forEach(page -> {
                    var docs = page.getDocs().stream().map(idAndRev -> new DocForDelete(
                            idAndRev.getId(),
                            idAndRev.getRev(),
                            true
                    )).collect(Collectors.toList());
                    db.bulk(docs);
                    docsDeleted[0] += docs.size();
                });
        return docsDeleted[0];
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
