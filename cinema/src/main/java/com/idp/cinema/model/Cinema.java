package com.idp.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table( name = "cinema",uniqueConstraints={@UniqueConstraint(columnNames = {"name"})})
@ToString
public class Cinema {

    @Id
    @Column(name = "cinema_id")
    @JsonProperty("cinema_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "cinema",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Film> films = new ArrayList<>();
}
