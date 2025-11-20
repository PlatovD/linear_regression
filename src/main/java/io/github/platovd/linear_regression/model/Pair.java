package io.github.platovd.linear_regression.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Pair<K, V> {
    private final K key;
    private final V value;
}

