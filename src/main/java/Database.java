import java.sql.*;


public class Database {
    protected static final String DATABASE_URL = "jdbc:sqlite:mydb.db";
    public static Connection connection;
    public static Statement statement;

    public Database() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
                System.out.println("Connection to SQLite has been closed.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}



