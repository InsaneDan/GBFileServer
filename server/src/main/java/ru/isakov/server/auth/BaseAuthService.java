package ru.isakov.server.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.JDBC;
import ru.isakov.server.Command;
import ru.isakov.server.commands.AuthCommandData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(BaseAuthService.class);

    // TODO: 30.03.2021 Небезопасное хранение паролей в открытой БД ~~> многократное хэширование? jBCrypt? что-то другое ???

    private static Connection connection;
    private static Statement statement;

    private static PreparedStatement createUserStatement;
    private static PreparedStatement getLoginStatement;
    private static PreparedStatement getUserIdStatement;
    private static PreparedStatement deleteUserStatement;

    private static final List<String> clients = new ArrayList<>();

    public Boolean isAuthorized(String login) {
        return clients.contains(login);
    }

    @Override
    public void start() {
        // подключение к БД
        try {
            connection = DriverManager.getConnection(JDBC.PREFIX.concat("users.db"));
            statement = connection.createStatement();
            // подготовка запросов
            prepareAllStatement();
            logger.info("Сервис авторизации запущен");
        } catch (Exception e) {
            logger.error("Не удалось подключиться к базе данных");
            logger.error(e.getMessage());
        }
    }

    @Override
    public void stop() {

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

        logger.info("Сервис авторизации остановлен");
    }

    public static void prepareAllStatement() throws SQLException {
        getLoginStatement = connection.prepareStatement("SELECT login FROM users WHERE login = ?");
        getUserIdStatement = connection.prepareStatement("SELECT login FROM users WHERE login = ? AND password = ?");
        createUserStatement = connection.prepareStatement("INSERT INTO users (login, password) VALUES (?, ?)");
        deleteUserStatement = connection.prepareStatement("DELETE FROM users WHERE login = ? AND password = ?");
    }

    // метод добавляет параметры (логин и пароль) к sql-запросу
    private void setPreparedStatementParameters(PreparedStatement stmt, String login, String password) throws SQLException {
        if (login != null) stmt.setString(1, login);
        if (password != null) stmt.setString(2, password);
    }

    @Override
    public Boolean isLoginExist(String login) {
        try {
            setPreparedStatementParameters(getLoginStatement, login, null);
            ResultSet rs = getLoginStatement.executeQuery();
            if (rs.next()) {
                return rs.getString(1).equals(login);
            }
        } catch (SQLException ex) {
            logger.error(ex.toString());
        }
        return false;
    }

    public Command isAuthOK(AuthCommandData command) {
        return isAuthOK(command.getLogin(), command.getPassword());
    }

    @Override
    public Command isAuthOK(String login, String password) {
        Command response = new Command();
        // если логин в списке - уже авторизован
        if (isAuthorized(login)) {
            logger.debug("Авторизация не пройдена: пользователь {} уже подключен", login);
            response = Command.authErrCommand(String.format("Пользователь '%s' уже подключен", login));
        }
        // если логина нет в БД - авторизация не пройдена
        if (!isLoginExist(login)) {
            logger.debug("Авторизация не пройдена: логин '{}' не зарегистрирован в базе данных", login);
            response = Command.authErrCommand(String.format("Логин '%s' не зарегистрирован в базе данных", login));
        }
        try {
            setPreparedStatementParameters(getUserIdStatement, login, password);
            ResultSet rs = getUserIdStatement.executeQuery();
            if (rs.next()) {
                clients.add(login);
                response = Command.authOkCommand();
            } else {
                logger.debug("Авторизация не пройдена: неверный пароль");
                response = Command.authErrCommand("Указан неверный пароль");
            }
        } catch (SQLException ex) {
            logger.error(ex.toString());
        }
        return response;
    }

    public Boolean addUser(AuthCommandData command) {
        return addUser(command.getLogin(), command.getPassword());
    }

    @Override
    public Boolean addUser(String login, String password) {
        // если логин уже есть в БД - не добавлять пользователя
        if (isLoginExist(login)) {
            logger.debug("Пользователь не добавлен: логин '{}' уже зарегистрирован в базе данных", login);
            return false;
        }
        try {
            setPreparedStatementParameters(createUserStatement, login, password);
            createUserStatement.execute();
            logger.debug("Пользователь успешно добавлен: логин '{}', пароль '{}'", login, password);
            return true;
        } catch (SQLException ex) {
            logger.error(ex.toString());
        }
        return false;
    }

    public Boolean removeUser(AuthCommandData command) {
        return removeUser(command.getLogin(), command.getPassword());
    }

    @Override
    public Boolean removeUser(String login, String password) {
        // если логина нет в БД - нечего удалять
        if (!isLoginExist(login)) {
            logger.debug("Запись не удалена: логин '{}' не зарегистрирован в базе данных", login);
            return false;
        }
        try {
            setPreparedStatementParameters(deleteUserStatement, login, password);
            deleteUserStatement.execute();
            logger.debug("Запись успешно удалена");
            return true;
        } catch (SQLException ex) {
            logger.error(ex.toString());
        }
        logger.debug("Запись не удалена: неверный пароль");
        return false;
    }
}
