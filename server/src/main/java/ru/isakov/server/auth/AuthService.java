package ru.isakov.server.auth;

import ru.isakov.server.Command;

public interface AuthService {

    void start();

    void stop();

    Boolean isLoginExist(String login);

    Command isAuthOK(String login, String password);

    Boolean addUser(String login, String password);

    Boolean removeUser(String login, String password);
}
