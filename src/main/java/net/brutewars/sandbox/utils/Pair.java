package net.brutewars.sandbox.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public final class Pair<K, V> {
    @Getter @Setter private K key;
    @Getter @Setter private V value;

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + key + "=" + value + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return pair.getKey().equals(getKey()) && pair.getValue().equals(getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

}