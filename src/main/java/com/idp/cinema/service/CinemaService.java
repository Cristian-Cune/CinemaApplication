package com.idp.cinema.service;

import com.idp.cinema.model.Cinema;
import com.idp.cinema.model.Film;
import com.idp.cinema.model.Reservation;
import com.idp.cinema.repository.CinemaRepository;
import com.idp.cinema.repository.FilmRepository;
import com.idp.cinema.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CinemaService {

    private static final String AVAILABLE_SEATS = "1A;1B;1C;1D;1E;1F;1G;1H;" +
            "2A;2B;2C;2D;2E;2F;2G;2H;" +
            "3A;3B;3C;3D;3E;3F;3G;3H;" +
            "4A;4B;4C;4D;4E;4F;4G;4H;" +
            "5A;5B;5C;5D;5E;5F;5G;5H;" +
            "6A;6B;6C;6D;6E;6F;6G;6H;" +
            "7A;7B;7C;7D;7E;7F;7G;7H;" +
            "8A;8B;8C;8D;8E;8F;8G;8H;";
    private final CinemaRepository cinemaRepository;
    private final FilmRepository filmRepository;
    private final ReservationRepository reservationRepository;

    public CinemaService(CinemaRepository cinemaRepository, FilmRepository filmRepository, ReservationRepository reservationRepository) {
        this.cinemaRepository = cinemaRepository;
        this.filmRepository = filmRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Cinema> getCinemas() {
        Iterable<Cinema> cinemas = cinemaRepository.findAll();
        List<Cinema> cinemasList = new ArrayList<>();
        cinemas.forEach(cinemasList::add);
        return cinemasList;
    }

    public Cinema saveCinema(Cinema cinema) {
        return cinemaRepository.save(cinema);
    }

    public List<Film> getFilms(String cinemaName) {
        Cinema cinema = cinemaRepository.findByNameEquals(cinemaName);
        return cinema.getFilms();
    }

    public Film saveFilm(Film film, String cinemaName) {
        Cinema cinema = cinemaRepository.findByNameEquals(cinemaName);
        film.setAvailableSeats(AVAILABLE_SEATS);
        film.setCinema(cinema);
        cinema.getFilms().add(film);

        Film savedFilm = filmRepository.save(film);
        cinemaRepository.save(cinema);
        return savedFilm;
    }

    public List<Reservation> getReservations() {
        Iterable<Reservation> reservations = reservationRepository.findAll();
        List<Reservation> reservationList = new ArrayList<>();
        reservations.forEach(reservationList::add);
        return reservationList;
    }

    public Reservation saveReservation(Reservation reservation) {
        Film film = filmRepository.findByNameAndCinema_NameAndStartTime(reservation.getFilmName(),
                    reservation.getCinemaName(),
                    reservation.getStartTime());
        List<String> availableSeats = Arrays.asList(film.getAvailableSeats().split(";"));

        StringBuilder updatedAvailableSeats = new StringBuilder();
        availableSeats.forEach(seat -> {
            if(!reservation.getReservedSeats().contains(seat))
                updatedAvailableSeats.append(seat).append(";");
        });
        film.setAvailableSeats(updatedAvailableSeats.toString());
        filmRepository.save(film);
        return reservationRepository.save(reservation);
    }
}
