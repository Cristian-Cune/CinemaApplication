package com.idp.cinema.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "reservation")
public class Reservation {

    @Id
    @Column(name = "reservation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("cinema_name")
    @Column(name = "cinema_name")
    private String cinemaName;

    @JsonProperty("film_name")
    @Column(name = "film_name")
    private String filmName;

    @Column(name = "start_time")
    @JsonProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd/HH:mm", timezone = "UTC")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startTime;

    @JsonProperty("reserved_seats")
    @Column(name = "reserved_seats")
    private String reservedSeats;

    @JsonProperty("username")
    @Column(name = "username")
    private String username;
}
