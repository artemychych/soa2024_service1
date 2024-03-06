package se.ifmo.ru.soa2024_lab3_service1.service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum TicketType {
    VIP("vip"),
    BUDGETARY("budgetary"),
    CHEAP("cheap");

    @Getter
    private final String value;

    @Override
    public String toString() {
        return value;
    }

    public static TicketType fromValue(String value){
        return Arrays.stream(TicketType.values())
                .filter(e-> Objects.equals(e.getValue(), value))
                .findFirst()
                .orElse(CHEAP);
    }

}
