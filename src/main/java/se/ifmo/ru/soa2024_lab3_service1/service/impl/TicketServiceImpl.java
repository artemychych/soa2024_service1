package se.ifmo.ru.soa2024_lab3_service1.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.ifmo.ru.soa2024_lab3_service1.mapper.TicketMapper;
import se.ifmo.ru.soa2024_lab3_service1.service.api.TicketService;
import se.ifmo.ru.soa2024_lab3_service1.service.model.Color;
import se.ifmo.ru.soa2024_lab3_service1.service.model.Ticket;
import se.ifmo.ru.soa2024_lab3_service1.service.model.TicketType;
import se.ifmo.ru.soa2024_lab3_service1.storage.model.*;
import se.ifmo.ru.soa2024_lab3_service1.storage.repository.TicketRepositoryImpl;
import se.ifmo.ru.soa2024_lab3_service1.web.model.TicketAddOrUpdateRequestDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepositoryImpl ticketDao;
    private final TicketMapper ticketMapper;

    @Autowired
    public TicketServiceImpl(TicketRepositoryImpl ticketDao, TicketMapper ticketMapper){
        this.ticketDao = ticketDao;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public Page<Ticket> getTickets(List<String> sortsList, List<String> filtersList, Integer page, Integer pageSize){
        if (page != null || pageSize != null) {
            if (page == null) {
                page = 0;
            }
            if (pageSize == null) {
                pageSize = 20;
            }
        }

        Pattern nestedFieldNamePattern = Pattern.compile("(.*)\\.(.*)");
        Pattern lhsPattern = Pattern.compile("(.*)\\[(.*)\\]=(.*)");

        List<Sort> sorts = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(sortsList)) {
            boolean containsOppositeSorts = sortsList.stream().anyMatch(e1 ->
                    sortsList.stream().anyMatch(e2 -> Objects.equals(e1, "-" + e2))
            );

            if (containsOppositeSorts) {
                throw new IllegalArgumentException("Request contains opposite sort parameters");
            }

            for (String sort : sortsList) {
                boolean desc = sort.startsWith("-");
                String sortFieldName = desc ? sort.split("-")[1] : sort;

                Matcher matcher = nestedFieldNamePattern.matcher(sortFieldName);
                if (matcher.find()) {
                    String nestedField = matcher.group(2).substring(0, 1).toUpperCase() + matcher.group(2).substring(1);
                    sortFieldName = matcher.group(1) + nestedField;
                }

                sorts.add(Sort
                        .builder()
                        .desc(desc)
                        .fieldName(sortFieldName)
                        .build()
                );
            }
        }

        List<Filter> filters = new ArrayList<>();

        for (String filter : filtersList) {

            Matcher matcher = lhsPattern.matcher(filter);
            String fieldName = null, fieldValue = null;
            FilteringOperation filteringOperation = null;

            if (matcher.find()) {
                fieldName = matcher.group(1);

                Matcher nestedFieldMatcher = nestedFieldNamePattern.matcher(fieldName);
                if (nestedFieldMatcher.find()) {
                    String nestedField = nestedFieldMatcher.group(2).substring(0, 1).toUpperCase() + nestedFieldMatcher.group(2).substring(1);
                    fieldName = nestedFieldMatcher.group(1) + nestedField;
                }

                filteringOperation = FilteringOperation.fromValue(matcher.group(2));
                fieldValue = matcher.group(3);

            }

            if (StringUtils.isEmpty(fieldName)) {
                throw new IllegalArgumentException("Filter field name is empty");
            }
            if (StringUtils.isEmpty(fieldValue)) {
                throw new IllegalArgumentException("Filter field value is empty");
            }
            if (Objects.equals(filteringOperation, FilteringOperation.UNDEFINED)) {
                throw new IllegalArgumentException("No or unknown filtering operation. Possible values are: eq,neq,gt,lt,gte,lte.");
            }

            filters.add(Filter.builder()
                    .fieldName(fieldName)
                    .fieldValue(fieldValue)
                    .filteringOperation(filteringOperation)
                    .build()
            );
        }
        Page<TicketEntity> entitiesPage;

        try {
            entitiesPage = ticketDao.getSortedAndFilteredPage(sorts, filters, page, pageSize);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error while getting page. Check query params format. " + e.getMessage(), e);
        }

        Page<Ticket> ret = new Page<>();
        ret.setObjects(ticketMapper.fromEntityList(entitiesPage.getObjects()));
        ret.setPage(entitiesPage.getPage());
        ret.setPageSize(entitiesPage.getPageSize());
        ret.setTotalPages(entitiesPage.getTotalPages());
        ret.setTotalCount(entitiesPage.getTotalCount());

        return ret;


    }

    @Override
    public Ticket getTicket(int id) {
        return ticketMapper.fromEntity(ticketDao.findById(id));
    }

    @Override
    public Ticket updateTicket(int id, TicketAddOrUpdateRequestDto requestDto) {
        TicketEntity ticketEntity = ticketDao.findById(id);

        if (ticketEntity == null) {
            return null;
        }

        ticketEntity.setName(requestDto.getName());
        ticketEntity.setCoordinatesX(requestDto.getCoordinates().getX());
        ticketEntity.setCoordinatesY(requestDto.getCoordinates().getY());
        ticketEntity.setPrice(requestDto.getPrice());
        ticketEntity.setType(TicketType.fromValue(requestDto.getType()));
        ticketEntity.setPersonWeight(requestDto.getPerson().getWeight());
        ticketEntity.setPersonHairColor(Color.fromValue(requestDto.getPerson().getHairColor()));
        ticketEntity.setPersonLocationX(requestDto.getPerson().getLocation().getX());
        ticketEntity.setPersonLocationY(requestDto.getPerson().getLocation().getY());
        ticketEntity.setPersonLocationZ(requestDto.getPerson().getLocation().getZ());

        ticketEntity = ticketDao.save(ticketEntity);
        return ticketMapper.fromEntity(ticketEntity);
    }

    @Override
    public Ticket addTicket(TicketAddOrUpdateRequestDto requestDto) {
        TicketEntity ticketEntity = TicketEntity.builder()
                .name(requestDto.getName())
                .coordinatesX(requestDto.getCoordinates().getX())
                .coordinatesY(requestDto.getCoordinates().getY())
                .price(requestDto.getPrice())
                .type(TicketType.fromValue(requestDto.getType()))
                .personWeight(requestDto.getPerson().getWeight())
                .personHairColor(Color.fromValue(requestDto.getPerson().getHairColor()))
                .personLocationX(requestDto.getPerson().getLocation().getX())
                .personLocationY(requestDto.getPerson().getLocation().getY())
                .personLocationZ(requestDto.getPerson().getLocation().getZ())
                .build();
        return ticketMapper.fromEntity(ticketDao.save(ticketEntity));
    }
    @Override
    public boolean deleteTicket(int id){
        return ticketDao.deleteById(id);
    }

    @Override
    public Ticket getMinimumTypeTicket() {
        return ticketMapper.fromEntity(ticketDao.getMinimumType());
    }
    @Override
    public long countTicketsByPrice(float price) {
        return ticketDao.countTicketByPrice(price);
    }

    @Override
    public List<Ticket> getTicketsGreaterType(String type) {
        return ticketMapper.fromEntityList(ticketDao.getTicketGreaterType(type));
    }

}
