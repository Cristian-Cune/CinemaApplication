package com.cc.cinemaprocessor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table( name = "cinema",uniqueConstraints={@UniqueConstraint(columnNames = {"name"})})
public class Cinema {

    @Id
    @Column(name = "cinema_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("cinema_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "cinema",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Film> films = new ArrayList<>();
}
