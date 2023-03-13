import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;


public class TransactionWithdraw {
    private static final Object lock = new Object();
    public static final BigDecimal MAX_WITHDRAW_AMOUNT = new BigDecimal("100000000");


    public void withdraw(int accountId, BigDecimal amount) throws IllegalArgumentException {
        if (amount.compareTo(MAX_WITHDRAW_AMOUNT) > 0) {
            throw new IllegalArgumentException("Transaction withdraw amount exceeds maximum limit.");
        }
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Transaction amount cannot be zero.");
        }
        synchronized (lock) {
            try (Connection conn = DriverManager.getConnection(Database.DATABASE_URL)) {
                conn.setAutoCommit(false);
                PreparedStatement selectPstmt = conn.prepareStatement(
                        "SELECT balance FROM Accounts WHERE accountId = ?");
                selectPstmt.setInt(1, accountId);
                ResultSet rs = selectPstmt.executeQuery();
                if (rs.next()) {
                    BigDecimal balance = rs.getBigDecimal("balance");
                    if (balance.compareTo(amount) < 0) {
                        System.out.println("Insufficient balance");
                        conn.rollback();
                    } else {
                        PreparedStatement updatePstmt = conn.prepareStatement(
                                "UPDATE Accounts SET balance = balance - ? WHERE accountId = ?");
                        updatePstmt.setBigDecimal(1, amount.setScale(3, RoundingMode.HALF_DOWN));
                        updatePstmt.setInt(2, accountId);
                        updatePstmt.executeUpdate();
                        PreparedStatement insertPstmt = conn.prepareStatement(
                                "INSERT INTO Transactions (accountId, amount) VALUES (?, ?)");
                        insertPstmt.setInt(1, accountId);
                        insertPstmt.setBigDecimal(2, amount.negate().setScale(3, RoundingMode.HALF_DOWN));
                        insertPstmt.executeUpdate();
                        conn.commit();
                        addTransactionWithdraw(accountId, amount.setScale(3, RoundingMode.HALF_DOWN));
                    }
                } else {
                    System.out.println("Account not found");
                }
            } catch (SQLException e) {
                System.out.println("Withdrawal failed: " + e.getMessage());
            }
        }
    }


    public void addTransactionWithdraw(int accountId, BigDecimal amount) {
        synchronized (lock) {
            try (Connection conn = DriverManager.getConnection(Database.DATABASE_URL)) {
                conn.setAutoCommit(false);
                PreparedStatement insertPstmt = conn.prepareStatement(
                        "INSERT INTO Transactions (accountId, amount) VALUES (?, ?)");
                insertPstmt.setInt(1, accountId);
                insertPstmt.setBigDecimal(2, amount.setScale(3, RoundingMode.HALF_DOWN));
                insertPstmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                System.out.println("Transaction failed: " + e.getMessage());

            }
        }
    }
}