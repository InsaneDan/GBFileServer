package ru.isakov.server.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    private static final Logger logger = LoggerFactory.getLogger(DataBase.class);

    private Connection connection;
    private Statement statement;
    private final String folderPostfix = "_folder/";
    private List<String> activeUsers = new ArrayList<>();

    public void connect(){
        try {
            connection = DriverManager.getConnection(JDBC.PREFIX + "users.db");
            statement = connection.createStatement();
        } catch (SQLException e){
            logger.error(e.toString());
        }
    }

    public void disconnect(){
        try {
            statement.close();
        } catch (SQLException e){
            logger.error(e.toString());
        }
        try {
            connection.close();
        } catch (SQLException e){
            logger.error(e.toString());
        }
    }
}
