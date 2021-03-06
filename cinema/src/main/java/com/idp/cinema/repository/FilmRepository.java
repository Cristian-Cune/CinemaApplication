package com.idp.cinema.repository;

import com.idp.cinema.model.Film;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface FilmRepository extends CrudRepository<Film, Long> {
    Film findByNameAndCinema_NameAndStartTime(String name, String Cinema, LocalDateTime startTime);

    void deleteAllByStartTimeLessThan(LocalDateTime date);
}
