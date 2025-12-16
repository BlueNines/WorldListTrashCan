package org.worldlisttrashcan.utils;
import java.util.HashSet;
import java.util.Set;

public class Enums {
    public static <E extends Enum<E>> E oneOf(Class<E> enumType, String... names) {
        for (String name : names) {
            try {
                return Enum.valueOf(enumType, name);
            } catch (IllegalArgumentException illegalArgumentException) {}
        }

        return null;
    }

    public static <E extends Enum<E>> Set<E> allOf(Class<E> enumType, String... names) {
        Set<E> result = new HashSet<>();
        for (String name : names) {
            try {
                result.add(Enum.valueOf(enumType, name));
            } catch (IllegalArgumentException illegalArgumentException) {}
        }

        return result;
    }
}