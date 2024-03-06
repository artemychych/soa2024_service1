package se.ifmo.ru.soa2024_lab3_service1.web.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlType
@XmlRootElement(name = "Ticket")
public class TicketGetResponseDto {
    private Integer id;
    private String name;
    private TicketCoordinatesGetResponseDto coordinates;
    private String creationDate;
    private Float price;
    private String type;
    private TicketPersonGetResponseDto person;


    @Data
    @XmlType
    @XmlRootElement(name = "coordinates")
    public static class TicketCoordinatesGetResponseDto {
        private Integer x;
        private int y;
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType
    @XmlRootElement(name = "person")
    public static class TicketPersonGetResponseDto {
        private Long weight; //Поле может быть null, Значение поля должно быть больше 0
        private String hairColor; //Поле не может быть null
        private TicketPersonLocationGetResponseDto location; //Поле не может быть null
    }

    @Data
    @XmlType
    @XmlRootElement(name = "location")
    public static class TicketPersonLocationGetResponseDto {
        private Float x; //Поле не может быть null
        private long y;
        private double z;
    }


}
