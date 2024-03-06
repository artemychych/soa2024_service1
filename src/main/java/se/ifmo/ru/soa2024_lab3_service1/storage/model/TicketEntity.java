package se.ifmo.ru.soa2024_lab3_service1.storage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.ifmo.ru.soa2024_lab3_service1.service.model.*;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ticket")
public class TicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "coordinates_x")
    private Integer coordinatesX;
    @Column(name = "coordinates_y")
    private int coordinatesY;
    @Column(name = "creation_date")
    private java.time.LocalDate creationDate;
    @Column(name = "price")
    private Float price;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TicketType type;
    @Column(name = "person_weight")
    private Long personWeight;
    @Column(name = "person_hair_color")
    @Enumerated(EnumType.STRING)
    private Color personHairColor;
    @Column(name = "person_location_x")
    private Float personLocationX;
    @Column(name = "person_location_y")
    private long personLocationY;
    @Column(name = "person_location_z")
    private double personLocationZ;

}
