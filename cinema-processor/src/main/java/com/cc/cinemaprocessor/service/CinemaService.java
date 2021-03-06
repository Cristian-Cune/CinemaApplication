package com.cc.cinemaprocessor.service;

import com.cc.cinemaprocessor.model.Cinema;
import com.cc.cinemaprocessor.model.Film;
import com.cc.cinemaprocessor.model.Reservation;
import com.cc.cinemaprocessor.repository.CinemaRepository;
import com.cc.cinemaprocessor.repository.FilmRepository;
import com.cc.cinemaprocessor.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
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

    @Transactional
    public Film saveFilm(Film film, String cinemaName) {
        Cinema cinema = cinemaRepository.findByNameEquals(cinemaName);
        film.setAvailableSeats(AVAILABLE_SEATS);
        film.setCinema(cinema);
        cinema.getFilms().add(film);

        Film savedFilm = filmRepository.save(film);
        cinemaRepository.save(cinema);
        return savedFilm;
    }

    public List<Reservation> getReservations(String username) {
        Iterable<Reservation> reservations = reservationRepository.findAllByUsername(username);
        List<Reservation> reservationList = new ArrayList<>();
        reservations.forEach(reservationList::add);
        return reservationList;
    }

    public Reservation saveReservation(Reservation reservation) {
        Film film = filmRepository.findByNameAndCinema_NameAndStartTime(reservation.getFilmName(),
                reservation.getCinemaName(),
                reservation.getStartTime());
        if (film == null) {
            log.error("Required film or date not found");
            return null;
        }
        List<String> availableSeats = Arrays.asList(film.getAvailableSeats().split(";"));
        List<String> wantedSeats = Arrays.asList(reservation.getReservedSeats().split(";"));

        if (!availableSeats.containsAll(wantedSeats))
            throw new IllegalArgumentException("Requested seats are not available!");

        StringBuilder updatedAvailableSeats = new StringBuilder();
        availableSeats.forEach(seat -> {
            if(!reservation.getReservedSeats().contains(seat))
                updatedAvailableSeats.append(seat).append(";");
        });
        film.setAvailableSeats(updatedAvailableSeats.toString());
        filmRepository.save(film);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public void deleteReservation(Long id, String username) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        if (!optionalReservation.isPresent())
            throw new IllegalArgumentException("Wrong reservation selected");
        Reservation reservation = optionalReservation.get();
        if (!reservation.getUsername().equals(username))
            throw new IllegalArgumentException("Wrong reservation selected");

        returnReservationSeats(reservation);
        reservationRepository.deleteByIdAndUsername(id, username);
    }

    private void returnReservationSeats(Reservation reservation) {
        String reservedSeats = reservation.getReservedSeats();

        Film film = filmRepository.findByNameAndCinema_NameAndStartTime(
                reservation.getFilmName(),
                reservation.getCinemaName(),
                reservation.getStartTime()
        );

        List<String> seatList = Arrays.asList((film.getAvailableSeats() + reservedSeats).split(";"));
        seatList.sort(String::compareTo);

        StringBuilder updatedAvailableSeats = new StringBuilder();
        seatList.forEach(seat -> updatedAvailableSeats.append(seat).append(";"));
        film.setAvailableSeats(updatedAvailableSeats.toString());
        filmRepository.save(film);
    }

    @Transactional
    public void deleteCinema(Long id) {
        reservationRepository.deleteAllByCinemaName(cinemaRepository.findById(id).get().getName());
        cinemaRepository.deleteById(id);
    }

    @Transactional
    public void deleteFilm(Long id, String cinemaName) {
        Film film = filmRepository.findById(id).get();
        if (!film.getCinema().getName().equals(cinemaName))
            throw new IllegalArgumentException(String.format("Cinema %s has no film with id: %s", cinemaName, id.toString()));
        reservationRepository.deleteAllByFilmName(film.getName());
        filmRepository.deleteById(id);
    }
}