package at.scch.nodedoc.nodeset;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface NodeSetUniverse {

    Collection<UANodeSet> getAllNodeSets();

    UANodeSet getNodeSetByNamespaceUri(String namespaceUri);

    @Getter
    @EqualsAndHashCode
    class Reference {
        private final UANode source;
        private final UANode target;
        private final UAReferenceType referenceType;

        public Reference(UANode source, UANode target, UAReferenceType referenceType) {
            this.source = source;
            this.target = target;
            this.referenceType = referenceType;
        }
    }

    Set<Reference> getReferences();

    Set<Reference> getReferencesFromNode(NodeId<?> source);

    Set<Reference> getReferencesToNode(NodeId<?> target);

    UANode getNodeById(NodeId<?> nodeId);

    Set<UANode> getAllNodes();

    Set<UAType> getUATypes();

    Set<UAObjectType> getUAObjectTypes();

    Set<UADataType> getUADataTypes();

    Set<UAVariableType> getUAVariableTypes();

    Set<UAReferenceType> getUAReferenceTypes();

    Set<UAObject> getUAObjects();

    Set<UAVariable> getUAVariables();

    Set<UAMethod> getUAMethods();

    Set<UAView> getUAViews();

    default Set<UAInstance> getInstances() {
        return Stream.of(
                getUAObjects(),
                getUAMethods(),
                getUAVariables(),
                getUAViews()
        ).flatMap(Collection::stream).collect(Collectors.toSet());
    }
}
