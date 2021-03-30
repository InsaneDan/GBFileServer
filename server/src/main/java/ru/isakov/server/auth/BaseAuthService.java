package ru.isakov.server.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(BaseAuthService.class);

    // TODO: 30.03.2021 Небезопасное хранение паролей в открытой БД ~~> многократное хэширование? jBCrypt? что-то другое ???

    private static Connection connection;
    private static Statement statement;

    // FIXME: 31.03.2021 вместо списка логинов сохранять список ClientHandler'ов (еще не реализован)

    private final List<String> clients = new ArrayList<>();

    @Override
    public void start() {
        try {
            connect();
            logger.info("Сервис авторизации запущен");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void stop() {
        disconnect();
        logger.info("Сервис авторизации остановлен");
    }

    @Override
    public String isLoginExist(String login) {
        return sqlSelectSingleField(String.format("SELECT login FROM users WHERE login = '%s'", login));
    }

    @Override
    public Boolean isAuthOK(String login, String password) {
        // если логина нет в БД - авторизация не пройдена
        if (!isLoginExist(login).equals(login)) {
            logger.debug("Авторизация не пройдена (логин не зарегистрирован в базе данных)");
            return false;
        }
        if (sqlSelectSingleField(String.format("SELECT login FROM users WHERE login = '%s' AND psw = '%s'", login, password)) != null) {
            clients.add(login);
            logger.info("Авторизация прошла успешно");
            return true;
        }
        logger.debug("Авторизация не пройдена (введен неверный пароль)");
        return false;
    }

    @Override
    public Boolean addUser(String login, String password) {
        try {
            statement.executeUpdate(String.format("INSERT INTO users (login, psw) VALUES ('%s', '%s');", login, password));
            logger.info("Новый пользователь успешно добавлен: {}", login);
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean removeUser(String login, String password) {
        // запрос на удаление (вернет null)
        sqlSelectSingleField(String.format("DELETE FROM users WHERE login = '%s' AND psw = '%s'", login, password));
        // если логин остался в БД, т.е. isLoginExist == true, значит метод не сработал, - вернуть false, и наоборот
        // isLoginExist == false, значит данные удалены из БД, вернуть true
        return !isLoginExist(login).equals(login);
    }

    private static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(JDBC.PREFIX.concat("users.db"));
            statement = connection.createStatement();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private static void disconnect() {

        try {
            statement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        try {
            connection.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    // метод выполняет запрос к БД и возвращает первое значение (первый столбец, первая строка) либо null
    private String sqlSelectSingleField(String sqlSelectQuery) {
        try {
            ResultSet rs = statement.executeQuery(sqlSelectQuery);
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return null;
    }


}
