package com.idp.cinema.service;

import com.idp.cinema.model.*;
import com.idp.cinema.repository.ReservationRepository;
import com.idp.cinema.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.ValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReservationRepository reservationRepository;
    private final CinemaService cinemaService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ReservationRepository reservationRepository, CinemaService cinemaService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.reservationRepository = reservationRepository;
        this.cinemaService = cinemaService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user != null)
            return user;
        else
            throw new UsernameNotFoundException(String.format("User with username - %s not found", username));
    }


    public UserView create(CreateUserRequest request) throws ValidationException {

        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new ValidationException("Username exists!");
        }
        if (!request.getPassword().equals(request.getRePassword())) {
            throw new ValidationException("Passwords don't match!");
        }
        if (request.getAuthorities() == null) {
            HashSet<String> authorities = new HashSet<>();
            authorities.add(Role.USER_CINEMA);
            request.setAuthorities(authorities);
        }

        User user = new User(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);

        return new UserView(user);
    }

    public UserView grantAdmin(String username) {
        User user = userRepository.findByUsername(username);
        Set<String> authorities = new HashSet<>();
        authorities.add(Role.USER_ADMIN);
        authorities.add(Role.USER_CINEMA);
        user.setAuthorities(authorities.stream().map(Role::new).collect(Collectors.toSet()));

        return new UserView(userRepository.save(user));
    }

    public UserView denyAdmin(String username) {
        User user = userRepository.findByUsername(username);
        Set<String> authorities = new HashSet<>();
        authorities.add(Role.USER_CINEMA);
        user.setAuthorities(authorities.stream().map(Role::new).collect(Collectors.toSet()));

        return new UserView(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(String username) {
        List<Reservation> reservations = reservationRepository.findAllByUsername(username);
        reservations.forEach(reservation -> cinemaService.deleteReservation(reservation.getId(), reservation.getUsername()));
        userRepository.deleteByUsername(username);
    }

    public UserView updateUser(String username, CreateUserRequest request) throws ValidationException {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new ValidationException("Username exists!");
        }
        if (!request.getPassword().equals(request.getRePassword())) {
            throw new ValidationException("Passwords don't match!");
        }
        List<Reservation> reservations = reservationRepository.findAllByUsername(username);

        User user = userRepository.findByUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());

        reservations.forEach(reservation -> {
            reservation.setUsername(request.getUsername());
            reservationRepository.save(reservation);
        });
        return new UserView(userRepository.save(user));
    }
}
