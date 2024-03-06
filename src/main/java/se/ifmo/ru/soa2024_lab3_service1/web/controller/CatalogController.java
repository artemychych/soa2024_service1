package se.ifmo.ru.soa2024_lab3_service1.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.ifmo.ru.soa2024_lab3_service1.mapper.TicketMapper;
import se.ifmo.ru.soa2024_lab3_service1.service.api.TicketService;
import se.ifmo.ru.soa2024_lab3_service1.service.model.Ticket;
import se.ifmo.ru.soa2024_lab3_service1.storage.model.Page;
import se.ifmo.ru.soa2024_lab3_service1.util.ResponseUtils;
import se.ifmo.ru.soa2024_lab3_service1.web.model.CountByPriceResponseDto;
import se.ifmo.ru.soa2024_lab3_service1.web.model.TicketAddOrUpdateRequestDto;
import se.ifmo.ru.soa2024_lab3_service1.web.model.TicketsListGetResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/service")
public class CatalogController {
    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final ResponseUtils responseUtils;

    @Autowired
    public CatalogController(TicketService ticketService, TicketMapper ticketMapper, ResponseUtils responseUtils){
        this.ticketService = ticketService;
        this.responseUtils = responseUtils;
        this.ticketMapper = ticketMapper;
    }
    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok("PING");
    }


    @GetMapping("/tickets")
    public ResponseEntity<?> getTickets(final HttpServletRequest request) {
        String[] sortParameters = request.getParameterValues("sort");
        String[] filterParameters = request.getParameterValues("filter");

        String pageParam = request.getParameter("page");
        String pageSizeParam = request.getParameter("pageSize");
        Integer page = null, pageSize = null;

        try {
            if (StringUtils.isNotEmpty(pageParam)) {
                page = Integer.parseInt(pageParam);
                if (page <= 0) {
                    throw new NumberFormatException();
                }
            }
            if (StringUtils.isNotEmpty(pageSizeParam)) {
                pageSize = Integer.parseInt(pageSizeParam);
                if (pageSize <= 0) {
                    throw new NumberFormatException();
                }
            }
        } catch (NumberFormatException numberFormatException) {
            return responseUtils.buildResponseWithMessage(HttpStatus.BAD_REQUEST, "Invalid query param value");
        }

        List<String> sort = sortParameters == null
                ? new ArrayList<>()
                : Stream.of(sortParameters).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        List<String> filter = filterParameters == null
                ? new ArrayList<>()
                : Stream.of(filterParameters).filter(StringUtils::isNotEmpty).collect(Collectors.toList());

        Page<Ticket> resultPage = ticketService.getTickets(sort, filter, page, pageSize);

        if (resultPage == null) {
            return responseUtils.buildResponseWithMessage(HttpStatus.NOT_FOUND, "Not found");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(new TicketsListGetResponseDto(
                        ticketMapper.toGetResponseDtoList(
                                resultPage.getObjects()
                        ),
                        resultPage.getPage(),
                        resultPage.getPageSize(),
                        resultPage.getTotalPages(),
                        resultPage.getTotalCount()
                ));


    }


    @GetMapping("/tickets/{id}")
    public ResponseEntity<?> getTicket(@PathVariable("id") int id) {
        Ticket ticket = ticketService.getTicket(id);

        if (ticket == null) {
            return responseUtils.buildResponseWithMessage(HttpStatus.NOT_FOUND,
                    "Ticket with id: " + id + " not found!");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(ticketMapper.toDto(ticket));
    }


    @PostMapping("/tickets")
    public ResponseEntity<?> addTicket(@Valid @RequestBody TicketAddOrUpdateRequestDto requestDto) {
        Ticket ticket = ticketService.addTicket(requestDto);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(ticketMapper.toDto(ticket));
    }

    @PutMapping("/tickets/{id}")
    public ResponseEntity<?> updateFlat(@PathVariable("id") int id, @Valid @RequestBody TicketAddOrUpdateRequestDto requestDto) {


        Ticket ticket = ticketService.updateTicket(id, requestDto);

        if (ticket == null) {
            return responseUtils.buildResponseWithMessage(HttpStatus.NOT_FOUND, "Flat with id " + id + " not found");
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(ticketMapper.toDto(ticket));
    }

    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<?> deleteFlat(@PathVariable("id") int id) {
        boolean deleted = ticketService.deleteTicket(id);

        if (!deleted) {
            return responseUtils.buildResponseWithMessage(HttpStatus.NOT_FOUND, "Flat with id " + id + " not found");
        }

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/tickets/minimum-type")
    public ResponseEntity<?> getMinimumType() {
        Ticket ticket = ticketService.getMinimumTypeTicket();
        if (ticket == null) {
            return responseUtils.buildResponseWithMessage(HttpStatus.NOT_FOUND,
                    "Ticket not found!");
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(ticketMapper.toDto(ticket));
    }


    @GetMapping("/tickets/name/{type}")
    public ResponseEntity<?> getGreaterType(@PathVariable("type") String type) {
        List<Ticket> tickets = ticketService.getTicketsGreaterType(type);

        if (tickets == null) {
            return responseUtils.buildResponseWithMessage(HttpStatus.NOT_FOUND,
                    "Tickets not found!");
        }


        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(new TicketsListGetResponseDto(
                        ticketMapper.toGetResponseDtoList(tickets),
                        0,
                        20,
                        40,
                        30L
                ))
                ;
    }


    @GetMapping("/tickets/count/{price}")
    public ResponseEntity<?> getCountByPrice(@PathVariable("price") long price) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(CountByPriceResponseDto.builder().count(ticketService.countTicketsByPrice(price)).build())
                ;
    }


    private ResponseEntity<?> validateTicketAddOrUpdateRequestDto(TicketAddOrUpdateRequestDto requestDto) {
        if (StringUtils.isEmpty(requestDto.getName())) {
            return responseUtils.buildResponseWithMessage(HttpStatus.BAD_REQUEST, "Name can not be empty");
        }
        if (requestDto.getCoordinates() == null) {
            return responseUtils.buildResponseWithMessage(HttpStatus.BAD_REQUEST, "Coordinates cannot be null");
        }
        if (requestDto.getPrice() == null || requestDto.getPrice() <= 0) {
            return responseUtils.buildResponseWithMessage(HttpStatus.BAD_REQUEST, "Price must be grater than 0");
        }
        if (requestDto.getType() == null) {
            return responseUtils.buildResponseWithMessage(HttpStatus.BAD_REQUEST, "Type cannot be null");
        }
        if (requestDto.getPerson() != null) {
            if (requestDto.getPerson().getWeight() != null && requestDto.getPerson().getWeight() <= 0) {
                return responseUtils.buildResponseWithMessage(HttpStatus.BAD_REQUEST, "Weight of Person must be greater than 0");
            }
            if (requestDto.getPerson().getHairColor() == null) {
                return responseUtils.buildResponseWithMessage(HttpStatus.BAD_REQUEST, "Hair color of Person cannot be null");
            }

            if (requestDto.getPerson().getLocation() == null) {
                return responseUtils.buildResponseWithMessage(HttpStatus.BAD_REQUEST, "Location of Person cannot be null");
            } else {
                if (requestDto.getPerson().getLocation().getX() == null && requestDto.getPerson().getWeight() != null) {
                    return responseUtils.buildResponseWithMessage(HttpStatus.BAD_REQUEST, "X Location of Person cannot be null");
                }
            }


        }

        return null;
    }


}
