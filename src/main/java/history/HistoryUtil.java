package main.java.history;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryUtil {
    public static String toString(List<Integer> history) {
        return history.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public static List<Integer> fromString(String value) {
        if (value == null || value.isEmpty()) return List.of();
        return Arrays.stream(value.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
