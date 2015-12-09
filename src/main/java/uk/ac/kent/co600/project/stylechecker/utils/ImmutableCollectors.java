package uk.ac.kent.co600.project.stylechecker.utils;

import com.google.common.base.Function;
import com.google.common.collect.*;

import java.util.stream.Collector;

public class ImmutableCollectors {

    public static <T> Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> toList() {
        return Collector.of(
                ImmutableList.Builder::new,
                ImmutableList.Builder::add,
                (b1, b2) -> b1.addAll(b2.build()),
                ImmutableList.Builder::build
        );
    }

    public static <T, K, V> Collector<T, ImmutableMap.Builder<K, V>, ImmutableMap<K, V>> toMap(
            Function<T, K> keyFunction, Function<T, V> valueFunction) {
        return Collector.of(
                ImmutableMap.Builder::new,
                (builder, t) -> builder.put(
                        keyFunction.apply(t), valueFunction.apply(t)
                ),
                (b1, b2) -> b1.putAll(b2.build()),
                ImmutableMap.Builder::build
        );
    }

    public static <T, K, V> Collector<T, ImmutableListMultimap.Builder<K, V>,
            ImmutableListMultimap<K, V>> toListMultiMap(
            Function<T, K> keyFunction, Function<T, V> valueFunction
    ) {
        return Collector.of(
                ImmutableListMultimap::builder,
                (builder, t) -> builder.put(
                        keyFunction.apply(t), valueFunction.apply(t)
                ),
                (b1, b2) -> b1.putAll(b2.build()),
                ImmutableListMultimap.Builder::build
        );
    }
}

