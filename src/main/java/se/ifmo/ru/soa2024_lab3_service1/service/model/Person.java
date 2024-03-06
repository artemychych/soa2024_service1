package se.ifmo.ru.soa2024_lab3_service1.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private Long weight; //Поле может быть null, Значение поля должно быть больше 0
    private Color hairColor; //Поле не может быть null
    private Location location; //Поле не может быть null
}
