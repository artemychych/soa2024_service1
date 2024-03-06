package se.ifmo.ru.soa2024_lab3_service1.web.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

@Data
@XmlType
@XmlRootElement(name = "Ticket")
public class TicketAddOrUpdateRequestDto {
    private String name;
    private TicketCoordinatesAddResponseDto coordinates;
    private Float price;
    private String type;
    private TicketPersonAddResponseDto person;

    @Data
    @XmlType
    @XmlRootElement(name = "coordinates")
    public static class TicketCoordinatesAddResponseDto {
        private Integer x;
        private int y;
    }
    @Data
    @XmlType
    @XmlRootElement(name = "person")
    public static class TicketPersonAddResponseDto {
        private Long weight; //Поле может быть null, Значение поля должно быть больше 0
        private String hairColor; //Поле не может быть null
        private TicketPersonLocationAddResponseDto location; //Поле не может быть null
    }

    @Data
    @XmlType
    @XmlRootElement(name = "location")
    public static class TicketPersonLocationAddResponseDto {
        private Float x; //Поле не может быть null
        private long y;
        private double z;
    }
}
