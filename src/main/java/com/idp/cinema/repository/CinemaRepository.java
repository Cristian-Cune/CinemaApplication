package com.idp.cinema.repository;

import com.idp.cinema.model.Cinema;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaRepository extends CrudRepository<Cinema, Long> {
    Cinema findByNameEquals(String cinemaName);
}
