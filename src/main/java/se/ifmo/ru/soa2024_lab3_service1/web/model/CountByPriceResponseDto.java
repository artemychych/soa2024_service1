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
@XmlRootElement(name = "Response")
public class CountByPriceResponseDto {
    private long count;
}
