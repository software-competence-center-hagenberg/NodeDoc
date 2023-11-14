package at.scch.nodedoc.diffengine;

import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.NodeSetUniverse;
import at.scch.nodedoc.nodeset.UANode;
import lombok.Getter;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiffContext {

    private final Map<NodeId<?>, DiffView<? extends UANode>> nodes = new HashMap<>();
    @Getter
    private final DiffView<NodeSetUniverse> universe;

    @Getter
    private final Set<DiffCollectionEntry<NodeSetUniverse.Reference>> diffReferences;

    private final Map<NodeId<?>, Set<DiffCollectionEntry<NodeSetUniverse.Reference>>> diffReferencesFromNode;

    public Set<DiffCollectionEntry<NodeSetUniverse.Reference>> getDiffReferencesFromNode(NodeId<?> sourceNodeId) {
        return diffReferencesFromNode.getOrDefault(sourceNodeId, Set.of());
    }

    private final Map<NodeId<?>, Set<DiffCollectionEntry<NodeSetUniverse.Reference>>> diffReferencesToNode;

    public Set<DiffCollectionEntry<NodeSetUniverse.Reference>> getDiffReferencesToNode(NodeId<?> targetNodeId) {
        return diffReferencesToNode.getOrDefault(targetNodeId, Set.of());
    }

    public DiffContext(NodeSetUniverse baseUniverse, NodeSetUniverse compareUniverse) {
        this.universe = new DiffView<>(baseUniverse, compareUniverse);
        var diffNodes = this.universe.getDiffSetWithValues(NodeSetUniverse::getAllNodes, UANode::getNodeId, NodeId.class);
        diffNodes.forEach(entry -> {
            var id = entry.getValue().getKeyProperty(UANode::getNodeId);
            nodes.put(id, entry.getValue());
        });
        this.diffReferences = universe.getDiffSetWithValues(NodeSetUniverse::getReferences, Function.identity(), NodeSetUniverse.Reference.class);
        this.diffReferencesFromNode = this.diffReferences.stream().collect(Collectors.groupingBy(
                ref -> ref.getValue().getProperty(NodeSetUniverse.Reference::getSource).getKeyProperty(UANode::getNodeId),
                Collectors.toSet()
        ));

        this.diffReferencesToNode = this.diffReferences.stream().collect(Collectors.groupingBy(
                ref -> ref.getValue().getProperty(NodeSetUniverse.Reference::getTarget).getKeyProperty(UANode::getNodeId),
                Collectors.toSet()
        ));
    }

    @SuppressWarnings("unchecked")
    private <Node extends UANode> DiffView<Node> getNodeById(NodeId<?> nodeId) {
        return (DiffView<Node>) nodes.get(nodeId);
    }

    @SuppressWarnings("unchecked")
    public <Node extends UANode> DiffView<Node> getNodeById(NodeId<?> nodeId, Class<Node> clazz) {
        return (DiffView<Node>) nodes.get(nodeId);
    }

    public enum EntryDiffType {
        ADDED,
        REMOVED,
        UNCHANGED
    }

    @Getter
    public class DiffCollectionEntry<T> {
        private final EntryDiffType entryDiffType;
        private final DiffView<T> value;

        public DiffCollectionEntry(EntryDiffType entryDiffType, DiffView<T> value) {
            this.entryDiffType = entryDiffType;
            this.value = value;
        }
    }

    @Getter
    public class DiffRelation<T> {
        private final DiffView<T> diffViewInBase;
        private final DiffView<T> diffViewInCompare;

        public DiffRelation(DiffView<T> diffViewInBase, DiffView<T> diffViewInCompare) {
            this.diffViewInBase = diffViewInBase;
            this.diffViewInCompare = diffViewInCompare;
        }

        public <P> DiffView<P> getMergedProperty(Function<T, P> propertyExtractor) {
            if (Objects.nonNull(diffViewInBase) && Objects.nonNull(diffViewInCompare)) {
                return new DiffView<>(
                        diffViewInBase.getProperty(propertyExtractor).getBaseValue(),
                        diffViewInCompare.getProperty(propertyExtractor).getCompareValue()
                );
            } else if (Objects.nonNull(diffViewInCompare)) {
                var value = diffViewInCompare.getProperty(propertyExtractor).getCompareValue();
                return new DiffView<>(value, value);
            } else if (Objects.nonNull(diffViewInBase)) {
                var value = diffViewInBase.getProperty(propertyExtractor).getBaseValue();
                return new DiffView<>(value, value);
            } else {
                return new DiffView<>(null, null);
            }
        }
    }

    public enum ValueDiffType {
        CHANGED,
        UNCHANGED;

        public static ValueDiffType getCombinedDiffType(ValueDiffType... valueDiffTypes) {
            return Arrays.stream(valueDiffTypes).anyMatch(valueDiffType -> valueDiffType == CHANGED)
                    ? CHANGED
                    : UNCHANGED;
        }
    }

    public <T> DiffView<T> diffViewOf(T baseValue, T compareValue) {
        return new DiffView<>(baseValue, compareValue);
    }

    public <T> DiffView<T> diffViewOf(T value) {
        return new DiffView<>(value, value);
    }

    @Getter
    public class DiffView<T> {

        private final T baseValue;
        private final T compareValue;

        public DiffView(T baseValue, T compareValue) {
            this.baseValue = baseValue;
            this.compareValue = compareValue;
        }

        public DiffContext getDiffContext() {
            return DiffContext.this;
        }

        public T getBaseOrElseCompareValue() {
            return !Objects.isNull(baseValue) ? baseValue : compareValue;
        }

        public T getCompareOrElseBaseValue() {
            return !Objects.isNull(compareValue) ? compareValue : baseValue;
        }

        public DiffView<T> toUnchangedKeepBaseValue() {
            return new DiffView<>(baseValue, null);
        }

        public DiffView<T> toUnchangedKeepCompareValue() {
            return new DiffView<>(null, compareValue);
        }

        public DiffView<T> replaceNull(T value) {
            return new DiffView<>(
                    Objects.nonNull(this.baseValue) ? this.baseValue : value,
                    Objects.nonNull(this.compareValue) ? this.compareValue : value
            );
        }

        public <O, R> DiffView<R> combineNonNull(DiffView<O> other, BiFunction<T, O, R> combiner) {
            return new DiffView<>(
                    Objects.nonNull(this.baseValue) && Objects.nonNull(other.baseValue) ? combiner.apply(this.baseValue, other.baseValue) : null,
                    Objects.nonNull(this.compareValue) && Objects.nonNull(other.compareValue) ? combiner.apply(this.compareValue, other.compareValue) : null
            );
        }

        public <P> DiffView<P> getProperty(Function<T, P> propertyExtractor) {
            return new DiffView<>(
                    !Objects.isNull(baseValue) ? propertyExtractor.apply(baseValue) : null,
                    !Objects.isNull(compareValue) ? propertyExtractor.apply(compareValue) : null
            );
        }

        public <P> P getKeyProperty(Function<T, P> keyExtractor) {
            return keyExtractor.apply(getBaseOrElseCompareValue());
        }

        public <Node extends UANode> DiffRelation<Node> navigate(Function<T, Node> navigator) {
            var nodeId = getProperty(navigator).getProperty(UANode::getNodeId);
            return new DiffRelation<>(
                    getNodeById(nodeId.getBaseValue()),
                    getNodeById(nodeId.getCompareValue())
            );
        }

        public <Node extends UANode> DiffView<Node> navigateInKey(Function<T, Node> navigator) {
            var nodeId = getProperty(navigator).getKeyProperty(UANode::getNodeId);
            return getNodeById(nodeId);
        }

        public <R, ID> Set<DiffCollectionEntry<R>> getDiffSetWithValues(Function<T, Set<R>> setExtractor, Function<R, ID> idExtractor, Class<ID> idClass) {
            var setDiff = Utils.createSetDiff(
                    baseValue != null ? setExtractor.apply(baseValue) : setExtractor.apply(compareValue),
                    compareValue != null ? setExtractor.apply(compareValue) : setExtractor.apply(baseValue),
                    idExtractor,
                    idClass
            );

            return setDiff.stream().map(entry -> {
                if (entry instanceof Utils.EntryAdded) {
                    return new DiffCollectionEntry<>(EntryDiffType.ADDED, new DiffView<>(null, ((Utils.EntryAdded<R>) entry).getAddedValue()));
                } else if (entry instanceof Utils.EntryRemoved) {
                    return new DiffCollectionEntry<>(EntryDiffType.REMOVED, new DiffView<>(((Utils.EntryRemoved<R>) entry).getRemovedValue(), null));
                } else if (entry instanceof Utils.EntryUnchanged) {
                    return new DiffCollectionEntry<>(EntryDiffType.UNCHANGED, new DiffView<>(((Utils.EntryUnchanged<R>) entry).getBaseValue(), ((Utils.EntryUnchanged<R>) entry).getCompareValue()));
                } else {
                    throw new RuntimeException("unreachable");
                }
            }).collect(Collectors.toSet());
        }

        @SuppressWarnings("unchecked")
        public <Node extends UANode> Set<DiffCollectionEntry<Node>> getDiffSetWithNodes(Function<T, Set<Node>> setExtractor) {
            var setDiff = Utils.createSetDiff(
                    baseValue != null ? setExtractor.apply(baseValue) : setExtractor.apply(compareValue),
                    compareValue != null ? setExtractor.apply(compareValue) : setExtractor.apply(baseValue),
                    UANode::getNodeId,
                    NodeId.class
            );

            return setDiff.stream().map(entry -> {
                if (entry instanceof Utils.EntryAdded) {
                    return new DiffCollectionEntry<>(EntryDiffType.ADDED, (DiffView<Node>) getNodeById(((Utils.EntryAdded<Node>) entry).getAddedValue().getNodeId()));
                } else if (entry instanceof Utils.EntryRemoved) {
                    return new DiffCollectionEntry<>(EntryDiffType.REMOVED, (DiffView<Node>) getNodeById(((Utils.EntryRemoved<Node>) entry).getRemovedValue().getNodeId()));
                } else if (entry instanceof Utils.EntryUnchanged) {
                    return new DiffCollectionEntry<>(EntryDiffType.UNCHANGED, (DiffView<Node>) getNodeById(((Utils.EntryUnchanged<Node>) entry).getBaseValue().getNodeId()));
                } else {
                    throw new RuntimeException("unreachable");
                }
            }).collect(Collectors.toSet());
        }

        public <R, ID> List<DiffCollectionEntry<R>> getDiffListWithValues(Function<T, List<R>> listExtractor, Function<R, ID> idExtractor, Class<ID> idClass) {
            var listDiff = Utils.createListDiff(
                    !Objects.isNull(baseValue) ? listExtractor.apply(baseValue) : listExtractor.apply(compareValue),
                    !Objects.isNull(compareValue) ? listExtractor.apply(compareValue) : listExtractor.apply(baseValue),
                    idExtractor,
                    idClass
            );

            return listDiff.stream().map(entry -> {
                if (entry instanceof Utils.EntryAdded) {
                    return new DiffCollectionEntry<>(EntryDiffType.ADDED, new DiffView<>(null, ((Utils.EntryAdded<R>) entry).getAddedValue()));
                } else if (entry instanceof Utils.EntryRemoved) {
                    return new DiffCollectionEntry<>(EntryDiffType.REMOVED, new DiffView<>(((Utils.EntryRemoved<R>) entry).getRemovedValue(), null));
                } else if (entry instanceof Utils.EntryUnchanged) {
                    return new DiffCollectionEntry<>(EntryDiffType.UNCHANGED, new DiffView<>(((Utils.EntryUnchanged<R>) entry).getBaseValue(), ((Utils.EntryUnchanged<R>) entry).getCompareValue()));
                } else {
                    throw new RuntimeException("unreachable");
                }
            }).collect(Collectors.toList());
        }

        public ValueDiffType getDiffType() {
            return getDiffType(Objects::equals);
        }

        public ValueDiffType getDiffType(BiFunction<T, T, Boolean> customEquals) {
            return Objects.isNull(baseValue) || Objects.isNull(compareValue) || customEquals.apply(baseValue, compareValue)
                    ? ValueDiffType.UNCHANGED
                    : ValueDiffType.CHANGED;
        }

        @SuppressWarnings("unchecked")
        public <R extends T> Optional<DiffView<R>> cast(Class<R> clazz) {
            if (clazz.isInstance(getBaseOrElseCompareValue())) {
                return Optional.of((DiffView<R>) this);
            } else {
                return Optional.empty();
            }
        }

        @SuppressWarnings("unchecked")
        public <R> Optional<DiffView<R>> anyCast(Class<R> clazz) {
            if (clazz.isInstance(getBaseOrElseCompareValue())) {
                return Optional.of((DiffView<R>) this);
            } else {
                return Optional.empty();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, R extends T> Optional<DiffContext.DiffView<? extends R>> readOnlyCast(DiffContext.DiffView<? extends T> diffView, Class<R> clazz) {
        if (clazz.isInstance(diffView.getBaseOrElseCompareValue())) {
            return Optional.of((DiffContext.DiffView<? extends R>) diffView);
        } else {
            return Optional.empty();
        }
    }
}
