package com.idp.cinema.controller;

import com.idp.cinema.configuration.JwtTokenUtil;
import com.idp.cinema.model.AuthRequest;
import com.idp.cinema.model.CreateUserRequest;
import com.idp.cinema.model.User;
import com.idp.cinema.model.UserView;
import com.idp.cinema.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;

@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserView> login(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            User user = (User) authentication.getPrincipal();
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateAccessToken(user))
                    .body(new UserView(user));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public UserView register(@RequestBody @Valid CreateUserRequest request) throws ValidationException {
        return userService.create(request);
    }

    @PreAuthorize("hasAuthority('USER_ADMIN')")
    @PutMapping("/grantAdmin/{username}")
    public UserView grantAdmin(@PathVariable String username) {
        return userService.grantAdmin(username);
    }

    @PreAuthorize("hasAuthority('USER_ADMIN')")
    @PutMapping("/denyAdmin/{username}")
    public UserView denyAdmin(@PathVariable String username) {
        return userService.denyAdmin(username);
    }

    @PreAuthorize("hasAuthority('USER_ADMIN')")
    @DeleteMapping("/users/{username}")
    public void deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
    }

    @PutMapping("/users/{username}")
    public UserView updateUser(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable String username,
            @RequestBody CreateUserRequest request) throws ValidationException {

        String authUsername = jwtTokenUtil.getUsername(jwtToken.split(" ")[1].trim());
        if (! authUsername.equals(username)) {
            throw new IllegalArgumentException("Authentication username is not the same as requested username");
        }
        return userService.updateUser(username, request);
    }
}
