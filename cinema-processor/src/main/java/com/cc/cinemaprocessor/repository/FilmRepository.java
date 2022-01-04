package com.cc.cinemaprocessor.repository;


import com.cc.cinemaprocessor.model.Film;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface FilmRepository extends CrudRepository<Film, Long> {
    Film findByNameAndCinema_NameAndStartTime(String name, String Cinema, LocalDateTime startTime);

    void deleteAllByStartTimeLessThan(LocalDateTime date);
}
