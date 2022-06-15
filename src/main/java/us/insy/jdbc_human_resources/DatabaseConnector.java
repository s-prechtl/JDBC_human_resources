package us.insy.jdbc_human_resources;

import java.sql.*;

public class DatabaseConnector{
    // wunderhübsches Singleton, Prachi gönn Plus
    private static DatabaseConnector instance;

    public static DatabaseConnector getInstance(){
        if(instance == null){
            instance = new DatabaseConnector();
        }
        return instance;
    }


    static final String dbUrl = "jdbc:postgresql://xserv:5432/dhain";
    private Connection connection;

    private DatabaseConnector(){
        startConnection();
    }

    private void startConnection(){
        try{
            connection = DriverManager.getConnection(dbUrl, MainApplication.user, MainApplication.pass);
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public ResultSet executeStatement(String statementString){
        ResultSet resultSet = null;
        try{
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(statementString);
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
        return resultSet;
    }

}
