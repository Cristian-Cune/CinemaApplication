package com.idp.cinema.controller;

import com.idp.cinema.model.Cinema;
import com.idp.cinema.model.Film;
import com.idp.cinema.service.CinemaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CinemaController {
    private final CinemaService cinemaService;

    public CinemaController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }


    @GetMapping(value = "/cinemas")
    public ResponseEntity<List<Cinema>> getCinemas() {
        List<Cinema> cinemas = cinemaService.getCinemas();
        return new ResponseEntity<>(cinemas, HttpStatus.OK);
    }

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


    @GetMapping(value = "/cinemas/{cinemaName}/films")
    public ResponseEntity<List<Film>> getCinemas(@PathVariable String cinemaName) {
        List<Film> films = cinemaService.getFilms(cinemaName);
        if (films == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

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
}
