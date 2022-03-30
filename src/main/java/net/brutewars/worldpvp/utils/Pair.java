package net.brutewars.worldpvp.utils;

import lombok.Getter;
import lombok.Setter;

public final class Pair<K, V> {

    @Getter @Setter private K key;
    @Getter @Setter private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + key + "=" + value + "}";
    }

}