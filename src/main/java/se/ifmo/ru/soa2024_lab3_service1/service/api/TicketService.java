package se.ifmo.ru.soa2024_lab3_service1.service.api;

import se.ifmo.ru.soa2024_lab3_service1.service.model.Ticket;
import se.ifmo.ru.soa2024_lab3_service1.storage.model.Page;
import se.ifmo.ru.soa2024_lab3_service1.web.model.TicketAddOrUpdateRequestDto;

import java.util.List;

public interface TicketService {
    Page<Ticket> getTickets(List<String> sortsList, List<String> filtersList, Integer page, Integer pageSize);

    Ticket getTicket(int id);

    Ticket updateTicket(int id, TicketAddOrUpdateRequestDto requestDto);

    Ticket addTicket(TicketAddOrUpdateRequestDto requestDto);

    boolean deleteTicket(int id);

    Ticket getMinimumTypeTicket();

    long countTicketsByPrice(float price);

    List<Ticket> getTicketsGreaterType(String type);



}
