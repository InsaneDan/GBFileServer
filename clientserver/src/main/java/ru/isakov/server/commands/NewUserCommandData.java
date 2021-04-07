package ru.isakov.server.commands;

import java.io.Serializable;

public class NewUserCommandData implements Serializable {

    private final String login;
    private final String password;
    private final String nickname;

    public NewUserCommandData(String login, String password, String nickname) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
}
