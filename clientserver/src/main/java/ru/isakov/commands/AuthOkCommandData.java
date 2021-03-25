package ru.isakov.commands;

import java.io.Serializable;

public class AuthOkCommandData implements Serializable {

    private final String nickname;

    public AuthOkCommandData(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
