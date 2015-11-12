package uk.ac.kent.co600.project.stylechecker.utils;

import com.google.common.collect.ImmutableList;

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
}
