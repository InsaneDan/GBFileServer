package ru.isakov.server.auth;

import ru.isakov.Command;
import ru.isakov.CommandType;

public class TestConnection {

    // TODO: 06.04.2021 УДАЛИТЬ ПЕРЕД СДАЧЕЙ ПРОЕКТА, поленился писать юнит-тесты...

    public static void main(String[] args) {
        BaseAuthService baseAuthService = new BaseAuthService();
        Command command;

        baseAuthService.start();
        String login = "user55";
        String psw = "test";

        baseAuthService.addUser(login, psw);
        System.out.println("Добавление пользователя: ожидаем true / " + baseAuthService.isLoginExist(login));
        baseAuthService.removeUser(login, psw);
        System.out.println("Удаление пользователя: ожидаем false / " + baseAuthService.isLoginExist(login));

        command = baseAuthService.isAuthOK("user1", "psw1");
        System.out.println("Авторизация [верные данные]: ожидаем true / " + command.getType().equals(CommandType.AUTH_OK));
        command = baseAuthService.isAuthOK("user2", "psw2");
        System.out.println("Авторизация [верные данные]: ожидаем true / " + command.getType().equals(CommandType.AUTH_OK));
        command = baseAuthService.isAuthOK("user3", "psw3");
        System.out.println("Авторизация [верные данные]: ожидаем true / " + command.getType().equals(CommandType.AUTH_OK));
        command = baseAuthService.isAuthOK("user333", "psw3");
        System.out.println("Авторизация [неверный логин]: ожидаем false / " + command.getType().equals(CommandType.AUTH_OK));
        command = baseAuthService.isAuthOK("user3", "psw333");
        System.out.println("Авторизация [неверный пароль]: ожидаем false / " + command.getType().equals(CommandType.AUTH_OK));

        System.out.println("Пользователь авторизован: ожидаем true / " + baseAuthService.isAuthorized("user2"));
        System.out.println("Пользователь не авторизован: ожидаем false / " + baseAuthService.isAuthorized("user222"));

        baseAuthService.stop();
    }

}
