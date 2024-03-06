package se.ifmo.ru.soa2024_lab3_service1.web.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@XmlType

@XmlRootElement(name = "Tickets")
public class TicketsListGetResponseDto {
    private List<TicketGetResponseDto> ticketGetResponseDtos;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalCount;

    @XmlElement(name="Ticket")
    public List<TicketGetResponseDto> getTicketGetResponseDtos() {
        return ticketGetResponseDtos;
    }

    public TicketsListGetResponseDto(
            List<TicketGetResponseDto> ticketGetResponseDtos,
            Integer page,
            Integer pageSize,
            Integer totalPages,
            Long totalCount
    ) {
        this.ticketGetResponseDtos = ticketGetResponseDtos;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
    }
}
