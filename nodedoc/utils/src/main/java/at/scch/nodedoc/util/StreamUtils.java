package at.scch.nodedoc.util;

import java.util.function.Function;
import java.util.stream.Stream;

public class StreamUtils {

    public static <From, To> Function<From, Stream<To>> filterCast(Class<To> clazz) {
        return (obj) -> {
            if (clazz.isInstance(obj)) {
                return Stream.of(clazz.cast(obj));
            } else {
                return Stream.empty();
            }
        };
    }

    public static <T> Iterable<T> toIterable(Stream<T> stream) {
        return stream::iterator;
    }
}
