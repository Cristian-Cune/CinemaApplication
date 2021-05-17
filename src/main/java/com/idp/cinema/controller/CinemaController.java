package com.idp.cinema.controller;

import com.idp.cinema.configuration.JwtTokenUtil;
import com.idp.cinema.model.Cinema;
import com.idp.cinema.model.Film;
import com.idp.cinema.model.Reservation;
import com.idp.cinema.service.CinemaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CinemaController {
    private final CinemaService cinemaService;
    private final JwtTokenUtil jwtTokenUtil;

    public CinemaController(CinemaService cinemaService, JwtTokenUtil jwtTokenUtil) {
        this.cinemaService = cinemaService;
        this.jwtTokenUtil = jwtTokenUtil;
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
            Cinema saved = cinemaService.saveCinema(cinema);
            return new ResponseEntity<>(saved.getId(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PreAuthorize("hasAuthority('USER_ADMIN')")
    @DeleteMapping(value = "/cinemas/{id}")
    public void deleteCinema(@PathVariable Long id) {
        cinemaService.deleteCinema(id);
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
            Film saved = cinemaService.saveFilm(film, cinemaName);
            return new ResponseEntity<>(saved.getId(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PreAuthorize("hasAuthority('USER_ADMIN')")
    @DeleteMapping(value = "/cinemas/{cinemaName}/films/{id}")
    public void deleteFIlm(@PathVariable String cinemaName, @PathVariable Long id) {
        cinemaService.deleteFilm(id, cinemaName);
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
            @RequestBody Reservation reservation) {

        if (reservation.getCinemaName() == null || reservation.getFilmName() == null || reservation.getStartTime() == null ||
            reservation.getReservedSeats() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String username = getUsernameFromToken(jwtToken);
        reservation.setUsername(username);
        Reservation saved = cinemaService.saveReservation(reservation);
        return new ResponseEntity<>(saved.getId(), HttpStatus.CREATED);

    }

    @DeleteMapping("/reservations/{id}")
    public void deleteReservation(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long id) {

        String username = getUsernameFromToken(jwtToken);
        cinemaService.deleteReservation(id, username);
    }

    private String getUsernameFromToken(String jwtToken) {
        return jwtTokenUtil.getUsername(jwtToken.split(" ")[1].trim());
    }
}
