package com.idp.cinema.model;

import lombok.Data;

@Data
public class UserView {
    private String id;
    private String username;
    private String fullName;

    public UserView(User user) {
        this.id = user.getId().toString();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
    }
}
