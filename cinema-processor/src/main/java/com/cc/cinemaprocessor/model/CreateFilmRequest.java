package com.cc.cinemaprocessor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class CreateFilmRequest {
    @JsonProperty("cinema_name")
    private String cinemaName;
    @JsonProperty("film")
    private Film film;
    public CreateFilmRequest(String cinemaName, Film film) {
        this.cinemaName = cinemaName;
        this.film = film;
    }

    public CreateFilmRequest(){}
}
