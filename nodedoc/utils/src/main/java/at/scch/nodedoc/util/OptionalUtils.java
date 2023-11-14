package at.scch.nodedoc.util;

import java.util.Optional;
import java.util.function.Function;

public class OptionalUtils {

    public static <From, To> Function<From, Optional<To>> filterCast(Class<To> clazz) {
        return (obj) -> {
            if (clazz.isInstance(obj)) {
                return Optional.of(clazz.cast(obj));
            } else {
                return Optional.empty();
            }
        };
    }
}
