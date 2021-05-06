package com.idp.cinema.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "reservation")
public class Reservation {

    @Id
    @Column(name = "reservation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cinema_name")
    private String cinemaName;

    @Column(name = "film_name")
    private String filmName;

    @Column(name = "name")
    private String name;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "reserved_seats")
    private String reservedSeats;

}
