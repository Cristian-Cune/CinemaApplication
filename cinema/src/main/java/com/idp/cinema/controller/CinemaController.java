package com.idp.cinema.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idp.cinema.configuration.CinemaProperties;
import com.idp.cinema.configuration.JwtTokenUtil;
import com.idp.cinema.model.Cinema;
import com.idp.cinema.model.CreateFilmRequest;
import com.idp.cinema.model.Film;
import com.idp.cinema.model.Reservation;
import com.idp.cinema.service.CinemaService;
import com.idp.cinema.service.RabbitSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class CinemaController {
    private final CinemaService cinemaService;
    private final JwtTokenUtil jwtTokenUtil;
    private final CinemaProperties cinemaProperties;
    private final ObjectMapper objectMapper;
    private final RabbitSender rabbitSender;

    public CinemaController(CinemaService cinemaService, JwtTokenUtil jwtTokenUtil, CinemaProperties cinemaProperties, ObjectMapper objectMapper, RabbitSender rabbitSender) {
        this.cinemaService = cinemaService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.cinemaProperties = cinemaProperties;
        this.objectMapper = objectMapper;
        this.rabbitSender = rabbitSender;
    }


    @GetMapping(value = "/cinemas")
    public ResponseEntity<List<Cinema>> getCinemas() {
        List<Cinema> cinemas = cinemaService.getCinemas();
        return new ResponseEntity<>(cinemas, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER_ADMIN')")
    @PostMapping(value = "/cinemas")
    public ResponseEntity<Long> addCinema(@RequestBody Cinema cinema) {
        if (cinema.getName() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            log.info("Sending create cinema request to queue: " + cinema.toString());
            rabbitSender.sendMessage(cinemaProperties.getExchange(), cinemaProperties.getAdminRoutingKey(), objectMapper.writeValueAsString(cinema));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PreAuthorize("hasAuthority('USER_ADMIN')")
    @DeleteMapping(value = "/cinemas/{id}")
    public void deleteCinema(@PathVariable Long id) {
//        cinemaService.deleteCinema(id);
        log.info("Sending delete cinema request to queue with id: " + id);
        rabbitSender.sendMessage(cinemaProperties.getExchange(), cinemaProperties.getAdminRoutingKey(), "DELETE_CINEMA " + id);

    }


    @GetMapping(value = "/cinemas/{cinemaName}/films")
    public ResponseEntity<List<Film>> getFilms(@PathVariable String cinemaName) {
        List<Film> films = cinemaService.getFilms(cinemaName);
        if (films == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER_ADMIN')")
    @PostMapping(value = "/cinemas/{cinemaName}/films")
    public ResponseEntity<Long> addFilm(@RequestBody Film film, @PathVariable String cinemaName) {
        if (film.getName() == null || film.getStartTime() == null || film.getLength() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            log.info(String.format("Sending create film request to queue for cinema %s: %s", cinemaName, film.toString()));
            rabbitSender.sendMessage(cinemaProperties.getExchange(), cinemaProperties.getAdminRoutingKey(), objectMapper.writeValueAsString(new CreateFilmRequest(cinemaName, film)));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PreAuthorize("hasAuthority('USER_ADMIN')")
    @DeleteMapping(value = "/cinemas/{cinemaName}/films/{id}")
    public void deleteFIlm(@PathVariable String cinemaName, @PathVariable Long id) {
        log.info(String.format("Sending delete film request to queue for cinema %s: %s", cinemaName, id));
        rabbitSender.sendMessage(cinemaProperties.getExchange(), cinemaProperties.getAdminRoutingKey(), String.format("DELETE_FILM %s %s", id, cinemaName));

    }

    @GetMapping(value = "/reservations")
    public ResponseEntity<List<Reservation>> getReservations(@RequestHeader("Authorization") String jwtToken) {
        String username = getUsernameFromToken(jwtToken);
        List<Reservation> reservations = cinemaService.getReservations(username);
        if (reservations == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @PostMapping(value = "/reservations")
    public ResponseEntity<Long> addReservation(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody Reservation reservation) throws JsonProcessingException {

        if (reservation.getCinemaName() == null || reservation.getFilmName() == null || reservation.getStartTime() == null ||
            reservation.getReservedSeats() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String username = getUsernameFromToken(jwtToken);
        reservation.setUsername(username);
        log.info(String.format("Sending create reservation request to queue: %s", reservation));
        rabbitSender.sendMessage(cinemaProperties.getExchange(), cinemaProperties.getRoutingKey(), objectMapper.writeValueAsString(reservation));

        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @DeleteMapping("/reservations/{id}")
    public void deleteReservation(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long id) {

        String username = getUsernameFromToken(jwtToken);
        log.info(String.format("Sending delete reservation request to queue: %s", id));
        rabbitSender.sendMessage(cinemaProperties.getExchange(), cinemaProperties.getRoutingKey(), String.format("DELETE_RESERVATION %s %s", id, username));

    }

    private String getUsernameFromToken(String jwtToken) {
        return jwtTokenUtil.getUsername(jwtToken.split(" ")[1].trim());
    }
}
