package com.idp.cinema.repository;

import com.idp.cinema.model.Film;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> findByNameEquals(String name);
}
