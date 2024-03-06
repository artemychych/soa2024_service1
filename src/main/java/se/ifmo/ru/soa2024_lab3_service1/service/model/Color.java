package se.ifmo.ru.soa2024_lab3_service1.service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum Color {
    GREEN("green"),
    ORANGE("orange"),
    WHITE("white"),
    BROWN("brown"),
    NONE("none");
    @Getter
    private final String value;

    @Override
    public String toString() {
        return value;
    }

    public static Color fromValue(String value){
        return Arrays.stream(Color.values())
                .filter(e-> Objects.equals(e.getValue(), value))
                .findFirst()
                .orElse(NONE);
    }
}
