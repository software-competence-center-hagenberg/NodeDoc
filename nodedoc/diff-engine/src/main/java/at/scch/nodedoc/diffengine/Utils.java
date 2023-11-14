package at.scch.nodedoc.diffengine;

import at.scch.nodedoc.util.StreamUtils;
import lombok.Getter;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.MappingStyle;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.container.*;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Utils {

    public interface DiffEntry<T> {
        T getBaseValueOrElseCompareValue();
    }

    @Getter
    public static class EntryAdded<T> implements DiffEntry<T> {
        private final T addedValue;

        public EntryAdded(T addedValue) {
            this.addedValue = addedValue;
        }

        @Override
        public T getBaseValueOrElseCompareValue() {
            return addedValue;
        }
    }

    @Getter
    public static class EntryRemoved<T> implements DiffEntry<T> {
        private final T removedValue;

        public EntryRemoved(T removedValue) {
            this.removedValue = removedValue;
        }

        @Override
        public T getBaseValueOrElseCompareValue() {
            return removedValue;
        }
    }

    @Getter
    public static class EntryUnchanged<T> implements DiffEntry<T> {
        private final T baseValue;
        private final T compareValue;

        public EntryUnchanged(T baseValue, T compareValue) {
            this.baseValue = baseValue;
            this.compareValue = compareValue;
        }

        @Override
        public T getBaseValueOrElseCompareValue() {
            return baseValue;
        }
    }

    private static final Javers JAVERS_FOR_SETS = JaversBuilder.javers()
            .withMappingStyle(MappingStyle.BEAN)
            .build();

    public static <T, ID> Set<DiffEntry<T>> createSetDiff(Set<T> baseSet, Set<T> compareSet, Function<T, ID> idExtractor, Class<ID> idClass) {
        var baseValues = baseSet.stream().collect(Collectors.toMap(idExtractor, Function.identity()));
        var compareValues = compareSet.stream().collect(Collectors.toMap(idExtractor, Function.identity()));

        var baseIds = baseValues.keySet();
        var compareIds = compareValues.keySet();

        var diff = JAVERS_FOR_SETS.compare(baseIds, compareIds);

        var containerChanges = diff.getChangesByType(SetChange.class).stream()
                .flatMap(change -> change.getChanges().stream())
                .collect(Collectors.toList());

        var addedIds = containerChanges.stream().flatMap(StreamUtils.filterCast(ValueAdded.class))
                .map(ValueAdded::getAddedValue)
                .flatMap(StreamUtils.filterCast(idClass)).collect(Collectors.toSet());

        var removedIds = containerChanges.stream().flatMap(StreamUtils.filterCast(ValueRemoved.class))
                .map(ValueRemoved::getRemovedValue)
                .flatMap(StreamUtils.filterCast(idClass)).collect(Collectors.toSet());

        return Stream.concat(
                baseIds.stream(),
                addedIds.stream()
        ).map(id -> {
            if (addedIds.contains(id)) {
                return new EntryAdded<>(compareValues.get(id));
            } else if (removedIds.contains(id)) {
                return new EntryRemoved<>(baseValues.get(id));
            } else {
                return new EntryUnchanged<>(baseValues.get(id), compareValues.get(id));
            }
        }).collect(Collectors.toSet());
    }

    private static final Javers JAVERS_FOR_LISTS = JaversBuilder.javers()
            .withMappingStyle(MappingStyle.BEAN)
            .withListCompareAlgorithm(ListCompareAlgorithm.SIMPLE)
            .build();
    public static <T, ID> List<DiffEntry<T>> createListDiff(List<T> baseList, List<T> compareList, Function<T, ID> idExtractor, Class<ID> idClass) {
        var baseValues = baseList.stream().collect(Collectors.toMap(idExtractor, Function.identity()));
        var compareValues = compareList.stream().collect(Collectors.toMap(idExtractor, Function.identity()));

        var baseIds = baseList.stream().map(idExtractor).collect(Collectors.toList());
        var compareIds = compareList.stream().map(idExtractor).collect(Collectors.toList());

        var diff = JAVERS_FOR_LISTS.compare(baseIds, compareIds);

        var containerChanges = diff.getChangesByType(ListChange.class).stream()
                .flatMap(change -> change.getChanges().stream())
                .collect(Collectors.toList());

        var addedValues = containerChanges.stream()
                .flatMap(StreamUtils.filterCast(ValueAdded.class))
                        .collect(Collectors.toMap(ValueAdded::getIndex, Function.identity()));

        var removedValues = containerChanges.stream()
                .flatMap(StreamUtils.filterCast(ValueRemoved.class))
                .collect(Collectors.toMap(ValueRemoved::getIndex, Function.identity()));

        var changedValues = containerChanges.stream()
                .flatMap(StreamUtils.filterCast(ElementValueChange.class))
                .collect(Collectors.toMap(ElementValueChange::getIndex, Function.identity()));

        return IntStream.range(0, Math.max(baseList.size(), compareList.size()))
                .boxed()
                .flatMap(index -> {
                    if (addedValues.containsKey(index)) {
                        var change = addedValues.get(index);
                        return Stream.of(new EntryAdded<>(compareValues.get(change.getAddedValue())));
                    } else if (removedValues.containsKey(index)) {
                        var change = removedValues.get(index);
                        return Stream.of(new EntryRemoved<>(baseValues.get(change.getRemovedValue())));
                    } else if (changedValues.containsKey(index)) {
                        var change = changedValues.get(index);
                        return Stream.of(
                                new EntryRemoved<>(baseValues.get(change.getLeftValue())),
                                new EntryAdded<>(compareValues.get(change.getRightValue()))
                        );
                    } else {
                        return Stream.of(new EntryUnchanged<>(baseList.get(index), compareList.get(index)));
                    }
                }).collect(Collectors.toList());
    }
}
