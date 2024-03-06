package se.ifmo.ru.soa2024_lab3_service1.storage.repository;

import org.springframework.stereotype.Repository;
import se.ifmo.ru.soa2024_lab3_service1.storage.model.Filter;
import se.ifmo.ru.soa2024_lab3_service1.storage.model.Page;
import se.ifmo.ru.soa2024_lab3_service1.storage.model.Sort;
import se.ifmo.ru.soa2024_lab3_service1.storage.model.TicketEntity;

import java.util.List;
@Repository
public interface TicketRepository{
    TicketEntity findById(int id);

    TicketEntity save(TicketEntity entity);
    boolean deleteById(int id);
    Page<TicketEntity> getSortedAndFilteredPage(List<Sort> sortList, List<Filter> filters, Integer page, Integer size);

    TicketEntity getMinimumType();

    long countTicketByPrice(float price);

    List<TicketEntity> getTicketGreaterType(String ticketType);
}
