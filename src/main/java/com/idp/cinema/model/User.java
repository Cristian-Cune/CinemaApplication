package com.idp.cinema.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User implements UserDetails, Serializable {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "enabled")
    private Boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> authorities = new HashSet<>();

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.enabled = true;
    }

    public User(CreateUserRequest request) {
        this.username = request.getUsername();
        this.password = request.getPassword();
        this.enabled = true;
        this.fullName = request.getFullName();
        this.authorities = request.getAuthorities().stream().map(Role::new).collect(toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
