package us.insy.jdbc_human_resources;

import java.sql.*;

public class DatabaseConnector {
    // wunderhübsches Singleton, Prachi gönn Plus
    private static DatabaseConnector instance;
    public static DatabaseConnector getInstance() {
    if (instance == null) {
        instance = new DatabaseConnector();
    }
    return instance;
}

    static final String dbUrl = "jdbc:postgresql://localhost:5432/dhain";
    private Connection connection;

    private DatabaseConnector() {
        startConnection();
    }

    /**
     * Connects with the database with the username and password from the CLI Arguments
     */
    private void startConnection(){
        try{
            connection = DriverManager.getConnection(dbUrl, MainApplication.user, MainApplication.pass);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes the query written in the statementString
     * @param statementString Query to execute
     * @return ResultSet of the results from the query
     */
    public ResultSet executeStatement(String statementString){
        ResultSet resultSet = null;
        try{
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(statementString);
        } catch(SQLException e){
            MainApplication.errorBox("Could not execute Query");
        }
        return resultSet;
    }

    /**
     * Executes the query written in the statementString
     * Does not throw any Exceptions
     * Used for any query that do not return a ResultSet like "INSERT INTO" or "DELETE"
     * @param statementString Query to execute
     */
    public void executeStatementNoError(String statementString){
        try{
            Statement statement = connection.createStatement();
            statement.executeQuery(statementString);
        } catch(SQLException ignored){

        }
    }

}
