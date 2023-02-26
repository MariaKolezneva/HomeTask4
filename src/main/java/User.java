import java.sql.*;

public class User {
    private int userId;
    private String name;
    private String address;

    private static final Object lock = new Object();

    public User(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public User(String name) {

        this.name = name;
    }

    public int getUserId() {

        return userId;
    }

    public void setUserId(int userId) {

        this.userId = userId;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {

        this.address = address;
    }

    public static void createUser(User user) {

        String sql = "INSERT INTO Users (name, address) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = DriverManager.getConnection(Database.DATABASE_URL);
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getAddress());
            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setUserId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void createAccount(Account account) {
        synchronized (lock) {
            if (accountExists(account)) {
                System.out.println("Account already exists with this currency for this user.");
            } else {
                String sql = "INSERT INTO Accounts (userId, currency, balance) VALUES (?, ?, 0)";
                try (Connection conn = DriverManager.getConnection(Database.DATABASE_URL);
                     PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, account.getUserId());
                    pstmt.setString(2, account.getCurrency().name());
                    pstmt.executeUpdate();
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            account.setAccountId(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Creating account failed, no ID obtained.");
                        }
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                } finally {
                    try {
                        if (Database.connection != null) {
                            Database.connection.close();
                        }
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

        }
    }

    public static boolean accountExists(Account account) {
        String sql = "SELECT COUNT(*) FROM Accounts WHERE userId = ? AND currency = ?";
        try (Connection conn = DriverManager.getConnection(Database.DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, account.getUserId());
            pstmt.setString(2, account.getCurrency().name());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}


