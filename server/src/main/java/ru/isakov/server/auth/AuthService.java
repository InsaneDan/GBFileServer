package ru.isakov.server.auth;

public interface AuthService {

    void start();

    void stop();

    String isLoginExist(String login);

    Boolean isAuthOK(String login, String password);

    Boolean addUser(String login, String password);

    Boolean removeUser(String login, String password);
}
